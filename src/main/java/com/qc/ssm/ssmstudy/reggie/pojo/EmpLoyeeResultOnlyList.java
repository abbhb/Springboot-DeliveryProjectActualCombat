package com.qc.ssm.ssmstudy.reggie.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 2023年1月11日14:52:26
 * 单独为员工管理列表进行升级
 * 加入是否允许操作的标志字段
 */
@Data
public class EmpLoyeeResultOnlyList extends EmployeeResult{
    public EmpLoyeeResultOnlyList(String id, String username, String name, String phone, String sex, String idNumber, Integer permissions, Integer status, String storeId, String token, Integer isAllowOperation) {
        super(id, username, name, phone, sex, idNumber, permissions, status, storeId, token);
        this.isAllowOperation = isAllowOperation;
    }

    private Integer isAllowOperation;//是否允许前端操作(表现在几个删除图标是否允许点击)
}
