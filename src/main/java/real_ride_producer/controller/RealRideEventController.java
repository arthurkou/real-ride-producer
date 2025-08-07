package real_ride_producer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import real_ride_producer.domain.RealRideEvent;
import real_ride_producer.producer.RealRideEventProducer;

@RestController
public class RealRideEventController {

    private final RealRideEventProducer rideEventProducer;

    public RealRideEventController(RealRideEventProducer rideEventProducer) {
        this.rideEventProducer = rideEventProducer;
    }

    @PostMapping("/real-rider")
    public ResponseEntity<RealRideEvent> postRealRideEvent(@RequestBody RealRideEvent realRideEvent) {
        rideEventProducer.send(realRideEvent);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
