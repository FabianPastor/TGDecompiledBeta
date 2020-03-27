package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.InstantCameraView;
import org.telegram.ui.Components.VideoPlayer;

@TargetApi(18)
public class InstantCameraView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private float animationTranslationY;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private Size aspectRatio;
    /* access modifiers changed from: private */
    public ChatActivity baseFragment;
    private BlurBehindDrawable blurBehindDrawable;
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
    /* access modifiers changed from: private */
    public TLRPC$InputEncryptedFile encryptedFile;
    /* access modifiers changed from: private */
    public TLRPC$InputFile file;
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
    public boolean opened;
    /* access modifiers changed from: private */
    public Paint paint;
    private float panTranslationY;
    private View parentView;
    private Size pictureSize;
    private int[] position = new int[2];
    /* access modifiers changed from: private */
    public Size previewSize;
    private float progress;
    private Timer progressTimer;
    /* access modifiers changed from: private */
    public long recordStartTime;
    /* access modifiers changed from: private */
    public long recordedTime;
    /* access modifiers changed from: private */
    public boolean recording;
    /* access modifiers changed from: private */
    public int recordingGuid;
    private RectF rect;
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
        this.parentView = chatActivity.getFragmentView();
        setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return InstantCameraView.this.lambda$new$0$InstantCameraView(view, motionEvent);
            }
        });
        setWillNotDraw(false);
        this.baseFragment = chatActivity;
        this.recordingGuid = chatActivity.getClassGuid();
        this.isSecretChat = this.baseFragment.getCurrentEncryptedChat() != null;
        AnonymousClass2 r14 = new Paint(1) {
            public void setAlpha(int i) {
                super.setAlpha(i);
                InstantCameraView.this.invalidate();
            }
        };
        this.paint = r14;
        r14.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setColor(-1);
        this.rect = new RectF();
        if (Build.VERSION.SDK_INT >= 21) {
            AnonymousClass3 r142 = new FrameLayout(context) {
                public void setScaleX(float f) {
                    super.setScaleX(f);
                    InstantCameraView.this.invalidate();
                }

                public void setAlpha(float f) {
                    super.setAlpha(f);
                    InstantCameraView.this.invalidate();
                }
            };
            this.cameraContainer = r142;
            r142.setOutlineProvider(new ViewOutlineProvider(this) {
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
            AnonymousClass5 r0 = new FrameLayout(context) {
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
            this.cameraContainer = r0;
            r0.setWillNotDraw(false);
            this.cameraContainer.setLayerType(2, (Paint) null);
        }
        FrameLayout frameLayout = this.cameraContainer;
        int i = AndroidUtilities.roundMessageSize;
        addView(frameLayout, new FrameLayout.LayoutParams(i, i, 17));
        ImageView imageView = new ImageView(context);
        this.switchCameraButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.switchCameraButton.setContentDescription(LocaleController.getString("AccDescrSwitchCamera", NUM));
        addView(this.switchCameraButton, LayoutHelper.createFrame(62, 62.0f, 83, 8.0f, 0.0f, 0.0f, 0.0f));
        this.switchCameraButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                InstantCameraView.this.lambda$new$1$InstantCameraView(view);
            }
        });
        ImageView imageView2 = new ImageView(context);
        this.muteImageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.muteImageView.setImageResource(NUM);
        this.muteImageView.setAlpha(0.0f);
        addView(this.muteImageView, LayoutHelper.createFrame(48, 48, 17));
        ((FrameLayout.LayoutParams) this.muteImageView.getLayoutParams()).topMargin = (AndroidUtilities.roundMessageSize / 2) - AndroidUtilities.dp(24.0f);
        BackupImageView backupImageView = new BackupImageView(getContext());
        this.textureOverlayView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.roundMessageSize / 2);
        BackupImageView backupImageView2 = this.textureOverlayView;
        int i2 = AndroidUtilities.roundMessageSize;
        addView(backupImageView2, new FrameLayout.LayoutParams(i2, i2, 17));
        setVisibility(4);
        this.blurBehindDrawable = new BlurBehindDrawable(this.parentView, this);
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
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.muteAnimation = animatorSet3;
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
                chatActivity.checkRecordLocked(false);
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$new$1$InstantCameraView(View view) {
        CameraSession cameraSession2;
        if (this.cameraReady && (cameraSession2 = this.cameraSession) != null && cameraSession2.isInitied() && this.cameraThread != null) {
            switchCamera();
            ObjectAnimator duration = ObjectAnimator.ofFloat(this.switchCameraButton, View.SCALE_X, new float[]{0.0f}).setDuration(100);
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    InstantCameraView.this.switchCameraButton.setImageResource(InstantCameraView.this.isFrontface ? NUM : NUM);
                    ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, View.SCALE_X, new float[]{1.0f}).setDuration(100).start();
                }
            });
            duration.start();
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
            this.animationTranslationY = (float) (getMeasuredHeight() / 2);
            updateTranslationY();
        }
        this.blurBehindDrawable.checkSizes();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.FileDidUpload) {
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
        this.blurBehindDrawable.draw(canvas);
        float x = this.cameraContainer.getX();
        float y = this.cameraContainer.getY();
        this.rect.set(x - ((float) AndroidUtilities.dp(8.0f)), y - ((float) AndroidUtilities.dp(8.0f)), ((float) this.cameraContainer.getMeasuredWidth()) + x + ((float) AndroidUtilities.dp(8.0f)), ((float) this.cameraContainer.getMeasuredHeight()) + y + ((float) AndroidUtilities.dp(8.0f)));
        if (this.recording) {
            long currentTimeMillis = System.currentTimeMillis() - this.recordStartTime;
            this.recordedTime = currentTimeMillis;
            this.progress = Math.min(1.0f, ((float) currentTimeMillis) / 60000.0f);
            invalidate();
        }
        if (this.progress != 0.0f) {
            canvas.save();
            canvas.scale(this.cameraContainer.getScaleX(), this.cameraContainer.getScaleY(), this.rect.centerX(), this.rect.centerY());
            canvas.drawArc(this.rect, -90.0f, this.progress * 360.0f, false, this.paint);
            canvas.restore();
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
        BlurBehindDrawable blurBehindDrawable2;
        super.setVisibility(i);
        if (!(i == 0 || (blurBehindDrawable2 = this.blurBehindDrawable) == null)) {
            blurBehindDrawable2.clear();
        }
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
                MediaController.getInstance().lambda$startAudioAgain$7$MediaController(MediaController.getInstance().getPlayingMessageObject());
                File directory = FileLoader.getDirectory(4);
                this.cameraFile = new File(directory, SharedConfig.getLastLocalId() + ".mp4");
                SharedConfig.saveConfig();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("show round camera");
                }
                TextureView textureView2 = new TextureView(getContext());
                this.textureView = textureView2;
                textureView2.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
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
                            CameraGLThread unused = InstantCameraView.this.cameraThread = new CameraGLThread(surfaceTexture, i, i2);
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
            animatorSet2.removeAllListeners();
            this.animatorSet.cancel();
        }
        PipRoundVideoView instance = PipRoundVideoView.getInstance();
        if (instance != null) {
            instance.showTemporary(!z);
        }
        if (z && !this.opened) {
            this.cameraContainer.setTranslationX(0.0f);
            this.textureOverlayView.setTranslationX(0.0f);
            this.animationTranslationY = ((float) getMeasuredHeight()) / 2.0f;
            updateTranslationY();
        }
        this.opened = z;
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
        this.blurBehindDrawable.show(z);
        this.animatorSet = new AnimatorSet();
        float dp = (z || this.recordedTime <= 300) ? 0.0f : ((float) AndroidUtilities.dp(24.0f)) - (((float) getMeasuredWidth()) / 2.0f);
        float[] fArr = new float[2];
        float f = 1.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        fArr[1] = z ? 0.0f : 1.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                InstantCameraView.this.lambda$startAnimation$2$InstantCameraView(valueAnimator);
            }
        });
        AnimatorSet animatorSet3 = this.animatorSet;
        Animator[] animatorArr = new Animator[12];
        ImageView imageView = this.switchCameraButton;
        Property property = View.ALPHA;
        float[] fArr2 = new float[1];
        fArr2[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(imageView, property, fArr2);
        animatorArr[1] = ObjectAnimator.ofFloat(this.muteImageView, View.ALPHA, new float[]{0.0f});
        Paint paint2 = this.paint;
        Property<Paint, Integer> property2 = AnimationProperties.PAINT_ALPHA;
        int[] iArr = new int[1];
        iArr[0] = z ? 255 : 0;
        animatorArr[2] = ObjectAnimator.ofInt(paint2, property2, iArr);
        FrameLayout frameLayout = this.cameraContainer;
        Property property3 = View.ALPHA;
        float[] fArr3 = new float[1];
        fArr3[0] = z ? 1.0f : 0.0f;
        animatorArr[3] = ObjectAnimator.ofFloat(frameLayout, property3, fArr3);
        FrameLayout frameLayout2 = this.cameraContainer;
        Property property4 = View.SCALE_X;
        float[] fArr4 = new float[1];
        fArr4[0] = z ? 1.0f : 0.1f;
        animatorArr[4] = ObjectAnimator.ofFloat(frameLayout2, property4, fArr4);
        FrameLayout frameLayout3 = this.cameraContainer;
        Property property5 = View.SCALE_Y;
        float[] fArr5 = new float[1];
        fArr5[0] = z ? 1.0f : 0.1f;
        animatorArr[5] = ObjectAnimator.ofFloat(frameLayout3, property5, fArr5);
        animatorArr[6] = ObjectAnimator.ofFloat(this.cameraContainer, View.TRANSLATION_X, new float[]{dp});
        BackupImageView backupImageView = this.textureOverlayView;
        Property property6 = View.ALPHA;
        float[] fArr6 = new float[1];
        fArr6[0] = z ? 1.0f : 0.0f;
        animatorArr[7] = ObjectAnimator.ofFloat(backupImageView, property6, fArr6);
        BackupImageView backupImageView2 = this.textureOverlayView;
        Property property7 = View.SCALE_X;
        float[] fArr7 = new float[1];
        fArr7[0] = z ? 1.0f : 0.1f;
        animatorArr[8] = ObjectAnimator.ofFloat(backupImageView2, property7, fArr7);
        BackupImageView backupImageView3 = this.textureOverlayView;
        Property property8 = View.SCALE_Y;
        float[] fArr8 = new float[1];
        if (!z) {
            f = 0.1f;
        }
        fArr8[0] = f;
        animatorArr[9] = ObjectAnimator.ofFloat(backupImageView3, property8, fArr8);
        animatorArr[10] = ObjectAnimator.ofFloat(this.textureOverlayView, View.TRANSLATION_X, new float[]{dp});
        animatorArr[11] = ofFloat;
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
        } else {
            setTranslationX(0.0f);
        }
        this.animatorSet.setDuration(180);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.start();
    }

    public /* synthetic */ void lambda$startAnimation$2$InstantCameraView(ValueAnimator valueAnimator) {
        this.animationTranslationY = (((float) getMeasuredHeight()) / 2.0f) * ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateTranslationY();
    }

    private void updateTranslationY() {
        this.textureOverlayView.setTranslationY(this.animationTranslationY + this.panTranslationY);
        this.cameraContainer.setTranslationY(this.animationTranslationY + this.panTranslationY);
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
            int i5 = 4;
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
                    long j3 = j2 - j;
                    videoEditedInfo4.estimatedDuration = j3;
                    double d2 = (double) this.size;
                    double d3 = (double) j3;
                    Double.isNaN(d3);
                    Double.isNaN(d);
                    Double.isNaN(d2);
                    videoEditedInfo4.estimatedSize = Math.max(1, (long) (d2 * (d3 / d)));
                    VideoEditedInfo videoEditedInfo5 = this.videoEditedInfo;
                    videoEditedInfo5.bitrate = 400000;
                    long j4 = videoEditedInfo5.startTime;
                    if (j4 > 0) {
                        videoEditedInfo5.startTime = j4 * 1000;
                    }
                    VideoEditedInfo videoEditedInfo6 = this.videoEditedInfo;
                    long j5 = videoEditedInfo6.endTime;
                    if (j5 > 0) {
                        videoEditedInfo6.endTime = j5 * 1000;
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
            boolean z2 = this.recordedTime < 800;
            this.cancelled = z2;
            this.recording = false;
            if (!z2) {
                i5 = i3 == 3 ? 2 : 5;
            }
            if (this.cameraThread != null) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recordStopped, Integer.valueOf(this.recordingGuid), Integer.valueOf(i5));
                int i6 = this.cancelled ? 0 : i3 == 3 ? 2 : 1;
                saveLastCameraBitmap();
                this.cameraThread.shutdown(i6);
                this.cameraThread = null;
            }
            if (this.cancelled) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), true, Integer.valueOf((int) this.recordedTime));
                startAnimation(false);
                MediaController.getInstance().requestAudioFocus(false);
            }
        }
    }

    private void saveLastCameraBitmap() {
        Bitmap bitmap = this.textureView.getBitmap();
        if (bitmap != null && bitmap.getPixel(0, 0) != 0) {
            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(this.textureView.getBitmap(), 80, 80, true);
            this.lastBitmap = createScaledBitmap;
            if (createScaledBitmap != null) {
                Utilities.blurBitmap(createScaledBitmap, 7, 1, createScaledBitmap.getWidth(), this.lastBitmap.getHeight(), this.lastBitmap.getRowBytes());
                try {
                    this.lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg")));
                } catch (Throwable unused) {
                }
            }
        }
    }

    public void cancel(boolean z) {
        stopProgressTimer();
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.releasePlayer(true);
            this.videoPlayer = null;
        }
        if (this.textureView != null) {
            this.cancelled = true;
            this.recording = false;
            NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
            int i = NotificationCenter.recordStopped;
            Object[] objArr = new Object[2];
            objArr[0] = Integer.valueOf(this.recordingGuid);
            objArr[1] = Integer.valueOf(z ? 0 : 6);
            instance.postNotificationName(i, objArr);
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
            this.blurBehindDrawable.show(false);
            invalidate();
        }
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
        ViewGroup viewGroup;
        destroy(z, (Runnable) null);
        this.cameraContainer.setTranslationX(0.0f);
        this.textureOverlayView.setTranslationX(0.0f);
        this.animationTranslationY = 0.0f;
        updateTranslationY();
        MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        TextureView textureView2 = this.textureView;
        if (!(textureView2 == null || (viewGroup = (ViewGroup) textureView2.getParent()) == null)) {
            viewGroup.removeView(this.textureView);
        }
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
        Size chooseOptimalSize = CameraController.chooseOptimalSize(pictureSizes, 480, 270, this.aspectRatio);
        this.pictureSize = chooseOptimalSize;
        if (this.previewSize.mWidth != chooseOptimalSize.mWidth) {
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
                InstantCameraView.this.lambda$createCamera$5$InstantCameraView(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$createCamera$5$InstantCameraView(SurfaceTexture surfaceTexture) {
        if (this.cameraThread != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("create camera session");
            }
            surfaceTexture.setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
            CameraSession cameraSession2 = new CameraSession(this.selectedCamera, this.previewSize, this.pictureSize, 256);
            this.cameraSession = cameraSession2;
            this.cameraThread.setCurrentSession(cameraSession2);
            CameraController.getInstance().openRound(this.cameraSession, surfaceTexture, new Runnable() {
                public final void run() {
                    InstantCameraView.this.lambda$null$3$InstantCameraView();
                }
            }, new Runnable() {
                public final void run() {
                    InstantCameraView.this.lambda$null$4$InstantCameraView();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$3$InstantCameraView() {
        if (this.cameraSession != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("camera initied");
            }
            this.cameraSession.setInitied();
        }
    }

    public /* synthetic */ void lambda$null$4$InstantCameraView() {
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
        Timer timer2 = new Timer();
        this.progressTimer = timer2;
        timer2.schedule(new TimerTask() {
            public void run() {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        InstantCameraView.AnonymousClass9.this.lambda$run$0$InstantCameraView$9();
                    }
                });
            }

            public /* synthetic */ void lambda$run$0$InstantCameraView$9() {
                try {
                    if (InstantCameraView.this.videoPlayer != null && InstantCameraView.this.videoEditedInfo != null) {
                        long j = 0;
                        if (InstantCameraView.this.videoEditedInfo.endTime > 0 && InstantCameraView.this.videoPlayer.getCurrentPosition() >= InstantCameraView.this.videoEditedInfo.endTime) {
                            VideoPlayer access$400 = InstantCameraView.this.videoPlayer;
                            if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                                j = InstantCameraView.this.videoEditedInfo.startTime;
                            }
                            access$400.seekTo(j);
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

    public boolean blurFullyDrawing() {
        BlurBehindDrawable blurBehindDrawable2 = this.blurBehindDrawable;
        return blurBehindDrawable2 != null && blurBehindDrawable2.isFullyDrawing() && this.opened;
    }

    public void invalidateBlur() {
        BlurBehindDrawable blurBehindDrawable2 = this.blurBehindDrawable;
        if (blurBehindDrawable2 != null) {
            blurBehindDrawable2.invalidate();
        }
    }

    public void cancelBlur() {
        this.blurBehindDrawable.show(false);
        invalidate();
    }

    public class CameraGLThread extends DispatchQueue {
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
            EGL10 egl102 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl102;
            EGLDisplay eglGetDisplay = egl102.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (!this.egl10.eglInitialize(eglGetDisplay, new int[2])) {
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
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture2 = this.surfaceTexture;
                if (surfaceTexture2 instanceof SurfaceTexture) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, eGLConfig, surfaceTexture2, (int[]) null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == null || eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else if (!this.egl10.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else {
                        this.eglContext.getGL();
                        float access$700 = (1.0f / InstantCameraView.this.scaleX) / 2.0f;
                        float access$800 = (1.0f / InstantCameraView.this.scaleY) / 2.0f;
                        float f = 0.5f - access$700;
                        float f2 = 0.5f - access$800;
                        float f3 = access$700 + 0.5f;
                        float f4 = access$800 + 0.5f;
                        this.videoEncoder = new VideoRecorder();
                        FloatBuffer unused = InstantCameraView.this.vertexBuffer = ByteBuffer.allocateDirect(48).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        InstantCameraView.this.vertexBuffer.put(new float[]{-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f}).position(0);
                        FloatBuffer unused2 = InstantCameraView.this.textureBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        InstantCameraView.this.textureBuffer.put(new float[]{f, f2, f3, f2, f, f4, f3, f4}).position(0);
                        Matrix.setIdentityM(InstantCameraView.this.mSTMatrix, 0);
                        int access$1300 = InstantCameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                        int access$13002 = InstantCameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
                        if (access$1300 == 0 || access$13002 == 0) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("failed creating shader");
                            }
                            finish();
                            return false;
                        }
                        int glCreateProgram = GLES20.glCreateProgram();
                        this.drawProgram = glCreateProgram;
                        GLES20.glAttachShader(glCreateProgram, access$1300);
                        GLES20.glAttachShader(this.drawProgram, access$13002);
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
                        SurfaceTexture surfaceTexture3 = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                        this.cameraSurface = surfaceTexture3;
                        surfaceTexture3.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
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
                        float access$700 = InstantCameraView.this.scaleX;
                        InstantCameraView instantCameraView = InstantCameraView.this;
                        float unused = instantCameraView.scaleX = instantCameraView.scaleY;
                        float unused2 = InstantCameraView.this.scaleY = access$700;
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
                    SurfaceTexture surfaceTexture3 = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                    this.cameraSurface = surfaceTexture3;
                    surfaceTexture3.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
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

    private static class AudioBufferInfo {
        ByteBuffer[] buffer = new ByteBuffer[10];
        boolean last;
        int lastWroteBuffer;
        long[] offset = new long[10];
        int[] read = new int[10];
        int results;

        public AudioBufferInfo() {
            for (int i = 0; i < 10; i++) {
                this.buffer[i] = ByteBuffer.allocateDirect(2048);
                this.buffer[i].order(ByteOrder.nativeOrder());
            }
        }
    }

    private class VideoRecorder implements Runnable {
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
        private int frameCount;
        /* access modifiers changed from: private */
        public volatile EncoderHandler handler;
        private ArrayList<Bitmap> keyframeThumbs;
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
            this.keyframeThumbs = new ArrayList<>();
            this.recorderRunnable = new Runnable() {
                /* JADX WARNING: Code restructure failed: missing block: B:12:0x002f, code lost:
                    if (org.telegram.ui.Components.InstantCameraView.VideoRecorder.access$2800(r1.this$1) == 0) goto L_0x011c;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r16 = this;
                        r1 = r16
                        r2 = -1
                        r4 = 0
                        r5 = r2
                        r0 = 0
                    L_0x0007:
                        r7 = 1
                        if (r0 != 0) goto L_0x011c
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r8 = r8.running
                        if (r8 != 0) goto L_0x0033
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        android.media.AudioRecord r8 = r8.audioRecorder
                        int r8 = r8.getRecordingState()
                        if (r8 == r7) goto L_0x0033
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x0028 }
                        android.media.AudioRecord r8 = r8.audioRecorder     // Catch:{ Exception -> 0x0028 }
                        r8.stop()     // Catch:{ Exception -> 0x0028 }
                        goto L_0x0029
                    L_0x0028:
                        r0 = 1
                    L_0x0029:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        int r8 = r8.sendWhenDone
                        if (r8 != 0) goto L_0x0033
                        goto L_0x011c
                    L_0x0033:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r8 = r8.buffers
                        boolean r8 = r8.isEmpty()
                        if (r8 == 0) goto L_0x0045
                        org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r8 = new org.telegram.ui.Components.InstantCameraView$AudioBufferInfo
                        r8.<init>()
                        goto L_0x0051
                    L_0x0045:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r8 = r8.buffers
                        java.lang.Object r8 = r8.poll()
                        org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r8 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r8
                    L_0x0051:
                        r8.lastWroteBuffer = r4
                        r9 = 10
                        r8.results = r9
                        r10 = 0
                    L_0x0058:
                        if (r10 >= r9) goto L_0x00d8
                        int r11 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
                        if (r11 != 0) goto L_0x0065
                        long r5 = java.lang.System.nanoTime()
                        r11 = 1000(0x3e8, double:4.94E-321)
                        long r5 = r5 / r11
                    L_0x0065:
                        java.nio.ByteBuffer[] r11 = r8.buffer
                        r11 = r11[r10]
                        r11.rewind()
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r12 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        android.media.AudioRecord r12 = r12.audioRecorder
                        r13 = 2048(0x800, float:2.87E-42)
                        int r12 = r12.read(r11, r13)
                        if (r12 <= 0) goto L_0x00af
                        int r13 = r10 % 2
                        if (r13 != 0) goto L_0x00af
                        r11.limit(r12)
                        r13 = 0
                        r15 = 0
                    L_0x0084:
                        int r2 = r12 / 2
                        if (r15 >= r2) goto L_0x0098
                        short r2 = r11.getShort()
                        int r2 = r2 * r2
                        double r2 = (double) r2
                        java.lang.Double.isNaN(r2)
                        double r13 = r13 + r2
                        int r15 = r15 + 1
                        r2 = -1
                        goto L_0x0084
                    L_0x0098:
                        double r2 = (double) r12
                        java.lang.Double.isNaN(r2)
                        double r13 = r13 / r2
                        r2 = 4611686018427387904(0xNUM, double:2.0)
                        double r13 = r13 / r2
                        double r2 = java.lang.Math.sqrt(r13)
                        org.telegram.ui.Components.-$$Lambda$InstantCameraView$VideoRecorder$1$KPZE6szM9PcDS6RXSdiCNzgniow r13 = new org.telegram.ui.Components.-$$Lambda$InstantCameraView$VideoRecorder$1$KPZE6szM9PcDS6RXSdiCNzgniow
                        r13.<init>(r1, r2)
                        org.telegram.messenger.AndroidUtilities.runOnUIThread(r13)
                        r11.position(r4)
                    L_0x00af:
                        if (r12 > 0) goto L_0x00be
                        r8.results = r10
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r2 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r2 = r2.running
                        if (r2 != 0) goto L_0x00d8
                        r8.last = r7
                        goto L_0x00d8
                    L_0x00be:
                        long[] r2 = r8.offset
                        r2[r10] = r5
                        int[] r2 = r8.read
                        r2[r10] = r12
                        r2 = 1000000(0xvar_, float:1.401298E-39)
                        int r12 = r12 * r2
                        r2 = 44100(0xaCLASSNAME, float:6.1797E-41)
                        int r12 = r12 / r2
                        int r12 = r12 / 2
                        long r2 = (long) r12
                        long r5 = r5 + r2
                        int r10 = r10 + 1
                        r2 = -1
                        goto L_0x0058
                    L_0x00d8:
                        int r2 = r8.results
                        if (r2 >= 0) goto L_0x00f5
                        boolean r2 = r8.last
                        if (r2 == 0) goto L_0x00e1
                        goto L_0x00f5
                    L_0x00e1:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r2 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r2 = r2.running
                        if (r2 != 0) goto L_0x00eb
                        r0 = 1
                        goto L_0x0118
                    L_0x00eb:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r2 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x0118 }
                        java.util.concurrent.ArrayBlockingQueue r2 = r2.buffers     // Catch:{ Exception -> 0x0118 }
                        r2.put(r8)     // Catch:{ Exception -> 0x0118 }
                        goto L_0x0118
                    L_0x00f5:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r2 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r2 = r2.running
                        if (r2 != 0) goto L_0x0102
                        int r2 = r8.results
                        if (r2 >= r9) goto L_0x0102
                        goto L_0x0103
                    L_0x0102:
                        r7 = r0
                    L_0x0103:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r0 = r0.handler
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r2 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r2 = r2.handler
                        r3 = 3
                        android.os.Message r2 = r2.obtainMessage(r3, r8)
                        r0.sendMessage(r2)
                        r0 = r7
                    L_0x0118:
                        r2 = -1
                        goto L_0x0007
                    L_0x011c:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x0126 }
                        android.media.AudioRecord r0 = r0.audioRecorder     // Catch:{ Exception -> 0x0126 }
                        r0.release()     // Catch:{ Exception -> 0x0126 }
                        goto L_0x012a
                    L_0x0126:
                        r0 = move-exception
                        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                    L_0x012a:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r0 = r0.handler
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r2 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r2 = r2.handler
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r3 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        int r3 = r3.sendWhenDone
                        android.os.Message r2 = r2.obtainMessage(r7, r3, r4)
                        r0.sendMessage(r2)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.AnonymousClass1.run():void");
                }

                public /* synthetic */ void lambda$run$0$InstantCameraView$VideoRecorder$1(double d) {
                    NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Integer.valueOf(InstantCameraView.this.recordingGuid), Double.valueOf(d));
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
                    this.keyframeThumbs.clear();
                    this.frameCount = 0;
                    this.handler.sendMessage(this.handler.obtainMessage(0));
                }
            }
        }

        public void stopRecording(int i) {
            this.handler.sendMessage(this.handler.obtainMessage(1, i, 0));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
            r5 = r4.zeroTimeStamps + 1;
            r4.zeroTimeStamps = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
            if (r5 <= 1) goto L_0x0026;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x002b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0020, code lost:
            org.telegram.messenger.FileLog.d("fix timestamp enabled");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0026, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0027, code lost:
            r4.zeroTimeStamps = 0;
            r7 = r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x002b, code lost:
            r4.handler.sendMessage(r4.handler.obtainMessage(2, (int) (r7 >> 32), (int) r7, r6));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x003d, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x000a, code lost:
            r0 = r5.getTimestamp();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
            if (r0 != 0) goto L_0x0027;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void frameAvailable(android.graphics.SurfaceTexture r5, java.lang.Integer r6, long r7) {
            /*
                r4 = this;
                java.lang.Object r0 = r4.sync
                monitor-enter(r0)
                boolean r1 = r4.ready     // Catch:{ all -> 0x003e }
                if (r1 != 0) goto L_0x0009
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                return
            L_0x0009:
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                long r0 = r5.getTimestamp()
                r2 = 0
                int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r5 != 0) goto L_0x0027
                int r5 = r4.zeroTimeStamps
                r0 = 1
                int r5 = r5 + r0
                r4.zeroTimeStamps = r5
                if (r5 <= r0) goto L_0x0026
                boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r5 == 0) goto L_0x002b
                java.lang.String r5 = "fix timestamp enabled"
                org.telegram.messenger.FileLog.d(r5)
                goto L_0x002b
            L_0x0026:
                return
            L_0x0027:
                r5 = 0
                r4.zeroTimeStamps = r5
                r7 = r0
            L_0x002b:
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
            L_0x003e:
                r5 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
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
        /* JADX WARNING: Removed duplicated region for block: B:31:0x00c8  */
        /* JADX WARNING: Removed duplicated region for block: B:98:0x00fb A[EDGE_INSN: B:98:0x00fb->B:38:0x00fb ?: BREAK  , SYNTHETIC] */
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
                if (r0 != 0) goto L_0x00fb
                long r3 = r1.videoFirst
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 != 0) goto L_0x0028
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x0027
                java.lang.String r0 = "video record not yet started"
                org.telegram.messenger.FileLog.d(r0)
            L_0x0027:
                return
            L_0x0028:
                r0 = 0
            L_0x0029:
                int r3 = r2.results
                if (r0 >= r3) goto L_0x00c5
                if (r0 != 0) goto L_0x0069
                long r3 = r1.videoFirst
                long[] r9 = r2.offset
                r10 = r9[r0]
                long r3 = r3 - r10
                long r3 = java.lang.Math.abs(r3)
                r9 = 10000000(0x989680, double:4.9406565E-317)
                int r11 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                if (r11 <= 0) goto L_0x0069
                long r3 = r1.videoFirst
                long[] r9 = r2.offset
                r10 = r9[r0]
                long r3 = r3 - r10
                r1.desyncTime = r3
                r3 = r9[r0]
                r1.audioFirst = r3
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x009d
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "detected desync between audio and video "
                r0.append(r3)
                long r3 = r1.desyncTime
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                org.telegram.messenger.FileLog.d(r0)
                goto L_0x009d
            L_0x0069:
                long[] r3 = r2.offset
                r9 = r3[r0]
                long r11 = r1.videoFirst
                java.lang.String r4 = " timestamp = "
                int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
                if (r13 < 0) goto L_0x009f
                r2.lastWroteBuffer = r0
                r9 = r3[r0]
                r1.audioFirst = r9
                boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r3 == 0) goto L_0x009d
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
            L_0x009d:
                r0 = 1
                goto L_0x00c6
            L_0x009f:
                boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r3 == 0) goto L_0x00c1
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
            L_0x00c1:
                int r0 = r0 + 1
                goto L_0x0029
            L_0x00c5:
                r0 = 0
            L_0x00c6:
                if (r0 != 0) goto L_0x00fb
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x00e2
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "first audio frame not found, removing buffers "
                r0.append(r3)
                int r3 = r2.results
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                org.telegram.messenger.FileLog.d(r0)
            L_0x00e2:
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                r0.remove(r2)
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00fa
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                java.lang.Object r0 = r0.get(r7)
                r2 = r0
                org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r2 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r2
                goto L_0x0028
            L_0x00fa:
                return
            L_0x00fb:
                long r3 = r1.audioStartTime
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 != 0) goto L_0x0109
                long[] r0 = r2.offset
                int r3 = r2.lastWroteBuffer
                r3 = r0[r3]
                r1.audioStartTime = r3
            L_0x0109:
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                int r0 = r0.size()
                if (r0 <= r8) goto L_0x011a
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                java.lang.Object r0 = r0.get(r7)
                r2 = r0
                org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r2 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r2
            L_0x011a:
                r1.drainEncoder(r7)     // Catch:{ Exception -> 0x011e }
                goto L_0x0123
            L_0x011e:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0123:
                r0 = 0
            L_0x0124:
                if (r2 == 0) goto L_0x0210
                android.media.MediaCodec r3 = r1.audioEncoder     // Catch:{ all -> 0x020c }
                r4 = 0
                int r10 = r3.dequeueInputBuffer(r4)     // Catch:{ all -> 0x020c }
                if (r10 < 0) goto L_0x0206
                int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x020c }
                r6 = 21
                if (r3 < r6) goto L_0x013d
                android.media.MediaCodec r3 = r1.audioEncoder     // Catch:{ all -> 0x020c }
                java.nio.ByteBuffer r3 = r3.getInputBuffer(r10)     // Catch:{ all -> 0x020c }
                goto L_0x0148
            L_0x013d:
                android.media.MediaCodec r3 = r1.audioEncoder     // Catch:{ all -> 0x020c }
                java.nio.ByteBuffer[] r3 = r3.getInputBuffers()     // Catch:{ all -> 0x020c }
                r3 = r3[r10]     // Catch:{ all -> 0x020c }
                r3.clear()     // Catch:{ all -> 0x020c }
            L_0x0148:
                long[] r6 = r2.offset     // Catch:{ all -> 0x020c }
                int r9 = r2.lastWroteBuffer     // Catch:{ all -> 0x020c }
                r11 = r6[r9]     // Catch:{ all -> 0x020c }
                int r6 = r2.lastWroteBuffer     // Catch:{ all -> 0x020c }
            L_0x0150:
                int r9 = r2.results     // Catch:{ all -> 0x020c }
                r13 = 0
                if (r6 > r9) goto L_0x01e4
                int r9 = r2.results     // Catch:{ all -> 0x020c }
                if (r6 >= r9) goto L_0x01b0
                boolean r9 = r1.running     // Catch:{ all -> 0x020c }
                if (r9 != 0) goto L_0x019b
                long[] r9 = r2.offset     // Catch:{ all -> 0x020c }
                r14 = r9[r6]     // Catch:{ all -> 0x020c }
                long r4 = r1.videoLast     // Catch:{ all -> 0x020c }
                long r7 = r1.desyncTime     // Catch:{ all -> 0x020c }
                long r4 = r4 - r7
                int r7 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
                if (r7 < 0) goto L_0x019b
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x020c }
                if (r0 == 0) goto L_0x0190
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x020c }
                r0.<init>()     // Catch:{ all -> 0x020c }
                java.lang.String r4 = "stop audio encoding because of stoped video recording at "
                r0.append(r4)     // Catch:{ all -> 0x020c }
                long[] r2 = r2.offset     // Catch:{ all -> 0x020c }
                r4 = r2[r6]     // Catch:{ all -> 0x020c }
                r0.append(r4)     // Catch:{ all -> 0x020c }
                java.lang.String r2 = " last video "
                r0.append(r2)     // Catch:{ all -> 0x020c }
                long r4 = r1.videoLast     // Catch:{ all -> 0x020c }
                r0.append(r4)     // Catch:{ all -> 0x020c }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x020c }
                org.telegram.messenger.FileLog.d(r0)     // Catch:{ all -> 0x020c }
            L_0x0190:
                r2 = 1
                r1.audioStopedByTime = r2     // Catch:{ all -> 0x020c }
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite     // Catch:{ all -> 0x020c }
                r0.clear()     // Catch:{ all -> 0x020c }
                r2 = r13
                r0 = 1
                goto L_0x01e4
            L_0x019b:
                int r4 = r3.remaining()     // Catch:{ all -> 0x020c }
                int[] r5 = r2.read     // Catch:{ all -> 0x020c }
                r5 = r5[r6]     // Catch:{ all -> 0x020c }
                if (r4 >= r5) goto L_0x01a9
                r2.lastWroteBuffer = r6     // Catch:{ all -> 0x020c }
                r2 = r13
                goto L_0x01e4
            L_0x01a9:
                java.nio.ByteBuffer[] r4 = r2.buffer     // Catch:{ all -> 0x020c }
                r4 = r4[r6]     // Catch:{ all -> 0x020c }
                r3.put(r4)     // Catch:{ all -> 0x020c }
            L_0x01b0:
                int r4 = r2.results     // Catch:{ all -> 0x020c }
                r5 = 1
                int r4 = r4 - r5
                if (r6 < r4) goto L_0x01db
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r4 = r1.buffersToWrite     // Catch:{ all -> 0x020c }
                r4.remove(r2)     // Catch:{ all -> 0x020c }
                boolean r4 = r1.running     // Catch:{ all -> 0x020c }
                if (r4 == 0) goto L_0x01c4
                java.util.concurrent.ArrayBlockingQueue<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r4 = r1.buffers     // Catch:{ all -> 0x020c }
                r4.put(r2)     // Catch:{ all -> 0x020c }
            L_0x01c4:
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r4 = r1.buffersToWrite     // Catch:{ all -> 0x020c }
                boolean r4 = r4.isEmpty()     // Catch:{ all -> 0x020c }
                if (r4 != 0) goto L_0x01d6
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r2 = r1.buffersToWrite     // Catch:{ all -> 0x020c }
                r4 = 0
                java.lang.Object r2 = r2.get(r4)     // Catch:{ all -> 0x020c }
                org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r2 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r2     // Catch:{ all -> 0x020c }
                goto L_0x01dc
            L_0x01d6:
                r4 = 0
                boolean r0 = r2.last     // Catch:{ all -> 0x020c }
                r2 = r13
                goto L_0x01e6
            L_0x01db:
                r4 = 0
            L_0x01dc:
                int r6 = r6 + 1
                r4 = 0
                r7 = 0
                r8 = 1
                goto L_0x0150
            L_0x01e4:
                r4 = 0
                r5 = 1
            L_0x01e6:
                android.media.MediaCodec r9 = r1.audioEncoder     // Catch:{ all -> 0x020c }
                r6 = 0
                int r3 = r3.position()     // Catch:{ all -> 0x020c }
                r7 = 0
                int r13 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
                if (r13 != 0) goto L_0x01f5
            L_0x01f3:
                r13 = r7
                goto L_0x01fa
            L_0x01f5:
                long r7 = r1.audioStartTime     // Catch:{ all -> 0x020c }
                long r7 = r11 - r7
                goto L_0x01f3
            L_0x01fa:
                if (r0 == 0) goto L_0x01ff
                r7 = 4
                r15 = 4
                goto L_0x0200
            L_0x01ff:
                r15 = 0
            L_0x0200:
                r11 = r6
                r12 = r3
                r9.queueInputBuffer(r10, r11, r12, r13, r15)     // Catch:{ all -> 0x020c }
                goto L_0x0208
            L_0x0206:
                r4 = 0
                r5 = 1
            L_0x0208:
                r7 = 0
                r8 = 1
                goto L_0x0124
            L_0x020c:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0210:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.handleAudioFrameAvailable(org.telegram.ui.Components.InstantCameraView$AudioBufferInfo):void");
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:17:0x0050  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x006a  */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x00f4  */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x0191  */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x01a0  */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x01be  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x0208  */
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
                if (r0 != 0) goto L_0x005f
                long r12 = r1.skippedTime
                long r12 = r12 + r10
                r1.skippedTime = r12
                r14 = 200000000(0xbebCLASSNAME, double:9.8813129E-316)
                int r0 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
                if (r0 >= 0) goto L_0x005d
                return
            L_0x005d:
                r1.skippedFirst = r4
            L_0x005f:
                long r12 = r1.currentTimestamp
                long r12 = r12 + r10
                r1.currentTimestamp = r12
                long r10 = r1.videoFirst
                int r0 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
                if (r0 != 0) goto L_0x008a
                r6 = 1000(0x3e8, double:4.94E-321)
                long r6 = r2 / r6
                r1.videoFirst = r6
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x008a
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r6 = "first video frame was at "
                r0.append(r6)
                long r6 = r1.videoFirst
                r0.append(r6)
                java.lang.String r0 = r0.toString()
                org.telegram.messenger.FileLog.d(r0)
            L_0x008a:
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
                if (r0 == 0) goto L_0x011b
                boolean r0 = r1.blendEnabled
                if (r0 != 0) goto L_0x00fd
                android.opengl.GLES20.glEnable(r2)
                r1.blendEnabled = r4
            L_0x00fd:
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
            L_0x011b:
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
                int r0 = android.os.Build.VERSION.SDK_INT
                r3 = 21
                if (r0 < r3) goto L_0x01a5
                int r0 = r1.frameCount
                int r0 = r0 % 30
                if (r0 != 0) goto L_0x01a5
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                android.view.TextureView r0 = r0.textureView
                if (r0 == 0) goto L_0x01a5
                r3 = 1113587712(0x42600000, float:56.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                android.graphics.Bitmap r0 = r0.getBitmap(r6, r3)
                if (r0 == 0) goto L_0x0189
                int r3 = r0.getPixel(r5, r5)
                if (r3 != 0) goto L_0x01a0
            L_0x0189:
                java.util.ArrayList<android.graphics.Bitmap> r3 = r1.keyframeThumbs
                int r3 = r3.size()
                if (r3 <= r4) goto L_0x01a0
                java.util.ArrayList<android.graphics.Bitmap> r0 = r1.keyframeThumbs
                int r3 = r0.size()
                int r3 = r3 - r4
                java.lang.Object r3 = r0.get(r3)
                r0.add(r3)
                goto L_0x01a5
            L_0x01a0:
                java.util.ArrayList<android.graphics.Bitmap> r3 = r1.keyframeThumbs
                r3.add(r0)
            L_0x01a5:
                int r0 = r1.frameCount
                int r0 = r0 + r4
                r1.frameCount = r0
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                int[] r0 = r0.oldCameraTexture
                r0 = r0[r5]
                if (r0 == 0) goto L_0x0208
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                float r0 = r0.cameraTextureAlpha
                int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
                if (r0 >= 0) goto L_0x0208
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
                if (r0 <= 0) goto L_0x021d
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
                if (r0 != 0) goto L_0x021d
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                boolean unused = r0.cameraReady = r4
                org.telegram.ui.Components.-$$Lambda$InstantCameraView$VideoRecorder$ONc2-IZzZjKNnbhigUCIh3I7vt4 r0 = new org.telegram.ui.Components.-$$Lambda$InstantCameraView$VideoRecorder$ONc2-IZzZjKNnbhigUCIh3I7vt4
                r0.<init>()
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                goto L_0x021d
            L_0x0208:
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                boolean r0 = r0.cameraReady
                if (r0 != 0) goto L_0x021d
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                boolean unused = r0.cameraReady = r4
                org.telegram.ui.Components.-$$Lambda$InstantCameraView$VideoRecorder$cj_4IZt_HllVK7x5iDyMvDcfDnc r0 = new org.telegram.ui.Components.-$$Lambda$InstantCameraView$VideoRecorder$cj_4IZt_HllVK7x5iDyMvDcfDnc
                r0.<init>()
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            L_0x021d:
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
            VideoEditedInfo access$500 = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalWidth = 240;
            access$500.resultWidth = 240;
            VideoEditedInfo access$5002 = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalHeight = 240;
            access$5002.resultHeight = 240;
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
                            VideoPlayer access$400 = InstantCameraView.this.videoPlayer;
                            long j = 0;
                            if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                                j = InstantCameraView.this.videoEditedInfo.startTime;
                            }
                            access$400.seekTo(j);
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
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofInt(InstantCameraView.this.paint, AnimationProperties.PAINT_ALPHA, new int[]{0}), ObjectAnimator.ofFloat(InstantCameraView.this.muteImageView, View.ALPHA, new float[]{1.0f})});
                animatorSet.setDuration(180);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.start();
                InstantCameraView.this.videoEditedInfo.estimatedDuration = InstantCameraView.this.recordedTime;
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.audioDidSent, Integer.valueOf(InstantCameraView.this.recordingGuid), InstantCameraView.this.videoEditedInfo, this.videoFile.getAbsolutePath(), this.keyframeThumbs);
            } else if (InstantCameraView.this.baseFragment.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(InstantCameraView.this.baseFragment.getParentActivity(), InstantCameraView.this.baseFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                    public final void didSelectDate(boolean z, int i) {
                        InstantCameraView.VideoRecorder.this.lambda$null$2$InstantCameraView$VideoRecorder(z, i);
                    }
                });
            } else {
                ChatActivity access$3500 = InstantCameraView.this.baseFragment;
                MediaController.PhotoEntry photoEntry = r4;
                MediaController.PhotoEntry photoEntry2 = new MediaController.PhotoEntry(0, 0, 0, this.videoFile.getAbsolutePath(), 0, true, 0, 0, 0);
                access$3500.sendMedia(photoEntry, InstantCameraView.this.videoEditedInfo, true, 0);
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
                audioRecord.startRecording();
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
                mediaFormat.setInteger("sample-rate", 44100);
                mediaFormat.setInteger("channel-count", 1);
                mediaFormat.setInteger("bitrate", 32000);
                mediaFormat.setInteger("max-input-size", 20480);
                MediaCodec createEncoderByType = MediaCodec.createEncoderByType("audio/mp4a-latm");
                this.audioEncoder = createEncoderByType;
                createEncoderByType.configure(mediaFormat, (Surface) null, (MediaCrypto) null, 1);
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
                    android.opengl.EGLDisplay eglGetDisplay = EGL14.eglGetDisplay(0);
                    this.eglDisplay = eglGetDisplay;
                    if (eglGetDisplay != EGL14.EGL_NO_DISPLAY) {
                        int[] iArr = new int[2];
                        if (EGL14.eglInitialize(eglGetDisplay, iArr, 0, iArr, 1)) {
                            if (this.eglContext == EGL14.EGL_NO_CONTEXT) {
                                android.opengl.EGLConfig[] eGLConfigArr = new android.opengl.EGLConfig[1];
                                if (EGL14.eglChooseConfig(this.eglDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12610, 1, 12344}, 0, eGLConfigArr, 0, 1, new int[1], 0)) {
                                    this.eglContext = EGL14.eglCreateContext(this.eglDisplay, eGLConfigArr[0], this.sharedEglContext, new int[]{12440, 2, 12344}, 0);
                                    this.eglConfig = eGLConfigArr[0];
                                } else {
                                    throw new RuntimeException("Unable to find a suitable EGLConfig");
                                }
                            }
                            EGL14.eglQueryContext(this.eglDisplay, this.eglContext, 12440, new int[1], 0);
                            if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
                                android.opengl.EGLSurface eglCreateWindowSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surface, new int[]{12344}, 0);
                                this.eglSurface = eglCreateWindowSurface;
                                if (eglCreateWindowSurface == null) {
                                    throw new RuntimeException("surface was null");
                                } else if (!EGL14.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
                                    }
                                    throw new RuntimeException("eglMakeCurrent failed");
                                } else {
                                    GLES20.glBlendFunc(770, 771);
                                    int access$1300 = InstantCameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                                    int access$13002 = InstantCameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   float coef = ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0));\n   vec3 color = texture2D(sTexture, vTextureCoord).rgb * coef + (1.0 - step(0.001, coef));\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n");
                                    if (access$1300 != 0 && access$13002 != 0) {
                                        int glCreateProgram = GLES20.glCreateProgram();
                                        this.drawProgram = glCreateProgram;
                                        GLES20.glAttachShader(glCreateProgram, access$1300);
                                        GLES20.glAttachShader(this.drawProgram, access$13002);
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
                InstantCameraView.this.invalidate();
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordStarted, Integer.valueOf(InstantCameraView.this.recordingGuid), false);
            }
        }

        private void didWriteData(File file, long j, boolean z) {
            long j2 = 0;
            if (this.videoConvertFirstWrite) {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).uploadFile(file.toString(), InstantCameraView.this.isSecretChat, false, 1, 33554432);
                this.videoConvertFirstWrite = false;
                if (z) {
                    FileLoader instance = FileLoader.getInstance(InstantCameraView.this.currentAccount);
                    String file2 = file.toString();
                    boolean access$3400 = InstantCameraView.this.isSecretChat;
                    if (z) {
                        j2 = file.length();
                    }
                    instance.checkUploadNewDataAvailable(file2, access$3400, j, j2);
                    return;
                }
                return;
            }
            FileLoader instance2 = FileLoader.getInstance(InstantCameraView.this.currentAccount);
            String file3 = file.toString();
            boolean access$34002 = InstantCameraView.this.isSecretChat;
            if (z) {
                j2 = file.length();
            }
            instance2.checkUploadNewDataAvailable(file3, access$34002, j, j2);
        }

        public void drainEncoder(boolean z) throws Exception {
            ByteBuffer byteBuffer;
            ByteBuffer byteBuffer2;
            ByteBuffer byteBuffer3;
            ByteBuffer byteBuffer4;
            if (z) {
                this.videoEncoder.signalEndOfInputStream();
            }
            int i = 21;
            ByteBuffer[] outputBuffers = Build.VERSION.SDK_INT < 21 ? this.videoEncoder.getOutputBuffers() : null;
            while (true) {
                int dequeueOutputBuffer = this.videoEncoder.dequeueOutputBuffer(this.videoBufferInfo, 10000);
                byte b = 1;
                if (dequeueOutputBuffer == -1) {
                    if (!z) {
                        break;
                    }
                } else if (dequeueOutputBuffer == -3) {
                    if (Build.VERSION.SDK_INT < i) {
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
                } else if (dequeueOutputBuffer >= 0) {
                    if (Build.VERSION.SDK_INT < i) {
                        byteBuffer2 = outputBuffers[dequeueOutputBuffer];
                    } else {
                        byteBuffer2 = this.videoEncoder.getOutputBuffer(dequeueOutputBuffer);
                    }
                    if (byteBuffer2 != null) {
                        MediaCodec.BufferInfo bufferInfo = this.videoBufferInfo;
                        int i2 = bufferInfo.size;
                        if (i2 > 1) {
                            int i3 = bufferInfo.flags;
                            if ((i3 & 2) == 0) {
                                int i4 = this.prependHeaderSize;
                                if (!(i4 == 0 || (i3 & 1) == 0)) {
                                    bufferInfo.offset += i4;
                                    bufferInfo.size = i2 - i4;
                                }
                                if (this.firstEncode) {
                                    MediaCodec.BufferInfo bufferInfo2 = this.videoBufferInfo;
                                    if ((bufferInfo2.flags & 1) != 0) {
                                        if (bufferInfo2.size > 100) {
                                            byteBuffer2.position(bufferInfo2.offset);
                                            byte[] bArr = new byte[100];
                                            byteBuffer2.get(bArr);
                                            int i5 = 0;
                                            int i6 = 0;
                                            while (true) {
                                                if (i5 < 96) {
                                                    if (bArr[i5] == 0 && bArr[i5 + 1] == 0 && bArr[i5 + 2] == 0 && bArr[i5 + 3] == 1 && (i6 = i6 + 1) > 1) {
                                                        MediaCodec.BufferInfo bufferInfo3 = this.videoBufferInfo;
                                                        bufferInfo3.offset += i5;
                                                        bufferInfo3.size -= i5;
                                                        break;
                                                    }
                                                    i5++;
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
                                byte[] bArr2 = new byte[i2];
                                byteBuffer2.limit(bufferInfo.offset + i2);
                                byteBuffer2.position(this.videoBufferInfo.offset);
                                byteBuffer2.get(bArr2);
                                int i7 = this.videoBufferInfo.size - 1;
                                while (true) {
                                    if (i7 < 0 || i7 <= 3) {
                                        byteBuffer4 = null;
                                        byteBuffer3 = null;
                                    } else {
                                        if (bArr2[i7] == b && bArr2[i7 - 1] == 0 && bArr2[i7 - 2] == 0) {
                                            int i8 = i7 - 3;
                                            if (bArr2[i8] == 0) {
                                                byteBuffer4 = ByteBuffer.allocate(i8);
                                                byteBuffer3 = ByteBuffer.allocate(this.videoBufferInfo.size - i8);
                                                byteBuffer4.put(bArr2, 0, i8).position(0);
                                                byteBuffer3.put(bArr2, i8, this.videoBufferInfo.size - i8).position(0);
                                                break;
                                            }
                                        }
                                        i7--;
                                        b = 1;
                                    }
                                }
                                byteBuffer4 = null;
                                byteBuffer3 = null;
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
                i = 21;
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
                } else if (dequeueOutputBuffer2 >= 0) {
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
