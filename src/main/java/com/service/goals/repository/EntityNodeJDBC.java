package com.service.goals.repository;

import com.service.goals.dto.EntityDataNodeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EntityNodeJDBC {
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(EntityNodeJDBC.class);

    @Autowired
    public EntityNodeJDBC(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean insertRecord(EntityDataNodeDTO entityDataNodeDTO) {
        try {
            String entityDataNodeQuery = "INSERT INTO entitynode (entityId, entityType, dataNodeId, nodeType, createdBy, createdOn, updatedBy, updatedOn) " +
                    "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP) RETURNING id";
            Long entityDataNodeId = jdbcTemplate.queryForObject(entityDataNodeQuery, Long.class,
                    entityDataNodeDTO.getEntityId(), entityDataNodeDTO.getEntityType(),
                    entityDataNodeDTO.getDataNodeId(), entityDataNodeDTO.getNodeType(),
                    entityDataNodeDTO.getCreatedBy(), entityDataNodeDTO.getUpdatedBy());

            if (entityDataNodeDTO.getProperties() != null && !entityDataNodeDTO.getProperties().isEmpty()) {
                String ednPropertyQuery = "INSERT INTO property (entityNodeId, propertyName, propertyValue) VALUES (?, ?, ?)";
                Map<String, Object> props = entityDataNodeDTO.getProperties();
                for (Map.Entry<String, Object> entry : props.entrySet()) {
                    jdbcTemplate.update(ednPropertyQuery, entityDataNodeId, entry.getKey(), entry.getValue().toString());
                }
            }
            return true;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean updateRecord(Long id, EntityDataNodeDTO entityDataNodeDTO) {
        try {
            String checkExistingQuery = "SELECT COUNT(*) FROM entitynode WHERE id = ?";
            Integer count = jdbcTemplate.queryForObject(checkExistingQuery, Integer.class, id);
            if (count == null) {
                throw new RuntimeException("No record found for id " + id + ". Update aborted.");
            }
            int countValue = count;
            if (countValue == 0) {
                throw new RuntimeException("Record with id " + id + " does not exist. Update aborted.");
            }
            StringBuilder updateQuery = new StringBuilder("UPDATE entitynode SET ");
            List<Object> params = new ArrayList<>();
            if (entityDataNodeDTO.getEntityId() != null) {
                updateQuery.append("entityId = ?, ");
                params.add(entityDataNodeDTO.getEntityId());
            }
            if (entityDataNodeDTO.getEntityType() != null) {
                updateQuery.append("entityType = ?, ");
                params.add(entityDataNodeDTO.getEntityType());
            }
            if (entityDataNodeDTO.getDataNodeId() != null) {
                updateQuery.append("dataNodeId = ?, ");
                params.add(entityDataNodeDTO.getDataNodeId());
            }
            if (entityDataNodeDTO.getNodeType() != null) {
                updateQuery.append("nodeType = ?, ");
                params.add(entityDataNodeDTO.getNodeType());
            }
            updateQuery.append("updatedBy = ?, updatedOn = CURRENT_TIMESTAMP WHERE id = ?");
            params.add(entityDataNodeDTO.getUpdatedBy());
            params.add(id);
            jdbcTemplate.update(updateQuery.toString(), params.toArray());
            Map<String, Object> props = entityDataNodeDTO.getProperties();
            if (props != null && !props.isEmpty()) {
                String deleteQuery = "DELETE FROM property WHERE entityNodeId = ?";
                jdbcTemplate.update(deleteQuery, id);
                String insertQuery = "INSERT INTO property (entityNodeId, propertyName, propertyValue) VALUES (?, ?, ?)";
                for (Map.Entry<String, Object> entry : props.entrySet()) {
                    jdbcTemplate.update(insertQuery, id, entry.getKey(), entry.getValue().toString());
                }
            }
            return true;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public List<EntityDataNodeDTO> getAllRecords() {
        try {
            String query = "SELECT en.id AS enId, en.entityId, en.entityType, en.dataNodeId, en.nodeType, " +
                    "en.createdBy, en.createdOn, en.updatedBy, en.updatedOn, " +
                    "p.propertyName, p.propertyValue " +
                    "FROM entitynode en " +
                    "LEFT JOIN property p ON en.id = p.entityNodeId";

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
            Map<Long, EntityDataNodeDTO> entityDataNodeMap = new HashMap<>();

            for (Map<String, Object> row : rows) {
                Long entityDataNodeId = (Long) row.get("enId");
                EntityDataNodeDTO entityDataNodeDTO = entityDataNodeMap.getOrDefault(entityDataNodeId, new EntityDataNodeDTO());

                entityDataNodeDTO.setId(entityDataNodeId);
                entityDataNodeDTO.setEntityId((Long) row.get("entityId"));
                entityDataNodeDTO.setEntityType((String) row.get("entityType"));
                entityDataNodeDTO.setDataNodeId((Long) row.get("dataNodeId"));
                entityDataNodeDTO.setNodeType((String) row.get("nodeType"));
                entityDataNodeDTO.setCreatedBy((String) row.get("createdBy"));
                entityDataNodeDTO.setCreatedOn(row.get("createdOn").toString());
                entityDataNodeDTO.setUpdatedBy((String) row.get("updatedBy"));
                entityDataNodeDTO.setUpdatedOn(row.get("updatedOn").toString());

                String propertyName = (String) row.get("propertyName");
                if (propertyName != null) {
                    Map<String, Object> properties = entityDataNodeDTO.getProperties();
                    if (properties == null) {
                        properties = new HashMap<>();
                        entityDataNodeDTO.setProperties(properties);
                    }
                    properties.put(propertyName, row.get("propertyValue"));
                }

                entityDataNodeMap.put(entityDataNodeId, entityDataNodeDTO);
            }

            return new ArrayList<>(entityDataNodeMap.values());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public EntityDataNodeDTO getRecordById(Long id) {
        try {
            String query = "SELECT en.id AS enId, en.entityId, en.entityType, en.dataNodeId, en.nodeType, " +
                    "en.createdBy, en.createdOn, en.updatedBy, en.updatedOn, " +
                    "p.propertyName, p.propertyValue " +
                    "FROM entitynode en " +
                    "LEFT JOIN property p ON en.id = p.entityNodeId " +
                    "WHERE en.id = ?";

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, id);
            Map<Long, EntityDataNodeDTO> entityDataNodeMap = new HashMap<>();
            for (Map<String, Object> row : rows) {
                Long entityDataNodeId = (Long) row.get("enId");
                EntityDataNodeDTO entityDataNodeDTO = entityDataNodeMap.getOrDefault(entityDataNodeId, new EntityDataNodeDTO());

                entityDataNodeDTO.setId(entityDataNodeId);
                entityDataNodeDTO.setEntityId((Long) row.get("entityId"));
                entityDataNodeDTO.setEntityType((String) row.get("entityType"));
                entityDataNodeDTO.setDataNodeId((Long) row.get("dataNodeId"));
                entityDataNodeDTO.setNodeType((String) row.get("nodeType"));
                entityDataNodeDTO.setCreatedBy((String) row.get("createdBy"));
                entityDataNodeDTO.setCreatedOn(row.get("createdOn").toString());
                entityDataNodeDTO.setUpdatedBy((String) row.get("updatedBy"));
                entityDataNodeDTO.setUpdatedOn(row.get("updatedOn").toString());

                String propertyName = (String) row.get("propertyName");
                if (propertyName != null) {
                    Map<String, Object> properties = entityDataNodeDTO.getProperties();
                    if (properties == null) {
                        properties = new HashMap<>();
                        entityDataNodeDTO.setProperties(properties);
                    }
                    properties.put(propertyName, row.get("propertyValue"));
                }

                entityDataNodeMap.put(entityDataNodeId, entityDataNodeDTO);
            }

            return entityDataNodeMap.isEmpty() ? null : entityDataNodeMap.values().iterator().next();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public List<EntityDataNodeDTO> filterSearch(EntityDataNodeDTO filters) {
        try {
            // Base query
            StringBuilder queryBuilder = new StringBuilder("SELECT en.id AS entity_data_node_id, en.entityId, en.entityType, en.dataNodeId, en.nodeType, " +
                    "en.createdBy, en.createdOn, en.updatedBy, en.updatedOn, " +
                    "p.propertyName, p.propertyValue " +
                    "FROM entitynode en " +
                    "LEFT JOIN property p ON en.id = p.entityNodeId");

            // Build WHERE clause based on non-null fields of the filter DTO
            List<Object> queryParams = new ArrayList<>();
            boolean hasWhere = false;
            try {
                for (Field field : filters.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(filters);
                    if (value != null) {
                        queryBuilder.append(hasWhere ? " AND " : " WHERE ");
                        queryBuilder.append("en.").append(field.getName()).append(" = ?");
                        queryParams.add(value);
                        hasWhere = true;
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            String query = queryBuilder.toString();
            logger.info("Filter search query{}", query);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, queryParams.toArray());
            Map<Long, EntityDataNodeDTO> entityDataNodeMap = new HashMap<>();

            for (Map<String, Object> row : rows) {
                Long entityDataNodeId = (Long) row.get("entity_data_node_id");
                EntityDataNodeDTO entityDataNodeDTO = entityDataNodeMap.computeIfAbsent(entityDataNodeId, k -> new EntityDataNodeDTO());

                // Set DTO attributes
                entityDataNodeDTO.setId(entityDataNodeId);
                entityDataNodeDTO.setEntityId((Long) row.get("entityId"));
                entityDataNodeDTO.setEntityType((String) row.get("entityType"));
                entityDataNodeDTO.setDataNodeId((Long) row.get("dataNodeId"));
                entityDataNodeDTO.setNodeType((String) row.get("nodeType"));
                entityDataNodeDTO.setCreatedBy((String) row.get("createdBy"));
                entityDataNodeDTO.setCreatedOn((String) row.get("createdOn"));
                entityDataNodeDTO.setUpdatedBy((String) row.get("updatedBy"));
                entityDataNodeDTO.setUpdatedOn((String) row.get("updatedOn"));
                logger.info("map Row to Entity DTO : {}", entityDataNodeDTO);
                Map<String, Object> properties = entityDataNodeDTO.getProperties();
                if (properties == null) {
                    properties = new HashMap<>();
                    entityDataNodeDTO.setProperties(properties);
                }
                String propertyName = (String) row.get("propertyName");
                if (propertyName != null) {
                    Object propertyValue = row.get("propertyValue");
                    properties.put(propertyName, propertyValue);
                }
            }

            return new ArrayList<>(entityDataNodeMap.values());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean deleteEntityById(Long id) {
        try {
            String deleteQuery = "DELETE FROM entitynode WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(deleteQuery, id);
            if(rowsAffected > 0){
                deletePropertyByEntityId(id);
            }
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error occurred while deleting entity with id " + id, e);
        }
    }
    public void deletePropertyByEntityId(Long entityId) {
        try {
            String deleteQuery = "DELETE FROM property WHERE entityNodeId = ?";
            jdbcTemplate.update(deleteQuery, entityId);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error occurred while deleting properties for entity with id " + entityId, e);
        }
    }
}
