package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.opengl.EGLExt;
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
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
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
import javax.microedition.khronos.opengles.GL;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
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
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public class InstantCameraView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final String FRAGMENT_SCREEN_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   float coef = ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0));\n   vec3 color = texture2D(sTexture, vTextureCoord).rgb * coef + (1.0 - step(0.001, coef));\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n";
    private static final int MSG_AUDIOFRAME_AVAILABLE = 3;
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_VIDEOFRAME_AVAILABLE = 2;
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    private float animationTranslationY;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private Size aspectRatio;
    /* access modifiers changed from: private */
    public ChatActivity baseFragment;
    private BlurBehindDrawable blurBehindDrawable;
    private InstantViewCameraContainer cameraContainer;
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
    public TLRPC.InputEncryptedFile encryptedFile;
    /* access modifiers changed from: private */
    public TLRPC.InputFile file;
    ValueAnimator finishZoomTransition;
    /* access modifiers changed from: private */
    public boolean isFrontface = true;
    boolean isInPinchToZoomTouchMode;
    private boolean isMessageTransition;
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
    boolean maybePinchToZoomTouchMode;
    /* access modifiers changed from: private */
    public float[] moldSTMatrix;
    /* access modifiers changed from: private */
    public AnimatorSet muteAnimation;
    /* access modifiers changed from: private */
    public ImageView muteImageView;
    /* access modifiers changed from: private */
    public boolean needDrawFlickerStub;
    /* access modifiers changed from: private */
    public int[] oldCameraTexture = new int[1];
    public boolean opened;
    /* access modifiers changed from: private */
    public Paint paint;
    private float panTranslationY;
    private View parentView;
    private Size pictureSize;
    float pinchScale;
    float pinchStartDistance;
    private int pointerId1;
    private int pointerId2;
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
    private boolean requestingPermissions;
    /* access modifiers changed from: private */
    public final Theme.ResourcesProvider resourcesProvider;
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
    public int textureViewSize;
    private boolean updateTextureViewSize;
    /* access modifiers changed from: private */
    public FloatBuffer vertexBuffer;
    /* access modifiers changed from: private */
    public VideoEditedInfo videoEditedInfo;
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;

    static /* synthetic */ float access$2216(InstantCameraView x0, float x1) {
        float f = x0.cameraTextureAlpha + x1;
        x0.cameraTextureAlpha = f;
        return f;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public InstantCameraView(Context context, ChatActivity parentFragment, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        this.aspectRatio = SharedConfig.roundCamera16to9 ? new Size(16, 9) : new Size(4, 3);
        this.mMVPMatrix = new float[16];
        this.mSTMatrix = new float[16];
        this.moldSTMatrix = new float[16];
        this.resourcesProvider = resourcesProvider2;
        this.parentView = parentFragment.getFragmentView();
        setWillNotDraw(false);
        this.baseFragment = parentFragment;
        this.recordingGuid = parentFragment.getClassGuid();
        this.isSecretChat = this.baseFragment.getCurrentEncryptedChat() != null;
        AnonymousClass1 r8 = new Paint(1) {
            public void setAlpha(int a) {
                super.setAlpha(a);
                InstantCameraView.this.invalidate();
            }
        };
        this.paint = r8;
        r8.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setColor(-1);
        this.rect = new RectF();
        if (Build.VERSION.SDK_INT >= 21) {
            AnonymousClass2 r3 = new InstantViewCameraContainer(context2) {
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    InstantCameraView.this.invalidate();
                }

                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    InstantCameraView.this.invalidate();
                }
            };
            this.cameraContainer = r3;
            r3.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, InstantCameraView.this.textureViewSize, InstantCameraView.this.textureViewSize);
                }
            });
            this.cameraContainer.setClipToOutline(true);
            this.cameraContainer.setWillNotDraw(false);
        } else {
            final Path path = new Path();
            final Paint paint2 = new Paint(1);
            paint2.setColor(-16777216);
            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            AnonymousClass4 r12 = new InstantViewCameraContainer(context2) {
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    InstantCameraView.this.invalidate();
                }

                /* access modifiers changed from: protected */
                public void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    path.reset();
                    path.addCircle((float) (w / 2), (float) (h / 2), (float) (w / 2), Path.Direction.CW);
                    path.toggleInverseFillType();
                }

                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    try {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(path, paint2);
                    } catch (Exception e) {
                    }
                }
            };
            this.cameraContainer = r12;
            r12.setWillNotDraw(false);
            this.cameraContainer.setLayerType(2, (Paint) null);
        }
        addView(this.cameraContainer, new FrameLayout.LayoutParams(AndroidUtilities.roundPlayingMessageSize, AndroidUtilities.roundPlayingMessageSize, 17));
        ImageView imageView = new ImageView(context2);
        this.switchCameraButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.switchCameraButton.setContentDescription(LocaleController.getString("AccDescrSwitchCamera", NUM));
        addView(this.switchCameraButton, LayoutHelper.createFrame(62, 62.0f, 83, 8.0f, 0.0f, 0.0f, 0.0f));
        this.switchCameraButton.setOnClickListener(new InstantCameraView$$ExternalSyntheticLambda2(this));
        ImageView imageView2 = new ImageView(context2);
        this.muteImageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.muteImageView.setImageResource(NUM);
        this.muteImageView.setAlpha(0.0f);
        addView(this.muteImageView, LayoutHelper.createFrame(48, 48, 17));
        final Paint blackoutPaint = new Paint(1);
        blackoutPaint.setColor(ColorUtils.setAlphaComponent(-16777216, 40));
        AnonymousClass6 r32 = new BackupImageView(getContext()) {
            CellFlickerDrawable flickerDrawable = new CellFlickerDrawable();

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (InstantCameraView.this.needDrawFlickerStub) {
                    this.flickerDrawable.setParentWidth(InstantCameraView.this.textureViewSize);
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) InstantCameraView.this.textureViewSize, (float) InstantCameraView.this.textureViewSize);
                    float rad = AndroidUtilities.rectTmp.width() / 2.0f;
                    canvas.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, blackoutPaint);
                    AndroidUtilities.rectTmp.inset((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f));
                    this.flickerDrawable.draw(canvas, AndroidUtilities.rectTmp, rad);
                    invalidate();
                }
            }
        };
        this.textureOverlayView = r32;
        addView(r32, new FrameLayout.LayoutParams(AndroidUtilities.roundPlayingMessageSize, AndroidUtilities.roundPlayingMessageSize, 17));
        setVisibility(4);
        this.blurBehindDrawable = new BlurBehindDrawable(this.parentView, this, 0, (Theme.ResourcesProvider) null);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-InstantCameraView  reason: not valid java name */
    public /* synthetic */ void m2346lambda$new$0$orgtelegramuiComponentsInstantCameraView(View v) {
        CameraSession cameraSession2;
        if (this.cameraReady && (cameraSession2 = this.cameraSession) != null && cameraSession2.isInitied() && this.cameraThread != null) {
            switchCamera();
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.switchCameraButton, View.SCALE_X, new float[]{0.0f}).setDuration(100);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    InstantCameraView.this.switchCameraButton.setImageResource(InstantCameraView.this.isFrontface ? NUM : NUM);
                    ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, View.SCALE_X, new float[]{1.0f}).setDuration(100).start();
                }
            });
            animator.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newSize;
        if (this.updateTextureViewSize) {
            if (((float) View.MeasureSpec.getSize(heightMeasureSpec)) > ((float) View.MeasureSpec.getSize(widthMeasureSpec)) * 1.3f) {
                newSize = AndroidUtilities.roundPlayingMessageSize;
            } else {
                newSize = AndroidUtilities.roundMessageSize;
            }
            if (newSize != this.textureViewSize) {
                this.textureViewSize = newSize;
                ViewGroup.LayoutParams layoutParams = this.textureOverlayView.getLayoutParams();
                ViewGroup.LayoutParams layoutParams2 = this.textureOverlayView.getLayoutParams();
                int i = this.textureViewSize;
                layoutParams2.height = i;
                layoutParams.width = i;
                ViewGroup.LayoutParams layoutParams3 = this.cameraContainer.getLayoutParams();
                ViewGroup.LayoutParams layoutParams4 = this.cameraContainer.getLayoutParams();
                int i2 = this.textureViewSize;
                layoutParams4.height = i2;
                layoutParams3.width = i2;
                ((FrameLayout.LayoutParams) this.muteImageView.getLayoutParams()).topMargin = (this.textureViewSize / 2) - AndroidUtilities.dp(24.0f);
                this.textureOverlayView.setRoundRadius(this.textureViewSize / 2);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.cameraContainer.invalidateOutline();
                }
            }
            this.updateTextureViewSize = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean checkPointerIds(MotionEvent ev) {
        if (ev.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == ev.getPointerId(0) && this.pointerId2 == ev.getPointerId(1)) {
            return true;
        }
        if (this.pointerId1 == ev.getPointerId(1) && this.pointerId2 == ev.getPointerId(0)) {
            return true;
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getVisibility() != 0) {
            this.animationTranslationY = (float) (getMeasuredHeight() / 2);
            updateTranslationY();
        }
        this.blurBehindDrawable.checkSizes();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileUploaded) {
            String location = args[0];
            File file2 = this.cameraFile;
            if (file2 != null && file2.getAbsolutePath().equals(location)) {
                this.file = args[1];
                this.encryptedFile = args[2];
                this.size = args[5].longValue();
                if (this.encryptedFile != null) {
                    this.key = args[3];
                    this.iv = args[4];
                }
            }
        }
    }

    public void destroy(boolean async, Runnable beforeDestroyRunnable) {
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.destroy();
            CameraController.getInstance().close(this.cameraSession, !async ? new CountDownLatch(1) : null, beforeDestroyRunnable);
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
            int x1 = ((int) x) - AndroidUtilities.dp(3.0f);
            int y1 = ((int) y) - AndroidUtilities.dp(2.0f);
            canvas.save();
            if (this.isMessageTransition) {
                canvas.scale(this.cameraContainer.getScaleX(), this.cameraContainer.getScaleY(), x, y);
            } else {
                float scaleX2 = this.cameraContainer.getScaleX();
                float scaleY2 = this.cameraContainer.getScaleY();
                int i = this.textureViewSize;
                canvas.scale(scaleX2, scaleY2, (((float) i) / 2.0f) + x, (((float) i) / 2.0f) + y);
            }
            Theme.chat_roundVideoShadow.setAlpha((int) (this.cameraContainer.getAlpha() * 255.0f));
            Theme.chat_roundVideoShadow.setBounds(x1, y1, this.textureViewSize + x1 + AndroidUtilities.dp(6.0f), this.textureViewSize + y1 + AndroidUtilities.dp(6.0f));
            Theme.chat_roundVideoShadow.draw(canvas);
            canvas.restore();
        }
    }

    public void setVisibility(int visibility) {
        BlurBehindDrawable blurBehindDrawable2;
        super.setVisibility(visibility);
        if (!(visibility == 0 || (blurBehindDrawable2 = this.blurBehindDrawable) == null)) {
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
            InstantViewCameraContainer instantViewCameraContainer = this.cameraContainer;
            instantViewCameraContainer.setPivotX((float) (instantViewCameraContainer.getMeasuredWidth() / 2));
            InstantViewCameraContainer instantViewCameraContainer2 = this.cameraContainer;
            instantViewCameraContainer2.setPivotY((float) (instantViewCameraContainer2.getMeasuredHeight() / 2));
            BackupImageView backupImageView = this.textureOverlayView;
            backupImageView.setPivotX((float) (backupImageView.getMeasuredWidth() / 2));
            BackupImageView backupImageView2 = this.textureOverlayView;
            backupImageView2.setPivotY((float) (backupImageView2.getMeasuredHeight() / 2));
        }
        if (visibility == 0) {
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
            this.textureOverlayView.invalidate();
            if (this.lastBitmap == null) {
                try {
                    this.lastBitmap = BitmapFactory.decodeFile(new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg").getAbsolutePath());
                } catch (Throwable th) {
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
            this.needDrawFlickerStub = true;
            if (initCamera()) {
                MediaController.getInstance().m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
                File directory = FileLoader.getDirectory(4);
                this.cameraFile = new File(directory, SharedConfig.getLastLocalId() + ".mp4");
                SharedConfig.saveConfig();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("show round camera");
                }
                TextureView textureView2 = new TextureView(getContext());
                this.textureView = textureView2;
                textureView2.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("camera surface available");
                        }
                        if (InstantCameraView.this.cameraThread == null && surface != null && !InstantCameraView.this.cancelled) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("start create thread");
                            }
                            CameraGLThread unused = InstantCameraView.this.cameraThread = new CameraGLThread(surface, width, height);
                        }
                    }

                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    }

                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
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

                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                    }
                });
                this.cameraContainer.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
                this.updateTextureViewSize = true;
                setVisibility(0);
                startAnimation(true);
                MediaController.getInstance().requestAudioFocus(true);
            }
        }
    }

    public InstantViewCameraContainer getCameraContainer() {
        return this.cameraContainer;
    }

    public void startAnimation(boolean open) {
        boolean z = open;
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.removeAllListeners();
            this.animatorSet.cancel();
        }
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        if (pipRoundVideoView != null) {
            pipRoundVideoView.showTemporary(!z);
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
        float toX = 0.0f;
        if (!z) {
            toX = this.recordedTime > 300 ? ((float) AndroidUtilities.dp(24.0f)) - (((float) getMeasuredWidth()) / 2.0f) : 0.0f;
        }
        float[] fArr = new float[2];
        float f = 1.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        fArr[1] = z ? 0.0f : 1.0f;
        ValueAnimator translationYAnimator = ValueAnimator.ofFloat(fArr);
        translationYAnimator.addUpdateListener(new InstantCameraView$$ExternalSyntheticLambda1(this));
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
        InstantViewCameraContainer instantViewCameraContainer = this.cameraContainer;
        Property property3 = View.ALPHA;
        float[] fArr3 = new float[1];
        fArr3[0] = z ? 1.0f : 0.0f;
        animatorArr[3] = ObjectAnimator.ofFloat(instantViewCameraContainer, property3, fArr3);
        InstantViewCameraContainer instantViewCameraContainer2 = this.cameraContainer;
        Property property4 = View.SCALE_X;
        float[] fArr4 = new float[1];
        fArr4[0] = z ? 1.0f : 0.1f;
        animatorArr[4] = ObjectAnimator.ofFloat(instantViewCameraContainer2, property4, fArr4);
        InstantViewCameraContainer instantViewCameraContainer3 = this.cameraContainer;
        Property property5 = View.SCALE_Y;
        float[] fArr5 = new float[1];
        fArr5[0] = z ? 1.0f : 0.1f;
        animatorArr[5] = ObjectAnimator.ofFloat(instantViewCameraContainer3, property5, fArr5);
        animatorArr[6] = ObjectAnimator.ofFloat(this.cameraContainer, View.TRANSLATION_X, new float[]{toX});
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
        animatorArr[10] = ObjectAnimator.ofFloat(this.textureOverlayView, View.TRANSLATION_X, new float[]{toX});
        animatorArr[11] = translationYAnimator;
        animatorSet3.playTogether(animatorArr);
        if (!z) {
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(InstantCameraView.this.animatorSet)) {
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

    /* renamed from: lambda$startAnimation$1$org-telegram-ui-Components-InstantCameraView  reason: not valid java name */
    public /* synthetic */ void m2347xca1896e2(ValueAnimator animation) {
        this.animationTranslationY = (((float) getMeasuredHeight()) / 2.0f) * ((Float) animation.getAnimatedValue()).floatValue();
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

    public void changeVideoPreviewState(int state, float progress2) {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            if (state == 0) {
                startProgressTimer();
                this.videoPlayer.play();
            } else if (state == 1) {
                stopProgressTimer();
                this.videoPlayer.pause();
            } else if (state == 2) {
                videoPlayer2.seekTo((long) (((float) videoPlayer2.getDuration()) * progress2));
            }
        }
    }

    public void send(int state, boolean notify, int scheduleDate) {
        int reason;
        int send;
        int i = state;
        if (this.textureView != null) {
            stopProgressTimer();
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 != null) {
                videoPlayer2.releasePlayer(true);
                this.videoPlayer = null;
            }
            if (i == 4) {
                if (this.videoEditedInfo.needConvert()) {
                    this.file = null;
                    this.encryptedFile = null;
                    this.key = null;
                    this.iv = null;
                    double totalDuration = (double) this.videoEditedInfo.estimatedDuration;
                    this.videoEditedInfo.estimatedDuration = (this.videoEditedInfo.endTime >= 0 ? this.videoEditedInfo.endTime : this.videoEditedInfo.estimatedDuration) - (this.videoEditedInfo.startTime >= 0 ? this.videoEditedInfo.startTime : 0);
                    VideoEditedInfo videoEditedInfo2 = this.videoEditedInfo;
                    double d = (double) this.size;
                    double d2 = (double) videoEditedInfo2.estimatedDuration;
                    Double.isNaN(d2);
                    Double.isNaN(totalDuration);
                    Double.isNaN(d);
                    videoEditedInfo2.estimatedSize = Math.max(1, (long) (d * (d2 / totalDuration)));
                    this.videoEditedInfo.bitrate = 1000000;
                    if (this.videoEditedInfo.startTime > 0) {
                        this.videoEditedInfo.startTime *= 1000;
                    }
                    if (this.videoEditedInfo.endTime > 0) {
                        this.videoEditedInfo.endTime *= 1000;
                    }
                    FileLoader.getInstance(this.currentAccount).cancelFileUpload(this.cameraFile.getAbsolutePath(), false);
                } else {
                    this.videoEditedInfo.estimatedSize = Math.max(1, this.size);
                }
                this.videoEditedInfo.file = this.file;
                this.videoEditedInfo.encryptedFile = this.encryptedFile;
                this.videoEditedInfo.key = this.key;
                this.videoEditedInfo.iv = this.iv;
                this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0, this.cameraFile.getAbsolutePath(), 0, true, 0, 0, 0), this.videoEditedInfo, notify, scheduleDate, false);
                if (scheduleDate != 0) {
                    startAnimation(false);
                }
                MediaController.getInstance().requestAudioFocus(false);
                return;
            }
            boolean z = this.recordedTime < 800;
            this.cancelled = z;
            this.recording = false;
            if (z) {
                reason = 4;
            } else {
                reason = i == 3 ? 2 : 5;
            }
            if (this.cameraThread != null) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recordStopped, Integer.valueOf(this.recordingGuid), Integer.valueOf(reason));
                if (this.cancelled) {
                    send = 0;
                } else if (i == 3) {
                    send = 2;
                } else {
                    send = 1;
                }
                saveLastCameraBitmap();
                this.cameraThread.shutdown(send);
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
            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(this.textureView.getBitmap(), 50, 50, true);
            this.lastBitmap = createScaledBitmap;
            if (createScaledBitmap != null) {
                Utilities.blurBitmap(createScaledBitmap, 7, 1, createScaledBitmap.getWidth(), this.lastBitmap.getHeight(), this.lastBitmap.getRowBytes());
                try {
                    FileOutputStream stream = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg"));
                    this.lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    stream.close();
                } catch (Throwable th) {
                }
            }
        }
    }

    public void cancel(boolean byGesture) {
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
            objArr[1] = Integer.valueOf(byGesture ? 0 : 6);
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

    public void hideCamera(boolean async) {
        ViewGroup parent;
        destroy(async, (Runnable) null);
        this.cameraContainer.setTranslationX(0.0f);
        this.textureOverlayView.setTranslationX(0.0f);
        this.animationTranslationY = 0.0f;
        updateTranslationY();
        MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        TextureView textureView2 = this.textureView;
        if (!(textureView2 == null || (parent = (ViewGroup) textureView2.getParent()) == null)) {
            parent.removeView(this.textureView);
        }
        this.textureView = null;
        this.cameraContainer.setImageReceiver((ImageReceiver) null);
    }

    private void switchCamera() {
        saveLastCameraBitmap();
        Bitmap bitmap = this.lastBitmap;
        if (bitmap != null) {
            this.needDrawFlickerStub = false;
            this.textureOverlayView.setImageBitmap(bitmap);
            this.textureOverlayView.setAlpha(1.0f);
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
        CameraInfo cameraInfo;
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        if (cameraInfos == null) {
            return false;
        }
        CameraInfo notFrontface = null;
        int a = 0;
        while (true) {
            if (a >= cameraInfos.size()) {
                break;
            }
            cameraInfo = cameraInfos.get(a);
            if (!cameraInfo.isFrontface()) {
                notFrontface = cameraInfo;
            }
            if ((!this.isFrontface || !cameraInfo.isFrontface()) && (this.isFrontface || cameraInfo.isFrontface())) {
                notFrontface = cameraInfo;
                a++;
            }
        }
        this.selectedCamera = cameraInfo;
        if (this.selectedCamera == null) {
            this.selectedCamera = notFrontface;
        }
        CameraInfo cameraInfo2 = this.selectedCamera;
        if (cameraInfo2 == null) {
            return false;
        }
        ArrayList<Size> previewSizes = cameraInfo2.getPreviewSizes();
        ArrayList<Size> pictureSizes = this.selectedCamera.getPictureSizes();
        this.previewSize = CameraController.chooseOptimalSize(previewSizes, 480, 270, this.aspectRatio);
        this.pictureSize = CameraController.chooseOptimalSize(pictureSizes, 480, 270, this.aspectRatio);
        if (this.previewSize.mWidth != this.pictureSize.mWidth) {
            boolean found = false;
            for (int a2 = previewSizes.size() - 1; a2 >= 0; a2--) {
                Size preview = previewSizes.get(a2);
                int b = pictureSizes.size() - 1;
                while (true) {
                    if (b < 0) {
                        break;
                    }
                    Size picture = pictureSizes.get(b);
                    if (preview.mWidth >= this.pictureSize.mWidth && preview.mHeight >= this.pictureSize.mHeight && preview.mWidth == picture.mWidth && preview.mHeight == picture.mHeight) {
                        this.previewSize = preview;
                        this.pictureSize = picture;
                        found = true;
                        break;
                    }
                    b--;
                }
                if (found) {
                    break;
                }
            }
            if (!found) {
                for (int a3 = previewSizes.size() - 1; a3 >= 0; a3--) {
                    Size preview2 = previewSizes.get(a3);
                    int b2 = pictureSizes.size() - 1;
                    while (true) {
                        if (b2 < 0) {
                            break;
                        }
                        Size picture2 = pictureSizes.get(b2);
                        if (preview2.mWidth >= 360 && preview2.mHeight >= 360 && preview2.mWidth == picture2.mWidth && preview2.mHeight == picture2.mHeight) {
                            this.previewSize = preview2;
                            this.pictureSize = picture2;
                            found = true;
                            break;
                        }
                        b2--;
                    }
                    if (found) {
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
        AndroidUtilities.runOnUIThread(new InstantCameraView$$ExternalSyntheticLambda5(this, surfaceTexture));
    }

    /* renamed from: lambda$createCamera$4$org-telegram-ui-Components-InstantCameraView  reason: not valid java name */
    public /* synthetic */ void m2344x5b4c1ca0(SurfaceTexture surfaceTexture) {
        if (this.cameraThread != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("create camera session");
            }
            surfaceTexture.setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
            CameraSession cameraSession2 = new CameraSession(this.selectedCamera, this.previewSize, this.pictureSize, 256, true);
            this.cameraSession = cameraSession2;
            this.cameraThread.setCurrentSession(cameraSession2);
            CameraController.getInstance().openRound(this.cameraSession, surfaceTexture, new InstantCameraView$$ExternalSyntheticLambda3(this), new InstantCameraView$$ExternalSyntheticLambda4(this));
        }
    }

    /* renamed from: lambda$createCamera$2$org-telegram-ui-Components-InstantCameraView  reason: not valid java name */
    public /* synthetic */ void m2342xe85d0162() {
        if (this.cameraSession != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("camera initied");
            }
            this.cameraSession.setInitied();
        }
    }

    /* renamed from: lambda$createCamera$3$org-telegram-ui-Components-InstantCameraView  reason: not valid java name */
    public /* synthetic */ void m2343xa1d48var_() {
        this.cameraThread.setCurrentSession(this.cameraSession);
    }

    /* access modifiers changed from: private */
    public int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compileStatus, 0);
        if (compileStatus[0] != 0) {
            return shader;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e(GLES20.glGetShaderInfoLog(shader));
        }
        GLES20.glDeleteShader(shader);
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
                AndroidUtilities.runOnUIThread(new InstantCameraView$9$$ExternalSyntheticLambda0(this));
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-InstantCameraView$9  reason: not valid java name */
            public /* synthetic */ void m2348lambda$run$0$orgtelegramuiComponentsInstantCameraView$9() {
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

    public void onPanTranslationUpdate(float y) {
        this.panTranslationY = y / 2.0f;
        updateTranslationY();
        this.blurBehindDrawable.onPanTranslationUpdate(y);
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public void setIsMessageTransition(boolean isMessageTransition2) {
        this.isMessageTransition = isMessageTransition2;
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

        public CameraGLThread(SurfaceTexture surface, int surfaceWidth, int surfaceHeight) {
            super("CameraGLThread");
            this.surfaceTexture = surface;
            int width = InstantCameraView.this.previewSize.getWidth();
            int height = InstantCameraView.this.previewSize.getHeight();
            float scale = ((float) surfaceWidth) / ((float) Math.min(width, height));
            int width2 = (int) (((float) width) * scale);
            int height2 = (int) (((float) height) * scale);
            if (width2 > height2) {
                float unused = InstantCameraView.this.scaleX = 1.0f;
                float unused2 = InstantCameraView.this.scaleY = ((float) width2) / ((float) surfaceHeight);
                return;
            }
            float unused3 = InstantCameraView.this.scaleX = ((float) height2) / ((float) surfaceWidth);
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
            int[] version = new int[2];
            if (!this.egl10.eglInitialize(this.eglDisplay, version)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12344}, configs, 1, configsCount)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (configsCount[0] > 0) {
                EGLConfig eglConfig = configs[0];
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
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
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, eglConfig, surfaceTexture2, (int[]) null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == null) {
                    } else if (eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                        int[] iArr = version;
                    } else {
                        EGL10 egl103 = this.egl10;
                        EGLDisplay eGLDisplay = this.eglDisplay;
                        EGLSurface eGLSurface = this.eglSurface;
                        if (!egl103.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            }
                            finish();
                            return false;
                        }
                        GL gl = this.eglContext.getGL();
                        float tX = (1.0f / InstantCameraView.this.scaleX) / 2.0f;
                        float tY = (1.0f / InstantCameraView.this.scaleY) / 2.0f;
                        float[] verticesData = {-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
                        float[] texData = {0.5f - tX, 0.5f - tY, tX + 0.5f, 0.5f - tY, 0.5f - tX, tY + 0.5f, tX + 0.5f, tY + 0.5f};
                        this.videoEncoder = new VideoRecorder();
                        FloatBuffer unused = InstantCameraView.this.vertexBuffer = ByteBuffer.allocateDirect(verticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        InstantCameraView.this.vertexBuffer.put(verticesData).position(0);
                        FloatBuffer unused2 = InstantCameraView.this.textureBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        InstantCameraView.this.textureBuffer.put(texData).position(0);
                        Matrix.setIdentityM(InstantCameraView.this.mSTMatrix, 0);
                        int vertexShader = InstantCameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                        int fragmentShader = InstantCameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
                        if (vertexShader == 0 || fragmentShader == 0) {
                            int[] iArr2 = version;
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("failed creating shader");
                            }
                            finish();
                            return false;
                        }
                        int glCreateProgram = GLES20.glCreateProgram();
                        this.drawProgram = glCreateProgram;
                        GLES20.glAttachShader(glCreateProgram, vertexShader);
                        GLES20.glAttachShader(this.drawProgram, fragmentShader);
                        GLES20.glLinkProgram(this.drawProgram);
                        int[] linkStatus = new int[1];
                        float[] fArr = texData;
                        int[] iArr3 = version;
                        GLES20.glGetProgramiv(this.drawProgram, 35714, linkStatus, 0);
                        if (linkStatus[0] == 0) {
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
                        surfaceTexture3.setOnFrameAvailableListener(new InstantCameraView$CameraGLThread$$ExternalSyntheticLambda1(this));
                        InstantCameraView.this.createCamera(this.cameraSurface);
                        if (!BuildVars.LOGS_ENABLED) {
                            return true;
                        }
                        FileLog.e("gl initied");
                        return true;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                finish();
                return false;
            } else {
                int[] iArr4 = version;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        /* renamed from: lambda$initGL$0$org-telegram-ui-Components-InstantCameraView$CameraGLThread  reason: not valid java name */
        public /* synthetic */ void m2350xbcdb2188(SurfaceTexture surfaceTexture2) {
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
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay != null) {
                this.egl10.eglTerminate(eGLDisplay);
                this.eglDisplay = null;
            }
        }

        public void setCurrentSession(CameraSession session) {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(3, session), 0);
            }
        }

        private void onDraw(Integer cameraId2) {
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
                    int orientation = this.currentSession.getCurrentOrientation();
                    if (orientation == 90 || orientation == 270) {
                        float temp = InstantCameraView.this.scaleX;
                        InstantCameraView instantCameraView = InstantCameraView.this;
                        float unused = instantCameraView.scaleX = instantCameraView.scaleY;
                        float unused2 = InstantCameraView.this.scaleY = temp;
                    }
                }
                this.videoEncoder.frameAvailable(this.cameraSurface, cameraId2, System.nanoTime());
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

        public void handleMessage(Message inputMessage) {
            switch (inputMessage.what) {
                case 0:
                    onDraw((Integer) inputMessage.obj);
                    return;
                case 1:
                    finish();
                    if (this.recording) {
                        this.videoEncoder.stopRecording(inputMessage.arg1);
                    }
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        looper.quit();
                        return;
                    }
                    return;
                case 2:
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
                        surfaceTexture3.setOnFrameAvailableListener(new InstantCameraView$CameraGLThread$$ExternalSyntheticLambda0(this));
                        InstantCameraView.this.createCamera(this.cameraSurface);
                        return;
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("set gl rednderer session");
                    }
                    CameraSession newSession = (CameraSession) inputMessage.obj;
                    CameraSession cameraSession = this.currentSession;
                    if (cameraSession == newSession) {
                        int rotationAngle = cameraSession.getWorldAngle();
                        Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                        if (rotationAngle != 0) {
                            Matrix.rotateM(InstantCameraView.this.mMVPMatrix, 0, (float) rotationAngle, 0.0f, 0.0f, 1.0f);
                            return;
                        }
                        return;
                    }
                    this.currentSession = newSession;
                    return;
                default:
                    return;
            }
        }

        /* renamed from: lambda$handleMessage$1$org-telegram-ui-Components-InstantCameraView$CameraGLThread  reason: not valid java name */
        public /* synthetic */ void m2349x43215339(SurfaceTexture surfaceTexture2) {
            requestRender();
        }

        public void shutdown(int send) {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(1, send, 0), 0);
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

        public EncoderHandler(VideoRecorder encoder) {
            this.mWeakEncoder = new WeakReference<>(encoder);
        }

        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;
            VideoRecorder encoder = (VideoRecorder) this.mWeakEncoder.get();
            if (encoder != null) {
                switch (what) {
                    case 0:
                        try {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("start encoder");
                            }
                            encoder.prepareEncoder();
                            return;
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                            encoder.handleStopRecording(0);
                            Looper.myLooper().quit();
                            return;
                        }
                    case 1:
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("stop encoder");
                        }
                        encoder.handleStopRecording(inputMessage.arg1);
                        return;
                    case 2:
                        encoder.handleVideoFrameAvailable((((long) inputMessage.arg1) << 32) | (((long) inputMessage.arg2) & 4294967295L), (Integer) inputMessage.obj);
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

    public static class AudioBufferInfo {
        public static final int MAX_SAMPLES = 10;
        public ByteBuffer[] buffer = new ByteBuffer[10];
        public boolean last;
        public int lastWroteBuffer;
        public long[] offset = new long[10];
        public int[] read = new int[10];
        public int results;

        public AudioBufferInfo() {
            for (int i = 0; i < 10; i++) {
                this.buffer[i] = ByteBuffer.allocateDirect(2048);
                this.buffer[i].order(ByteOrder.nativeOrder());
            }
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
        private int frameCount;
        private DispatchQueue generateKeyframeThumbsQueue;
        /* access modifiers changed from: private */
        public volatile EncoderHandler handler;
        /* access modifiers changed from: private */
        public ArrayList<Bitmap> keyframeThumbs;
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
                /* JADX WARNING: Code restructure failed: missing block: B:12:0x0031, code lost:
                    if (org.telegram.ui.Components.InstantCameraView.VideoRecorder.access$3000(r1.this$1) == 0) goto L_0x0132;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r17 = this;
                        r1 = r17
                        r2 = -1
                        r0 = 0
                        r3 = r2
                        r2 = r0
                    L_0x0007:
                        r5 = 0
                        r6 = 1
                        if (r2 != 0) goto L_0x0132
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r0 = r0.running
                        if (r0 != 0) goto L_0x0035
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        android.media.AudioRecord r0 = r0.audioRecorder
                        int r0 = r0.getRecordingState()
                        if (r0 == r6) goto L_0x0035
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x0029 }
                        android.media.AudioRecord r0 = r0.audioRecorder     // Catch:{ Exception -> 0x0029 }
                        r0.stop()     // Catch:{ Exception -> 0x0029 }
                        goto L_0x002b
                    L_0x0029:
                        r0 = move-exception
                        r2 = 1
                    L_0x002b:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        int r0 = r0.sendWhenDone
                        if (r0 != 0) goto L_0x0035
                        goto L_0x0132
                    L_0x0035:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r0 = r0.buffers
                        boolean r0 = r0.isEmpty()
                        if (r0 == 0) goto L_0x0048
                        org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r0 = new org.telegram.ui.Components.InstantCameraView$AudioBufferInfo
                        r0.<init>()
                        r7 = r0
                        goto L_0x0055
                    L_0x0048:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r0 = r0.buffers
                        java.lang.Object r0 = r0.poll()
                        org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r0 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r0
                        r7 = r0
                    L_0x0055:
                        r7.lastWroteBuffer = r5
                        r0 = 10
                        r7.results = r0
                        r8 = 0
                    L_0x005c:
                        if (r8 >= r0) goto L_0x00ee
                        r9 = -1
                        int r11 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                        if (r11 != 0) goto L_0x006c
                        long r9 = java.lang.System.nanoTime()
                        r11 = 1000(0x3e8, double:4.94E-321)
                        long r3 = r9 / r11
                    L_0x006c:
                        java.nio.ByteBuffer[] r9 = r7.buffer
                        r9 = r9[r8]
                        r9.rewind()
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r10 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        android.media.AudioRecord r10 = r10.audioRecorder
                        r11 = 2048(0x800, float:2.87E-42)
                        int r10 = r10.read(r9, r11)
                        if (r10 <= 0) goto L_0x00bd
                        int r11 = r8 % 2
                        if (r11 != 0) goto L_0x00bd
                        r9.limit(r10)
                        r11 = 0
                        r13 = 0
                    L_0x008b:
                        int r14 = r10 / 2
                        if (r13 >= r14) goto L_0x00a2
                        short r14 = r9.getShort()
                        int r15 = r14 * r14
                        r16 = r7
                        double r6 = (double) r15
                        java.lang.Double.isNaN(r6)
                        double r11 = r11 + r6
                        int r13 = r13 + 1
                        r7 = r16
                        r6 = 1
                        goto L_0x008b
                    L_0x00a2:
                        r16 = r7
                        double r6 = (double) r10
                        java.lang.Double.isNaN(r6)
                        double r6 = r11 / r6
                        r13 = 4611686018427387904(0xNUM, double:2.0)
                        double r6 = r6 / r13
                        double r6 = java.lang.Math.sqrt(r6)
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder$1$$ExternalSyntheticLambda0 r13 = new org.telegram.ui.Components.InstantCameraView$VideoRecorder$1$$ExternalSyntheticLambda0
                        r13.<init>(r1, r6)
                        org.telegram.messenger.AndroidUtilities.runOnUIThread(r13)
                        r9.position(r5)
                        goto L_0x00bf
                    L_0x00bd:
                        r16 = r7
                    L_0x00bf:
                        if (r10 > 0) goto L_0x00d1
                        r6 = r16
                        r6.results = r8
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r5 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r5 = r5.running
                        if (r5 != 0) goto L_0x00ef
                        r5 = 1
                        r6.last = r5
                        goto L_0x00ef
                    L_0x00d1:
                        r6 = r16
                        long[] r7 = r6.offset
                        r7[r8] = r3
                        int[] r7 = r6.read
                        r7[r8] = r10
                        r7 = 1000000(0xvar_, float:1.401298E-39)
                        int r7 = r7 * r10
                        r11 = 44100(0xaCLASSNAME, float:6.1797E-41)
                        int r7 = r7 / r11
                        int r7 = r7 / 2
                        long r11 = (long) r7
                        long r3 = r3 + r11
                        int r8 = r8 + 1
                        r7 = r6
                        r6 = 1
                        goto L_0x005c
                    L_0x00ee:
                        r6 = r7
                    L_0x00ef:
                        int r5 = r6.results
                        if (r5 >= 0) goto L_0x010f
                        boolean r5 = r6.last
                        if (r5 == 0) goto L_0x00f8
                        goto L_0x010f
                    L_0x00f8:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r0 = r0.running
                        if (r0 != 0) goto L_0x0103
                        r0 = 1
                        r2 = r0
                        goto L_0x0130
                    L_0x0103:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x010d }
                        java.util.concurrent.ArrayBlockingQueue r0 = r0.buffers     // Catch:{ Exception -> 0x010d }
                        r0.put(r6)     // Catch:{ Exception -> 0x010d }
                        goto L_0x0130
                    L_0x010d:
                        r0 = move-exception
                        goto L_0x0130
                    L_0x010f:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r5 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        boolean r5 = r5.running
                        if (r5 != 0) goto L_0x011c
                        int r5 = r6.results
                        if (r5 >= r0) goto L_0x011c
                        r2 = 1
                    L_0x011c:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r0 = r0.handler
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r5 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r5 = r5.handler
                        r7 = 3
                        android.os.Message r5 = r5.obtainMessage(r7, r6)
                        r0.sendMessage(r5)
                    L_0x0130:
                        goto L_0x0007
                    L_0x0132:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x013c }
                        android.media.AudioRecord r0 = r0.audioRecorder     // Catch:{ Exception -> 0x013c }
                        r0.release()     // Catch:{ Exception -> 0x013c }
                        goto L_0x0140
                    L_0x013c:
                        r0 = move-exception
                        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                    L_0x0140:
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r0 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r0 = r0.handler
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r6 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        org.telegram.ui.Components.InstantCameraView$EncoderHandler r6 = r6.handler
                        org.telegram.ui.Components.InstantCameraView$VideoRecorder r7 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this
                        int r7 = r7.sendWhenDone
                        r8 = 1
                        android.os.Message r5 = r6.obtainMessage(r8, r7, r5)
                        r0.sendMessage(r5)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.AnonymousClass1.run():void");
                }

                /* renamed from: lambda$run$0$org-telegram-ui-Components-InstantCameraView$VideoRecorder$1  reason: not valid java name */
                public /* synthetic */ void m2357x7davar_d6(double amplitude) {
                    NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Integer.valueOf(InstantCameraView.this.recordingGuid), Double.valueOf(amplitude));
                }
            };
        }

        /* JADX WARNING: Code restructure failed: missing block: B:15:0x004c, code lost:
            r5.keyframeThumbs.clear();
            r5.frameCount = 0;
            r3 = r5.generateKeyframeThumbsQueue;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0056, code lost:
            if (r3 == null) goto L_0x0060;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0058, code lost:
            r3.cleanupQueue();
            r5.generateKeyframeThumbsQueue.recycle();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0060, code lost:
            r5.generateKeyframeThumbsQueue = new org.telegram.messenger.DispatchQueue("keyframes_thumb_queque");
            r5.handler.sendMessage(r5.handler.obtainMessage(0));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0074, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void startRecording(java.io.File r6, android.opengl.EGLContext r7) {
            /*
                r5 = this;
                org.telegram.ui.Components.InstantCameraView r0 = org.telegram.ui.Components.InstantCameraView.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
                int r0 = r0.roundVideoSize
                org.telegram.ui.Components.InstantCameraView r1 = org.telegram.ui.Components.InstantCameraView.this
                int r1 = r1.currentAccount
                org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
                int r1 = r1.roundVideoBitrate
                int r1 = r1 * 1024
                r5.videoFile = r6
                r5.videoWidth = r0
                r5.videoHeight = r0
                r5.videoBitrate = r1
                r5.sharedEglContext = r7
                java.lang.Object r2 = r5.sync
                monitor-enter(r2)
                boolean r3 = r5.running     // Catch:{ all -> 0x0075 }
                if (r3 == 0) goto L_0x002d
                monitor-exit(r2)     // Catch:{ all -> 0x0075 }
                return
            L_0x002d:
                r3 = 1
                r5.running = r3     // Catch:{ all -> 0x0075 }
                java.lang.Thread r3 = new java.lang.Thread     // Catch:{ all -> 0x0075 }
                java.lang.String r4 = "TextureMovieEncoder"
                r3.<init>(r5, r4)     // Catch:{ all -> 0x0075 }
                r4 = 10
                r3.setPriority(r4)     // Catch:{ all -> 0x0075 }
                r3.start()     // Catch:{ all -> 0x0075 }
            L_0x003f:
                boolean r4 = r5.ready     // Catch:{ all -> 0x0075 }
                if (r4 != 0) goto L_0x004b
                java.lang.Object r4 = r5.sync     // Catch:{ InterruptedException -> 0x0049 }
                r4.wait()     // Catch:{ InterruptedException -> 0x0049 }
                goto L_0x004a
            L_0x0049:
                r4 = move-exception
            L_0x004a:
                goto L_0x003f
            L_0x004b:
                monitor-exit(r2)     // Catch:{ all -> 0x0075 }
                java.util.ArrayList<android.graphics.Bitmap> r2 = r5.keyframeThumbs
                r2.clear()
                r2 = 0
                r5.frameCount = r2
                org.telegram.messenger.DispatchQueue r3 = r5.generateKeyframeThumbsQueue
                if (r3 == 0) goto L_0x0060
                r3.cleanupQueue()
                org.telegram.messenger.DispatchQueue r3 = r5.generateKeyframeThumbsQueue
                r3.recycle()
            L_0x0060:
                org.telegram.messenger.DispatchQueue r3 = new org.telegram.messenger.DispatchQueue
                java.lang.String r4 = "keyframes_thumb_queque"
                r3.<init>(r4)
                r5.generateKeyframeThumbsQueue = r3
                org.telegram.ui.Components.InstantCameraView$EncoderHandler r3 = r5.handler
                org.telegram.ui.Components.InstantCameraView$EncoderHandler r4 = r5.handler
                android.os.Message r2 = r4.obtainMessage(r2)
                r3.sendMessage(r2)
                return
            L_0x0075:
                r3 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0075 }
                goto L_0x0079
            L_0x0078:
                throw r3
            L_0x0079:
                goto L_0x0078
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.startRecording(java.io.File, android.opengl.EGLContext):void");
        }

        public void stopRecording(int send) {
            this.handler.sendMessage(this.handler.obtainMessage(1, send, 0));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
            r2 = r7.zeroTimeStamps + 1;
            r7.zeroTimeStamps = r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
            if (r2 <= 1) goto L_0x0027;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x0025;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0020, code lost:
            org.telegram.messenger.FileLog.d("fix timestamp enabled");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0025, code lost:
            r0 = r10;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0027, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0028, code lost:
            r7.zeroTimeStamps = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x002b, code lost:
            r7.handler.sendMessage(r7.handler.obtainMessage(2, (int) (r0 >> 32), (int) r0, r9));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x000a, code lost:
            r0 = r8.getTimestamp();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
            if (r0 != 0) goto L_0x0028;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void frameAvailable(android.graphics.SurfaceTexture r8, java.lang.Integer r9, long r10) {
            /*
                r7 = this;
                java.lang.Object r0 = r7.sync
                monitor-enter(r0)
                boolean r1 = r7.ready     // Catch:{ all -> 0x003e }
                if (r1 != 0) goto L_0x0009
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                return
            L_0x0009:
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                long r0 = r8.getTimestamp()
                r2 = 0
                int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r4 != 0) goto L_0x0028
                int r2 = r7.zeroTimeStamps
                r3 = 1
                int r2 = r2 + r3
                r7.zeroTimeStamps = r2
                if (r2 <= r3) goto L_0x0027
                boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r2 == 0) goto L_0x0025
                java.lang.String r2 = "fix timestamp enabled"
                org.telegram.messenger.FileLog.d(r2)
            L_0x0025:
                r0 = r10
                goto L_0x002b
            L_0x0027:
                return
            L_0x0028:
                r2 = 0
                r7.zeroTimeStamps = r2
            L_0x002b:
                org.telegram.ui.Components.InstantCameraView$EncoderHandler r2 = r7.handler
                org.telegram.ui.Components.InstantCameraView$EncoderHandler r3 = r7.handler
                r4 = 2
                r5 = 32
                long r5 = r0 >> r5
                int r6 = (int) r5
                int r5 = (int) r0
                android.os.Message r3 = r3.obtainMessage(r4, r6, r5, r9)
                r2.sendMessage(r3)
                return
            L_0x003e:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                throw r1
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
        public void handleAudioFrameAvailable(AudioBufferInfo input) {
            ByteBuffer inputBuffer;
            if (!this.audioStopedByTime) {
                AudioBufferInfo input2 = input;
                this.buffersToWrite.add(input2);
                if (this.audioFirst == -1) {
                    if (this.videoFirst != -1) {
                        while (true) {
                            boolean ok = false;
                            int a = 0;
                            while (true) {
                                if (a >= input2.results) {
                                    break;
                                } else if (a == 0 && Math.abs(this.videoFirst - input2.offset[a]) > 10000000) {
                                    this.desyncTime = this.videoFirst - input2.offset[a];
                                    this.audioFirst = input2.offset[a];
                                    ok = true;
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("detected desync between audio and video " + this.desyncTime);
                                    }
                                } else if (input2.offset[a] >= this.videoFirst) {
                                    input2.lastWroteBuffer = a;
                                    this.audioFirst = input2.offset[a];
                                    ok = true;
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("found first audio frame at " + a + " timestamp = " + input2.offset[a]);
                                    }
                                } else {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("ignore first audio frame at " + a + " timestamp = " + input2.offset[a]);
                                    }
                                    a++;
                                }
                            }
                            if (ok) {
                                break;
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("first audio frame not found, removing buffers " + input2.results);
                            }
                            this.buffersToWrite.remove(input2);
                            if (!this.buffersToWrite.isEmpty()) {
                                input2 = this.buffersToWrite.get(0);
                            } else {
                                return;
                            }
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("video record not yet started");
                        return;
                    } else {
                        return;
                    }
                }
                if (this.audioStartTime == -1) {
                    this.audioStartTime = input2.offset[input2.lastWroteBuffer];
                }
                if (this.buffersToWrite.size() > 1) {
                    input2 = this.buffersToWrite.get(0);
                }
                try {
                    drainEncoder(false);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                boolean isLast = false;
                while (input2 != null) {
                    try {
                        int inputBufferIndex = this.audioEncoder.dequeueInputBuffer(0);
                        if (inputBufferIndex >= 0) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                inputBuffer = this.audioEncoder.getInputBuffer(inputBufferIndex);
                            } else {
                                ByteBuffer inputBuffer2 = this.audioEncoder.getInputBuffers()[inputBufferIndex];
                                inputBuffer2.clear();
                                inputBuffer = inputBuffer2;
                            }
                            long startWriteTime = input2.offset[input2.lastWroteBuffer];
                            int a2 = input2.lastWroteBuffer;
                            while (true) {
                                if (a2 > input2.results) {
                                    break;
                                }
                                if (a2 < input2.results) {
                                    if (!this.running && input2.offset[a2] >= this.videoLast - this.desyncTime) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.d("stop audio encoding because of stoped video recording at " + input2.offset[a2] + " last video " + this.videoLast);
                                        }
                                        this.audioStopedByTime = true;
                                        isLast = true;
                                        input2 = null;
                                        this.buffersToWrite.clear();
                                    } else if (inputBuffer.remaining() < input2.read[a2]) {
                                        input2.lastWroteBuffer = a2;
                                        input2 = null;
                                        break;
                                    } else {
                                        inputBuffer.put(input2.buffer[a2]);
                                    }
                                }
                                if (a2 >= input2.results - 1) {
                                    this.buffersToWrite.remove(input2);
                                    if (this.running) {
                                        this.buffers.put(input2);
                                    }
                                    if (this.buffersToWrite.isEmpty()) {
                                        isLast = input2.last;
                                        input2 = null;
                                        break;
                                    }
                                    input2 = this.buffersToWrite.get(0);
                                }
                                a2++;
                            }
                            MediaCodec mediaCodec = this.audioEncoder;
                            int position = inputBuffer.position();
                            long j = 0;
                            if (startWriteTime != 0) {
                                j = startWriteTime - this.audioStartTime;
                            }
                            mediaCodec.queueInputBuffer(inputBufferIndex, 0, position, j, isLast ? 4 : 0);
                        }
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                        return;
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void handleVideoFrameAvailable(long timestampNanos, Integer cameraId) {
            long dt;
            long alphaDt;
            long j = timestampNanos;
            Integer num = cameraId;
            try {
                drainEncoder(false);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (!this.lastCameraId.equals(num)) {
                this.lastTimestamp = -1;
                this.lastCameraId = num;
            }
            long dt2 = this.lastTimestamp;
            if (dt2 == -1) {
                this.lastTimestamp = j;
                dt = 0;
                if (this.currentTimestamp != 0) {
                    alphaDt = (System.currentTimeMillis() - this.lastCommitedFrameTime) * 1000000;
                    dt = 0;
                } else {
                    alphaDt = 0;
                }
            } else {
                alphaDt = j - dt2;
                long j2 = alphaDt;
                this.lastTimestamp = j;
                dt = alphaDt;
            }
            this.lastCommitedFrameTime = System.currentTimeMillis();
            if (!this.skippedFirst) {
                long j3 = this.skippedTime + alphaDt;
                this.skippedTime = j3;
                if (j3 >= NUM) {
                    this.skippedFirst = true;
                } else {
                    return;
                }
            }
            this.currentTimestamp += alphaDt;
            if (this.videoFirst == -1) {
                this.videoFirst = j / 1000;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("first video frame was at " + this.videoFirst);
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
            if (InstantCameraView.this.oldCameraTexture[0] != 0) {
                if (!this.blendEnabled) {
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
            createKeyframeThumb();
            this.frameCount++;
            if (InstantCameraView.this.oldCameraTexture[0] != 0 && InstantCameraView.this.cameraTextureAlpha < 1.0f) {
                InstantCameraView.access$2216(InstantCameraView.this, ((float) dt) / 2.0E8f);
                if (InstantCameraView.this.cameraTextureAlpha > 1.0f) {
                    GLES20.glDisable(3042);
                    this.blendEnabled = false;
                    float unused = InstantCameraView.this.cameraTextureAlpha = 1.0f;
                    GLES20.glDeleteTextures(1, InstantCameraView.this.oldCameraTexture, 0);
                    InstantCameraView.this.oldCameraTexture[0] = 0;
                    if (!InstantCameraView.this.cameraReady) {
                        boolean unused2 = InstantCameraView.this.cameraReady = true;
                        AndroidUtilities.runOnUIThread(new InstantCameraView$VideoRecorder$$ExternalSyntheticLambda1(this));
                    }
                }
            } else if (!InstantCameraView.this.cameraReady) {
                boolean unused3 = InstantCameraView.this.cameraReady = true;
                AndroidUtilities.runOnUIThread(new InstantCameraView$VideoRecorder$$ExternalSyntheticLambda2(this));
            }
        }

        /* renamed from: lambda$handleVideoFrameAvailable$0$org-telegram-ui-Components-InstantCameraView$VideoRecorder  reason: not valid java name */
        public /* synthetic */ void m2354xd54df1e5() {
            InstantCameraView.this.textureOverlayView.animate().setDuration(120).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).start();
        }

        /* renamed from: lambda$handleVideoFrameAvailable$1$org-telegram-ui-Components-InstantCameraView$VideoRecorder  reason: not valid java name */
        public /* synthetic */ void m2355xdb51bd44() {
            InstantCameraView.this.textureOverlayView.animate().setDuration(120).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).start();
        }

        private void createKeyframeThumb() {
            if (Build.VERSION.SDK_INT >= 21 && SharedConfig.getDevicePerformanceClass() != 0 && this.frameCount % 33 == 0) {
                this.generateKeyframeThumbsQueue.postRunnable(new GenerateKeyframeThumbTask());
            }
        }

        private class GenerateKeyframeThumbTask implements Runnable {
            private GenerateKeyframeThumbTask() {
            }

            public void run() {
                TextureView textureView = InstantCameraView.this.textureView;
                if (textureView != null) {
                    AndroidUtilities.runOnUIThread(new InstantCameraView$VideoRecorder$GenerateKeyframeThumbTask$$ExternalSyntheticLambda0(this, textureView.getBitmap(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f))));
                }
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-InstantCameraView$VideoRecorder$GenerateKeyframeThumbTask  reason: not valid java name */
            public /* synthetic */ void m2358x5a43423d(Bitmap bitmap) {
                if ((bitmap == null || bitmap.getPixel(0, 0) == 0) && VideoRecorder.this.keyframeThumbs.size() > 1) {
                    VideoRecorder.this.keyframeThumbs.add((Bitmap) VideoRecorder.this.keyframeThumbs.get(VideoRecorder.this.keyframeThumbs.size() - 1));
                } else {
                    VideoRecorder.this.keyframeThumbs.add(bitmap);
                }
            }
        }

        /* access modifiers changed from: private */
        public void handleStopRecording(int send) {
            if (this.running) {
                this.sendWhenDone = send;
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
            DispatchQueue dispatchQueue = this.generateKeyframeThumbsQueue;
            if (dispatchQueue != null) {
                dispatchQueue.cleanupQueue();
                this.generateKeyframeThumbsQueue.recycle();
                this.generateKeyframeThumbsQueue = null;
            }
            if (send != 0) {
                AndroidUtilities.runOnUIThread(new InstantCameraView$VideoRecorder$$ExternalSyntheticLambda4(this, send));
            } else {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).cancelFileUpload(this.videoFile.getAbsolutePath(), false);
                this.videoFile.delete();
            }
            EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            Surface surface2 = this.surface;
            if (surface2 != null) {
                surface2.release();
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

        /* renamed from: lambda$handleStopRecording$4$org-telegram-ui-Components-InstantCameraView$VideoRecorder  reason: not valid java name */
        public /* synthetic */ void m2353xedeb4749(int send) {
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
            VideoEditedInfo access$700 = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalWidth = 360;
            access$700.resultWidth = 360;
            VideoEditedInfo access$7002 = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalHeight = 360;
            access$7002.resultHeight = 360;
            InstantCameraView.this.videoEditedInfo.originalPath = this.videoFile.getAbsolutePath();
            if (send != 1) {
                VideoPlayer unused2 = InstantCameraView.this.videoPlayer = new VideoPlayer();
                InstantCameraView.this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                    public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                        VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
                    }

                    public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
                    }

                    public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
                    }

                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        if (InstantCameraView.this.videoPlayer != null && InstantCameraView.this.videoPlayer.isPlaying() && playbackState == 4) {
                            VideoPlayer access$600 = InstantCameraView.this.videoPlayer;
                            long j = 0;
                            if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                                j = InstantCameraView.this.videoEditedInfo.startTime;
                            }
                            access$600.seekTo(j);
                        }
                    }

                    public void onError(VideoPlayer player, Exception e) {
                        FileLog.e((Throwable) e);
                    }

                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                    }

                    public void onRenderedFirstFrame() {
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
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
                AlertsCreator.createScheduleDatePickerDialog((Context) InstantCameraView.this.baseFragment.getParentActivity(), InstantCameraView.this.baseFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new InstantCameraView$VideoRecorder$$ExternalSyntheticLambda5(this), (Runnable) new InstantCameraView$VideoRecorder$$ExternalSyntheticLambda0(this), InstantCameraView.this.resourcesProvider);
            } else {
                InstantCameraView.this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0, this.videoFile.getAbsolutePath(), 0, true, 0, 0, 0), InstantCameraView.this.videoEditedInfo, true, 0, false);
            }
            didWriteData(this.videoFile, 0, true);
            MediaController.getInstance().requestAudioFocus(false);
        }

        /* renamed from: lambda$handleStopRecording$2$org-telegram-ui-Components-InstantCameraView$VideoRecorder  reason: not valid java name */
        public /* synthetic */ void m2351xe1e3b08b(boolean notify, int scheduleDate) {
            InstantCameraView.this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0, this.videoFile.getAbsolutePath(), 0, true, 0, 0, 0), InstantCameraView.this.videoEditedInfo, notify, scheduleDate, false);
            InstantCameraView.this.startAnimation(false);
        }

        /* renamed from: lambda$handleStopRecording$3$org-telegram-ui-Components-InstantCameraView$VideoRecorder  reason: not valid java name */
        public /* synthetic */ void m2352xe7e77bea() {
            InstantCameraView.this.startAnimation(false);
        }

        /* access modifiers changed from: private */
        public void prepareEncoder() {
            try {
                int recordBufferSize = AudioRecord.getMinBufferSize(44100, 16, 2);
                if (recordBufferSize <= 0) {
                    recordBufferSize = 3584;
                }
                int bufferSize = 49152;
                if (49152 < recordBufferSize) {
                    bufferSize = ((recordBufferSize / 2048) + 1) * 2048 * 2;
                }
                for (int a = 0; a < 3; a++) {
                    this.buffers.add(new AudioBufferInfo());
                }
                AudioRecord audioRecord = r9;
                AudioRecord audioRecord2 = new AudioRecord(0, 44100, 16, 2, bufferSize);
                this.audioRecorder = audioRecord;
                audioRecord.startRecording();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("initied audio record with channels " + this.audioRecorder.getChannelCount() + " sample rate = " + this.audioRecorder.getSampleRate() + " bufferSize = " + bufferSize);
                }
                Thread thread = new Thread(this.recorderRunnable);
                thread.setPriority(10);
                thread.start();
                this.audioBufferInfo = new MediaCodec.BufferInfo();
                this.videoBufferInfo = new MediaCodec.BufferInfo();
                MediaFormat audioFormat = new MediaFormat();
                audioFormat.setString("mime", "audio/mp4a-latm");
                audioFormat.setInteger("sample-rate", 44100);
                audioFormat.setInteger("channel-count", 1);
                audioFormat.setInteger("bitrate", MessagesController.getInstance(InstantCameraView.this.currentAccount).roundAudioBitrate * 1024);
                audioFormat.setInteger("max-input-size", 20480);
                MediaCodec createEncoderByType = MediaCodec.createEncoderByType("audio/mp4a-latm");
                this.audioEncoder = createEncoderByType;
                createEncoderByType.configure(audioFormat, (Surface) null, (MediaCrypto) null, 1);
                this.audioEncoder.start();
                this.videoEncoder = MediaCodec.createEncoderByType("video/avc");
                this.firstEncode = true;
                MediaFormat format = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                format.setInteger("color-format", NUM);
                format.setInteger("bitrate", this.videoBitrate);
                format.setInteger("frame-rate", 30);
                format.setInteger("i-frame-interval", 1);
                this.videoEncoder.configure(format, (Surface) null, (MediaCrypto) null, 1);
                this.surface = this.videoEncoder.createInputSurface();
                this.videoEncoder.start();
                Mp4Movie movie = new Mp4Movie();
                movie.setCacheFile(this.videoFile);
                movie.setRotation(0);
                movie.setSize(this.videoWidth, this.videoHeight);
                this.mediaMuxer = new MP4Builder().createMovie(movie, InstantCameraView.this.isSecretChat);
                AndroidUtilities.runOnUIThread(new InstantCameraView$VideoRecorder$$ExternalSyntheticLambda3(this));
                if (this.eglDisplay == EGL14.EGL_NO_DISPLAY) {
                    android.opengl.EGLDisplay eglGetDisplay = EGL14.eglGetDisplay(0);
                    this.eglDisplay = eglGetDisplay;
                    if (eglGetDisplay != EGL14.EGL_NO_DISPLAY) {
                        int[] version = new int[2];
                        if (EGL14.eglInitialize(this.eglDisplay, version, 0, version, 1)) {
                            if (this.eglContext == EGL14.EGL_NO_CONTEXT) {
                                int[] attribList = {12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12610, 1, 12344};
                                android.opengl.EGLConfig[] configs = new android.opengl.EGLConfig[1];
                                if (EGL14.eglChooseConfig(this.eglDisplay, attribList, 0, configs, 0, configs.length, new int[1], 0)) {
                                    this.eglContext = EGL14.eglCreateContext(this.eglDisplay, configs[0], this.sharedEglContext, new int[]{12440, 2, 12344}, 0);
                                    this.eglConfig = configs[0];
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
                                    int vertexShader = InstantCameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                                    int fragmentShader = InstantCameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   float coef = ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0));\n   vec3 color = texture2D(sTexture, vTextureCoord).rgb * coef + (1.0 - step(0.001, coef));\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n");
                                    if (vertexShader != 0 && fragmentShader != 0) {
                                        int glCreateProgram = GLES20.glCreateProgram();
                                        this.drawProgram = glCreateProgram;
                                        GLES20.glAttachShader(glCreateProgram, vertexShader);
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
            } catch (Exception ioe) {
                throw new RuntimeException(ioe);
            }
        }

        /* renamed from: lambda$prepareEncoder$5$org-telegram-ui-Components-InstantCameraView$VideoRecorder  reason: not valid java name */
        public /* synthetic */ void m2356x434a2d80() {
            if (!InstantCameraView.this.cancelled) {
                try {
                    InstantCameraView.this.performHapticFeedback(3, 2);
                } catch (Exception e) {
                }
                AndroidUtilities.lockOrientation(InstantCameraView.this.baseFragment.getParentActivity());
                boolean unused = InstantCameraView.this.recording = true;
                long unused2 = InstantCameraView.this.recordStartTime = System.currentTimeMillis();
                InstantCameraView.this.invalidate();
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordStarted, Integer.valueOf(InstantCameraView.this.recordingGuid), false);
            }
        }

        public Surface getInputSurface() {
            return this.surface;
        }

        private void didWriteData(File file, long availableSize, boolean last) {
            long j = 0;
            if (this.videoConvertFirstWrite) {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).uploadFile(file.toString(), InstantCameraView.this.isSecretChat, false, 1, 33554432, false);
                this.videoConvertFirstWrite = false;
                if (last) {
                    FileLoader instance = FileLoader.getInstance(InstantCameraView.this.currentAccount);
                    String file2 = file.toString();
                    boolean access$3800 = InstantCameraView.this.isSecretChat;
                    if (last) {
                        j = file.length();
                    }
                    instance.checkUploadNewDataAvailable(file2, access$3800, availableSize, j);
                    return;
                }
                return;
            }
            FileLoader instance2 = FileLoader.getInstance(InstantCameraView.this.currentAccount);
            String file3 = file.toString();
            boolean access$38002 = InstantCameraView.this.isSecretChat;
            if (last) {
                j = file.length();
            }
            instance2.checkUploadNewDataAvailable(file3, access$38002, availableSize, j);
        }

        public void drainEncoder(boolean endOfStream) throws Exception {
            ByteBuffer encodedData;
            ByteBuffer encodedData2;
            if (endOfStream) {
                this.videoEncoder.signalEndOfInputStream();
            }
            ByteBuffer[] encoderOutputBuffers = null;
            int i = 21;
            if (Build.VERSION.SDK_INT < 21) {
                encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
            }
            while (true) {
                int encoderStatus = this.videoEncoder.dequeueOutputBuffer(this.videoBufferInfo, 10000);
                if (encoderStatus != -1) {
                    if (encoderStatus == -3) {
                        if (Build.VERSION.SDK_INT < i) {
                            encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
                        }
                    } else if (encoderStatus == -2) {
                        MediaFormat newFormat = this.videoEncoder.getOutputFormat();
                        if (this.videoTrackIndex == -5) {
                            this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat, false);
                            if (newFormat.containsKey("prepend-sps-pps-to-idr-frames") && newFormat.getInteger("prepend-sps-pps-to-idr-frames") == 1) {
                                this.prependHeaderSize = newFormat.getByteBuffer("csd-0").limit() + newFormat.getByteBuffer("csd-1").limit();
                            }
                        }
                    } else if (encoderStatus < 0) {
                        continue;
                    } else {
                        if (Build.VERSION.SDK_INT < i) {
                            encodedData2 = encoderOutputBuffers[encoderStatus];
                        } else {
                            encodedData2 = this.videoEncoder.getOutputBuffer(encoderStatus);
                        }
                        if (encodedData2 != null) {
                            if (this.videoBufferInfo.size > 1) {
                                if ((this.videoBufferInfo.flags & 2) == 0) {
                                    if (!(this.prependHeaderSize == 0 || (this.videoBufferInfo.flags & 1) == 0)) {
                                        this.videoBufferInfo.offset += this.prependHeaderSize;
                                        this.videoBufferInfo.size -= this.prependHeaderSize;
                                    }
                                    if (this.firstEncode && (this.videoBufferInfo.flags & 1) != 0) {
                                        if (this.videoBufferInfo.size > 100) {
                                            encodedData2.position(this.videoBufferInfo.offset);
                                            byte[] temp = new byte[100];
                                            encodedData2.get(temp);
                                            int nalCount = 0;
                                            int a = 0;
                                            while (true) {
                                                if (a < temp.length - 4) {
                                                    if (temp[a] == 0 && temp[a + 1] == 0 && temp[a + 2] == 0 && temp[a + 3] == 1 && (nalCount = nalCount + 1) > 1) {
                                                        this.videoBufferInfo.offset += a;
                                                        this.videoBufferInfo.size -= a;
                                                        break;
                                                    }
                                                    a++;
                                                } else {
                                                    break;
                                                }
                                            }
                                        }
                                        this.firstEncode = false;
                                    }
                                    long availableSize = this.mediaMuxer.writeSampleData(this.videoTrackIndex, encodedData2, this.videoBufferInfo, true);
                                    if (availableSize != 0) {
                                        didWriteData(this.videoFile, availableSize, false);
                                    }
                                } else if (this.videoTrackIndex == -5) {
                                    byte[] csd = new byte[this.videoBufferInfo.size];
                                    encodedData2.limit(this.videoBufferInfo.offset + this.videoBufferInfo.size);
                                    encodedData2.position(this.videoBufferInfo.offset);
                                    encodedData2.get(csd);
                                    ByteBuffer sps = null;
                                    ByteBuffer pps = null;
                                    int a2 = this.videoBufferInfo.size - 1;
                                    while (true) {
                                        if (a2 >= 0 && a2 > 3) {
                                            if (csd[a2] == 1 && csd[a2 - 1] == 0 && csd[a2 - 2] == 0 && csd[a2 - 3] == 0) {
                                                sps = ByteBuffer.allocate(a2 - 3);
                                                pps = ByteBuffer.allocate(this.videoBufferInfo.size - (a2 - 3));
                                                sps.put(csd, 0, a2 - 3).position(0);
                                                pps.put(csd, a2 - 3, this.videoBufferInfo.size - (a2 - 3)).position(0);
                                                break;
                                            }
                                            a2--;
                                        } else {
                                            break;
                                        }
                                    }
                                    MediaFormat newFormat2 = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                                    if (!(sps == null || pps == null)) {
                                        newFormat2.setByteBuffer("csd-0", sps);
                                        newFormat2.setByteBuffer("csd-1", pps);
                                    }
                                    this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat2, false);
                                }
                            }
                            this.videoEncoder.releaseOutputBuffer(encoderStatus, false);
                            if ((this.videoBufferInfo.flags & 4) != 0) {
                                break;
                            }
                        } else {
                            throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                        }
                    }
                    i = 21;
                } else if (!endOfStream) {
                    break;
                } else {
                    i = 21;
                }
            }
            if (Build.VERSION.SDK_INT < 21) {
                encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
            }
            while (true) {
                int encoderStatus2 = this.audioEncoder.dequeueOutputBuffer(this.audioBufferInfo, 0);
                if (encoderStatus2 == -1) {
                    if (!endOfStream) {
                        return;
                    }
                    if (!this.running && this.sendWhenDone == 0) {
                        return;
                    }
                } else if (encoderStatus2 == -3) {
                    if (Build.VERSION.SDK_INT < 21) {
                        encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
                    }
                } else if (encoderStatus2 == -2) {
                    MediaFormat newFormat3 = this.audioEncoder.getOutputFormat();
                    if (this.audioTrackIndex == -5) {
                        this.audioTrackIndex = this.mediaMuxer.addTrack(newFormat3, true);
                    }
                } else if (encoderStatus2 >= 0) {
                    if (Build.VERSION.SDK_INT < 21) {
                        encodedData = encoderOutputBuffers[encoderStatus2];
                    } else {
                        encodedData = this.audioEncoder.getOutputBuffer(encoderStatus2);
                    }
                    if (encodedData != null) {
                        if ((this.audioBufferInfo.flags & 2) != 0) {
                            this.audioBufferInfo.size = 0;
                        }
                        if (this.audioBufferInfo.size != 0) {
                            long availableSize2 = this.mediaMuxer.writeSampleData(this.audioTrackIndex, encodedData, this.audioBufferInfo, false);
                            if (availableSize2 != 0) {
                                didWriteData(this.videoFile, availableSize2, false);
                            }
                        }
                        this.audioEncoder.releaseOutputBuffer(encoderStatus2, false);
                        if ((this.audioBufferInfo.flags & 4) != 0) {
                            return;
                        }
                    } else {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus2 + " was null");
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

    public class InstantViewCameraContainer extends FrameLayout {
        float imageProgress;
        ImageReceiver imageReceiver;

        public InstantViewCameraContainer(Context context) {
            super(context);
            InstantCameraView.this.setWillNotDraw(false);
        }

        public void setImageReceiver(ImageReceiver imageReceiver2) {
            if (this.imageReceiver == null) {
                this.imageProgress = 0.0f;
            }
            this.imageReceiver = imageReceiver2;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            float f = this.imageProgress;
            if (f != 1.0f) {
                float f2 = f + 0.064f;
                this.imageProgress = f2;
                if (f2 > 1.0f) {
                    this.imageProgress = 1.0f;
                }
                invalidate();
            }
            if (this.imageReceiver != null) {
                canvas.save();
                if (this.imageReceiver.getImageWidth() != ((float) InstantCameraView.this.textureViewSize)) {
                    float s = ((float) InstantCameraView.this.textureViewSize) / this.imageReceiver.getImageWidth();
                    canvas.scale(s, s);
                }
                canvas.translate(-this.imageReceiver.getImageX(), -this.imageReceiver.getImageY());
                float oldAlpha = this.imageReceiver.getAlpha();
                this.imageReceiver.setAlpha(this.imageProgress);
                this.imageReceiver.draw(canvas);
                this.imageReceiver.setAlpha(oldAlpha);
                canvas.restore();
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        VideoPlayer videoPlayer2;
        if (!(ev.getAction() != 0 || this.baseFragment == null || (videoPlayer2 = this.videoPlayer) == null)) {
            boolean mute = !videoPlayer2.isMuted();
            this.videoPlayer.setMute(mute);
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
            fArr[0] = mute ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(imageView, property, fArr);
            ImageView imageView2 = this.muteImageView;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            float f = 0.5f;
            fArr2[0] = mute ? 1.0f : 0.5f;
            animatorArr[1] = ObjectAnimator.ofFloat(imageView2, property2, fArr2);
            ImageView imageView3 = this.muteImageView;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            if (mute) {
                f = 1.0f;
            }
            fArr3[0] = f;
            animatorArr[2] = ObjectAnimator.ofFloat(imageView3, property3, fArr3);
            animatorSet3.playTogether(animatorArr);
            this.muteAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(InstantCameraView.this.muteAnimation)) {
                        AnimatorSet unused = InstantCameraView.this.muteAnimation = null;
                    }
                }
            });
            this.muteAnimation.setDuration(180);
            this.muteAnimation.setInterpolator(new DecelerateInterpolator());
            this.muteAnimation.start();
        }
        if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
            if (this.maybePinchToZoomTouchMode && !this.isInPinchToZoomTouchMode && ev.getPointerCount() == 2 && this.finishZoomTransition == null && this.recording) {
                this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)));
                this.pinchScale = 1.0f;
                this.pointerId1 = ev.getPointerId(0);
                this.pointerId2 = ev.getPointerId(1);
                this.isInPinchToZoomTouchMode = true;
            }
            if (ev.getActionMasked() == 0) {
                AndroidUtilities.rectTmp.set(this.cameraContainer.getX(), this.cameraContainer.getY(), this.cameraContainer.getX() + ((float) this.cameraContainer.getMeasuredWidth()), this.cameraContainer.getY() + ((float) this.cameraContainer.getMeasuredHeight()));
                this.maybePinchToZoomTouchMode = AndroidUtilities.rectTmp.contains(ev.getX(), ev.getY());
            }
            return true;
        }
        if (ev.getActionMasked() == 2 && this.isInPinchToZoomTouchMode) {
            int index1 = -1;
            int index2 = -1;
            for (int i = 0; i < ev.getPointerCount(); i++) {
                if (this.pointerId1 == ev.getPointerId(i)) {
                    index1 = i;
                }
                if (this.pointerId2 == ev.getPointerId(i)) {
                    index2 = i;
                }
            }
            if (index1 == -1 || index2 == -1) {
                this.isInPinchToZoomTouchMode = false;
                finishZoom();
                return false;
            }
            float hypot = ((float) Math.hypot((double) (ev.getX(index2) - ev.getX(index1)), (double) (ev.getY(index2) - ev.getY(index1)))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            this.cameraSession.setZoom(Math.min(1.0f, Math.max(0.0f, hypot - 1.0f)));
        } else if ((ev.getActionMasked() == 1 || ((ev.getActionMasked() == 6 && checkPointerIds(ev)) || ev.getActionMasked() == 3)) && this.isInPinchToZoomTouchMode) {
            this.isInPinchToZoomTouchMode = false;
            finishZoom();
        }
        return true;
    }

    public void finishZoom() {
        if (this.finishZoomTransition == null) {
            float zoom = Math.min(1.0f, Math.max(0.0f, this.pinchScale - 1.0f));
            if (zoom > 0.0f) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{zoom, 0.0f});
                this.finishZoomTransition = ofFloat;
                ofFloat.addUpdateListener(new InstantCameraView$$ExternalSyntheticLambda0(this));
                this.finishZoomTransition.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (InstantCameraView.this.finishZoomTransition != null) {
                            InstantCameraView.this.finishZoomTransition = null;
                        }
                    }
                });
                this.finishZoomTransition.setDuration(350);
                this.finishZoomTransition.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.finishZoomTransition.start();
            }
        }
    }

    /* renamed from: lambda$finishZoom$5$org-telegram-ui-Components-InstantCameraView  reason: not valid java name */
    public /* synthetic */ void m2345lambda$finishZoom$5$orgtelegramuiComponentsInstantCameraView(ValueAnimator valueAnimator) {
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.setZoom(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }
}
