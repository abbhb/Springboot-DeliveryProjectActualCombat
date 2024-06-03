(function (win) {
  axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
  // 创建axios实例
  const service = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: '/',
    // 超时
    timeout: 10000
  })

  // request拦截器
  service.interceptors.request.use(config => {
    // 是否需要设置 token
    // const isToken = (config.headers || {}).isToken === false
    // if (getToken() && !isToken) {
    //   config.headers['Authorization'] = 'Bearer ' + getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
    // }

    config.headers['Authorization'] = localStorage.getItem('token') // 让每个请求携带自定义token 请根据实际情况自行修改
    // get请求映射params参数
    if (config.method === 'get' && config.params) {
      let url = config.url + '?';
      for (const propName of Object.keys(config.params)) {
        const value = config.params[propName];
        var part = encodeURIComponent(propName) + "=";
        if (value !== null && typeof(value) !== "undefined") {
          if (typeof value === 'object') {
            for (const key of Object.keys(value)) {
              let params = propName + '[' + key + ']';
              var subPart = encodeURIComponent(params) + "=";
              url += subPart + encodeURIComponent(value[key]) + "&";
            }
          } else {
            url += part + encodeURIComponent(value) + "&";
          }
        }
      }
      url = url.slice(0, -1);
      config.params = {};
      config.url = url;
    }
    return config
  }, error => {
      console.log(error)
      Promise.reject(error)
  })

  // 响应拦截器
  service.interceptors.response.use(res => {
      if (res.data.code !== 1&& res.data.msg !==""){
        if (res.data.msg === '安全退出成功'){
          window.ELEMENT.Message({
            message: res.data.msg,
            type: 'success',
            duration: 2 * 1000
          })
        }else {
          window.ELEMENT.Message({
            message: res.data.msg,
            type: 'error',
            duration: 2 * 1000
          })
        }

      }
      console.log(res.data)
      if (res.data.code === 900) {// 返回登录页面
        console.log('---/backend/page/login/login.html---')
        // localStorage.removeItem('userInfo')
        localStorage.clear()
        window.top.location.href = $web_login
      } else {
        return res.data
      }
    },
    error => {
      console.log('err' + error)
      console.log(error.response)
      let errmsg = ''
      if (error.response.status === 900) {//强制清除本地缓存,返回登录页面
        errmsg = "登录异常";
        localStorage.clear();
        window.top.location.href = $web_login
      }
      else {
        errmsg = "未知错误"
      }
      // else if (message.includes("timeout")) {
      //   message = "系统接口请求超时";
      // }
      // else if (message.includes("Request failed with status code")) {
      //   message = "系统接口" + message.substr(message.length - 3) + "异常";
      // }
      window.ELEMENT.Message({
        message: errmsg,
        type: 'error',
        duration: 3 * 1000
      })
      return Promise.reject(error)
    }
  )
   win.$axios = service
})(window);
