package com.google.android.gms.vision;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.SystemClock;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import com.google.android.gms.common.images.Size;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CameraSource
{
  @SuppressLint({"InlinedApi"})
  public static final int CAMERA_FACING_BACK = 0;
  @SuppressLint({"InlinedApi"})
  public static final int CAMERA_FACING_FRONT = 1;
  private Context mContext;
  private int zzOa;
  private Map<byte[], ByteBuffer> zzbMA = new HashMap();
  private final Object zzbMo = new Object();
  private Camera zzbMp;
  private int zzbMq = 0;
  private Size zzbMr;
  private float zzbMs = 30.0F;
  private int zzbMt = 1024;
  private int zzbMu = 768;
  private boolean zzbMv = false;
  private SurfaceTexture zzbMw;
  private boolean zzbMx;
  private Thread zzbMy;
  private zzb zzbMz;
  
  @SuppressLint({"InlinedApi"})
  private final Camera zzDK()
    throws IOException
  {
    int k = 0;
    int j = this.zzbMq;
    Object localObject1 = new Camera.CameraInfo();
    int i = 0;
    if (i < Camera.getNumberOfCameras())
    {
      Camera.getCameraInfo(i, (Camera.CameraInfo)localObject1);
      if (((Camera.CameraInfo)localObject1).facing != j) {}
    }
    for (j = i;; j = -1)
    {
      if (j != -1) {
        break label69;
      }
      throw new IOException("Could not find requested camera.");
      i += 1;
      break;
    }
    label69:
    localObject1 = Camera.open(j);
    Object localObject3 = zza((Camera)localObject1, this.zzbMt, this.zzbMu);
    if (localObject3 == null) {
      throw new IOException("Could not find suitable preview size.");
    }
    Object localObject2 = ((zze)localObject3).zzDM();
    this.zzbMr = ((zze)localObject3).zzDL();
    int[] arrayOfInt = zza((Camera)localObject1, this.zzbMs);
    if (arrayOfInt == null) {
      throw new IOException("Could not find suitable preview frames per second range.");
    }
    localObject3 = ((Camera)localObject1).getParameters();
    if (localObject2 != null) {
      ((Camera.Parameters)localObject3).setPictureSize(((Size)localObject2).getWidth(), ((Size)localObject2).getHeight());
    }
    ((Camera.Parameters)localObject3).setPreviewSize(this.zzbMr.getWidth(), this.zzbMr.getHeight());
    ((Camera.Parameters)localObject3).setPreviewFpsRange(arrayOfInt[0], arrayOfInt[1]);
    ((Camera.Parameters)localObject3).setPreviewFormat(17);
    int m = ((WindowManager)this.mContext.getSystemService("window")).getDefaultDisplay().getRotation();
    i = k;
    switch (m)
    {
    default: 
      Log.e("CameraSource", 31 + "Bad rotation value: " + m);
      i = k;
    case 0: 
      localObject2 = new Camera.CameraInfo();
      Camera.getCameraInfo(j, (Camera.CameraInfo)localObject2);
      if (((Camera.CameraInfo)localObject2).facing == 1)
      {
        j = (((Camera.CameraInfo)localObject2).orientation + i) % 360;
        i = (360 - j) % 360;
        label344:
        this.zzOa = (j / 90);
        ((Camera)localObject1).setDisplayOrientation(i);
        ((Camera.Parameters)localObject3).setRotation(j);
        if (this.zzbMv)
        {
          if (!((Camera.Parameters)localObject3).getSupportedFocusModes().contains("continuous-video")) {
            break label510;
          }
          ((Camera.Parameters)localObject3).setFocusMode("continuous-video");
        }
      }
      break;
    }
    for (;;)
    {
      ((Camera)localObject1).setParameters((Camera.Parameters)localObject3);
      ((Camera)localObject1).setPreviewCallbackWithBuffer(new zza(null));
      ((Camera)localObject1).addCallbackBuffer(zza(this.zzbMr));
      ((Camera)localObject1).addCallbackBuffer(zza(this.zzbMr));
      ((Camera)localObject1).addCallbackBuffer(zza(this.zzbMr));
      ((Camera)localObject1).addCallbackBuffer(zza(this.zzbMr));
      return (Camera)localObject1;
      i = 90;
      break;
      i = 180;
      break;
      i = 270;
      break;
      i = (((Camera.CameraInfo)localObject2).orientation - i + 360) % 360;
      j = i;
      break label344;
      label510:
      Log.i("CameraSource", "Camera auto focus is not supported on this device.");
    }
  }
  
  private static zze zza(Camera paramCamera, int paramInt1, int paramInt2)
  {
    zze localzze = null;
    Object localObject1 = paramCamera.getParameters();
    paramCamera = ((Camera.Parameters)localObject1).getSupportedPreviewSizes();
    Object localObject2 = ((Camera.Parameters)localObject1).getSupportedPictureSizes();
    localObject1 = new ArrayList();
    Iterator localIterator1 = paramCamera.iterator();
    for (;;)
    {
      if (!localIterator1.hasNext()) {
        break label153;
      }
      Camera.Size localSize1 = (Camera.Size)localIterator1.next();
      float f = localSize1.width / localSize1.height;
      Iterator localIterator2 = ((List)localObject2).iterator();
      if (localIterator2.hasNext())
      {
        Camera.Size localSize2 = (Camera.Size)localIterator2.next();
        if (Math.abs(f - localSize2.width / localSize2.height) >= 0.01F) {
          break;
        }
        ((List)localObject1).add(new zze(localSize1, localSize2));
      }
    }
    label153:
    if (((List)localObject1).size() == 0)
    {
      Log.w("CameraSource", "No preview sizes have a corresponding same-aspect-ratio picture size");
      paramCamera = paramCamera.iterator();
      while (paramCamera.hasNext()) {
        ((List)localObject1).add(new zze((Camera.Size)paramCamera.next(), null));
      }
    }
    int i = Integer.MAX_VALUE;
    localObject1 = (ArrayList)localObject1;
    int m = ((ArrayList)localObject1).size();
    int j = 0;
    paramCamera = localzze;
    if (j < m)
    {
      localzze = (zze)((ArrayList)localObject1).get(j);
      localObject2 = localzze.zzDL();
      int k = Math.abs(((Size)localObject2).getWidth() - paramInt1);
      k = Math.abs(((Size)localObject2).getHeight() - paramInt2) + k;
      if (k >= i) {
        break label319;
      }
      paramCamera = localzze;
      i = k;
    }
    label319:
    for (;;)
    {
      j += 1;
      break;
      return paramCamera;
    }
  }
  
  @SuppressLint({"InlinedApi"})
  private final byte[] zza(Size paramSize)
  {
    paramSize = new byte[(int)Math.ceil(ImageFormat.getBitsPerPixel(17) * (paramSize.getHeight() * paramSize.getWidth()) / 8.0D) + 1];
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramSize);
    if ((!localByteBuffer.hasArray()) || (localByteBuffer.array() != paramSize)) {
      throw new IllegalStateException("Failed to create valid buffer for camera source.");
    }
    this.zzbMA.put(paramSize, localByteBuffer);
    return paramSize;
  }
  
  @SuppressLint({"InlinedApi"})
  private static int[] zza(Camera paramCamera, float paramFloat)
  {
    int k = (int)(1000.0F * paramFloat);
    int[] arrayOfInt = null;
    int i = Integer.MAX_VALUE;
    Iterator localIterator = paramCamera.getParameters().getSupportedPreviewFpsRange().iterator();
    paramCamera = arrayOfInt;
    if (localIterator.hasNext())
    {
      arrayOfInt = (int[])localIterator.next();
      int j = arrayOfInt[0];
      int m = arrayOfInt[1];
      j = Math.abs(k - j) + Math.abs(k - m);
      if (j >= i) {
        break label97;
      }
      paramCamera = arrayOfInt;
      i = j;
    }
    label97:
    for (;;)
    {
      break;
      return paramCamera;
    }
  }
  
  public int getCameraFacing()
  {
    return this.zzbMq;
  }
  
  public Size getPreviewSize()
  {
    return this.zzbMr;
  }
  
  public void release()
  {
    synchronized (this.zzbMo)
    {
      stop();
      this.zzbMz.release();
      return;
    }
  }
  
  @RequiresPermission("android.permission.CAMERA")
  public CameraSource start()
    throws IOException
  {
    synchronized (this.zzbMo)
    {
      if (this.zzbMp != null) {
        return this;
      }
      this.zzbMp = zzDK();
      this.zzbMw = new SurfaceTexture(100);
      this.zzbMp.setPreviewTexture(this.zzbMw);
      this.zzbMx = true;
      this.zzbMp.startPreview();
      this.zzbMy = new Thread(this.zzbMz);
      this.zzbMz.setActive(true);
      this.zzbMy.start();
      return this;
    }
  }
  
  @RequiresPermission("android.permission.CAMERA")
  public CameraSource start(SurfaceHolder paramSurfaceHolder)
    throws IOException
  {
    synchronized (this.zzbMo)
    {
      if (this.zzbMp != null) {
        return this;
      }
      this.zzbMp = zzDK();
      this.zzbMp.setPreviewDisplay(paramSurfaceHolder);
      this.zzbMp.startPreview();
      this.zzbMy = new Thread(this.zzbMz);
      this.zzbMz.setActive(true);
      this.zzbMy.start();
      this.zzbMx = false;
      return this;
    }
  }
  
  /* Error */
  public void stop()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 68	com/google/android/gms/vision/CameraSource:zzbMo	Ljava/lang/Object;
    //   4: astore_1
    //   5: aload_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 266	com/google/android/gms/vision/CameraSource:zzbMz	Lcom/google/android/gms/vision/CameraSource$zzb;
    //   11: iconst_0
    //   12: invokevirtual 417	com/google/android/gms/vision/CameraSource$zzb:setActive	(Z)V
    //   15: aload_0
    //   16: getfield 413	com/google/android/gms/vision/CameraSource:zzbMy	Ljava/lang/Thread;
    //   19: astore_2
    //   20: aload_2
    //   21: ifnull +15 -> 36
    //   24: aload_0
    //   25: getfield 413	com/google/android/gms/vision/CameraSource:zzbMy	Ljava/lang/Thread;
    //   28: invokevirtual 431	java/lang/Thread:join	()V
    //   31: aload_0
    //   32: aconst_null
    //   33: putfield 413	com/google/android/gms/vision/CameraSource:zzbMy	Ljava/lang/Thread;
    //   36: aload_0
    //   37: getfield 373	com/google/android/gms/vision/CameraSource:zzbMp	Landroid/hardware/Camera;
    //   40: ifnull +45 -> 85
    //   43: aload_0
    //   44: getfield 373	com/google/android/gms/vision/CameraSource:zzbMp	Landroid/hardware/Camera;
    //   47: invokevirtual 434	android/hardware/Camera:stopPreview	()V
    //   50: aload_0
    //   51: getfield 373	com/google/android/gms/vision/CameraSource:zzbMp	Landroid/hardware/Camera;
    //   54: aconst_null
    //   55: invokevirtual 246	android/hardware/Camera:setPreviewCallbackWithBuffer	(Landroid/hardware/Camera$PreviewCallback;)V
    //   58: aload_0
    //   59: getfield 403	com/google/android/gms/vision/CameraSource:zzbMx	Z
    //   62: ifeq +53 -> 115
    //   65: aload_0
    //   66: getfield 373	com/google/android/gms/vision/CameraSource:zzbMp	Landroid/hardware/Camera;
    //   69: aconst_null
    //   70: invokevirtual 401	android/hardware/Camera:setPreviewTexture	(Landroid/graphics/SurfaceTexture;)V
    //   73: aload_0
    //   74: getfield 373	com/google/android/gms/vision/CameraSource:zzbMp	Landroid/hardware/Camera;
    //   77: invokevirtual 435	android/hardware/Camera:release	()V
    //   80: aload_0
    //   81: aconst_null
    //   82: putfield 373	com/google/android/gms/vision/CameraSource:zzbMp	Landroid/hardware/Camera;
    //   85: aload_0
    //   86: getfield 84	com/google/android/gms/vision/CameraSource:zzbMA	Ljava/util/Map;
    //   89: invokeinterface 438 1 0
    //   94: aload_1
    //   95: monitorexit
    //   96: return
    //   97: astore_2
    //   98: ldc -70
    //   100: ldc_w 440
    //   103: invokestatic 443	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   106: pop
    //   107: goto -76 -> 31
    //   110: astore_2
    //   111: aload_1
    //   112: monitorexit
    //   113: aload_2
    //   114: athrow
    //   115: aload_0
    //   116: getfield 373	com/google/android/gms/vision/CameraSource:zzbMp	Landroid/hardware/Camera;
    //   119: aconst_null
    //   120: invokevirtual 424	android/hardware/Camera:setPreviewDisplay	(Landroid/view/SurfaceHolder;)V
    //   123: goto -50 -> 73
    //   126: astore_2
    //   127: aload_2
    //   128: invokestatic 449	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   131: astore_2
    //   132: ldc -70
    //   134: new 188	java/lang/StringBuilder
    //   137: dup
    //   138: aload_2
    //   139: invokestatic 449	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   142: invokevirtual 452	java/lang/String:length	()I
    //   145: bipush 32
    //   147: iadd
    //   148: invokespecial 190	java/lang/StringBuilder:<init>	(I)V
    //   151: ldc_w 454
    //   154: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   157: aload_2
    //   158: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   161: invokevirtual 203	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   164: invokestatic 209	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   167: pop
    //   168: goto -95 -> 73
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	171	0	this	CameraSource
    //   4	108	1	localObject1	Object
    //   19	2	2	localThread	Thread
    //   97	1	2	localInterruptedException	InterruptedException
    //   110	4	2	localObject2	Object
    //   126	2	2	localException	Exception
    //   131	27	2	str	String
    // Exception table:
    //   from	to	target	type
    //   24	31	97	java/lang/InterruptedException
    //   7	20	110	finally
    //   24	31	110	finally
    //   31	36	110	finally
    //   36	58	110	finally
    //   58	73	110	finally
    //   73	85	110	finally
    //   85	96	110	finally
    //   98	107	110	finally
    //   111	113	110	finally
    //   115	123	110	finally
    //   127	168	110	finally
    //   58	73	126	java/lang/Exception
    //   115	123	126	java/lang/Exception
  }
  
  public void takePicture(ShutterCallback paramShutterCallback, PictureCallback paramPictureCallback)
  {
    synchronized (this.zzbMo)
    {
      if (this.zzbMp != null)
      {
        zzd localzzd = new zzd(null);
        zzd.zza(localzzd, paramShutterCallback);
        paramShutterCallback = new zzc(null);
        zzc.zza(paramShutterCallback, paramPictureCallback);
        this.zzbMp.takePicture(localzzd, null, null, paramShutterCallback);
      }
      return;
    }
  }
  
  public static class Builder
  {
    private final Detector<?> zzbMB;
    private CameraSource zzbMC = new CameraSource(null);
    
    public Builder(Context paramContext, Detector<?> paramDetector)
    {
      if (paramContext == null) {
        throw new IllegalArgumentException("No context supplied.");
      }
      if (paramDetector == null) {
        throw new IllegalArgumentException("No detector supplied.");
      }
      this.zzbMB = paramDetector;
      CameraSource.zza(this.zzbMC, paramContext);
    }
    
    public CameraSource build()
    {
      CameraSource localCameraSource1 = this.zzbMC;
      CameraSource localCameraSource2 = this.zzbMC;
      localCameraSource2.getClass();
      CameraSource.zza(localCameraSource1, new CameraSource.zzb(localCameraSource2, this.zzbMB));
      return this.zzbMC;
    }
    
    public Builder setAutoFocusEnabled(boolean paramBoolean)
    {
      CameraSource.zza(this.zzbMC, paramBoolean);
      return this;
    }
    
    public Builder setFacing(int paramInt)
    {
      if ((paramInt != 0) && (paramInt != 1)) {
        throw new IllegalArgumentException(27 + "Invalid camera: " + paramInt);
      }
      CameraSource.zzc(this.zzbMC, paramInt);
      return this;
    }
    
    public Builder setRequestedFps(float paramFloat)
    {
      if (paramFloat <= 0.0F) {
        throw new IllegalArgumentException(28 + "Invalid fps: " + paramFloat);
      }
      CameraSource.zza(this.zzbMC, paramFloat);
      return this;
    }
    
    public Builder setRequestedPreviewSize(int paramInt1, int paramInt2)
    {
      if ((paramInt1 <= 0) || (paramInt1 > 1000000) || (paramInt2 <= 0) || (paramInt2 > 1000000)) {
        throw new IllegalArgumentException(45 + "Invalid preview size: " + paramInt1 + "x" + paramInt2);
      }
      CameraSource.zza(this.zzbMC, paramInt1);
      CameraSource.zzb(this.zzbMC, paramInt2);
      return this;
    }
  }
  
  public static abstract interface PictureCallback
  {
    public abstract void onPictureTaken(byte[] paramArrayOfByte);
  }
  
  public static abstract interface ShutterCallback
  {
    public abstract void onShutter();
  }
  
  final class zza
    implements Camera.PreviewCallback
  {
    private zza() {}
    
    public final void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
    {
      CameraSource.zzc(CameraSource.this).zza(paramArrayOfByte, paramCamera);
    }
  }
  
  final class zzb
    implements Runnable
  {
    private boolean mActive = true;
    private final Object mLock = new Object();
    private long zzagZ = SystemClock.elapsedRealtime();
    private Detector<?> zzbMB;
    private long zzbME;
    private int zzbMF = 0;
    private ByteBuffer zzbMG;
    
    zzb()
    {
      Detector localDetector;
      this.zzbMB = localDetector;
    }
    
    @SuppressLint({"Assert"})
    final void release()
    {
      this.zzbMB.release();
      this.zzbMB = null;
    }
    
    @SuppressLint({"InlinedApi"})
    public final void run()
    {
      for (;;)
      {
        synchronized (this.mLock)
        {
          if (this.mActive)
          {
            ByteBuffer localByteBuffer1 = this.zzbMG;
            if (localByteBuffer1 == null) {
              try
              {
                this.mLock.wait();
              }
              catch (InterruptedException localInterruptedException)
              {
                Log.d("CameraSource", "Frame processing loop terminated.", localInterruptedException);
                return;
              }
            }
          }
          if (!this.mActive) {
            return;
          }
        }
        Frame localFrame = new Frame.Builder().setImageData(this.zzbMG, CameraSource.zzf(CameraSource.this).getWidth(), CameraSource.zzf(CameraSource.this).getHeight(), 17).setId(this.zzbMF).setTimestampMillis(this.zzbME).setRotation(CameraSource.zze(CameraSource.this)).build();
        ByteBuffer localByteBuffer2 = this.zzbMG;
        this.zzbMG = null;
        try
        {
          this.zzbMB.receiveFrame(localFrame);
          CameraSource.zzb(CameraSource.this).addCallbackBuffer(localByteBuffer2.array());
        }
        catch (Throwable localThrowable)
        {
          Log.e("CameraSource", "Exception thrown from receiver.", localThrowable);
          CameraSource.zzb(CameraSource.this).addCallbackBuffer(localByteBuffer2.array());
        }
        finally
        {
          CameraSource.zzb(CameraSource.this).addCallbackBuffer(localByteBuffer2.array());
        }
      }
    }
    
    final void setActive(boolean paramBoolean)
    {
      synchronized (this.mLock)
      {
        this.mActive = paramBoolean;
        this.mLock.notifyAll();
        return;
      }
    }
    
    final void zza(byte[] paramArrayOfByte, Camera paramCamera)
    {
      synchronized (this.mLock)
      {
        if (this.zzbMG != null)
        {
          paramCamera.addCallbackBuffer(this.zzbMG.array());
          this.zzbMG = null;
        }
        if (!CameraSource.zzd(CameraSource.this).containsKey(paramArrayOfByte))
        {
          Log.d("CameraSource", "Skipping frame. Could not find ByteBuffer associated with the image data from the camera.");
          return;
        }
        this.zzbME = (SystemClock.elapsedRealtime() - this.zzagZ);
        this.zzbMF += 1;
        this.zzbMG = ((ByteBuffer)CameraSource.zzd(CameraSource.this).get(paramArrayOfByte));
        this.mLock.notifyAll();
        return;
      }
    }
  }
  
  final class zzc
    implements Camera.PictureCallback
  {
    private CameraSource.PictureCallback zzbMH;
    
    private zzc() {}
    
    public final void onPictureTaken(byte[] arg1, Camera paramCamera)
    {
      if (this.zzbMH != null) {
        this.zzbMH.onPictureTaken(???);
      }
      synchronized (CameraSource.zza(CameraSource.this))
      {
        if (CameraSource.zzb(CameraSource.this) != null) {
          CameraSource.zzb(CameraSource.this).startPreview();
        }
        return;
      }
    }
  }
  
  static final class zzd
    implements Camera.ShutterCallback
  {
    private CameraSource.ShutterCallback zzbMI;
    
    public final void onShutter()
    {
      if (this.zzbMI != null) {
        this.zzbMI.onShutter();
      }
    }
  }
  
  static final class zze
  {
    private Size zzbMJ;
    private Size zzbMK;
    
    public zze(Camera.Size paramSize1, Camera.Size paramSize2)
    {
      this.zzbMJ = new Size(paramSize1.width, paramSize1.height);
      if (paramSize2 != null) {
        this.zzbMK = new Size(paramSize2.width, paramSize2.height);
      }
    }
    
    public final Size zzDL()
    {
      return this.zzbMJ;
    }
    
    public final Size zzDM()
    {
      return this.zzbMK;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/CameraSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */