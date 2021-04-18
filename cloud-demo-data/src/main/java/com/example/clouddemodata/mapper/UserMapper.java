package com.example.clouddemodata.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clouddemodata.po.DcUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 用户mapper
 *
 * @author xub
 * @date 2019/10/10 下午8:52
 */
@Mapper
public interface UserMapper extends BaseMapper<DcUser> {

    public int insertForeach();




}