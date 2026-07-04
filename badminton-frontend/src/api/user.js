import request from './index'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/user/changePassword',
    method: 'post',
    params: data
  })
}

export function getBalance() {
  return request({
    url: '/user/balance',
    method: 'get'
  })
}

export function recharge(amount) {
  return request({
    url: '/user/recharge',
    method: 'post',
    params: { amount }
  })
}

export function getUserList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

export function adminUpdateUser(id, data) {
  return request({
    url: `/user/admin/${id}`,
    method: 'put',
    data
  })
}

export function adminCreateUser(data) {
  return request({
    url: '/user/admin',
    method: 'post',
    data
  })
}

export function adminDeleteUser(id) {
  return request({
    url: `/user/admin/${id}`,
    method: 'delete'
  })
}
