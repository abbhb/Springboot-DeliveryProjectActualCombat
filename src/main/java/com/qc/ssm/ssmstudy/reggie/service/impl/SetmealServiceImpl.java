package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.mapper.SetmealMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.SetmealResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Setmeal;
import com.qc.ssm.ssmstudy.reggie.pojo.vo.DishAndCategoryVO;
import com.qc.ssm.ssmstudy.reggie.pojo.vo.SetmealAndCategoryVO;
import com.qc.ssm.ssmstudy.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public R<PageData<SetmealResult>> getSetmeal(Integer pageNum, Integer pageSize, Long storeId, String name) {
        Integer pageNumD = 1;
        Integer pageSizeD = 10;
        if (storeId==null){
            return R.error("环境异常");
        }
        if (pageNum!=null){
            pageNumD = pageNum;
        }
        if (pageSize!=null){
            pageSizeD = pageSize;
        }

        Page<SetmealAndCategoryVO> pageInfo = new Page<>(pageNumD,pageSizeD);
        QueryWrapper<SetmealAndCategoryVO> setmealAndCategoryVOQueryWrapper = new QueryWrapper<>();
        setmealAndCategoryVOQueryWrapper.eq("setmeal.store_id",storeId);
        if (name!=null){
            setmealAndCategoryVOQueryWrapper.like("setmeal.name",name);//给查询动态加条件
        }
//            <!--这里注意，自己写的sql，得注意mp的逻辑删除都不能用,包括增改的自动填充，所以得手写条件-->
        setmealAndCategoryVOQueryWrapper.eq("setmeal.is_deleted",0);//需要未删除的数据
        setmealMapper.getSetmealAndCategoryVO(pageInfo,setmealAndCategoryVOQueryWrapper);
        if (pageInfo==null){
            return R.error("查询错误");
        }
        log.info(pageInfo.getRecords().toString());

        List<SetmealAndCategoryVO> setmealResults = pageInfo.getRecords();
        List<SetmealResult> setmealResultList = new ArrayList<>();
        for (SetmealAndCategoryVO setmealAndCategoryVO:
                setmealResults) {
            SetmealResult setmealResult = new SetmealResult();
            setmealResult.setId(String.valueOf(setmealAndCategoryVO.getId()));
            setmealResult.setName(setmealAndCategoryVO.getName());
            setmealResult.setCode(setmealAndCategoryVO.getCode());
            setmealResult.setDescription(setmealAndCategoryVO.getDescription());
            setmealResult.setImage(setmealAndCategoryVO.getImage());
            setmealResult.setSort(setmealAndCategoryVO.getSort());
            setmealResult.setPrice(String.valueOf(setmealAndCategoryVO.getPrice()));
            setmealResult.setCategoryId(String.valueOf(setmealAndCategoryVO.getCategoryId()));
            setmealResult.setStatus(setmealAndCategoryVO.getStatus());
            setmealResult.setVersion(setmealAndCategoryVO.getVersion());
            setmealResult.setCreateTime(setmealAndCategoryVO.getCreateTime());
            setmealResult.setCreateUser(String.valueOf(setmealAndCategoryVO.getCreateUser()));
            setmealResult.setUpdateTime(setmealAndCategoryVO.getUpdateTime());
            setmealResult.setUpdateUser(String.valueOf(setmealAndCategoryVO.getUpdateUser()));
            setmealResult.setCategoryName(setmealAndCategoryVO.getCategoryName());
            setmealResultList.add(setmealResult);
        }
        PageData<SetmealResult> resultPageData = new PageData<>();
        resultPageData.setRecords(setmealResultList);
        resultPageData.setPages(pageInfo.getPages());
        resultPageData.setMaxLimit(pageInfo.getMaxLimit());
        resultPageData.setSize(pageInfo.getSize());
        resultPageData.setTotal(pageInfo.getTotal());
        resultPageData.setCurrent(pageInfo.getCurrent());
        resultPageData.setCountId(pageInfo.getCountId());
        if (setmealResultList.size()!=0){
            return R.success(resultPageData);
        }
        return R.error("空");
    }
}
