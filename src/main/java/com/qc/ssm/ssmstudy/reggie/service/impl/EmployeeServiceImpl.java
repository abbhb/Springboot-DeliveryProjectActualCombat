package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.Code;
import com.qc.ssm.ssmstudy.reggie.common.CustomException;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.EmpLoyeeResultOnlyList;
import com.qc.ssm.ssmstudy.reggie.pojo.EmployeeResult;
import com.qc.ssm.ssmstudy.reggie.pojo.ValueLabelResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Employee;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.mapper.EmployeeMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Store;
import com.qc.ssm.ssmstudy.reggie.service.EmployeeService;
import com.qc.ssm.ssmstudy.reggie.service.IStringRedisService;
import com.qc.ssm.ssmstudy.reggie.utils.JWTUtil;
import com.qc.ssm.ssmstudy.reggie.utils.PWDMD5;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        EmployeeResult employeeResult = new EmployeeResult(String.valueOf(one.getId()),one.getUsername(),one.getName(),one.getPhone(),one.getSex(),one.getIdNumber(),one.getPermissions(),one.getStatus(),String.valueOf(one.getStoreId()),token);

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
        EmployeeResult employeeResult = new EmployeeResult(String.valueOf(employee.getId()),employee.getUsername(),employee.getName(),employee.getPhone(),employee.getSex(),employee.getIdNumber(),employee.getPermissions(),employee.getStatus(),String.valueOf(employee.getStoreId()),token);
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
//        employee.setUpdateUser(id);

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
//        employee.setUpdateUser(Long.valueOf(id));
        //操作数据库更新密码和盐
        boolean update = employeeService.update(employee, lambdaUpdateWrapper);
        if (update){
            return R.successOnlyMsg("修改成功",Code.SUCCESS);
        }

        return R.error("修改失败");
    }

    @Override
    public R<PageData> getEmployeeList(Integer pageNum, Integer pageSize, String name,String caozuoId) {
        if (pageNum==null){
            return R.error("传参错误");
        }
        if (pageSize==null){
            return R.error("传参错误");
        }
        if (!StringUtils.isNotEmpty(caozuoId)){
            return R.error("环境异常");
        }
        Employee caozuoEmployee = employeeService.getById(Long.valueOf(caozuoId));
        if (caozuoEmployee==null){
            throw new CustomException("业务异常");
        }
        Page pageInfo = new Page(pageNum,pageSize);
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //选择返回的数据
        lambdaQueryWrapper.select(Employee::getId,Employee::getName,Employee::getPhone,Employee::getSex,Employee::getUsername,Employee::getPhone,Employee::getIdNumber,Employee::getPermissions,Employee::getStatus,Employee::getStoreId);
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        if (caozuoEmployee.getPermissions()==2){
            //当前是门店管理身份,此时仅仅显示门店内的数据
            lambdaQueryWrapper.eq(Employee::getStoreId,caozuoEmployee.getStoreId());
        }else if (caozuoEmployee.getPermissions()==3){
            //若当前是员工的身份，此时不返回数据了
            return R.error("员工就看员工该看的，不该看的别看");

        }
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Employee::getCreateTime);//按照创建时间排序
        employeeService.page(pageInfo,lambdaQueryWrapper);
        PageData<EmpLoyeeResultOnlyList> pageData = new PageData<>();
        List<EmpLoyeeResultOnlyList> results = new ArrayList<>();
        for (Object employee : pageInfo.getRecords()) {
            Employee employee1 = (Employee) employee;

            Integer isAllowOperation = 1;//默认是允许(1)
            if (employee1.getPermissions()<=caozuoEmployee.getPermissions()){//如果权限大于等于操作人的权限,为不可操作，id为1的员工除外，为超级管理员
                isAllowOperation = 0;//不允许
            }
            if (caozuoEmployee.getId()==1L){
                isAllowOperation = 1;
            }
            EmpLoyeeResultOnlyList employeeResult = new EmpLoyeeResultOnlyList(String.valueOf(employee1.getId()),employee1.getUsername(),employee1.getName(),employee1.getPhone(),employee1.getSex(),employee1.getIdNumber(),employee1.getPermissions(),employee1.getStatus(),String.valueOf(employee1.getStoreId()),null,isAllowOperation);
            results.add(employeeResult);
        }
        pageData.setPages(pageInfo.getPages());
        pageData.setTotal(pageInfo.getTotal());
        pageData.setCountId(pageInfo.getCountId());
        pageData.setCurrent(pageInfo.getCurrent());
        pageData.setSize(pageInfo.getSize());
        pageData.setRecords(results);
        pageData.setMaxLimit(pageInfo.getMaxLimit());
        return R.success(pageData);
    }

    @Override
    public R<String> updataEmployeeStatus(String userId, String caozuoId, String userStatus, String token) {
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
        Employee employees = employeeService.getById(Long.valueOf(caozuoId));//操作人的信息
        if (employees==null){
            throw new CustomException("业务异常");
        }




        Integer status = null;
        if (userStatus.equals("1")){
            status = 0;
        }else if (userStatus.equals("0")){
            status = 1;
        }else {
//            log.info(status+"");
        }
        LambdaUpdateWrapper<Employee> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Employee::getId,Long.valueOf(userId));
        if(employees.getPermissions()==2){
            updateWrapper.eq(Employee::getStoreId,employees.getStoreId());//门店管理只允许更改本门店的员工
        }
        if (employees.getId()==1L){
            //admin,超级管理员，百无禁忌,不过不能对自身进行权限和删除禁用操作
            if (Long.valueOf(userId)==Long.valueOf(caozuoId)||Long.valueOf(userId)==1L){//不能对自身操作，其他的情况都在gt中避免了
                return R.error("不能禁用admin用户哦");
            }

        }else {
            updateWrapper.gt(Employee::getPermissions,employees.getPermissions());
        }


        Employee employee = new Employee();
        employee.setId(Long.valueOf(userId));
        employee.setStatus(status);
//        employee.setUpdateUser(Long.valueOf(caozuoId));
        boolean update = employeeService.update(employee, updateWrapper);
        if (update){
            return R.success("更改成功");
        }
        return R.error("系统异常!");
    }

    @Override
    public R<EmployeeResult> deleteEmployee(String userId, String caozuoId, String token) {//删除和禁用后立即删除token
        if (!StringUtils.isNotEmpty(caozuoId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (!StringUtils.isNotEmpty(userId)){
            return R.error("参数异常");
        }
        Employee employees = employeeService.getById(Long.valueOf(caozuoId));//操作人的信息
        if (employees==null){
            throw new CustomException("业务异常");
        }

        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getId,Long.valueOf(userId));

        if(employees.getPermissions()==2){
            employeeLambdaQueryWrapper.eq(Employee::getStoreId,employees.getStoreId());//门店管理只允许删除本门店的员工
        }

        if (employees.getId()==1L){
            //admin,超级管理员，百无禁忌,不过不能对自身进行权限和删除禁用操作
            if (Long.valueOf(userId)==Long.valueOf(caozuoId)||Long.valueOf(userId)==1L){//不能对自身操作，其他的情况都在gt中避免了
                return R.error("不能删除admin用户哦");
            }

        }else {
            employeeLambdaQueryWrapper.gt(Employee::getPermissions,employees.getPermissions());
        }
        boolean b = employeeService.remove(employeeLambdaQueryWrapper);
        if (b){
            return R.success("删除成功");
        }

        return R.error("系统异常,请刷新重试!");
    }

    @Override
    public R<String> updataEmployee(String caozuoId, String userid,String name, String username, String phone, String idNumber, String status, String permissions, String storeId, String sex, String token) {
        if (token==null){
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (caozuoId==null){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (userid==null){
            return R.error("传参异常(id)");
        }
        if (name==null){
            return R.error("名字不能为空");
        }
        if (username==null){
            return R.error("用户名不能为空");
        }
        if (phone==null){
            return R.error("手机号不能为空");
        }
        if (idNumber==null){
            return R.error("身份证号不能为空");
        }
        if (status==null){
            return R.error("状态不能为空");
        }
        if (permissions==null){
            return R.error("权限不能为空");
        }
        if (sex==null){
            return R.error("性别不能为空");
        }
        log.info(permissions.getClass().getName()+"+"+permissions);
        if (Integer.valueOf(permissions)!=1){
            if (storeId==null){
                return R.error("当前权限必须绑定门店");
            }
            if (storeId.equals("null")){
                return R.error("当前权限必须绑定门店");
            }
        }
        Employee employees = employeeService.getById(Long.valueOf(caozuoId));
        if (employees==null){
            throw new CustomException("业务异常");
        }

        LambdaUpdateWrapper<Employee> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Employee::getId,Long.valueOf(userid));


        if (employees.getPermissions()==1&&employees.getId()==1L){
            //超级管理员,无限制
        }else if (employees.getPermissions()==1&&employees.getId()!=1L){
            //不能添加超级管理员的超级管理员
            if (Integer.valueOf(permissions)==1){
                return R.error("你没有权限添加超级管理员");
            }
        }else if (employees.getPermissions()==2){
            if (Integer.valueOf(permissions)<2){
                return R.error("你没有权限添加管理员");
            }
            if (Long.valueOf(storeId)!=employees.getStoreId()){
                return R.error("你只能在本门店内添加用户");
            }
        }else {
            return R.error("你没有权限添加员工");
        }


        if (employees.getId()==1L){
            //admin,超级管理员，百无禁忌,不过不能对自身进行权限和删除禁用操作
            if (Long.valueOf(userid)==Long.valueOf(caozuoId)||Long.valueOf(userid)==1L){//不能对自身操作，其他的情况都在gt中避免了
                if (Integer.valueOf(permissions)!=1){
                    return R.error("admin用户的权限必须为超级管理员");
                }
                if (Integer.valueOf(status)!=1){
                    return R.error("admin用户的权限必须启用");
                }
            }
        }else {
            updateWrapper.gt(Employee::getPermissions,employees.getPermissions());
        }
        Employee employee = new Employee();
        employee.setId(Long.valueOf(userid));
        employee.setName(name);
        employee.setUsername(username);
        employee.setPhone(phone);
        employee.setStatus(Integer.valueOf(status));
        employee.setSex(sex);
//        employee.setUpdateUser(Long.valueOf(caozuoId));
        employee.setIdNumber(idNumber);
        employee.setPermissions(Integer.valueOf(permissions));
        if (Integer.valueOf(permissions)==1){
            updateWrapper.set(Employee::getStoreId,null);
        }else {
            employee.setStoreId(Long.valueOf(storeId));
        }
        boolean update = employeeService.update(employee, updateWrapper);
        if (update){
            return R.success("更新成功");
        }
        return R.error("更新失败");
    }

    @Override
    public R<EmployeeResult> addEmployee(String caozuoId, String name, String username, String password, String phone, String idNumber, String status, String permissions, String storeId, String sex, String token) {
        if (token==null){
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (caozuoId==null){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (password==null){
            return R.error("必须赋予初始密码");
        }
        if (name==null){
            return R.error("名字不能为空");
        }
        if (username==null){
            return R.error("用户名不能为空");
        }
        if (phone==null){
            return R.error("手机号不能为空");
        }
        if (idNumber==null){
            return R.error("身份证号不能为空");
        }
        if (status==null){
            return R.error("状态不能为空");
        }
        if (permissions==null){
            return R.error("权限不能为空");
        }
        if (sex==null){
            return R.error("性别不能为空");
        }
        log.info(permissions.getClass().getName()+"+"+permissions);
        if (Integer.valueOf(permissions)!=1){
            if (storeId==null){
                return R.error("当前权限必须绑定门店");
            }
            if (storeId.equals("null")){
                return R.error("当前权限必须绑定门店");
            }
        }

        Employee employeeadd = employeeService.getById(Long.valueOf(caozuoId));//添加人
        if (employeeadd.getPermissions()==1&&employeeadd.getId()==1L){
            //超级管理员
        }else if (employeeadd.getPermissions()==1&&employeeadd.getId()!=1L){
            //不能添加超级管理员的超级管理员
            if (Integer.valueOf(permissions)==1){
                return R.error("你没有权限添加超级管理员");
            }
        }else if (employeeadd.getPermissions()==2){
            if (Integer.valueOf(permissions)<2){
                return R.error("你没有权限添加管理员");
            }
            if (!Long.valueOf(storeId).equals(employeeadd.getStoreId())){
                return R.error("你只能在本门店内添加用户");
            }
        }else {
            return R.error("你没有权限添加员工");
        }

        String salt = PWDMD5.getSalt();
        String md5Encryption = PWDMD5.getMD5Encryption(password, salt);
        Employee employee = new Employee();
        employee.setUsername(username);
        employee.setName(name);
        employee.setIdNumber(idNumber);
        employee.setPhone(phone);
        employee.setSex(sex);
        employee.setPermissions(Integer.valueOf(permissions));

        employee.setPassword(md5Encryption);
        employee.setSalt(salt);
        employee.setStatus(Integer.valueOf(status));
        if (Integer.valueOf(permissions)==1){
            //默认就是null
        }else {
            employee.setStoreId(Long.valueOf(storeId));
        }
        boolean save = employeeService.save(employee);
        if (save){
            return R.success("添加成功");
        }
        return R.error("创建失败");
    }

    @Override
    public R<List<ValueLabelResult>> getEmployeeListOnlyIdWithName(String caozuoId) {
        if (!StringUtils.isNotEmpty(caozuoId)){
            return R.error("环境遗产");
        }
        Employee byId = employeeService.getById(Long.valueOf(caozuoId));
        if (byId==null){
            throw new CustomException("环境异常");
        }


        List<ValueLabelResult> employeeIdNames = new ArrayList<>();
        if (byId.getPermissions()==1&&byId.getId()!=1L){
            //不是超级管理员的管理员
            ValueLabelResult valueLabelResult2 = new ValueLabelResult("2","门店管理");
            ValueLabelResult valueLabelResult3 = new ValueLabelResult("3","门店用户");
            employeeIdNames.add(valueLabelResult2);
            employeeIdNames.add(valueLabelResult3);

        }else if (byId.getId()==1L){
            ValueLabelResult valueLabelResult1 = new ValueLabelResult("1","超级管理员");
            ValueLabelResult valueLabelResult2 = new ValueLabelResult("2","门店管理");
            ValueLabelResult valueLabelResult3 = new ValueLabelResult("3","门店用户");
            employeeIdNames.add(valueLabelResult1);
            employeeIdNames.add(valueLabelResult2);
            employeeIdNames.add(valueLabelResult3);
        }else if (byId.getPermissions()==2){
            ValueLabelResult valueLabelResult3 = new ValueLabelResult("3","门店用户");
            employeeIdNames.add(valueLabelResult3);
        }else {
            return R.error("异常");
        }

        return R.success(employeeIdNames);
    }


}
