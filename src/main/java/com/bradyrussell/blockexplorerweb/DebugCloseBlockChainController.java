package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.Util;
import com.bradyrussell.uiscoin.address.UISCoinAddress;
import com.bradyrussell.uiscoin.address.UISCoinKeypair;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.interfaces.ECPublicKey;

@Controller
public class DebugCloseBlockChainController {

	@GetMapping("/close")
	public String close(Model model) {
		BlockChain.get().close();
		return "close";
	}

}