package com.leo.demos

class ImmediateResponseMessage {
    String code
    String data
    String message
}

enum ImmediateResponseCode {
    // 消息发送成功
    SUCCESS("0000"),
    // 上线通知
    ONLINE("0001"),
    // 下线通知
    OFFLINE("0002"),
    // 消息发送失败
    FAIL("9999"),
    // 获取用户ID成功
    USER_ID_SUCCESS("0003");

    String code
    private ImmediateResponseCode(String code) {
        this.code = code
    }
}
