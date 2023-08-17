package com.flyzebra.ffplay;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.flyzebra.utils.FlyLog;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Author: FlyZebra
 * Time: 18-5-14 下午9:00.
 * Discription: This is GlRender
 */
public class GlRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private Context mContext;
    private FloatBuffer vertexBuffer1;
    private FloatBuffer vertexBuffer2;
    private FloatBuffer textureBuffer;
    //顶点坐标1
    private static float vertexData1[] = {   // in counterclockwise order:
            -1f, -1f, // bottom left
            +1f, -1f, // bottom right
            -1f, +1f, // top left
            +1f, +1f, // top right
    };
    //顶点坐标2
    private static float vertexData2[] = {   // in counterclockwise order:
            +1f, -1f, // top left
            +1f, +1f, // bottom left
            -1f, -1f, // top right
            -1f, +1f, // bottom right
    };
    //纹理坐标1
    private static float textureData[] = {   // in counterclockwise order:
            0f, 1f, // top left
            1f, 1f, // top right
            0f, 0f, // bottom left
            1f, 0f, // bottom right
    };
    private int glprogram;
    private int vPosition;
    private int fPosition;
    private int sampler_y;
    private int sampler_u;
    private int sampler_v;
    private int[] textureIds;
    int width;
    int height;
    ByteBuffer y;
    ByteBuffer u;
    ByteBuffer v;
    private final Object dataLock = new Object();

    public GlRender(Context context) {
        mContext = context;
        vertexBuffer1 = ByteBuffer.allocateDirect(vertexData1.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData1);
        vertexBuffer1.position(0);
        vertexBuffer2 = ByteBuffer.allocateDirect(vertexData2.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData2);
        vertexBuffer2.position(0);
        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        y = ByteBuffer.wrap(new byte[this.width * this.height]);
        u = ByteBuffer.wrap(new byte[this.width * this.height / 4]);
        v = ByteBuffer.wrap(new byte[this.width * this.height / 4]);
    }

    public void upFrame(byte[] yuv, int w, int h, int size) {
        if (w != width || h != height) {
            FlyLog.e("yuv size error.");
            return;
        }
        synchronized (dataLock) {
            y.position(0);
            u.position(0);
            v.position(0);
            y.put(yuv, 0, w * h);
            u.put(yuv, w * h, w * h / 4);
            v.put(yuv, w * h * 5 / 4, w * h / 4);
            y.flip();
            u.flip();
            v.flip();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        FlyLog.d("onSurfaceCreated");
        GLES20.glClearColor(0f, 0f, 0f, 1f);

        //创建一个渲染程序
        String vertexShader = GlShaderUtils.readRawTextFile(mContext, R.raw.glsl_yuv_vertex);
        String fragmentShader = GlShaderUtils.readRawTextFile(mContext, R.raw.glsl_yuv_fragment);
        glprogram = GlShaderUtils.createProgram(vertexShader, fragmentShader);

        //得到着色器中的属性
        vPosition = GLES20.glGetAttribLocation(glprogram, "av_Position");
        fPosition = GLES20.glGetAttribLocation(glprogram, "af_Position");
        sampler_y = GLES20.glGetUniformLocation(glprogram, "sampler_y");
        sampler_u = GLES20.glGetUniformLocation(glprogram, "sampler_u");
        sampler_v = GLES20.glGetUniformLocation(glprogram, "sampler_v");

        //创建纹理
        textureIds = new int[3];
        GLES20.glGenTextures(3, textureIds, 0);
        for (int i = 0; i < 3; i++) {
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[i]);
            //设置环绕和过滤方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        FlyLog.d("onSurfaceChanged, width:" + width + ",height :" + height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        if (width > 0 && height > 0 && y != null && u != null && v != null) {
            synchronized (dataLock) {
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0]);
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE, width, height, 0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, y);//
                GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[1]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width / 2, height / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, u);
                GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[2]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width / 2, height / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, v);
            }
        }

        GLES30.glUseProgram(glprogram);
        GLES30.glUniform1i(sampler_y, 0);
        GLES30.glUniform1i(sampler_u, 1);
        GLES30.glUniform1i(sampler_v, 2);

        GLES30.glEnableVertexAttribArray(vPosition);
        GLES30.glEnableVertexAttribArray(fPosition);
        GLES30.glVertexAttribPointer(vPosition, 2, GLES30.GL_FLOAT, false, 0, (width > height) ? vertexBuffer1 : vertexBuffer2);
        GLES30.glVertexAttribPointer(fPosition, 2, GLES30.GL_FLOAT, false, 0, textureBuffer);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glDisableVertexAttribArray(vPosition);
        GLES30.glDisableVertexAttribArray(fPosition);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        FlyLog.d("updateSurface");
    }

}
