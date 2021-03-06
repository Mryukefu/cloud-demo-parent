package com.example.clouddemodata.mapper;


import com.example.clouddemodata.entry.po.DcUser;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;
import tk.mybatis.mapper.common.sqlserver.InsertSelectiveMapper;

import java.util.List;

/**
 * @Description: 用户mapper
 *
 * @author xub
 * @date 2019/10/10 下午8:52
 */
public interface UserMapper extends InsertUseGeneratedKeysMapper<DcUser>, BaseMapper<DcUser> {

    public int insertForeach(@Param("vos") List<DcUser> list);


    public DcUser getList(@Param("userIds") List<Integer> userIds);


    Integer myInsert(DcUser dcUser);
}