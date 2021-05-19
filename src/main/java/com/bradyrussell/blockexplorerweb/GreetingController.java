package com.bradyrussell.blockexplorerweb;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class GreetingController {

	@GetMapping("/greeting")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model, HttpServletRequest request) {
		ArrayList<WebAlert> alerts = new ArrayList<>();


		HttpSession session = request.getSession();

		String sessionAttr = "randomNumber";
		if(session.getAttribute(sessionAttr) == null) session.setAttribute(sessionAttr, ThreadLocalRandom.current().nextInt(0,256));

		alerts.add(new WebAlert("Your lucky number is "+ session.getAttribute(sessionAttr)+".", WebAlert.AlertClasses.Info));
		alerts.add(new WebAlert("Your user is: "+request.getRemoteUser(), WebAlert.AlertClasses.Success));
		alerts.add(new WebAlert("Your AT is: "+request.getAuthType(), WebAlert.AlertClasses.Danger));
		model.addAttribute("alerts", alerts);
		return "greeting";
	}

}