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
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.annotation.Keep;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Scroller;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class SecretMediaViewer implements OnDoubleTapListener, OnGestureListener, NotificationCenterDelegate {
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
    private PhotoBackgroundDrawable photoBackgroundDrawable = new PhotoBackgroundDrawable(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
    private long photoTransitionAnimationStartTime;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale = 1.0f;
    private float pinchStartX;
    private float pinchStartY;
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

    /* renamed from: org.telegram.ui.SecretMediaViewer$4 */
    class C16674 implements OnApplyWindowInsetsListener {
        C16674() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            WindowInsets windowInsets2 = (WindowInsets) SecretMediaViewer.this.lastInsets;
            SecretMediaViewer.this.lastInsets = windowInsets;
            if (windowInsets2 == null || windowInsets2.toString().equals(windowInsets.toString()) == null) {
                SecretMediaViewer.this.windowView.requestLayout();
            }
            return windowInsets.consumeSystemWindowInsets();
        }
    }

    /* renamed from: org.telegram.ui.SecretMediaViewer$6 */
    class C16686 implements Runnable {
        C16686() {
        }

        public void run() {
            SecretMediaViewer.this.photoAnimationInProgress = 0;
            SecretMediaViewer.this.imageMoveAnimation = null;
            if (SecretMediaViewer.this.containerView != null) {
                if (VERSION.SDK_INT >= 18) {
                    SecretMediaViewer.this.containerView.setLayerType(0, null);
                }
                SecretMediaViewer.this.containerView.invalidate();
            }
        }
    }

    /* renamed from: org.telegram.ui.SecretMediaViewer$7 */
    class C16697 extends AnimatorListenerAdapter {
        C16697() {
        }

        public void onAnimationEnd(Animator animator) {
            if (SecretMediaViewer.this.photoAnimationEndRunnable != null) {
                SecretMediaViewer.this.photoAnimationEndRunnable.run();
                SecretMediaViewer.this.photoAnimationEndRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.SecretMediaViewer$9 */
    class C16719 extends AnimatorListenerAdapter {
        C16719() {
        }

        public void onAnimationEnd(Animator animator) {
            if (SecretMediaViewer.this.currentActionBarAnimation != null && SecretMediaViewer.this.currentActionBarAnimation.equals(animator) != null) {
                SecretMediaViewer.this.actionBar.setVisibility(8);
                SecretMediaViewer.this.currentActionBarAnimation = null;
            }
        }
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(null);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            SecretMediaViewer.this.processTouchEvent(motionEvent);
            return true;
        }

        protected void onDraw(Canvas canvas) {
            SecretMediaViewer.this.onDraw(canvas);
        }

        protected boolean drawChild(Canvas canvas, View view, long j) {
            return (view == SecretMediaViewer.this.aspectRatioFrameLayout || super.drawChild(canvas, view, j) == null) ? null : true;
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
                boolean z;
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) SecretMediaViewer.this.parentActivity).drawerLayoutContainer;
                if (SecretMediaViewer.this.isPhotoVisible) {
                    if (i == 255) {
                        z = false;
                        drawerLayoutContainer.setAllowDrawContent(z);
                    }
                }
                z = true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            super.setAlpha(i);
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (getAlpha() != null) {
                if (this.frame != 2 || this.drawRunnable == null) {
                    invalidateSelf();
                } else {
                    this.drawRunnable.run();
                    this.drawRunnable = null;
                }
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
            /* renamed from: x */
            float f30x;
            /* renamed from: y */
            float f31y;

            private Particle() {
            }
        }

        public SecretDeleteTimer(Context context) {
            super(context);
            SecretMediaViewer secretMediaViewer = null;
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
            this.circlePaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.drawable = context.getResources().getDrawable(C0446R.drawable.flame_small);
            while (secretMediaViewer < 40) {
                this.freeParticles.add(new Particle());
                secretMediaViewer++;
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
                if (particle.currentTime >= particle.lifeTime) {
                    if (this.freeParticles.size() < 40) {
                        this.freeParticles.add(particle);
                    }
                    this.particles.remove(i);
                    i--;
                    size--;
                } else {
                    particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(particle.currentTime / particle.lifeTime);
                    float f = (float) j;
                    particle.f30x += ((particle.vx * particle.velocity) * f) / 500.0f;
                    particle.f31y += ((particle.vy * particle.velocity) * f) / 500.0f;
                    particle.currentTime += f;
                }
                i++;
            }
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            i = (getMeasuredHeight() / 2) - (AndroidUtilities.dp(28.0f) / 2);
            this.deleteProgressRect.set((float) (getMeasuredWidth() - AndroidUtilities.dp(49.0f)), (float) i, (float) (getMeasuredWidth() - AndroidUtilities.dp(21.0f)), (float) (i + AndroidUtilities.dp(28.0f)));
        }

        @SuppressLint({"DrawAllocation"})
        protected void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            if (SecretMediaViewer.this.currentMessageObject != null) {
                if (SecretMediaViewer.this.currentMessageObject.messageOwner.destroyTime != 0) {
                    long duration;
                    float f;
                    canvas2.drawCircle((float) (getMeasuredWidth() - AndroidUtilities.dp(35.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(16.0f), r0.circlePaint);
                    if (r0.useVideoProgress) {
                        if (SecretMediaViewer.this.videoPlayer != null) {
                            duration = SecretMediaViewer.this.videoPlayer.getDuration();
                            long currentPosition = SecretMediaViewer.this.videoPlayer.getCurrentPosition();
                            if (!(duration == C0542C.TIME_UNSET || currentPosition == C0542C.TIME_UNSET)) {
                                f = 1.0f - (((float) currentPosition) / ((float) duration));
                            }
                        }
                        f = 1.0f;
                    } else {
                        f = ((float) Math.max(0, r0.destroyTime - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance(SecretMediaViewer.this.currentAccount).getTimeDifference() * 1000))))) / (((float) r0.destroyTtl) * 1000.0f);
                    }
                    int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(40.0f);
                    int measuredHeight = ((getMeasuredHeight() - AndroidUtilities.dp(14.0f)) / 2) - AndroidUtilities.dp(0.5f);
                    r0.drawable.setBounds(measuredWidth, measuredHeight, AndroidUtilities.dp(10.0f) + measuredWidth, AndroidUtilities.dp(14.0f) + measuredHeight);
                    r0.drawable.draw(canvas2);
                    float f2 = -360.0f * f;
                    canvas2.drawArc(r0.deleteProgressRect, -90.0f, f2, false, r0.afterDeleteProgressPaint);
                    int size = r0.particles.size();
                    for (measuredHeight = 0; measuredHeight < size; measuredHeight++) {
                        Particle particle = (Particle) r0.particles.get(measuredHeight);
                        r0.particlePaint.setAlpha((int) (255.0f * particle.alpha));
                        canvas2.drawPoint(particle.f30x, particle.f31y, r0.particlePaint);
                    }
                    double d = ((double) (f2 - 90.0f)) * 0.017453292519943295d;
                    double sin = Math.sin(d);
                    d = -Math.cos(d);
                    double dp = (double) AndroidUtilities.dp(14.0f);
                    f = (float) (((-d) * dp) + ((double) r0.deleteProgressRect.centerX()));
                    float centerY = (float) ((dp * sin) + ((double) r0.deleteProgressRect.centerY()));
                    for (int i = 0; i < 1; i++) {
                        Particle particle2;
                        if (r0.freeParticles.isEmpty()) {
                            particle2 = new Particle();
                        } else {
                            particle2 = (Particle) r0.freeParticles.get(0);
                            r0.freeParticles.remove(0);
                        }
                        particle2.f30x = f;
                        particle2.f31y = centerY;
                        double nextInt = ((double) (Utilities.random.nextInt(140) - 70)) * 0.017453292519943295d;
                        if (nextInt < 0.0d) {
                            nextInt += 6.283185307179586d;
                        }
                        particle2.vx = (float) ((Math.cos(nextInt) * sin) - (Math.sin(nextInt) * d));
                        particle2.vy = (float) ((Math.sin(nextInt) * sin) + (Math.cos(nextInt) * d));
                        particle2.alpha = 1.0f;
                        particle2.currentTime = 0.0f;
                        particle2.lifeTime = (float) (400 + Utilities.random.nextInt(100));
                        particle2.velocity = 20.0f + (Utilities.random.nextFloat() * 4.0f);
                        r0.particles.add(particle2);
                    }
                    duration = System.currentTimeMillis();
                    updateParticles(duration - r0.lastAnimationTime);
                    r0.lastAnimationTime = duration;
                    invalidate();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.SecretMediaViewer$1 */
    class C22711 implements VideoPlayerDelegate {
        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        C22711() {
        }

        public void onStateChanged(boolean z, int i) {
            if (SecretMediaViewer.this.videoPlayer) {
                if (SecretMediaViewer.this.currentMessageObject) {
                    if (i == 4 || i == 1) {
                        try {
                            SecretMediaViewer.this.parentActivity.getWindow().clearFlags(128);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    } else {
                        try {
                            SecretMediaViewer.this.parentActivity.getWindow().addFlags(128);
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                    if (i == true && SecretMediaViewer.this.aspectRatioFrameLayout.getVisibility()) {
                        SecretMediaViewer.this.aspectRatioFrameLayout.setVisibility(0);
                    }
                    if (!SecretMediaViewer.this.videoPlayer.isPlaying() || i == 4) {
                        if (SecretMediaViewer.this.isPlaying) {
                            SecretMediaViewer.this.isPlaying = false;
                            if (i == 4) {
                                SecretMediaViewer.this.videoWatchedOneTime = true;
                                if (SecretMediaViewer.this.closeVideoAfterWatch) {
                                    SecretMediaViewer.this.closePhoto(true, true);
                                } else {
                                    SecretMediaViewer.this.videoPlayer.seekTo(0);
                                    SecretMediaViewer.this.videoPlayer.play();
                                }
                            }
                        }
                    } else if (!SecretMediaViewer.this.isPlaying) {
                        SecretMediaViewer.this.isPlaying = true;
                    }
                }
            }
        }

        public void onError(Exception exception) {
            FileLog.m3e((Throwable) exception);
        }

        public void onVideoSizeChanged(int i, int i2, int i3, float f) {
            if (SecretMediaViewer.this.aspectRatioFrameLayout != null) {
                if (i3 != 90) {
                    if (i3 != 270) {
                        int i4 = i2;
                        i2 = i;
                        i = i4;
                    }
                }
                SecretMediaViewer.this.aspectRatioFrameLayout.setAspectRatio(i == 0 ? NUM : (((float) i2) * f) / ((float) i), i3);
            }
        }

        public void onRenderedFirstFrame() {
            if (!SecretMediaViewer.this.textureUploaded) {
                SecretMediaViewer.this.textureUploaded = true;
                SecretMediaViewer.this.containerView.invalidate();
            }
        }
    }

    /* renamed from: org.telegram.ui.SecretMediaViewer$5 */
    class C22735 extends ActionBarMenuOnItemClick {
        C22735() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                SecretMediaViewer.this.closePhoto(true, false);
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
            if (!(this.currentMessageObject == 0 || ((Integer) objArr[1]).intValue() != 0 || ((ArrayList) objArr[0]).contains(Integer.valueOf(this.currentMessageObject.getId())) == 0)) {
                if (this.isVideo == 0 || this.videoWatchedOneTime != 0) {
                    closePhoto(true, true);
                } else {
                    this.closeVideoAfterWatch = true;
                }
            }
        } else if (i == NotificationCenter.didCreatedNewDeleteTask) {
            if (this.currentMessageObject != 0) {
                if (this.secretDeleteTimer != 0) {
                    SparseArray sparseArray = (SparseArray) objArr[0];
                    for (i2 = 0; i2 < sparseArray.size(); i2++) {
                        objArr = sparseArray.keyAt(i2);
                        ArrayList arrayList = (ArrayList) sparseArray.get(objArr);
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
                                this.currentMessageObject.messageOwner.destroyTime = objArr;
                                this.secretDeleteTimer.invalidate();
                                return;
                            }
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.updateMessageMedia) {
            if (this.currentMessageObject.getId() == ((Message) objArr[0]).id) {
                if (this.isVideo == 0 || this.videoWatchedOneTime != 0) {
                    closePhoto(true, true);
                } else {
                    this.closeVideoAfterWatch = true;
                }
            }
        }
    }

    private void preparePlayer(File file) {
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
                this.videoPlayer.setDelegate(new C22711());
            }
            this.videoPlayer.preparePlayer(Uri.fromFile(file), "other");
            this.videoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (this.videoPlayer != null) {
            this.videoPlayer.releasePlayer();
            this.videoPlayer = null;
        }
        try {
            if (this.parentActivity != null) {
                this.parentActivity.getWindow().clearFlags(128);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        if (this.aspectRatioFrameLayout != null) {
            this.containerView.removeView(this.aspectRatioFrameLayout);
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
                protected void onMeasure(int i, int i2) {
                    i = MeasureSpec.getSize(i);
                    i2 = MeasureSpec.getSize(i2);
                    if (VERSION.SDK_INT >= 21 && SecretMediaViewer.this.lastInsets != null) {
                        WindowInsets windowInsets = (WindowInsets) SecretMediaViewer.this.lastInsets;
                        if (AndroidUtilities.incorrectDisplaySizeFix) {
                            if (i2 > AndroidUtilities.displaySize.y) {
                                i2 = AndroidUtilities.displaySize.y;
                            }
                            i2 += AndroidUtilities.statusBarHeight;
                        }
                        i2 -= windowInsets.getSystemWindowInsetBottom();
                        i -= windowInsets.getSystemWindowInsetRight();
                    } else if (i2 > AndroidUtilities.displaySize.y) {
                        i2 = AndroidUtilities.displaySize.y;
                    }
                    setMeasuredDimension(i, i2);
                    if (VERSION.SDK_INT >= 21 && SecretMediaViewer.this.lastInsets != null) {
                        i -= ((WindowInsets) SecretMediaViewer.this.lastInsets).getSystemWindowInsetLeft();
                    }
                    SecretMediaViewer.this.containerView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                }

                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    i = (VERSION.SDK_INT < 21 || SecretMediaViewer.this.lastInsets == 0) ? 0 : ((WindowInsets) SecretMediaViewer.this.lastInsets).getSystemWindowInsetLeft() + 0;
                    SecretMediaViewer.this.containerView.layout(i, 0, SecretMediaViewer.this.containerView.getMeasuredWidth() + i, SecretMediaViewer.this.containerView.getMeasuredHeight());
                    if (z) {
                        if (!SecretMediaViewer.this.imageMoveAnimation) {
                            SecretMediaViewer.this.scale = NUM;
                            SecretMediaViewer.this.translationX = 0.0f;
                            SecretMediaViewer.this.translationY = 0.0f;
                        }
                        SecretMediaViewer.this.updateMinMax(SecretMediaViewer.this.scale);
                    }
                }
            };
            this.windowView.setBackgroundDrawable(this.photoBackgroundDrawable);
            this.windowView.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            this.containerView = new FrameLayoutDrawer(activity) {
                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    if (SecretMediaViewer.this.secretDeleteTimer) {
                        z = ((ActionBar.getCurrentActionBarHeight() - SecretMediaViewer.this.secretDeleteTimer.getMeasuredHeight()) / 2) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                        SecretMediaViewer.this.secretDeleteTimer.layout(SecretMediaViewer.this.secretDeleteTimer.getLeft(), z, SecretMediaViewer.this.secretDeleteTimer.getRight(), SecretMediaViewer.this.secretDeleteTimer.getMeasuredHeight() + z);
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
                this.containerView.setOnApplyWindowInsetsListener(new C16674());
                this.containerView.setSystemUiVisibility(1280);
            }
            this.gestureDetector = new GestureDetector(this.containerView.getContext(), this);
            this.gestureDetector.setOnDoubleTapListener(this);
            this.actionBar = new ActionBar(activity);
            this.actionBar.setTitleColor(-1);
            this.actionBar.setSubtitleColor(-1);
            this.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.actionBar.setOccupyStatusBar(VERSION.SDK_INT >= 21);
            this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
            this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
            this.actionBar.setTitleRightMargin(AndroidUtilities.dp(70.0f));
            this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.actionBar.setActionBarMenuOnItemClick(new C22735());
            this.secretDeleteTimer = new SecretDeleteTimer(activity);
            this.containerView.addView(this.secretDeleteTimer, LayoutHelper.createFrame(119, 48.0f, 53, 0.0f, 0.0f, 0.0f, 0.0f));
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 48;
            this.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -NUM;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.centerImage.setParentView(this.containerView);
            this.centerImage.setForceCrossfade(true);
        }
    }

    public void openMedia(MessageObject messageObject, PhotoViewerProvider photoViewerProvider) {
        MessageObject messageObject2 = messageObject;
        PhotoViewerProvider photoViewerProvider2 = photoViewerProvider;
        if (!(this.parentActivity == null || messageObject2 == null || !messageObject.needDrawBluredPreview())) {
            if (photoViewerProvider2 != null) {
                final PlaceProviderObject placeForPhoto = photoViewerProvider2.getPlaceForPhoto(messageObject2, null, 0);
                if (placeForPhoto != null) {
                    r1.currentProvider = photoViewerProvider2;
                    r1.openTime = System.currentTimeMillis();
                    r1.closeTime = 0;
                    r1.isActionBarVisible = true;
                    r1.isPhotoVisible = true;
                    r1.draggingDown = false;
                    if (r1.aspectRatioFrameLayout != null) {
                        r1.aspectRatioFrameLayout.setVisibility(4);
                    }
                    releasePlayer();
                    r1.pinchStartDistance = 0.0f;
                    r1.pinchStartScale = 1.0f;
                    r1.pinchCenterX = 0.0f;
                    r1.pinchCenterY = 0.0f;
                    r1.pinchStartX = 0.0f;
                    r1.pinchStartY = 0.0f;
                    r1.moveStartX = 0.0f;
                    r1.moveStartY = 0.0f;
                    r1.zooming = false;
                    r1.moving = false;
                    r1.doubleTap = false;
                    r1.invalidCoords = false;
                    r1.canDragDown = true;
                    updateMinMax(r1.scale);
                    r1.photoBackgroundDrawable.setAlpha(0);
                    r1.containerView.setAlpha(1.0f);
                    r1.containerView.setVisibility(0);
                    r1.secretDeleteTimer.setAlpha(1.0f);
                    r1.isVideo = false;
                    r1.videoWatchedOneTime = false;
                    r1.closeVideoAfterWatch = false;
                    r1.disableShowCheck = true;
                    r1.centerImage.setManualAlphaAnimator(false);
                    Rect drawRegion = placeForPhoto.imageReceiver.getDrawRegion();
                    float f = (float) (drawRegion.right - drawRegion.left);
                    float f2 = (float) (drawRegion.bottom - drawRegion.top);
                    int i = AndroidUtilities.displaySize.x;
                    int i2 = AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                    r1.scale = Math.max(f / ((float) i), f2 / ((float) i2));
                    r1.translationX = (((float) (placeForPhoto.viewX + drawRegion.left)) + (f / 2.0f)) - ((float) (i / 2));
                    r1.translationY = (((float) (placeForPhoto.viewY + drawRegion.top)) + (f2 / 2.0f)) - ((float) (i2 / 2));
                    r1.clipHorizontal = (float) Math.abs(drawRegion.left - placeForPhoto.imageReceiver.getImageX());
                    int abs = Math.abs(drawRegion.top - placeForPhoto.imageReceiver.getImageY());
                    int[] iArr = new int[2];
                    placeForPhoto.parentView.getLocationInWindow(iArr);
                    r1.clipTop = (float) (((iArr[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (placeForPhoto.viewY + drawRegion.top)) + placeForPhoto.clipTopAddition);
                    if (r1.clipTop < 0.0f) {
                        r1.clipTop = 0.0f;
                    }
                    r1.clipBottom = (float) ((((placeForPhoto.viewY + drawRegion.top) + ((int) f2)) - ((iArr[1] + placeForPhoto.parentView.getHeight()) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) + placeForPhoto.clipBottomAddition);
                    if (r1.clipBottom < 0.0f) {
                        r1.clipBottom = 0.0f;
                    }
                    float f3 = (float) abs;
                    r1.clipTop = Math.max(r1.clipTop, f3);
                    r1.clipBottom = Math.max(r1.clipBottom, f3);
                    r1.animationStartTime = System.currentTimeMillis();
                    r1.animateToX = 0.0f;
                    r1.animateToY = 0.0f;
                    r1.animateToClipBottom = 0.0f;
                    r1.animateToClipHorizontal = 0.0f;
                    r1.animateToClipTop = 0.0f;
                    r1.animateToScale = 1.0f;
                    r1.zoomAnimation = true;
                    NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messagesDeleted);
                    NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.updateMessageMedia);
                    NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.didCreatedNewDeleteTask);
                    r1.currentChannelId = messageObject2.messageOwner.to_id != null ? messageObject2.messageOwner.to_id.channel_id : 0;
                    toggleActionBar(true, false);
                    r1.currentMessageObject = messageObject2;
                    TLObject document = messageObject.getDocument();
                    if (r1.currentThumb != null) {
                        r1.currentThumb.release();
                        r1.currentThumb = null;
                    }
                    r1.currentThumb = placeForPhoto.imageReceiver.getThumbBitmapSafe();
                    if (document == null) {
                        r1.actionBar.setTitle(LocaleController.getString("DisappearingPhoto", C0446R.string.DisappearingPhoto));
                        r1.centerImage.setImage(FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, AndroidUtilities.getPhotoSize()).location, null, r1.currentThumb != null ? new BitmapDrawable(r1.currentThumb.bitmap) : null, -1, null, 2);
                        r1.secretDeleteTimer.setDestroyTime(((long) messageObject2.messageOwner.destroyTime) * 1000, (long) messageObject2.messageOwner.ttl, false);
                    } else if (MessageObject.isGifDocument((Document) document)) {
                        r1.actionBar.setTitle(LocaleController.getString("DisappearingGif", C0446R.string.DisappearingGif));
                        r1.centerImage.setImage(document, null, r1.currentThumb != null ? new BitmapDrawable(r1.currentThumb.bitmap) : null, -1, null, 1);
                        r1.secretDeleteTimer.setDestroyTime(((long) messageObject2.messageOwner.destroyTime) * 1000, (long) messageObject2.messageOwner.ttl, false);
                    } else {
                        r1.actionBar.setTitle(LocaleController.getString("DisappearingVideo", C0446R.string.DisappearingVideo));
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
                        r1.isVideo = true;
                        r1.centerImage.setImage(null, null, r1.currentThumb != null ? new BitmapDrawable(r1.currentThumb.bitmap) : null, -1, null, 2);
                        if (((long) (messageObject.getDuration() * 1000)) > (((long) messageObject2.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance(r1.currentAccount).getTimeDifference() * 1000)))) {
                            r1.secretDeleteTimer.setDestroyTime(-1, -1, true);
                        } else {
                            r1.secretDeleteTimer.setDestroyTime(((long) messageObject2.messageOwner.destroyTime) * 1000, (long) messageObject2.messageOwner.ttl, false);
                        }
                    }
                    try {
                        if (r1.windowView.getParent() != null) {
                            ((WindowManager) r1.parentActivity.getSystemService("window")).removeView(r1.windowView);
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    ((WindowManager) r1.parentActivity.getSystemService("window")).addView(r1.windowView, r1.windowLayoutParams);
                    r1.secretDeleteTimer.invalidate();
                    r1.isVisible = true;
                    r1.imageMoveAnimation = new AnimatorSet();
                    r1.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(r1.actionBar, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(r1.secretDeleteTimer, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofInt(r1.photoBackgroundDrawable, "alpha", new int[]{0, 255}), ObjectAnimator.ofFloat(r1.secretDeleteTimer, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(r1, "animationValue", new float[]{0.0f, 1.0f})});
                    r1.photoAnimationInProgress = 3;
                    r1.photoAnimationEndRunnable = new C16686();
                    r1.imageMoveAnimation.setDuration(250);
                    r1.imageMoveAnimation.addListener(new C16697());
                    r1.photoTransitionAnimationStartTime = System.currentTimeMillis();
                    if (VERSION.SDK_INT >= 18) {
                        r1.containerView.setLayerType(2, null);
                    }
                    r1.imageMoveAnimation.setInterpolator(new DecelerateInterpolator());
                    r1.photoBackgroundDrawable.frame = 0;
                    r1.photoBackgroundDrawable.drawRunnable = new Runnable() {
                        public void run() {
                            SecretMediaViewer.this.disableShowCheck = false;
                            placeForPhoto.imageReceiver.setVisible(false, true);
                        }
                    };
                    r1.imageMoveAnimation.start();
                }
            }
        }
    }

    public boolean isShowingImage(MessageObject messageObject) {
        return (!this.isVisible || this.disableShowCheck || messageObject == null || this.currentMessageObject == null || this.currentMessageObject.getId() != messageObject.getId()) ? null : true;
    }

    private void toggleActionBar(boolean z, boolean z2) {
        if (z) {
            this.actionBar.setVisibility(0);
        }
        this.actionBar.setEnabled(z);
        this.isActionBarVisible = z;
        float f = 0.0f;
        if (z2) {
            z2 = new ArrayList();
            ActionBar actionBar = this.actionBar;
            String str = "alpha";
            float[] fArr = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr[0] = f;
            z2.add(ObjectAnimator.ofFloat(actionBar, str, fArr));
            this.currentActionBarAnimation = new AnimatorSet();
            this.currentActionBarAnimation.playTogether(z2);
            if (!z) {
                this.currentActionBarAnimation.addListener(new C16719());
            }
            this.currentActionBarAnimation.setDuration(200);
            this.currentActionBarAnimation.start();
            return;
        }
        z2 = this.actionBar;
        if (z) {
            f = 1.0f;
        }
        z2.setAlpha(f);
        if (!z) {
            this.actionBar.setVisibility(true);
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
        if (this.currentThumb != null) {
            this.currentThumb.release();
            this.currentThumb = null;
        }
        releasePlayer();
        if (!(this.parentActivity == null || this.windowView == null)) {
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        Instance = null;
    }

    private void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.isPhotoVisible) {
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            float f6;
            float f7;
            float containerViewHeight;
            int bitmapWidth;
            int bitmapHeight;
            int i;
            float f8;
            int i2;
            float f9;
            long currentTimeMillis;
            long j;
            boolean z = false;
            if (r0.imageMoveAnimation != null) {
                if (!r0.scroller.isFinished()) {
                    r0.scroller.abortAnimation();
                }
                if (r0.useOvershootForScale) {
                    if (r0.animationValue < 0.9f) {
                        f = r0.animationValue / 0.9f;
                        f2 = r0.scale + (((r0.animateToScale * 1.02f) - r0.scale) * f);
                    } else {
                        f2 = r0.animateToScale + ((r0.animateToScale * 0.01999998f) * (1.0f - ((r0.animationValue - 0.9f) / 0.100000024f)));
                        f = 1.0f;
                    }
                    f3 = r0.translationX + ((r0.animateToX - r0.translationX) * f);
                    f4 = r0.clipTop + ((r0.animateToClipTop - r0.clipTop) * f);
                    f5 = r0.clipBottom + ((r0.animateToClipBottom - r0.clipBottom) * f);
                    f6 = r0.clipHorizontal + ((r0.animateToClipHorizontal - r0.clipHorizontal) * f);
                    f = r0.translationY + ((r0.animateToY - r0.translationY) * f);
                } else {
                    f2 = ((r0.animateToScale - r0.scale) * r0.animationValue) + r0.scale;
                    f = r0.translationY + ((r0.animateToY - r0.translationY) * r0.animationValue);
                    f3 = ((r0.animateToX - r0.translationX) * r0.animationValue) + r0.translationX;
                    f4 = ((r0.animateToClipTop - r0.clipTop) * r0.animationValue) + r0.clipTop;
                    f5 = ((r0.animateToClipBottom - r0.clipBottom) * r0.animationValue) + r0.clipBottom;
                    f6 = ((r0.animateToClipHorizontal - r0.clipHorizontal) * r0.animationValue) + r0.clipHorizontal;
                }
                f7 = (r0.animateToScale == 1.0f && r0.scale == 1.0f && r0.translationX == 0.0f) ? f : -1.0f;
                r0.containerView.invalidate();
            } else {
                if (r0.animationStartTime != 0) {
                    r0.translationX = r0.animateToX;
                    r0.translationY = r0.animateToY;
                    r0.clipBottom = r0.animateToClipBottom;
                    r0.clipTop = r0.animateToClipTop;
                    r0.clipHorizontal = r0.animateToClipHorizontal;
                    r0.scale = r0.animateToScale;
                    r0.animationStartTime = 0;
                    updateMinMax(r0.scale);
                    r0.zoomAnimation = false;
                    r0.useOvershootForScale = false;
                }
                if (!r0.scroller.isFinished() && r0.scroller.computeScrollOffset()) {
                    if (((float) r0.scroller.getStartX()) < r0.maxX && ((float) r0.scroller.getStartX()) > r0.minX) {
                        r0.translationX = (float) r0.scroller.getCurrX();
                    }
                    if (((float) r0.scroller.getStartY()) < r0.maxY && ((float) r0.scroller.getStartY()) > r0.minY) {
                        r0.translationY = (float) r0.scroller.getCurrY();
                    }
                    r0.containerView.invalidate();
                }
                f2 = r0.scale;
                f = r0.translationY;
                f3 = r0.translationX;
                f4 = r0.clipTop;
                f5 = r0.clipBottom;
                f6 = r0.clipHorizontal;
                f7 = !r0.moving ? r0.translationY : -1.0f;
            }
            if (r0.photoAnimationInProgress != 3) {
                if (r0.scale != 1.0f || f7 == -1.0f || r0.zoomAnimation) {
                    r0.photoBackgroundDrawable.setAlpha(255);
                } else {
                    containerViewHeight = ((float) getContainerViewHeight()) / 4.0f;
                    r0.photoBackgroundDrawable.setAlpha((int) Math.max(127.0f, 255.0f * (1.0f - (Math.min(Math.abs(f7), containerViewHeight) / containerViewHeight))));
                }
                if (!r0.zoomAnimation && f3 > r0.maxX) {
                    containerViewHeight = Math.min(1.0f, (f3 - r0.maxX) / ((float) canvas.getWidth()));
                    f7 = 0.3f * containerViewHeight;
                    containerViewHeight = 1.0f - containerViewHeight;
                    f3 = r0.maxX;
                    if (r0.aspectRatioFrameLayout != null && r0.aspectRatioFrameLayout.getVisibility() == 0) {
                        z = true;
                    }
                    canvas.save();
                    f2 -= f7;
                    canvas2.translate(((float) (getContainerViewWidth() / 2)) + f3, ((float) (getContainerViewHeight() / 2)) + f);
                    canvas2.scale(f2, f2);
                    bitmapWidth = r0.centerImage.getBitmapWidth();
                    bitmapHeight = r0.centerImage.getBitmapHeight();
                    if (z && r0.textureUploaded && Math.abs((((float) bitmapWidth) / ((float) bitmapHeight)) - (((float) r0.videoTextureView.getMeasuredWidth()) / ((float) r0.videoTextureView.getMeasuredHeight()))) > 0.01f) {
                        bitmapWidth = r0.videoTextureView.getMeasuredWidth();
                        bitmapHeight = r0.videoTextureView.getMeasuredHeight();
                    }
                    f7 = (float) bitmapHeight;
                    f = (float) bitmapWidth;
                    f3 = Math.min(((float) getContainerViewHeight()) / f7, ((float) getContainerViewWidth()) / f);
                    bitmapWidth = (int) (f * f3);
                    bitmapHeight = (int) (f7 * f3);
                    i = (-bitmapWidth) / 2;
                    f8 = (float) i;
                    f6 /= f2;
                    i2 = (-bitmapHeight) / 2;
                    f9 = (float) i2;
                    canvas2.clipRect(f8 + f6, (f4 / f2) + f9, ((float) (bitmapWidth / 2)) - f6, ((float) (bitmapHeight / 2)) - (f5 / f2));
                    if (!(z && r0.textureUploaded && r0.videoCrossfadeStarted && r0.videoCrossfadeAlpha == 1.0f)) {
                        r0.centerImage.setAlpha(containerViewHeight);
                        r0.centerImage.setImageCoords(i, i2, bitmapWidth, bitmapHeight);
                        r0.centerImage.draw(canvas2);
                    }
                    if (z) {
                        if (!r0.videoCrossfadeStarted && r0.textureUploaded) {
                            r0.videoCrossfadeStarted = true;
                            r0.videoCrossfadeAlpha = 0.0f;
                            r0.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                        }
                        canvas2.translate(f8, f9);
                        r0.videoTextureView.setAlpha(containerViewHeight * r0.videoCrossfadeAlpha);
                        r0.aspectRatioFrameLayout.draw(canvas2);
                        if (r0.videoCrossfadeStarted && r0.videoCrossfadeAlpha < 1.0f) {
                            currentTimeMillis = System.currentTimeMillis();
                            j = currentTimeMillis - r0.videoCrossfadeAlphaLastTime;
                            r0.videoCrossfadeAlphaLastTime = currentTimeMillis;
                            r0.videoCrossfadeAlpha += ((float) j) / 200.0f;
                            r0.containerView.invalidate();
                            if (r0.videoCrossfadeAlpha > 1.0f) {
                                r0.videoCrossfadeAlpha = 1.0f;
                            }
                        }
                    }
                    canvas.restore();
                }
            }
            f7 = 0.0f;
            containerViewHeight = 1.0f;
            z = true;
            canvas.save();
            f2 -= f7;
            canvas2.translate(((float) (getContainerViewWidth() / 2)) + f3, ((float) (getContainerViewHeight() / 2)) + f);
            canvas2.scale(f2, f2);
            bitmapWidth = r0.centerImage.getBitmapWidth();
            bitmapHeight = r0.centerImage.getBitmapHeight();
            bitmapWidth = r0.videoTextureView.getMeasuredWidth();
            bitmapHeight = r0.videoTextureView.getMeasuredHeight();
            f7 = (float) bitmapHeight;
            f = (float) bitmapWidth;
            f3 = Math.min(((float) getContainerViewHeight()) / f7, ((float) getContainerViewWidth()) / f);
            bitmapWidth = (int) (f * f3);
            bitmapHeight = (int) (f7 * f3);
            i = (-bitmapWidth) / 2;
            f8 = (float) i;
            f6 /= f2;
            i2 = (-bitmapHeight) / 2;
            f9 = (float) i2;
            canvas2.clipRect(f8 + f6, (f4 / f2) + f9, ((float) (bitmapWidth / 2)) - f6, ((float) (bitmapHeight / 2)) - (f5 / f2));
            r0.centerImage.setAlpha(containerViewHeight);
            r0.centerImage.setImageCoords(i, i2, bitmapWidth, bitmapHeight);
            r0.centerImage.draw(canvas2);
            if (z) {
                r0.videoCrossfadeStarted = true;
                r0.videoCrossfadeAlpha = 0.0f;
                r0.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                canvas2.translate(f8, f9);
                r0.videoTextureView.setAlpha(containerViewHeight * r0.videoCrossfadeAlpha);
                r0.aspectRatioFrameLayout.draw(canvas2);
                currentTimeMillis = System.currentTimeMillis();
                j = currentTimeMillis - r0.videoCrossfadeAlphaLastTime;
                r0.videoCrossfadeAlphaLastTime = currentTimeMillis;
                r0.videoCrossfadeAlpha += ((float) j) / 200.0f;
                r0.containerView.invalidate();
                if (r0.videoCrossfadeAlpha > 1.0f) {
                    r0.videoCrossfadeAlpha = 1.0f;
                }
            }
            canvas.restore();
        }
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
            if (this.photoAnimationEndRunnable != null) {
                this.photoAnimationEndRunnable.run();
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

    public void closePhoto(boolean z, boolean z2) {
        if (this.parentActivity != null && r0.isPhotoVisible) {
            if (!checkPhotoAnimation()) {
                PlaceProviderObject placeForPhoto;
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                int i;
                NotificationCenter.getInstance(r0.currentAccount).removeObserver(r0, NotificationCenter.messagesDeleted);
                NotificationCenter.getInstance(r0.currentAccount).removeObserver(r0, NotificationCenter.updateMessageMedia);
                NotificationCenter.getInstance(r0.currentAccount).removeObserver(r0, NotificationCenter.didCreatedNewDeleteTask);
                r0.isActionBarVisible = false;
                if (r0.velocityTracker != null) {
                    r0.velocityTracker.recycle();
                    r0.velocityTracker = null;
                }
                r0.closeTime = System.currentTimeMillis();
                if (!(r0.currentProvider == null || (r0.currentMessageObject.messageOwner.media.photo instanceof TL_photoEmpty))) {
                    if (!(r0.currentMessageObject.messageOwner.media.document instanceof TL_documentEmpty)) {
                        placeForPhoto = r0.currentProvider.getPlaceForPhoto(r0.currentMessageObject, null, 0);
                        if (r0.videoPlayer != null) {
                            r0.videoPlayer.pause();
                        }
                        if (z) {
                            animatorSet = new AnimatorSet();
                            animatorArr = new Animator[4];
                            animatorArr[0] = ObjectAnimator.ofFloat(r0.containerView, "scaleX", new float[]{0.9f});
                            animatorArr[1] = ObjectAnimator.ofFloat(r0.containerView, "scaleY", new float[]{0.9f});
                            animatorArr[2] = ObjectAnimator.ofInt(r0.photoBackgroundDrawable, "alpha", new int[]{0});
                            animatorArr[3] = ObjectAnimator.ofFloat(r0.actionBar, "alpha", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                            r0.photoAnimationInProgress = 2;
                            r0.photoAnimationEndRunnable = new Runnable() {
                                public void run() {
                                    if (SecretMediaViewer.this.containerView != null) {
                                        if (VERSION.SDK_INT >= 18) {
                                            SecretMediaViewer.this.containerView.setLayerType(0, null);
                                        }
                                        SecretMediaViewer.this.containerView.setVisibility(4);
                                        SecretMediaViewer.this.photoAnimationInProgress = 0;
                                        SecretMediaViewer.this.onPhotoClosed(placeForPhoto);
                                        SecretMediaViewer.this.containerView.setScaleX(1.0f);
                                        SecretMediaViewer.this.containerView.setScaleY(1.0f);
                                    }
                                }
                            };
                            animatorSet.setDuration(200);
                            animatorSet.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (SecretMediaViewer.this.photoAnimationEndRunnable != null) {
                                        SecretMediaViewer.this.photoAnimationEndRunnable.run();
                                        SecretMediaViewer.this.photoAnimationEndRunnable = null;
                                    }
                                }
                            });
                            r0.photoTransitionAnimationStartTime = System.currentTimeMillis();
                            if (VERSION.SDK_INT >= 18) {
                                r0.containerView.setLayerType(2, null);
                            }
                            animatorSet.start();
                        } else {
                            r0.photoAnimationInProgress = 3;
                            r0.containerView.invalidate();
                            r0.imageMoveAnimation = new AnimatorSet();
                            if (placeForPhoto != null || placeForPhoto.imageReceiver.getThumbBitmap() == null || z2) {
                                i = AndroidUtilities.displaySize.y + (VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0);
                                if (r0.translationY < 0.0f) {
                                    i = -i;
                                }
                                r0.animateToY = (float) i;
                            } else {
                                placeForPhoto.imageReceiver.setVisible(false, true);
                                Rect drawRegion = placeForPhoto.imageReceiver.getDrawRegion();
                                float f = (float) (drawRegion.right - drawRegion.left);
                                float f2 = (float) (drawRegion.bottom - drawRegion.top);
                                int i2 = AndroidUtilities.displaySize.x;
                                int i3 = AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                                r0.animateToScale = Math.max(f / ((float) i2), f2 / ((float) i3));
                                r0.animateToX = (((float) (placeForPhoto.viewX + drawRegion.left)) + (f / 2.0f)) - ((float) (i2 / 2));
                                r0.animateToY = (((float) (placeForPhoto.viewY + drawRegion.top)) + (f2 / 2.0f)) - ((float) (i3 / 2));
                                r0.animateToClipHorizontal = (float) Math.abs(drawRegion.left - placeForPhoto.imageReceiver.getImageX());
                                i = Math.abs(drawRegion.top - placeForPhoto.imageReceiver.getImageY());
                                int[] iArr = new int[2];
                                placeForPhoto.parentView.getLocationInWindow(iArr);
                                r0.animateToClipTop = (float) (((iArr[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (placeForPhoto.viewY + drawRegion.top)) + placeForPhoto.clipTopAddition);
                                if (r0.animateToClipTop < 0.0f) {
                                    r0.animateToClipTop = 0.0f;
                                }
                                r0.animateToClipBottom = (float) ((((placeForPhoto.viewY + drawRegion.top) + ((int) f2)) - ((iArr[1] + placeForPhoto.parentView.getHeight()) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) + placeForPhoto.clipBottomAddition);
                                if (r0.animateToClipBottom < 0.0f) {
                                    r0.animateToClipBottom = 0.0f;
                                }
                                r0.animationStartTime = System.currentTimeMillis();
                                float f3 = (float) i;
                                r0.animateToClipBottom = Math.max(r0.animateToClipBottom, f3);
                                r0.animateToClipTop = Math.max(r0.animateToClipTop, f3);
                                r0.zoomAnimation = true;
                            }
                            if (r0.isVideo) {
                                r0.centerImage.setManualAlphaAnimator(true);
                                animatorSet = r0.imageMoveAnimation;
                                animatorArr = new Animator[5];
                                animatorArr[0] = ObjectAnimator.ofInt(r0.photoBackgroundDrawable, "alpha", new int[]{0});
                                animatorArr[1] = ObjectAnimator.ofFloat(r0, "animationValue", new float[]{0.0f, 1.0f});
                                animatorArr[2] = ObjectAnimator.ofFloat(r0.actionBar, "alpha", new float[]{0.0f});
                                animatorArr[3] = ObjectAnimator.ofFloat(r0.secretDeleteTimer, "alpha", new float[]{0.0f});
                                animatorArr[4] = ObjectAnimator.ofFloat(r0.centerImage, "currentAlpha", new float[]{0.0f});
                                animatorSet.playTogether(animatorArr);
                            } else {
                                r0.videoCrossfadeStarted = false;
                                r0.textureUploaded = false;
                                animatorSet = r0.imageMoveAnimation;
                                animatorArr = new Animator[5];
                                animatorArr[0] = ObjectAnimator.ofInt(r0.photoBackgroundDrawable, "alpha", new int[]{0});
                                animatorArr[1] = ObjectAnimator.ofFloat(r0, "animationValue", new float[]{0.0f, 1.0f});
                                animatorArr[2] = ObjectAnimator.ofFloat(r0.actionBar, "alpha", new float[]{0.0f});
                                animatorArr[3] = ObjectAnimator.ofFloat(r0.secretDeleteTimer, "alpha", new float[]{0.0f});
                                animatorArr[4] = ObjectAnimator.ofFloat(r0, "videoCrossfadeAlpha", new float[]{0.0f});
                                animatorSet.playTogether(animatorArr);
                            }
                            r0.photoAnimationEndRunnable = new Runnable() {
                                public void run() {
                                    SecretMediaViewer.this.imageMoveAnimation = null;
                                    SecretMediaViewer.this.photoAnimationInProgress = 0;
                                    if (VERSION.SDK_INT >= 18) {
                                        SecretMediaViewer.this.containerView.setLayerType(0, null);
                                    }
                                    SecretMediaViewer.this.containerView.setVisibility(4);
                                    SecretMediaViewer.this.onPhotoClosed(placeForPhoto);
                                }
                            };
                            r0.imageMoveAnimation.setInterpolator(new DecelerateInterpolator());
                            r0.imageMoveAnimation.setDuration(250);
                            r0.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {

                                /* renamed from: org.telegram.ui.SecretMediaViewer$11$1 */
                                class C16651 implements Runnable {
                                    C16651() {
                                    }

                                    public void run() {
                                        if (SecretMediaViewer.this.photoAnimationEndRunnable != null) {
                                            SecretMediaViewer.this.photoAnimationEndRunnable.run();
                                            SecretMediaViewer.this.photoAnimationEndRunnable = null;
                                        }
                                    }
                                }

                                public void onAnimationEnd(Animator animator) {
                                    if (placeForPhoto != null) {
                                        placeForPhoto.imageReceiver.setVisible(true, true);
                                    }
                                    SecretMediaViewer.this.isVisible = false;
                                    AndroidUtilities.runOnUIThread(new C16651());
                                }
                            });
                            r0.photoTransitionAnimationStartTime = System.currentTimeMillis();
                            if (VERSION.SDK_INT >= 18) {
                                r0.containerView.setLayerType(2, null);
                            }
                            r0.imageMoveAnimation.start();
                        }
                    }
                }
                placeForPhoto = null;
                if (r0.videoPlayer != null) {
                    r0.videoPlayer.pause();
                }
                if (z) {
                    animatorSet = new AnimatorSet();
                    animatorArr = new Animator[4];
                    animatorArr[0] = ObjectAnimator.ofFloat(r0.containerView, "scaleX", new float[]{0.9f});
                    animatorArr[1] = ObjectAnimator.ofFloat(r0.containerView, "scaleY", new float[]{0.9f});
                    animatorArr[2] = ObjectAnimator.ofInt(r0.photoBackgroundDrawable, "alpha", new int[]{0});
                    animatorArr[3] = ObjectAnimator.ofFloat(r0.actionBar, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    r0.photoAnimationInProgress = 2;
                    r0.photoAnimationEndRunnable = /* anonymous class already generated */;
                    animatorSet.setDuration(200);
                    animatorSet.addListener(/* anonymous class already generated */);
                    r0.photoTransitionAnimationStartTime = System.currentTimeMillis();
                    if (VERSION.SDK_INT >= 18) {
                        r0.containerView.setLayerType(2, null);
                    }
                    animatorSet.start();
                } else {
                    r0.photoAnimationInProgress = 3;
                    r0.containerView.invalidate();
                    r0.imageMoveAnimation = new AnimatorSet();
                    if (placeForPhoto != null) {
                    }
                    if (VERSION.SDK_INT < 21) {
                    }
                    i = AndroidUtilities.displaySize.y + (VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0);
                    if (r0.translationY < 0.0f) {
                        i = -i;
                    }
                    r0.animateToY = (float) i;
                    if (r0.isVideo) {
                        r0.centerImage.setManualAlphaAnimator(true);
                        animatorSet = r0.imageMoveAnimation;
                        animatorArr = new Animator[5];
                        animatorArr[0] = ObjectAnimator.ofInt(r0.photoBackgroundDrawable, "alpha", new int[]{0});
                        animatorArr[1] = ObjectAnimator.ofFloat(r0, "animationValue", new float[]{0.0f, 1.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(r0.actionBar, "alpha", new float[]{0.0f});
                        animatorArr[3] = ObjectAnimator.ofFloat(r0.secretDeleteTimer, "alpha", new float[]{0.0f});
                        animatorArr[4] = ObjectAnimator.ofFloat(r0.centerImage, "currentAlpha", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                    } else {
                        r0.videoCrossfadeStarted = false;
                        r0.textureUploaded = false;
                        animatorSet = r0.imageMoveAnimation;
                        animatorArr = new Animator[5];
                        animatorArr[0] = ObjectAnimator.ofInt(r0.photoBackgroundDrawable, "alpha", new int[]{0});
                        animatorArr[1] = ObjectAnimator.ofFloat(r0, "animationValue", new float[]{0.0f, 1.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(r0.actionBar, "alpha", new float[]{0.0f});
                        animatorArr[3] = ObjectAnimator.ofFloat(r0.secretDeleteTimer, "alpha", new float[]{0.0f});
                        animatorArr[4] = ObjectAnimator.ofFloat(r0, "videoCrossfadeAlpha", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                    }
                    r0.photoAnimationEndRunnable = /* anonymous class already generated */;
                    r0.imageMoveAnimation.setInterpolator(new DecelerateInterpolator());
                    r0.imageMoveAnimation.setDuration(250);
                    r0.imageMoveAnimation.addListener(/* anonymous class already generated */);
                    r0.photoTransitionAnimationStartTime = System.currentTimeMillis();
                    if (VERSION.SDK_INT >= 18) {
                        r0.containerView.setLayerType(2, null);
                    }
                    r0.imageMoveAnimation.start();
                }
            }
        }
    }

    private void onPhotoClosed(PlaceProviderObject placeProviderObject) {
        this.isVisible = false;
        this.currentProvider = null;
        this.disableShowCheck = false;
        releasePlayer();
        placeProviderObject = new ArrayList();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (SecretMediaViewer.this.currentThumb != null) {
                    SecretMediaViewer.this.currentThumb.release();
                    SecretMediaViewer.this.currentThumb = null;
                }
                SecretMediaViewer.this.centerImage.setImageBitmap((Bitmap) null);
                try {
                    if (SecretMediaViewer.this.windowView.getParent() != null) {
                        ((WindowManager) SecretMediaViewer.this.parentActivity.getSystemService("window")).removeView(SecretMediaViewer.this.windowView);
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                SecretMediaViewer.this.isPhotoVisible = false;
            }
        }, 50);
    }

    private void updateMinMax(float f) {
        int imageWidth = ((int) ((((float) this.centerImage.getImageWidth()) * f) - ((float) getContainerViewWidth()))) / 2;
        f = ((int) ((((float) this.centerImage.getImageHeight()) * f) - ((float) getContainerViewHeight()))) / 2;
        if (imageWidth > 0) {
            this.minX = (float) (-imageWidth);
            this.maxX = (float) imageWidth;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (f > null) {
            this.minY = (float) (-f);
            this.maxY = (float) f;
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

    private boolean processTouchEvent(MotionEvent motionEvent) {
        if (this.photoAnimationInProgress == 0) {
            if (this.animationStartTime == 0) {
                if (motionEvent.getPointerCount() == 1 && this.gestureDetector.onTouchEvent(motionEvent) && this.doubleTap) {
                    this.doubleTap = false;
                    this.moving = false;
                    this.zooming = false;
                    checkMinMax(false);
                    return true;
                }
                if (motionEvent.getActionMasked() != 0) {
                    if (motionEvent.getActionMasked() != 5) {
                        float f = 0.0f;
                        float abs;
                        if (motionEvent.getActionMasked() == 2) {
                            if (motionEvent.getPointerCount() == 2 && !this.draggingDown && this.zooming) {
                                this.discardTap = true;
                                this.scale = (((float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)))) / this.pinchStartDistance) * this.pinchStartScale;
                                this.translationX = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (this.scale / this.pinchStartScale));
                                this.translationY = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (this.scale / this.pinchStartScale));
                                updateMinMax(this.scale);
                                this.containerView.invalidate();
                            } else if (motionEvent.getPointerCount() == 1) {
                                if (this.velocityTracker != null) {
                                    this.velocityTracker.addMovement(motionEvent);
                                }
                                abs = Math.abs(motionEvent.getX() - this.moveStartX);
                                float abs2 = Math.abs(motionEvent.getY() - this.dragY);
                                if (abs > ((float) AndroidUtilities.dp(3.0f)) || abs2 > ((float) AndroidUtilities.dp(3.0f))) {
                                    this.discardTap = true;
                                }
                                if (this.canDragDown && !this.draggingDown && this.scale == 1.0f && abs2 >= ((float) AndroidUtilities.dp(30.0f)) && abs2 / 2.0f > abs) {
                                    this.draggingDown = true;
                                    this.moving = false;
                                    this.dragY = motionEvent.getY();
                                    if (this.isActionBarVisible != null) {
                                        toggleActionBar(false, true);
                                    }
                                    return true;
                                } else if (this.draggingDown) {
                                    this.translationY = motionEvent.getY() - this.dragY;
                                    this.containerView.invalidate();
                                } else if (this.invalidCoords || this.animationStartTime != 0) {
                                    this.invalidCoords = false;
                                    this.moveStartX = motionEvent.getX();
                                    this.moveStartY = motionEvent.getY();
                                } else {
                                    abs = this.moveStartX - motionEvent.getX();
                                    float y = this.moveStartY - motionEvent.getY();
                                    if (this.moving || ((this.scale == 1.0f && Math.abs(y) + ((float) AndroidUtilities.dp(12.0f)) < Math.abs(abs)) || this.scale != 1.0f)) {
                                        if (!this.moving) {
                                            this.moving = true;
                                            this.canDragDown = false;
                                            abs = 0.0f;
                                            y = abs;
                                        }
                                        this.moveStartX = motionEvent.getX();
                                        this.moveStartY = motionEvent.getY();
                                        updateMinMax(this.scale);
                                        if (this.translationX < this.minX || this.translationX > this.maxX) {
                                            abs /= 3.0f;
                                        }
                                        if (this.maxY == null && this.minY == null) {
                                            if (this.translationY - y < this.minY) {
                                                this.translationY = this.minY;
                                            } else if (this.translationY - y > this.maxY) {
                                                this.translationY = this.maxY;
                                            }
                                            this.translationX -= abs;
                                            if (this.scale != NUM) {
                                                this.translationY -= f;
                                            }
                                            this.containerView.invalidate();
                                        } else {
                                            if (this.translationY >= this.minY) {
                                                if (this.translationY > this.maxY) {
                                                }
                                            }
                                            f = y / 3.0f;
                                            this.translationX -= abs;
                                            if (this.scale != NUM) {
                                                this.translationY -= f;
                                            }
                                            this.containerView.invalidate();
                                        }
                                        f = y;
                                        this.translationX -= abs;
                                        if (this.scale != NUM) {
                                            this.translationY -= f;
                                        }
                                        this.containerView.invalidate();
                                    }
                                }
                            }
                        } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
                            if (this.zooming) {
                                this.invalidCoords = true;
                                if (this.scale < NUM) {
                                    updateMinMax(1.0f);
                                    animateTo(1.0f, 0.0f, 0.0f, true);
                                } else if (this.scale > NUM) {
                                    motionEvent = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                                    abs = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                                    updateMinMax(3.0f);
                                    if (motionEvent < this.minX) {
                                        motionEvent = this.minX;
                                    } else if (motionEvent > this.maxX) {
                                        motionEvent = this.maxX;
                                    }
                                    if (abs < this.minY) {
                                        abs = this.minY;
                                    } else if (abs > this.maxY) {
                                        abs = this.maxY;
                                    }
                                    animateTo(3.0f, motionEvent, abs, true);
                                } else {
                                    checkMinMax(true);
                                }
                                this.zooming = false;
                            } else if (this.draggingDown) {
                                if (Math.abs(this.dragY - motionEvent.getY()) > ((float) getContainerViewHeight()) / 6.0f) {
                                    closePhoto(true, false);
                                } else {
                                    animateTo(1.0f, 0.0f, 0.0f, false);
                                }
                                this.draggingDown = false;
                            } else if (this.moving != null) {
                                motionEvent = this.translationX;
                                abs = this.translationY;
                                updateMinMax(this.scale);
                                this.moving = false;
                                this.canDragDown = true;
                                if (this.velocityTracker != null && this.scale == 1.0f) {
                                    this.velocityTracker.computeCurrentVelocity(1000);
                                }
                                if (this.translationX < this.minX) {
                                    motionEvent = this.minX;
                                } else if (this.translationX > this.maxX) {
                                    motionEvent = this.maxX;
                                }
                                if (this.translationY < this.minY) {
                                    abs = this.minY;
                                } else if (this.translationY > this.maxY) {
                                    abs = this.maxY;
                                }
                                animateTo(this.scale, motionEvent, abs, false);
                            }
                        }
                        return false;
                    }
                }
                this.discardTap = false;
                if (!this.scroller.isFinished()) {
                    this.scroller.abortAnimation();
                }
                if (!this.draggingDown) {
                    if (motionEvent.getPointerCount() == 2) {
                        this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                        this.pinchStartScale = this.scale;
                        this.pinchCenterX = (motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f;
                        this.pinchCenterY = (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f;
                        this.pinchStartX = this.translationX;
                        this.pinchStartY = this.translationY;
                        this.zooming = true;
                        this.moving = false;
                        if (this.velocityTracker != null) {
                            this.velocityTracker.clear();
                        }
                    } else if (motionEvent.getPointerCount() == 1) {
                        this.moveStartX = motionEvent.getX();
                        motionEvent = motionEvent.getY();
                        this.moveStartY = motionEvent;
                        this.dragY = motionEvent;
                        this.draggingDown = false;
                        this.canDragDown = true;
                        if (this.velocityTracker != null) {
                            this.velocityTracker.clear();
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }

    private void checkMinMax(boolean z) {
        float f = this.translationX;
        float f2 = this.translationY;
        updateMinMax(this.scale);
        if (this.translationX < this.minX) {
            f = this.minX;
        } else if (this.translationX > this.maxX) {
            f = this.maxX;
        }
        if (this.translationY < this.minY) {
            f2 = this.minY;
        } else if (this.translationY > this.maxY) {
            f2 = this.maxY;
        }
        animateTo(this.scale, f, f2, z);
    }

    private void animateTo(float f, float f2, float f3, boolean z) {
        animateTo(f, f2, f3, z, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    }

    private void animateTo(float f, float f2, float f3, boolean z, int i) {
        if (this.scale != f || this.translationX != f2 || this.translationY != f3) {
            this.zoomAnimation = z;
            this.animateToScale = f;
            this.animateToX = f2;
            this.animateToY = f3;
            this.animationStartTime = System.currentTimeMillis();
            this.imageMoveAnimation = new AnimatorSet();
            this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f})});
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
        if (this.scale != NUM) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(f), Math.round(f2), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.containerView.postInvalidate();
        }
        return null;
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if (this.discardTap != null) {
            return null;
        }
        toggleActionBar(this.isActionBarVisible ^ 1, true);
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onDoubleTap(MotionEvent motionEvent) {
        if ((this.scale != 1.0f || (this.translationY == 0.0f && this.translationX == 0.0f)) && this.animationStartTime == 0) {
            if (this.photoAnimationInProgress == 0) {
                if (this.scale == 1.0f) {
                    float x = (motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - (((motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - this.translationX) * (3.0f / this.scale));
                    float y = (motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - (((motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - this.translationY) * (3.0f / this.scale));
                    updateMinMax(3.0f);
                    if (x < this.minX) {
                        x = this.minX;
                    } else if (x > this.maxX) {
                        x = this.maxX;
                    }
                    if (y < this.minY) {
                        y = this.minY;
                    } else if (y > this.maxY) {
                        y = this.maxY;
                    }
                    animateTo(3.0f, x, y, true);
                } else {
                    animateTo(1.0f, 0.0f, 0.0f, true);
                }
                this.doubleTap = true;
                return true;
            }
        }
        return false;
    }
}
