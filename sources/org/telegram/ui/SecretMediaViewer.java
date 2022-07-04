package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Property;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import androidx.annotation.Keep;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Scroller;
import org.telegram.ui.Components.TimerParticles;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.PhotoViewer;

public class SecretMediaViewer implements NotificationCenter.NotificationCenterDelegate, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile SecretMediaViewer Instance;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    private int[] animateFromRadius;
    private float animateToClipBottom;
    private float animateToClipBottomOrigin;
    private float animateToClipHorizontal;
    private float animateToClipTop;
    private float animateToClipTopOrigin;
    private boolean animateToRadius;
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private long animationStartTime;
    private float animationValue;
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout aspectRatioFrameLayout;
    /* access modifiers changed from: private */
    public Paint blackPaint = new Paint();
    private boolean canDragDown = true;
    /* access modifiers changed from: private */
    public ImageReceiver centerImage = new ImageReceiver();
    private float clipBottom;
    private float clipBottomOrigin;
    private float clipHorizontal;
    private float clipTop;
    private float clipTopOrigin;
    private boolean closeAfterAnimation;
    private long closeTime;
    /* access modifiers changed from: private */
    public boolean closeVideoAfterWatch;
    /* access modifiers changed from: private */
    public FrameLayoutDrawer containerView;
    /* access modifiers changed from: private */
    public int currentAccount;
    /* access modifiers changed from: private */
    public AnimatorSet currentActionBarAnimation;
    private long currentDialogId;
    /* access modifiers changed from: private */
    public MessageObject currentMessageObject;
    private PhotoViewer.PhotoViewerProvider currentProvider;
    private float[] currentRadii;
    private ImageReceiver.BitmapHolder currentThumb;
    private boolean disableShowCheck;
    private boolean discardTap;
    private boolean doubleTap;
    private float dragY;
    private boolean draggingDown;
    private GestureDetector gestureDetector;
    /* access modifiers changed from: private */
    public AnimatorSet imageMoveAnimation;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private boolean invalidCoords;
    private boolean isActionBarVisible = true;
    /* access modifiers changed from: private */
    public boolean isPhotoVisible;
    /* access modifiers changed from: private */
    public boolean isPlaying;
    private boolean isVideo;
    /* access modifiers changed from: private */
    public boolean isVisible;
    /* access modifiers changed from: private */
    public Object lastInsets;
    private float maxX;
    private float maxY;
    private float minX;
    private float minY;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private long openTime;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public Runnable photoAnimationEndRunnable;
    /* access modifiers changed from: private */
    public int photoAnimationInProgress;
    /* access modifiers changed from: private */
    public PhotoBackgroundDrawable photoBackgroundDrawable = new PhotoBackgroundDrawable(-16777216);
    private long photoTransitionAnimationStartTime;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale = 1.0f;
    private float pinchStartX;
    private float pinchStartY;
    /* access modifiers changed from: private */
    public int playerRetryPlayCount;
    private Path roundRectPath = new Path();
    /* access modifiers changed from: private */
    public float scale = 1.0f;
    private Scroller scroller;
    /* access modifiers changed from: private */
    public SecretDeleteTimer secretDeleteTimer;
    /* access modifiers changed from: private */
    public boolean textureUploaded;
    /* access modifiers changed from: private */
    public float translationX;
    /* access modifiers changed from: private */
    public float translationY;
    private boolean useOvershootForScale;
    private VelocityTracker velocityTracker;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;
    private TextureView videoTextureView;
    /* access modifiers changed from: private */
    public boolean videoWatchedOneTime;
    private boolean wasLightNavigationBar;
    private int wasNavigationBarColor;
    private WindowManager.LayoutParams windowLayoutParams;
    private FrameLayout windowView;
    private boolean zoomAnimation;
    private boolean zooming;

    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    static /* synthetic */ int access$1210(SecretMediaViewer secretMediaViewer) {
        int i = secretMediaViewer.playerRetryPlayCount;
        secretMediaViewer.playerRetryPlayCount = i - 1;
        return i;
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean unused = SecretMediaViewer.this.processTouchEvent(motionEvent);
            return true;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            SecretMediaViewer.this.onDraw(canvas);
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            return view != SecretMediaViewer.this.aspectRatioFrameLayout && super.drawChild(canvas, view, j);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            SecretMediaViewer.this.centerImage.onAttachedToWindow();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            SecretMediaViewer.this.centerImage.onDetachedFromWindow();
        }
    }

    private class SecretDeleteTimer extends FrameLayout {
        private Paint afterDeleteProgressPaint;
        private Paint circlePaint;
        private RectF deleteProgressRect = new RectF();
        private long destroyTime;
        private long destroyTtl;
        private Drawable drawable;
        private Paint particlePaint;
        private TimerParticles timerParticles = new TimerParticles();
        private boolean useVideoProgress;

        public SecretDeleteTimer(Context context) {
            super(context);
            setWillNotDraw(false);
            Paint paint = new Paint(1);
            this.particlePaint = paint;
            paint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
            this.particlePaint.setColor(-1644826);
            this.particlePaint.setStrokeCap(Paint.Cap.ROUND);
            this.particlePaint.setStyle(Paint.Style.STROKE);
            Paint paint2 = new Paint(1);
            this.afterDeleteProgressPaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            this.afterDeleteProgressPaint.setStrokeCap(Paint.Cap.ROUND);
            this.afterDeleteProgressPaint.setColor(-1644826);
            this.afterDeleteProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            Paint paint3 = new Paint(1);
            this.circlePaint = paint3;
            paint3.setColor(NUM);
            this.drawable = context.getResources().getDrawable(NUM);
        }

        /* access modifiers changed from: private */
        public void setDestroyTime(long j, long j2, boolean z) {
            this.destroyTime = j;
            this.destroyTtl = j2;
            this.useVideoProgress = z;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            int measuredHeight = (getMeasuredHeight() / 2) - (AndroidUtilities.dp(28.0f) / 2);
            this.deleteProgressRect.set((float) (getMeasuredWidth() - AndroidUtilities.dp(49.0f)), (float) measuredHeight, (float) (getMeasuredWidth() - AndroidUtilities.dp(21.0f)), (float) (measuredHeight + AndroidUtilities.dp(28.0f)));
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"DrawAllocation"})
        public void onDraw(Canvas canvas) {
            if (SecretMediaViewer.this.currentMessageObject != null && SecretMediaViewer.this.currentMessageObject.messageOwner.destroyTime != 0) {
                canvas.drawCircle((float) (getMeasuredWidth() - AndroidUtilities.dp(35.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(16.0f), this.circlePaint);
                float f = 1.0f;
                if (!this.useVideoProgress) {
                    f = ((float) Math.max(0, this.destroyTime - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance(SecretMediaViewer.this.currentAccount).getTimeDifference() * 1000))))) / (((float) this.destroyTtl) * 1000.0f);
                } else if (SecretMediaViewer.this.videoPlayer != null) {
                    long duration = SecretMediaViewer.this.videoPlayer.getDuration();
                    long currentPosition = SecretMediaViewer.this.videoPlayer.getCurrentPosition();
                    if (!(duration == -9223372036854775807L || currentPosition == -9223372036854775807L)) {
                        f = 1.0f - (((float) currentPosition) / ((float) duration));
                    }
                }
                int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(40.0f);
                int measuredHeight = ((getMeasuredHeight() - AndroidUtilities.dp(14.0f)) / 2) - AndroidUtilities.dp(0.5f);
                this.drawable.setBounds(measuredWidth, measuredHeight, AndroidUtilities.dp(10.0f) + measuredWidth, AndroidUtilities.dp(14.0f) + measuredHeight);
                this.drawable.draw(canvas);
                float f2 = -360.0f * f;
                canvas.drawArc(this.deleteProgressRect, -90.0f, f2, false, this.afterDeleteProgressPaint);
                this.timerParticles.draw(canvas, this.particlePaint, this.deleteProgressRect, f2, 1.0f);
                invalidate();
            }
        }
    }

    private class PhotoBackgroundDrawable extends ColorDrawable {
        /* access modifiers changed from: private */
        public Runnable drawRunnable;
        /* access modifiers changed from: private */
        public int frame;

        public PhotoBackgroundDrawable(int i) {
            super(i);
        }

        @Keep
        public void setAlpha(int i) {
            if (SecretMediaViewer.this.parentActivity instanceof LaunchActivity) {
                ((LaunchActivity) SecretMediaViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(!SecretMediaViewer.this.isPhotoVisible || i != 255);
            }
            super.setAlpha(i);
        }

        public void draw(Canvas canvas) {
            Runnable runnable;
            super.draw(canvas);
            if (getAlpha() != 0) {
                if (this.frame != 2 || (runnable = this.drawRunnable) == null) {
                    invalidateSelf();
                } else {
                    runnable.run();
                    this.drawRunnable = null;
                }
                this.frame++;
            }
        }
    }

    public static SecretMediaViewer getInstance() {
        SecretMediaViewer secretMediaViewer = Instance;
        if (secretMediaViewer == null) {
            synchronized (PhotoViewer.class) {
                secretMediaViewer = Instance;
                if (secretMediaViewer == null) {
                    secretMediaViewer = new SecretMediaViewer();
                    Instance = secretMediaViewer;
                }
            }
        }
        return secretMediaViewer;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.messagesDeleted) {
            if (objArr[2].booleanValue() || this.currentMessageObject == null || objArr[1].longValue() != 0 || !objArr[0].contains(Integer.valueOf(this.currentMessageObject.getId()))) {
                return;
            }
            if (this.isVideo && !this.videoWatchedOneTime) {
                this.closeVideoAfterWatch = true;
            } else if (!closePhoto(true, true)) {
                this.closeAfterAnimation = true;
            }
        } else if (i == NotificationCenter.didCreatedNewDeleteTask) {
            if (this.currentMessageObject != null && this.secretDeleteTimer != null && objArr[0].longValue() == this.currentDialogId) {
                SparseArray sparseArray = objArr[1];
                for (int i3 = 0; i3 < sparseArray.size(); i3++) {
                    int keyAt = sparseArray.keyAt(i3);
                    ArrayList arrayList = (ArrayList) sparseArray.get(keyAt);
                    for (int i4 = 0; i4 < arrayList.size(); i4++) {
                        if (((long) this.currentMessageObject.getId()) == ((long) ((Integer) arrayList.get(i4)).intValue())) {
                            this.currentMessageObject.messageOwner.destroyTime = keyAt;
                            this.secretDeleteTimer.invalidate();
                            return;
                        }
                    }
                }
            }
        } else if (i != NotificationCenter.updateMessageMedia || this.currentMessageObject.getId() != objArr[0].id) {
        } else {
            if (this.isVideo && !this.videoWatchedOneTime) {
                this.closeVideoAfterWatch = true;
            } else if (!closePhoto(true, true)) {
                this.closeAfterAnimation = true;
            }
        }
    }

    /* access modifiers changed from: private */
    public void preparePlayer(final File file) {
        if (this.parentActivity != null) {
            releasePlayer();
            if (this.videoTextureView == null) {
                AspectRatioFrameLayout aspectRatioFrameLayout2 = new AspectRatioFrameLayout(this.parentActivity);
                this.aspectRatioFrameLayout = aspectRatioFrameLayout2;
                aspectRatioFrameLayout2.setVisibility(4);
                this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                TextureView textureView = new TextureView(this.parentActivity);
                this.videoTextureView = textureView;
                textureView.setOpaque(false);
                this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
            }
            this.textureUploaded = false;
            this.videoCrossfadeStarted = false;
            TextureView textureView2 = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView2.setAlpha(0.0f);
            if (this.videoPlayer == null) {
                VideoPlayer videoPlayer2 = new VideoPlayer();
                this.videoPlayer = videoPlayer2;
                videoPlayer2.setTextureView(this.videoTextureView);
                this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                    public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                        VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
                    }

                    public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
                    }

                    public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }

                    public void onStateChanged(boolean z, int i) {
                        if (SecretMediaViewer.this.videoPlayer != null && SecretMediaViewer.this.currentMessageObject != null) {
                            if (i == 4 || i == 1) {
                                try {
                                    SecretMediaViewer.this.parentActivity.getWindow().clearFlags(128);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            } else {
                                try {
                                    SecretMediaViewer.this.parentActivity.getWindow().addFlags(128);
                                } catch (Exception e2) {
                                    FileLog.e((Throwable) e2);
                                }
                            }
                            if (i == 3 && SecretMediaViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                SecretMediaViewer.this.aspectRatioFrameLayout.setVisibility(0);
                            }
                            if (!SecretMediaViewer.this.videoPlayer.isPlaying() || i == 4) {
                                if (SecretMediaViewer.this.isPlaying) {
                                    boolean unused = SecretMediaViewer.this.isPlaying = false;
                                    if (i == 4) {
                                        boolean unused2 = SecretMediaViewer.this.videoWatchedOneTime = true;
                                        if (SecretMediaViewer.this.closeVideoAfterWatch) {
                                            SecretMediaViewer.this.closePhoto(true, true);
                                            return;
                                        }
                                        SecretMediaViewer.this.videoPlayer.seekTo(0);
                                        SecretMediaViewer.this.videoPlayer.play();
                                    }
                                }
                            } else if (!SecretMediaViewer.this.isPlaying) {
                                boolean unused3 = SecretMediaViewer.this.isPlaying = true;
                            }
                        }
                    }

                    public void onError(VideoPlayer videoPlayer, Exception exc) {
                        if (SecretMediaViewer.this.playerRetryPlayCount > 0) {
                            SecretMediaViewer.access$1210(SecretMediaViewer.this);
                            AndroidUtilities.runOnUIThread(new SecretMediaViewer$1$$ExternalSyntheticLambda0(this, file), 100);
                            return;
                        }
                        FileLog.e((Throwable) exc);
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onError$0(File file) {
                        SecretMediaViewer.this.preparePlayer(file);
                    }

                    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                        if (SecretMediaViewer.this.aspectRatioFrameLayout != null) {
                            if (!(i3 == 90 || i3 == 270)) {
                                int i4 = i2;
                                i2 = i;
                                i = i4;
                            }
                            SecretMediaViewer.this.aspectRatioFrameLayout.setAspectRatio(i == 0 ? 1.0f : (((float) i2) * f) / ((float) i), i3);
                        }
                    }

                    public void onRenderedFirstFrame() {
                        if (!SecretMediaViewer.this.textureUploaded) {
                            boolean unused = SecretMediaViewer.this.textureUploaded = true;
                            SecretMediaViewer.this.containerView.invalidate();
                        }
                    }
                });
            }
            this.videoPlayer.preparePlayer(Uri.fromFile(file), "other");
            this.videoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            this.playerRetryPlayCount = 0;
            videoPlayer2.releasePlayer(true);
            this.videoPlayer = null;
        }
        try {
            Activity activity = this.parentActivity;
            if (activity != null) {
                activity.getWindow().clearFlags(128);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AspectRatioFrameLayout aspectRatioFrameLayout2 = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout2 != null) {
            this.containerView.removeView(aspectRatioFrameLayout2);
            this.aspectRatioFrameLayout = null;
        }
        if (this.videoTextureView != null) {
            this.videoTextureView = null;
        }
        this.isPlaying = false;
    }

    public void setParentActivity(Activity activity) {
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.centerImage.setCurrentAccount(i);
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.scroller = new Scroller(activity);
            AnonymousClass2 r0 = new FrameLayout(activity) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int size = View.MeasureSpec.getSize(i);
                    int size2 = View.MeasureSpec.getSize(i2);
                    int i3 = Build.VERSION.SDK_INT;
                    if (i3 < 21 || SecretMediaViewer.this.lastInsets == null) {
                        int i4 = AndroidUtilities.displaySize.y;
                        if (size2 > i4) {
                            size2 = i4;
                        }
                    } else {
                        WindowInsets windowInsets = (WindowInsets) SecretMediaViewer.this.lastInsets;
                        if (AndroidUtilities.incorrectDisplaySizeFix) {
                            int i5 = AndroidUtilities.displaySize.y;
                            if (size2 > i5) {
                                size2 = i5;
                            }
                            size2 += AndroidUtilities.statusBarHeight;
                        }
                        size2 -= windowInsets.getSystemWindowInsetBottom();
                        size -= windowInsets.getSystemWindowInsetRight();
                    }
                    setMeasuredDimension(size, size2);
                    if (i3 >= 21 && SecretMediaViewer.this.lastInsets != null) {
                        size -= ((WindowInsets) SecretMediaViewer.this.lastInsets).getSystemWindowInsetLeft();
                    }
                    SecretMediaViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int systemWindowInsetLeft = (Build.VERSION.SDK_INT < 21 || SecretMediaViewer.this.lastInsets == null) ? 0 : ((WindowInsets) SecretMediaViewer.this.lastInsets).getSystemWindowInsetLeft() + 0;
                    SecretMediaViewer.this.containerView.layout(systemWindowInsetLeft, 0, SecretMediaViewer.this.containerView.getMeasuredWidth() + systemWindowInsetLeft, SecretMediaViewer.this.containerView.getMeasuredHeight());
                    if (z) {
                        if (SecretMediaViewer.this.imageMoveAnimation == null) {
                            float unused = SecretMediaViewer.this.scale = 1.0f;
                            float unused2 = SecretMediaViewer.this.translationX = 0.0f;
                            float unused3 = SecretMediaViewer.this.translationY = 0.0f;
                        }
                        SecretMediaViewer secretMediaViewer = SecretMediaViewer.this;
                        secretMediaViewer.updateMinMax(secretMediaViewer.scale);
                    }
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (Build.VERSION.SDK_INT >= 21 && SecretMediaViewer.this.isVisible && SecretMediaViewer.this.lastInsets != null) {
                        WindowInsets windowInsets = (WindowInsets) SecretMediaViewer.this.lastInsets;
                        if (SecretMediaViewer.this.photoAnimationInProgress != 0) {
                            SecretMediaViewer.this.blackPaint.setAlpha(SecretMediaViewer.this.photoBackgroundDrawable.getAlpha());
                        } else {
                            SecretMediaViewer.this.blackPaint.setAlpha(255);
                        }
                        canvas.drawRect(0.0f, (float) getMeasuredHeight(), (float) getMeasuredWidth(), (float) (getMeasuredHeight() + windowInsets.getSystemWindowInsetBottom()), SecretMediaViewer.this.blackPaint);
                    }
                }
            };
            this.windowView = r0;
            r0.setBackgroundDrawable(this.photoBackgroundDrawable);
            this.windowView.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            AnonymousClass3 r02 = new FrameLayoutDrawer(activity) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    if (SecretMediaViewer.this.secretDeleteTimer != null) {
                        int currentActionBarHeight = ((ActionBar.getCurrentActionBarHeight() - SecretMediaViewer.this.secretDeleteTimer.getMeasuredHeight()) / 2) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                        SecretMediaViewer.this.secretDeleteTimer.layout(SecretMediaViewer.this.secretDeleteTimer.getLeft(), currentActionBarHeight, SecretMediaViewer.this.secretDeleteTimer.getRight(), SecretMediaViewer.this.secretDeleteTimer.getMeasuredHeight() + currentActionBarHeight);
                    }
                }
            };
            this.containerView = r02;
            r02.setFocusable(false);
            this.windowView.addView(this.containerView);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.containerView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.gravity = 51;
            this.containerView.setLayoutParams(layoutParams);
            int i2 = Build.VERSION.SDK_INT;
            if (i2 >= 21) {
                this.containerView.setFitsSystemWindows(true);
                this.containerView.setOnApplyWindowInsetsListener(new SecretMediaViewer$$ExternalSyntheticLambda0(this));
                this.containerView.setSystemUiVisibility(1280);
            }
            GestureDetector gestureDetector2 = new GestureDetector(this.containerView.getContext(), this);
            this.gestureDetector = gestureDetector2;
            gestureDetector2.setOnDoubleTapListener(this);
            ActionBar actionBar2 = new ActionBar(activity);
            this.actionBar = actionBar2;
            actionBar2.setTitleColor(-1);
            this.actionBar.setSubtitleColor(-1);
            this.actionBar.setBackgroundColor(NUM);
            this.actionBar.setOccupyStatusBar(i2 >= 21);
            this.actionBar.setItemsBackgroundColor(NUM, false);
            this.actionBar.setBackButtonImage(NUM);
            this.actionBar.setTitleRightMargin(AndroidUtilities.dp(70.0f));
            this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                public void onItemClick(int i) {
                    if (i == -1) {
                        SecretMediaViewer.this.closePhoto(true, false);
                    }
                }
            });
            SecretDeleteTimer secretDeleteTimer2 = new SecretDeleteTimer(activity);
            this.secretDeleteTimer = secretDeleteTimer2;
            this.containerView.addView(secretDeleteTimer2, LayoutHelper.createFrame(119, 48.0f, 53, 0.0f, 0.0f, 0.0f, 0.0f));
            WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams2;
            layoutParams2.height = -1;
            layoutParams2.format = -3;
            layoutParams2.width = -1;
            layoutParams2.gravity = 48;
            layoutParams2.type = 99;
            if (i2 >= 21) {
                layoutParams2.flags = -NUM;
            } else {
                layoutParams2.flags = 8;
            }
            layoutParams2.flags |= 8192;
            this.centerImage.setParentView(this.containerView);
            this.centerImage.setForceCrossfade(true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ WindowInsets lambda$setParentActivity$0(View view, WindowInsets windowInsets) {
        WindowInsets windowInsets2 = (WindowInsets) this.lastInsets;
        this.lastInsets = windowInsets;
        if (windowInsets2 == null || !windowInsets2.toString().equals(windowInsets.toString())) {
            this.windowView.requestLayout();
        }
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return windowInsets.consumeSystemWindowInsets();
    }

    public void openMedia(MessageObject messageObject, PhotoViewer.PhotoViewerProvider photoViewerProvider, Runnable runnable) {
        PhotoViewer.PlaceProviderObject placeForPhoto;
        char c;
        int i;
        MessageObject messageObject2 = messageObject;
        PhotoViewer.PhotoViewerProvider photoViewerProvider2 = photoViewerProvider;
        if (this.parentActivity != null && messageObject2 != null && messageObject.needDrawBluredPreview() && photoViewerProvider2 != null && (placeForPhoto = photoViewerProvider2.getPlaceForPhoto(messageObject2, (TLRPC$FileLocation) null, 0, true)) != null) {
            this.currentProvider = photoViewerProvider2;
            this.openTime = System.currentTimeMillis();
            this.closeTime = 0;
            this.isActionBarVisible = true;
            this.isPhotoVisible = true;
            this.draggingDown = false;
            AspectRatioFrameLayout aspectRatioFrameLayout2 = this.aspectRatioFrameLayout;
            if (aspectRatioFrameLayout2 != null) {
                aspectRatioFrameLayout2.setVisibility(4);
            }
            releasePlayer();
            this.pinchStartDistance = 0.0f;
            this.pinchStartScale = 1.0f;
            this.pinchCenterX = 0.0f;
            this.pinchCenterY = 0.0f;
            this.pinchStartX = 0.0f;
            this.pinchStartY = 0.0f;
            this.moveStartX = 0.0f;
            this.moveStartY = 0.0f;
            this.zooming = false;
            this.moving = false;
            this.doubleTap = false;
            this.invalidCoords = false;
            this.canDragDown = true;
            updateMinMax(this.scale);
            this.photoBackgroundDrawable.setAlpha(0);
            this.containerView.setAlpha(1.0f);
            this.containerView.setVisibility(0);
            this.secretDeleteTimer.setAlpha(1.0f);
            this.isVideo = false;
            this.videoWatchedOneTime = false;
            this.closeVideoAfterWatch = false;
            this.disableShowCheck = true;
            this.centerImage.setManualAlphaAnimator(false);
            RectF drawRegion = placeForPhoto.imageReceiver.getDrawRegion();
            float width = drawRegion.width();
            float height = drawRegion.height();
            Point point = AndroidUtilities.displaySize;
            int i2 = point.x;
            int i3 = point.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
            this.scale = Math.max(width / ((float) i2), height / ((float) i3));
            int[] iArr = placeForPhoto.radius;
            if (iArr != null) {
                this.animateFromRadius = new int[iArr.length];
                int i4 = 0;
                while (true) {
                    int[] iArr2 = placeForPhoto.radius;
                    if (i4 >= iArr2.length) {
                        break;
                    }
                    this.animateFromRadius[i4] = iArr2[i4];
                    i4++;
                }
            } else {
                this.animateFromRadius = null;
            }
            float f = drawRegion.left;
            this.translationX = ((((float) placeForPhoto.viewX) + f) + (width / 2.0f)) - ((float) (i2 / 2));
            this.translationY = ((((float) placeForPhoto.viewY) + drawRegion.top) + (height / 2.0f)) - ((float) (i3 / 2));
            this.clipHorizontal = Math.abs(f - placeForPhoto.imageReceiver.getImageX());
            int abs = (int) Math.abs(drawRegion.top - placeForPhoto.imageReceiver.getImageY());
            int[] iArr3 = new int[2];
            placeForPhoto.parentView.getLocationInWindow(iArr3);
            int i5 = iArr3[1];
            int i6 = Build.VERSION.SDK_INT;
            float f2 = (((float) (i5 - (i6 >= 21 ? 0 : AndroidUtilities.statusBarHeight))) - (((float) placeForPhoto.viewY) + drawRegion.top)) + ((float) placeForPhoto.clipTopAddition);
            this.clipTop = f2;
            float f3 = (float) abs;
            this.clipTop = Math.max(0.0f, Math.max(f2, f3));
            float height2 = (((((float) placeForPhoto.viewY) + drawRegion.top) + ((float) ((int) height))) - ((float) ((iArr3[1] + placeForPhoto.parentView.getHeight()) - (i6 >= 21 ? 0 : AndroidUtilities.statusBarHeight)))) + ((float) placeForPhoto.clipBottomAddition);
            this.clipBottom = height2;
            this.clipBottom = Math.max(0.0f, Math.max(height2, f3));
            this.clipTopOrigin = 0.0f;
            this.clipTopOrigin = Math.max(0.0f, Math.max(0.0f, f3));
            this.clipBottomOrigin = 0.0f;
            this.clipBottomOrigin = Math.max(0.0f, Math.max(0.0f, f3));
            this.animationStartTime = System.currentTimeMillis();
            this.animateToX = 0.0f;
            this.animateToY = 0.0f;
            this.animateToClipBottom = 0.0f;
            this.animateToClipBottomOrigin = 0.0f;
            this.animateToClipHorizontal = 0.0f;
            this.animateToClipTop = 0.0f;
            this.animateToClipTopOrigin = 0.0f;
            this.animateToScale = 1.0f;
            this.animateToRadius = true;
            this.zoomAnimation = true;
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateMessageMedia);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didCreatedNewDeleteTask);
            this.currentDialogId = MessageObject.getPeerId(messageObject2.messageOwner.peer_id);
            toggleActionBar(true, false);
            this.currentMessageObject = messageObject2;
            TLRPC$Document document = messageObject.getDocument();
            ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.currentThumb = null;
            }
            this.currentThumb = placeForPhoto.imageReceiver.getThumbBitmapSafe();
            if (document == null) {
                i = 21;
                c = 4;
                this.actionBar.setTitle(LocaleController.getString("DisappearingPhoto", NUM));
                this.centerImage.setImage(ImageLocation.getForObject(FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, AndroidUtilities.getPhotoSize()), messageObject2.photoThumbsObject), (String) null, (Drawable) this.currentThumb != null ? new BitmapDrawable(this.currentThumb.bitmap) : null, -1, (String) null, (Object) messageObject, 2);
                SecretDeleteTimer secretDeleteTimer2 = this.secretDeleteTimer;
                TLRPC$Message tLRPC$Message = messageObject2.messageOwner;
                secretDeleteTimer2.setDestroyTime(((long) tLRPC$Message.destroyTime) * 1000, (long) tLRPC$Message.ttl, false);
            } else if (MessageObject.isGifDocument(document)) {
                this.actionBar.setTitle(LocaleController.getString("DisappearingGif", NUM));
                i = 21;
                c = 4;
                this.centerImage.setImage(ImageLocation.getForDocument(document), (String) null, (Drawable) this.currentThumb != null ? new BitmapDrawable(this.currentThumb.bitmap) : null, -1, (String) null, (Object) messageObject, 1);
                SecretDeleteTimer secretDeleteTimer3 = this.secretDeleteTimer;
                TLRPC$Message tLRPC$Message2 = messageObject2.messageOwner;
                secretDeleteTimer3.setDestroyTime(((long) tLRPC$Message2.destroyTime) * 1000, (long) tLRPC$Message2.ttl, false);
            } else {
                i = 21;
                c = 4;
                this.playerRetryPlayCount = 1;
                this.actionBar.setTitle(LocaleController.getString("DisappearingVideo", NUM));
                File file = new File(messageObject2.messageOwner.attachPath);
                if (file.exists()) {
                    preparePlayer(file);
                } else {
                    File pathToMessage = FileLoader.getInstance(this.currentAccount).getPathToMessage(messageObject2.messageOwner);
                    File file2 = new File(pathToMessage.getAbsolutePath() + ".enc");
                    if (file2.exists()) {
                        pathToMessage = file2;
                    }
                    preparePlayer(pathToMessage);
                }
                this.isVideo = true;
                this.centerImage.setImage((ImageLocation) null, (String) null, (Drawable) this.currentThumb != null ? new BitmapDrawable(this.currentThumb.bitmap) : null, -1, (String) null, (Object) messageObject, 2);
                if (((long) messageObject.getDuration()) * 1000 > (((long) messageObject2.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + (((long) ConnectionsManager.getInstance(this.currentAccount).getTimeDifference()) * 1000))) {
                    this.secretDeleteTimer.setDestroyTime(-1, -1, true);
                } else {
                    SecretDeleteTimer secretDeleteTimer4 = this.secretDeleteTimer;
                    TLRPC$Message tLRPC$Message3 = messageObject2.messageOwner;
                    secretDeleteTimer4.setDestroyTime(((long) tLRPC$Message3.destroyTime) * 1000, (long) tLRPC$Message3.ttl, false);
                }
            }
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
            this.secretDeleteTimer.invalidate();
            this.isVisible = true;
            Window window = this.parentActivity.getWindow();
            int i7 = Build.VERSION.SDK_INT;
            if (i7 >= i) {
                this.wasNavigationBarColor = window.getNavigationBarColor();
                this.wasLightNavigationBar = AndroidUtilities.getLightNavigationBar(window);
                AndroidUtilities.setLightNavigationBar(window, false);
                AndroidUtilities.setNavigationBarColor(window, -16777216);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.imageMoveAnimation = animatorSet;
            Animator[] animatorArr = new Animator[5];
            animatorArr[0] = ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f, 1.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.secretDeleteTimer, View.ALPHA, new float[]{0.0f, 1.0f});
            animatorArr[2] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0, 255});
            animatorArr[3] = ObjectAnimator.ofFloat(this.secretDeleteTimer, View.ALPHA, new float[]{0.0f, 1.0f});
            animatorArr[c] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f});
            animatorSet.playTogether(animatorArr);
            this.photoAnimationInProgress = 3;
            this.photoAnimationEndRunnable = new SecretMediaViewer$$ExternalSyntheticLambda2(this, runnable);
            this.imageMoveAnimation.setDuration(250);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (SecretMediaViewer.this.photoAnimationEndRunnable != null) {
                        SecretMediaViewer.this.photoAnimationEndRunnable.run();
                        Runnable unused = SecretMediaViewer.this.photoAnimationEndRunnable = null;
                    }
                }
            });
            this.photoTransitionAnimationStartTime = System.currentTimeMillis();
            if (i7 >= 18) {
                this.containerView.setLayerType(2, (Paint) null);
            }
            this.imageMoveAnimation.setInterpolator(new DecelerateInterpolator());
            int unused = this.photoBackgroundDrawable.frame = 0;
            Runnable unused2 = this.photoBackgroundDrawable.drawRunnable = new SecretMediaViewer$$ExternalSyntheticLambda4(this, placeForPhoto);
            this.imageMoveAnimation.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openMedia$1(Runnable runnable) {
        this.photoAnimationInProgress = 0;
        this.imageMoveAnimation = null;
        if (runnable != null) {
            runnable.run();
        }
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        if (frameLayoutDrawer != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                frameLayoutDrawer.setLayerType(0, (Paint) null);
            }
            this.containerView.invalidate();
            if (this.closeAfterAnimation) {
                closePhoto(true, true);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openMedia$2(PhotoViewer.PlaceProviderObject placeProviderObject) {
        this.disableShowCheck = false;
        placeProviderObject.imageReceiver.setVisible(false, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000a, code lost:
        r0 = r1.currentMessageObject;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isShowingImage(org.telegram.messenger.MessageObject r2) {
        /*
            r1 = this;
            boolean r0 = r1.isVisible
            if (r0 == 0) goto L_0x001a
            boolean r0 = r1.disableShowCheck
            if (r0 != 0) goto L_0x001a
            if (r2 == 0) goto L_0x001a
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            if (r0 == 0) goto L_0x001a
            int r0 = r0.getId()
            int r2 = r2.getId()
            if (r0 != r2) goto L_0x001a
            r2 = 1
            goto L_0x001b
        L_0x001a:
            r2 = 0
        L_0x001b:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretMediaViewer.isShowingImage(org.telegram.messenger.MessageObject):boolean");
    }

    private void toggleActionBar(boolean z, boolean z2) {
        if (z) {
            this.actionBar.setVisibility(0);
        }
        this.actionBar.setEnabled(z);
        this.isActionBarVisible = z;
        float f = 1.0f;
        if (z2) {
            ArrayList arrayList = new ArrayList();
            ActionBar actionBar2 = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar2, property, fArr));
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentActionBarAnimation = animatorSet;
            animatorSet.playTogether(arrayList);
            if (!z) {
                this.currentActionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (SecretMediaViewer.this.currentActionBarAnimation != null && SecretMediaViewer.this.currentActionBarAnimation.equals(animator)) {
                            SecretMediaViewer.this.actionBar.setVisibility(8);
                            AnimatorSet unused = SecretMediaViewer.this.currentActionBarAnimation = null;
                        }
                    }
                });
            }
            this.currentActionBarAnimation.setDuration(200);
            this.currentActionBarAnimation.start();
            return;
        }
        ActionBar actionBar3 = this.actionBar;
        if (!z) {
            f = 0.0f;
        }
        actionBar3.setAlpha(f);
        if (!z) {
            this.actionBar.setVisibility(8);
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void destroyPhotoViewer() {
        FrameLayout frameLayout;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        this.isVisible = false;
        this.currentProvider = null;
        ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        releasePlayer();
        if (!(this.parentActivity == null || (frameLayout = this.windowView) == null)) {
            try {
                if (frameLayout.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        Instance = null;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x0315  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0245  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0248  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x02d4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r21) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            boolean r2 = r0.isPhotoVisible
            if (r2 != 0) goto L_0x0009
            return
        L_0x0009:
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            r4 = 0
            r5 = 0
            r6 = 1065353216(0x3var_, float:1.0)
            if (r2 == 0) goto L_0x00e7
            org.telegram.ui.Components.Scroller r2 = r0.scroller
            boolean r2 = r2.isFinished()
            if (r2 != 0) goto L_0x001e
            org.telegram.ui.Components.Scroller r2 = r0.scroller
            r2.abortAnimation()
        L_0x001e:
            boolean r2 = r0.useOvershootForScale
            if (r2 == 0) goto L_0x0087
            float r2 = r0.animationValue
            r7 = 1063675494(0x3var_, float:0.9)
            int r8 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r8 >= 0) goto L_0x003a
            float r2 = r2 / r7
            float r7 = r0.scale
            float r8 = r0.animateToScale
            r9 = 1065520988(0x3var_f5c, float:1.02)
            float r8 = r8 * r9
            float r8 = r8 - r7
            float r8 = r8 * r2
            float r7 = r7 + r8
            goto L_0x004e
        L_0x003a:
            float r8 = r0.animateToScale
            r9 = 1017370368(0x3ca3d700, float:0.01999998)
            float r9 = r9 * r8
            float r2 = r2 - r7
            r7 = 1036831952(0x3dccccd0, float:0.NUM)
            float r2 = r2 / r7
            float r2 = r6 - r2
            float r9 = r9 * r2
            float r7 = r8 + r9
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x004e:
            float r8 = r0.translationY
            float r9 = r0.animateToY
            float r9 = r9 - r8
            float r9 = r9 * r2
            float r8 = r8 + r9
            float r9 = r0.translationX
            float r10 = r0.animateToX
            float r10 = r10 - r9
            float r10 = r10 * r2
            float r9 = r9 + r10
            float r10 = r0.clipTop
            float r11 = r0.animateToClipTop
            float r11 = r11 - r10
            float r11 = r11 * r2
            float r10 = r10 + r11
            float r11 = r0.clipBottom
            float r12 = r0.animateToClipBottom
            float r12 = r12 - r11
            float r12 = r12 * r2
            float r11 = r11 + r12
            float r12 = r0.clipTopOrigin
            float r13 = r0.animateToClipTopOrigin
            float r13 = r13 - r12
            float r13 = r13 * r2
            float r12 = r12 + r13
            float r13 = r0.clipBottomOrigin
            float r14 = r0.animateToClipBottomOrigin
            float r14 = r14 - r13
            float r14 = r14 * r2
            float r13 = r13 + r14
            float r14 = r0.clipHorizontal
            float r15 = r0.animateToClipHorizontal
            float r15 = r15 - r14
            float r15 = r15 * r2
            float r14 = r14 + r15
            goto L_0x00ca
        L_0x0087:
            float r2 = r0.scale
            float r7 = r0.animateToScale
            float r7 = r7 - r2
            float r8 = r0.animationValue
            float r7 = r7 * r8
            float r7 = r7 + r2
            float r2 = r0.translationY
            float r9 = r0.animateToY
            float r9 = r9 - r2
            float r9 = r9 * r8
            float r2 = r2 + r9
            float r9 = r0.translationX
            float r10 = r0.animateToX
            float r10 = r10 - r9
            float r10 = r10 * r8
            float r9 = r9 + r10
            float r10 = r0.clipTop
            float r11 = r0.animateToClipTop
            float r11 = r11 - r10
            float r11 = r11 * r8
            float r10 = r10 + r11
            float r11 = r0.clipBottom
            float r12 = r0.animateToClipBottom
            float r12 = r12 - r11
            float r12 = r12 * r8
            float r11 = r11 + r12
            float r12 = r0.clipTopOrigin
            float r13 = r0.animateToClipTopOrigin
            float r13 = r13 - r12
            float r13 = r13 * r8
            float r12 = r12 + r13
            float r13 = r0.clipBottomOrigin
            float r14 = r0.animateToClipBottomOrigin
            float r14 = r14 - r13
            float r14 = r14 * r8
            float r13 = r13 + r14
            float r14 = r0.clipHorizontal
            float r15 = r0.animateToClipHorizontal
            float r15 = r15 - r14
            float r15 = r15 * r8
            float r14 = r14 + r15
            r8 = r2
        L_0x00ca:
            float r2 = r0.animateToScale
            int r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x00de
            float r2 = r0.scale
            int r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x00de
            float r2 = r0.translationX
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x00de
            r2 = r8
            goto L_0x00e0
        L_0x00de:
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x00e0:
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r15 = r0.containerView
            r15.invalidate()
            goto L_0x018b
        L_0x00e7:
            long r7 = r0.animationStartTime
            r9 = 0
            int r2 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0118
            float r2 = r0.animateToX
            r0.translationX = r2
            float r2 = r0.animateToY
            r0.translationY = r2
            float r2 = r0.animateToClipBottom
            r0.clipBottom = r2
            float r2 = r0.animateToClipTop
            r0.clipTop = r2
            float r2 = r0.animateToClipTopOrigin
            r0.clipTopOrigin = r2
            float r2 = r0.animateToClipBottomOrigin
            r0.clipBottomOrigin = r2
            float r2 = r0.animateToClipHorizontal
            r0.clipHorizontal = r2
            float r2 = r0.animateToScale
            r0.scale = r2
            r0.animationStartTime = r9
            r0.updateMinMax(r2)
            r0.zoomAnimation = r5
            r0.useOvershootForScale = r5
        L_0x0118:
            org.telegram.ui.Components.Scroller r2 = r0.scroller
            boolean r2 = r2.isFinished()
            if (r2 != 0) goto L_0x0173
            org.telegram.ui.Components.Scroller r2 = r0.scroller
            boolean r2 = r2.computeScrollOffset()
            if (r2 == 0) goto L_0x0173
            org.telegram.ui.Components.Scroller r2 = r0.scroller
            int r2 = r2.getStartX()
            float r2 = (float) r2
            float r7 = r0.maxX
            int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r2 >= 0) goto L_0x014b
            org.telegram.ui.Components.Scroller r2 = r0.scroller
            int r2 = r2.getStartX()
            float r2 = (float) r2
            float r7 = r0.minX
            int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r2 <= 0) goto L_0x014b
            org.telegram.ui.Components.Scroller r2 = r0.scroller
            int r2 = r2.getCurrX()
            float r2 = (float) r2
            r0.translationX = r2
        L_0x014b:
            org.telegram.ui.Components.Scroller r2 = r0.scroller
            int r2 = r2.getStartY()
            float r2 = (float) r2
            float r7 = r0.maxY
            int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r2 >= 0) goto L_0x016e
            org.telegram.ui.Components.Scroller r2 = r0.scroller
            int r2 = r2.getStartY()
            float r2 = (float) r2
            float r7 = r0.minY
            int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r2 <= 0) goto L_0x016e
            org.telegram.ui.Components.Scroller r2 = r0.scroller
            int r2 = r2.getCurrY()
            float r2 = (float) r2
            r0.translationY = r2
        L_0x016e:
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r2 = r0.containerView
            r2.invalidate()
        L_0x0173:
            float r7 = r0.scale
            float r8 = r0.translationY
            float r9 = r0.translationX
            float r10 = r0.clipTop
            float r11 = r0.clipBottom
            float r12 = r0.clipTopOrigin
            float r13 = r0.clipBottomOrigin
            float r14 = r0.clipHorizontal
            boolean r2 = r0.moving
            if (r2 != 0) goto L_0x0189
            r2 = r8
            goto L_0x018b
        L_0x0189:
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x018b:
            int[] r15 = r0.animateFromRadius
            if (r15 == 0) goto L_0x01d1
            float[] r15 = r0.currentRadii
            r5 = 8
            if (r15 != 0) goto L_0x0199
            float[] r15 = new float[r5]
            r0.currentRadii = r15
        L_0x0199:
            boolean r15 = r0.animateToRadius
            if (r15 == 0) goto L_0x01a0
            float r15 = r0.animationValue
            goto L_0x01a4
        L_0x01a0:
            float r15 = r0.animationValue
            float r15 = r6 - r15
        L_0x01a4:
            r3 = 0
            r17 = 1
        L_0x01a7:
            if (r3 >= r5) goto L_0x01d3
            float[] r5 = r0.currentRadii
            int r18 = r3 + 1
            int[] r6 = r0.animateFromRadius
            int r19 = r3 / 2
            r6 = r6[r19]
            float r6 = (float) r6
            r19 = 1073741824(0x40000000, float:2.0)
            float r6 = r6 * r19
            float r6 = org.telegram.messenger.AndroidUtilities.lerp((float) r6, (float) r4, (float) r15)
            r5[r18] = r6
            r5[r3] = r6
            float[] r5 = r0.currentRadii
            r5 = r5[r3]
            int r5 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x01ca
            r17 = 0
        L_0x01ca:
            int r3 = r3 + 2
            r5 = 8
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x01a7
        L_0x01d1:
            r17 = 1
        L_0x01d3:
            int r3 = r0.photoAnimationInProgress
            r5 = 3
            if (r3 == r5) goto L_0x0238
            float r3 = r0.scale
            r5 = 1065353216(0x3var_, float:1.0)
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 != 0) goto L_0x0210
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 == 0) goto L_0x0210
            boolean r3 = r0.zoomAnimation
            if (r3 != 0) goto L_0x0210
            int r3 = r20.getContainerViewHeight()
            float r3 = (float) r3
            r5 = 1082130432(0x40800000, float:4.0)
            float r3 = r3 / r5
            org.telegram.ui.SecretMediaViewer$PhotoBackgroundDrawable r5 = r0.photoBackgroundDrawable
            r6 = 1123942400(0x42fe0000, float:127.0)
            r15 = 1132396544(0x437var_, float:255.0)
            float r2 = java.lang.Math.abs(r2)
            float r2 = java.lang.Math.min(r2, r3)
            float r2 = r2 / r3
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = r3 - r2
            float r2 = r2 * r15
            float r2 = java.lang.Math.max(r6, r2)
            int r2 = (int) r2
            r5.setAlpha(r2)
            goto L_0x0217
        L_0x0210:
            org.telegram.ui.SecretMediaViewer$PhotoBackgroundDrawable r2 = r0.photoBackgroundDrawable
            r3 = 255(0xff, float:3.57E-43)
            r2.setAlpha(r3)
        L_0x0217:
            boolean r2 = r0.zoomAnimation
            if (r2 != 0) goto L_0x0238
            float r2 = r0.maxX
            int r3 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x0238
            float r9 = r9 - r2
            int r2 = r21.getWidth()
            float r2 = (float) r2
            float r9 = r9 / r2
            r2 = 1065353216(0x3var_, float:1.0)
            float r3 = java.lang.Math.min(r2, r9)
            r5 = 1050253722(0x3e99999a, float:0.3)
            float r5 = r5 * r3
            float r3 = r2 - r3
            float r9 = r0.maxX
            goto L_0x023b
        L_0x0238:
            r3 = 1065353216(0x3var_, float:1.0)
            r5 = 0
        L_0x023b:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r2 = r0.aspectRatioFrameLayout
            if (r2 == 0) goto L_0x0248
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x0248
            r16 = 1
            goto L_0x024a
        L_0x0248:
            r16 = 0
        L_0x024a:
            r21.save()
            float r7 = r7 - r5
            int r2 = r20.getContainerViewWidth()
            int r2 = r2 / 2
            float r2 = (float) r2
            float r2 = r2 + r9
            int r5 = r20.getContainerViewHeight()
            int r5 = r5 / 2
            float r5 = (float) r5
            float r5 = r5 + r8
            r1.translate(r2, r5)
            r1.scale(r7, r7)
            org.telegram.messenger.ImageReceiver r2 = r0.centerImage
            int r2 = r2.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r5 = r0.centerImage
            int r5 = r5.getBitmapHeight()
            if (r16 == 0) goto L_0x02a0
            boolean r6 = r0.textureUploaded
            if (r6 == 0) goto L_0x02a0
            float r6 = (float) r2
            float r8 = (float) r5
            float r6 = r6 / r8
            android.view.TextureView r8 = r0.videoTextureView
            int r8 = r8.getMeasuredWidth()
            float r8 = (float) r8
            android.view.TextureView r9 = r0.videoTextureView
            int r9 = r9.getMeasuredHeight()
            float r9 = (float) r9
            float r8 = r8 / r9
            float r6 = r6 - r8
            float r6 = java.lang.Math.abs(r6)
            r8 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 <= 0) goto L_0x02a0
            android.view.TextureView r2 = r0.videoTextureView
            int r2 = r2.getMeasuredWidth()
            android.view.TextureView r5 = r0.videoTextureView
            int r5 = r5.getMeasuredHeight()
        L_0x02a0:
            int r6 = r20.getContainerViewHeight()
            float r6 = (float) r6
            float r5 = (float) r5
            float r6 = r6 / r5
            int r8 = r20.getContainerViewWidth()
            float r8 = (float) r8
            float r2 = (float) r2
            float r8 = r8 / r2
            float r6 = java.lang.Math.min(r6, r8)
            float r2 = r2 * r6
            int r2 = (int) r2
            float r5 = r5 * r6
            int r5 = (int) r5
            int r6 = -r2
            int r6 = r6 / 2
            float r6 = (float) r6
            float r14 = r14 / r7
            float r8 = r6 + r14
            int r9 = -r5
            int r9 = r9 / 2
            float r9 = (float) r9
            float r10 = r10 / r7
            float r10 = r10 + r9
            int r15 = r2 / 2
            float r15 = (float) r15
            float r15 = r15 - r14
            int r14 = r5 / 2
            float r14 = (float) r14
            float r11 = r11 / r7
            float r11 = r14 - r11
            r1.clipRect(r8, r10, r15, r11)
            if (r17 != 0) goto L_0x02f0
            android.graphics.Path r10 = r0.roundRectPath
            r10.reset()
            android.graphics.RectF r10 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r12 = r12 / r7
            float r12 = r12 + r9
            float r13 = r13 / r7
            float r14 = r14 - r13
            r10.set(r8, r12, r15, r14)
            android.graphics.Path r7 = r0.roundRectPath
            float[] r8 = r0.currentRadii
            android.graphics.Path$Direction r11 = android.graphics.Path.Direction.CW
            r7.addRoundRect(r10, r8, r11)
            android.graphics.Path r7 = r0.roundRectPath
            r1.clipPath(r7)
        L_0x02f0:
            if (r16 == 0) goto L_0x0302
            boolean r7 = r0.textureUploaded
            if (r7 == 0) goto L_0x0302
            boolean r7 = r0.videoCrossfadeStarted
            if (r7 == 0) goto L_0x0302
            float r7 = r0.videoCrossfadeAlpha
            r8 = 1065353216(0x3var_, float:1.0)
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 == 0) goto L_0x0313
        L_0x0302:
            org.telegram.messenger.ImageReceiver r7 = r0.centerImage
            r7.setAlpha(r3)
            org.telegram.messenger.ImageReceiver r7 = r0.centerImage
            float r2 = (float) r2
            float r5 = (float) r5
            r7.setImageCoords(r6, r9, r2, r5)
            org.telegram.messenger.ImageReceiver r2 = r0.centerImage
            r2.draw(r1)
        L_0x0313:
            if (r16 == 0) goto L_0x0367
            boolean r2 = r0.videoCrossfadeStarted
            if (r2 != 0) goto L_0x0328
            boolean r2 = r0.textureUploaded
            if (r2 == 0) goto L_0x0328
            r2 = 1
            r0.videoCrossfadeStarted = r2
            r0.videoCrossfadeAlpha = r4
            long r4 = java.lang.System.currentTimeMillis()
            r0.videoCrossfadeAlphaLastTime = r4
        L_0x0328:
            r1.translate(r6, r9)
            android.view.TextureView r2 = r0.videoTextureView
            float r4 = r0.videoCrossfadeAlpha
            float r3 = r3 * r4
            r2.setAlpha(r3)
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r2 = r0.aspectRatioFrameLayout
            r2.draw(r1)
            boolean r2 = r0.videoCrossfadeStarted
            if (r2 == 0) goto L_0x0367
            float r2 = r0.videoCrossfadeAlpha
            r3 = 1065353216(0x3var_, float:1.0)
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x0367
            long r2 = java.lang.System.currentTimeMillis()
            long r4 = r0.videoCrossfadeAlphaLastTime
            long r4 = r2 - r4
            r0.videoCrossfadeAlphaLastTime = r2
            float r2 = r0.videoCrossfadeAlpha
            float r3 = (float) r4
            r4 = 1128792064(0x43480000, float:200.0)
            float r3 = r3 / r4
            float r2 = r2 + r3
            r0.videoCrossfadeAlpha = r2
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r2 = r0.containerView
            r2.invalidate()
            float r2 = r0.videoCrossfadeAlpha
            r3 = 1065353216(0x3var_, float:1.0)
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0367
            r0.videoCrossfadeAlpha = r3
        L_0x0367:
            r21.restore()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretMediaViewer.onDraw(android.graphics.Canvas):void");
    }

    @Keep
    public float getVideoCrossfadeAlpha() {
        return this.videoCrossfadeAlpha;
    }

    @Keep
    public void setVideoCrossfadeAlpha(float f) {
        this.videoCrossfadeAlpha = f;
        this.containerView.invalidate();
    }

    private boolean checkPhotoAnimation() {
        if (this.photoAnimationInProgress != 0 && Math.abs(this.photoTransitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            Runnable runnable = this.photoAnimationEndRunnable;
            if (runnable != null) {
                runnable.run();
                this.photoAnimationEndRunnable = null;
            }
            this.photoAnimationInProgress = 0;
        }
        if (this.photoAnimationInProgress != 0) {
            return true;
        }
        return false;
    }

    public long getOpenTime() {
        return this.openTime;
    }

    public long getCloseTime() {
        return this.closeTime;
    }

    public MessageObject getCurrentMessageObject() {
        return this.currentMessageObject;
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x007a  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0277  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean closePhoto(boolean r19, boolean r20) {
        /*
            r18 = this;
            r0 = r18
            android.app.Activity r1 = r0.parentActivity
            r2 = 0
            if (r1 == 0) goto L_0x02e9
            boolean r1 = r0.isPhotoVisible
            if (r1 == 0) goto L_0x02e9
            boolean r1 = r18.checkPhotoAnimation()
            if (r1 == 0) goto L_0x0013
            goto L_0x02e9
        L_0x0013:
            android.app.Activity r1 = r0.parentActivity
            if (r1 == 0) goto L_0x0025
            android.view.Window r1 = r1.getWindow()
            boolean r3 = r0.wasLightNavigationBar
            org.telegram.messenger.AndroidUtilities.setLightNavigationBar((android.view.Window) r1, (boolean) r3)
            int r3 = r0.wasNavigationBarColor
            org.telegram.messenger.AndroidUtilities.setNavigationBarColor(r1, r3)
        L_0x0025:
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r3 = org.telegram.messenger.NotificationCenter.messagesDeleted
            r1.removeObserver(r0, r3)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r3 = org.telegram.messenger.NotificationCenter.updateMessageMedia
            r1.removeObserver(r0, r3)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r3 = org.telegram.messenger.NotificationCenter.didCreatedNewDeleteTask
            r1.removeObserver(r0, r3)
            r0.isActionBarVisible = r2
            android.view.VelocityTracker r1 = r0.velocityTracker
            r3 = 0
            if (r1 == 0) goto L_0x0052
            r1.recycle()
            r0.velocityTracker = r3
        L_0x0052:
            long r4 = java.lang.System.currentTimeMillis()
            r0.closeTime = r4
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r1 = r0.currentProvider
            r4 = 1
            if (r1 == 0) goto L_0x0075
            org.telegram.messenger.MessageObject r5 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            org.telegram.tgnet.TLRPC$Photo r7 = r6.photo
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r7 != 0) goto L_0x0075
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r6 == 0) goto L_0x0070
            goto L_0x0075
        L_0x0070:
            org.telegram.ui.PhotoViewer$PlaceProviderObject r1 = r1.getPlaceForPhoto(r5, r3, r2, r4)
            goto L_0x0076
        L_0x0075:
            r1 = r3
        L_0x0076:
            org.telegram.ui.Components.VideoPlayer r5 = r0.videoPlayer
            if (r5 == 0) goto L_0x007d
            r5.pause()
        L_0x007d:
            r7 = 3
            r8 = 2
            r9 = 0
            if (r19 == 0) goto L_0x0277
            r0.photoAnimationInProgress = r7
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r10 = r0.containerView
            r10.invalidate()
            android.animation.AnimatorSet r10 = new android.animation.AnimatorSet
            r10.<init>()
            r0.imageMoveAnimation = r10
            r10 = 21
            if (r1 == 0) goto L_0x017e
            org.telegram.messenger.ImageReceiver r11 = r1.imageReceiver
            android.graphics.Bitmap r11 = r11.getThumbBitmap()
            if (r11 == 0) goto L_0x017e
            if (r20 != 0) goto L_0x017e
            org.telegram.messenger.ImageReceiver r11 = r1.imageReceiver
            r11.setVisible(r2, r4)
            org.telegram.messenger.ImageReceiver r11 = r1.imageReceiver
            android.graphics.RectF r11 = r11.getDrawRegion()
            float r12 = r11.right
            float r13 = r11.left
            float r12 = r12 - r13
            float r13 = r11.bottom
            float r14 = r11.top
            float r13 = r13 - r14
            android.graphics.Point r14 = org.telegram.messenger.AndroidUtilities.displaySize
            int r15 = r14.x
            int r14 = r14.y
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r10) goto L_0x00c0
            int r16 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x00c2
        L_0x00c0:
            r16 = 0
        L_0x00c2:
            int r14 = r14 + r16
            float r5 = (float) r15
            float r5 = r12 / r5
            float r6 = (float) r14
            float r6 = r13 / r6
            float r5 = java.lang.Math.max(r5, r6)
            r0.animateToScale = r5
            int r5 = r1.viewX
            float r5 = (float) r5
            float r6 = r11.left
            float r5 = r5 + r6
            r17 = 1073741824(0x40000000, float:2.0)
            float r12 = r12 / r17
            float r5 = r5 + r12
            int r15 = r15 / r8
            float r12 = (float) r15
            float r5 = r5 - r12
            r0.animateToX = r5
            int r5 = r1.viewY
            float r5 = (float) r5
            float r12 = r11.top
            float r5 = r5 + r12
            float r12 = r13 / r17
            float r5 = r5 + r12
            int r14 = r14 / r8
            float r12 = (float) r14
            float r5 = r5 - r12
            r0.animateToY = r5
            org.telegram.messenger.ImageReceiver r5 = r1.imageReceiver
            float r5 = r5.getImageX()
            float r6 = r6 - r5
            float r5 = java.lang.Math.abs(r6)
            r0.animateToClipHorizontal = r5
            float r5 = r11.top
            org.telegram.messenger.ImageReceiver r6 = r1.imageReceiver
            float r6 = r6.getImageY()
            float r5 = r5 - r6
            float r5 = java.lang.Math.abs(r5)
            int r5 = (int) r5
            int[] r6 = new int[r8]
            android.view.View r12 = r1.parentView
            r12.getLocationInWindow(r6)
            r12 = r6[r4]
            if (r3 < r10) goto L_0x0116
            r14 = 0
            goto L_0x0118
        L_0x0116:
            int r14 = org.telegram.messenger.AndroidUtilities.statusBarHeight
        L_0x0118:
            int r12 = r12 - r14
            float r12 = (float) r12
            int r14 = r1.viewY
            float r14 = (float) r14
            float r15 = r11.top
            float r14 = r14 + r15
            float r12 = r12 - r14
            int r14 = r1.clipTopAddition
            float r14 = (float) r14
            float r12 = r12 + r14
            r0.animateToClipTop = r12
            float r5 = (float) r5
            float r12 = java.lang.Math.max(r12, r5)
            float r12 = java.lang.Math.max(r9, r12)
            r0.animateToClipTop = r12
            int r12 = r1.viewY
            float r12 = (float) r12
            float r11 = r11.top
            float r12 = r12 + r11
            int r11 = (int) r13
            float r11 = (float) r11
            float r12 = r12 + r11
            r6 = r6[r4]
            android.view.View r11 = r1.parentView
            int r11 = r11.getHeight()
            int r6 = r6 + r11
            if (r3 < r10) goto L_0x0148
            r3 = 0
            goto L_0x014a
        L_0x0148:
            int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
        L_0x014a:
            int r6 = r6 - r3
            float r3 = (float) r6
            float r12 = r12 - r3
            int r3 = r1.clipBottomAddition
            float r3 = (float) r3
            float r12 = r12 + r3
            r0.animateToClipBottom = r12
            float r3 = java.lang.Math.max(r12, r5)
            float r3 = java.lang.Math.max(r9, r3)
            r0.animateToClipBottom = r3
            r0.animateToClipTopOrigin = r9
            float r3 = java.lang.Math.max(r9, r5)
            float r3 = java.lang.Math.max(r9, r3)
            r0.animateToClipTopOrigin = r3
            r0.animateToClipBottomOrigin = r9
            float r3 = java.lang.Math.max(r9, r5)
            float r3 = java.lang.Math.max(r9, r3)
            r0.animateToClipBottomOrigin = r3
            long r5 = java.lang.System.currentTimeMillis()
            r0.animationStartTime = r5
            r0.zoomAnimation = r4
            goto L_0x0196
        L_0x017e:
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r3.y
            int r5 = android.os.Build.VERSION.SDK_INT
            if (r5 < r10) goto L_0x0189
            int r5 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x018a
        L_0x0189:
            r5 = 0
        L_0x018a:
            int r3 = r3 + r5
            float r5 = r0.translationY
            int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r5 < 0) goto L_0x0192
            goto L_0x0193
        L_0x0192:
            int r3 = -r3
        L_0x0193:
            float r3 = (float) r3
            r0.animateToY = r3
        L_0x0196:
            r0.animateToRadius = r2
            boolean r3 = r0.isVideo
            java.lang.String r5 = "animationValue"
            r6 = 5
            if (r3 == 0) goto L_0x01ed
            r0.videoCrossfadeStarted = r2
            r0.textureUploaded = r2
            android.animation.AnimatorSet r3 = r0.imageMoveAnimation
            android.animation.Animator[] r6 = new android.animation.Animator[r6]
            org.telegram.ui.SecretMediaViewer$PhotoBackgroundDrawable r10 = r0.photoBackgroundDrawable
            android.util.Property<android.graphics.drawable.ColorDrawable, java.lang.Integer> r11 = org.telegram.ui.Components.AnimationProperties.COLOR_DRAWABLE_ALPHA
            int[] r12 = new int[r4]
            r12[r2] = r2
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofInt(r10, r11, r12)
            r6[r2] = r10
            float[] r10 = new float[r8]
            r10 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r0, r5, r10)
            r6[r4] = r5
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r4]
            r11[r2] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r10, r11)
            r6[r8] = r5
            org.telegram.ui.SecretMediaViewer$SecretDeleteTimer r5 = r0.secretDeleteTimer
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r4]
            r11[r2] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r10, r11)
            r6[r7] = r5
            float[] r5 = new float[r4]
            r5[r2] = r9
            java.lang.String r2 = "videoCrossfadeAlpha"
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r2, r5)
            r5 = 4
            r6[r5] = r2
            r3.playTogether(r6)
            goto L_0x023d
        L_0x01ed:
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            r3.setManualAlphaAnimator(r4)
            android.animation.AnimatorSet r3 = r0.imageMoveAnimation
            android.animation.Animator[] r6 = new android.animation.Animator[r6]
            org.telegram.ui.SecretMediaViewer$PhotoBackgroundDrawable r10 = r0.photoBackgroundDrawable
            android.util.Property<android.graphics.drawable.ColorDrawable, java.lang.Integer> r11 = org.telegram.ui.Components.AnimationProperties.COLOR_DRAWABLE_ALPHA
            int[] r12 = new int[r4]
            r12[r2] = r2
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofInt(r10, r11, r12)
            r6[r2] = r10
            float[] r10 = new float[r8]
            r10 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r0, r5, r10)
            r6[r4] = r5
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r4]
            r11[r2] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r10, r11)
            r6[r8] = r5
            org.telegram.ui.SecretMediaViewer$SecretDeleteTimer r5 = r0.secretDeleteTimer
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r4]
            r11[r2] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r10, r11)
            r6[r7] = r5
            org.telegram.messenger.ImageReceiver r5 = r0.centerImage
            float[] r7 = new float[r4]
            r7[r2] = r9
            java.lang.String r2 = "currentAlpha"
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r5, r2, r7)
            r5 = 4
            r6[r5] = r2
            r3.playTogether(r6)
        L_0x023d:
            org.telegram.ui.SecretMediaViewer$$ExternalSyntheticLambda5 r2 = new org.telegram.ui.SecretMediaViewer$$ExternalSyntheticLambda5
            r2.<init>(r0, r1)
            r0.photoAnimationEndRunnable = r2
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            android.view.animation.DecelerateInterpolator r3 = new android.view.animation.DecelerateInterpolator
            r3.<init>()
            r2.setInterpolator(r3)
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            r5 = 250(0xfa, double:1.235E-321)
            r2.setDuration(r5)
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            org.telegram.ui.SecretMediaViewer$7 r3 = new org.telegram.ui.SecretMediaViewer$7
            r3.<init>(r1)
            r2.addListener(r3)
            long r1 = java.lang.System.currentTimeMillis()
            r0.photoTransitionAnimationStartTime = r1
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 18
            if (r1 < r2) goto L_0x0271
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r1 = r0.containerView
            r2 = 0
            r1.setLayerType(r8, r2)
        L_0x0271:
            android.animation.AnimatorSet r1 = r0.imageMoveAnimation
            r1.start()
            goto L_0x02e8
        L_0x0277:
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            r5 = 4
            android.animation.Animator[] r5 = new android.animation.Animator[r5]
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r6 = r0.containerView
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r11 = new float[r4]
            r12 = 1063675494(0x3var_, float:0.9)
            r11[r2] = r12
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r10, r11)
            r5[r2] = r6
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r6 = r0.containerView
            android.util.Property r10 = android.view.View.SCALE_Y
            float[] r11 = new float[r4]
            r11[r2] = r12
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r10, r11)
            r5[r4] = r6
            org.telegram.ui.SecretMediaViewer$PhotoBackgroundDrawable r6 = r0.photoBackgroundDrawable
            android.util.Property<android.graphics.drawable.ColorDrawable, java.lang.Integer> r10 = org.telegram.ui.Components.AnimationProperties.COLOR_DRAWABLE_ALPHA
            int[] r11 = new int[r4]
            r11[r2] = r2
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofInt(r6, r10, r11)
            r5[r8] = r6
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r4]
            r11[r2] = r9
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r6, r10, r11)
            r5[r7] = r2
            r3.playTogether(r5)
            r0.photoAnimationInProgress = r8
            org.telegram.ui.SecretMediaViewer$$ExternalSyntheticLambda3 r2 = new org.telegram.ui.SecretMediaViewer$$ExternalSyntheticLambda3
            r2.<init>(r0, r1)
            r0.photoAnimationEndRunnable = r2
            r1 = 200(0xc8, double:9.9E-322)
            r3.setDuration(r1)
            org.telegram.ui.SecretMediaViewer$8 r1 = new org.telegram.ui.SecretMediaViewer$8
            r1.<init>()
            r3.addListener(r1)
            long r1 = java.lang.System.currentTimeMillis()
            r0.photoTransitionAnimationStartTime = r1
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 18
            if (r1 < r2) goto L_0x02e5
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r1 = r0.containerView
            r2 = 0
            r1.setLayerType(r8, r2)
        L_0x02e5:
            r3.start()
        L_0x02e8:
            return r4
        L_0x02e9:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretMediaViewer.closePhoto(boolean, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$closePhoto$3(PhotoViewer.PlaceProviderObject placeProviderObject) {
        this.imageMoveAnimation = null;
        this.photoAnimationInProgress = 0;
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, (Paint) null);
        }
        this.containerView.setVisibility(4);
        onPhotoClosed(placeProviderObject);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$closePhoto$4(PhotoViewer.PlaceProviderObject placeProviderObject) {
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        if (frameLayoutDrawer != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                frameLayoutDrawer.setLayerType(0, (Paint) null);
            }
            this.containerView.setVisibility(4);
            this.photoAnimationInProgress = 0;
            onPhotoClosed(placeProviderObject);
            this.containerView.setScaleX(1.0f);
            this.containerView.setScaleY(1.0f);
        }
    }

    private void onPhotoClosed(PhotoViewer.PlaceProviderObject placeProviderObject) {
        this.isVisible = false;
        this.currentProvider = null;
        this.disableShowCheck = false;
        releasePlayer();
        new ArrayList();
        AndroidUtilities.runOnUIThread(new SecretMediaViewer$$ExternalSyntheticLambda1(this), 50);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPhotoClosed$5() {
        ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        this.centerImage.setImageBitmap((Bitmap) null);
        try {
            if (this.windowView.getParent() != null) {
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.isPhotoVisible = false;
    }

    /* access modifiers changed from: private */
    public void updateMinMax(float f) {
        int imageWidth = ((int) ((this.centerImage.getImageWidth() * f) - ((float) getContainerViewWidth()))) / 2;
        int imageHeight = ((int) ((this.centerImage.getImageHeight() * f) - ((float) getContainerViewHeight()))) / 2;
        if (imageWidth > 0) {
            this.minX = (float) (-imageWidth);
            this.maxX = (float) imageWidth;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (imageHeight > 0) {
            this.minY = (float) (-imageHeight);
            this.maxY = (float) imageHeight;
            return;
        }
        this.maxY = 0.0f;
        this.minY = 0.0f;
    }

    private int getContainerViewWidth() {
        return this.containerView.getWidth();
    }

    private int getContainerViewHeight() {
        return this.containerView.getHeight();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x026c, code lost:
        if (r13 > r3) goto L_0x0266;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x027b, code lost:
        if (r0 > r3) goto L_0x0275;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x02dd, code lost:
        if (r2 > r3) goto L_0x02d7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x02ee, code lost:
        if (r2 > r3) goto L_0x02e8;
     */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01de  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processTouchEvent(android.view.MotionEvent r13) {
        /*
            r12 = this;
            int r0 = r12.photoAnimationInProgress
            r1 = 0
            if (r0 != 0) goto L_0x037b
            long r2 = r12.animationStartTime
            r4 = 0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x000f
            goto L_0x037b
        L_0x000f:
            int r0 = r13.getPointerCount()
            r2 = 1
            if (r0 != r2) goto L_0x002c
            android.view.GestureDetector r0 = r12.gestureDetector
            boolean r0 = r0.onTouchEvent(r13)
            if (r0 == 0) goto L_0x002c
            boolean r0 = r12.doubleTap
            if (r0 == 0) goto L_0x002c
            r12.doubleTap = r1
            r12.moving = r1
            r12.zooming = r1
            r12.checkMinMax(r1)
            return r2
        L_0x002c:
            int r0 = r13.getActionMasked()
            r3 = 1073741824(0x40000000, float:2.0)
            r6 = 2
            if (r0 == 0) goto L_0x02f8
            int r0 = r13.getActionMasked()
            r7 = 5
            if (r0 != r7) goto L_0x003e
            goto L_0x02f8
        L_0x003e:
            int r0 = r13.getActionMasked()
            r7 = 1077936128(0x40400000, float:3.0)
            r8 = 1065353216(0x3var_, float:1.0)
            r9 = 0
            if (r0 != r6) goto L_0x01fa
            int r0 = r13.getPointerCount()
            if (r0 != r6) goto L_0x00c4
            boolean r0 = r12.draggingDown
            if (r0 != 0) goto L_0x00c4
            boolean r0 = r12.zooming
            if (r0 == 0) goto L_0x00c4
            r12.discardTap = r2
            float r0 = r13.getX(r2)
            float r3 = r13.getX(r1)
            float r0 = r0 - r3
            double r3 = (double) r0
            float r0 = r13.getY(r2)
            float r13 = r13.getY(r1)
            float r0 = r0 - r13
            double r7 = (double) r0
            double r2 = java.lang.Math.hypot(r3, r7)
            float r13 = (float) r2
            float r0 = r12.pinchStartDistance
            float r13 = r13 / r0
            float r0 = r12.pinchStartScale
            float r13 = r13 * r0
            r12.scale = r13
            float r13 = r12.pinchCenterX
            int r0 = r12.getContainerViewWidth()
            int r0 = r0 / r6
            float r0 = (float) r0
            float r13 = r13 - r0
            float r0 = r12.pinchCenterX
            int r2 = r12.getContainerViewWidth()
            int r2 = r2 / r6
            float r2 = (float) r2
            float r0 = r0 - r2
            float r2 = r12.pinchStartX
            float r0 = r0 - r2
            float r2 = r12.scale
            float r3 = r12.pinchStartScale
            float r2 = r2 / r3
            float r0 = r0 * r2
            float r13 = r13 - r0
            r12.translationX = r13
            float r13 = r12.pinchCenterY
            int r0 = r12.getContainerViewHeight()
            int r0 = r0 / r6
            float r0 = (float) r0
            float r13 = r13 - r0
            float r0 = r12.pinchCenterY
            int r2 = r12.getContainerViewHeight()
            int r2 = r2 / r6
            float r2 = (float) r2
            float r0 = r0 - r2
            float r2 = r12.pinchStartY
            float r0 = r0 - r2
            float r2 = r12.scale
            float r3 = r12.pinchStartScale
            float r3 = r2 / r3
            float r0 = r0 * r3
            float r13 = r13 - r0
            r12.translationY = r13
            r12.updateMinMax(r2)
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r13 = r12.containerView
            r13.invalidate()
            goto L_0x037b
        L_0x00c4:
            int r0 = r13.getPointerCount()
            if (r0 != r2) goto L_0x037b
            android.view.VelocityTracker r0 = r12.velocityTracker
            if (r0 == 0) goto L_0x00d1
            r0.addMovement(r13)
        L_0x00d1:
            float r0 = r13.getX()
            float r6 = r12.moveStartX
            float r0 = r0 - r6
            float r0 = java.lang.Math.abs(r0)
            float r6 = r13.getY()
            float r10 = r12.dragY
            float r6 = r6 - r10
            float r6 = java.lang.Math.abs(r6)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r10 = (float) r10
            int r10 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r10 > 0) goto L_0x00f9
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r10 = (float) r10
            int r10 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r10 <= 0) goto L_0x00fb
        L_0x00f9:
            r12.discardTap = r2
        L_0x00fb:
            boolean r10 = r12.canDragDown
            if (r10 == 0) goto L_0x012b
            boolean r10 = r12.draggingDown
            if (r10 != 0) goto L_0x012b
            float r10 = r12.scale
            int r10 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x012b
            r10 = 1106247680(0x41var_, float:30.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            int r10 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r10 < 0) goto L_0x012b
            float r6 = r6 / r3
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x012b
            r12.draggingDown = r2
            r12.moving = r1
            float r13 = r13.getY()
            r12.dragY = r13
            boolean r13 = r12.isActionBarVisible
            if (r13 == 0) goto L_0x012a
            r12.toggleActionBar(r1, r2)
        L_0x012a:
            return r2
        L_0x012b:
            boolean r0 = r12.draggingDown
            if (r0 == 0) goto L_0x013f
            float r13 = r13.getY()
            float r0 = r12.dragY
            float r13 = r13 - r0
            r12.translationY = r13
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r13 = r12.containerView
            r13.invalidate()
            goto L_0x037b
        L_0x013f:
            boolean r0 = r12.invalidCoords
            if (r0 != 0) goto L_0x01ea
            long r10 = r12.animationStartTime
            int r0 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x01ea
            float r0 = r12.moveStartX
            float r3 = r13.getX()
            float r0 = r0 - r3
            float r3 = r12.moveStartY
            float r4 = r13.getY()
            float r3 = r3 - r4
            boolean r4 = r12.moving
            if (r4 != 0) goto L_0x017b
            float r4 = r12.scale
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 != 0) goto L_0x0175
            float r4 = java.lang.Math.abs(r3)
            r5 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r4 = r4 + r5
            float r5 = java.lang.Math.abs(r0)
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 < 0) goto L_0x017b
        L_0x0175:
            float r4 = r12.scale
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 == 0) goto L_0x037b
        L_0x017b:
            boolean r4 = r12.moving
            if (r4 != 0) goto L_0x0185
            r12.moving = r2
            r12.canDragDown = r1
            r0 = 0
            r3 = 0
        L_0x0185:
            float r2 = r13.getX()
            r12.moveStartX = r2
            float r13 = r13.getY()
            r12.moveStartY = r13
            float r13 = r12.scale
            r12.updateMinMax(r13)
            float r13 = r12.translationX
            float r2 = r12.minX
            int r2 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x01a4
            float r2 = r12.maxX
            int r2 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x01a5
        L_0x01a4:
            float r0 = r0 / r7
        L_0x01a5:
            float r2 = r12.maxY
            int r4 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r4 != 0) goto L_0x01c4
            float r4 = r12.minY
            int r5 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r5 != 0) goto L_0x01c4
            float r5 = r12.translationY
            float r6 = r5 - r3
            int r6 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x01bc
            r12.translationY = r4
            goto L_0x01d5
        L_0x01bc:
            float r5 = r5 - r3
            int r4 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x01d1
            r12.translationY = r2
            goto L_0x01d5
        L_0x01c4:
            float r4 = r12.translationY
            float r5 = r12.minY
            int r5 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r5 < 0) goto L_0x01d3
            int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x01d1
            goto L_0x01d3
        L_0x01d1:
            r9 = r3
            goto L_0x01d5
        L_0x01d3:
            float r9 = r3 / r7
        L_0x01d5:
            float r13 = r13 - r0
            r12.translationX = r13
            float r13 = r12.scale
            int r13 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
            if (r13 == 0) goto L_0x01e3
            float r13 = r12.translationY
            float r13 = r13 - r9
            r12.translationY = r13
        L_0x01e3:
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r13 = r12.containerView
            r13.invalidate()
            goto L_0x037b
        L_0x01ea:
            r12.invalidCoords = r1
            float r0 = r13.getX()
            r12.moveStartX = r0
            float r13 = r13.getY()
            r12.moveStartY = r13
            goto L_0x037b
        L_0x01fa:
            int r0 = r13.getActionMasked()
            r3 = 3
            if (r0 == r3) goto L_0x020e
            int r0 = r13.getActionMasked()
            if (r0 == r2) goto L_0x020e
            int r0 = r13.getActionMasked()
            r3 = 6
            if (r0 != r3) goto L_0x037b
        L_0x020e:
            boolean r0 = r12.zooming
            if (r0 == 0) goto L_0x0289
            r12.invalidCoords = r2
            float r13 = r12.scale
            int r0 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
            if (r0 >= 0) goto L_0x0221
            r12.updateMinMax(r8)
            r12.animateTo(r8, r9, r9, r2)
            goto L_0x0285
        L_0x0221:
            int r13 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r13 <= 0) goto L_0x0282
            float r13 = r12.pinchCenterX
            int r0 = r12.getContainerViewWidth()
            int r0 = r0 / r6
            float r0 = (float) r0
            float r13 = r13 - r0
            float r0 = r12.pinchCenterX
            int r3 = r12.getContainerViewWidth()
            int r3 = r3 / r6
            float r3 = (float) r3
            float r0 = r0 - r3
            float r3 = r12.pinchStartX
            float r0 = r0 - r3
            float r3 = r12.pinchStartScale
            float r3 = r7 / r3
            float r0 = r0 * r3
            float r13 = r13 - r0
            float r0 = r12.pinchCenterY
            int r3 = r12.getContainerViewHeight()
            int r3 = r3 / r6
            float r3 = (float) r3
            float r0 = r0 - r3
            float r3 = r12.pinchCenterY
            int r4 = r12.getContainerViewHeight()
            int r4 = r4 / r6
            float r4 = (float) r4
            float r3 = r3 - r4
            float r4 = r12.pinchStartY
            float r3 = r3 - r4
            float r4 = r12.pinchStartScale
            float r4 = r7 / r4
            float r3 = r3 * r4
            float r0 = r0 - r3
            r12.updateMinMax(r7)
            float r3 = r12.minX
            int r4 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0268
        L_0x0266:
            r13 = r3
            goto L_0x026f
        L_0x0268:
            float r3 = r12.maxX
            int r4 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x026f
            goto L_0x0266
        L_0x026f:
            float r3 = r12.minY
            int r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0277
        L_0x0275:
            r0 = r3
            goto L_0x027e
        L_0x0277:
            float r3 = r12.maxY
            int r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x027e
            goto L_0x0275
        L_0x027e:
            r12.animateTo(r7, r13, r0, r2)
            goto L_0x0285
        L_0x0282:
            r12.checkMinMax(r2)
        L_0x0285:
            r12.zooming = r1
            goto L_0x037b
        L_0x0289:
            boolean r0 = r12.draggingDown
            if (r0 == 0) goto L_0x02af
            float r0 = r12.dragY
            float r13 = r13.getY()
            float r0 = r0 - r13
            float r13 = java.lang.Math.abs(r0)
            int r0 = r12.getContainerViewHeight()
            float r0 = (float) r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            float r0 = r0 / r3
            int r13 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
            if (r13 <= 0) goto L_0x02a8
            r12.closePhoto(r2, r1)
            goto L_0x02ab
        L_0x02a8:
            r12.animateTo(r8, r9, r9, r1)
        L_0x02ab:
            r12.draggingDown = r1
            goto L_0x037b
        L_0x02af:
            boolean r13 = r12.moving
            if (r13 == 0) goto L_0x037b
            float r13 = r12.translationX
            float r0 = r12.translationY
            float r3 = r12.scale
            r12.updateMinMax(r3)
            r12.moving = r1
            r12.canDragDown = r2
            android.view.VelocityTracker r2 = r12.velocityTracker
            if (r2 == 0) goto L_0x02cf
            float r3 = r12.scale
            int r3 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r3 != 0) goto L_0x02cf
            r3 = 1000(0x3e8, float:1.401E-42)
            r2.computeCurrentVelocity(r3)
        L_0x02cf:
            float r2 = r12.translationX
            float r3 = r12.minX
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x02d9
        L_0x02d7:
            r13 = r3
            goto L_0x02e0
        L_0x02d9:
            float r3 = r12.maxX
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x02e0
            goto L_0x02d7
        L_0x02e0:
            float r2 = r12.translationY
            float r3 = r12.minY
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x02ea
        L_0x02e8:
            r0 = r3
            goto L_0x02f1
        L_0x02ea:
            float r3 = r12.maxY
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x02f1
            goto L_0x02e8
        L_0x02f1:
            float r2 = r12.scale
            r12.animateTo(r2, r13, r0, r1)
            goto L_0x037b
        L_0x02f8:
            r12.discardTap = r1
            org.telegram.ui.Components.Scroller r0 = r12.scroller
            boolean r0 = r0.isFinished()
            if (r0 != 0) goto L_0x0307
            org.telegram.ui.Components.Scroller r0 = r12.scroller
            r0.abortAnimation()
        L_0x0307:
            boolean r0 = r12.draggingDown
            if (r0 != 0) goto L_0x037b
            int r0 = r13.getPointerCount()
            if (r0 != r6) goto L_0x035c
            float r0 = r13.getX(r2)
            float r4 = r13.getX(r1)
            float r0 = r0 - r4
            double r4 = (double) r0
            float r0 = r13.getY(r2)
            float r6 = r13.getY(r1)
            float r0 = r0 - r6
            double r6 = (double) r0
            double r4 = java.lang.Math.hypot(r4, r6)
            float r0 = (float) r4
            r12.pinchStartDistance = r0
            float r0 = r12.scale
            r12.pinchStartScale = r0
            float r0 = r13.getX(r1)
            float r4 = r13.getX(r2)
            float r0 = r0 + r4
            float r0 = r0 / r3
            r12.pinchCenterX = r0
            float r0 = r13.getY(r1)
            float r13 = r13.getY(r2)
            float r0 = r0 + r13
            float r0 = r0 / r3
            r12.pinchCenterY = r0
            float r13 = r12.translationX
            r12.pinchStartX = r13
            float r13 = r12.translationY
            r12.pinchStartY = r13
            r12.zooming = r2
            r12.moving = r1
            android.view.VelocityTracker r13 = r12.velocityTracker
            if (r13 == 0) goto L_0x037b
            r13.clear()
            goto L_0x037b
        L_0x035c:
            int r0 = r13.getPointerCount()
            if (r0 != r2) goto L_0x037b
            float r0 = r13.getX()
            r12.moveStartX = r0
            float r13 = r13.getY()
            r12.moveStartY = r13
            r12.dragY = r13
            r12.draggingDown = r1
            r12.canDragDown = r2
            android.view.VelocityTracker r13 = r12.velocityTracker
            if (r13 == 0) goto L_0x037b
            r13.clear()
        L_0x037b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretMediaViewer.processTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0017, code lost:
        if (r2 > r3) goto L_0x0011;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0028, code lost:
        if (r2 > r3) goto L_0x0022;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkMinMax(boolean r6) {
        /*
            r5 = this;
            float r0 = r5.translationX
            float r1 = r5.translationY
            float r2 = r5.scale
            r5.updateMinMax(r2)
            float r2 = r5.translationX
            float r3 = r5.minX
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0013
        L_0x0011:
            r0 = r3
            goto L_0x001a
        L_0x0013:
            float r3 = r5.maxX
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x001a
            goto L_0x0011
        L_0x001a:
            float r2 = r5.translationY
            float r3 = r5.minY
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0024
        L_0x0022:
            r1 = r3
            goto L_0x002b
        L_0x0024:
            float r3 = r5.maxY
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x002b
            goto L_0x0022
        L_0x002b:
            float r2 = r5.scale
            r5.animateTo(r2, r0, r1, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretMediaViewer.checkMinMax(boolean):void");
    }

    private void animateTo(float f, float f2, float f3, boolean z) {
        animateTo(f, f2, f3, z, 250);
    }

    private void animateTo(float f, float f2, float f3, boolean z, int i) {
        if (this.scale != f || this.translationX != f2 || this.translationY != f3) {
            this.zoomAnimation = z;
            this.animateToScale = f;
            this.animateToX = f2;
            this.animateToY = f3;
            this.animationStartTime = System.currentTimeMillis();
            AnimatorSet animatorSet = new AnimatorSet();
            this.imageMoveAnimation = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f})});
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) i);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = SecretMediaViewer.this.imageMoveAnimation = null;
                    SecretMediaViewer.this.containerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    @Keep
    public void setAnimationValue(float f) {
        this.animationValue = f;
        this.containerView.invalidate();
    }

    @Keep
    public float getAnimationValue() {
        return this.animationValue;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.scale == 1.0f) {
            return false;
        }
        this.scroller.abortAnimation();
        this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(f), Math.round(f2), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
        this.containerView.postInvalidate();
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if (this.discardTap) {
            return false;
        }
        toggleActionBar(!this.isActionBarVisible, true);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x007f, code lost:
        if (r0 > r10) goto L_0x0079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x008e, code lost:
        if (r2 > r10) goto L_0x0088;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onDoubleTap(android.view.MotionEvent r10) {
        /*
            r9 = this;
            float r0 = r9.scale
            r1 = 0
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0017
            float r4 = r9.translationY
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 != 0) goto L_0x0016
            float r4 = r9.translationX
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x0017
        L_0x0016:
            return r1
        L_0x0017:
            long r4 = r9.animationStartTime
            r6 = 0
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 != 0) goto L_0x009a
            int r4 = r9.photoAnimationInProgress
            if (r4 == 0) goto L_0x0025
            goto L_0x009a
        L_0x0025:
            r1 = 1
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x0095
            float r0 = r10.getX()
            int r2 = r9.getContainerViewWidth()
            int r2 = r2 / 2
            float r2 = (float) r2
            float r0 = r0 - r2
            float r2 = r10.getX()
            int r3 = r9.getContainerViewWidth()
            int r3 = r3 / 2
            float r3 = (float) r3
            float r2 = r2 - r3
            float r3 = r9.translationX
            float r2 = r2 - r3
            float r3 = r9.scale
            r4 = 1077936128(0x40400000, float:3.0)
            float r3 = r4 / r3
            float r2 = r2 * r3
            float r0 = r0 - r2
            float r2 = r10.getY()
            int r3 = r9.getContainerViewHeight()
            int r3 = r3 / 2
            float r3 = (float) r3
            float r2 = r2 - r3
            float r10 = r10.getY()
            int r3 = r9.getContainerViewHeight()
            int r3 = r3 / 2
            float r3 = (float) r3
            float r10 = r10 - r3
            float r3 = r9.translationY
            float r10 = r10 - r3
            float r3 = r9.scale
            float r3 = r4 / r3
            float r10 = r10 * r3
            float r2 = r2 - r10
            r9.updateMinMax(r4)
            float r10 = r9.minX
            int r3 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r3 >= 0) goto L_0x007b
        L_0x0079:
            r0 = r10
            goto L_0x0082
        L_0x007b:
            float r10 = r9.maxX
            int r3 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r3 <= 0) goto L_0x0082
            goto L_0x0079
        L_0x0082:
            float r10 = r9.minY
            int r3 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r3 >= 0) goto L_0x008a
        L_0x0088:
            r2 = r10
            goto L_0x0091
        L_0x008a:
            float r10 = r9.maxY
            int r3 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r3 <= 0) goto L_0x0091
            goto L_0x0088
        L_0x0091:
            r9.animateTo(r4, r0, r2, r1)
            goto L_0x0098
        L_0x0095:
            r9.animateTo(r2, r3, r3, r1)
        L_0x0098:
            r9.doubleTap = r1
        L_0x009a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretMediaViewer.onDoubleTap(android.view.MotionEvent):boolean");
    }
}
