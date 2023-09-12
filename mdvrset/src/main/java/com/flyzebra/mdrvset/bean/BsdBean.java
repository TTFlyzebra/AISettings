package com.flyzebra.mdrvset.bean;


import android.graphics.Point;

import com.flyzebra.mdrvset.http.BsdInfo;

public class BsdBean {
    public int BSD_CHN_INDEX = 0;
    public int reversed = 4;
    public Point[] baseLine = new Point[4];
    public Point[] highLine = new Point[2];
    public Point[] mediumLine = new Point[2];
    public Point[] lowLine = new Point[2];

    public BsdBean() {
        for (int i = 0; i < 4; i++) {
            baseLine[i] = new Point();
        }
        for (int i = 0; i < 2; i++) {
            highLine[i] = new Point();
            mediumLine[i] = new Point();
            lowLine[i] = new Point();
        }
    }

    public static BsdBean fromBsdInfo(BsdInfo bsdInfo) {
        BsdBean bsdBean = new BsdBean();
        bsdBean.BSD_CHN_INDEX = bsdInfo.BSD_CHN_INDEX;
        bsdBean.reversed = bsdInfo.reversed;
        bsdBean.baseLine[0].x = bsdInfo.baseLinePoints_0_x; //基准线
        bsdBean.baseLine[0].y = bsdInfo.baseLinePoints_0_y;
        bsdBean.baseLine[1].x = bsdInfo.baseLinePoints_1_x;
        bsdBean.baseLine[1].y = bsdInfo.baseLinePoints_1_y;
        bsdBean.baseLine[2].x = bsdInfo.baseLinePoints_2_x;
        bsdBean.baseLine[2].y = bsdInfo.baseLinePoints_2_y;
        bsdBean.baseLine[3].x = bsdInfo.baseLinePoints_3_x;
        bsdBean.baseLine[3].y = bsdInfo.baseLinePoints_3_y;
        bsdBean.highLine[0].x = bsdInfo.highDangerLine_startPoint_x;//高危险等级基准线
        bsdBean.highLine[0].y = bsdInfo.highDangerLine_startPoint_y;
        bsdBean.highLine[1].x = bsdInfo.highDangerLine_endPoint_x;
        bsdBean.highLine[1].y = bsdInfo.highDangerLine_endPoint_y;
        bsdBean.mediumLine[0].x = bsdInfo.mediumDangerLine_startPoint_x;//中危险等级基准线
        bsdBean.mediumLine[0].y = bsdInfo.mediumDangerLine_startPoint_y;
        bsdBean.mediumLine[1].x = bsdInfo.mediumDangerLine_endPoint_x;
        bsdBean.mediumLine[1].y = bsdInfo.mediumDangerLine_endPoint_y;
        bsdBean.lowLine[0].x = bsdInfo.lowDangerLine_startPoint_x;//低危险等级基准线
        bsdBean.lowLine[0].y = bsdInfo.lowDangerLine_startPoint_y;
        bsdBean.lowLine[1].x = bsdInfo.lowDangerLine_endPoint_x;
        bsdBean.lowLine[1].y = bsdInfo.lowDangerLine_endPoint_y;
        return bsdBean;
    }

    public BsdInfo toBsdInfo(BsdBean bsdBean) {
        BsdInfo bsdInfo = new BsdInfo();
        bsdInfo.BSD_CHN_INDEX = bsdBean.BSD_CHN_INDEX;
        bsdInfo.reversed = bsdBean.reversed;
        bsdInfo.baseLinePoints_0_x = bsdBean.baseLine[0].x;
        bsdInfo.baseLinePoints_0_y = bsdBean.baseLine[0].y;
        bsdInfo.baseLinePoints_1_x = bsdBean.baseLine[1].x;
        bsdInfo.baseLinePoints_1_y = bsdBean.baseLine[1].y;
        bsdInfo.baseLinePoints_2_x = bsdBean.baseLine[2].x;
        bsdInfo.baseLinePoints_2_y = bsdBean.baseLine[2].y;
        bsdInfo.baseLinePoints_3_x = bsdBean.baseLine[3].x;
        bsdInfo.baseLinePoints_3_y = bsdBean.baseLine[3].y;
        bsdInfo.highDangerLine_startPoint_x = bsdBean.highLine[0].x;
        bsdInfo.highDangerLine_startPoint_y = bsdBean.highLine[0].y;
        bsdInfo.highDangerLine_endPoint_x = bsdBean.highLine[1].x;
        bsdInfo.highDangerLine_endPoint_y = bsdBean.highLine[1].y;
        bsdInfo.mediumDangerLine_startPoint_x = bsdBean.mediumLine[0].x;
        bsdInfo.mediumDangerLine_startPoint_y = bsdBean.mediumLine[0].y;
        bsdInfo.mediumDangerLine_endPoint_x = bsdBean.mediumLine[1].x;
        bsdInfo.mediumDangerLine_endPoint_y = bsdBean.mediumLine[1].y;
        bsdInfo.lowDangerLine_startPoint_x = bsdBean.lowLine[0].x;
        bsdInfo.lowDangerLine_startPoint_y = bsdBean.lowLine[0].y;
        bsdInfo.lowDangerLine_endPoint_x = bsdBean.lowLine[1].x;
        bsdInfo.lowDangerLine_endPoint_y = bsdBean.lowLine[1].y;
        return bsdInfo;
    }

    public String toText() {
        StringBuffer str = new StringBuffer();
        str.append("baseLine:\n");
        for (int i = 0; i < 4; i++) {
            str.append("p").append(i + 1).append("(").append(baseLine[i].x).append(",").append(baseLine[i].y).append("), ");
        }
        str.delete(str.length() - 2, str.length());
        str.append("; \n");
        str.append("highDangerLine:\n");
        for (int i = 0; i < 2; i++) {
            str.append("p").append(i + 1).append("(").append(highLine[i].x).append(",").append(highLine[i].y).append("), ");
        }
        str.delete(str.length() - 2, str.length());
        str.append("; \n");
        str.append("mediumDangerLine:\n");
        for (int i = 0; i < 2; i++) {
            str.append("p").append(i + 1).append("(").append(mediumLine[i].x).append(",").append(mediumLine[i].y).append("), ");
        }
        str.delete(str.length() - 2, str.length());
        str.append("; \n");
        str.append("lowDangerLine:\n");
        for (int i = 0; i < 2; i++) {
            str.append("p").append(i + 1).append("(").append(lowLine[i].x).append(",").append(lowLine[i].y).append("), ");
        }
        str.delete(str.length() - 2, str.length());
        return str.toString();
    }
}
