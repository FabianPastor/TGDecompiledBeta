package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import androidx.annotation.Keep;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Scroller;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class SecretMediaViewer implements NotificationCenterDelegate, OnGestureListener, OnDoubleTapListener {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile SecretMediaViewer Instance;
    private ActionBar actionBar;
    private float animateToClipBottom;
    private float animateToClipHorizontal;
    private float animateToClipTop;
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private long animationStartTime;
    private float animationValue;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private Paint blackPaint = new Paint();
    private boolean canDragDown = true;
    private ImageReceiver centerImage = new ImageReceiver();
    private float clipBottom;
    private float clipHorizontal;
    private float clipTop;
    private long closeTime;
    private boolean closeVideoAfterWatch;
    private FrameLayoutDrawer containerView;
    private int[] coords = new int[2];
    private int currentAccount;
    private AnimatorSet currentActionBarAnimation;
    private int currentChannelId;
    private MessageObject currentMessageObject;
    private PhotoViewerProvider currentProvider;
    private int currentRotation;
    private BitmapHolder currentThumb;
    private boolean disableShowCheck;
    private boolean discardTap;
    private boolean doubleTap;
    private float dragY;
    private boolean draggingDown;
    private GestureDetector gestureDetector;
    private AnimatorSet imageMoveAnimation;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private boolean invalidCoords;
    private boolean isActionBarVisible = true;
    private boolean isPhotoVisible;
    private boolean isPlaying;
    private boolean isVideo;
    private boolean isVisible;
    private Object lastInsets;
    private float maxX;
    private float maxY;
    private float minX;
    private float minY;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private long openTime;
    private Activity parentActivity;
    private Runnable photoAnimationEndRunnable;
    private int photoAnimationInProgress;
    private PhotoBackgroundDrawable photoBackgroundDrawable = new PhotoBackgroundDrawable(-16777216);
    private long photoTransitionAnimationStartTime;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale = 1.0f;
    private float pinchStartX;
    private float pinchStartY;
    private int playerRetryPlayCount;
    private float scale = 1.0f;
    private Scroller scroller;
    private SecretDeleteTimer secretDeleteTimer;
    private boolean textureUploaded;
    private float translationX;
    private float translationY;
    private boolean useOvershootForScale;
    private VelocityTracker velocityTracker;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private VideoPlayer videoPlayer;
    private TextureView videoTextureView;
    private boolean videoWatchedOneTime;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;
    private boolean zoomAnimation;
    private boolean zooming;

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            SecretMediaViewer.this.processTouchEvent(motionEvent);
            return true;
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            SecretMediaViewer.this.onDraw(canvas);
        }

        /* Access modifiers changed, original: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            return view != SecretMediaViewer.this.aspectRatioFrameLayout && super.drawChild(canvas, view, j);
        }
    }

    private class PhotoBackgroundDrawable extends ColorDrawable {
        private Runnable drawRunnable;
        private int frame;

        public PhotoBackgroundDrawable(int i) {
            super(i);
        }

        @Keep
        public void setAlpha(int i) {
            if (SecretMediaViewer.this.parentActivity instanceof LaunchActivity) {
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) SecretMediaViewer.this.parentActivity).drawerLayoutContainer;
                boolean z = (SecretMediaViewer.this.isPhotoVisible && i == 255) ? false : true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            super.setAlpha(i);
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (getAlpha() != 0) {
                if (this.frame == 2) {
                    Runnable runnable = this.drawRunnable;
                    if (runnable != null) {
                        runnable.run();
                        this.drawRunnable = null;
                        this.frame++;
                    }
                }
                invalidateSelf();
                this.frame++;
            }
        }
    }

    private class SecretDeleteTimer extends FrameLayout {
        private Paint afterDeleteProgressPaint;
        private Paint circlePaint;
        private Paint deleteProgressPaint;
        private RectF deleteProgressRect = new RectF();
        private long destroyTime;
        private long destroyTtl;
        private Drawable drawable;
        private ArrayList<Particle> freeParticles = new ArrayList();
        private long lastAnimationTime;
        private Paint particlePaint;
        private ArrayList<Particle> particles = new ArrayList();
        private boolean useVideoProgress;

        private class Particle {
            float alpha;
            float currentTime;
            float lifeTime;
            float velocity;
            float vx;
            float vy;
            float x;
            float y;

            private Particle() {
            }

            /* synthetic */ Particle(SecretDeleteTimer secretDeleteTimer, AnonymousClass1 anonymousClass1) {
                this();
            }
        }

        public SecretDeleteTimer(Context context) {
            super(context);
            int i = 0;
            setWillNotDraw(false);
            this.particlePaint = new Paint(1);
            this.particlePaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
            this.particlePaint.setColor(-1644826);
            this.particlePaint.setStrokeCap(Cap.ROUND);
            this.particlePaint.setStyle(Style.STROKE);
            this.deleteProgressPaint = new Paint(1);
            this.deleteProgressPaint.setColor(-1644826);
            this.afterDeleteProgressPaint = new Paint(1);
            this.afterDeleteProgressPaint.setStyle(Style.STROKE);
            this.afterDeleteProgressPaint.setStrokeCap(Cap.ROUND);
            this.afterDeleteProgressPaint.setColor(-1644826);
            this.afterDeleteProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.circlePaint = new Paint(1);
            this.circlePaint.setColor(NUM);
            this.drawable = context.getResources().getDrawable(NUM);
            while (i < 40) {
                this.freeParticles.add(new Particle(this, null));
                i++;
            }
        }

        private void setDestroyTime(long j, long j2, boolean z) {
            this.destroyTime = j;
            this.destroyTtl = j2;
            this.useVideoProgress = z;
            this.lastAnimationTime = System.currentTimeMillis();
            invalidate();
        }

        private void updateParticles(long j) {
            int size = this.particles.size();
            int i = 0;
            while (i < size) {
                Particle particle = (Particle) this.particles.get(i);
                float f = particle.currentTime;
                float f2 = particle.lifeTime;
                if (f >= f2) {
                    if (this.freeParticles.size() < 40) {
                        this.freeParticles.add(particle);
                    }
                    this.particles.remove(i);
                    i--;
                    size--;
                } else {
                    particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(f / f2);
                    f = particle.x;
                    f2 = particle.vx;
                    float f3 = particle.velocity;
                    float f4 = (float) j;
                    particle.x = f + (((f2 * f3) * f4) / 500.0f);
                    particle.y += ((particle.vy * f3) * f4) / 500.0f;
                    particle.currentTime += f4;
                }
                i++;
            }
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            i = (getMeasuredHeight() / 2) - (AndroidUtilities.dp(28.0f) / 2);
            this.deleteProgressRect.set((float) (getMeasuredWidth() - AndroidUtilities.dp(49.0f)), (float) i, (float) (getMeasuredWidth() - AndroidUtilities.dp(21.0f)), (float) (i + AndroidUtilities.dp(28.0f)));
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"DrawAllocation"})
        public void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            if (SecretMediaViewer.this.currentMessageObject != null && SecretMediaViewer.this.currentMessageObject.messageOwner.destroyTime != 0) {
                long duration;
                float f;
                canvas2.drawCircle((float) (getMeasuredWidth() - AndroidUtilities.dp(35.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(16.0f), this.circlePaint);
                if (this.useVideoProgress) {
                    if (SecretMediaViewer.this.videoPlayer != null) {
                        duration = SecretMediaViewer.this.videoPlayer.getDuration();
                        long currentPosition = SecretMediaViewer.this.videoPlayer.getCurrentPosition();
                        if (!(duration == -9223372036854775807L || currentPosition == -9223372036854775807L)) {
                            f = 1.0f - (((float) currentPosition) / ((float) duration));
                        }
                    }
                    f = 1.0f;
                } else {
                    f = ((float) Math.max(0, this.destroyTime - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance(SecretMediaViewer.this.currentAccount).getTimeDifference() * 1000))))) / (((float) this.destroyTtl) * 1000.0f);
                }
                int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(40.0f);
                int measuredHeight = ((getMeasuredHeight() - AndroidUtilities.dp(14.0f)) / 2) - AndroidUtilities.dp(0.5f);
                this.drawable.setBounds(measuredWidth, measuredHeight, AndroidUtilities.dp(10.0f) + measuredWidth, AndroidUtilities.dp(14.0f) + measuredHeight);
                this.drawable.draw(canvas2);
                float f2 = f * -360.0f;
                canvas.drawArc(this.deleteProgressRect, -90.0f, f2, false, this.afterDeleteProgressPaint);
                int size = this.particles.size();
                for (measuredHeight = 0; measuredHeight < size; measuredHeight++) {
                    Particle particle = (Particle) this.particles.get(measuredHeight);
                    this.particlePaint.setAlpha((int) (particle.alpha * 255.0f));
                    canvas2.drawPoint(particle.x, particle.y, this.particlePaint);
                }
                double d = (double) (f2 - 90.0f);
                double d2 = 0.017453292519943295d;
                Double.isNaN(d);
                d *= 0.017453292519943295d;
                double sin = Math.sin(d);
                d = -Math.cos(d);
                double d3 = -d;
                double dp = (double) AndroidUtilities.dp(14.0f);
                Double.isNaN(dp);
                d3 *= dp;
                double centerX = (double) this.deleteProgressRect.centerX();
                Double.isNaN(centerX);
                f = (float) (d3 + centerX);
                Double.isNaN(dp);
                dp *= sin;
                centerX = (double) this.deleteProgressRect.centerY();
                Double.isNaN(centerX);
                float f3 = (float) (dp + centerX);
                int i = 0;
                while (i < 1) {
                    Particle particle2;
                    if (this.freeParticles.isEmpty()) {
                        particle2 = new Particle(this, null);
                    } else {
                        particle2 = (Particle) this.freeParticles.get(0);
                        this.freeParticles.remove(0);
                    }
                    particle2.x = f;
                    particle2.y = f3;
                    double nextInt = (double) (Utilities.random.nextInt(140) - 70);
                    Double.isNaN(nextInt);
                    nextInt *= d2;
                    if (nextInt < 0.0d) {
                        nextInt += 6.283185307179586d;
                    }
                    particle2.vx = (float) ((Math.cos(nextInt) * sin) - (Math.sin(nextInt) * d));
                    particle2.vy = (float) ((Math.sin(nextInt) * sin) + (Math.cos(nextInt) * d));
                    particle2.alpha = 1.0f;
                    particle2.currentTime = 0.0f;
                    particle2.lifeTime = (float) (Utilities.random.nextInt(100) + 400);
                    particle2.velocity = (Utilities.random.nextFloat() * 4.0f) + 20.0f;
                    this.particles.add(particle2);
                    i++;
                    d2 = 0.017453292519943295d;
                }
                duration = System.currentTimeMillis();
                updateParticles(duration - this.lastAnimationTime);
                this.lastAnimationTime = duration;
                invalidate();
            }
        }
    }

    private boolean scaleToFill() {
        return false;
    }

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
            if (this.currentMessageObject != null && ((Integer) objArr[1]).intValue() == 0 && ((ArrayList) objArr[0]).contains(Integer.valueOf(this.currentMessageObject.getId()))) {
                if (!this.isVideo || this.videoWatchedOneTime) {
                    closePhoto(true, true);
                } else {
                    this.closeVideoAfterWatch = true;
                }
            }
        } else if (i == NotificationCenter.didCreatedNewDeleteTask) {
            if (this.currentMessageObject != null && this.secretDeleteTimer != null) {
                SparseArray sparseArray = (SparseArray) objArr[0];
                for (i2 = 0; i2 < sparseArray.size(); i2++) {
                    int keyAt = sparseArray.keyAt(i2);
                    ArrayList arrayList = (ArrayList) sparseArray.get(keyAt);
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        long longValue = ((Long) arrayList.get(i3)).longValue();
                        if (i3 == 0) {
                            int i4 = (int) (longValue >> 32);
                            if (i4 < 0) {
                                i4 = 0;
                            }
                            if (i4 != this.currentChannelId) {
                                return;
                            }
                        }
                        if (((long) this.currentMessageObject.getId()) == longValue) {
                            this.currentMessageObject.messageOwner.destroyTime = keyAt;
                            this.secretDeleteTimer.invalidate();
                            return;
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.updateMessageMedia) {
            if (this.currentMessageObject.getId() == ((Message) objArr[0]).id) {
                if (!this.isVideo || this.videoWatchedOneTime) {
                    closePhoto(true, true);
                } else {
                    this.closeVideoAfterWatch = true;
                }
            }
        }
    }

    private void preparePlayer(final File file) {
        if (this.parentActivity != null) {
            releasePlayer();
            if (this.videoTextureView == null) {
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity);
                this.aspectRatioFrameLayout.setVisibility(4);
                this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                this.videoTextureView = new TextureView(this.parentActivity);
                this.videoTextureView.setOpaque(false);
                this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
            }
            this.textureUploaded = false;
            this.videoCrossfadeStarted = false;
            TextureView textureView = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView.setAlpha(0.0f);
            if (this.videoPlayer == null) {
                this.videoPlayer = new VideoPlayer();
                this.videoPlayer.setTextureView(this.videoTextureView);
                this.videoPlayer.setDelegate(new VideoPlayerDelegate() {
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
                                    FileLog.e(e);
                                }
                            } else {
                                try {
                                    SecretMediaViewer.this.parentActivity.getWindow().addFlags(128);
                                } catch (Exception e2) {
                                    FileLog.e(e2);
                                }
                            }
                            if (i == 3 && SecretMediaViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                SecretMediaViewer.this.aspectRatioFrameLayout.setVisibility(0);
                            }
                            if (!SecretMediaViewer.this.videoPlayer.isPlaying() || i == 4) {
                                if (SecretMediaViewer.this.isPlaying) {
                                    SecretMediaViewer.this.isPlaying = false;
                                    if (i == 4) {
                                        SecretMediaViewer.this.videoWatchedOneTime = true;
                                        if (SecretMediaViewer.this.closeVideoAfterWatch) {
                                            SecretMediaViewer.this.closePhoto(true, true);
                                            return;
                                        }
                                        SecretMediaViewer.this.videoPlayer.seekTo(0);
                                        SecretMediaViewer.this.videoPlayer.play();
                                    }
                                }
                            } else if (!SecretMediaViewer.this.isPlaying) {
                                SecretMediaViewer.this.isPlaying = true;
                            }
                        }
                    }

                    public void onError(Exception exception) {
                        if (SecretMediaViewer.this.playerRetryPlayCount > 0) {
                            SecretMediaViewer.this.playerRetryPlayCount = SecretMediaViewer.this.playerRetryPlayCount - 1;
                            AndroidUtilities.runOnUIThread(new -$$Lambda$SecretMediaViewer$1$dToY1BP1yRZuDyMam95X6X8QLzo(this, file), 100);
                            return;
                        }
                        FileLog.e((Throwable) exception);
                    }

                    public /* synthetic */ void lambda$onError$0$SecretMediaViewer$1(File file) {
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
                            SecretMediaViewer.this.textureUploaded = true;
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
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            this.playerRetryPlayCount = 0;
            videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
        }
        try {
            if (this.parentActivity != null) {
                this.parentActivity.getWindow().clearFlags(128);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            this.containerView.removeView(aspectRatioFrameLayout);
            this.aspectRatioFrameLayout = null;
        }
        if (this.videoTextureView != null) {
            this.videoTextureView = null;
        }
        this.isPlaying = false;
    }

    public void setParentActivity(Activity activity) {
        this.currentAccount = UserConfig.selectedAccount;
        this.centerImage.setCurrentAccount(this.currentAccount);
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.scroller = new Scroller(activity);
            this.windowView = new FrameLayout(activity) {
                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    i = MeasureSpec.getSize(i);
                    i2 = MeasureSpec.getSize(i2);
                    if (VERSION.SDK_INT < 21 || SecretMediaViewer.this.lastInsets == null) {
                        int i3 = AndroidUtilities.displaySize.y;
                        if (i2 > i3) {
                            i2 = i3;
                        }
                    } else {
                        WindowInsets windowInsets = (WindowInsets) SecretMediaViewer.this.lastInsets;
                        if (AndroidUtilities.incorrectDisplaySizeFix) {
                            int i4 = AndroidUtilities.displaySize.y;
                            if (i2 > i4) {
                                i2 = i4;
                            }
                            i2 += AndroidUtilities.statusBarHeight;
                        }
                        i2 -= windowInsets.getSystemWindowInsetBottom();
                        i -= windowInsets.getSystemWindowInsetRight();
                    }
                    setMeasuredDimension(i, i2);
                    if (VERSION.SDK_INT >= 21 && SecretMediaViewer.this.lastInsets != null) {
                        i -= ((WindowInsets) SecretMediaViewer.this.lastInsets).getSystemWindowInsetLeft();
                    }
                    SecretMediaViewer.this.containerView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                }

                /* Access modifiers changed, original: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    i = (VERSION.SDK_INT < 21 || SecretMediaViewer.this.lastInsets == null) ? 0 : ((WindowInsets) SecretMediaViewer.this.lastInsets).getSystemWindowInsetLeft() + 0;
                    SecretMediaViewer.this.containerView.layout(i, 0, SecretMediaViewer.this.containerView.getMeasuredWidth() + i, SecretMediaViewer.this.containerView.getMeasuredHeight());
                    if (z) {
                        if (SecretMediaViewer.this.imageMoveAnimation == null) {
                            SecretMediaViewer.this.scale = 1.0f;
                            SecretMediaViewer.this.translationX = 0.0f;
                            SecretMediaViewer.this.translationY = 0.0f;
                        }
                        SecretMediaViewer secretMediaViewer = SecretMediaViewer.this;
                        secretMediaViewer.updateMinMax(secretMediaViewer.scale);
                    }
                }
            };
            this.windowView.setBackgroundDrawable(this.photoBackgroundDrawable);
            this.windowView.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            this.containerView = new FrameLayoutDrawer(activity) {
                /* Access modifiers changed, original: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    if (SecretMediaViewer.this.secretDeleteTimer != null) {
                        int currentActionBarHeight = ((ActionBar.getCurrentActionBarHeight() - SecretMediaViewer.this.secretDeleteTimer.getMeasuredHeight()) / 2) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                        SecretMediaViewer.this.secretDeleteTimer.layout(SecretMediaViewer.this.secretDeleteTimer.getLeft(), currentActionBarHeight, SecretMediaViewer.this.secretDeleteTimer.getRight(), SecretMediaViewer.this.secretDeleteTimer.getMeasuredHeight() + currentActionBarHeight);
                    }
                }
            };
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.containerView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.gravity = 51;
            this.containerView.setLayoutParams(layoutParams);
            if (VERSION.SDK_INT >= 21) {
                this.containerView.setFitsSystemWindows(true);
                this.containerView.setOnApplyWindowInsetsListener(new -$$Lambda$SecretMediaViewer$NFiT5K5Ywrc0uS0Pq28kGtaBHeE(this));
                this.containerView.setSystemUiVisibility(1280);
            }
            this.gestureDetector = new GestureDetector(this.containerView.getContext(), this);
            this.gestureDetector.setOnDoubleTapListener(this);
            this.actionBar = new ActionBar(activity);
            this.actionBar.setTitleColor(-1);
            this.actionBar.setSubtitleColor(-1);
            this.actionBar.setBackgroundColor(NUM);
            this.actionBar.setOccupyStatusBar(VERSION.SDK_INT >= 21);
            this.actionBar.setItemsBackgroundColor(NUM, false);
            this.actionBar.setBackButtonImage(NUM);
            this.actionBar.setTitleRightMargin(AndroidUtilities.dp(70.0f));
            this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
                public void onItemClick(int i) {
                    if (i == -1) {
                        SecretMediaViewer.this.closePhoto(true, false);
                    }
                }
            });
            this.secretDeleteTimer = new SecretDeleteTimer(activity);
            this.containerView.addView(this.secretDeleteTimer, LayoutHelper.createFrame(119, 48.0f, 53, 0.0f, 0.0f, 0.0f, 0.0f));
            this.windowLayoutParams = new LayoutParams();
            LayoutParams layoutParams2 = this.windowLayoutParams;
            layoutParams2.height = -1;
            layoutParams2.format = -3;
            layoutParams2.width = -1;
            layoutParams2.gravity = 48;
            layoutParams2.type = 99;
            if (VERSION.SDK_INT >= 21) {
                layoutParams2.flags = -NUM;
            } else {
                layoutParams2.flags = 8;
            }
            layoutParams2 = this.windowLayoutParams;
            layoutParams2.flags |= 8192;
            this.centerImage.setParentView(this.containerView);
            this.centerImage.setForceCrossfade(true);
        }
    }

    public /* synthetic */ WindowInsets lambda$setParentActivity$0$SecretMediaViewer(View view, WindowInsets windowInsets) {
        WindowInsets windowInsets2 = (WindowInsets) this.lastInsets;
        this.lastInsets = windowInsets;
        if (windowInsets2 == null || !windowInsets2.toString().equals(windowInsets.toString())) {
            this.windowView.requestLayout();
        }
        return windowInsets.consumeSystemWindowInsets();
    }

    public void openMedia(MessageObject messageObject, PhotoViewerProvider photoViewerProvider) {
        MessageObject messageObject2 = messageObject;
        PhotoViewerProvider photoViewerProvider2 = photoViewerProvider;
        String str = "window";
        if (!(this.parentActivity == null || messageObject2 == null || !messageObject.needDrawBluredPreview() || photoViewerProvider2 == null)) {
            PlaceProviderObject placeForPhoto = photoViewerProvider2.getPlaceForPhoto(messageObject2, null, 0, true);
            if (placeForPhoto != null) {
                int i;
                this.currentProvider = photoViewerProvider2;
                this.openTime = System.currentTimeMillis();
                this.closeTime = 0;
                this.isActionBarVisible = true;
                this.isPhotoVisible = true;
                this.draggingDown = false;
                AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
                if (aspectRatioFrameLayout != null) {
                    aspectRatioFrameLayout.setVisibility(4);
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
                int i3 = point.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                this.scale = Math.max(width / ((float) i2), height / ((float) i3));
                float f = (float) placeForPhoto.viewX;
                float f2 = drawRegion.left;
                this.translationX = ((f + f2) + (width / 2.0f)) - ((float) (i2 / 2));
                this.translationY = ((((float) placeForPhoto.viewY) + drawRegion.top) + (height / 2.0f)) - ((float) (i3 / 2));
                this.clipHorizontal = Math.abs(f2 - ((float) placeForPhoto.imageReceiver.getImageX()));
                i3 = (int) Math.abs(drawRegion.top - ((float) placeForPhoto.imageReceiver.getImageY()));
                int[] iArr = new int[2];
                placeForPhoto.parentView.getLocationInWindow(iArr);
                this.clipTop = (((float) (iArr[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) - (((float) placeForPhoto.viewY) + drawRegion.top)) + ((float) placeForPhoto.clipTopAddition);
                if (this.clipTop < 0.0f) {
                    this.clipTop = 0.0f;
                }
                this.clipBottom = (((((float) placeForPhoto.viewY) + drawRegion.top) + ((float) ((int) height))) - ((float) ((iArr[1] + placeForPhoto.parentView.getHeight()) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)))) + ((float) placeForPhoto.clipBottomAddition);
                if (this.clipBottom < 0.0f) {
                    this.clipBottom = 0.0f;
                }
                width = (float) i3;
                this.clipTop = Math.max(this.clipTop, width);
                this.clipBottom = Math.max(this.clipBottom, width);
                this.animationStartTime = System.currentTimeMillis();
                this.animateToX = 0.0f;
                this.animateToY = 0.0f;
                this.animateToClipBottom = 0.0f;
                this.animateToClipHorizontal = 0.0f;
                this.animateToClipTop = 0.0f;
                this.animateToScale = 1.0f;
                this.zoomAnimation = true;
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateMessageMedia);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didCreatedNewDeleteTask);
                Peer peer = messageObject2.messageOwner.to_id;
                this.currentChannelId = peer != null ? peer.channel_id : 0;
                toggleActionBar(true, false);
                this.currentMessageObject = messageObject2;
                Document document = messageObject.getDocument();
                BitmapHolder bitmapHolder = this.currentThumb;
                if (bitmapHolder != null) {
                    bitmapHolder.release();
                    this.currentThumb = null;
                }
                this.currentThumb = placeForPhoto.imageReceiver.getThumbBitmapSafe();
                ImageReceiver imageReceiver;
                ImageLocation forObject;
                BitmapHolder bitmapHolder2;
                SecretDeleteTimer secretDeleteTimer;
                Message message;
                if (document == null) {
                    i = 2;
                    this.actionBar.setTitle(LocaleController.getString("DisappearingPhoto", NUM));
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, AndroidUtilities.getPhotoSize());
                    imageReceiver = this.centerImage;
                    forObject = ImageLocation.getForObject(closestPhotoSizeWithSize, messageObject2.photoThumbsObject);
                    bitmapHolder2 = this.currentThumb;
                    imageReceiver.setImage(forObject, null, bitmapHolder2 != null ? new BitmapDrawable(bitmapHolder2.bitmap) : null, -1, null, (Object) messageObject, 2);
                    secretDeleteTimer = this.secretDeleteTimer;
                    message = messageObject2.messageOwner;
                    secretDeleteTimer.setDestroyTime(((long) message.destroyTime) * 1000, (long) message.ttl, false);
                } else if (MessageObject.isGifDocument(document)) {
                    this.actionBar.setTitle(LocaleController.getString("DisappearingGif", NUM));
                    imageReceiver = this.centerImage;
                    forObject = ImageLocation.getForDocument(document);
                    bitmapHolder2 = this.currentThumb;
                    i = 2;
                    imageReceiver.setImage(forObject, null, bitmapHolder2 != null ? new BitmapDrawable(bitmapHolder2.bitmap) : null, -1, null, (Object) messageObject, 1);
                    secretDeleteTimer = this.secretDeleteTimer;
                    message = messageObject2.messageOwner;
                    secretDeleteTimer.setDestroyTime(((long) message.destroyTime) * 1000, (long) message.ttl, false);
                } else {
                    i = 2;
                    this.playerRetryPlayCount = 1;
                    this.actionBar.setTitle(LocaleController.getString("DisappearingVideo", NUM));
                    File file = new File(messageObject2.messageOwner.attachPath);
                    if (file.exists()) {
                        preparePlayer(file);
                    } else {
                        file = FileLoader.getPathToMessage(messageObject2.messageOwner);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(file.getAbsolutePath());
                        stringBuilder.append(".enc");
                        File file2 = new File(stringBuilder.toString());
                        if (file2.exists()) {
                            file = file2;
                        }
                        preparePlayer(file);
                    }
                    this.isVideo = true;
                    ImageReceiver imageReceiver2 = this.centerImage;
                    BitmapHolder bitmapHolder3 = this.currentThumb;
                    imageReceiver2.setImage(null, null, bitmapHolder3 != null ? new BitmapDrawable(bitmapHolder3.bitmap) : null, -1, null, (Object) messageObject, 2);
                    if (((long) (messageObject.getDuration() * 1000)) > (((long) messageObject2.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance(this.currentAccount).getTimeDifference() * 1000)))) {
                        this.secretDeleteTimer.setDestroyTime(-1, -1, true);
                    } else {
                        secretDeleteTimer = this.secretDeleteTimer;
                        message = messageObject2.messageOwner;
                        secretDeleteTimer.setDestroyTime(((long) message.destroyTime) * 1000, (long) message.ttl, false);
                    }
                }
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService(str)).removeView(this.windowView);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                ((WindowManager) this.parentActivity.getSystemService(str)).addView(this.windowView, this.windowLayoutParams);
                this.secretDeleteTimer.invalidate();
                this.isVisible = true;
                this.imageMoveAnimation = new AnimatorSet();
                AnimatorSet animatorSet = this.imageMoveAnimation;
                r2 = new Animator[5];
                String str2 = "alpha";
                r2[0] = ObjectAnimator.ofFloat(this.actionBar, str2, new float[]{0.0f, 1.0f});
                r2[1] = ObjectAnimator.ofFloat(this.secretDeleteTimer, str2, new float[]{0.0f, 1.0f});
                r2[i] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, str2, new int[]{0, 255});
                r2[3] = ObjectAnimator.ofFloat(this.secretDeleteTimer, str2, new float[]{0.0f, 1.0f});
                r2[4] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f});
                animatorSet.playTogether(r2);
                this.photoAnimationInProgress = 3;
                this.photoAnimationEndRunnable = new -$$Lambda$SecretMediaViewer$KK7Q-lwTroM_vlzg7v5qjBIm368(this);
                this.imageMoveAnimation.setDuration(250);
                this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (SecretMediaViewer.this.photoAnimationEndRunnable != null) {
                            SecretMediaViewer.this.photoAnimationEndRunnable.run();
                            SecretMediaViewer.this.photoAnimationEndRunnable = null;
                        }
                    }
                });
                this.photoTransitionAnimationStartTime = System.currentTimeMillis();
                if (VERSION.SDK_INT >= 18) {
                    this.containerView.setLayerType(i, null);
                }
                this.imageMoveAnimation.setInterpolator(new DecelerateInterpolator());
                this.photoBackgroundDrawable.frame = 0;
                this.photoBackgroundDrawable.drawRunnable = new -$$Lambda$SecretMediaViewer$-WXiS-iPl6V2WQvBv2qBotC4RRs(this, placeForPhoto);
                this.imageMoveAnimation.start();
            }
        }
    }

    public /* synthetic */ void lambda$openMedia$1$SecretMediaViewer() {
        this.photoAnimationInProgress = 0;
        this.imageMoveAnimation = null;
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        if (frameLayoutDrawer != null) {
            if (VERSION.SDK_INT >= 18) {
                frameLayoutDrawer.setLayerType(0, null);
            }
            this.containerView.invalidate();
        }
    }

    public /* synthetic */ void lambda$openMedia$2$SecretMediaViewer(PlaceProviderObject placeProviderObject) {
        this.disableShowCheck = false;
        placeProviderObject.imageReceiver.setVisible(false, true);
    }

    public boolean isShowingImage(MessageObject messageObject) {
        if (!(!this.isVisible || this.disableShowCheck || messageObject == null)) {
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 != null && messageObject2.getId() == messageObject.getId()) {
                return true;
            }
        }
        return false;
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
            ActionBar actionBar = this.actionBar;
            float[] fArr = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, "alpha", fArr));
            this.currentActionBarAnimation = new AnimatorSet();
            this.currentActionBarAnimation.playTogether(arrayList);
            if (!z) {
                this.currentActionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (SecretMediaViewer.this.currentActionBarAnimation != null && SecretMediaViewer.this.currentActionBarAnimation.equals(animator)) {
                            SecretMediaViewer.this.actionBar.setVisibility(8);
                            SecretMediaViewer.this.currentActionBarAnimation = null;
                        }
                    }
                });
            }
            this.currentActionBarAnimation.setDuration(200);
            this.currentActionBarAnimation.start();
            return;
        }
        ActionBar actionBar2 = this.actionBar;
        if (!z) {
            f = 0.0f;
        }
        actionBar2.setAlpha(f);
        if (!z) {
            this.actionBar.setVisibility(8);
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void destroyPhotoViewer() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        this.isVisible = false;
        this.currentProvider = null;
        BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        releasePlayer();
        if (this.parentActivity != null) {
            FrameLayout frameLayout = this.windowView;
            if (frameLayout != null) {
                try {
                    if (frameLayout.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                    }
                    this.windowView = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
        Instance = null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:85:0x0281  */
    private void onDraw(android.graphics.Canvas r18) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r18;
        r2 = r0.isPhotoVisible;
        if (r2 != 0) goto L_0x0009;
    L_0x0008:
        return;
    L_0x0009:
        r2 = r0.imageMoveAnimation;
        r3 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r4 = 0;
        r5 = 0;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r2 == 0) goto L_0x00cf;
    L_0x0013:
        r2 = r0.scroller;
        r2 = r2.isFinished();
        if (r2 != 0) goto L_0x0020;
    L_0x001b:
        r2 = r0.scroller;
        r2.abortAnimation();
    L_0x0020:
        r2 = r0.useOvershootForScale;
        if (r2 == 0) goto L_0x007a;
    L_0x0024:
        r2 = r0.animationValue;
        r7 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
        r8 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
        if (r8 >= 0) goto L_0x003c;
    L_0x002d:
        r2 = r2 / r7;
        r7 = r0.scale;
        r8 = r0.animateToScale;
        r9 = NUM; // 0x3var_f5c float:1.02 double:5.26437315E-315;
        r8 = r8 * r9;
        r8 = r8 - r7;
        r8 = r8 * r2;
        r7 = r7 + r8;
        goto L_0x0050;
    L_0x003c:
        r8 = r0.animateToScale;
        r9 = NUM; // 0x3ca3d700 float:0.01999998 double:5.02647748E-315;
        r9 = r9 * r8;
        r2 = r2 - r7;
        r7 = NUM; // 0x3dccccd0 float:0.NUM double:5.12263048E-315;
        r2 = r2 / r7;
        r2 = r6 - r2;
        r9 = r9 * r2;
        r7 = r8 + r9;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0050:
        r8 = r0.translationY;
        r9 = r0.animateToY;
        r9 = r9 - r8;
        r9 = r9 * r2;
        r8 = r8 + r9;
        r9 = r0.translationX;
        r10 = r0.animateToX;
        r10 = r10 - r9;
        r10 = r10 * r2;
        r9 = r9 + r10;
        r10 = r0.clipTop;
        r11 = r0.animateToClipTop;
        r11 = r11 - r10;
        r11 = r11 * r2;
        r10 = r10 + r11;
        r11 = r0.clipBottom;
        r12 = r0.animateToClipBottom;
        r12 = r12 - r11;
        r12 = r12 * r2;
        r11 = r11 + r12;
        r12 = r0.clipHorizontal;
        r13 = r0.animateToClipHorizontal;
        r13 = r13 - r12;
        r13 = r13 * r2;
        r12 = r12 + r13;
        r2 = r8;
        goto L_0x00ac;
    L_0x007a:
        r2 = r0.scale;
        r7 = r0.animateToScale;
        r7 = r7 - r2;
        r8 = r0.animationValue;
        r7 = r7 * r8;
        r7 = r7 + r2;
        r2 = r0.translationY;
        r9 = r0.animateToY;
        r9 = r9 - r2;
        r9 = r9 * r8;
        r2 = r2 + r9;
        r9 = r0.translationX;
        r10 = r0.animateToX;
        r10 = r10 - r9;
        r10 = r10 * r8;
        r9 = r9 + r10;
        r10 = r0.clipTop;
        r11 = r0.animateToClipTop;
        r11 = r11 - r10;
        r11 = r11 * r8;
        r10 = r10 + r11;
        r11 = r0.clipBottom;
        r12 = r0.animateToClipBottom;
        r12 = r12 - r11;
        r12 = r12 * r8;
        r11 = r11 + r12;
        r12 = r0.clipHorizontal;
        r13 = r0.animateToClipHorizontal;
        r13 = r13 - r12;
        r13 = r13 * r8;
        r12 = r12 + r13;
    L_0x00ac:
        r8 = r0.animateToScale;
        r8 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r8 != 0) goto L_0x00c0;
    L_0x00b2:
        r8 = r0.scale;
        r8 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r8 != 0) goto L_0x00c0;
    L_0x00b8:
        r8 = r0.translationX;
        r8 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r8 != 0) goto L_0x00c0;
    L_0x00be:
        r8 = r2;
        goto L_0x00c2;
    L_0x00c0:
        r8 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
    L_0x00c2:
        r13 = r0.containerView;
        r13.invalidate();
        r16 = r7;
        r7 = r2;
        r2 = r8;
        r8 = r16;
        goto L_0x016c;
    L_0x00cf:
        r7 = r0.animationStartTime;
        r9 = 0;
        r2 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
        if (r2 == 0) goto L_0x00fa;
    L_0x00d7:
        r2 = r0.animateToX;
        r0.translationX = r2;
        r2 = r0.animateToY;
        r0.translationY = r2;
        r2 = r0.animateToClipBottom;
        r0.clipBottom = r2;
        r2 = r0.animateToClipTop;
        r0.clipTop = r2;
        r2 = r0.animateToClipHorizontal;
        r0.clipHorizontal = r2;
        r2 = r0.animateToScale;
        r0.scale = r2;
        r0.animationStartTime = r9;
        r2 = r0.scale;
        r0.updateMinMax(r2);
        r0.zoomAnimation = r5;
        r0.useOvershootForScale = r5;
    L_0x00fa:
        r2 = r0.scroller;
        r2 = r2.isFinished();
        if (r2 != 0) goto L_0x0155;
    L_0x0102:
        r2 = r0.scroller;
        r2 = r2.computeScrollOffset();
        if (r2 == 0) goto L_0x0155;
    L_0x010a:
        r2 = r0.scroller;
        r2 = r2.getStartX();
        r2 = (float) r2;
        r7 = r0.maxX;
        r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
        if (r2 >= 0) goto L_0x012d;
    L_0x0117:
        r2 = r0.scroller;
        r2 = r2.getStartX();
        r2 = (float) r2;
        r7 = r0.minX;
        r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
        if (r2 <= 0) goto L_0x012d;
    L_0x0124:
        r2 = r0.scroller;
        r2 = r2.getCurrX();
        r2 = (float) r2;
        r0.translationX = r2;
    L_0x012d:
        r2 = r0.scroller;
        r2 = r2.getStartY();
        r2 = (float) r2;
        r7 = r0.maxY;
        r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
        if (r2 >= 0) goto L_0x0150;
    L_0x013a:
        r2 = r0.scroller;
        r2 = r2.getStartY();
        r2 = (float) r2;
        r7 = r0.minY;
        r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
        if (r2 <= 0) goto L_0x0150;
    L_0x0147:
        r2 = r0.scroller;
        r2 = r2.getCurrY();
        r2 = (float) r2;
        r0.translationY = r2;
    L_0x0150:
        r2 = r0.containerView;
        r2.invalidate();
    L_0x0155:
        r7 = r0.scale;
        r2 = r0.translationY;
        r9 = r0.translationX;
        r10 = r0.clipTop;
        r11 = r0.clipBottom;
        r12 = r0.clipHorizontal;
        r8 = r0.moving;
        if (r8 != 0) goto L_0x0168;
    L_0x0165:
        r8 = r7;
        r7 = r2;
        goto L_0x016c;
    L_0x0168:
        r8 = r7;
        r7 = r2;
        r2 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
    L_0x016c:
        r13 = r0.photoAnimationInProgress;
        r14 = 3;
        if (r13 == r14) goto L_0x01c9;
    L_0x0171:
        r13 = r0.scale;
        r13 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1));
        if (r13 != 0) goto L_0x01a3;
    L_0x0177:
        r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r3 == 0) goto L_0x01a3;
    L_0x017b:
        r3 = r0.zoomAnimation;
        if (r3 != 0) goto L_0x01a3;
    L_0x017f:
        r3 = r17.getContainerViewHeight();
        r3 = (float) r3;
        r13 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = r3 / r13;
        r13 = r0.photoBackgroundDrawable;
        r14 = NUM; // 0x42fe0000 float:127.0 double:5.553013277E-315;
        r15 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r2 = java.lang.Math.abs(r2);
        r2 = java.lang.Math.min(r2, r3);
        r2 = r2 / r3;
        r2 = r6 - r2;
        r2 = r2 * r15;
        r2 = java.lang.Math.max(r14, r2);
        r2 = (int) r2;
        r13.setAlpha(r2);
        goto L_0x01aa;
    L_0x01a3:
        r2 = r0.photoBackgroundDrawable;
        r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r2.setAlpha(r3);
    L_0x01aa:
        r2 = r0.zoomAnimation;
        if (r2 != 0) goto L_0x01c9;
    L_0x01ae:
        r2 = r0.maxX;
        r3 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1));
        if (r3 <= 0) goto L_0x01c9;
    L_0x01b4:
        r9 = r9 - r2;
        r2 = r18.getWidth();
        r2 = (float) r2;
        r9 = r9 / r2;
        r2 = java.lang.Math.min(r6, r9);
        r3 = NUM; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r3 = r3 * r2;
        r2 = r6 - r2;
        r9 = r0.maxX;
        goto L_0x01cc;
    L_0x01c9:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = 0;
    L_0x01cc:
        r13 = r0.aspectRatioFrameLayout;
        if (r13 == 0) goto L_0x01d7;
    L_0x01d0:
        r13 = r13.getVisibility();
        if (r13 != 0) goto L_0x01d7;
    L_0x01d6:
        r5 = 1;
    L_0x01d7:
        r18.save();
        r8 = r8 - r3;
        r3 = r17.getContainerViewWidth();
        r3 = r3 / 2;
        r3 = (float) r3;
        r3 = r3 + r9;
        r9 = r17.getContainerViewHeight();
        r9 = r9 / 2;
        r9 = (float) r9;
        r9 = r9 + r7;
        r1.translate(r3, r9);
        r1.scale(r8, r8);
        r3 = r0.centerImage;
        r3 = r3.getBitmapWidth();
        r7 = r0.centerImage;
        r7 = r7.getBitmapHeight();
        if (r5 == 0) goto L_0x022d;
    L_0x01ff:
        r9 = r0.textureUploaded;
        if (r9 == 0) goto L_0x022d;
    L_0x0203:
        r9 = (float) r3;
        r13 = (float) r7;
        r9 = r9 / r13;
        r13 = r0.videoTextureView;
        r13 = r13.getMeasuredWidth();
        r13 = (float) r13;
        r15 = r0.videoTextureView;
        r15 = r15.getMeasuredHeight();
        r15 = (float) r15;
        r13 = r13 / r15;
        r9 = r9 - r13;
        r9 = java.lang.Math.abs(r9);
        r13 = NUM; // 0x3CLASSNAMEd70a float:0.01 double:4.9850323E-315;
        r9 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1));
        if (r9 <= 0) goto L_0x022d;
    L_0x0221:
        r3 = r0.videoTextureView;
        r3 = r3.getMeasuredWidth();
        r7 = r0.videoTextureView;
        r7 = r7.getMeasuredHeight();
    L_0x022d:
        r9 = r17.getContainerViewHeight();
        r9 = (float) r9;
        r7 = (float) r7;
        r9 = r9 / r7;
        r13 = r17.getContainerViewWidth();
        r13 = (float) r13;
        r3 = (float) r3;
        r13 = r13 / r3;
        r9 = java.lang.Math.min(r9, r13);
        r3 = r3 * r9;
        r3 = (int) r3;
        r7 = r7 * r9;
        r7 = (int) r7;
        r9 = -r3;
        r9 = r9 / 2;
        r13 = (float) r9;
        r12 = r12 / r8;
        r15 = r13 + r12;
        r4 = -r7;
        r4 = r4 / 2;
        r14 = (float) r4;
        r10 = r10 / r8;
        r10 = r10 + r14;
        r6 = r3 / 2;
        r6 = (float) r6;
        r6 = r6 - r12;
        r12 = r7 / 2;
        r12 = (float) r12;
        r11 = r11 / r8;
        r12 = r12 - r11;
        r1.clipRect(r15, r10, r6, r12);
        if (r5 == 0) goto L_0x0270;
    L_0x0260:
        r6 = r0.textureUploaded;
        if (r6 == 0) goto L_0x0270;
    L_0x0264:
        r6 = r0.videoCrossfadeStarted;
        if (r6 == 0) goto L_0x0270;
    L_0x0268:
        r6 = r0.videoCrossfadeAlpha;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r6 == 0) goto L_0x027f;
    L_0x0270:
        r6 = r0.centerImage;
        r6.setAlpha(r2);
        r6 = r0.centerImage;
        r6.setImageCoords(r9, r4, r3, r7);
        r3 = r0.centerImage;
        r3.draw(r1);
    L_0x027f:
        if (r5 == 0) goto L_0x02d4;
    L_0x0281:
        r3 = r0.videoCrossfadeStarted;
        if (r3 != 0) goto L_0x0295;
    L_0x0285:
        r3 = r0.textureUploaded;
        if (r3 == 0) goto L_0x0295;
    L_0x0289:
        r3 = 1;
        r0.videoCrossfadeStarted = r3;
        r3 = 0;
        r0.videoCrossfadeAlpha = r3;
        r3 = java.lang.System.currentTimeMillis();
        r0.videoCrossfadeAlphaLastTime = r3;
    L_0x0295:
        r1.translate(r13, r14);
        r3 = r0.videoTextureView;
        r4 = r0.videoCrossfadeAlpha;
        r2 = r2 * r4;
        r3.setAlpha(r2);
        r2 = r0.aspectRatioFrameLayout;
        r2.draw(r1);
        r2 = r0.videoCrossfadeStarted;
        if (r2 == 0) goto L_0x02d4;
    L_0x02aa:
        r2 = r0.videoCrossfadeAlpha;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 >= 0) goto L_0x02d4;
    L_0x02b2:
        r2 = java.lang.System.currentTimeMillis();
        r4 = r0.videoCrossfadeAlphaLastTime;
        r4 = r2 - r4;
        r0.videoCrossfadeAlphaLastTime = r2;
        r2 = r0.videoCrossfadeAlpha;
        r3 = (float) r4;
        r4 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r3 = r3 / r4;
        r2 = r2 + r3;
        r0.videoCrossfadeAlpha = r2;
        r2 = r0.containerView;
        r2.invalidate();
        r2 = r0.videoCrossfadeAlpha;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 <= 0) goto L_0x02d4;
    L_0x02d2:
        r0.videoCrossfadeAlpha = r3;
    L_0x02d4:
        r18.restore();
        return;
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

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0251  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0072  */
    public void closePhoto(boolean r18, boolean r19) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r0.parentActivity;
        if (r1 == 0) goto L_0x02be;
    L_0x0006:
        r1 = r0.isPhotoVisible;
        if (r1 == 0) goto L_0x02be;
    L_0x000a:
        r1 = r17.checkPhotoAnimation();
        if (r1 == 0) goto L_0x0012;
    L_0x0010:
        goto L_0x02be;
    L_0x0012:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r1);
        r2 = org.telegram.messenger.NotificationCenter.messagesDeleted;
        r1.removeObserver(r0, r2);
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r1);
        r2 = org.telegram.messenger.NotificationCenter.updateMessageMedia;
        r1.removeObserver(r0, r2);
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r1);
        r2 = org.telegram.messenger.NotificationCenter.didCreatedNewDeleteTask;
        r1.removeObserver(r0, r2);
        r1 = 0;
        r0.isActionBarVisible = r1;
        r2 = r0.velocityTracker;
        r3 = 0;
        if (r2 == 0) goto L_0x0040;
    L_0x003b:
        r2.recycle();
        r0.velocityTracker = r3;
    L_0x0040:
        r4 = java.lang.System.currentTimeMillis();
        r0.closeTime = r4;
        r2 = r0.currentProvider;
        r4 = 1;
        if (r2 == 0) goto L_0x0063;
    L_0x004b:
        r5 = r0.currentMessageObject;
        r6 = r5.messageOwner;
        r6 = r6.media;
        r7 = r6.photo;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty;
        if (r7 != 0) goto L_0x0063;
    L_0x0057:
        r6 = r6.document;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty;
        if (r6 == 0) goto L_0x005e;
    L_0x005d:
        goto L_0x0063;
    L_0x005e:
        r2 = r2.getPlaceForPhoto(r5, r3, r1, r4);
        goto L_0x0064;
    L_0x0063:
        r2 = r3;
    L_0x0064:
        r5 = r0.videoPlayer;
        if (r5 == 0) goto L_0x006b;
    L_0x0068:
        r5.pause();
    L_0x006b:
        r7 = 3;
        r8 = "alpha";
        r9 = 2;
        r10 = 0;
        if (r18 == 0) goto L_0x0251;
    L_0x0072:
        r0.photoAnimationInProgress = r7;
        r11 = r0.containerView;
        r11.invalidate();
        r11 = new android.animation.AnimatorSet;
        r11.<init>();
        r0.imageMoveAnimation = r11;
        r11 = 21;
        if (r2 == 0) goto L_0x0166;
    L_0x0084:
        r12 = r2.imageReceiver;
        r12 = r12.getThumbBitmap();
        if (r12 == 0) goto L_0x0166;
    L_0x008c:
        if (r19 != 0) goto L_0x0166;
    L_0x008e:
        r12 = r2.imageReceiver;
        r12.setVisible(r1, r4);
        r12 = r2.imageReceiver;
        r12 = r12.getDrawRegion();
        r13 = r12.right;
        r14 = r12.left;
        r13 = r13 - r14;
        r14 = r12.bottom;
        r15 = r12.top;
        r14 = r14 - r15;
        r15 = org.telegram.messenger.AndroidUtilities.displaySize;
        r3 = r15.x;
        r15 = r15.y;
        r5 = android.os.Build.VERSION.SDK_INT;
        if (r5 < r11) goto L_0x00b0;
    L_0x00ad:
        r5 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        goto L_0x00b1;
    L_0x00b0:
        r5 = 0;
    L_0x00b1:
        r15 = r15 + r5;
        r5 = (float) r3;
        r5 = r13 / r5;
        r6 = (float) r15;
        r6 = r14 / r6;
        r5 = java.lang.Math.max(r5, r6);
        r0.animateToScale = r5;
        r5 = r2.viewX;
        r5 = (float) r5;
        r6 = r12.left;
        r5 = r5 + r6;
        r16 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r13 = r13 / r16;
        r5 = r5 + r13;
        r3 = r3 / r9;
        r3 = (float) r3;
        r5 = r5 - r3;
        r0.animateToX = r5;
        r3 = r2.viewY;
        r3 = (float) r3;
        r5 = r12.top;
        r3 = r3 + r5;
        r5 = r14 / r16;
        r3 = r3 + r5;
        r15 = r15 / r9;
        r5 = (float) r15;
        r3 = r3 - r5;
        r0.animateToY = r3;
        r3 = r2.imageReceiver;
        r3 = r3.getImageX();
        r3 = (float) r3;
        r6 = r6 - r3;
        r3 = java.lang.Math.abs(r6);
        r0.animateToClipHorizontal = r3;
        r3 = r12.top;
        r5 = r2.imageReceiver;
        r5 = r5.getImageY();
        r5 = (float) r5;
        r3 = r3 - r5;
        r3 = java.lang.Math.abs(r3);
        r3 = (int) r3;
        r5 = new int[r9];
        r6 = r2.parentView;
        r6.getLocationInWindow(r5);
        r6 = r5[r4];
        r13 = android.os.Build.VERSION.SDK_INT;
        if (r13 < r11) goto L_0x0108;
    L_0x0106:
        r13 = 0;
        goto L_0x010a;
    L_0x0108:
        r13 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
    L_0x010a:
        r6 = r6 - r13;
        r6 = (float) r6;
        r13 = r2.viewY;
        r13 = (float) r13;
        r15 = r12.top;
        r13 = r13 + r15;
        r6 = r6 - r13;
        r13 = r2.clipTopAddition;
        r13 = (float) r13;
        r6 = r6 + r13;
        r0.animateToClipTop = r6;
        r6 = r0.animateToClipTop;
        r6 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r6 >= 0) goto L_0x0121;
    L_0x011f:
        r0.animateToClipTop = r10;
    L_0x0121:
        r6 = r2.viewY;
        r6 = (float) r6;
        r12 = r12.top;
        r6 = r6 + r12;
        r12 = (int) r14;
        r12 = (float) r12;
        r6 = r6 + r12;
        r5 = r5[r4];
        r12 = r2.parentView;
        r12 = r12.getHeight();
        r5 = r5 + r12;
        r12 = android.os.Build.VERSION.SDK_INT;
        if (r12 < r11) goto L_0x0139;
    L_0x0137:
        r11 = 0;
        goto L_0x013b;
    L_0x0139:
        r11 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
    L_0x013b:
        r5 = r5 - r11;
        r5 = (float) r5;
        r6 = r6 - r5;
        r5 = r2.clipBottomAddition;
        r5 = (float) r5;
        r6 = r6 + r5;
        r0.animateToClipBottom = r6;
        r5 = r0.animateToClipBottom;
        r5 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1));
        if (r5 >= 0) goto L_0x014c;
    L_0x014a:
        r0.animateToClipBottom = r10;
    L_0x014c:
        r5 = java.lang.System.currentTimeMillis();
        r0.animationStartTime = r5;
        r5 = r0.animateToClipBottom;
        r3 = (float) r3;
        r5 = java.lang.Math.max(r5, r3);
        r0.animateToClipBottom = r5;
        r5 = r0.animateToClipTop;
        r3 = java.lang.Math.max(r5, r3);
        r0.animateToClipTop = r3;
        r0.zoomAnimation = r4;
        goto L_0x017e;
    L_0x0166:
        r3 = org.telegram.messenger.AndroidUtilities.displaySize;
        r3 = r3.y;
        r5 = android.os.Build.VERSION.SDK_INT;
        if (r5 < r11) goto L_0x0171;
    L_0x016e:
        r5 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        goto L_0x0172;
    L_0x0171:
        r5 = 0;
    L_0x0172:
        r3 = r3 + r5;
        r5 = r0.translationY;
        r5 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1));
        if (r5 < 0) goto L_0x017a;
    L_0x0179:
        goto L_0x017b;
    L_0x017a:
        r3 = -r3;
    L_0x017b:
        r3 = (float) r3;
        r0.animateToY = r3;
    L_0x017e:
        r3 = r0.isVideo;
        r5 = "animationValue";
        r6 = 5;
        if (r3 == 0) goto L_0x01cd;
    L_0x0185:
        r0.videoCrossfadeStarted = r1;
        r0.textureUploaded = r1;
        r3 = r0.imageMoveAnimation;
        r6 = new android.animation.Animator[r6];
        r11 = r0.photoBackgroundDrawable;
        r12 = new int[r4];
        r12[r1] = r1;
        r11 = android.animation.ObjectAnimator.ofInt(r11, r8, r12);
        r6[r1] = r11;
        r11 = new float[r9];
        r11 = {0, NUM};
        r5 = android.animation.ObjectAnimator.ofFloat(r0, r5, r11);
        r6[r4] = r5;
        r5 = r0.actionBar;
        r11 = new float[r4];
        r11[r1] = r10;
        r5 = android.animation.ObjectAnimator.ofFloat(r5, r8, r11);
        r6[r9] = r5;
        r5 = r0.secretDeleteTimer;
        r11 = new float[r4];
        r11[r1] = r10;
        r5 = android.animation.ObjectAnimator.ofFloat(r5, r8, r11);
        r6[r7] = r5;
        r4 = new float[r4];
        r4[r1] = r10;
        r1 = "videoCrossfadeAlpha";
        r1 = android.animation.ObjectAnimator.ofFloat(r0, r1, r4);
        r4 = 4;
        r6[r4] = r1;
        r3.playTogether(r6);
        goto L_0x0217;
    L_0x01cd:
        r3 = r0.centerImage;
        r3.setManualAlphaAnimator(r4);
        r3 = r0.imageMoveAnimation;
        r6 = new android.animation.Animator[r6];
        r11 = r0.photoBackgroundDrawable;
        r12 = new int[r4];
        r12[r1] = r1;
        r11 = android.animation.ObjectAnimator.ofInt(r11, r8, r12);
        r6[r1] = r11;
        r11 = new float[r9];
        r11 = {0, NUM};
        r5 = android.animation.ObjectAnimator.ofFloat(r0, r5, r11);
        r6[r4] = r5;
        r5 = r0.actionBar;
        r11 = new float[r4];
        r11[r1] = r10;
        r5 = android.animation.ObjectAnimator.ofFloat(r5, r8, r11);
        r6[r9] = r5;
        r5 = r0.secretDeleteTimer;
        r11 = new float[r4];
        r11[r1] = r10;
        r5 = android.animation.ObjectAnimator.ofFloat(r5, r8, r11);
        r6[r7] = r5;
        r5 = r0.centerImage;
        r4 = new float[r4];
        r4[r1] = r10;
        r1 = "currentAlpha";
        r1 = android.animation.ObjectAnimator.ofFloat(r5, r1, r4);
        r4 = 4;
        r6[r4] = r1;
        r3.playTogether(r6);
    L_0x0217:
        r1 = new org.telegram.ui.-$$Lambda$SecretMediaViewer$FsmwJiUXxyTDqbCVCkGBtpTvcxw;
        r1.<init>(r0, r2);
        r0.photoAnimationEndRunnable = r1;
        r1 = r0.imageMoveAnimation;
        r3 = new android.view.animation.DecelerateInterpolator;
        r3.<init>();
        r1.setInterpolator(r3);
        r1 = r0.imageMoveAnimation;
        r3 = 250; // 0xfa float:3.5E-43 double:1.235E-321;
        r1.setDuration(r3);
        r1 = r0.imageMoveAnimation;
        r3 = new org.telegram.ui.SecretMediaViewer$7;
        r3.<init>(r2);
        r1.addListener(r3);
        r1 = java.lang.System.currentTimeMillis();
        r0.photoTransitionAnimationStartTime = r1;
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 18;
        if (r1 < r2) goto L_0x024b;
    L_0x0245:
        r1 = r0.containerView;
        r2 = 0;
        r1.setLayerType(r9, r2);
    L_0x024b:
        r1 = r0.imageMoveAnimation;
        r1.start();
        goto L_0x02be;
    L_0x0251:
        r3 = new android.animation.AnimatorSet;
        r3.<init>();
        r5 = 4;
        r5 = new android.animation.Animator[r5];
        r6 = r0.containerView;
        r11 = new float[r4];
        r12 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
        r11[r1] = r12;
        r13 = "scaleX";
        r6 = android.animation.ObjectAnimator.ofFloat(r6, r13, r11);
        r5[r1] = r6;
        r6 = r0.containerView;
        r11 = new float[r4];
        r11[r1] = r12;
        r12 = "scaleY";
        r6 = android.animation.ObjectAnimator.ofFloat(r6, r12, r11);
        r5[r4] = r6;
        r6 = r0.photoBackgroundDrawable;
        r11 = new int[r4];
        r11[r1] = r1;
        r6 = android.animation.ObjectAnimator.ofInt(r6, r8, r11);
        r5[r9] = r6;
        r6 = r0.actionBar;
        r4 = new float[r4];
        r4[r1] = r10;
        r1 = android.animation.ObjectAnimator.ofFloat(r6, r8, r4);
        r5[r7] = r1;
        r3.playTogether(r5);
        r0.photoAnimationInProgress = r9;
        r1 = new org.telegram.ui.-$$Lambda$SecretMediaViewer$SXGR0dGO1jG_N03wXFFoPW86sNo;
        r1.<init>(r0, r2);
        r0.photoAnimationEndRunnable = r1;
        r1 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r3.setDuration(r1);
        r1 = new org.telegram.ui.SecretMediaViewer$8;
        r1.<init>();
        r3.addListener(r1);
        r1 = java.lang.System.currentTimeMillis();
        r0.photoTransitionAnimationStartTime = r1;
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 18;
        if (r1 < r2) goto L_0x02bb;
    L_0x02b5:
        r1 = r0.containerView;
        r2 = 0;
        r1.setLayerType(r9, r2);
    L_0x02bb:
        r3.start();
    L_0x02be:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretMediaViewer.closePhoto(boolean, boolean):void");
    }

    public /* synthetic */ void lambda$closePhoto$3$SecretMediaViewer(PlaceProviderObject placeProviderObject) {
        this.imageMoveAnimation = null;
        this.photoAnimationInProgress = 0;
        if (VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, null);
        }
        this.containerView.setVisibility(4);
        onPhotoClosed(placeProviderObject);
    }

    public /* synthetic */ void lambda$closePhoto$4$SecretMediaViewer(PlaceProviderObject placeProviderObject) {
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        if (frameLayoutDrawer != null) {
            if (VERSION.SDK_INT >= 18) {
                frameLayoutDrawer.setLayerType(0, null);
            }
            this.containerView.setVisibility(4);
            this.photoAnimationInProgress = 0;
            onPhotoClosed(placeProviderObject);
            this.containerView.setScaleX(1.0f);
            this.containerView.setScaleY(1.0f);
        }
    }

    private void onPhotoClosed(PlaceProviderObject placeProviderObject) {
        this.isVisible = false;
        this.currentProvider = null;
        this.disableShowCheck = false;
        releasePlayer();
        ArrayList arrayList = new ArrayList();
        AndroidUtilities.runOnUIThread(new -$$Lambda$SecretMediaViewer$WZXZJ3O2MqtXJfvuP6KrDf_oyHw(this), 50);
    }

    public /* synthetic */ void lambda$onPhotoClosed$5$SecretMediaViewer() {
        BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        this.centerImage.setImageBitmap(null);
        try {
            if (this.windowView.getParent() != null) {
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.isPhotoVisible = false;
    }

    private void updateMinMax(float f) {
        int imageWidth = ((int) ((((float) this.centerImage.getImageWidth()) * f) - ((float) getContainerViewWidth()))) / 2;
        int imageHeight = ((int) ((((float) this.centerImage.getImageHeight()) * f) - ((float) getContainerViewHeight()))) / 2;
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

    /* JADX WARNING: Removed duplicated region for block: B:90:0x01e2  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01e2  */
    private boolean processTouchEvent(android.view.MotionEvent r13) {
        /*
        r12 = this;
        r0 = r12.photoAnimationInProgress;
        r1 = 0;
        if (r0 != 0) goto L_0x037f;
    L_0x0005:
        r2 = r12.animationStartTime;
        r4 = 0;
        r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r0 == 0) goto L_0x000f;
    L_0x000d:
        goto L_0x037f;
    L_0x000f:
        r0 = r13.getPointerCount();
        r2 = 1;
        if (r0 != r2) goto L_0x002c;
    L_0x0016:
        r0 = r12.gestureDetector;
        r0 = r0.onTouchEvent(r13);
        if (r0 == 0) goto L_0x002c;
    L_0x001e:
        r0 = r12.doubleTap;
        if (r0 == 0) goto L_0x002c;
    L_0x0022:
        r12.doubleTap = r1;
        r12.moving = r1;
        r12.zooming = r1;
        r12.checkMinMax(r1);
        return r2;
    L_0x002c:
        r0 = r13.getActionMasked();
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = 2;
        if (r0 == 0) goto L_0x02fc;
    L_0x0035:
        r0 = r13.getActionMasked();
        r7 = 5;
        if (r0 != r7) goto L_0x003e;
    L_0x003c:
        goto L_0x02fc;
    L_0x003e:
        r0 = r13.getActionMasked();
        r7 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = 0;
        if (r0 != r6) goto L_0x01fe;
    L_0x0049:
        r0 = r13.getPointerCount();
        if (r0 != r6) goto L_0x00c4;
    L_0x004f:
        r0 = r12.draggingDown;
        if (r0 != 0) goto L_0x00c4;
    L_0x0053:
        r0 = r12.zooming;
        if (r0 == 0) goto L_0x00c4;
    L_0x0057:
        r12.discardTap = r2;
        r0 = r13.getX(r2);
        r3 = r13.getX(r1);
        r0 = r0 - r3;
        r3 = (double) r0;
        r0 = r13.getY(r2);
        r13 = r13.getY(r1);
        r0 = r0 - r13;
        r7 = (double) r0;
        r2 = java.lang.Math.hypot(r3, r7);
        r13 = (float) r2;
        r0 = r12.pinchStartDistance;
        r13 = r13 / r0;
        r0 = r12.pinchStartScale;
        r13 = r13 * r0;
        r12.scale = r13;
        r13 = r12.pinchCenterX;
        r0 = r12.getContainerViewWidth();
        r0 = r0 / r6;
        r0 = (float) r0;
        r13 = r13 - r0;
        r0 = r12.pinchCenterX;
        r2 = r12.getContainerViewWidth();
        r2 = r2 / r6;
        r2 = (float) r2;
        r0 = r0 - r2;
        r2 = r12.pinchStartX;
        r0 = r0 - r2;
        r2 = r12.scale;
        r3 = r12.pinchStartScale;
        r2 = r2 / r3;
        r0 = r0 * r2;
        r13 = r13 - r0;
        r12.translationX = r13;
        r13 = r12.pinchCenterY;
        r0 = r12.getContainerViewHeight();
        r0 = r0 / r6;
        r0 = (float) r0;
        r13 = r13 - r0;
        r0 = r12.pinchCenterY;
        r2 = r12.getContainerViewHeight();
        r2 = r2 / r6;
        r2 = (float) r2;
        r0 = r0 - r2;
        r2 = r12.pinchStartY;
        r0 = r0 - r2;
        r2 = r12.scale;
        r3 = r12.pinchStartScale;
        r3 = r2 / r3;
        r0 = r0 * r3;
        r13 = r13 - r0;
        r12.translationY = r13;
        r12.updateMinMax(r2);
        r13 = r12.containerView;
        r13.invalidate();
        goto L_0x037f;
    L_0x00c4:
        r0 = r13.getPointerCount();
        if (r0 != r2) goto L_0x037f;
    L_0x00ca:
        r0 = r12.velocityTracker;
        if (r0 == 0) goto L_0x00d1;
    L_0x00ce:
        r0.addMovement(r13);
    L_0x00d1:
        r0 = r13.getX();
        r6 = r12.moveStartX;
        r0 = r0 - r6;
        r0 = java.lang.Math.abs(r0);
        r6 = r13.getY();
        r10 = r12.dragY;
        r6 = r6 - r10;
        r6 = java.lang.Math.abs(r6);
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r10 = (float) r10;
        r10 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1));
        if (r10 > 0) goto L_0x00f9;
    L_0x00f0:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r10 = (float) r10;
        r10 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r10 <= 0) goto L_0x00fb;
    L_0x00f9:
        r12.discardTap = r2;
    L_0x00fb:
        r10 = r12.canDragDown;
        if (r10 == 0) goto L_0x012b;
    L_0x00ff:
        r10 = r12.draggingDown;
        if (r10 != 0) goto L_0x012b;
    L_0x0103:
        r10 = r12.scale;
        r10 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1));
        if (r10 != 0) goto L_0x012b;
    L_0x0109:
        r10 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r10 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r10 < 0) goto L_0x012b;
    L_0x0114:
        r6 = r6 / r3;
        r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1));
        if (r0 <= 0) goto L_0x012b;
    L_0x0119:
        r12.draggingDown = r2;
        r12.moving = r1;
        r13 = r13.getY();
        r12.dragY = r13;
        r13 = r12.isActionBarVisible;
        if (r13 == 0) goto L_0x012a;
    L_0x0127:
        r12.toggleActionBar(r1, r2);
    L_0x012a:
        return r2;
    L_0x012b:
        r0 = r12.draggingDown;
        if (r0 == 0) goto L_0x013f;
    L_0x012f:
        r13 = r13.getY();
        r0 = r12.dragY;
        r13 = r13 - r0;
        r12.translationY = r13;
        r13 = r12.containerView;
        r13.invalidate();
        goto L_0x037f;
    L_0x013f:
        r0 = r12.invalidCoords;
        if (r0 != 0) goto L_0x01ee;
    L_0x0143:
        r10 = r12.animationStartTime;
        r0 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x01ee;
    L_0x0149:
        r0 = r12.moveStartX;
        r3 = r13.getX();
        r0 = r0 - r3;
        r3 = r12.moveStartY;
        r4 = r13.getY();
        r3 = r3 - r4;
        r4 = r12.moving;
        if (r4 != 0) goto L_0x017b;
    L_0x015b:
        r4 = r12.scale;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x0175;
    L_0x0161:
        r4 = java.lang.Math.abs(r3);
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r4 = r4 + r5;
        r5 = java.lang.Math.abs(r0);
        r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1));
        if (r4 < 0) goto L_0x017b;
    L_0x0175:
        r4 = r12.scale;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x037f;
    L_0x017b:
        r4 = r12.moving;
        if (r4 != 0) goto L_0x0185;
    L_0x017f:
        r12.moving = r2;
        r12.canDragDown = r1;
        r0 = 0;
        r3 = 0;
    L_0x0185:
        r2 = r13.getX();
        r12.moveStartX = r2;
        r13 = r13.getY();
        r12.moveStartY = r13;
        r13 = r12.scale;
        r12.updateMinMax(r13);
        r13 = r12.translationX;
        r2 = r12.minX;
        r2 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x01a4;
    L_0x019e:
        r2 = r12.maxX;
        r13 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r13 <= 0) goto L_0x01a5;
    L_0x01a4:
        r0 = r0 / r7;
    L_0x01a5:
        r13 = r12.maxY;
        r2 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1));
        if (r2 != 0) goto L_0x01c4;
    L_0x01ab:
        r2 = r12.minY;
        r4 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r4 != 0) goto L_0x01c4;
    L_0x01b1:
        r4 = r12.translationY;
        r5 = r4 - r3;
        r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1));
        if (r5 >= 0) goto L_0x01bc;
    L_0x01b9:
        r12.translationY = r2;
        goto L_0x01d7;
    L_0x01bc:
        r4 = r4 - r3;
        r2 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1));
        if (r2 <= 0) goto L_0x01d3;
    L_0x01c1:
        r12.translationY = r13;
        goto L_0x01d7;
    L_0x01c4:
        r13 = r12.translationY;
        r2 = r12.minY;
        r2 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x01d5;
    L_0x01cc:
        r2 = r12.maxY;
        r13 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r13 <= 0) goto L_0x01d3;
    L_0x01d2:
        goto L_0x01d5;
    L_0x01d3:
        r9 = r3;
        goto L_0x01d7;
    L_0x01d5:
        r9 = r3 / r7;
    L_0x01d7:
        r13 = r12.translationX;
        r13 = r13 - r0;
        r12.translationX = r13;
        r13 = r12.scale;
        r13 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1));
        if (r13 == 0) goto L_0x01e7;
    L_0x01e2:
        r13 = r12.translationY;
        r13 = r13 - r9;
        r12.translationY = r13;
    L_0x01e7:
        r13 = r12.containerView;
        r13.invalidate();
        goto L_0x037f;
    L_0x01ee:
        r12.invalidCoords = r1;
        r0 = r13.getX();
        r12.moveStartX = r0;
        r13 = r13.getY();
        r12.moveStartY = r13;
        goto L_0x037f;
    L_0x01fe:
        r0 = r13.getActionMasked();
        r3 = 3;
        if (r0 == r3) goto L_0x0212;
    L_0x0205:
        r0 = r13.getActionMasked();
        if (r0 == r2) goto L_0x0212;
    L_0x020b:
        r0 = r13.getActionMasked();
        r3 = 6;
        if (r0 != r3) goto L_0x037f;
    L_0x0212:
        r0 = r12.zooming;
        if (r0 == 0) goto L_0x028d;
    L_0x0216:
        r12.invalidCoords = r2;
        r13 = r12.scale;
        r0 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1));
        if (r0 >= 0) goto L_0x0225;
    L_0x021e:
        r12.updateMinMax(r8);
        r12.animateTo(r8, r9, r9, r2);
        goto L_0x0289;
    L_0x0225:
        r13 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1));
        if (r13 <= 0) goto L_0x0286;
    L_0x0229:
        r13 = r12.pinchCenterX;
        r0 = r12.getContainerViewWidth();
        r0 = r0 / r6;
        r0 = (float) r0;
        r13 = r13 - r0;
        r0 = r12.pinchCenterX;
        r3 = r12.getContainerViewWidth();
        r3 = r3 / r6;
        r3 = (float) r3;
        r0 = r0 - r3;
        r3 = r12.pinchStartX;
        r0 = r0 - r3;
        r3 = r12.pinchStartScale;
        r3 = r7 / r3;
        r0 = r0 * r3;
        r13 = r13 - r0;
        r0 = r12.pinchCenterY;
        r3 = r12.getContainerViewHeight();
        r3 = r3 / r6;
        r3 = (float) r3;
        r0 = r0 - r3;
        r3 = r12.pinchCenterY;
        r4 = r12.getContainerViewHeight();
        r4 = r4 / r6;
        r4 = (float) r4;
        r3 = r3 - r4;
        r4 = r12.pinchStartY;
        r3 = r3 - r4;
        r4 = r12.pinchStartScale;
        r4 = r7 / r4;
        r3 = r3 * r4;
        r0 = r0 - r3;
        r12.updateMinMax(r7);
        r3 = r12.minX;
        r4 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1));
        if (r4 >= 0) goto L_0x026b;
    L_0x026a:
        goto L_0x0273;
    L_0x026b:
        r3 = r12.maxX;
        r4 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1));
        if (r4 <= 0) goto L_0x0272;
    L_0x0271:
        goto L_0x0273;
    L_0x0272:
        r3 = r13;
    L_0x0273:
        r13 = r12.minY;
        r4 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r4 >= 0) goto L_0x027a;
    L_0x0279:
        goto L_0x0282;
    L_0x027a:
        r13 = r12.maxY;
        r4 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r4 <= 0) goto L_0x0281;
    L_0x0280:
        goto L_0x0282;
    L_0x0281:
        r13 = r0;
    L_0x0282:
        r12.animateTo(r7, r3, r13, r2);
        goto L_0x0289;
    L_0x0286:
        r12.checkMinMax(r2);
    L_0x0289:
        r12.zooming = r1;
        goto L_0x037f;
    L_0x028d:
        r0 = r12.draggingDown;
        if (r0 == 0) goto L_0x02b3;
    L_0x0291:
        r0 = r12.dragY;
        r13 = r13.getY();
        r0 = r0 - r13;
        r13 = java.lang.Math.abs(r0);
        r0 = r12.getContainerViewHeight();
        r0 = (float) r0;
        r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r0 = r0 / r3;
        r13 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1));
        if (r13 <= 0) goto L_0x02ac;
    L_0x02a8:
        r12.closePhoto(r2, r1);
        goto L_0x02af;
    L_0x02ac:
        r12.animateTo(r8, r9, r9, r1);
    L_0x02af:
        r12.draggingDown = r1;
        goto L_0x037f;
    L_0x02b3:
        r13 = r12.moving;
        if (r13 == 0) goto L_0x037f;
    L_0x02b7:
        r13 = r12.translationX;
        r0 = r12.translationY;
        r3 = r12.scale;
        r12.updateMinMax(r3);
        r12.moving = r1;
        r12.canDragDown = r2;
        r2 = r12.velocityTracker;
        if (r2 == 0) goto L_0x02d3;
    L_0x02c8:
        r3 = r12.scale;
        r3 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1));
        if (r3 != 0) goto L_0x02d3;
    L_0x02ce:
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r2.computeCurrentVelocity(r3);
    L_0x02d3:
        r2 = r12.translationX;
        r3 = r12.minX;
        r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r4 >= 0) goto L_0x02dc;
    L_0x02db:
        goto L_0x02e4;
    L_0x02dc:
        r3 = r12.maxX;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 <= 0) goto L_0x02e3;
    L_0x02e2:
        goto L_0x02e4;
    L_0x02e3:
        r3 = r13;
    L_0x02e4:
        r13 = r12.translationY;
        r2 = r12.minY;
        r4 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r4 >= 0) goto L_0x02ed;
    L_0x02ec:
        goto L_0x02f5;
    L_0x02ed:
        r2 = r12.maxY;
        r13 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r13 <= 0) goto L_0x02f4;
    L_0x02f3:
        goto L_0x02f5;
    L_0x02f4:
        r2 = r0;
    L_0x02f5:
        r13 = r12.scale;
        r12.animateTo(r13, r3, r2, r1);
        goto L_0x037f;
    L_0x02fc:
        r12.discardTap = r1;
        r0 = r12.scroller;
        r0 = r0.isFinished();
        if (r0 != 0) goto L_0x030b;
    L_0x0306:
        r0 = r12.scroller;
        r0.abortAnimation();
    L_0x030b:
        r0 = r12.draggingDown;
        if (r0 != 0) goto L_0x037f;
    L_0x030f:
        r0 = r13.getPointerCount();
        if (r0 != r6) goto L_0x0360;
    L_0x0315:
        r0 = r13.getX(r2);
        r4 = r13.getX(r1);
        r0 = r0 - r4;
        r4 = (double) r0;
        r0 = r13.getY(r2);
        r6 = r13.getY(r1);
        r0 = r0 - r6;
        r6 = (double) r0;
        r4 = java.lang.Math.hypot(r4, r6);
        r0 = (float) r4;
        r12.pinchStartDistance = r0;
        r0 = r12.scale;
        r12.pinchStartScale = r0;
        r0 = r13.getX(r1);
        r4 = r13.getX(r2);
        r0 = r0 + r4;
        r0 = r0 / r3;
        r12.pinchCenterX = r0;
        r0 = r13.getY(r1);
        r13 = r13.getY(r2);
        r0 = r0 + r13;
        r0 = r0 / r3;
        r12.pinchCenterY = r0;
        r13 = r12.translationX;
        r12.pinchStartX = r13;
        r13 = r12.translationY;
        r12.pinchStartY = r13;
        r12.zooming = r2;
        r12.moving = r1;
        r13 = r12.velocityTracker;
        if (r13 == 0) goto L_0x037f;
    L_0x035c:
        r13.clear();
        goto L_0x037f;
    L_0x0360:
        r0 = r13.getPointerCount();
        if (r0 != r2) goto L_0x037f;
    L_0x0366:
        r0 = r13.getX();
        r12.moveStartX = r0;
        r13 = r13.getY();
        r12.moveStartY = r13;
        r12.dragY = r13;
        r12.draggingDown = r1;
        r12.canDragDown = r2;
        r13 = r12.velocityTracker;
        if (r13 == 0) goto L_0x037f;
    L_0x037c:
        r13.clear();
    L_0x037f:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretMediaViewer.processTouchEvent(android.view.MotionEvent):boolean");
    }

    private void checkMinMax(boolean z) {
        float f = this.translationX;
        float f2 = this.translationY;
        updateMinMax(this.scale);
        float f3 = this.translationX;
        float f4 = this.minX;
        if (f3 >= f4) {
            f4 = this.maxX;
            if (f3 <= f4) {
                f4 = f;
            }
        }
        f = this.translationY;
        f3 = this.minY;
        if (f >= f3) {
            f3 = this.maxY;
            if (f <= f3) {
                f3 = f2;
            }
        }
        animateTo(this.scale, f4, f3, z);
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
            this.imageMoveAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.imageMoveAnimation;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f});
            animatorSet.playTogether(animatorArr);
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) i);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SecretMediaViewer.this.imageMoveAnimation = null;
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
        if (this.scale != 1.0f) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(f), Math.round(f2), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.containerView.postInvalidate();
        }
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if (this.discardTap) {
            return false;
        }
        toggleActionBar(this.isActionBarVisible ^ 1, true);
        return true;
    }

    public boolean onDoubleTap(MotionEvent motionEvent) {
        boolean z = false;
        if (this.scale == 1.0f && (this.translationY != 0.0f || this.translationX != 0.0f)) {
            return false;
        }
        if (this.animationStartTime == 0 && this.photoAnimationInProgress == 0) {
            z = true;
            if (this.scale == 1.0f) {
                float x = (motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - (((motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - this.translationX) * (3.0f / this.scale));
                float y = (motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - (((motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - this.translationY) * (3.0f / this.scale));
                updateMinMax(3.0f);
                float f = this.minX;
                if (x >= f) {
                    f = this.maxX;
                    if (x <= f) {
                        f = x;
                    }
                }
                x = this.minY;
                if (y >= x) {
                    x = this.maxY;
                    if (y <= x) {
                        x = y;
                    }
                }
                animateTo(3.0f, f, x, true);
            } else {
                animateTo(1.0f, 0.0f, 0.0f, true);
            }
            this.doubleTap = true;
        }
        return z;
    }
}
