package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.AddressBookResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.AddressBook;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    R<String> setDefault(AddressBook addressBook);

   List<AddressBookResult> listAddressBook(Wrapper<AddressBook> queryWrapper);
}
