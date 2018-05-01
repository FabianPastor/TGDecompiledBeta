package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.net.Uri;
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
import android.support.annotation.Keep;
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
import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.GL;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraInfo;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.Size;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSink;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.video.Mp4Movie;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;

@TargetApi(18)
public class InstantCameraView extends FrameLayout implements NotificationCenterDelegate {
    private static final String FRAGMENT_SCREEN_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   float coef = ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0));\n   vec3 color = texture2D(sTexture, vTextureCoord).rgb * coef + (1.0 - step(0.001, coef));\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n";
    private static final int MSG_AUDIOFRAME_AVAILABLE = 3;
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_VIDEOFRAME_AVAILABLE = 2;
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    private AnimatorSet animatorSet;
    private Size aspectRatio;
    private ChatActivity baseFragment;
    private FrameLayout cameraContainer;
    private File cameraFile;
    private volatile boolean cameraReady;
    private CameraSession cameraSession;
    private int[] cameraTexture = new int[1];
    private float cameraTextureAlpha = 1.0f;
    private CameraGLThread cameraThread;
    private boolean cancelled;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean deviceHasGoodCamera;
    private long duration;
    private InputEncryptedFile encryptedFile;
    private InputFile file;
    private boolean isFrontface = true;
    private boolean isSecretChat;
    private byte[] iv;
    private byte[] key;
    private float[] mMVPMatrix;
    private float[] mSTMatrix;
    private float[] moldSTMatrix;
    private AnimatorSet muteAnimation;
    private ImageView muteImageView;
    private int[] oldCameraTexture = new int[1];
    private Paint paint;
    private Size pictureSize;
    private int[] position = new int[2];
    private Size previewSize;
    private float progress;
    private Timer progressTimer;
    private long recordStartTime;
    private long recordedTime;
    private boolean recording;
    private RectF rect;
    private boolean requestingPermissions;
    private float scaleX;
    private float scaleY;
    private CameraInfo selectedCamera;
    private long size;
    private ImageView switchCameraButton;
    private FloatBuffer textureBuffer;
    private TextureView textureView;
    private Runnable timerRunnable = new C11761();
    private FloatBuffer vertexBuffer;
    private VideoEditedInfo videoEditedInfo;
    private VideoPlayer videoPlayer;

    /* renamed from: org.telegram.ui.Components.InstantCameraView$1 */
    class C11761 implements Runnable {
        C11761() {
        }

        public void run() {
            if (InstantCameraView.this.recording) {
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(InstantCameraView.this.duration = System.currentTimeMillis() - InstantCameraView.this.recordStartTime), Double.valueOf(0.0d));
                AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable, 50);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.InstantCameraView$2 */
    class C11782 implements OnTouchListener {

        /* renamed from: org.telegram.ui.Components.InstantCameraView$2$1 */
        class C11771 extends AnimatorListenerAdapter {
            C11771() {
            }

            public void onAnimationEnd(Animator animator) {
                if (animator.equals(InstantCameraView.this.muteAnimation) != null) {
                    InstantCameraView.this.muteAnimation = null;
                }
            }
        }

        C11782() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == null && InstantCameraView.this.baseFragment != null) {
                if (InstantCameraView.this.videoPlayer != null) {
                    view = InstantCameraView.this.videoPlayer.isMuted() ^ 1;
                    InstantCameraView.this.videoPlayer.setMute(view);
                    if (InstantCameraView.this.muteAnimation != null) {
                        InstantCameraView.this.muteAnimation.cancel();
                    }
                    InstantCameraView.this.muteAnimation = new AnimatorSet();
                    AnimatorSet access$700 = InstantCameraView.this.muteAnimation;
                    Animator[] animatorArr = new Animator[3];
                    ImageView access$800 = InstantCameraView.this.muteImageView;
                    String str = "alpha";
                    float[] fArr = new float[1];
                    float f = 1.0f;
                    fArr[0] = view != null ? 1.0f : 0.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(access$800, str, fArr);
                    access$800 = InstantCameraView.this.muteImageView;
                    str = "scaleX";
                    fArr = new float[1];
                    fArr[0] = view != null ? 1.0f : 0.5f;
                    animatorArr[1] = ObjectAnimator.ofFloat(access$800, str, fArr);
                    ImageView access$8002 = InstantCameraView.this.muteImageView;
                    String str2 = "scaleY";
                    float[] fArr2 = new float[1];
                    if (view == null) {
                        f = 0.5f;
                    }
                    fArr2[0] = f;
                    animatorArr[2] = ObjectAnimator.ofFloat(access$8002, str2, fArr2);
                    access$700.playTogether(animatorArr);
                    InstantCameraView.this.muteAnimation.addListener(new C11771());
                    InstantCameraView.this.muteAnimation.setDuration(180);
                    InstantCameraView.this.muteAnimation.setInterpolator(new DecelerateInterpolator());
                    InstantCameraView.this.muteAnimation.start();
                } else {
                    InstantCameraView.this.baseFragment.checkRecordLocked();
                }
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.InstantCameraView$5 */
    class C11815 extends ViewOutlineProvider {
        C11815() {
        }

        @TargetApi(21)
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
        }
    }

    /* renamed from: org.telegram.ui.Components.InstantCameraView$7 */
    class C11847 implements OnClickListener {

        /* renamed from: org.telegram.ui.Components.InstantCameraView$7$1 */
        class C11831 extends AnimatorListenerAdapter {
            C11831() {
            }

            public void onAnimationEnd(Animator animator) {
                InstantCameraView.this.switchCameraButton.setImageResource(InstantCameraView.this.isFrontface ? C0446R.drawable.camera_revert1 : C0446R.drawable.camera_revert2);
                ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "scaleX", new float[]{1.0f}).setDuration(100).start();
            }
        }

        C11847() {
        }

        public void onClick(View view) {
            if (!(InstantCameraView.this.cameraReady == null || InstantCameraView.this.cameraSession == null || InstantCameraView.this.cameraSession.isInitied() == null)) {
                if (InstantCameraView.this.cameraThread != null) {
                    InstantCameraView.this.switchCamera();
                    view = ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "scaleX", new float[]{0.0f}).setDuration(100);
                    view.addListener(new C11831());
                    view.start();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.InstantCameraView$8 */
    class C11858 implements SurfaceTextureListener {
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        C11858() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("camera surface available");
            }
            if (InstantCameraView.this.cameraThread == null && surfaceTexture != null && !InstantCameraView.this.cancelled) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("start create thread");
                }
                InstantCameraView.this.cameraThread = new CameraGLThread(surfaceTexture, i, i2);
            }
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (InstantCameraView.this.cameraThread != null) {
                InstantCameraView.this.cameraThread.shutdown(0);
                InstantCameraView.this.cameraThread = null;
            }
            if (InstantCameraView.this.cameraSession != null) {
                CameraController.getInstance().close(InstantCameraView.this.cameraSession, null, null);
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.InstantCameraView$9 */
    class C11869 extends AnimatorListenerAdapter {
        C11869() {
        }

        public void onAnimationEnd(Animator animator) {
            if (animator.equals(InstantCameraView.this.animatorSet) != null) {
                InstantCameraView.this.hideCamera(true);
                InstantCameraView.this.setVisibility(4);
            }
        }
    }

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

        public EncoderHandler(VideoRecorder videoRecorder) {
            this.mWeakEncoder = new WeakReference(videoRecorder);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            Object obj = message.obj;
            VideoRecorder videoRecorder = (VideoRecorder) this.mWeakEncoder.get();
            if (videoRecorder != null) {
                switch (i) {
                    case 0:
                        try {
                            if (BuildVars.LOGS_ENABLED != null) {
                                FileLog.m1e("start encoder");
                            }
                            videoRecorder.prepareEncoder();
                            break;
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                            videoRecorder.handleStopRecording(null);
                            Looper.myLooper().quit();
                            break;
                        }
                    case 1:
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m1e("stop encoder");
                        }
                        videoRecorder.handleStopRecording(message.arg1);
                        break;
                    case 2:
                        videoRecorder.handleVideoFrameAvailable((((long) message.arg1) << 32) | (((long) message.arg2) & 4294967295L), (Integer) message.obj);
                        break;
                    case 3:
                        videoRecorder.handleAudioFrameAvailable((AudioBufferInfo) message.obj);
                        break;
                    default:
                        break;
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
        private static final int IFRAME_INTERVAL = 1;
        private static final String VIDEO_MIME_TYPE = "video/avc";
        private int alphaHandle;
        private BufferInfo audioBufferInfo;
        private MediaCodec audioEncoder;
        private long audioFirst;
        private AudioRecord audioRecorder;
        private long audioStartTime;
        private boolean audioStopedByTime;
        private int audioTrackIndex;
        private boolean blendEnabled;
        private ArrayBlockingQueue<AudioBufferInfo> buffers;
        private ArrayList<AudioBufferInfo> buffersToWrite;
        private long currentTimestamp;
        private long desyncTime;
        private int drawProgram;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private volatile EncoderHandler handler;
        private Integer lastCameraId;
        private long lastCommitedFrameTime;
        private long lastTimestamp;
        private MP4Builder mediaMuxer;
        private int positionHandle;
        private boolean ready;
        private Runnable recorderRunnable;
        private volatile boolean running;
        private int scaleXHandle;
        private int scaleYHandle;
        private volatile int sendWhenDone;
        private EGLContext sharedEglContext;
        private boolean skippedFirst;
        private long skippedTime;
        private Surface surface;
        private final Object sync;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private int videoBitrate;
        private BufferInfo videoBufferInfo;
        private boolean videoConvertFirstWrite;
        private MediaCodec videoEncoder;
        private File videoFile;
        private long videoFirst;
        private int videoHeight;
        private long videoLast;
        private int videoTrackIndex;
        private int videoWidth;
        private int zeroTimeStamps;

        /* renamed from: org.telegram.ui.Components.InstantCameraView$VideoRecorder$1 */
        class C11891 implements Runnable {
            C11891() {
            }

            public void run() {
                /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                /*
                r15 = this;
                r0 = -1;
                r2 = 0;
                r4 = r0;
                r3 = r2;
            L_0x0005:
                r6 = 1;
                if (r3 != 0) goto L_0x00e4;
            L_0x0008:
                r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r7 = r7.running;
                if (r7 != 0) goto L_0x0031;
            L_0x0010:
                r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r7 = r7.audioRecorder;
                r7 = r7.getRecordingState();
                if (r7 == r6) goto L_0x0031;
            L_0x001c:
                r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;	 Catch:{ Exception -> 0x0026 }
                r7 = r7.audioRecorder;	 Catch:{ Exception -> 0x0026 }
                r7.stop();	 Catch:{ Exception -> 0x0026 }
                goto L_0x0027;
            L_0x0026:
                r3 = r6;
            L_0x0027:
                r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r7 = r7.sendWhenDone;
                if (r7 != 0) goto L_0x0031;
            L_0x002f:
                goto L_0x00e4;
            L_0x0031:
                r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r7 = r7.buffers;
                r7 = r7.isEmpty();
                if (r7 == 0) goto L_0x0048;
            L_0x003d:
                r7 = new org.telegram.ui.Components.InstantCameraView$AudioBufferInfo;
                r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r8 = org.telegram.ui.Components.InstantCameraView.this;
                r9 = 0;
                r7.<init>();
                goto L_0x0054;
            L_0x0048:
                r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r7 = r7.buffers;
                r7 = r7.poll();
                r7 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r7;
            L_0x0054:
                r7.lastWroteBuffer = r2;
                r8 = 10;
                r7.results = r8;
                r9 = r4;
                r4 = r2;
            L_0x005c:
                if (r4 >= r8) goto L_0x00a1;
            L_0x005e:
                r5 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1));
                if (r5 != 0) goto L_0x0069;
            L_0x0062:
                r9 = java.lang.System.nanoTime();
                r11 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
                r9 = r9 / r11;
            L_0x0069:
                r5 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r5 = r5.audioRecorder;
                r11 = r7.buffer;
                r12 = r4 * 2048;
                r13 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
                r5 = r5.read(r11, r12, r13);
                if (r5 > 0) goto L_0x0088;
            L_0x007b:
                r7.results = r4;
                r4 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r4 = r4.running;
                if (r4 != 0) goto L_0x00a1;
            L_0x0085:
                r7.last = r6;
                goto L_0x00a1;
            L_0x0088:
                r11 = r7.offset;
                r11[r4] = r9;
                r11 = r7.read;
                r11[r4] = r5;
                r11 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
                r11 = r11 * r5;
                r5 = 44100; // 0xac44 float:6.1797E-41 double:2.17883E-319;
                r11 = r11 / r5;
                r11 = r11 / 2;
                r11 = (long) r11;
                r13 = r9 + r11;
                r4 = r4 + 1;
                r9 = r13;
                goto L_0x005c;
            L_0x00a1:
                r4 = r9;
                r9 = r7.results;
                if (r9 >= 0) goto L_0x00c1;
            L_0x00a6:
                r9 = r7.last;
                if (r9 == 0) goto L_0x00ab;
            L_0x00aa:
                goto L_0x00c1;
            L_0x00ab:
                r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r8 = r8.running;
                if (r8 != 0) goto L_0x00b6;
            L_0x00b3:
                r3 = r6;
                goto L_0x0005;
            L_0x00b6:
                r6 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;	 Catch:{ Exception -> 0x0005 }
                r6 = r6.buffers;	 Catch:{ Exception -> 0x0005 }
                r6.put(r7);	 Catch:{ Exception -> 0x0005 }
                goto L_0x0005;
            L_0x00c1:
                r9 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r9 = r9.running;
                if (r9 != 0) goto L_0x00ce;
            L_0x00c9:
                r9 = r7.results;
                if (r9 >= r8) goto L_0x00ce;
            L_0x00cd:
                r3 = r6;
            L_0x00ce:
                r6 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r6 = r6.handler;
                r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r8 = r8.handler;
                r9 = 3;
                r7 = r8.obtainMessage(r9, r7);
                r6.sendMessage(r7);
                goto L_0x0005;
            L_0x00e4:
                r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;	 Catch:{ Exception -> 0x00ee }
                r0 = r0.audioRecorder;	 Catch:{ Exception -> 0x00ee }
                r0.release();	 Catch:{ Exception -> 0x00ee }
                goto L_0x00f2;
            L_0x00ee:
                r0 = move-exception;
                org.telegram.messenger.FileLog.m3e(r0);
            L_0x00f2:
                r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r0 = r0.handler;
                r1 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r1 = r1.handler;
                r3 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r3 = r3.sendWhenDone;
                r1 = r1.obtainMessage(r6, r3, r2);
                r0.sendMessage(r1);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.1.run():void");
            }
        }

        /* renamed from: org.telegram.ui.Components.InstantCameraView$VideoRecorder$3 */
        class C11913 implements Runnable {
            C11913() {
            }

            public void run() {
                /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                /*
                r3 = this;
                r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r0 = org.telegram.ui.Components.InstantCameraView.this;
                r0 = r0.cancelled;
                if (r0 == 0) goto L_0x000b;
            L_0x000a:
                return;
            L_0x000b:
                r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;	 Catch:{ Exception -> 0x0014 }
                r0 = org.telegram.ui.Components.InstantCameraView.this;	 Catch:{ Exception -> 0x0014 }
                r1 = 3;	 Catch:{ Exception -> 0x0014 }
                r2 = 2;	 Catch:{ Exception -> 0x0014 }
                r0.performHapticFeedback(r1, r2);	 Catch:{ Exception -> 0x0014 }
            L_0x0014:
                r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r0 = org.telegram.ui.Components.InstantCameraView.this;
                r0 = r0.baseFragment;
                r0 = r0.getParentActivity();
                org.telegram.messenger.AndroidUtilities.lockOrientation(r0);
                r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r0 = org.telegram.ui.Components.InstantCameraView.this;
                r1 = 1;
                r0.recording = r1;
                r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r0 = org.telegram.ui.Components.InstantCameraView.this;
                r1 = java.lang.System.currentTimeMillis();
                r0.recordStartTime = r1;
                r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r0 = org.telegram.ui.Components.InstantCameraView.this;
                r0 = r0.timerRunnable;
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
                r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                r0 = org.telegram.ui.Components.InstantCameraView.this;
                r0 = r0.currentAccount;
                r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
                r1 = org.telegram.messenger.NotificationCenter.recordStarted;
                r2 = 0;
                r2 = new java.lang.Object[r2];
                r0.postNotificationName(r1, r2);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.3.run():void");
            }
        }

        private VideoRecorder() {
            this.videoConvertFirstWrite = true;
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            this.buffersToWrite = new ArrayList();
            this.videoTrackIndex = -5;
            this.audioTrackIndex = -5;
            this.audioStartTime = -1;
            this.currentTimestamp = 0;
            this.lastTimestamp = -1;
            this.sync = new Object();
            this.videoFirst = -1;
            this.audioFirst = -1;
            this.lastCameraId = Integer.valueOf(null);
            this.buffers = new ArrayBlockingQueue(10);
            this.recorderRunnable = new C11891();
        }

        public void startRecording(java.io.File r3, android.opengl.EGLContext r4) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r2 = this;
            r0 = android.os.Build.DEVICE;
            if (r0 != 0) goto L_0x0006;
        L_0x0004:
            r0 = "";
        L_0x0006:
            r1 = "zeroflte";
            r1 = r0.startsWith(r1);
            if (r1 != 0) goto L_0x001d;
        L_0x000e:
            r1 = "zenlte";
            r0 = r0.startsWith(r1);
            if (r0 == 0) goto L_0x0017;
        L_0x0016:
            goto L_0x001d;
        L_0x0017:
            r0 = 240; // 0xf0 float:3.36E-43 double:1.186E-321;
            r1 = 400000; // 0x61a80 float:5.6052E-40 double:1.976263E-318;
            goto L_0x0022;
        L_0x001d:
            r0 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
            r1 = 600000; // 0x927c0 float:8.40779E-40 double:2.964394E-318;
        L_0x0022:
            r2.videoFile = r3;
            r2.videoWidth = r0;
            r2.videoHeight = r0;
            r2.videoBitrate = r1;
            r2.sharedEglContext = r4;
            r3 = r2.sync;
            monitor-enter(r3);
            r4 = r2.running;	 Catch:{ all -> 0x005f }
            if (r4 == 0) goto L_0x0035;	 Catch:{ all -> 0x005f }
        L_0x0033:
            monitor-exit(r3);	 Catch:{ all -> 0x005f }
            return;	 Catch:{ all -> 0x005f }
        L_0x0035:
            r4 = 1;	 Catch:{ all -> 0x005f }
            r2.running = r4;	 Catch:{ all -> 0x005f }
            r4 = new java.lang.Thread;	 Catch:{ all -> 0x005f }
            r0 = "TextureMovieEncoder";	 Catch:{ all -> 0x005f }
            r4.<init>(r2, r0);	 Catch:{ all -> 0x005f }
            r0 = 10;	 Catch:{ all -> 0x005f }
            r4.setPriority(r0);	 Catch:{ all -> 0x005f }
            r4.start();	 Catch:{ all -> 0x005f }
        L_0x0047:
            r4 = r2.ready;	 Catch:{ all -> 0x005f }
            if (r4 != 0) goto L_0x0051;
        L_0x004b:
            r4 = r2.sync;	 Catch:{ InterruptedException -> 0x0047 }
            r4.wait();	 Catch:{ InterruptedException -> 0x0047 }
            goto L_0x0047;
        L_0x0051:
            monitor-exit(r3);	 Catch:{ all -> 0x005f }
            r3 = r2.handler;
            r4 = r2.handler;
            r0 = 0;
            r4 = r4.obtainMessage(r0);
            r3.sendMessage(r4);
            return;
        L_0x005f:
            r4 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x005f }
            throw r4;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.startRecording(java.io.File, android.opengl.EGLContext):void");
        }

        public void stopRecording(int i) {
            this.handler.sendMessage(this.handler.obtainMessage(1, i, 0));
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void frameAvailable(SurfaceTexture surfaceTexture, Integer num, long j) {
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

        private void handleAudioFrameAvailable(AudioBufferInfo audioBufferInfo) {
            if (!this.audioStopedByTime) {
                StringBuilder stringBuilder;
                boolean z;
                AudioBufferInfo audioBufferInfo2 = audioBufferInfo;
                r1.buffersToWrite.add(audioBufferInfo2);
                boolean z2 = false;
                if (r1.audioFirst == -1) {
                    if (r1.videoFirst == -1) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("video record not yet started");
                        }
                        return;
                    }
                    while (true) {
                        int i = 0;
                        while (i < audioBufferInfo2.results) {
                            if (i == 0 && Math.abs(r1.videoFirst - audioBufferInfo2.offset[i]) > 100000000) {
                                r1.desyncTime = r1.videoFirst - audioBufferInfo2.offset[i];
                                r1.audioFirst = audioBufferInfo2.offset[i];
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("detected desync between audio and video ");
                                    stringBuilder.append(r1.desyncTime);
                                    FileLog.m0d(stringBuilder.toString());
                                }
                            } else if (audioBufferInfo2.offset[i] >= r1.videoFirst) {
                                audioBufferInfo2.lastWroteBuffer = i;
                                r1.audioFirst = audioBufferInfo2.offset[i];
                                if (BuildVars.LOGS_ENABLED) {
                                    r8 = new StringBuilder();
                                    r8.append("found first audio frame at ");
                                    r8.append(i);
                                    r8.append(" timestamp = ");
                                    r8.append(audioBufferInfo2.offset[i]);
                                    FileLog.m0d(r8.toString());
                                }
                            } else {
                                if (BuildVars.LOGS_ENABLED) {
                                    r8 = new StringBuilder();
                                    r8.append("ignore first audio frame at ");
                                    r8.append(i);
                                    r8.append(" timestamp = ");
                                    r8.append(audioBufferInfo2.offset[i]);
                                    FileLog.m0d(r8.toString());
                                }
                                i++;
                            }
                            z = true;
                            if (!z) {
                                break;
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("first audio frame not found, removing buffers ");
                                stringBuilder.append(audioBufferInfo2.results);
                                FileLog.m0d(stringBuilder.toString());
                            }
                            r1.buffersToWrite.remove(audioBufferInfo2);
                            if (!r1.buffersToWrite.isEmpty()) {
                                audioBufferInfo2 = (AudioBufferInfo) r1.buffersToWrite.get(0);
                            } else {
                                return;
                            }
                        }
                        z = false;
                        if (!z) {
                            break;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("first audio frame not found, removing buffers ");
                            stringBuilder.append(audioBufferInfo2.results);
                            FileLog.m0d(stringBuilder.toString());
                        }
                        r1.buffersToWrite.remove(audioBufferInfo2);
                        if (!r1.buffersToWrite.isEmpty()) {
                            audioBufferInfo2 = (AudioBufferInfo) r1.buffersToWrite.get(0);
                        } else {
                            return;
                        }
                    }
                }
                if (r1.audioStartTime == -1) {
                    r1.audioStartTime = audioBufferInfo2.offset[audioBufferInfo2.lastWroteBuffer];
                }
                if (r1.buffersToWrite.size() > 1) {
                    audioBufferInfo2 = (AudioBufferInfo) r1.buffersToWrite.get(0);
                }
                try {
                    drainEncoder(false);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                z = false;
                while (audioBufferInfo2 != null) {
                    try {
                        int dequeueInputBuffer = r1.audioEncoder.dequeueInputBuffer(0);
                        if (dequeueInputBuffer >= 0) {
                            ByteBuffer inputBuffer;
                            if (VERSION.SDK_INT >= 21) {
                                inputBuffer = r1.audioEncoder.getInputBuffer(dequeueInputBuffer);
                            } else {
                                inputBuffer = r1.audioEncoder.getInputBuffers()[dequeueInputBuffer];
                                inputBuffer.clear();
                            }
                            long j = audioBufferInfo2.offset[audioBufferInfo2.lastWroteBuffer];
                            for (int i2 = audioBufferInfo2.lastWroteBuffer; i2 <= audioBufferInfo2.results; i2++) {
                                if (i2 < audioBufferInfo2.results) {
                                    if (!r1.running) {
                                        ByteBuffer byteBuffer = inputBuffer;
                                        if (audioBufferInfo2.offset[i2] >= r1.videoLast - r1.desyncTime) {
                                            if (BuildVars.LOGS_ENABLED) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("stop audio encoding because of stoped video recording at ");
                                                stringBuilder.append(audioBufferInfo2.offset[i2]);
                                                stringBuilder.append(" last video ");
                                                stringBuilder.append(r1.videoLast);
                                                FileLog.m0d(stringBuilder.toString());
                                            }
                                            r1.audioStopedByTime = true;
                                            r1.buffersToWrite.clear();
                                            z = true;
                                            audioBufferInfo2 = null;
                                            inputBuffer = byteBuffer;
                                            z2 = false;
                                            break;
                                        }
                                        inputBuffer = byteBuffer;
                                    }
                                    if (inputBuffer.remaining() < audioBufferInfo2.read[i2]) {
                                        audioBufferInfo2.lastWroteBuffer = i2;
                                        audioBufferInfo2 = null;
                                        z2 = false;
                                        break;
                                    }
                                    inputBuffer.put(audioBufferInfo2.buffer, i2 * 2048, audioBufferInfo2.read[i2]);
                                }
                                if (i2 >= audioBufferInfo2.results - 1) {
                                    r1.buffersToWrite.remove(audioBufferInfo2);
                                    if (r1.running) {
                                        r1.buffers.put(audioBufferInfo2);
                                    }
                                    if (r1.buffersToWrite.isEmpty()) {
                                        z2 = false;
                                        z = audioBufferInfo2.last;
                                        audioBufferInfo2 = null;
                                        break;
                                    }
                                    z2 = false;
                                    audioBufferInfo2 = (AudioBufferInfo) r1.buffersToWrite.get(0);
                                } else {
                                    z2 = false;
                                }
                            }
                            MediaCodec mediaCodec = r1.audioEncoder;
                            int position = inputBuffer.position();
                            long j2 = 0;
                            if (j != 0) {
                                j2 = j - r1.audioStartTime;
                            }
                            mediaCodec.queueInputBuffer(dequeueInputBuffer, 0, position, j2, z ? 4 : z2);
                        }
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                }
            }
        }

        private void handleVideoFrameAvailable(long j, Integer num) {
            long currentTimeMillis;
            try {
                drainEncoder(false);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (!this.lastCameraId.equals(num)) {
                this.lastTimestamp = -1;
                this.lastCameraId = num;
            }
            long j2 = 0;
            if (this.lastTimestamp == -1) {
                this.lastTimestamp = j;
                currentTimeMillis = this.currentTimestamp != 0 ? C0542C.MICROS_PER_SECOND * (System.currentTimeMillis() - this.lastCommitedFrameTime) : 0;
            } else {
                currentTimeMillis = j - this.lastTimestamp;
                this.lastTimestamp = j;
                j2 = currentTimeMillis;
            }
            this.lastCommitedFrameTime = System.currentTimeMillis();
            if (this.skippedFirst == null) {
                this.skippedTime += currentTimeMillis;
                if (this.skippedTime >= 200000000) {
                    this.skippedFirst = true;
                } else {
                    return;
                }
            }
            this.currentTimestamp += currentTimeMillis;
            if (this.videoFirst == -1) {
                this.videoFirst = j / 1000;
                if (BuildVars.LOGS_ENABLED != null) {
                    num = new StringBuilder();
                    num.append("first video frame was at ");
                    num.append(this.videoFirst);
                    FileLog.m0d(num.toString());
                }
            }
            this.videoLast = j;
            GLES20.glUseProgram(this.drawProgram);
            GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, InstantCameraView.this.vertexBuffer);
            GLES20.glEnableVertexAttribArray(this.positionHandle);
            GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, InstantCameraView.this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.textureHandle);
            GLES20.glUniform1f(this.scaleXHandle, InstantCameraView.this.scaleX);
            GLES20.glUniform1f(this.scaleYHandle, InstantCameraView.this.scaleY);
            GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
            GLES20.glActiveTexture(33984);
            if (InstantCameraView.this.oldCameraTexture[0] != null) {
                if (this.blendEnabled == null) {
                    GLES20.glEnable(3042);
                    this.blendEnabled = true;
                }
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
            if (InstantCameraView.this.oldCameraTexture[0] != null && InstantCameraView.this.cameraTextureAlpha < NUM) {
                InstantCameraView.this.cameraTextureAlpha = InstantCameraView.this.cameraTextureAlpha + (((float) j2) / 2.0E8f);
                if (InstantCameraView.this.cameraTextureAlpha > NUM) {
                    GLES20.glDisable(3042);
                    this.blendEnabled = false;
                    InstantCameraView.this.cameraTextureAlpha = 1.0f;
                    GLES20.glDeleteTextures(1, InstantCameraView.this.oldCameraTexture, 0);
                    InstantCameraView.this.oldCameraTexture[0] = null;
                    if (InstantCameraView.this.cameraReady == null) {
                        InstantCameraView.this.cameraReady = true;
                    }
                }
            } else if (InstantCameraView.this.cameraReady == null) {
                InstantCameraView.this.cameraReady = true;
            }
        }

        private void handleStopRecording(final int i) {
            if (this.running) {
                this.sendWhenDone = i;
                this.running = false;
                return;
            }
            try {
                drainEncoder(true);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (this.videoEncoder != null) {
                try {
                    this.videoEncoder.stop();
                    this.videoEncoder.release();
                    this.videoEncoder = null;
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
            if (this.audioEncoder != null) {
                try {
                    this.audioEncoder.stop();
                    this.audioEncoder.release();
                    this.audioEncoder = null;
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
            }
            if (this.mediaMuxer != null) {
                try {
                    this.mediaMuxer.finishMovie();
                } catch (Throwable e222) {
                    FileLog.m3e(e222);
                }
            }
            if (i != 0) {
                AndroidUtilities.runOnUIThread(new Runnable() {

                    /* renamed from: org.telegram.ui.Components.InstantCameraView$VideoRecorder$2$1 */
                    class C20631 implements VideoPlayerDelegate {
                        public void onRenderedFirstFrame() {
                        }

                        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                            return false;
                        }

                        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                        }

                        public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                        }

                        C20631() {
                        }

                        public void onStateChanged(boolean z, int i) {
                            if (InstantCameraView.this.videoPlayer && InstantCameraView.this.videoPlayer.isPlaying() && i == true) {
                                z = InstantCameraView.this.videoPlayer;
                                long j = 0;
                                if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                                    j = InstantCameraView.this.videoEditedInfo.startTime;
                                }
                                z.seekTo(j);
                            }
                        }

                        public void onError(Exception exception) {
                            FileLog.m3e((Throwable) exception);
                        }
                    }

                    public void run() {
                        InstantCameraView.this.videoEditedInfo = new VideoEditedInfo();
                        InstantCameraView.this.videoEditedInfo.roundVideo = true;
                        InstantCameraView.this.videoEditedInfo.startTime = -1;
                        InstantCameraView.this.videoEditedInfo.endTime = -1;
                        InstantCameraView.this.videoEditedInfo.file = InstantCameraView.this.file;
                        InstantCameraView.this.videoEditedInfo.encryptedFile = InstantCameraView.this.encryptedFile;
                        InstantCameraView.this.videoEditedInfo.key = InstantCameraView.this.key;
                        InstantCameraView.this.videoEditedInfo.iv = InstantCameraView.this.iv;
                        InstantCameraView.this.videoEditedInfo.estimatedSize = InstantCameraView.this.size;
                        VideoEditedInfo access$2000 = InstantCameraView.this.videoEditedInfo;
                        InstantCameraView.this.videoEditedInfo.originalWidth = PsExtractor.VIDEO_STREAM_MASK;
                        access$2000.resultWidth = PsExtractor.VIDEO_STREAM_MASK;
                        access$2000 = InstantCameraView.this.videoEditedInfo;
                        InstantCameraView.this.videoEditedInfo.originalHeight = PsExtractor.VIDEO_STREAM_MASK;
                        access$2000.resultHeight = PsExtractor.VIDEO_STREAM_MASK;
                        InstantCameraView.this.videoEditedInfo.originalPath = VideoRecorder.this.videoFile.getAbsolutePath();
                        if (i == 1) {
                            InstantCameraView.this.baseFragment.sendMedia(new PhotoEntry(0, 0, 0, VideoRecorder.this.videoFile.getAbsolutePath(), 0, true), InstantCameraView.this.videoEditedInfo);
                        } else {
                            InstantCameraView.this.videoPlayer = new VideoPlayer();
                            InstantCameraView.this.videoPlayer.setDelegate(new C20631());
                            InstantCameraView.this.videoPlayer.setTextureView(InstantCameraView.this.textureView);
                            InstantCameraView.this.videoPlayer.preparePlayer(Uri.fromFile(VideoRecorder.this.videoFile), "other");
                            InstantCameraView.this.videoPlayer.play();
                            InstantCameraView.this.videoPlayer.setMute(true);
                            InstantCameraView.this.startProgressTimer();
                            AnimatorSet animatorSet = new AnimatorSet();
                            r2 = new Animator[3];
                            r2[0] = ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "alpha", new float[]{0.0f});
                            r2[1] = ObjectAnimator.ofInt(InstantCameraView.this.paint, "alpha", new int[]{0});
                            r2[2] = ObjectAnimator.ofFloat(InstantCameraView.this.muteImageView, "alpha", new float[]{1.0f});
                            animatorSet.playTogether(r2);
                            animatorSet.setDuration(180);
                            animatorSet.setInterpolator(new DecelerateInterpolator());
                            animatorSet.start();
                            InstantCameraView.this.videoEditedInfo.estimatedDuration = InstantCameraView.this.duration;
                            NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.audioDidSent, InstantCameraView.this.videoEditedInfo, VideoRecorder.this.videoFile.getAbsolutePath());
                        }
                        VideoRecorder.this.didWriteData(VideoRecorder.this.videoFile, true);
                    }
                });
            } else {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).cancelUploadFile(this.videoFile.getAbsolutePath(), false);
                this.videoFile.delete();
            }
            EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            if (this.surface != 0) {
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
            VideoRecorder videoRecorder = this;
            try {
                int minBufferSize = AudioRecord.getMinBufferSize(44100, 16, 2);
                if (minBufferSize <= 0) {
                    minBufferSize = 3584;
                }
                int i = 49152;
                if (49152 < minBufferSize) {
                    i = (((minBufferSize / 2048) + 1) * 2048) * 2;
                }
                for (int i2 = 0; i2 < 3; i2++) {
                    videoRecorder.buffers.add(new AudioBufferInfo());
                }
                videoRecorder.audioRecorder = new AudioRecord(1, 44100, 16, 2, i);
                videoRecorder.audioRecorder.startRecording();
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("initied audio record with channels ");
                    stringBuilder.append(videoRecorder.audioRecorder.getChannelCount());
                    stringBuilder.append(" sample rate = ");
                    stringBuilder.append(videoRecorder.audioRecorder.getSampleRate());
                    stringBuilder.append(" bufferSize = ");
                    stringBuilder.append(i);
                    FileLog.m0d(stringBuilder.toString());
                }
                Thread thread = new Thread(videoRecorder.recorderRunnable);
                thread.setPriority(10);
                thread.start();
                videoRecorder.audioBufferInfo = new BufferInfo();
                videoRecorder.videoBufferInfo = new BufferInfo();
                MediaFormat mediaFormat = new MediaFormat();
                mediaFormat.setString("mime", "audio/mp4a-latm");
                mediaFormat.setInteger("aac-profile", 2);
                mediaFormat.setInteger("sample-rate", 44100);
                mediaFormat.setInteger("channel-count", 1);
                mediaFormat.setInteger("bitrate", 32000);
                mediaFormat.setInteger("max-input-size", CacheDataSink.DEFAULT_BUFFER_SIZE);
                videoRecorder.audioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
                videoRecorder.audioEncoder.configure(mediaFormat, null, null, 1);
                videoRecorder.audioEncoder.start();
                videoRecorder.videoEncoder = MediaCodec.createEncoderByType("video/avc");
                MediaFormat createVideoFormat = MediaFormat.createVideoFormat("video/avc", videoRecorder.videoWidth, videoRecorder.videoHeight);
                createVideoFormat.setInteger("color-format", NUM);
                createVideoFormat.setInteger("bitrate", videoRecorder.videoBitrate);
                createVideoFormat.setInteger("frame-rate", FRAME_RATE);
                createVideoFormat.setInteger("i-frame-interval", 1);
                videoRecorder.videoEncoder.configure(createVideoFormat, null, null, 1);
                videoRecorder.surface = videoRecorder.videoEncoder.createInputSurface();
                videoRecorder.videoEncoder.start();
                Mp4Movie mp4Movie = new Mp4Movie();
                mp4Movie.setCacheFile(videoRecorder.videoFile);
                mp4Movie.setRotation(0);
                mp4Movie.setSize(videoRecorder.videoWidth, videoRecorder.videoHeight);
                videoRecorder.mediaMuxer = new MP4Builder().createMovie(mp4Movie, InstantCameraView.this.isSecretChat);
                AndroidUtilities.runOnUIThread(new C11913());
                if (videoRecorder.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                    throw new RuntimeException("EGL already set up");
                }
                videoRecorder.eglDisplay = EGL14.eglGetDisplay(0);
                if (videoRecorder.eglDisplay == EGL14.EGL_NO_DISPLAY) {
                    throw new RuntimeException("unable to get EGL14 display");
                }
                int[] iArr = new int[2];
                if (EGL14.eglInitialize(videoRecorder.eglDisplay, iArr, 0, iArr, 1)) {
                    if (videoRecorder.eglContext == EGL14.EGL_NO_CONTEXT) {
                        EGLConfig[] eGLConfigArr = new EGLConfig[1];
                        if (EGL14.eglChooseConfig(videoRecorder.eglDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12610, 1, 12344}, 0, eGLConfigArr, 0, eGLConfigArr.length, new int[1], 0)) {
                            videoRecorder.eglContext = EGL14.eglCreateContext(videoRecorder.eglDisplay, eGLConfigArr[0], videoRecorder.sharedEglContext, new int[]{12440, 2, 12344}, 0);
                            videoRecorder.eglConfig = eGLConfigArr[0];
                        } else {
                            throw new RuntimeException("Unable to find a suitable EGLConfig");
                        }
                    }
                    EGL14.eglQueryContext(videoRecorder.eglDisplay, videoRecorder.eglContext, 12440, new int[1], 0);
                    if (videoRecorder.eglSurface != EGL14.EGL_NO_SURFACE) {
                        throw new IllegalStateException("surface already created");
                    }
                    videoRecorder.eglSurface = EGL14.eglCreateWindowSurface(videoRecorder.eglDisplay, videoRecorder.eglConfig, videoRecorder.surface, new int[]{12344}, 0);
                    if (videoRecorder.eglSurface == null) {
                        throw new RuntimeException("surface was null");
                    } else if (EGL14.eglMakeCurrent(videoRecorder.eglDisplay, videoRecorder.eglSurface, videoRecorder.eglSurface, videoRecorder.eglContext)) {
                        GLES20.glBlendFunc(770, 771);
                        int access$2700 = InstantCameraView.this.loadShader(35633, InstantCameraView.VERTEX_SHADER);
                        int access$27002 = InstantCameraView.this.loadShader(35632, InstantCameraView.FRAGMENT_SHADER);
                        if (access$2700 != 0 && access$27002 != 0) {
                            videoRecorder.drawProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(videoRecorder.drawProgram, access$2700);
                            GLES20.glAttachShader(videoRecorder.drawProgram, access$27002);
                            GLES20.glLinkProgram(videoRecorder.drawProgram);
                            iArr = new int[1];
                            GLES20.glGetProgramiv(videoRecorder.drawProgram, 35714, iArr, 0);
                            if (iArr[0] == 0) {
                                GLES20.glDeleteProgram(videoRecorder.drawProgram);
                                videoRecorder.drawProgram = 0;
                                return;
                            }
                            videoRecorder.positionHandle = GLES20.glGetAttribLocation(videoRecorder.drawProgram, "aPosition");
                            videoRecorder.textureHandle = GLES20.glGetAttribLocation(videoRecorder.drawProgram, "aTextureCoord");
                            videoRecorder.scaleXHandle = GLES20.glGetUniformLocation(videoRecorder.drawProgram, "scaleX");
                            videoRecorder.scaleYHandle = GLES20.glGetUniformLocation(videoRecorder.drawProgram, "scaleY");
                            videoRecorder.alphaHandle = GLES20.glGetUniformLocation(videoRecorder.drawProgram, "alpha");
                            videoRecorder.vertexMatrixHandle = GLES20.glGetUniformLocation(videoRecorder.drawProgram, "uMVPMatrix");
                            videoRecorder.textureMatrixHandle = GLES20.glGetUniformLocation(videoRecorder.drawProgram, "uSTMatrix");
                            return;
                        }
                        return;
                    } else {
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("eglMakeCurrent failed ");
                            stringBuilder2.append(GLUtils.getEGLErrorString(EGL14.eglGetError()));
                            FileLog.m1e(stringBuilder2.toString());
                        }
                        throw new RuntimeException("eglMakeCurrent failed");
                    }
                }
                videoRecorder.eglDisplay = null;
                throw new RuntimeException("unable to initialize EGL14");
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public Surface getInputSurface() {
            return this.surface;
        }

        private void didWriteData(File file, boolean z) {
            long j = 0;
            if (this.videoConvertFirstWrite) {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).uploadFile(file.toString(), InstantCameraView.this.isSecretChat, false, 1, ConnectionsManager.FileTypeVideo);
                this.videoConvertFirstWrite = false;
                if (z) {
                    FileLoader instance = FileLoader.getInstance(InstantCameraView.this.currentAccount);
                    String file2 = file.toString();
                    boolean access$5500 = InstantCameraView.this.isSecretChat;
                    long length = file.length();
                    if (z) {
                        j = file.length();
                    }
                    instance.checkUploadNewDataAvailable(file2, access$5500, length, j);
                    return;
                }
                return;
            }
            instance = FileLoader.getInstance(InstantCameraView.this.currentAccount);
            file2 = file.toString();
            access$5500 = InstantCameraView.this.isSecretChat;
            length = file.length();
            if (z) {
                j = file.length();
            }
            instance.checkUploadNewDataAvailable(file2, access$5500, length, j);
        }

        public void drainEncoder(boolean z) throws Exception {
            VideoRecorder videoRecorder = this;
            if (z) {
                videoRecorder.videoEncoder.signalEndOfInputStream();
            }
            ByteBuffer[] outputBuffers = VERSION.SDK_INT < 21 ? videoRecorder.videoEncoder.getOutputBuffers() : null;
            while (true) {
                MediaFormat createVideoFormat;
                int dequeueOutputBuffer = videoRecorder.videoEncoder.dequeueOutputBuffer(videoRecorder.videoBufferInfo, 10000);
                if (dequeueOutputBuffer == -1) {
                    if (!z) {
                        break;
                    }
                } else if (dequeueOutputBuffer == -3) {
                    if (VERSION.SDK_INT < 21) {
                        outputBuffers = videoRecorder.videoEncoder.getOutputBuffers();
                    }
                } else if (dequeueOutputBuffer == -2) {
                    MediaFormat outputFormat = videoRecorder.videoEncoder.getOutputFormat();
                    if (videoRecorder.videoTrackIndex == -5) {
                        videoRecorder.videoTrackIndex = videoRecorder.mediaMuxer.addTrack(outputFormat, false);
                    }
                } else if (dequeueOutputBuffer < 0) {
                    continue;
                } else {
                    ByteBuffer byteBuffer;
                    if (VERSION.SDK_INT < 21) {
                        byteBuffer = outputBuffers[dequeueOutputBuffer];
                    } else {
                        byteBuffer = videoRecorder.videoEncoder.getOutputBuffer(dequeueOutputBuffer);
                    }
                    if (byteBuffer == null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("encoderOutputBuffer ");
                        stringBuilder.append(dequeueOutputBuffer);
                        stringBuilder.append(" was null");
                        throw new RuntimeException(stringBuilder.toString());
                    }
                    if (videoRecorder.videoBufferInfo.size > 1) {
                        if ((videoRecorder.videoBufferInfo.flags & 2) == 0) {
                            if (videoRecorder.mediaMuxer.writeSampleData(videoRecorder.videoTrackIndex, byteBuffer, videoRecorder.videoBufferInfo, true)) {
                                didWriteData(videoRecorder.videoFile, false);
                            }
                        } else if (videoRecorder.videoTrackIndex == -5) {
                            ByteBuffer allocate;
                            byte[] bArr = new byte[videoRecorder.videoBufferInfo.size];
                            byteBuffer.limit(videoRecorder.videoBufferInfo.offset + videoRecorder.videoBufferInfo.size);
                            byteBuffer.position(videoRecorder.videoBufferInfo.offset);
                            byteBuffer.get(bArr);
                            int i = videoRecorder.videoBufferInfo.size - 1;
                            while (i >= 0 && i > 3) {
                                if (bArr[i] == (byte) 1 && bArr[i - 1] == (byte) 0 && bArr[i - 2] == (byte) 0) {
                                    int i2 = i - 3;
                                    if (bArr[i2] == (byte) 0) {
                                        byteBuffer = ByteBuffer.allocate(i2);
                                        allocate = ByteBuffer.allocate(videoRecorder.videoBufferInfo.size - i2);
                                        byteBuffer.put(bArr, 0, i2).position(0);
                                        allocate.put(bArr, i2, videoRecorder.videoBufferInfo.size - i2).position(0);
                                        break;
                                    }
                                }
                                i--;
                            }
                            byteBuffer = null;
                            allocate = null;
                            createVideoFormat = MediaFormat.createVideoFormat("video/avc", videoRecorder.videoWidth, videoRecorder.videoHeight);
                            if (!(byteBuffer == null || allocate == null)) {
                                createVideoFormat.setByteBuffer("csd-0", byteBuffer);
                                createVideoFormat.setByteBuffer("csd-1", allocate);
                            }
                            videoRecorder.videoTrackIndex = videoRecorder.mediaMuxer.addTrack(createVideoFormat, false);
                        }
                    }
                    videoRecorder.videoEncoder.releaseOutputBuffer(dequeueOutputBuffer, false);
                    if ((videoRecorder.videoBufferInfo.flags & 4) != 0) {
                        break;
                    }
                }
            }
            if (VERSION.SDK_INT < 21) {
                outputBuffers = videoRecorder.audioEncoder.getOutputBuffers();
            }
            while (true) {
                int dequeueOutputBuffer2 = videoRecorder.audioEncoder.dequeueOutputBuffer(videoRecorder.audioBufferInfo, 0);
                if (dequeueOutputBuffer2 == -1) {
                    if (!z) {
                        return;
                    }
                    if (!videoRecorder.running && videoRecorder.sendWhenDone == 0) {
                        return;
                    }
                } else if (dequeueOutputBuffer2 == -3) {
                    if (VERSION.SDK_INT < 21) {
                        outputBuffers = videoRecorder.audioEncoder.getOutputBuffers();
                    }
                } else if (dequeueOutputBuffer2 == -2) {
                    createVideoFormat = videoRecorder.audioEncoder.getOutputFormat();
                    if (videoRecorder.audioTrackIndex == -5) {
                        videoRecorder.audioTrackIndex = videoRecorder.mediaMuxer.addTrack(createVideoFormat, true);
                    }
                } else if (dequeueOutputBuffer2 < 0) {
                    continue;
                } else {
                    ByteBuffer byteBuffer2;
                    if (VERSION.SDK_INT < 21) {
                        byteBuffer2 = outputBuffers[dequeueOutputBuffer2];
                    } else {
                        byteBuffer2 = videoRecorder.audioEncoder.getOutputBuffer(dequeueOutputBuffer2);
                    }
                    if (byteBuffer2 == null) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("encoderOutputBuffer ");
                        stringBuilder.append(dequeueOutputBuffer2);
                        stringBuilder.append(" was null");
                        throw new RuntimeException(stringBuilder.toString());
                    }
                    if ((videoRecorder.audioBufferInfo.flags & 2) != 0) {
                        videoRecorder.audioBufferInfo.size = 0;
                    }
                    if (videoRecorder.audioBufferInfo.size != 0 && videoRecorder.mediaMuxer.writeSampleData(videoRecorder.audioTrackIndex, byteBuffer2, videoRecorder.audioBufferInfo, false)) {
                        didWriteData(videoRecorder.videoFile, false);
                    }
                    videoRecorder.audioEncoder.releaseOutputBuffer(dequeueOutputBuffer2, false);
                    if ((videoRecorder.audioBufferInfo.flags & 4) != 0) {
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
        private SurfaceTexture surfaceTexture;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private VideoRecorder videoEncoder;

        /* renamed from: org.telegram.ui.Components.InstantCameraView$CameraGLThread$1 */
        class C11871 implements OnFrameAvailableListener {
            C11871() {
            }

            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                CameraGLThread.this.requestRender();
            }
        }

        /* renamed from: org.telegram.ui.Components.InstantCameraView$CameraGLThread$2 */
        class C11882 implements OnFrameAvailableListener {
            C11882() {
            }

            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                CameraGLThread.this.requestRender();
            }
        }

        public CameraGLThread(SurfaceTexture surfaceTexture, int i, int i2) {
            super("CameraGLThread");
            this.surfaceTexture = surfaceTexture;
            surfaceTexture = InstantCameraView.this.previewSize.getWidth();
            int height = InstantCameraView.this.previewSize.getHeight();
            i = (float) i;
            float min = i / ((float) Math.min(surfaceTexture, height));
            surfaceTexture = (int) (((float) surfaceTexture) * min);
            height = (int) (((float) height) * min);
            if (surfaceTexture > height) {
                InstantCameraView.this.scaleX = 1.0f;
                InstantCameraView.this.scaleY = ((float) surfaceTexture) / ((float) i2);
                return;
            }
            InstantCameraView.this.scaleX = ((float) height) / i;
            InstantCameraView.this.scaleY = 1.0f;
        }

        private boolean initGL() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("start init gl");
            }
            this.egl10 = (EGL10) javax.microedition.khronos.egl.EGLContext.getEGL();
            this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.eglDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("eglGetDisplay failed ");
                    stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    FileLog.m1e(stringBuilder.toString());
                }
                finish();
                return false;
            }
            if (this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                int[] iArr = new int[1];
                javax.microedition.khronos.egl.EGLConfig[] eGLConfigArr = new javax.microedition.khronos.egl.EGLConfig[1];
                if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12344}, eGLConfigArr, 1, iArr)) {
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("eglChooseConfig failed ");
                        stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        FileLog.m1e(stringBuilder.toString());
                    }
                    finish();
                    return false;
                } else if (iArr[0] > 0) {
                    this.eglConfig = eGLConfigArr[0];
                    this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                    if (this.eglContext == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("eglCreateContext failed ");
                            stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            FileLog.m1e(stringBuilder.toString());
                        }
                        finish();
                        return false;
                    } else if (this.surfaceTexture instanceof SurfaceTexture) {
                        this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
                        if (this.eglSurface != null) {
                            if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
                                if (this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                                    this.gl = this.eglContext.getGL();
                                    float access$2100 = (1.0f / InstantCameraView.this.scaleX) / 2.0f;
                                    float access$2200 = (1.0f / InstantCameraView.this.scaleY) / 2.0f;
                                    float[] fArr = new float[]{-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
                                    r7 = new float[8];
                                    float f = 0.5f - access$2100;
                                    r7[0] = f;
                                    float f2 = 0.5f - access$2200;
                                    r7[1] = f2;
                                    access$2100 += 0.5f;
                                    r7[2] = access$2100;
                                    r7[3] = f2;
                                    r7[4] = f;
                                    float f3 = 0.5f + access$2200;
                                    r7[5] = f3;
                                    r7[6] = access$2100;
                                    r7[7] = f3;
                                    this.videoEncoder = new VideoRecorder();
                                    InstantCameraView.this.vertexBuffer = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                                    InstantCameraView.this.vertexBuffer.put(fArr).position(0);
                                    InstantCameraView.this.textureBuffer = ByteBuffer.allocateDirect(r7.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                                    InstantCameraView.this.textureBuffer.put(r7).position(0);
                                    Matrix.setIdentityM(InstantCameraView.this.mSTMatrix, 0);
                                    int access$2700 = InstantCameraView.this.loadShader(35633, InstantCameraView.VERTEX_SHADER);
                                    int access$27002 = InstantCameraView.this.loadShader(35632, InstantCameraView.FRAGMENT_SCREEN_SHADER);
                                    if (access$2700 == 0 || access$27002 == 0) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m1e("failed creating shader");
                                        }
                                        finish();
                                        return false;
                                    }
                                    this.drawProgram = GLES20.glCreateProgram();
                                    GLES20.glAttachShader(this.drawProgram, access$2700);
                                    GLES20.glAttachShader(this.drawProgram, access$27002);
                                    GLES20.glLinkProgram(this.drawProgram);
                                    int[] iArr2 = new int[1];
                                    GLES20.glGetProgramiv(this.drawProgram, 35714, iArr2, 0);
                                    if (iArr2[0] == 0) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m1e("failed link shader");
                                        }
                                        GLES20.glDeleteProgram(this.drawProgram);
                                        this.drawProgram = 0;
                                    } else {
                                        this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                                        this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                                        this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                                        this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                                    }
                                    GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                                    GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                                    GLES20.glTexParameteri(36197, 10241, 9729);
                                    GLES20.glTexParameteri(36197, 10240, 9729);
                                    GLES20.glTexParameteri(36197, 10242, 33071);
                                    GLES20.glTexParameteri(36197, 10243, 33071);
                                    Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                                    this.cameraSurface = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                                    this.cameraSurface.setOnFrameAvailableListener(new C11871());
                                    InstantCameraView.this.createCamera(this.cameraSurface);
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m1e("gl initied");
                                    }
                                    return true;
                                }
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("eglMakeCurrent failed ");
                                    stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                                    FileLog.m1e(stringBuilder.toString());
                                }
                                finish();
                                return false;
                            }
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("createWindowSurface failed ");
                            stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            FileLog.m1e(stringBuilder.toString());
                        }
                        finish();
                        return false;
                    } else {
                        finish();
                        return false;
                    }
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m1e("eglConfig not initialized");
                    }
                    finish();
                    return false;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("eglInitialize failed ");
                stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                FileLog.m1e(stringBuilder.toString());
            }
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

        public void setCurrentSession(CameraSession cameraSession) {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(3, cameraSession), 0);
            }
        }

        private void onDraw(Integer num) {
            if (!this.initied) {
                return;
            }
            if ((this.eglContext.equals(this.egl10.eglGetCurrentContext()) && this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) || this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                this.cameraSurface.updateTexImage();
                if (!this.recording) {
                    this.videoEncoder.startRecording(InstantCameraView.this.cameraFile, EGL14.eglGetCurrentContext());
                    this.recording = true;
                    int currentOrientation = this.currentSession.getCurrentOrientation();
                    if (currentOrientation == 90 || currentOrientation == 270) {
                        float access$2100 = InstantCameraView.this.scaleX;
                        InstantCameraView.this.scaleX = InstantCameraView.this.scaleY;
                        InstantCameraView.this.scaleY = access$2100;
                    }
                }
                this.videoEncoder.frameAvailable(this.cameraSurface, num, System.nanoTime());
                this.cameraSurface.getTransformMatrix(InstantCameraView.this.mSTMatrix);
                GLES20.glUseProgram(this.drawProgram);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, InstantCameraView.this.vertexBuffer);
                GLES20.glEnableVertexAttribArray(this.positionHandle);
                GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, InstantCameraView.this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.textureHandle);
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
            if (BuildVars.LOGS_ENABLED != null) {
                num = new StringBuilder();
                num.append("eglMakeCurrent failed ");
                num.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                FileLog.m1e(num.toString());
            }
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    onDraw((Integer) message.obj);
                    break;
                case 1:
                    finish();
                    if (this.recording) {
                        this.videoEncoder.stopRecording(message.arg1);
                    }
                    message = Looper.myLooper();
                    if (message != null) {
                        message.quit();
                        break;
                    }
                    break;
                case 2:
                    if (this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext) != null) {
                        if (this.cameraSurface != null) {
                            this.cameraSurface.getTransformMatrix(InstantCameraView.this.moldSTMatrix);
                            this.cameraSurface.setOnFrameAvailableListener(null);
                            this.cameraSurface.release();
                            InstantCameraView.this.oldCameraTexture[0] = InstantCameraView.this.cameraTexture[0];
                            InstantCameraView.this.cameraTextureAlpha = 0.0f;
                            InstantCameraView.this.cameraTexture[0] = null;
                        }
                        message = this.cameraId;
                        this.cameraId = Integer.valueOf(this.cameraId.intValue() + 1);
                        InstantCameraView.this.cameraReady = false;
                        GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                        GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                        GLES20.glTexParameteri(36197, 10241, 9729);
                        GLES20.glTexParameteri(36197, 10240, 9729);
                        GLES20.glTexParameteri(36197, 10242, 33071);
                        GLES20.glTexParameteri(36197, 10243, 33071);
                        this.cameraSurface = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                        this.cameraSurface.setOnFrameAvailableListener(new C11882());
                        InstantCameraView.this.createCamera(this.cameraSurface);
                        break;
                    }
                    if (BuildVars.LOGS_ENABLED != null) {
                        message = new StringBuilder();
                        message.append("eglMakeCurrent failed ");
                        message.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        FileLog.m0d(message.toString());
                    }
                    return;
                case 3:
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("set gl rednderer session");
                    }
                    CameraSession cameraSession = (CameraSession) message.obj;
                    if (this.currentSession != cameraSession) {
                        this.currentSession = cameraSession;
                        break;
                    }
                    this.rotationAngle = this.currentSession.getWorldAngle();
                    Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                    if (this.rotationAngle != null) {
                        Matrix.rotateM(InstantCameraView.this.mMVPMatrix, 0, (float) this.rotationAngle, 0.0f, 0.0f, 1.0f);
                        break;
                    }
                    break;
                default:
                    break;
            }
        }

        public void shutdown(int i) {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(1, i, 0), 0);
            }
        }

        public void requestRender() {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(0, this.cameraId), 0);
            }
        }
    }

    public InstantCameraView(Context context, ChatActivity chatActivity) {
        super(context);
        this.aspectRatio = SharedConfig.roundCamera16to9 ? new Size(16, 9) : new Size(4, 3);
        this.mMVPMatrix = new float[16];
        this.mSTMatrix = new float[16];
        this.moldSTMatrix = new float[16];
        setOnTouchListener(new C11782());
        setWillNotDraw(false);
        setBackgroundColor(-NUM);
        this.baseFragment = chatActivity;
        this.isSecretChat = this.baseFragment.getCurrentEncryptedChat() != null ? 1 : null;
        this.paint = new Paint(1) {
            public void setAlpha(int i) {
                super.setAlpha(i);
                InstantCameraView.this.invalidate();
            }
        };
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setColor(-1);
        this.rect = new RectF();
        if (VERSION.SDK_INT >= 21) {
            this.cameraContainer = new FrameLayout(context) {
                public void setScaleX(float f) {
                    super.setScaleX(f);
                    InstantCameraView.this.invalidate();
                }

                public void setAlpha(float f) {
                    super.setAlpha(f);
                    InstantCameraView.this.invalidate();
                }
            };
            this.cameraContainer.setOutlineProvider(new C11815());
            this.cameraContainer.setClipToOutline(true);
            this.cameraContainer.setWillNotDraw(false);
        } else {
            chatActivity = new Path();
            final Paint paint = new Paint(1);
            paint.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            this.cameraContainer = new FrameLayout(context) {
                public void setScaleX(float f) {
                    super.setScaleX(f);
                    InstantCameraView.this.invalidate();
                }

                protected void onSizeChanged(int i, int i2, int i3, int i4) {
                    super.onSizeChanged(i, i2, i3, i4);
                    chatActivity.reset();
                    i = (float) (i / 2);
                    chatActivity.addCircle(i, (float) (i2 / 2), i, Direction.CW);
                    chatActivity.toggleInverseFillType();
                }

                protected void dispatchDraw(android.graphics.Canvas r3) {
                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                    /*
                    r2 = this;
                    super.dispatchDraw(r3);	 Catch:{ Exception -> 0x000a }
                    r0 = r15;	 Catch:{ Exception -> 0x000a }
                    r1 = r4;	 Catch:{ Exception -> 0x000a }
                    r3.drawPath(r0, r1);	 Catch:{ Exception -> 0x000a }
                L_0x000a:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.6.dispatchDraw(android.graphics.Canvas):void");
                }
            };
            this.cameraContainer.setWillNotDraw(false);
            this.cameraContainer.setLayerType(2, null);
        }
        addView(this.cameraContainer, new LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize, 17));
        this.switchCameraButton = new ImageView(context);
        this.switchCameraButton.setScaleType(ScaleType.CENTER);
        addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48.0f, 83, 20.0f, 0.0f, 0.0f, 14.0f));
        this.switchCameraButton.setOnClickListener(new C11847());
        this.muteImageView = new ImageView(context);
        this.muteImageView.setScaleType(ScaleType.CENTER);
        this.muteImageView.setImageResource(C0446R.drawable.video_mute);
        this.muteImageView.setAlpha(null);
        addView(this.muteImageView, LayoutHelper.createFrame(48, 48, 17));
        ((LayoutParams) this.muteImageView.getLayoutParams()).topMargin = (AndroidUtilities.roundMessageSize / 2) - AndroidUtilities.dp(24.0f);
        setVisibility(4);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(motionEvent);
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (getVisibility() != 0) {
            this.cameraContainer.setTranslationY((float) (getMeasuredHeight() / 2));
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.recordProgressChanged) {
            i = ((Long) objArr[0]).longValue();
            this.progress = ((float) i) / 60000.0f;
            this.recordedTime = i;
            invalidate();
        } else if (i == NotificationCenter.FileDidUpload) {
            String str = (String) objArr[0];
            if (this.cameraFile != 0 && this.cameraFile.getAbsolutePath().equals(str) != 0) {
                this.file = (InputFile) objArr[1];
                this.encryptedFile = (InputEncryptedFile) objArr[2];
                this.size = ((Long) objArr[5]).longValue();
                if (this.encryptedFile != 0) {
                    this.key = (byte[]) objArr[3];
                    this.iv = (byte[]) objArr[4];
                }
            }
        }
    }

    public void destroy(boolean z, Runnable runnable) {
        if (this.cameraSession != null) {
            this.cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, !z ? new CountDownLatch(1) : false, runnable);
        }
    }

    protected void onDraw(Canvas canvas) {
        float x = this.cameraContainer.getX();
        float y = this.cameraContainer.getY();
        this.rect.set(x - ((float) AndroidUtilities.dp(8.0f)), y - ((float) AndroidUtilities.dp(8.0f)), (((float) this.cameraContainer.getMeasuredWidth()) + x) + ((float) AndroidUtilities.dp(8.0f)), (((float) this.cameraContainer.getMeasuredHeight()) + y) + ((float) AndroidUtilities.dp(8.0f)));
        if (this.progress != 0.0f) {
            canvas.drawArc(this.rect, -90.0f, 360.0f * this.progress, false, this.paint);
        }
        if (Theme.chat_roundVideoShadow != null) {
            int dp = ((int) x) - AndroidUtilities.dp(3.0f);
            int dp2 = ((int) y) - AndroidUtilities.dp(2.0f);
            canvas.save();
            canvas.scale(this.cameraContainer.getScaleX(), this.cameraContainer.getScaleY(), (float) (((AndroidUtilities.roundMessageSize / 2) + dp) + AndroidUtilities.dp(3.0f)), (float) (((AndroidUtilities.roundMessageSize / 2) + dp2) + AndroidUtilities.dp(3.0f)));
            Theme.chat_roundVideoShadow.setAlpha((int) (this.cameraContainer.getAlpha() * 255.0f));
            Theme.chat_roundVideoShadow.setBounds(dp, dp2, (AndroidUtilities.roundMessageSize + dp) + AndroidUtilities.dp(6.0f), (AndroidUtilities.roundMessageSize + dp2) + AndroidUtilities.dp(6.0f));
            Theme.chat_roundVideoShadow.draw(canvas);
            canvas.restore();
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        setAlpha(0.0f);
        this.switchCameraButton.setAlpha(0.0f);
        this.cameraContainer.setAlpha(0.0f);
        this.muteImageView.setAlpha(0.0f);
        this.muteImageView.setScaleX(1.0f);
        this.muteImageView.setScaleY(1.0f);
        this.cameraContainer.setScaleX(0.1f);
        this.cameraContainer.setScaleY(0.1f);
        if (this.cameraContainer.getMeasuredWidth() != 0) {
            this.cameraContainer.setPivotX((float) (this.cameraContainer.getMeasuredWidth() / 2));
            this.cameraContainer.setPivotY((float) (this.cameraContainer.getMeasuredHeight() / 2));
        }
        if (i == 0) {
            try {
                ((Activity) getContext()).getWindow().addFlags(128);
                return;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return;
            }
        }
        ((Activity) getContext()).getWindow().clearFlags(128);
    }

    public void showCamera() {
        if (this.textureView == null) {
            this.switchCameraButton.setImageResource(C0446R.drawable.camera_revert1);
            this.isFrontface = true;
            this.selectedCamera = null;
            this.recordedTime = 0;
            this.progress = 0.0f;
            this.cancelled = false;
            this.file = null;
            this.encryptedFile = null;
            this.key = null;
            this.iv = null;
            if (initCamera()) {
                MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
                File directory = FileLoader.getDirectory(4);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(SharedConfig.getLastLocalId());
                stringBuilder.append(".mp4");
                this.cameraFile = new File(directory, stringBuilder.toString());
                SharedConfig.saveConfig();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("show round camera");
                }
                this.textureView = new TextureView(getContext());
                this.textureView.setSurfaceTextureListener(new C11858());
                this.cameraContainer.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
                setVisibility(0);
                startAnimation(true);
            }
        }
    }

    public FrameLayout getCameraContainer() {
        return this.cameraContainer;
    }

    public void startAnimation(boolean z) {
        if (this.animatorSet != null) {
            this.animatorSet.cancel();
        }
        PipRoundVideoView instance = PipRoundVideoView.getInstance();
        if (instance != null) {
            instance.showTemporary(z ^ 1);
        }
        this.animatorSet = new AnimatorSet();
        AnimatorSet animatorSet = this.animatorSet;
        Animator[] animatorArr = new Animator[8];
        String str = "alpha";
        float[] fArr = new float[1];
        float f = 1.0f;
        float f2 = 0.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(this, str, fArr);
        ImageView imageView = this.switchCameraButton;
        String str2 = "alpha";
        float[] fArr2 = new float[1];
        fArr2[0] = z ? 1.0f : 0.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(imageView, str2, fArr2);
        animatorArr[2] = ObjectAnimator.ofFloat(this.muteImageView, "alpha", new float[]{0.0f});
        Paint paint = this.paint;
        String str3 = "alpha";
        int[] iArr = new int[1];
        iArr[0] = z ? 255 : 0;
        animatorArr[3] = ObjectAnimator.ofInt(paint, str3, iArr);
        FrameLayout frameLayout = this.cameraContainer;
        str3 = "alpha";
        float[] fArr3 = new float[1];
        fArr3[0] = z ? 1.0f : 0.0f;
        animatorArr[4] = ObjectAnimator.ofFloat(frameLayout, str3, fArr3);
        frameLayout = this.cameraContainer;
        str3 = "scaleX";
        fArr3 = new float[1];
        fArr3[0] = z ? 1.0f : 0.1f;
        animatorArr[5] = ObjectAnimator.ofFloat(frameLayout, str3, fArr3);
        frameLayout = this.cameraContainer;
        str3 = "scaleY";
        fArr3 = new float[1];
        if (!z) {
            f = 0.1f;
        }
        fArr3[0] = f;
        animatorArr[6] = ObjectAnimator.ofFloat(frameLayout, str3, fArr3);
        FrameLayout frameLayout2 = this.cameraContainer;
        String str4 = "translationY";
        float[] fArr4 = new float[2];
        fArr4[0] = z ? (float) (getMeasuredHeight() / 2) : 0.0f;
        if (!z) {
            f2 = (float) (getMeasuredHeight() / 2);
        }
        fArr4[1] = f2;
        animatorArr[7] = ObjectAnimator.ofFloat(frameLayout2, str4, fArr4);
        animatorSet.playTogether(animatorArr);
        if (!z) {
            this.animatorSet.addListener(new C11869());
        }
        this.animatorSet.setDuration(180);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.start();
    }

    public Rect getCameraRect() {
        this.cameraContainer.getLocationOnScreen(this.position);
        return new Rect((float) this.position[0], (float) this.position[1], (float) this.cameraContainer.getWidth(), (float) this.cameraContainer.getHeight());
    }

    public void changeVideoPreviewState(int i, float f) {
        if (this.videoPlayer != null) {
            if (i == 0) {
                startProgressTimer();
                this.videoPlayer.play();
            } else if (i == 1) {
                stopProgressTimer();
                this.videoPlayer.pause();
            } else if (i == 2) {
                this.videoPlayer.seekTo((long) (f * ((float) this.videoPlayer.getDuration())));
            }
        }
    }

    public void send(int i) {
        if (this.textureView != null) {
            stopProgressTimer();
            if (this.videoPlayer != null) {
                this.videoPlayer.releasePlayer();
                this.videoPlayer = null;
            }
            if (i == 4) {
                if (this.videoEditedInfo.needConvert() != 0) {
                    this.file = null;
                    this.encryptedFile = null;
                    this.key = null;
                    this.iv = null;
                    double d = (double) this.videoEditedInfo.estimatedDuration;
                    this.videoEditedInfo.estimatedDuration = (this.videoEditedInfo.endTime >= 0 ? this.videoEditedInfo.endTime : this.videoEditedInfo.estimatedDuration) - (this.videoEditedInfo.startTime >= 0 ? this.videoEditedInfo.startTime : 0);
                    this.videoEditedInfo.estimatedSize = (long) (((double) this.size) * (((double) this.videoEditedInfo.estimatedDuration) / d));
                    this.videoEditedInfo.bitrate = 400000;
                    if (this.videoEditedInfo.startTime > 0) {
                        i = this.videoEditedInfo;
                        i.startTime *= 1000;
                    }
                    if (this.videoEditedInfo.endTime > 0) {
                        i = this.videoEditedInfo;
                        i.endTime *= 1000;
                    }
                    FileLoader.getInstance(this.currentAccount).cancelUploadFile(this.cameraFile.getAbsolutePath(), false);
                } else {
                    this.videoEditedInfo.estimatedSize = this.size;
                }
                this.videoEditedInfo.file = this.file;
                this.videoEditedInfo.encryptedFile = this.encryptedFile;
                this.videoEditedInfo.key = this.key;
                this.videoEditedInfo.iv = this.iv;
                this.baseFragment.sendMedia(new PhotoEntry(0, 0, 0, this.cameraFile.getAbsolutePath(), 0, true), this.videoEditedInfo);
            } else {
                int i2 = 1;
                this.cancelled = this.recordedTime < 800;
                this.recording = false;
                AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
                if (this.cameraThread != null) {
                    NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
                    int i3 = NotificationCenter.recordStopped;
                    Object[] objArr = new Object[1];
                    int i4 = (this.cancelled || i != 3) ? 0 : 2;
                    objArr[0] = Integer.valueOf(i4);
                    instance.postNotificationName(i3, objArr);
                    if (this.cancelled) {
                        i2 = 0;
                    } else if (i == 3) {
                        i2 = 2;
                    }
                    this.cameraThread.shutdown(i2);
                    this.cameraThread = null;
                }
                if (this.cancelled != 0) {
                    startAnimation(false);
                }
            }
        }
    }

    public void cancel() {
        stopProgressTimer();
        if (this.videoPlayer != null) {
            this.videoPlayer.releasePlayer();
            this.videoPlayer = null;
        }
        if (this.textureView != null) {
            this.cancelled = true;
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recordStopped, Integer.valueOf(0));
            if (this.cameraThread != null) {
                this.cameraThread.shutdown(0);
                this.cameraThread = null;
            }
            if (this.cameraFile != null) {
                this.cameraFile.delete();
                this.cameraFile = null;
            }
            startAnimation(false);
        }
    }

    @Keep
    public void setAlpha(float f) {
        ((ColorDrawable) getBackground()).setAlpha((int) (192.0f * f));
        invalidate();
    }

    public View getSwitchButtonView() {
        return this.switchCameraButton;
    }

    public View getMuteImageView() {
        return this.muteImageView;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void hideCamera(boolean z) {
        destroy(z, null);
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
        this.isFrontface ^= 1;
        initCamera();
        this.cameraReady = false;
        this.cameraThread.reinitForNewCamera();
    }

    private boolean initCamera() {
        ArrayList cameras = CameraController.getInstance().getCameras();
        boolean z = false;
        if (cameras == null) {
            return false;
        }
        CameraInfo cameraInfo = null;
        int i = 0;
        while (i < cameras.size()) {
            CameraInfo cameraInfo2 = (CameraInfo) cameras.get(i);
            if (!cameraInfo2.isFrontface()) {
                cameraInfo = cameraInfo2;
            }
            if ((this.isFrontface && cameraInfo2.isFrontface()) || (!this.isFrontface && !cameraInfo2.isFrontface())) {
                this.selectedCamera = cameraInfo2;
                break;
            }
            i++;
            cameraInfo = cameraInfo2;
        }
        if (this.selectedCamera == null) {
            this.selectedCamera = cameraInfo;
        }
        if (this.selectedCamera == null) {
            return false;
        }
        cameras = this.selectedCamera.getPreviewSizes();
        ArrayList pictureSizes = this.selectedCamera.getPictureSizes();
        this.previewSize = CameraController.chooseOptimalSize(cameras, 480, 270, this.aspectRatio);
        this.pictureSize = CameraController.chooseOptimalSize(pictureSizes, 480, 270, this.aspectRatio);
        if (this.previewSize.mWidth != this.pictureSize.mWidth) {
            int size;
            Size size2;
            int size3;
            Size size4;
            for (size = cameras.size() - 1; size >= 0; size--) {
                size2 = (Size) cameras.get(size);
                for (size3 = pictureSizes.size() - 1; size3 >= 0; size3--) {
                    size4 = (Size) pictureSizes.get(size3);
                    if (size2.mWidth >= this.pictureSize.mWidth && size2.mHeight >= this.pictureSize.mHeight && size2.mWidth == size4.mWidth && size2.mHeight == size4.mHeight) {
                        this.previewSize = size2;
                        this.pictureSize = size4;
                        z = true;
                        break;
                    }
                }
                if (z) {
                    break;
                }
            }
            if (!z) {
                for (size = cameras.size() - 1; size >= 0; size--) {
                    size2 = (Size) cameras.get(size);
                    for (size3 = pictureSizes.size() - 1; size3 >= 0; size3--) {
                        size4 = (Size) pictureSizes.get(size3);
                        if (size2.mWidth >= PsExtractor.VIDEO_STREAM_MASK && size2.mHeight >= PsExtractor.VIDEO_STREAM_MASK && size2.mWidth == size4.mWidth && size2.mHeight == size4.mHeight) {
                            this.previewSize = size2;
                            this.pictureSize = size4;
                            z = true;
                            break;
                        }
                    }
                    if (z) {
                        break;
                    }
                }
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("preview w = ");
            stringBuilder.append(this.previewSize.mWidth);
            stringBuilder.append(" h = ");
            stringBuilder.append(this.previewSize.mHeight);
            FileLog.m0d(stringBuilder.toString());
        }
        return true;
    }

    private void createCamera(final SurfaceTexture surfaceTexture) {
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: org.telegram.ui.Components.InstantCameraView$10$1 */
            class C11731 implements Runnable {
                C11731() {
                }

                public void run() {
                    if (InstantCameraView.this.cameraSession != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("camera initied");
                        }
                        InstantCameraView.this.cameraSession.setInitied();
                    }
                }
            }

            /* renamed from: org.telegram.ui.Components.InstantCameraView$10$2 */
            class C11742 implements Runnable {
                C11742() {
                }

                public void run() {
                    InstantCameraView.this.cameraThread.setCurrentSession(InstantCameraView.this.cameraSession);
                }
            }

            public void run() {
                if (InstantCameraView.this.cameraThread != null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("create camera session");
                    }
                    surfaceTexture.setDefaultBufferSize(InstantCameraView.this.previewSize.getWidth(), InstantCameraView.this.previewSize.getHeight());
                    InstantCameraView.this.cameraSession = new CameraSession(InstantCameraView.this.selectedCamera, InstantCameraView.this.previewSize, InstantCameraView.this.pictureSize, 256);
                    InstantCameraView.this.cameraThread.setCurrentSession(InstantCameraView.this.cameraSession);
                    CameraController.getInstance().openRound(InstantCameraView.this.cameraSession, surfaceTexture, new C11731(), new C11742());
                }
            }
        });
    }

    private int loadShader(int i, String str) {
        i = GLES20.glCreateShader(i);
        GLES20.glShaderSource(i, str);
        GLES20.glCompileShader(i);
        str = new int[1];
        GLES20.glGetShaderiv(i, 35713, str, 0);
        if (str[0] != null) {
            return i;
        }
        if (BuildVars.LOGS_ENABLED != null) {
            FileLog.m1e(GLES20.glGetShaderInfoLog(i));
        }
        GLES20.glDeleteShader(i);
        return 0;
    }

    private void startProgressTimer() {
        if (this.progressTimer != null) {
            try {
                this.progressTimer.cancel();
                this.progressTimer = null;
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        this.progressTimer = new Timer();
        this.progressTimer.schedule(new TimerTask() {

            /* renamed from: org.telegram.ui.Components.InstantCameraView$11$1 */
            class C11751 implements Runnable {
                C11751() {
                }

                public void run() {
                    try {
                        if (InstantCameraView.this.videoPlayer != null && InstantCameraView.this.videoEditedInfo != null) {
                            long j = 0;
                            if (InstantCameraView.this.videoEditedInfo.endTime > 0 && InstantCameraView.this.videoPlayer.getCurrentPosition() >= InstantCameraView.this.videoEditedInfo.endTime) {
                                VideoPlayer access$600 = InstantCameraView.this.videoPlayer;
                                if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                                    j = InstantCameraView.this.videoEditedInfo.startTime;
                                }
                                access$600.seekTo(j);
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C11751());
            }
        }, 0, 17);
    }

    private void stopProgressTimer() {
        if (this.progressTimer != null) {
            try {
                this.progressTimer.cancel();
                this.progressTimer = null;
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }
}
