package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
