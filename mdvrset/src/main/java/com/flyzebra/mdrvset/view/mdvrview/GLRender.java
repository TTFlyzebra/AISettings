/**
 * FileName: GlRender
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2022/12/2 19:08
 * Description:
 */
package com.flyzebra.mdrvset.view.mdvrview;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.GlShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRender implements GLSurfaceView.Renderer {
    private Context mContext;
    private int[] textureid_yuv = new int[3];
    private int width = 1280;
    private int height = 720;
    private int yuv_w = 0;
    private int yuv_h = 0;
    private byte[] yByte = new byte[width * height];
    private byte[] uByte = new byte[width * height / 4];
    private byte[] vByte = new byte[width * height / 4];
    private ByteBuffer y;
    private ByteBuffer u;
    private ByteBuffer v;
    private final Object objectLock = new Object();

    //顶点坐标
    private float[] vertexData = {   // in counterclockwise order:
            -1f, -1f, // bottom left
            +1f, -1f, // bottom right+
            -1f, +1f, // top left
            +1f, +1f, // top right
    };
    //纹理坐标
    private float[] textureData = {   // in counterclockwise order:
            0f, 1f,  // bottom left
            1f, 1f,  // bottom right
            0f, 0f,  // top left
            1f, 0f,  // top right
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int glprogram;
    private int vPosition;
    private int fPosition;
    private int sampler_y;
    private int sampler_u;
    private int sampler_v;
    private int vMatrix;
    private float[] vMatrixBaseData = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    public GLRender(Context context) {
        mContext = context;
        y = ByteBuffer.wrap(yByte);
        u = ByteBuffer.wrap(uByte);
        v = ByteBuffer.wrap(vByte);
    }

    public void upYuvData(byte[] yuv, int offset, int w, int h, int size) {
        if (w * h > width * height) {
            FlyLog.e("yuv size error.");
            return;
        }
        synchronized (objectLock) {
            y.clear();
            u.clear();
            v.clear();
            y.put(yuv, offset, w * h);
            u.put(yuv, offset + w * h, w * h / 4);
            v.put(yuv, offset + w * h * 5 / 4, w * h / 4);
            y.flip();
            u.flip();
            v.flip();
            yuv_w = w;
            yuv_h = h;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //创建一个渲染程序
        glprogram = GlShaderUtil.createProgram(
                GlShaderUtil.readRawTextFile(mContext, R.raw.glsl_yuv_vertex),
                GlShaderUtil.readRawTextFile(mContext, R.raw.glsl_yuv_fragment));

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);

        //得到着色器中的属性
        vPosition = GLES30.glGetAttribLocation(glprogram, "vPosition");
        fPosition = GLES30.glGetAttribLocation(glprogram, "fPosition");
        sampler_y = GLES30.glGetUniformLocation(glprogram, "sampler_y");
        sampler_u = GLES30.glGetUniformLocation(glprogram, "sampler_u");
        sampler_v = GLES30.glGetUniformLocation(glprogram, "sampler_v");
        vMatrix = GLES30.glGetUniformLocation(glprogram, "vMatrix");

        //创建纹理
        GLES30.glGenTextures(3, textureid_yuv, 0);
        for (int i = 0; i < 3; i++) {
            //绑定纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureid_yuv[i]);
            //设置环绕和过滤方式
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glClearColor(0f, 0f, 0f, 1f);
        synchronized (objectLock) {
            if(yuv_w==0 || yuv_h==0) return;
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureid_yuv[0]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE, yuv_w, yuv_h, 0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, y);//
            GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureid_yuv[1]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE, yuv_w / 2, yuv_h / 2, 0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, u);
            GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureid_yuv[2]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE, yuv_w / 2, yuv_h / 2, 0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, v);
        }
        GLES30.glUseProgram(glprogram);
        GLES30.glUniform1i(sampler_y, 0);
        GLES30.glUniform1i(sampler_u, 1);
        GLES30.glUniform1i(sampler_v, 2);
        GLES30.glUniformMatrix4fv(vMatrix, 1, false, vMatrixBaseData, 0);
        GLES30.glEnableVertexAttribArray(vPosition);
        GLES30.glVertexAttribPointer(vPosition, 2, GLES30.GL_FLOAT, false, 0, vertexBuffer);//为顶点属性赋值
        GLES30.glEnableVertexAttribArray(fPosition);
        GLES30.glVertexAttribPointer(fPosition, 2, GLES30.GL_FLOAT, false, 0, textureBuffer);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glDisableVertexAttribArray(vPosition);
        GLES30.glDisableVertexAttribArray(fPosition);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    public void showDisconnect() {
        synchronized (objectLock) {
            u.position(0);
            v.position(0);
            Arrays.fill(uByte, (byte) 0x7F);
            Arrays.fill(vByte, (byte) 0x7F);
            ((ByteBuffer) u).put(uByte, 0, yuv_w * yuv_h / 4);
            ((ByteBuffer) v).put(vByte, 0, yuv_w * yuv_h / 4);
            u.flip();
            v.flip();
        }
    }
}
