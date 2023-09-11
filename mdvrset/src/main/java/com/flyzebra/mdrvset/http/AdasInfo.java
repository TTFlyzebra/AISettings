package com.flyzebra.mdrvset.http;

public class AdasInfo {
    public int horizon = 420; //地平线，单位：pixel，有效值：大于等于0
    public int carMiddle = 0; //车辆中线距离光心偏移量，单位：pixel
    public int cameraHeight = 200; //摄像头距离地面高度，单位：cm，有效值：大于等于0
    public int cameraToAxle = 200; //摄像头距离前车轴距离，单位：cm
    public int carWidth = 200; //车辆宽度，单位：cm，有效值：大于0
    public int cameraToBumper = 200; //摄像头距离车头距离，单位：cm
    public int cameraToLeftWheel = 120; //摄像头距离左前轮的水平距离，单位：cm

    /*WEB->MDVR
    {
        "CMD":"GETADASCALIB",
    }
    MDVR->WEB
    {
        "CMD":" GETADASCALIB",
        "DATA":{
            "horizon":420,
            "carMiddle":0,
            "cameraHeight":200,
            "cameraToAxle":200,
            "carWidth":200,
            "cameraToBumper":200,
            "cameraToLeftWheel":120
        },
        "ErrNO":"0000"
    }*/
    public static class GetRequest {
        public String CMD = "GETADASCALIB";
    }

    public static class GetResult {
        public String CMD;
        public AdasInfo DATA;
        public String ErrNO;
    }

    /*WEB->MDVR
    {
        "CMD":"SETADASCALIB",
        "DATA":{
        "horizon":420,
            "carMiddle":0,
            "cameraHeight":200,
            "cameraToAxle":200,
            "carWidth":200,
            "cameraToBumper":200,
            "cameraToLeftWheel":120
        }
    }
    MDVR->WEB
    {
        "CMD":" SETADASCALIB",
        "ErrNO":"0000"
    }*/
    public static class SetRequest {
        public String CMD = "SETADASCALIB";
        public AdasInfo DATA;
    }

    public static class SetResult {
        public String CMD;
        public String ErrNO;
    }
}
