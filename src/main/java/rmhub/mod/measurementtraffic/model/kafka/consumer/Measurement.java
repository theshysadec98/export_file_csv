
package rmhub.mod.measurementtraffic.model.kafka.consumer;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data",
    "highway",
    "name",
    "timestamp"
})
public class Measurement {

    @JsonProperty("data")
    private Data data;
    @JsonProperty("highway")
    private String highway;
    @JsonProperty("name")
    private String name;
    @JsonProperty("timestamp")
    private Timestamp timestamp;

    @JsonProperty("data")
    public Data getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Data data) {
        this.data = data;
    }

    @JsonProperty("highway")
    public String getHighway() {
        return highway;
    }

    @JsonProperty("highway")
    public void setHighway(String highway) {
        this.highway = highway;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
