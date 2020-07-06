package com.weeg.service.impl;

import com.weeg.bean.IotReptCtrlReponse;
import com.weeg.dao.IotReptCtrlReponseMapper;
import com.weeg.service.IotReptCtrlReponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IotReptCtrlReponseServiceImpl implements IotReptCtrlReponseService {

	@Autowired
    private IotReptCtrlReponseMapper IotReptCtrlReponseMapper;

	@Override
	public List<IotReptCtrlReponse> selectByClassId(String classid) {
		// TODO Auto-generated method stub
		return IotReptCtrlReponseMapper.selectByClassId(classid);
	}

	@Override
	public int insert(IotReptCtrlReponse record) {
		// TODO Auto-generated method stub
		return IotReptCtrlReponseMapper.insert(record);
	}
	
	

}