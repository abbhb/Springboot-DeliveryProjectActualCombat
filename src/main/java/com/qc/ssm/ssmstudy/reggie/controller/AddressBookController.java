package com.qc.ssm.ssmstudy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qc.ssm.ssmstudy.reggie.common.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.AddressBookResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.AddressBook;
import com.qc.ssm.ssmstudy.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    private final AddressBookService addressBookService;

    @Autowired
    public AddressBookController(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }


    /**
     * 新增
     */
    @PostMapping
    @NeedToken
    public R<String> save(@RequestHeader(value="userid", defaultValue = "")String userid,@RequestBody AddressBook addressBook) {
//        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{},userid:{}", addressBook,userid);
        if (!StringUtils.isNotEmpty(userid)){
            return R.error("userid为空");
        }
        addressBook.setUserId(Long.valueOf(userid));
        boolean save = addressBookService.save(addressBook);
        if (!save){
            return R.error("保存失败");
        }
        return R.success("保存成功");
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<String> setDefault(@RequestHeader(value="userid", defaultValue = "")String userid,@RequestBody AddressBook addressBook) {
        log.info("addressBook:{},userid:{}", addressBook,userid);
        if (!StringUtils.isNotEmpty(userid)){
            return R.error("userid为空");
        }
        addressBook.setUserId(Long.valueOf(userid));
        return addressBookService.setDefault(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R<AddressBookResult> get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null) {
            return R.error("没有找到该对象");
        }
        AddressBookResult addressBookResult = new AddressBookResult();
        BeanUtils.copyProperties(addressBook,addressBookResult);
        return R.success(addressBookResult);
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBookResult> getDefault(@RequestHeader(value="userid", defaultValue = "")String userid) {
        if(!StringUtils.isNotEmpty(userid)){
            return R.error("userid");
        }
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,Long.valueOf(userid));
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (null == addressBook) {
            return R.error("没有找到该对象");
        }
        AddressBookResult addressBookResult = new AddressBookResult();
        BeanUtils.copyProperties(addressBook,addressBookResult);//深拷贝，可能会出现错误
        return R.success(addressBookResult);
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBookResult>> list(@RequestHeader(value="userid", defaultValue = "")String userid) {
        log.info("userid:{}",userid);
        if (!StringUtils.isNotEmpty(userid)){
            return R.error("请先登录!");
        }
        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,Long.valueOf(userid));
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBookResult> addressBookResults = addressBookService.listAddressBook(queryWrapper);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookResults);
    }
}
