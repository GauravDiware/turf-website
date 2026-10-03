package com.gsdeveloper.bookmyslot.dto;

import java.util.List;

/** Dynamic public filter values derived from visible venues and playing areas. */
public record VenueFilterOptionsResponse(List<CityFilterOption> cities, List<String> games) { }
