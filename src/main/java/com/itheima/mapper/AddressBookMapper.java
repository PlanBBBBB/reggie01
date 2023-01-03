package com.itheima.mapper;

import com.itheima.entity.AddressBook;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86139
* @description 针对表【address_book(地址管理)】的数据库操作Mapper
* @createDate 2022-11-20 17:27:06
* @Entity com.itheima.entity.AddressBook
*/
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}




