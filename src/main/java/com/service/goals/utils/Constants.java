package com.service.goals.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String OK = "Ok";
    public static final String ERROR = "Error";
    public static final String SUCCESS = "Success";
    public static final List<String> NODETYPE_ENUM = new ArrayList<>(Arrays.asList("objective", "keyresult", "initiative"));


    public interface Endpoints {
        public String CREATE_NODE = "/create";
        public String UPDATE_NODE = "/update/";
        public String READ_ALL = "/read/all";
        public String READ_BY_ID = "/read/";
        public String FILTER_BY = "/filter/";
        public String DELETE = "/delete/";
        public String DATANODE_ENDPOINT = "v1/node/";
    }

    public interface DataNodes {
        public String ID = "id";
        public String NAME = "name";
        public String DESCRIPTION = "description";
        public String NODE_TYPE = "node_type";
        public String CODE = "code";
        public String CREATED_ON = "created_on";
        public String CREATED_BY = "created_by";
        public String UPDATED_ON = "updated_on";
        public String UPDATED_BY = "updated_by";

    }

    public interface EntityDataNode {
        public String ID = "id";
        public String DATANODE_ID = "datanode_id";
        public String ENTITY_ID = "entity_id";
        public String NODE_TYPE = "node_type";
        public String ENTITY_TYPE = "entity_type";
        public String CREATED_ON = "created_on";
        public String CREATED_BY = "created_by";
        public String UPDATED_ON = "updated_on";
        public String UPDATED_BY = "updated_by";

    }

    public interface EDNProps {
        public String ID = "id";
        public String EDN_ID = "entity_data_node_id";
        public String PROPERTY_NAME = "property_name";
        public String PROPERTY_VALUE = "property_value";

    }

}
