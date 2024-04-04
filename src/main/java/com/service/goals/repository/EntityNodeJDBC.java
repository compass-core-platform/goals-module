package com.service.goals.repository;

import com.service.goals.dto.EntityDataNodeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EntityNodeJDBC {
    private final JdbcTemplate jdbcTemplate;
    private Logger logger = LoggerFactory.getLogger(EntityNodeJDBC.class);

    @Autowired
    public EntityNodeJDBC(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean insertRecord(EntityDataNodeDTO entityDataNodeDTO){
        try {
            String entityDataNodeQuery = "INSERT INTO entity_datanode (entity_id, entity_type, datanode_id, node_type, created_by, created_on, updated_by, updated_on) " +
                    "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP) RETURNING id";
            Long entityDataNodeId = jdbcTemplate.queryForObject(entityDataNodeQuery, Long.class,
                    entityDataNodeDTO.getEntityId(), entityDataNodeDTO.getEntityType(),
                    entityDataNodeDTO.getDataNodeId(), entityDataNodeDTO.getNodeType(),
                    entityDataNodeDTO.getCreatedBy(), entityDataNodeDTO.getUpdatedBy());
            logger.info("Create Entity "+ entityDataNodeQuery);
            logger.info("entity data node id :"+ entityDataNodeId);
            String ednPropertyQuery = "INSERT INTO edn_property (entity_data_node_id, property_name, property_value) VALUES (?, ?, ?)";
            Map<String, Object> props = entityDataNodeDTO.getProperties();
            for (Map.Entry<String, Object> entry : props.entrySet()) {
                jdbcTemplate.update(ednPropertyQuery, entityDataNodeId, entry.getKey(), entry.getValue().toString());
            }

            return true;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }


    }

    public Boolean updateRecord(Long id, EntityDataNodeDTO entityDataNodeDTO) {
        try {
            // Check if the record with the provided entityId exists
            String checkExistingQuery = "SELECT COUNT(*) FROM entity_datanode WHERE id = ?";
            int count = jdbcTemplate.queryForObject(checkExistingQuery, Integer.class, id);

            if (count == 0) {
                throw new RuntimeException("Record with id " + id + " does not exist. Update aborted.");
            }

            // Prepare the update query and parameters for entity_datanode table
            StringBuilder updateQuery = new StringBuilder("UPDATE entity_datanode SET ");
            List<Object> params = new ArrayList<>();
            if (entityDataNodeDTO.getEntityId() != null) {
                updateQuery.append("entity_id = ?, ");
                params.add(entityDataNodeDTO.getEntityId());
            }
            if (entityDataNodeDTO.getEntityType() != null) {
                updateQuery.append("entity_type = ?, ");
                params.add(entityDataNodeDTO.getEntityType());
            }
            if (entityDataNodeDTO.getDataNodeId() != null) {
                updateQuery.append("datanode_id = ?, ");
                params.add(entityDataNodeDTO.getDataNodeId());
            }
            if (entityDataNodeDTO.getNodeType() != null) {
                updateQuery.append("node_type = ?, ");
                params.add(entityDataNodeDTO.getNodeType());
            }
            updateQuery.append("updated_by = ?, updated_on = CURRENT_TIMESTAMP WHERE id = ?");
            params.add(entityDataNodeDTO.getUpdatedBy());
            params.add(id);
            jdbcTemplate.update(updateQuery.toString(), params.toArray());
            logger.info("Update entity query "+ updateQuery);
            List<Map<String,Object>> properties = List.of(entityDataNodeDTO.getProperties());
            if (properties != null) {
                // Delete existing relations for the data node
                String deleteQuery = "DELETE FROM edn_property WHERE entity_data_node_id = ?";
                jdbcTemplate.update(deleteQuery, id);
                // Insert new relations
                String insertQuery = "INSERT INTO edn_property (entity_data_node_id,property_name, property_value) VALUES (?, ?, ?)";
                Map<String, Object> props = entityDataNodeDTO.getProperties();
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
            String query = "SELECT ed.id AS entity_data_node_id, ed.entity_id, ed.entity_type, ed.datanode_id, ed.node_type, " +
                    "ed.created_by, ed.created_on, ed.updated_by, ed.updated_on, " +
                    "ep.property_name, ep.property_value " +
                    "FROM entity_datanode ed " +
                    "LEFT JOIN edn_property ep ON ed.id = ep.entity_data_node_id";

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
            Map<Long, EntityDataNodeDTO> entityDataNodeMap = new HashMap<>();

            for (Map<String, Object> row : rows) {
                Long entityDataNodeId = (Long) row.get("entity_data_node_id");
                EntityDataNodeDTO entityDataNodeDTO = entityDataNodeMap.getOrDefault(entityDataNodeId, new EntityDataNodeDTO());

                entityDataNodeDTO.setId(entityDataNodeId);
                entityDataNodeDTO.setEntityId((Long) row.get("entity_id"));
                entityDataNodeDTO.setEntityType((String) row.get("entity_type"));
                entityDataNodeDTO.setDataNodeId((Long) row.get("datanode_id"));
                entityDataNodeDTO.setNodeType((String) row.get("node_type"));
                entityDataNodeDTO.setCreatedBy((String) row.get("created_by"));
                entityDataNodeDTO.setCreatedOn(row.get("created_on").toString());
                entityDataNodeDTO.setUpdatedBy((String) row.get("updated_by"));
                entityDataNodeDTO.setUpdatedOn(row.get("updated_on").toString());

                String propertyName = (String) row.get("property_name");
                if (propertyName != null) {
                    Map<String, Object> properties = entityDataNodeDTO.getProperties();
                    if (properties == null) {
                        properties = new HashMap<>();
                        entityDataNodeDTO.setProperties(properties);
                    }
                    properties.put(propertyName, row.get("property_value"));
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
            String query = "SELECT ed.id AS entity_data_node_id, ed.entity_id, ed.entity_type, ed.datanode_id, ed.node_type, " +
                    "ed.created_by, ed.created_on, ed.updated_by, ed.updated_on, " +
                    "ep.property_name, ep.property_value " +
                    "FROM entity_datanode ed " +
                    "LEFT JOIN edn_property ep ON ed.id = ep.entity_data_node_id " +
                    "WHERE ed.id = ?";

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, id);
            Map<Long, EntityDataNodeDTO> entityDataNodeMap = new HashMap<>();
            logger.info("Get entity by id : "+ rows);
            for (Map<String, Object> row : rows) {
                Long entityDataNodeId = (Long) row.get("entity_data_node_id");
                EntityDataNodeDTO entityDataNodeDTO = entityDataNodeMap.getOrDefault(entityDataNodeId, new EntityDataNodeDTO());

                entityDataNodeDTO.setId(entityDataNodeId);
                entityDataNodeDTO.setEntityId((Long) row.get("entity_id"));
                entityDataNodeDTO.setEntityType((String) row.get("entity_type"));
                entityDataNodeDTO.setDataNodeId((Long) row.get("datanode_id"));
                entityDataNodeDTO.setNodeType((String) row.get("node_type"));
                entityDataNodeDTO.setCreatedBy((String) row.get("created_by"));
                entityDataNodeDTO.setCreatedOn(row.get("created_on").toString());
                entityDataNodeDTO.setUpdatedBy((String) row.get("updated_by"));
                entityDataNodeDTO.setUpdatedOn(row.get("updated_on").toString());

                String propertyName = (String) row.get("property_name");
                if (propertyName != null) {
                    Map<String, Object> properties = entityDataNodeDTO.getProperties();
                    if (properties == null) {
                        properties = new HashMap<>();
                        entityDataNodeDTO.setProperties(properties);
                    }
                    properties.put(propertyName, row.get("property_value"));
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
            StringBuilder queryBuilder = new StringBuilder("SELECT ed.id AS entity_data_node_id, ed.entity_id, ed.entity_type, ed.datanode_id, ed.node_type, " +
                    "ed.created_by, ed.created_on, ed.updated_by, ed.updated_on, " +
                    "ep.property_name, ep.property_value " +
                    "FROM entity_datanode ed " +
                    "LEFT JOIN edn_property ep ON ed.id = ep.entity_data_node_id");

            // Build WHERE clause based on filter DTO
            List<Object> queryParams = new ArrayList<>();
            boolean hasWhere = false;

            // Check each field in the filter DTO and add conditions to the WHERE clause accordingly
            if (filters.getId() != null) {
                queryBuilder.append(" WHERE ");
                queryBuilder.append("ed.id = ?");
                queryParams.add(filters.getId());
                hasWhere = true;
            }
            if (filters.getEntityId() != null) {
                queryBuilder.append(hasWhere ? " AND " : " WHERE ");
                queryBuilder.append("ed.entity_id = ?");
                queryParams.add(filters.getEntityId());
                hasWhere = true;
            }
            if (filters.getEntityType() != null) {
                queryBuilder.append(hasWhere ? " AND " : " WHERE ");
                queryBuilder.append("ed.entity_type = ?");
                queryParams.add(filters.getEntityType());
                hasWhere = true;
            }
            if (filters.getNodeType() != null) {
                queryBuilder.append(hasWhere ? " AND " : " WHERE ");
                queryBuilder.append("ed.node_type = ?");
                queryParams.add(filters.getEntityType());
            }
            String query = queryBuilder.toString();
            logger.info("Filter search query"+ query);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, queryParams.toArray());
            List<EntityDataNodeDTO> entityDataNodeDTOs = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                EntityDataNodeDTO entityDataNodeDTO = mapRowToEntityDataNodeDTO(row);
                entityDataNodeDTOs.add(entityDataNodeDTO);
            }
            return entityDataNodeDTOs;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private EntityDataNodeDTO mapRowToEntityDataNodeDTO(Map<String, Object> row) {
        EntityDataNodeDTO entityDataNodeDTO = new EntityDataNodeDTO();

        // Set DTO attributes
        entityDataNodeDTO.setId((Long) row.get("entity_data_node_id"));
        entityDataNodeDTO.setEntityId((Long) row.get("entity_id"));
        entityDataNodeDTO.setEntityType((String) row.get("entity_type"));
        entityDataNodeDTO.setDataNodeId((Long) row.get("datanode_id"));
        entityDataNodeDTO.setNodeType((String) row.get("node_type"));
        entityDataNodeDTO.setCreatedBy((String) row.get("created_by"));
        entityDataNodeDTO.setCreatedOn((String) row.get("created_on"));
        entityDataNodeDTO.setUpdatedBy((String) row.get("updated_by"));
        entityDataNodeDTO.setUpdatedOn((String) row.get("updated_on"));
        logger.info("map Rwo to Entity DTO : "+ entityDataNodeDTO);
        // Create or retrieve properties map
        Map<String, Object> properties = entityDataNodeDTO.getProperties();
        if (properties == null) {
            properties = new HashMap<>();
            entityDataNodeDTO.setProperties(properties);
        }

        // Set properties from row
        String propertyName = (String) row.get("property_name");
        if (propertyName != null) {
            Object propertyValue = row.get("property_value");
            properties.put(propertyName, propertyValue);
        }

        return entityDataNodeDTO;
    }



}
