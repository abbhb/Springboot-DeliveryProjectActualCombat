//获取所有的菜品分类
function categoryListApi(storeId) {
    return $axios({
      'url': '/category/list',
      'method': 'get',
        params:{
          storeId:storeId
        }
    })
}


//获取商店信息
function getStoreInfo(storeId) {
    return $axios({
        'url': '/store/getInfo',
        'method': 'get',
        params:{
            storeId:storeId
        }
    })
}

//获取菜品分类对应的菜品
function dishListApi(data) {
    return $axios({
        'url': '/dish/list',
        'method': 'get',
        params:{...data}
    })
}

//获取菜品分类对应的套餐
function setmealListApi(data) {
    return $axios({
        'url': '/setmeal/list',
        'method': 'get',
        params:{...data}
    })
}

//获取购物车内商品的集合
function cartListApi(data) {
    return $axios({
        'url': '/shoppingCart/list',
        'method': 'get',
        params:{...data}
    })
}

//购物车中添加商品
function  addCartApi(data){
    return $axios({
        'url': '/shoppingCart/add',
        'method': 'post',
        data
      })
}

//购物车中修改商品
function  updateCartApi(data){
    return $axios({
        'url': '/shoppingCart/numberUpdate',
        'method': 'post',
        data:{...data}
      })
}

//删除购物车的商品
function clearCartApi(params) {
    return $axios({
        'url': '/shoppingCart/clean',
        'method': 'delete',
        params:{...params}
    })
}

//获取套餐的全部菜品
function setMealDishDetailsApi(id) {
    return $axios({
        'url': `/setmeal/dish/${id}`,
        'method': 'get',
    })
}


