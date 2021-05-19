package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.BytesUtil;
import com.bradyrussell.uiscoin.address.UISCoinAddress;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import com.bradyrussell.uiscoin.blockchain.exception.NoSuchTransactionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;

@Controller
public class AddressController {

    @GetMapping("/address")
    public String address(@RequestParam(name = "address", required = true) String AddressString, Model model, HttpServletRequest request) {
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

        byte[] address = BytesUtil.Base64Decode(AddressString);
        UISCoinAddress.DecodedAddress decodedAddress = UISCoinAddress.decodeAddress(address);

        model.addAttribute("address", AddressString);
        model.addAttribute("decoded", decodedAddress);

        if(!UISCoinAddress.verifyAddressChecksum(address)) model.addAttribute("alerts",Collections.singletonList(new WebAlert("This is not a valid address!", WebAlert.AlertClasses.Danger)));

        ArrayList<byte[]> unspentOutputsToAddress = BlockChain.get().matchUTXOForP2PKHAddress(decodedAddress.HashData);
        ArrayList<UTXO> utxos = new ArrayList<>();

        long totalAmount = 0;

        for (byte[] toAddress : unspentOutputsToAddress) {
            byte[] TsxnHash = new byte[64];
            byte[] AmountBytes = new byte[4];

            System.arraycopy(toAddress, 0, TsxnHash, 0, 64);
            System.arraycopy(toAddress, 64, AmountBytes, 0, 4);

            long amount = 0;
            try {
                amount = BlockChain.get().getUnspentTransactionOutput(TsxnHash, BytesUtil.ByteArrayToNumber32(AmountBytes)).Amount;
                utxos.add(new UTXO(TsxnHash, BytesUtil.ByteArrayToNumber32(AmountBytes),amount));
                totalAmount+=amount;
            } catch (NoSuchTransactionException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("total", totalAmount);
        model.addAttribute("utxos", utxos);

        model.addAttribute("alerts", alerts);
        return "address";
    }

}