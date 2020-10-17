package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.Conversions;
import com.bradyrussell.uiscoin.Util;
import com.bradyrussell.uiscoin.address.UISCoinAddress;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import com.bradyrussell.uiscoin.transaction.Transaction;
import com.bradyrussell.uiscoin.transaction.TransactionOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

@Controller
public class AddressController {

    @GetMapping("/address")
    public String address(@RequestParam(name = "address", required = true) String AddressString, Model model) {
        byte[] address = Util.Base64Decode(AddressString);
        UISCoinAddress.DecodedAddress decodedAddress = UISCoinAddress.decodeAddress(address);

        model.addAttribute("address", AddressString);
        model.addAttribute("decoded", decodedAddress);

        if(!UISCoinAddress.verifyAddressChecksum(address)) model.addAttribute("alerts",Collections.singletonList(new WebAlert("This is not a valid address!", WebAlert.AlertClasses.Danger)));

        ArrayList<byte[]> unspentOutputsToAddress = BlockChain.get().ScanUnspentOutputsToAddress(decodedAddress.PublicKeyHash);
        ArrayList<UTXO> utxos = new ArrayList<>();

        long totalAmount = 0;

        for (byte[] toAddress : unspentOutputsToAddress) {
            byte[] TsxnHash = new byte[64];
            byte[] AmountBytes = new byte[4];

            System.arraycopy(toAddress, 0, TsxnHash, 0, 64);
            System.arraycopy(toAddress, 64, AmountBytes, 0, 4);

            long amount = BlockChain.get().getUnspentTransactionOutput(TsxnHash, Util.ByteArrayToNumber(AmountBytes)).Amount;
            utxos.add(new UTXO(TsxnHash,Util.ByteArrayToNumber(AmountBytes),amount));
            totalAmount+=amount;
        }
        model.addAttribute("total", totalAmount);
        model.addAttribute("utxos", utxos);

        return "address";
    }

}