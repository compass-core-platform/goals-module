package com.service.goals.controller;

import com.service.goals.dto.EntityDataNodeDTO;
import com.service.goals.service.EntityDataService;
import com.service.goals.utils.Constants;
import com.service.goals.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/entity") 
public class EntityDataController {
    private Logger logger = LoggerFactory.getLogger(EntityDataController.class);
    @Autowired
    EntityDataService entityDataService;

    @PostMapping(Constants.Endpoints.CREATE_NODE)
    public ResponseEntity<Response<Boolean>> createEntityNode(@RequestBody EntityDataNodeDTO entityDataNodeDTO) {
        try {
            logger.info("Create Entity API Controller");
            boolean result = entityDataService.saveEntityData(entityDataNodeDTO);
            Response<Boolean> response = new Response<>(Constants.SUCCESS, null, HttpStatus.CREATED.value(), result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<Boolean> errorResponse = new Response<>(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PatchMapping(Constants.Endpoints.UPDATE_NODE)
    public ResponseEntity<Response<Boolean>> updateRecord(@RequestParam Long id,@RequestBody EntityDataNodeDTO entityDataNodeDTO) {
        try {
            logger.info("Update Entity API Controller");
            boolean result = entityDataService.updateEntityData(id, entityDataNodeDTO);
            Response<Boolean> response = new Response<>(Constants.SUCCESS, null, HttpStatus.OK.value(), result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<Boolean> errorResponse = new Response<>(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping(Constants.Endpoints.READ_ALL)
    public ResponseEntity<Response<List<EntityDataNodeDTO>>> getAllRecord() {
        try {
            logger.info("Read All Entity API Controller");
            List<EntityDataNodeDTO> result = entityDataService.getAllEntityDataNodes();
            Response<List<EntityDataNodeDTO>> response = new Response<>(Constants.SUCCESS, null, HttpStatus.OK.value(), result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<List<EntityDataNodeDTO>> errorResponse = new Response<>(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping(Constants.Endpoints.READ_BY_ID)
    public ResponseEntity<Response<EntityDataNodeDTO>> getRecord(@RequestParam Long id) {
        try {
            logger.info("Read By ID Entity API Controller");
            EntityDataNodeDTO result = entityDataService.getEntityNodesById(id);
            Response<EntityDataNodeDTO > response = new Response<>(Constants.SUCCESS, null, HttpStatus.OK.value(), result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<EntityDataNodeDTO> errorResponse = new Response<>(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PostMapping(Constants.Endpoints.FILTER_BY)
    public ResponseEntity<Response<List<EntityDataNodeDTO>>> getDataNodesByFilter(@RequestBody EntityDataNodeDTO filter) {
        try {
            logger.info("Filter Entity API Controller");
            List<EntityDataNodeDTO> result = entityDataService.filterSearch(filter);
            Response<List<EntityDataNodeDTO>> response = new Response<>(Constants.SUCCESS, null, 200, result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<List<EntityDataNodeDTO>> errorResponse = new Response<>(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping(Constants.Endpoints.DELETE)
    public ResponseEntity deleteRecord(@RequestParam Long id){
        try {
            logger.info("Delete Entity API Controller");
            boolean result = entityDataService.deleteEntityNode(id);
            Response response = new Response(Constants.SUCCESS,null,200,result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response response = new Response(Constants.ERROR, e.getMessage(), 500,null);
            return ResponseEntity.ok(response);
        }
    }
}
