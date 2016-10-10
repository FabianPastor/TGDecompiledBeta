package org.telegram.messenger.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.view.Display;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.AndroidUtilities;

@SuppressLint({"NewApi"})
public class CameraView
  extends FrameLayout
  implements TextureView.SurfaceTextureListener
{
  private CameraSession cameraSession;
  private int clipLeft;
  private int clipTop;
  private CameraViewDelegate delegate;
  private boolean initied;
  private boolean isFrontface;
  private boolean mirror;
  private Size previewSize;
  private TextureView textureView;
  private Matrix txform = new Matrix();
  
  public CameraView(Context paramContext)
  {
    super(paramContext, null);
    this.textureView = new TextureView(paramContext);
    this.textureView.setSurfaceTextureListener(this);
    addView(this.textureView);
  }
  
  private void adjustAspectRatio(int paramInt1, int paramInt2, int paramInt3)
  {
    this.txform.reset();
    int i = getWidth();
    int j = getHeight();
    float f2 = i / 2;
    float f3 = j / 2;
    float f1;
    if ((paramInt3 == 0) || (paramInt3 == 2))
    {
      f1 = Math.max((this.clipTop + j) / paramInt1, (this.clipLeft + i) / paramInt2);
      float f5 = paramInt1;
      float f4 = paramInt2 * f1 / i;
      f1 = f5 * f1 / j;
      this.txform.postScale(f4, f1, f2, f3);
      if ((1 != paramInt3) && (3 != paramInt3)) {
        break label242;
      }
      this.txform.postRotate((paramInt3 - 2) * 90, f2, f3);
    }
    for (;;)
    {
      if (this.mirror) {
        this.txform.postScale(-1.0F, 1.0F, f2, f3);
      }
      if ((this.clipTop != 0) || (this.clipLeft != 0)) {
        this.txform.postTranslate(-this.clipLeft / 2, -this.clipTop / 2);
      }
      this.textureView.setTransform(this.txform);
      return;
      f1 = Math.max((this.clipTop + j) / paramInt2, (this.clipLeft + i) / paramInt1);
      break;
      label242:
      if (2 == paramInt3) {
        this.txform.postRotate(180.0F, f2, f3);
      }
    }
  }
  
  private void checkPreviewMatrix()
  {
    if (this.previewSize == null) {
      return;
    }
    adjustAspectRatio(this.previewSize.getWidth(), this.previewSize.getHeight(), ((Activity)getContext()).getWindowManager().getDefaultDisplay().getRotation());
  }
  
  private void initCamera(boolean paramBoolean)
  {
    Size localSize = null;
    ArrayList localArrayList = CameraController.getInstance().getCameras();
    int i = 0;
    Object localObject;
    for (;;)
    {
      localObject = localSize;
      if (i < localArrayList.size())
      {
        localObject = (CameraInfo)localArrayList.get(i);
        if (((!this.isFrontface) || (((CameraInfo)localObject).frontCamera == 0)) && ((this.isFrontface) || (((CameraInfo)localObject).frontCamera != 0))) {}
      }
      else
      {
        if (localObject != null) {
          break;
        }
        return;
      }
      i += 1;
    }
    if (Math.abs(Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - 1.3333334F) < 0.1F) {}
    for (localSize = new Size(4, 3);; localSize = new Size(16, 9))
    {
      if ((this.textureView.getWidth() > 0) && (this.textureView.getHeight() > 0))
      {
        i = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        int j = localSize.getHeight() * i / localSize.getWidth();
        this.previewSize = CameraController.chooseOptimalSize(((CameraInfo)localObject).getPreviewSizes(), i, j, localSize);
      }
      localSize = CameraController.chooseOptimalSize(((CameraInfo)localObject).getPictureSizes(), 1280, 1280, localSize);
      if ((this.previewSize == null) || (this.textureView.getSurfaceTexture() == null)) {
        break;
      }
      this.textureView.getSurfaceTexture().setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
      this.cameraSession = new CameraSession((CameraInfo)localObject, this.previewSize, localSize, 256);
      CameraController.getInstance().open(this.cameraSession, this.textureView.getSurfaceTexture(), new Runnable()
      {
        public void run()
        {
          if (CameraView.this.cameraSession != null) {
            CameraView.this.cameraSession.setInitied();
          }
          CameraView.this.checkPreviewMatrix();
        }
      });
      return;
    }
  }
  
  public void destroy(boolean paramBoolean)
  {
    CameraController localCameraController;
    CameraSession localCameraSession;
    if (this.cameraSession != null)
    {
      this.cameraSession.destroy();
      localCameraController = CameraController.getInstance();
      localCameraSession = this.cameraSession;
      if (paramBoolean) {
        break label45;
      }
    }
    label45:
    for (Semaphore localSemaphore = new Semaphore(0);; localSemaphore = null)
    {
      localCameraController.close(localCameraSession, localSemaphore);
      return;
    }
  }
  
  public CameraSession getCameraSession()
  {
    return this.cameraSession;
  }
  
  public boolean hasFrontFaceCamera()
  {
    ArrayList localArrayList = CameraController.getInstance().getCameras();
    int i = 0;
    while (i < localArrayList.size())
    {
      if (((CameraInfo)localArrayList.get(i)).frontCamera != 0) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public boolean isFrontface()
  {
    return this.isFrontface;
  }
  
  public boolean isInitied()
  {
    return this.initied;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    checkPreviewMatrix();
  }
  
  public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    initCamera(this.isFrontface);
  }
  
  public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
  {
    if (this.cameraSession != null) {
      CameraController.getInstance().close(this.cameraSession, null);
    }
    return false;
  }
  
  public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    checkPreviewMatrix();
  }
  
  public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture)
  {
    if ((!this.initied) && (this.cameraSession != null) && (this.cameraSession.isInitied()))
    {
      if (this.delegate != null) {
        this.delegate.onCameraInit();
      }
      this.initied = true;
    }
  }
  
  public void setClipLeft(int paramInt)
  {
    this.clipLeft = paramInt;
  }
  
  public void setClipTop(int paramInt)
  {
    this.clipTop = paramInt;
  }
  
  public void setDelegate(CameraViewDelegate paramCameraViewDelegate)
  {
    this.delegate = paramCameraViewDelegate;
  }
  
  public void setMirror(boolean paramBoolean)
  {
    this.mirror = paramBoolean;
  }
  
  public void switchCamera()
  {
    boolean bool = false;
    if (this.cameraSession != null)
    {
      CameraController.getInstance().close(this.cameraSession, null);
      this.cameraSession = null;
    }
    this.initied = false;
    if (!this.isFrontface) {
      bool = true;
    }
    this.isFrontface = bool;
    initCamera(this.isFrontface);
  }
  
  public static abstract interface CameraViewDelegate
  {
    public abstract void onCameraInit();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/camera/CameraView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */