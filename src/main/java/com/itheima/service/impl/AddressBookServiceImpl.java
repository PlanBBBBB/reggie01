package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.AddressBook;
import com.itheima.service.AddressBookService;
import com.itheima.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author 86139
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2022-11-20 17:27:06
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




