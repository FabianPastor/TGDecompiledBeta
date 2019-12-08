package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.Property;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.Keep;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
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
                        float f = rawX - this.startX;
                        float f2 = rawY - this.startY;
                        if (this.startDragging) {
                            if (Math.abs(f) >= AndroidUtilities.getPixelsInCM(0.3f, true) || Math.abs(f2) >= AndroidUtilities.getPixelsInCM(0.3f, false)) {
                                this.dragging = true;
                                this.startDragging = false;
                            }
                        } else if (this.dragging) {
                            LayoutParams access$000 = PipRoundVideoView.this.windowLayoutParams;
                            access$000.x = (int) (((float) access$000.x) + f);
                            LayoutParams access$0002 = PipRoundVideoView.this.windowLayoutParams;
                            access$0002.y = (int) (((float) access$0002.y) + f2);
                            int access$100 = PipRoundVideoView.this.videoWidth / 2;
                            int i = -access$100;
                            if (PipRoundVideoView.this.windowLayoutParams.x < i) {
                                PipRoundVideoView.this.windowLayoutParams.x = i;
                            } else if (PipRoundVideoView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - PipRoundVideoView.this.windowLayoutParams.width) + access$100) {
                                PipRoundVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipRoundVideoView.this.windowLayoutParams.width) + access$100;
                            }
                            float f3 = 1.0f;
                            if (PipRoundVideoView.this.windowLayoutParams.x < 0) {
                                f3 = 1.0f + ((((float) PipRoundVideoView.this.windowLayoutParams.x) / ((float) access$100)) * 0.5f);
                            } else if (PipRoundVideoView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - PipRoundVideoView.this.windowLayoutParams.width) {
                                f3 = 1.0f - ((((float) ((PipRoundVideoView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + PipRoundVideoView.this.windowLayoutParams.width)) / ((float) access$100)) * 0.5f);
                            }
                            if (PipRoundVideoView.this.windowView.getAlpha() != f3) {
                                PipRoundVideoView.this.windowView.setAlpha(f3);
                            }
                            if (PipRoundVideoView.this.windowLayoutParams.y < 0) {
                                PipRoundVideoView.this.windowLayoutParams.y = 0;
                            } else if (PipRoundVideoView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - PipRoundVideoView.this.windowLayoutParams.height) + 0) {
                                PipRoundVideoView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - PipRoundVideoView.this.windowLayoutParams.height) + 0;
                            }
                            PipRoundVideoView.this.windowManager.updateViewLayout(PipRoundVideoView.this.windowView, PipRoundVideoView.this.windowLayoutParams);
                            this.startX = rawX;
                            this.startY = rawY;
                        }
                    } else if (motionEvent.getAction() == 1) {
                        if (this.startDragging && !this.dragging) {
                            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                            if (playingMessageObject != null) {
                                if (MediaController.getInstance().isMessagePaused()) {
                                    MediaController.getInstance().playMessage(playingMessageObject);
                                } else {
                                    MediaController.getInstance().lambda$startAudioAgain$5$MediaController(playingMessageObject);
                                }
                            }
                        }
                        this.dragging = false;
                        this.startDragging = false;
                        PipRoundVideoView.this.animateToBoundsMaybe();
                    }
                    return true;
                }

                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    Drawable drawable = Theme.chat_roundVideoShadow;
                    if (drawable != null) {
                        drawable.setAlpha((int) (getAlpha() * 255.0f));
                        Theme.chat_roundVideoShadow.setBounds(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(125.0f), AndroidUtilities.dp(125.0f));
                        Theme.chat_roundVideoShadow.draw(canvas);
                        Theme.chat_docBackPaint.setColor(Theme.getColor("chat_inBubble"));
                        Theme.chat_docBackPaint.setAlpha((int) (getAlpha() * 255.0f));
                        canvas.drawCircle((float) AndroidUtilities.dp(63.0f), (float) AndroidUtilities.dp(63.0f), (float) AndroidUtilities.dp(59.5f), Theme.chat_docBackPaint);
                    }
                }
            };
            this.windowView.setWillNotDraw(false);
            this.videoWidth = AndroidUtilities.dp(126.0f);
            this.videoHeight = AndroidUtilities.dp(126.0f);
            if (VERSION.SDK_INT >= 21) {
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(activity) {
                    /* Access modifiers changed, original: protected */
                    public boolean drawChild(Canvas canvas, View view, long j) {
                        boolean drawChild = super.drawChild(canvas, view, j);
                        if (view == PipRoundVideoView.this.textureView) {
                            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                            if (playingMessageObject != null) {
                                PipRoundVideoView.this.rect.set(AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), ((float) getMeasuredWidth()) - AndroidUtilities.dpf2(1.5f), ((float) getMeasuredHeight()) - AndroidUtilities.dpf2(1.5f));
                                canvas.drawArc(PipRoundVideoView.this.rect, -90.0f, playingMessageObject.audioProgress * 360.0f, false, Theme.chat_radialProgressPaint);
                            }
                        }
                        return drawChild;
                    }
                };
                this.aspectRatioFrameLayout.setOutlineProvider(new ViewOutlineProvider() {
                    @TargetApi(21)
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f));
                    }
                });
                this.aspectRatioFrameLayout.setClipToOutline(true);
            } else {
                final Paint paint = new Paint(1);
                paint.setColor(-16777216);
                paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(activity) {
                    private Path aspectPath = new Path();

                    /* Access modifiers changed, original: protected */
                    public void onSizeChanged(int i, int i2, int i3, int i4) {
                        super.onSizeChanged(i, i2, i3, i4);
                        this.aspectPath.reset();
                        float f = (float) (i / 2);
                        this.aspectPath.addCircle(f, (float) (i2 / 2), f, Direction.CW);
                        this.aspectPath.toggleInverseFillType();
                    }

                    /* Access modifiers changed, original: protected */
                    public void dispatchDraw(Canvas canvas) {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(this.aspectPath, paint);
                    }

                    /* Access modifiers changed, original: protected */
                    public boolean drawChild(Canvas canvas, View view, long j) {
                        boolean drawChild;
                        try {
                            drawChild = super.drawChild(canvas, view, j);
                        } catch (Throwable unused) {
                            drawChild = false;
                        }
                        if (view == PipRoundVideoView.this.textureView) {
                            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                            if (playingMessageObject != null) {
                                PipRoundVideoView.this.rect.set(AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), ((float) getMeasuredWidth()) - AndroidUtilities.dpf2(1.5f), ((float) getMeasuredHeight()) - AndroidUtilities.dpf2(1.5f));
                                canvas.drawArc(PipRoundVideoView.this.rect, -90.0f, playingMessageObject.audioProgress * 360.0f, false, Theme.chat_radialProgressPaint);
                            }
                        }
                        return drawChild;
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
            int i = this.preferences.getInt("sidex", 1);
            int i2 = this.preferences.getInt("sidey", 0);
            float f = this.preferences.getFloat("px", 0.0f);
            float f2 = this.preferences.getFloat("py", 0.0f);
            try {
                this.windowLayoutParams = new LayoutParams();
                this.windowLayoutParams.width = this.videoWidth;
                this.windowLayoutParams.height = this.videoHeight;
                this.windowLayoutParams.x = getSideCoord(true, i, f, this.videoWidth);
                this.windowLayoutParams.y = getSideCoord(false, i2, f2, this.videoHeight);
                this.windowLayoutParams.format = -3;
                this.windowLayoutParams.gravity = 51;
                this.windowLayoutParams.type = 99;
                this.windowLayoutParams.flags = 16777736;
                this.windowManager.addView(this.windowView, this.windowLayoutParams);
                this.parentActivity = activity;
                this.currentAccount = UserConfig.selectedAccount;
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
                runShowHideAnimation(true);
            } catch (Exception e) {
                FileLog.e(e);
            }
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
            if (aspectRatioFrameLayout != null) {
                aspectRatioFrameLayout.invalidate();
            }
        }
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public void close(boolean z) {
        if (z) {
            TextureView textureView = this.textureView;
            if (textureView != null && textureView.getParent() != null) {
                if (this.textureView.getWidth() > 0 && this.textureView.getHeight() > 0) {
                    this.bitmap = Bitmaps.createBitmap(this.textureView.getWidth(), this.textureView.getHeight(), Config.ARGB_8888);
                }
                try {
                    this.textureView.getBitmap(this.bitmap);
                } catch (Throwable unused) {
                    this.bitmap = null;
                }
                this.imageView.setImageBitmap(this.bitmap);
                try {
                    this.aspectRatioFrameLayout.removeView(this.textureView);
                } catch (Exception unused2) {
                }
                this.imageView.setVisibility(0);
                runShowHideAnimation(false);
                return;
            }
            return;
        }
        if (this.bitmap != null) {
            this.imageView.setImageDrawable(null);
            this.bitmap.recycle();
            this.bitmap = null;
        }
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception unused3) {
        }
        if (instance == this) {
            instance = null;
        }
        this.parentActivity = null;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
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
        AnimatorSet animatorSet = this.hideShowAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.hideShowAnimation = new AnimatorSet();
        animatorSet = this.hideShowAnimation;
        Animator[] animatorArr = new Animator[3];
        FrameLayout frameLayout = this.windowView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
        frameLayout = this.windowView;
        property = View.SCALE_X;
        fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.8f;
        animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
        FrameLayout frameLayout2 = this.windowView;
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[1];
        if (!z) {
            f = 0.8f;
        }
        fArr2[0] = f;
        animatorArr[2] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
        animatorSet.playTogether(animatorArr);
        this.hideShowAnimation.setDuration(150);
        if (this.decelerateInterpolator == null) {
            this.decelerateInterpolator = new DecelerateInterpolator();
        }
        this.hideShowAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PipRoundVideoView.this.hideShowAnimation)) {
                    PipRoundVideoView.this.hideShowAnimation = null;
                }
            }
        });
        this.hideShowAnimation.setInterpolator(this.decelerateInterpolator);
        this.hideShowAnimation.start();
    }

    private void runShowHideAnimation(final boolean z) {
        AnimatorSet animatorSet = this.hideShowAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.hideShowAnimation = new AnimatorSet();
        animatorSet = this.hideShowAnimation;
        Animator[] animatorArr = new Animator[3];
        FrameLayout frameLayout = this.windowView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
        frameLayout = this.windowView;
        property = View.SCALE_X;
        fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.8f;
        animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
        FrameLayout frameLayout2 = this.windowView;
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[1];
        if (!z) {
            f = 0.8f;
        }
        fArr2[0] = f;
        animatorArr[2] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
        animatorSet.playTogether(animatorArr);
        this.hideShowAnimation.setDuration(150);
        if (this.decelerateInterpolator == null) {
            this.decelerateInterpolator = new DecelerateInterpolator();
        }
        this.hideShowAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PipRoundVideoView.this.hideShowAnimation)) {
                    if (!z) {
                        PipRoundVideoView.this.close(false);
                    }
                    PipRoundVideoView.this.hideShowAnimation = null;
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (animator.equals(PipRoundVideoView.this.hideShowAnimation)) {
                    PipRoundVideoView.this.hideShowAnimation = null;
                }
            }
        });
        this.hideShowAnimation.setInterpolator(this.decelerateInterpolator);
        this.hideShowAnimation.start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0113  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x017d  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0113  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x017d  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0113  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x017d  */
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
        r12 = "sidex";
        r13 = "x";
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r10 <= r9) goto L_0x00e4;
    L_0x003b:
        r10 = r0.windowLayoutParams;
        r10 = r10.x;
        if (r10 >= 0) goto L_0x004a;
    L_0x0041:
        r15 = r0.videoWidth;
        r15 = -r15;
        r15 = r15 / 4;
        if (r10 <= r15) goto L_0x004a;
    L_0x0048:
        goto L_0x00e4;
    L_0x004a:
        r10 = r0.windowLayoutParams;
        r10 = r10.x;
        r10 = r5 - r10;
        r10 = java.lang.Math.abs(r10);
        if (r10 <= r9) goto L_0x00b6;
    L_0x0056:
        r10 = r0.windowLayoutParams;
        r10 = r10.x;
        r15 = org.telegram.messenger.AndroidUtilities.displaySize;
        r15 = r15.x;
        r2 = r0.videoWidth;
        r11 = r15 - r2;
        if (r10 <= r11) goto L_0x006c;
    L_0x0064:
        r2 = r2 / 4;
        r2 = r2 * 3;
        r15 = r15 - r2;
        if (r10 >= r15) goto L_0x006c;
    L_0x006b:
        goto L_0x00b6;
    L_0x006c:
        r2 = r0.windowView;
        r2 = r2.getAlpha();
        r2 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1));
        if (r2 == 0) goto L_0x00a2;
    L_0x0076:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = r0.windowLayoutParams;
        r2 = r2.x;
        if (r2 >= 0) goto L_0x0090;
    L_0x0081:
        r2 = new int[r4];
        r5 = r0.videoWidth;
        r5 = -r5;
        r2[r3] = r5;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r13, r2);
        r1.add(r2);
        goto L_0x009f;
    L_0x0090:
        r2 = new int[r4];
        r5 = org.telegram.messenger.AndroidUtilities.displaySize;
        r5 = r5.x;
        r2[r3] = r5;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r13, r2);
        r1.add(r2);
    L_0x009f:
        r2 = r1;
        r1 = 1;
        goto L_0x0111;
    L_0x00a2:
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
        r8.putInt(r12, r1);
        r1 = 0;
        goto L_0x00e2;
    L_0x00b6:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r8.putInt(r12, r4);
        r2 = r0.windowView;
        r2 = r2.getAlpha();
        r2 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1));
        if (r2 == 0) goto L_0x00d7;
    L_0x00c8:
        r2 = r0.windowView;
        r10 = android.view.View.ALPHA;
        r11 = new float[r4];
        r11[r3] = r14;
        r2 = android.animation.ObjectAnimator.ofFloat(r2, r10, r11);
        r1.add(r2);
    L_0x00d7:
        r2 = new int[r4];
        r2[r3] = r5;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r13, r2);
        r1.add(r2);
    L_0x00e2:
        r2 = r1;
        goto L_0x0110;
    L_0x00e4:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r8.putInt(r12, r3);
        r5 = r0.windowView;
        r5 = r5.getAlpha();
        r5 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1));
        if (r5 == 0) goto L_0x0105;
    L_0x00f6:
        r5 = r0.windowView;
        r10 = android.view.View.ALPHA;
        r11 = new float[r4];
        r11[r3] = r14;
        r5 = android.animation.ObjectAnimator.ofFloat(r5, r10, r11);
        r2.add(r5);
    L_0x0105:
        r5 = new int[r4];
        r5[r3] = r1;
        r1 = android.animation.ObjectAnimator.ofInt(r0, r13, r5);
        r2.add(r1);
    L_0x0110:
        r1 = 0;
    L_0x0111:
        if (r1 != 0) goto L_0x017b;
    L_0x0113:
        r5 = r0.windowLayoutParams;
        r5 = r5.y;
        r5 = r6 - r5;
        r5 = java.lang.Math.abs(r5);
        r10 = "y";
        r11 = "sidey";
        if (r5 <= r9) goto L_0x0163;
    L_0x0123:
        r5 = r0.windowLayoutParams;
        r5 = r5.y;
        r12 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
        if (r5 > r12) goto L_0x012e;
    L_0x012d:
        goto L_0x0163;
    L_0x012e:
        r5 = r0.windowLayoutParams;
        r5 = r5.y;
        r5 = r7 - r5;
        r5 = java.lang.Math.abs(r5);
        if (r5 > r9) goto L_0x0150;
    L_0x013a:
        if (r2 != 0) goto L_0x0141;
    L_0x013c:
        r2 = new java.util.ArrayList;
        r2.<init>();
    L_0x0141:
        r8.putInt(r11, r4);
        r5 = new int[r4];
        r5[r3] = r7;
        r5 = android.animation.ObjectAnimator.ofInt(r0, r10, r5);
        r2.add(r5);
        goto L_0x0178;
    L_0x0150:
        r5 = r0.windowLayoutParams;
        r5 = r5.y;
        r5 = r5 - r6;
        r5 = (float) r5;
        r7 = r7 - r6;
        r6 = (float) r7;
        r5 = r5 / r6;
        r6 = "py";
        r8.putFloat(r6, r5);
        r5 = 2;
        r8.putInt(r11, r5);
        goto L_0x0178;
    L_0x0163:
        if (r2 != 0) goto L_0x016a;
    L_0x0165:
        r2 = new java.util.ArrayList;
        r2.<init>();
    L_0x016a:
        r8.putInt(r11, r3);
        r5 = new int[r4];
        r5[r3] = r6;
        r5 = android.animation.ObjectAnimator.ofInt(r0, r10, r5);
        r2.add(r5);
    L_0x0178:
        r8.commit();
    L_0x017b:
        if (r2 == 0) goto L_0x01b7;
    L_0x017d:
        r5 = r0.decelerateInterpolator;
        if (r5 != 0) goto L_0x0188;
    L_0x0181:
        r5 = new android.view.animation.DecelerateInterpolator;
        r5.<init>();
        r0.decelerateInterpolator = r5;
    L_0x0188:
        r5 = new android.animation.AnimatorSet;
        r5.<init>();
        r6 = r0.decelerateInterpolator;
        r5.setInterpolator(r6);
        r6 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r5.setDuration(r6);
        if (r1 == 0) goto L_0x01b1;
    L_0x0199:
        r1 = r0.windowView;
        r6 = android.view.View.ALPHA;
        r4 = new float[r4];
        r7 = 0;
        r4[r3] = r7;
        r1 = android.animation.ObjectAnimator.ofFloat(r1, r6, r4);
        r2.add(r1);
        r1 = new org.telegram.ui.Components.PipRoundVideoView$7;
        r1.<init>();
        r5.addListener(r1);
    L_0x01b1:
        r5.playTogether(r2);
        r5.start();
    L_0x01b7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipRoundVideoView.animateToBoundsMaybe():void");
    }

    public int getX() {
        return this.windowLayoutParams.x;
    }

    public int getY() {
        return this.windowLayoutParams.y;
    }

    @Keep
    public void setX(int i) {
        LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.x = i;
        try {
            this.windowManager.updateViewLayout(this.windowView, layoutParams);
        } catch (Exception unused) {
        }
    }

    @Keep
    public void setY(int i) {
        LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.y = i;
        try {
            this.windowManager.updateViewLayout(this.windowView, layoutParams);
        } catch (Exception unused) {
        }
    }

    public static PipRoundVideoView getInstance() {
        return instance;
    }
}
