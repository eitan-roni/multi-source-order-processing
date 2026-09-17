package com.eitan.orders.service;

import com.eitan.orders.model.CountryInfo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CountryService {

    private final Map<String, CountryInfo> countryInfoByCode = Map.of(
            "US", new CountryInfo("United States", "USD"),
            "GB", new CountryInfo("United Kingdom", "GBP"),
            "DE", new CountryInfo("Germany", "EUR")
    );

    public CountryInfo getCountryInfo(String countryCode) {
        CountryInfo countryInfo = countryInfoByCode.get(countryCode);
        if (countryInfo == null) {
            throw new IllegalArgumentException("Unsupported country code: " + countryCode);
        }
        return countryInfo;
    }
}
