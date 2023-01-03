package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.entity.Employee;
import com.itheima.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import com.itheima.common.R;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 用于员工管理的操作
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    /**
     * 员工登录功能
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //1.将页面提交过来的password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据用户提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果用户不存在则退出
        if (emp == null) {
            return R.error("登录失败");
        }

        //4.密码比对，如果不成功则退出
        if (!password.equals(emp.getPassword())) {
            return R.error("登录失败");
        }

        //5.查看账号是否已被禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        //登录成功，将员工的id存入Session中，并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工登出功能
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工功能
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {

        //设置默认密码(使用md5加密处理)
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        //获取登录时传入Session中的id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        //添加创建人信息
//        employee.setCreateUser(empId);
//        //添加修改人信息
//        employee.setUpdateUser(empId);
//        //添加创建时间
//        employee.setCreateTime(LocalDateTime.now());
//        //添加更新时间
//        employee.setUpdateTime(LocalDateTime.now());
        //将该对象的属性存入表中
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 分页查询功能，即将数据库信息显示在页面的功能
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造分页查询器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //构造条件查询器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotBlank(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 更改员工信息功能，包括启用，禁用员工功能，也包括编辑其他信息的功能
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 编辑员工功能第一步，点击编辑时让原有数据显示在页面上
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }

}
