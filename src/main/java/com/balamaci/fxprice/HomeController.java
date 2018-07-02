package com.balamaci.fxprice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

import static com.balamaci.fxprice.entity.CurrencyPair.EUR_CHF;
import static com.balamaci.fxprice.entity.CurrencyPair.EUR_JPY;
import static com.balamaci.fxprice.entity.CurrencyPair.EUR_USD;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("currencies",
				Arrays.asList(EUR_USD, EUR_CHF, EUR_JPY));
		return "index";
	}

}