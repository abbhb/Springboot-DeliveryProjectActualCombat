function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

function sendCodeApi(params) {
    return $axios({
        'url': '/user/sendmsg',
        'method': 'get',
        params
    })
}

