package com.example.clouddemocommon.entry.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Redis1Dmo implements Serializable {

    private Integer age;

    private String name;

   // private Long hi;
}
