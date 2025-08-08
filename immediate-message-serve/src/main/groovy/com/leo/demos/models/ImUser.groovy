package com.leo.demos.models

class ImUser {
    // 用户id
    String id
    // 用户名
    String username
    // 昵称
    String name
    // 密码
    String password
    // 头像
    String avatar
    // 登录token
    String token
    // 生日
    Date birthday
    // 0 女， 1 男
    int gender = 1
    // 0 禁用， 1 启用
    int status = 1
    long createTime
    long updateTime
}
