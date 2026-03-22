package com.gautama.bankhitsaccount.service;

import com.gautama.bankhitsaccount.model.AccountCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {
    private static final Pattern VALUTE_PATTERN = Pattern.compile(
            "<Valute[^>]*>.*?<CharCode>%s</CharCode>.*?<VunitRate>([^<]+)</VunitRate>.*?</Valute>",
            Pattern.DOTALL
    );

    private final RestClient.Builder restClientBuilder;

    @Value("${exchange-rate.api.daily-url}")
    private String dailyRatesUrl;

    public BigDecimal convert(AccountCurrency from, AccountCurrency to, BigDecimal amount) {
        if (from == to) {
            return amount;
        }

        BigDecimal rate = getRate(from, to);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getRate(AccountCurrency from, AccountCurrency to) {
        if (from == to) {
            return BigDecimal.ONE;
        }

        String xml = restClientBuilder.build()
                .get()
                .uri(dailyRatesUrl)
                .retrieve()
                .body(String.class);

        if (xml == null || xml.isBlank()) {
            throw new IllegalStateException("Failed to fetch exchange rates from CBR");
        }

        BigDecimal fromRateInRub = getRateInRub(xml, from);
        BigDecimal toRateInRub = getRateInRub(xml, to);

        return fromRateInRub.divide(toRateInRub, 10, RoundingMode.HALF_UP);
    }

    private BigDecimal getRateInRub(String xml, AccountCurrency currency) {
        if (currency == AccountCurrency.RUB) {
            return BigDecimal.ONE;
        }

        Pattern currencyPattern = Pattern.compile(String.format(VALUTE_PATTERN.pattern(), currency.name()), Pattern.DOTALL);
        Matcher matcher = currencyPattern.matcher(xml);
        if (!matcher.find()) {
            throw new IllegalStateException("Currency " + currency + " is not available in CBR daily rates");
        }

        return new BigDecimal(matcher.group(1).replace(',', '.'));
    }
}
