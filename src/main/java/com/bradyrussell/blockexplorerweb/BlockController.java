package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.Util;
import com.bradyrussell.uiscoin.block.Block;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Date;

@Controller
public class BlockController {

	@GetMapping("/block")
	public String block(@RequestParam(name="hash", required=false) String BlockHash, @RequestParam(name="height", required=false) Integer BlockHeight, Model model) {
		Block block = null;
		if(BlockHash == null && BlockHeight == null){
			block = BlockChain.get().getBlockByHeight(BlockChain.get().BlockHeight);
		} else if(BlockHeight != null) {
			block = BlockChain.get().getBlockByHeight(BlockHeight);
		}
		else {
			byte[] blockHash = Util.Base64Decode(BlockHash);
			if (blockHash.length == 64) {
				block = BlockChain.get().getBlock(blockHash);
			}
		}

		if(block != null) {
			model.addAttribute("block", block);
			model.addAttribute("blockDate", new Date(block.Header.Time));
			model.addAttribute("blockHash",Util.Base64Encode(block.Header.getHash()));
		} else {
			model.addAttribute("alerts", Collections.singletonList(new WebAlert("This is not a valid block!", WebAlert.AlertClasses.Danger)));
		}

		return "block";
	}

}