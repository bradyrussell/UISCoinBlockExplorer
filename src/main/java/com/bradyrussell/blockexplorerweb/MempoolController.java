package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.Util;
import com.bradyrussell.uiscoin.address.UISCoinAddress;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import com.bradyrussell.uiscoin.transaction.Transaction;
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
import java.util.List;

@Controller
public class MempoolController {

    @GetMapping("/mempool")
    public String mempool(@RequestParam(name = "hash", required = false) String MempoolTransactionHash, Model model, HttpServletRequest request) {
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

        List<Transaction> mempool = BlockChain.get().getMempool();

        if(MempoolTransactionHash != null) {
            for (Transaction transaction : mempool) {
                if (Util.Base64Encode(transaction.getHash()).equals(MempoolTransactionHash)) {
                    model.addAttribute("mempoolTransaction", transaction);

                    ArrayList<String> pubKeyHashes = new ArrayList<>();

                    for (TransactionOutput output : transaction.Outputs) {
                        pubKeyHashes.add(UISCoinUtil.extractAddressFromScript(output.LockingScript));
                    }
                    model.addAttribute("pubKeyHashes", pubKeyHashes);
                }
            }
            if(!model.containsAttribute("mempoolTransaction")) {
                try {
                    if (BlockChain.get().getTransaction(Util.Base64Decode(MempoolTransactionHash)) != null) {
                        model.addAttribute("alerts", new WebAlert("This transaction is no longer in the MemPool because it was included in a block!", WebAlert.AlertClasses.Info, "<a class=\"btn btn-primary btn-sm\" href=\"" + "/transaction?hash=" + MempoolTransactionHash + "\" role=\"button\"><img src=\"/icons/cash.svg\" alt=\"Block\"> Go to Transaction</a>"));
                    }
                } catch (Exception e) {
                    model.addAttribute("alerts", Arrays.asList(new WebAlert("That transaction does not exist in the MemPool.", WebAlert.AlertClasses.Danger), new WebAlert(e.toString(), WebAlert.AlertClasses.Warning)));
                }
            }
        }

        model.addAttribute("mempool", mempool);
/*        if(mempool.size() == 0) {
            model.addAttribute("mempool", mempool);
        } else {
            model.addAttribute("mempool", mempool.subList(0,Math.min(mempool.size()-1,50)));
        }*/
        model.addAttribute("alerts", alerts);
        return "mempool";
    }

}