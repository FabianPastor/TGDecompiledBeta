package org.telegram.messenger.video;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.GLES20;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class OutputSurface
  implements SurfaceTexture.OnFrameAvailableListener
{
  private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
  private static final int EGL_OPENGL_ES2_BIT = 4;
  private EGL10 mEGL;
  private EGLContext mEGLContext = null;
  private EGLDisplay mEGLDisplay = null;
  private EGLSurface mEGLSurface = null;
  private boolean mFrameAvailable;
  private final Object mFrameSyncObject = new Object();
  private int mHeight;
  private ByteBuffer mPixelBuf;
  private Surface mSurface;
  private SurfaceTexture mSurfaceTexture;
  private TextureRenderer mTextureRender;
  private int mWidth;
  private int rotateRender = 0;
  
  public OutputSurface()
  {
    setup();
  }
  
  public OutputSurface(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new IllegalArgumentException();
    }
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    this.rotateRender = paramInt3;
    this.mPixelBuf = ByteBuffer.allocateDirect(this.mWidth * this.mHeight * 4);
    this.mPixelBuf.order(ByteOrder.LITTLE_ENDIAN);
    eglSetup(paramInt1, paramInt2);
    makeCurrent();
    setup();
  }
  
  private void checkEglError(String paramString)
  {
    if (this.mEGL.eglGetError() != 12288) {
      throw new RuntimeException("EGL error encountered (see log)");
    }
  }
  
  private void eglSetup(int paramInt1, int paramInt2)
  {
    this.mEGL = ((EGL10)EGLContext.getEGL());
    this.mEGLDisplay = this.mEGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
    if (this.mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
      throw new RuntimeException("unable to get EGL10 display");
    }
    if (!this.mEGL.eglInitialize(this.mEGLDisplay, null))
    {
      this.mEGLDisplay = null;
      throw new RuntimeException("unable to initialize EGL10");
    }
    EGLConfig[] arrayOfEGLConfig = new EGLConfig[1];
    int[] arrayOfInt = new int[1];
    EGL10 localEGL10 = this.mEGL;
    EGLDisplay localEGLDisplay = this.mEGLDisplay;
    int i = arrayOfEGLConfig.length;
    if (!localEGL10.eglChooseConfig(localEGLDisplay, new int[] { 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12339, 1, 12352, 4, 12344 }, arrayOfEGLConfig, i, arrayOfInt)) {
      throw new RuntimeException("unable to find RGB888+pbuffer EGL config");
    }
    this.mEGLContext = this.mEGL.eglCreateContext(this.mEGLDisplay, arrayOfEGLConfig[0], EGL10.EGL_NO_CONTEXT, new int[] { 12440, 2, 12344 });
    checkEglError("eglCreateContext");
    if (this.mEGLContext == null) {
      throw new RuntimeException("null context");
    }
    this.mEGLSurface = this.mEGL.eglCreatePbufferSurface(this.mEGLDisplay, arrayOfEGLConfig[0], new int[] { 12375, paramInt1, 12374, paramInt2, 12344 });
    checkEglError("eglCreatePbufferSurface");
    if (this.mEGLSurface == null) {
      throw new RuntimeException("surface was null");
    }
  }
  
  private void setup()
  {
    this.mTextureRender = new TextureRenderer(this.rotateRender);
    this.mTextureRender.surfaceCreated();
    this.mSurfaceTexture = new SurfaceTexture(this.mTextureRender.getTextureId());
    this.mSurfaceTexture.setOnFrameAvailableListener(this);
    this.mSurface = new Surface(this.mSurfaceTexture);
  }
  
  public void awaitNewImage()
  {
    synchronized (this.mFrameSyncObject)
    {
      for (;;)
      {
        boolean bool = this.mFrameAvailable;
        if (!bool) {
          try
          {
            this.mFrameSyncObject.wait(2500L);
            if (!this.mFrameAvailable)
            {
              localRuntimeException = new java/lang/RuntimeException;
              localRuntimeException.<init>("Surface frame wait timed out");
              throw localRuntimeException;
            }
          }
          catch (InterruptedException localInterruptedException)
          {
            RuntimeException localRuntimeException = new java/lang/RuntimeException;
            localRuntimeException.<init>(localInterruptedException);
            throw localRuntimeException;
          }
        }
      }
    }
    this.mFrameAvailable = false;
    this.mTextureRender.checkGlError("before updateTexImage");
    this.mSurfaceTexture.updateTexImage();
  }
  
  public void drawImage(boolean paramBoolean)
  {
    this.mTextureRender.drawFrame(this.mSurfaceTexture, paramBoolean);
  }
  
  public ByteBuffer getFrame()
  {
    this.mPixelBuf.rewind();
    GLES20.glReadPixels(0, 0, this.mWidth, this.mHeight, 6408, 5121, this.mPixelBuf);
    return this.mPixelBuf;
  }
  
  public Surface getSurface()
  {
    return this.mSurface;
  }
  
  public void makeCurrent()
  {
    if (this.mEGL == null) {
      throw new RuntimeException("not configured for makeCurrent");
    }
    checkEglError("before makeCurrent");
    if (!this.mEGL.eglMakeCurrent(this.mEGLDisplay, this.mEGLSurface, this.mEGLSurface, this.mEGLContext)) {
      throw new RuntimeException("eglMakeCurrent failed");
    }
  }
  
  public void onFrameAvailable(SurfaceTexture arg1)
  {
    synchronized (this.mFrameSyncObject)
    {
      if (this.mFrameAvailable)
      {
        RuntimeException localRuntimeException = new java/lang/RuntimeException;
        localRuntimeException.<init>("mFrameAvailable already set, frame could be dropped");
        throw localRuntimeException;
      }
    }
    this.mFrameAvailable = true;
    this.mFrameSyncObject.notifyAll();
  }
  
  public void release()
  {
    if (this.mEGL != null)
    {
      if (this.mEGL.eglGetCurrentContext().equals(this.mEGLContext)) {
        this.mEGL.eglMakeCurrent(this.mEGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
      }
      this.mEGL.eglDestroySurface(this.mEGLDisplay, this.mEGLSurface);
      this.mEGL.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
    }
    this.mSurface.release();
    this.mEGLDisplay = null;
    this.mEGLContext = null;
    this.mEGLSurface = null;
    this.mEGL = null;
    this.mTextureRender = null;
    this.mSurface = null;
    this.mSurfaceTexture = null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/video/OutputSurface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */