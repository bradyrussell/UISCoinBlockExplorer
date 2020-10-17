package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.Util;
import com.bradyrussell.uiscoin.block.Block;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import com.bradyrussell.uiscoin.transaction.Transaction;
import com.bradyrussell.uiscoin.transaction.TransactionInput;
import com.bradyrussell.uiscoin.transaction.TransactionOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

@Controller
public class TransactionController {

    @GetMapping("/transaction")
    public String transaction(@RequestParam(name = "hash", required = true) String TransactionHash, Model model) {
        Transaction transaction = null;
        if (TransactionHash != null) {
            byte[] transactionHash = Util.Base64Decode(TransactionHash);
            if (transactionHash.length == 64) transaction = BlockChain.get().getTransaction(transactionHash);
        }

        if (transaction != null) {
            Block blockWithTransaction = BlockChain.get().getBlockWithTransaction(transaction.getHash());

            model.addAttribute("transaction", transaction);
            model.addAttribute("block", blockWithTransaction);
            model.addAttribute("transactionDate", new Date(transaction.TimeStamp));
            model.addAttribute("transactionHash", Util.Base64Encode(transaction.getHash()));

            for (int i = 0; i < blockWithTransaction.Transactions.size(); i++) {
                if(Arrays.equals(transaction.getHash(), blockWithTransaction.Transactions.get(i).getHash())) model.addAttribute("bIsCoinbaseTransaction", i == 0);
            }

            ArrayList<String> pubKeyHashes = new ArrayList<>();

            for (TransactionOutput output : transaction.Outputs) {
                pubKeyHashes.add(UISCoinUtil.extractAddressFromScript(output.LockingScript));
            }
            model.addAttribute("pubKeyHashes", pubKeyHashes);

            ArrayList<Boolean> unspent = new ArrayList<>();

            ArrayList<TransactionOutput> outputs = transaction.Outputs;
            for (int i = 0; i < outputs.size(); i++) {
                try {
                    unspent.add(BlockChain.get().getUnspentTransactionOutput(transaction.getHash(), i) != null);
                }catch (Exception e){
                    unspent.add(false);
                }
            }
            model.addAttribute("unspent", unspent);
        } else {
            model.addAttribute("alerts", Collections.singletonList(new WebAlert("This is not a valid transaction!", WebAlert.AlertClasses.Danger)));
        }

        return "transaction";
    }

}