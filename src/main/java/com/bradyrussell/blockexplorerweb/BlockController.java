package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.BytesUtil;
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
import java.util.Collections;
import java.util.Date;

@Controller
public class BlockController {

	@GetMapping("/block")
	public String block(@RequestParam(name="hash", required=false) String BlockHash, @RequestParam(name="height", required=false) Integer BlockHeight, Model model, HttpServletRequest request) {
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

		Block block = null;
		if(BlockHash == null && BlockHeight == null){
			try {
				block = BlockChain.get().getBlockByHeight(BlockChain.get().BlockHeight);
			} catch (NoSuchBlockException e) {
				e.printStackTrace();
			}
		} else if(BlockHeight != null) {
			try {
				block = BlockChain.get().getBlockByHeight(BlockHeight);
			} catch (NoSuchBlockException e) {
				e.printStackTrace();
			}
		}
		else {
			byte[] blockHash = BytesUtil.Base64Decode(BlockHash);
			if (blockHash.length == 64) {
				try {
					block = BlockChain.get().getBlock(blockHash);
				} catch (NoSuchBlockException e) {
					e.printStackTrace();
				}
			}
		}

		if(block != null) {
			model.addAttribute("block", block);
			model.addAttribute("blockDate", new Date(block.Header.Time));
			model.addAttribute("blockHash", BytesUtil.Base64Encode(block.Header.getHash()));
		} else {
			model.addAttribute("alerts", Collections.singletonList(new WebAlert("This is not a valid block!", WebAlert.AlertClasses.Danger)));
		}
		model.addAttribute("alerts", alerts);
		return "block";
	}

}