package com.weeg.dao;

import com.weeg.bean.IotReptCtrlReponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IotReptCtrlReponseMapper {

	int insert(IotReptCtrlReponse record);

	List<IotReptCtrlReponse> selectByClassId(@Param(value = "classid") String classid);

}