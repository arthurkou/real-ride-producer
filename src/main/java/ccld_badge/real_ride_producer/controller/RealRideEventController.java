package ccld_badge.real_ride_producer.controller;

import ccld_badge.real_ride_producer.domain.RealRideEvent;
import ccld_badge.real_ride_producer.service.RealRideEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RealRideEventController {

    private final RealRideEventProducer rideEventProducer;

    @Autowired
    public RealRideEventController(RealRideEventProducer rideEventProducer) {
        this.rideEventProducer = rideEventProducer;
    }

    @PostMapping("/real-rider")
    public ResponseEntity<RealRideEvent> postLibraryEvent(@RequestBody RealRideEvent realRideEvent) throws JsonProcessingException {
        rideEventProducer.send(realRideEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(realRideEvent);
    }
}
