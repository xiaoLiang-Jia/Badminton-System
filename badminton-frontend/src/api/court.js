import request from './index'

export function getCourtList(params) {
  return request({
    url: '/court/list',
    method: 'get',
    params
  })
}

export function getCourtById(id) {
  return request({
    url: `/court/${id}`,
    method: 'get'
  })
}

export function getAvailableCourts() {
  return request({
    url: '/court/available',
    method: 'get'
  })
}

export function getAvailableTimeSlots(courtId, date) {
  return request({
    url: '/court/available-time',
    method: 'get',
    params: { courtId, date }
  })
}

export function addCourt(data) {
  return request({
    url: '/court/add',
    method: 'post',
    data
  })
}

export function updateCourt(data) {
  return request({
    url: '/court/update',
    method: 'put',
    data
  })
}

export function deleteCourt(id) {
  return request({
    url: `/court/delete/${id}`,
    method: 'delete'
  })
}
