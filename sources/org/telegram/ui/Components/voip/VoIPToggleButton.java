package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class VoIPToggleButton extends FrameLayout {
    int animateToBackgroundColor;
    public int animationDelay;
    int backgroundColor;
    private Paint bitmapPaint = new Paint(1);
    private boolean checkable;
    private boolean checked;
    Paint circlePaint = new Paint(1);
    private float crossOffset;
    private Paint crossPaint = new Paint(1);
    private float crossProgress;
    int currentBackgroundColor;
    int currentIconColor;
    int currentIconRes;
    String currentText;
    private boolean drawBackground = true;
    private boolean drawCross;
    Drawable[] icon = new Drawable[2];
    private Bitmap iconBitmap;
    private Canvas iconCanvas;
    /* access modifiers changed from: private */
    public boolean iconChangeColor;
    ValueAnimator replaceAnimator;
    private int replaceColorFrom;
    float replaceProgress;
    Drawable rippleDrawable;
    TextView[] textView = new TextView[2];
    private Paint xRefPaint = new Paint(1);

    public VoIPToggleButton(Context context) {
        super(context);
        setWillNotDraw(false);
        for (int i = 0; i < 2; i++) {
            TextView textView2 = new TextView(context);
            textView2.setGravity(1);
            textView2.setTextSize(1, 11.0f);
            textView2.setTextColor(-1);
            textView2.setImportantForAccessibility(2);
            addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 58.0f, 0.0f, 0.0f));
            this.textView[i] = textView2;
        }
        this.textView[1].setVisibility(8);
        this.xRefPaint.setColor(-16777216);
        this.xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.xRefPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.crossPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.crossPaint.setStrokeCap(Paint.Cap.ROUND);
        this.bitmapPaint.setFilterBitmap(true);
    }

    public void setTextSize(int i) {
        for (int i2 = 0; i2 < 2; i2++) {
            this.textView[i2].setTextSize(1, (float) i);
        }
    }

    public void setDrawBackground(boolean z) {
        this.drawBackground = z;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x02a4  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r21) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            float r2 = r0.replaceProgress
            r3 = 0
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x0019
            android.graphics.Paint r4 = r0.circlePaint
            int r5 = r0.backgroundColor
            int r6 = r0.animateToBackgroundColor
            int r2 = androidx.core.graphics.ColorUtils.blendARGB(r5, r6, r2)
            r4.setColor(r2)
            goto L_0x0020
        L_0x0019:
            android.graphics.Paint r2 = r0.circlePaint
            int r4 = r0.backgroundColor
            r2.setColor(r4)
        L_0x0020:
            int r2 = r20.getWidth()
            float r2 = (float) r2
            r4 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r4
            r5 = 1112539136(0x42500000, float:52.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            float r6 = r6 / r4
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r7 = (float) r7
            float r7 = r7 / r4
            boolean r8 = r0.drawBackground
            if (r8 == 0) goto L_0x0045
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r8 = (float) r8
            float r8 = r8 / r4
            android.graphics.Paint r9 = r0.circlePaint
            r1.drawCircle(r2, r6, r8, r9)
        L_0x0045:
            android.graphics.drawable.Drawable r8 = r0.rippleDrawable
            r9 = 0
            if (r8 != 0) goto L_0x0059
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r8 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r5, r9, r8)
            r0.rippleDrawable = r5
            r5.setCallback(r0)
        L_0x0059:
            android.graphics.drawable.Drawable r5 = r0.rippleDrawable
            float r8 = r2 - r7
            int r8 = (int) r8
            float r10 = r6 - r7
            int r10 = (int) r10
            float r11 = r2 + r7
            int r11 = (int) r11
            float r7 = r7 + r6
            int r7 = (int) r7
            r5.setBounds(r8, r10, r11, r7)
            android.graphics.drawable.Drawable r5 = r0.rippleDrawable
            r5.draw(r1)
            boolean r5 = r0.drawCross
            r7 = 255(0xff, float:3.57E-43)
            r8 = 1
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 2
            if (r5 != 0) goto L_0x0122
            float r5 = r0.crossProgress
            int r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0080
            goto L_0x0122
        L_0x0080:
            float r5 = r0.replaceProgress
            int r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x008d
            boolean r5 = r0.iconChangeColor
            if (r5 == 0) goto L_0x008b
            goto L_0x008d
        L_0x008b:
            r5 = 2
            goto L_0x008e
        L_0x008d:
            r5 = 1
        L_0x008e:
            if (r9 >= r5) goto L_0x02e5
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r9]
            if (r5 == 0) goto L_0x011e
            r21.save()
            float r5 = r0.replaceProgress
            int r12 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r12 == 0) goto L_0x00b7
            boolean r12 = r0.iconChangeColor
            if (r12 != 0) goto L_0x00b7
            if (r9 != 0) goto L_0x00a7
            float r5 = r10 - r5
        L_0x00a7:
            r1.scale(r5, r5, r2, r6)
            android.graphics.drawable.Drawable[] r12 = r0.icon
            r12 = r12[r9]
            r13 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r13
            int r5 = (int) r5
            r12.setAlpha(r5)
            goto L_0x00dd
        L_0x00b7:
            boolean r12 = r0.iconChangeColor
            if (r12 == 0) goto L_0x00d6
            int r12 = r0.replaceColorFrom
            int r13 = r0.currentIconColor
            int r5 = androidx.core.graphics.ColorUtils.blendARGB(r12, r13, r5)
            android.graphics.drawable.Drawable[] r12 = r0.icon
            r12 = r12[r9]
            android.graphics.PorterDuffColorFilter r13 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r13.<init>(r5, r14)
            r12.setColorFilter(r13)
            android.graphics.Paint r12 = r0.crossPaint
            r12.setColor(r5)
        L_0x00d6:
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r9]
            r5.setAlpha(r7)
        L_0x00dd:
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r12 = r5[r9]
            r5 = r5[r9]
            int r5 = r5.getIntrinsicWidth()
            float r5 = (float) r5
            float r5 = r5 / r4
            float r5 = r2 - r5
            int r5 = (int) r5
            android.graphics.drawable.Drawable[] r13 = r0.icon
            r13 = r13[r9]
            int r13 = r13.getIntrinsicHeight()
            int r13 = r13 / r11
            float r13 = (float) r13
            float r13 = r6 - r13
            int r13 = (int) r13
            android.graphics.drawable.Drawable[] r14 = r0.icon
            r14 = r14[r9]
            int r14 = r14.getIntrinsicWidth()
            int r14 = r14 / r11
            float r14 = (float) r14
            float r14 = r14 + r2
            int r14 = (int) r14
            android.graphics.drawable.Drawable[] r15 = r0.icon
            r15 = r15[r9]
            int r15 = r15.getIntrinsicHeight()
            int r15 = r15 / r11
            float r15 = (float) r15
            float r15 = r15 + r6
            int r15 = (int) r15
            r12.setBounds(r5, r13, r14, r15)
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r9]
            r5.draw(r1)
            r21.restore()
        L_0x011e:
            int r9 = r9 + 1
            goto L_0x0080
        L_0x0122:
            boolean r5 = r0.iconChangeColor
            if (r5 == 0) goto L_0x0143
            int r5 = r0.replaceColorFrom
            int r12 = r0.currentIconColor
            float r13 = r0.replaceProgress
            int r5 = androidx.core.graphics.ColorUtils.blendARGB(r5, r12, r13)
            android.graphics.drawable.Drawable[] r12 = r0.icon
            r12 = r12[r9]
            android.graphics.PorterDuffColorFilter r13 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r13.<init>(r5, r14)
            r12.setColorFilter(r13)
            android.graphics.Paint r12 = r0.crossPaint
            r12.setColor(r5)
        L_0x0143:
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r9]
            r5.setAlpha(r7)
            android.graphics.Bitmap r5 = r0.iconBitmap
            if (r5 != 0) goto L_0x016a
            r5 = 1107296256(0x42000000, float:32.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.Bitmap$Config r13 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r5 = android.graphics.Bitmap.createBitmap(r12, r5, r13)
            r0.iconBitmap = r5
            android.graphics.Canvas r5 = new android.graphics.Canvas
            android.graphics.Bitmap r12 = r0.iconBitmap
            r5.<init>(r12)
            r0.iconCanvas = r5
            goto L_0x016d
        L_0x016a:
            r5.eraseColor(r9)
        L_0x016d:
            android.graphics.Bitmap r5 = r0.iconBitmap
            int r5 = r5.getWidth()
            int r5 = r5 >> r8
            float r5 = (float) r5
            android.graphics.Bitmap r12 = r0.iconBitmap
            int r12 = r12.getHeight()
            int r8 = r12 >> 1
            float r8 = (float) r8
            float r12 = r0.replaceProgress
            int r13 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r13 == 0) goto L_0x01a3
            boolean r13 = r0.iconChangeColor
            if (r13 == 0) goto L_0x01a3
            int r13 = r0.replaceColorFrom
            int r14 = r0.currentIconColor
            int r12 = androidx.core.graphics.ColorUtils.blendARGB(r13, r14, r12)
            android.graphics.drawable.Drawable[] r13 = r0.icon
            r13 = r13[r9]
            android.graphics.PorterDuffColorFilter r14 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.MULTIPLY
            r14.<init>(r12, r15)
            r13.setColorFilter(r14)
            android.graphics.Paint r13 = r0.crossPaint
            r13.setColor(r12)
        L_0x01a3:
            android.graphics.drawable.Drawable[] r12 = r0.icon
            r12 = r12[r9]
            r12.setAlpha(r7)
            boolean r7 = r0.drawCross
            r12 = 1034147594(0x3da3d70a, float:0.08)
            if (r7 == 0) goto L_0x01c5
            float r13 = r0.crossProgress
            int r14 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r14 >= 0) goto L_0x01c5
            float r13 = r13 + r12
            r0.crossProgress = r13
            int r7 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r7 <= 0) goto L_0x01c1
            r0.crossProgress = r10
            goto L_0x01d6
        L_0x01c1:
            r20.invalidate()
            goto L_0x01d6
        L_0x01c5:
            if (r7 != 0) goto L_0x01d6
            float r7 = r0.crossProgress
            float r7 = r7 - r12
            r0.crossProgress = r7
            int r7 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r7 >= 0) goto L_0x01d3
            r0.crossProgress = r3
            goto L_0x01d6
        L_0x01d3:
            r20.invalidate()
        L_0x01d6:
            float r7 = r0.crossProgress
            int r3 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x02a4
            android.graphics.drawable.Drawable[] r3 = r0.icon
            r3 = r3[r9]
            int r3 = r3.getIntrinsicWidth()
            float r3 = (float) r3
            float r3 = r3 / r4
            float r3 = r5 - r3
            int r3 = (int) r3
            android.graphics.drawable.Drawable[] r7 = r0.icon
            r7 = r7[r9]
            int r7 = r7.getIntrinsicHeight()
            int r7 = r7 / r11
            float r7 = (float) r7
            float r7 = r5 - r7
            int r7 = (int) r7
            float r3 = (float) r3
            r12 = 1090519040(0x41000000, float:8.0)
            float r13 = org.telegram.messenger.AndroidUtilities.dpf2(r12)
            float r3 = r3 + r13
            float r13 = r0.crossOffset
            float r3 = r3 + r13
            float r7 = (float) r7
            float r12 = org.telegram.messenger.AndroidUtilities.dpf2(r12)
            float r7 = r7 + r12
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r10 = r3 - r10
            r12 = 1099431936(0x41880000, float:17.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r13 = (float) r13
            org.telegram.ui.Components.CubicBezierInterpolator r14 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            float r15 = r0.crossProgress
            float r15 = r14.getInterpolation(r15)
            float r13 = r13 * r15
            float r10 = r10 + r13
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r13 = r0.crossProgress
            float r13 = r14.getInterpolation(r13)
            float r12 = r12 * r13
            float r12 = r12 + r7
            android.graphics.drawable.Drawable[] r13 = r0.icon
            r14 = r13[r9]
            r13 = r13[r9]
            int r13 = r13.getIntrinsicWidth()
            float r13 = (float) r13
            float r13 = r13 / r4
            float r13 = r5 - r13
            int r13 = (int) r13
            android.graphics.drawable.Drawable[] r15 = r0.icon
            r15 = r15[r9]
            int r15 = r15.getIntrinsicHeight()
            int r15 = r15 / r11
            float r15 = (float) r15
            float r15 = r8 - r15
            int r15 = (int) r15
            android.graphics.drawable.Drawable[] r4 = r0.icon
            r4 = r4[r9]
            int r4 = r4.getIntrinsicWidth()
            int r4 = r4 / r11
            float r4 = (float) r4
            float r4 = r4 + r5
            int r4 = (int) r4
            android.graphics.drawable.Drawable[] r11 = r0.icon
            r11 = r11[r9]
            int r11 = r11.getIntrinsicHeight()
            r17 = 2
            int r11 = r11 / 2
            float r11 = (float) r11
            float r11 = r11 + r8
            int r11 = (int) r11
            r14.setBounds(r13, r15, r4, r11)
            android.graphics.drawable.Drawable[] r4 = r0.icon
            r4 = r4[r9]
            android.graphics.Canvas r9 = r0.iconCanvas
            r4.draw(r9)
            android.graphics.Canvas r14 = r0.iconCanvas
            r4 = 1073741824(0x40000000, float:2.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r9 = (float) r9
            float r16 = r7 - r9
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r18 = r12 - r4
            android.graphics.Paint r4 = r0.xRefPaint
            r15 = r3
            r17 = r10
            r19 = r4
            r14.drawLine(r15, r16, r17, r18, r19)
            android.graphics.Canvas r14 = r0.iconCanvas
            android.graphics.Paint r4 = r0.crossPaint
            r16 = r7
            r18 = r12
            r19 = r4
            r14.drawLine(r15, r16, r17, r18, r19)
            android.graphics.Bitmap r3 = r0.iconBitmap
            float r2 = r2 - r5
            float r6 = r6 - r8
            android.graphics.Paint r4 = r0.bitmapPaint
            r1.drawBitmap(r3, r2, r6, r4)
            goto L_0x02e5
        L_0x02a4:
            android.graphics.drawable.Drawable[] r3 = r0.icon
            r4 = r3[r9]
            r3 = r3[r9]
            int r3 = r3.getIntrinsicWidth()
            float r3 = (float) r3
            r5 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r5
            float r3 = r2 - r3
            int r3 = (int) r3
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r9]
            int r5 = r5.getIntrinsicHeight()
            r7 = 2
            int r5 = r5 / r7
            float r5 = (float) r5
            float r5 = r6 - r5
            int r5 = (int) r5
            android.graphics.drawable.Drawable[] r8 = r0.icon
            r8 = r8[r9]
            int r8 = r8.getIntrinsicWidth()
            int r8 = r8 / r7
            float r8 = (float) r8
            float r2 = r2 + r8
            int r2 = (int) r2
            android.graphics.drawable.Drawable[] r8 = r0.icon
            r8 = r8[r9]
            int r8 = r8.getIntrinsicHeight()
            int r8 = r8 / r7
            float r7 = (float) r8
            float r6 = r6 + r7
            int r6 = (int) r6
            r4.setBounds(r3, r5, r2, r6)
            android.graphics.drawable.Drawable[] r2 = r0.icon
            r2 = r2[r9]
            r2.draw(r1)
        L_0x02e5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPToggleButton.onDraw(android.graphics.Canvas):void");
    }

    public void setData(int i, int i2, int i3, String str, boolean z, boolean z2) {
        setData(i, i2, i3, 1.0f, true, str, z, z2);
    }

    public void setData(int i, int i2, int i3, float f, boolean z, String str, boolean z2, boolean z3) {
        String str2;
        if (getVisibility() != 0) {
            setVisibility(0);
            z3 = false;
        }
        if (this.currentIconRes != i || this.currentIconColor != i2 || this.currentBackgroundColor != i3 || (str2 = this.currentText) == null || !str2.equals(str)) {
            if (this.rippleDrawable == null || z) {
                if (Color.alpha(i3) != 255 || ((double) AndroidUtilities.computePerceivedBrightness(i3)) <= 0.5d) {
                    Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(52.0f), 0, ColorUtils.setAlphaComponent(-1, (int) (f * 76.5f)));
                    this.rippleDrawable = createSimpleSelectorCircleDrawable;
                    createSimpleSelectorCircleDrawable.setCallback(this);
                } else {
                    Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(52.0f), 0, ColorUtils.setAlphaComponent(-16777216, (int) (f * 25.5f)));
                    this.rippleDrawable = createSimpleSelectorCircleDrawable2;
                    createSimpleSelectorCircleDrawable2.setCallback(this);
                }
            }
            boolean z4 = this.currentIconRes == i;
            this.iconChangeColor = z4;
            if (z4) {
                this.replaceColorFrom = this.currentIconColor;
            }
            this.currentIconRes = i;
            this.currentIconColor = i2;
            this.currentBackgroundColor = i3;
            this.currentText = str;
            this.drawCross = z2;
            ValueAnimator valueAnimator = this.replaceAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (!z3) {
                this.icon[0] = ContextCompat.getDrawable(getContext(), i).mutate();
                this.icon[0].setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
                this.crossPaint.setColor(i2);
                this.backgroundColor = i3;
                this.textView[0].setText(str);
                this.crossProgress = this.drawCross ? 1.0f : 0.0f;
                this.iconChangeColor = false;
                this.replaceProgress = 0.0f;
                invalidate();
                return;
            }
            if (!this.iconChangeColor) {
                this.icon[1] = ContextCompat.getDrawable(getContext(), i).mutate();
                this.icon[1].setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
            }
            this.animateToBackgroundColor = i3;
            final boolean z5 = !this.textView[0].getText().toString().equals(str);
            if (!z5) {
                this.textView[0].setText(str);
            } else {
                this.textView[1].setText(str);
                this.textView[1].setVisibility(0);
                this.textView[1].setAlpha(0.0f);
                this.textView[1].setScaleX(0.0f);
                this.textView[1].setScaleY(0.0f);
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.replaceAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(z5) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    VoIPToggleButton.this.lambda$setData$0$VoIPToggleButton(this.f$1, valueAnimator);
                }
            });
            this.replaceAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (z5) {
                        TextView[] textViewArr = VoIPToggleButton.this.textView;
                        TextView textView = textViewArr[0];
                        textViewArr[0] = textViewArr[1];
                        textViewArr[1] = textView;
                        textViewArr[1].setVisibility(8);
                    }
                    if (!VoIPToggleButton.this.iconChangeColor) {
                        Drawable[] drawableArr = VoIPToggleButton.this.icon;
                        if (drawableArr[1] != null) {
                            drawableArr[0] = drawableArr[1];
                            drawableArr[1] = null;
                        }
                    }
                    boolean unused = VoIPToggleButton.this.iconChangeColor = false;
                    VoIPToggleButton voIPToggleButton = VoIPToggleButton.this;
                    voIPToggleButton.backgroundColor = voIPToggleButton.animateToBackgroundColor;
                    voIPToggleButton.replaceProgress = 0.0f;
                    voIPToggleButton.invalidate();
                }
            });
            this.replaceAnimator.setDuration(150).start();
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setData$0 */
    public /* synthetic */ void lambda$setData$0$VoIPToggleButton(boolean z, ValueAnimator valueAnimator) {
        this.replaceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (z) {
            this.textView[0].setAlpha(1.0f - this.replaceProgress);
            this.textView[0].setScaleX(1.0f - this.replaceProgress);
            this.textView[0].setScaleY(1.0f - this.replaceProgress);
            this.textView[1].setAlpha(this.replaceProgress);
            this.textView[1].setScaleX(this.replaceProgress);
            this.textView[1].setScaleY(this.replaceProgress);
        }
    }

    public void setCrossOffset(float f) {
        this.crossOffset = f;
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.setState(getDrawableState());
        }
    }

    public boolean verifyDrawable(Drawable drawable) {
        return this.rippleDrawable == drawable || super.verifyDrawable(drawable);
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    public void setCheckable(boolean z) {
        this.checkable = z;
    }

    public void setChecked(boolean z) {
        this.checked = z;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setText(this.currentText);
        accessibilityNodeInfo.setClassName(Button.class.getName());
        if (this.checkable) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(this.checked);
        }
    }
}
