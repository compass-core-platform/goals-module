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

    public Long insertRecord(DataNodeDTO dataNodeDTO) {
        try {
            String dataNodeQuery = "INSERT INTO datanode (name, description, code, nodeType, createdOn, createdBy, updatedOn, updatedBy) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?) RETURNING id";
            Long dataNodeId = jdbcTemplate.queryForObject(dataNodeQuery, Long.class, dataNodeDTO.getName(), dataNodeDTO.getDescription(), dataNodeDTO.getCode(), dataNodeDTO.getNodeType(), dataNodeDTO.getCreatedBy(), dataNodeDTO.getUpdatedBy());
            if (dataNodeDTO.getChildren() != null && !dataNodeDTO.getChildren().isEmpty()) {
                String dataNodeRelationsQuery = "INSERT INTO relations (parentId, childId) VALUES (?, ?)";
                List<Long> children = dataNodeDTO.getChildren();
                for (Long childId : children) {
                    jdbcTemplate.update(dataNodeRelationsQuery, dataNodeId, childId);
                }
            }
            return dataNodeId;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DataNodeDTO> getAllDetails() {
        try {
            String query = "SELECT * FROM datanode";
            List<DataNodeDTO> results = jdbcTemplate.query(query, (rs, rowNum) -> {
                DataNodeDTO dataNodeDTO = new DataNodeDTO();
                dataNodeDTO.setId(rs.getLong("id"));
                dataNodeDTO.setName(rs.getString("name"));
                dataNodeDTO.setDescription(rs.getString("description"));
                dataNodeDTO.setCode(rs.getString("code"));
                dataNodeDTO.setNodeType(rs.getString("nodeType"));
                dataNodeDTO.setCreatedBy(rs.getString("createdBy"));
                dataNodeDTO.setCreatedOn(rs.getString("createdOn"));
                dataNodeDTO.setUpdatedBy(rs.getString("updatedBy"));
                dataNodeDTO.setUpdatedOn(rs.getString("updatedOn"));
                dataNodeDTO.setChildren(getChildDetails(rs.getLong("id")));
                return dataNodeDTO;
            });
            return results;
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public List<DataNodeDTO> getDataByType(String type) {
        try {
            String query = "SELECT * FROM datanode WHERE nodeType = ?";
            List<DataNodeDTO> results = jdbcTemplate.query(query, new Object[]{type}, (rs, rowNum) -> {
                DataNodeDTO dataNodeDTO = new DataNodeDTO();
                dataNodeDTO.setId(rs.getLong("id"));
                dataNodeDTO.setName(rs.getString("name"));
                dataNodeDTO.setDescription(rs.getString("description"));
                dataNodeDTO.setCode(rs.getString("code"));
                dataNodeDTO.setNodeType(rs.getString("nodeType"));
                dataNodeDTO.setCreatedBy(rs.getString("createdBy"));
                dataNodeDTO.setCreatedOn(rs.getString("createdOn"));
                dataNodeDTO.setUpdatedBy(rs.getString("updatedBy"));
                dataNodeDTO.setUpdatedOn(rs.getString("updatedOn"));
                dataNodeDTO.setChildren(getChildDetails(rs.getLong("id")));
                return dataNodeDTO;
            });
            return results;
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public DataNodeDTO getDataNodeDetails(Long id) {
        try {
            String query = "SELECT * FROM datanode WHERE id = ?";
            DataNodeDTO dataNodeDTO = jdbcTemplate.queryForObject(query, new Object[]{id}, (rs, rowNum) -> {
                DataNodeDTO dto = new DataNodeDTO();
                dto.setId(rs.getLong("id"));
                dto.setName(rs.getString("name"));
                dto.setDescription(rs.getString("description"));
                dto.setCode(rs.getString("code"));
                dto.setNodeType(rs.getString("nodeType"));
                dto.setCreatedBy(rs.getString("createdBy"));
                dto.setCreatedOn(rs.getString("createdOn"));
                dto.setUpdatedBy(rs.getString("updatedBy"));
                dto.setUpdatedOn(rs.getString("updatedOn"));
                dto.setChildren(getChildDetails(id));
                return dto;
            });
            return dataNodeDTO;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public Boolean updateRecord(Long id, DataNodeDTO dataNodeDTO) {
        try {

            // Check if the record with the provided id exists
            String checkExistingQuery = "SELECT COUNT(*) FROM datanode WHERE id = ?";
            int count = jdbcTemplate.queryForObject(checkExistingQuery, Integer.class, id);

            if (count == 0) {
                throw new RuntimeException("Record with id " + id + " does not exist. Update aborted.");
            }
            StringBuilder updateQuery = new StringBuilder("UPDATE datanode SET ");
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
                updateQuery.append("nodeType = ?, ");
                params.add(dataNodeDTO.getNodeType());
            }
            if (updateQuery.charAt(updateQuery.length() - 2) == ',') {
                updateQuery.setLength(updateQuery.length() - 2);
            }
            updateQuery.append(" updatedOn = CURRENT_TIMESTAMP, updatedBy = ?");
            params.add(dataNodeDTO.getUpdatedBy());

            updateQuery.append(" WHERE id = ?");
            params.add(id);
            jdbcTemplate.update(updateQuery.toString(), params.toArray());
            List<Long> children = dataNodeDTO.getChildren();
            if (children != null) {
                String deleteQuery = "DELETE FROM relations WHERE parentId = ?";
                jdbcTemplate.update(deleteQuery, id);
                String insertQuery = "INSERT INTO relations (parentId, childId) VALUES (?, ?)";
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
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM datanode");
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
            logger.info("Filter query : {}", query);
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, queryParams.toArray());
            for (Map<String, Object> node : result) {
                node.put("children", getChildDetails((Long) node.get("id")));
            }
            logger.info("Filter Result{}", result);
            return result;
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<DataNodeDTO> getChildDetails(Long id) {
        String dataNodeRelationsQuery = "SELECT childId FROM relations WHERE parentId = ?";
        List<DataNodeDTO> children = jdbcTemplate.query(dataNodeRelationsQuery, new Object[]{id}, (rs, rowNum) -> {
            Long childId = rs.getLong("childId");
            return getDataNodeDetails(childId);
        });
        return children;
    }
    public boolean deleteDataNodeById(Long dataNodeId) {
        try {
            boolean isUsed = isDataNodeUsed(dataNodeId);

            if (isUsed) {
                throw new RuntimeException("Data node with id " + dataNodeId + " is used in entity node and cannot be deleted.");
            }

            boolean dataNodeDeleted = deleteDataNode(dataNodeId);
            boolean relationsDeleted = deleteRelationsByParentId(dataNodeId);

            return dataNodeDeleted && relationsDeleted;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error occurred while deleting data node with id " + dataNodeId, e);
        }
    }

    private boolean isDataNodeUsed(Long dataNodeId) {
        try {
            String query = "SELECT COUNT(*) FROM entitynode WHERE dataNodeId = ?";
            int count = jdbcTemplate.queryForObject(query, Integer.class, dataNodeId);

            return count > 0;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error occurred while checking if data node is used", e);
        }
    }


    private boolean deleteDataNode(Long dataNodeId) {
        try {
            String deleteQuery = "DELETE FROM datanode WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(deleteQuery, dataNodeId);

            return rowsAffected > 0;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error occurred while deleting data node with id " + dataNodeId, e);
        }
    }

    private boolean deleteRelationsByParentId(Long parentId) {
        try {
            String deleteQuery = "DELETE FROM relations WHERE parentId = ?";
            int rowsAffected = jdbcTemplate.update(deleteQuery, parentId);

            return rowsAffected > 0;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error occurred while deleting relations with parent id " + parentId, e);
        }
    }



}
