package org.telegram.messenger.camera;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;

public class CameraController
  implements MediaRecorder.OnInfoListener
{
  private static final int CORE_POOL_SIZE = 1;
  private static volatile CameraController Instance = null;
  private static final int KEEP_ALIVE_SECONDS = 60;
  private static final int MAX_POOL_SIZE = 1;
  protected ArrayList<String> availableFlashModes = new ArrayList();
  protected ArrayList<CameraInfo> cameraInfos = null;
  private boolean cameraInitied;
  private boolean loadingCameras;
  private VideoTakeCallback onVideoTakeCallback;
  private String recordedFile;
  private MediaRecorder recorder;
  private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
  
  public static Size chooseOptimalSize(List<Size> paramList, int paramInt1, int paramInt2, Size paramSize)
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramSize.getWidth();
    int j = paramSize.getHeight();
    for (int k = 0; k < paramList.size(); k++)
    {
      paramSize = (Size)paramList.get(k);
      if ((paramSize.getHeight() == paramSize.getWidth() * j / i) && (paramSize.getWidth() >= paramInt1) && (paramSize.getHeight() >= paramInt2)) {
        localArrayList.add(paramSize);
      }
    }
    if (localArrayList.size() > 0) {}
    for (paramList = (Size)Collections.min(localArrayList, new CompareSizesByArea());; paramList = (Size)Collections.max(paramList, new CompareSizesByArea())) {
      return paramList;
    }
  }
  
  /* Error */
  private void finishRecordingVideo()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aconst_null
    //   3: astore_2
    //   4: lconst_0
    //   5: lstore_3
    //   6: aload_1
    //   7: astore 5
    //   9: new 176	android/media/MediaMetadataRetriever
    //   12: astore 6
    //   14: aload_1
    //   15: astore 5
    //   17: aload 6
    //   19: invokespecial 177	android/media/MediaMetadataRetriever:<init>	()V
    //   22: aload 6
    //   24: aload_0
    //   25: getfield 127	org/telegram/messenger/camera/CameraController:recordedFile	Ljava/lang/String;
    //   28: invokevirtual 181	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   31: aload 6
    //   33: bipush 9
    //   35: invokevirtual 185	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   38: astore 5
    //   40: lload_3
    //   41: lstore 7
    //   43: aload 5
    //   45: ifnull +24 -> 69
    //   48: aload 5
    //   50: invokestatic 191	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   53: l2f
    //   54: ldc -64
    //   56: fdiv
    //   57: f2d
    //   58: invokestatic 198	java/lang/Math:ceil	(D)D
    //   61: dstore 9
    //   63: dload 9
    //   65: d2i
    //   66: i2l
    //   67: lstore 7
    //   69: aload 6
    //   71: ifnull +8 -> 79
    //   74: aload 6
    //   76: invokevirtual 201	android/media/MediaMetadataRetriever:release	()V
    //   79: aload_0
    //   80: getfield 127	org/telegram/messenger/camera/CameraController:recordedFile	Ljava/lang/String;
    //   83: iconst_1
    //   84: invokestatic 207	android/media/ThumbnailUtils:createVideoThumbnail	(Ljava/lang/String;I)Landroid/graphics/Bitmap;
    //   87: astore 5
    //   89: new 209	java/lang/StringBuilder
    //   92: dup
    //   93: invokespecial 210	java/lang/StringBuilder:<init>	()V
    //   96: ldc -44
    //   98: invokevirtual 216	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   101: invokestatic 221	org/telegram/messenger/SharedConfig:getLastLocalId	()I
    //   104: invokevirtual 224	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   107: ldc -30
    //   109: invokevirtual 216	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: invokevirtual 230	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   115: astore 6
    //   117: new 232	java/io/File
    //   120: dup
    //   121: iconst_4
    //   122: invokestatic 238	org/telegram/messenger/FileLoader:getDirectory	(I)Ljava/io/File;
    //   125: aload 6
    //   127: invokespecial 241	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   130: astore 6
    //   132: new 243	java/io/FileOutputStream
    //   135: astore_1
    //   136: aload_1
    //   137: aload 6
    //   139: invokespecial 246	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   142: aload 5
    //   144: getstatic 252	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   147: bipush 55
    //   149: aload_1
    //   150: invokevirtual 258	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   153: pop
    //   154: invokestatic 261	org/telegram/messenger/SharedConfig:saveConfig	()V
    //   157: new 16	org/telegram/messenger/camera/CameraController$10
    //   160: dup
    //   161: aload_0
    //   162: aload 6
    //   164: aload 5
    //   166: lload 7
    //   168: invokespecial 264	org/telegram/messenger/camera/CameraController$10:<init>	(Lorg/telegram/messenger/camera/CameraController;Ljava/io/File;Landroid/graphics/Bitmap;J)V
    //   171: invokestatic 270	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
    //   174: return
    //   175: astore 5
    //   177: aload 5
    //   179: invokestatic 276	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   182: goto -103 -> 79
    //   185: astore_1
    //   186: aload_2
    //   187: astore 6
    //   189: aload 6
    //   191: astore 5
    //   193: aload_1
    //   194: invokestatic 276	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   197: lload_3
    //   198: lstore 7
    //   200: aload 6
    //   202: ifnull -123 -> 79
    //   205: aload 6
    //   207: invokevirtual 201	android/media/MediaMetadataRetriever:release	()V
    //   210: lload_3
    //   211: lstore 7
    //   213: goto -134 -> 79
    //   216: astore 5
    //   218: aload 5
    //   220: invokestatic 276	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   223: lload_3
    //   224: lstore 7
    //   226: goto -147 -> 79
    //   229: astore 6
    //   231: aload 5
    //   233: astore_1
    //   234: aload 6
    //   236: astore 5
    //   238: aload_1
    //   239: ifnull +7 -> 246
    //   242: aload_1
    //   243: invokevirtual 201	android/media/MediaMetadataRetriever:release	()V
    //   246: aload 5
    //   248: athrow
    //   249: astore 6
    //   251: aload 6
    //   253: invokestatic 276	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   256: goto -10 -> 246
    //   259: astore_1
    //   260: aload_1
    //   261: invokestatic 276	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   264: goto -110 -> 154
    //   267: astore 5
    //   269: aload 6
    //   271: astore_1
    //   272: goto -34 -> 238
    //   275: astore_1
    //   276: goto -87 -> 189
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	279	0	this	CameraController
    //   1	149	1	localFileOutputStream	java.io.FileOutputStream
    //   185	9	1	localException1	Exception
    //   233	10	1	localObject1	Object
    //   259	2	1	localThrowable	Throwable
    //   271	1	1	localObject2	Object
    //   275	1	1	localException2	Exception
    //   3	184	2	localObject3	Object
    //   5	219	3	l1	long
    //   7	158	5	localObject4	Object
    //   175	3	5	localException3	Exception
    //   191	1	5	localObject5	Object
    //   216	16	5	localException4	Exception
    //   236	11	5	localObject6	Object
    //   267	1	5	localObject7	Object
    //   12	194	6	localObject8	Object
    //   229	6	6	localObject9	Object
    //   249	21	6	localException5	Exception
    //   41	184	7	l2	long
    //   61	3	9	d	double
    // Exception table:
    //   from	to	target	type
    //   74	79	175	java/lang/Exception
    //   9	14	185	java/lang/Exception
    //   17	22	185	java/lang/Exception
    //   205	210	216	java/lang/Exception
    //   9	14	229	finally
    //   17	22	229	finally
    //   193	197	229	finally
    //   242	246	249	java/lang/Exception
    //   132	154	259	java/lang/Throwable
    //   22	40	267	finally
    //   48	63	267	finally
    //   22	40	275	java/lang/Exception
    //   48	63	275	java/lang/Exception
  }
  
  /* Error */
  public static CameraController getInstance()
  {
    // Byte code:
    //   0: getstatic 70	org/telegram/messenger/camera/CameraController:Instance	Lorg/telegram/messenger/camera/CameraController;
    //   3: astore_0
    //   4: aload_0
    //   5: astore_1
    //   6: aload_0
    //   7: ifnonnull +31 -> 38
    //   10: ldc 2
    //   12: monitorenter
    //   13: getstatic 70	org/telegram/messenger/camera/CameraController:Instance	Lorg/telegram/messenger/camera/CameraController;
    //   16: astore_0
    //   17: aload_0
    //   18: astore_1
    //   19: aload_0
    //   20: ifnonnull +15 -> 35
    //   23: new 2	org/telegram/messenger/camera/CameraController
    //   26: astore_1
    //   27: aload_1
    //   28: invokespecial 279	org/telegram/messenger/camera/CameraController:<init>	()V
    //   31: aload_1
    //   32: putstatic 70	org/telegram/messenger/camera/CameraController:Instance	Lorg/telegram/messenger/camera/CameraController;
    //   35: ldc 2
    //   37: monitorexit
    //   38: aload_1
    //   39: areturn
    //   40: astore_1
    //   41: ldc 2
    //   43: monitorexit
    //   44: aload_1
    //   45: athrow
    //   46: astore_1
    //   47: goto -6 -> 41
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	17	0	localCameraController1	CameraController
    //   5	34	1	localCameraController2	CameraController
    //   40	5	1	localObject1	Object
    //   46	1	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   13	17	40	finally
    //   23	31	40	finally
    //   35	38	40	finally
    //   41	44	40	finally
    //   31	35	46	finally
  }
  
  private static int getOrientation(byte[] paramArrayOfByte)
  {
    boolean bool = true;
    int i = 0;
    if (paramArrayOfByte == null)
    {
      j = i;
      return j;
    }
    int j = 0;
    label14:
    int k;
    int i1;
    int i2;
    do
    {
      do
      {
        for (;;)
        {
          k = 0;
          m = k;
          n = j;
          if (j + 3 >= paramArrayOfByte.length) {
            break label132;
          }
          n = j + 1;
          if ((paramArrayOfByte[j] & 0xFF) != 255) {
            break label463;
          }
          i1 = paramArrayOfByte[n] & 0xFF;
          if (i1 != 255) {
            break;
          }
          j = n;
        }
        i2 = n + 1;
        j = i2;
      } while (i1 == 216);
      j = i2;
    } while (i1 == 1);
    int m = k;
    int n = i2;
    if (i1 != 217)
    {
      if (i1 != 218) {
        break label336;
      }
      n = i2;
      m = k;
    }
    for (;;)
    {
      label132:
      j = i;
      if (m <= 8) {
        break;
      }
      i2 = pack(paramArrayOfByte, n, 4, false);
      if (i2 != NUM)
      {
        j = i;
        if (i2 != NUM) {
          break;
        }
      }
      if (i2 == NUM)
      {
        i2 = pack(paramArrayOfByte, n + 4, 4, bool) + 2;
        j = i;
        if (i2 < 10) {
          break;
        }
        j = i;
        if (i2 > m) {
          break;
        }
        n += i2;
        i2 = m - i2;
      }
      for (m = pack(paramArrayOfByte, n - 2, 2, bool);; m--)
      {
        j = i;
        if (m <= 0) {
          break;
        }
        j = i;
        if (i2 < 12) {
          break;
        }
        if (pack(paramArrayOfByte, n, 2, bool) == 274)
        {
          j = i;
          switch (pack(paramArrayOfByte, n + 8, 2, bool))
          {
          case 1: 
          case 2: 
          case 4: 
          case 5: 
          case 7: 
          default: 
            j = i;
            break;
          case 3: 
            j = 180;
            break;
            m = pack(paramArrayOfByte, i2, 2, false);
            j = i;
            if (m < 2) {
              break;
            }
            j = i;
            if (i2 + m > paramArrayOfByte.length) {
              break;
            }
            if ((i1 == 225) && (m >= 8) && (pack(paramArrayOfByte, i2 + 2, 4, false) == NUM) && (pack(paramArrayOfByte, i2 + 6, 2, false) == 0))
            {
              n = i2 + 8;
              m -= 8;
              break label132;
            }
            j = i2 + m;
            break label14;
            bool = false;
            break;
          case 6: 
            j = 90;
            break;
          case 8: 
            label336:
            j = 270;
            break;
          }
        }
        n += 12;
        i2 -= 12;
      }
      label463:
      m = k;
    }
  }
  
  private static int pack(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = 1;
    int j = paramInt1;
    if (paramBoolean)
    {
      j = paramInt1 + (paramInt2 - 1);
      i = -1;
    }
    int k = 0;
    paramInt1 = paramInt2;
    paramInt2 = k;
    while (paramInt1 > 0)
    {
      paramInt2 = paramInt2 << 8 | paramArrayOfByte[j] & 0xFF;
      j += i;
      paramInt1--;
    }
    return paramInt2;
  }
  
  public void cleanup()
  {
    this.threadPool.execute(new Runnable()
    {
      public void run()
      {
        if ((CameraController.this.cameraInfos == null) || (CameraController.this.cameraInfos.isEmpty())) {}
        for (;;)
        {
          return;
          for (int i = 0; i < CameraController.this.cameraInfos.size(); i++)
          {
            CameraInfo localCameraInfo = (CameraInfo)CameraController.this.cameraInfos.get(i);
            if (localCameraInfo.camera != null)
            {
              localCameraInfo.camera.stopPreview();
              localCameraInfo.camera.setPreviewCallbackWithBuffer(null);
              localCameraInfo.camera.release();
              localCameraInfo.camera = null;
            }
          }
          CameraController.this.cameraInfos = null;
        }
      }
    });
  }
  
  public void close(final CameraSession paramCameraSession, final CountDownLatch paramCountDownLatch, final Runnable paramRunnable)
  {
    paramCameraSession.destroy();
    this.threadPool.execute(new Runnable()
    {
      public void run()
      {
        if (paramRunnable != null) {
          paramRunnable.run();
        }
        if (paramCameraSession.cameraInfo.camera == null) {}
        for (;;)
        {
          return;
          try
          {
            paramCameraSession.cameraInfo.camera.stopPreview();
            paramCameraSession.cameraInfo.camera.setPreviewCallbackWithBuffer(null);
          }
          catch (Exception localException1)
          {
            try
            {
              for (;;)
              {
                paramCameraSession.cameraInfo.camera.release();
                paramCameraSession.cameraInfo.camera = null;
                if (paramCountDownLatch == null) {
                  break;
                }
                paramCountDownLatch.countDown();
                break;
                localException1 = localException1;
                FileLog.e(localException1);
              }
            }
            catch (Exception localException2)
            {
              for (;;)
              {
                FileLog.e(localException2);
              }
            }
          }
        }
      }
    });
    if (paramCountDownLatch != null) {}
    try
    {
      paramCountDownLatch.await();
      return;
    }
    catch (Exception paramCameraSession)
    {
      for (;;)
      {
        FileLog.e(paramCameraSession);
      }
    }
  }
  
  public ArrayList<CameraInfo> getCameras()
  {
    return this.cameraInfos;
  }
  
  public void initCamera()
  {
    if ((this.loadingCameras) || (this.cameraInitied)) {}
    for (;;)
    {
      return;
      this.loadingCameras = true;
      this.threadPool.execute(new Runnable()
      {
        public void run()
        {
          ArrayList localArrayList1;
          int j;
          Object localObject1;
          Object localObject2;
          Object localObject3;
          int k;
          Camera.Size localSize;
          Object localObject4;
          try
          {
            if (CameraController.this.cameraInfos == null)
            {
              int i = Camera.getNumberOfCameras();
              localArrayList1 = new java/util/ArrayList;
              localArrayList1.<init>();
              Camera.CameraInfo localCameraInfo = new android/hardware/Camera$CameraInfo;
              localCameraInfo.<init>();
              j = 0;
              if (j < i)
              {
                Camera.getCameraInfo(j, localCameraInfo);
                CameraInfo localCameraInfo1 = new org/telegram/messenger/camera/CameraInfo;
                localCameraInfo1.<init>(j, localCameraInfo);
                localObject1 = Camera.open(localCameraInfo1.getCameraId());
                localObject2 = ((Camera)localObject1).getParameters();
                localObject3 = ((Camera.Parameters)localObject2).getSupportedPreviewSizes();
                k = 0;
                if (k < ((List)localObject3).size())
                {
                  localSize = (Camera.Size)((List)localObject3).get(k);
                  if ((localSize.width == 1280) && (localSize.height != 720)) {}
                  for (;;)
                  {
                    k++;
                    break;
                    if ((localSize.height < 2160) && (localSize.width < 2160))
                    {
                      ArrayList localArrayList2 = localCameraInfo1.previewSizes;
                      localObject4 = new org/telegram/messenger/camera/Size;
                      ((Size)localObject4).<init>(localSize.width, localSize.height);
                      localArrayList2.add(localObject4);
                      if (BuildVars.LOGS_ENABLED)
                      {
                        localObject4 = new java/lang/StringBuilder;
                        ((StringBuilder)localObject4).<init>();
                        FileLog.d("preview size = " + localSize.width + " " + localSize.height);
                      }
                    }
                  }
                  return;
                }
              }
            }
          }
          catch (Exception localException)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                CameraController.access$002(CameraController.this, false);
                CameraController.access$102(CameraController.this, false);
              }
            });
            FileLog.e(localException);
          }
          for (;;)
          {
            localObject2 = ((Camera.Parameters)localObject2).getSupportedPictureSizes();
            k = 0;
            if (k < ((List)localObject2).size())
            {
              localSize = (Camera.Size)((List)localObject2).get(k);
              if ((localSize.width == 1280) && (localSize.height != 720)) {}
              for (;;)
              {
                k++;
                break;
                if ((!"samsung".equals(Build.MANUFACTURER)) || (!"jflteuc".equals(Build.PRODUCT)) || (localSize.width < 2048))
                {
                  localObject4 = localException.pictureSizes;
                  localObject3 = new org/telegram/messenger/camera/Size;
                  ((Size)localObject3).<init>(localSize.width, localSize.height);
                  ((ArrayList)localObject4).add(localObject3);
                  if (BuildVars.LOGS_ENABLED)
                  {
                    localObject3 = new java/lang/StringBuilder;
                    ((StringBuilder)localObject3).<init>();
                    FileLog.d("picture size = " + localSize.width + " " + localSize.height);
                  }
                }
              }
            }
            ((Camera)localObject1).release();
            localArrayList1.add(localException);
            localObject1 = new org/telegram/messenger/camera/CameraController$1$1;
            ((1)localObject1).<init>(this);
            Collections.sort(localException.previewSizes, (Comparator)localObject1);
            Collections.sort(localException.pictureSizes, (Comparator)localObject1);
            j++;
            break;
            CameraController.this.cameraInfos = localArrayList1;
            Runnable local2 = new org/telegram/messenger/camera/CameraController$1$2;
            local2.<init>(this);
            AndroidUtilities.runOnUIThread(local2);
          }
        }
      });
    }
  }
  
  public boolean isCameraInitied()
  {
    if ((this.cameraInitied) && (this.cameraInfos != null) && (!this.cameraInfos.isEmpty())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void onInfo(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 800) || (paramInt1 == 801) || (paramInt1 == 1))
    {
      paramMediaRecorder = this.recorder;
      this.recorder = null;
      if (paramMediaRecorder != null)
      {
        paramMediaRecorder.stop();
        paramMediaRecorder.release();
      }
      if (this.onVideoTakeCallback != null) {
        finishRecordingVideo();
      }
    }
  }
  
  public void open(final CameraSession paramCameraSession, final SurfaceTexture paramSurfaceTexture, final Runnable paramRunnable1, final Runnable paramRunnable2)
  {
    if ((paramCameraSession == null) || (paramSurfaceTexture == null)) {}
    for (;;)
    {
      return;
      this.threadPool.execute(new Runnable()
      {
        @SuppressLint({"NewApi"})
        public void run()
        {
          Object localObject1 = paramCameraSession.cameraInfo.camera;
          Object localObject2 = localObject1;
          Object localObject3;
          if (localObject1 == null) {
            localObject3 = localObject1;
          }
          try
          {
            Object localObject4 = paramCameraSession.cameraInfo;
            localObject3 = localObject1;
            localObject2 = Camera.open(paramCameraSession.cameraInfo.cameraId);
            localObject3 = localObject1;
            ((CameraInfo)localObject4).camera = ((Camera)localObject2);
            localObject3 = localObject2;
            localObject4 = ((Camera)localObject2).getParameters().getSupportedFlashModes();
            localObject3 = localObject2;
            CameraController.this.availableFlashModes.clear();
            if (localObject4 != null)
            {
              for (int i = 0;; i++)
              {
                localObject3 = localObject2;
                if (i >= ((List)localObject4).size()) {
                  break;
                }
                localObject3 = localObject2;
                localObject1 = (String)((List)localObject4).get(i);
                localObject3 = localObject2;
                if (!((String)localObject1).equals("off"))
                {
                  localObject3 = localObject2;
                  if (!((String)localObject1).equals("on"))
                  {
                    localObject3 = localObject2;
                    if (!((String)localObject1).equals("auto")) {
                      continue;
                    }
                  }
                }
                localObject3 = localObject2;
                CameraController.this.availableFlashModes.add(localObject1);
              }
              localObject3 = localObject2;
              paramCameraSession.checkFlashMode((String)CameraController.this.availableFlashModes.get(0));
            }
            localObject3 = localObject2;
            if (paramRunnable2 != null)
            {
              localObject3 = localObject2;
              paramRunnable2.run();
            }
            localObject3 = localObject2;
            paramCameraSession.configurePhotoCamera();
            localObject3 = localObject2;
            ((Camera)localObject2).setPreviewTexture(paramSurfaceTexture);
            localObject3 = localObject2;
            ((Camera)localObject2).startPreview();
            localObject3 = localObject2;
            if (paramRunnable1 != null)
            {
              localObject3 = localObject2;
              AndroidUtilities.runOnUIThread(paramRunnable1);
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              paramCameraSession.cameraInfo.camera = null;
              if (localObject3 != null) {
                ((Camera)localObject3).release();
              }
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void openRound(final CameraSession paramCameraSession, final SurfaceTexture paramSurfaceTexture, final Runnable paramRunnable1, final Runnable paramRunnable2)
  {
    if ((paramCameraSession == null) || (paramSurfaceTexture == null)) {
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("failed to open round " + paramCameraSession + " tex = " + paramSurfaceTexture);
      }
    }
    for (;;)
    {
      return;
      this.threadPool.execute(new Runnable()
      {
        @SuppressLint({"NewApi"})
        public void run()
        {
          Camera localCamera1 = paramCameraSession.cameraInfo.camera;
          Object localObject = localCamera1;
          try
          {
            if (BuildVars.LOGS_ENABLED)
            {
              localObject = localCamera1;
              FileLog.d("start creating round camera session");
            }
            Camera localCamera2 = localCamera1;
            if (localCamera1 == null)
            {
              localObject = localCamera1;
              CameraInfo localCameraInfo = paramCameraSession.cameraInfo;
              localObject = localCamera1;
              localCamera2 = Camera.open(paramCameraSession.cameraInfo.cameraId);
              localObject = localCamera1;
              localCameraInfo.camera = localCamera2;
            }
            localObject = localCamera2;
            localCamera2.getParameters();
            localObject = localCamera2;
            paramCameraSession.configureRoundCamera();
            localObject = localCamera2;
            if (paramRunnable2 != null)
            {
              localObject = localCamera2;
              paramRunnable2.run();
            }
            localObject = localCamera2;
            localCamera2.setPreviewTexture(paramSurfaceTexture);
            localObject = localCamera2;
            localCamera2.startPreview();
            localObject = localCamera2;
            if (paramRunnable1 != null)
            {
              localObject = localCamera2;
              AndroidUtilities.runOnUIThread(paramRunnable1);
            }
            localObject = localCamera2;
            if (BuildVars.LOGS_ENABLED)
            {
              localObject = localCamera2;
              FileLog.d("round camera session created");
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              paramCameraSession.cameraInfo.camera = null;
              if (localObject != null) {
                ((Camera)localObject).release();
              }
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void recordVideo(final CameraSession paramCameraSession, final File paramFile, final VideoTakeCallback paramVideoTakeCallback, final Runnable paramRunnable)
  {
    if (paramCameraSession == null) {}
    for (;;)
    {
      return;
      final CameraInfo localCameraInfo = paramCameraSession.cameraInfo;
      final Camera localCamera = localCameraInfo.camera;
      this.threadPool.execute(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 31	org/telegram/messenger/camera/CameraController$9:val$camera	Landroid/hardware/Camera;
          //   4: astore_1
          //   5: aload_1
          //   6: ifnull +307 -> 313
          //   9: aload_0
          //   10: getfield 31	org/telegram/messenger/camera/CameraController$9:val$camera	Landroid/hardware/Camera;
          //   13: invokevirtual 54	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
          //   16: astore_2
          //   17: aload_0
          //   18: getfield 33	org/telegram/messenger/camera/CameraController$9:val$session	Lorg/telegram/messenger/camera/CameraSession;
          //   21: invokevirtual 60	org/telegram/messenger/camera/CameraSession:getCurrentFlashMode	()Ljava/lang/String;
          //   24: ldc 62
          //   26: invokevirtual 68	java/lang/String:equals	(Ljava/lang/Object;)Z
          //   29: ifeq +285 -> 314
          //   32: ldc 70
          //   34: astore_1
          //   35: aload_2
          //   36: aload_1
          //   37: invokevirtual 76	android/hardware/Camera$Parameters:setFlashMode	(Ljava/lang/String;)V
          //   40: aload_0
          //   41: getfield 31	org/telegram/messenger/camera/CameraController$9:val$camera	Landroid/hardware/Camera;
          //   44: aload_2
          //   45: invokevirtual 80	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
          //   48: aload_0
          //   49: getfield 31	org/telegram/messenger/camera/CameraController$9:val$camera	Landroid/hardware/Camera;
          //   52: invokevirtual 83	android/hardware/Camera:unlock	()V
          //   55: aload_0
          //   56: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   59: astore_2
          //   60: new 85	android/media/MediaRecorder
          //   63: astore_1
          //   64: aload_1
          //   65: invokespecial 86	android/media/MediaRecorder:<init>	()V
          //   68: aload_2
          //   69: aload_1
          //   70: invokestatic 90	org/telegram/messenger/camera/CameraController:access$302	(Lorg/telegram/messenger/camera/CameraController;Landroid/media/MediaRecorder;)Landroid/media/MediaRecorder;
          //   73: pop
          //   74: aload_0
          //   75: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   78: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   81: aload_0
          //   82: getfield 31	org/telegram/messenger/camera/CameraController$9:val$camera	Landroid/hardware/Camera;
          //   85: invokevirtual 98	android/media/MediaRecorder:setCamera	(Landroid/hardware/Camera;)V
          //   88: aload_0
          //   89: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   92: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   95: iconst_1
          //   96: invokevirtual 102	android/media/MediaRecorder:setVideoSource	(I)V
          //   99: aload_0
          //   100: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   103: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   106: iconst_5
          //   107: invokevirtual 105	android/media/MediaRecorder:setAudioSource	(I)V
          //   110: aload_0
          //   111: getfield 33	org/telegram/messenger/camera/CameraController$9:val$session	Lorg/telegram/messenger/camera/CameraSession;
          //   114: iconst_1
          //   115: aload_0
          //   116: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   119: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   122: invokevirtual 109	org/telegram/messenger/camera/CameraSession:configureRecorder	(ILandroid/media/MediaRecorder;)V
          //   125: aload_0
          //   126: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   129: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   132: aload_0
          //   133: getfield 35	org/telegram/messenger/camera/CameraController$9:val$path	Ljava/io/File;
          //   136: invokevirtual 114	java/io/File:getAbsolutePath	()Ljava/lang/String;
          //   139: invokevirtual 117	android/media/MediaRecorder:setOutputFile	(Ljava/lang/String;)V
          //   142: aload_0
          //   143: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   146: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   149: ldc2_w 118
          //   152: invokevirtual 123	android/media/MediaRecorder:setMaxFileSize	(J)V
          //   155: aload_0
          //   156: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   159: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   162: bipush 30
          //   164: invokevirtual 126	android/media/MediaRecorder:setVideoFrameRate	(I)V
          //   167: aload_0
          //   168: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   171: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   174: iconst_0
          //   175: invokevirtual 129	android/media/MediaRecorder:setMaxDuration	(I)V
          //   178: new 131	org/telegram/messenger/camera/Size
          //   181: astore_1
          //   182: aload_1
          //   183: bipush 16
          //   185: bipush 9
          //   187: invokespecial 134	org/telegram/messenger/camera/Size:<init>	(II)V
          //   190: aload_0
          //   191: getfield 37	org/telegram/messenger/camera/CameraController$9:val$info	Lorg/telegram/messenger/camera/CameraInfo;
          //   194: invokevirtual 140	org/telegram/messenger/camera/CameraInfo:getPictureSizes	()Ljava/util/ArrayList;
          //   197: sipush 720
          //   200: sipush 480
          //   203: aload_1
          //   204: invokestatic 144	org/telegram/messenger/camera/CameraController:chooseOptimalSize	(Ljava/util/List;IILorg/telegram/messenger/camera/Size;)Lorg/telegram/messenger/camera/Size;
          //   207: astore_1
          //   208: aload_0
          //   209: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   212: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   215: ldc -111
          //   217: invokevirtual 148	android/media/MediaRecorder:setVideoEncodingBitRate	(I)V
          //   220: aload_0
          //   221: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   224: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   227: aload_1
          //   228: invokevirtual 152	org/telegram/messenger/camera/Size:getWidth	()I
          //   231: aload_1
          //   232: invokevirtual 155	org/telegram/messenger/camera/Size:getHeight	()I
          //   235: invokevirtual 158	android/media/MediaRecorder:setVideoSize	(II)V
          //   238: aload_0
          //   239: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   242: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   245: aload_0
          //   246: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   249: invokevirtual 162	android/media/MediaRecorder:setOnInfoListener	(Landroid/media/MediaRecorder$OnInfoListener;)V
          //   252: aload_0
          //   253: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   256: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   259: invokevirtual 165	android/media/MediaRecorder:prepare	()V
          //   262: aload_0
          //   263: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   266: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   269: invokevirtual 168	android/media/MediaRecorder:start	()V
          //   272: aload_0
          //   273: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   276: aload_0
          //   277: getfield 39	org/telegram/messenger/camera/CameraController$9:val$callback	Lorg/telegram/messenger/camera/CameraController$VideoTakeCallback;
          //   280: invokestatic 172	org/telegram/messenger/camera/CameraController:access$402	(Lorg/telegram/messenger/camera/CameraController;Lorg/telegram/messenger/camera/CameraController$VideoTakeCallback;)Lorg/telegram/messenger/camera/CameraController$VideoTakeCallback;
          //   283: pop
          //   284: aload_0
          //   285: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   288: aload_0
          //   289: getfield 35	org/telegram/messenger/camera/CameraController$9:val$path	Ljava/io/File;
          //   292: invokevirtual 114	java/io/File:getAbsolutePath	()Ljava/lang/String;
          //   295: invokestatic 176	org/telegram/messenger/camera/CameraController:access$502	(Lorg/telegram/messenger/camera/CameraController;Ljava/lang/String;)Ljava/lang/String;
          //   298: pop
          //   299: aload_0
          //   300: getfield 41	org/telegram/messenger/camera/CameraController$9:val$onVideoStartRecord	Ljava/lang/Runnable;
          //   303: ifnull +10 -> 313
          //   306: aload_0
          //   307: getfield 41	org/telegram/messenger/camera/CameraController$9:val$onVideoStartRecord	Ljava/lang/Runnable;
          //   310: invokestatic 182	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
          //   313: return
          //   314: ldc -72
          //   316: astore_1
          //   317: goto -282 -> 35
          //   320: astore_1
          //   321: aload_1
          //   322: invokestatic 190	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   325: goto -277 -> 48
          //   328: astore_1
          //   329: aload_1
          //   330: invokestatic 190	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   333: goto -20 -> 313
          //   336: astore_1
          //   337: aload_0
          //   338: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   341: invokestatic 94	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
          //   344: invokevirtual 193	android/media/MediaRecorder:release	()V
          //   347: aload_0
          //   348: getfield 29	org/telegram/messenger/camera/CameraController$9:this$0	Lorg/telegram/messenger/camera/CameraController;
          //   351: aconst_null
          //   352: invokestatic 90	org/telegram/messenger/camera/CameraController:access$302	(Lorg/telegram/messenger/camera/CameraController;Landroid/media/MediaRecorder;)Landroid/media/MediaRecorder;
          //   355: pop
          //   356: aload_1
          //   357: invokestatic 190	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   360: goto -47 -> 313
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	363	0	this	9
          //   4	313	1	localObject1	Object
          //   320	2	1	localException1	Exception
          //   328	2	1	localException2	Exception
          //   336	21	1	localException3	Exception
          //   16	53	2	localObject2	Object
          // Exception table:
          //   from	to	target	type
          //   9	32	320	java/lang/Exception
          //   35	48	320	java/lang/Exception
          //   0	5	328	java/lang/Exception
          //   48	55	328	java/lang/Exception
          //   321	325	328	java/lang/Exception
          //   337	360	328	java/lang/Exception
          //   55	313	336	java/lang/Exception
        }
      });
    }
  }
  
  public void startPreview(final CameraSession paramCameraSession)
  {
    if (paramCameraSession == null) {}
    for (;;)
    {
      return;
      this.threadPool.execute(new Runnable()
      {
        @SuppressLint({"NewApi"})
        public void run()
        {
          Camera localCamera1 = paramCameraSession.cameraInfo.camera;
          Camera localCamera2 = localCamera1;
          Camera localCamera3;
          if (localCamera1 == null) {
            localCamera3 = localCamera1;
          }
          try
          {
            CameraInfo localCameraInfo = paramCameraSession.cameraInfo;
            localCamera3 = localCamera1;
            localCamera2 = Camera.open(paramCameraSession.cameraInfo.cameraId);
            localCamera3 = localCamera1;
            localCameraInfo.camera = localCamera2;
            localCamera3 = localCamera2;
            localCamera2.startPreview();
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              paramCameraSession.cameraInfo.camera = null;
              if (localCamera3 != null) {
                localCamera3.release();
              }
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void stopPreview(final CameraSession paramCameraSession)
  {
    if (paramCameraSession == null) {}
    for (;;)
    {
      return;
      this.threadPool.execute(new Runnable()
      {
        @SuppressLint({"NewApi"})
        public void run()
        {
          Camera localCamera1 = paramCameraSession.cameraInfo.camera;
          Camera localCamera2 = localCamera1;
          Camera localCamera3;
          if (localCamera1 == null) {
            localCamera3 = localCamera1;
          }
          try
          {
            CameraInfo localCameraInfo = paramCameraSession.cameraInfo;
            localCamera3 = localCamera1;
            localCamera2 = Camera.open(paramCameraSession.cameraInfo.cameraId);
            localCamera3 = localCamera1;
            localCameraInfo.camera = localCamera2;
            localCamera3 = localCamera2;
            localCamera2.stopPreview();
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              paramCameraSession.cameraInfo.camera = null;
              if (localCamera3 != null) {
                localCamera3.release();
              }
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void stopVideoRecording(final CameraSession paramCameraSession, final boolean paramBoolean)
  {
    this.threadPool.execute(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 25	org/telegram/messenger/camera/CameraController$11:val$session	Lorg/telegram/messenger/camera/CameraSession;
        //   4: getfield 40	org/telegram/messenger/camera/CameraSession:cameraInfo	Lorg/telegram/messenger/camera/CameraInfo;
        //   7: getfield 46	org/telegram/messenger/camera/CameraInfo:camera	Landroid/hardware/Camera;
        //   10: astore_1
        //   11: aload_1
        //   12: ifnull +53 -> 65
        //   15: aload_0
        //   16: getfield 23	org/telegram/messenger/camera/CameraController$11:this$0	Lorg/telegram/messenger/camera/CameraController;
        //   19: invokestatic 50	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   22: ifnull +43 -> 65
        //   25: aload_0
        //   26: getfield 23	org/telegram/messenger/camera/CameraController$11:this$0	Lorg/telegram/messenger/camera/CameraController;
        //   29: invokestatic 50	org/telegram/messenger/camera/CameraController:access$300	(Lorg/telegram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   32: astore_2
        //   33: aload_0
        //   34: getfield 23	org/telegram/messenger/camera/CameraController$11:this$0	Lorg/telegram/messenger/camera/CameraController;
        //   37: aconst_null
        //   38: invokestatic 54	org/telegram/messenger/camera/CameraController:access$302	(Lorg/telegram/messenger/camera/CameraController;Landroid/media/MediaRecorder;)Landroid/media/MediaRecorder;
        //   41: pop
        //   42: aload_2
        //   43: invokevirtual 59	android/media/MediaRecorder:stop	()V
        //   46: aload_2
        //   47: invokevirtual 62	android/media/MediaRecorder:release	()V
        //   50: aload_1
        //   51: invokevirtual 67	android/hardware/Camera:reconnect	()V
        //   54: aload_1
        //   55: invokevirtual 70	android/hardware/Camera:startPreview	()V
        //   58: aload_0
        //   59: getfield 25	org/telegram/messenger/camera/CameraController$11:val$session	Lorg/telegram/messenger/camera/CameraSession;
        //   62: invokevirtual 72	org/telegram/messenger/camera/CameraSession:stopVideoRecording	()V
        //   65: aload_1
        //   66: invokevirtual 76	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
        //   69: astore_2
        //   70: aload_2
        //   71: ldc 78
        //   73: invokevirtual 84	android/hardware/Camera$Parameters:setFlashMode	(Ljava/lang/String;)V
        //   76: aload_1
        //   77: aload_2
        //   78: invokevirtual 88	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
        //   81: aload_0
        //   82: getfield 23	org/telegram/messenger/camera/CameraController$11:this$0	Lorg/telegram/messenger/camera/CameraController;
        //   85: invokestatic 92	org/telegram/messenger/camera/CameraController:access$600	(Lorg/telegram/messenger/camera/CameraController;)Ljava/util/concurrent/ThreadPoolExecutor;
        //   88: astore_2
        //   89: new 13	org/telegram/messenger/camera/CameraController$11$1
        //   92: astore_3
        //   93: aload_3
        //   94: aload_0
        //   95: aload_1
        //   96: invokespecial 95	org/telegram/messenger/camera/CameraController$11$1:<init>	(Lorg/telegram/messenger/camera/CameraController$11;Landroid/hardware/Camera;)V
        //   99: aload_2
        //   100: aload_3
        //   101: invokevirtual 101	java/util/concurrent/ThreadPoolExecutor:execute	(Ljava/lang/Runnable;)V
        //   104: aload_0
        //   105: getfield 27	org/telegram/messenger/camera/CameraController$11:val$abandon	Z
        //   108: ifne +65 -> 173
        //   111: aload_0
        //   112: getfield 23	org/telegram/messenger/camera/CameraController$11:this$0	Lorg/telegram/messenger/camera/CameraController;
        //   115: invokestatic 105	org/telegram/messenger/camera/CameraController:access$400	(Lorg/telegram/messenger/camera/CameraController;)Lorg/telegram/messenger/camera/CameraController$VideoTakeCallback;
        //   118: ifnull +55 -> 173
        //   121: aload_0
        //   122: getfield 23	org/telegram/messenger/camera/CameraController$11:this$0	Lorg/telegram/messenger/camera/CameraController;
        //   125: invokestatic 109	org/telegram/messenger/camera/CameraController:access$700	(Lorg/telegram/messenger/camera/CameraController;)V
        //   128: return
        //   129: astore_3
        //   130: aload_3
        //   131: invokestatic 115	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   134: goto -88 -> 46
        //   137: astore_1
        //   138: goto -10 -> 128
        //   141: astore_2
        //   142: aload_2
        //   143: invokestatic 115	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   146: goto -96 -> 50
        //   149: astore_2
        //   150: aload_2
        //   151: invokestatic 115	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   154: goto -96 -> 58
        //   157: astore_2
        //   158: aload_2
        //   159: invokestatic 115	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   162: goto -97 -> 65
        //   165: astore_2
        //   166: aload_2
        //   167: invokestatic 115	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   170: goto -89 -> 81
        //   173: aload_0
        //   174: getfield 23	org/telegram/messenger/camera/CameraController$11:this$0	Lorg/telegram/messenger/camera/CameraController;
        //   177: aconst_null
        //   178: invokestatic 119	org/telegram/messenger/camera/CameraController:access$402	(Lorg/telegram/messenger/camera/CameraController;Lorg/telegram/messenger/camera/CameraController$VideoTakeCallback;)Lorg/telegram/messenger/camera/CameraController$VideoTakeCallback;
        //   181: pop
        //   182: goto -54 -> 128
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	185	0	this	11
        //   10	86	1	localCamera	Camera
        //   137	1	1	localException1	Exception
        //   32	68	2	localObject	Object
        //   141	2	2	localException2	Exception
        //   149	2	2	localException3	Exception
        //   157	2	2	localException4	Exception
        //   165	2	2	localException5	Exception
        //   92	9	3	local1	1
        //   129	2	3	localException6	Exception
        // Exception table:
        //   from	to	target	type
        //   42	46	129	java/lang/Exception
        //   0	11	137	java/lang/Exception
        //   15	42	137	java/lang/Exception
        //   81	128	137	java/lang/Exception
        //   130	134	137	java/lang/Exception
        //   142	146	137	java/lang/Exception
        //   150	154	137	java/lang/Exception
        //   158	162	137	java/lang/Exception
        //   166	170	137	java/lang/Exception
        //   173	182	137	java/lang/Exception
        //   46	50	141	java/lang/Exception
        //   50	58	149	java/lang/Exception
        //   58	65	157	java/lang/Exception
        //   65	81	165	java/lang/Exception
      }
    });
  }
  
  public boolean takePicture(File paramFile, CameraSession paramCameraSession, Runnable paramRunnable)
  {
    boolean bool = false;
    if (paramCameraSession == null) {}
    for (;;)
    {
      return bool;
      paramCameraSession = paramCameraSession.cameraInfo;
      Camera localCamera = paramCameraSession.camera;
      try
      {
        Camera.PictureCallback local4 = new org/telegram/messenger/camera/CameraController$4;
        local4.<init>(this, paramFile, paramCameraSession, paramRunnable);
        localCamera.takePicture(null, null, local4);
        bool = true;
      }
      catch (Exception paramFile)
      {
        FileLog.e(paramFile);
      }
    }
  }
  
  static class CompareSizesByArea
    implements Comparator<Size>
  {
    public int compare(Size paramSize1, Size paramSize2)
    {
      return Long.signum(paramSize1.getWidth() * paramSize1.getHeight() - paramSize2.getWidth() * paramSize2.getHeight());
    }
  }
  
  public static abstract interface VideoTakeCallback
  {
    public abstract void onFinishVideoRecording(String paramString, long paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/camera/CameraController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */