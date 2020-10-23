package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.Util;
import com.bradyrussell.uiscoin.block.Block;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import com.bradyrussell.uiscoin.blockchain.exception.NoSuchBlockException;
import com.bradyrussell.uiscoin.blockchain.exception.NoSuchTransactionException;
import com.bradyrussell.uiscoin.transaction.Transaction;
import com.bradyrussell.uiscoin.transaction.TransactionInput;
import com.bradyrussell.uiscoin.transaction.TransactionOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

@Controller
public class TransactionController {

    @GetMapping("/transaction")
    public String transaction(@RequestParam(name = "hash", required = true) String TransactionHash, Model model, HttpServletRequest request) {
        //////// GDPR Cookies Notice ////////
        ArrayList<WebAlert> alerts = new ArrayList<>();
        HttpSession session = request.getSession();
        if(session.getAttribute("bAcceptCookies") == null) {
            alerts.add(new WebAlert("This website uses cookies. You can configure them here: ", WebAlert.AlertClasses.Warning, "<a class=\"btn btn-success btn-sm\" href=\"/enablecookies\">Allow</a><a class=\"btn btn-danger btn-sm\" href=\"/disablecookies\">Deny</a>"));
        } else {
            if(!((boolean) session.getAttribute("bAcceptCookies"))) {
                return "redirect:/disablecookies";
            }
        }
        /////////////////////////////////////

        Transaction transaction = null;
        if (TransactionHash != null) {
            byte[] transactionHash = Util.Base64Decode(TransactionHash);
            if (transactionHash.length == 64) {
                try {
                    transaction = BlockChain.get().getTransaction(transactionHash);
                } catch (NoSuchTransactionException | NoSuchBlockException e) {
                    alerts.add(new WebAlert(e.getMessage(), WebAlert.AlertClasses.Danger));
                }
            }
        }

        if (transaction != null) {
            Block blockWithTransaction = null;
            try {
                blockWithTransaction = BlockChain.get().getBlockWithTransaction(transaction.getHash());
                model.addAttribute("block", blockWithTransaction);
                for (int i = 0; i < blockWithTransaction.Transactions.size(); i++) {
                    if(Arrays.equals(transaction.getHash(), blockWithTransaction.Transactions.get(i).getHash())) model.addAttribute("bIsCoinbaseTransaction", i == 0);
                }
            } catch (NoSuchBlockException e) {
                e.printStackTrace();
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
            model.addAttribute("transaction", transaction);

            model.addAttribute("transactionDate", new Date(transaction.TimeStamp));
            model.addAttribute("transactionHash", Util.Base64Encode(transaction.getHash()));

        } else {
            model.addAttribute("alerts", Collections.singletonList(new WebAlert("This is not a valid transaction!", WebAlert.AlertClasses.Danger)));
        }
        model.addAttribute("alerts", alerts);
        return "transaction";
    }

}