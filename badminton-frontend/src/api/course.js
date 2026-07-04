import request from './index'

export function getCourseList(params) {
  return request({
    url: '/course/list',
    method: 'get',
    params
  })
}

export function getCourseById(id) {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

export function getCoachCourses(coachId) {
  return request({
    url: `/course/coach/${coachId}`,
    method: 'get'
  })
}

export function addCourse(data) {
  return request({
    url: '/course/add',
    method: 'post',
    data
  })
}

export function updateCourse(data) {
  return request({
    url: '/course/update',
    method: 'put',
    data
  })
}

export function deleteCourse(id) {
  return request({
    url: `/course/delete/${id}`,
    method: 'delete'
  })
}

export function enrollCourse(id) {
  return request({
    url: `/course/enroll/${id}`,
    method: 'post'
  })
}

export function cancelCourse(id) {
  return request({
    url: `/course/cancel/${id}`,
    method: 'post'
  })
}

export function getMyCourses(params) {
  return request({
    url: '/course/my-courses',
    method: 'get',
    params
  })
}

export function getCoachStudents(params) {
  return request({
    url: '/course/coach-students',
    method: 'get',
    params
  })
}
