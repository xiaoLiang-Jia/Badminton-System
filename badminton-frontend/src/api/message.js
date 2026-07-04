import request from './index'

export function getConversations() {
  return request({
    url: '/messages/conversations',
    method: 'get'
  })
}

export function getMessages(userId) {
  return request({
    url: `/messages/${userId}`,
    method: 'get'
  })
}

export function sendMessage(data) {
  return request({
    url: '/messages',
    method: 'post',
    data
  })
}

export function markAsRead(userId) {
  return request({
    url: `/messages/read/${userId}`,
    method: 'put'
  })
}

export function getUnreadCount() {
  return request({
    url: '/messages/unread',
    method: 'get'
  })
}