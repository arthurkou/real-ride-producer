package real_ride_producer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RealRideEvent {

    private Integer rideID;
    private Integer driverID;
    private Integer passengerID;
    private Long pickupTime;
    private String pickupLocation;
    private Long arrivalTime;
    private String arrivalLocation;
    private RideStatusType status;
}
