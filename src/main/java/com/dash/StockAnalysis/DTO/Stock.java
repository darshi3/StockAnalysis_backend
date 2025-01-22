package com.dash.StockAnalysis.DTO;

import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
//@Data
//@ToString
public class Stock {
	String symbol;
	String name;
	int days;
}
