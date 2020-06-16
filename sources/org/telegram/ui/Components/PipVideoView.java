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
import androidx.annotation.Keep;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.Components.PipVideoView;
import org.telegram.ui.PhotoViewer;

public class PipVideoView {
    /* access modifiers changed from: private */
    public View controlsView;
    private DecelerateInterpolator decelerateInterpolator;
    private boolean isInAppOnly;
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
        private Runnable hideRunnable = new Runnable() {
            public final void run() {
                PipVideoView.MiniControlsView.this.lambda$new$0$PipVideoView$MiniControlsView();
            }
        };
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

        static /* synthetic */ boolean lambda$new$3(View view, MotionEvent motionEvent) {
            return true;
        }

        public /* synthetic */ void lambda$new$0$PipVideoView$MiniControlsView() {
            show(false, true);
        }

        public MiniControlsView(Context context, boolean z) {
            super(context);
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(NUM);
            addView(imageView, LayoutHelper.createFrame(56, 48, 53));
            imageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PipVideoView.MiniControlsView.this.lambda$new$1$PipVideoView$MiniControlsView(view);
                }
            });
            if (z) {
                Paint paint = new Paint();
                this.progressPaint = paint;
                paint.setColor(-15095832);
                Paint paint2 = new Paint();
                this.progressInnerPaint = paint2;
                paint2.setColor(-6975081);
                setWillNotDraw(false);
                ImageView imageView2 = new ImageView(context);
                this.playButton = imageView2;
                imageView2.setScaleType(ImageView.ScaleType.CENTER);
                addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
                this.playButton.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        PipVideoView.MiniControlsView.this.lambda$new$2$PipVideoView$MiniControlsView(view);
                    }
                });
            }
            setOnTouchListener($$Lambda$PipVideoView$MiniControlsView$jKPELGvyg4VHgdM0XCbZaGLO4U.INSTANCE);
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
                if (this.isVisible) {
                    if (z2) {
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
                } else if (z2) {
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

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                if (!this.isVisible) {
                    show(true, true);
                    return true;
                }
                checkNeedHide();
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.currentAnimation != null) {
                return true;
            }
            return super.onTouchEvent(motionEvent);
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            super.requestDisallowInterceptTouchEvent(z);
            checkNeedHide();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            checkNeedHide();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
            AndroidUtilities.dp(7.0f);
            float f = (float) (measuredWidth - 0);
            int i = ((int) (this.progress * f)) + 0;
            float f2 = this.bufferedPosition;
            if (f2 != 0.0f) {
                float f3 = (float) 0;
                canvas.drawRect(f3, (float) measuredHeight, f3 + (f * f2), (float) (AndroidUtilities.dp(3.0f) + measuredHeight), this.progressInnerPaint);
            }
            Canvas canvas2 = canvas;
            canvas2.drawRect((float) 0, (float) measuredHeight, (float) i, (float) (measuredHeight + AndroidUtilities.dp(3.0f)), this.progressPaint);
        }
    }

    public PipVideoView(boolean z) {
        this.isInAppOnly = z;
    }

    public TextureView show(Activity activity, EmbedBottomSheet embedBottomSheet, View view, float f, int i, WebView webView) {
        return show(activity, (PhotoViewer) null, embedBottomSheet, view, f, i, webView);
    }

    public TextureView show(Activity activity, PhotoViewer photoViewer2, float f, int i) {
        return show(activity, photoViewer2, (EmbedBottomSheet) null, (View) null, f, i, (WebView) null);
    }

    public TextureView show(Activity activity, PhotoViewer photoViewer2, EmbedBottomSheet embedBottomSheet, View view, float f, int i, WebView webView) {
        TextureView textureView;
        this.parentSheet = embedBottomSheet;
        this.photoViewer = photoViewer2;
        this.windowView = new FrameLayout(activity) {
            private boolean dragging;
            private float startX;
            private float startY;

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
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
                if (!this.dragging) {
                    return super.dispatchTouchEvent(motionEvent);
                }
                if (motionEvent.getAction() == 2) {
                    WindowManager.LayoutParams access$500 = PipVideoView.this.windowLayoutParams;
                    access$500.x = (int) (((float) access$500.x) + (rawX - this.startX));
                    WindowManager.LayoutParams access$5002 = PipVideoView.this.windowLayoutParams;
                    access$5002.y = (int) (((float) access$5002.y) + (rawY - this.startY));
                    int access$600 = PipVideoView.this.videoWidth / 2;
                    int i = -access$600;
                    if (PipVideoView.this.windowLayoutParams.x < i) {
                        PipVideoView.this.windowLayoutParams.x = i;
                    } else if (PipVideoView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + access$600) {
                        PipVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + access$600;
                    }
                    float f = 1.0f;
                    if (PipVideoView.this.windowLayoutParams.x < 0) {
                        f = 1.0f + ((((float) PipVideoView.this.windowLayoutParams.x) / ((float) access$600)) * 0.5f);
                    } else if (PipVideoView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) {
                        f = 1.0f - ((((float) ((PipVideoView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + PipVideoView.this.windowLayoutParams.width)) / ((float) access$600)) * 0.5f);
                    }
                    if (PipVideoView.this.windowView.getAlpha() != f) {
                        PipVideoView.this.windowView.setAlpha(f);
                    }
                    if (PipVideoView.this.windowLayoutParams.y < 0) {
                        PipVideoView.this.windowLayoutParams.y = 0;
                    } else if (PipVideoView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + 0) {
                        PipVideoView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + 0;
                    }
                    PipVideoView.this.windowManager.updateViewLayout(PipVideoView.this.windowView, PipVideoView.this.windowLayoutParams);
                    this.startX = rawX;
                    this.startY = rawY;
                } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    this.dragging = false;
                    PipVideoView.this.animateToBoundsMaybe();
                }
                return true;
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return super.onInterceptTouchEvent(motionEvent);
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
            this.controlsView = new MiniControlsView(activity, photoViewer2 != null);
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
        int i2 = sharedPreferences.getInt("sidex", 1);
        int i3 = this.preferences.getInt("sidey", 0);
        float f2 = this.preferences.getFloat("px", 0.0f);
        float f3 = this.preferences.getFloat("py", 0.0f);
        try {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
            layoutParams.width = this.videoWidth;
            layoutParams.height = this.videoHeight;
            layoutParams.x = getSideCoord(true, i2, f2, this.videoWidth);
            this.windowLayoutParams.y = getSideCoord(false, i3, f3, this.videoHeight);
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
        int i4;
        if (z) {
            i3 = AndroidUtilities.displaySize.x;
        } else {
            i3 = AndroidUtilities.displaySize.y - i2;
            i2 = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
        }
        int i5 = i3 - i2;
        if (i == 0) {
            i4 = AndroidUtilities.dp(10.0f);
        } else if (i == 1) {
            i4 = i5 - AndroidUtilities.dp(10.0f);
        } else {
            i4 = Math.round(((float) (i5 - AndroidUtilities.dp(20.0f))) * f) + AndroidUtilities.dp(10.0f);
        }
        return !z ? i4 + ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight : i4;
    }

    public void close() {
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception unused) {
        }
        this.parentSheet = null;
        this.photoViewer = null;
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0185  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void animateToBoundsMaybe() {
        /*
            r16 = this;
            r0 = r16
            int r1 = r0.videoWidth
            r2 = 1
            r3 = 0
            r4 = 0
            int r1 = getSideCoord(r2, r3, r4, r1)
            int r5 = r0.videoWidth
            int r5 = getSideCoord(r2, r2, r4, r5)
            int r6 = r0.videoHeight
            int r6 = getSideCoord(r3, r3, r4, r6)
            int r7 = r0.videoHeight
            int r7 = getSideCoord(r3, r2, r4, r7)
            android.content.SharedPreferences r8 = r0.preferences
            android.content.SharedPreferences$Editor r8 = r8.edit()
            r9 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            android.view.WindowManager$LayoutParams r10 = r0.windowLayoutParams
            int r10 = r10.x
            int r10 = r1 - r10
            int r10 = java.lang.Math.abs(r10)
            java.lang.String r12 = "alpha"
            java.lang.String r13 = "sidex"
            java.lang.String r14 = "x"
            r15 = 1065353216(0x3var_, float:1.0)
            if (r10 <= r9) goto L_0x00e7
            android.view.WindowManager$LayoutParams r10 = r0.windowLayoutParams
            int r10 = r10.x
            if (r10 >= 0) goto L_0x004c
            int r4 = r0.videoWidth
            int r4 = -r4
            int r4 = r4 / 4
            if (r10 <= r4) goto L_0x004c
            goto L_0x00e7
        L_0x004c:
            android.view.WindowManager$LayoutParams r4 = r0.windowLayoutParams
            int r4 = r4.x
            int r4 = r5 - r4
            int r4 = java.lang.Math.abs(r4)
            if (r4 <= r9) goto L_0x00b9
            android.view.WindowManager$LayoutParams r4 = r0.windowLayoutParams
            int r4 = r4.x
            android.graphics.Point r10 = org.telegram.messenger.AndroidUtilities.displaySize
            int r10 = r10.x
            int r11 = r0.videoWidth
            int r3 = r10 - r11
            if (r4 <= r3) goto L_0x006e
            int r11 = r11 / 4
            int r11 = r11 * 3
            int r10 = r10 - r11
            if (r4 >= r10) goto L_0x006e
            goto L_0x00b9
        L_0x006e:
            android.widget.FrameLayout r3 = r0.windowView
            float r3 = r3.getAlpha()
            int r3 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r3 == 0) goto L_0x00a5
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            android.view.WindowManager$LayoutParams r3 = r0.windowLayoutParams
            int r3 = r3.x
            if (r3 >= 0) goto L_0x0093
            int[] r3 = new int[r2]
            int r4 = r0.videoWidth
            int r4 = -r4
            r5 = 0
            r3[r5] = r4
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofInt(r0, r14, r3)
            r1.add(r3)
            goto L_0x00a3
        L_0x0093:
            r5 = 0
            int[] r3 = new int[r2]
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r4.x
            r3[r5] = r4
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofInt(r0, r14, r3)
            r1.add(r3)
        L_0x00a3:
            r5 = 1
            goto L_0x0114
        L_0x00a5:
            android.view.WindowManager$LayoutParams r3 = r0.windowLayoutParams
            int r3 = r3.x
            int r3 = r3 - r1
            float r3 = (float) r3
            int r5 = r5 - r1
            float r1 = (float) r5
            float r3 = r3 / r1
            java.lang.String r1 = "px"
            r8.putFloat(r1, r3)
            r1 = 2
            r8.putInt(r13, r1)
            r1 = 0
            goto L_0x0113
        L_0x00b9:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.putInt(r13, r2)
            android.widget.FrameLayout r3 = r0.windowView
            float r3 = r3.getAlpha()
            int r3 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r3 == 0) goto L_0x00da
            android.widget.FrameLayout r3 = r0.windowView
            float[] r4 = new float[r2]
            r10 = 0
            r4[r10] = r15
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r12, r4)
            r1.add(r3)
            goto L_0x00db
        L_0x00da:
            r10 = 0
        L_0x00db:
            int[] r3 = new int[r2]
            r3[r10] = r5
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofInt(r0, r14, r3)
            r1.add(r3)
            goto L_0x0113
        L_0x00e7:
            r10 = 0
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r8.putInt(r13, r10)
            android.widget.FrameLayout r4 = r0.windowView
            float r4 = r4.getAlpha()
            int r4 = (r4 > r15 ? 1 : (r4 == r15 ? 0 : -1))
            if (r4 == 0) goto L_0x0107
            android.widget.FrameLayout r4 = r0.windowView
            float[] r5 = new float[r2]
            r5[r10] = r15
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r12, r5)
            r3.add(r4)
        L_0x0107:
            int[] r4 = new int[r2]
            r4[r10] = r1
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofInt(r0, r14, r4)
            r3.add(r1)
            r1 = r3
        L_0x0113:
            r5 = 0
        L_0x0114:
            if (r5 != 0) goto L_0x0183
            android.view.WindowManager$LayoutParams r3 = r0.windowLayoutParams
            int r3 = r3.y
            int r3 = r6 - r3
            int r3 = java.lang.Math.abs(r3)
            java.lang.String r4 = "y"
            java.lang.String r10 = "sidey"
            if (r3 <= r9) goto L_0x016a
            android.view.WindowManager$LayoutParams r3 = r0.windowLayoutParams
            int r3 = r3.y
            int r11 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            int r13 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            int r11 = r11 + r13
            if (r3 > r11) goto L_0x0134
            goto L_0x016a
        L_0x0134:
            android.view.WindowManager$LayoutParams r3 = r0.windowLayoutParams
            int r3 = r3.y
            int r3 = r7 - r3
            int r3 = java.lang.Math.abs(r3)
            if (r3 > r9) goto L_0x0157
            if (r1 != 0) goto L_0x0147
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
        L_0x0147:
            r8.putInt(r10, r2)
            int[] r3 = new int[r2]
            r6 = 0
            r3[r6] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofInt(r0, r4, r3)
            r1.add(r3)
            goto L_0x0180
        L_0x0157:
            android.view.WindowManager$LayoutParams r3 = r0.windowLayoutParams
            int r3 = r3.y
            int r3 = r3 - r6
            float r3 = (float) r3
            int r7 = r7 - r6
            float r4 = (float) r7
            float r3 = r3 / r4
            java.lang.String r4 = "py"
            r8.putFloat(r4, r3)
            r3 = 2
            r8.putInt(r10, r3)
            goto L_0x0180
        L_0x016a:
            if (r1 != 0) goto L_0x0171
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
        L_0x0171:
            r3 = 0
            r8.putInt(r10, r3)
            int[] r7 = new int[r2]
            r7[r3] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofInt(r0, r4, r7)
            r1.add(r3)
        L_0x0180:
            r8.commit()
        L_0x0183:
            if (r1 == 0) goto L_0x01be
            android.view.animation.DecelerateInterpolator r3 = r0.decelerateInterpolator
            if (r3 != 0) goto L_0x0190
            android.view.animation.DecelerateInterpolator r3 = new android.view.animation.DecelerateInterpolator
            r3.<init>()
            r0.decelerateInterpolator = r3
        L_0x0190:
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            android.view.animation.DecelerateInterpolator r4 = r0.decelerateInterpolator
            r3.setInterpolator(r4)
            r6 = 150(0x96, double:7.4E-322)
            r3.setDuration(r6)
            if (r5 == 0) goto L_0x01b8
            android.widget.FrameLayout r4 = r0.windowView
            float[] r2 = new float[r2]
            r5 = 0
            r6 = 0
            r2[r6] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r4, r12, r2)
            r1.add(r2)
            org.telegram.ui.Components.PipVideoView$2 r2 = new org.telegram.ui.Components.PipVideoView$2
            r2.<init>()
            r3.addListener(r2)
        L_0x01b8:
            r3.playTogether(r1)
            r3.start()
        L_0x01be:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipVideoView.animateToBoundsMaybe():void");
    }

    public static Rect getPipRect(float f) {
        int i;
        int i2;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        int i3 = sharedPreferences.getInt("sidex", 1);
        int i4 = sharedPreferences.getInt("sidey", 0);
        float f2 = sharedPreferences.getFloat("px", 0.0f);
        float f3 = sharedPreferences.getFloat("py", 0.0f);
        if (f > 1.0f) {
            i2 = AndroidUtilities.dp(192.0f);
            i = (int) (((float) i2) / f);
        } else {
            int dp = AndroidUtilities.dp(192.0f);
            int i5 = dp;
            i2 = (int) (((float) dp) * f);
            i = i5;
        }
        return new Rect((float) getSideCoord(true, i3, f2, i2), (float) getSideCoord(false, i4, f3, i), (float) i2, (float) i);
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
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.x = i;
        this.windowManager.updateViewLayout(this.windowView, layoutParams);
    }

    @Keep
    public void setY(int i) {
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.y = i;
        this.windowManager.updateViewLayout(this.windowView, layoutParams);
    }
}
