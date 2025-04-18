package com.dash.StockAnalysis.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.StringBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.dash.StockAnalysis.DTO.Stock;
import io.github.mainstringargs.alphavantagescraper.AlphaVantageConnector;
import io.github.mainstringargs.alphavantagescraper.TimeSeries;
import io.github.mainstringargs.alphavantagescraper.input.timeseries.OutputSize;
import io.github.mainstringargs.alphavantagescraper.output.AlphaVantageException;
import io.github.mainstringargs.alphavantagescraper.output.timeseries.data.StockData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

	@Value("${api.key}")
	public String apiKey;

	@Value("${openaiapi.key}")
	public String openAiApiKey;

	private final com.dash.StockAnalysis.DTO.Stock stockRequest;

	public StringBuilder stockDataString = new StringBuilder();

	public String getAllfaAllStockData(Stock stock) throws URISyntaxException, JsonProcessingException {

		log.info("API key = " + apiKey);
		log.info("Open Ai key = " + openAiApiKey);
		log.info("Symbol = " + stock.getSymbol());
		log.info("Days = " + stock.getDays());

		int timeout = 3000;
		AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
		TimeSeries stockTimeSeries = new TimeSeries(apiConnector);

		try {

			io.github.mainstringargs.alphavantagescraper.output.timeseries.Daily response = stockTimeSeries
					.daily(stock.getSymbol(), OutputSize.COMPACT);
			Map<String, String> metaData = response.getMetaData();
			System.out.println("Information: " + metaData.get("1. Information"));
			System.out.println("Stock: " + metaData.get("2. Symbol"));

			List<StockData> stockData = response.getStockData().stream().limit(stock.getDays()).toList();

			stockData.forEach(stocks -> {
				System.out.println("date:   " + stocks.getDateTime());
				System.out.println("open:   " + stocks.getOpen());
				System.out.println("high:   " + stocks.getHigh());
				System.out.println("low:    " + stocks.getLow());
				System.out.println("close:  " + stocks.getClose());
				System.out.println("volume: " + stocks.getVolume());
			});

			String generatedAnalysis = generate(stock, stockData);
			return generatedAnalysis;

		} catch (AlphaVantageException e) {
			System.out.println("something went wrong" + e.getStackTrace() + e.getMessage() + "cause = " + e.getCause());
		}
		return null;
	}

	public String generate(Stock stock, List<StockData> stockData) throws URISyntaxException, JsonProcessingException {

		String requirements = "### Requirements: 1. Provide detailed analysis, including key statistics, 2. Identify the stock's trend based on the given days' data(from today to last given days), 3. Forecast the short-term trend for TCS based on historical data. Complete the sentence at last.(whenever sentence ends start new one from new line.)";

		List<Map<String, Object>> stockDataJsonList = new ArrayList<>();
		// Assuming stockData is a list of stock objects with getter methods
		stockData.forEach(stocks -> {
			// Creating a map for each stock data record
			Map<String, Object> stockRecord = new HashMap<>();
			stockRecord.put("date", stocks.getDateTime());
			stockRecord.put("open", stocks.getOpen());
			stockRecord.put("high", stocks.getHigh());
			stockRecord.put("low", stocks.getLow());
			stockRecord.put("close", stocks.getClose());
			stockRecord.put("volume", stocks.getVolume());

			// Add this record to the list
			stockDataJsonList.add(stockRecord);
		});

		// Convert the list of maps to a JSON string using Jackson's ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		String jsonResult = objectMapper.writeValueAsString(stockDataJsonList);

		// Now jsonResult is a valid JSON string
		System.out.println("JSON Result -> " + jsonResult);

		String result = jsonResult.toString();
		log.info("Result = " + result);

		if (result != null) {

			String prompt = String.format(
					"Analyze the stock performance for the following details: 1. Stock Symbol: %s, 2. Number of Days(From Today): %d (last %d days of data), 3. Time Series Data: %s, requirements: %s. ",
					stock.getSymbol(), stock.getDays(), stock.getDays(), result, requirements, 400);

			System.out.println("prompt is " + prompt);

			String generatedAnalysis = callOpenAiApi(prompt);
			System.out.println("AAAA  generatedAnalysis" + generatedAnalysis);

			return generatedAnalysis;
		}
		return null;
	}

	private String callOpenAiApi(String prompt) throws URISyntaxException, JsonProcessingException {

		URI uri = new URI("https://api.openai.com/v1/chat/completions");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + openAiApiKey);
		headers.set("Content-Type", "application/json");

		// Creating the JSON body using ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode messageNode = objectMapper.createObjectNode();
		((ObjectNode) messageNode).put("role", "user");
		((ObjectNode) messageNode).put("content", prompt);

		// Create the root JSON object with the model and messages
		JsonNode rootNode = objectMapper.createObjectNode();
		((ObjectNode) rootNode).put("model", "gpt-3.5-turbo");
		((ObjectNode) rootNode).set("messages", objectMapper.createArrayNode().add(messageNode));
		((ObjectNode) rootNode).put("max_tokens", 400);

		// Convert the JSON to a string
		String requestBody = objectMapper.writeValueAsString(rootNode);
		System.out.println("Request Body: " + requestBody);

		// Send the request
		HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

		String generatedText = extractGeneratedText(response.getBody());
		return generatedText;

	}

	private String extractGeneratedText(String responseBody) {

		try {
			ObjectMapper mapper = new ObjectMapper();

			// Parse the JSON response
			JsonNode rootNode = mapper.readTree(responseBody);

			// Navigate through the JSON tree to extract the generated text
			JsonNode choicesNode = rootNode.path("choices");
			if (choicesNode.isArray() && choicesNode.size() > 0) {
				JsonNode firstChoice = choicesNode.get(0);
				JsonNode messageNode = firstChoice.path("message");
				// itineraryRepository.save(responseBody);

				System.out.println("THE PLAIN TEXT IS " + messageNode.path("content").asText());
				return messageNode.path("content").asText();
			}
			return "No content found";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error parsing response";
		}
	}

}
