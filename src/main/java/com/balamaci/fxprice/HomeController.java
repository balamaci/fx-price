package com.balamaci.fxprice;

import com.balamaci.fxprice.entity.CurrencyPair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.balamaci.fxprice.entity.CurrencyPair.*;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home(Model model) {

		Map<Integer, List<CurrencyPair>> currenciesMap = new LinkedHashMap<>();
		currenciesMap.put(1, Arrays.asList(EUR_USD, EUR_CHF, EUR_GBP));
		currenciesMap.put(2, Arrays.asList(EUR_JPY, GBP_USD, EUR_SGD));

		model.addAttribute("currenciesMap", currenciesMap);
		return "index";
	}

}