package com.weeg.service.impl;

import com.weeg.bean.JiemaViewdb;
import com.weeg.dao.JiemaViewdbMapper;
import com.weeg.service.JiemaViewdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JiemaViewdbServiceImpl implements JiemaViewdbService{
	@Autowired
    private JiemaViewdbMapper JiemaViewdbMapper;
	@Override
	public JiemaViewdb selectByChildclassId(String childclassid) {
		// TODO Auto-generated method stub
		return JiemaViewdbMapper.selectByChildclassId(childclassid);
	}
}