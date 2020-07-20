package com.weeg.service.impl;

import com.weeg.bean.DevControlCmd;
import com.weeg.dao.DevControlCmdMapper;
import com.weeg.service.DevControlCmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DevControlCmdServiceImpl implements DevControlCmdService{

	@Autowired
    private DevControlCmdMapper devControlCmdMapper;
	
	@Override
	public int insert(DevControlCmd record) {
		// TODO Auto-generated method stub
		return devControlCmdMapper.insert(record);
	}

	@Override
	public List<DevControlCmd> selectBySerialandTime(String devserial,
                                                     String startTime, String endTime) {
		// TODO Auto-generated method stub
		return devControlCmdMapper.selectBySerialandTime(devserial, startTime, endTime);
	}
	
	@Override
	public int updatecmdNo(int cmdNo,String devserial,String ctrlvalue,String random) {
		// TODO Auto-generated method stub
		return devControlCmdMapper.updatecmdNo(cmdNo,devserial,ctrlvalue,random);
	}
	
	@Override
	public List<DevControlCmd> selectBySerialandcmdFlag(String devserial, String cmdFlag){
		// TODO Auto-generated method stub
		return devControlCmdMapper.selectBySerialandcmdFlag(devserial, cmdFlag);
	}

	@Override
	public int updateCmd0001(String devserial,String data) {
		return devControlCmdMapper.updateCmd0001(devserial,data);
	}

	@Override
	public DevControlCmd selectByDevserial(String devserial) {
		return devControlCmdMapper.selectByDevserial(devserial);
	}
}