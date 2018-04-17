package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.support.annotation.Keep;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.PhotoViewer;

public class PipVideoView {
    private View controlsView;
    private DecelerateInterpolator decelerateInterpolator;
    private Activity parentActivity;
    private EmbedBottomSheet parentSheet;
    private PhotoViewer photoViewer;
    private SharedPreferences preferences;
    private int videoHeight;
    private int videoWidth;
    private LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private FrameLayout windowView;

    /* renamed from: org.telegram.ui.Components.PipVideoView$2 */
    class C12742 extends AnimatorListenerAdapter {
        C12742() {
        }

        public void onAnimationEnd(Animator animation) {
            if (PipVideoView.this.parentSheet != null) {
                PipVideoView.this.parentSheet.destroy();
            } else if (PipVideoView.this.photoViewer != null) {
                PipVideoView.this.photoViewer.destroyPhotoViewer();
            }
        }
    }

    private class MiniControlsView extends FrameLayout {
        private float bufferedPosition;
        private AnimatorSet currentAnimation;
        private Runnable hideRunnable = new C12751();
        private ImageView inlineButton;
        private boolean isCompleted;
        private boolean isVisible = true;
        private ImageView playButton;
        private float progress;
        private Paint progressInnerPaint;
        private Paint progressPaint;
        private Runnable progressRunnable = new C12762();

        /* renamed from: org.telegram.ui.Components.PipVideoView$MiniControlsView$1 */
        class C12751 implements Runnable {
            C12751() {
            }

            public void run() {
                MiniControlsView.this.show(false, true);
            }
        }

        /* renamed from: org.telegram.ui.Components.PipVideoView$MiniControlsView$2 */
        class C12762 implements Runnable {
            C12762() {
            }

            public void run() {
                if (PipVideoView.this.photoViewer != null) {
                    VideoPlayer videoPlayer = PipVideoView.this.photoViewer.getVideoPlayer();
                    if (videoPlayer != null) {
                        MiniControlsView.this.setProgress(((float) videoPlayer.getCurrentPosition()) / ((float) videoPlayer.getDuration()));
                        if (PipVideoView.this.photoViewer == null) {
                            MiniControlsView.this.setBufferedProgress(((float) videoPlayer.getBufferedPosition()) / ((float) videoPlayer.getDuration()));
                        }
                        AndroidUtilities.runOnUIThread(MiniControlsView.this.progressRunnable, 1000);
                    }
                }
            }
        }

        /* renamed from: org.telegram.ui.Components.PipVideoView$MiniControlsView$6 */
        class C12806 extends AnimatorListenerAdapter {
            C12806() {
            }

            public void onAnimationEnd(Animator animator) {
                MiniControlsView.this.currentAnimation = null;
            }
        }

        /* renamed from: org.telegram.ui.Components.PipVideoView$MiniControlsView$7 */
        class C12817 extends AnimatorListenerAdapter {
            C12817() {
            }

            public void onAnimationEnd(Animator animator) {
                MiniControlsView.this.currentAnimation = null;
            }
        }

        public MiniControlsView(Context context, boolean fullControls) {
            super(context);
            this.inlineButton = new ImageView(context);
            this.inlineButton.setScaleType(ScaleType.CENTER);
            this.inlineButton.setImageResource(R.drawable.ic_outinline);
            addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new OnClickListener(PipVideoView.this) {
                public void onClick(View v) {
                    if (PipVideoView.this.parentSheet != null) {
                        PipVideoView.this.parentSheet.exitFromPip();
                    } else if (PipVideoView.this.photoViewer != null) {
                        PipVideoView.this.photoViewer.exitFromPip();
                    }
                }
            });
            if (fullControls) {
                this.progressPaint = new Paint();
                this.progressPaint.setColor(-15095832);
                this.progressInnerPaint = new Paint();
                this.progressInnerPaint.setColor(-6975081);
                setWillNotDraw(false);
                this.playButton = new ImageView(context);
                this.playButton.setScaleType(ScaleType.CENTER);
                addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
                this.playButton.setOnClickListener(new OnClickListener(PipVideoView.this) {
                    public void onClick(View v) {
                        if (PipVideoView.this.photoViewer != null) {
                            VideoPlayer videoPlayer = PipVideoView.this.photoViewer.getVideoPlayer();
                            if (videoPlayer != null) {
                                if (videoPlayer.isPlaying()) {
                                    videoPlayer.pause();
                                } else {
                                    videoPlayer.play();
                                }
                                MiniControlsView.this.updatePlayButton();
                            }
                        }
                    }
                });
            }
            setOnTouchListener(new OnTouchListener(PipVideoView.this) {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            updatePlayButton();
            show(false, false);
        }

        private void updatePlayButton() {
            if (PipVideoView.this.photoViewer != null) {
                VideoPlayer videoPlayer = PipVideoView.this.photoViewer.getVideoPlayer();
                if (videoPlayer != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
                    if (videoPlayer.isPlaying()) {
                        this.playButton.setImageResource(R.drawable.ic_pauseinline);
                        AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
                    } else if (this.isCompleted) {
                        this.playButton.setImageResource(R.drawable.ic_againinline);
                    } else {
                        this.playButton.setImageResource(R.drawable.ic_playinline);
                    }
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
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                }
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (this.isVisible) {
                    if (animated) {
                        this.currentAnimation = new AnimatorSet();
                        animatorSet = this.currentAnimation;
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0f});
                        animatorSet.playTogether(animatorArr);
                        this.currentAnimation.setDuration(150);
                        this.currentAnimation.addListener(new C12806());
                        this.currentAnimation.start();
                    } else {
                        setAlpha(1.0f);
                    }
                } else if (animated) {
                    this.currentAnimation = new AnimatorSet();
                    animatorSet = this.currentAnimation;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    this.currentAnimation.setDuration(150);
                    this.currentAnimation.addListener(new C12817());
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
                if (this.isVisible) {
                    checkNeedHide();
                } else {
                    show(true, true);
                    return true;
                }
            }
            return super.onInterceptTouchEvent(ev);
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
            checkNeedHide();
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            checkNeedHide();
        }

        protected void onDraw(Canvas canvas) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int progressLineY = height - AndroidUtilities.dp(3.0f);
            int cy = height - AndroidUtilities.dp(7.0f);
            int progressX = ((int) (((float) (width - 0)) * this.progress)) + 0;
            if (this.bufferedPosition != 0.0f) {
                canvas.drawRect((float) null, (float) progressLineY, ((float) null) + (((float) (width - 0)) * r0.bufferedPosition), (float) (AndroidUtilities.dp(3.0f) + progressLineY), r0.progressInnerPaint);
            }
            canvas.drawRect((float) null, (float) progressLineY, (float) progressX, (float) (AndroidUtilities.dp(3.0f) + progressLineY), r0.progressPaint);
        }
    }

    public TextureView show(Activity activity, EmbedBottomSheet sheet, View controls, float aspectRatio, int rotation, WebView webview) {
        return show(activity, null, sheet, controls, aspectRatio, rotation, webview);
    }

    public TextureView show(Activity activity, PhotoViewer viewer, float aspectRatio, int rotation) {
        return show(activity, viewer, null, null, aspectRatio, rotation, null);
    }

    public TextureView show(Activity activity, PhotoViewer viewer, EmbedBottomSheet sheet, View controls, float aspectRatio, int rotation, WebView webview) {
        TextureView textureView;
        Context context = activity;
        PhotoViewer photoViewer = viewer;
        View view = controls;
        float f = aspectRatio;
        View view2 = webview;
        this.parentSheet = sheet;
        this.parentActivity = context;
        this.photoViewer = photoViewer;
        this.windowView = new FrameLayout(context) {
            private boolean dragging;
            private float startX;
            private float startY;

            public boolean onInterceptTouchEvent(MotionEvent event) {
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
                return super.onInterceptTouchEvent(event);
            }

            public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                super.requestDisallowInterceptTouchEvent(disallowIntercept);
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (!this.dragging) {
                    return false;
                }
                float x = event.getRawX();
                float y = event.getRawY();
                if (event.getAction() == 2) {
                    float dx = x - this.startX;
                    float dy = y - this.startY;
                    LayoutParams access$600 = PipVideoView.this.windowLayoutParams;
                    access$600.x = (int) (((float) access$600.x) + dx);
                    access$600 = PipVideoView.this.windowLayoutParams;
                    access$600.y = (int) (((float) access$600.y) + dy);
                    int maxDiff = PipVideoView.this.videoWidth / 2;
                    if (PipVideoView.this.windowLayoutParams.x < (-maxDiff)) {
                        PipVideoView.this.windowLayoutParams.x = -maxDiff;
                    } else if (PipVideoView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + maxDiff) {
                        PipVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + maxDiff;
                    }
                    float alpha = 1.0f;
                    if (PipVideoView.this.windowLayoutParams.x < 0) {
                        alpha = 1.0f + ((((float) PipVideoView.this.windowLayoutParams.x) / ((float) maxDiff)) * 0.5f);
                    } else if (PipVideoView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) {
                        alpha = 1.0f - ((((float) ((PipVideoView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + PipVideoView.this.windowLayoutParams.width)) / ((float) maxDiff)) * 0.5f);
                    }
                    if (PipVideoView.this.windowView.getAlpha() != alpha) {
                        PipVideoView.this.windowView.setAlpha(alpha);
                    }
                    if (PipVideoView.this.windowLayoutParams.y < (-null)) {
                        PipVideoView.this.windowLayoutParams.y = -null;
                    } else if (PipVideoView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + 0) {
                        PipVideoView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + 0;
                    }
                    PipVideoView.this.windowManager.updateViewLayout(PipVideoView.this.windowView, PipVideoView.this.windowLayoutParams);
                    this.startX = x;
                    this.startY = y;
                } else if (event.getAction() == 1) {
                    this.dragging = false;
                    PipVideoView.this.animateToBoundsMaybe();
                }
                return true;
            }
        };
        if (f > 1.0f) {
            r1.videoWidth = AndroidUtilities.dp(192.0f);
            r1.videoHeight = (int) (((float) r1.videoWidth) / f);
        } else {
            r1.videoHeight = AndroidUtilities.dp(192.0f);
            r1.videoWidth = (int) (((float) r1.videoHeight) * f);
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(context);
        aspectRatioFrameLayout.setAspectRatio(f, rotation);
        r1.windowView.addView(aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        if (view2 != null) {
            ViewGroup parent = (ViewGroup) webview.getParent();
            if (parent != null) {
                parent.removeView(view2);
            }
            aspectRatioFrameLayout.addView(view2, LayoutHelper.createFrame(-1, -1.0f));
            textureView = null;
        } else {
            textureView = new TextureView(context);
            aspectRatioFrameLayout.addView(textureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (view == null) {
            r1.controlsView = new MiniControlsView(context, photoViewer != null);
        } else {
            r1.controlsView = view;
        }
        r1.windowView.addView(r1.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        r1.windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        r1.preferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        int sidex = r1.preferences.getInt("sidex", 1);
        int sidey = r1.preferences.getInt("sidey", 0);
        float px = r1.preferences.getFloat("px", 0.0f);
        float py = r1.preferences.getFloat("py", 0.0f);
        try {
            r1.windowLayoutParams = new LayoutParams();
            r1.windowLayoutParams.width = r1.videoWidth;
            r1.windowLayoutParams.height = r1.videoHeight;
            r1.windowLayoutParams.x = getSideCoord(true, sidex, px, r1.videoWidth);
            r1.windowLayoutParams.y = getSideCoord(false, sidey, py, r1.videoHeight);
            r1.windowLayoutParams.format = -3;
            r1.windowLayoutParams.gravity = 51;
            if (VERSION.SDK_INT >= 26) {
                r1.windowLayoutParams.type = 2038;
            } else {
                r1.windowLayoutParams.type = 2003;
            }
            r1.windowLayoutParams.flags = 16777736;
            r1.windowManager.addView(r1.windowView, r1.windowLayoutParams);
            return textureView;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return null;
        }
    }

    public void onVideoCompleted() {
        if (this.controlsView instanceof MiniControlsView) {
            MiniControlsView miniControlsView = this.controlsView;
            miniControlsView.isCompleted = true;
            miniControlsView.progress = 0.0f;
            miniControlsView.bufferedPosition = 0.0f;
            miniControlsView.updatePlayButton();
            miniControlsView.invalidate();
            miniControlsView.show(true, true);
        }
    }

    public void setBufferedProgress(float progress) {
        if (this.controlsView instanceof MiniControlsView) {
            ((MiniControlsView) this.controlsView).setBufferedProgress(progress);
        }
    }

    public void updatePlayButton() {
        if (this.controlsView instanceof MiniControlsView) {
            MiniControlsView miniControlsView = this.controlsView;
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
            total = (AndroidUtilities.displaySize.y - sideSize) - ActionBar.getCurrentActionBarHeight();
        }
        if (side == 0) {
            result = AndroidUtilities.dp(10.0f);
        } else if (side == 1) {
            result = total - AndroidUtilities.dp(10.0f);
        } else {
            result = AndroidUtilities.dp(10.0f) + Math.round(((float) (total - AndroidUtilities.dp(20.0f))) * p);
            if (isX) {
                return result + ActionBar.getCurrentActionBarHeight();
            }
            return result;
        }
        if (isX) {
            return result;
        }
        return result + ActionBar.getCurrentActionBarHeight();
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

    private void animateToBoundsMaybe() {
        AnimatorSet animatorSet;
        int startX = getSideCoord(true, 0, 0.0f, this.videoWidth);
        int endX = getSideCoord(true, 1, 0.0f, this.videoWidth);
        int startY = getSideCoord(false, 0, 0.0f, this.videoHeight);
        int endY = getSideCoord(false, 1, 0.0f, this.videoHeight);
        ArrayList<Animator> animators = null;
        Editor editor = this.preferences.edit();
        int maxDiff = AndroidUtilities.dp(NUM);
        boolean slideOut = false;
        if (Math.abs(startX - this.windowLayoutParams.x) > maxDiff) {
            if (r0.windowLayoutParams.x >= 0 || r0.windowLayoutParams.x <= (-r0.videoWidth) / 4) {
                if (Math.abs(endX - r0.windowLayoutParams.x) > maxDiff) {
                    if (r0.windowLayoutParams.x <= AndroidUtilities.displaySize.x - r0.videoWidth || r0.windowLayoutParams.x >= AndroidUtilities.displaySize.x - ((r0.videoWidth / 4) * 3)) {
                        if (r0.windowView.getAlpha() != 1.0f) {
                            if (null == null) {
                                animators = new ArrayList();
                            }
                            if (r0.windowLayoutParams.x < 0) {
                                animators.add(ObjectAnimator.ofInt(r0, "x", new int[]{-r0.videoWidth}));
                            } else {
                                animators.add(ObjectAnimator.ofInt(r0, "x", new int[]{AndroidUtilities.displaySize.x}));
                            }
                            slideOut = true;
                        } else {
                            editor.putFloat("px", ((float) (r0.windowLayoutParams.x - startX)) / ((float) (endX - startX)));
                            editor.putInt("sidex", 2);
                        }
                        if (!slideOut) {
                            if (Math.abs(startY - r0.windowLayoutParams.y) > maxDiff) {
                                if (r0.windowLayoutParams.y <= ActionBar.getCurrentActionBarHeight()) {
                                    if (Math.abs(endY - r0.windowLayoutParams.y) <= maxDiff) {
                                        if (animators == null) {
                                            animators = new ArrayList();
                                        }
                                        editor.putInt("sidey", 1);
                                        animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{endY}));
                                    } else {
                                        editor.putFloat("py", ((float) (r0.windowLayoutParams.y - startY)) / ((float) (endY - startY)));
                                        editor.putInt("sidey", 2);
                                    }
                                    editor.commit();
                                }
                            }
                            if (animators == null) {
                                animators = new ArrayList();
                            }
                            editor.putInt("sidey", 0);
                            animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{startY}));
                            editor.commit();
                        }
                        if (animators == null) {
                            if (r0.decelerateInterpolator == null) {
                                r0.decelerateInterpolator = new DecelerateInterpolator();
                            }
                            animatorSet = new AnimatorSet();
                            animatorSet.setInterpolator(r0.decelerateInterpolator);
                            animatorSet.setDuration(150);
                            if (slideOut) {
                                animators.add(ObjectAnimator.ofFloat(r0.windowView, "alpha", new float[]{0.0f}));
                                animatorSet.addListener(new C12742());
                            }
                            animatorSet.playTogether(animators);
                            animatorSet.start();
                        }
                    }
                }
                if (null == null) {
                    animators = new ArrayList();
                }
                editor.putInt("sidex", 1);
                if (r0.windowView.getAlpha() != 1.0f) {
                    animators.add(ObjectAnimator.ofFloat(r0.windowView, "alpha", new float[]{1.0f}));
                }
                animators.add(ObjectAnimator.ofInt(r0, "x", new int[]{endX}));
                if (slideOut) {
                    if (Math.abs(startY - r0.windowLayoutParams.y) > maxDiff) {
                        if (r0.windowLayoutParams.y <= ActionBar.getCurrentActionBarHeight()) {
                            if (Math.abs(endY - r0.windowLayoutParams.y) <= maxDiff) {
                                editor.putFloat("py", ((float) (r0.windowLayoutParams.y - startY)) / ((float) (endY - startY)));
                                editor.putInt("sidey", 2);
                            } else {
                                if (animators == null) {
                                    animators = new ArrayList();
                                }
                                editor.putInt("sidey", 1);
                                animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{endY}));
                            }
                            editor.commit();
                        }
                    }
                    if (animators == null) {
                        animators = new ArrayList();
                    }
                    editor.putInt("sidey", 0);
                    animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{startY}));
                    editor.commit();
                }
                if (animators == null) {
                    if (r0.decelerateInterpolator == null) {
                        r0.decelerateInterpolator = new DecelerateInterpolator();
                    }
                    animatorSet = new AnimatorSet();
                    animatorSet.setInterpolator(r0.decelerateInterpolator);
                    animatorSet.setDuration(150);
                    if (slideOut) {
                        animators.add(ObjectAnimator.ofFloat(r0.windowView, "alpha", new float[]{0.0f}));
                        animatorSet.addListener(new C12742());
                    }
                    animatorSet.playTogether(animators);
                    animatorSet.start();
                }
            }
        }
        if (null == null) {
            animators = new ArrayList();
        }
        editor.putInt("sidex", 0);
        if (r0.windowView.getAlpha() != 1.0f) {
            animators.add(ObjectAnimator.ofFloat(r0.windowView, "alpha", new float[]{1.0f}));
        }
        animators.add(ObjectAnimator.ofInt(r0, "x", new int[]{startX}));
        if (slideOut) {
            if (Math.abs(startY - r0.windowLayoutParams.y) > maxDiff) {
                if (r0.windowLayoutParams.y <= ActionBar.getCurrentActionBarHeight()) {
                    if (Math.abs(endY - r0.windowLayoutParams.y) <= maxDiff) {
                        if (animators == null) {
                            animators = new ArrayList();
                        }
                        editor.putInt("sidey", 1);
                        animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{endY}));
                    } else {
                        editor.putFloat("py", ((float) (r0.windowLayoutParams.y - startY)) / ((float) (endY - startY)));
                        editor.putInt("sidey", 2);
                    }
                    editor.commit();
                }
            }
            if (animators == null) {
                animators = new ArrayList();
            }
            editor.putInt("sidey", 0);
            animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{startY}));
            editor.commit();
        }
        if (animators == null) {
            if (r0.decelerateInterpolator == null) {
                r0.decelerateInterpolator = new DecelerateInterpolator();
            }
            animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(r0.decelerateInterpolator);
            animatorSet.setDuration(150);
            if (slideOut) {
                animators.add(ObjectAnimator.ofFloat(r0.windowView, "alpha", new float[]{0.0f}));
                animatorSet.addListener(new C12742());
            }
            animatorSet.playTogether(animators);
            animatorSet.start();
        }
    }

    public static Rect getPipRect(float aspectRatio) {
        int videoWidth;
        int videoHeight;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        int sidex = preferences.getInt("sidex", 1);
        int sidey = preferences.getInt("sidey", 0);
        float px = preferences.getFloat("px", 0.0f);
        float py = preferences.getFloat("py", 0.0f);
        if (aspectRatio > 1.0f) {
            videoWidth = AndroidUtilities.dp(192.0f);
            videoHeight = (int) (((float) videoWidth) / aspectRatio);
        } else {
            videoHeight = AndroidUtilities.dp(192.0f);
            videoWidth = (int) (((float) videoHeight) * aspectRatio);
        }
        return new Rect((float) getSideCoord(true, sidex, px, videoWidth), (float) getSideCoord(false, sidey, py, videoHeight), (float) videoWidth, (float) videoHeight);
    }

    @Keep
    public int getX() {
        return this.windowLayoutParams.x;
    }

    @Keep
    public int getY() {
        return this.windowLayoutParams.y;
    }

    @Keep
    public void setX(int value) {
        this.windowLayoutParams.x = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    @Keep
    public void setY(int value) {
        this.windowLayoutParams.y = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }
}
