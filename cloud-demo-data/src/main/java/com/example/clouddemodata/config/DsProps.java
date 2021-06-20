package com.example.clouddemodata.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "data.sharding")
public class DsProps {

    private List<DsProp> ds;


    @Data
    public static class DsProp{

        private String url;
        private String username;
        private String password;
        private String type;
        private String dcName;
        private List<DsProp> slaveDs;



    }


}

