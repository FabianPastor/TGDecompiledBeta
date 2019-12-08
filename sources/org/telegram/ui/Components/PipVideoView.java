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
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import androidx.annotation.Keep;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
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

    private class MiniControlsView extends FrameLayout {
        private float bufferedPosition;
        private AnimatorSet currentAnimation;
        private Runnable hideRunnable = new -$$Lambda$PipVideoView$MiniControlsView$1lMZ0uhQOCqoH6v0Akw4gfbgK2g(this);
        private ImageView inlineButton;
        private boolean isCompleted;
        private boolean isVisible = true;
        private ImageView playButton;
        private float progress;
        private Paint progressInnerPaint;
        private Paint progressPaint;
        private Runnable progressRunnable = new Runnable() {
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
        };

        public /* synthetic */ void lambda$new$0$PipVideoView$MiniControlsView() {
            show(false, true);
        }

        public MiniControlsView(Context context, boolean z) {
            super(context);
            this.inlineButton = new ImageView(context);
            this.inlineButton.setScaleType(ScaleType.CENTER);
            this.inlineButton.setImageResource(NUM);
            addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new -$$Lambda$PipVideoView$MiniControlsView$wbm8DJnUhe315ylEORpdCN9TCPU(this));
            if (z) {
                this.progressPaint = new Paint();
                this.progressPaint.setColor(-15095832);
                this.progressInnerPaint = new Paint();
                this.progressInnerPaint.setColor(-6975081);
                setWillNotDraw(false);
                this.playButton = new ImageView(context);
                this.playButton.setScaleType(ScaleType.CENTER);
                addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
                this.playButton.setOnClickListener(new -$$Lambda$PipVideoView$MiniControlsView$1JTbmkeJbI8PQhj0gt64f8TrJNY(this));
            }
            setOnTouchListener(-$$Lambda$PipVideoView$MiniControlsView$jKPELGvyg4VHgdM0XCb-ZaGLO4U.INSTANCE);
            updatePlayButton();
            show(false, false);
        }

        public /* synthetic */ void lambda$new$1$PipVideoView$MiniControlsView(View view) {
            if (PipVideoView.this.parentSheet != null) {
                PipVideoView.this.parentSheet.exitFromPip();
            } else if (PipVideoView.this.photoViewer != null) {
                PipVideoView.this.photoViewer.exitFromPip();
            }
        }

        public /* synthetic */ void lambda$new$2$PipVideoView$MiniControlsView(View view) {
            if (PipVideoView.this.photoViewer != null) {
                VideoPlayer videoPlayer = PipVideoView.this.photoViewer.getVideoPlayer();
                if (videoPlayer != null) {
                    if (videoPlayer.isPlaying()) {
                        videoPlayer.pause();
                    } else {
                        videoPlayer.play();
                    }
                    updatePlayButton();
                }
            }
        }

        private void updatePlayButton() {
            if (PipVideoView.this.photoViewer != null) {
                VideoPlayer videoPlayer = PipVideoView.this.photoViewer.getVideoPlayer();
                if (videoPlayer != null) {
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
        }

        public void setBufferedProgress(float f) {
            this.bufferedPosition = f;
            invalidate();
        }

        public void setProgress(float f) {
            this.progress = f;
            invalidate();
        }

        public void show(boolean z, boolean z2) {
            if (this.isVisible != z) {
                this.isVisible = z;
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                String str = "alpha";
                AnimatorSet animatorSet2;
                Animator[] animatorArr;
                if (this.isVisible) {
                    if (z2) {
                        this.currentAnimation = new AnimatorSet();
                        animatorSet2 = this.currentAnimation;
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this, str, new float[]{1.0f});
                        animatorSet2.playTogether(animatorArr);
                        this.currentAnimation.setDuration(150);
                        this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                MiniControlsView.this.currentAnimation = null;
                            }
                        });
                        this.currentAnimation.start();
                    } else {
                        setAlpha(1.0f);
                    }
                } else if (z2) {
                    this.currentAnimation = new AnimatorSet();
                    animatorSet2 = this.currentAnimation;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, str, new float[]{0.0f});
                    animatorSet2.playTogether(animatorArr);
                    this.currentAnimation.setDuration(150);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            MiniControlsView.this.currentAnimation = null;
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

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                if (this.isVisible) {
                    checkNeedHide();
                } else {
                    show(true, true);
                    return true;
                }
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            super.requestDisallowInterceptTouchEvent(z);
            checkNeedHide();
        }

        /* Access modifiers changed, original: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            checkNeedHide();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
            AndroidUtilities.dp(7.0f);
            float f = (float) (measuredWidth - 0);
            int i = ((int) (this.progress * f)) + 0;
            float f2 = this.bufferedPosition;
            if (f2 != 0.0f) {
                float f3 = (float) null;
                canvas.drawRect(f3, (float) measuredHeight, f3 + (f * f2), (float) (AndroidUtilities.dp(3.0f) + measuredHeight), this.progressInnerPaint);
            }
            canvas.drawRect((float) null, (float) measuredHeight, (float) i, (float) (measuredHeight + AndroidUtilities.dp(3.0f)), this.progressPaint);
        }
    }

    public TextureView show(Activity activity, EmbedBottomSheet embedBottomSheet, View view, float f, int i, WebView webView) {
        return show(activity, null, embedBottomSheet, view, f, i, webView);
    }

    public TextureView show(Activity activity, PhotoViewer photoViewer, float f, int i) {
        return show(activity, photoViewer, null, null, f, i, null);
    }

    public TextureView show(Activity activity, PhotoViewer photoViewer, EmbedBottomSheet embedBottomSheet, View view, float f, int i, WebView webView) {
        TextureView textureView;
        this.parentSheet = embedBottomSheet;
        this.parentActivity = activity;
        this.photoViewer = photoViewer;
        this.windowView = new FrameLayout(activity) {
            private boolean dragging;
            private float startX;
            private float startY;

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                if (motionEvent.getAction() == 0) {
                    this.startX = rawX;
                    this.startY = rawY;
                } else if (motionEvent.getAction() == 2 && !this.dragging && (Math.abs(this.startX - rawX) >= AndroidUtilities.getPixelsInCM(0.3f, true) || Math.abs(this.startY - rawY) >= AndroidUtilities.getPixelsInCM(0.3f, false))) {
                    this.dragging = true;
                    this.startX = rawX;
                    this.startY = rawY;
                    if (PipVideoView.this.controlsView != null) {
                        ((ViewParent) PipVideoView.this.controlsView).requestDisallowInterceptTouchEvent(true);
                    }
                    return true;
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            public void requestDisallowInterceptTouchEvent(boolean z) {
                super.requestDisallowInterceptTouchEvent(z);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (!this.dragging) {
                    return false;
                }
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                if (motionEvent.getAction() == 2) {
                    float f = rawX - this.startX;
                    float f2 = rawY - this.startY;
                    LayoutParams access$500 = PipVideoView.this.windowLayoutParams;
                    access$500.x = (int) (((float) access$500.x) + f);
                    LayoutParams access$5002 = PipVideoView.this.windowLayoutParams;
                    access$5002.y = (int) (((float) access$5002.y) + f2);
                    int access$600 = PipVideoView.this.videoWidth / 2;
                    int i = -access$600;
                    if (PipVideoView.this.windowLayoutParams.x < i) {
                        PipVideoView.this.windowLayoutParams.x = i;
                    } else if (PipVideoView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + access$600) {
                        PipVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + access$600;
                    }
                    float f3 = 1.0f;
                    if (PipVideoView.this.windowLayoutParams.x < 0) {
                        f3 = 1.0f + ((((float) PipVideoView.this.windowLayoutParams.x) / ((float) access$600)) * 0.5f);
                    } else if (PipVideoView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) {
                        f3 = 1.0f - ((((float) ((PipVideoView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + PipVideoView.this.windowLayoutParams.width)) / ((float) access$600)) * 0.5f);
                    }
                    if (PipVideoView.this.windowView.getAlpha() != f3) {
                        PipVideoView.this.windowView.setAlpha(f3);
                    }
                    if (PipVideoView.this.windowLayoutParams.y < 0) {
                        PipVideoView.this.windowLayoutParams.y = 0;
                    } else if (PipVideoView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + 0) {
                        PipVideoView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + 0;
                    }
                    PipVideoView.this.windowManager.updateViewLayout(PipVideoView.this.windowView, PipVideoView.this.windowLayoutParams);
                    this.startX = rawX;
                    this.startY = rawY;
                } else if (motionEvent.getAction() == 1) {
                    this.dragging = false;
                    PipVideoView.this.animateToBoundsMaybe();
                }
                return true;
            }
        };
        if (f > 1.0f) {
            this.videoWidth = AndroidUtilities.dp(192.0f);
            this.videoHeight = (int) (((float) this.videoWidth) / f);
        } else {
            this.videoHeight = AndroidUtilities.dp(192.0f);
            this.videoWidth = (int) (((float) this.videoHeight) * f);
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(activity);
        aspectRatioFrameLayout.setAspectRatio(f, i);
        this.windowView.addView(aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        if (webView != null) {
            ViewGroup viewGroup = (ViewGroup) webView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(webView);
            }
            aspectRatioFrameLayout.addView(webView, LayoutHelper.createFrame(-1, -1.0f));
            textureView = null;
        } else {
            textureView = new TextureView(activity);
            aspectRatioFrameLayout.addView(textureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (view == null) {
            this.controlsView = new MiniControlsView(activity, photoViewer != null);
        } else {
            this.controlsView = view;
        }
        this.windowView.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        this.windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        int i2 = this.preferences.getInt("sidex", 1);
        int i3 = this.preferences.getInt("sidey", 0);
        float f2 = this.preferences.getFloat("px", 0.0f);
        float f3 = this.preferences.getFloat("py", 0.0f);
        try {
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.width = this.videoWidth;
            this.windowLayoutParams.height = this.videoHeight;
            this.windowLayoutParams.x = getSideCoord(true, i2, f2, this.videoWidth);
            this.windowLayoutParams.y = getSideCoord(false, i3, f3, this.videoHeight);
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.gravity = 51;
            if (VERSION.SDK_INT >= 26) {
                this.windowLayoutParams.type = 2038;
            } else {
                this.windowLayoutParams.type = 2003;
            }
            this.windowLayoutParams.flags = 16777736;
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            return textureView;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public void onVideoCompleted() {
        View view = this.controlsView;
        if (view instanceof MiniControlsView) {
            MiniControlsView miniControlsView = (MiniControlsView) view;
            miniControlsView.isCompleted = true;
            miniControlsView.progress = 0.0f;
            miniControlsView.bufferedPosition = 0.0f;
            miniControlsView.updatePlayButton();
            miniControlsView.invalidate();
            miniControlsView.show(true, true);
        }
    }

    public void setBufferedProgress(float f) {
        View view = this.controlsView;
        if (view instanceof MiniControlsView) {
            ((MiniControlsView) view).setBufferedProgress(f);
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

    private static int getSideCoord(boolean z, int i, float f, int i2) {
        int i3;
        if (z) {
            i3 = AndroidUtilities.displaySize.x;
        } else {
            i3 = AndroidUtilities.displaySize.y - i2;
            i2 = ActionBar.getCurrentActionBarHeight();
        }
        i3 -= i2;
        if (i == 0) {
            i = AndroidUtilities.dp(10.0f);
        } else if (i == 1) {
            i = i3 - AndroidUtilities.dp(10.0f);
        } else {
            i = Math.round(((float) (i3 - AndroidUtilities.dp(20.0f))) * f) + AndroidUtilities.dp(10.0f);
        }
        return !z ? i + ActionBar.getCurrentActionBarHeight() : i;
    }

    public void close() {
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception unused) {
        }
        this.parentSheet = null;
        this.photoViewer = null;
        this.parentActivity = null;
    }

    public void onConfigurationChanged() {
        int i = this.preferences.getInt("sidex", 1);
        int i2 = this.preferences.getInt("sidey", 0);
        float f = this.preferences.getFloat("px", 0.0f);
        float f2 = this.preferences.getFloat("py", 0.0f);
        this.windowLayoutParams.x = getSideCoord(true, i, f, this.videoWidth);
        this.windowLayoutParams.y = getSideCoord(false, i2, f2, this.videoHeight);
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0182  */
    private void animateToBoundsMaybe() {
        /*
        r16 = this;
        r0 = r16;
        r1 = r0.videoWidth;
        r2 = 0;
        r3 = 0;
        r4 = 1;
        r1 = getSideCoord(r4, r3, r2, r1);
        r5 = r0.videoWidth;
        r5 = getSideCoord(r4, r4, r2, r5);
        r6 = r0.videoHeight;
        r6 = getSideCoord(r3, r3, r2, r6);
        r7 = r0.videoHeight;
        r7 = getSideCoord(r3, r4, r2, r7);
        r8 = r0.preferences;
        r8 = r8.edit();
        r9 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = r0.windowLayoutParams;
        r10 = r10.x;
        r10 = r1 - r10;
        r10 = java.lang.Math.abs(r10);
        r12 = "alpha";
        r13 = "sidex";
        r14 = "x";
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r10 <= r9) goto L_0x00e7;
    L_0x003d:
        r10 = r0.windowLayoutParams;
        r10 = r10.x;
        if (r10 >= 0) goto L_0x004c;
    L_0x0043:
        r2 = r0.videoWidth;
        r2 = -r2;
        r2 = r2 / 4;
        if (r10 <= r2) goto L_0x004c;
    L_0x004a:
        goto L_0x00e7;
    L_0x004c:
        r2 = r0.windowLayoutParams;
        r2 = r2.x;
        r2 = r5 - r2;
        r2 = java.lang.Math.abs(r2);
        if (r2 <= r9) goto L_0x00b9;
    L_0x0058:
        r2 = r0.windowLayoutParams;
        r2 = r2.x;
        r10 = org.telegram.messenger.AndroidUtilities.displaySize;
        r10 = r10.x;
        r11 = r0.videoWidth;
        r3 = r10 - r11;
        if (r2 <= r3) goto L_0x006e;
    L_0x0066:
        r11 = r11 / 4;
        r11 = r11 * 3;
        r10 = r10 - r11;
        if (r2 >= r10) goto L_0x006e;
    L_0x006d:
        goto L_0x00b9;
    L_0x006e:
        r2 = r0.windowView;
        r2 = r2.getAlpha();
        r2 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1));
        if (r2 == 0) goto L_0x00a5;
    L_0x0078:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = r0.windowLayoutParams;
        r2 = r2.x;
        if (r2 >= 0) goto L_0x0093;
    L_0x0083:
        r2 = new int[r4];
        r3 = r0.videoWidth;
        r3 = -r3;
        r5 = 0;
        r2[r5] = r3;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r14, r2);
        r1.add(r2);
        goto L_0x00a3;
    L_0x0093:
        r5 = 0;
        r2 = new int[r4];
        r3 = org.telegram.messenger.AndroidUtilities.displaySize;
        r3 = r3.x;
        r2[r5] = r3;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r14, r2);
        r1.add(r2);
    L_0x00a3:
        r10 = 1;
        goto L_0x0114;
    L_0x00a5:
        r2 = r0.windowLayoutParams;
        r2 = r2.x;
        r2 = r2 - r1;
        r2 = (float) r2;
        r5 = r5 - r1;
        r1 = (float) r5;
        r2 = r2 / r1;
        r1 = "px";
        r8.putFloat(r1, r2);
        r1 = 2;
        r8.putInt(r13, r1);
        r1 = 0;
        goto L_0x0113;
    L_0x00b9:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r8.putInt(r13, r4);
        r2 = r0.windowView;
        r2 = r2.getAlpha();
        r2 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1));
        if (r2 == 0) goto L_0x00da;
    L_0x00cb:
        r2 = r0.windowView;
        r3 = new float[r4];
        r10 = 0;
        r3[r10] = r15;
        r2 = android.animation.ObjectAnimator.ofFloat(r2, r12, r3);
        r1.add(r2);
        goto L_0x00db;
    L_0x00da:
        r10 = 0;
    L_0x00db:
        r2 = new int[r4];
        r2[r10] = r5;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r14, r2);
        r1.add(r2);
        goto L_0x0114;
    L_0x00e7:
        r10 = 0;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r8.putInt(r13, r10);
        r3 = r0.windowView;
        r3 = r3.getAlpha();
        r3 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1));
        if (r3 == 0) goto L_0x0107;
    L_0x00fa:
        r3 = r0.windowView;
        r5 = new float[r4];
        r5[r10] = r15;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r12, r5);
        r2.add(r3);
    L_0x0107:
        r3 = new int[r4];
        r3[r10] = r1;
        r1 = android.animation.ObjectAnimator.ofInt(r0, r14, r3);
        r2.add(r1);
        r1 = r2;
    L_0x0113:
        r10 = 0;
    L_0x0114:
        if (r10 != 0) goto L_0x0180;
    L_0x0116:
        r2 = r0.windowLayoutParams;
        r2 = r2.y;
        r2 = r6 - r2;
        r2 = java.lang.Math.abs(r2);
        r3 = "y";
        r5 = "sidey";
        if (r2 <= r9) goto L_0x0167;
    L_0x0126:
        r2 = r0.windowLayoutParams;
        r2 = r2.y;
        r11 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
        if (r2 > r11) goto L_0x0131;
    L_0x0130:
        goto L_0x0167;
    L_0x0131:
        r2 = r0.windowLayoutParams;
        r2 = r2.y;
        r2 = r7 - r2;
        r2 = java.lang.Math.abs(r2);
        if (r2 > r9) goto L_0x0154;
    L_0x013d:
        if (r1 != 0) goto L_0x0144;
    L_0x013f:
        r1 = new java.util.ArrayList;
        r1.<init>();
    L_0x0144:
        r8.putInt(r5, r4);
        r2 = new int[r4];
        r5 = 0;
        r2[r5] = r7;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r3, r2);
        r1.add(r2);
        goto L_0x017d;
    L_0x0154:
        r2 = r0.windowLayoutParams;
        r2 = r2.y;
        r2 = r2 - r6;
        r2 = (float) r2;
        r7 = r7 - r6;
        r3 = (float) r7;
        r2 = r2 / r3;
        r3 = "py";
        r8.putFloat(r3, r2);
        r2 = 2;
        r8.putInt(r5, r2);
        goto L_0x017d;
    L_0x0167:
        if (r1 != 0) goto L_0x016e;
    L_0x0169:
        r1 = new java.util.ArrayList;
        r1.<init>();
    L_0x016e:
        r2 = 0;
        r8.putInt(r5, r2);
        r5 = new int[r4];
        r5[r2] = r6;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r3, r5);
        r1.add(r2);
    L_0x017d:
        r8.commit();
    L_0x0180:
        if (r1 == 0) goto L_0x01bb;
    L_0x0182:
        r2 = r0.decelerateInterpolator;
        if (r2 != 0) goto L_0x018d;
    L_0x0186:
        r2 = new android.view.animation.DecelerateInterpolator;
        r2.<init>();
        r0.decelerateInterpolator = r2;
    L_0x018d:
        r2 = new android.animation.AnimatorSet;
        r2.<init>();
        r3 = r0.decelerateInterpolator;
        r2.setInterpolator(r3);
        r5 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r2.setDuration(r5);
        if (r10 == 0) goto L_0x01b5;
    L_0x019e:
        r3 = r0.windowView;
        r4 = new float[r4];
        r5 = 0;
        r6 = 0;
        r4[r6] = r5;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r12, r4);
        r1.add(r3);
        r3 = new org.telegram.ui.Components.PipVideoView$2;
        r3.<init>();
        r2.addListener(r3);
    L_0x01b5:
        r2.playTogether(r1);
        r2.start();
    L_0x01bb:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipVideoView.animateToBoundsMaybe():void");
    }

    public static Rect getPipRect(float f) {
        int dp;
        int i;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        int i2 = sharedPreferences.getInt("sidex", 1);
        int i3 = sharedPreferences.getInt("sidey", 0);
        float f2 = sharedPreferences.getFloat("px", 0.0f);
        float f3 = sharedPreferences.getFloat("py", 0.0f);
        if (f > 1.0f) {
            dp = AndroidUtilities.dp(192.0f);
            int i4 = dp;
            dp = (int) (((float) dp) / f);
            i = i4;
        } else {
            dp = AndroidUtilities.dp(192.0f);
            i = (int) (((float) dp) * f);
        }
        return new Rect((float) getSideCoord(true, i2, f2, i), (float) getSideCoord(false, i3, f3, dp), (float) i, (float) dp);
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
    public void setX(int i) {
        LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.x = i;
        this.windowManager.updateViewLayout(this.windowView, layoutParams);
    }

    @Keep
    public void setY(int i) {
        LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.y = i;
        this.windowManager.updateViewLayout(this.windowView, layoutParams);
    }
}
