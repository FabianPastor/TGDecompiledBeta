package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.google.android.gms.gcm.Task;
import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.GL;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraInfo;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.Size;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSink;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.video.Mp4Movie;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;

@TargetApi(18)
public class InstantCameraView extends FrameLayout implements NotificationCenterDelegate {
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   gl_FragColor = texture2D(sTexture, vTextureCoord) * ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0)) * alpha;\n}\n";
    private static final int MSG_AUDIOFRAME_AVAILABLE = 3;
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_VIDEOFRAME_AVAILABLE = 2;
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    private View actionBar;
    private AnimatorSet animatorSet;
    private Size aspectRatio = new Size(16, 9);
    private ChatActivity baseFragment;
    private FrameLayout cameraContainer;
    private File cameraFile;
    private CameraSession cameraSession;
    private int[] cameraTexture = new int[1];
    private float cameraTextureAlpha = 1.0f;
    private CameraGLThread cameraThread;
    private boolean cancelled;
    private boolean deviceHasGoodCamera;
    private boolean isFrontface = true;
    private float[] mMVPMatrix = new float[16];
    private float[] mSTMatrix = new float[16];
    private float[] moldSTMatrix = new float[16];
    private int[] oldCameraTexture = new int[1];
    private Paint paint;
    private int[] position = new int[2];
    private Size previewSize;
    private float progress;
    private long recordStartTime;
    private long recordedTime;
    private boolean recording;
    private RectF rect;
    private boolean requestingPermissions;
    private float scaleX;
    private float scaleY;
    private CameraInfo selectedCamera;
    private int skippedTime;
    private ImageView switchCameraButton;
    private FloatBuffer textureBuffer;
    private TextureView textureView;
    private Runnable timerRunnable = new Runnable() {
        public void run() {
            if (InstantCameraView.this.recording) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(System.currentTimeMillis() - InstantCameraView.this.recordStartTime), Double.valueOf(0.0d));
                AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable, 50);
            }
        }
    };
    private FloatBuffer vertexBuffer;

    private class AudioBufferInfo {
        byte[] buffer;
        boolean last;
        int lastWroteBuffer;
        long[] offset;
        int[] read;
        int results;

        private AudioBufferInfo() {
            this.buffer = new byte[CacheDataSink.DEFAULT_BUFFER_SIZE];
            this.offset = new long[10];
            this.read = new int[10];
        }
    }

    private static class EncoderHandler extends Handler {
        private WeakReference<VideoRecorder> mWeakEncoder;

        public EncoderHandler(VideoRecorder encoder) {
            this.mWeakEncoder = new WeakReference(encoder);
        }

        public void handleMessage(Message inputMessage) {
            boolean z = false;
            int what = inputMessage.what;
            Object obj = inputMessage.obj;
            VideoRecorder encoder = (VideoRecorder) this.mWeakEncoder.get();
            if (encoder != null) {
                switch (what) {
                    case 0:
                        try {
                            FileLog.e("start encoder");
                            encoder.prepareEncoder();
                            return;
                        } catch (Throwable e) {
                            FileLog.e(e);
                            encoder.handleStopRecording(false);
                            Looper.myLooper().quit();
                            return;
                        }
                    case 1:
                        FileLog.e("stop encoder");
                        if (inputMessage.arg1 != 0) {
                            z = true;
                        }
                        encoder.handleStopRecording(z);
                        return;
                    case 2:
                        encoder.handleVideoFrameAvailable((((long) inputMessage.arg1) << 32) | (((long) inputMessage.arg2) & 4294967295L));
                        return;
                    case 3:
                        encoder.handleAudioFrameAvailable((AudioBufferInfo) inputMessage.obj);
                        return;
                    default:
                        return;
                }
            }
        }

        public void exit() {
            Looper.myLooper().quit();
        }
    }

    private class VideoRecorder implements Runnable {
        private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
        private static final int FRAME_RATE = 30;
        private static final int IFRAME_INTERVAL = 5;
        private static final String VIDEO_MIME_TYPE = "video/avc";
        private int alphaHandle;
        private BufferInfo audioBufferInfo;
        private MediaCodec audioEncoder;
        private long audioStartTime;
        private int audioTrackIndex;
        private ArrayBlockingQueue<AudioBufferInfo> buffers;
        private ArrayList<AudioBufferInfo> buffersToWrite;
        private long currentTimestamp;
        private int drawProgram;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private volatile EncoderHandler handler;
        private long lastCommitedFrameTime;
        private long lastTimestamp;
        private MP4Builder mediaMuxer;
        private int positionHandle;
        private boolean ready;
        private Runnable recorderRunnable;
        private volatile boolean running;
        private int scaleXHandle;
        private int scaleYHandle;
        private volatile boolean sendWhenDone;
        private EGLContext sharedEglContext;
        private Surface surface;
        private final Object sync;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private int videoBitrate;
        private BufferInfo videoBufferInfo;
        private boolean videoConvertFirstWrite;
        private MediaCodec videoEncoder;
        private volatile long videoEndRecordTime;
        private File videoFile;
        private int videoHeight;
        private long videoStartRecordTime;
        private int videoTrackIndex;
        private int videoWidth;

        private VideoRecorder() {
            this.videoConvertFirstWrite = true;
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            this.buffersToWrite = new ArrayList();
            this.videoTrackIndex = -5;
            this.audioTrackIndex = -5;
            this.videoStartRecordTime = -1;
            this.audioStartTime = -1;
            this.currentTimestamp = 0;
            this.lastTimestamp = -1;
            this.sync = new Object();
            this.buffers = new ArrayBlockingQueue(5);
            this.recorderRunnable = new Runnable() {
                /* JADX WARNING: inconsistent code. */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    int a;
                    int recordBufferSize = AudioRecord.getMinBufferSize(44100, 16, 2);
                    if (recordBufferSize <= 0) {
                        recordBufferSize = 3584;
                    }
                    int bufferSize = 49152;
                    if (49152 < recordBufferSize) {
                        bufferSize = (((recordBufferSize / 2048) + 1) * 2048) * 2;
                    }
                    for (a = 0; a < 3; a++) {
                        VideoRecorder.this.buffers.add(new AudioBufferInfo());
                    }
                    AudioRecord audioRecorder = new AudioRecord(1, 44100, 16, 2, bufferSize);
                    audioRecorder.startRecording();
                    boolean done = false;
                    while (!done) {
                        AudioBufferInfo buffer;
                        if (!(VideoRecorder.this.running || audioRecorder.getRecordingState() == 1)) {
                            try {
                                audioRecorder.stop();
                            } catch (Exception e) {
                                done = true;
                            }
                        }
                        if (VideoRecorder.this.buffers.isEmpty()) {
                            buffer = new AudioBufferInfo();
                        } else {
                            buffer = (AudioBufferInfo) VideoRecorder.this.buffers.poll();
                        }
                        buffer.lastWroteBuffer = 0;
                        buffer.results = 10;
                        a = 0;
                        while (a < 10) {
                            long audioPresentationTimeNs = System.nanoTime();
                            int readResult = audioRecorder.read(buffer.buffer, a * 2048, 2048);
                            if (readResult <= 0) {
                                buffer.results = a;
                                if (!VideoRecorder.this.running) {
                                    buffer.last = true;
                                }
                                if (buffer.results < 0 || buffer.last) {
                                    if (!VideoRecorder.this.running && buffer.results < 10) {
                                        done = true;
                                    }
                                    VideoRecorder.this.handler.sendMessage(VideoRecorder.this.handler.obtainMessage(3, buffer));
                                } else if (VideoRecorder.this.running) {
                                    try {
                                        VideoRecorder.this.buffers.put(buffer);
                                    } catch (Exception e2) {
                                    }
                                } else {
                                    done = true;
                                }
                            } else {
                                buffer.offset[a] = audioPresentationTimeNs;
                                buffer.read[a] = readResult;
                                a++;
                            }
                        }
                        if (buffer.results < 0) {
                        }
                        done = true;
                        VideoRecorder.this.handler.sendMessage(VideoRecorder.this.handler.obtainMessage(3, buffer));
                    }
                    try {
                        audioRecorder.release();
                    } catch (Throwable e3) {
                        FileLog.e(e3);
                    }
                    VideoRecorder.this.handler.sendMessage(VideoRecorder.this.handler.obtainMessage(1, VideoRecorder.this.sendWhenDone ? 1 : 0, 0));
                }
            };
        }

        public void startRecording(File outputFile, int width, int height, int bitRate, EGLContext sharedContext) {
            this.videoFile = outputFile;
            this.videoWidth = width;
            this.videoHeight = height;
            this.videoBitrate = bitRate;
            this.sharedEglContext = sharedContext;
            synchronized (this.sync) {
                if (this.running) {
                    return;
                }
                this.running = true;
                new Thread(this, "TextureMovieEncoder").start();
                while (!this.ready) {
                    try {
                        this.sync.wait();
                    } catch (InterruptedException e) {
                    }
                }
                this.handler.sendMessage(this.handler.obtainMessage(0));
            }
        }

        public void stopRecording(boolean send) {
            int i;
            EncoderHandler encoderHandler = this.handler;
            EncoderHandler encoderHandler2 = this.handler;
            if (send) {
                i = 1;
            } else {
                i = 0;
            }
            encoderHandler.sendMessage(encoderHandler2.obtainMessage(1, i, 0));
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void frameAvailable(SurfaceTexture st) {
            synchronized (this.sync) {
                if (!this.ready) {
                }
            }
        }

        public void run() {
            Looper.prepare();
            synchronized (this.sync) {
                this.handler = new EncoderHandler(this);
                this.ready = true;
                this.sync.notify();
            }
            Looper.loop();
            synchronized (this.sync) {
                this.ready = false;
            }
        }

        private void handleAudioFrameAvailable(AudioBufferInfo input) {
            if (this.audioStartTime == -1) {
                this.audioStartTime = input.offset[0];
            }
            this.buffersToWrite.add(input);
            if (this.buffersToWrite.size() > 1) {
                input = (AudioBufferInfo) this.buffersToWrite.get(0);
            }
            try {
                drainEncoder(false);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            boolean isLast = false;
            while (input != null) {
                int inputBufferIndex = this.audioEncoder.dequeueInputBuffer(0);
                if (inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer;
                    if (VERSION.SDK_INT >= 21) {
                        inputBuffer = this.audioEncoder.getInputBuffer(inputBufferIndex);
                    } else {
                        try {
                            inputBuffer = this.audioEncoder.getInputBuffers()[inputBufferIndex];
                            inputBuffer.clear();
                        } catch (Throwable e2) {
                            FileLog.e(e2);
                            return;
                        }
                    }
                    long startWriteTime = input.offset[input.lastWroteBuffer];
                    int count = 0;
                    for (int a = input.lastWroteBuffer; a < input.results; a++) {
                        if (inputBuffer.remaining() < input.read[a]) {
                            input.lastWroteBuffer = a;
                            input = null;
                            break;
                        }
                        inputBuffer.put(input.buffer, a * 2048, input.read[a]);
                        count++;
                        if (a >= input.results - 1) {
                            this.buffersToWrite.remove(input);
                            this.buffers.put(input);
                            if (this.buffersToWrite.isEmpty()) {
                                isLast = input.last;
                                input = null;
                                break;
                            }
                            input = (AudioBufferInfo) this.buffersToWrite.get(0);
                        }
                    }
                    this.audioEncoder.queueInputBuffer(inputBufferIndex, 0, inputBuffer.position(), (startWriteTime - this.audioStartTime) / 1000, isLast ? 4 : 0);
                }
            }
        }

        private void handleVideoFrameAvailable(long timestampNanos) {
            long dt;
            long alphaDt;
            if (this.videoStartRecordTime == -1) {
                this.videoStartRecordTime = System.nanoTime();
            }
            try {
                drainEncoder(false);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (this.lastTimestamp == -1) {
                this.lastTimestamp = timestampNanos;
                if (this.currentTimestamp != 0) {
                    dt = System.nanoTime() - this.lastCommitedFrameTime;
                    alphaDt = 0;
                } else {
                    dt = 0;
                    alphaDt = 0;
                }
            } else {
                dt = timestampNanos - this.lastTimestamp;
                alphaDt = dt;
                this.lastTimestamp = timestampNanos;
            }
            InstantCameraView.this.skippedTime = (int) (((long) InstantCameraView.this.skippedTime) + dt);
            this.currentTimestamp += dt;
            this.lastCommitedFrameTime = System.nanoTime();
            GLES20.glUseProgram(this.drawProgram);
            GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, InstantCameraView.this.vertexBuffer);
            GLES20.glEnableVertexAttribArray(this.positionHandle);
            GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, InstantCameraView.this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.textureHandle);
            GLES20.glUniform1f(this.scaleXHandle, InstantCameraView.this.scaleX);
            GLES20.glUniform1f(this.scaleYHandle, InstantCameraView.this.scaleY);
            GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
            GLES20.glActiveTexture(33984);
            if (InstantCameraView.this.oldCameraTexture[0] != 0) {
                GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.moldSTMatrix, 0);
                GLES20.glUniform1f(this.alphaHandle, 1.0f);
                GLES20.glBindTexture(36197, InstantCameraView.this.oldCameraTexture[0]);
                GLES20.glDrawArrays(5, 0, 4);
            }
            GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.mSTMatrix, 0);
            GLES20.glUniform1f(this.alphaHandle, InstantCameraView.this.cameraTextureAlpha);
            GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glDisableVertexAttribArray(this.positionHandle);
            GLES20.glDisableVertexAttribArray(this.textureHandle);
            GLES20.glBindTexture(36197, 0);
            GLES20.glUseProgram(0);
            EGLExt.eglPresentationTimeANDROID(this.eglDisplay, this.eglSurface, this.currentTimestamp);
            EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
            if (InstantCameraView.this.oldCameraTexture[0] != 0 && InstantCameraView.this.cameraTextureAlpha < 1.0f) {
                InstantCameraView.this.cameraTextureAlpha = InstantCameraView.this.cameraTextureAlpha + (((float) alphaDt) / 2.0E8f);
                if (InstantCameraView.this.cameraTextureAlpha > 1.0f) {
                    InstantCameraView.this.cameraTextureAlpha = 1.0f;
                    GLES20.glDeleteTextures(1, InstantCameraView.this.oldCameraTexture, 0);
                    InstantCameraView.this.oldCameraTexture[0] = 0;
                }
            }
        }

        private void handleStopRecording(boolean send) {
            if (this.running) {
                this.sendWhenDone = send;
                this.running = false;
                this.videoEndRecordTime = System.nanoTime();
                return;
            }
            try {
                drainEncoder(true);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (this.videoEncoder != null) {
                try {
                    this.videoEncoder.stop();
                    this.videoEncoder.release();
                    this.videoEncoder = null;
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
            if (this.audioEncoder != null) {
                try {
                    this.audioEncoder.stop();
                    this.audioEncoder.release();
                    this.audioEncoder = null;
                } catch (Throwable e22) {
                    FileLog.e(e22);
                }
            }
            if (this.mediaMuxer != null) {
                try {
                    this.mediaMuxer.finishMovie(false);
                } catch (Throwable e222) {
                    FileLog.e(e222);
                }
            }
            if (send) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
                        videoEditedInfo.roundVideo = true;
                        InstantCameraView.this.baseFragment.sendMedia(new PhotoEntry(0, 0, 0, VideoRecorder.this.videoFile.getAbsolutePath(), 0, true), videoEditedInfo);
                        VideoRecorder.this.didWriteData(VideoRecorder.this.videoFile, true);
                    }
                });
            } else {
                FileLoader.getInstance().cancelUploadFile(this.videoFile.getAbsolutePath(), false);
                this.videoFile.delete();
            }
            EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            if (this.surface != null) {
                this.surface.release();
                this.surface = null;
            }
            if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(this.eglDisplay);
            }
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglConfig = null;
            this.handler.exit();
        }

        private void prepareEncoder() {
            try {
                this.audioBufferInfo = new BufferInfo();
                this.videoBufferInfo = new BufferInfo();
                MediaFormat audioFormat = new MediaFormat();
                audioFormat.setString("mime", "audio/mp4a-latm");
                audioFormat.setInteger("aac-profile", 2);
                audioFormat.setInteger("sample-rate", 44100);
                audioFormat.setInteger("channel-count", 1);
                audioFormat.setInteger("bitrate", 32000);
                audioFormat.setInteger("max-input-size", CacheDataSink.DEFAULT_BUFFER_SIZE);
                this.audioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
                this.audioEncoder.configure(audioFormat, null, null, 1);
                this.audioEncoder.start();
                MediaFormat format = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                format.setInteger("color-format", NUM);
                format.setInteger("bitrate", this.videoBitrate);
                format.setInteger("frame-rate", 30);
                format.setInteger("i-frame-interval", 5);
                this.videoEncoder = MediaCodec.createEncoderByType("video/avc");
                this.videoEncoder.configure(format, null, null, 1);
                this.surface = this.videoEncoder.createInputSurface();
                this.videoEncoder.start();
                new Thread(this.recorderRunnable).start();
                Mp4Movie movie = new Mp4Movie();
                movie.setCacheFile(this.videoFile);
                movie.setRotation(0);
                movie.setSize(this.videoWidth, this.videoHeight);
                this.mediaMuxer = new MP4Builder().createMovie(movie);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (!InstantCameraView.this.cancelled) {
                            try {
                                ((Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50);
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                            AndroidUtilities.lockOrientation(InstantCameraView.this.baseFragment.getParentActivity());
                            InstantCameraView.this.recording = true;
                            InstantCameraView.this.recordStartTime = System.currentTimeMillis();
                            AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStarted, new Object[0]);
                        }
                    }
                });
                if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                    throw new RuntimeException("EGL already set up");
                }
                this.eglDisplay = EGL14.eglGetDisplay(0);
                if (this.eglDisplay == EGL14.EGL_NO_DISPLAY) {
                    throw new RuntimeException("unable to get EGL14 display");
                }
                int[] version = new int[2];
                if (EGL14.eglInitialize(this.eglDisplay, version, 0, version, 1)) {
                    if (this.eglContext == EGL14.EGL_NO_CONTEXT) {
                        EGLConfig[] configs = new EGLConfig[1];
                        if (EGL14.eglChooseConfig(this.eglDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12610, 1, 12344}, 0, configs, 0, configs.length, new int[1], 0)) {
                            this.eglContext = EGL14.eglCreateContext(this.eglDisplay, configs[0], this.sharedEglContext, new int[]{12440, 2, 12344}, 0);
                            this.eglConfig = configs[0];
                        } else {
                            throw new RuntimeException("Unable to find a suitable EGLConfig");
                        }
                    }
                    EGL14.eglQueryContext(this.eglDisplay, this.eglContext, 12440, new int[1], 0);
                    if (this.eglSurface != EGL14.EGL_NO_SURFACE) {
                        throw new IllegalStateException("surface already created");
                    }
                    this.eglSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surface, new int[]{12344}, 0);
                    if (this.eglSurface == null) {
                        throw new RuntimeException("surface was null");
                    } else if (EGL14.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                        GLES20.glEnable(3042);
                        GLES20.glBlendFunc(770, 771);
                        int vertexShader = InstantCameraView.this.loadShader(35633, InstantCameraView.VERTEX_SHADER);
                        int fragmentShader = InstantCameraView.this.loadShader(35632, InstantCameraView.FRAGMENT_SHADER);
                        if (vertexShader != 0 && fragmentShader != 0) {
                            this.drawProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.drawProgram, vertexShader);
                            GLES20.glAttachShader(this.drawProgram, fragmentShader);
                            GLES20.glLinkProgram(this.drawProgram);
                            int[] linkStatus = new int[1];
                            GLES20.glGetProgramiv(this.drawProgram, 35714, linkStatus, 0);
                            if (linkStatus[0] == 0) {
                                GLES20.glDeleteProgram(this.drawProgram);
                                this.drawProgram = 0;
                                return;
                            }
                            this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                            this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                            this.scaleXHandle = GLES20.glGetUniformLocation(this.drawProgram, "scaleX");
                            this.scaleYHandle = GLES20.glGetUniformLocation(this.drawProgram, "scaleY");
                            this.alphaHandle = GLES20.glGetUniformLocation(this.drawProgram, "alpha");
                            this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                            this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                            return;
                        }
                        return;
                    } else {
                        throw new RuntimeException("eglMakeCurrent failed");
                    }
                }
                this.eglDisplay = null;
                throw new RuntimeException("unable to initialize EGL14");
            } catch (Exception ioe) {
                throw new RuntimeException(ioe);
            }
        }

        public Surface getInputSurface() {
            return this.surface;
        }

        private void didWriteData(File file, boolean last) {
            if (this.videoConvertFirstWrite) {
                FileLoader.getInstance().uploadFile(file.toString(), false, false, 1, ConnectionsManager.FileTypeVideo);
                this.videoConvertFirstWrite = false;
                return;
            }
            FileLoader.getInstance().checkUploadNewDataAvailable(file.toString(), false, last ? file.length() : 0);
        }

        public void drainEncoder(boolean endOfStream) throws Exception {
            if (endOfStream) {
                this.videoEncoder.signalEndOfInputStream();
            }
            ByteBuffer[] encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
            while (true) {
                MediaFormat newFormat;
                ByteBuffer encodedData;
                int encoderStatus = this.videoEncoder.dequeueOutputBuffer(this.videoBufferInfo, 10000);
                if (encoderStatus == -1) {
                    if (!endOfStream) {
                        break;
                    }
                } else if (encoderStatus == -3) {
                    if (VERSION.SDK_INT < 21) {
                        encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
                    }
                } else if (encoderStatus == -2) {
                    newFormat = this.videoEncoder.getOutputFormat();
                    if (this.videoTrackIndex == -5) {
                        this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat, false);
                    }
                } else if (encoderStatus < 0) {
                    continue;
                } else {
                    if (VERSION.SDK_INT < 21) {
                        encodedData = encoderOutputBuffers[encoderStatus];
                    } else {
                        encodedData = this.videoEncoder.getOutputBuffer(encoderStatus);
                    }
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                    }
                    if (this.videoBufferInfo.size > 1) {
                        if ((this.videoBufferInfo.flags & 2) == 0) {
                            if (this.mediaMuxer.writeSampleData(this.videoTrackIndex, encodedData, this.videoBufferInfo, true)) {
                                didWriteData(this.videoFile, false);
                            }
                        } else if (this.videoTrackIndex == -5) {
                            byte[] csd = new byte[this.videoBufferInfo.size];
                            encodedData.limit(this.videoBufferInfo.offset + this.videoBufferInfo.size);
                            encodedData.position(this.videoBufferInfo.offset);
                            encodedData.get(csd);
                            ByteBuffer sps = null;
                            ByteBuffer pps = null;
                            int a = this.videoBufferInfo.size - 1;
                            while (a >= 0 && a > 3) {
                                if (csd[a] == (byte) 1 && csd[a - 1] == (byte) 0 && csd[a - 2] == (byte) 0 && csd[a - 3] == (byte) 0) {
                                    sps = ByteBuffer.allocate(a - 3);
                                    pps = ByteBuffer.allocate(this.videoBufferInfo.size - (a - 3));
                                    sps.put(csd, 0, a - 3).position(0);
                                    pps.put(csd, a - 3, this.videoBufferInfo.size - (a - 3)).position(0);
                                    break;
                                }
                                a--;
                            }
                            newFormat = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                            if (!(sps == null || pps == null)) {
                                newFormat.setByteBuffer("csd-0", sps);
                                newFormat.setByteBuffer("csd-1", pps);
                            }
                            this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat, false);
                        }
                    }
                    this.videoEncoder.releaseOutputBuffer(encoderStatus, false);
                    if ((this.videoBufferInfo.flags & 4) != 0) {
                        break;
                    }
                }
            }
            encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
            while (true) {
                encoderStatus = this.audioEncoder.dequeueOutputBuffer(this.audioBufferInfo, 0);
                if (encoderStatus == -1) {
                    if (!endOfStream) {
                        return;
                    }
                    if (!(this.running || this.sendWhenDone)) {
                        return;
                    }
                } else if (encoderStatus == -3) {
                    if (VERSION.SDK_INT < 21) {
                        encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
                    }
                } else if (encoderStatus == -2) {
                    newFormat = this.audioEncoder.getOutputFormat();
                    if (this.audioTrackIndex == -5) {
                        this.audioTrackIndex = this.mediaMuxer.addTrack(newFormat, true);
                    }
                } else if (encoderStatus < 0) {
                    continue;
                } else {
                    if (VERSION.SDK_INT < 21) {
                        encodedData = encoderOutputBuffers[encoderStatus];
                    } else {
                        encodedData = this.audioEncoder.getOutputBuffer(encoderStatus);
                    }
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                    }
                    if ((this.audioBufferInfo.flags & 2) != 0) {
                        this.audioBufferInfo.size = 0;
                    }
                    if (this.audioBufferInfo.size != 0 && this.mediaMuxer.writeSampleData(this.audioTrackIndex, encodedData, this.audioBufferInfo, false)) {
                        didWriteData(this.videoFile, false);
                    }
                    this.audioEncoder.releaseOutputBuffer(encoderStatus, false);
                    if ((this.audioBufferInfo.flags & 4) != 0) {
                        return;
                    }
                }
            }
        }

        protected void finalize() throws Throwable {
            try {
                if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                    EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                    EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                    EGL14.eglReleaseThread();
                    EGL14.eglTerminate(this.eglDisplay);
                    this.eglDisplay = EGL14.EGL_NO_DISPLAY;
                    this.eglContext = EGL14.EGL_NO_CONTEXT;
                    this.eglConfig = null;
                }
                super.finalize();
            } catch (Throwable th) {
                super.finalize();
            }
        }
    }

    public class CameraGLThread extends DispatchQueue {
        private final int DO_REINIT_MESSAGE = 2;
        private final int DO_RENDER_MESSAGE = 0;
        private final int DO_SETSESSION_MESSAGE = 3;
        private final int DO_SHUTDOWN_MESSAGE = 1;
        private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private final int EGL_OPENGL_ES2_BIT = 4;
        private int alphaHandle;
        private Integer cameraId = Integer.valueOf(0);
        private SurfaceTexture cameraSurface;
        private CameraSession currentSession;
        private int drawProgram;
        private EGL10 egl10;
        private javax.microedition.khronos.egl.EGLConfig eglConfig;
        private javax.microedition.khronos.egl.EGLContext eglContext;
        private javax.microedition.khronos.egl.EGLDisplay eglDisplay;
        private javax.microedition.khronos.egl.EGLSurface eglSurface;
        private GL gl;
        private boolean initied;
        private int positionHandle;
        private boolean recording;
        private int rotationAngle;
        private int scaleXHandle;
        private int scaleYHandle;
        private SurfaceTexture surfaceTexture;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private VideoRecorder videoEncoder;

        public CameraGLThread(SurfaceTexture surface, int surfaceWidth, int surfaceHeight) {
            super("CameraGLThread");
            this.surfaceTexture = surface;
            int width = InstantCameraView.this.previewSize.getWidth();
            int height = InstantCameraView.this.previewSize.getHeight();
            float scale = ((float) surfaceWidth) / ((float) Math.min(width, height));
            width = (int) (((float) width) * scale);
            height = (int) (((float) height) * scale);
            if (width > height) {
                InstantCameraView.this.scaleX = 1.0f;
                InstantCameraView.this.scaleY = ((float) width) / ((float) surfaceHeight);
                return;
            }
            InstantCameraView.this.scaleX = ((float) height) / ((float) surfaceWidth);
            InstantCameraView.this.scaleY = 1.0f;
        }

        private boolean initGL() {
            this.egl10 = (EGL10) javax.microedition.khronos.egl.EGLContext.getEGL();
            this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.eglDisplay == EGL10.EGL_NO_DISPLAY) {
                FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                finish();
                return false;
            }
            if (this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                int[] configsCount = new int[1];
                javax.microedition.khronos.egl.EGLConfig[] configs = new javax.microedition.khronos.egl.EGLConfig[1];
                if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12344}, configs, 1, configsCount)) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    finish();
                    return false;
                } else if (configsCount[0] > 0) {
                    this.eglConfig = configs[0];
                    this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                    if (this.eglContext == null) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        finish();
                        return false;
                    } else if (this.surfaceTexture instanceof SurfaceTexture) {
                        this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
                        if (this.eglSurface == null || this.eglSurface == EGL10.EGL_NO_SURFACE) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            finish();
                            return false;
                        } else if (this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                            this.gl = this.eglContext.getGL();
                            float tX = (1.0f / InstantCameraView.this.scaleX) / 2.0f;
                            float tY = (1.0f / InstantCameraView.this.scaleY) / 2.0f;
                            float[] fArr = new float[12];
                            fArr = new float[]{-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
                            float[] texData = new float[]{0.5f - tX, 0.5f - tY, 0.5f + tX, 0.5f - tY, 0.5f - tX, 0.5f + tY, 0.5f + tX, 0.5f + tY};
                            this.videoEncoder = new VideoRecorder();
                            InstantCameraView.this.vertexBuffer = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                            InstantCameraView.this.vertexBuffer.put(fArr).position(0);
                            InstantCameraView.this.textureBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                            InstantCameraView.this.textureBuffer.put(texData).position(0);
                            Matrix.setIdentityM(InstantCameraView.this.mSTMatrix, 0);
                            int vertexShader = InstantCameraView.this.loadShader(35633, InstantCameraView.VERTEX_SHADER);
                            int fragmentShader = InstantCameraView.this.loadShader(35632, InstantCameraView.FRAGMENT_SHADER);
                            if (vertexShader == 0 || fragmentShader == 0) {
                                finish();
                                return false;
                            }
                            this.drawProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.drawProgram, vertexShader);
                            GLES20.glAttachShader(this.drawProgram, fragmentShader);
                            GLES20.glLinkProgram(this.drawProgram);
                            int[] linkStatus = new int[1];
                            GLES20.glGetProgramiv(this.drawProgram, 35714, linkStatus, 0);
                            if (linkStatus[0] == 0) {
                                GLES20.glDeleteProgram(this.drawProgram);
                                this.drawProgram = 0;
                            } else {
                                this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                                this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                                this.scaleXHandle = GLES20.glGetUniformLocation(this.drawProgram, "scaleX");
                                this.scaleYHandle = GLES20.glGetUniformLocation(this.drawProgram, "scaleY");
                                this.alphaHandle = GLES20.glGetUniformLocation(this.drawProgram, "alpha");
                                this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                                this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                            }
                            GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                            GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                            GLES20.glTexParameterf(36197, 10241, 9728.0f);
                            GLES20.glTexParameterf(36197, Task.EXTRAS_LIMIT_BYTES, 9729.0f);
                            GLES20.glTexParameteri(36197, 10242, 33071);
                            GLES20.glTexParameteri(36197, 10243, 33071);
                            Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                            if (this.rotationAngle != 0) {
                                Matrix.rotateM(InstantCameraView.this.mMVPMatrix, 0, (float) this.rotationAngle, 0.0f, 0.0f, 1.0f);
                            }
                            this.cameraSurface = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                            this.cameraSurface.setOnFrameAvailableListener(new OnFrameAvailableListener() {
                                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                                    CameraGLThread.this.requestRender();
                                }
                            });
                            InstantCameraView.this.createCamera(this.cameraSurface);
                            return true;
                        } else {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            finish();
                            return false;
                        }
                    } else {
                        finish();
                        return false;
                    }
                } else {
                    FileLog.e("eglConfig not initialized");
                    finish();
                    return false;
                }
            }
            FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
            finish();
            return false;
        }

        public void reinitForNewCamera() {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(2), 0);
            }
        }

        public void finish() {
            if (this.eglSurface != null) {
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            if (this.eglContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, this.eglContext);
                this.eglContext = null;
            }
            if (this.eglDisplay != null) {
                this.egl10.eglTerminate(this.eglDisplay);
                this.eglDisplay = null;
            }
        }

        public void setCurrentSession(CameraSession session) {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(3, session), 0);
            }
        }

        private void onDraw() {
            if (!this.initied) {
                return;
            }
            if ((this.eglContext.equals(this.egl10.eglGetCurrentContext()) && this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) || this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                this.cameraSurface.updateTexImage();
                if (!this.recording) {
                    this.videoEncoder.startRecording(InstantCameraView.this.cameraFile, PsExtractor.VIDEO_STREAM_MASK, PsExtractor.VIDEO_STREAM_MASK, 400000, EGL14.eglGetCurrentContext());
                    this.recording = true;
                    int orientation = this.currentSession.getCurrentOrientation();
                    if (orientation == 90 || orientation == 270) {
                        float temp = InstantCameraView.this.scaleX;
                        InstantCameraView.this.scaleX = InstantCameraView.this.scaleY;
                        InstantCameraView.this.scaleY = temp;
                    }
                }
                this.videoEncoder.frameAvailable(this.cameraSurface);
                this.cameraSurface.getTransformMatrix(InstantCameraView.this.mSTMatrix);
                GLES20.glUseProgram(this.drawProgram);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, InstantCameraView.this.vertexBuffer);
                GLES20.glEnableVertexAttribArray(this.positionHandle);
                GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, InstantCameraView.this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.textureHandle);
                GLES20.glUniform1f(this.scaleXHandle, InstantCameraView.this.scaleX);
                GLES20.glUniform1f(this.scaleYHandle, InstantCameraView.this.scaleY);
                GLES20.glUniform1f(this.alphaHandle, 1.0f);
                GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.mSTMatrix, 0);
                GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
                GLES20.glDrawArrays(5, 0, 4);
                GLES20.glDisableVertexAttribArray(this.positionHandle);
                GLES20.glDisableVertexAttribArray(this.textureHandle);
                GLES20.glBindTexture(36197, 0);
                GLES20.glUseProgram(0);
                this.egl10.eglSwapBuffers(this.eglDisplay, this.eglSurface);
                return;
            }
            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }

        public void handleMessage(Message inputMessage) {
            boolean z = true;
            switch (inputMessage.what) {
                case 0:
                    onDraw();
                    return;
                case 1:
                    finish();
                    if (this.recording) {
                        VideoRecorder videoRecorder = this.videoEncoder;
                        if (inputMessage.arg1 == 0) {
                            z = false;
                        }
                        videoRecorder.stopRecording(z);
                    }
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        looper.quit();
                        return;
                    }
                    return;
                case 2:
                    if (this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                        if (this.cameraSurface != null) {
                            this.cameraSurface.getTransformMatrix(InstantCameraView.this.moldSTMatrix);
                            this.cameraSurface.setOnFrameAvailableListener(null);
                            this.cameraSurface.release();
                            InstantCameraView.this.skippedTime = 0;
                            InstantCameraView.this.oldCameraTexture[0] = InstantCameraView.this.cameraTexture[0];
                            InstantCameraView.this.cameraTextureAlpha = 0.0f;
                            InstantCameraView.this.cameraTexture[0] = 0;
                        }
                        GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                        GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                        GLES20.glTexParameterf(36197, 10241, 9728.0f);
                        GLES20.glTexParameterf(36197, Task.EXTRAS_LIMIT_BYTES, 9729.0f);
                        GLES20.glTexParameteri(36197, 10242, 33071);
                        GLES20.glTexParameteri(36197, 10243, 33071);
                        this.cameraSurface = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                        this.cameraSurface.setOnFrameAvailableListener(new OnFrameAvailableListener() {
                            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                                CameraGLThread.this.requestRender();
                            }
                        });
                        InstantCameraView.this.createCamera(this.cameraSurface);
                        this.videoEncoder.lastTimestamp = -1;
                        return;
                    }
                    FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    return;
                case 3:
                    this.currentSession = (CameraSession) inputMessage.obj;
                    return;
                default:
                    return;
            }
        }

        public void shutdown(boolean send) {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                int i;
                if (send) {
                    i = 1;
                } else {
                    i = 0;
                }
                sendMessage(handler.obtainMessage(1, i, 0), 0);
            }
        }

        public void requestRender() {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(0), 0);
            }
        }
    }

    public InstantCameraView(Context context, ChatActivity parentFragment, View actionBarOverlay) {
        super(context);
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                InstantCameraView.this.getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            }
        });
        setWillNotDraw(false);
        this.actionBar = actionBarOverlay;
        this.actionBar.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                InstantCameraView.this.getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            }
        });
        setBackgroundColor(-NUM);
        this.baseFragment = parentFragment;
        this.paint = new Paint(1);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setColor(-1);
        this.rect = new RectF();
        if (VERSION.SDK_INT >= 21) {
            this.cameraContainer = new FrameLayout(context) {
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    InstantCameraView.this.invalidate();
                }
            };
            this.cameraContainer.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
                }
            });
            this.cameraContainer.setClipToOutline(true);
            this.cameraContainer.setWillNotDraw(false);
        } else {
            final Path path = new Path();
            final Paint paint = new Paint(1);
            paint.setColor(-16777216);
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            this.cameraContainer = new FrameLayout(context) {
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    InstantCameraView.this.invalidate();
                }

                protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    path.reset();
                    path.addCircle((float) (w / 2), (float) (h / 2), (float) (w / 2), Direction.CW);
                    path.toggleInverseFillType();
                }

                protected void dispatchDraw(Canvas canvas) {
                    try {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(path, paint);
                    } catch (Exception e) {
                    }
                }
            };
            this.cameraContainer.setWillNotDraw(false);
            this.cameraContainer.setLayerType(2, null);
        }
        LayoutParams layoutParams = new LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize, 17);
        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
        addView(this.cameraContainer, layoutParams);
        this.switchCameraButton = new ImageView(context);
        this.switchCameraButton.setScaleType(ScaleType.CENTER);
        addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48.0f, 83, 20.0f, 0.0f, 0.0f, 100.0f));
        this.switchCameraButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (InstantCameraView.this.cameraSession != null && InstantCameraView.this.cameraSession.isInitied() && InstantCameraView.this.cameraThread != null) {
                    InstantCameraView.this.switchCamera();
                    ObjectAnimator animator = ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "scaleX", new float[]{0.0f}).setDuration(100);
                    animator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            InstantCameraView.this.switchCameraButton.setImageResource(InstantCameraView.this.isFrontface ? R.drawable.camera_revert1 : R.drawable.camera_revert2);
                            ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "scaleX", new float[]{1.0f}).setDuration(100).start();
                        }
                    });
                    animator.start();
                }
            }
        });
        setVisibility(4);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getVisibility() != 0) {
            this.cameraContainer.setTranslationY((float) (getMeasuredHeight() / 2));
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordProgressChanged);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordProgressChanged);
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.recordProgressChanged) {
            long t = ((Long) args[0]).longValue();
            this.progress = ((float) t) / 60000.0f;
            this.recordedTime = t;
            invalidate();
        }
    }

    public void destroy(boolean async, Runnable beforeDestroyRunnable) {
        if (this.cameraSession != null) {
            this.cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, !async ? new Semaphore(0) : null, beforeDestroyRunnable);
        }
    }

    public void checkCamera(boolean request) {
        if (this.baseFragment != null) {
            boolean old = this.deviceHasGoodCamera;
            if (VERSION.SDK_INT >= 23) {
                if (this.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                    if (request) {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 17);
                    }
                    this.deviceHasGoodCamera = false;
                } else {
                    CameraController.getInstance().initCamera();
                    this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
                }
            } else if (VERSION.SDK_INT >= 16) {
                CameraController.getInstance().initCamera();
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            }
            if (this.deviceHasGoodCamera && this.baseFragment != null) {
                showCamera();
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        float x = this.cameraContainer.getX();
        float y = this.cameraContainer.getY();
        this.rect.set(x - ((float) AndroidUtilities.dp(8.0f)), y - ((float) AndroidUtilities.dp(8.0f)), (((float) this.cameraContainer.getMeasuredWidth()) + x) + ((float) AndroidUtilities.dp(8.0f)), (((float) this.cameraContainer.getMeasuredHeight()) + y) + ((float) AndroidUtilities.dp(8.0f)));
        if (this.progress != 0.0f) {
            canvas.drawArc(this.rect, -90.0f, this.progress * 360.0f, false, this.paint);
        }
        if (Theme.chat_roundVideoShadow != null) {
            int x1 = ((int) x) - AndroidUtilities.dp(3.0f);
            int y1 = ((int) y) - AndroidUtilities.dp(2.0f);
            canvas.save();
            canvas.scale(this.cameraContainer.getScaleX(), this.cameraContainer.getScaleY(), (float) (((AndroidUtilities.roundMessageSize / 2) + x1) + AndroidUtilities.dp(3.0f)), (float) (((AndroidUtilities.roundMessageSize / 2) + y1) + AndroidUtilities.dp(3.0f)));
            Theme.chat_roundVideoShadow.setAlpha((int) (this.cameraContainer.getAlpha() * 255.0f));
            Theme.chat_roundVideoShadow.setBounds(x1, y1, (AndroidUtilities.roundMessageSize + x1) + AndroidUtilities.dp(6.0f), (AndroidUtilities.roundMessageSize + y1) + AndroidUtilities.dp(6.0f));
            Theme.chat_roundVideoShadow.draw(canvas);
            canvas.restore();
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.actionBar.setVisibility(visibility);
        setAlpha(0.0f);
        this.actionBar.setAlpha(0.0f);
        this.switchCameraButton.setAlpha(0.0f);
        this.cameraContainer.setAlpha(0.0f);
        this.cameraContainer.setScaleX(0.1f);
        this.cameraContainer.setScaleY(0.1f);
        if (this.cameraContainer.getMeasuredWidth() != 0) {
            this.cameraContainer.setPivotX((float) (this.cameraContainer.getMeasuredWidth() / 2));
            this.cameraContainer.setPivotY((float) (this.cameraContainer.getMeasuredHeight() / 2));
        }
    }

    public void showCamera() {
        if (this.textureView == null) {
            this.switchCameraButton.setImageResource(R.drawable.camera_revert1);
            this.isFrontface = true;
            this.selectedCamera = null;
            this.recordedTime = 0;
            this.progress = 0.0f;
            this.cancelled = false;
            if (initCamera()) {
                MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
                this.cameraFile = new File(FileLoader.getInstance().getDirectory(4), UserConfig.lastLocalId + ".mp4");
                UserConfig.lastLocalId--;
                UserConfig.saveConfig(false);
                this.textureView = new TextureView(getContext());
                this.textureView.setSurfaceTextureListener(new SurfaceTextureListener() {
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        if (InstantCameraView.this.cameraThread == null && surface != null && !InstantCameraView.this.cancelled) {
                            if (VERSION.SDK_INT < 23 || InstantCameraView.this.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                                InstantCameraView.this.cameraThread = new CameraGLThread(surface, width, height);
                                return;
                            }
                            InstantCameraView.this.requestingPermissions = true;
                            InstantCameraView.this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 21);
                        }
                    }

                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    }

                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                        if (InstantCameraView.this.cameraThread != null) {
                            InstantCameraView.this.cameraThread.shutdown(false);
                            InstantCameraView.this.cameraThread = null;
                        }
                        if (InstantCameraView.this.cameraSession != null) {
                            CameraController.getInstance().close(InstantCameraView.this.cameraSession, null, null);
                        }
                        return true;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                    }
                });
                this.cameraContainer.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
                setVisibility(0);
                startAnimation(true);
            }
        }
    }

    public FrameLayout getCameraContainer() {
        return this.cameraContainer;
    }

    public void startAnimation(boolean open) {
        float f;
        float f2 = 1.0f;
        float f3 = 0.0f;
        if (this.animatorSet != null) {
            this.animatorSet.cancel();
        }
        this.animatorSet = new AnimatorSet();
        AnimatorSet animatorSet = this.animatorSet;
        Animator[] animatorArr = new Animator[6];
        View view = this.actionBar;
        String str = "alpha";
        float[] fArr = new float[1];
        fArr[0] = open ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, str, fArr);
        String str2 = "alpha";
        float[] fArr2 = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        fArr2[0] = f;
        animatorArr[1] = ObjectAnimator.ofFloat(this, str2, fArr2);
        FrameLayout frameLayout = this.cameraContainer;
        String str3 = "alpha";
        float[] fArr3 = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        fArr3[0] = f;
        animatorArr[2] = ObjectAnimator.ofFloat(frameLayout, str3, fArr3);
        frameLayout = this.cameraContainer;
        str3 = "scaleX";
        fArr3 = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.1f;
        }
        fArr3[0] = f;
        animatorArr[3] = ObjectAnimator.ofFloat(frameLayout, str3, fArr3);
        FrameLayout frameLayout2 = this.cameraContainer;
        str = "scaleY";
        fArr = new float[1];
        if (!open) {
            f2 = 0.1f;
        }
        fArr[0] = f2;
        animatorArr[4] = ObjectAnimator.ofFloat(frameLayout2, str, fArr);
        FrameLayout frameLayout3 = this.cameraContainer;
        str2 = "translationY";
        fArr2 = new float[2];
        if (open) {
            f = (float) (getMeasuredHeight() / 2);
        } else {
            f = 0.0f;
        }
        fArr2[0] = f;
        if (!open) {
            f3 = (float) (getMeasuredHeight() / 2);
        }
        fArr2[1] = f3;
        animatorArr[5] = ObjectAnimator.ofFloat(frameLayout3, str2, fArr2);
        animatorSet.playTogether(animatorArr);
        if (!open) {
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(InstantCameraView.this.animatorSet)) {
                        InstantCameraView.this.hideCamera(true);
                        InstantCameraView.this.setVisibility(4);
                    }
                }
            });
        }
        this.animatorSet.setDuration(180);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.start();
    }

    public Rect getCameraRect() {
        this.cameraContainer.getLocationOnScreen(this.position);
        return new Rect((float) this.position[0], (float) this.position[1], (float) this.cameraContainer.getWidth(), (float) this.cameraContainer.getHeight());
    }

    public void send() {
        boolean z = true;
        if (this.textureView != null) {
            this.cancelled = this.recordedTime < 2;
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[0]);
            if (this.cameraThread != null) {
                CameraGLThread cameraGLThread = this.cameraThread;
                if (this.cancelled) {
                    z = false;
                }
                cameraGLThread.shutdown(z);
                this.cameraThread = null;
            }
            if (this.cancelled) {
                startAnimation(false);
            }
        }
    }

    public void cancel() {
        if (this.textureView != null) {
            this.cancelled = true;
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[0]);
            if (this.cameraThread != null) {
                this.cameraThread.shutdown(false);
                this.cameraThread = null;
            }
            this.cameraFile.delete();
            this.cameraFile = null;
            startAnimation(false);
        }
    }

    public void setAlpha(float alpha) {
        ((ColorDrawable) getBackground()).setAlpha((int) (150.0f * alpha));
        this.paint.setAlpha((int) (255.0f * alpha));
        this.switchCameraButton.setAlpha(alpha);
        invalidate();
    }

    public void hideCamera(boolean async) {
        destroy(async, null);
        this.cameraContainer.removeView(this.textureView);
        this.cameraContainer.setTranslationX(0.0f);
        this.cameraContainer.setTranslationY(0.0f);
        this.textureView = null;
    }

    private void switchCamera() {
        if (this.cameraSession != null) {
            this.cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, null, null);
            this.cameraSession = null;
        }
        this.isFrontface = !this.isFrontface;
        initCamera();
        this.cameraThread.reinitForNewCamera();
    }

    private boolean initCamera() {
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        if (cameraInfos == null) {
            return false;
        }
        CameraInfo notFrontface = null;
        for (int a = 0; a < cameraInfos.size(); a++) {
            CameraInfo cameraInfo = (CameraInfo) cameraInfos.get(a);
            if (!cameraInfo.isFrontface()) {
                notFrontface = cameraInfo;
            }
            if ((this.isFrontface && cameraInfo.isFrontface()) || (!this.isFrontface && !cameraInfo.isFrontface())) {
                this.selectedCamera = cameraInfo;
                break;
            }
            notFrontface = cameraInfo;
        }
        if (this.selectedCamera == null) {
            this.selectedCamera = notFrontface;
        }
        this.previewSize = CameraController.chooseOptimalSize(this.selectedCamera.getPreviewSizes(), 480, 270, this.aspectRatio);
        return true;
    }

    private void createCamera(final SurfaceTexture surfaceTexture) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (InstantCameraView.this.cameraThread != null) {
                    Size pictureSize = CameraController.chooseOptimalSize(InstantCameraView.this.selectedCamera.getPictureSizes(), 480, 270, InstantCameraView.this.aspectRatio);
                    if (pictureSize.getWidth() >= 1280 && pictureSize.getHeight() >= 1280) {
                        InstantCameraView.this.aspectRatio = new Size(9, 16);
                        Size pictureSize2 = CameraController.chooseOptimalSize(InstantCameraView.this.selectedCamera.getPictureSizes(), 270, 480, InstantCameraView.this.aspectRatio);
                        if (pictureSize2.getWidth() < 1280 || pictureSize2.getHeight() < 1280) {
                            pictureSize = pictureSize2;
                        }
                    }
                    surfaceTexture.setDefaultBufferSize(InstantCameraView.this.previewSize.getWidth(), InstantCameraView.this.previewSize.getHeight());
                    InstantCameraView.this.cameraSession = new CameraSession(InstantCameraView.this.selectedCamera, InstantCameraView.this.previewSize, pictureSize, 256);
                    InstantCameraView.this.cameraThread.setCurrentSession(InstantCameraView.this.cameraSession);
                    CameraController.getInstance().openRound(InstantCameraView.this.cameraSession, surfaceTexture, new Runnable() {
                        public void run() {
                            if (InstantCameraView.this.cameraSession != null) {
                                InstantCameraView.this.cameraSession.setInitied();
                            }
                        }
                    });
                }
            }
        });
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compileStatus, 0);
        if (compileStatus[0] != 0) {
            return shader;
        }
        FileLog.e(GLES20.glGetShaderInfoLog(shader));
        GLES20.glDeleteShader(shader);
        return 0;
    }
}
