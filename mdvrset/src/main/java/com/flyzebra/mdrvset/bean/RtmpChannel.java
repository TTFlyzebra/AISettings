package com.flyzebra.mdrvset.bean;

import java.util.List;


/**
 {
    "LIVE_PREVIEW_RTMP": [
        {
            "Channel": 2,
            "CMD": "PLAY",
            "STREAM_TYPE": 1,
            "RTMP_ADDR": "rtmp://192.168.3.249:1935/live2"
        }
    ]
 }
 */

public class RtmpChannel {
    public int Channel;
    public String CMD;
    public int STREAM_TYPE;
    public String RTMP_ADDR;

    public static class GetRtmpResult {
        public List<RtmpChannel> LIVE_PREVIEW_RTMP;
    }
}
