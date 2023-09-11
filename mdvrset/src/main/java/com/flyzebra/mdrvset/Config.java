package com.flyzebra.mdrvset;

import java.util.Hashtable;

public class Config {
    public static final int CAM_WIDTH = 1280;
    public static final int CAM_HEIGHT = 720;

    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;

    public static final byte[] MIN_SCREEN = {
            (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    public static final byte[] MAX_SCREEN = {
            (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    public static Hashtable<Long, byte[]> itemSetList = new Hashtable<>();
}
