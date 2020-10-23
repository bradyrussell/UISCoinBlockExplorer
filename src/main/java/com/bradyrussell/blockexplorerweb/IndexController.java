package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.block.Block;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import com.bradyrussell.uiscoin.blockchain.exception.NoSuchBlockException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class IndexController {

	@GetMapping("/")
	public String index(Model model, HttpServletRequest request) {
		ArrayList<WebAlert> alerts = new ArrayList<>();

		HttpSession session = request.getSession();

		long NumTransactions = 0;
		long BlockChainSize = 0; // todo slow cache like blockchainsize-n where n is height
		try {
			for (Block block : BlockChain.get().getBlockChainFromHeight(0)) {
				BlockChainSize += block.getSize();
				NumTransactions += block.Transactions.size();
			}
		} catch (NoSuchBlockException e) {
			e.printStackTrace();
		}
		model.addAttribute("blockchainSize", BlockChainSize);
		model.addAttribute("numTransactions", NumTransactions);
		model.addAttribute("blockHeight", BlockChain.get().BlockHeight);

		String sessionAttr = "randomNumber";
		if(session.getAttribute(sessionAttr) == null) session.setAttribute(sessionAttr, ThreadLocalRandom.current().nextInt(0,256));

		//////// GDPR Cookies Notice ////////
		if(session.getAttribute("bAcceptCookies") == null) {
			alerts.add(new WebAlert("This website uses cookies. You can configure them here: ", WebAlert.AlertClasses.Warning, "<a class=\"btn btn-success btn-sm\" href=\"/enablecookies\">Allow</a><a class=\"btn btn-danger btn-sm\" href=\"/disablecookies\">Deny</a>"));
		} else {
			if(!((boolean) session.getAttribute("bAcceptCookies"))) {
				return "redirect:/disablecookies";
			}
		}
		/////////////////////////////////////


		model.addAttribute("alerts", alerts);
		return "greeting";
	}

}