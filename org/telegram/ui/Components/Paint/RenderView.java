package org.telegram.ui.Components.Paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import java.util.concurrent.Semaphore;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.Size;

public class RenderView
  extends TextureView
{
  private Bitmap bitmap;
  private Brush brush;
  private int color;
  private RenderViewDelegate delegate;
  private Input input;
  private CanvasInternal internal;
  private int orientation;
  private Painting painting;
  private DispatchQueue queue;
  private boolean shuttingDown;
  private boolean transformedBitmap;
  private UndoStore undoStore;
  private float weight;
  
  public RenderView(Context paramContext, Painting paramPainting, Bitmap paramBitmap, int paramInt)
  {
    super(paramContext);
    this.bitmap = paramBitmap;
    this.orientation = paramInt;
    this.painting = paramPainting;
    this.painting.setRenderView(this);
    setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
    {
      public void onSurfaceTextureAvailable(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if ((paramAnonymousSurfaceTexture == null) || (RenderView.this.internal != null)) {}
        do
        {
          return;
          RenderView.access$002(RenderView.this, new RenderView.CanvasInternal(RenderView.this, paramAnonymousSurfaceTexture));
          RenderView.this.internal.setBufferSize(paramAnonymousInt1, paramAnonymousInt2);
          RenderView.this.updateTransform();
          RenderView.this.internal.requestRender();
        } while (!RenderView.this.painting.isPaused());
        RenderView.this.painting.onResume();
      }
      
      public boolean onSurfaceTextureDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
      {
        if (RenderView.this.internal == null) {}
        while (RenderView.this.shuttingDown) {
          return true;
        }
        RenderView.this.painting.onPause(new Runnable()
        {
          public void run()
          {
            RenderView.this.internal.shutdown();
            RenderView.access$002(RenderView.this, null);
          }
        });
        return true;
      }
      
      public void onSurfaceTextureSizeChanged(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (RenderView.this.internal == null) {
          return;
        }
        RenderView.this.internal.setBufferSize(paramAnonymousInt1, paramAnonymousInt2);
        RenderView.this.updateTransform();
        RenderView.this.internal.onSizeChanged();
      }
      
      public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture) {}
    });
    this.input = new Input(this);
    this.painting.setDelegate(new Painting.PaintingDelegate()
    {
      public void contentChanged(RectF paramAnonymousRectF)
      {
        if (RenderView.this.internal != null) {
          RenderView.this.internal.scheduleRedraw();
        }
      }
      
      public DispatchQueue requestDispatchQueue()
      {
        return RenderView.this.queue;
      }
      
      public UndoStore requestUndoStore()
      {
        return RenderView.this.undoStore;
      }
      
      public void strokeCommited() {}
    });
  }
  
  private float brushWeightForSize(float paramFloat)
  {
    float f = this.painting.getSize().width;
    return 0.00390625F * f + 0.043945312F * f * paramFloat;
  }
  
  private void updateTransform()
  {
    Object localObject = new Matrix();
    int i = getWidth();
    if (this.painting != null) {}
    for (float f1 = i / this.painting.getSize().width;; f1 = 1.0F)
    {
      float f2 = f1;
      if (f1 <= 0.0F) {
        f2 = 1.0F;
      }
      Size localSize = getPainting().getSize();
      ((Matrix)localObject).preTranslate(getWidth() / 2.0F, getHeight() / 2.0F);
      ((Matrix)localObject).preScale(f2, -f2);
      ((Matrix)localObject).preTranslate(-localSize.width / 2.0F, -localSize.height / 2.0F);
      this.input.setMatrix((Matrix)localObject);
      localObject = GLMatrix.MultiplyMat4f(GLMatrix.LoadOrtho(0.0F, this.internal.bufferWidth, 0.0F, this.internal.bufferHeight, -1.0F, 1.0F), GLMatrix.LoadGraphicsMatrix((Matrix)localObject));
      this.painting.setRenderProjection((float[])localObject);
      return;
    }
  }
  
  public Brush getCurrentBrush()
  {
    return this.brush;
  }
  
  public int getCurrentColor()
  {
    return this.color;
  }
  
  public float getCurrentWeight()
  {
    return this.weight;
  }
  
  public Painting getPainting()
  {
    return this.painting;
  }
  
  public Bitmap getResultBitmap()
  {
    if (this.internal != null) {
      return this.internal.getTexture();
    }
    return null;
  }
  
  public void onBeganDrawing()
  {
    if (this.delegate != null) {
      this.delegate.onBeganDrawing();
    }
  }
  
  public void onFinishedDrawing(boolean paramBoolean)
  {
    if (this.delegate != null) {
      this.delegate.onFinishedDrawing(paramBoolean);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    Log.d("a", "b");
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = true;
    boolean bool1;
    if (paramMotionEvent.getPointerCount() > 1) {
      bool1 = false;
    }
    do
    {
      do
      {
        do
        {
          return bool1;
          bool1 = bool2;
        } while (this.internal == null);
        bool1 = bool2;
      } while (!this.internal.initialized);
      bool1 = bool2;
    } while (!this.internal.ready);
    this.input.process(paramMotionEvent);
    return true;
  }
  
  public void performInContext(final Runnable paramRunnable)
  {
    if (this.internal == null) {
      return;
    }
    this.internal.postRunnable(new Runnable()
    {
      public void run()
      {
        if ((RenderView.this.internal == null) || (!RenderView.CanvasInternal.access$600(RenderView.this.internal))) {
          return;
        }
        RenderView.CanvasInternal.access$1300(RenderView.this.internal);
        paramRunnable.run();
      }
    });
  }
  
  public void setBrush(Brush paramBrush)
  {
    Painting localPainting = this.painting;
    this.brush = paramBrush;
    localPainting.setBrush(paramBrush);
  }
  
  public void setBrushSize(float paramFloat)
  {
    this.weight = brushWeightForSize(paramFloat);
  }
  
  public void setColor(int paramInt)
  {
    this.color = paramInt;
  }
  
  public void setDelegate(RenderViewDelegate paramRenderViewDelegate)
  {
    this.delegate = paramRenderViewDelegate;
  }
  
  public void setQueue(DispatchQueue paramDispatchQueue)
  {
    this.queue = paramDispatchQueue;
  }
  
  public void setUndoStore(UndoStore paramUndoStore)
  {
    this.undoStore = paramUndoStore;
  }
  
  public boolean shouldDraw()
  {
    return (this.delegate == null) || (this.delegate.shouldDraw());
  }
  
  public void shutdown()
  {
    this.shuttingDown = true;
    if (this.internal != null) {
      performInContext(new Runnable()
      {
        public void run()
        {
          RenderView.this.painting.cleanResources(RenderView.this.transformedBitmap);
          RenderView.this.internal.shutdown();
          RenderView.access$002(RenderView.this, null);
        }
      });
    }
    setVisibility(8);
  }
  
  private class CanvasInternal
    extends DispatchQueue
  {
    private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private final int EGL_OPENGL_ES2_BIT = 4;
    private int bufferHeight;
    private int bufferWidth;
    private Runnable drawRunnable = new Runnable()
    {
      public void run()
      {
        if ((!RenderView.CanvasInternal.this.initialized) || (RenderView.this.shuttingDown)) {}
        do
        {
          return;
          RenderView.CanvasInternal.this.setCurrentContext();
          GLES20.glBindFramebuffer(36160, 0);
          GLES20.glViewport(0, 0, RenderView.CanvasInternal.this.bufferWidth, RenderView.CanvasInternal.this.bufferHeight);
          GLES20.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
          GLES20.glClear(16384);
          RenderView.this.painting.render();
          GLES20.glBlendFunc(1, 771);
          RenderView.CanvasInternal.this.egl10.eglSwapBuffers(RenderView.CanvasInternal.this.eglDisplay, RenderView.CanvasInternal.this.eglSurface);
        } while (RenderView.CanvasInternal.this.ready);
        RenderView.this.queue.postRunnable(new Runnable()
        {
          public void run()
          {
            RenderView.CanvasInternal.access$702(RenderView.CanvasInternal.this, true);
          }
        }, 200L);
      }
    };
    private EGL10 egl10;
    private EGLConfig eglConfig;
    private EGLContext eglContext;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface;
    private boolean initialized;
    private long lastRenderCallTime;
    private boolean ready;
    private Runnable scheduledRunnable;
    private SurfaceTexture surfaceTexture;
    
    public CanvasInternal(SurfaceTexture paramSurfaceTexture)
    {
      super();
      this.surfaceTexture = paramSurfaceTexture;
    }
    
    private void checkBitmap()
    {
      Size localSize = RenderView.this.painting.getSize();
      if ((RenderView.this.bitmap.getWidth() != localSize.width) || (RenderView.this.bitmap.getHeight() != localSize.height) || (RenderView.this.orientation != 0))
      {
        float f = RenderView.this.bitmap.getWidth();
        if ((RenderView.this.orientation % 360 == 90) || (RenderView.this.orientation % 360 == 270)) {
          f = RenderView.this.bitmap.getHeight();
        }
        f = localSize.width / f;
        RenderView.access$1102(RenderView.this, createBitmap(RenderView.this.bitmap, f));
        RenderView.access$1202(RenderView.this, 0);
        RenderView.access$1002(RenderView.this, true);
      }
    }
    
    private Bitmap createBitmap(Bitmap paramBitmap, float paramFloat)
    {
      Matrix localMatrix = new Matrix();
      localMatrix.setScale(paramFloat, paramFloat);
      localMatrix.postRotate(RenderView.this.orientation);
      return Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
    }
    
    private boolean initGL()
    {
      this.egl10 = ((EGL10)EGLContext.getEGL());
      this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
      if (this.eglDisplay == EGL10.EGL_NO_DISPLAY)
      {
        Log.e("tmessages", "eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
        finish();
        return false;
      }
      int[] arrayOfInt = new int[2];
      if (!this.egl10.eglInitialize(this.eglDisplay, arrayOfInt))
      {
        Log.e("tmessages", "eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
        finish();
        return false;
      }
      arrayOfInt = new int[1];
      EGLConfig[] arrayOfEGLConfig = new EGLConfig[1];
      if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[] { 12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344 }, arrayOfEGLConfig, 1, arrayOfInt))
      {
        Log.e("tmessages", "eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
        finish();
        return false;
      }
      if (arrayOfInt[0] > 0)
      {
        this.eglConfig = arrayOfEGLConfig[0];
        this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[] { 12440, 2, 12344 });
        if (this.eglContext == null)
        {
          Log.e("tmessages", "eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
          finish();
          return false;
        }
      }
      else
      {
        Log.e("tmessages", "eglConfig not initialized");
        finish();
        return false;
      }
      if ((this.surfaceTexture instanceof SurfaceTexture))
      {
        this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
        if ((this.eglSurface == null) || (this.eglSurface == EGL10.EGL_NO_SURFACE))
        {
          Log.e("tmessages", "createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
          finish();
          return false;
        }
      }
      else
      {
        finish();
        return false;
      }
      if (!this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext))
      {
        Log.e("tmessages", "eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
        finish();
        return false;
      }
      GLES20.glEnable(3042);
      GLES20.glDisable(3024);
      GLES20.glDisable(2960);
      GLES20.glDisable(2929);
      RenderView.this.painting.setupShaders();
      checkBitmap();
      RenderView.this.painting.setBitmap(RenderView.this.bitmap);
      Utils.HasGLError();
      return true;
    }
    
    private boolean setCurrentContext()
    {
      if (!this.initialized) {}
      while (((!this.eglContext.equals(this.egl10.eglGetCurrentContext())) || (!this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377)))) && (!this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext))) {
        return false;
      }
      return true;
    }
    
    public void finish()
    {
      if (this.eglSurface != null)
      {
        this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
        this.eglSurface = null;
      }
      if (this.eglContext != null)
      {
        this.egl10.eglDestroyContext(this.eglDisplay, this.eglContext);
        this.eglContext = null;
      }
      if (this.eglDisplay != null)
      {
        this.egl10.eglTerminate(this.eglDisplay);
        this.eglDisplay = null;
      }
    }
    
    public Bitmap getTexture()
    {
      if (!this.initialized) {
        return null;
      }
      final Semaphore localSemaphore = new Semaphore(0);
      final Bitmap[] arrayOfBitmap = new Bitmap[1];
      try
      {
        postRunnable(new Runnable()
        {
          public void run()
          {
            Painting.PaintingData localPaintingData = RenderView.this.painting.getPaintingData(new RectF(0.0F, 0.0F, RenderView.this.painting.getSize().width, RenderView.this.painting.getSize().height), false);
            arrayOfBitmap[0] = localPaintingData.bitmap;
            localSemaphore.release();
          }
        });
        localSemaphore.acquire();
        return arrayOfBitmap[0];
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
    
    public void onSizeChanged()
    {
      postRunnable(new Runnable()
      {
        public void run()
        {
          long l = System.currentTimeMillis();
          if (Math.abs(RenderView.CanvasInternal.this.lastRenderCallTime - l) > 30L)
          {
            RenderView.CanvasInternal.access$1702(RenderView.CanvasInternal.this, l);
            RenderView.CanvasInternal.this.drawRunnable.run();
            RenderView.CanvasInternal.this.drawRunnable.run();
          }
        }
      });
    }
    
    public void requestRender()
    {
      postRunnable(new Runnable()
      {
        public void run()
        {
          long l = System.currentTimeMillis();
          if (Math.abs(RenderView.CanvasInternal.this.lastRenderCallTime - l) > 30L)
          {
            RenderView.CanvasInternal.access$1702(RenderView.CanvasInternal.this, l);
            RenderView.CanvasInternal.this.drawRunnable.run();
          }
        }
      });
    }
    
    public void run()
    {
      if ((RenderView.this.bitmap == null) || (RenderView.this.bitmap.isRecycled())) {
        return;
      }
      this.initialized = initGL();
      super.run();
    }
    
    public void scheduleRedraw()
    {
      if (this.scheduledRunnable != null)
      {
        cancelRunnable(this.scheduledRunnable);
        this.scheduledRunnable = null;
      }
      this.scheduledRunnable = new Runnable()
      {
        public void run()
        {
          RenderView.CanvasInternal.access$1902(RenderView.CanvasInternal.this, null);
          RenderView.CanvasInternal.this.drawRunnable.run();
        }
      };
      postRunnable(this.scheduledRunnable, 1L);
    }
    
    public void setBufferSize(int paramInt1, int paramInt2)
    {
      this.bufferWidth = paramInt1;
      this.bufferHeight = paramInt2;
    }
    
    public void shutdown()
    {
      postRunnable(new Runnable()
      {
        public void run()
        {
          RenderView.CanvasInternal.this.finish();
          Looper localLooper = Looper.myLooper();
          if (localLooper != null) {
            localLooper.quit();
          }
        }
      });
    }
  }
  
  public static abstract interface RenderViewDelegate
  {
    public abstract void onBeganDrawing();
    
    public abstract void onFinishedDrawing(boolean paramBoolean);
    
    public abstract boolean shouldDraw();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/RenderView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */