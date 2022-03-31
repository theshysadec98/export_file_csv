
package rmhub.mod.measurementtraffic.model.kafka.consumer;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "saturation",
    "speed",
    "vehicles"
})
public class Data {

    @JsonProperty("saturation")
    private List<Saturation> saturation = null;
    @JsonProperty("speed")
    private List<Speed> speed = null;
    @JsonProperty("vehicles")
    private List<Vehicle> vehicles = null;

    @JsonProperty("saturation")
    public List<Saturation> getSaturation() {
        return saturation;
    }

    @JsonProperty("saturation")
    public void setSaturation(List<Saturation> saturation) {
        this.saturation = saturation;
    }

    @JsonProperty("speed")
    public List<Speed> getSpeed() {
        return speed;
    }

    @JsonProperty("speed")
    public void setSpeed(List<Speed> speed) {
        this.speed = speed;
    }

    @JsonProperty("vehicles")
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    @JsonProperty("vehicles")
    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

}
