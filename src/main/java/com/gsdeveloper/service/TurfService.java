package com.gsdeveloper.bookmyslot.service;

import com.gsdeveloper.bookmyslot.entity.Turf;
import com.gsdeveloper.bookmyslot.repository.TurfRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TurfService {

    private final TurfRepository turfRepository;

    public TurfService(TurfRepository turfRepository) {
        this.turfRepository = turfRepository;
    }

    // Create Turf
    public Turf saveTurf(Turf turf) {

        if (turfRepository.existsByNameAndLocation(
                turf.getName(),
                turf.getLocation())) {

            throw new RuntimeException("Turf already exists.");
        }

        return turfRepository.save(turf);
    }

    // Get All Turfs
    public List<Turf> getAllTurfs() {
        return turfRepository.findAll();
    }

    // Get Turf ById
    public Turf getTurfById(UUID id) {
        return turfRepository.findById(id).orElse(null);
    }

    // Update Turf
    public Turf updateTurf(UUID id, Turf updatedTurf) {

        Turf turf = turfRepository.findById(id).orElse(null);

        if (turf != null) {

            turf.setName(updatedTurf.getName());
            turf.setLocation(updatedTurf.getLocation());
            turf.setPricePerHour(updatedTurf.getPricePerHour());
            turf.setSportType(updatedTurf.getSportType());
            turf.setDescription(updatedTurf.getDescription());
            turf.setAvailable(updatedTurf.isAvailable());

            return turfRepository.save(turf);
        }

        return null;
    }

    // Delete Turf
    public void deleteTurf(UUID id) {
        turfRepository.deleteById(id);
    }

    public void findById(UUID id) {
        turfRepository.findById(id);
    }
}