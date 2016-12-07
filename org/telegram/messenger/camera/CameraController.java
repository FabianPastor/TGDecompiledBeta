package org.telegram.messenger.camera;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.ThumbnailUtils;
import android.os.Build;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public class CameraController implements OnInfoListener {
    private static final int CORE_POOL_SIZE = 1;
    private static volatile CameraController Instance = null;
    private static final int KEEP_ALIVE_SECONDS = 60;
    private static final int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    protected ArrayList<String> availableFlashModes = new ArrayList();
    protected ArrayList<CameraInfo> cameraInfos = null;
    private boolean cameraInitied;
    private VideoTakeCallback onVideoTakeCallback;
    private String recordedFile;
    private MediaRecorder recorder;
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, MAX_POOL_SIZE, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());

    static class CompareSizesByArea implements Comparator<Size> {
        CompareSizesByArea() {
        }

        public int compare(Size lhs, Size rhs) {
            return Long.signum((((long) lhs.getWidth()) * ((long) lhs.getHeight())) - (((long) rhs.getWidth()) * ((long) rhs.getHeight())));
        }
    }

    public interface VideoTakeCallback {
        void onFinishVideoRecording(Bitmap bitmap);
    }

    public static CameraController getInstance() {
        CameraController localInstance = Instance;
        if (localInstance == null) {
            synchronized (CameraController.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        CameraController localInstance2 = new CameraController();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public void initCamera() {
        if (!this.cameraInitied) {
            this.threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        if (CameraController.this.cameraInfos == null) {
                            int count = Camera.getNumberOfCameras();
                            ArrayList<CameraInfo> result = new ArrayList();
                            CameraInfo info = new CameraInfo();
                            for (int cameraId = 0; cameraId < count; cameraId++) {
                                int a;
                                Size size;
                                Camera.getCameraInfo(cameraId, info);
                                CameraInfo cameraInfo = new CameraInfo(cameraId, info);
                                Camera camera = Camera.open(cameraInfo.getCameraId());
                                Parameters params = camera.getParameters();
                                List<Size> list = params.getSupportedPreviewSizes();
                                for (a = 0; a < list.size(); a++) {
                                    size = (Size) list.get(a);
                                    if (size.height < 2160 && size.width < 2160) {
                                        cameraInfo.previewSizes.add(new Size(size.width, size.height));
                                    }
                                }
                                list = params.getSupportedPictureSizes();
                                for (a = 0; a < list.size(); a++) {
                                    size = (Size) list.get(a);
                                    if (!"samsung".equals(Build.MANUFACTURER) || !"jflteuc".equals(Build.PRODUCT) || size.width < 2048) {
                                        cameraInfo.pictureSizes.add(new Size(size.width, size.height));
                                    }
                                }
                                camera.release();
                                result.add(cameraInfo);
                            }
                            CameraController.this.cameraInfos = result;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                CameraController.this.cameraInitied = true;
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.cameraInitied, new Object[0]);
                            }
                        });
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            });
        }
    }

    public boolean isCameraInitied() {
        return (!this.cameraInitied || this.cameraInfos == null || this.cameraInfos.isEmpty()) ? false : true;
    }

    public void cleanup() {
        this.threadPool.execute(new Runnable() {
            public void run() {
                if (CameraController.this.cameraInfos != null && !CameraController.this.cameraInfos.isEmpty()) {
                    for (int a = 0; a < CameraController.this.cameraInfos.size(); a++) {
                        CameraInfo info = (CameraInfo) CameraController.this.cameraInfos.get(a);
                        if (info.camera != null) {
                            info.camera.stopPreview();
                            info.camera.release();
                            info.camera = null;
                        }
                    }
                    CameraController.this.cameraInfos = null;
                }
            }
        });
    }

    public void close(CameraSession session, final Semaphore semaphore) {
        session.destroy();
        final Camera camera = session.cameraInfo.camera;
        session.cameraInfo.camera = null;
        this.threadPool.execute(new Runnable() {
            public void run() {
                try {
                    if (camera != null) {
                        camera.stopPreview();
                        camera.release();
                    }
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                if (semaphore != null) {
                    semaphore.release();
                }
            }
        });
        if (semaphore != null) {
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    public ArrayList<CameraInfo> getCameras() {
        return this.cameraInfos;
    }

    private static int getOrientation(byte[] jpeg) {
        boolean littleEndian = true;
        if (jpeg == null) {
            return 0;
        }
        int offset = 0;
        int length = 0;
        while (offset + 3 < jpeg.length) {
            int offset2 = offset + 1;
            if ((jpeg[offset] & 255) != 255) {
                offset = offset2;
                break;
            }
            int marker = jpeg[offset2] & 255;
            if (marker != 255) {
                offset = offset2 + 1;
                if (!(marker == 216 || marker == 1)) {
                    if (marker != 217 && marker != 218) {
                        length = pack(jpeg, offset, 2, false);
                        if (length >= 2 && offset + length <= jpeg.length) {
                            if (marker == 225 && length >= 8 && pack(jpeg, offset + 2, 4, false) == NUM && pack(jpeg, offset + 6, 2, false) == 0) {
                                offset += 8;
                                length -= 8;
                                break;
                            }
                            offset += length;
                            length = 0;
                        } else {
                            return 0;
                        }
                    }
                    break;
                }
            }
            offset = offset2;
        }
        if (length <= 8) {
            return 0;
        }
        int tag = pack(jpeg, offset, 4, false);
        if (tag != NUM && tag != NUM) {
            return 0;
        }
        if (tag != NUM) {
            littleEndian = false;
        }
        int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
        if (count < 10 || count > length) {
            return 0;
        }
        offset += count;
        length -= count;
        int count2 = pack(jpeg, offset - 2, 2, littleEndian);
        while (true) {
            count = count2 - 1;
            if (count2 <= 0 || length < 12) {
                return 0;
            }
            if (pack(jpeg, offset, 2, littleEndian) == 274) {
                break;
            }
            offset += 12;
            length -= 12;
            count2 = count;
        }
        switch (pack(jpeg, offset + 8, 2, littleEndian)) {
            case 1:
                return 0;
            case 3:
                return 180;
            case 6:
                return 90;
            case 8:
                return 270;
            default:
                return 0;
        }
    }

    private static int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }
        int value = 0;
        int length2 = length;
        while (true) {
            length = length2 - 1;
            if (length2 <= 0) {
                return value;
            }
            value = (value << 8) | (bytes[offset] & 255);
            offset += step;
            length2 = length;
        }
    }

    public boolean takePicture(final File path, CameraSession session, final Runnable callback) {
        if (session == null) {
            return false;
        }
        final CameraInfo info = session.cameraInfo;
        try {
            info.camera.takePicture(null, null, new PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    Bitmap bitmap = null;
                    int size = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
                    String key = String.format(Locale.US, "%s@%d_%d", new Object[]{Utilities.MD5(path.getAbsolutePath()), Integer.valueOf(size), Integer.valueOf(size)});
                    try {
                        Options options = new Options();
                        options.inPurgeable = true;
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                    try {
                        FileOutputStream outputStream;
                        if (info.frontCamera != 0) {
                            try {
                                Matrix matrix = new Matrix();
                                matrix.setRotate((float) CameraController.getOrientation(data));
                                matrix.postScale(-1.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                Bitmap scaled = Bitmaps.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                                bitmap.recycle();
                                outputStream = new FileOutputStream(path);
                                scaled.compress(CompressFormat.JPEG, 80, outputStream);
                                outputStream.flush();
                                outputStream.getFD().sync();
                                outputStream.close();
                                if (scaled != null) {
                                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(scaled), key);
                                }
                                if (callback != null) {
                                    callback.run();
                                    return;
                                }
                                return;
                            } catch (Throwable e2) {
                                FileLog.e("tmessages", e2);
                            }
                        }
                        outputStream = new FileOutputStream(path);
                        outputStream.write(data);
                        outputStream.flush();
                        outputStream.getFD().sync();
                        outputStream.close();
                        if (bitmap != null) {
                            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), key);
                        }
                    } catch (Throwable e22) {
                        FileLog.e("tmessages", e22);
                    }
                    if (callback != null) {
                        callback.run();
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return false;
        }
    }

    public void startPreview(final CameraSession session) {
        if (session != null) {
            this.threadPool.execute(new Runnable() {
                @SuppressLint({"NewApi"})
                public void run() {
                    Camera camera = session.cameraInfo.camera;
                    if (camera == null) {
                        try {
                            CameraInfo cameraInfo = session.cameraInfo;
                            Camera camera2 = Camera.open(session.cameraInfo.cameraId);
                            cameraInfo.camera = camera2;
                            camera = camera2;
                        } catch (Throwable e) {
                            session.cameraInfo.camera = null;
                            if (camera != null) {
                                camera.release();
                            }
                            FileLog.e("tmessages", e);
                            return;
                        }
                    }
                    camera.startPreview();
                }
            });
        }
    }

    public void open(final CameraSession session, final SurfaceTexture texture, final Runnable callback) {
        if (session != null && texture != null) {
            this.threadPool.execute(new Runnable() {
                @SuppressLint({"NewApi"})
                public void run() {
                    Camera camera = session.cameraInfo.camera;
                    if (camera == null) {
                        try {
                            CameraInfo cameraInfo = session.cameraInfo;
                            Camera camera2 = Camera.open(session.cameraInfo.cameraId);
                            cameraInfo.camera = camera2;
                            camera = camera2;
                        } catch (Throwable e) {
                            session.cameraInfo.camera = null;
                            if (camera != null) {
                                camera.release();
                            }
                            FileLog.e("tmessages", e);
                            return;
                        }
                    }
                    List<String> rawFlashModes = camera.getParameters().getSupportedFlashModes();
                    CameraController.this.availableFlashModes.clear();
                    if (rawFlashModes != null) {
                        for (int a = 0; a < rawFlashModes.size(); a++) {
                            String rawFlashMode = (String) rawFlashModes.get(a);
                            if (rawFlashMode.equals("off") || rawFlashMode.equals("on") || rawFlashMode.equals("auto")) {
                                CameraController.this.availableFlashModes.add(rawFlashMode);
                            }
                        }
                        session.checkFlashMode((String) CameraController.this.availableFlashModes.get(0));
                    }
                    session.configurePhotoCamera();
                    camera.setPreviewTexture(texture);
                    camera.startPreview();
                    if (callback != null) {
                        AndroidUtilities.runOnUIThread(callback);
                    }
                }
            });
        }
    }

    public void recordVideo(CameraSession session, File path, VideoTakeCallback callback) {
        if (session != null) {
            try {
                CameraInfo info = session.cameraInfo;
                Camera camera = info.camera;
                if (camera != null) {
                    camera.stopPreview();
                    camera.unlock();
                    try {
                        this.recorder = new MediaRecorder();
                        this.recorder.setCamera(camera);
                        this.recorder.setVideoSource(1);
                        this.recorder.setAudioSource(5);
                        session.configureRecorder(1, this.recorder);
                        this.recorder.setOutputFile(path.getAbsolutePath());
                        this.recorder.setMaxFileSize(NUM);
                        this.recorder.setVideoFrameRate(30);
                        this.recorder.setMaxDuration(0);
                        Size pictureSize = chooseOptimalSize(info.getPictureSizes(), 720, 480, new Size(16, 9));
                        this.recorder.setVideoSize(pictureSize.getWidth(), pictureSize.getHeight());
                        this.recorder.setVideoEncodingBitRate(1800000);
                        this.recorder.setOnInfoListener(this);
                        this.recorder.prepare();
                        this.recorder.start();
                        this.onVideoTakeCallback = callback;
                        this.recordedFile = path.getAbsolutePath();
                    } catch (Throwable e) {
                        this.recorder.release();
                        this.recorder = null;
                        FileLog.e("tmessages", e);
                    }
                }
            } catch (Throwable e2) {
                FileLog.e("tmessages", e2);
            }
        }
    }

    public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
        if (what == 800 || what == 801 || what == 1) {
            MediaRecorder tempRecorder = this.recorder;
            this.recorder = null;
            if (tempRecorder != null) {
                tempRecorder.stop();
                tempRecorder.release();
            }
            if (this.onVideoTakeCallback != null) {
                final Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        CameraController.this.onVideoTakeCallback.onFinishVideoRecording(bitmap);
                    }
                });
            }
        }
    }

    public void stopVideoRecording(CameraSession session, boolean abandon) {
        try {
            Camera camera = session.cameraInfo.camera;
            if (!(camera == null || this.recorder == null)) {
                MediaRecorder tempRecorder = this.recorder;
                this.recorder = null;
                try {
                    tempRecorder.stop();
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                try {
                    tempRecorder.release();
                } catch (Throwable e2) {
                    FileLog.e("tmessages", e2);
                }
                try {
                    camera.reconnect();
                    camera.startPreview();
                } catch (Throwable e22) {
                    FileLog.e("tmessages", e22);
                }
                try {
                    session.stopVideoRecording();
                } catch (Throwable e222) {
                    FileLog.e("tmessages", e222);
                }
            }
            if (!abandon) {
                if (this.onVideoTakeCallback != null) {
                    final Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            CameraController.this.onVideoTakeCallback.onFinishVideoRecording(bitmap);
                        }
                    });
                }
            }
        } catch (Throwable e2222) {
            FileLog.e("tmessages", e2222);
        }
    }

    public static Size chooseOptimalSize(List<Size> choices, int width, int height, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (int a = 0; a < choices.size(); a++) {
            Size option = (Size) choices.get(a);
            if (option.getHeight() == (option.getWidth() * h) / w && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return (Size) Collections.min(bigEnough, new CompareSizesByArea());
        }
        return (Size) Collections.max(choices, new CompareSizesByArea());
    }
}
