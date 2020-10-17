package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.Util;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import com.bradyrussell.uiscoin.script.ScriptBuilder;
import com.bradyrussell.uiscoin.transaction.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ScriptController {

	@GetMapping("/script")
	public String script(@RequestParam(name="script", required=true) String ScriptEncoded, Model model) {
		byte[] Script = Util.Base64Decode(ScriptEncoded);
		String ScriptAsText = new ScriptBuilder(Script.length).data(Script).toText();

		if(Script != null) {
			model.addAttribute("scriptText", ScriptAsText.split("\n"));
		} else {
			model.addAttribute("error", "This is not a valid script!");
		}

		return "script";
	}

}