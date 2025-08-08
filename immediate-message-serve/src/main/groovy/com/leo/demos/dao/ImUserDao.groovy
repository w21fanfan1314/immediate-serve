package com.leo.demos.dao

import com.leo.demos.db.DbUtil
import com.leo.demos.models.ImUser

import java.sql.ResultSet

class ImUserDao {

    static ImUser findByUsername(String username) {
        def sql = "select * from im_user where username = ?"
        def rs = DbUtil.executeQuery(sql, username)
        if (rs.next()) {
            return toUserByResult(rs)
        }
        return null
    }
    static ImUser findByToken(String token) {
        def sql = "select * from im_user where token = ?"
        def rs = DbUtil.executeQuery(sql, token)
        if (rs.next()) {
            return toUserByResult(rs)
        }
        return null
    }
    static int insert(ImUser user) {
        def sql = "insert into im_user(id, username, name, password, avatar, token, birthday, gender, status, create_time, update_time) values(?,?,?,?,?,?,?,?,?,?,?)"
        return DbUtil.executeUpdate(sql, user.id, user.username, user.name, user.password, user.avatar, user.token, user.birthday, user.gender, user.status, user.createTime, user.updateTime)
    }
    static int update(ImUser user) {
        def sql = "update im_user set name = ?, avatar = ?, birthday = ?, gender = ?, status = ?, update_time = ? where id = ?"
        return DbUtil.executeUpdate(sql, user.name, user.avatar, user.birthday, user.gender, user.status, user.updateTime, user.id)
    }
    static int delete(String id) {
        def sql = "delete from im_user where id = ?"
        return DbUtil.executeUpdate(sql, id)
    }


    private static ImUser toUserByResult(ResultSet rs) {
        def user = new ImUser()
        user.with {
            id = rs.getString("id")
            name = rs.getString("name")
            password = rs.getString("password")
            avatar = rs.getString("avatar")
            token = rs.getString("token")
            birthday = rs.getDate("birthday")
            gender = rs.getInt("gender")
            status = rs.getInt("status")
            createTime = rs.getLong("create_time")
            updateTime = rs.getLong("update_time")
        }
        return user
    }
}
