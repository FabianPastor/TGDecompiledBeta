package org.telegram.messenger.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

public class CameraSession
{
  public static final int ORIENTATION_HYSTERESIS = 5;
  private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback()
  {
    public void onAutoFocus(boolean paramAnonymousBoolean, Camera paramAnonymousCamera)
    {
      if (paramAnonymousBoolean) {}
    }
  };
  protected CameraInfo cameraInfo;
  private String currentFlashMode = "off";
  private int currentOrientation;
  private int diffOrientation;
  private boolean initied;
  private boolean isVideo;
  private int jpegOrientation;
  private int lastDisplayOrientation = -1;
  private int lastOrientation = -1;
  private boolean meteringAreaSupported;
  private OrientationEventListener orientationEventListener;
  private final int pictureFormat;
  private final Size pictureSize;
  private final Size previewSize;
  private boolean sameTakePictureOrientation;
  
  public CameraSession(CameraInfo paramCameraInfo, Size paramSize1, Size paramSize2, int paramInt)
  {
    this.previewSize = paramSize1;
    this.pictureSize = paramSize2;
    this.pictureFormat = paramInt;
    this.cameraInfo = paramCameraInfo;
    paramSize1 = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0);
    if (this.cameraInfo.frontCamera != 0)
    {
      paramCameraInfo = "flashMode_front";
      this.currentFlashMode = paramSize1.getString(paramCameraInfo, "off");
      this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext)
      {
        public void onOrientationChanged(int paramAnonymousInt)
        {
          if ((CameraSession.this.orientationEventListener == null) || (!CameraSession.this.initied) || (paramAnonymousInt == -1)) {}
          for (;;)
          {
            return;
            CameraSession.access$202(CameraSession.this, CameraSession.this.roundOrientation(paramAnonymousInt, CameraSession.this.jpegOrientation));
            paramAnonymousInt = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            if ((CameraSession.this.lastOrientation != CameraSession.this.jpegOrientation) || (paramAnonymousInt != CameraSession.this.lastDisplayOrientation))
            {
              if (!CameraSession.this.isVideo) {
                CameraSession.this.configurePhotoCamera();
              }
              CameraSession.access$502(CameraSession.this, paramAnonymousInt);
              CameraSession.access$402(CameraSession.this, CameraSession.this.jpegOrientation);
            }
          }
        }
      };
      if (!this.orientationEventListener.canDetectOrientation()) {
        break label128;
      }
      this.orientationEventListener.enable();
    }
    for (;;)
    {
      return;
      paramCameraInfo = "flashMode";
      break;
      label128:
      this.orientationEventListener.disable();
      this.orientationEventListener = null;
    }
  }
  
  private int getDisplayOrientation(Camera.CameraInfo paramCameraInfo, boolean paramBoolean)
  {
    int i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
    int j = 0;
    switch (i)
    {
    default: 
      if (paramCameraInfo.facing == 1)
      {
        i = (360 - (paramCameraInfo.orientation + j) % 360) % 360;
        j = i;
        if (!paramBoolean)
        {
          j = i;
          if (i == 90) {
            j = 270;
          }
        }
        i = j;
        if (!paramBoolean)
        {
          i = j;
          if ("Huawei".equals(Build.MANUFACTURER))
          {
            i = j;
            if ("angler".equals(Build.PRODUCT))
            {
              i = j;
              if (j != 270) {
                break;
              }
            }
          }
        }
      }
      break;
    }
    for (i = 90;; i = (paramCameraInfo.orientation - j + 360) % 360)
    {
      return i;
      j = 0;
      break;
      j = 90;
      break;
      j = 180;
      break;
      j = 270;
      break;
    }
  }
  
  private int getHigh()
  {
    if (("LGE".equals(Build.MANUFACTURER)) && ("g3_tmo_us".equals(Build.PRODUCT))) {}
    for (int i = 4;; i = 1) {
      return i;
    }
  }
  
  private int roundOrientation(int paramInt1, int paramInt2)
  {
    if (paramInt2 == -1)
    {
      i = 1;
      if (i != 0) {
        paramInt2 = (paramInt1 + 45) / 90 * 90 % 360;
      }
      return paramInt2;
    }
    int i = Math.abs(paramInt1 - paramInt2);
    if (Math.min(i, 360 - i) >= 50) {}
    for (i = 1;; i = 0) {
      break;
    }
  }
  
  public void checkFlashMode(String paramString)
  {
    if (CameraController.getInstance().availableFlashModes.contains(this.currentFlashMode)) {
      return;
    }
    this.currentFlashMode = paramString;
    configurePhotoCamera();
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit();
    if (this.cameraInfo.frontCamera != 0) {}
    for (String str = "flashMode_front";; str = "flashMode")
    {
      localEditor.putString(str, paramString).commit();
      break;
    }
  }
  
  /* Error */
  protected void configurePhotoCamera()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_1
    //   2: iconst_1
    //   3: istore_2
    //   4: aload_0
    //   5: getfield 59	org/telegram/messenger/camera/CameraSession:cameraInfo	Lorg/telegram/messenger/camera/CameraInfo;
    //   8: getfield 231	org/telegram/messenger/camera/CameraInfo:camera	Landroid/hardware/Camera;
    //   11: astore_3
    //   12: aload_3
    //   13: ifnull +264 -> 277
    //   16: new 153	android/hardware/Camera$CameraInfo
    //   19: astore 4
    //   21: aload 4
    //   23: invokespecial 232	android/hardware/Camera$CameraInfo:<init>	()V
    //   26: aconst_null
    //   27: astore 5
    //   29: aload_3
    //   30: invokevirtual 238	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
    //   33: astore 6
    //   35: aload 6
    //   37: astore 5
    //   39: aload_0
    //   40: getfield 59	org/telegram/messenger/camera/CameraSession:cameraInfo	Lorg/telegram/messenger/camera/CameraInfo;
    //   43: invokevirtual 241	org/telegram/messenger/camera/CameraInfo:getCameraId	()I
    //   46: aload 4
    //   48: invokestatic 245	android/hardware/Camera:getCameraInfo	(ILandroid/hardware/Camera$CameraInfo;)V
    //   51: aload_0
    //   52: aload 4
    //   54: iconst_1
    //   55: invokespecial 247	org/telegram/messenger/camera/CameraSession:getDisplayOrientation	(Landroid/hardware/Camera$CameraInfo;Z)I
    //   58: istore 7
    //   60: ldc -7
    //   62: getstatic 166	android/os/Build:MANUFACTURER	Ljava/lang/String;
    //   65: invokevirtual 172	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   68: ifeq +230 -> 298
    //   71: ldc -5
    //   73: getstatic 177	android/os/Build:PRODUCT	Ljava/lang/String;
    //   76: invokevirtual 172	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   79: ifeq +219 -> 298
    //   82: iconst_0
    //   83: istore 8
    //   85: aload_0
    //   86: iload 8
    //   88: putfield 253	org/telegram/messenger/camera/CameraSession:currentOrientation	I
    //   91: aload_3
    //   92: iload 8
    //   94: invokevirtual 257	android/hardware/Camera:setDisplayOrientation	(I)V
    //   97: aload 5
    //   99: ifnull +178 -> 277
    //   102: aload 5
    //   104: aload_0
    //   105: getfield 53	org/telegram/messenger/camera/CameraSession:previewSize	Lorg/telegram/messenger/camera/Size;
    //   108: invokevirtual 262	org/telegram/messenger/camera/Size:getWidth	()I
    //   111: aload_0
    //   112: getfield 53	org/telegram/messenger/camera/CameraSession:previewSize	Lorg/telegram/messenger/camera/Size;
    //   115: invokevirtual 265	org/telegram/messenger/camera/Size:getHeight	()I
    //   118: invokevirtual 271	android/hardware/Camera$Parameters:setPreviewSize	(II)V
    //   121: aload 5
    //   123: aload_0
    //   124: getfield 55	org/telegram/messenger/camera/CameraSession:pictureSize	Lorg/telegram/messenger/camera/Size;
    //   127: invokevirtual 262	org/telegram/messenger/camera/Size:getWidth	()I
    //   130: aload_0
    //   131: getfield 55	org/telegram/messenger/camera/CameraSession:pictureSize	Lorg/telegram/messenger/camera/Size;
    //   134: invokevirtual 265	org/telegram/messenger/camera/Size:getHeight	()I
    //   137: invokevirtual 274	android/hardware/Camera$Parameters:setPictureSize	(II)V
    //   140: aload 5
    //   142: aload_0
    //   143: getfield 57	org/telegram/messenger/camera/CameraSession:pictureFormat	I
    //   146: invokevirtual 277	android/hardware/Camera$Parameters:setPictureFormat	(I)V
    //   149: aload 5
    //   151: invokevirtual 281	android/hardware/Camera$Parameters:getSupportedFocusModes	()Ljava/util/List;
    //   154: ldc_w 283
    //   157: invokeinterface 286 2 0
    //   162: ifeq +11 -> 173
    //   165: aload 5
    //   167: ldc_w 283
    //   170: invokevirtual 289	android/hardware/Camera$Parameters:setFocusMode	(Ljava/lang/String;)V
    //   173: iconst_0
    //   174: istore 8
    //   176: aload_0
    //   177: getfield 116	org/telegram/messenger/camera/CameraSession:jpegOrientation	I
    //   180: iconst_m1
    //   181: if_icmpeq +32 -> 213
    //   184: aload 4
    //   186: getfield 156	android/hardware/Camera$CameraInfo:facing	I
    //   189: iconst_1
    //   190: if_icmpne +243 -> 433
    //   193: aload 4
    //   195: getfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   198: aload_0
    //   199: getfield 116	org/telegram/messenger/camera/CameraSession:jpegOrientation	I
    //   202: isub
    //   203: sipush 360
    //   206: iadd
    //   207: sipush 360
    //   210: irem
    //   211: istore 8
    //   213: aload 5
    //   215: iload 8
    //   217: invokevirtual 292	android/hardware/Camera$Parameters:setRotation	(I)V
    //   220: aload 4
    //   222: getfield 156	android/hardware/Camera$CameraInfo:facing	I
    //   225: iconst_1
    //   226: if_icmpne +231 -> 457
    //   229: sipush 360
    //   232: iload 7
    //   234: isub
    //   235: sipush 360
    //   238: irem
    //   239: iload 8
    //   241: if_icmpne +211 -> 452
    //   244: aload_0
    //   245: iload_2
    //   246: putfield 294	org/telegram/messenger/camera/CameraSession:sameTakePictureOrientation	Z
    //   249: aload 5
    //   251: aload_0
    //   252: getfield 42	org/telegram/messenger/camera/CameraSession:currentFlashMode	Ljava/lang/String;
    //   255: invokevirtual 297	android/hardware/Camera$Parameters:setFlashMode	(Ljava/lang/String;)V
    //   258: aload_3
    //   259: aload 5
    //   261: invokevirtual 301	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
    //   264: aload 5
    //   266: invokevirtual 304	android/hardware/Camera$Parameters:getMaxNumMeteringAreas	()I
    //   269: ifle +8 -> 277
    //   272: aload_0
    //   273: iconst_1
    //   274: putfield 306	org/telegram/messenger/camera/CameraSession:meteringAreaSupported	Z
    //   277: return
    //   278: astore 6
    //   280: aload 6
    //   282: invokestatic 312	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   285: goto -246 -> 39
    //   288: astore 5
    //   290: aload 5
    //   292: invokestatic 312	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   295: goto -18 -> 277
    //   298: iconst_0
    //   299: istore 8
    //   301: iload 7
    //   303: tableswitch	default:+29->332, 0:+80->383, 1:+86->389, 2:+93->396, 3:+101->404
    //   332: aload 4
    //   334: getfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   337: bipush 90
    //   339: irem
    //   340: ifeq +9 -> 349
    //   343: aload 4
    //   345: iconst_0
    //   346: putfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   349: aload 4
    //   351: getfield 156	android/hardware/Camera$CameraInfo:facing	I
    //   354: iconst_1
    //   355: if_icmpne +57 -> 412
    //   358: sipush 360
    //   361: aload 4
    //   363: getfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   366: iload 8
    //   368: iadd
    //   369: sipush 360
    //   372: irem
    //   373: isub
    //   374: sipush 360
    //   377: irem
    //   378: istore 8
    //   380: goto -295 -> 85
    //   383: iconst_0
    //   384: istore 8
    //   386: goto -54 -> 332
    //   389: bipush 90
    //   391: istore 8
    //   393: goto -61 -> 332
    //   396: sipush 180
    //   399: istore 8
    //   401: goto -69 -> 332
    //   404: sipush 270
    //   407: istore 8
    //   409: goto -77 -> 332
    //   412: aload 4
    //   414: getfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   417: iload 8
    //   419: isub
    //   420: sipush 360
    //   423: iadd
    //   424: sipush 360
    //   427: irem
    //   428: istore 8
    //   430: goto -50 -> 380
    //   433: aload 4
    //   435: getfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   438: aload_0
    //   439: getfield 116	org/telegram/messenger/camera/CameraSession:jpegOrientation	I
    //   442: iadd
    //   443: sipush 360
    //   446: irem
    //   447: istore 8
    //   449: goto -236 -> 213
    //   452: iconst_0
    //   453: istore_2
    //   454: goto -210 -> 244
    //   457: iload 7
    //   459: iload 8
    //   461: if_icmpne +18 -> 479
    //   464: iload_1
    //   465: istore_2
    //   466: aload_0
    //   467: iload_2
    //   468: putfield 294	org/telegram/messenger/camera/CameraSession:sameTakePictureOrientation	Z
    //   471: goto -222 -> 249
    //   474: astore 6
    //   476: goto -227 -> 249
    //   479: iconst_0
    //   480: istore_2
    //   481: goto -15 -> 466
    //   484: astore 6
    //   486: goto -222 -> 264
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	489	0	this	CameraSession
    //   1	464	1	bool1	boolean
    //   3	478	2	bool2	boolean
    //   11	248	3	localCamera	Camera
    //   19	415	4	localCameraInfo	Camera.CameraInfo
    //   27	238	5	localObject	Object
    //   288	3	5	localThrowable	Throwable
    //   33	3	6	localParameters	android.hardware.Camera.Parameters
    //   278	3	6	localException1	Exception
    //   474	1	6	localException2	Exception
    //   484	1	6	localException3	Exception
    //   58	404	7	i	int
    //   83	379	8	j	int
    // Exception table:
    //   from	to	target	type
    //   29	35	278	java/lang/Exception
    //   4	12	288	java/lang/Throwable
    //   16	26	288	java/lang/Throwable
    //   29	35	288	java/lang/Throwable
    //   39	82	288	java/lang/Throwable
    //   85	97	288	java/lang/Throwable
    //   102	173	288	java/lang/Throwable
    //   176	213	288	java/lang/Throwable
    //   213	229	288	java/lang/Throwable
    //   244	249	288	java/lang/Throwable
    //   249	258	288	java/lang/Throwable
    //   258	264	288	java/lang/Throwable
    //   264	277	288	java/lang/Throwable
    //   280	285	288	java/lang/Throwable
    //   332	349	288	java/lang/Throwable
    //   349	380	288	java/lang/Throwable
    //   412	430	288	java/lang/Throwable
    //   433	449	288	java/lang/Throwable
    //   466	471	288	java/lang/Throwable
    //   213	229	474	java/lang/Exception
    //   244	249	474	java/lang/Exception
    //   466	471	474	java/lang/Exception
    //   258	264	484	java/lang/Exception
  }
  
  protected void configureRecorder(int paramInt, MediaRecorder paramMediaRecorder)
  {
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    Camera.getCameraInfo(this.cameraInfo.cameraId, localCameraInfo);
    getDisplayOrientation(localCameraInfo, false);
    int i = 0;
    boolean bool2;
    if (this.jpegOrientation != -1)
    {
      if (localCameraInfo.facing == 1) {
        i = (localCameraInfo.orientation - this.jpegOrientation + 360) % 360;
      }
    }
    else
    {
      paramMediaRecorder.setOrientationHint(i);
      i = getHigh();
      boolean bool1 = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, i);
      bool2 = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, 0);
      if ((!bool1) || ((paramInt != 1) && (bool2))) {
        break label158;
      }
      paramMediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, i));
    }
    for (;;)
    {
      this.isVideo = true;
      return;
      i = (localCameraInfo.orientation + this.jpegOrientation) % 360;
      break;
      label158:
      if (!bool2) {
        break label181;
      }
      paramMediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, 0));
    }
    label181:
    throw new IllegalStateException("cannot find valid CamcorderProfile");
  }
  
  /* Error */
  protected void configureRoundCamera()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_1
    //   2: iconst_1
    //   3: istore_2
    //   4: aload_0
    //   5: iconst_1
    //   6: putfield 131	org/telegram/messenger/camera/CameraSession:isVideo	Z
    //   9: aload_0
    //   10: getfield 59	org/telegram/messenger/camera/CameraSession:cameraInfo	Lorg/telegram/messenger/camera/CameraInfo;
    //   13: getfield 231	org/telegram/messenger/camera/CameraInfo:camera	Landroid/hardware/Camera;
    //   16: astore_3
    //   17: aload_3
    //   18: ifnull +391 -> 409
    //   21: new 153	android/hardware/Camera$CameraInfo
    //   24: astore 4
    //   26: aload 4
    //   28: invokespecial 232	android/hardware/Camera$CameraInfo:<init>	()V
    //   31: aconst_null
    //   32: astore 5
    //   34: aload_3
    //   35: invokevirtual 238	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
    //   38: astore 6
    //   40: aload 6
    //   42: astore 5
    //   44: aload_0
    //   45: getfield 59	org/telegram/messenger/camera/CameraSession:cameraInfo	Lorg/telegram/messenger/camera/CameraInfo;
    //   48: invokevirtual 241	org/telegram/messenger/camera/CameraInfo:getCameraId	()I
    //   51: aload 4
    //   53: invokestatic 245	android/hardware/Camera:getCameraInfo	(ILandroid/hardware/Camera$CameraInfo;)V
    //   56: aload_0
    //   57: aload 4
    //   59: iconst_1
    //   60: invokespecial 247	org/telegram/messenger/camera/CameraSession:getDisplayOrientation	(Landroid/hardware/Camera$CameraInfo;Z)I
    //   63: istore 7
    //   65: ldc -7
    //   67: getstatic 166	android/os/Build:MANUFACTURER	Ljava/lang/String;
    //   70: invokevirtual 172	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   73: ifeq +357 -> 430
    //   76: ldc -5
    //   78: getstatic 177	android/os/Build:PRODUCT	Ljava/lang/String;
    //   81: invokevirtual 172	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   84: ifeq +346 -> 430
    //   87: iconst_0
    //   88: istore 8
    //   90: aload_0
    //   91: iload 8
    //   93: putfield 253	org/telegram/messenger/camera/CameraSession:currentOrientation	I
    //   96: aload_3
    //   97: iload 8
    //   99: invokevirtual 257	android/hardware/Camera:setDisplayOrientation	(I)V
    //   102: aload_0
    //   103: aload_0
    //   104: getfield 253	org/telegram/messenger/camera/CameraSession:currentOrientation	I
    //   107: iload 7
    //   109: isub
    //   110: putfield 347	org/telegram/messenger/camera/CameraSession:diffOrientation	I
    //   113: aload 5
    //   115: ifnull +294 -> 409
    //   118: getstatic 352	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   121: ifeq +53 -> 174
    //   124: new 354	java/lang/StringBuilder
    //   127: astore 6
    //   129: aload 6
    //   131: invokespecial 355	java/lang/StringBuilder:<init>	()V
    //   134: aload 6
    //   136: ldc_w 357
    //   139: invokevirtual 361	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   142: aload_0
    //   143: getfield 53	org/telegram/messenger/camera/CameraSession:previewSize	Lorg/telegram/messenger/camera/Size;
    //   146: invokevirtual 262	org/telegram/messenger/camera/Size:getWidth	()I
    //   149: invokevirtual 364	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   152: ldc_w 366
    //   155: invokevirtual 361	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   158: aload_0
    //   159: getfield 53	org/telegram/messenger/camera/CameraSession:previewSize	Lorg/telegram/messenger/camera/Size;
    //   162: invokevirtual 265	org/telegram/messenger/camera/Size:getHeight	()I
    //   165: invokevirtual 364	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   168: invokevirtual 370	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   171: invokestatic 373	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   174: aload 5
    //   176: aload_0
    //   177: getfield 53	org/telegram/messenger/camera/CameraSession:previewSize	Lorg/telegram/messenger/camera/Size;
    //   180: invokevirtual 262	org/telegram/messenger/camera/Size:getWidth	()I
    //   183: aload_0
    //   184: getfield 53	org/telegram/messenger/camera/CameraSession:previewSize	Lorg/telegram/messenger/camera/Size;
    //   187: invokevirtual 265	org/telegram/messenger/camera/Size:getHeight	()I
    //   190: invokevirtual 271	android/hardware/Camera$Parameters:setPreviewSize	(II)V
    //   193: getstatic 352	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   196: ifeq +53 -> 249
    //   199: new 354	java/lang/StringBuilder
    //   202: astore 6
    //   204: aload 6
    //   206: invokespecial 355	java/lang/StringBuilder:<init>	()V
    //   209: aload 6
    //   211: ldc_w 375
    //   214: invokevirtual 361	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   217: aload_0
    //   218: getfield 55	org/telegram/messenger/camera/CameraSession:pictureSize	Lorg/telegram/messenger/camera/Size;
    //   221: invokevirtual 262	org/telegram/messenger/camera/Size:getWidth	()I
    //   224: invokevirtual 364	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   227: ldc_w 366
    //   230: invokevirtual 361	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   233: aload_0
    //   234: getfield 55	org/telegram/messenger/camera/CameraSession:pictureSize	Lorg/telegram/messenger/camera/Size;
    //   237: invokevirtual 265	org/telegram/messenger/camera/Size:getHeight	()I
    //   240: invokevirtual 364	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   243: invokevirtual 370	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   246: invokestatic 373	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   249: aload 5
    //   251: aload_0
    //   252: getfield 55	org/telegram/messenger/camera/CameraSession:pictureSize	Lorg/telegram/messenger/camera/Size;
    //   255: invokevirtual 262	org/telegram/messenger/camera/Size:getWidth	()I
    //   258: aload_0
    //   259: getfield 55	org/telegram/messenger/camera/CameraSession:pictureSize	Lorg/telegram/messenger/camera/Size;
    //   262: invokevirtual 265	org/telegram/messenger/camera/Size:getHeight	()I
    //   265: invokevirtual 274	android/hardware/Camera$Parameters:setPictureSize	(II)V
    //   268: aload 5
    //   270: aload_0
    //   271: getfield 57	org/telegram/messenger/camera/CameraSession:pictureFormat	I
    //   274: invokevirtual 277	android/hardware/Camera$Parameters:setPictureFormat	(I)V
    //   277: aload 5
    //   279: iconst_1
    //   280: invokevirtual 379	android/hardware/Camera$Parameters:setRecordingHint	(Z)V
    //   283: aload 5
    //   285: invokevirtual 281	android/hardware/Camera$Parameters:getSupportedFocusModes	()Ljava/util/List;
    //   288: ldc_w 381
    //   291: invokeinterface 286 2 0
    //   296: ifeq +11 -> 307
    //   299: aload 5
    //   301: ldc_w 381
    //   304: invokevirtual 289	android/hardware/Camera$Parameters:setFocusMode	(Ljava/lang/String;)V
    //   307: iconst_0
    //   308: istore 8
    //   310: aload_0
    //   311: getfield 116	org/telegram/messenger/camera/CameraSession:jpegOrientation	I
    //   314: iconst_m1
    //   315: if_icmpeq +32 -> 347
    //   318: aload 4
    //   320: getfield 156	android/hardware/Camera$CameraInfo:facing	I
    //   323: iconst_1
    //   324: if_icmpne +241 -> 565
    //   327: aload 4
    //   329: getfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   332: aload_0
    //   333: getfield 116	org/telegram/messenger/camera/CameraSession:jpegOrientation	I
    //   336: isub
    //   337: sipush 360
    //   340: iadd
    //   341: sipush 360
    //   344: irem
    //   345: istore 8
    //   347: aload 5
    //   349: iload 8
    //   351: invokevirtual 292	android/hardware/Camera$Parameters:setRotation	(I)V
    //   354: aload 4
    //   356: getfield 156	android/hardware/Camera$CameraInfo:facing	I
    //   359: iconst_1
    //   360: if_icmpne +229 -> 589
    //   363: sipush 360
    //   366: iload 7
    //   368: isub
    //   369: sipush 360
    //   372: irem
    //   373: iload 8
    //   375: if_icmpne +209 -> 584
    //   378: aload_0
    //   379: iload_2
    //   380: putfield 294	org/telegram/messenger/camera/CameraSession:sameTakePictureOrientation	Z
    //   383: aload 5
    //   385: ldc 40
    //   387: invokevirtual 297	android/hardware/Camera$Parameters:setFlashMode	(Ljava/lang/String;)V
    //   390: aload_3
    //   391: aload 5
    //   393: invokevirtual 301	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
    //   396: aload 5
    //   398: invokevirtual 304	android/hardware/Camera$Parameters:getMaxNumMeteringAreas	()I
    //   401: ifle +8 -> 409
    //   404: aload_0
    //   405: iconst_1
    //   406: putfield 306	org/telegram/messenger/camera/CameraSession:meteringAreaSupported	Z
    //   409: return
    //   410: astore 6
    //   412: aload 6
    //   414: invokestatic 312	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   417: goto -373 -> 44
    //   420: astore 5
    //   422: aload 5
    //   424: invokestatic 312	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   427: goto -18 -> 409
    //   430: iconst_0
    //   431: istore 8
    //   433: iload 7
    //   435: tableswitch	default:+29->464, 0:+80->515, 1:+86->521, 2:+93->528, 3:+101->536
    //   464: aload 4
    //   466: getfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   469: bipush 90
    //   471: irem
    //   472: ifeq +9 -> 481
    //   475: aload 4
    //   477: iconst_0
    //   478: putfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   481: aload 4
    //   483: getfield 156	android/hardware/Camera$CameraInfo:facing	I
    //   486: iconst_1
    //   487: if_icmpne +57 -> 544
    //   490: sipush 360
    //   493: aload 4
    //   495: getfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   498: iload 8
    //   500: iadd
    //   501: sipush 360
    //   504: irem
    //   505: isub
    //   506: sipush 360
    //   509: irem
    //   510: istore 8
    //   512: goto -422 -> 90
    //   515: iconst_0
    //   516: istore 8
    //   518: goto -54 -> 464
    //   521: bipush 90
    //   523: istore 8
    //   525: goto -61 -> 464
    //   528: sipush 180
    //   531: istore 8
    //   533: goto -69 -> 464
    //   536: sipush 270
    //   539: istore 8
    //   541: goto -77 -> 464
    //   544: aload 4
    //   546: getfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   549: iload 8
    //   551: isub
    //   552: sipush 360
    //   555: iadd
    //   556: sipush 360
    //   559: irem
    //   560: istore 8
    //   562: goto -50 -> 512
    //   565: aload 4
    //   567: getfield 159	android/hardware/Camera$CameraInfo:orientation	I
    //   570: aload_0
    //   571: getfield 116	org/telegram/messenger/camera/CameraSession:jpegOrientation	I
    //   574: iadd
    //   575: sipush 360
    //   578: irem
    //   579: istore 8
    //   581: goto -234 -> 347
    //   584: iconst_0
    //   585: istore_2
    //   586: goto -208 -> 378
    //   589: iload 7
    //   591: iload 8
    //   593: if_icmpne +18 -> 611
    //   596: iload_1
    //   597: istore_2
    //   598: aload_0
    //   599: iload_2
    //   600: putfield 294	org/telegram/messenger/camera/CameraSession:sameTakePictureOrientation	Z
    //   603: goto -220 -> 383
    //   606: astore 6
    //   608: goto -225 -> 383
    //   611: iconst_0
    //   612: istore_2
    //   613: goto -15 -> 598
    //   616: astore 6
    //   618: goto -222 -> 396
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	621	0	this	CameraSession
    //   1	596	1	bool1	boolean
    //   3	610	2	bool2	boolean
    //   16	375	3	localCamera	Camera
    //   24	542	4	localCameraInfo	Camera.CameraInfo
    //   32	365	5	localObject1	Object
    //   420	3	5	localThrowable	Throwable
    //   38	172	6	localObject2	Object
    //   410	3	6	localException1	Exception
    //   606	1	6	localException2	Exception
    //   616	1	6	localException3	Exception
    //   63	531	7	i	int
    //   88	506	8	j	int
    // Exception table:
    //   from	to	target	type
    //   34	40	410	java/lang/Exception
    //   4	17	420	java/lang/Throwable
    //   21	31	420	java/lang/Throwable
    //   34	40	420	java/lang/Throwable
    //   44	87	420	java/lang/Throwable
    //   90	113	420	java/lang/Throwable
    //   118	174	420	java/lang/Throwable
    //   174	249	420	java/lang/Throwable
    //   249	307	420	java/lang/Throwable
    //   310	347	420	java/lang/Throwable
    //   347	363	420	java/lang/Throwable
    //   378	383	420	java/lang/Throwable
    //   383	390	420	java/lang/Throwable
    //   390	396	420	java/lang/Throwable
    //   396	409	420	java/lang/Throwable
    //   412	417	420	java/lang/Throwable
    //   464	481	420	java/lang/Throwable
    //   481	512	420	java/lang/Throwable
    //   544	562	420	java/lang/Throwable
    //   565	581	420	java/lang/Throwable
    //   598	603	420	java/lang/Throwable
    //   347	363	606	java/lang/Exception
    //   378	383	606	java/lang/Exception
    //   598	603	606	java/lang/Exception
    //   390	396	616	java/lang/Exception
  }
  
  public void destroy()
  {
    this.initied = false;
    if (this.orientationEventListener != null)
    {
      this.orientationEventListener.disable();
      this.orientationEventListener = null;
    }
  }
  
  /* Error */
  protected void focusToRect(android.graphics.Rect paramRect1, android.graphics.Rect paramRect2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 59	org/telegram/messenger/camera/CameraSession:cameraInfo	Lorg/telegram/messenger/camera/CameraInfo;
    //   4: getfield 231	org/telegram/messenger/camera/CameraInfo:camera	Landroid/hardware/Camera;
    //   7: astore_3
    //   8: aload_3
    //   9: ifnull +128 -> 137
    //   12: aload_3
    //   13: invokevirtual 387	android/hardware/Camera:cancelAutoFocus	()V
    //   16: aconst_null
    //   17: astore 4
    //   19: aload_3
    //   20: invokevirtual 238	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
    //   23: astore 5
    //   25: aload 5
    //   27: astore 4
    //   29: aload 4
    //   31: ifnull +106 -> 137
    //   34: aload 4
    //   36: ldc_w 381
    //   39: invokevirtual 289	android/hardware/Camera$Parameters:setFocusMode	(Ljava/lang/String;)V
    //   42: new 205	java/util/ArrayList
    //   45: astore 5
    //   47: aload 5
    //   49: invokespecial 388	java/util/ArrayList:<init>	()V
    //   52: new 390	android/hardware/Camera$Area
    //   55: astore 6
    //   57: aload 6
    //   59: aload_1
    //   60: sipush 1000
    //   63: invokespecial 393	android/hardware/Camera$Area:<init>	(Landroid/graphics/Rect;I)V
    //   66: aload 5
    //   68: aload 6
    //   70: invokevirtual 396	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   73: pop
    //   74: aload 4
    //   76: aload 5
    //   78: invokevirtual 400	android/hardware/Camera$Parameters:setFocusAreas	(Ljava/util/List;)V
    //   81: aload_0
    //   82: getfield 306	org/telegram/messenger/camera/CameraSession:meteringAreaSupported	Z
    //   85: ifeq +38 -> 123
    //   88: new 205	java/util/ArrayList
    //   91: astore_1
    //   92: aload_1
    //   93: invokespecial 388	java/util/ArrayList:<init>	()V
    //   96: new 390	android/hardware/Camera$Area
    //   99: astore 5
    //   101: aload 5
    //   103: aload_2
    //   104: sipush 1000
    //   107: invokespecial 393	android/hardware/Camera$Area:<init>	(Landroid/graphics/Rect;I)V
    //   110: aload_1
    //   111: aload 5
    //   113: invokevirtual 396	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   116: pop
    //   117: aload 4
    //   119: aload_1
    //   120: invokevirtual 403	android/hardware/Camera$Parameters:setMeteringAreas	(Ljava/util/List;)V
    //   123: aload_3
    //   124: aload 4
    //   126: invokevirtual 301	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
    //   129: aload_3
    //   130: aload_0
    //   131: getfield 51	org/telegram/messenger/camera/CameraSession:autoFocusCallback	Landroid/hardware/Camera$AutoFocusCallback;
    //   134: invokevirtual 407	android/hardware/Camera:autoFocus	(Landroid/hardware/Camera$AutoFocusCallback;)V
    //   137: return
    //   138: astore 5
    //   140: aload 5
    //   142: invokestatic 312	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   145: goto -116 -> 29
    //   148: astore_1
    //   149: aload_1
    //   150: invokestatic 312	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   153: goto -16 -> 137
    //   156: astore_1
    //   157: aload_1
    //   158: invokestatic 312	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   161: goto -24 -> 137
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	164	0	this	CameraSession
    //   0	164	1	paramRect1	android.graphics.Rect
    //   0	164	2	paramRect2	android.graphics.Rect
    //   7	123	3	localCamera	Camera
    //   17	108	4	localObject1	Object
    //   23	89	5	localObject2	Object
    //   138	3	5	localException	Exception
    //   55	14	6	localArea	android.hardware.Camera.Area
    // Exception table:
    //   from	to	target	type
    //   19	25	138	java/lang/Exception
    //   0	8	148	java/lang/Exception
    //   12	16	148	java/lang/Exception
    //   34	123	148	java/lang/Exception
    //   140	145	148	java/lang/Exception
    //   157	161	148	java/lang/Exception
    //   123	137	156	java/lang/Exception
  }
  
  public String getCurrentFlashMode()
  {
    return this.currentFlashMode;
  }
  
  public int getCurrentOrientation()
  {
    return this.currentOrientation;
  }
  
  public int getDisplayOrientation()
  {
    try
    {
      Camera.CameraInfo localCameraInfo = new android/hardware/Camera$CameraInfo;
      localCameraInfo.<init>();
      Camera.getCameraInfo(this.cameraInfo.getCameraId(), localCameraInfo);
      i = getDisplayOrientation(localCameraInfo, true);
      return i;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        int i = 0;
      }
    }
  }
  
  public String getNextFlashMode()
  {
    Object localObject = CameraController.getInstance().availableFlashModes;
    int i = 0;
    if (i < ((ArrayList)localObject).size()) {
      if (((String)((ArrayList)localObject).get(i)).equals(this.currentFlashMode)) {
        if (i < ((ArrayList)localObject).size() - 1) {
          localObject = (String)((ArrayList)localObject).get(i + 1);
        }
      }
    }
    for (;;)
    {
      return (String)localObject;
      localObject = (String)((ArrayList)localObject).get(0);
      continue;
      i++;
      break;
      localObject = this.currentFlashMode;
    }
  }
  
  public int getWorldAngle()
  {
    return this.diffOrientation;
  }
  
  public boolean isInitied()
  {
    return this.initied;
  }
  
  public boolean isSameTakePictureOrientation()
  {
    return this.sameTakePictureOrientation;
  }
  
  public void setCurrentFlashMode(String paramString)
  {
    this.currentFlashMode = paramString;
    configurePhotoCamera();
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit();
    if (this.cameraInfo.frontCamera != 0) {}
    for (String str = "flashMode_front";; str = "flashMode")
    {
      localEditor.putString(str, paramString).commit();
      return;
    }
  }
  
  public void setInitied()
  {
    this.initied = true;
  }
  
  protected void stopVideoRecording()
  {
    this.isVideo = false;
    configurePhotoCamera();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/camera/CameraSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */