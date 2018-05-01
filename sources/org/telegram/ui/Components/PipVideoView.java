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
import java.util.Collection;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
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

        public void onAnimationEnd(Animator animator) {
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

        public MiniControlsView(Context context, boolean z) {
            super(context);
            this.inlineButton = new ImageView(context);
            this.inlineButton.setScaleType(ScaleType.CENTER);
            this.inlineButton.setImageResource(C0446R.drawable.ic_outinline);
            addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new OnClickListener(PipVideoView.this) {
                public void onClick(View view) {
                    if (PipVideoView.this.parentSheet != null) {
                        PipVideoView.this.parentSheet.exitFromPip();
                    } else if (PipVideoView.this.photoViewer != null) {
                        PipVideoView.this.photoViewer.exitFromPip();
                    }
                }
            });
            if (z) {
                this.progressPaint = new Paint();
                this.progressPaint.setColor(-15095832);
                this.progressInnerPaint = new Paint();
                this.progressInnerPaint.setColor(-6975081);
                setWillNotDraw(false);
                this.playButton = new ImageView(context);
                this.playButton.setScaleType(ScaleType.CENTER);
                addView(this.playButton, LayoutHelper.createFrame(48, 48, true));
                this.playButton.setOnClickListener(new OnClickListener(PipVideoView.this) {
                    public void onClick(View view) {
                        if (PipVideoView.this.photoViewer != null) {
                            view = PipVideoView.this.photoViewer.getVideoPlayer();
                            if (view != null) {
                                if (view.isPlaying()) {
                                    view.pause();
                                } else {
                                    view.play();
                                }
                                MiniControlsView.this.updatePlayButton();
                            }
                        }
                    }
                });
            }
            setOnTouchListener(new OnTouchListener(PipVideoView.this) {
                public boolean onTouch(View view, MotionEvent motionEvent) {
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
                        this.playButton.setImageResource(C0446R.drawable.ic_pauseinline);
                        AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
                    } else if (this.isCompleted) {
                        this.playButton.setImageResource(C0446R.drawable.ic_againinline);
                    } else {
                        this.playButton.setImageResource(C0446R.drawable.ic_playinline);
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
                if (this.currentAnimation) {
                    this.currentAnimation.cancel();
                }
                Animator[] animatorArr;
                if (this.isVisible) {
                    if (z2) {
                        this.currentAnimation = new AnimatorSet();
                        z2 = this.currentAnimation;
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0f});
                        z2.playTogether(animatorArr);
                        this.currentAnimation.setDuration(150);
                        this.currentAnimation.addListener(new C12806());
                        this.currentAnimation.start();
                    } else {
                        setAlpha(1.0f);
                    }
                } else if (z2) {
                    this.currentAnimation = new AnimatorSet();
                    z2 = this.currentAnimation;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f});
                    z2.playTogether(animatorArr);
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

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            checkNeedHide();
        }

        protected void onDraw(Canvas canvas) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
            AndroidUtilities.dp(7.0f);
            float f = (float) (measuredWidth - 0);
            int i = ((int) (this.progress * f)) + 0;
            if (this.bufferedPosition != 0.0f) {
                float f2 = (float) null;
                canvas.drawRect(f2, (float) measuredHeight, f2 + (f * r0.bufferedPosition), (float) (AndroidUtilities.dp(3.0f) + measuredHeight), r0.progressInnerPaint);
            }
            canvas.drawRect((float) null, (float) measuredHeight, (float) i, (float) (measuredHeight + AndroidUtilities.dp(3.0f)), r0.progressPaint);
        }
    }

    public TextureView show(Activity activity, EmbedBottomSheet embedBottomSheet, View view, float f, int i, WebView webView) {
        return show(activity, null, embedBottomSheet, view, f, i, webView);
    }

    public TextureView show(Activity activity, PhotoViewer photoViewer, float f, int i) {
        return show(activity, photoViewer, null, null, f, i, null);
    }

    public TextureView show(Activity activity, PhotoViewer photoViewer, EmbedBottomSheet embedBottomSheet, View view, float f, int i, WebView webView) {
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
                    motionEvent = rawX - this.startX;
                    float f = rawY - this.startY;
                    LayoutParams access$600 = PipVideoView.this.windowLayoutParams;
                    access$600.x = (int) (((float) access$600.x) + motionEvent);
                    motionEvent = PipVideoView.this.windowLayoutParams;
                    motionEvent.y = (int) (((float) motionEvent.y) + f);
                    motionEvent = PipVideoView.this.videoWidth / 2;
                    int i = -motionEvent;
                    if (PipVideoView.this.windowLayoutParams.x < i) {
                        PipVideoView.this.windowLayoutParams.x = i;
                    } else if (PipVideoView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + motionEvent) {
                        PipVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + motionEvent;
                    }
                    float f2 = 1.0f;
                    if (PipVideoView.this.windowLayoutParams.x < 0) {
                        f2 = 1.0f + ((((float) PipVideoView.this.windowLayoutParams.x) / ((float) motionEvent)) * 0.5f);
                    } else if (PipVideoView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) {
                        f2 = 1.0f - ((((float) ((PipVideoView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + PipVideoView.this.windowLayoutParams.width)) / ((float) motionEvent)) * 0.5f);
                    }
                    if (PipVideoView.this.windowView.getAlpha() != f2) {
                        PipVideoView.this.windowView.setAlpha(f2);
                    }
                    if (PipVideoView.this.windowLayoutParams.y < null) {
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
        embedBottomSheet = new AspectRatioFrameLayout(activity);
        embedBottomSheet.setAspectRatio(f, i);
        this.windowView.addView(embedBottomSheet, LayoutHelper.createFrame(-1, -1, 17));
        if (webView != null) {
            ViewGroup viewGroup = (ViewGroup) webView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(webView);
            }
            embedBottomSheet.addView(webView, LayoutHelper.createFrame(-1, -1.0f));
            webView = null;
        } else {
            webView = new TextureView(activity);
            embedBottomSheet.addView(webView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (view == null) {
            this.controlsView = new MiniControlsView(activity, photoViewer != null ? 1 : null);
        } else {
            this.controlsView = view;
        }
        this.windowView.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        this.windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        activity = this.preferences.getInt("sidex", 1);
        photoViewer = this.preferences.getInt("sidey", 0);
        view = this.preferences.getFloat("px", 0.0f);
        i = this.preferences.getFloat("py", 0.0f);
        try {
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.width = this.videoWidth;
            this.windowLayoutParams.height = this.videoHeight;
            this.windowLayoutParams.x = getSideCoord(true, activity, view, this.videoWidth);
            this.windowLayoutParams.y = getSideCoord(false, photoViewer, i, this.videoHeight);
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.gravity = 51;
            if (VERSION.SDK_INT >= 26) {
                this.windowLayoutParams.type = 2038;
            } else {
                this.windowLayoutParams.type = 2003;
            }
            this.windowLayoutParams.flags = 16777736;
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            return webView;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return null;
        }
    }

    public void onVideoCompleted() {
        if (this.controlsView instanceof MiniControlsView) {
            MiniControlsView miniControlsView = (MiniControlsView) this.controlsView;
            miniControlsView.isCompleted = true;
            miniControlsView.progress = 0.0f;
            miniControlsView.bufferedPosition = 0.0f;
            miniControlsView.updatePlayButton();
            miniControlsView.invalidate();
            miniControlsView.show(true, true);
        }
    }

    public void setBufferedProgress(float f) {
        if (this.controlsView instanceof MiniControlsView) {
            ((MiniControlsView) this.controlsView).setBufferedProgress(f);
        }
    }

    public void updatePlayButton() {
        if (this.controlsView instanceof MiniControlsView) {
            MiniControlsView miniControlsView = (MiniControlsView) this.controlsView;
            miniControlsView.updatePlayButton();
            miniControlsView.invalidate();
        }
    }

    private static int getSideCoord(boolean z, int i, float f, int i2) {
        int i3;
        if (z) {
            i3 = AndroidUtilities.displaySize.x - i2;
        } else {
            i3 = (AndroidUtilities.displaySize.y - i2) - ActionBar.getCurrentActionBarHeight();
        }
        if (i == 0) {
            i = AndroidUtilities.dp(10.0f);
        } else if (i == 1) {
            i = i3 - AndroidUtilities.dp(10.0f);
        } else {
            i = Math.round(((float) (i3 - AndroidUtilities.dp(NUM))) * f) + AndroidUtilities.dp(10.0f);
        }
        return !z ? i + ActionBar.getCurrentActionBarHeight() : i;
    }

    public void close() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = r2.windowManager;	 Catch:{ Exception -> 0x0007 }
        r1 = r2.windowView;	 Catch:{ Exception -> 0x0007 }
        r0.removeView(r1);	 Catch:{ Exception -> 0x0007 }
    L_0x0007:
        r0 = 0;
        r2.parentSheet = r0;
        r2.photoViewer = r0;
        r2.parentActivity = r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipVideoView.close():void");
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

    private void animateToBoundsMaybe() {
        Collection collection;
        boolean z;
        AnimatorSet animatorSet;
        int sideCoord = getSideCoord(true, 0, 0.0f, this.videoWidth);
        int sideCoord2 = getSideCoord(true, 1, 0.0f, this.videoWidth);
        int sideCoord3 = getSideCoord(false, 0, 0.0f, this.videoHeight);
        int sideCoord4 = getSideCoord(false, 1, 0.0f, this.videoHeight);
        Editor edit = this.preferences.edit();
        int dp = AndroidUtilities.dp(20.0f);
        if (Math.abs(sideCoord - this.windowLayoutParams.x) > dp) {
            if (this.windowLayoutParams.x >= 0 || this.windowLayoutParams.x <= (-this.videoWidth) / 4) {
                ArrayList arrayList;
                if (Math.abs(sideCoord2 - this.windowLayoutParams.x) > dp) {
                    if (this.windowLayoutParams.x <= AndroidUtilities.displaySize.x - this.videoWidth || this.windowLayoutParams.x >= AndroidUtilities.displaySize.x - ((this.videoWidth / 4) * 3)) {
                        if (this.windowView.getAlpha() != 1.0f) {
                            arrayList = new ArrayList();
                            if (this.windowLayoutParams.x < 0) {
                                arrayList.add(ObjectAnimator.ofInt(this, "x", new int[]{-this.videoWidth}));
                            } else {
                                arrayList.add(ObjectAnimator.ofInt(this, "x", new int[]{AndroidUtilities.displaySize.x}));
                            }
                            collection = arrayList;
                            z = true;
                            if (!z) {
                                if (Math.abs(sideCoord3 - this.windowLayoutParams.y) > dp) {
                                    if (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight()) {
                                        if (Math.abs(sideCoord4 - this.windowLayoutParams.y) > dp) {
                                            if (collection == null) {
                                                collection = new ArrayList();
                                            }
                                            edit.putInt("sidey", 1);
                                            collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord4}));
                                        } else {
                                            edit.putFloat("py", ((float) (this.windowLayoutParams.y - sideCoord3)) / ((float) (sideCoord4 - sideCoord3)));
                                            edit.putInt("sidey", 2);
                                        }
                                        edit.commit();
                                    }
                                }
                                if (collection == null) {
                                    collection = new ArrayList();
                                }
                                edit.putInt("sidey", 0);
                                collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord3}));
                                edit.commit();
                            }
                            if (collection == null) {
                                if (this.decelerateInterpolator == null) {
                                    this.decelerateInterpolator = new DecelerateInterpolator();
                                }
                                animatorSet = new AnimatorSet();
                                animatorSet.setInterpolator(this.decelerateInterpolator);
                                animatorSet.setDuration(150);
                                if (z) {
                                    collection.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f}));
                                    animatorSet.addListener(new C12742());
                                }
                                animatorSet.playTogether(collection);
                                animatorSet.start();
                            }
                        }
                        edit.putFloat("px", ((float) (this.windowLayoutParams.x - sideCoord)) / ((float) (sideCoord2 - sideCoord)));
                        edit.putInt("sidex", 2);
                        arrayList = null;
                        collection = arrayList;
                        z = false;
                        if (z) {
                            if (Math.abs(sideCoord3 - this.windowLayoutParams.y) > dp) {
                                if (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight()) {
                                    if (Math.abs(sideCoord4 - this.windowLayoutParams.y) > dp) {
                                        edit.putFloat("py", ((float) (this.windowLayoutParams.y - sideCoord3)) / ((float) (sideCoord4 - sideCoord3)));
                                        edit.putInt("sidey", 2);
                                    } else {
                                        if (collection == null) {
                                            collection = new ArrayList();
                                        }
                                        edit.putInt("sidey", 1);
                                        collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord4}));
                                    }
                                    edit.commit();
                                }
                            }
                            if (collection == null) {
                                collection = new ArrayList();
                            }
                            edit.putInt("sidey", 0);
                            collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord3}));
                            edit.commit();
                        }
                        if (collection == null) {
                            if (this.decelerateInterpolator == null) {
                                this.decelerateInterpolator = new DecelerateInterpolator();
                            }
                            animatorSet = new AnimatorSet();
                            animatorSet.setInterpolator(this.decelerateInterpolator);
                            animatorSet.setDuration(150);
                            if (z) {
                                collection.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f}));
                                animatorSet.addListener(new C12742());
                            }
                            animatorSet.playTogether(collection);
                            animatorSet.start();
                        }
                    }
                }
                arrayList = new ArrayList();
                edit.putInt("sidex", 1);
                if (this.windowView.getAlpha() != 1.0f) {
                    arrayList.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{1.0f}));
                }
                arrayList.add(ObjectAnimator.ofInt(this, "x", new int[]{sideCoord2}));
                collection = arrayList;
                z = false;
                if (z) {
                    if (Math.abs(sideCoord3 - this.windowLayoutParams.y) > dp) {
                        if (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight()) {
                            if (Math.abs(sideCoord4 - this.windowLayoutParams.y) > dp) {
                                if (collection == null) {
                                    collection = new ArrayList();
                                }
                                edit.putInt("sidey", 1);
                                collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord4}));
                            } else {
                                edit.putFloat("py", ((float) (this.windowLayoutParams.y - sideCoord3)) / ((float) (sideCoord4 - sideCoord3)));
                                edit.putInt("sidey", 2);
                            }
                            edit.commit();
                        }
                    }
                    if (collection == null) {
                        collection = new ArrayList();
                    }
                    edit.putInt("sidey", 0);
                    collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord3}));
                    edit.commit();
                }
                if (collection == null) {
                    if (this.decelerateInterpolator == null) {
                        this.decelerateInterpolator = new DecelerateInterpolator();
                    }
                    animatorSet = new AnimatorSet();
                    animatorSet.setInterpolator(this.decelerateInterpolator);
                    animatorSet.setDuration(150);
                    if (z) {
                        collection.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f}));
                        animatorSet.addListener(new C12742());
                    }
                    animatorSet.playTogether(collection);
                    animatorSet.start();
                }
            }
        }
        collection = new ArrayList();
        edit.putInt("sidex", 0);
        if (this.windowView.getAlpha() != 1.0f) {
            collection.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{1.0f}));
        }
        collection.add(ObjectAnimator.ofInt(this, "x", new int[]{sideCoord}));
        z = false;
        if (z) {
            if (Math.abs(sideCoord3 - this.windowLayoutParams.y) > dp) {
                if (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight()) {
                    if (Math.abs(sideCoord4 - this.windowLayoutParams.y) > dp) {
                        edit.putFloat("py", ((float) (this.windowLayoutParams.y - sideCoord3)) / ((float) (sideCoord4 - sideCoord3)));
                        edit.putInt("sidey", 2);
                    } else {
                        if (collection == null) {
                            collection = new ArrayList();
                        }
                        edit.putInt("sidey", 1);
                        collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord4}));
                    }
                    edit.commit();
                }
            }
            if (collection == null) {
                collection = new ArrayList();
            }
            edit.putInt("sidey", 0);
            collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord3}));
            edit.commit();
        }
        if (collection == null) {
            if (this.decelerateInterpolator == null) {
                this.decelerateInterpolator = new DecelerateInterpolator();
            }
            animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(this.decelerateInterpolator);
            animatorSet.setDuration(150);
            if (z) {
                collection.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f}));
                animatorSet.addListener(new C12742());
            }
            animatorSet.playTogether(collection);
            animatorSet.start();
        }
    }

    public static Rect getPipRect(float f) {
        int dp;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        int i = sharedPreferences.getInt("sidex", 1);
        int i2 = sharedPreferences.getInt("sidey", 0);
        float f2 = sharedPreferences.getFloat("px", 0.0f);
        float f3 = sharedPreferences.getFloat("py", 0.0f);
        if (f > 1.0f) {
            dp = AndroidUtilities.dp(192.0f);
            int i3 = dp;
            dp = (int) (((float) dp) / f);
            f = i3;
        } else {
            dp = AndroidUtilities.dp(192.0f);
            f = (int) (((float) dp) * f);
        }
        return new Rect((float) getSideCoord(true, i, f2, f), (float) getSideCoord(false, i2, f3, dp), (float) f, (float) dp);
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
        this.windowLayoutParams.x = i;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    @Keep
    public void setY(int i) {
        this.windowLayoutParams.y = i;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }
}
