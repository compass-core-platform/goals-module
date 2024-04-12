package com.service.goals.service.impl;

import com.service.goals.dto.DataNodeDTO;
import com.service.goals.repository.DataNodeJDBC;
import com.service.goals.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.service.goals.utils.Constants.NODETYPE_ENUM;


@Service
public class DataServiceImpl implements DataService {

    private final DataNodeJDBC dataNodeJDBC;

    @Autowired
    public DataServiceImpl(DataNodeJDBC dataNodeJDBC) {
        this.dataNodeJDBC = dataNodeJDBC;
    }


    @Override
    public Map<String, Object> createNode(DataNodeDTO dataNodeDTO) {
        try {
            if (dataNodeDTO.getCode() != null && dataNodeDTO.getName() != null && dataNodeDTO.getDescription() != null && dataNodeDTO.getCreatedOn() != null) {
                if (NODETYPE_ENUM.contains(dataNodeDTO.getNodeType())) {
                    Long id = dataNodeJDBC.insertRecord(dataNodeDTO);
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", id);
                    return result;
                }
                else {
                    throw new RuntimeException("Invalid node type : accepted values [ objective, keyresult, initiative] ");
                }
            } else {
                throw new RuntimeException("Mandatory parameter Missing");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DataNodeDTO> readAllNodes() {
        try {
            return dataNodeJDBC.getAllDetails();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DataNodeDTO> readNodeType(String type) {
        try {
            return dataNodeJDBC.getDataByType(type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataNodeDTO readNodeById(Long id) {
        try {
            return dataNodeJDBC.getDataNodeDetails(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean updateNode(Long id, DataNodeDTO dataNodeDTO) {
        try {
            return dataNodeJDBC.updateRecord(id, dataNodeDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> filterSearch(Map<String, Object> filter) {
        try {
            return dataNodeJDBC.getFilteredDetails(filter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean retireNode(Long id) {
        try {
            dataNodeJDBC.deleteDataNodeById(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
