package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Property;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.Keep;
import java.io.File;
import java.io.FileOutputStream;
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
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraInfo;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.Size;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.video.Mp4Movie;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.InstantCameraView;
import org.telegram.ui.Components.VideoPlayer;

@TargetApi(18)
public class InstantCameraView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final String FRAGMENT_SCREEN_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   float coef = ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0));\n   vec3 color = texture2D(sTexture, vTextureCoord).rgb * coef + (1.0 - step(0.001, coef));\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n";
    private static final int MSG_AUDIOFRAME_AVAILABLE = 3;
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_VIDEOFRAME_AVAILABLE = 2;
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private Size aspectRatio;
    /* access modifiers changed from: private */
    public ChatActivity baseFragment;
    private FrameLayout cameraContainer;
    /* access modifiers changed from: private */
    public File cameraFile;
    /* access modifiers changed from: private */
    public volatile boolean cameraReady;
    /* access modifiers changed from: private */
    public CameraSession cameraSession;
    /* access modifiers changed from: private */
    public int[] cameraTexture = new int[1];
    /* access modifiers changed from: private */
    public float cameraTextureAlpha = 1.0f;
    /* access modifiers changed from: private */
    public CameraGLThread cameraThread;
    /* access modifiers changed from: private */
    public boolean cancelled;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private boolean deviceHasGoodCamera;
    /* access modifiers changed from: private */
    public long duration;
    /* access modifiers changed from: private */
    public TLRPC.InputEncryptedFile encryptedFile;
    /* access modifiers changed from: private */
    public TLRPC.InputFile file;
    /* access modifiers changed from: private */
    public boolean isFrontface = true;
    /* access modifiers changed from: private */
    public boolean isSecretChat;
    /* access modifiers changed from: private */
    public byte[] iv;
    /* access modifiers changed from: private */
    public byte[] key;
    private Bitmap lastBitmap;
    /* access modifiers changed from: private */
    public float[] mMVPMatrix;
    /* access modifiers changed from: private */
    public float[] mSTMatrix;
    /* access modifiers changed from: private */
    public float[] moldSTMatrix;
    /* access modifiers changed from: private */
    public AnimatorSet muteAnimation;
    /* access modifiers changed from: private */
    public ImageView muteImageView;
    /* access modifiers changed from: private */
    public int[] oldCameraTexture = new int[1];
    /* access modifiers changed from: private */
    public Paint paint;
    private Size pictureSize;
    private int[] position = new int[2];
    /* access modifiers changed from: private */
    public Size previewSize;
    private float progress;
    private Timer progressTimer;
    /* access modifiers changed from: private */
    public long recordStartTime;
    private long recordedTime;
    /* access modifiers changed from: private */
    public boolean recording;
    /* access modifiers changed from: private */
    public int recordingGuid;
    private RectF rect;
    private boolean requestingPermissions;
    /* access modifiers changed from: private */
    public float scaleX;
    /* access modifiers changed from: private */
    public float scaleY;
    private CameraInfo selectedCamera;
    /* access modifiers changed from: private */
    public long size;
    /* access modifiers changed from: private */
    public ImageView switchCameraButton;
    /* access modifiers changed from: private */
    public FloatBuffer textureBuffer;
    /* access modifiers changed from: private */
    public BackupImageView textureOverlayView;
    /* access modifiers changed from: private */
    public TextureView textureView;
    /* access modifiers changed from: private */
    public Runnable timerRunnable = new Runnable() {
        public void run() {
            if (InstantCameraView.this.recording) {
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Integer.valueOf(InstantCameraView.this.recordingGuid), Long.valueOf(InstantCameraView.this.duration = System.currentTimeMillis() - InstantCameraView.this.recordStartTime), Double.valueOf(0.0d));
                AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable, 50);
            }
        }
    };
    /* access modifiers changed from: private */
    public FloatBuffer vertexBuffer;
    /* access modifiers changed from: private */
    public VideoEditedInfo videoEditedInfo;
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;

    public InstantCameraView(Context context, ChatActivity chatActivity) {
        super(context);
        this.aspectRatio = SharedConfig.roundCamera16to9 ? new Size(16, 9) : new Size(4, 3);
        this.mMVPMatrix = new float[16];
        this.mSTMatrix = new float[16];
        this.moldSTMatrix = new float[16];
        setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return InstantCameraView.this.lambda$new$0$InstantCameraView(view, motionEvent);
            }
        });
        setWillNotDraw(false);
        setBackgroundColor(-NUM);
        this.baseFragment = chatActivity;
        this.recordingGuid = this.baseFragment.getClassGuid();
        this.isSecretChat = this.baseFragment.getCurrentEncryptedChat() != null;
        this.paint = new Paint(1) {
            public void setAlpha(int i) {
                super.setAlpha(i);
                InstantCameraView.this.invalidate();
            }
        };
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setColor(-1);
        this.rect = new RectF();
        if (Build.VERSION.SDK_INT >= 21) {
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
            this.cameraContainer.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    int i = AndroidUtilities.roundMessageSize;
                    outline.setOval(0, 0, i, i);
                }
            });
            this.cameraContainer.setClipToOutline(true);
            this.cameraContainer.setWillNotDraw(false);
        } else {
            final Path path = new Path();
            final Paint paint2 = new Paint(1);
            paint2.setColor(-16777216);
            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            this.cameraContainer = new FrameLayout(context) {
                public void setScaleX(float f) {
                    super.setScaleX(f);
                    InstantCameraView.this.invalidate();
                }

                /* access modifiers changed from: protected */
                public void onSizeChanged(int i, int i2, int i3, int i4) {
                    super.onSizeChanged(i, i2, i3, i4);
                    path.reset();
                    float f = (float) (i / 2);
                    path.addCircle(f, (float) (i2 / 2), f, Path.Direction.CW);
                    path.toggleInverseFillType();
                }

                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    try {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(path, paint2);
                    } catch (Exception unused) {
                    }
                }
            };
            this.cameraContainer.setWillNotDraw(false);
            this.cameraContainer.setLayerType(2, (Paint) null);
        }
        FrameLayout frameLayout = this.cameraContainer;
        int i = AndroidUtilities.roundMessageSize;
        addView(frameLayout, new FrameLayout.LayoutParams(i, i, 17));
        this.switchCameraButton = new ImageView(context);
        this.switchCameraButton.setScaleType(ImageView.ScaleType.CENTER);
        this.switchCameraButton.setContentDescription(LocaleController.getString("AccDescrSwitchCamera", NUM));
        addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48.0f, 83, 20.0f, 0.0f, 0.0f, 14.0f));
        this.switchCameraButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                InstantCameraView.this.lambda$new$1$InstantCameraView(view);
            }
        });
        this.muteImageView = new ImageView(context);
        this.muteImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.muteImageView.setImageResource(NUM);
        this.muteImageView.setAlpha(0.0f);
        addView(this.muteImageView, LayoutHelper.createFrame(48, 48, 17));
        ((FrameLayout.LayoutParams) this.muteImageView.getLayoutParams()).topMargin = (AndroidUtilities.roundMessageSize / 2) - AndroidUtilities.dp(24.0f);
        this.textureOverlayView = new BackupImageView(getContext());
        this.textureOverlayView.setRoundRadius(AndroidUtilities.roundMessageSize / 2);
        BackupImageView backupImageView = this.textureOverlayView;
        int i2 = AndroidUtilities.roundMessageSize;
        addView(backupImageView, new FrameLayout.LayoutParams(i2, i2, 17));
        setVisibility(4);
    }

    public /* synthetic */ boolean lambda$new$0$InstantCameraView(View view, MotionEvent motionEvent) {
        ChatActivity chatActivity;
        if (motionEvent.getAction() == 0 && (chatActivity = this.baseFragment) != null) {
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 != null) {
                boolean z = !videoPlayer2.isMuted();
                this.videoPlayer.setMute(z);
                AnimatorSet animatorSet2 = this.muteAnimation;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                this.muteAnimation = new AnimatorSet();
                AnimatorSet animatorSet3 = this.muteAnimation;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView = this.muteImageView;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                float f = 1.0f;
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView, property, fArr);
                ImageView imageView2 = this.muteImageView;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 1.0f : 0.5f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView2, property2, fArr2);
                ImageView imageView3 = this.muteImageView;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (!z) {
                    f = 0.5f;
                }
                fArr3[0] = f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView3, property3, fArr3);
                animatorSet3.playTogether(animatorArr);
                this.muteAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(InstantCameraView.this.muteAnimation)) {
                            AnimatorSet unused = InstantCameraView.this.muteAnimation = null;
                        }
                    }
                });
                this.muteAnimation.setDuration(180);
                this.muteAnimation.setInterpolator(new DecelerateInterpolator());
                this.muteAnimation.start();
            } else {
                chatActivity.checkRecordLocked();
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$new$1$InstantCameraView(View view) {
        CameraSession cameraSession2;
        if (this.cameraReady && (cameraSession2 = this.cameraSession) != null && cameraSession2.isInitied() && this.cameraThread != null) {
            switchCamera();
            ObjectAnimator duration2 = ObjectAnimator.ofFloat(this.switchCameraButton, View.SCALE_X, new float[]{0.0f}).setDuration(100);
            duration2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    InstantCameraView.this.switchCameraButton.setImageResource(InstantCameraView.this.isFrontface ? NUM : NUM);
                    ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, View.SCALE_X, new float[]{1.0f}).setDuration(100).start();
                }
            });
            duration2.start();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (getVisibility() != 0) {
            this.cameraContainer.setTranslationY((float) (getMeasuredHeight() / 2));
            this.textureOverlayView.setTranslationY((float) (getMeasuredHeight() / 2));
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.recordProgressChanged) {
            if (objArr[0].intValue() == this.recordingGuid) {
                long longValue = objArr[1].longValue();
                this.progress = ((float) longValue) / 60000.0f;
                this.recordedTime = longValue;
                invalidate();
            }
        } else if (i == NotificationCenter.FileDidUpload) {
            String str = objArr[0];
            File file2 = this.cameraFile;
            if (file2 != null && file2.getAbsolutePath().equals(str)) {
                this.file = objArr[1];
                this.encryptedFile = objArr[2];
                this.size = objArr[5].longValue();
                if (this.encryptedFile != null) {
                    this.key = objArr[3];
                    this.iv = objArr[4];
                }
            }
        }
    }

    public void destroy(boolean z, Runnable runnable) {
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.destroy();
            CameraController.getInstance().close(this.cameraSession, !z ? new CountDownLatch(1) : null, runnable);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float x = this.cameraContainer.getX();
        float y = this.cameraContainer.getY();
        this.rect.set(x - ((float) AndroidUtilities.dp(8.0f)), y - ((float) AndroidUtilities.dp(8.0f)), ((float) this.cameraContainer.getMeasuredWidth()) + x + ((float) AndroidUtilities.dp(8.0f)), ((float) this.cameraContainer.getMeasuredHeight()) + y + ((float) AndroidUtilities.dp(8.0f)));
        float f = this.progress;
        if (f != 0.0f) {
            canvas.drawArc(this.rect, -90.0f, f * 360.0f, false, this.paint);
        }
        if (Theme.chat_roundVideoShadow != null) {
            int dp = ((int) x) - AndroidUtilities.dp(3.0f);
            int dp2 = ((int) y) - AndroidUtilities.dp(2.0f);
            canvas.save();
            canvas.scale(this.cameraContainer.getScaleX(), this.cameraContainer.getScaleY(), (float) ((AndroidUtilities.roundMessageSize / 2) + dp + AndroidUtilities.dp(3.0f)), (float) ((AndroidUtilities.roundMessageSize / 2) + dp2 + AndroidUtilities.dp(3.0f)));
            Theme.chat_roundVideoShadow.setAlpha((int) (this.cameraContainer.getAlpha() * 255.0f));
            Theme.chat_roundVideoShadow.setBounds(dp, dp2, AndroidUtilities.roundMessageSize + dp + AndroidUtilities.dp(6.0f), AndroidUtilities.roundMessageSize + dp2 + AndroidUtilities.dp(6.0f));
            Theme.chat_roundVideoShadow.draw(canvas);
            canvas.restore();
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        setAlpha(0.0f);
        this.switchCameraButton.setAlpha(0.0f);
        this.cameraContainer.setAlpha(0.0f);
        this.textureOverlayView.setAlpha(0.0f);
        this.muteImageView.setAlpha(0.0f);
        this.muteImageView.setScaleX(1.0f);
        this.muteImageView.setScaleY(1.0f);
        this.cameraContainer.setScaleX(0.1f);
        this.cameraContainer.setScaleY(0.1f);
        this.textureOverlayView.setScaleX(0.1f);
        this.textureOverlayView.setScaleY(0.1f);
        if (this.cameraContainer.getMeasuredWidth() != 0) {
            FrameLayout frameLayout = this.cameraContainer;
            frameLayout.setPivotX((float) (frameLayout.getMeasuredWidth() / 2));
            FrameLayout frameLayout2 = this.cameraContainer;
            frameLayout2.setPivotY((float) (frameLayout2.getMeasuredHeight() / 2));
            BackupImageView backupImageView = this.textureOverlayView;
            backupImageView.setPivotX((float) (backupImageView.getMeasuredWidth() / 2));
            BackupImageView backupImageView2 = this.textureOverlayView;
            backupImageView2.setPivotY((float) (backupImageView2.getMeasuredHeight() / 2));
        }
        if (i == 0) {
            try {
                ((Activity) getContext()).getWindow().addFlags(128);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            ((Activity) getContext()).getWindow().clearFlags(128);
        }
    }

    public void showCamera() {
        if (this.textureView == null) {
            this.switchCameraButton.setImageResource(NUM);
            this.textureOverlayView.setAlpha(1.0f);
            if (this.lastBitmap == null) {
                try {
                    this.lastBitmap = BitmapFactory.decodeFile(new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg").getAbsolutePath());
                } catch (Throwable unused) {
                }
            }
            Bitmap bitmap = this.lastBitmap;
            if (bitmap != null) {
                this.textureOverlayView.setImageBitmap(bitmap);
            } else {
                this.textureOverlayView.setImageResource(NUM);
            }
            this.cameraReady = false;
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
                MediaController.getInstance().lambda$startAudioAgain$6$MediaController(MediaController.getInstance().getPlayingMessageObject());
                File directory = FileLoader.getDirectory(4);
                this.cameraFile = new File(directory, SharedConfig.getLastLocalId() + ".mp4");
                SharedConfig.saveConfig();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("show round camera");
                }
                this.textureView = new TextureView(getContext());
                this.textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }

                    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("camera surface available");
                        }
                        if (InstantCameraView.this.cameraThread == null && surfaceTexture != null && !InstantCameraView.this.cancelled) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("start create thread");
                            }
                            InstantCameraView instantCameraView = InstantCameraView.this;
                            CameraGLThread unused = instantCameraView.cameraThread = new CameraGLThread(surfaceTexture, i, i2);
                        }
                    }

                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                        if (InstantCameraView.this.cameraThread != null) {
                            InstantCameraView.this.cameraThread.shutdown(0);
                            CameraGLThread unused = InstantCameraView.this.cameraThread = null;
                        }
                        if (InstantCameraView.this.cameraSession == null) {
                            return true;
                        }
                        CameraController.getInstance().close(InstantCameraView.this.cameraSession, (CountDownLatch) null, (Runnable) null);
                        return true;
                    }
                });
                this.cameraContainer.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
                setVisibility(0);
                startAnimation(true);
                MediaController.getInstance().requestAudioFocus(true);
            }
        }
    }

    public FrameLayout getCameraContainer() {
        return this.cameraContainer;
    }

    public void startAnimation(boolean z) {
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        PipRoundVideoView instance = PipRoundVideoView.getInstance();
        if (instance != null) {
            instance.showTemporary(!z);
        }
        this.animatorSet = new AnimatorSet();
        AnimatorSet animatorSet3 = this.animatorSet;
        Animator[] animatorArr = new Animator[12];
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        float f2 = 0.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
        ImageView imageView = this.switchCameraButton;
        Property property2 = View.ALPHA;
        float[] fArr2 = new float[1];
        fArr2[0] = z ? 1.0f : 0.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(imageView, property2, fArr2);
        animatorArr[2] = ObjectAnimator.ofFloat(this.muteImageView, View.ALPHA, new float[]{0.0f});
        Paint paint2 = this.paint;
        int[] iArr = new int[1];
        iArr[0] = z ? 255 : 0;
        animatorArr[3] = ObjectAnimator.ofInt(paint2, "alpha", iArr);
        FrameLayout frameLayout = this.cameraContainer;
        Property property3 = View.ALPHA;
        float[] fArr3 = new float[1];
        fArr3[0] = z ? 1.0f : 0.0f;
        animatorArr[4] = ObjectAnimator.ofFloat(frameLayout, property3, fArr3);
        FrameLayout frameLayout2 = this.cameraContainer;
        Property property4 = View.SCALE_X;
        float[] fArr4 = new float[1];
        fArr4[0] = z ? 1.0f : 0.1f;
        animatorArr[5] = ObjectAnimator.ofFloat(frameLayout2, property4, fArr4);
        FrameLayout frameLayout3 = this.cameraContainer;
        Property property5 = View.SCALE_Y;
        float[] fArr5 = new float[1];
        fArr5[0] = z ? 1.0f : 0.1f;
        animatorArr[6] = ObjectAnimator.ofFloat(frameLayout3, property5, fArr5);
        FrameLayout frameLayout4 = this.cameraContainer;
        Property property6 = View.TRANSLATION_Y;
        float[] fArr6 = new float[2];
        fArr6[0] = z ? (float) (getMeasuredHeight() / 2) : 0.0f;
        fArr6[1] = z ? 0.0f : (float) (getMeasuredHeight() / 2);
        animatorArr[7] = ObjectAnimator.ofFloat(frameLayout4, property6, fArr6);
        BackupImageView backupImageView = this.textureOverlayView;
        Property property7 = View.ALPHA;
        float[] fArr7 = new float[1];
        fArr7[0] = z ? 1.0f : 0.0f;
        animatorArr[8] = ObjectAnimator.ofFloat(backupImageView, property7, fArr7);
        BackupImageView backupImageView2 = this.textureOverlayView;
        Property property8 = View.SCALE_X;
        float[] fArr8 = new float[1];
        fArr8[0] = z ? 1.0f : 0.1f;
        animatorArr[9] = ObjectAnimator.ofFloat(backupImageView2, property8, fArr8);
        BackupImageView backupImageView3 = this.textureOverlayView;
        Property property9 = View.SCALE_Y;
        float[] fArr9 = new float[1];
        if (!z) {
            f = 0.1f;
        }
        fArr9[0] = f;
        animatorArr[10] = ObjectAnimator.ofFloat(backupImageView3, property9, fArr9);
        BackupImageView backupImageView4 = this.textureOverlayView;
        Property property10 = View.TRANSLATION_Y;
        float[] fArr10 = new float[2];
        fArr10[0] = z ? (float) (getMeasuredHeight() / 2) : 0.0f;
        if (!z) {
            f2 = (float) (getMeasuredHeight() / 2);
        }
        fArr10[1] = f2;
        animatorArr[11] = ObjectAnimator.ofFloat(backupImageView4, property10, fArr10);
        animatorSet3.playTogether(animatorArr);
        if (!z) {
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(InstantCameraView.this.animatorSet)) {
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
        int[] iArr = this.position;
        return new Rect((float) iArr[0], (float) iArr[1], (float) this.cameraContainer.getWidth(), (float) this.cameraContainer.getHeight());
    }

    public void changeVideoPreviewState(int i, float f) {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            if (i == 0) {
                startProgressTimer();
                this.videoPlayer.play();
            } else if (i == 1) {
                stopProgressTimer();
                this.videoPlayer.pause();
            } else if (i == 2) {
                videoPlayer2.seekTo((long) (f * ((float) videoPlayer2.getDuration())));
            }
        }
    }

    public void send(int i, boolean z, int i2) {
        int i3 = i;
        int i4 = i2;
        if (this.textureView != null) {
            stopProgressTimer();
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 != null) {
                videoPlayer2.releasePlayer(true);
                this.videoPlayer = null;
            }
            if (i3 == 4) {
                if (this.videoEditedInfo.needConvert()) {
                    this.file = null;
                    this.encryptedFile = null;
                    this.key = null;
                    this.iv = null;
                    VideoEditedInfo videoEditedInfo2 = this.videoEditedInfo;
                    double d = (double) videoEditedInfo2.estimatedDuration;
                    long j = videoEditedInfo2.startTime;
                    if (j < 0) {
                        j = 0;
                    }
                    VideoEditedInfo videoEditedInfo3 = this.videoEditedInfo;
                    long j2 = videoEditedInfo3.endTime;
                    if (j2 < 0) {
                        j2 = videoEditedInfo3.estimatedDuration;
                    }
                    VideoEditedInfo videoEditedInfo4 = this.videoEditedInfo;
                    videoEditedInfo4.estimatedDuration = j2 - j;
                    double d2 = (double) this.size;
                    double d3 = (double) videoEditedInfo4.estimatedDuration;
                    Double.isNaN(d3);
                    Double.isNaN(d);
                    Double.isNaN(d2);
                    videoEditedInfo4.estimatedSize = Math.max(1, (long) (d2 * (d3 / d)));
                    VideoEditedInfo videoEditedInfo5 = this.videoEditedInfo;
                    videoEditedInfo5.bitrate = 400000;
                    long j3 = videoEditedInfo5.startTime;
                    if (j3 > 0) {
                        videoEditedInfo5.startTime = j3 * 1000;
                    }
                    VideoEditedInfo videoEditedInfo6 = this.videoEditedInfo;
                    long j4 = videoEditedInfo6.endTime;
                    if (j4 > 0) {
                        videoEditedInfo6.endTime = j4 * 1000;
                    }
                    FileLoader.getInstance(this.currentAccount).cancelUploadFile(this.cameraFile.getAbsolutePath(), false);
                } else {
                    this.videoEditedInfo.estimatedSize = Math.max(1, this.size);
                }
                VideoEditedInfo videoEditedInfo7 = this.videoEditedInfo;
                videoEditedInfo7.file = this.file;
                videoEditedInfo7.encryptedFile = this.encryptedFile;
                videoEditedInfo7.key = this.key;
                videoEditedInfo7.iv = this.iv;
                this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0, this.cameraFile.getAbsolutePath(), 0, true, 0, 0, 0), this.videoEditedInfo, z, i4);
                if (i4 != 0) {
                    startAnimation(false);
                }
                MediaController.getInstance().requestAudioFocus(false);
                return;
            }
            this.cancelled = this.recordedTime < 800;
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            if (this.cameraThread != null) {
                NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
                int i5 = NotificationCenter.recordStopped;
                Object[] objArr = new Object[2];
                objArr[0] = Integer.valueOf(this.recordingGuid);
                objArr[1] = Integer.valueOf((this.cancelled || i3 != 3) ? 0 : 2);
                instance.postNotificationName(i5, objArr);
                int i6 = this.cancelled ? 0 : i3 == 3 ? 2 : 1;
                saveLastCameraBitmap();
                this.cameraThread.shutdown(i6);
                this.cameraThread = null;
            }
            if (this.cancelled) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), true);
                startAnimation(false);
                MediaController.getInstance().requestAudioFocus(false);
            }
        }
    }

    private void saveLastCameraBitmap() {
        if (this.textureView.getBitmap() != null) {
            this.lastBitmap = Bitmap.createScaledBitmap(this.textureView.getBitmap(), 80, 80, true);
            Bitmap bitmap = this.lastBitmap;
            if (bitmap != null) {
                Utilities.blurBitmap(bitmap, 7, 1, bitmap.getWidth(), this.lastBitmap.getHeight(), this.lastBitmap.getRowBytes());
                try {
                    this.lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg")));
                } catch (Throwable unused) {
                }
            }
        }
    }

    public void cancel() {
        stopProgressTimer();
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.releasePlayer(true);
            this.videoPlayer = null;
        }
        if (this.textureView != null) {
            this.cancelled = true;
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recordStopped, Integer.valueOf(this.recordingGuid), 0);
            if (this.cameraThread != null) {
                saveLastCameraBitmap();
                this.cameraThread.shutdown(0);
                this.cameraThread = null;
            }
            File file2 = this.cameraFile;
            if (file2 != null) {
                file2.delete();
                this.cameraFile = null;
            }
            MediaController.getInstance().requestAudioFocus(false);
            startAnimation(false);
        }
    }

    @Keep
    public void setAlpha(float f) {
        ((ColorDrawable) getBackground()).setAlpha((int) (f * 192.0f));
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
        destroy(z, (Runnable) null);
        this.cameraContainer.removeView(this.textureView);
        this.cameraContainer.setTranslationX(0.0f);
        this.cameraContainer.setTranslationY(0.0f);
        this.textureOverlayView.setTranslationX(0.0f);
        this.textureOverlayView.setTranslationY(0.0f);
        this.textureView = null;
    }

    private void switchCamera() {
        saveLastCameraBitmap();
        Bitmap bitmap = this.lastBitmap;
        if (bitmap != null) {
            this.textureOverlayView.setImageBitmap(bitmap);
            this.textureOverlayView.animate().setDuration(120).alpha(1.0f).setInterpolator(new DecelerateInterpolator()).start();
        }
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.destroy();
            CameraController.getInstance().close(this.cameraSession, (CountDownLatch) null, (Runnable) null);
            this.cameraSession = null;
        }
        this.isFrontface = !this.isFrontface;
        initCamera();
        this.cameraReady = false;
        this.cameraThread.reinitForNewCamera();
    }

    private boolean initCamera() {
        int i;
        int i2;
        CameraInfo cameraInfo;
        ArrayList<CameraInfo> cameras = CameraController.getInstance().getCameras();
        boolean z = false;
        if (cameras == null) {
            return false;
        }
        CameraInfo cameraInfo2 = null;
        int i3 = 0;
        while (true) {
            if (i3 >= cameras.size()) {
                break;
            }
            cameraInfo = cameras.get(i3);
            if (!cameraInfo.isFrontface()) {
                cameraInfo2 = cameraInfo;
            }
            if ((!this.isFrontface || !cameraInfo.isFrontface()) && (this.isFrontface || cameraInfo.isFrontface())) {
                i3++;
                cameraInfo2 = cameraInfo;
            }
        }
        this.selectedCamera = cameraInfo;
        if (this.selectedCamera == null) {
            this.selectedCamera = cameraInfo2;
        }
        CameraInfo cameraInfo3 = this.selectedCamera;
        if (cameraInfo3 == null) {
            return false;
        }
        ArrayList<Size> previewSizes = cameraInfo3.getPreviewSizes();
        ArrayList<Size> pictureSizes = this.selectedCamera.getPictureSizes();
        this.previewSize = CameraController.chooseOptimalSize(previewSizes, 480, 270, this.aspectRatio);
        this.pictureSize = CameraController.chooseOptimalSize(pictureSizes, 480, 270, this.aspectRatio);
        if (this.previewSize.mWidth != this.pictureSize.mWidth) {
            for (int size2 = previewSizes.size() - 1; size2 >= 0; size2--) {
                Size size3 = previewSizes.get(size2);
                int size4 = pictureSizes.size() - 1;
                while (true) {
                    if (size4 < 0) {
                        break;
                    }
                    Size size5 = pictureSizes.get(size4);
                    int i4 = size3.mWidth;
                    Size size6 = this.pictureSize;
                    if (i4 >= size6.mWidth && (i2 = size3.mHeight) >= size6.mHeight && i4 == size5.mWidth && i2 == size5.mHeight) {
                        this.previewSize = size3;
                        this.pictureSize = size5;
                        z = true;
                        break;
                    }
                    size4--;
                }
                if (z) {
                    break;
                }
            }
            if (!z) {
                for (int size7 = previewSizes.size() - 1; size7 >= 0; size7--) {
                    Size size8 = previewSizes.get(size7);
                    int size9 = pictureSizes.size() - 1;
                    while (true) {
                        if (size9 < 0) {
                            break;
                        }
                        Size size10 = pictureSizes.get(size9);
                        int i5 = size8.mWidth;
                        if (i5 >= 240 && (i = size8.mHeight) >= 240 && i5 == size10.mWidth && i == size10.mHeight) {
                            this.previewSize = size8;
                            this.pictureSize = size10;
                            z = true;
                            break;
                        }
                        size9--;
                    }
                    if (z) {
                        break;
                    }
                }
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("preview w = " + this.previewSize.mWidth + " h = " + this.previewSize.mHeight);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void createCamera(SurfaceTexture surfaceTexture) {
        AndroidUtilities.runOnUIThread(new Runnable(surfaceTexture) {
            private final /* synthetic */ SurfaceTexture f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                InstantCameraView.this.lambda$createCamera$4$InstantCameraView(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$createCamera$4$InstantCameraView(SurfaceTexture surfaceTexture) {
        if (this.cameraThread != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("create camera session");
            }
            surfaceTexture.setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
            this.cameraSession = new CameraSession(this.selectedCamera, this.previewSize, this.pictureSize, 256);
            this.cameraThread.setCurrentSession(this.cameraSession);
            CameraController.getInstance().openRound(this.cameraSession, surfaceTexture, new Runnable() {
                public final void run() {
                    InstantCameraView.this.lambda$null$2$InstantCameraView();
                }
            }, new Runnable() {
                public final void run() {
                    InstantCameraView.this.lambda$null$3$InstantCameraView();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$2$InstantCameraView() {
        if (this.cameraSession != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("camera initied");
            }
            this.cameraSession.setInitied();
        }
    }

    public /* synthetic */ void lambda$null$3$InstantCameraView() {
        this.cameraThread.setCurrentSession(this.cameraSession);
    }

    /* access modifiers changed from: private */
    public int loadShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        GLES20.glShaderSource(glCreateShader, str);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] != 0) {
            return glCreateShader;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e(GLES20.glGetShaderInfoLog(glCreateShader));
        }
        GLES20.glDeleteShader(glCreateShader);
        return 0;
    }

    /* access modifiers changed from: private */
    public void startProgressTimer() {
        Timer timer = this.progressTimer;
        if (timer != null) {
            try {
                timer.cancel();
                this.progressTimer = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        this.progressTimer = new Timer();
        this.progressTimer.schedule(new TimerTask() {
            public void run() {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        InstantCameraView.AnonymousClass10.this.lambda$run$0$InstantCameraView$10();
                    }
                });
            }

            public /* synthetic */ void lambda$run$0$InstantCameraView$10() {
                try {
                    if (InstantCameraView.this.videoPlayer != null && InstantCameraView.this.videoEditedInfo != null) {
                        long j = 0;
                        if (InstantCameraView.this.videoEditedInfo.endTime > 0 && InstantCameraView.this.videoPlayer.getCurrentPosition() >= InstantCameraView.this.videoEditedInfo.endTime) {
                            VideoPlayer access$1000 = InstantCameraView.this.videoPlayer;
                            if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                                j = InstantCameraView.this.videoEditedInfo.startTime;
                            }
                            access$1000.seekTo(j);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }, 0, 17);
    }

    private void stopProgressTimer() {
        Timer timer = this.progressTimer;
        if (timer != null) {
            try {
                timer.cancel();
                this.progressTimer = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public class CameraGLThread extends DispatchQueue {
        private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private static final int EGL_OPENGL_ES2_BIT = 4;
        private final int DO_REINIT_MESSAGE = 2;
        private final int DO_RENDER_MESSAGE = 0;
        private final int DO_SETSESSION_MESSAGE = 3;
        private final int DO_SHUTDOWN_MESSAGE = 1;
        private Integer cameraId = 0;
        private SurfaceTexture cameraSurface;
        private CameraSession currentSession;
        private int drawProgram;
        private EGL10 egl10;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private boolean initied;
        private int positionHandle;
        private boolean recording;
        private SurfaceTexture surfaceTexture;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private VideoRecorder videoEncoder;

        public CameraGLThread(SurfaceTexture surfaceTexture2, int i, int i2) {
            super("CameraGLThread");
            this.surfaceTexture = surfaceTexture2;
            int width = InstantCameraView.this.previewSize.getWidth();
            int height = InstantCameraView.this.previewSize.getHeight();
            float f = (float) i;
            float min = f / ((float) Math.min(width, height));
            int i3 = (int) (((float) width) * min);
            int i4 = (int) (((float) height) * min);
            if (i3 > i4) {
                float unused = InstantCameraView.this.scaleX = 1.0f;
                float unused2 = InstantCameraView.this.scaleY = ((float) i3) / ((float) i2);
                return;
            }
            float unused3 = InstantCameraView.this.scaleX = ((float) i4) / f;
            float unused4 = InstantCameraView.this.scaleY = 1.0f;
        }

        private boolean initGL() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start init gl");
            }
            this.egl10 = (EGL10) EGLContext.getEGL();
            this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (!this.egl10.eglInitialize(eGLDisplay, new int[2])) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] iArr = new int[1];
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12344}, eGLConfigArr, 1, iArr)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (iArr[0] > 0) {
                EGLConfig eGLConfig = eGLConfigArr[0];
                this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                if (this.eglContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture2 = this.surfaceTexture;
                if (surfaceTexture2 instanceof SurfaceTexture) {
                    this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, eGLConfig, surfaceTexture2, (int[]) null);
                    EGLSurface eGLSurface = this.eglSurface;
                    if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else if (!this.egl10.eglMakeCurrent(this.eglDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else {
                        this.eglContext.getGL();
                        float access$1300 = (1.0f / InstantCameraView.this.scaleX) / 2.0f;
                        float access$1400 = (1.0f / InstantCameraView.this.scaleY) / 2.0f;
                        float[] fArr = {-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
                        float f = 0.5f - access$1300;
                        float f2 = 0.5f - access$1400;
                        float f3 = access$1300 + 0.5f;
                        float f4 = access$1400 + 0.5f;
                        float[] fArr2 = {f, f2, f3, f2, f, f4, f3, f4};
                        this.videoEncoder = new VideoRecorder();
                        FloatBuffer unused = InstantCameraView.this.vertexBuffer = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        InstantCameraView.this.vertexBuffer.put(fArr).position(0);
                        FloatBuffer unused2 = InstantCameraView.this.textureBuffer = ByteBuffer.allocateDirect(fArr2.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        InstantCameraView.this.textureBuffer.put(fArr2).position(0);
                        Matrix.setIdentityM(InstantCameraView.this.mSTMatrix, 0);
                        int access$1900 = InstantCameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                        int access$19002 = InstantCameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
                        if (access$1900 == 0 || access$19002 == 0) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("failed creating shader");
                            }
                            finish();
                            return false;
                        }
                        this.drawProgram = GLES20.glCreateProgram();
                        GLES20.glAttachShader(this.drawProgram, access$1900);
                        GLES20.glAttachShader(this.drawProgram, access$19002);
                        GLES20.glLinkProgram(this.drawProgram);
                        int[] iArr2 = new int[1];
                        GLES20.glGetProgramiv(this.drawProgram, 35714, iArr2, 0);
                        if (iArr2[0] == 0) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("failed link shader");
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
                        this.cameraSurface.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                            public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
                                InstantCameraView.CameraGLThread.this.lambda$initGL$0$InstantCameraView$CameraGLThread(surfaceTexture);
                            }
                        });
                        InstantCameraView.this.createCamera(this.cameraSurface);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("gl initied");
                        }
                        return true;
                    }
                } else {
                    finish();
                    return false;
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        public /* synthetic */ void lambda$initGL$0$InstantCameraView$CameraGLThread(SurfaceTexture surfaceTexture2) {
            requestRender();
        }

        public void reinitForNewCamera() {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(2), 0);
            }
        }

        public void finish() {
            if (this.eglSurface != null) {
                EGL10 egl102 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = EGL10.EGL_NO_SURFACE;
                egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay2 = this.eglDisplay;
            if (eGLDisplay2 != null) {
                this.egl10.eglTerminate(eGLDisplay2);
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
            if (this.initied) {
                if (!this.eglContext.equals(this.egl10.eglGetCurrentContext()) || !this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) {
                    EGL10 egl102 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (!egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            return;
                        }
                        return;
                    }
                }
                this.cameraSurface.updateTexImage();
                if (!this.recording) {
                    this.videoEncoder.startRecording(InstantCameraView.this.cameraFile, EGL14.eglGetCurrentContext());
                    this.recording = true;
                    int currentOrientation = this.currentSession.getCurrentOrientation();
                    if (currentOrientation == 90 || currentOrientation == 270) {
                        float access$1300 = InstantCameraView.this.scaleX;
                        InstantCameraView instantCameraView = InstantCameraView.this;
                        float unused = instantCameraView.scaleX = instantCameraView.scaleY;
                        float unused2 = InstantCameraView.this.scaleY = access$1300;
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
            }
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                onDraw((Integer) message.obj);
            } else if (i == 1) {
                finish();
                if (this.recording) {
                    this.videoEncoder.stopRecording(message.arg1);
                }
                Looper myLooper = Looper.myLooper();
                if (myLooper != null) {
                    myLooper.quit();
                }
            } else if (i == 2) {
                EGL10 egl102 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = this.eglSurface;
                if (egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                    SurfaceTexture surfaceTexture2 = this.cameraSurface;
                    if (surfaceTexture2 != null) {
                        surfaceTexture2.getTransformMatrix(InstantCameraView.this.moldSTMatrix);
                        this.cameraSurface.setOnFrameAvailableListener((SurfaceTexture.OnFrameAvailableListener) null);
                        this.cameraSurface.release();
                        InstantCameraView.this.oldCameraTexture[0] = InstantCameraView.this.cameraTexture[0];
                        float unused = InstantCameraView.this.cameraTextureAlpha = 0.0f;
                        InstantCameraView.this.cameraTexture[0] = 0;
                    }
                    this.cameraId = Integer.valueOf(this.cameraId.intValue() + 1);
                    boolean unused2 = InstantCameraView.this.cameraReady = false;
                    GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                    GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                    GLES20.glTexParameteri(36197, 10241, 9729);
                    GLES20.glTexParameteri(36197, 10240, 9729);
                    GLES20.glTexParameteri(36197, 10242, 33071);
                    GLES20.glTexParameteri(36197, 10243, 33071);
                    this.cameraSurface = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                    this.cameraSurface.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                        public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
                            InstantCameraView.CameraGLThread.this.lambda$handleMessage$1$InstantCameraView$CameraGLThread(surfaceTexture);
                        }
                    });
                    InstantCameraView.this.createCamera(this.cameraSurface);
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
            } else if (i == 3) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("set gl rednderer session");
                }
                CameraSession cameraSession = (CameraSession) message.obj;
                CameraSession cameraSession2 = this.currentSession;
                if (cameraSession2 == cameraSession) {
                    int worldAngle = cameraSession2.getWorldAngle();
                    Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                    if (worldAngle != 0) {
                        Matrix.rotateM(InstantCameraView.this.mMVPMatrix, 0, (float) worldAngle, 0.0f, 0.0f, 1.0f);
                        return;
                    }
                    return;
                }
                this.currentSession = cameraSession;
            }
        }

        public /* synthetic */ void lambda$handleMessage$1$InstantCameraView$CameraGLThread(SurfaceTexture surfaceTexture2) {
            requestRender();
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

    private static class EncoderHandler extends Handler {
        private WeakReference<VideoRecorder> mWeakEncoder;

        public EncoderHandler(VideoRecorder videoRecorder) {
            this.mWeakEncoder = new WeakReference<>(videoRecorder);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            Object obj = message.obj;
            VideoRecorder videoRecorder = (VideoRecorder) this.mWeakEncoder.get();
            if (videoRecorder != null) {
                if (i == 0) {
                    try {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("start encoder");
                        }
                        videoRecorder.prepareEncoder();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        videoRecorder.handleStopRecording(0);
                        Looper.myLooper().quit();
                    }
                } else if (i == 1) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("stop encoder");
                    }
                    videoRecorder.handleStopRecording(message.arg1);
                } else if (i == 2) {
                    videoRecorder.handleVideoFrameAvailable((((long) message.arg1) << 32) | (((long) message.arg2) & 4294967295L), (Integer) message.obj);
                } else if (i == 3) {
                    videoRecorder.handleAudioFrameAvailable((AudioBufferInfo) message.obj);
                }
            }
        }

        public void exit() {
            Looper.myLooper().quit();
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
            this.buffer = new byte[20480];
            this.offset = new long[10];
            this.read = new int[10];
        }
    }

    private class VideoRecorder implements Runnable {
        private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
        private static final int FRAME_RATE = 30;
        private static final int IFRAME_INTERVAL = 1;
        private static final String VIDEO_MIME_TYPE = "video/avc";
        private int alphaHandle;
        private MediaCodec.BufferInfo audioBufferInfo;
        private MediaCodec audioEncoder;
        private long audioFirst;
        /* access modifiers changed from: private */
        public AudioRecord audioRecorder;
        private long audioStartTime;
        private boolean audioStopedByTime;
        private int audioTrackIndex;
        private boolean blendEnabled;
        /* access modifiers changed from: private */
        public ArrayBlockingQueue<AudioBufferInfo> buffers;
        private ArrayList<AudioBufferInfo> buffersToWrite;
        private long currentTimestamp;
        private long desyncTime;
        private int drawProgram;
        private android.opengl.EGLConfig eglConfig;
        private android.opengl.EGLContext eglContext;
        private android.opengl.EGLDisplay eglDisplay;
        private android.opengl.EGLSurface eglSurface;
        private boolean firstEncode;
        /* access modifiers changed from: private */
        public volatile EncoderHandler handler;
        private Integer lastCameraId;
        private long lastCommitedFrameTime;
        private long lastTimestamp;
        private MP4Builder mediaMuxer;
        private int positionHandle;
        private int prependHeaderSize;
        private boolean ready;
        private Runnable recorderRunnable;
        /* access modifiers changed from: private */
        public volatile boolean running;
        private int scaleXHandle;
        private int scaleYHandle;
        /* access modifiers changed from: private */
        public volatile int sendWhenDone;
        private android.opengl.EGLContext sharedEglContext;
        private boolean skippedFirst;
        private long skippedTime;
        private Surface surface;
        private final Object sync;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private int videoBitrate;
        private MediaCodec.BufferInfo videoBufferInfo;
        private boolean videoConvertFirstWrite;
        private MediaCodec videoEncoder;
        private File videoFile;
        private long videoFirst;
        private int videoHeight;
        private long videoLast;
        private int videoTrackIndex;
        private int videoWidth;
        private int zeroTimeStamps;

        private VideoRecorder() {
            this.videoConvertFirstWrite = true;
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            this.buffersToWrite = new ArrayList<>();
            this.videoTrackIndex = -5;
            this.audioTrackIndex = -5;
            this.audioStartTime = -1;
            this.currentTimestamp = 0;
            this.lastTimestamp = -1;
            this.sync = new Object();
            this.videoFirst = -1;
            this.audioFirst = -1;
            this.lastCameraId = 0;
            this.buffers = new ArrayBlockingQueue<>(10);
            this.recorderRunnable = new Runnable() {
                /* JADX WARNING: Code restructure failed: missing block: B:12:0x002d, code lost:
                    if (org.telegram.ui.Components.InstantCameraView.VideoRecorder.access$3400(r14.this$1) == 0) goto L_0x00e6;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r14 = this;
                        r0 = -1
                        r2 = 0
                        r4 = r0
                        r3 = 0
                    L_0x0005:
                        r6 = 1
                        if (r3 != 0) goto L_0x00e6
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r7 = r7.running
                        if (r7 != 0) goto L_0x0031
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        android.media.AudioRecord r7 = r7.audioRecorder
                        int r7 = r7.getRecordingState()
                        if (r7 == r6) goto L_0x0031
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x0026 }
                        android.media.AudioRecord r7 = r7.audioRecorder     // Catch:{ Exception -> 0x0026 }
                        r7.stop()     // Catch:{ Exception -> 0x0026 }
                        goto L_0x0027
                    L_0x0026:
                        r3 = 1
                    L_0x0027:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        int r7 = r7.sendWhenDone
                        if (r7 != 0) goto L_0x0031
                        goto L_0x00e6
                    L_0x0031:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r7 = r7.buffers
                        boolean r7 = r7.isEmpty()
                        if (r7 == 0) goto L_0x0048
                        org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r7 = new org.telegram.ui.Components.InstantCameraView$AudioBufferInfo
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView r8 = org.telegram.ui.Components.InstantCameraView.this
                        r9 = 0
                        r7.<init>()
                        goto L_0x0054
                    L_0x0048:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r7 = r7.buffers
                        java.lang.Object r7 = r7.poll()
                        org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r7 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r7
                    L_0x0054:
                        r7.lastWroteBuffer = r2
                        r8 = 10
                        r7.results = r8
                        r9 = r4
                        r4 = 0
                    L_0x005c:
                        if (r4 >= r8) goto L_0x00a0
                        int r5 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                        if (r5 != 0) goto L_0x0069
                        long r9 = java.lang.System.nanoTime()
                        r11 = 1000(0x3e8, double:4.94E-321)
                        long r9 = r9 / r11
                    L_0x0069:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r5 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        android.media.AudioRecord r5 = r5.audioRecorder
                        byte[] r11 = r7.buffer
                        int r12 = r4 * 2048
                        r13 = 2048(0x800, float:2.87E-42)
                        int r5 = r5.read(r11, r12, r13)
                        if (r5 > 0) goto L_0x0088
                        r7.results = r4
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r4 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r4 = r4.running
                        if (r4 != 0) goto L_0x00a0
                        r7.last = r6
                        goto L_0x00a0
                    L_0x0088:
                        long[] r11 = r7.offset
                        r11[r4] = r9
                        int[] r11 = r7.read
                        r11[r4] = r5
                        r11 = 1000000(0xvar_, float:1.401298E-39)
                        int r5 = r5 * r11
                        r11 = 44100(0xaCLASSNAME, float:6.1797E-41)
                        int r5 = r5 / r11
                        int r5 = r5 / 2
                        long r11 = (long) r5
                        long r9 = r9 + r11
                        int r4 = r4 + 1
                        goto L_0x005c
                    L_0x00a0:
                        r4 = r9
                        int r9 = r7.results
                        if (r9 >= 0) goto L_0x00c3
                        boolean r9 = r7.last
                        if (r9 == 0) goto L_0x00aa
                        goto L_0x00c3
                    L_0x00aa:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r8 = r8.running
                        if (r8 != 0) goto L_0x00b5
                        r3 = 1
                        goto L_0x0005
                    L_0x00b5:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r6 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x00c0 }
                        java.util.concurrent.ArrayBlockingQueue r6 = r6.buffers     // Catch:{ Exception -> 0x00c0 }
                        r6.put(r7)     // Catch:{ Exception -> 0x00c0 }
                        goto L_0x0005
                    L_0x00c0:
                        goto L_0x0005
                    L_0x00c3:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r9 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r9 = r9.running
                        if (r9 != 0) goto L_0x00d0
                        int r9 = r7.results
                        if (r9 >= r8) goto L_0x00d0
                        r3 = 1
                    L_0x00d0:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r6 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r6 = r6.handler
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r8 = r8.handler
                        r9 = 3
                        android.os.Message r7 = r8.obtainMessage(r9, r7)
                        r6.sendMessage(r7)
                        goto L_0x0005
                    L_0x00e6:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x00f0 }
                        android.media.AudioRecord r0 = r0.audioRecorder     // Catch:{ Exception -> 0x00f0 }
                        r0.release()     // Catch:{ Exception -> 0x00f0 }
                        goto L_0x00f4
                    L_0x00f0:
                        r0 = move-exception
                        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                    L_0x00f4:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r0 = r0.handler
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r1 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r1 = r1.handler
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r3 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        int r3 = r3.sendWhenDone
                        android.os.Message r1 = r1.obtainMessage(r6, r3, r2)
                        r0.sendMessage(r1)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.AnonymousClass1.run():void");
                }
            };
        }

        public void startRecording(File file, android.opengl.EGLContext eGLContext) {
            int i;
            int i2;
            String str = Build.DEVICE;
            if (str == null) {
                str = "";
            }
            if (str.startsWith("zeroflte") || str.startsWith("zenlte")) {
                i2 = 320;
                i = 600000;
            } else {
                i2 = 240;
                i = 400000;
            }
            this.videoFile = file;
            this.videoWidth = i2;
            this.videoHeight = i2;
            this.videoBitrate = i;
            this.sharedEglContext = eGLContext;
            synchronized (this.sync) {
                if (!this.running) {
                    this.running = true;
                    Thread thread = new Thread(this, "TextureMovieEncoder");
                    thread.setPriority(10);
                    thread.start();
                    while (!this.ready) {
                        try {
                            this.sync.wait();
                        } catch (InterruptedException unused) {
                        }
                    }
                    this.handler.sendMessage(this.handler.obtainMessage(0));
                }
            }
        }

        public void stopRecording(int i) {
            this.handler.sendMessage(this.handler.obtainMessage(1, i, 0));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
            r4.zeroTimeStamps++;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x001c, code lost:
            if (r4.zeroTimeStamps <= 1) goto L_0x0028;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0020, code lost:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x002d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0022, code lost:
            org.telegram.messenger.FileLog.d("fix timestamp enabled");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0028, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0029, code lost:
            r4.zeroTimeStamps = 0;
            r7 = r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x002d, code lost:
            r4.handler.sendMessage(r4.handler.obtainMessage(2, (int) (r7 >> 32), (int) r7, r6));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x003f, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x000a, code lost:
            r0 = r5.getTimestamp();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
            if (r0 != 0) goto L_0x0029;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void frameAvailable(android.graphics.SurfaceTexture r5, java.lang.Integer r6, long r7) {
            /*
                r4 = this;
                java.lang.Object r0 = r4.sync
                monitor-enter(r0)
                boolean r1 = r4.ready     // Catch:{ all -> 0x0040 }
                if (r1 != 0) goto L_0x0009
                monitor-exit(r0)     // Catch:{ all -> 0x0040 }
                return
            L_0x0009:
                monitor-exit(r0)     // Catch:{ all -> 0x0040 }
                long r0 = r5.getTimestamp()
                r2 = 0
                int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r5 != 0) goto L_0x0029
                int r5 = r4.zeroTimeStamps
                r0 = 1
                int r5 = r5 + r0
                r4.zeroTimeStamps = r5
                int r5 = r4.zeroTimeStamps
                if (r5 <= r0) goto L_0x0028
                boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r5 == 0) goto L_0x002d
                java.lang.String r5 = "fix timestamp enabled"
                org.telegram.messenger.FileLog.d(r5)
                goto L_0x002d
            L_0x0028:
                return
            L_0x0029:
                r5 = 0
                r4.zeroTimeStamps = r5
                r7 = r0
            L_0x002d:
                org.telegram.ui.Components.InstantCameraView$EncoderHandler r5 = r4.handler
                org.telegram.ui.Components.InstantCameraView$EncoderHandler r0 = r4.handler
                r1 = 2
                r2 = 32
                long r2 = r7 >> r2
                int r3 = (int) r2
                int r8 = (int) r7
                android.os.Message r6 = r0.obtainMessage(r1, r3, r8, r6)
                r5.sendMessage(r6)
                return
            L_0x0040:
                r5 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0040 }
                throw r5
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.frameAvailable(android.graphics.SurfaceTexture, java.lang.Integer, long):void");
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

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x00c9  */
        /* JADX WARNING: Removed duplicated region for block: B:98:0x00fc A[EDGE_INSN: B:98:0x00fc->B:38:0x00fc ?: BREAK  , SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleAudioFrameAvailable(org.telegram.ui.Components.InstantCameraView.AudioBufferInfo r17) {
            /*
                r16 = this;
                r1 = r16
                boolean r0 = r1.audioStopedByTime
                if (r0 == 0) goto L_0x0007
                return
            L_0x0007:
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                r2 = r17
                r0.add(r2)
                long r3 = r1.audioFirst
                r5 = -1
                r7 = 0
                r8 = 1
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 != 0) goto L_0x00fc
                long r3 = r1.videoFirst
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 != 0) goto L_0x0029
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x0028
                java.lang.String r0 = "video record not yet started"
                org.telegram.messenger.FileLog.d(r0)
            L_0x0028:
                return
            L_0x0029:
                r0 = 0
            L_0x002a:
                int r3 = r2.results
                if (r0 >= r3) goto L_0x00c6
                if (r0 != 0) goto L_0x006a
                long r3 = r1.videoFirst
                long[] r9 = r2.offset
                r10 = r9[r0]
                long r3 = r3 - r10
                long r3 = java.lang.Math.abs(r3)
                r9 = 10000000(0x989680, double:4.9406565E-317)
                int r11 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                if (r11 <= 0) goto L_0x006a
                long r3 = r1.videoFirst
                long[] r9 = r2.offset
                r10 = r9[r0]
                long r3 = r3 - r10
                r1.desyncTime = r3
                r3 = r9[r0]
                r1.audioFirst = r3
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x009e
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "detected desync between audio and video "
                r0.append(r3)
                long r3 = r1.desyncTime
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                org.telegram.messenger.FileLog.d(r0)
                goto L_0x009e
            L_0x006a:
                long[] r3 = r2.offset
                r9 = r3[r0]
                long r11 = r1.videoFirst
                java.lang.String r4 = " timestamp = "
                int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
                if (r13 < 0) goto L_0x00a0
                r2.lastWroteBuffer = r0
                r9 = r3[r0]
                r1.audioFirst = r9
                boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r3 == 0) goto L_0x009e
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r9 = "found first audio frame at "
                r3.append(r9)
                r3.append(r0)
                r3.append(r4)
                long[] r4 = r2.offset
                r9 = r4[r0]
                r3.append(r9)
                java.lang.String r0 = r3.toString()
                org.telegram.messenger.FileLog.d(r0)
            L_0x009e:
                r0 = 1
                goto L_0x00c7
            L_0x00a0:
                boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r3 == 0) goto L_0x00c2
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r9 = "ignore first audio frame at "
                r3.append(r9)
                r3.append(r0)
                r3.append(r4)
                long[] r4 = r2.offset
                r9 = r4[r0]
                r3.append(r9)
                java.lang.String r3 = r3.toString()
                org.telegram.messenger.FileLog.d(r3)
            L_0x00c2:
                int r0 = r0 + 1
                goto L_0x002a
            L_0x00c6:
                r0 = 0
            L_0x00c7:
                if (r0 != 0) goto L_0x00fc
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x00e3
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "first audio frame not found, removing buffers "
                r0.append(r3)
                int r3 = r2.results
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                org.telegram.messenger.FileLog.d(r0)
            L_0x00e3:
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                r0.remove(r2)
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00fb
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                java.lang.Object r0 = r0.get(r7)
                r2 = r0
                org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r2 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r2
                goto L_0x0029
            L_0x00fb:
                return
            L_0x00fc:
                long r3 = r1.audioStartTime
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 != 0) goto L_0x010a
                long[] r0 = r2.offset
                int r3 = r2.lastWroteBuffer
                r3 = r0[r3]
                r1.audioStartTime = r3
            L_0x010a:
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                int r0 = r0.size()
                if (r0 <= r8) goto L_0x011b
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                java.lang.Object r0 = r0.get(r7)
                r2 = r0
                org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r2 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r2
            L_0x011b:
                r1.drainEncoder(r7)     // Catch:{ Exception -> 0x011f }
                goto L_0x0124
            L_0x011f:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0124:
                r0 = 0
            L_0x0125:
                if (r2 == 0) goto L_0x0216
                android.media.MediaCodec r3 = r1.audioEncoder     // Catch:{ all -> 0x0212 }
                r4 = 0
                int r10 = r3.dequeueInputBuffer(r4)     // Catch:{ all -> 0x0212 }
                if (r10 < 0) goto L_0x020c
                int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0212 }
                r6 = 21
                if (r3 < r6) goto L_0x013e
                android.media.MediaCodec r3 = r1.audioEncoder     // Catch:{ all -> 0x0212 }
                java.nio.ByteBuffer r3 = r3.getInputBuffer(r10)     // Catch:{ all -> 0x0212 }
                goto L_0x0149
            L_0x013e:
                android.media.MediaCodec r3 = r1.audioEncoder     // Catch:{ all -> 0x0212 }
                java.nio.ByteBuffer[] r3 = r3.getInputBuffers()     // Catch:{ all -> 0x0212 }
                r3 = r3[r10]     // Catch:{ all -> 0x0212 }
                r3.clear()     // Catch:{ all -> 0x0212 }
            L_0x0149:
                long[] r6 = r2.offset     // Catch:{ all -> 0x0212 }
                int r9 = r2.lastWroteBuffer     // Catch:{ all -> 0x0212 }
                r11 = r6[r9]     // Catch:{ all -> 0x0212 }
                int r6 = r2.lastWroteBuffer     // Catch:{ all -> 0x0212 }
            L_0x0151:
                int r9 = r2.results     // Catch:{ all -> 0x0212 }
                r13 = 0
                if (r6 > r9) goto L_0x01ea
                int r9 = r2.results     // Catch:{ all -> 0x0212 }
                if (r6 >= r9) goto L_0x01b5
                boolean r9 = r1.running     // Catch:{ all -> 0x0212 }
                if (r9 != 0) goto L_0x019c
                long[] r9 = r2.offset     // Catch:{ all -> 0x0212 }
                r14 = r9[r6]     // Catch:{ all -> 0x0212 }
                long r4 = r1.videoLast     // Catch:{ all -> 0x0212 }
                long r7 = r1.desyncTime     // Catch:{ all -> 0x0212 }
                long r4 = r4 - r7
                int r7 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
                if (r7 < 0) goto L_0x019c
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0212 }
                if (r0 == 0) goto L_0x0191
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0212 }
                r0.<init>()     // Catch:{ all -> 0x0212 }
                java.lang.String r4 = "stop audio encoding because of stoped video recording at "
                r0.append(r4)     // Catch:{ all -> 0x0212 }
                long[] r2 = r2.offset     // Catch:{ all -> 0x0212 }
                r4 = r2[r6]     // Catch:{ all -> 0x0212 }
                r0.append(r4)     // Catch:{ all -> 0x0212 }
                java.lang.String r2 = " last video "
                r0.append(r2)     // Catch:{ all -> 0x0212 }
                long r4 = r1.videoLast     // Catch:{ all -> 0x0212 }
                r0.append(r4)     // Catch:{ all -> 0x0212 }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0212 }
                org.telegram.messenger.FileLog.d(r0)     // Catch:{ all -> 0x0212 }
            L_0x0191:
                r2 = 1
                r1.audioStopedByTime = r2     // Catch:{ all -> 0x0212 }
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite     // Catch:{ all -> 0x0212 }
                r0.clear()     // Catch:{ all -> 0x0212 }
                r2 = r13
                r0 = 1
                goto L_0x01ea
            L_0x019c:
                int r4 = r3.remaining()     // Catch:{ all -> 0x0212 }
                int[] r5 = r2.read     // Catch:{ all -> 0x0212 }
                r5 = r5[r6]     // Catch:{ all -> 0x0212 }
                if (r4 >= r5) goto L_0x01aa
                r2.lastWroteBuffer = r6     // Catch:{ all -> 0x0212 }
                r2 = r13
                goto L_0x01ea
            L_0x01aa:
                byte[] r4 = r2.buffer     // Catch:{ all -> 0x0212 }
                int r5 = r6 * 2048
                int[] r7 = r2.read     // Catch:{ all -> 0x0212 }
                r7 = r7[r6]     // Catch:{ all -> 0x0212 }
                r3.put(r4, r5, r7)     // Catch:{ all -> 0x0212 }
            L_0x01b5:
                int r4 = r2.results     // Catch:{ all -> 0x0212 }
                r5 = 1
                int r4 = r4 - r5
                if (r6 < r4) goto L_0x01e1
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r4 = r1.buffersToWrite     // Catch:{ all -> 0x0212 }
                r4.remove(r2)     // Catch:{ all -> 0x0212 }
                boolean r4 = r1.running     // Catch:{ all -> 0x0212 }
                if (r4 == 0) goto L_0x01c9
                java.util.concurrent.ArrayBlockingQueue<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r4 = r1.buffers     // Catch:{ all -> 0x0212 }
                r4.put(r2)     // Catch:{ all -> 0x0212 }
            L_0x01c9:
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r4 = r1.buffersToWrite     // Catch:{ all -> 0x0212 }
                boolean r4 = r4.isEmpty()     // Catch:{ all -> 0x0212 }
                if (r4 != 0) goto L_0x01db
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r2 = r1.buffersToWrite     // Catch:{ all -> 0x0212 }
                r4 = 0
                java.lang.Object r2 = r2.get(r4)     // Catch:{ all -> 0x0212 }
                org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r2 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r2     // Catch:{ all -> 0x0212 }
                goto L_0x01e2
            L_0x01db:
                r4 = 0
                boolean r8 = r2.last     // Catch:{ all -> 0x0212 }
                r0 = r8
                r2 = r13
                goto L_0x01ec
            L_0x01e1:
                r4 = 0
            L_0x01e2:
                int r6 = r6 + 1
                r4 = 0
                r7 = 0
                r8 = 1
                goto L_0x0151
            L_0x01ea:
                r4 = 0
                r5 = 1
            L_0x01ec:
                android.media.MediaCodec r9 = r1.audioEncoder     // Catch:{ all -> 0x0212 }
                r6 = 0
                int r3 = r3.position()     // Catch:{ all -> 0x0212 }
                r7 = 0
                int r13 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
                if (r13 != 0) goto L_0x01fb
            L_0x01f9:
                r13 = r7
                goto L_0x0200
            L_0x01fb:
                long r7 = r1.audioStartTime     // Catch:{ all -> 0x0212 }
                long r7 = r11 - r7
                goto L_0x01f9
            L_0x0200:
                if (r0 == 0) goto L_0x0205
                r7 = 4
                r15 = 4
                goto L_0x0206
            L_0x0205:
                r15 = 0
            L_0x0206:
                r11 = r6
                r12 = r3
                r9.queueInputBuffer(r10, r11, r12, r13, r15)     // Catch:{ all -> 0x0212 }
                goto L_0x020e
            L_0x020c:
                r4 = 0
                r5 = 1
            L_0x020e:
                r7 = 0
                r8 = 1
                goto L_0x0125
            L_0x0212:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0216:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.handleAudioFrameAvailable(org.telegram.ui.Components.InstantCameraView$AudioBufferInfo):void");
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:17:0x0050  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x006c  */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x00f6  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x0175  */
        /* JADX WARNING: Removed duplicated region for block: B:41:0x01bf  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleVideoFrameAvailable(long r19, java.lang.Integer r21) {
            /*
                r18 = this;
                r1 = r18
                r2 = r19
                r4 = r21
                r5 = 0
                r1.drainEncoder(r5)     // Catch:{ Exception -> 0x000b }
                goto L_0x0010
            L_0x000b:
                r0 = move-exception
                r6 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
            L_0x0010:
                java.lang.Integer r0 = r1.lastCameraId
                boolean r0 = r0.equals(r4)
                r6 = -1
                if (r0 != 0) goto L_0x001e
                r1.lastTimestamp = r6
                r1.lastCameraId = r4
            L_0x001e:
                long r8 = r1.lastTimestamp
                r10 = 0
                int r0 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                if (r0 != 0) goto L_0x0040
                r1.lastTimestamp = r2
                long r8 = r1.currentTimestamp
                int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                if (r0 == 0) goto L_0x0044
                long r8 = java.lang.System.currentTimeMillis()
                long r12 = r1.lastCommitedFrameTime
                long r8 = r8 - r12
                r12 = 1000000(0xvar_, double:4.940656E-318)
                long r8 = r8 * r12
                r16 = r8
                r8 = r10
                r10 = r16
                goto L_0x0045
            L_0x0040:
                long r10 = r2 - r8
                r1.lastTimestamp = r2
            L_0x0044:
                r8 = r10
            L_0x0045:
                long r12 = java.lang.System.currentTimeMillis()
                r1.lastCommitedFrameTime = r12
                boolean r0 = r1.skippedFirst
                r4 = 1
                if (r0 != 0) goto L_0x0061
                long r12 = r1.skippedTime
                long r12 = r12 + r10
                r1.skippedTime = r12
                long r12 = r1.skippedTime
                r14 = 200000000(0xbebCLASSNAME, double:9.8813129E-316)
                int r0 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
                if (r0 >= 0) goto L_0x005f
                return
            L_0x005f:
                r1.skippedFirst = r4
            L_0x0061:
                long r12 = r1.currentTimestamp
                long r12 = r12 + r10
                r1.currentTimestamp = r12
                long r10 = r1.videoFirst
                int r0 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
                if (r0 != 0) goto L_0x008c
                r6 = 1000(0x3e8, double:4.94E-321)
                long r6 = r2 / r6
                r1.videoFirst = r6
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x008c
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r6 = "first video frame was at "
                r0.append(r6)
                long r6 = r1.videoFirst
                r0.append(r6)
                java.lang.String r0 = r0.toString()
                org.telegram.messenger.FileLog.d(r0)
            L_0x008c:
                r1.videoLast = r2
                int r0 = r1.drawProgram
                android.opengl.GLES20.glUseProgram(r0)
                int r10 = r1.positionHandle
                r11 = 3
                r12 = 5126(0x1406, float:7.183E-42)
                r13 = 0
                r14 = 12
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                java.nio.FloatBuffer r15 = r0.vertexBuffer
                android.opengl.GLES20.glVertexAttribPointer(r10, r11, r12, r13, r14, r15)
                int r0 = r1.positionHandle
                android.opengl.GLES20.glEnableVertexAttribArray(r0)
                int r10 = r1.textureHandle
                r11 = 2
                r14 = 8
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                java.nio.FloatBuffer r15 = r0.textureBuffer
                android.opengl.GLES20.glVertexAttribPointer(r10, r11, r12, r13, r14, r15)
                int r0 = r1.textureHandle
                android.opengl.GLES20.glEnableVertexAttribArray(r0)
                int r0 = r1.scaleXHandle
                org.telegram.ui.Components.InstantCameraView r2 = org.telegram.ui.Components.InstantCameraView.this
                float r2 = r2.scaleX
                android.opengl.GLES20.glUniform1f(r0, r2)
                int r0 = r1.scaleYHandle
                org.telegram.ui.Components.InstantCameraView r2 = org.telegram.ui.Components.InstantCameraView.this
                float r2 = r2.scaleY
                android.opengl.GLES20.glUniform1f(r0, r2)
                int r0 = r1.vertexMatrixHandle
                org.telegram.ui.Components.InstantCameraView r2 = org.telegram.ui.Components.InstantCameraView.this
                float[] r2 = r2.mMVPMatrix
                android.opengl.GLES20.glUniformMatrix4fv(r0, r4, r5, r2, r5)
                r0 = 33984(0x84c0, float:4.7622E-41)
                android.opengl.GLES20.glActiveTexture(r0)
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                int[] r0 = r0.oldCameraTexture
                r0 = r0[r5]
                r2 = 3042(0xbe2, float:4.263E-42)
                r3 = 4
                r6 = 5
                r7 = 36197(0x8d65, float:5.0723E-41)
                r10 = 1065353216(0x3var_, float:1.0)
                if (r0 == 0) goto L_0x011d
                boolean r0 = r1.blendEnabled
                if (r0 != 0) goto L_0x00ff
                android.opengl.GLES20.glEnable(r2)
                r1.blendEnabled = r4
            L_0x00ff:
                int r0 = r1.textureMatrixHandle
                org.telegram.ui.Components.InstantCameraView r11 = org.telegram.ui.Components.InstantCameraView.this
                float[] r11 = r11.moldSTMatrix
                android.opengl.GLES20.glUniformMatrix4fv(r0, r4, r5, r11, r5)
                int r0 = r1.alphaHandle
                android.opengl.GLES20.glUniform1f(r0, r10)
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                int[] r0 = r0.oldCameraTexture
                r0 = r0[r5]
                android.opengl.GLES20.glBindTexture(r7, r0)
                android.opengl.GLES20.glDrawArrays(r6, r5, r3)
            L_0x011d:
                int r0 = r1.textureMatrixHandle
                org.telegram.ui.Components.InstantCameraView r11 = org.telegram.ui.Components.InstantCameraView.this
                float[] r11 = r11.mSTMatrix
                android.opengl.GLES20.glUniformMatrix4fv(r0, r4, r5, r11, r5)
                int r0 = r1.alphaHandle
                org.telegram.ui.Components.InstantCameraView r11 = org.telegram.ui.Components.InstantCameraView.this
                float r11 = r11.cameraTextureAlpha
                android.opengl.GLES20.glUniform1f(r0, r11)
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                int[] r0 = r0.cameraTexture
                r0 = r0[r5]
                android.opengl.GLES20.glBindTexture(r7, r0)
                android.opengl.GLES20.glDrawArrays(r6, r5, r3)
                int r0 = r1.positionHandle
                android.opengl.GLES20.glDisableVertexAttribArray(r0)
                int r0 = r1.textureHandle
                android.opengl.GLES20.glDisableVertexAttribArray(r0)
                android.opengl.GLES20.glBindTexture(r7, r5)
                android.opengl.GLES20.glUseProgram(r5)
                android.opengl.EGLDisplay r0 = r1.eglDisplay
                android.opengl.EGLSurface r3 = r1.eglSurface
                long r6 = r1.currentTimestamp
                android.opengl.EGLExt.eglPresentationTimeANDROID(r0, r3, r6)
                android.opengl.EGLDisplay r0 = r1.eglDisplay
                android.opengl.EGLSurface r3 = r1.eglSurface
                android.opengl.EGL14.eglSwapBuffers(r0, r3)
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                int[] r0 = r0.oldCameraTexture
                r0 = r0[r5]
                if (r0 == 0) goto L_0x01bf
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                float r0 = r0.cameraTextureAlpha
                int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
                if (r0 >= 0) goto L_0x01bf
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                float r3 = r0.cameraTextureAlpha
                float r6 = (float) r8
                r7 = 1295957024(0x4d3ebCLASSNAME, float:2.0E8)
                float r6 = r6 / r7
                float r3 = r3 + r6
                float unused = r0.cameraTextureAlpha = r3
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                float r0 = r0.cameraTextureAlpha
                int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
                if (r0 <= 0) goto L_0x01d4
                android.opengl.GLES20.glDisable(r2)
                r1.blendEnabled = r5
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                float unused = r0.cameraTextureAlpha = r10
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                int[] r0 = r0.oldCameraTexture
                android.opengl.GLES20.glDeleteTextures(r4, r0, r5)
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                int[] r0 = r0.oldCameraTexture
                r0[r5] = r5
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                boolean r0 = r0.cameraReady
                if (r0 != 0) goto L_0x01d4
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                boolean unused = r0.cameraReady = r4
                org.telegram.ui.Components.-$$Lambda$InstantCameraView$VideoRecorder$ONc2-IZzZjKNnbhigUCIh3I7vt4 r0 = new org.telegram.ui.Components.-$$Lambda$InstantCameraView$VideoRecorder$ONc2-IZzZjKNnbhigUCIh3I7vt4
                r0.<init>()
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                goto L_0x01d4
            L_0x01bf:
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                boolean r0 = r0.cameraReady
                if (r0 != 0) goto L_0x01d4
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                boolean unused = r0.cameraReady = r4
                org.telegram.ui.Components.-$$Lambda$InstantCameraView$VideoRecorder$cj_4IZt_HllVK7x5iDyMvDcfDnc r0 = new org.telegram.ui.Components.-$$Lambda$InstantCameraView$VideoRecorder$cj_4IZt_HllVK7x5iDyMvDcfDnc
                r0.<init>()
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            L_0x01d4:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.handleVideoFrameAvailable(long, java.lang.Integer):void");
        }

        public /* synthetic */ void lambda$handleVideoFrameAvailable$0$InstantCameraView$VideoRecorder() {
            InstantCameraView.this.textureOverlayView.animate().setDuration(120).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).start();
        }

        public /* synthetic */ void lambda$handleVideoFrameAvailable$1$InstantCameraView$VideoRecorder() {
            InstantCameraView.this.textureOverlayView.animate().setDuration(120).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).start();
        }

        /* access modifiers changed from: private */
        public void handleStopRecording(int i) {
            if (this.running) {
                this.sendWhenDone = i;
                this.running = false;
                return;
            }
            try {
                drainEncoder(true);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            MediaCodec mediaCodec = this.videoEncoder;
            if (mediaCodec != null) {
                try {
                    mediaCodec.stop();
                    this.videoEncoder.release();
                    this.videoEncoder = null;
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            MediaCodec mediaCodec2 = this.audioEncoder;
            if (mediaCodec2 != null) {
                try {
                    mediaCodec2.stop();
                    this.audioEncoder.release();
                    this.audioEncoder = null;
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
            }
            MP4Builder mP4Builder = this.mediaMuxer;
            if (mP4Builder != null) {
                try {
                    mP4Builder.finishMovie();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            if (i != 0) {
                AndroidUtilities.runOnUIThread(new Runnable(i) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        InstantCameraView.VideoRecorder.this.lambda$handleStopRecording$3$InstantCameraView$VideoRecorder(this.f$1);
                    }
                });
            } else {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).cancelUploadFile(this.videoFile.getAbsolutePath(), false);
                this.videoFile.delete();
            }
            EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            Surface surface2 = this.surface;
            if (surface2 != null) {
                surface2.release();
                this.surface = null;
            }
            android.opengl.EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay != EGL14.EGL_NO_DISPLAY) {
                android.opengl.EGLSurface eGLSurface = EGL14.EGL_NO_SURFACE;
                EGL14.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(this.eglDisplay);
            }
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglConfig = null;
            this.handler.exit();
        }

        public /* synthetic */ void lambda$handleStopRecording$3$InstantCameraView$VideoRecorder(int i) {
            VideoEditedInfo unused = InstantCameraView.this.videoEditedInfo = new VideoEditedInfo();
            InstantCameraView.this.videoEditedInfo.roundVideo = true;
            InstantCameraView.this.videoEditedInfo.startTime = -1;
            InstantCameraView.this.videoEditedInfo.endTime = -1;
            InstantCameraView.this.videoEditedInfo.file = InstantCameraView.this.file;
            InstantCameraView.this.videoEditedInfo.encryptedFile = InstantCameraView.this.encryptedFile;
            InstantCameraView.this.videoEditedInfo.key = InstantCameraView.this.key;
            InstantCameraView.this.videoEditedInfo.iv = InstantCameraView.this.iv;
            InstantCameraView.this.videoEditedInfo.estimatedSize = Math.max(1, InstantCameraView.this.size);
            InstantCameraView.this.videoEditedInfo.framerate = 25;
            VideoEditedInfo access$1100 = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalWidth = 240;
            access$1100.resultWidth = 240;
            VideoEditedInfo access$11002 = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalHeight = 240;
            access$11002.resultHeight = 240;
            InstantCameraView.this.videoEditedInfo.originalPath = this.videoFile.getAbsolutePath();
            if (i != 1) {
                VideoPlayer unused2 = InstantCameraView.this.videoPlayer = new VideoPlayer();
                InstantCameraView.this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                    public void onRenderedFirstFrame() {
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }

                    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                    }

                    public void onStateChanged(boolean z, int i) {
                        if (InstantCameraView.this.videoPlayer != null && InstantCameraView.this.videoPlayer.isPlaying() && i == 4) {
                            VideoPlayer access$1000 = InstantCameraView.this.videoPlayer;
                            long j = 0;
                            if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                                j = InstantCameraView.this.videoEditedInfo.startTime;
                            }
                            access$1000.seekTo(j);
                        }
                    }

                    public void onError(Exception exc) {
                        FileLog.e((Throwable) exc);
                    }
                });
                InstantCameraView.this.videoPlayer.setTextureView(InstantCameraView.this.textureView);
                InstantCameraView.this.videoPlayer.preparePlayer(Uri.fromFile(this.videoFile), "other");
                InstantCameraView.this.videoPlayer.play();
                InstantCameraView.this.videoPlayer.setMute(true);
                InstantCameraView.this.startProgressTimer();
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofInt(InstantCameraView.this.paint, "alpha", new int[]{0}), ObjectAnimator.ofFloat(InstantCameraView.this.muteImageView, View.ALPHA, new float[]{1.0f})});
                animatorSet.setDuration(180);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.start();
                InstantCameraView.this.videoEditedInfo.estimatedDuration = InstantCameraView.this.duration;
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.audioDidSent, Integer.valueOf(InstantCameraView.this.recordingGuid), InstantCameraView.this.videoEditedInfo, this.videoFile.getAbsolutePath());
            } else if (InstantCameraView.this.baseFragment.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(InstantCameraView.this.baseFragment.getParentActivity(), InstantCameraView.this.baseFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                    public final void didSelectDate(boolean z, int i) {
                        InstantCameraView.VideoRecorder.this.lambda$null$2$InstantCameraView$VideoRecorder(z, i);
                    }
                });
            } else {
                ChatActivity access$3900 = InstantCameraView.this.baseFragment;
                MediaController.PhotoEntry photoEntry = r4;
                MediaController.PhotoEntry photoEntry2 = new MediaController.PhotoEntry(0, 0, 0, this.videoFile.getAbsolutePath(), 0, true, 0, 0, 0);
                access$3900.sendMedia(photoEntry, InstantCameraView.this.videoEditedInfo, true, 0);
            }
            didWriteData(this.videoFile, 0, true);
            MediaController.getInstance().requestAudioFocus(false);
        }

        public /* synthetic */ void lambda$null$2$InstantCameraView$VideoRecorder(boolean z, int i) {
            InstantCameraView.this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0, this.videoFile.getAbsolutePath(), 0, true, 0, 0, 0), InstantCameraView.this.videoEditedInfo, z, i);
            InstantCameraView.this.startAnimation(false);
        }

        /* access modifiers changed from: private */
        public void prepareEncoder() {
            try {
                int minBufferSize = AudioRecord.getMinBufferSize(44100, 16, 2);
                if (minBufferSize <= 0) {
                    minBufferSize = 3584;
                }
                int i = 49152;
                if (49152 < minBufferSize) {
                    i = ((minBufferSize / 2048) + 1) * 2048 * 2;
                }
                for (int i2 = 0; i2 < 3; i2++) {
                    this.buffers.add(new AudioBufferInfo());
                }
                AudioRecord audioRecord = r9;
                AudioRecord audioRecord2 = new AudioRecord(0, 44100, 16, 2, i);
                this.audioRecorder = audioRecord;
                this.audioRecorder.startRecording();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("initied audio record with channels " + this.audioRecorder.getChannelCount() + " sample rate = " + this.audioRecorder.getSampleRate() + " bufferSize = " + i);
                }
                Thread thread = new Thread(this.recorderRunnable);
                thread.setPriority(10);
                thread.start();
                this.audioBufferInfo = new MediaCodec.BufferInfo();
                this.videoBufferInfo = new MediaCodec.BufferInfo();
                MediaFormat mediaFormat = new MediaFormat();
                mediaFormat.setString("mime", "audio/mp4a-latm");
                mediaFormat.setInteger("aac-profile", 2);
                mediaFormat.setInteger("sample-rate", 44100);
                mediaFormat.setInteger("channel-count", 1);
                mediaFormat.setInteger("bitrate", 32000);
                mediaFormat.setInteger("max-input-size", 20480);
                this.audioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
                this.audioEncoder.configure(mediaFormat, (Surface) null, (MediaCrypto) null, 1);
                this.audioEncoder.start();
                this.videoEncoder = MediaCodec.createEncoderByType("video/avc");
                this.firstEncode = true;
                MediaFormat createVideoFormat = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                createVideoFormat.setInteger("color-format", NUM);
                createVideoFormat.setInteger("bitrate", this.videoBitrate);
                createVideoFormat.setInteger("frame-rate", 30);
                createVideoFormat.setInteger("i-frame-interval", 1);
                this.videoEncoder.configure(createVideoFormat, (Surface) null, (MediaCrypto) null, 1);
                this.surface = this.videoEncoder.createInputSurface();
                this.videoEncoder.start();
                Mp4Movie mp4Movie = new Mp4Movie();
                mp4Movie.setCacheFile(this.videoFile);
                mp4Movie.setRotation(0);
                mp4Movie.setSize(this.videoWidth, this.videoHeight);
                this.mediaMuxer = new MP4Builder().createMovie(mp4Movie, InstantCameraView.this.isSecretChat);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        InstantCameraView.VideoRecorder.this.lambda$prepareEncoder$4$InstantCameraView$VideoRecorder();
                    }
                });
                if (this.eglDisplay == EGL14.EGL_NO_DISPLAY) {
                    this.eglDisplay = EGL14.eglGetDisplay(0);
                    android.opengl.EGLDisplay eGLDisplay = this.eglDisplay;
                    if (eGLDisplay != EGL14.EGL_NO_DISPLAY) {
                        int[] iArr = new int[2];
                        if (EGL14.eglInitialize(eGLDisplay, iArr, 0, iArr, 1)) {
                            if (this.eglContext == EGL14.EGL_NO_CONTEXT) {
                                android.opengl.EGLConfig[] eGLConfigArr = new android.opengl.EGLConfig[1];
                                if (EGL14.eglChooseConfig(this.eglDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12610, 1, 12344}, 0, eGLConfigArr, 0, eGLConfigArr.length, new int[1], 0)) {
                                    this.eglContext = EGL14.eglCreateContext(this.eglDisplay, eGLConfigArr[0], this.sharedEglContext, new int[]{12440, 2, 12344}, 0);
                                    this.eglConfig = eGLConfigArr[0];
                                } else {
                                    throw new RuntimeException("Unable to find a suitable EGLConfig");
                                }
                            }
                            EGL14.eglQueryContext(this.eglDisplay, this.eglContext, 12440, new int[1], 0);
                            if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
                                this.eglSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surface, new int[]{12344}, 0);
                                android.opengl.EGLSurface eGLSurface = this.eglSurface;
                                if (eGLSurface == null) {
                                    throw new RuntimeException("surface was null");
                                } else if (!EGL14.eglMakeCurrent(this.eglDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
                                    }
                                    throw new RuntimeException("eglMakeCurrent failed");
                                } else {
                                    GLES20.glBlendFunc(770, 771);
                                    int access$1900 = InstantCameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                                    int access$19002 = InstantCameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   float coef = ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0));\n   vec3 color = texture2D(sTexture, vTextureCoord).rgb * coef + (1.0 - step(0.001, coef));\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n");
                                    if (access$1900 != 0 && access$19002 != 0) {
                                        this.drawProgram = GLES20.glCreateProgram();
                                        GLES20.glAttachShader(this.drawProgram, access$1900);
                                        GLES20.glAttachShader(this.drawProgram, access$19002);
                                        GLES20.glLinkProgram(this.drawProgram);
                                        int[] iArr2 = new int[1];
                                        GLES20.glGetProgramiv(this.drawProgram, 35714, iArr2, 0);
                                        if (iArr2[0] == 0) {
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
                                    }
                                }
                            } else {
                                throw new IllegalStateException("surface already created");
                            }
                        } else {
                            this.eglDisplay = null;
                            throw new RuntimeException("unable to initialize EGL14");
                        }
                    } else {
                        throw new RuntimeException("unable to get EGL14 display");
                    }
                } else {
                    throw new RuntimeException("EGL already set up");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public /* synthetic */ void lambda$prepareEncoder$4$InstantCameraView$VideoRecorder() {
            if (!InstantCameraView.this.cancelled) {
                try {
                    InstantCameraView.this.performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
                AndroidUtilities.lockOrientation(InstantCameraView.this.baseFragment.getParentActivity());
                boolean unused2 = InstantCameraView.this.recording = true;
                long unused3 = InstantCameraView.this.recordStartTime = System.currentTimeMillis();
                AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable);
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordStarted, Integer.valueOf(InstantCameraView.this.recordingGuid));
            }
        }

        public Surface getInputSurface() {
            return this.surface;
        }

        private void didWriteData(File file, long j, boolean z) {
            long j2 = 0;
            if (this.videoConvertFirstWrite) {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).uploadFile(file.toString(), InstantCameraView.this.isSecretChat, false, 1, 33554432);
                this.videoConvertFirstWrite = false;
                if (z) {
                    FileLoader instance = FileLoader.getInstance(InstantCameraView.this.currentAccount);
                    String file2 = file.toString();
                    boolean access$3800 = InstantCameraView.this.isSecretChat;
                    if (z) {
                        j2 = file.length();
                    }
                    instance.checkUploadNewDataAvailable(file2, access$3800, j, j2);
                    return;
                }
                return;
            }
            FileLoader instance2 = FileLoader.getInstance(InstantCameraView.this.currentAccount);
            String file3 = file.toString();
            boolean access$38002 = InstantCameraView.this.isSecretChat;
            if (z) {
                j2 = file.length();
            }
            instance2.checkUploadNewDataAvailable(file3, access$38002, j, j2);
        }

        public void drainEncoder(boolean z) throws Exception {
            ByteBuffer byteBuffer;
            ByteBuffer byteBuffer2;
            ByteBuffer byteBuffer3;
            ByteBuffer byteBuffer4;
            if (z) {
                this.videoEncoder.signalEndOfInputStream();
            }
            ByteBuffer[] outputBuffers = Build.VERSION.SDK_INT < 21 ? this.videoEncoder.getOutputBuffers() : null;
            while (true) {
                int dequeueOutputBuffer = this.videoEncoder.dequeueOutputBuffer(this.videoBufferInfo, 10000);
                byte b = 1;
                if (dequeueOutputBuffer == -1) {
                    if (!z) {
                        break;
                    }
                } else if (dequeueOutputBuffer == -3) {
                    if (Build.VERSION.SDK_INT < 21) {
                        outputBuffers = this.videoEncoder.getOutputBuffers();
                    }
                } else if (dequeueOutputBuffer == -2) {
                    MediaFormat outputFormat = this.videoEncoder.getOutputFormat();
                    if (this.videoTrackIndex == -5) {
                        this.videoTrackIndex = this.mediaMuxer.addTrack(outputFormat, false);
                        if (outputFormat.containsKey("prepend-sps-pps-to-idr-frames") && outputFormat.getInteger("prepend-sps-pps-to-idr-frames") == 1) {
                            this.prependHeaderSize = outputFormat.getByteBuffer("csd-0").limit() + outputFormat.getByteBuffer("csd-1").limit();
                        }
                    }
                } else if (dequeueOutputBuffer < 0) {
                    continue;
                } else {
                    if (Build.VERSION.SDK_INT < 21) {
                        byteBuffer2 = outputBuffers[dequeueOutputBuffer];
                    } else {
                        byteBuffer2 = this.videoEncoder.getOutputBuffer(dequeueOutputBuffer);
                    }
                    if (byteBuffer2 != null) {
                        MediaCodec.BufferInfo bufferInfo = this.videoBufferInfo;
                        int i = bufferInfo.size;
                        if (i > 1) {
                            int i2 = bufferInfo.flags;
                            if ((i2 & 2) == 0) {
                                int i3 = this.prependHeaderSize;
                                if (!(i3 == 0 || (i2 & 1) == 0)) {
                                    bufferInfo.offset += i3;
                                    bufferInfo.size = i - i3;
                                }
                                if (this.firstEncode) {
                                    MediaCodec.BufferInfo bufferInfo2 = this.videoBufferInfo;
                                    if ((bufferInfo2.flags & 1) != 0) {
                                        if (bufferInfo2.size > 100) {
                                            byteBuffer2.position(bufferInfo2.offset);
                                            byte[] bArr = new byte[100];
                                            byteBuffer2.get(bArr);
                                            int i4 = 0;
                                            int i5 = 0;
                                            while (true) {
                                                if (i4 < bArr.length - 4) {
                                                    if (bArr[i4] == 0 && bArr[i4 + 1] == 0 && bArr[i4 + 2] == 0 && bArr[i4 + 3] == 1 && (i5 = i5 + 1) > 1) {
                                                        MediaCodec.BufferInfo bufferInfo3 = this.videoBufferInfo;
                                                        bufferInfo3.offset += i4;
                                                        bufferInfo3.size -= i4;
                                                        break;
                                                    }
                                                    i4++;
                                                } else {
                                                    break;
                                                }
                                            }
                                        }
                                        this.firstEncode = false;
                                    }
                                }
                                long writeSampleData = this.mediaMuxer.writeSampleData(this.videoTrackIndex, byteBuffer2, this.videoBufferInfo, true);
                                if (writeSampleData != 0) {
                                    didWriteData(this.videoFile, writeSampleData, false);
                                }
                            } else if (this.videoTrackIndex == -5) {
                                byte[] bArr2 = new byte[i];
                                byteBuffer2.limit(bufferInfo.offset + i);
                                byteBuffer2.position(this.videoBufferInfo.offset);
                                byteBuffer2.get(bArr2);
                                int i6 = this.videoBufferInfo.size - 1;
                                while (true) {
                                    if (i6 < 0 || i6 <= 3) {
                                        byteBuffer4 = null;
                                        byteBuffer3 = null;
                                    } else {
                                        if (bArr2[i6] == b && bArr2[i6 - 1] == 0 && bArr2[i6 - 2] == 0) {
                                            int i7 = i6 - 3;
                                            if (bArr2[i7] == 0) {
                                                byteBuffer4 = ByteBuffer.allocate(i7);
                                                byteBuffer3 = ByteBuffer.allocate(this.videoBufferInfo.size - i7);
                                                byteBuffer4.put(bArr2, 0, i7).position(0);
                                                byteBuffer3.put(bArr2, i7, this.videoBufferInfo.size - i7).position(0);
                                                break;
                                            }
                                        }
                                        i6--;
                                        b = 1;
                                    }
                                }
                                MediaFormat createVideoFormat = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                                if (!(byteBuffer4 == null || byteBuffer3 == null)) {
                                    createVideoFormat.setByteBuffer("csd-0", byteBuffer4);
                                    createVideoFormat.setByteBuffer("csd-1", byteBuffer3);
                                }
                                this.videoTrackIndex = this.mediaMuxer.addTrack(createVideoFormat, false);
                            }
                        }
                        this.videoEncoder.releaseOutputBuffer(dequeueOutputBuffer, false);
                        if ((this.videoBufferInfo.flags & 4) != 0) {
                            break;
                        }
                    } else {
                        throw new RuntimeException("encoderOutputBuffer " + dequeueOutputBuffer + " was null");
                    }
                }
            }
            if (Build.VERSION.SDK_INT < 21) {
                outputBuffers = this.audioEncoder.getOutputBuffers();
            }
            while (true) {
                int dequeueOutputBuffer2 = this.audioEncoder.dequeueOutputBuffer(this.audioBufferInfo, 0);
                if (dequeueOutputBuffer2 == -1) {
                    if (!z) {
                        return;
                    }
                    if (!this.running && this.sendWhenDone == 0) {
                        return;
                    }
                } else if (dequeueOutputBuffer2 == -3) {
                    if (Build.VERSION.SDK_INT < 21) {
                        outputBuffers = this.audioEncoder.getOutputBuffers();
                    }
                } else if (dequeueOutputBuffer2 == -2) {
                    MediaFormat outputFormat2 = this.audioEncoder.getOutputFormat();
                    if (this.audioTrackIndex == -5) {
                        this.audioTrackIndex = this.mediaMuxer.addTrack(outputFormat2, true);
                    }
                } else if (dequeueOutputBuffer2 < 0) {
                    continue;
                } else {
                    if (Build.VERSION.SDK_INT < 21) {
                        byteBuffer = outputBuffers[dequeueOutputBuffer2];
                    } else {
                        byteBuffer = this.audioEncoder.getOutputBuffer(dequeueOutputBuffer2);
                    }
                    if (byteBuffer != null) {
                        MediaCodec.BufferInfo bufferInfo4 = this.audioBufferInfo;
                        if ((bufferInfo4.flags & 2) != 0) {
                            bufferInfo4.size = 0;
                        }
                        MediaCodec.BufferInfo bufferInfo5 = this.audioBufferInfo;
                        if (bufferInfo5.size != 0) {
                            long writeSampleData2 = this.mediaMuxer.writeSampleData(this.audioTrackIndex, byteBuffer, bufferInfo5, false);
                            if (writeSampleData2 != 0) {
                                didWriteData(this.videoFile, writeSampleData2, false);
                            }
                        }
                        this.audioEncoder.releaseOutputBuffer(dequeueOutputBuffer2, false);
                        if ((this.audioBufferInfo.flags & 4) != 0) {
                            return;
                        }
                    } else {
                        throw new RuntimeException("encoderOutputBuffer " + dequeueOutputBuffer2 + " was null");
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
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
            } finally {
                super.finalize();
            }
        }
    }
}
