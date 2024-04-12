package com.service.goals.service.impl;


import com.service.goals.dto.EntityDataNodeDTO;
import com.service.goals.model.EntityDataNode;
import com.service.goals.repository.EntityNodeJDBC;
import com.service.goals.service.EntityDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EntityDataServiceImpl implements EntityDataService {

    private final EntityNodeJDBC entityNodeJDBC;

    @Autowired
    public EntityDataServiceImpl(EntityNodeJDBC entityNodeJDBC){
     this.entityNodeJDBC = entityNodeJDBC;
    }

    @Override
    public boolean saveEntityData(EntityDataNodeDTO entityDataNodeDTO){
        try {
            boolean result = entityNodeJDBC.insertRecord(entityDataNodeDTO);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean updateEntityData( Long id, EntityDataNodeDTO entityDataNodeDTO){
        try {
            boolean result = entityNodeJDBC.updateRecord(id, entityDataNodeDTO);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<EntityDataNode> getEntityDataNodesByAttributes(Map<String, Object> attributes) {
        try {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<EntityDataNodeDTO> getAllEntityDataNodes(){
        try {
            return entityNodeJDBC.getAllRecords();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public EntityDataNodeDTO getEntityNodesById(Long  id){
        try {
            return entityNodeJDBC.getRecordById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public List<EntityDataNodeDTO> filterSearch(EntityDataNodeDTO entityDataNodeDTO){
        try {
            return entityNodeJDBC.filterSearch(entityDataNodeDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public boolean deleteEntityNode(Long id) {
        try {
            return entityNodeJDBC.deleteEntityById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
