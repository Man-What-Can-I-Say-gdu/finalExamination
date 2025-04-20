package com.pumpkin.entity;

import java.io.BufferedReader;

public enum RoomInstruction {
    BUILD_ROOM(1),
    JOIN_ROOM(2),
    UPDATE_ROOM(3),
    LEAVE_ROOM(5),
    DESTROY_ROOM(4),
    START_GAME(6);

    private final int code;
    RoomInstruction(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
