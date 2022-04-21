package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Scroller;
import org.telegram.ui.Components.TimerParticles;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.PhotoViewer;

public class SecretMediaViewer implements NotificationCenter.NotificationCenterDelegate, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private static volatile SecretMediaViewer Instance = null;
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
    private ImageReceiver centerImage = new ImageReceiver();
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
    private int[] coords = new int[2];
    /* access modifiers changed from: private */
    public int currentAccount;
    /* access modifiers changed from: private */
    public AnimatorSet currentActionBarAnimation;
    private long currentDialogId;
    /* access modifiers changed from: private */
    public MessageObject currentMessageObject;
    private PhotoViewer.PhotoViewerProvider currentProvider;
    private float[] currentRadii;
    private int currentRotation;
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

    static /* synthetic */ int access$1110(SecretMediaViewer x0) {
        int i = x0.playerRetryPlayCount;
        x0.playerRetryPlayCount = i - 1;
        return i;
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean unused = SecretMediaViewer.this.processTouchEvent(event);
            return true;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            SecretMediaViewer.this.onDraw(canvas);
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return child != SecretMediaViewer.this.aspectRatioFrameLayout && super.drawChild(canvas, child, drawingTime);
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
        public void setDestroyTime(long time, long ttl, boolean videoProgress) {
            this.destroyTime = time;
            this.destroyTtl = ttl;
            this.useVideoProgress = videoProgress;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int y = (getMeasuredHeight() / 2) - (AndroidUtilities.dp(28.0f) / 2);
            this.deleteProgressRect.set((float) (getMeasuredWidth() - AndroidUtilities.dp(49.0f)), (float) y, (float) (getMeasuredWidth() - AndroidUtilities.dp(21.0f)), (float) (AndroidUtilities.dp(28.0f) + y));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float progress;
            if (SecretMediaViewer.this.currentMessageObject != null && SecretMediaViewer.this.currentMessageObject.messageOwner.destroyTime != 0) {
                canvas.drawCircle((float) (getMeasuredWidth() - AndroidUtilities.dp(35.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(16.0f), this.circlePaint);
                if (!this.useVideoProgress) {
                    progress = ((float) Math.max(0, this.destroyTime - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance(SecretMediaViewer.this.currentAccount).getTimeDifference() * 1000))))) / (((float) this.destroyTtl) * 1000.0f);
                } else if (SecretMediaViewer.this.videoPlayer != null) {
                    long duration = SecretMediaViewer.this.videoPlayer.getDuration();
                    long position = SecretMediaViewer.this.videoPlayer.getCurrentPosition();
                    if (duration == -9223372036854775807L || position == -9223372036854775807L) {
                        progress = 1.0f;
                    } else {
                        progress = 1.0f - (((float) position) / ((float) duration));
                    }
                } else {
                    progress = 1.0f;
                }
                int x = getMeasuredWidth() - AndroidUtilities.dp(40.0f);
                int y = ((getMeasuredHeight() - AndroidUtilities.dp(14.0f)) / 2) - AndroidUtilities.dp(0.5f);
                this.drawable.setBounds(x, y, AndroidUtilities.dp(10.0f) + x, AndroidUtilities.dp(14.0f) + y);
                this.drawable.draw(canvas);
                float radProgress = -360.0f * progress;
                canvas.drawArc(this.deleteProgressRect, -90.0f, radProgress, false, this.afterDeleteProgressPaint);
                this.timerParticles.draw(canvas, this.particlePaint, this.deleteProgressRect, radProgress, 1.0f);
                invalidate();
            }
        }
    }

    private class PhotoBackgroundDrawable extends ColorDrawable {
        /* access modifiers changed from: private */
        public Runnable drawRunnable;
        /* access modifiers changed from: private */
        public int frame;

        public PhotoBackgroundDrawable(int color) {
            super(color);
        }

        public void setAlpha(int alpha) {
            if (SecretMediaViewer.this.parentActivity instanceof LaunchActivity) {
                ((LaunchActivity) SecretMediaViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(!SecretMediaViewer.this.isPhotoVisible || alpha != 255);
            }
            super.setAlpha(alpha);
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
        SecretMediaViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    SecretMediaViewer secretMediaViewer = new SecretMediaViewer();
                    localInstance = secretMediaViewer;
                    Instance = secretMediaViewer;
                }
            }
        }
        return localInstance;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.messagesDeleted) {
            if (args[2].booleanValue() || this.currentMessageObject == null || args[1].longValue() != 0 || !args[0].contains(Integer.valueOf(this.currentMessageObject.getId()))) {
                return;
            }
            if (this.isVideo && !this.videoWatchedOneTime) {
                this.closeVideoAfterWatch = true;
            } else if (!closePhoto(true, true)) {
                this.closeAfterAnimation = true;
            }
        } else if (id == NotificationCenter.didCreatedNewDeleteTask) {
            if (this.currentMessageObject != null && this.secretDeleteTimer != null && args[0].longValue() == this.currentDialogId) {
                SparseArray<ArrayList<Integer>> mids = args[1];
                for (int i = 0; i < mids.size(); i++) {
                    int key = mids.keyAt(i);
                    ArrayList<Integer> arr = mids.get(key);
                    for (int a = 0; a < arr.size(); a++) {
                        if (((long) this.currentMessageObject.getId()) == ((long) arr.get(a).intValue())) {
                            this.currentMessageObject.messageOwner.destroyTime = key;
                            this.secretDeleteTimer.invalidate();
                            return;
                        }
                    }
                }
            }
        } else if (id != NotificationCenter.updateMessageMedia || this.currentMessageObject.getId() != args[0].id) {
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

                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        if (SecretMediaViewer.this.videoPlayer != null && SecretMediaViewer.this.currentMessageObject != null) {
                            if (playbackState == 4 || playbackState == 1) {
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
                            if (playbackState == 3 && SecretMediaViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                SecretMediaViewer.this.aspectRatioFrameLayout.setVisibility(0);
                            }
                            if (!SecretMediaViewer.this.videoPlayer.isPlaying() || playbackState == 4) {
                                if (SecretMediaViewer.this.isPlaying) {
                                    boolean unused = SecretMediaViewer.this.isPlaying = false;
                                    if (playbackState == 4) {
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

                    public void onError(VideoPlayer player, Exception e) {
                        if (SecretMediaViewer.this.playerRetryPlayCount > 0) {
                            SecretMediaViewer.access$1110(SecretMediaViewer.this);
                            AndroidUtilities.runOnUIThread(new SecretMediaViewer$1$$ExternalSyntheticLambda0(this, file), 100);
                            return;
                        }
                        FileLog.e((Throwable) e);
                    }

                    /* renamed from: lambda$onError$0$org-telegram-ui-SecretMediaViewer$1  reason: not valid java name */
                    public /* synthetic */ void m3220lambda$onError$0$orgtelegramuiSecretMediaViewer$1(File file) {
                        SecretMediaViewer.this.preparePlayer(file);
                    }

                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        if (SecretMediaViewer.this.aspectRatioFrameLayout != null) {
                            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                                int temp = width;
                                width = height;
                                height = temp;
                            }
                            SecretMediaViewer.this.aspectRatioFrameLayout.setAspectRatio(height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height), unappliedRotationDegrees);
                        }
                    }

                    public void onRenderedFirstFrame() {
                        if (!SecretMediaViewer.this.textureUploaded) {
                            boolean unused = SecretMediaViewer.this.textureUploaded = true;
                            SecretMediaViewer.this.containerView.invalidate();
                        }
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
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
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                    int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                    if (Build.VERSION.SDK_INT >= 21 && SecretMediaViewer.this.lastInsets != null) {
                        WindowInsets insets = (WindowInsets) SecretMediaViewer.this.lastInsets;
                        if (AndroidUtilities.incorrectDisplaySizeFix) {
                            if (heightSize > AndroidUtilities.displaySize.y) {
                                heightSize = AndroidUtilities.displaySize.y;
                            }
                            heightSize += AndroidUtilities.statusBarHeight;
                        }
                        heightSize -= insets.getSystemWindowInsetBottom();
                        widthSize -= insets.getSystemWindowInsetRight();
                    } else if (heightSize > AndroidUtilities.displaySize.y) {
                        heightSize = AndroidUtilities.displaySize.y;
                    }
                    setMeasuredDimension(widthSize, heightSize);
                    if (Build.VERSION.SDK_INT >= 21 && SecretMediaViewer.this.lastInsets != null) {
                        widthSize -= ((WindowInsets) SecretMediaViewer.this.lastInsets).getSystemWindowInsetLeft();
                    }
                    SecretMediaViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(heightSize, NUM));
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int x = 0;
                    if (Build.VERSION.SDK_INT >= 21 && SecretMediaViewer.this.lastInsets != null) {
                        x = 0 + ((WindowInsets) SecretMediaViewer.this.lastInsets).getSystemWindowInsetLeft();
                    }
                    SecretMediaViewer.this.containerView.layout(x, 0, SecretMediaViewer.this.containerView.getMeasuredWidth() + x, SecretMediaViewer.this.containerView.getMeasuredHeight());
                    if (changed) {
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
                        WindowInsets insets = (WindowInsets) SecretMediaViewer.this.lastInsets;
                        if (SecretMediaViewer.this.photoAnimationInProgress != 0) {
                            SecretMediaViewer.this.blackPaint.setAlpha(SecretMediaViewer.this.photoBackgroundDrawable.getAlpha());
                        } else {
                            SecretMediaViewer.this.blackPaint.setAlpha(255);
                        }
                        canvas.drawRect(0.0f, (float) getMeasuredHeight(), (float) getMeasuredWidth(), (float) (getMeasuredHeight() + insets.getSystemWindowInsetBottom()), SecretMediaViewer.this.blackPaint);
                    }
                }
            };
            this.windowView = r0;
            r0.setBackgroundDrawable(this.photoBackgroundDrawable);
            this.windowView.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            AnonymousClass3 r02 = new FrameLayoutDrawer(activity) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    super.onLayout(changed, left, top, right, bottom);
                    if (SecretMediaViewer.this.secretDeleteTimer != null) {
                        int y = ((ActionBar.getCurrentActionBarHeight() - SecretMediaViewer.this.secretDeleteTimer.getMeasuredHeight()) / 2) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                        SecretMediaViewer.this.secretDeleteTimer.layout(SecretMediaViewer.this.secretDeleteTimer.getLeft(), y, SecretMediaViewer.this.secretDeleteTimer.getRight(), SecretMediaViewer.this.secretDeleteTimer.getMeasuredHeight() + y);
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
            if (Build.VERSION.SDK_INT >= 21) {
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
            this.actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21);
            this.actionBar.setItemsBackgroundColor(NUM, false);
            this.actionBar.setBackButtonImage(NUM);
            this.actionBar.setTitleRightMargin(AndroidUtilities.dp(70.0f));
            this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    if (id == -1) {
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
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 48;
            this.windowLayoutParams.type = 99;
            if (Build.VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -NUM;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.windowLayoutParams.flags |= 8192;
            this.centerImage.setParentView(this.containerView);
            this.centerImage.setForceCrossfade(true);
        }
    }

    /* renamed from: lambda$setParentActivity$0$org-telegram-ui-SecretMediaViewer  reason: not valid java name */
    public /* synthetic */ WindowInsets m3219lambda$setParentActivity$0$orgtelegramuiSecretMediaViewer(View v, WindowInsets insets) {
        WindowInsets oldInsets = (WindowInsets) this.lastInsets;
        this.lastInsets = insets;
        if (oldInsets == null || !oldInsets.toString().equals(insets.toString())) {
            this.windowView.requestLayout();
        }
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return insets.consumeSystemWindowInsets();
    }

    public void openMedia(MessageObject messageObject, PhotoViewer.PhotoViewerProvider provider, Runnable onOpen) {
        char c;
        PhotoViewer.PlaceProviderObject object;
        MessageObject messageObject2 = messageObject;
        PhotoViewer.PhotoViewerProvider photoViewerProvider = provider;
        if (this.parentActivity == null || messageObject2 == null || !messageObject.needDrawBluredPreview()) {
            Runnable runnable = onOpen;
        } else if (photoViewerProvider == null) {
            Runnable runnable2 = onOpen;
        } else {
            PhotoViewer.PlaceProviderObject object2 = photoViewerProvider.getPlaceForPhoto(messageObject2, (TLRPC.FileLocation) null, 0, true);
            if (object2 != null) {
                this.currentProvider = photoViewerProvider;
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
                RectF drawRegion = object2.imageReceiver.getDrawRegion();
                float width = drawRegion.width();
                float height = drawRegion.height();
                int viewWidth = AndroidUtilities.displaySize.x;
                int viewHeight = (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y;
                this.scale = Math.max(width / ((float) viewWidth), height / ((float) viewHeight));
                if (object2.radius != null) {
                    this.animateFromRadius = new int[object2.radius.length];
                    for (int i = 0; i < object2.radius.length; i++) {
                        this.animateFromRadius[i] = object2.radius[i];
                    }
                } else {
                    this.animateFromRadius = null;
                }
                this.translationX = ((((float) object2.viewX) + drawRegion.left) + (width / 2.0f)) - ((float) (viewWidth / 2));
                this.translationY = ((((float) object2.viewY) + drawRegion.top) + (height / 2.0f)) - ((float) (viewHeight / 2));
                this.clipHorizontal = Math.abs(drawRegion.left - object2.imageReceiver.getImageX());
                int clipVertical = (int) Math.abs(drawRegion.top - object2.imageReceiver.getImageY());
                int[] coords2 = new int[2];
                object2.parentView.getLocationInWindow(coords2);
                float f = (((float) (coords2[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) - (((float) object2.viewY) + drawRegion.top)) + ((float) object2.clipTopAddition);
                this.clipTop = f;
                this.clipTop = Math.max(0.0f, Math.max(f, (float) clipVertical));
                float height2 = (((((float) object2.viewY) + drawRegion.top) + ((float) ((int) height))) - ((float) ((coords2[1] + object2.parentView.getHeight()) - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)))) + ((float) object2.clipBottomAddition);
                this.clipBottom = height2;
                this.clipBottom = Math.max(0.0f, Math.max(height2, (float) clipVertical));
                this.clipTopOrigin = 0.0f;
                this.clipTopOrigin = Math.max(0.0f, Math.max(0.0f, (float) clipVertical));
                this.clipBottomOrigin = 0.0f;
                this.clipBottomOrigin = Math.max(0.0f, Math.max(0.0f, (float) clipVertical));
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
                TLRPC.Document document = messageObject.getDocument();
                ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
                if (bitmapHolder != null) {
                    bitmapHolder.release();
                    this.currentThumb = null;
                }
                this.currentThumb = object2.imageReceiver.getThumbBitmapSafe();
                if (document == null) {
                    int i2 = viewHeight;
                    int i3 = viewWidth;
                    float f2 = height;
                    RectF rectF = drawRegion;
                    int i4 = clipVertical;
                    object = object2;
                    c = 4;
                    this.actionBar.setTitle(LocaleController.getString("DisappearingPhoto", NUM));
                    this.centerImage.setImage(ImageLocation.getForObject(FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, AndroidUtilities.getPhotoSize()), messageObject2.photoThumbsObject), (String) null, (Drawable) this.currentThumb != null ? new BitmapDrawable(this.currentThumb.bitmap) : null, -1, (String) null, (Object) messageObject, 2);
                    this.secretDeleteTimer.setDestroyTime(((long) messageObject2.messageOwner.destroyTime) * 1000, (long) messageObject2.messageOwner.ttl, false);
                } else if (MessageObject.isGifDocument(document)) {
                    this.actionBar.setTitle(LocaleController.getString("DisappearingGif", NUM));
                    int[] iArr = coords2;
                    int i5 = viewHeight;
                    int i6 = viewWidth;
                    float f3 = height;
                    RectF rectF2 = drawRegion;
                    int i7 = clipVertical;
                    c = 4;
                    object = object2;
                    this.centerImage.setImage(ImageLocation.getForDocument(document), (String) null, (Drawable) this.currentThumb != null ? new BitmapDrawable(this.currentThumb.bitmap) : null, -1, (String) null, (Object) messageObject, 1);
                    this.secretDeleteTimer.setDestroyTime(((long) messageObject2.messageOwner.destroyTime) * 1000, (long) messageObject2.messageOwner.ttl, false);
                } else {
                    int i8 = viewHeight;
                    int i9 = viewWidth;
                    float f4 = height;
                    RectF rectF3 = drawRegion;
                    int i10 = clipVertical;
                    object = object2;
                    c = 4;
                    this.playerRetryPlayCount = 1;
                    this.actionBar.setTitle(LocaleController.getString("DisappearingVideo", NUM));
                    File f5 = new File(messageObject2.messageOwner.attachPath);
                    if (f5.exists()) {
                        preparePlayer(f5);
                    } else {
                        File file = FileLoader.getPathToMessage(messageObject2.messageOwner);
                        File encryptedFile = new File(file.getAbsolutePath() + ".enc");
                        if (encryptedFile.exists()) {
                            file = encryptedFile;
                        }
                        preparePlayer(file);
                    }
                    this.isVideo = true;
                    this.centerImage.setImage((ImageLocation) null, (String) null, (Drawable) this.currentThumb != null ? new BitmapDrawable(this.currentThumb.bitmap) : null, -1, (String) null, (Object) messageObject, 2);
                    long destroyTime = ((long) messageObject2.messageOwner.destroyTime) * 1000;
                    File file2 = f5;
                    long j = destroyTime;
                    if (((long) messageObject.getDuration()) * 1000 > destroyTime - (System.currentTimeMillis() + (((long) ConnectionsManager.getInstance(this.currentAccount).getTimeDifference()) * 1000))) {
                        this.secretDeleteTimer.setDestroyTime(-1, -1, true);
                    } else {
                        this.secretDeleteTimer.setDestroyTime(((long) messageObject2.messageOwner.destroyTime) * 1000, (long) messageObject2.messageOwner.ttl, false);
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
                if (Build.VERSION.SDK_INT >= 21) {
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
                this.photoAnimationEndRunnable = new SecretMediaViewer$$ExternalSyntheticLambda2(this, onOpen);
                this.imageMoveAnimation.setDuration(250);
                this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (SecretMediaViewer.this.photoAnimationEndRunnable != null) {
                            SecretMediaViewer.this.photoAnimationEndRunnable.run();
                            Runnable unused = SecretMediaViewer.this.photoAnimationEndRunnable = null;
                        }
                    }
                });
                this.photoTransitionAnimationStartTime = System.currentTimeMillis();
                if (Build.VERSION.SDK_INT >= 18) {
                    this.containerView.setLayerType(2, (Paint) null);
                }
                this.imageMoveAnimation.setInterpolator(new DecelerateInterpolator());
                int unused = this.photoBackgroundDrawable.frame = 0;
                Runnable unused2 = this.photoBackgroundDrawable.drawRunnable = new SecretMediaViewer$$ExternalSyntheticLambda5(this, object);
                this.imageMoveAnimation.start();
            }
        }
    }

    /* renamed from: lambda$openMedia$1$org-telegram-ui-SecretMediaViewer  reason: not valid java name */
    public /* synthetic */ void m3217lambda$openMedia$1$orgtelegramuiSecretMediaViewer(Runnable onOpen) {
        this.photoAnimationInProgress = 0;
        this.imageMoveAnimation = null;
        if (onOpen != null) {
            onOpen.run();
        }
        if (this.containerView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, (Paint) null);
            }
            this.containerView.invalidate();
            if (this.closeAfterAnimation) {
                closePhoto(true, true);
            }
        }
    }

    /* renamed from: lambda$openMedia$2$org-telegram-ui-SecretMediaViewer  reason: not valid java name */
    public /* synthetic */ void m3218lambda$openMedia$2$orgtelegramuiSecretMediaViewer(PhotoViewer.PlaceProviderObject object) {
        this.disableShowCheck = false;
        object.imageReceiver.setVisible(false, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000a, code lost:
        r0 = r2.currentMessageObject;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isShowingImage(org.telegram.messenger.MessageObject r3) {
        /*
            r2 = this;
            boolean r0 = r2.isVisible
            if (r0 == 0) goto L_0x001a
            boolean r0 = r2.disableShowCheck
            if (r0 != 0) goto L_0x001a
            if (r3 == 0) goto L_0x001a
            org.telegram.messenger.MessageObject r0 = r2.currentMessageObject
            if (r0 == 0) goto L_0x001a
            int r0 = r0.getId()
            int r1 = r3.getId()
            if (r0 != r1) goto L_0x001a
            r0 = 1
            goto L_0x001b
        L_0x001a:
            r0 = 0
        L_0x001b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretMediaViewer.isShowingImage(org.telegram.messenger.MessageObject):boolean");
    }

    private void toggleActionBar(boolean show, boolean animated) {
        if (show) {
            this.actionBar.setVisibility(0);
        }
        this.actionBar.setEnabled(show);
        this.isActionBarVisible = show;
        float f = 1.0f;
        if (animated) {
            ArrayList<Animator> arrayList = new ArrayList<>();
            ActionBar actionBar2 = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar2, property, fArr));
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentActionBarAnimation = animatorSet;
            animatorSet.playTogether(arrayList);
            if (!show) {
                this.currentActionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (SecretMediaViewer.this.currentActionBarAnimation != null && SecretMediaViewer.this.currentActionBarAnimation.equals(animation)) {
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
        if (!show) {
            f = 0.0f;
        }
        actionBar3.setAlpha(f);
        if (!show) {
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
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0375  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x03a4  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0404  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0270  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0273  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x02a4  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x02e9  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x033c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r32) {
        /*
            r31 = this;
            r0 = r31
            r1 = r32
            boolean r2 = r0.isPhotoVisible
            if (r2 != 0) goto L_0x0009
            return
        L_0x0009:
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.animation.AnimatorSet r3 = r0.imageMoveAnimation
            r6 = 1065353216(0x3var_, float:1.0)
            if (r3 == 0) goto L_0x00f3
            org.telegram.ui.Components.Scroller r3 = r0.scroller
            boolean r3 = r3.isFinished()
            if (r3 != 0) goto L_0x001e
            org.telegram.ui.Components.Scroller r3 = r0.scroller
            r3.abortAnimation()
        L_0x001e:
            boolean r3 = r0.useOvershootForScale
            if (r3 == 0) goto L_0x008e
            r3 = 1065520988(0x3var_f5c, float:1.02)
            r7 = 1063675494(0x3var_, float:0.9)
            float r8 = r0.animationValue
            r9 = 1063675494(0x3var_, float:0.9)
            int r10 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r10 >= 0) goto L_0x0040
            float r8 = r8 / r9
            float r9 = r0.scale
            float r10 = r0.animateToScale
            r11 = 1065520988(0x3var_f5c, float:1.02)
            float r10 = r10 * r11
            float r10 = r10 - r9
            float r10 = r10 * r8
            float r9 = r9 + r10
            goto L_0x0055
        L_0x0040:
            r10 = 1065353216(0x3var_, float:1.0)
            float r11 = r0.animateToScale
            r12 = 1017370368(0x3ca3d700, float:0.01999998)
            float r12 = r12 * r11
            float r8 = r8 - r9
            r9 = 1036831952(0x3dccccd0, float:0.NUM)
            float r8 = r8 / r9
            float r8 = r6 - r8
            float r12 = r12 * r8
            float r9 = r11 + r12
            r8 = r10
        L_0x0055:
            float r10 = r0.translationY
            float r11 = r0.animateToY
            float r11 = r11 - r10
            float r11 = r11 * r8
            float r10 = r10 + r11
            float r11 = r0.translationX
            float r12 = r0.animateToX
            float r12 = r12 - r11
            float r12 = r12 * r8
            float r11 = r11 + r12
            float r12 = r0.clipTop
            float r13 = r0.animateToClipTop
            float r13 = r13 - r12
            float r13 = r13 * r8
            float r12 = r12 + r13
            float r13 = r0.clipBottom
            float r14 = r0.animateToClipBottom
            float r14 = r14 - r13
            float r14 = r14 * r8
            float r13 = r13 + r14
            float r14 = r0.clipTopOrigin
            float r15 = r0.animateToClipTopOrigin
            float r15 = r15 - r14
            float r15 = r15 * r8
            float r14 = r14 + r15
            float r15 = r0.clipBottomOrigin
            float r4 = r0.animateToClipBottomOrigin
            float r4 = r4 - r15
            float r4 = r4 * r8
            float r15 = r15 + r4
            float r4 = r0.clipHorizontal
            float r5 = r0.animateToClipHorizontal
            float r5 = r5 - r4
            float r5 = r5 * r8
            float r4 = r4 + r5
            goto L_0x00d7
        L_0x008e:
            float r3 = r0.scale
            float r4 = r0.animateToScale
            float r4 = r4 - r3
            float r5 = r0.animationValue
            float r4 = r4 * r5
            float r9 = r3 + r4
            float r3 = r0.translationY
            float r4 = r0.animateToY
            float r4 = r4 - r3
            float r4 = r4 * r5
            float r10 = r3 + r4
            float r3 = r0.translationX
            float r4 = r0.animateToX
            float r4 = r4 - r3
            float r4 = r4 * r5
            float r11 = r3 + r4
            float r3 = r0.clipTop
            float r4 = r0.animateToClipTop
            float r4 = r4 - r3
            float r4 = r4 * r5
            float r12 = r3 + r4
            float r3 = r0.clipBottom
            float r4 = r0.animateToClipBottom
            float r4 = r4 - r3
            float r4 = r4 * r5
            float r13 = r3 + r4
            float r3 = r0.clipTopOrigin
            float r4 = r0.animateToClipTopOrigin
            float r4 = r4 - r3
            float r4 = r4 * r5
            float r14 = r3 + r4
            float r3 = r0.clipBottomOrigin
            float r4 = r0.animateToClipBottomOrigin
            float r4 = r4 - r3
            float r4 = r4 * r5
            float r15 = r3 + r4
            float r3 = r0.clipHorizontal
            float r4 = r0.animateToClipHorizontal
            float r4 = r4 - r3
            float r4 = r4 * r5
            float r4 = r4 + r3
        L_0x00d7:
            float r3 = r0.animateToScale
            int r3 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r3 != 0) goto L_0x00eb
            float r3 = r0.scale
            int r3 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r3 != 0) goto L_0x00eb
            float r3 = r0.translationX
            r5 = 0
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 != 0) goto L_0x00eb
            r2 = r10
        L_0x00eb:
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r3 = r0.containerView
            r3.invalidate()
            r3 = 0
            goto L_0x0198
        L_0x00f3:
            long r3 = r0.animationStartTime
            r7 = 0
            int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r5 == 0) goto L_0x0126
            float r3 = r0.animateToX
            r0.translationX = r3
            float r3 = r0.animateToY
            r0.translationY = r3
            float r3 = r0.animateToClipBottom
            r0.clipBottom = r3
            float r3 = r0.animateToClipTop
            r0.clipTop = r3
            float r3 = r0.animateToClipTopOrigin
            r0.clipTopOrigin = r3
            float r3 = r0.animateToClipBottomOrigin
            r0.clipBottomOrigin = r3
            float r3 = r0.animateToClipHorizontal
            r0.clipHorizontal = r3
            float r3 = r0.animateToScale
            r0.scale = r3
            r0.animationStartTime = r7
            r0.updateMinMax(r3)
            r3 = 0
            r0.zoomAnimation = r3
            r0.useOvershootForScale = r3
            goto L_0x0127
        L_0x0126:
            r3 = 0
        L_0x0127:
            org.telegram.ui.Components.Scroller r4 = r0.scroller
            boolean r4 = r4.isFinished()
            if (r4 != 0) goto L_0x0182
            org.telegram.ui.Components.Scroller r4 = r0.scroller
            boolean r4 = r4.computeScrollOffset()
            if (r4 == 0) goto L_0x0182
            org.telegram.ui.Components.Scroller r4 = r0.scroller
            int r4 = r4.getStartX()
            float r4 = (float) r4
            float r5 = r0.maxX
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x015a
            org.telegram.ui.Components.Scroller r4 = r0.scroller
            int r4 = r4.getStartX()
            float r4 = (float) r4
            float r5 = r0.minX
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x015a
            org.telegram.ui.Components.Scroller r4 = r0.scroller
            int r4 = r4.getCurrX()
            float r4 = (float) r4
            r0.translationX = r4
        L_0x015a:
            org.telegram.ui.Components.Scroller r4 = r0.scroller
            int r4 = r4.getStartY()
            float r4 = (float) r4
            float r5 = r0.maxY
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x017d
            org.telegram.ui.Components.Scroller r4 = r0.scroller
            int r4 = r4.getStartY()
            float r4 = (float) r4
            float r5 = r0.minY
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x017d
            org.telegram.ui.Components.Scroller r4 = r0.scroller
            int r4 = r4.getCurrY()
            float r4 = (float) r4
            r0.translationY = r4
        L_0x017d:
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r4 = r0.containerView
            r4.invalidate()
        L_0x0182:
            float r9 = r0.scale
            float r10 = r0.translationY
            float r11 = r0.translationX
            float r12 = r0.clipTop
            float r13 = r0.clipBottom
            float r14 = r0.clipTopOrigin
            float r15 = r0.clipBottomOrigin
            float r4 = r0.clipHorizontal
            boolean r5 = r0.moving
            if (r5 != 0) goto L_0x0198
            float r2 = r0.translationY
        L_0x0198:
            r5 = 1
            int[] r7 = r0.animateFromRadius
            if (r7 == 0) goto L_0x01e8
            float[] r7 = r0.currentRadii
            r8 = 8
            if (r7 != 0) goto L_0x01a7
            float[] r7 = new float[r8]
            r0.currentRadii = r7
        L_0x01a7:
            boolean r7 = r0.animateToRadius
            if (r7 == 0) goto L_0x01ae
            float r7 = r0.animationValue
            goto L_0x01b2
        L_0x01ae:
            float r7 = r0.animationValue
            float r7 = r6 - r7
        L_0x01b2:
            r5 = 1
            r16 = 0
            r3 = r16
        L_0x01b7:
            if (r3 >= r8) goto L_0x01e6
            float[] r8 = r0.currentRadii
            int r18 = r3 + 1
            int[] r6 = r0.animateFromRadius
            int r20 = r3 / 2
            r6 = r6[r20]
            float r6 = (float) r6
            r20 = 1073741824(0x40000000, float:2.0)
            float r6 = r6 * r20
            r20 = r5
            r5 = 0
            float r6 = org.telegram.messenger.AndroidUtilities.lerp((float) r6, (float) r5, (float) r7)
            r8[r18] = r6
            r8[r3] = r6
            float[] r6 = r0.currentRadii
            r6 = r6[r3]
            int r6 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r6 <= 0) goto L_0x01dd
            r5 = 0
            goto L_0x01df
        L_0x01dd:
            r5 = r20
        L_0x01df:
            int r3 = r3 + 2
            r6 = 1065353216(0x3var_, float:1.0)
            r8 = 8
            goto L_0x01b7
        L_0x01e6:
            r20 = r5
        L_0x01e8:
            r3 = r11
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            int r8 = r0.photoAnimationInProgress
            r17 = r6
            r6 = 3
            if (r8 == r6) goto L_0x025e
            float r6 = r0.scale
            r8 = 1065353216(0x3var_, float:1.0)
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 != 0) goto L_0x022f
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            int r6 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r6 == 0) goto L_0x022f
            boolean r6 = r0.zoomAnimation
            if (r6 != 0) goto L_0x022f
            int r6 = r31.getContainerViewHeight()
            float r6 = (float) r6
            r8 = 1082130432(0x40800000, float:4.0)
            float r6 = r6 / r8
            org.telegram.ui.SecretMediaViewer$PhotoBackgroundDrawable r8 = r0.photoBackgroundDrawable
            r18 = r7
            r20 = 1132396544(0x437var_, float:255.0)
            float r7 = java.lang.Math.abs(r2)
            float r7 = java.lang.Math.min(r7, r6)
            float r7 = r7 / r6
            r19 = 1065353216(0x3var_, float:1.0)
            float r7 = r19 - r7
            float r7 = r7 * r20
            r20 = r2
            r2 = 1123942400(0x42fe0000, float:127.0)
            float r2 = java.lang.Math.max(r2, r7)
            int r2 = (int) r2
            r8.setAlpha(r2)
            goto L_0x023a
        L_0x022f:
            r20 = r2
            r18 = r7
            org.telegram.ui.SecretMediaViewer$PhotoBackgroundDrawable r2 = r0.photoBackgroundDrawable
            r6 = 255(0xff, float:3.57E-43)
            r2.setAlpha(r6)
        L_0x023a:
            boolean r2 = r0.zoomAnimation
            if (r2 != 0) goto L_0x0262
            float r2 = r0.maxX
            int r6 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r6 <= 0) goto L_0x0262
            float r2 = r3 - r2
            int r6 = r32.getWidth()
            float r6 = (float) r6
            float r2 = r2 / r6
            r6 = 1065353216(0x3var_, float:1.0)
            float r2 = java.lang.Math.min(r6, r2)
            r7 = 1050253722(0x3e99999a, float:0.3)
            float r7 = r7 * r2
            float r2 = r6 - r2
            float r3 = r0.maxX
            r6 = r7
            r7 = r2
            goto L_0x0266
        L_0x025e:
            r20 = r2
            r18 = r7
        L_0x0262:
            r6 = r17
            r7 = r18
        L_0x0266:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r2 = r0.aspectRatioFrameLayout
            if (r2 == 0) goto L_0x0273
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x0273
            r16 = 1
            goto L_0x0275
        L_0x0273:
            r16 = 0
        L_0x0275:
            r2 = r16
            r32.save()
            float r8 = r9 - r6
            int r17 = r31.getContainerViewWidth()
            r18 = r6
            int r6 = r17 / 2
            float r6 = (float) r6
            float r6 = r6 + r3
            int r17 = r31.getContainerViewHeight()
            r21 = r3
            int r3 = r17 / 2
            float r3 = (float) r3
            float r3 = r3 + r10
            r1.translate(r6, r3)
            r1.scale(r8, r8)
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            int r3 = r3.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r6 = r0.centerImage
            int r6 = r6.getBitmapHeight()
            if (r2 == 0) goto L_0x02e9
            r17 = r9
            boolean r9 = r0.textureUploaded
            if (r9 == 0) goto L_0x02e4
            float r9 = (float) r3
            r22 = r3
            float r3 = (float) r6
            float r9 = r9 / r3
            android.view.TextureView r3 = r0.videoTextureView
            int r3 = r3.getMeasuredWidth()
            float r3 = (float) r3
            r23 = r6
            android.view.TextureView r6 = r0.videoTextureView
            int r6 = r6.getMeasuredHeight()
            float r6 = (float) r6
            float r3 = r3 / r6
            float r6 = r9 - r3
            float r6 = java.lang.Math.abs(r6)
            r24 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
            int r6 = (r6 > r24 ? 1 : (r6 == r24 ? 0 : -1))
            if (r6 <= 0) goto L_0x02e1
            android.view.TextureView r6 = r0.videoTextureView
            int r6 = r6.getMeasuredWidth()
            r24 = r3
            android.view.TextureView r3 = r0.videoTextureView
            int r3 = r3.getMeasuredHeight()
            r30 = r6
            r6 = r3
            r3 = r30
            goto L_0x02f3
        L_0x02e1:
            r24 = r3
            goto L_0x02ef
        L_0x02e4:
            r22 = r3
            r23 = r6
            goto L_0x02ef
        L_0x02e9:
            r22 = r3
            r23 = r6
            r17 = r9
        L_0x02ef:
            r3 = r22
            r6 = r23
        L_0x02f3:
            int r9 = r31.getContainerViewHeight()
            float r9 = (float) r9
            r22 = r10
            float r10 = (float) r6
            float r9 = r9 / r10
            int r10 = r31.getContainerViewWidth()
            float r10 = (float) r10
            r23 = r11
            float r11 = (float) r3
            float r10 = r10 / r11
            float r9 = java.lang.Math.min(r9, r10)
            float r10 = (float) r3
            float r10 = r10 * r9
            int r10 = (int) r10
            float r11 = (float) r6
            float r11 = r11 * r9
            int r11 = (int) r11
            r24 = r3
            int r3 = -r10
            int r3 = r3 / 2
            float r3 = (float) r3
            float r25 = r4 / r8
            float r3 = r3 + r25
            r25 = r6
            int r6 = -r11
            int r6 = r6 / 2
            float r6 = (float) r6
            float r26 = r12 / r8
            float r6 = r6 + r26
            r26 = r9
            int r9 = r10 / 2
            float r9 = (float) r9
            float r27 = r4 / r8
            float r9 = r9 - r27
            r27 = r12
            int r12 = r11 / 2
            float r12 = (float) r12
            float r28 = r13 / r8
            float r12 = r12 - r28
            r1.clipRect(r3, r6, r9, r12)
            if (r5 != 0) goto L_0x0375
            android.graphics.Path r3 = r0.roundRectPath
            r3.reset()
            android.graphics.RectF r3 = org.telegram.messenger.AndroidUtilities.rectTmp
            int r6 = -r10
            int r6 = r6 / 2
            float r6 = (float) r6
            float r9 = r4 / r8
            float r6 = r6 + r9
            int r9 = -r11
            int r9 = r9 / 2
            float r9 = (float) r9
            float r12 = r14 / r8
            float r9 = r9 + r12
            int r12 = r10 / 2
            float r12 = (float) r12
            float r28 = r4 / r8
            float r12 = r12 - r28
            r28 = r4
            int r4 = r11 / 2
            float r4 = (float) r4
            float r29 = r15 / r8
            float r4 = r4 - r29
            r3.set(r6, r9, r12, r4)
            android.graphics.Path r3 = r0.roundRectPath
            android.graphics.RectF r4 = org.telegram.messenger.AndroidUtilities.rectTmp
            float[] r6 = r0.currentRadii
            android.graphics.Path$Direction r9 = android.graphics.Path.Direction.CW
            r3.addRoundRect(r4, r6, r9)
            android.graphics.Path r3 = r0.roundRectPath
            r1.clipPath(r3)
            goto L_0x0377
        L_0x0375:
            r28 = r4
        L_0x0377:
            if (r2 == 0) goto L_0x0389
            boolean r3 = r0.textureUploaded
            if (r3 == 0) goto L_0x0389
            boolean r3 = r0.videoCrossfadeStarted
            if (r3 == 0) goto L_0x0389
            float r3 = r0.videoCrossfadeAlpha
            r4 = 1065353216(0x3var_, float:1.0)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x03a2
        L_0x0389:
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            r3.setAlpha(r7)
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            int r4 = -r10
            int r4 = r4 / 2
            float r4 = (float) r4
            int r6 = -r11
            int r6 = r6 / 2
            float r6 = (float) r6
            float r9 = (float) r10
            float r12 = (float) r11
            r3.setImageCoords(r4, r6, r9, r12)
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            r3.draw(r1)
        L_0x03a2:
            if (r2 == 0) goto L_0x0404
            boolean r3 = r0.videoCrossfadeStarted
            if (r3 != 0) goto L_0x03b8
            boolean r3 = r0.textureUploaded
            if (r3 == 0) goto L_0x03b8
            r3 = 1
            r0.videoCrossfadeStarted = r3
            r3 = 0
            r0.videoCrossfadeAlpha = r3
            long r3 = java.lang.System.currentTimeMillis()
            r0.videoCrossfadeAlphaLastTime = r3
        L_0x03b8:
            int r3 = -r10
            int r3 = r3 / 2
            float r3 = (float) r3
            int r4 = -r11
            int r4 = r4 / 2
            float r4 = (float) r4
            r1.translate(r3, r4)
            android.view.TextureView r3 = r0.videoTextureView
            float r4 = r0.videoCrossfadeAlpha
            float r4 = r4 * r7
            r3.setAlpha(r4)
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r3 = r0.aspectRatioFrameLayout
            r3.draw(r1)
            boolean r3 = r0.videoCrossfadeStarted
            if (r3 == 0) goto L_0x0402
            float r3 = r0.videoCrossfadeAlpha
            r4 = 1065353216(0x3var_, float:1.0)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x0402
            long r3 = java.lang.System.currentTimeMillis()
            r6 = r2
            long r1 = r0.videoCrossfadeAlphaLastTime
            long r1 = r3 - r1
            r0.videoCrossfadeAlphaLastTime = r3
            float r9 = r0.videoCrossfadeAlpha
            float r12 = (float) r1
            r16 = 1128792064(0x43480000, float:200.0)
            float r12 = r12 / r16
            float r9 = r9 + r12
            r0.videoCrossfadeAlpha = r9
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r9 = r0.containerView
            r9.invalidate()
            float r9 = r0.videoCrossfadeAlpha
            r12 = 1065353216(0x3var_, float:1.0)
            int r9 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
            if (r9 <= 0) goto L_0x0405
            r0.videoCrossfadeAlpha = r12
            goto L_0x0405
        L_0x0402:
            r6 = r2
            goto L_0x0405
        L_0x0404:
            r6 = r2
        L_0x0405:
            r32.restore()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretMediaViewer.onDraw(android.graphics.Canvas):void");
    }

    public float getVideoCrossfadeAlpha() {
        return this.videoCrossfadeAlpha;
    }

    public void setVideoCrossfadeAlpha(float value) {
        this.videoCrossfadeAlpha = value;
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

    public boolean closePhoto(boolean animated, boolean byDelete) {
        final PhotoViewer.PlaceProviderObject object;
        if (this.parentActivity == null || !this.isPhotoVisible || checkPhotoAnimation()) {
            return false;
        }
        Activity activity = this.parentActivity;
        if (activity != null) {
            Window window = activity.getWindow();
            AndroidUtilities.setLightNavigationBar(window, this.wasLightNavigationBar);
            AndroidUtilities.setNavigationBarColor(window, this.wasNavigationBarColor);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        this.isActionBarVisible = false;
        VelocityTracker velocityTracker2 = this.velocityTracker;
        if (velocityTracker2 != null) {
            velocityTracker2.recycle();
            this.velocityTracker = null;
        }
        this.closeTime = System.currentTimeMillis();
        if (this.currentProvider == null || (this.currentMessageObject.messageOwner.media.photo instanceof TLRPC.TL_photoEmpty) || (this.currentMessageObject.messageOwner.media.document instanceof TLRPC.TL_documentEmpty)) {
            object = null;
        } else {
            object = this.currentProvider.getPlaceForPhoto(this.currentMessageObject, (TLRPC.FileLocation) null, 0, true);
        }
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.pause();
        }
        if (animated) {
            this.photoAnimationInProgress = 3;
            this.containerView.invalidate();
            this.imageMoveAnimation = new AnimatorSet();
            if (object == null || object.imageReceiver.getThumbBitmap() == null || byDelete) {
                int h = AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                this.animateToY = this.translationY >= 0.0f ? (float) h : (float) (-h);
            } else {
                object.imageReceiver.setVisible(false, true);
                RectF drawRegion = object.imageReceiver.getDrawRegion();
                float width = drawRegion.right - drawRegion.left;
                float height = drawRegion.bottom - drawRegion.top;
                int viewWidth = AndroidUtilities.displaySize.x;
                int viewHeight = AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                this.animateToScale = Math.max(width / ((float) viewWidth), height / ((float) viewHeight));
                this.animateToX = ((((float) object.viewX) + drawRegion.left) + (width / 2.0f)) - ((float) (viewWidth / 2));
                this.animateToY = ((((float) object.viewY) + drawRegion.top) + (height / 2.0f)) - ((float) (viewHeight / 2));
                this.animateToClipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
                int clipVertical = (int) Math.abs(drawRegion.top - object.imageReceiver.getImageY());
                int[] coords2 = new int[2];
                object.parentView.getLocationInWindow(coords2);
                float f = (((float) (coords2[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) - (((float) object.viewY) + drawRegion.top)) + ((float) object.clipTopAddition);
                this.animateToClipTop = f;
                this.animateToClipTop = Math.max(0.0f, Math.max(f, (float) clipVertical));
                float height2 = (((((float) object.viewY) + drawRegion.top) + ((float) ((int) height))) - ((float) ((coords2[1] + object.parentView.getHeight()) - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)))) + ((float) object.clipBottomAddition);
                this.animateToClipBottom = height2;
                this.animateToClipBottom = Math.max(0.0f, Math.max(height2, (float) clipVertical));
                this.animateToClipTopOrigin = 0.0f;
                this.animateToClipTopOrigin = Math.max(0.0f, Math.max(0.0f, (float) clipVertical));
                this.animateToClipBottomOrigin = 0.0f;
                this.animateToClipBottomOrigin = Math.max(0.0f, Math.max(0.0f, (float) clipVertical));
                this.animationStartTime = System.currentTimeMillis();
                this.zoomAnimation = true;
            }
            this.animateToRadius = false;
            if (this.isVideo) {
                this.videoCrossfadeStarted = false;
                this.textureUploaded = false;
                this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofInt(this.photoBackgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0}), ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.secretDeleteTimer, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this, "videoCrossfadeAlpha", new float[]{0.0f})});
            } else {
                this.centerImage.setManualAlphaAnimator(true);
                this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofInt(this.photoBackgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0}), ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.secretDeleteTimer, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.centerImage, "currentAlpha", new float[]{0.0f})});
            }
            this.photoAnimationEndRunnable = new SecretMediaViewer$$ExternalSyntheticLambda3(this, object);
            this.imageMoveAnimation.setInterpolator(new DecelerateInterpolator());
            this.imageMoveAnimation.setDuration(250);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PhotoViewer.PlaceProviderObject placeProviderObject = object;
                    if (placeProviderObject != null) {
                        placeProviderObject.imageReceiver.setVisible(true, true);
                    }
                    boolean unused = SecretMediaViewer.this.isVisible = false;
                    AndroidUtilities.runOnUIThread(new SecretMediaViewer$7$$ExternalSyntheticLambda0(this));
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-SecretMediaViewer$7  reason: not valid java name */
                public /* synthetic */ void m3221lambda$onAnimationEnd$0$orgtelegramuiSecretMediaViewer$7() {
                    if (SecretMediaViewer.this.photoAnimationEndRunnable != null) {
                        SecretMediaViewer.this.photoAnimationEndRunnable.run();
                        Runnable unused = SecretMediaViewer.this.photoAnimationEndRunnable = null;
                    }
                }
            });
            this.photoTransitionAnimationStartTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(2, (Paint) null);
            }
            this.imageMoveAnimation.start();
        } else {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.SCALE_X, new float[]{0.9f}), ObjectAnimator.ofFloat(this.containerView, View.SCALE_Y, new float[]{0.9f}), ObjectAnimator.ofInt(this.photoBackgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f})});
            this.photoAnimationInProgress = 2;
            this.photoAnimationEndRunnable = new SecretMediaViewer$$ExternalSyntheticLambda4(this, object);
            animatorSet.setDuration(200);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (SecretMediaViewer.this.photoAnimationEndRunnable != null) {
                        SecretMediaViewer.this.photoAnimationEndRunnable.run();
                        Runnable unused = SecretMediaViewer.this.photoAnimationEndRunnable = null;
                    }
                }
            });
            this.photoTransitionAnimationStartTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(2, (Paint) null);
            }
            animatorSet.start();
        }
        return true;
    }

    /* renamed from: lambda$closePhoto$3$org-telegram-ui-SecretMediaViewer  reason: not valid java name */
    public /* synthetic */ void m3214lambda$closePhoto$3$orgtelegramuiSecretMediaViewer(PhotoViewer.PlaceProviderObject object) {
        this.imageMoveAnimation = null;
        this.photoAnimationInProgress = 0;
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, (Paint) null);
        }
        this.containerView.setVisibility(4);
        onPhotoClosed(object);
    }

    /* renamed from: lambda$closePhoto$4$org-telegram-ui-SecretMediaViewer  reason: not valid java name */
    public /* synthetic */ void m3215lambda$closePhoto$4$orgtelegramuiSecretMediaViewer(PhotoViewer.PlaceProviderObject object) {
        if (this.containerView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, (Paint) null);
            }
            this.containerView.setVisibility(4);
            this.photoAnimationInProgress = 0;
            onPhotoClosed(object);
            this.containerView.setScaleX(1.0f);
            this.containerView.setScaleY(1.0f);
        }
    }

    private void onPhotoClosed(PhotoViewer.PlaceProviderObject object) {
        this.isVisible = false;
        this.currentProvider = null;
        this.disableShowCheck = false;
        releasePlayer();
        new ArrayList();
        AndroidUtilities.runOnUIThread(new SecretMediaViewer$$ExternalSyntheticLambda1(this), 50);
    }

    /* renamed from: lambda$onPhotoClosed$5$org-telegram-ui-SecretMediaViewer  reason: not valid java name */
    public /* synthetic */ void m3216lambda$onPhotoClosed$5$orgtelegramuiSecretMediaViewer() {
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
    public void updateMinMax(float scale2) {
        int maxW = ((int) ((this.centerImage.getImageWidth() * scale2) - ((float) getContainerViewWidth()))) / 2;
        int maxH = ((int) ((this.centerImage.getImageHeight() * scale2) - ((float) getContainerViewHeight()))) / 2;
        if (maxW > 0) {
            this.minX = (float) (-maxW);
            this.maxX = (float) maxW;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (maxH > 0) {
            this.minY = (float) (-maxH);
            this.maxY = (float) maxH;
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
    /* JADX WARNING: Removed duplicated region for block: B:89:0x01dd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processTouchEvent(android.view.MotionEvent r13) {
        /*
            r12 = this;
            int r0 = r12.photoAnimationInProgress
            r1 = 0
            if (r0 != 0) goto L_0x0382
            long r2 = r12.animationStartTime
            r4 = 0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x000f
            goto L_0x0382
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
            if (r0 == 0) goto L_0x02fe
            int r0 = r13.getActionMasked()
            r7 = 5
            if (r0 != r7) goto L_0x003e
            goto L_0x02fe
        L_0x003e:
            int r0 = r13.getActionMasked()
            r7 = 0
            r8 = 1077936128(0x40400000, float:3.0)
            r9 = 1065353216(0x3var_, float:1.0)
            if (r0 != r6) goto L_0x01f8
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
            float r2 = r13.getY(r1)
            float r0 = r0 - r2
            double r7 = (double) r0
            double r2 = java.lang.Math.hypot(r3, r7)
            float r0 = (float) r2
            float r2 = r12.pinchStartDistance
            float r0 = r0 / r2
            float r2 = r12.pinchStartScale
            float r0 = r0 * r2
            r12.scale = r0
            float r0 = r12.pinchCenterX
            int r2 = r12.getContainerViewWidth()
            int r2 = r2 / r6
            float r2 = (float) r2
            float r0 = r0 - r2
            float r2 = r12.pinchCenterX
            int r3 = r12.getContainerViewWidth()
            int r3 = r3 / r6
            float r3 = (float) r3
            float r2 = r2 - r3
            float r3 = r12.pinchStartX
            float r2 = r2 - r3
            float r3 = r12.scale
            float r4 = r12.pinchStartScale
            float r3 = r3 / r4
            float r2 = r2 * r3
            float r0 = r0 - r2
            r12.translationX = r0
            float r0 = r12.pinchCenterY
            int r2 = r12.getContainerViewHeight()
            int r2 = r2 / r6
            float r2 = (float) r2
            float r0 = r0 - r2
            float r2 = r12.pinchCenterY
            int r3 = r12.getContainerViewHeight()
            int r3 = r3 / r6
            float r3 = (float) r3
            float r2 = r2 - r3
            float r3 = r12.pinchStartY
            float r2 = r2 - r3
            float r3 = r12.scale
            float r4 = r12.pinchStartScale
            float r4 = r3 / r4
            float r2 = r2 * r4
            float r0 = r0 - r2
            r12.translationY = r0
            r12.updateMinMax(r3)
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r0 = r12.containerView
            r0.invalidate()
            goto L_0x0381
        L_0x00c4:
            int r0 = r13.getPointerCount()
            if (r0 != r2) goto L_0x0381
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
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r10 = (float) r10
            int r10 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r10 > 0) goto L_0x00f9
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r10 = (float) r10
            int r10 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r10 <= 0) goto L_0x00fb
        L_0x00f9:
            r12.discardTap = r2
        L_0x00fb:
            boolean r10 = r12.canDragDown
            if (r10 == 0) goto L_0x012c
            boolean r10 = r12.draggingDown
            if (r10 != 0) goto L_0x012c
            float r10 = r12.scale
            int r10 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1))
            if (r10 != 0) goto L_0x012c
            r10 = 1106247680(0x41var_, float:30.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            int r10 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r10 < 0) goto L_0x012c
            float r3 = r6 / r3
            int r3 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r3 <= 0) goto L_0x012c
            r12.draggingDown = r2
            r12.moving = r1
            float r3 = r13.getY()
            r12.dragY = r3
            boolean r3 = r12.isActionBarVisible
            if (r3 == 0) goto L_0x012b
            r12.toggleActionBar(r1, r2)
        L_0x012b:
            return r2
        L_0x012c:
            boolean r3 = r12.draggingDown
            if (r3 == 0) goto L_0x0140
            float r2 = r13.getY()
            float r3 = r12.dragY
            float r2 = r2 - r3
            r12.translationY = r2
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r2 = r12.containerView
            r2.invalidate()
            goto L_0x01f6
        L_0x0140:
            boolean r3 = r12.invalidCoords
            if (r3 != 0) goto L_0x01e8
            long r10 = r12.animationStartTime
            int r3 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r3 != 0) goto L_0x01e8
            float r3 = r12.moveStartX
            float r4 = r13.getX()
            float r3 = r3 - r4
            float r4 = r12.moveStartY
            float r5 = r13.getY()
            float r4 = r4 - r5
            boolean r5 = r12.moving
            if (r5 != 0) goto L_0x017c
            float r5 = r12.scale
            int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r5 != 0) goto L_0x0176
            float r5 = java.lang.Math.abs(r4)
            r10 = 1094713344(0x41400000, float:12.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r5 = r5 + r10
            float r10 = java.lang.Math.abs(r3)
            int r5 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r5 < 0) goto L_0x017c
        L_0x0176:
            float r5 = r12.scale
            int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r5 == 0) goto L_0x01e7
        L_0x017c:
            boolean r5 = r12.moving
            if (r5 != 0) goto L_0x0186
            r3 = 0
            r4 = 0
            r12.moving = r2
            r12.canDragDown = r1
        L_0x0186:
            float r2 = r13.getX()
            r12.moveStartX = r2
            float r2 = r13.getY()
            r12.moveStartY = r2
            float r2 = r12.scale
            r12.updateMinMax(r2)
            float r2 = r12.translationX
            float r5 = r12.minX
            int r5 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r5 < 0) goto L_0x01a5
            float r5 = r12.maxX
            int r5 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x01a6
        L_0x01a5:
            float r3 = r3 / r8
        L_0x01a6:
            float r5 = r12.maxY
            int r10 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r10 != 0) goto L_0x01c7
            float r10 = r12.minY
            int r7 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
            if (r7 != 0) goto L_0x01c7
            float r7 = r12.translationY
            float r8 = r7 - r4
            int r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r8 >= 0) goto L_0x01be
            r12.translationY = r10
            r4 = 0
            goto L_0x01d4
        L_0x01be:
            float r7 = r7 - r4
            int r7 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x01d4
            r12.translationY = r5
            r4 = 0
            goto L_0x01d4
        L_0x01c7:
            float r7 = r12.translationY
            float r10 = r12.minY
            int r10 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r10 < 0) goto L_0x01d3
            int r5 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x01d4
        L_0x01d3:
            float r4 = r4 / r8
        L_0x01d4:
            float r2 = r2 - r3
            r12.translationX = r2
            float r2 = r12.scale
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x01e2
            float r2 = r12.translationY
            float r2 = r2 - r4
            r12.translationY = r2
        L_0x01e2:
            org.telegram.ui.SecretMediaViewer$FrameLayoutDrawer r2 = r12.containerView
            r2.invalidate()
        L_0x01e7:
            goto L_0x01f6
        L_0x01e8:
            r12.invalidCoords = r1
            float r2 = r13.getX()
            r12.moveStartX = r2
            float r2 = r13.getY()
            r12.moveStartY = r2
        L_0x01f6:
            goto L_0x0381
        L_0x01f8:
            int r0 = r13.getActionMasked()
            r3 = 3
            if (r0 == r3) goto L_0x020c
            int r0 = r13.getActionMasked()
            if (r0 == r2) goto L_0x020c
            int r0 = r13.getActionMasked()
            r3 = 6
            if (r0 != r3) goto L_0x0381
        L_0x020c:
            boolean r0 = r12.zooming
            if (r0 == 0) goto L_0x028b
            r12.invalidCoords = r2
            float r0 = r12.scale
            int r3 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r3 >= 0) goto L_0x021f
            r12.updateMinMax(r9)
            r12.animateTo(r9, r7, r7, r2)
            goto L_0x0287
        L_0x021f:
            int r0 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r0 <= 0) goto L_0x0284
            float r0 = r12.pinchCenterX
            int r3 = r12.getContainerViewWidth()
            int r3 = r3 / r6
            float r3 = (float) r3
            float r0 = r0 - r3
            float r3 = r12.pinchCenterX
            int r4 = r12.getContainerViewWidth()
            int r4 = r4 / r6
            float r4 = (float) r4
            float r3 = r3 - r4
            float r4 = r12.pinchStartX
            float r3 = r3 - r4
            float r4 = r12.pinchStartScale
            float r4 = r8 / r4
            float r3 = r3 * r4
            float r0 = r0 - r3
            float r3 = r12.pinchCenterY
            int r4 = r12.getContainerViewHeight()
            int r4 = r4 / r6
            float r4 = (float) r4
            float r3 = r3 - r4
            float r4 = r12.pinchCenterY
            int r5 = r12.getContainerViewHeight()
            int r5 = r5 / r6
            float r5 = (float) r5
            float r4 = r4 - r5
            float r5 = r12.pinchStartY
            float r4 = r4 - r5
            float r5 = r12.pinchStartScale
            float r5 = r8 / r5
            float r4 = r4 * r5
            float r3 = r3 - r4
            r12.updateMinMax(r8)
            float r4 = r12.minX
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x0267
            float r0 = r12.minX
            goto L_0x026f
        L_0x0267:
            float r4 = r12.maxX
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x026f
            float r0 = r12.maxX
        L_0x026f:
            float r4 = r12.minY
            int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x0278
            float r3 = r12.minY
            goto L_0x0280
        L_0x0278:
            float r4 = r12.maxY
            int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x0280
            float r3 = r12.maxY
        L_0x0280:
            r12.animateTo(r8, r0, r3, r2)
            goto L_0x0287
        L_0x0284:
            r12.checkMinMax(r2)
        L_0x0287:
            r12.zooming = r1
            goto L_0x0381
        L_0x028b:
            boolean r0 = r12.draggingDown
            if (r0 == 0) goto L_0x02b1
            float r0 = r12.dragY
            float r3 = r13.getY()
            float r0 = r0 - r3
            float r0 = java.lang.Math.abs(r0)
            int r3 = r12.getContainerViewHeight()
            float r3 = (float) r3
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            float r3 = r3 / r4
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x02aa
            r12.closePhoto(r2, r1)
            goto L_0x02ad
        L_0x02aa:
            r12.animateTo(r9, r7, r7, r1)
        L_0x02ad:
            r12.draggingDown = r1
            goto L_0x0381
        L_0x02b1:
            boolean r0 = r12.moving
            if (r0 == 0) goto L_0x0381
            float r0 = r12.translationX
            float r3 = r12.translationY
            float r4 = r12.scale
            r12.updateMinMax(r4)
            r12.moving = r1
            r12.canDragDown = r2
            android.view.VelocityTracker r2 = r12.velocityTracker
            if (r2 == 0) goto L_0x02d1
            float r4 = r12.scale
            int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r4 != 0) goto L_0x02d1
            r4 = 1000(0x3e8, float:1.401E-42)
            r2.computeCurrentVelocity(r4)
        L_0x02d1:
            float r2 = r12.translationX
            float r4 = r12.minX
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x02dc
            float r0 = r12.minX
            goto L_0x02e4
        L_0x02dc:
            float r4 = r12.maxX
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x02e4
            float r0 = r12.maxX
        L_0x02e4:
            float r2 = r12.translationY
            float r4 = r12.minY
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x02ef
            float r3 = r12.minY
            goto L_0x02f7
        L_0x02ef:
            float r4 = r12.maxY
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x02f7
            float r3 = r12.maxY
        L_0x02f7:
            float r2 = r12.scale
            r12.animateTo(r2, r0, r3, r1)
            goto L_0x0381
        L_0x02fe:
            r12.discardTap = r1
            org.telegram.ui.Components.Scroller r0 = r12.scroller
            boolean r0 = r0.isFinished()
            if (r0 != 0) goto L_0x030d
            org.telegram.ui.Components.Scroller r0 = r12.scroller
            r0.abortAnimation()
        L_0x030d:
            boolean r0 = r12.draggingDown
            if (r0 != 0) goto L_0x0381
            int r0 = r13.getPointerCount()
            if (r0 != r6) goto L_0x0362
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
            float r4 = r13.getY(r2)
            float r0 = r0 + r4
            float r0 = r0 / r3
            r12.pinchCenterY = r0
            float r0 = r12.translationX
            r12.pinchStartX = r0
            float r0 = r12.translationY
            r12.pinchStartY = r0
            r12.zooming = r2
            r12.moving = r1
            android.view.VelocityTracker r0 = r12.velocityTracker
            if (r0 == 0) goto L_0x0381
            r0.clear()
            goto L_0x0381
        L_0x0362:
            int r0 = r13.getPointerCount()
            if (r0 != r2) goto L_0x0381
            float r0 = r13.getX()
            r12.moveStartX = r0
            float r0 = r13.getY()
            r12.moveStartY = r0
            r12.dragY = r0
            r12.draggingDown = r1
            r12.canDragDown = r2
            android.view.VelocityTracker r0 = r12.velocityTracker
            if (r0 == 0) goto L_0x0381
            r0.clear()
        L_0x0381:
            return r1
        L_0x0382:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretMediaViewer.processTouchEvent(android.view.MotionEvent):boolean");
    }

    private void checkMinMax(boolean zoom) {
        float moveToX = this.translationX;
        float moveToY = this.translationY;
        updateMinMax(this.scale);
        float f = this.translationX;
        if (f < this.minX) {
            moveToX = this.minX;
        } else if (f > this.maxX) {
            moveToX = this.maxX;
        }
        float f2 = this.translationY;
        if (f2 < this.minY) {
            moveToY = this.minY;
        } else if (f2 > this.maxY) {
            moveToY = this.maxY;
        }
        animateTo(this.scale, moveToX, moveToY, zoom);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom) {
        animateTo(newScale, newTx, newTy, isZoom, 250);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom, int duration) {
        if (this.scale != newScale || this.translationX != newTx || this.translationY != newTy) {
            this.zoomAnimation = isZoom;
            this.animateToScale = newScale;
            this.animateToX = newTx;
            this.animateToY = newTy;
            this.animationStartTime = System.currentTimeMillis();
            AnimatorSet animatorSet = new AnimatorSet();
            this.imageMoveAnimation = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f})});
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) duration);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = SecretMediaViewer.this.imageMoveAnimation = null;
                    SecretMediaViewer.this.containerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    public void setAnimationValue(float value) {
        this.animationValue = value;
        this.containerView.invalidate();
    }

    public float getAnimationValue() {
        return this.animationValue;
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (this.scale == 1.0f) {
            return false;
        }
        this.scroller.abortAnimation();
        this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(velocityX), Math.round(velocityY), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
        this.containerView.postInvalidate();
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (this.discardTap) {
            return false;
        }
        toggleActionBar(!this.isActionBarVisible, true);
        return true;
    }

    public boolean onDoubleTap(MotionEvent e) {
        float f = this.scale;
        if ((f == 1.0f && (this.translationY != 0.0f || this.translationX != 0.0f)) || this.animationStartTime != 0 || this.photoAnimationInProgress != 0) {
            return false;
        }
        if (f == 1.0f) {
            float atx = (e.getX() - ((float) (getContainerViewWidth() / 2))) - (((e.getX() - ((float) (getContainerViewWidth() / 2))) - this.translationX) * (3.0f / this.scale));
            float aty = (e.getY() - ((float) (getContainerViewHeight() / 2))) - (((e.getY() - ((float) (getContainerViewHeight() / 2))) - this.translationY) * (3.0f / this.scale));
            updateMinMax(3.0f);
            if (atx < this.minX) {
                atx = this.minX;
            } else if (atx > this.maxX) {
                atx = this.maxX;
            }
            if (aty < this.minY) {
                aty = this.minY;
            } else if (aty > this.maxY) {
                aty = this.maxY;
            }
            animateTo(3.0f, atx, aty, true);
        } else {
            animateTo(1.0f, 0.0f, 0.0f, true);
        }
        this.doubleTap = true;
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    private boolean scaleToFill() {
        return false;
    }
}
