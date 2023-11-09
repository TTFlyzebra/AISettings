package com.flyzebra.mdrvset.http;

/**
 * Description:GpsSpeed
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/11/8 14:35
 */
public class SpeedInfo {
    public String CMD;
    public int speed;

    /*WEB->MDVR
    {
        "CMD":"SET_TEMP_SPEED",
            "DATA":{
        "speed":50,
    }
    }
    MDVR->WEB
    {
        "CMD":" SET_TEMP_SPEED ",
            "ErrNO":"0000"
    }*/
}
