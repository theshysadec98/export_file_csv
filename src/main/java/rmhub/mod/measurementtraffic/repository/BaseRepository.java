package rmhub.mod.measurementtraffic.repository;

import java.util.Iterator;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class BaseRepository {

  @PersistenceContext
  protected EntityManager entityManager;

  protected void appendSortQuery(Pageable pageable, StringBuilder sql) {
    appendSortQuery("", pageable, sql);
  }

  protected void appendSortQuery(String prefix, Pageable pageable, StringBuilder sql) {
    if ( pageable != null ) {
      pageable.getSort();
      Sort sort = pageable.getSort();
      if ( !sort.iterator().hasNext() ) {
        return;
      }
      sql.append(" ORDER BY ");
      for ( Iterator<Sort.Order> i = sort.iterator(); i.hasNext(); ) {
        Sort.Order order = i.next();
        sql.append(String.format(" %s%s %s", prefix, order.getProperty(), order.getDirection().isAscending() ? "ASC" : "DESC"));
        if ( i.hasNext() ) {
          sql.append(", ");
        }
      }
    }
  }

  protected void appendPageQuery(Pageable pageable, Query query) {
    if (pageable != null) {
      int page = pageable.getPageNumber();
      int pageSize = pageable.getPageSize();
      query.setFirstResult(page * pageSize);
      query.setMaxResults(pageSize);
    }
  }
}
