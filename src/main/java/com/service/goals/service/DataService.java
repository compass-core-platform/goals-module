package com.service.goals.service;

import com.service.goals.dto.DataNodeDTO;
import com.service.goals.model.DataNode;

import java.util.List;
import java.util.Map;

public interface DataService {
    boolean retireNode(Long id);

    Map<String,Object> createNode(DataNodeDTO dataNodeDTO);

    List<Map<String, Object>> readAllNodes();

    Map<String,Object> readNodeById(Long id);

    Boolean updateNode(Long id, DataNodeDTO dataNodeDTO);

    List<Map<String,Object>> filterSearch(Map<String, Object> filter);
}
