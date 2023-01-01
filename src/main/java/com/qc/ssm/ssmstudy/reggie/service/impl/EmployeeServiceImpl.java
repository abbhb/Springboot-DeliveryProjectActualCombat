package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.Code;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.dto.EmployeeResult;
import com.qc.ssm.ssmstudy.reggie.entity.Employee;
import com.qc.ssm.ssmstudy.reggie.mapper.EmployeeMapper;
import com.qc.ssm.ssmstudy.reggie.service.EmployeeService;
import com.qc.ssm.ssmstudy.reggie.service.IStringRedisService;
import com.qc.ssm.ssmstudy.reggie.utils.JWTUtil;
import com.qc.ssm.ssmstudy.reggie.utils.PWDMD5;
import com.qc.ssm.ssmstudy.reggie.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

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
        EmployeeResult employeeResult = new EmployeeResult(String.valueOf(one.getId()),one.getUsername(),one.getName(),one.getPhone(),one.getSex(),one.getIdNumber(),one.getPermissions(),token);

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
        EmployeeResult employeeResult = new EmployeeResult(String.valueOf(employee.getId()),employee.getUsername(),employee.getName(),employee.getPhone(),employee.getSex(),employee.getIdNumber(),employee.getPermissions(),token);
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

        String tokenId = iStringRedisService.getTokenId(token);
        if (tokenId==null){
            return R.error(Code.DEL_TOKEN,"token异常");
        }
        Long aLong = Long.valueOf(tokenId);
        System.out.println("aLong = " + aLong);
        if (aLong!=id){
            return R.error("信息异常");
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
}
