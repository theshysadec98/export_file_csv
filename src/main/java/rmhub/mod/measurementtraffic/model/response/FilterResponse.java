package rmhub.mod.measurementtraffic.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterResponse<T> {

  private Integer page;
  private Integer pageElements;
  private Integer size;
  private Integer totalPages;
  private Long totalElements;
  private List<String> orders;
  @JsonIgnore
  private List<T> data;

  public Map<String, Object> paginationMap() {
    Map<String, Object> pagination = new HashMap<>();
    if (page != null) {
      pagination.put("page", page);
    }
    if (pageElements != null) {
      pagination.put("pageElements", pageElements);
    }
    if (size != null) {
      pagination.put("size", size);
    }
    if (totalPages != null) {
      pagination.put("totalPages", totalPages);
    }
    if (totalElements != null) {
      pagination.put("totalElements", totalElements);
    }
    if (orders != null) {
      pagination.put("orders", orders);
    }
    return pagination;
  }
}
