package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.exception.CustomException;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.common.annotation.StoreStateDetection;
import com.qc.ssm.ssmstudy.reggie.mapper.SetmealMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.DishResult;
import com.qc.ssm.ssmstudy.reggie.pojo.SetmealFlavorResult;
import com.qc.ssm.ssmstudy.reggie.pojo.SetmealResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.*;
import com.qc.ssm.ssmstudy.reggie.pojo.vo.SetmealAndCategoryVO;
import com.qc.ssm.ssmstudy.reggie.service.SetmealDishService;
import com.qc.ssm.ssmstudy.reggie.service.SetmealFlavorService;
import com.qc.ssm.ssmstudy.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealService setmealService;

    private final SetmealFlavorService setmealFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    public SetmealServiceImpl(SetmealFlavorService setmealFlavorService) {
        this.setmealFlavorService = setmealFlavorService;
    }

    @Override
    public R<PageData<SetmealResult>> getSetmeal(Integer pageNum, Integer pageSize, Long storeId, String name) {
        Integer pageNumD = 1;
        Integer pageSizeD = 10;
        if (storeId==null){
            return R.error("????????????");
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
            setmealAndCategoryVOQueryWrapper.like("setmeal.name",name);//????????????????????????
        }
//            <!--???????????????????????????sql????????????mp???????????????????????????,???????????????????????????????????????????????????-->
        setmealAndCategoryVOQueryWrapper.eq("setmeal.is_deleted",0);//????????????????????????
        setmealMapper.getSetmealAndCategoryVO(pageInfo,setmealAndCategoryVOQueryWrapper);
        if (pageInfo==null){
            return R.error("????????????");
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

//
//            setmealResult.setDishResults(dishResults);
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
        return R.success("???????????????");
    }

    @Override
    @Transactional
    public R<String> addSetmeal(SetmealResult setmealResult) {
        if (!StringUtils.isNotEmpty(setmealResult.getStoreId())){
            return R.error("????????????");
        }
        if (setmealResult.getDishResults()==null){
            return R.error("???????????????");
        }
        if (!StringUtils.isNotEmpty(setmealResult.getName())){
            return R.error("?????????????????????");
        }
        if (!StringUtils.isNotEmpty(setmealResult.getCategoryId())){
            return R.error("??????????????????");
        }
        if (!StringUtils.isNotEmpty(setmealResult.getPrice())){
            return R.error("??????????????????");
        }
        if (setmealResult.getSort()==null){
            return R.error("??????????????????");
        }
        if (setmealResult.getStatus()==null){
            return R.error("??????????????????");
        }
        if (setmealResult.getSetmealFlavors()==null){
            return R.error("??????????????????????????????");
        }
        List<DishResult> dishResults = setmealResult.getDishResults();

        Setmeal setmeal = new Setmeal();
        setmeal.setCode(setmealResult.getCode());
        setmeal.setImage(setmealResult.getImage());
        setmeal.setDescription(setmealResult.getDescription());
        setmeal.setSort(setmealResult.getSort());
        BigDecimal bigDecimal = new BigDecimal(setmealResult.getPrice());
        bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);//????????????2?????????????????????
        setmeal.setPrice(bigDecimal);
        setmeal.setName(setmealResult.getName());
        setmeal.setStatus(setmealResult.getStatus());
        setmeal.setType(2);//setmealtype???2
        setmeal.setImage(setmealResult.getImage());
        setmeal.setSaleNum(0L);//??????????????????0
        setmeal.setCategoryId(Long.valueOf(setmealResult.getCategoryId()));
        setmeal.setStoreId(Long.valueOf(setmealResult.getStoreId()));
        boolean save = setmealService.save(setmeal);
        if (!save){
            throw new CustomException("????????????");
        }
        for (DishResult dish:
             dishResults) {//???????????????????????????????????????
            SetmealDish setmealDish = new SetmealDish();
            setmealDish.setDishId(Long.valueOf(dish.getId()));//??????dish???ID
            setmealDish.setSetmealId(setmeal.getId());//?????????????????????
            setmealDish.setName(dish.getName());
//            setmealDish.setSort(dish.getSort());
            setmealDish.setCopies(dish.getCopies());
            BigDecimal bigDecimals = new BigDecimal(dish.getPrice());
            bigDecimals.setScale(2,BigDecimal.ROUND_HALF_UP);//????????????2?????????????????????
            setmealDish.setPrice(bigDecimals);
            setmealDish.setImage(dish.getImage());

            setmealDish.setStoreId(Long.valueOf(setmealResult.getStoreId()));

            setmealDishService.save(setmealDish);

        }

        //????????????,????????????????????????????????????
        for (Object obj:
                setmealResult.getSetmealFlavors()) {
            Map<String,Object> setmealFlavors = (Map<String, Object>) obj;
            System.out.println(setmealFlavors.toString());
            SetmealFlavor setmealFlavor = new SetmealFlavor();
            setmealFlavor.setSetmealId(setmeal.getId());//save????????????????????????id
            setmealFlavor.setStoreId(Long.valueOf(setmealResult.getStoreId()));
            setmealFlavor.setName((String) setmealFlavors.get("name"));
            setmealFlavor.setValue(String.valueOf((List) setmealFlavors.get("value")));
            boolean save1 = setmealFlavorService.save(setmealFlavor);
            if (!save1){
                throw new CustomException("????????????:Setmeal->addSetmealFlavor");
            }
        }

        if (setmealResult.getDishResults().size()==0){
            return R.success("???????????????????????????????????????");//??????????????????????????????
        }
        return R.success("????????????");
    }

    @Transactional
    @Override
    public R<String> updateStatus(String id, String status) {
        log.info("id = {},status = {}",id,status);
        Collection<Setmeal> entityList = new ArrayList<>();
        String[] split = id.split(",");

        for(int i =0; i < split.length ; i++){
            Setmeal setmeal = new Setmeal();
            setmeal.setId(Long.valueOf(split[i]));
            setmeal.setStatus(Integer.valueOf(status));
            entityList.add(setmeal);
        }
        boolean b = setmealService.updateBatchById(entityList);
        if (b){
            return R.success("????????????");
        }
        return R.error("????????????");
    }

    @Override
    @Transactional
    public R<String> deleteSetmeal(String id) {
        log.info("id = {}",id);
        Collection<Long> longList = new ArrayList<>();
        Collection<SetmealDish> listBig = new ArrayList<>();
        Collection<Setmeal> setmeals = new ArrayList<>();
        String[] split = id.split(",");
        for(int i =0; i < split.length ; i++){
            longList.add(Long.valueOf(split[i]));
            SetmealDish setmealDish = new SetmealDish();
            setmealDish.setSetmealId(Long.valueOf(split[i]));
            Setmeal setmeal = new Setmeal();
            setmeal.setId(Long.valueOf(split[i]));
            setmeals.add(setmeal);
            listBig.add(setmealDish);
        }
        /**
         * notebook:??????????????????Id????????????????????????????????????????????????????????????????????????
         */
        //????????????
        setmealService.removeByIds(longList);
        //???????????????????????????
        Collection<Long> setmealDishOnlyIdListBySetmealIdList = setmealDishService.getSetmealDishOnlyIdListBySetmealIdList(listBig);
        if (setmealDishOnlyIdListBySetmealIdList.size()!=0){
            setmealDishService.removeByIds(setmealDishOnlyIdListBySetmealIdList);
        }
        //???????????????????????????
        Collection<Long> setmealFlavorOnlyIdListBySetmealIdList = setmealFlavorService.getSetmealFlavorOnlyIdListBySetmealIdList(setmeals);
        if (setmealFlavorOnlyIdListBySetmealIdList.size()!=0){
            setmealFlavorService.removeByIds(setmealFlavorOnlyIdListBySetmealIdList);
        }

        return R.success("????????????");
    }

    @Override
    @Transactional
    public R<String> editSetmeal(SetmealResult setmealResult) {
        if (!StringUtils.isNotEmpty(setmealResult.getId())){return R.error("id");}
        if (!StringUtils.isNotEmpty(setmealResult.getCategoryId())){return R.error("??????Id");}
        if (!StringUtils.isNotEmpty(setmealResult.getPrice())){return R.error("???????????????");}
        if (!StringUtils.isNotEmpty(setmealResult.getName())){return R.error("??????????????????");}
        if (!StringUtils.isNotEmpty(setmealResult.getStoreId())){return R.error("??????id????????????");}
        if (setmealResult.getVersion()==null){return R.error("????????????");}
        if (setmealResult.getSort()==null){return R.error("??????????????????");}
        if (setmealResult.getStatus()==null){return R.error("??????????????????");}
        if (setmealResult.getSetmealFlavors()==null){
            return R.error("??????????????????????????????");
        }
        if (setmealResult.getSetmealFlavors().size()==0){
            return R.error("????????????????????????");
        }
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(Long.valueOf(setmealResult.getCategoryId()));
        setmeal.setName(setmealResult.getName());
        setmeal.setCode(setmealResult.getCode());
        BigDecimal bigDecimal = new BigDecimal(setmealResult.getPrice());
        bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);//????????????2?????????????????????
        setmeal.setPrice(bigDecimal);
        setmeal.setSort(setmealResult.getSort());
        setmeal.setImage(setmealResult.getImage());
        setmeal.setStatus(setmealResult.getStatus());

        setmeal.setDescription(setmealResult.getDescription());
        setmeal.setVersion(setmealResult.getVersion());//????????????,?????????????????????????????????????????????????????????????????????????????????
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Setmeal::getId,Long.valueOf(setmealResult.getId()));
        updateWrapper.eq(Setmeal::getStoreId,Long.valueOf(setmealResult.getStoreId()));
        boolean update = setmealService.update(setmeal, updateWrapper);
        if (!update){
            throw new CustomException("????????????:update");
        }
        //???????????????????????????????????????IdList
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,Long.valueOf(setmealResult.getId()));
        queryWrapper.eq(SetmealDish::getStoreId,Long.valueOf(setmealResult.getStoreId()));
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        //???????????????????????????????????????
        if (list.size()!=0){
            //list?????????idList
            Collection<Long> idList = new ArrayList<>();
            for (SetmealDish sd:
                    list) {
                idList.add(sd.getId());
            }
            //???????????????????????????
            boolean b = setmealDishService.removeByIds(idList);
            if (!b){
                throw new CustomException("????????????:removeByIds");
            }
        }

        //???????????????????????????????????????IdList
        LambdaQueryWrapper<SetmealFlavor> queryWrapperSetmealFlavor = new LambdaQueryWrapper<>();
        queryWrapperSetmealFlavor.eq(SetmealFlavor::getSetmealId,Long.valueOf(setmealResult.getId()));
        queryWrapperSetmealFlavor.eq(SetmealFlavor::getStoreId,Long.valueOf(setmealResult.getStoreId()));
        List<SetmealFlavor> listSetmealFlavor = setmealFlavorService.list(queryWrapperSetmealFlavor);
        //???????????????????????????????????????
        if (listSetmealFlavor.size()!=0){
            //list?????????idList
            Collection<Long> idList = new ArrayList<>();
            for (SetmealFlavor sd:
                    listSetmealFlavor) {
                idList.add(sd.getId());
            }
            //???????????????????????????
            boolean b = setmealFlavorService.removeByIds(idList);
            if (!b){
                throw new CustomException("????????????:removeByIds");
            }
        }

        if (setmealResult.getDishResults().size()!=0){
            List<DishResult> dishResults = setmealResult.getDishResults();
            List<SetmealDish> setmealDishList = new ArrayList<>();
            for (DishResult d:
                 dishResults) {
                SetmealDish setmealDish = new SetmealDish();
                setmealDish.setDishId(Long.valueOf(d.getId()));
                setmealDish.setName(d.getName());
                BigDecimal bigDecimalS = new BigDecimal(d.getPrice());
                bigDecimalS.setScale(2,BigDecimal.ROUND_HALF_UP);//????????????2?????????????????????
                setmealDish.setPrice(bigDecimalS);
                setmealDish.setSetmealId(Long.valueOf(setmealResult.getId()));
                setmealDish.setStoreId(Long.valueOf(setmealResult.getStoreId()));
                setmealDish.setCopies(d.getCopies());
                setmealDish.setImage(d.getImage());
                setmealDishList.add(setmealDish);
            }
            boolean b = setmealDishService.saveBatch(setmealDishList);
            if (!b){
                throw new CustomException("????????????:saveBatch");
            }
        }

        for (Object obj:
                setmealResult.getSetmealFlavors()) {
            Map<String,Object> setmealFlavors = (Map<String, Object>) obj;
            System.out.println(setmealFlavors.toString());
            SetmealFlavor setmealFlavor = new SetmealFlavor();
            setmealFlavor.setSetmealId(Long.valueOf(setmealResult.getId()));
            setmealFlavor.setStoreId(Long.valueOf(setmealResult.getStoreId()));
            setmealFlavor.setName((String) setmealFlavors.get("name"));
            setmealFlavor.setValue(String.valueOf((List) setmealFlavors.get("value")));
            boolean save1 = setmealFlavorService.save(setmealFlavor);
            if (!save1){
                throw new CustomException("????????????:Setmeal->addSetmealFlavor");
            }
        }

        return R.success("????????????");
    }

    @Override
    @StoreStateDetection
    public R<List<SetmealResult>> getSetmealList(Long categoryId, Long storeId) {
        if (categoryId==null){
            return R.error("????????????");
        }
        if (storeId==null){
            return R.error("????????????");
        }
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStoreId,storeId);
        queryWrapper.eq(Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(Setmeal::getStatus,1);//??????????????????
        queryWrapper.orderByAsc(Setmeal::getSort);
        List<Setmeal> list = super.list(queryWrapper);
        if (list==null){
            R.error("????????????");
        }
        List<SetmealResult> setmealResultList = new ArrayList<>();
        for (Setmeal setmeal:
             list) {
            //??????????????????????????????
            LambdaQueryWrapper<SetmealFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(SetmealFlavor::getSetmealId, setmeal.getId());
            List<SetmealFlavor> list1 = setmealFlavorService.list(queryWrapper1);
            List<SetmealFlavorResult> list2 = new ArrayList<>();
            for (SetmealFlavor setmealFlavor:
                    list1) {
                SetmealFlavorResult setmealFlavorResult = new SetmealFlavorResult();
                //???????????????????????????
                BeanUtils.copyProperties(setmealFlavor,setmealFlavorResult);
                list2.add(setmealFlavorResult);
            }
            SetmealResult setmealResult = new SetmealResult();
            setmealResult.setName(setmeal.getName());
            setmealResult.setSort(setmeal.getSort());
            setmealResult.setStatus(setmeal.getStatus());
            setmealResult.setCode(setmeal.getCode());
            setmealResult.setVersion(setmeal.getVersion());
            setmealResult.setId(String.valueOf(setmeal.getId()));
            setmealResult.setPrice(String.valueOf(setmeal.getPrice()));
            setmealResult.setImage(setmeal.getImage());
            setmealResult.setFlavors(list2);
            setmealResult.setType(setmeal.getType());
            log.info("setmeal = {}",setmeal.getType());
            setmealResult.setSaleNum(String.valueOf(setmeal.getSaleNum()));
            setmealResult.setStoreId(String.valueOf(setmeal.getStoreId()));
            setmealResultList.add(setmealResult);
        }
        return R.success(setmealResultList);
    }

    @Override
    public R<SetmealResult> getSetmealDetail(String setmealId) {
        if (!StringUtils.isNotEmpty(setmealId)){
            return R.error("?????????");
        }
        Setmeal setmeal = super.getById(Long.valueOf(setmealId));
        if (setmeal==null){
            throw new CustomException("??????");
        }
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,Long.valueOf(setmealId));//??????id???????????????????????????????????????????????????
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        SetmealResult setmealResult = new SetmealResult();
        BeanUtils.copyProperties(setmeal,setmealResult);
        List<DishResult> dishResults = new ArrayList<>();
        for (SetmealDish setmealDish:
             list) {
            DishResult dishResult = new DishResult();
            dishResult.setName(setmealDish.getName());//????????????????????????
            dishResult.setPrice(String.valueOf(setmealDish.getPrice()));//??????????????????????????????????????????
            dishResult.setCopies(setmealDish.getCopies());//??????
            dishResult.setImage(setmealDish.getImage());
            dishResults.add(dishResult);
        }
        setmealResult.setDishResults(dishResults);
        return R.success(setmealResult);
    }
}


