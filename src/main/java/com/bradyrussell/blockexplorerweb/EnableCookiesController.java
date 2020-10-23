package com.bradyrussell.blockexplorerweb;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class EnableCookiesController {

	@GetMapping("/enablecookies")
	public String script(Model model, HttpServletRequest request, @RequestParam(name="redirect", required=false) String Redirect) {
		HttpSession session = request.getSession();
		if(session.getAttribute("bAcceptCookies") == null) {
			session.setAttribute("bAcceptCookies", true);
		}
		if(Redirect != null && Redirect.startsWith("/")) Redirect = Redirect.substring(1); // ensure we only link to on site
		return Redirect == null || Redirect.isEmpty() ? "redirect:/" : "redirect:"+Redirect;
	}

}