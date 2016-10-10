package org.telegram.messenger.camera;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;

public class CameraController
  implements MediaRecorder.OnInfoListener
{
  private static final int CORE_POOL_SIZE = 1;
  private static volatile CameraController Instance = null;
  private static final int KEEP_ALIVE_SECONDS = 60;
  private static final int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();
  protected ArrayList<String> availableFlashModes = new ArrayList();
  protected ArrayList<CameraInfo> cameraInfos = null;
  private boolean cameraInitied;
  private Runnable onVideoTakeCallback;
  private MediaRecorder recorder;
  private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, MAX_POOL_SIZE, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
  
  public static Size chooseOptimalSize(List<Size> paramList, int paramInt1, int paramInt2, Size paramSize)
  {
    ArrayList localArrayList = new ArrayList();
    int j = paramSize.getWidth();
    int k = paramSize.getHeight();
    int i = 0;
    while (i < paramList.size())
    {
      paramSize = (Size)paramList.get(i);
      if ((paramSize.getHeight() == paramSize.getWidth() * k / j) && (paramSize.getWidth() >= paramInt1) && (paramSize.getHeight() >= paramInt2)) {
        localArrayList.add(paramSize);
      }
      i += 1;
    }
    if (localArrayList.size() > 0) {
      return (Size)Collections.min(localArrayList, new CompareSizesByArea());
    }
    return (Size)Collections.max(paramList, new CompareSizesByArea());
  }
  
  public static CameraController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          CameraController localCameraController2 = Instance;
          localObject1 = localCameraController2;
          if (localCameraController2 == null) {
            localObject1 = new CameraController();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (CameraController)localObject1;
          return (CameraController)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localCameraController1;
  }
  
  private static int getOrientation(byte[] paramArrayOfByte)
  {
    boolean bool = true;
    if (paramArrayOfByte == null) {}
    label11:
    label119:
    label294:
    label410:
    label412:
    label416:
    for (;;)
    {
      return 0;
      int i = 0;
      int n;
      int i1;
      int m;
      do
      {
        do
        {
          for (;;)
          {
            n = 0;
            k = n;
            j = i;
            if (i + 3 >= paramArrayOfByte.length) {
              break label119;
            }
            j = i + 1;
            if ((paramArrayOfByte[i] & 0xFF) != 255) {
              break label412;
            }
            i1 = paramArrayOfByte[j] & 0xFF;
            if (i1 != 255) {
              break;
            }
            i = j;
          }
          m = j + 1;
          i = m;
        } while (i1 == 216);
        i = m;
      } while (i1 == 1);
      int k = n;
      int j = m;
      if (i1 != 217)
      {
        if (i1 != 218) {
          break label294;
        }
        j = m;
        k = n;
      }
      for (;;)
      {
        if (k <= 8) {
          break label416;
        }
        i = pack(paramArrayOfByte, j, 4, false);
        if ((i != 1229531648) && (i != 1296891946)) {
          break;
        }
        if (i == 1229531648)
        {
          m = pack(paramArrayOfByte, j + 4, 4, bool) + 2;
          if ((m < 10) || (m > k)) {
            break;
          }
          i = j + m;
          j = k - m;
          m = pack(paramArrayOfByte, i - 2, 2, bool);
          k = i;
          i = m;
        }
        for (;;)
        {
          if ((i <= 0) || (j < 12)) {
            break label410;
          }
          if (pack(paramArrayOfByte, k, 2, bool) == 274) {
            switch (pack(paramArrayOfByte, k + 8, 2, bool))
            {
            case 1: 
            case 2: 
            case 4: 
            case 5: 
            case 7: 
            default: 
              return 0;
            case 3: 
              return 180;
              i = pack(paramArrayOfByte, m, 2, false);
              if ((i < 2) || (m + i > paramArrayOfByte.length)) {
                break;
              }
              if ((i1 == 225) && (i >= 8) && (pack(paramArrayOfByte, m + 2, 4, false) == 1165519206) && (pack(paramArrayOfByte, m + 6, 2, false) == 0))
              {
                j = m + 8;
                k = i - 8;
                break label119;
              }
              i = m + i;
              break label11;
              bool = false;
              break;
            case 6: 
              return 90;
            case 8: 
              return 270;
            }
          }
          k += 12;
          j -= 12;
          i -= 1;
        }
        break;
        k = n;
      }
    }
  }
  
  private static int pack(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = 1;
    int i = paramInt1;
    if (paramBoolean)
    {
      i = paramInt1 + (paramInt2 - 1);
      j = -1;
    }
    paramInt1 = 0;
    while (paramInt2 > 0)
    {
      paramInt1 = paramInt1 << 8 | paramArrayOfByte[i] & 0xFF;
      i += j;
      paramInt2 -= 1;
    }
    return paramInt1;
  }
  
  public void cleanup()
  {
    this.threadPool.execute(new Runnable()
    {
      public void run()
      {
        if ((CameraController.this.cameraInfos == null) || (CameraController.this.cameraInfos.isEmpty())) {
          return;
        }
        int i = 0;
        while (i < CameraController.this.cameraInfos.size())
        {
          CameraInfo localCameraInfo = (CameraInfo)CameraController.this.cameraInfos.get(i);
          if (localCameraInfo.camera != null)
          {
            localCameraInfo.camera.stopPreview();
            localCameraInfo.camera.release();
            localCameraInfo.camera = null;
          }
          i += 1;
        }
        CameraController.this.cameraInfos = null;
      }
    });
  }
  
  public void close(CameraSession paramCameraSession, final Semaphore paramSemaphore)
  {
    paramCameraSession.destroy();
    final Camera localCamera = paramCameraSession.cameraInfo.camera;
    paramCameraSession.cameraInfo.camera = null;
    this.threadPool.execute(new Runnable()
    {
      public void run()
      {
        try
        {
          if (localCamera != null)
          {
            localCamera.stopPreview();
            localCamera.release();
          }
          if (paramSemaphore != null) {
            paramSemaphore.release();
          }
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException);
          }
        }
      }
    });
    if (paramSemaphore != null) {}
    try
    {
      paramSemaphore.acquire();
      return;
    }
    catch (Exception paramCameraSession)
    {
      FileLog.e("tmessages", paramCameraSession);
    }
  }
  
  public ArrayList<CameraInfo> getCameras()
  {
    return this.cameraInfos;
  }
  
  public void initCamera()
  {
    if (this.cameraInitied) {
      return;
    }
    this.threadPool.execute(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          int j;
          try
          {
            if (CameraController.this.cameraInfos == null)
            {
              int k = Camera.getNumberOfCameras();
              ArrayList localArrayList = new ArrayList();
              Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
              int i = 0;
              if (i < k)
              {
                Camera.getCameraInfo(i, localCameraInfo);
                CameraInfo localCameraInfo1 = new CameraInfo(i, localCameraInfo);
                Camera localCamera = Camera.open(localCameraInfo1.getCameraId());
                Object localObject1 = localCamera.getParameters();
                Object localObject2 = ((Camera.Parameters)localObject1).getSupportedPreviewSizes();
                j = 0;
                if (j < ((List)localObject2).size())
                {
                  Camera.Size localSize = (Camera.Size)((List)localObject2).get(j);
                  if ((localSize.height < 2160) && (localSize.width < 2160)) {
                    localCameraInfo1.previewSizes.add(new Size(localSize.width, localSize.height));
                  }
                }
                else
                {
                  localObject1 = ((Camera.Parameters)localObject1).getSupportedPictureSizes();
                  j = 0;
                  if (j < ((List)localObject1).size())
                  {
                    localObject2 = (Camera.Size)((List)localObject1).get(j);
                    if (("samsung".equals(Build.MANUFACTURER)) && ("jflteuc".equals(Build.PRODUCT)) && (((Camera.Size)localObject2).width >= 2048)) {
                      break label311;
                    }
                    localCameraInfo1.pictureSizes.add(new Size(((Camera.Size)localObject2).width, ((Camera.Size)localObject2).height));
                    break label311;
                  }
                  localCamera.release();
                  localArrayList.add(localCameraInfo1);
                  i += 1;
                }
              }
              else
              {
                CameraController.this.cameraInfos = localArrayList;
              }
            }
            else
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  CameraController.access$002(CameraController.this, true);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.cameraInitied, new Object[0]);
                }
              });
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          j += 1;
          continue;
          label311:
          j += 1;
        }
      }
    });
  }
  
  public boolean isCameraInitied()
  {
    return (this.cameraInitied) && (this.cameraInfos != null) && (!this.cameraInfos.isEmpty());
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
      AndroidUtilities.runOnUIThread(this.onVideoTakeCallback);
    }
  }
  
  public void open(final CameraSession paramCameraSession, final SurfaceTexture paramSurfaceTexture, final Runnable paramRunnable)
  {
    if ((paramCameraSession == null) || (paramSurfaceTexture == null)) {
      return;
    }
    this.threadPool.execute(new Runnable()
    {
      @SuppressLint({"NewApi"})
      public void run()
      {
        Object localObject3 = paramCameraSession.cameraInfo.camera;
        Object localObject1 = localObject3;
        Object localObject2;
        if (localObject3 == null) {
          localObject2 = localObject3;
        }
        for (;;)
        {
          int i;
          try
          {
            Object localObject4 = paramCameraSession.cameraInfo;
            localObject2 = localObject3;
            localObject1 = Camera.open(paramCameraSession.cameraInfo.cameraId);
            localObject2 = localObject3;
            ((CameraInfo)localObject4).camera = ((Camera)localObject1);
            localObject2 = localObject1;
            localObject3 = ((Camera)localObject1).getParameters().getSupportedFlashModes();
            localObject2 = localObject1;
            CameraController.this.availableFlashModes.clear();
            if (localObject3 != null)
            {
              i = 0;
              localObject2 = localObject1;
              if (i < ((List)localObject3).size())
              {
                localObject2 = localObject1;
                localObject4 = (String)((List)localObject3).get(i);
                localObject2 = localObject1;
                if (!((String)localObject4).equals("off"))
                {
                  localObject2 = localObject1;
                  if (!((String)localObject4).equals("on"))
                  {
                    localObject2 = localObject1;
                    if (!((String)localObject4).equals("auto")) {
                      break label264;
                    }
                  }
                }
                localObject2 = localObject1;
                CameraController.this.availableFlashModes.add(localObject4);
              }
              else
              {
                localObject2 = localObject1;
                paramCameraSession.checkFlashMode((String)CameraController.this.availableFlashModes.get(0));
              }
            }
            else
            {
              localObject2 = localObject1;
              paramCameraSession.configurePhotoCamera();
              localObject2 = localObject1;
              ((Camera)localObject1).setPreviewTexture(paramSurfaceTexture);
              localObject2 = localObject1;
              ((Camera)localObject1).startPreview();
              localObject2 = localObject1;
              if (paramRunnable != null)
              {
                localObject2 = localObject1;
                AndroidUtilities.runOnUIThread(paramRunnable);
              }
              return;
            }
          }
          catch (Exception localException)
          {
            paramCameraSession.cameraInfo.camera = null;
            if (localObject2 != null) {
              ((Camera)localObject2).release();
            }
            FileLog.e("tmessages", localException);
            return;
          }
          label264:
          i += 1;
        }
      }
    });
  }
  
  public void recordVideo(CameraSession paramCameraSession, File paramFile, Runnable paramRunnable)
  {
    if (paramCameraSession == null) {}
    for (;;)
    {
      return;
      try
      {
        CameraInfo localCameraInfo = paramCameraSession.cameraInfo;
        Camera localCamera = localCameraInfo.camera;
        if (localCamera == null) {
          continue;
        }
        localCamera.stopPreview();
        localCamera.unlock();
        try
        {
          this.recorder = new MediaRecorder();
          this.recorder.setCamera(localCamera);
          this.recorder.setVideoSource(1);
          this.recorder.setAudioSource(5);
          paramCameraSession.configureRecorder(1, this.recorder);
          this.recorder.setOutputFile(paramFile.getAbsolutePath());
          this.recorder.setMaxFileSize(1073741824L);
          this.recorder.setVideoFrameRate(30);
          this.recorder.setMaxDuration(0);
          paramCameraSession = new Size(16, 9);
          paramCameraSession = chooseOptimalSize(localCameraInfo.getPictureSizes(), 720, 480, paramCameraSession);
          this.recorder.setVideoSize(paramCameraSession.getWidth(), paramCameraSession.getHeight());
          this.recorder.setVideoEncodingBitRate(1800000);
          this.recorder.setOnInfoListener(this);
          this.recorder.prepare();
          this.recorder.start();
          this.onVideoTakeCallback = paramRunnable;
          return;
        }
        catch (Exception paramCameraSession)
        {
          this.recorder.release();
          this.recorder = null;
          FileLog.e("tmessages", paramCameraSession);
          return;
        }
        return;
      }
      catch (Exception paramCameraSession)
      {
        FileLog.e("tmessages", paramCameraSession);
      }
    }
  }
  
  public void startPreview(final CameraSession paramCameraSession)
  {
    if (paramCameraSession == null) {
      return;
    }
    this.threadPool.execute(new Runnable()
    {
      @SuppressLint({"NewApi"})
      public void run()
      {
        Camera localCamera3 = paramCameraSession.cameraInfo.camera;
        Camera localCamera1 = localCamera3;
        Camera localCamera2;
        if (localCamera3 == null) {
          localCamera2 = localCamera3;
        }
        try
        {
          CameraInfo localCameraInfo = paramCameraSession.cameraInfo;
          localCamera2 = localCamera3;
          localCamera1 = Camera.open(paramCameraSession.cameraInfo.cameraId);
          localCamera2 = localCamera3;
          localCameraInfo.camera = localCamera1;
          localCamera2 = localCamera1;
          localCamera1.startPreview();
          return;
        }
        catch (Exception localException)
        {
          paramCameraSession.cameraInfo.camera = null;
          if (localCamera2 != null) {
            localCamera2.release();
          }
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void stopVideoRecording(CameraSession paramCameraSession, boolean paramBoolean)
  {
    try
    {
      Camera localCamera = paramCameraSession.cameraInfo.camera;
      if ((localCamera != null) && (this.recorder != null))
      {
        MediaRecorder localMediaRecorder = this.recorder;
        this.recorder = null;
        localMediaRecorder.stop();
        localMediaRecorder.release();
        localCamera.reconnect();
        localCamera.startPreview();
        paramCameraSession.stopVideoRecording();
      }
      if (!paramBoolean) {
        AndroidUtilities.runOnUIThread(this.onVideoTakeCallback);
      }
      return;
    }
    catch (Exception paramCameraSession)
    {
      FileLog.e("tmessages", paramCameraSession);
    }
  }
  
  public boolean takePicture(final File paramFile, final CameraSession paramCameraSession, final Runnable paramRunnable)
  {
    if (paramCameraSession == null) {
      return false;
    }
    paramCameraSession = paramCameraSession.cameraInfo;
    Camera localCamera = paramCameraSession.camera;
    try
    {
      localCamera.takePicture(null, null, new Camera.PictureCallback()
      {
        public void onPictureTaken(byte[] paramAnonymousArrayOfByte, Camera paramAnonymousCamera)
        {
          do
          {
            try
            {
              int i = paramCameraSession.frontCamera;
              if (i != 0) {
                try
                {
                  paramAnonymousCamera = new Matrix();
                  paramAnonymousCamera.setRotate(CameraController.getOrientation(paramAnonymousArrayOfByte));
                  paramAnonymousCamera.postScale(-1.0F, 1.0F);
                  Object localObject = new BitmapFactory.Options();
                  ((BitmapFactory.Options)localObject).inPurgeable = true;
                  localObject = BitmapFactory.decodeByteArray(paramAnonymousArrayOfByte, 0, paramAnonymousArrayOfByte.length, (BitmapFactory.Options)localObject);
                  paramAnonymousCamera = Bitmaps.createBitmap((Bitmap)localObject, 0, 0, ((Bitmap)localObject).getWidth(), ((Bitmap)localObject).getHeight(), paramAnonymousCamera, false);
                  ((Bitmap)localObject).recycle();
                  localObject = new FileOutputStream(paramFile);
                  paramAnonymousCamera.compress(Bitmap.CompressFormat.JPEG, 80, (OutputStream)localObject);
                  ((FileOutputStream)localObject).flush();
                  ((FileOutputStream)localObject).getFD().sync();
                  ((FileOutputStream)localObject).close();
                  paramAnonymousCamera.recycle();
                  if (paramRunnable != null) {
                    paramRunnable.run();
                  }
                  return;
                }
                catch (Throwable paramAnonymousCamera)
                {
                  FileLog.e("tmessages", paramAnonymousCamera);
                }
              }
              paramAnonymousCamera = new FileOutputStream(paramFile);
              paramAnonymousCamera.write(paramAnonymousArrayOfByte);
              paramAnonymousCamera.flush();
              paramAnonymousCamera.getFD().sync();
              paramAnonymousCamera.close();
            }
            catch (Exception paramAnonymousArrayOfByte)
            {
              for (;;)
              {
                FileLog.e("tmessages", paramAnonymousArrayOfByte);
              }
            }
          } while (paramRunnable == null);
          paramRunnable.run();
        }
      });
      return true;
    }
    catch (Exception paramFile)
    {
      FileLog.e("tmessages", paramFile);
    }
    return false;
  }
  
  static class CompareSizesByArea
    implements Comparator<Size>
  {
    public int compare(Size paramSize1, Size paramSize2)
    {
      return Long.signum(paramSize1.getWidth() * paramSize1.getHeight() - paramSize2.getWidth() * paramSize2.getHeight());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/camera/CameraController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */