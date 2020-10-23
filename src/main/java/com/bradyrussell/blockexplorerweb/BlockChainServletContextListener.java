package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.BlockChainStorageLevelDB;
import com.bradyrussell.uiscoin.HTTP;
import com.bradyrussell.uiscoin.MagicBytes;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import com.bradyrussell.uiscoin.blockchain.exception.InvalidBlockException;
import com.bradyrussell.uiscoin.blockchain.exception.NoSuchBlockException;
import com.bradyrussell.uiscoin.blockchain.exception.NoSuchTransactionException;
import com.bradyrussell.uiscoin.node.Node;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BlockChainServletContextListener implements ServletContextListener {
    private static Node uisCoinNode;
    private static final Path peerlist = Path.of("peers.txt");

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("CONTEXT DESTROYED - CLOSING BLOCKCHAIN");
        BlockChain.get().close();
        SavePeers(uisCoinNode);
        uisCoinNode.Stop();
    }
 
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("CONTEXT INITIALIZED - OPENING BLOCKCHAIN");

        BlockChain.Storage = new BlockChainStorageLevelDB();
        BlockChain.get().open();

        uisCoinNode = new Node(MagicBytes.ProtocolVersion.Value);

        SyncBlockChain();

        //uisCoinNode.Start(); // make configurable whether to run a server or not
    }

    private static void SyncBlockChain() {

        System.out.println("Starting node with " + SetupPeers(uisCoinNode) + " peers.");

        try { // need time to allow peer connections
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Syncing blockheight with peers. " + uisCoinNode.getPeers().size());
        uisCoinNode.RequestBlockHeightFromPeers();

        if (uisCoinNode.getPeers().size() > 0 || BlockChain.get().BlockHeight < 0) { // we cant skip this if we have never had the blockchain
            while (!Thread.interrupted() && uisCoinNode.HighestSeenBlockHeight == -1) {
                try {
                    System.out.println("Waiting for blockheight sync...");
                    Thread.sleep(500);
                    uisCoinNode.RequestBlockHeightFromPeers();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("BlockHeight synced.");

            while (!Thread.interrupted() && BlockChain.get().BlockHeight < uisCoinNode.HighestSeenBlockHeight) {
                try {
                    System.out.println("Waiting for blockchain sync... Have: " + BlockChain.get().BlockHeight + " Seen: " + uisCoinNode.HighestSeenBlockHeight);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("BlockChain synced.");
        }

        boolean verify = false;
        try {
            verify = BlockChain.Verify(0);
            BlockChain.BuildUTXOSet(0);
        } catch (NoSuchBlockException | NoSuchTransactionException | InvalidBlockException e) {
            e.printStackTrace();
        }

        System.out.println("Full BlockChain verification: " + verify);
        //if (!verify) System.exit(100); // todo disconnect that node and retry
    }


    private static void SavePeers(Node node) {
        StringBuilder sb = new StringBuilder();
        for (InetAddress peerAddr : node.getPeers()) {
            sb.append(peerAddr.getHostAddress());
            sb.append("\n");
        }

        try {
            Files.writeString(BlockChainServletContextListener.peerlist, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int SetupPeers(Node node) {
        List<String> connected = new ArrayList<>();

        if (Files.exists(BlockChainServletContextListener.peerlist)) {
            System.out.println("Loading peerlist...");
            try {
                List<String> peers = Files.readAllLines(BlockChainServletContextListener.peerlist);

                for (String peer : peers) {
                    if (!peer.isEmpty() && !connected.contains(peer)) {
                        System.out.println("Connecting to peer " + peer);
                        node.ConnectToPeer(InetAddress.getByName(peer));
                        connected.add(peer);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Getting peerlist from repository...");
        String request = HTTP.Request("https://raw.githubusercontent.com/bradyrussell/UISCoinNodes/master/nodes.txt", "GET", null, null);
        JsonObject json = new Gson().fromJson(request, JsonObject.class);
        JsonArray peers = json.get("peers").getAsJsonArray();

        System.out.println("Found " + peers.size() + " peers.");

        peers.forEach((jsonElement -> {
            try {
                String peerString = jsonElement.getAsString();
                if (!connected.contains(peerString)) {
                    System.out.println("Connecting to repository peer " + peerString);
                    node.ConnectToPeer(InetAddress.getByName(peerString));
                    connected.add(peerString);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }));
        return connected.size();
    }
}