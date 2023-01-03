package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.Employee;
import com.itheima.service.EmployeeService;
import com.itheima.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

/**
* @author 86139
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2022-11-09 17:30:29
*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{

}




