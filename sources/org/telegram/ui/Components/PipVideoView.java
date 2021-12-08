package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.PhotoViewer;

public class PipVideoView {
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public View controlsView;
    private DecelerateInterpolator decelerateInterpolator;
    private boolean isInAppOnly;
    private Activity parentActivity;
    /* access modifiers changed from: private */
    public EmbedBottomSheet parentSheet;
    /* access modifiers changed from: private */
    public PhotoViewer photoViewer;
    private SharedPreferences preferences;
    private int videoHeight;
    /* access modifiers changed from: private */
    public int videoWidth;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager windowManager;
    /* access modifiers changed from: private */
    public FrameLayout windowView;

    private class MiniControlsView extends FrameLayout {
        /* access modifiers changed from: private */
        public float bufferedPosition;
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation;
        private Runnable hideRunnable = new PipVideoView$MiniControlsView$$ExternalSyntheticLambda3(this);
        /* access modifiers changed from: private */
        public boolean isCompleted;
        private boolean isVisible = true;
        private ImageView playButton;
        /* access modifiers changed from: private */
        public float progress;
        private Paint progressInnerPaint;
        private Paint progressPaint;
        /* access modifiers changed from: private */
        public Runnable progressRunnable = new Runnable() {
            public void run() {
                VideoPlayer videoPlayer;
                if (PipVideoView.this.photoViewer != null && (videoPlayer = PipVideoView.this.photoViewer.getVideoPlayer()) != null) {
                    MiniControlsView.this.setProgress(((float) videoPlayer.getCurrentPosition()) / ((float) videoPlayer.getDuration()));
                    if (PipVideoView.this.photoViewer == null) {
                        MiniControlsView.this.setBufferedProgress(((float) videoPlayer.getBufferedPosition()) / ((float) videoPlayer.getDuration()));
                    }
                    AndroidUtilities.runOnUIThread(MiniControlsView.this.progressRunnable, 1000);
                }
            }
        };

        /* renamed from: lambda$new$0$org-telegram-ui-Components-PipVideoView$MiniControlsView  reason: not valid java name */
        public /* synthetic */ void m2503xd1abb901() {
            show(false, true);
        }

        public MiniControlsView(Context context, boolean fullControls) {
            super(context);
            ImageView inlineButton = new ImageView(context);
            inlineButton.setScaleType(ImageView.ScaleType.CENTER);
            inlineButton.setImageResource(NUM);
            addView(inlineButton, LayoutHelper.createFrame(56, 48, 53));
            inlineButton.setOnClickListener(new PipVideoView$MiniControlsView$$ExternalSyntheticLambda0(this));
            if (fullControls) {
                Paint paint = new Paint();
                this.progressPaint = paint;
                paint.setColor(-15095832);
                Paint paint2 = new Paint();
                this.progressInnerPaint = paint2;
                paint2.setColor(-6975081);
                setWillNotDraw(false);
                ImageView imageView = new ImageView(context);
                this.playButton = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
                this.playButton.setOnClickListener(new PipVideoView$MiniControlsView$$ExternalSyntheticLambda1(this));
            }
            setOnTouchListener(PipVideoView$MiniControlsView$$ExternalSyntheticLambda2.INSTANCE);
            updatePlayButton();
            show(false, false);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Components-PipVideoView$MiniControlsView  reason: not valid java name */
        public /* synthetic */ void m2504x5e98d020(View v) {
            if (PipVideoView.this.parentSheet != null) {
                PipVideoView.this.parentSheet.exitFromPip();
            } else if (PipVideoView.this.photoViewer != null) {
                PipVideoView.this.photoViewer.exitFromPip();
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-Components-PipVideoView$MiniControlsView  reason: not valid java name */
        public /* synthetic */ void m2505xeb85e73f(View v) {
            VideoPlayer videoPlayer;
            if (PipVideoView.this.photoViewer != null && (videoPlayer = PipVideoView.this.photoViewer.getVideoPlayer()) != null) {
                if (videoPlayer.isPlaying()) {
                    videoPlayer.pause();
                } else {
                    videoPlayer.play();
                }
                updatePlayButton();
            }
        }

        static /* synthetic */ boolean lambda$new$3(View v, MotionEvent event) {
            return true;
        }

        /* access modifiers changed from: private */
        public void updatePlayButton() {
            VideoPlayer videoPlayer;
            if (PipVideoView.this.photoViewer != null && (videoPlayer = PipVideoView.this.photoViewer.getVideoPlayer()) != null) {
                AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
                if (videoPlayer.isPlaying()) {
                    this.playButton.setImageResource(NUM);
                    AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
                } else if (this.isCompleted) {
                    this.playButton.setImageResource(NUM);
                } else {
                    this.playButton.setImageResource(NUM);
                }
            }
        }

        public void setBufferedProgress(float position) {
            this.bufferedPosition = position;
            invalidate();
        }

        public void setProgress(float value) {
            this.progress = value;
            invalidate();
        }

        public void show(boolean value, boolean animated) {
            if (this.isVisible != value) {
                this.isVisible = value;
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                if (this.isVisible) {
                    if (animated) {
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        this.currentAnimation = animatorSet2;
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{1.0f})});
                        this.currentAnimation.setDuration(150);
                        this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = MiniControlsView.this.currentAnimation = null;
                            }
                        });
                        this.currentAnimation.start();
                    } else {
                        setAlpha(1.0f);
                    }
                } else if (animated) {
                    AnimatorSet animatorSet3 = new AnimatorSet();
                    this.currentAnimation = animatorSet3;
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f})});
                    this.currentAnimation.setDuration(150);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AnimatorSet unused = MiniControlsView.this.currentAnimation = null;
                        }
                    });
                    this.currentAnimation.start();
                } else {
                    setAlpha(0.0f);
                }
                checkNeedHide();
            }
        }

        private void checkNeedHide() {
            AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            if (this.isVisible) {
                AndroidUtilities.runOnUIThread(this.hideRunnable, 3000);
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() == 0) {
                if (!this.isVisible) {
                    show(true, true);
                    return true;
                }
                checkNeedHide();
            }
            return super.onInterceptTouchEvent(ev);
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (this.currentAnimation != null) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
            checkNeedHide();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            checkNeedHide();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int progressLineY = height - AndroidUtilities.dp(3.0f);
            int dp = height - AndroidUtilities.dp(7.0f);
            int progressX = ((int) (((float) (width - 0)) * this.progress)) + 0;
            float f = this.bufferedPosition;
            if (f != 0.0f) {
                canvas.drawRect((float) 0, (float) progressLineY, ((float) 0) + (((float) (width - 0)) * f), (float) (AndroidUtilities.dp(3.0f) + progressLineY), this.progressInnerPaint);
            }
            canvas.drawRect((float) 0, (float) progressLineY, (float) progressX, (float) (AndroidUtilities.dp(3.0f) + progressLineY), this.progressPaint);
        }
    }

    public PipVideoView(boolean inAppOnly) {
        this.isInAppOnly = inAppOnly;
    }

    public TextureView show(Activity activity, EmbedBottomSheet sheet, View controls, float aspectRatio, int rotation, WebView webview) {
        return show(activity, (PhotoViewer) null, sheet, controls, aspectRatio, rotation, webview);
    }

    public TextureView show(Activity activity, PhotoViewer viewer, float aspectRatio, int rotation, WebView webview) {
        return show(activity, viewer, (EmbedBottomSheet) null, (View) null, aspectRatio, rotation, webview);
    }

    public TextureView show(Activity activity, PhotoViewer viewer, float aspectRatio, int rotation) {
        return show(activity, viewer, (EmbedBottomSheet) null, (View) null, aspectRatio, rotation, (WebView) null);
    }

    public TextureView show(Activity activity, PhotoViewer viewer, EmbedBottomSheet sheet, View controls, float aspectRatio, int rotation, WebView webview) {
        TextureView textureView;
        Activity activity2 = activity;
        PhotoViewer photoViewer2 = viewer;
        View view = controls;
        float f = aspectRatio;
        WebView webView = webview;
        this.parentSheet = sheet;
        this.parentActivity = activity2;
        this.photoViewer = photoViewer2;
        this.windowView = new FrameLayout(activity2) {
            private boolean dragging;
            private float startX;
            private float startY;

            public boolean dispatchTouchEvent(MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();
                if (event.getAction() == 0) {
                    this.startX = x;
                    this.startY = y;
                } else if (event.getAction() == 2 && !this.dragging && (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.3f, true) || Math.abs(this.startY - y) >= AndroidUtilities.getPixelsInCM(0.3f, false))) {
                    this.dragging = true;
                    this.startX = x;
                    this.startY = y;
                    if (PipVideoView.this.controlsView != null) {
                        ((ViewParent) PipVideoView.this.controlsView).requestDisallowInterceptTouchEvent(true);
                    }
                    return true;
                }
                if (!this.dragging) {
                    return super.dispatchTouchEvent(event);
                }
                if (event.getAction() == 2) {
                    WindowManager.LayoutParams access$500 = PipVideoView.this.windowLayoutParams;
                    access$500.x = (int) (((float) access$500.x) + (x - this.startX));
                    WindowManager.LayoutParams access$5002 = PipVideoView.this.windowLayoutParams;
                    access$5002.y = (int) (((float) access$5002.y) + (y - this.startY));
                    int maxDiff = PipVideoView.this.videoWidth / 2;
                    if (PipVideoView.this.windowLayoutParams.x < (-maxDiff)) {
                        PipVideoView.this.windowLayoutParams.x = -maxDiff;
                    } else if (PipVideoView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + maxDiff) {
                        PipVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + maxDiff;
                    }
                    float alpha = 1.0f;
                    if (PipVideoView.this.windowLayoutParams.x < 0) {
                        alpha = ((((float) PipVideoView.this.windowLayoutParams.x) / ((float) maxDiff)) * 0.5f) + 1.0f;
                    } else if (PipVideoView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) {
                        alpha = 1.0f - ((((float) ((PipVideoView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + PipVideoView.this.windowLayoutParams.width)) / ((float) maxDiff)) * 0.5f);
                    }
                    if (PipVideoView.this.windowView.getAlpha() != alpha) {
                        PipVideoView.this.windowView.setAlpha(alpha);
                    }
                    if (PipVideoView.this.windowLayoutParams.y < (-0)) {
                        PipVideoView.this.windowLayoutParams.y = -0;
                    } else if (PipVideoView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + 0) {
                        PipVideoView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + 0;
                    }
                    PipVideoView.this.windowManager.updateViewLayout(PipVideoView.this.windowView, PipVideoView.this.windowLayoutParams);
                    this.startX = x;
                    this.startY = y;
                } else if (event.getAction() == 1 || event.getAction() == 3) {
                    this.dragging = false;
                    PipVideoView.this.animateToBoundsMaybe();
                }
                return true;
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                if (PipVideoView.this.animatorSet != null) {
                    PipVideoView.this.animatorSet.cancel();
                    AnimatorSet unused = PipVideoView.this.animatorSet = null;
                }
            }
        };
        if (f > 1.0f) {
            int dp = AndroidUtilities.dp(192.0f);
            this.videoWidth = dp;
            this.videoHeight = (int) (((float) dp) / f);
        } else {
            int dp2 = AndroidUtilities.dp(192.0f);
            this.videoHeight = dp2;
            this.videoWidth = (int) (((float) dp2) * f);
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(activity2);
        aspectRatioFrameLayout.setAspectRatio(f, rotation);
        this.windowView.addView(aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        if (webView != null) {
            ViewGroup parent = (ViewGroup) webview.getParent();
            if (parent != null) {
                parent.removeView(webView);
            }
            aspectRatioFrameLayout.addView(webView, LayoutHelper.createFrame(-1, -1.0f));
            textureView = null;
        } else {
            textureView = new TextureView(activity2);
            aspectRatioFrameLayout.addView(textureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (view == null) {
            this.controlsView = new MiniControlsView(activity2, photoViewer2 != null);
        } else {
            this.controlsView = view;
        }
        this.windowView.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        if (this.isInAppOnly) {
            this.windowManager = activity.getWindowManager();
        } else {
            this.windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        }
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        this.preferences = sharedPreferences;
        int sidex = sharedPreferences.getInt("sidex", 1);
        int sidey = this.preferences.getInt("sidey", 0);
        float px = this.preferences.getFloat("px", 0.0f);
        float py = this.preferences.getFloat("py", 0.0f);
        try {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
            layoutParams.width = this.videoWidth;
            this.windowLayoutParams.height = this.videoHeight;
            this.windowLayoutParams.x = getSideCoord(true, sidex, px, this.videoWidth);
            this.windowLayoutParams.y = getSideCoord(false, sidey, py, this.videoHeight);
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.gravity = 51;
            if (this.isInAppOnly) {
                this.windowLayoutParams.type = 99;
            } else if (Build.VERSION.SDK_INT >= 26) {
                this.windowLayoutParams.type = 2038;
            } else {
                this.windowLayoutParams.type = 2003;
            }
            this.windowLayoutParams.flags = 16777992;
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            return textureView;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public void onVideoCompleted() {
        View view = this.controlsView;
        if (view instanceof MiniControlsView) {
            MiniControlsView miniControlsView = (MiniControlsView) view;
            boolean unused = miniControlsView.isCompleted = true;
            float unused2 = miniControlsView.progress = 0.0f;
            float unused3 = miniControlsView.bufferedPosition = 0.0f;
            miniControlsView.updatePlayButton();
            miniControlsView.invalidate();
            miniControlsView.show(true, true);
        }
    }

    public void setBufferedProgress(float progress) {
        View view = this.controlsView;
        if (view instanceof MiniControlsView) {
            ((MiniControlsView) view).setBufferedProgress(progress);
        }
    }

    public void updatePlayButton() {
        View view = this.controlsView;
        if (view instanceof MiniControlsView) {
            MiniControlsView miniControlsView = (MiniControlsView) view;
            miniControlsView.updatePlayButton();
            miniControlsView.invalidate();
        }
    }

    private static int getSideCoord(boolean isX, int side, float p, int sideSize) {
        int total;
        int result;
        if (isX) {
            total = AndroidUtilities.displaySize.x - sideSize;
        } else {
            total = (AndroidUtilities.displaySize.y - sideSize) - (ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight);
        }
        if (side == 0) {
            result = AndroidUtilities.dp(10.0f);
        } else if (side == 1) {
            result = total - AndroidUtilities.dp(10.0f);
        } else {
            result = AndroidUtilities.dp(10.0f) + Math.round(((float) (total - AndroidUtilities.dp(20.0f))) * p);
        }
        if (!isX) {
            return result + ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
        }
        return result;
    }

    public void close() {
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception e) {
        }
        this.parentSheet = null;
        this.photoViewer = null;
        this.parentActivity = null;
    }

    public void onConfigurationChanged() {
        int sidex = this.preferences.getInt("sidex", 1);
        int sidey = this.preferences.getInt("sidey", 0);
        float px = this.preferences.getFloat("px", 0.0f);
        float py = this.preferences.getFloat("py", 0.0f);
        this.windowLayoutParams.x = getSideCoord(true, sidex, px, this.videoWidth);
        this.windowLayoutParams.y = getSideCoord(false, sidey, py, this.videoHeight);
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    /* access modifiers changed from: private */
    public void animateToBoundsMaybe() {
        int startX = getSideCoord(true, 0, 0.0f, this.videoWidth);
        int endX = getSideCoord(true, 1, 0.0f, this.videoWidth);
        int startY = getSideCoord(false, 0, 0.0f, this.videoHeight);
        int endY = getSideCoord(false, 1, 0.0f, this.videoHeight);
        ArrayList<Animator> animators = null;
        SharedPreferences.Editor editor = this.preferences.edit();
        int maxDiff = AndroidUtilities.dp(20.0f);
        boolean slideOut = false;
        if (Math.abs(startX - this.windowLayoutParams.x) <= maxDiff || (this.windowLayoutParams.x < 0 && this.windowLayoutParams.x > (-this.videoWidth) / 4)) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            editor.putInt("sidex", 0);
            if (this.windowView.getAlpha() != 1.0f) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f}));
            }
            animators.add(ObjectAnimator.ofInt(this, "x", new int[]{startX}));
        } else if (Math.abs(endX - this.windowLayoutParams.x) <= maxDiff || (this.windowLayoutParams.x > AndroidUtilities.displaySize.x - this.videoWidth && this.windowLayoutParams.x < AndroidUtilities.displaySize.x - ((this.videoWidth / 4) * 3))) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            editor.putInt("sidex", 1);
            if (this.windowView.getAlpha() != 1.0f) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f}));
            }
            animators.add(ObjectAnimator.ofInt(this, "x", new int[]{endX}));
        } else if (this.windowView.getAlpha() != 1.0f) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            if (this.windowLayoutParams.x < 0) {
                animators.add(ObjectAnimator.ofInt(this, "x", new int[]{-this.videoWidth}));
            } else {
                animators.add(ObjectAnimator.ofInt(this, "x", new int[]{AndroidUtilities.displaySize.x}));
            }
            slideOut = true;
        } else {
            editor.putFloat("px", ((float) (this.windowLayoutParams.x - startX)) / ((float) (endX - startX)));
            editor.putInt("sidex", 2);
        }
        if (!slideOut) {
            if (Math.abs(startY - this.windowLayoutParams.y) <= maxDiff || this.windowLayoutParams.y <= ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) {
                if (animators == null) {
                    animators = new ArrayList<>();
                }
                editor.putInt("sidey", 0);
                animators.add(ObjectAnimator.ofInt(this, "y", new int[]{startY}));
            } else if (Math.abs(endY - this.windowLayoutParams.y) <= maxDiff) {
                if (animators == null) {
                    animators = new ArrayList<>();
                }
                editor.putInt("sidey", 1);
                animators.add(ObjectAnimator.ofInt(this, "y", new int[]{endY}));
            } else {
                editor.putFloat("py", ((float) (this.windowLayoutParams.y - startY)) / ((float) (endY - startY)));
                editor.putInt("sidey", 2);
            }
            editor.commit();
        }
        if (animators != null) {
            if (this.decelerateInterpolator == null) {
                this.decelerateInterpolator = new DecelerateInterpolator();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animatorSet = animatorSet2;
            animatorSet2.setInterpolator(this.decelerateInterpolator);
            this.animatorSet.setDuration(150);
            if (slideOut) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f}));
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet unused = PipVideoView.this.animatorSet = null;
                        if (PipVideoView.this.parentSheet != null) {
                            PipVideoView.this.parentSheet.destroy();
                        } else if (PipVideoView.this.photoViewer != null) {
                            PipVideoView.this.photoViewer.destroyPhotoViewer();
                        }
                    }
                });
            }
            this.animatorSet.playTogether(animators);
            this.animatorSet.start();
        }
    }

    public static Rect getPipRect(float aspectRatio) {
        int videoHeight2;
        int videoWidth2;
        SharedPreferences preferences2 = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        int sidex = preferences2.getInt("sidex", 1);
        int sidey = preferences2.getInt("sidey", 0);
        float px = preferences2.getFloat("px", 0.0f);
        float py = preferences2.getFloat("py", 0.0f);
        if (aspectRatio > 1.0f) {
            videoWidth2 = AndroidUtilities.dp(192.0f);
            videoHeight2 = (int) (((float) videoWidth2) / aspectRatio);
        } else {
            videoHeight2 = AndroidUtilities.dp(192.0f);
            videoWidth2 = (int) (((float) videoHeight2) * aspectRatio);
        }
        return new Rect((float) getSideCoord(true, sidex, px, videoWidth2), (float) getSideCoord(false, sidey, py, videoHeight2), (float) videoWidth2, (float) videoHeight2);
    }

    public int getX() {
        return this.windowLayoutParams.x;
    }

    public int getY() {
        return this.windowLayoutParams.y;
    }

    public void setX(int value) {
        this.windowLayoutParams.x = value;
        try {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        } catch (Exception e) {
        }
    }

    public void setY(int value) {
        this.windowLayoutParams.y = value;
        try {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        } catch (Exception e) {
        }
    }

    public int getWidth() {
        return this.windowLayoutParams.width;
    }

    public int getHeight() {
        return this.windowLayoutParams.height;
    }

    public void setWidth(int value) {
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        this.videoWidth = value;
        layoutParams.width = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    public void setHeight(int value) {
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        this.videoHeight = value;
        layoutParams.height = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }
}
