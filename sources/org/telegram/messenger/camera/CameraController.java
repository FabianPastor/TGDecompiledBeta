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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
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
    class C05291 implements Runnable {

        /* renamed from: org.telegram.messenger.camera.CameraController$1$1 */
        class C05251 implements Comparator<Size> {
            C05251() {
            }

            public int compare(Size size, Size size2) {
                if (size.mWidth < size2.mWidth) {
                    return 1;
                }
                if (size.mWidth > size2.mWidth) {
                    return -1;
                }
                if (size.mHeight < size2.mHeight) {
                    return 1;
                }
                if (size.mHeight > size2.mHeight) {
                    return -1;
                }
                return null;
            }
        }

        /* renamed from: org.telegram.messenger.camera.CameraController$1$2 */
        class C05262 implements Runnable {
            C05262() {
            }

            public void run() {
                CameraController.this.loadingCameras = false;
                CameraController.this.cameraInitied = true;
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.cameraInitied, new Object[0]);
            }
        }

        /* renamed from: org.telegram.messenger.camera.CameraController$1$3 */
        class C05273 implements Runnable {
            C05273() {
            }

            public void run() {
                CameraController.this.loadingCameras = false;
                CameraController.this.cameraInitied = false;
            }
        }

        C05291() {
        }

        public void run() {
            try {
                if (CameraController.this.cameraInfos == null) {
                    int numberOfCameras = Camera.getNumberOfCameras();
                    ArrayList arrayList = new ArrayList();
                    CameraInfo cameraInfo = new CameraInfo();
                    for (int i = 0; i < numberOfCameras; i++) {
                        int i2;
                        Camera.getCameraInfo(i, cameraInfo);
                        CameraInfo cameraInfo2 = new CameraInfo(i, cameraInfo);
                        Camera open = Camera.open(cameraInfo2.getCameraId());
                        Parameters parameters = open.getParameters();
                        List supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                        int i3 = 0;
                        while (true) {
                            i2 = 720;
                            if (i3 >= supportedPreviewSizes.size()) {
                                break;
                            }
                            Size size = (Size) supportedPreviewSizes.get(i3);
                            if (size.width != 1280 || size.height == 720) {
                                if (size.height < 2160 && size.width < 2160) {
                                    cameraInfo2.previewSizes.add(new Size(size.width, size.height));
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
                            i3++;
                        }
                        List supportedPictureSizes = parameters.getSupportedPictureSizes();
                        int i4 = 0;
                        while (i4 < supportedPictureSizes.size()) {
                            Size size2 = (Size) supportedPictureSizes.get(i4);
                            if (size2.width != 1280 || size2.height == r13) {
                                if (!"samsung".equals(Build.MANUFACTURER) || !"jflteuc".equals(Build.PRODUCT) || size2.width < 2048) {
                                    cameraInfo2.pictureSizes.add(new Size(size2.width, size2.height));
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
                            i4++;
                            i2 = 720;
                        }
                        open.release();
                        arrayList.add(cameraInfo2);
                        Comparator c05251 = new C05251();
                        Collections.sort(cameraInfo2.previewSizes, c05251);
                        Collections.sort(cameraInfo2.pictureSizes, c05251);
                    }
                    CameraController.this.cameraInfos = arrayList;
                }
                AndroidUtilities.runOnUIThread(new C05262());
            } catch (Throwable e) {
                Throwable th = e;
                AndroidUtilities.runOnUIThread(new C05273());
                FileLog.m3e(th);
            }
        }
    }

    /* renamed from: org.telegram.messenger.camera.CameraController$2 */
    class C05302 implements Runnable {
        C05302() {
        }

        public void run() {
            if (CameraController.this.cameraInfos != null) {
                if (!CameraController.this.cameraInfos.isEmpty()) {
                    for (int i = 0; i < CameraController.this.cameraInfos.size(); i++) {
                        CameraInfo cameraInfo = (CameraInfo) CameraController.this.cameraInfos.get(i);
                        if (cameraInfo.camera != null) {
                            cameraInfo.camera.stopPreview();
                            cameraInfo.camera.setPreviewCallbackWithBuffer(null);
                            cameraInfo.camera.release();
                            cameraInfo.camera = null;
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

        public int compare(Size size, Size size2) {
            return Long.signum((((long) size.getWidth()) * ((long) size.getHeight())) - (((long) size2.getWidth()) * ((long) size2.getHeight())));
        }
    }

    public interface VideoTakeCallback {
        void onFinishVideoRecording(String str, long j);
    }

    public static CameraController getInstance() {
        CameraController cameraController = Instance;
        if (cameraController == null) {
            synchronized (CameraController.class) {
                cameraController = Instance;
                if (cameraController == null) {
                    cameraController = new CameraController();
                    Instance = cameraController;
                }
            }
        }
        return cameraController;
    }

    public void initCamera() {
        if (!this.loadingCameras) {
            if (!this.cameraInitied) {
                this.loadingCameras = true;
                this.threadPool.execute(new C05291());
            }
        }
    }

    public boolean isCameraInitied() {
        return (!this.cameraInitied || this.cameraInfos == null || this.cameraInfos.isEmpty()) ? false : true;
    }

    public void cleanup() {
        this.threadPool.execute(new C05302());
    }

    public void close(final CameraSession cameraSession, final CountDownLatch countDownLatch, final Runnable runnable) {
        cameraSession.destroy();
        this.threadPool.execute(new Runnable() {
            public void run() {
                if (runnable != null) {
                    runnable.run();
                }
                if (cameraSession.cameraInfo.camera != null) {
                    try {
                        cameraSession.cameraInfo.camera.stopPreview();
                        cameraSession.cameraInfo.camera.setPreviewCallbackWithBuffer(null);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    try {
                        cameraSession.cameraInfo.camera.release();
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                    cameraSession.cameraInfo.camera = null;
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int getOrientation(byte[] bArr) {
        if (bArr == null) {
            return 0;
        }
        int i;
        int i2 = 0;
        while (i2 + 3 < bArr.length) {
            int pack;
            int i3;
            i = i2 + 1;
            if ((bArr[i2] & 255) == 255) {
                i2 = bArr[i] & 255;
                if (i2 != 255) {
                    i++;
                    if (i2 != 216) {
                        if (i2 != 1) {
                            if (i2 != 217) {
                                if (i2 != 218) {
                                    pack = pack(bArr, i, 2, false);
                                    if (pack >= 2) {
                                        i3 = i + pack;
                                        if (i3 <= bArr.length) {
                                            if (i2 == 225 && pack >= 8 && pack(bArr, i + 2, 4, false) == NUM && pack(bArr, i + 6, 2, false) == 0) {
                                                i2 = i + 8;
                                                i = pack - 8;
                                                break;
                                            }
                                            i2 = i3;
                                        }
                                    }
                                    return 0;
                                }
                            }
                        }
                    }
                }
                i2 = i;
            }
            i2 = i;
        }
        i = 0;
        if (i > 8) {
            pack = pack(bArr, i2, 4, false);
            if (pack != NUM && pack != NUM) {
                return 0;
            }
            boolean z = pack == NUM;
            int pack2 = pack(bArr, i2 + 4, 4, z) + 2;
            if (pack2 >= 10) {
                if (pack2 <= i) {
                    i2 += pack2;
                    i -= pack2;
                    pack2 = pack(bArr, i2 - 2, 2, z);
                    while (true) {
                        i3 = pack2 - 1;
                        if (pack2 <= 0 || i < 12) {
                            break;
                        } else if (pack(bArr, i2, 2, z) == 274) {
                            break;
                        } else {
                            i2 += 12;
                            i -= 12;
                            pack2 = i3;
                        }
                    }
                }
            }
            return 0;
        }
        return 0;
    }

    private static int pack(byte[] bArr, int i, int i2, boolean z) {
        if (z) {
            i += i2 - 1;
            z = true;
        } else {
            z = true;
        }
        int i3 = 0;
        while (true) {
            int i4 = i2 - 1;
            if (i2 <= 0) {
                return i3;
            }
            i3 = (bArr[i] & 255) | (i3 << 8);
            i += z;
            i2 = i4;
        }
    }

    public boolean takePicture(final File file, CameraSession cameraSession, final Runnable runnable) {
        if (cameraSession == null) {
            return false;
        }
        cameraSession = cameraSession.cameraInfo;
        try {
            cameraSession.camera.takePicture(null, null, new PictureCallback() {
                public void onPictureTaken(byte[] bArr, Camera camera) {
                    Bitmap decodeByteArray;
                    camera = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
                    camera = String.format(Locale.US, "%s@%d_%d", new Object[]{Utilities.MD5(file.getAbsolutePath()), Integer.valueOf(camera), Integer.valueOf(camera)});
                    try {
                        Options options = new Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
                        float max = Math.max(((float) options.outWidth) / ((float) AndroidUtilities.getPhotoSize()), ((float) options.outHeight) / ((float) AndroidUtilities.getPhotoSize()));
                        if (max < 1.0f) {
                            max = 1.0f;
                        }
                        options.inJustDecodeBounds = false;
                        options.inSampleSize = (int) max;
                        options.inPurgeable = true;
                        decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
                    } catch (Throwable th) {
                        FileLog.m3e(th);
                        decodeByteArray = null;
                    }
                    try {
                        if (cameraSession.frontCamera != 0) {
                            try {
                                Matrix matrix = new Matrix();
                                matrix.setRotate((float) CameraController.getOrientation(bArr));
                                matrix.postScale(-1.0f, 1.0f);
                                Bitmap createBitmap = Bitmaps.createBitmap(decodeByteArray, 0, 0, decodeByteArray.getWidth(), decodeByteArray.getHeight(), matrix, false);
                                if (createBitmap != decodeByteArray) {
                                    decodeByteArray.recycle();
                                }
                                OutputStream fileOutputStream = new FileOutputStream(file);
                                createBitmap.compress(CompressFormat.JPEG, 80, fileOutputStream);
                                fileOutputStream.flush();
                                fileOutputStream.getFD().sync();
                                fileOutputStream.close();
                                if (createBitmap != null) {
                                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(createBitmap), camera);
                                }
                                if (runnable != null) {
                                    runnable.run();
                                }
                                return;
                            } catch (Throwable th2) {
                                FileLog.m3e(th2);
                            }
                        }
                        FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                        fileOutputStream2.write(bArr);
                        fileOutputStream2.flush();
                        fileOutputStream2.getFD().sync();
                        fileOutputStream2.close();
                        if (decodeByteArray != null) {
                            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(decodeByteArray), camera);
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return false;
        }
    }

    public void startPreview(final CameraSession cameraSession) {
        if (cameraSession != null) {
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
                    camera.startPreview();
                }
            });
        }
    }

    public void stopPreview(final CameraSession cameraSession) {
        if (cameraSession != null) {
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
                    camera.stopPreview();
                }
            });
        }
    }

    public void openRound(CameraSession cameraSession, SurfaceTexture surfaceTexture, Runnable runnable, Runnable runnable2) {
        if (cameraSession != null) {
            if (surfaceTexture != null) {
                final CameraSession cameraSession2 = cameraSession;
                final Runnable runnable3 = runnable2;
                final SurfaceTexture surfaceTexture2 = surfaceTexture;
                final Runnable runnable4 = runnable;
                this.threadPool.execute(new Runnable() {
                    @SuppressLint({"NewApi"})
                    public void run() {
                        Camera camera = cameraSession2.cameraInfo.camera;
                        try {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("start creating round camera session");
                            }
                            if (camera == null) {
                                CameraInfo cameraInfo = cameraSession2.cameraInfo;
                                Camera open = Camera.open(cameraSession2.cameraInfo.cameraId);
                                cameraInfo.camera = open;
                                camera = open;
                            }
                            camera.getParameters();
                            cameraSession2.configureRoundCamera();
                            if (runnable3 != null) {
                                runnable3.run();
                            }
                            camera.setPreviewTexture(surfaceTexture2);
                            camera.startPreview();
                            if (runnable4 != null) {
                                AndroidUtilities.runOnUIThread(runnable4);
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("round camera session created");
                            }
                        } catch (Throwable e) {
                            cameraSession2.cameraInfo.camera = null;
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
        if (BuildVars.LOGS_ENABLED != null) {
            runnable = new StringBuilder();
            runnable.append("failed to open round ");
            runnable.append(cameraSession);
            runnable.append(" tex = ");
            runnable.append(surfaceTexture);
            FileLog.m0d(runnable.toString());
        }
    }

    public void open(CameraSession cameraSession, SurfaceTexture surfaceTexture, Runnable runnable, Runnable runnable2) {
        if (cameraSession != null) {
            if (surfaceTexture != null) {
                final CameraSession cameraSession2 = cameraSession;
                final Runnable runnable3 = runnable2;
                final SurfaceTexture surfaceTexture2 = surfaceTexture;
                final Runnable runnable4 = runnable;
                this.threadPool.execute(new Runnable() {
                    @SuppressLint({"NewApi"})
                    public void run() {
                        Camera camera = cameraSession2.cameraInfo.camera;
                        if (camera == null) {
                            try {
                                CameraInfo cameraInfo = cameraSession2.cameraInfo;
                                Camera open = Camera.open(cameraSession2.cameraInfo.cameraId);
                                cameraInfo.camera = open;
                                camera = open;
                            } catch (Throwable e) {
                                cameraSession2.cameraInfo.camera = null;
                                if (camera != null) {
                                    camera.release();
                                }
                                FileLog.m3e(e);
                                return;
                            }
                        }
                        List supportedFlashModes = camera.getParameters().getSupportedFlashModes();
                        CameraController.this.availableFlashModes.clear();
                        if (supportedFlashModes != null) {
                            for (int i = 0; i < supportedFlashModes.size(); i++) {
                                String str = (String) supportedFlashModes.get(i);
                                if (str.equals("off") || str.equals("on") || str.equals("auto")) {
                                    CameraController.this.availableFlashModes.add(str);
                                }
                            }
                            cameraSession2.checkFlashMode((String) CameraController.this.availableFlashModes.get(0));
                        }
                        if (runnable3 != null) {
                            runnable3.run();
                        }
                        cameraSession2.configurePhotoCamera();
                        camera.setPreviewTexture(surfaceTexture2);
                        camera.startPreview();
                        if (runnable4 != null) {
                            AndroidUtilities.runOnUIThread(runnable4);
                        }
                    }
                });
            }
        }
    }

    public void recordVideo(CameraSession cameraSession, File file, VideoTakeCallback videoTakeCallback, Runnable runnable) {
        if (cameraSession != null) {
            final CameraInfo cameraInfo = cameraSession.cameraInfo;
            final Camera camera = cameraInfo.camera;
            final CameraSession cameraSession2 = cameraSession;
            final File file2 = file;
            final VideoTakeCallback videoTakeCallback2 = videoTakeCallback;
            final Runnable runnable2 = runnable;
            this.threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        if (camera != null) {
                            try {
                                Parameters parameters = camera.getParameters();
                                parameters.setFlashMode(cameraSession2.getCurrentFlashMode().equals("on") ? "torch" : "off");
                                camera.setParameters(parameters);
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
                                CameraController.this.recorder.setOutputFile(file2.getAbsolutePath());
                                CameraController.this.recorder.setMaxFileSize(NUM);
                                CameraController.this.recorder.setVideoFrameRate(30);
                                CameraController.this.recorder.setMaxDuration(0);
                                Size chooseOptimalSize = CameraController.chooseOptimalSize(cameraInfo.getPictureSizes(), 720, 480, new Size(16, 9));
                                CameraController.this.recorder.setVideoEncodingBitRate(1800000);
                                CameraController.this.recorder.setVideoSize(chooseOptimalSize.getWidth(), chooseOptimalSize.getHeight());
                                CameraController.this.recorder.setOnInfoListener(CameraController.this);
                                CameraController.this.recorder.prepare();
                                CameraController.this.recorder.start();
                                CameraController.this.onVideoTakeCallback = videoTakeCallback2;
                                CameraController.this.recordedFile = file2.getAbsolutePath();
                                if (runnable2 != null) {
                                    AndroidUtilities.runOnUIThread(runnable2);
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
        final long j;
        final Bitmap createVideoThumbnail;
        StringBuilder stringBuilder;
        final File file;
        long j2 = 0;
        MediaMetadataRetriever mediaMetadataRetriever;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            try {
                mediaMetadataRetriever.setDataSource(this.recordedFile);
                String extractMetadata = mediaMetadataRetriever.extractMetadata(9);
                if (extractMetadata != null) {
                    j2 = (long) ((int) Math.ceil((double) (((float) Long.parseLong(extractMetadata)) / 1000.0f)));
                }
                if (mediaMetadataRetriever != null) {
                    try {
                        mediaMetadataRetriever.release();
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.m3e(e);
                        j = j2;
                        createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("-2147483648_");
                        stringBuilder.append(SharedConfig.getLastLocalId());
                        stringBuilder.append(".jpg");
                        file = new File(FileLoader.getDirectory(4), stringBuilder.toString());
                        createVideoThumbnail.compress(CompressFormat.JPEG, 55, new FileOutputStream(file));
                        SharedConfig.saveConfig();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (CameraController.this.onVideoTakeCallback != null) {
                                    String absolutePath = file.getAbsolutePath();
                                    if (createVideoThumbnail != null) {
                                        ImageLoader.getInstance().putImageToCache(new BitmapDrawable(createVideoThumbnail), Utilities.MD5(absolutePath));
                                    }
                                    CameraController.this.onVideoTakeCallback.onFinishVideoRecording(absolutePath, j);
                                    CameraController.this.onVideoTakeCallback = null;
                                }
                            }
                        });
                    }
                }
            } catch (Exception e3) {
                e = e3;
                try {
                    FileLog.m3e(e);
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                        } catch (Exception e4) {
                            e = e4;
                            FileLog.m3e(e);
                            j = j2;
                            createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("-2147483648_");
                            stringBuilder.append(SharedConfig.getLastLocalId());
                            stringBuilder.append(".jpg");
                            file = new File(FileLoader.getDirectory(4), stringBuilder.toString());
                            createVideoThumbnail.compress(CompressFormat.JPEG, 55, new FileOutputStream(file));
                            SharedConfig.saveConfig();
                            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                        }
                    }
                    j = j2;
                    createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("-2147483648_");
                    stringBuilder.append(SharedConfig.getLastLocalId());
                    stringBuilder.append(".jpg");
                    file = new File(FileLoader.getDirectory(4), stringBuilder.toString());
                    createVideoThumbnail.compress(CompressFormat.JPEG, 55, new FileOutputStream(file));
                    SharedConfig.saveConfig();
                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                } catch (Throwable th) {
                    e = th;
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                        } catch (Throwable e5) {
                            FileLog.m3e(e5);
                        }
                    }
                    throw e;
                }
            }
        } catch (Throwable e6) {
            Throwable th2 = e6;
            mediaMetadataRetriever = null;
            e = th2;
            FileLog.m3e(e);
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
            j = j2;
            createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
            stringBuilder = new StringBuilder();
            stringBuilder.append("-2147483648_");
            stringBuilder.append(SharedConfig.getLastLocalId());
            stringBuilder.append(".jpg");
            file = new File(FileLoader.getDirectory(4), stringBuilder.toString());
            createVideoThumbnail.compress(CompressFormat.JPEG, 55, new FileOutputStream(file));
            SharedConfig.saveConfig();
            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
        } catch (Throwable e52) {
            mediaMetadataRetriever = null;
            e = e52;
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
            throw e;
        }
        j = j2;
        createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
        stringBuilder = new StringBuilder();
        stringBuilder.append("-2147483648_");
        stringBuilder.append(SharedConfig.getLastLocalId());
        stringBuilder.append(".jpg");
        file = new File(FileLoader.getDirectory(4), stringBuilder.toString());
        try {
            createVideoThumbnail.compress(CompressFormat.JPEG, 55, new FileOutputStream(file));
        } catch (Throwable e7) {
            FileLog.m3e(e7);
        }
        SharedConfig.saveConfig();
        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
    }

    public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
        if (i == 800 || i == 801 || i == 1) {
            mediaRecorder = this.recorder;
            this.recorder = 0;
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
            }
            if (this.onVideoTakeCallback != null) {
                finishRecordingVideo();
            }
        }
    }

    public void stopVideoRecording(final CameraSession cameraSession, final boolean z) {
        this.threadPool.execute(new Runnable() {
            public void run() {
                /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                /*
                r4 = this;
                r0 = r3;	 Catch:{ Exception -> 0x0077 }
                r0 = r0.cameraInfo;	 Catch:{ Exception -> 0x0077 }
                r0 = r0.camera;	 Catch:{ Exception -> 0x0077 }
                r1 = 0;	 Catch:{ Exception -> 0x0077 }
                if (r0 == 0) goto L_0x0041;	 Catch:{ Exception -> 0x0077 }
            L_0x0009:
                r2 = org.telegram.messenger.camera.CameraController.this;	 Catch:{ Exception -> 0x0077 }
                r2 = r2.recorder;	 Catch:{ Exception -> 0x0077 }
                if (r2 == 0) goto L_0x0041;	 Catch:{ Exception -> 0x0077 }
            L_0x0011:
                r2 = org.telegram.messenger.camera.CameraController.this;	 Catch:{ Exception -> 0x0077 }
                r2 = r2.recorder;	 Catch:{ Exception -> 0x0077 }
                r3 = org.telegram.messenger.camera.CameraController.this;	 Catch:{ Exception -> 0x0077 }
                r3.recorder = r1;	 Catch:{ Exception -> 0x0077 }
                r2.stop();	 Catch:{ Exception -> 0x0020 }
                goto L_0x0024;
            L_0x0020:
                r3 = move-exception;
                org.telegram.messenger.FileLog.m3e(r3);	 Catch:{ Exception -> 0x0077 }
            L_0x0024:
                r2.release();	 Catch:{ Exception -> 0x0028 }
                goto L_0x002c;
            L_0x0028:
                r2 = move-exception;
                org.telegram.messenger.FileLog.m3e(r2);	 Catch:{ Exception -> 0x0077 }
            L_0x002c:
                r0.reconnect();	 Catch:{ Exception -> 0x0033 }
                r0.startPreview();	 Catch:{ Exception -> 0x0033 }
                goto L_0x0037;
            L_0x0033:
                r2 = move-exception;
                org.telegram.messenger.FileLog.m3e(r2);	 Catch:{ Exception -> 0x0077 }
            L_0x0037:
                r2 = r3;	 Catch:{ Exception -> 0x003d }
                r2.stopVideoRecording();	 Catch:{ Exception -> 0x003d }
                goto L_0x0041;
            L_0x003d:
                r2 = move-exception;
                org.telegram.messenger.FileLog.m3e(r2);	 Catch:{ Exception -> 0x0077 }
            L_0x0041:
                r2 = r0.getParameters();	 Catch:{ Exception -> 0x004e }
                r3 = "off";	 Catch:{ Exception -> 0x004e }
                r2.setFlashMode(r3);	 Catch:{ Exception -> 0x004e }
                r0.setParameters(r2);	 Catch:{ Exception -> 0x004e }
                goto L_0x0052;
            L_0x004e:
                r2 = move-exception;
                org.telegram.messenger.FileLog.m3e(r2);	 Catch:{ Exception -> 0x0077 }
            L_0x0052:
                r2 = org.telegram.messenger.camera.CameraController.this;	 Catch:{ Exception -> 0x0077 }
                r2 = r2.threadPool;	 Catch:{ Exception -> 0x0077 }
                r3 = new org.telegram.messenger.camera.CameraController$11$1;	 Catch:{ Exception -> 0x0077 }
                r3.<init>(r0);	 Catch:{ Exception -> 0x0077 }
                r2.execute(r3);	 Catch:{ Exception -> 0x0077 }
                r0 = r4;	 Catch:{ Exception -> 0x0077 }
                if (r0 != 0) goto L_0x0072;	 Catch:{ Exception -> 0x0077 }
            L_0x0064:
                r0 = org.telegram.messenger.camera.CameraController.this;	 Catch:{ Exception -> 0x0077 }
                r0 = r0.onVideoTakeCallback;	 Catch:{ Exception -> 0x0077 }
                if (r0 == 0) goto L_0x0072;	 Catch:{ Exception -> 0x0077 }
            L_0x006c:
                r0 = org.telegram.messenger.camera.CameraController.this;	 Catch:{ Exception -> 0x0077 }
                r0.finishRecordingVideo();	 Catch:{ Exception -> 0x0077 }
                goto L_0x0077;	 Catch:{ Exception -> 0x0077 }
            L_0x0072:
                r0 = org.telegram.messenger.camera.CameraController.this;	 Catch:{ Exception -> 0x0077 }
                r0.onVideoTakeCallback = r1;	 Catch:{ Exception -> 0x0077 }
            L_0x0077:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraController.11.run():void");
            }
        });
    }

    public static Size chooseOptimalSize(List<Size> list, int i, int i2, Size size) {
        Collection arrayList = new ArrayList();
        int width = size.getWidth();
        size = size.getHeight();
        for (int i3 = 0; i3 < list.size(); i3++) {
            Size size2 = (Size) list.get(i3);
            if (size2.getHeight() == (size2.getWidth() * size) / width && size2.getWidth() >= i && size2.getHeight() >= i2) {
                arrayList.add(size2);
            }
        }
        if (arrayList.size() > 0) {
            return (Size) Collections.min(arrayList, new CompareSizesByArea());
        }
        return (Size) Collections.max(list, new CompareSizesByArea());
    }
}
