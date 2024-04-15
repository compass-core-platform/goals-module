package com.service.goals.controller;

import com.service.goals.dto.DataNodeDTO;
import com.service.goals.service.DataService;
import com.service.goals.utils.Constants;
import com.service.goals.utils.Constants.Endpoints;
import com.service.goals.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(Endpoints.DATANODE_ENDPOINT)
public class DataController {
    private Logger logger = LoggerFactory.getLogger(DataController.class);
    @Autowired
    DataService dataService;

    @PostMapping(Endpoints.CREATE_NODE)
    public ResponseEntity<Response<Map<String,Object>>> createDataNode(@RequestBody DataNodeDTO dataNodeDTO) {
        try {
            logger.info("Create Node API Controller");
            Map<String,Object> result = dataService.createNode(dataNodeDTO);
            Response<Map<String,Object>> response = new Response<>(Constants.SUCCESS, null, HttpStatus.CREATED.value(), result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<Map<String,Object>> errorResponse = new Response<>(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping(Endpoints.READ_ALL)
    public ResponseEntity<Response<List<DataNodeDTO>>> getAllRecord() {
        try {
            logger.info("Read All Node API Controller");
            List<DataNodeDTO> result = dataService.readAllNodes();
            Response<List<DataNodeDTO>> response = new Response<>(Constants.SUCCESS, null, HttpStatus.OK.value(), result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<List<DataNodeDTO>> errorResponse = new Response<>(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/read/type/")
    public ResponseEntity<Response<List<DataNodeDTO>>> getAllRecord(@RequestParam String nodeType) {
        try {
            logger.info("Read NodeType API Controller");
            List<DataNodeDTO> result = dataService.readNodeType(nodeType);
            Response<List<DataNodeDTO>> response = new Response<>(Constants.SUCCESS, null, HttpStatus.OK.value(), result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<List<DataNodeDTO>> errorResponse = new Response<>(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PatchMapping(Endpoints.UPDATE_NODE)
    public ResponseEntity<Response<Boolean>> updateRecord(@RequestParam Long id,@RequestBody DataNodeDTO dataNodeDTO) {
        try {
            logger.info("Update Node API Controller");
            boolean result = dataService.updateNode(id, dataNodeDTO);
            Response<Boolean> response = new Response<>(Constants.SUCCESS, null, HttpStatus.OK.value(), result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<Boolean> errorResponse = new Response<>(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping(Endpoints.READ_BY_ID)
    public ResponseEntity<Response<DataNodeDTO>> getRecord(@RequestParam Long id) {
        try {
            logger.info("Read By ID Node API Controller");
            DataNodeDTO result = dataService.readNodeById(id);
            Response<DataNodeDTO > response = new Response<>(Constants.SUCCESS, null, HttpStatus.OK.value(), result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<DataNodeDTO> errorResponse = new Response<>(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PostMapping(Endpoints.FILTER_BY)
    public ResponseEntity<Response<List<Map<String, Object>>>> getDataNodesByFilter(@RequestBody Map<String, Object> filter) {
        try {
            logger.info("Filter Node API Controller");
            List<Map<String, Object>> result = dataService.filterSearch(filter);
            Response<List<Map<String, Object>>> response = new Response<>(Constants.SUCCESS, null, 200, result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<List<Map<String, Object>>> errorResponse = new Response<>(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping(Endpoints.DELETE)
    public ResponseEntity<Response<Boolean>> deleteRecord(@RequestParam Long id){
        try {
            logger.info("Delete Node API Controller");
            boolean result = dataService.retireNode(id);
            Response response = new Response(Constants.SUCCESS,null,200,result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response response = new Response(Constants.ERROR, e.getMessage(), 500,null);
            return ResponseEntity.ok(response);
        }
    }

}
