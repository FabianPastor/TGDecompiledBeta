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
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Property;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
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
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;

public class PipRoundVideoView implements NotificationCenter.NotificationCenterDelegate {
    @SuppressLint({"StaticFieldLeak"})
    private static PipRoundVideoView instance;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private Bitmap bitmap;
    private int currentAccount;
    private DecelerateInterpolator decelerateInterpolator;
    /* access modifiers changed from: private */
    public AnimatorSet hideShowAnimation;
    private ImageView imageView;
    /* access modifiers changed from: private */
    public Runnable onCloseRunnable;
    private SharedPreferences preferences;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public TextureView textureView;
    private int videoHeight;
    /* access modifiers changed from: private */
    public int videoWidth;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager windowManager;
    /* access modifiers changed from: private */
    public FrameLayout windowView;

    public void show(Activity activity, Runnable runnable) {
        if (activity != null) {
            instance = this;
            this.onCloseRunnable = runnable;
            AnonymousClass1 r13 = new FrameLayout(activity) {
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
                    MessageObject playingMessageObject;
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
                            WindowManager.LayoutParams access$000 = PipRoundVideoView.this.windowLayoutParams;
                            access$000.x = (int) (((float) access$000.x) + f);
                            WindowManager.LayoutParams access$0002 = PipRoundVideoView.this.windowLayoutParams;
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
                        if (this.startDragging && !this.dragging && (playingMessageObject = MediaController.getInstance().getPlayingMessageObject()) != null) {
                            if (MediaController.getInstance().isMessagePaused()) {
                                MediaController.getInstance().playMessage(playingMessageObject);
                            } else {
                                MediaController.getInstance().lambda$startAudioAgain$7$MediaController(playingMessageObject);
                            }
                        }
                        this.dragging = false;
                        this.startDragging = false;
                        PipRoundVideoView.this.animateToBoundsMaybe();
                    }
                    return true;
                }

                /* access modifiers changed from: protected */
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
            this.windowView = r13;
            r13.setWillNotDraw(false);
            this.videoWidth = AndroidUtilities.dp(126.0f);
            this.videoHeight = AndroidUtilities.dp(126.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                AnonymousClass2 r132 = new AspectRatioFrameLayout(activity) {
                    /* access modifiers changed from: protected */
                    public boolean drawChild(Canvas canvas, View view, long j) {
                        MessageObject playingMessageObject;
                        boolean drawChild = super.drawChild(canvas, view, j);
                        if (view == PipRoundVideoView.this.textureView && (playingMessageObject = MediaController.getInstance().getPlayingMessageObject()) != null) {
                            PipRoundVideoView.this.rect.set(AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), ((float) getMeasuredWidth()) - AndroidUtilities.dpf2(1.5f), ((float) getMeasuredHeight()) - AndroidUtilities.dpf2(1.5f));
                            canvas.drawArc(PipRoundVideoView.this.rect, -90.0f, playingMessageObject.audioProgress * 360.0f, false, Theme.chat_radialProgressPaint);
                        }
                        return drawChild;
                    }
                };
                this.aspectRatioFrameLayout = r132;
                r132.setOutlineProvider(new ViewOutlineProvider(this) {
                    @TargetApi(21)
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f));
                    }
                });
                this.aspectRatioFrameLayout.setClipToOutline(true);
            } else {
                final Paint paint = new Paint(1);
                paint.setColor(-16777216);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                AnonymousClass4 r1 = new AspectRatioFrameLayout(activity) {
                    private Path aspectPath = new Path();

                    /* access modifiers changed from: protected */
                    public void onSizeChanged(int i, int i2, int i3, int i4) {
                        super.onSizeChanged(i, i2, i3, i4);
                        this.aspectPath.reset();
                        float f = (float) (i / 2);
                        this.aspectPath.addCircle(f, (float) (i2 / 2), f, Path.Direction.CW);
                        this.aspectPath.toggleInverseFillType();
                    }

                    /* access modifiers changed from: protected */
                    public void dispatchDraw(Canvas canvas) {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(this.aspectPath, paint);
                    }

                    /* access modifiers changed from: protected */
                    public boolean drawChild(Canvas canvas, View view, long j) {
                        boolean z;
                        MessageObject playingMessageObject;
                        try {
                            z = super.drawChild(canvas, view, j);
                        } catch (Throwable unused) {
                            z = false;
                        }
                        if (view == PipRoundVideoView.this.textureView && (playingMessageObject = MediaController.getInstance().getPlayingMessageObject()) != null) {
                            PipRoundVideoView.this.rect.set(AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), ((float) getMeasuredWidth()) - AndroidUtilities.dpf2(1.5f), ((float) getMeasuredHeight()) - AndroidUtilities.dpf2(1.5f));
                            canvas.drawArc(PipRoundVideoView.this.rect, -90.0f, playingMessageObject.audioProgress * 360.0f, false, Theme.chat_radialProgressPaint);
                        }
                        return z;
                    }
                };
                this.aspectRatioFrameLayout = r1;
                r1.setLayerType(2, (Paint) null);
            }
            this.aspectRatioFrameLayout.setAspectRatio(1.0f, 0);
            this.windowView.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(120, 120.0f, 51, 3.0f, 3.0f, 0.0f, 0.0f));
            this.windowView.setAlpha(1.0f);
            this.windowView.setScaleX(0.8f);
            this.windowView.setScaleY(0.8f);
            this.textureView = new TextureView(activity);
            float dpf2 = (AndroidUtilities.dpf2(120.0f) + AndroidUtilities.dpf2(2.0f)) / AndroidUtilities.dpf2(120.0f);
            this.textureView.setScaleX(dpf2);
            this.textureView.setScaleY(dpf2);
            this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
            ImageView imageView2 = new ImageView(activity);
            this.imageView = imageView2;
            this.aspectRatioFrameLayout.addView(imageView2, LayoutHelper.createFrame(-1, -1.0f));
            this.imageView.setVisibility(4);
            this.windowManager = (WindowManager) activity.getSystemService("window");
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
            this.preferences = sharedPreferences;
            int i = sharedPreferences.getInt("sidex", 1);
            int i2 = this.preferences.getInt("sidey", 0);
            float f = this.preferences.getFloat("px", 0.0f);
            float f2 = this.preferences.getFloat("py", 0.0f);
            try {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                this.windowLayoutParams = layoutParams;
                layoutParams.width = this.videoWidth;
                layoutParams.height = this.videoHeight;
                layoutParams.x = getSideCoord(true, i, f, this.videoWidth);
                this.windowLayoutParams.y = getSideCoord(false, i2, f2, this.videoHeight);
                this.windowLayoutParams.format = -3;
                this.windowLayoutParams.gravity = 51;
                this.windowLayoutParams.type = 99;
                this.windowLayoutParams.flags = 16777736;
                this.windowManager.addView(this.windowView, this.windowLayoutParams);
                int i3 = UserConfig.selectedAccount;
                this.currentAccount = i3;
                NotificationCenter.getInstance(i3).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
                runShowHideAnimation(true);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private static int getSideCoord(boolean z, int i, float f, int i2) {
        int i3;
        int i4;
        if (z) {
            i3 = AndroidUtilities.displaySize.x;
        } else {
            i3 = AndroidUtilities.displaySize.y - i2;
            i2 = ActionBar.getCurrentActionBarHeight();
        }
        int i5 = i3 - i2;
        if (i == 0) {
            i4 = AndroidUtilities.dp(10.0f);
        } else if (i == 1) {
            i4 = i5 - AndroidUtilities.dp(10.0f);
        } else {
            i4 = Math.round(((float) (i5 - AndroidUtilities.dp(20.0f))) * f) + AndroidUtilities.dp(10.0f);
        }
        return !z ? i4 + ActionBar.getCurrentActionBarHeight() : i4;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        AspectRatioFrameLayout aspectRatioFrameLayout2;
        if (i == NotificationCenter.messagePlayingProgressDidChanged && (aspectRatioFrameLayout2 = this.aspectRatioFrameLayout) != null) {
            aspectRatioFrameLayout2.invalidate();
        }
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public void close(boolean z) {
        if (z) {
            TextureView textureView2 = this.textureView;
            if (textureView2 != null && textureView2.getParent() != null) {
                if (this.textureView.getWidth() > 0 && this.textureView.getHeight() > 0) {
                    this.bitmap = Bitmaps.createBitmap(this.textureView.getWidth(), this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
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
            this.imageView.setImageDrawable((Drawable) null);
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
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.hideShowAnimation = animatorSet2;
        Animator[] animatorArr = new Animator[3];
        FrameLayout frameLayout = this.windowView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
        FrameLayout frameLayout2 = this.windowView;
        Property property2 = View.SCALE_X;
        float[] fArr2 = new float[1];
        fArr2[0] = z ? 1.0f : 0.8f;
        animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
        FrameLayout frameLayout3 = this.windowView;
        Property property3 = View.SCALE_Y;
        float[] fArr3 = new float[1];
        if (!z) {
            f = 0.8f;
        }
        fArr3[0] = f;
        animatorArr[2] = ObjectAnimator.ofFloat(frameLayout3, property3, fArr3);
        animatorSet2.playTogether(animatorArr);
        this.hideShowAnimation.setDuration(150);
        if (this.decelerateInterpolator == null) {
            this.decelerateInterpolator = new DecelerateInterpolator();
        }
        this.hideShowAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PipRoundVideoView.this.hideShowAnimation)) {
                    AnimatorSet unused = PipRoundVideoView.this.hideShowAnimation = null;
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
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.hideShowAnimation = animatorSet2;
        Animator[] animatorArr = new Animator[3];
        FrameLayout frameLayout = this.windowView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
        FrameLayout frameLayout2 = this.windowView;
        Property property2 = View.SCALE_X;
        float[] fArr2 = new float[1];
        fArr2[0] = z ? 1.0f : 0.8f;
        animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
        FrameLayout frameLayout3 = this.windowView;
        Property property3 = View.SCALE_Y;
        float[] fArr3 = new float[1];
        if (!z) {
            f = 0.8f;
        }
        fArr3[0] = f;
        animatorArr[2] = ObjectAnimator.ofFloat(frameLayout3, property3, fArr3);
        animatorSet2.playTogether(animatorArr);
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
                    AnimatorSet unused = PipRoundVideoView.this.hideShowAnimation = null;
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (animator.equals(PipRoundVideoView.this.hideShowAnimation)) {
                    AnimatorSet unused = PipRoundVideoView.this.hideShowAnimation = null;
                }
            }
        });
        this.hideShowAnimation.setInterpolator(this.decelerateInterpolator);
        this.hideShowAnimation.start();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0113  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x017e  */
    /* JADX WARNING: Removed duplicated region for block: B:53:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void animateToBoundsMaybe() {
        /*
            r16 = this;
            r0 = r16
            int r1 = r0.videoWidth
            r2 = 0
            r3 = 0
            r4 = 1
            int r1 = getSideCoord(r4, r3, r2, r1)
            int r5 = r0.videoWidth
            int r5 = getSideCoord(r4, r4, r2, r5)
            int r6 = r0.videoHeight
            int r6 = getSideCoord(r3, r3, r2, r6)
            int r7 = r0.videoHeight
            int r7 = getSideCoord(r3, r4, r2, r7)
            android.content.SharedPreferences r8 = r0.preferences
            android.content.SharedPreferences$Editor r8 = r8.edit()
            r9 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            android.view.WindowManager$LayoutParams r10 = r0.windowLayoutParams
            int r10 = r10.x
            int r10 = r1 - r10
            int r10 = java.lang.Math.abs(r10)
            java.lang.String r12 = "sidex"
            java.lang.String r13 = "x"
            r14 = 1065353216(0x3var_, float:1.0)
            if (r10 <= r9) goto L_0x00e3
            android.view.WindowManager$LayoutParams r10 = r0.windowLayoutParams
            int r10 = r10.x
            if (r10 >= 0) goto L_0x004b
            int r15 = r0.videoWidth
            int r15 = -r15
            int r15 = r15 / 4
            if (r10 <= r15) goto L_0x004b
            goto L_0x00e3
        L_0x004b:
            android.view.WindowManager$LayoutParams r10 = r0.windowLayoutParams
            int r10 = r10.x
            int r10 = r5 - r10
            int r10 = java.lang.Math.abs(r10)
            if (r10 <= r9) goto L_0x00b6
            android.view.WindowManager$LayoutParams r10 = r0.windowLayoutParams
            int r10 = r10.x
            android.graphics.Point r15 = org.telegram.messenger.AndroidUtilities.displaySize
            int r15 = r15.x
            int r2 = r0.videoWidth
            int r11 = r15 - r2
            if (r10 <= r11) goto L_0x006d
            int r2 = r2 / 4
            int r2 = r2 * 3
            int r15 = r15 - r2
            if (r10 >= r15) goto L_0x006d
            goto L_0x00b6
        L_0x006d:
            android.widget.FrameLayout r2 = r0.windowView
            float r2 = r2.getAlpha()
            int r2 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r2 == 0) goto L_0x00a2
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            android.view.WindowManager$LayoutParams r2 = r0.windowLayoutParams
            int r2 = r2.x
            if (r2 >= 0) goto L_0x0091
            int[] r2 = new int[r4]
            int r5 = r0.videoWidth
            int r5 = -r5
            r2[r3] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofInt(r0, r13, r2)
            r1.add(r2)
            goto L_0x00a0
        L_0x0091:
            int[] r2 = new int[r4]
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r5.x
            r2[r3] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofInt(r0, r13, r2)
            r1.add(r2)
        L_0x00a0:
            r2 = 1
            goto L_0x0111
        L_0x00a2:
            android.view.WindowManager$LayoutParams r2 = r0.windowLayoutParams
            int r2 = r2.x
            int r2 = r2 - r1
            float r2 = (float) r2
            int r5 = r5 - r1
            float r1 = (float) r5
            float r2 = r2 / r1
            java.lang.String r1 = "px"
            r8.putFloat(r1, r2)
            r1 = 2
            r8.putInt(r12, r1)
            r1 = 0
            goto L_0x0110
        L_0x00b6:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.putInt(r12, r4)
            android.widget.FrameLayout r2 = r0.windowView
            float r2 = r2.getAlpha()
            int r2 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r2 == 0) goto L_0x00d7
            android.widget.FrameLayout r2 = r0.windowView
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r4]
            r11[r3] = r14
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r10, r11)
            r1.add(r2)
        L_0x00d7:
            int[] r2 = new int[r4]
            r2[r3] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofInt(r0, r13, r2)
            r1.add(r2)
            goto L_0x0110
        L_0x00e3:
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r8.putInt(r12, r3)
            android.widget.FrameLayout r5 = r0.windowView
            float r5 = r5.getAlpha()
            int r5 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r5 == 0) goto L_0x0104
            android.widget.FrameLayout r5 = r0.windowView
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r4]
            r11[r3] = r14
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r10, r11)
            r2.add(r5)
        L_0x0104:
            int[] r5 = new int[r4]
            r5[r3] = r1
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofInt(r0, r13, r5)
            r2.add(r1)
            r1 = r2
        L_0x0110:
            r2 = 0
        L_0x0111:
            if (r2 != 0) goto L_0x017c
            android.view.WindowManager$LayoutParams r5 = r0.windowLayoutParams
            int r5 = r5.y
            int r5 = r6 - r5
            int r5 = java.lang.Math.abs(r5)
            java.lang.String r10 = "y"
            java.lang.String r11 = "sidey"
            if (r5 <= r9) goto L_0x0164
            android.view.WindowManager$LayoutParams r5 = r0.windowLayoutParams
            int r5 = r5.y
            int r12 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            if (r5 > r12) goto L_0x012f
            goto L_0x0164
        L_0x012f:
            android.view.WindowManager$LayoutParams r5 = r0.windowLayoutParams
            int r5 = r5.y
            int r5 = r7 - r5
            int r5 = java.lang.Math.abs(r5)
            if (r5 > r9) goto L_0x0151
            if (r1 != 0) goto L_0x0142
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
        L_0x0142:
            r8.putInt(r11, r4)
            int[] r5 = new int[r4]
            r5[r3] = r7
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofInt(r0, r10, r5)
            r1.add(r5)
            goto L_0x0179
        L_0x0151:
            android.view.WindowManager$LayoutParams r5 = r0.windowLayoutParams
            int r5 = r5.y
            int r5 = r5 - r6
            float r5 = (float) r5
            int r7 = r7 - r6
            float r6 = (float) r7
            float r5 = r5 / r6
            java.lang.String r6 = "py"
            r8.putFloat(r6, r5)
            r5 = 2
            r8.putInt(r11, r5)
            goto L_0x0179
        L_0x0164:
            if (r1 != 0) goto L_0x016b
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
        L_0x016b:
            r8.putInt(r11, r3)
            int[] r5 = new int[r4]
            r5[r3] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofInt(r0, r10, r5)
            r1.add(r5)
        L_0x0179:
            r8.commit()
        L_0x017c:
            if (r1 == 0) goto L_0x01b8
            android.view.animation.DecelerateInterpolator r5 = r0.decelerateInterpolator
            if (r5 != 0) goto L_0x0189
            android.view.animation.DecelerateInterpolator r5 = new android.view.animation.DecelerateInterpolator
            r5.<init>()
            r0.decelerateInterpolator = r5
        L_0x0189:
            android.animation.AnimatorSet r5 = new android.animation.AnimatorSet
            r5.<init>()
            android.view.animation.DecelerateInterpolator r6 = r0.decelerateInterpolator
            r5.setInterpolator(r6)
            r6 = 150(0x96, double:7.4E-322)
            r5.setDuration(r6)
            if (r2 == 0) goto L_0x01b2
            android.widget.FrameLayout r2 = r0.windowView
            android.util.Property r6 = android.view.View.ALPHA
            float[] r4 = new float[r4]
            r7 = 0
            r4[r3] = r7
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r6, r4)
            r1.add(r2)
            org.telegram.ui.Components.PipRoundVideoView$7 r2 = new org.telegram.ui.Components.PipRoundVideoView$7
            r2.<init>()
            r5.addListener(r2)
        L_0x01b2:
            r5.playTogether(r1)
            r5.start()
        L_0x01b8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipRoundVideoView.animateToBoundsMaybe():void");
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
        try {
            this.windowManager.updateViewLayout(this.windowView, layoutParams);
        } catch (Exception unused) {
        }
    }

    @Keep
    public void setY(int i) {
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
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
