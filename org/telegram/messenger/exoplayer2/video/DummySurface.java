package org.telegram.messenger.exoplayer2.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(17)
public final class DummySurface
  extends Surface
{
  private static final int EGL_PROTECTED_CONTENT_EXT = 12992;
  private static final String EXTENSION_PROTECTED_CONTENT = "EGL_EXT_protected_content";
  private static final String EXTENSION_SURFACELESS_CONTEXT = "EGL_KHR_surfaceless_context";
  private static final int SECURE_MODE_NONE = 0;
  private static final int SECURE_MODE_PROTECTED_PBUFFER = 2;
  private static final int SECURE_MODE_SURFACELESS_CONTEXT = 1;
  private static final String TAG = "DummySurface";
  private static int secureMode;
  private static boolean secureModeInitialized;
  public final boolean secure;
  private final DummySurfaceThread thread;
  private boolean threadReleased;
  
  private DummySurface(DummySurfaceThread paramDummySurfaceThread, SurfaceTexture paramSurfaceTexture, boolean paramBoolean)
  {
    super(paramSurfaceTexture);
    this.thread = paramDummySurfaceThread;
    this.secure = paramBoolean;
  }
  
  private static void assertApiLevel17OrHigher()
  {
    if (Util.SDK_INT < 17) {
      throw new UnsupportedOperationException("Unsupported prior to API level 17");
    }
  }
  
  @TargetApi(24)
  private static int getSecureModeV24(Context paramContext)
  {
    int i = 0;
    int j;
    if (Util.SDK_INT < 26)
    {
      j = i;
      if (!"samsung".equals(Util.MANUFACTURER))
      {
        if (!"XT1650".equals(Util.MODEL)) {
          break label38;
        }
        j = i;
      }
    }
    for (;;)
    {
      return j;
      label38:
      if (Util.SDK_INT < 26)
      {
        j = i;
        if (!paramContext.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance")) {}
      }
      else
      {
        paramContext = EGL14.eglQueryString(EGL14.eglGetDisplay(0), 12373);
        j = i;
        if (paramContext != null)
        {
          j = i;
          if (paramContext.contains("EGL_EXT_protected_content")) {
            if (paramContext.contains("EGL_KHR_surfaceless_context")) {
              j = 1;
            } else {
              j = 2;
            }
          }
        }
      }
    }
  }
  
  /* Error */
  public static boolean isSecureSupported(Context paramContext)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_1
    //   2: ldc 2
    //   4: monitorenter
    //   5: getstatic 120	org/telegram/messenger/exoplayer2/video/DummySurface:secureModeInitialized	Z
    //   8: ifne +21 -> 29
    //   11: getstatic 61	org/telegram/messenger/exoplayer2/util/Util:SDK_INT	I
    //   14: bipush 24
    //   16: if_icmpge +26 -> 42
    //   19: iconst_0
    //   20: istore_2
    //   21: iload_2
    //   22: putstatic 122	org/telegram/messenger/exoplayer2/video/DummySurface:secureMode	I
    //   25: iconst_1
    //   26: putstatic 120	org/telegram/messenger/exoplayer2/video/DummySurface:secureModeInitialized	Z
    //   29: getstatic 122	org/telegram/messenger/exoplayer2/video/DummySurface:secureMode	I
    //   32: istore_2
    //   33: iload_2
    //   34: ifeq +16 -> 50
    //   37: ldc 2
    //   39: monitorexit
    //   40: iload_1
    //   41: ireturn
    //   42: aload_0
    //   43: invokestatic 124	org/telegram/messenger/exoplayer2/video/DummySurface:getSecureModeV24	(Landroid/content/Context;)I
    //   46: istore_2
    //   47: goto -26 -> 21
    //   50: iconst_0
    //   51: istore_1
    //   52: goto -15 -> 37
    //   55: astore_0
    //   56: ldc 2
    //   58: monitorexit
    //   59: aload_0
    //   60: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	61	0	paramContext	Context
    //   1	51	1	bool	boolean
    //   20	27	2	i	int
    // Exception table:
    //   from	to	target	type
    //   5	19	55	finally
    //   21	29	55	finally
    //   29	33	55	finally
    //   42	47	55	finally
  }
  
  public static DummySurface newInstanceV17(Context paramContext, boolean paramBoolean)
  {
    int i = 0;
    assertApiLevel17OrHigher();
    if ((!paramBoolean) || (isSecureSupported(paramContext))) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      paramContext = new DummySurfaceThread();
      if (paramBoolean) {
        i = secureMode;
      }
      return paramContext.init(i);
    }
  }
  
  public void release()
  {
    super.release();
    synchronized (this.thread)
    {
      if (!this.threadReleased)
      {
        this.thread.release();
        this.threadReleased = true;
      }
      return;
    }
  }
  
  private static class DummySurfaceThread
    extends HandlerThread
    implements SurfaceTexture.OnFrameAvailableListener, Handler.Callback
  {
    private static final int MSG_INIT = 1;
    private static final int MSG_RELEASE = 3;
    private static final int MSG_UPDATE_TEXTURE = 2;
    private EGLContext context;
    private EGLDisplay display;
    private Handler handler;
    private Error initError;
    private RuntimeException initException;
    private EGLSurface pbuffer;
    private DummySurface surface;
    private SurfaceTexture surfaceTexture;
    private final int[] textureIdHolder = new int[1];
    
    public DummySurfaceThread()
    {
      super();
    }
    
    private void initInternal(int paramInt)
    {
      this.display = EGL14.eglGetDisplay(0);
      Object localObject;
      int[] arrayOfInt;
      if (this.display != null)
      {
        bool = true;
        Assertions.checkState(bool, "eglGetDisplay failed");
        localObject = new int[2];
        Assertions.checkState(EGL14.eglInitialize(this.display, (int[])localObject, 0, (int[])localObject, 1), "eglInitialize failed");
        localObject = new EGLConfig[1];
        arrayOfInt = new int[1];
        if ((!EGL14.eglChooseConfig(this.display, new int[] { 12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12327, 12344, 12339, 4, 12344 }, 0, (EGLConfig[])localObject, 0, 1, arrayOfInt, 0)) || (arrayOfInt[0] <= 0) || (localObject[0] == null)) {
          break label355;
        }
        bool = true;
        label192:
        Assertions.checkState(bool, "eglChooseConfig failed");
        arrayOfInt = localObject[0];
        if (paramInt != 0) {
          break label360;
        }
        localObject = new int[3];
        Object tmp212_211 = localObject;
        tmp212_211[0] = '゘';
        Object tmp218_212 = tmp212_211;
        tmp218_212[1] = 2;
        Object tmp222_218 = tmp218_212;
        tmp222_218[2] = '〸';
        tmp222_218;
        label229:
        this.context = EGL14.eglCreateContext(this.display, arrayOfInt, EGL14.EGL_NO_CONTEXT, (int[])localObject, 0);
        if (this.context == null) {
          break label395;
        }
        bool = true;
        label256:
        Assertions.checkState(bool, "eglCreateContext failed");
        if (paramInt != 1) {
          break label400;
        }
        localObject = EGL14.EGL_NO_SURFACE;
        Assertions.checkState(EGL14.eglMakeCurrent(this.display, (EGLSurface)localObject, (EGLSurface)localObject, this.context), "eglMakeCurrent failed");
        GLES20.glGenTextures(1, this.textureIdHolder, 0);
        this.surfaceTexture = new SurfaceTexture(this.textureIdHolder[0]);
        this.surfaceTexture.setOnFrameAvailableListener(this);
        localObject = this.surfaceTexture;
        if (paramInt == 0) {
          break label527;
        }
      }
      label355:
      label360:
      label395:
      label400:
      label449:
      label522:
      label527:
      for (boolean bool = true;; bool = false)
      {
        this.surface = new DummySurface(this, (SurfaceTexture)localObject, bool, null);
        return;
        bool = false;
        break;
        bool = false;
        break label192;
        localObject = new int[5];
        tmp365_364 = localObject;
        tmp365_364[0] = '゘';
        tmp371_365 = tmp365_364;
        tmp371_365[1] = 2;
        tmp375_371 = tmp371_365;
        tmp375_371[2] = '㋀';
        tmp381_375 = tmp375_371;
        tmp381_375[3] = 1;
        tmp385_381 = tmp381_375;
        tmp385_381[4] = '〸';
        tmp385_381;
        break label229;
        bool = false;
        break label256;
        if (paramInt == 2)
        {
          localObject = new int[7];
          tmp411_410 = localObject;
          tmp411_410[0] = 'し';
          tmp417_411 = tmp411_410;
          tmp417_411[1] = 1;
          tmp421_417 = tmp417_411;
          tmp421_417[2] = 'ざ';
          tmp427_421 = tmp421_417;
          tmp427_421[3] = 1;
          tmp431_427 = tmp427_421;
          tmp431_427[4] = '㋀';
          tmp437_431 = tmp431_427;
          tmp437_431[5] = 1;
          tmp441_437 = tmp437_431;
          tmp441_437[6] = '〸';
          tmp441_437;
          this.pbuffer = EGL14.eglCreatePbufferSurface(this.display, arrayOfInt, (int[])localObject, 0);
          if (this.pbuffer == null) {
            break label522;
          }
        }
        for (bool = true;; bool = false)
        {
          Assertions.checkState(bool, "eglCreatePbufferSurface failed");
          localObject = this.pbuffer;
          break;
          localObject = new int[5];
          tmp492_491 = localObject;
          tmp492_491[0] = 'し';
          tmp498_492 = tmp492_491;
          tmp498_492[1] = 1;
          tmp502_498 = tmp498_492;
          tmp502_498[2] = 'ざ';
          tmp508_502 = tmp502_498;
          tmp508_502[3] = 1;
          tmp512_508 = tmp508_502;
          tmp512_508[4] = '〸';
          tmp512_508;
          break label449;
        }
      }
    }
    
    private void releaseInternal()
    {
      try
      {
        if (this.surfaceTexture != null)
        {
          this.surfaceTexture.release();
          GLES20.glDeleteTextures(1, this.textureIdHolder, 0);
        }
        return;
      }
      finally
      {
        if (this.pbuffer != null) {
          EGL14.eglDestroySurface(this.display, this.pbuffer);
        }
        if (this.context != null) {
          EGL14.eglDestroyContext(this.display, this.context);
        }
        this.pbuffer = null;
        this.context = null;
        this.display = null;
        this.surface = null;
        this.surfaceTexture = null;
      }
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
      case 1: 
        for (;;)
        {
          return true;
          try
          {
            initInternal(paramMessage.arg1);
            try
            {
              notify();
            }
            finally {}
          }
          catch (RuntimeException paramMessage)
          {
            Log.e("DummySurface", "Failed to initialize dummy surface", paramMessage);
            this.initException = paramMessage;
            try
            {
              notify();
            }
            finally {}
          }
          catch (Error paramMessage)
          {
            Log.e("DummySurface", "Failed to initialize dummy surface", paramMessage);
            this.initError = paramMessage;
            try
            {
              notify();
            }
            finally {}
          }
          finally {}
        }
      }
      try
      {
        notify();
        throw paramMessage;
      }
      finally
      {
        throw paramMessage;
        this.surfaceTexture.updateTexImage();
      }
    }
    
    public DummySurface init(int paramInt)
    {
      start();
      this.handler = new Handler(getLooper(), this);
      int i = 0;
      try
      {
        this.handler.obtainMessage(1, paramInt, 0).sendToTarget();
        paramInt = i;
        while ((this.surface == null) && (this.initException == null))
        {
          Error localError = this.initError;
          if (localError != null) {
            break;
          }
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            paramInt = 1;
          }
        }
        if (paramInt != 0) {
          Thread.currentThread().interrupt();
        }
        if (this.initException != null) {
          throw this.initException;
        }
      }
      finally {}
      if (this.initError != null) {
        throw this.initError;
      }
      return this.surface;
    }
    
    public void onFrameAvailable(SurfaceTexture paramSurfaceTexture)
    {
      this.handler.sendEmptyMessage(2);
    }
    
    public void release()
    {
      this.handler.sendEmptyMessage(3);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface SecureMode {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/video/DummySurface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */