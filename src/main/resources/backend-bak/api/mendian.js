function addmendian(data) {
    return $axios({
        'url': '/store/addmendian',
        'method': 'post',
        data
    })
}

function editmendian(data) {
    return $axios({
        'url': '/store/editmendian',
        'method': 'post',
        data
    })
}
function querymendianById(data) {
    return $axios({
        'url': '/store/querymendianById',
        'method': 'post',
        data:{
            id:data
        }
    })
}