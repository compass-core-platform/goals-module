package com.service.goals.repository;

import com.service.goals.dto.DataNodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DataNodeJDBC {
    private final JdbcTemplate jdbcTemplate;
    private Logger logger = LoggerFactory.getLogger(DataNodeJDBC.class);

    @Autowired
    public DataNodeJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteRecord(Long id) {
        String sql = "DELETE FROM data_node WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Long insertRecord(DataNodeDTO dataNodeDTO) {
        try {
            String dataNodeQuery = "INSERT INTO data_node (name, description, code, node_type, created_on, created_by, updated_on, updated_by) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?) RETURNING id";
            Long dataNodeId = jdbcTemplate.queryForObject(dataNodeQuery, Long.class, dataNodeDTO.getName(), dataNodeDTO.getDescription(), dataNodeDTO.getCode(), dataNodeDTO.getNodeType(), "admin", "admin");
            logger.info("Create Datanode Query : "+ dataNodeQuery);
            logger.info("data node ID : " + dataNodeId);
            if(dataNodeDTO.getChildren() != null){
                String dataNodeRelationsQuery = "INSERT INTO dn_relations (parentid, childid) VALUES (?, ?)";
                List<Long> children = dataNodeDTO.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    jdbcTemplate.update(dataNodeRelationsQuery, dataNodeId, children.get(i));
                }
            }
            return dataNodeId;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> gelAllDetails() {
        try {
            String query = "SELECT * FROM data_node";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
            for (Map<String, Object> result : results) {
                Long id = (Long) result.get("id");
                result.put("children", getChildDetails(id));
            }
            logger.info("List All Datanode : "+ results);
            return results;
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> getDataNodeDetails(Long id) {
        try {
            String query = "SELECT * FROM data_node WHERE id = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(query, id);
            result.put("children", getChildDetails((Long) result.get("id")));
            logger.info("List Datanode ID id : "+ id);
            logger.info("List Datanode By Id : "+ result);
            return result;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public Boolean updateRecord(Long id, DataNodeDTO dataNodeDTO) {
        try {

            // Check if the record with the provided id exists
            String checkExistingQuery = "SELECT COUNT(*) FROM data_node WHERE id = ?";
            int count = jdbcTemplate.queryForObject(checkExistingQuery, Integer.class, id);

            if (count == 0) {
                throw new RuntimeException("Record with id " + id + " does not exist. Update aborted.");
            }
            StringBuilder updateQuery = new StringBuilder("UPDATE data_node SET ");
            List<Object> params = new ArrayList<>();

            if (dataNodeDTO.getName() != null) {
                updateQuery.append("name = ?, ");
                params.add(dataNodeDTO.getName());
            }
            if (dataNodeDTO.getDescription() != null) {
                updateQuery.append("description = ?, ");
                params.add(dataNodeDTO.getDescription());
            }
            if (dataNodeDTO.getCode() != null) {
                updateQuery.append("code = ?, ");
                params.add(dataNodeDTO.getCode());
            }

            if (dataNodeDTO.getNodeType() != null) {
                updateQuery.append("node_type = ?, ");
                params.add(dataNodeDTO.getNodeType());
            }
            if (updateQuery.charAt(updateQuery.length() - 2) == ',') {
                updateQuery.setLength(updateQuery.length() - 2);
            }
            updateQuery.append(" updated_on = CURRENT_TIMESTAMP, updated_by = ?");
            params.add("admin");

            updateQuery.append(" WHERE id = ?");
            params.add(id);
            jdbcTemplate.update(updateQuery.toString(), params.toArray());
            List<Long> children = dataNodeDTO.getChildren();
            if (children != null) {
                String deleteQuery = "DELETE FROM dn_relations WHERE parentID = ?";
                jdbcTemplate.update(deleteQuery, id);
                String insertQuery = "INSERT INTO dn_relations (parentID, childID) VALUES (?, ?)";
                for (int i = 0; i < children.size(); i++) {
                    jdbcTemplate.update(insertQuery, id, children.get(i));
                }
            }
            return true;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> getFilteredDetails(Map<String, Object> filters) {
        try {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM data_node");
            List<Object> queryParams = new ArrayList<>();
            if (!filters.isEmpty()) {
                queryBuilder.append(" WHERE ");
                int index = 0;
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    String column = entry.getKey();
                    Object value = entry.getValue();
                    if (value != null) {
                        if (index > 0) {
                            queryBuilder.append(" AND ");
                        }
                        queryBuilder.append(column).append(" = ?");
                        queryParams.add(value);
                        index++;
                    }
                }
            }

            String query = queryBuilder.toString();
            logger.info("Filter query : "+ query);
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, queryParams.toArray());
            for (Map<String, Object> node : result) {
                node.put("children", getChildDetails((Long) node.get("id")));
            }
            logger.info("Filter Result"+ result);
            return result;
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<Map<String,Object>> getChildDetails(Long id){
        String dataNodeRelationsQuery = "SELECT childid FROM dn_relations WHERE parentid = ?";
        List<Map<String, Object>> children = jdbcTemplate.queryForList(dataNodeRelationsQuery, id);
        for (Map<String, Object> child : children) {
            child.put("data", getDataNodeDetails((Long) child.get("childid")));
        }
        return children;
    }
}
