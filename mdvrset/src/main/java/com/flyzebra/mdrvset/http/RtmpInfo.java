package com.flyzebra.mdrvset.http;

import java.util.List;

public class RtmpInfo {
    public int Channel;
    public String CMD;
    public String RTMP_ADDR;

/*WEB->MDVR
{
    "CMD": " LIVE_PREVIEW_RTMP",
    "DO": [
        {
            "Channel": 1,
            "CMD": "PLAY",
            "STREAM_TYPE": 1
        },
        {
            "Channel": 2,
            "CMD": "STOP",
            "STREAM_TYPE": 1
        }
    ]
}*/

/*MDVR->WEB
{
    " LIVE_PREVIEW_RTMP": [
        {
            "Channel": 1,
            "CMD": "PLAY",
            "RTMP_ADDR": " rtmp://192.168.3.249:1935/live1"
        },
        {
            "Channel": 2,
            "CMD": "STOP",
            "RESULT": "OK"
        }
    ]
}
*/

    /*请求json定义太复杂，直接字符串format效率*/
    public static final String GetRequest = "{\"CMD\":\"LIVE_PREVIEW_RTMP\",\"DO\":[{\"Channel\":%s,\"CMD\":\"PLAY\",\"STREAM_TYPE\":1},";

    public static class GetRtmpResult {
        public List<RtmpInfo> LIVE_PREVIEW_RTMP;
    }
}
