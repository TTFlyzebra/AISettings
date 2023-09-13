package com.flyzebra.mdrvset.http;

import com.flyzebra.mdrvset.bean.BsdBean;

public class BsdInfo {
    public int BSD_CHN_INDEX = 0;//0:前BSD通道  1:后BSD通道 2:左BSD通道 3:右BSD通道
    public int reversed = 4;//0：平视（右后）；1：平视（左前）；2：平视（右前）；3：平视（左后）；4：俯视
    public int baseLinePoints_0_x = 100; //基准线
    public int baseLinePoints_0_y = 100;
    public int baseLinePoints_1_x = 100;
    public int baseLinePoints_1_y = 266;
    public int baseLinePoints_2_x = 100;
    public int baseLinePoints_2_y = 433;
    public int baseLinePoints_3_x = 100;
    public int baseLinePoints_3_y = 600;
    public int highDangerLine_startPoint_x = 400;//高危险等级基准线
    public int highDangerLine_startPoint_y = 100;
    public int highDangerLine_endPoint_x = 400;
    public int highDangerLine_endPoint_y = 600;
    public int mediumDangerLine_startPoint_x = 800;//中危险等级基准线
    public int mediumDangerLine_startPoint_y = 100;
    public int mediumDangerLine_endPoint_x = 800;
    public int mediumDangerLine_endPoint_y = 600;
    public int lowDangerLine_startPoint_x = 1200;//低危险等级基准线
    public int lowDangerLine_startPoint_y = 100;
    public int lowDangerLine_endPoint_x = 1200;
    public int lowDangerLine_endPoint_y = 600;

    public String toText() {
        return BsdBean.fromBsdInfo(this).toText();
    }

    /*WEB->MDVR
    {
        "CMD":"GETBSDCALIB",
         "DATA":{
            "BSD_CHN_INDEX":0,//0:前BSD通道  1:后BSD通道 2:左BSD通道 3:右BSD通道
        }
    }
    MDVR->WEB
    {
        "CMD":"GETBSDCALIB",
        "DATA":{
            "BSD_CHN_INDEX":0,
            "baseLinePoints_0_x":420,
            "baseLinePoints_0_y":420,
            "baseLinePoints_1_x":420,
            "baseLinePoints_1_y":420,
            "baseLinePoints_2_x":420,
            "baseLinePoints_2_y":420,
            "baseLinePoints_3_x":420,
            "baseLinePoints_3_y":420,
            "highDangerLine_startPoint_x":420,
            "highDangerLine_startPoint_y":420,
            "highDangerLine_endPoint_x":420,
            "highDangerLine_endPoint_y":420,
            "mediumDangerLine_startPoint_x":420,
            "mediumDangerLine_startPoint_y":420,
            "mediumDangerLine_endPoint_x":420,
            "mediumDangerLine_endPoint_y":420,
            "lowDangerLine_startPoint_x":420,
            "lowDangerLine_startPoint_y":420,
            "lowDangerLine_endPoint_x":420,
            "lowDangerLine_endPoint_y":420,
            "reversed":420
        },
        "ErrNO":"0000"
    }*/
    /*请求json定义太复杂，直接字符串format效率*/
    public static final String GetRequest = "{\"CMD\":\"GETBSDCALIB\",\"DATA\":{\"BSD_CHN_INDEX\":%s,}}";

    public static class GetResult {
        public String CMD;
        public BsdInfo DATA;
        public String ErrNO;
    }

    /*WEB->MDVR
    {
        "CMD":"SETBSDCALIB",
        "DATA":{
            "BSD_CHN_INDEX":0,
            "baseLinePoints_0_x":420,
            "baseLinePoints_0_y":420,
            "baseLinePoints_1_x":420,
            "baseLinePoints_1_y":420,
            "baseLinePoints_2_x":420,
            "baseLinePoints_2_y":420,
            "baseLinePoints_3_x":420,
            "baseLinePoints_3_y":420,
            "highDangerLine_startPoint_x":420,
            "highDangerLine_startPoint_y":420,
            "highDangerLine_endPoint_x":420,
            "highDangerLine_endPoint_y":420,
            "mediumDangerLine_startPoint_x":420,
            "mediumDangerLine_startPoint_y":420,
            "mediumDangerLine_endPoint_x":420,
            "mediumDangerLine_endPoint_y":420,
            "lowDangerLine_startPoint_x":420,
            "lowDangerLine_startPoint_y":420,
            "lowDangerLine_endPoint_x":420,
            "lowDangerLine_endPoint_y":420,
            "reversed":420
        }
    }
    MDVR->WEB
    {
    	"CMD":"SETBSDCALIB",
        "BSD_CHN_INDEX":0,
    	"ErrNO":"0000"
    }*/
    public static class SetRequest {
        public String CMD = "SETBSDCALIB";
        public BsdInfo DATA;
    }

    public static class SetResult {
        public String CMD;
        public int BSD_CHN_INDEX;
        public String ErrNO;
    }
}
