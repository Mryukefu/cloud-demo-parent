package com.example.clouddemoquarz.dao;


import com.example.clouddemoquarz.entity.JobAndTrigger;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JobAndTriggerMapper {

	public List<JobAndTrigger> getJobAndTriggerDetails();
}
