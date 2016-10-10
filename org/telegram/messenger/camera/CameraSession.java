package org.telegram.messenger.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;

public class CameraSession
{
  protected CameraInfo cameraInfo;
  private String currentFlashMode = "off";
  private boolean initied;
  private boolean isVideo;
  private int lastOrientation = -1;
  private OrientationEventListener orientationEventListener;
  private final int pictureFormat;
  private final Size pictureSize;
  private final Size previewSize;
  
  public CameraSession(CameraInfo paramCameraInfo, Size paramSize1, Size paramSize2, int paramInt)
  {
    this.previewSize = paramSize1;
    this.pictureSize = paramSize2;
    this.pictureFormat = paramInt;
    this.cameraInfo = paramCameraInfo;
    paramSize1 = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0);
    if (this.cameraInfo.frontCamera != 0) {}
    for (paramCameraInfo = "flashMode_front";; paramCameraInfo = "flashMode")
    {
      this.currentFlashMode = paramSize1.getString(paramCameraInfo, "off");
      this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext)
      {
        public void onOrientationChanged(int paramAnonymousInt)
        {
          if ((CameraSession.this.orientationEventListener == null) || (!CameraSession.this.initied)) {}
          do
          {
            return;
            paramAnonymousInt = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
          } while (CameraSession.this.lastOrientation == paramAnonymousInt);
          if (!CameraSession.this.isVideo) {
            CameraSession.this.configurePhotoCamera();
          }
          CameraSession.access$202(CameraSession.this, paramAnonymousInt);
        }
      };
      if (!this.orientationEventListener.canDetectOrientation()) {
        break;
      }
      this.orientationEventListener.enable();
      return;
    }
    this.orientationEventListener.disable();
    this.orientationEventListener = null;
  }
  
  private int getDisplayOrientation(Camera.CameraInfo paramCameraInfo, boolean paramBoolean)
  {
    int j = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
    int i = 0;
    switch (j)
    {
    }
    while (paramCameraInfo.facing == 1)
    {
      j = (360 - (paramCameraInfo.orientation + i) % 360) % 360;
      i = j;
      if (!paramBoolean)
      {
        i = j;
        if (j == 90) {
          i = 270;
        }
      }
      j = i;
      if (!paramBoolean)
      {
        j = i;
        if ("Huawei".equals(Build.MANUFACTURER))
        {
          j = i;
          if ("angler".equals(Build.PRODUCT))
          {
            j = i;
            if (i == 270) {
              j = 90;
            }
          }
        }
      }
      return j;
      i = 0;
      continue;
      i = 90;
      continue;
      i = 180;
      continue;
      i = 270;
    }
    return (paramCameraInfo.orientation - i + 360) % 360;
  }
  
  private int getHigh()
  {
    if (("LGE".equals(Build.MANUFACTURER)) && ("g3_tmo_us".equals(Build.PRODUCT))) {
      return 4;
    }
    return 1;
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
      return;
    }
  }
  
  /* Error */
  protected void configurePhotoCamera()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 40	org/telegram/messenger/camera/CameraSession:cameraInfo	Lorg/telegram/messenger/camera/CameraInfo;
    //   4: getfield 191	org/telegram/messenger/camera/CameraInfo:camera	Landroid/hardware/Camera;
    //   7: astore 5
    //   9: aload 5
    //   11: ifnull +180 -> 191
    //   14: new 122	android/hardware/Camera$CameraInfo
    //   17: dup
    //   18: invokespecial 192	android/hardware/Camera$CameraInfo:<init>	()V
    //   21: astore 6
    //   23: aconst_null
    //   24: astore_3
    //   25: aload 5
    //   27: invokevirtual 198	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
    //   30: astore 4
    //   32: aload 4
    //   34: astore_3
    //   35: aload_0
    //   36: getfield 40	org/telegram/messenger/camera/CameraSession:cameraInfo	Lorg/telegram/messenger/camera/CameraInfo;
    //   39: invokevirtual 201	org/telegram/messenger/camera/CameraInfo:getCameraId	()I
    //   42: aload 6
    //   44: invokestatic 205	android/hardware/Camera:getCameraInfo	(ILandroid/hardware/Camera$CameraInfo;)V
    //   47: aload_0
    //   48: aload 6
    //   50: iconst_1
    //   51: invokespecial 207	org/telegram/messenger/camera/CameraSession:getDisplayOrientation	(Landroid/hardware/Camera$CameraInfo;Z)I
    //   54: istore_2
    //   55: ldc -47
    //   57: getstatic 135	android/os/Build:MANUFACTURER	Ljava/lang/String;
    //   60: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   63: ifeq +151 -> 214
    //   66: ldc -45
    //   68: getstatic 146	android/os/Build:PRODUCT	Ljava/lang/String;
    //   71: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   74: ifeq +140 -> 214
    //   77: iconst_0
    //   78: istore_1
    //   79: aload 5
    //   81: iload_1
    //   82: invokevirtual 215	android/hardware/Camera:setDisplayOrientation	(I)V
    //   85: aload_3
    //   86: ifnull +105 -> 191
    //   89: aload_3
    //   90: aload_0
    //   91: getfield 34	org/telegram/messenger/camera/CameraSession:previewSize	Lorg/telegram/messenger/camera/Size;
    //   94: invokevirtual 220	org/telegram/messenger/camera/Size:getWidth	()I
    //   97: aload_0
    //   98: getfield 34	org/telegram/messenger/camera/CameraSession:previewSize	Lorg/telegram/messenger/camera/Size;
    //   101: invokevirtual 223	org/telegram/messenger/camera/Size:getHeight	()I
    //   104: invokevirtual 229	android/hardware/Camera$Parameters:setPreviewSize	(II)V
    //   107: aload_3
    //   108: aload_0
    //   109: getfield 36	org/telegram/messenger/camera/CameraSession:pictureSize	Lorg/telegram/messenger/camera/Size;
    //   112: invokevirtual 220	org/telegram/messenger/camera/Size:getWidth	()I
    //   115: aload_0
    //   116: getfield 36	org/telegram/messenger/camera/CameraSession:pictureSize	Lorg/telegram/messenger/camera/Size;
    //   119: invokevirtual 223	org/telegram/messenger/camera/Size:getHeight	()I
    //   122: invokevirtual 232	android/hardware/Camera$Parameters:setPictureSize	(II)V
    //   125: aload_3
    //   126: aload_0
    //   127: getfield 38	org/telegram/messenger/camera/CameraSession:pictureFormat	I
    //   130: invokevirtual 235	android/hardware/Camera$Parameters:setPictureFormat	(I)V
    //   133: aload_3
    //   134: invokevirtual 239	android/hardware/Camera$Parameters:getSupportedFocusModes	()Ljava/util/List;
    //   137: ldc -15
    //   139: invokeinterface 244 2 0
    //   144: ifeq +9 -> 153
    //   147: aload_3
    //   148: ldc -15
    //   150: invokevirtual 247	android/hardware/Camera$Parameters:setFocusMode	(Ljava/lang/String;)V
    //   153: aload 6
    //   155: getfield 125	android/hardware/Camera$CameraInfo:facing	I
    //   158: iconst_1
    //   159: if_icmpne +157 -> 316
    //   162: sipush 360
    //   165: iload_2
    //   166: isub
    //   167: sipush 360
    //   170: irem
    //   171: istore_1
    //   172: aload_3
    //   173: iload_1
    //   174: invokevirtual 250	android/hardware/Camera$Parameters:setRotation	(I)V
    //   177: aload_3
    //   178: aload_0
    //   179: getfield 30	org/telegram/messenger/camera/CameraSession:currentFlashMode	Ljava/lang/String;
    //   182: invokevirtual 253	android/hardware/Camera$Parameters:setFlashMode	(Ljava/lang/String;)V
    //   185: aload 5
    //   187: aload_3
    //   188: invokevirtual 257	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
    //   191: return
    //   192: astore 4
    //   194: ldc_w 259
    //   197: aload 4
    //   199: invokestatic 265	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   202: goto -167 -> 35
    //   205: astore_3
    //   206: ldc_w 259
    //   209: aload_3
    //   210: invokestatic 265	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   213: return
    //   214: iconst_0
    //   215: istore_1
    //   216: iload_2
    //   217: tableswitch	default:+31->248, 0:+114->331, 1:+119->336, 2:+125->342, 3:+132->349
    //   248: aload 6
    //   250: getfield 128	android/hardware/Camera$CameraInfo:orientation	I
    //   253: bipush 90
    //   255: irem
    //   256: ifeq +9 -> 265
    //   259: aload 6
    //   261: iconst_0
    //   262: putfield 128	android/hardware/Camera$CameraInfo:orientation	I
    //   265: aload 6
    //   267: getfield 125	android/hardware/Camera$CameraInfo:facing	I
    //   270: iconst_1
    //   271: if_icmpne +26 -> 297
    //   274: sipush 360
    //   277: aload 6
    //   279: getfield 128	android/hardware/Camera$CameraInfo:orientation	I
    //   282: iload_1
    //   283: iadd
    //   284: sipush 360
    //   287: irem
    //   288: isub
    //   289: sipush 360
    //   292: irem
    //   293: istore_1
    //   294: goto +34 -> 328
    //   297: aload 6
    //   299: getfield 128	android/hardware/Camera$CameraInfo:orientation	I
    //   302: iload_1
    //   303: isub
    //   304: sipush 360
    //   307: iadd
    //   308: sipush 360
    //   311: irem
    //   312: istore_1
    //   313: goto +15 -> 328
    //   316: iload_2
    //   317: istore_1
    //   318: goto -146 -> 172
    //   321: astore 4
    //   323: goto -146 -> 177
    //   326: astore_3
    //   327: return
    //   328: goto -249 -> 79
    //   331: iconst_0
    //   332: istore_1
    //   333: goto -85 -> 248
    //   336: bipush 90
    //   338: istore_1
    //   339: goto -91 -> 248
    //   342: sipush 180
    //   345: istore_1
    //   346: goto -98 -> 248
    //   349: sipush 270
    //   352: istore_1
    //   353: goto -105 -> 248
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	356	0	this	CameraSession
    //   78	275	1	i	int
    //   54	263	2	j	int
    //   24	164	3	localObject	Object
    //   205	5	3	localThrowable	Throwable
    //   326	1	3	localException1	Exception
    //   30	3	4	localParameters	android.hardware.Camera.Parameters
    //   192	6	4	localException2	Exception
    //   321	1	4	localException3	Exception
    //   7	179	5	localCamera	Camera
    //   21	277	6	localCameraInfo	Camera.CameraInfo
    // Exception table:
    //   from	to	target	type
    //   25	32	192	java/lang/Exception
    //   0	9	205	java/lang/Throwable
    //   14	23	205	java/lang/Throwable
    //   25	32	205	java/lang/Throwable
    //   35	77	205	java/lang/Throwable
    //   79	85	205	java/lang/Throwable
    //   89	153	205	java/lang/Throwable
    //   153	162	205	java/lang/Throwable
    //   172	177	205	java/lang/Throwable
    //   177	185	205	java/lang/Throwable
    //   185	191	205	java/lang/Throwable
    //   194	202	205	java/lang/Throwable
    //   248	265	205	java/lang/Throwable
    //   265	294	205	java/lang/Throwable
    //   297	313	205	java/lang/Throwable
    //   172	177	321	java/lang/Exception
    //   185	191	326	java/lang/Exception
  }
  
  protected void configureRecorder(int paramInt, MediaRecorder paramMediaRecorder)
  {
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    Camera.getCameraInfo(this.cameraInfo.cameraId, localCameraInfo);
    paramMediaRecorder.setOrientationHint(getDisplayOrientation(localCameraInfo, false));
    int i = getHigh();
    boolean bool1 = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, i);
    boolean bool2 = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, 0);
    if ((bool1) && ((paramInt == 1) || (!bool2))) {
      paramMediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, i));
    }
    for (;;)
    {
      this.isVideo = true;
      return;
      if (!bool2) {
        break;
      }
      paramMediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, 0));
    }
    throw new IllegalStateException("cannot find valid CamcorderProfile");
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
  
  public String getCurrentFlashMode()
  {
    return this.currentFlashMode;
  }
  
  public String getNextFlashMode()
  {
    ArrayList localArrayList = CameraController.getInstance().availableFlashModes;
    int i = 0;
    while (i < localArrayList.size())
    {
      if (((String)localArrayList.get(i)).equals(this.currentFlashMode))
      {
        if (i < localArrayList.size() - 1) {
          return (String)localArrayList.get(i + 1);
        }
        return (String)localArrayList.get(0);
      }
      i += 1;
    }
    return this.currentFlashMode;
  }
  
  protected boolean isInitied()
  {
    return this.initied;
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
  
  protected void setInitied()
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