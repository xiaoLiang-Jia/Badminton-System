import request from './index'

export function createBooking(data) {
  return request({
    url: '/booking/create',
    method: 'post',
    data
  })
}

export function getBookingList(params) {
  return request({
    url: '/booking/list',
    method: 'get',
    params
  })
}

export function getBookingById(id) {
  return request({
    url: `/booking/${id}`,
    method: 'get'
  })
}

export function cancelBooking(id) {
  return request({
    url: `/booking/cancel/${id}`,
    method: 'post'
  })
}

export function payBooking(id, paymentMethod = 3) {
  return request({
    url: `/booking/pay/${id}`,
    method: 'post',
    params: { paymentMethod }
  })
}

export function checkCourtAvailable(params) {
  return request({
    url: '/booking/check',
    method: 'get',
    params
  })
}
