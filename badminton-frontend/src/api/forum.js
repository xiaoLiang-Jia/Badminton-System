import request from './index'

export function getForumList(params) {
  return request({
    url: '/forum/list',
    method: 'get',
    params
  })
}

export function getForumById(id) {
  return request({
    url: `/forum/${id}`,
    method: 'get'
  })
}

export function publishForum(data) {
  return request({
    url: '/forum/publish',
    method: 'post',
    data
  })
}

export function updateForum(data) {
  return request({
    url: '/forum/update',
    method: 'put',
    data
  })
}

export function deleteForum(id) {
  return request({
    url: `/forum/delete/${id}`,
    method: 'delete'
  })
}

export function likeForum(id) {
  return request({
    url: `/forum/like/${id}`,
    method: 'post'
  })
}

export function unlikeForum(id) {
  return request({
    url: `/forum/unlike/${id}`,
    method: 'post'
  })
}

export function getForumComments(id) {
  return request({
    url: `/forum/comments/${id}`,
    method: 'get'
  })
}

export function addComment(data) {
  return request({
    url: '/forum/comment',
    method: 'post',
    params: data
  })
}

export function deleteComment(id) {
  return request({
    url: `/forum/comment/${id}`,
    method: 'delete'
  })
}
