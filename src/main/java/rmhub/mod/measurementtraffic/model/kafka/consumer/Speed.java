package rmhub.mod.measurementtraffic.model.kafka.consumer;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "direction",
    "lane",
    "value"
})
public class Speed {

    @JsonProperty("direction")
    private String direction;
    @JsonProperty("lane")
    private String lane;
    @JsonProperty("value")
    private Integer value;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("direction")
    public String getDirection() {
        return direction;
    }

    @JsonProperty("direction")
    public void setDirection(String direction) {
        this.direction = direction;
    }

    @JsonProperty("lane")
    public String getLane() {
        return lane;
    }

    @JsonProperty("lane")
    public void setLane(String lane) {
        this.lane = lane;
    }

    @JsonProperty("value")
    public Integer getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Integer value) {
        this.value = value;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
