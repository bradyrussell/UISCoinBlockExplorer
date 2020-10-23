package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.Util;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import com.bradyrussell.uiscoin.script.ScriptBuilder;
import com.bradyrussell.uiscoin.transaction.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Controller
public class ScriptController {

	@GetMapping("/script")
	public String script(@RequestParam(name="script", required=true) String ScriptEncoded, Model model, HttpServletRequest request) {
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

		byte[] Script = Util.Base64Decode(ScriptEncoded);
		String ScriptAsText = new ScriptBuilder(Script.length).data(Script).toText();

		if(ScriptAsText != null) {
			model.addAttribute("scriptText", ScriptAsText.split("\n"));
		} else {
			model.addAttribute("error", "This is not a valid script!");
		}
		model.addAttribute("alerts", alerts);
		return "script";
	}

}