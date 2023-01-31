package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.mapper.AddressBookMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.AddressBookResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.AddressBook;
import com.qc.ssm.ssmstudy.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Override
    @Transactional
    public boolean save(AddressBook entity) {
        if (!StringUtils.isNotEmpty(entity.getConsignee())){
            return false;
        }
        if (!StringUtils.isNotEmpty(entity.getSex())){
            return false;
        }
        if (!StringUtils.isNotEmpty(entity.getPhone())){
            return false;
        }
        if (!StringUtils.isNotEmpty(entity.getAbout())){
            return false;
        }
        if (!StringUtils.isNotEmpty(entity.getLabel())){
            return false;
        }
        if (!StringUtils.isNotEmpty(entity.getDetail())){
            return false;
        }
        if (ObjectUtils.isEmpty(entity.getIsDefault())){
            return false;
        }
        if (entity.getIsDefault()==null){
            return false;
        }
        if (ObjectUtils.isEmpty(entity.getUserId())){
            return false;
        }
        if (entity.getIsDefault()==1){
            LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AddressBook::getUserId,entity.getUserId());
            updateWrapper.eq(AddressBook::getIsDefault,1);
            updateWrapper.set(AddressBook::getIsDefault,0);//如果已经有默认了，先给他设置成0
            super.update(updateWrapper);
            //如果报错会自动回滚事物
        }
        return super.save(entity);


    }

    @Override
    @Transactional
    public R<String> setDefault(AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,addressBook.getUserId());
        updateWrapper.eq(AddressBook::getIsDefault,1);
        updateWrapper.set(AddressBook::getIsDefault,0);//如果已经有默认了，先给他设置成0
        super.update(updateWrapper);
        LambdaUpdateWrapper<AddressBook> updateWrapper1 = new LambdaUpdateWrapper<>();
        updateWrapper1.eq(AddressBook::getId,addressBook.getId());
        updateWrapper1.set(AddressBook::getIsDefault,1);
        super.update(updateWrapper1);
        return R.success("设置成功");
    }


    public List<AddressBookResult> listAddressBook(Wrapper<AddressBook> queryWrapper) {
        List<AddressBook> list = super.list(queryWrapper);
        List<AddressBookResult> addressBookResults = new ArrayList<>();
        for (AddressBook a:
             list) {
            AddressBookResult addressBookResult = new AddressBookResult();
            addressBookResult.setId(String.valueOf(a.getId()));
            addressBookResult.setConsignee(a.getConsignee());
            addressBookResult.setDetail(a.getDetail());
            addressBookResult.setAbout(a.getAbout());
            addressBookResult.setSex(a.getSex());
            addressBookResult.setLabel(a.getLabel());
            addressBookResult.setPhone(a.getPhone());
            addressBookResult.setCreateTime(a.getCreateTime());
            addressBookResult.setUpdateTime(a.getUpdateTime());
            addressBookResult.setCreateUser(String.valueOf(a.getCreateUser()));
            addressBookResult.setUpdateUser(String.valueOf(a.getUpdateUser()));
            addressBookResult.setUserId(String.valueOf(a.getUserId()));
            addressBookResult.setIsDefault(a.getIsDefault());
            addressBookResults.add(addressBookResult);
        }
        return addressBookResults;
    }
}
