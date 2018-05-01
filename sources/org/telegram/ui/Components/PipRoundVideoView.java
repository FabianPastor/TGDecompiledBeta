package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Collection;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;

public class PipRoundVideoView implements NotificationCenterDelegate {
    @SuppressLint({"StaticFieldLeak"})
    private static PipRoundVideoView instance;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private Bitmap bitmap;
    private int currentAccount;
    private DecelerateInterpolator decelerateInterpolator;
    private AnimatorSet hideShowAnimation;
    private ImageView imageView;
    private Runnable onCloseRunnable;
    private Activity parentActivity;
    private SharedPreferences preferences;
    private RectF rect = new RectF();
    private TextureView textureView;
    private int videoHeight;
    private int videoWidth;
    private LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private FrameLayout windowView;

    /* renamed from: org.telegram.ui.Components.PipRoundVideoView$3 */
    class C12693 extends ViewOutlineProvider {
        C12693() {
        }

        @TargetApi(21)
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f));
        }
    }

    /* renamed from: org.telegram.ui.Components.PipRoundVideoView$5 */
    class C12705 extends AnimatorListenerAdapter {
        C12705() {
        }

        public void onAnimationEnd(Animator animator) {
            if (animator.equals(PipRoundVideoView.this.hideShowAnimation) != null) {
                PipRoundVideoView.this.hideShowAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.PipRoundVideoView$7 */
    class C12727 extends AnimatorListenerAdapter {
        C12727() {
        }

        public void onAnimationEnd(Animator animator) {
            PipRoundVideoView.this.close(false);
            if (PipRoundVideoView.this.onCloseRunnable != null) {
                PipRoundVideoView.this.onCloseRunnable.run();
            }
        }
    }

    public void show(Activity activity, Runnable runnable) {
        if (activity != null) {
            instance = this;
            this.onCloseRunnable = runnable;
            this.windowView = new FrameLayout(activity) {
                private boolean dragging;
                private boolean startDragging;
                private float startX;
                private float startY;

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0) {
                        this.startX = motionEvent.getRawX();
                        this.startY = motionEvent.getRawY();
                        this.startDragging = true;
                    }
                    return true;
                }

                public void requestDisallowInterceptTouchEvent(boolean z) {
                    super.requestDisallowInterceptTouchEvent(z);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (!this.startDragging && !this.dragging) {
                        return false;
                    }
                    float rawX = motionEvent.getRawX();
                    float rawY = motionEvent.getRawY();
                    if (motionEvent.getAction() == 2) {
                        motionEvent = rawX - this.startX;
                        float f = rawY - this.startY;
                        if (this.startDragging) {
                            if (Math.abs(motionEvent) >= AndroidUtilities.getPixelsInCM(0.3f, true) || Math.abs(f) >= AndroidUtilities.getPixelsInCM(0.3f, false)) {
                                this.dragging = true;
                                this.startDragging = false;
                            }
                        } else if (this.dragging) {
                            LayoutParams access$000 = PipRoundVideoView.this.windowLayoutParams;
                            access$000.x = (int) (((float) access$000.x) + motionEvent);
                            motionEvent = PipRoundVideoView.this.windowLayoutParams;
                            motionEvent.y = (int) (((float) motionEvent.y) + f);
                            motionEvent = PipRoundVideoView.this.videoWidth / 2;
                            int i = -motionEvent;
                            if (PipRoundVideoView.this.windowLayoutParams.x < i) {
                                PipRoundVideoView.this.windowLayoutParams.x = i;
                            } else if (PipRoundVideoView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - PipRoundVideoView.this.windowLayoutParams.width) + motionEvent) {
                                PipRoundVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipRoundVideoView.this.windowLayoutParams.width) + motionEvent;
                            }
                            float f2 = 1.0f;
                            if (PipRoundVideoView.this.windowLayoutParams.x < 0) {
                                f2 = 1.0f + ((((float) PipRoundVideoView.this.windowLayoutParams.x) / ((float) motionEvent)) * 0.5f);
                            } else if (PipRoundVideoView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - PipRoundVideoView.this.windowLayoutParams.width) {
                                f2 = 1.0f - ((((float) ((PipRoundVideoView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + PipRoundVideoView.this.windowLayoutParams.width)) / ((float) motionEvent)) * 0.5f);
                            }
                            if (PipRoundVideoView.this.windowView.getAlpha() != f2) {
                                PipRoundVideoView.this.windowView.setAlpha(f2);
                            }
                            if (PipRoundVideoView.this.windowLayoutParams.y < null) {
                                PipRoundVideoView.this.windowLayoutParams.y = 0;
                            } else if (PipRoundVideoView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - PipRoundVideoView.this.windowLayoutParams.height) + 0) {
                                PipRoundVideoView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - PipRoundVideoView.this.windowLayoutParams.height) + 0;
                            }
                            PipRoundVideoView.this.windowManager.updateViewLayout(PipRoundVideoView.this.windowView, PipRoundVideoView.this.windowLayoutParams);
                            this.startX = rawX;
                            this.startY = rawY;
                        }
                    } else if (motionEvent.getAction() == 1) {
                        if (this.startDragging != null && this.dragging == null) {
                            motionEvent = MediaController.getInstance().getPlayingMessageObject();
                            if (motionEvent != null) {
                                if (MediaController.getInstance().isMessagePaused()) {
                                    MediaController.getInstance().playMessage(motionEvent);
                                } else {
                                    MediaController.getInstance().pauseMessage(motionEvent);
                                }
                            }
                        }
                        this.dragging = false;
                        this.startDragging = false;
                        PipRoundVideoView.this.animateToBoundsMaybe();
                    }
                    return true;
                }

                protected void onDraw(Canvas canvas) {
                    if (Theme.chat_roundVideoShadow != null) {
                        Theme.chat_roundVideoShadow.setAlpha((int) (getAlpha() * 255.0f));
                        Theme.chat_roundVideoShadow.setBounds(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(125.0f), AndroidUtilities.dp(125.0f));
                        Theme.chat_roundVideoShadow.draw(canvas);
                    }
                }
            };
            this.windowView.setWillNotDraw(false);
            this.videoWidth = AndroidUtilities.dp(126.0f);
            this.videoHeight = AndroidUtilities.dp(126.0f);
            if (VERSION.SDK_INT >= 21) {
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(activity) {
                    protected boolean drawChild(Canvas canvas, View view, long j) {
                        j = super.drawChild(canvas, view, j);
                        if (view == PipRoundVideoView.this.textureView) {
                            view = MediaController.getInstance().getPlayingMessageObject();
                            if (view != null) {
                                PipRoundVideoView.this.rect.set(AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), ((float) getMeasuredWidth()) - AndroidUtilities.dpf2(1.5f), ((float) getMeasuredHeight()) - AndroidUtilities.dpf2(1.5f));
                                canvas.drawArc(PipRoundVideoView.this.rect, -90.0f, 360.0f * view.audioProgress, false, Theme.chat_radialProgressPaint);
                            }
                        }
                        return j;
                    }
                };
                this.aspectRatioFrameLayout.setOutlineProvider(new C12693());
                this.aspectRatioFrameLayout.setClipToOutline(true);
            } else {
                runnable = new Paint(1);
                runnable.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
                runnable.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(activity) {
                    private Path aspectPath = new Path();

                    protected void onSizeChanged(int i, int i2, int i3, int i4) {
                        super.onSizeChanged(i, i2, i3, i4);
                        this.aspectPath.reset();
                        i = (float) (i / 2);
                        this.aspectPath.addCircle(i, (float) (i2 / 2), i, Direction.CW);
                        this.aspectPath.toggleInverseFillType();
                    }

                    protected void dispatchDraw(Canvas canvas) {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(this.aspectPath, runnable);
                    }

                    protected boolean drawChild(android.graphics.Canvas r7, android.view.View r8, long r9) {
                        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                        /*
                        r6 = this;
                        r9 = super.drawChild(r7, r8, r9);	 Catch:{ Throwable -> 0x0005 }
                        goto L_0x0006;
                    L_0x0005:
                        r9 = 0;
                    L_0x0006:
                        r10 = org.telegram.ui.Components.PipRoundVideoView.this;
                        r10 = r10.textureView;
                        if (r8 != r10) goto L_0x0054;
                    L_0x000e:
                        r8 = org.telegram.messenger.MediaController.getInstance();
                        r8 = r8.getPlayingMessageObject();
                        if (r8 == 0) goto L_0x0054;
                    L_0x0018:
                        r10 = org.telegram.ui.Components.PipRoundVideoView.this;
                        r10 = r10.rect;
                        r0 = NUM; // 0x3fc00000 float:1.5 double:5.28426686E-315;
                        r1 = org.telegram.messenger.AndroidUtilities.dpf2(r0);
                        r2 = org.telegram.messenger.AndroidUtilities.dpf2(r0);
                        r3 = r6.getMeasuredWidth();
                        r3 = (float) r3;
                        r4 = org.telegram.messenger.AndroidUtilities.dpf2(r0);
                        r3 = r3 - r4;
                        r4 = r6.getMeasuredHeight();
                        r4 = (float) r4;
                        r0 = org.telegram.messenger.AndroidUtilities.dpf2(r0);
                        r4 = r4 - r0;
                        r10.set(r1, r2, r3, r4);
                        r10 = org.telegram.ui.Components.PipRoundVideoView.this;
                        r1 = r10.rect;
                        r2 = -NUM; // 0xffffffffc2b40000 float:-90.0 double:NaN;
                        r10 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
                        r8 = r8.audioProgress;
                        r3 = r10 * r8;
                        r4 = 0;
                        r5 = org.telegram.ui.ActionBar.Theme.chat_radialProgressPaint;
                        r0 = r7;
                        r0.drawArc(r1, r2, r3, r4, r5);
                    L_0x0054:
                        return r9;
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipRoundVideoView.4.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
                    }
                };
                this.aspectRatioFrameLayout.setLayerType(2, null);
            }
            this.aspectRatioFrameLayout.setAspectRatio(1.0f, 0);
            this.windowView.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(120, 120.0f, 51, 3.0f, 3.0f, 0.0f, 0.0f));
            this.windowView.setAlpha(1.0f);
            this.windowView.setScaleX(0.8f);
            this.windowView.setScaleY(0.8f);
            this.textureView = new TextureView(activity);
            this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
            this.imageView = new ImageView(activity);
            this.aspectRatioFrameLayout.addView(this.imageView, LayoutHelper.createFrame(-1, -1.0f));
            this.imageView.setVisibility(4);
            this.windowManager = (WindowManager) activity.getSystemService("window");
            this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
            runnable = this.preferences.getInt("sidex", 1);
            int i = this.preferences.getInt("sidey", 0);
            float f = this.preferences.getFloat("px", 0.0f);
            float f2 = this.preferences.getFloat("py", 0.0f);
            try {
                this.windowLayoutParams = new LayoutParams();
                this.windowLayoutParams.width = this.videoWidth;
                this.windowLayoutParams.height = this.videoHeight;
                this.windowLayoutParams.x = getSideCoord(true, runnable, f, this.videoWidth);
                this.windowLayoutParams.y = getSideCoord(false, i, f2, this.videoHeight);
                this.windowLayoutParams.format = -3;
                this.windowLayoutParams.gravity = 51;
                this.windowLayoutParams.type = 99;
                this.windowLayoutParams.flags = 16777736;
                this.windowManager.addView(this.windowView, this.windowLayoutParams);
                this.parentActivity = activity;
                this.currentAccount = UserConfig.selectedAccount;
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
                runShowHideAnimation(true);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.messagePlayingProgressDidChanged && this.aspectRatioFrameLayout != 0) {
            this.aspectRatioFrameLayout.invalidate();
        }
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public void close(boolean r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r3 = this;
        r0 = 0;
        if (r4 == 0) goto L_0x0055;
    L_0x0003:
        r4 = r3.textureView;
        if (r4 == 0) goto L_0x007f;
    L_0x0007:
        r4 = r3.textureView;
        r4 = r4.getParent();
        if (r4 == 0) goto L_0x007f;
    L_0x000f:
        r4 = r3.textureView;
        r4 = r4.getWidth();
        if (r4 <= 0) goto L_0x0033;
    L_0x0017:
        r4 = r3.textureView;
        r4 = r4.getHeight();
        if (r4 <= 0) goto L_0x0033;
    L_0x001f:
        r4 = r3.textureView;
        r4 = r4.getWidth();
        r1 = r3.textureView;
        r1 = r1.getHeight();
        r2 = android.graphics.Bitmap.Config.ARGB_8888;
        r4 = org.telegram.messenger.Bitmaps.createBitmap(r4, r1, r2);
        r3.bitmap = r4;
    L_0x0033:
        r4 = r3.textureView;	 Catch:{ Throwable -> 0x003b }
        r1 = r3.bitmap;	 Catch:{ Throwable -> 0x003b }
        r4.getBitmap(r1);	 Catch:{ Throwable -> 0x003b }
        goto L_0x003d;
    L_0x003b:
        r3.bitmap = r0;
    L_0x003d:
        r4 = r3.imageView;
        r0 = r3.bitmap;
        r4.setImageBitmap(r0);
        r4 = r3.aspectRatioFrameLayout;	 Catch:{ Exception -> 0x004b }
        r0 = r3.textureView;	 Catch:{ Exception -> 0x004b }
        r4.removeView(r0);	 Catch:{ Exception -> 0x004b }
    L_0x004b:
        r4 = r3.imageView;
        r0 = 0;
        r4.setVisibility(r0);
        r3.runShowHideAnimation(r0);
        goto L_0x007f;
    L_0x0055:
        r4 = r3.bitmap;
        if (r4 == 0) goto L_0x0065;
    L_0x0059:
        r4 = r3.imageView;
        r4.setImageDrawable(r0);
        r4 = r3.bitmap;
        r4.recycle();
        r3.bitmap = r0;
    L_0x0065:
        r4 = r3.windowManager;	 Catch:{ Exception -> 0x006c }
        r1 = r3.windowView;	 Catch:{ Exception -> 0x006c }
        r4.removeView(r1);	 Catch:{ Exception -> 0x006c }
    L_0x006c:
        r4 = instance;
        if (r4 != r3) goto L_0x0072;
    L_0x0070:
        instance = r0;
    L_0x0072:
        r3.parentActivity = r0;
        r4 = r3.currentAccount;
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r0 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r4.removeObserver(r3, r0);
    L_0x007f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipRoundVideoView.close(boolean):void");
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

    public void showTemporary(boolean z) {
        if (this.hideShowAnimation != null) {
            this.hideShowAnimation.cancel();
        }
        this.hideShowAnimation = new AnimatorSet();
        AnimatorSet animatorSet = this.hideShowAnimation;
        Animator[] animatorArr = new Animator[3];
        FrameLayout frameLayout = this.windowView;
        String str = "alpha";
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
        frameLayout = this.windowView;
        str = "scaleX";
        fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.8f;
        animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
        FrameLayout frameLayout2 = this.windowView;
        String str2 = "scaleY";
        float[] fArr2 = new float[1];
        if (!z) {
            f = 0.8f;
        }
        fArr2[0] = f;
        animatorArr[2] = ObjectAnimator.ofFloat(frameLayout2, str2, fArr2);
        animatorSet.playTogether(animatorArr);
        this.hideShowAnimation.setDuration(150);
        if (!this.decelerateInterpolator) {
            this.decelerateInterpolator = new DecelerateInterpolator();
        }
        this.hideShowAnimation.addListener(new C12705());
        this.hideShowAnimation.setInterpolator(this.decelerateInterpolator);
        this.hideShowAnimation.start();
    }

    private void runShowHideAnimation(final boolean z) {
        if (this.hideShowAnimation != null) {
            this.hideShowAnimation.cancel();
        }
        this.hideShowAnimation = new AnimatorSet();
        AnimatorSet animatorSet = this.hideShowAnimation;
        Animator[] animatorArr = new Animator[3];
        FrameLayout frameLayout = this.windowView;
        String str = "alpha";
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
        frameLayout = this.windowView;
        str = "scaleX";
        fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.8f;
        animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
        FrameLayout frameLayout2 = this.windowView;
        String str2 = "scaleY";
        float[] fArr2 = new float[1];
        if (!z) {
            f = 0.8f;
        }
        fArr2[0] = f;
        animatorArr[2] = ObjectAnimator.ofFloat(frameLayout2, str2, fArr2);
        animatorSet.playTogether(animatorArr);
        this.hideShowAnimation.setDuration(150);
        if (this.decelerateInterpolator == null) {
            this.decelerateInterpolator = new DecelerateInterpolator();
        }
        this.hideShowAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PipRoundVideoView.this.hideShowAnimation) != null) {
                    if (z == null) {
                        PipRoundVideoView.this.close(false);
                    }
                    PipRoundVideoView.this.hideShowAnimation = null;
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (animator.equals(PipRoundVideoView.this.hideShowAnimation) != null) {
                    PipRoundVideoView.this.hideShowAnimation = null;
                }
            }
        });
        this.hideShowAnimation.setInterpolator(this.decelerateInterpolator);
        this.hideShowAnimation.start();
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
                                    animatorSet.addListener(new C12727());
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
                                animatorSet.addListener(new C12727());
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
                        animatorSet.addListener(new C12727());
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
                animatorSet.addListener(new C12727());
            }
            animatorSet.playTogether(collection);
            animatorSet.start();
        }
    }

    public int getX() {
        return this.windowLayoutParams.x;
    }

    public int getY() {
        return this.windowLayoutParams.y;
    }

    @android.support.annotation.Keep
    public void setX(int r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = r2.windowLayoutParams;
        r0.x = r3;
        r3 = r2.windowManager;	 Catch:{ Exception -> 0x000d }
        r0 = r2.windowView;	 Catch:{ Exception -> 0x000d }
        r1 = r2.windowLayoutParams;	 Catch:{ Exception -> 0x000d }
        r3.updateViewLayout(r0, r1);	 Catch:{ Exception -> 0x000d }
    L_0x000d:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipRoundVideoView.setX(int):void");
    }

    @android.support.annotation.Keep
    public void setY(int r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = r2.windowLayoutParams;
        r0.y = r3;
        r3 = r2.windowManager;	 Catch:{ Exception -> 0x000d }
        r0 = r2.windowView;	 Catch:{ Exception -> 0x000d }
        r1 = r2.windowLayoutParams;	 Catch:{ Exception -> 0x000d }
        r3.updateViewLayout(r0, r1);	 Catch:{ Exception -> 0x000d }
    L_0x000d:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipRoundVideoView.setY(int):void");
    }

    public static PipRoundVideoView getInstance() {
        return instance;
    }
}
