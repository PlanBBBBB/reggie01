package com.itheima.mapper;

import com.itheima.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86139
* @description 针对表【employee(员工信息)】的数据库操作Mapper
* @createDate 2022-11-09 17:30:29
* @Entity com.itheima.entity.Employee
*/
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}




