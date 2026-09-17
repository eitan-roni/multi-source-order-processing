package com.eitan.orders.service;

import com.eitan.orders.model.CountryInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CountryServiceTest {

    private final CountryService countryService = new CountryService();

    @Test
    void mapsUsToUnitedStatesAndUsd() {
        CountryInfo result = countryService.getCountryInfo("US");

        assertEquals("United States", result.countryName());
        assertEquals("USD", result.currency());
    }

    @Test
    void mapsGbToUnitedKingdomAndGbp() {
        CountryInfo result = countryService.getCountryInfo("GB");

        assertEquals("United Kingdom", result.countryName());
        assertEquals("GBP", result.currency());
    }

    @Test
    void mapsDeToGermanyAndEur() {
        CountryInfo result = countryService.getCountryInfo("DE");

        assertEquals("Germany", result.countryName());
        assertEquals("EUR", result.currency());
    }

    @Test
    void throwsIllegalArgumentExceptionForUnsupportedCountryCode() {
        assertThrows(IllegalArgumentException.class, () -> countryService.getCountryInfo("IL"));
    }
}
