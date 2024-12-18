package ccld_badge.real_ride_producer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RealRideEvent {

    private Integer rideID;
    private Integer driverID;
    private Integer passengerID;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime pickupTime;

    private String pickupLocation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime arrivalTime;

    private String arrivalLocation;
    private RideStatusType status;
}
