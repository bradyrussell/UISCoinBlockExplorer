package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.Util;
import com.bradyrussell.uiscoin.block.Block;
import com.bradyrussell.uiscoin.blockchain.BlockChain;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.regex.Pattern;

@Controller
public class SearchController {

	@PostMapping("/search")
	public String search(@RequestParam(name="search", required=true) String Query, @RequestParam(name="type", required=true) String QueryType, Model model) {
		if(Query.isEmpty() || Query.isBlank()) return "redirect:/";
		return "redirect:/"+ QueryType + "?" + (QueryType.equalsIgnoreCase("script") ? "script" : (QueryType.equalsIgnoreCase("address") ? "address" : (QueryType.equalsIgnoreCase("block") && isNumeric(Query) ? "height" : "hash"))) + "=" + Query;
	}

	private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	public boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		return pattern.matcher(strNum).matches();
	}
}