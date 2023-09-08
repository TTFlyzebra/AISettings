package com.flyzebra.mdrvset;

import java.util.Hashtable;

public class Config {
    public static final int CAMERA_W = 1280;
    public static final int CAMERA_H = 720;
    public static final byte[] MIN_SCREEN = {
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    public static final byte[] MAX_SCREEN = {
            (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    public static Hashtable<Long, byte[]> itemSetList = new Hashtable<>();
}
