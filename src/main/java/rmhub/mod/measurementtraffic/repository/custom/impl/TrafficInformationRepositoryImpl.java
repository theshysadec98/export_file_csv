package rmhub.mod.measurementtraffic.repository.custom.impl;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import rmhub.mod.measurementtraffic.model.entity.MeasurementInformation;
import rmhub.mod.measurementtraffic.model.TrafficSearchRequest;
import rmhub.mod.measurementtraffic.model.DirectionRequest;
import rmhub.mod.measurementtraffic.repository.BaseRepository;
import rmhub.mod.measurementtraffic.repository.custom.TrafficInformationRepositoryCustom;

public class TrafficInformationRepositoryImpl extends BaseRepository implements TrafficInformationRepositoryCustom {

  @Override
  public Page<Timestamp> filterPeriodDate(TrafficSearchRequest searchRequest, Pageable pageable) {
    Query countingQuery = generateSearchingQuery(searchRequest, pageable, true);
    List<Long> totalList = countingQuery.getResultList();
    if (CollectionUtils.isEmpty(totalList)) {
      return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }
    long total = totalList.size();
    Query query = generateSearchingQuery(searchRequest, pageable, false);
    List<Date> results = query.getResultList();
    return new PageImpl(results, pageable, total);
  }

  private Query generateSearchingQuery(TrafficSearchRequest searchRequest, Pageable pageable, boolean isCounting) {
    StringBuilder sql = new StringBuilder();
    if (isCounting) {
      sql.append("SELECT COUNT(o.periodDate) FROM TrafficInformation o ");
    } else {
      sql.append("SELECT o.periodDate FROM TrafficInformation o ");
    }
    if (!StringUtils.isEmpty(searchRequest.getHighway()) || !CollectionUtils.isEmpty(searchRequest.getDeviceNames())) {
      sql.append(" JOIN o.device d ");
    }
    if (!CollectionUtils.isEmpty(searchRequest.getDirections())) {
      sql.append(" JOIN o.directionLane dl ");
    }
    sql.append(" WHERE 1=1 ");
    if (!CollectionUtils.isEmpty(searchRequest.getDeviceNames())) {
      sql.append(" AND d.name IN :deviceNames ");
    }
    if (!StringUtils.isEmpty(searchRequest.getHighway())) {
      sql.append(" AND d.highway = :highway ");
    }
    if (searchRequest.getFromTimestamp() != null) {
      sql.append(" AND o.periodDate >= :fromTimestamp ");
    }
    if (searchRequest.getToTimestamp() != null) {
      sql.append(" AND o.periodDate <= :toTimestamp ");
    }
    if (!CollectionUtils.isEmpty(searchRequest.getDirections())) {
      sql.append(" AND dl.direction IN :directions ");
      Set<String> lanes = searchRequest.getDirections().stream()
          .map(DirectionRequest::getLane).flatMap(List::stream)
          .collect(Collectors.toSet());
      if (!CollectionUtils.isEmpty(lanes)) {
        sql.append(" AND dl.lane IN :lanes ");
      }
    }
    sql.append(" GROUP BY o.periodDate ");
    Query query;
    if (isCounting) {
      query = entityManager.createQuery(sql.toString());
    } else {
      appendSortQuery(pageable, sql);
      query = entityManager.createQuery(sql.toString(), Date.class);
      appendPageQuery(pageable, query);
    }
    if (!CollectionUtils.isEmpty(searchRequest.getDeviceNames())) {
      query.setParameter("deviceNames", searchRequest.getDeviceNames());
    }
    if (!StringUtils.isEmpty(searchRequest.getHighway())) {
      query.setParameter("highway", searchRequest.getHighway());
    }
    if (searchRequest.getFromTimestamp() != null) {
      query.setParameter("fromTimestamp", new Timestamp(searchRequest.getFromTimestamp()));
    }
    if (searchRequest.getToTimestamp() != null) {
      query.setParameter("toTimestamp", new Timestamp(searchRequest.getToTimestamp()));
    }
    if (!CollectionUtils.isEmpty(searchRequest.getDirections())) {
      Set<String> directions = searchRequest.getDirections().stream().map(DirectionRequest::getName).collect(Collectors.toSet());
      query.setParameter("directions", directions);
      Set<String> lanes = searchRequest.getDirections().stream()
          .map(DirectionRequest::getLane).flatMap(List::stream)
          .collect(Collectors.toSet());
      if (!CollectionUtils.isEmpty(lanes)) {
        query.setParameter("lanes", lanes);
      }
    }
    return query;
  }

  @Override
  public List<MeasurementInformation> findMeasurements(List<Timestamp> timestamps, List<String> directions, List<String> lanes, List<String> devices) {
    StringBuilder sql = new StringBuilder();
    sql.append(" SELECT new rmhub.mod.measurementtraffic.model.entity.MeasurementInformation(d.originId ,d.name, dl.direction, dl.lane, o.saturation, o.speed, o.vehicles, o.periodDate) ");
    sql.append(" FROM TrafficInformation o JOIN o.device d JOIN o.directionLane dl ");
    sql.append(" WHERE o.periodDate IN :periodDates ");
    if(!CollectionUtils.isEmpty(devices)){
      sql.append(" AND d.name IN :names");
    }
    if (!CollectionUtils.isEmpty(directions)) {
      sql.append(" AND dl.direction IN :directions ");
    }
    if (!CollectionUtils.isEmpty(lanes)) {
      sql.append(" AND dl.lane IN :lanes ");
    }
    Query query = entityManager.createQuery(sql.toString(), MeasurementInformation.class);
    query.setParameter("periodDates", timestamps);
    if(!CollectionUtils.isEmpty(devices)){
      query.setParameter("names", devices);
    }
    if (!CollectionUtils.isEmpty(directions)) {
      query.setParameter("directions", directions);
    }
    if (!CollectionUtils.isEmpty(lanes)) {
      query.setParameter("lanes", lanes);
    }
    return query.getResultList();
  }

}
