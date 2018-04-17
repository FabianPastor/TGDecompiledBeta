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
import android.media.MediaMetadataRetriever;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;

public class CameraController implements OnInfoListener {
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
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());

    /* renamed from: org.telegram.messenger.camera.CameraController$1 */
    class C05261 implements Runnable {

        /* renamed from: org.telegram.messenger.camera.CameraController$1$1 */
        class C05221 implements Comparator<Size> {
            C05221() {
            }

            public int compare(Size o1, Size o2) {
                if (o1.mWidth < o2.mWidth) {
                    return 1;
                }
                if (o1.mWidth > o2.mWidth) {
                    return -1;
                }
                if (o1.mHeight < o2.mHeight) {
                    return 1;
                }
                if (o1.mHeight > o2.mHeight) {
                    return -1;
                }
                return 0;
            }
        }

        /* renamed from: org.telegram.messenger.camera.CameraController$1$2 */
        class C05232 implements Runnable {
            C05232() {
            }

            public void run() {
                CameraController.this.loadingCameras = false;
                CameraController.this.cameraInitied = true;
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.cameraInitied, new Object[0]);
            }
        }

        /* renamed from: org.telegram.messenger.camera.CameraController$1$3 */
        class C05243 implements Runnable {
            C05243() {
            }

            public void run() {
                CameraController.this.loadingCameras = false;
                CameraController.this.cameraInitied = false;
            }
        }

        C05261() {
        }

        public void run() {
            try {
                if (CameraController.this.cameraInfos == null) {
                    int count = Camera.getNumberOfCameras();
                    ArrayList<CameraInfo> result = new ArrayList();
                    CameraInfo info = new CameraInfo();
                    for (int cameraId = 0; cameraId < count; cameraId++) {
                        int i;
                        int i2;
                        Camera.getCameraInfo(cameraId, info);
                        CameraInfo cameraInfo = new CameraInfo(cameraId, info);
                        Camera camera = Camera.open(cameraInfo.getCameraId());
                        Parameters params = camera.getParameters();
                        List<Size> list = params.getSupportedPreviewSizes();
                        int a = 0;
                        while (true) {
                            i = 720;
                            i2 = 1280;
                            if (a >= list.size()) {
                                break;
                            }
                            Size size = (Size) list.get(a);
                            if (size.width != 1280 || size.height == 720) {
                                if (size.height < 2160 && size.width < 2160) {
                                    cameraInfo.previewSizes.add(new Size(size.width, size.height));
                                    if (BuildVars.LOGS_ENABLED) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("preview size = ");
                                        stringBuilder.append(size.width);
                                        stringBuilder.append(" ");
                                        stringBuilder.append(size.height);
                                        FileLog.m0d(stringBuilder.toString());
                                    }
                                }
                            }
                            a++;
                        }
                        List<Size> list2 = params.getSupportedPictureSizes();
                        int a2 = 0;
                        while (a2 < list2.size()) {
                            Size size2 = (Size) list2.get(a2);
                            if (size2.width != i2 || size2.height == r13) {
                                if (!"samsung".equals(Build.MANUFACTURER) || !"jflteuc".equals(Build.PRODUCT) || size2.width < 2048) {
                                    cameraInfo.pictureSizes.add(new Size(size2.width, size2.height));
                                    if (BuildVars.LOGS_ENABLED) {
                                        StringBuilder stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append("picture size = ");
                                        stringBuilder2.append(size2.width);
                                        stringBuilder2.append(" ");
                                        stringBuilder2.append(size2.height);
                                        FileLog.m0d(stringBuilder2.toString());
                                    }
                                }
                            }
                            a2++;
                            i = 720;
                            i2 = 1280;
                        }
                        camera.release();
                        result.add(cameraInfo);
                        Comparator<Size> comparator = new C05221();
                        Collections.sort(cameraInfo.previewSizes, comparator);
                        Collections.sort(cameraInfo.pictureSizes, comparator);
                    }
                    CameraController.this.cameraInfos = result;
                }
                AndroidUtilities.runOnUIThread(new C05232());
            } catch (Throwable e) {
                Throwable e2 = e;
                AndroidUtilities.runOnUIThread(new C05243());
                FileLog.m3e(e2);
            }
        }
    }

    /* renamed from: org.telegram.messenger.camera.CameraController$2 */
    class C05272 implements Runnable {
        C05272() {
        }

        public void run() {
            if (CameraController.this.cameraInfos != null) {
                if (!CameraController.this.cameraInfos.isEmpty()) {
                    for (int a = 0; a < CameraController.this.cameraInfos.size(); a++) {
                        CameraInfo info = (CameraInfo) CameraController.this.cameraInfos.get(a);
                        if (info.camera != null) {
                            info.camera.stopPreview();
                            info.camera.setPreviewCallbackWithBuffer(null);
                            info.camera.release();
                            info.camera = null;
                        }
                    }
                    CameraController.this.cameraInfos = null;
                }
            }
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {
        CompareSizesByArea() {
        }

        public int compare(Size lhs, Size rhs) {
            return Long.signum((((long) lhs.getWidth()) * ((long) lhs.getHeight())) - (((long) rhs.getWidth()) * ((long) rhs.getHeight())));
        }
    }

    public interface VideoTakeCallback {
        void onFinishVideoRecording(String str, long j);
    }

    public static CameraController getInstance() {
        CameraController localInstance = Instance;
        if (localInstance == null) {
            synchronized (CameraController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    CameraController cameraController = new CameraController();
                    localInstance = cameraController;
                    Instance = cameraController;
                }
            }
        }
        return localInstance;
    }

    public void initCamera() {
        if (!this.loadingCameras) {
            if (!this.cameraInitied) {
                this.loadingCameras = true;
                this.threadPool.execute(new C05261());
            }
        }
    }

    public boolean isCameraInitied() {
        return (!this.cameraInitied || this.cameraInfos == null || this.cameraInfos.isEmpty()) ? false : true;
    }

    public void cleanup() {
        this.threadPool.execute(new C05272());
    }

    public void close(final CameraSession session, final CountDownLatch countDownLatch, final Runnable beforeDestroyRunnable) {
        session.destroy();
        this.threadPool.execute(new Runnable() {
            public void run() {
                if (beforeDestroyRunnable != null) {
                    beforeDestroyRunnable.run();
                }
                if (session.cameraInfo.camera != null) {
                    try {
                        session.cameraInfo.camera.stopPreview();
                        session.cameraInfo.camera.setPreviewCallbackWithBuffer(null);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    try {
                        session.cameraInfo.camera.release();
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                    session.cameraInfo.camera = null;
                    if (countDownLatch != null) {
                        countDownLatch.countDown();
                    }
                }
            }
        });
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public ArrayList<CameraInfo> getCameras() {
        return this.cameraInfos;
    }

    private static int getOrientation(byte[] jpeg) {
        if (jpeg == null) {
            return 0;
        }
        int offset;
        int offset2 = 0;
        int length = 0;
        while (offset2 + 3 < jpeg.length) {
            offset = offset2 + 1;
            if ((jpeg[offset2] & 255) != 255) {
                break;
            }
            offset2 = jpeg[offset] & 255;
            if (offset2 != 255) {
                offset++;
                if (offset2 == 216) {
                    continue;
                } else if (offset2 != 1) {
                    if (offset2 == 217) {
                        break;
                    } else if (offset2 == 218) {
                        break;
                    } else {
                        length = pack(jpeg, offset, 2, false);
                        if (length >= 2) {
                            if (offset + length <= jpeg.length) {
                                if (offset2 == 225 && length >= 8 && pack(jpeg, offset + 2, 4, false) == NUM && pack(jpeg, offset + 6, 2, false) == 0) {
                                    offset += 8;
                                    length -= 8;
                                    break;
                                }
                                offset += length;
                                length = 0;
                            }
                        }
                        return 0;
                    }
                }
            }
            offset2 = offset;
        }
        offset = offset2;
        if (length > 8) {
            offset2 = pack(jpeg, offset, 4, false);
            if (offset2 != NUM && offset2 != NUM) {
                return 0;
            }
            boolean littleEndian = offset2 == NUM;
            int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
            if (count >= 10) {
                if (count <= length) {
                    offset += count;
                    length -= count;
                    count = pack(jpeg, offset - 2, 2, littleEndian);
                    while (true) {
                        int count2 = count - 1;
                        if (count <= 0 || length < 12) {
                            break;
                        } else if (pack(jpeg, offset, 2, littleEndian) == 274) {
                            break;
                        } else {
                            offset += 12;
                            length -= 12;
                            count = count2;
                        }
                    }
                    count = pack(jpeg, offset + 8, 2, littleEndian);
                    if (count == 1) {
                        return 0;
                    }
                    if (count == 3) {
                        return 180;
                    }
                    if (count == 6) {
                        return 90;
                    }
                    if (count != 8) {
                        return 0;
                    }
                    return 270;
                }
            }
            return 0;
        }
        return 0;
    }

    private static int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }
        int value = 0;
        while (true) {
            int length2 = length - 1;
            if (length <= 0) {
                return value;
            }
            value = (value << 8) | (bytes[offset] & 255);
            offset += step;
            length = length2;
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
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeByteArray(data, 0, data.length, options);
                        float scaleFactor = Math.max(((float) options.outWidth) / ((float) AndroidUtilities.getPhotoSize()), ((float) options.outHeight) / ((float) AndroidUtilities.getPhotoSize()));
                        if (scaleFactor < 1.0f) {
                            scaleFactor = 1.0f;
                        }
                        options.inJustDecodeBounds = false;
                        options.inSampleSize = (int) scaleFactor;
                        options.inPurgeable = true;
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    try {
                        if (info.frontCamera != 0) {
                            try {
                                Matrix matrix = new Matrix();
                                matrix.setRotate((float) CameraController.getOrientation(data));
                                matrix.postScale(-1.0f, 1.0f);
                                Bitmap scaled = Bitmaps.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                                if (scaled != bitmap) {
                                    bitmap.recycle();
                                }
                                FileOutputStream outputStream = new FileOutputStream(path);
                                scaled.compress(CompressFormat.JPEG, 80, outputStream);
                                outputStream.flush();
                                outputStream.getFD().sync();
                                outputStream.close();
                                if (scaled != null) {
                                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(scaled), key);
                                }
                                if (callback != null) {
                                    callback.run();
                                }
                                return;
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                            }
                        }
                        FileOutputStream outputStream2 = new FileOutputStream(path);
                        outputStream2.write(data);
                        outputStream2.flush();
                        outputStream2.getFD().sync();
                        outputStream2.close();
                        if (bitmap != null) {
                            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), key);
                        }
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                    }
                    if (callback != null) {
                        callback.run();
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            FileLog.m3e(e);
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
                            Camera open = Camera.open(session.cameraInfo.cameraId);
                            cameraInfo.camera = open;
                            camera = open;
                        } catch (Throwable e) {
                            session.cameraInfo.camera = null;
                            if (camera != null) {
                                camera.release();
                            }
                            FileLog.m3e(e);
                            return;
                        }
                    }
                    camera.startPreview();
                }
            });
        }
    }

    public void stopPreview(final CameraSession session) {
        if (session != null) {
            this.threadPool.execute(new Runnable() {
                @SuppressLint({"NewApi"})
                public void run() {
                    Camera camera = session.cameraInfo.camera;
                    if (camera == null) {
                        try {
                            CameraInfo cameraInfo = session.cameraInfo;
                            Camera open = Camera.open(session.cameraInfo.cameraId);
                            cameraInfo.camera = open;
                            camera = open;
                        } catch (Throwable e) {
                            session.cameraInfo.camera = null;
                            if (camera != null) {
                                camera.release();
                            }
                            FileLog.m3e(e);
                            return;
                        }
                    }
                    camera.stopPreview();
                }
            });
        }
    }

    public void openRound(CameraSession session, SurfaceTexture texture, Runnable callback, Runnable configureCallback) {
        if (session != null) {
            if (texture != null) {
                final CameraSession cameraSession = session;
                final Runnable runnable = configureCallback;
                final SurfaceTexture surfaceTexture = texture;
                final Runnable runnable2 = callback;
                this.threadPool.execute(new Runnable() {
                    @SuppressLint({"NewApi"})
                    public void run() {
                        Camera camera = cameraSession.cameraInfo.camera;
                        try {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("start creating round camera session");
                            }
                            if (camera == null) {
                                CameraInfo cameraInfo = cameraSession.cameraInfo;
                                Camera open = Camera.open(cameraSession.cameraInfo.cameraId);
                                cameraInfo.camera = open;
                                camera = open;
                            }
                            Parameters params = camera.getParameters();
                            cameraSession.configureRoundCamera();
                            if (runnable != null) {
                                runnable.run();
                            }
                            camera.setPreviewTexture(surfaceTexture);
                            camera.startPreview();
                            if (runnable2 != null) {
                                AndroidUtilities.runOnUIThread(runnable2);
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("round camera session created");
                            }
                        } catch (Throwable e) {
                            cameraSession.cameraInfo.camera = null;
                            if (camera != null) {
                                camera.release();
                            }
                            FileLog.m3e(e);
                        }
                    }
                });
                return;
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("failed to open round ");
            stringBuilder.append(session);
            stringBuilder.append(" tex = ");
            stringBuilder.append(texture);
            FileLog.m0d(stringBuilder.toString());
        }
    }

    public void open(CameraSession session, SurfaceTexture texture, Runnable callback, Runnable prestartCallback) {
        if (session != null) {
            if (texture != null) {
                final CameraSession cameraSession = session;
                final Runnable runnable = prestartCallback;
                final SurfaceTexture surfaceTexture = texture;
                final Runnable runnable2 = callback;
                this.threadPool.execute(new Runnable() {
                    @SuppressLint({"NewApi"})
                    public void run() {
                        Camera camera = cameraSession.cameraInfo.camera;
                        if (camera == null) {
                            try {
                                CameraInfo cameraInfo = cameraSession.cameraInfo;
                                Camera open = Camera.open(cameraSession.cameraInfo.cameraId);
                                cameraInfo.camera = open;
                                camera = open;
                            } catch (Throwable e) {
                                cameraSession.cameraInfo.camera = null;
                                if (camera != null) {
                                    camera.release();
                                }
                                FileLog.m3e(e);
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
                            cameraSession.checkFlashMode((String) CameraController.this.availableFlashModes.get(0));
                        }
                        if (runnable != null) {
                            runnable.run();
                        }
                        cameraSession.configurePhotoCamera();
                        camera.setPreviewTexture(surfaceTexture);
                        camera.startPreview();
                        if (runnable2 != null) {
                            AndroidUtilities.runOnUIThread(runnable2);
                        }
                    }
                });
            }
        }
    }

    public void recordVideo(CameraSession session, File path, VideoTakeCallback callback, Runnable onVideoStartRecord) {
        CameraSession cameraSession = session;
        if (cameraSession != null) {
            CameraInfo info = cameraSession.cameraInfo;
            final Camera camera = info.camera;
            final CameraSession cameraSession2 = cameraSession;
            final File file = path;
            final CameraInfo cameraInfo = info;
            final VideoTakeCallback videoTakeCallback = callback;
            final Runnable runnable = onVideoStartRecord;
            this.threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        if (camera != null) {
                            try {
                                Parameters params = camera.getParameters();
                                params.setFlashMode(cameraSession2.getCurrentFlashMode().equals("on") ? "torch" : "off");
                                camera.setParameters(params);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            camera.unlock();
                            try {
                                CameraController.this.recorder = new MediaRecorder();
                                CameraController.this.recorder.setCamera(camera);
                                CameraController.this.recorder.setVideoSource(1);
                                CameraController.this.recorder.setAudioSource(5);
                                cameraSession2.configureRecorder(1, CameraController.this.recorder);
                                CameraController.this.recorder.setOutputFile(file.getAbsolutePath());
                                CameraController.this.recorder.setMaxFileSize(NUM);
                                CameraController.this.recorder.setVideoFrameRate(30);
                                CameraController.this.recorder.setMaxDuration(0);
                                Size pictureSize = CameraController.chooseOptimalSize(cameraInfo.getPictureSizes(), 720, 480, new Size(16, 9));
                                CameraController.this.recorder.setVideoEncodingBitRate(1800000);
                                CameraController.this.recorder.setVideoSize(pictureSize.getWidth(), pictureSize.getHeight());
                                CameraController.this.recorder.setOnInfoListener(CameraController.this);
                                CameraController.this.recorder.prepare();
                                CameraController.this.recorder.start();
                                CameraController.this.onVideoTakeCallback = videoTakeCallback;
                                CameraController.this.recordedFile = file.getAbsolutePath();
                                if (runnable != null) {
                                    AndroidUtilities.runOnUIThread(runnable);
                                }
                            } catch (Throwable e2) {
                                CameraController.this.recorder.release();
                                CameraController.this.recorder = null;
                                FileLog.m3e(e2);
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                    }
                }
            });
        }
    }

    private void finishRecordingVideo() {
        Throwable e;
        long duration;
        Bitmap bitmap;
        StringBuilder stringBuilder;
        File cacheFile;
        final long durationFinal;
        final File file;
        final Bitmap bitmap2;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        long duration2 = 0;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(this.recordedFile);
            String d = mediaMetadataRetriever.extractMetadata(9);
            if (d != null) {
                duration2 = (long) ((int) Math.ceil((double) (((float) Long.parseLong(d)) / 1000.0f)));
            }
            if (mediaMetadataRetriever != null) {
                try {
                    mediaMetadataRetriever.release();
                } catch (Exception e2) {
                    e = e2;
                    FileLog.m3e(e);
                    duration = duration2;
                    bitmap = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("-2147483648_");
                    stringBuilder.append(SharedConfig.getLastLocalId());
                    stringBuilder.append(".jpg");
                    cacheFile = new File(FileLoader.getDirectory(4), stringBuilder.toString());
                    bitmap.compress(CompressFormat.JPEG, 55, new FileOutputStream(cacheFile));
                    SharedConfig.saveConfig();
                    durationFinal = duration;
                    file = cacheFile;
                    bitmap2 = bitmap;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (CameraController.this.onVideoTakeCallback != null) {
                                String path = file.getAbsolutePath();
                                if (bitmap2 != null) {
                                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), Utilities.MD5(path));
                                }
                                CameraController.this.onVideoTakeCallback.onFinishVideoRecording(path, durationFinal);
                                CameraController.this.onVideoTakeCallback = null;
                            }
                        }
                    });
                }
            }
        } catch (Throwable e3) {
            FileLog.m3e(e3);
            if (mediaMetadataRetriever != null) {
                try {
                    mediaMetadataRetriever.release();
                } catch (Exception e4) {
                    e3 = e4;
                    FileLog.m3e(e3);
                    duration = duration2;
                    bitmap = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("-2147483648_");
                    stringBuilder.append(SharedConfig.getLastLocalId());
                    stringBuilder.append(".jpg");
                    cacheFile = new File(FileLoader.getDirectory(4), stringBuilder.toString());
                    bitmap.compress(CompressFormat.JPEG, 55, new FileOutputStream(cacheFile));
                    SharedConfig.saveConfig();
                    durationFinal = duration;
                    file = cacheFile;
                    bitmap2 = bitmap;
                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                }
            }
        } catch (Throwable th) {
            if (mediaMetadataRetriever != null) {
                try {
                    mediaMetadataRetriever.release();
                } catch (Throwable e5) {
                    FileLog.m3e(e5);
                }
            }
        }
        duration = duration2;
        bitmap = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
        stringBuilder = new StringBuilder();
        stringBuilder.append("-2147483648_");
        stringBuilder.append(SharedConfig.getLastLocalId());
        stringBuilder.append(".jpg");
        cacheFile = new File(FileLoader.getDirectory(4), stringBuilder.toString());
        try {
            bitmap.compress(CompressFormat.JPEG, 55, new FileOutputStream(cacheFile));
        } catch (Throwable e6) {
            FileLog.m3e(e6);
        }
        SharedConfig.saveConfig();
        durationFinal = duration;
        file = cacheFile;
        bitmap2 = bitmap;
        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
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
                finishRecordingVideo();
            }
        }
    }

    public void stopVideoRecording(final CameraSession session, final boolean abandon) {
        this.threadPool.execute(new Runnable() {
            public void run() {
                try {
                    final Camera camera = session.cameraInfo.camera;
                    if (!(camera == null || CameraController.this.recorder == null)) {
                        MediaRecorder tempRecorder = CameraController.this.recorder;
                        CameraController.this.recorder = null;
                        try {
                            tempRecorder.stop();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        try {
                            tempRecorder.release();
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                        try {
                            camera.reconnect();
                            camera.startPreview();
                        } catch (Throwable e22) {
                            FileLog.m3e(e22);
                        }
                        try {
                            session.stopVideoRecording();
                        } catch (Throwable e222) {
                            FileLog.m3e(e222);
                        }
                    }
                    try {
                        Parameters params = camera.getParameters();
                        params.setFlashMode("off");
                        camera.setParameters(params);
                    } catch (Throwable e3) {
                        FileLog.m3e(e3);
                    }
                    CameraController.this.threadPool.execute(new Runnable() {
                        public void run() {
                            try {
                                Parameters params = camera.getParameters();
                                params.setFlashMode(session.getCurrentFlashMode());
                                camera.setParameters(params);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    });
                    if (abandon || CameraController.this.onVideoTakeCallback == null) {
                        CameraController.this.onVideoTakeCallback = null;
                    } else {
                        CameraController.this.finishRecordingVideo();
                    }
                } catch (Exception e4) {
                }
            }
        });
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
