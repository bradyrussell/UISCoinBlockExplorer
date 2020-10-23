package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.Util;
import com.bradyrussell.uiscoin.script.ScriptBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class DisableCookiesController {

	@GetMapping("/disablecookies")
	public String script(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if(session.getAttribute("bAcceptCookies") == null) {
			session.setAttribute("bAcceptCookies", false);
		}
		return "nocookies";
	}

}