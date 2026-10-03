package com.gsdeveloper.bookmyslot.repository;

/** Projection returned directly by the active-venue city-count query. */
public interface CityTurfCount {
    String getCity();
    long getTurfCount();
}
