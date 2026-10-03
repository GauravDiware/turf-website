package com.gsdeveloper.bookmyslot.controller;

import com.gsdeveloper.bookmyslot.entity.Turf;
import com.gsdeveloper.bookmyslot.service.TurfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/turfs")
public class TurfController {

    private final TurfService turfService;

    public TurfController(TurfService turfService) {
        this.turfService = turfService;
    }


    // Create Turf
    @PostMapping
    public ResponseEntity<?> createTurf(@RequestBody Turf turf) {

        try {
            Turf savedTurf = turfService.saveTurf(turf);
            return new ResponseEntity<>(savedTurf, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }


    // Get Turf ById
    @GetMapping("/{id}")
    public ResponseEntity<Turf> getTurfById(@PathVariable UUID id) {

      Turf turf =  turfService.getTurfById(id);

      if(turf == null){
          return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
      return ResponseEntity.ok(turf);
    }


    // Update Turf
    @PutMapping("/{id}")
    public Turf updateTurf(@PathVariable UUID id,
                           @RequestBody Turf turf) {



        return turfService.updateTurf(id, turf);
    }

    // Delete Turf
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTurf(@PathVariable UUID id) {

        Turf turf = turfService.getTurfById(id);

        if (turf == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Turf not found.");
        }

        turfService.deleteTurf(id);

        return ResponseEntity.ok("Turf deleted successfully.");
    }
}
