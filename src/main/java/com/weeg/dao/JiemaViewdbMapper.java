package com.weeg.dao;

import com.weeg.bean.JiemaViewdb;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JiemaViewdbMapper {
	JiemaViewdb selectByChildclassId(@Param(value = "childclassid") String childclassid);
}