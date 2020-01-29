package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.StateSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class Switch extends View {
    private boolean attachedToWindow;
    private boolean bitmapsCreated;
    /* access modifiers changed from: private */
    public ObjectAnimator checkAnimator;
    private int colorSet;
    private int drawIconType;
    private boolean drawRipple;
    /* access modifiers changed from: private */
    public ObjectAnimator iconAnimator;
    private Drawable iconDrawable;
    private float iconProgress = 1.0f;
    private boolean isChecked;
    private int lastIconColor;
    private OnCheckedChangeListener onCheckedChangeListener;
    private Bitmap[] overlayBitmap;
    private Canvas[] overlayCanvas;
    private float overlayCx;
    private float overlayCy;
    private Paint overlayEraserPaint;
    private Bitmap overlayMaskBitmap;
    private Canvas overlayMaskCanvas;
    private Paint overlayMaskPaint;
    private float overlayRad;
    private int overrideColorProgress;
    private Paint paint = new Paint(1);
    private Paint paint2 = new Paint(1);
    private int[] pressedState = {16842910, 16842919};
    private float progress;
    private RectF rectF = new RectF();
    private RippleDrawable rippleDrawable;
    /* access modifiers changed from: private */
    public Paint ripplePaint;
    private String thumbCheckedColorKey = "windowBackgroundWhite";
    private String thumbColorKey = "windowBackgroundWhite";
    private String trackCheckedColorKey = "switch2TrackChecked";
    private String trackColorKey = "switch2Track";

    public interface OnCheckedChangeListener {
        void onCheckedChanged(Switch switchR, boolean z);
    }

    public Switch(Context context) {
        super(context);
        this.paint2.setStyle(Paint.Style.STROKE);
        this.paint2.setStrokeCap(Paint.Cap.ROUND);
        this.paint2.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
        }
    }

    @Keep
    public float getProgress() {
        return this.progress;
    }

    @Keep
    public void setIconProgress(float f) {
        if (this.iconProgress != f) {
            this.iconProgress = f;
            invalidate();
        }
    }

    @Keep
    public float getIconProgress() {
        return this.iconProgress;
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void cancelIconAnimator() {
        ObjectAnimator objectAnimator = this.iconAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.iconAnimator = null;
        }
    }

    public void setDrawIconType(int i) {
        this.drawIconType = i;
    }

    public void setDrawRipple(boolean z) {
        AnonymousClass1 r0;
        if (Build.VERSION.SDK_INT >= 21 && z != this.drawRipple) {
            this.drawRipple = z;
            int i = 1;
            if (this.rippleDrawable == null) {
                this.ripplePaint = new Paint(1);
                this.ripplePaint.setColor(-1);
                if (Build.VERSION.SDK_INT >= 23) {
                    r0 = null;
                } else {
                    r0 = new Drawable() {
                        public int getOpacity() {
                            return 0;
                        }

                        public void setAlpha(int i) {
                        }

                        public void setColorFilter(ColorFilter colorFilter) {
                        }

                        public void draw(Canvas canvas) {
                            Rect bounds = getBounds();
                            canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) AndroidUtilities.dp(18.0f), Switch.this.ripplePaint);
                        }
                    };
                }
                this.rippleDrawable = new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{0}), (Drawable) null, r0);
                if (Build.VERSION.SDK_INT >= 23) {
                    this.rippleDrawable.setRadius(AndroidUtilities.dp(18.0f));
                }
                this.rippleDrawable.setCallback(this);
            }
            if ((this.isChecked && this.colorSet != 2) || (!this.isChecked && this.colorSet != 1)) {
                this.rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{Theme.getColor(this.isChecked ? "switchTrackBlueSelectorChecked" : "switchTrackBlueSelector")}));
                if (this.isChecked) {
                    i = 2;
                }
                this.colorSet = i;
            }
            if (Build.VERSION.SDK_INT >= 28 && z) {
                this.rippleDrawable.setHotspot(this.isChecked ? 0.0f : (float) AndroidUtilities.dp(100.0f), (float) AndroidUtilities.dp(18.0f));
            }
            this.rippleDrawable.setState(z ? this.pressedState : StateSet.NOTHING);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.rippleDrawable;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean verifyDrawable(android.graphics.drawable.Drawable r2) {
        /*
            r1 = this;
            boolean r0 = super.verifyDrawable(r2)
            if (r0 != 0) goto L_0x000f
            android.graphics.drawable.RippleDrawable r0 = r1.rippleDrawable
            if (r0 == 0) goto L_0x000d
            if (r2 != r0) goto L_0x000d
            goto L_0x000f
        L_0x000d:
            r2 = 0
            goto L_0x0010
        L_0x000f:
            r2 = 1
        L_0x0010:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Switch.verifyDrawable(android.graphics.drawable.Drawable):boolean");
    }

    public void setColors(String str, String str2, String str3, String str4) {
        this.trackColorKey = str;
        this.trackCheckedColorKey = str2;
        this.thumbColorKey = str3;
        this.thumbCheckedColorKey = str4;
    }

    private void animateToCheckedState(boolean z) {
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator.setDuration(250);
        this.checkAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ObjectAnimator unused = Switch.this.checkAnimator = null;
            }
        });
        this.checkAnimator.start();
    }

    private void animateIcon(boolean z) {
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        this.iconAnimator = ObjectAnimator.ofFloat(this, "iconProgress", fArr);
        this.iconAnimator.setDuration(250);
        this.iconAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ObjectAnimator unused = Switch.this.iconAnimator = null;
            }
        });
        this.iconAnimator.start();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener2) {
        this.onCheckedChangeListener = onCheckedChangeListener2;
    }

    public void setChecked(boolean z, boolean z2) {
        setChecked(z, this.drawIconType, z2);
    }

    public void setChecked(boolean z, int i, boolean z2) {
        float f = 1.0f;
        if (z != this.isChecked) {
            this.isChecked = z;
            if (!this.attachedToWindow || !z2) {
                cancelCheckAnimator();
                setProgress(z ? 1.0f : 0.0f);
            } else {
                animateToCheckedState(z);
            }
            OnCheckedChangeListener onCheckedChangeListener2 = this.onCheckedChangeListener;
            if (onCheckedChangeListener2 != null) {
                onCheckedChangeListener2.onCheckedChanged(this, z);
            }
        }
        if (this.drawIconType != i) {
            this.drawIconType = i;
            if (!this.attachedToWindow || !z2) {
                cancelIconAnimator();
                if (i != 0) {
                    f = 0.0f;
                }
                setIconProgress(f);
                return;
            }
            animateIcon(i == 0);
        }
    }

    public void setIcon(int i) {
        if (i != 0) {
            this.iconDrawable = getResources().getDrawable(i).mutate();
            Drawable drawable = this.iconDrawable;
            if (drawable != null) {
                int color = Theme.getColor(this.isChecked ? this.trackCheckedColorKey : this.trackColorKey);
                this.lastIconColor = color;
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                return;
            }
            return;
        }
        this.iconDrawable = null;
    }

    public boolean hasIcon() {
        return this.iconDrawable != null;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setOverrideColor(int i) {
        if (this.overrideColorProgress != i) {
            if (this.overlayBitmap == null) {
                try {
                    this.overlayBitmap = new Bitmap[2];
                    this.overlayCanvas = new Canvas[2];
                    for (int i2 = 0; i2 < 2; i2++) {
                        this.overlayBitmap[i2] = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                        this.overlayCanvas[i2] = new Canvas(this.overlayBitmap[i2]);
                    }
                    this.overlayMaskBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                    this.overlayMaskCanvas = new Canvas(this.overlayMaskBitmap);
                    this.overlayEraserPaint = new Paint(1);
                    this.overlayEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    this.overlayMaskPaint = new Paint(1);
                    this.overlayMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                    this.bitmapsCreated = true;
                } catch (Throwable unused) {
                    return;
                }
            }
            if (this.bitmapsCreated) {
                this.overrideColorProgress = i;
                this.overlayCx = 0.0f;
                this.overlayCy = 0.0f;
                this.overlayRad = 0.0f;
                invalidate();
            }
        }
    }

    public void setOverrideColorProgress(float f, float f2, float f3) {
        this.overlayCx = f;
        this.overlayCy = f2;
        this.overlayRad = f3;
        invalidate();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x00a8, code lost:
        if (r12 == 0) goto L_0x00aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x00ad, code lost:
        r16 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00b2, code lost:
        if (r12 == 0) goto L_0x00ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x01ef, code lost:
        if (r1 == 0) goto L_0x01f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x01f3, code lost:
        r4 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x01f9, code lost:
        if (r1 == 0) goto L_0x01f3;
     */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00d1  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0198  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x026d  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x03ce  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x03d7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r32) {
        /*
            r31 = this;
            r0 = r31
            r1 = r32
            int r2 = r31.getVisibility()
            if (r2 == 0) goto L_0x000b
            return
        L_0x000b:
            r2 = 1106771968(0x41var_, float:31.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3 = 1101004800(0x41a00000, float:20.0)
            org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r31.getMeasuredWidth()
            int r3 = r3 - r2
            r4 = 2
            int r3 = r3 / r4
            int r5 = r31.getMeasuredHeight()
            float r5 = (float) r5
            r6 = 1096810496(0x41600000, float:14.0)
            float r7 = org.telegram.messenger.AndroidUtilities.dpf2(r6)
            float r5 = r5 - r7
            r7 = 1073741824(0x40000000, float:2.0)
            float r5 = r5 / r7
            r8 = 1088421888(0x40e00000, float:7.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r9 = r9 + r3
            r10 = 1099431936(0x41880000, float:17.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r11 = r0.progress
            float r10 = r10 * r11
            int r10 = (int) r10
            int r9 = r9 + r10
            int r10 = r31.getMeasuredHeight()
            int r10 = r10 / r4
            r11 = 0
            r12 = 0
        L_0x0047:
            r15 = 1
            if (r12 >= r4) goto L_0x01b1
            if (r12 != r15) goto L_0x0054
            int r13 = r0.overrideColorProgress
            if (r13 != 0) goto L_0x0054
            r7 = 1096810496(0x41600000, float:14.0)
            goto L_0x01a3
        L_0x0054:
            if (r12 != 0) goto L_0x0058
            r13 = r1
            goto L_0x005c
        L_0x0058:
            android.graphics.Canvas[] r13 = r0.overlayCanvas
            r13 = r13[r11]
        L_0x005c:
            if (r12 != r15) goto L_0x00a4
            android.graphics.Bitmap[] r7 = r0.overlayBitmap
            r7 = r7[r11]
            r7.eraseColor(r11)
            android.graphics.Paint r7 = r0.paint
            r11 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r7.setColor(r11)
            android.graphics.Canvas r7 = r0.overlayMaskCanvas
            r20 = 0
            r21 = 0
            android.graphics.Bitmap r11 = r0.overlayMaskBitmap
            int r11 = r11.getWidth()
            float r11 = (float) r11
            android.graphics.Bitmap r14 = r0.overlayMaskBitmap
            int r14 = r14.getHeight()
            float r14 = (float) r14
            android.graphics.Paint r8 = r0.paint
            r19 = r7
            r22 = r11
            r23 = r14
            r24 = r8
            r19.drawRect(r20, r21, r22, r23, r24)
            android.graphics.Canvas r7 = r0.overlayMaskCanvas
            float r8 = r0.overlayCx
            float r11 = r31.getX()
            float r8 = r8 - r11
            float r11 = r0.overlayCy
            float r14 = r31.getY()
            float r11 = r11 - r14
            float r14 = r0.overlayRad
            android.graphics.Paint r6 = r0.overlayEraserPaint
            r7.drawCircle(r8, r11, r14, r6)
        L_0x00a4:
            int r6 = r0.overrideColorProgress
            if (r6 != r15) goto L_0x00b0
            if (r12 != 0) goto L_0x00ad
        L_0x00aa:
            r16 = 0
            goto L_0x00b9
        L_0x00ad:
            r16 = 1065353216(0x3var_, float:1.0)
            goto L_0x00b9
        L_0x00b0:
            if (r6 != r4) goto L_0x00b5
            if (r12 != 0) goto L_0x00aa
            goto L_0x00ad
        L_0x00b5:
            float r6 = r0.progress
            r16 = r6
        L_0x00b9:
            java.lang.String r6 = r0.trackColorKey
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            java.lang.String r7 = r0.trackCheckedColorKey
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            if (r12 != 0) goto L_0x00eb
            android.graphics.drawable.Drawable r8 = r0.iconDrawable
            if (r8 == 0) goto L_0x00eb
            int r8 = r0.lastIconColor
            boolean r11 = r0.isChecked
            if (r11 == 0) goto L_0x00d3
            r11 = r7
            goto L_0x00d4
        L_0x00d3:
            r11 = r6
        L_0x00d4:
            if (r8 == r11) goto L_0x00eb
            android.graphics.drawable.Drawable r8 = r0.iconDrawable
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            boolean r14 = r0.isChecked
            if (r14 == 0) goto L_0x00e0
            r14 = r7
            goto L_0x00e1
        L_0x00e0:
            r14 = r6
        L_0x00e1:
            r0.lastIconColor = r14
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r14, r4)
            r8.setColorFilter(r11)
        L_0x00eb:
            int r4 = android.graphics.Color.red(r6)
            int r8 = android.graphics.Color.red(r7)
            int r11 = android.graphics.Color.green(r6)
            int r14 = android.graphics.Color.green(r7)
            int r15 = android.graphics.Color.blue(r6)
            int r22 = android.graphics.Color.blue(r7)
            int r6 = android.graphics.Color.alpha(r6)
            int r7 = android.graphics.Color.alpha(r7)
            float r1 = (float) r4
            int r8 = r8 - r4
            float r4 = (float) r8
            float r4 = r4 * r16
            float r1 = r1 + r4
            int r1 = (int) r1
            float r4 = (float) r11
            int r14 = r14 - r11
            float r8 = (float) r14
            float r8 = r8 * r16
            float r4 = r4 + r8
            int r4 = (int) r4
            float r8 = (float) r15
            int r11 = r22 - r15
            float r11 = (float) r11
            float r11 = r11 * r16
            float r8 = r8 + r11
            int r8 = (int) r8
            float r11 = (float) r6
            int r7 = r7 - r6
            float r6 = (float) r7
            float r6 = r6 * r16
            float r11 = r11 + r6
            int r6 = (int) r11
            r6 = r6 & 255(0xff, float:3.57E-43)
            int r6 = r6 << 24
            r1 = r1 & 255(0xff, float:3.57E-43)
            int r1 = r1 << 16
            r1 = r1 | r6
            r4 = r4 & 255(0xff, float:3.57E-43)
            int r4 = r4 << 8
            r1 = r1 | r4
            r4 = r8 & 255(0xff, float:3.57E-43)
            r1 = r1 | r4
            android.graphics.Paint r4 = r0.paint
            r4.setColor(r1)
            android.graphics.Paint r4 = r0.paint2
            r4.setColor(r1)
            android.graphics.RectF r1 = r0.rectF
            float r4 = (float) r3
            int r6 = r3 + r2
            float r6 = (float) r6
            r7 = 1096810496(0x41600000, float:14.0)
            float r8 = org.telegram.messenger.AndroidUtilities.dpf2(r7)
            float r8 = r8 + r5
            r1.set(r4, r5, r6, r8)
            android.graphics.RectF r1 = r0.rectF
            r4 = 1088421888(0x40e00000, float:7.0)
            float r6 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
            float r8 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
            android.graphics.Paint r4 = r0.paint
            r13.drawRoundRect(r1, r6, r8, r4)
            float r1 = (float) r9
            float r4 = (float) r10
            r6 = 1092616192(0x41200000, float:10.0)
            float r6 = org.telegram.messenger.AndroidUtilities.dpf2(r6)
            android.graphics.Paint r8 = r0.paint
            r13.drawCircle(r1, r4, r6, r8)
            if (r12 != 0) goto L_0x0198
            android.graphics.drawable.RippleDrawable r1 = r0.rippleDrawable
            if (r1 == 0) goto L_0x0198
            r4 = 1099956224(0x41900000, float:18.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = r9 - r6
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r8 = r10 - r8
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r11 = r11 + r9
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r10
            r1.setBounds(r6, r8, r11, r4)
            android.graphics.drawable.RippleDrawable r1 = r0.rippleDrawable
            r1.draw(r13)
            goto L_0x01a3
        L_0x0198:
            r1 = 1
            if (r12 != r1) goto L_0x01a3
            android.graphics.Bitmap r1 = r0.overlayMaskBitmap
            android.graphics.Paint r4 = r0.overlayMaskPaint
            r6 = 0
            r13.drawBitmap(r1, r6, r6, r4)
        L_0x01a3:
            int r12 = r12 + 1
            r1 = r32
            r4 = 2
            r6 = 1096810496(0x41600000, float:14.0)
            r7 = 1073741824(0x40000000, float:2.0)
            r8 = 1088421888(0x40e00000, float:7.0)
            r11 = 0
            goto L_0x0047
        L_0x01b1:
            r6 = 0
            int r1 = r0.overrideColorProgress
            r2 = 0
            if (r1 == 0) goto L_0x01c2
            android.graphics.Bitmap[] r1 = r0.overlayBitmap
            r3 = 0
            r1 = r1[r3]
            r3 = r32
            r3.drawBitmap(r1, r6, r6, r2)
            goto L_0x01c4
        L_0x01c2:
            r3 = r32
        L_0x01c4:
            r1 = 0
        L_0x01c5:
            r4 = 2
            if (r1 >= r4) goto L_0x03dd
            r4 = 1
            if (r1 != r4) goto L_0x01d7
            int r5 = r0.overrideColorProgress
            if (r5 != 0) goto L_0x01d7
            r2 = 2
            r7 = 0
            r8 = 1088421888(0x40e00000, float:7.0)
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x03d8
        L_0x01d7:
            if (r1 != 0) goto L_0x01db
            r5 = r3
            goto L_0x01df
        L_0x01db:
            android.graphics.Canvas[] r5 = r0.overlayCanvas
            r5 = r5[r4]
        L_0x01df:
            if (r1 != r4) goto L_0x01ea
            android.graphics.Bitmap[] r6 = r0.overlayBitmap
            r6 = r6[r4]
            r7 = 0
            r6.eraseColor(r7)
            goto L_0x01eb
        L_0x01ea:
            r7 = 0
        L_0x01eb:
            int r6 = r0.overrideColorProgress
            if (r6 != r4) goto L_0x01f6
            if (r1 != 0) goto L_0x01f3
        L_0x01f1:
            r4 = 0
            goto L_0x01fe
        L_0x01f3:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x01fe
        L_0x01f6:
            r4 = 2
            if (r6 != r4) goto L_0x01fc
            if (r1 != 0) goto L_0x01f1
            goto L_0x01f3
        L_0x01fc:
            float r4 = r0.progress
        L_0x01fe:
            java.lang.String r6 = r0.thumbColorKey
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            java.lang.String r8 = r0.thumbCheckedColorKey
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            int r11 = android.graphics.Color.red(r6)
            int r12 = android.graphics.Color.red(r8)
            int r13 = android.graphics.Color.green(r6)
            int r14 = android.graphics.Color.green(r8)
            int r15 = android.graphics.Color.blue(r6)
            int r18 = android.graphics.Color.blue(r8)
            int r6 = android.graphics.Color.alpha(r6)
            int r8 = android.graphics.Color.alpha(r8)
            float r7 = (float) r11
            int r12 = r12 - r11
            float r11 = (float) r12
            float r11 = r11 * r4
            float r7 = r7 + r11
            int r7 = (int) r7
            float r11 = (float) r13
            int r14 = r14 - r13
            float r12 = (float) r14
            float r12 = r12 * r4
            float r11 = r11 + r12
            int r11 = (int) r11
            float r12 = (float) r15
            int r13 = r18 - r15
            float r13 = (float) r13
            float r13 = r13 * r4
            float r12 = r12 + r13
            int r12 = (int) r12
            float r13 = (float) r6
            int r8 = r8 - r6
            float r6 = (float) r8
            float r6 = r6 * r4
            float r13 = r13 + r6
            int r4 = (int) r13
            android.graphics.Paint r6 = r0.paint
            r4 = r4 & 255(0xff, float:3.57E-43)
            int r4 = r4 << 24
            r7 = r7 & 255(0xff, float:3.57E-43)
            int r7 = r7 << 16
            r4 = r4 | r7
            r7 = r11 & 255(0xff, float:3.57E-43)
            int r7 = r7 << 8
            r4 = r4 | r7
            r7 = r12 & 255(0xff, float:3.57E-43)
            r4 = r4 | r7
            r6.setColor(r4)
            float r4 = (float) r9
            float r6 = (float) r10
            r7 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            android.graphics.Paint r8 = r0.paint
            r5.drawCircle(r4, r6, r7, r8)
            if (r1 != 0) goto L_0x03c6
            android.graphics.drawable.Drawable r7 = r0.iconDrawable
            if (r7 == 0) goto L_0x029c
            int r4 = r7.getIntrinsicWidth()
            r6 = 2
            int r4 = r4 / r6
            int r4 = r9 - r4
            android.graphics.drawable.Drawable r8 = r0.iconDrawable
            int r8 = r8.getIntrinsicHeight()
            int r8 = r8 / r6
            int r8 = r10 - r8
            android.graphics.drawable.Drawable r11 = r0.iconDrawable
            int r11 = r11.getIntrinsicWidth()
            int r11 = r11 / r6
            int r11 = r11 + r9
            android.graphics.drawable.Drawable r12 = r0.iconDrawable
            int r12 = r12.getIntrinsicHeight()
            int r12 = r12 / r6
            int r12 = r12 + r10
            r7.setBounds(r4, r8, r11, r12)
            android.graphics.drawable.Drawable r4 = r0.iconDrawable
            r4.draw(r5)
            goto L_0x03c6
        L_0x029c:
            int r7 = r0.drawIconType
            r8 = 1
            if (r7 != r8) goto L_0x0370
            r7 = 1093455053(0x412ccccd, float:10.8)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r8 = 1067869798(0x3fa66666, float:1.3)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            float r9 = r0.progress
            float r8 = r8 * r9
            float r7 = r7 - r8
            float r4 = r4 - r7
            int r9 = (int) r4
            r4 = 1091043328(0x41080000, float:8.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r7 = 1056964608(0x3var_, float:0.5)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r8 = r0.progress
            float r7 = r7 * r8
            float r4 = r4 - r7
            float r6 = r6 - r4
            int r10 = (int) r6
            r4 = 1083388723(0x40933333, float:4.6)
            float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
            int r4 = (int) r4
            int r4 = r4 + r9
            r6 = 1092091904(0x41180000, float:9.5)
            float r6 = org.telegram.messenger.AndroidUtilities.dpf2(r6)
            float r7 = (float) r10
            float r6 = r6 + r7
            int r6 = (int) r6
            r8 = 1073741824(0x40000000, float:2.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 + r4
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r11 = r11 + r6
            r12 = 1089470464(0x40var_, float:7.5)
            float r13 = org.telegram.messenger.AndroidUtilities.dpf2(r12)
            int r13 = (int) r13
            int r13 = r13 + r9
            r14 = 1085066445(0x40accccd, float:5.4)
            float r14 = org.telegram.messenger.AndroidUtilities.dpf2(r14)
            int r14 = (int) r14
            int r14 = r14 + r10
            r15 = 1088421888(0x40e00000, float:7.0)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r8 = r13 + r17
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r14 + r17
            float r2 = (float) r13
            int r4 = r4 - r13
            float r4 = (float) r4
            float r13 = r0.progress
            float r4 = r4 * r13
            float r2 = r2 + r4
            int r2 = (int) r2
            float r4 = (float) r14
            int r6 = r6 - r14
            float r6 = (float) r6
            float r6 = r6 * r13
            float r4 = r4 + r6
            int r4 = (int) r4
            float r6 = (float) r8
            int r7 = r7 - r8
            float r7 = (float) r7
            float r7 = r7 * r13
            float r6 = r6 + r7
            int r6 = (int) r6
            float r7 = (float) r15
            int r11 = r11 - r15
            float r8 = (float) r11
            float r8 = r8 * r13
            float r7 = r7 + r8
            int r7 = (int) r7
            float r2 = (float) r2
            float r4 = (float) r4
            float r6 = (float) r6
            float r7 = (float) r7
            android.graphics.Paint r8 = r0.paint2
            r25 = r5
            r26 = r2
            r27 = r4
            r28 = r6
            r29 = r7
            r30 = r8
            r25.drawLine(r26, r27, r28, r29, r30)
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r12)
            int r2 = (int) r2
            int r2 = r2 + r9
            r4 = 1095237632(0x41480000, float:12.5)
            float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
            int r4 = (int) r4
            int r4 = r4 + r10
            r8 = 1088421888(0x40e00000, float:7.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = r6 + r2
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r4 - r7
            float r2 = (float) r2
            float r4 = (float) r4
            float r6 = (float) r6
            float r7 = (float) r7
            android.graphics.Paint r11 = r0.paint2
            r26 = r2
            r27 = r4
            r28 = r6
            r29 = r7
            r30 = r11
            r25.drawLine(r26, r27, r28, r29, r30)
            r2 = 2
            r4 = 1
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x03cc
        L_0x0370:
            r2 = 2
            r8 = 1088421888(0x40e00000, float:7.0)
            if (r7 == r2) goto L_0x0379
            android.animation.ObjectAnimator r7 = r0.iconAnimator
            if (r7 == 0) goto L_0x03c9
        L_0x0379:
            android.graphics.Paint r7 = r0.paint2
            r11 = 1132396544(0x437var_, float:255.0)
            float r12 = r0.iconProgress
            r13 = 1065353216(0x3var_, float:1.0)
            float r12 = r13 - r12
            float r12 = r12 * r11
            int r11 = (int) r12
            r7.setAlpha(r11)
            r7 = 1084227584(0x40a00000, float:5.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r10 - r7
            float r7 = (float) r7
            android.graphics.Paint r11 = r0.paint2
            r25 = r5
            r26 = r4
            r27 = r6
            r28 = r4
            r29 = r7
            r30 = r11
            r25.drawLine(r26, r27, r28, r29, r30)
            r5.save()
            r7 = -1028390912(0xffffffffc2b40000, float:-90.0)
            float r11 = r0.iconProgress
            float r11 = r11 * r7
            r5.rotate(r11, r4, r6)
            r7 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r9
            float r7 = (float) r7
            android.graphics.Paint r11 = r0.paint2
            r28 = r7
            r29 = r6
            r30 = r11
            r25.drawLine(r26, r27, r28, r29, r30)
            r5.restore()
            goto L_0x03cb
        L_0x03c6:
            r2 = 2
            r8 = 1088421888(0x40e00000, float:7.0)
        L_0x03c9:
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x03cb:
            r4 = 1
        L_0x03cc:
            if (r1 != r4) goto L_0x03d7
            android.graphics.Bitmap r4 = r0.overlayMaskBitmap
            android.graphics.Paint r6 = r0.overlayMaskPaint
            r7 = 0
            r5.drawBitmap(r4, r7, r7, r6)
            goto L_0x03d8
        L_0x03d7:
            r7 = 0
        L_0x03d8:
            int r1 = r1 + 1
            r2 = 0
            goto L_0x01c5
        L_0x03dd:
            r7 = 0
            int r1 = r0.overrideColorProgress
            if (r1 == 0) goto L_0x03eb
            android.graphics.Bitmap[] r1 = r0.overlayBitmap
            r2 = 1
            r1 = r1[r2]
            r2 = 0
            r3.drawBitmap(r1, r7, r7, r2)
        L_0x03eb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Switch.onDraw(android.graphics.Canvas):void");
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.Switch");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.isChecked);
    }
}
