package com.astarlabs.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	
	@RequestMapping("/inicio")
	public String execute(){
		System.out.println("Executou...");
		return "inicio";
	}

}
