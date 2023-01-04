package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.Code;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.dto.EmployeeResult;
import com.qc.ssm.ssmstudy.reggie.dto.StoreResult;
import com.qc.ssm.ssmstudy.reggie.entity.Employee;
import com.qc.ssm.ssmstudy.reggie.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.entity.Store;
import com.qc.ssm.ssmstudy.reggie.mapper.EmployeeMapper;
import com.qc.ssm.ssmstudy.reggie.service.EmployeeService;
import com.qc.ssm.ssmstudy.reggie.service.IStringRedisService;
import com.qc.ssm.ssmstudy.reggie.utils.JWTUtil;
import com.qc.ssm.ssmstudy.reggie.utils.PWDMD5;
import com.qc.ssm.ssmstudy.reggie.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{


    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private IStringRedisService iStringRedisService;
    @Override
    public R<EmployeeResult> login(String username, String password) {
        if (username==null||username.equals("")){
            return R.error("用户名不存在");
        }
        if (password==null||password.equals("")){
            return R.error("密码不存在");
        }
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,username);
        Employee one = employeeService.getOne(queryWrapper);
        if (one==null){
            return R.error("用户名或密码错误");
        }
        String salt = one.getSalt();
        if (!PWDMD5.getMD5Encryption(password,salt).equals(one.getPassword())){//前端传入的明文密码加上后端的盐，处理后跟库中密码比对，一样登陆成功
            return R.error("用户名或密码错误");
        }

        if(one.getStatus() == 0){
            return R.error("账号已禁用!");
        }
        //jwt生成token，token里面有userid
        String token = JWTUtil.createToken(one.getId());
        iStringRedisService.setTokenWithTime(token, String.valueOf(one.getId()),3600L);
        EmployeeResult employeeResult = new EmployeeResult(String.valueOf(one.getId()),one.getUsername(),one.getName(),one.getPhone(),one.getSex(),one.getIdNumber(),one.getPermissions(),one.getStatus(),one.getStoreId(),token);

        return R.success(employeeResult);
    }

    @Override
    public R<EmployeeResult> logout(String token) {
        iStringRedisService.del(token);
        return R.successOnlyMsg("安全退出成功", Code.DEL_TOKEN);//前端在拦截器配置，只要是返回的900错误码都是删除token和本地userInfo的缓存
    }

    @Override
    public R<EmployeeResult> loginByToken(String token) {
        if (token==null){
            return R.error(Code.DEL_TOKEN,"token校验失败");
        }
        if (token.equals("")){
            return R.error(Code.DEL_TOKEN,"token校验失败");
        }
        String tokenId = iStringRedisService.getTokenId(token);
        if (tokenId==null){
            return R.error(Code.DEL_TOKEN,"token校验失败");
        }
        Long aLong = Long.valueOf(tokenId);
        Employee employee = employeeMapper.selectById(aLong);
        if (employee==null){
            return R.error(Code.DEL_TOKEN,"token校验失败");
        }
        EmployeeResult employeeResult = new EmployeeResult(String.valueOf(employee.getId()),employee.getUsername(),employee.getName(),employee.getPhone(),employee.getSex(),employee.getIdNumber(),employee.getPermissions(),employee.getStatus(),employee.getStoreId(),token);
        return R.success(employeeResult);
    }

    @Transactional
    @Override
    public R<EmployeeResult> updataForUser(Long id, String username, String name, String sex, String idNumber, String phone,String token) {
        if (idNumber.length()!=18){
            return R.error("身份证号为18位");
        }
        if (phone.length()!=11){
            return R.error("手机号为11位");
        }


        Employee employee = new Employee();
        employee.setId(id);
        employee.setUpdateUser(id);
        employee.setSex(sex);
        employee.setName(name);
        employee.setPhone(phone);
        employee.setIdNumber(idNumber);

        LambdaUpdateChainWrapper<Employee> employeeLambdaUpdateChainWrapper = employeeService.lambdaUpdate();
        boolean update = employeeLambdaUpdateChainWrapper.eq(Employee::getId, id).eq(Employee::getUsername, username).update(employee);


//        log.info(update+"updata");
        if (update){
            return R.successOnlyMsg("更新成功", Code.SUCCESS);
        }
        return R.error("更新失败");
    }

    @Override
    @Transactional
    public R<EmployeeResult> changePassword(String id, String username, String password, String newpassword, String checknewpassword, String token) {
        if (id==null){
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (username==null){
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (password==null){
            return  R.error("请输入原密码");
        }
        if (newpassword==null){
            return R.error("请输入新密码");
        }
        if (checknewpassword==null){
            return R.error("请输入确认密码");
        }
        if (token==null){
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (!newpassword.equals(checknewpassword)){
            return R.error("两次密码不一致!");
        }
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getId,Long.valueOf(id)).eq(Employee::getUsername,username);
        Employee one = employeeService.getOne(queryWrapper);
        if (one==null){
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        String salt = one.getSalt();
        if (!PWDMD5.getMD5Encryption(password,salt).equals(one.getPassword())){//前端传入的明文密码加上后端的盐，处理后跟库中密码比对，一样登陆成功
            return R.error("原密码错误");
        }

        String newSalt = PWDMD5.getSalt();
        String newMD5Password = PWDMD5.getMD5Encryption(newpassword,newSalt);
        LambdaUpdateWrapper<Employee> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Employee::getId,Long.valueOf(id)).eq(Employee::getUsername,username);
        Employee employee = new Employee();
        employee.setId(Long.valueOf(id));
        employee.setUsername(username);
        employee.setPassword(newMD5Password);
        employee.setSalt(newSalt);
        employee.setUpdateUser(Long.valueOf(id));
        //操作数据库更新密码和盐
        boolean update = employeeService.update(employee, lambdaUpdateWrapper);
        if (update){
            return R.successOnlyMsg("修改成功",Code.SUCCESS);
        }

        return R.error("修改失败");
    }

    @Override
    public R<PageData> getEmployeeList(Integer pageNum, Integer pageSize, String name) {
        if (pageNum==null){
            return R.error("传参错误");
        }
        if (pageSize==null){
            return R.error("传参错误");
        }
        Page pageInfo = new Page(pageNum,pageSize);
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //选择返回的数据
        lambdaQueryWrapper.select(Employee::getId,Employee::getName,Employee::getPhone,Employee::getSex,Employee::getUsername,Employee::getPhone,Employee::getIdNumber,Employee::getPermissions,Employee::getStatus,Employee::getStoreId);
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getCreateTime);//按照创建时间排序
        employeeService.page(pageInfo,lambdaQueryWrapper);
        PageData<EmployeeResult> pageData = new PageData<>();
        List<EmployeeResult> results = new ArrayList<>();
        for (Object employee : pageInfo.getRecords()) {
            Employee employee1 = (Employee) employee;
            EmployeeResult employeeResult = new EmployeeResult(String.valueOf(employee1.getId()),employee1.getUsername(),employee1.getName(),employee1.getPhone(),employee1.getSex(),employee1.getIdNumber(),employee1.getPermissions(),employee1.getStatus(),employee1.getStoreId(),null);
            results.add(employeeResult);
        }
        pageData.setPages(pageInfo.getPages());
        pageData.setTotal(pageInfo.getTotal());
        pageData.setCountId(pageInfo.getCountId());
        pageData.setCurrent(pageInfo.getCurrent());
        pageData.setSize(pageInfo.getSize());
        pageData.setRecords(results);
        return R.success(pageData);
    }

    @Override
    public R<EmployeeResult> updataEmployeeStatus(String userId, String caozuoId, String userStatus, String token) {
        if (!StringUtils.isNotEmpty(caozuoId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (!StringUtils.isNotEmpty(userId)){
            return R.error("系统异常,请重试!");
        }
        if (!StringUtils.isNotEmpty(userStatus)){
            return R.error("系统异常,请重试!");
        }
        if (!StringUtils.isNotEmpty(token)){
            return R.error("状态异常");
        }
        Integer status = null;
        if (userStatus.equals("1")){
            status = 0;
        }else if (userStatus.equals("0")){
            status = 1;
        }else {
            log.info(status+"");
        }
        LambdaUpdateWrapper<Employee> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Employee::getId,Long.valueOf(userId));
        Employee employee = new Employee();
        employee.setId(Long.valueOf(userId));
        employee.setStatus(status);
        employee.setUpdateUser(Long.valueOf(caozuoId));
        boolean update = employeeService.update(employee, updateWrapper);
        if (update){
            return R.success("更改成功");
        }
        return R.error("系统异常!");
    }

    @Override
    public R<EmployeeResult> deleteEmployee(String userId, String caozuoId, String token) {//删除和禁用后立即删除token
//        log.info(userId+":userid"+caozuoId+":caozuo"+token);
        if (!StringUtils.isNotEmpty(caozuoId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (!StringUtils.isNotEmpty(userId)){
            return R.error("参数异常");
        }
        boolean b = employeeService.removeById(Long.valueOf(userId));
        if (b){
            return R.success("删除成功");
        }

        return R.error("系统异常,请刷新重试!");
    }


}
