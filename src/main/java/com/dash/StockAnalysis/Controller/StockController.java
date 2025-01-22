package com.dash.StockAnalysis.Controller;

import java.net.URISyntaxException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dash.StockAnalysis.DTO.Stock;
import com.dash.StockAnalysis.Service.StockService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:3000") // Allow only React app
@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {

	private final StockService stockService;

	@GetMapping("/test")
	public String testing(Stock stock) throws URISyntaxException, JsonProcessingException {
		String response = stockService.getAllfaAllStockData(stock);

		log.info("ALFA WINTAGE API CALLED AND GET ANALYSIS DATA USING PROMPT ENGINEERING");
		return response;

	}

}
