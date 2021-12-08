package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class VoIPToggleButton extends FrameLayout {
    private boolean animateBackground;
    int animateToBackgroundColor;
    public int animationDelay;
    /* access modifiers changed from: private */
    public int backgroundCheck1;
    /* access modifiers changed from: private */
    public int backgroundCheck2;
    int backgroundColor;
    private Paint bitmapPaint;
    private ValueAnimator checkAnimator;
    /* access modifiers changed from: private */
    public boolean checkable;
    private boolean checkableForAccessibility;
    /* access modifiers changed from: private */
    public boolean checked;
    /* access modifiers changed from: private */
    public float checkedProgress;
    Paint circlePaint;
    private float crossOffset;
    private Paint crossPaint;
    private float crossProgress;
    int currentBackgroundColor;
    int currentIconColor;
    int currentIconRes;
    String currentText;
    private boolean drawBackground;
    private boolean drawCross;
    Drawable[] icon;
    /* access modifiers changed from: private */
    public boolean iconChangeColor;
    private float radius;
    ValueAnimator replaceAnimator;
    private int replaceColorFrom;
    float replaceProgress;
    Drawable rippleDrawable;
    FrameLayout textLayoutContainer;
    TextView[] textView;
    private Paint xRefPaint;

    public VoIPToggleButton(Context context) {
        this(context, 52.0f);
    }

    public VoIPToggleButton(Context context, float f) {
        super(context);
        this.circlePaint = new Paint(1);
        this.drawBackground = true;
        this.icon = new Drawable[2];
        this.textView = new TextView[2];
        this.crossPaint = new Paint(1);
        this.xRefPaint = new Paint(1);
        this.bitmapPaint = new Paint(1);
        this.radius = f;
        setWillNotDraw(false);
        FrameLayout frameLayout = new FrameLayout(context);
        this.textLayoutContainer = frameLayout;
        addView(frameLayout);
        for (int i = 0; i < 2; i++) {
            TextView textView2 = new TextView(context);
            textView2.setGravity(1);
            textView2.setTextSize(1, 11.0f);
            textView2.setTextColor(-1);
            textView2.setImportantForAccessibility(2);
            this.textLayoutContainer.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 4.0f + f, 0.0f, 0.0f));
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
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01c3  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0290  */
    /* JADX WARNING: Removed duplicated region for block: B:74:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0042  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r19) {
        /*
            r18 = this;
            r0 = r18
            r8 = r19
            boolean r1 = r0.animateBackground
            r2 = 0
            if (r1 == 0) goto L_0x001d
            float r1 = r0.replaceProgress
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x001d
            android.graphics.Paint r3 = r0.circlePaint
            int r4 = r0.backgroundColor
            int r5 = r0.animateToBackgroundColor
            int r1 = androidx.core.graphics.ColorUtils.blendARGB(r4, r5, r1)
            r3.setColor(r1)
            goto L_0x0024
        L_0x001d:
            android.graphics.Paint r1 = r0.circlePaint
            int r3 = r0.backgroundColor
            r1.setColor(r3)
        L_0x0024:
            int r1 = r18.getWidth()
            float r1 = (float) r1
            r9 = 1073741824(0x40000000, float:2.0)
            float r10 = r1 / r9
            float r1 = r0.radius
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r11 = r1 / r9
            float r1 = r0.radius
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 / r9
            boolean r3 = r0.drawBackground
            if (r3 == 0) goto L_0x004f
            float r3 = r0.radius
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 / r9
            android.graphics.Paint r4 = r0.circlePaint
            r8.drawCircle(r10, r11, r3, r4)
        L_0x004f:
            android.graphics.drawable.Drawable r3 = r0.rippleDrawable
            r12 = 0
            if (r3 != 0) goto L_0x0065
            float r3 = r0.radius
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r3, r12, r4)
            r0.rippleDrawable = r3
            r3.setCallback(r0)
        L_0x0065:
            android.graphics.drawable.Drawable r3 = r0.rippleDrawable
            float r4 = r10 - r1
            int r4 = (int) r4
            float r5 = r11 - r1
            int r5 = (int) r5
            float r6 = r10 + r1
            int r6 = (int) r6
            float r1 = r1 + r11
            int r1 = (int) r1
            r3.setBounds(r4, r5, r6, r1)
            android.graphics.drawable.Drawable r1 = r0.rippleDrawable
            r1.draw(r8)
            int r1 = r0.currentIconRes
            if (r1 == 0) goto L_0x02ce
            boolean r1 = r0.drawCross
            r3 = 255(0xff, float:3.57E-43)
            r4 = 1065353216(0x3var_, float:1.0)
            r13 = 2
            if (r1 != 0) goto L_0x013d
            float r1 = r0.crossProgress
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x008f
            goto L_0x013d
        L_0x008f:
            r1 = 0
        L_0x0090:
            float r5 = r0.replaceProgress
            r6 = 1
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x009e
            boolean r5 = r0.iconChangeColor
            if (r5 == 0) goto L_0x009c
            goto L_0x009e
        L_0x009c:
            r5 = 2
            goto L_0x009f
        L_0x009e:
            r5 = 1
        L_0x009f:
            if (r1 >= r5) goto L_0x02ce
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r1]
            if (r5 == 0) goto L_0x0139
            r19.save()
            float r5 = r0.replaceProgress
            int r7 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r7 == 0) goto L_0x00d2
            boolean r7 = r0.iconChangeColor
            if (r7 != 0) goto L_0x00d2
            android.graphics.drawable.Drawable[] r7 = r0.icon
            r14 = r7[r12]
            if (r14 == 0) goto L_0x00d2
            r6 = r7[r6]
            if (r6 == 0) goto L_0x00d2
            if (r1 != 0) goto L_0x00c2
            float r5 = r4 - r5
        L_0x00c2:
            r8.scale(r5, r5, r10, r11)
            android.graphics.drawable.Drawable[] r6 = r0.icon
            r6 = r6[r1]
            r7 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r7
            int r5 = (int) r5
            r6.setAlpha(r5)
            goto L_0x00f8
        L_0x00d2:
            boolean r6 = r0.iconChangeColor
            if (r6 == 0) goto L_0x00f1
            int r6 = r0.replaceColorFrom
            int r7 = r0.currentIconColor
            int r5 = androidx.core.graphics.ColorUtils.blendARGB(r6, r7, r5)
            android.graphics.drawable.Drawable[] r6 = r0.icon
            r6 = r6[r1]
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r5, r14)
            r6.setColorFilter(r7)
            android.graphics.Paint r6 = r0.crossPaint
            r6.setColor(r5)
        L_0x00f1:
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r1]
            r5.setAlpha(r3)
        L_0x00f8:
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r6 = r5[r1]
            r5 = r5[r1]
            int r5 = r5.getIntrinsicWidth()
            float r5 = (float) r5
            float r5 = r5 / r9
            float r5 = r10 - r5
            int r5 = (int) r5
            android.graphics.drawable.Drawable[] r7 = r0.icon
            r7 = r7[r1]
            int r7 = r7.getIntrinsicHeight()
            int r7 = r7 / r13
            float r7 = (float) r7
            float r7 = r11 - r7
            int r7 = (int) r7
            android.graphics.drawable.Drawable[] r14 = r0.icon
            r14 = r14[r1]
            int r14 = r14.getIntrinsicWidth()
            int r14 = r14 / r13
            float r14 = (float) r14
            float r14 = r14 + r10
            int r14 = (int) r14
            android.graphics.drawable.Drawable[] r15 = r0.icon
            r15 = r15[r1]
            int r15 = r15.getIntrinsicHeight()
            int r15 = r15 / r13
            float r15 = (float) r15
            float r15 = r15 + r11
            int r15 = (int) r15
            r6.setBounds(r5, r7, r14, r15)
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r1]
            r5.draw(r8)
            r19.restore()
        L_0x0139:
            int r1 = r1 + 1
            goto L_0x0090
        L_0x013d:
            boolean r1 = r0.iconChangeColor
            if (r1 == 0) goto L_0x015e
            int r1 = r0.replaceColorFrom
            int r5 = r0.currentIconColor
            float r6 = r0.replaceProgress
            int r1 = androidx.core.graphics.ColorUtils.blendARGB(r1, r5, r6)
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r12]
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r1, r7)
            r5.setColorFilter(r6)
            android.graphics.Paint r5 = r0.crossPaint
            r5.setColor(r1)
        L_0x015e:
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r1 = r1[r12]
            r1.setAlpha(r3)
            float r1 = r0.replaceProgress
            int r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x018a
            boolean r5 = r0.iconChangeColor
            if (r5 == 0) goto L_0x018a
            int r5 = r0.replaceColorFrom
            int r6 = r0.currentIconColor
            int r1 = androidx.core.graphics.ColorUtils.blendARGB(r5, r6, r1)
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r12]
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r1, r7)
            r5.setColorFilter(r6)
            android.graphics.Paint r5 = r0.crossPaint
            r5.setColor(r1)
        L_0x018a:
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r1 = r1[r12]
            r1.setAlpha(r3)
            boolean r1 = r0.drawCross
            r3 = 1034147594(0x3da3d70a, float:0.08)
            if (r1 == 0) goto L_0x01ac
            float r5 = r0.crossProgress
            int r6 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x01ac
            float r5 = r5 + r3
            r0.crossProgress = r5
            int r1 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r1 <= 0) goto L_0x01a8
            r0.crossProgress = r4
            goto L_0x01bd
        L_0x01a8:
            r18.invalidate()
            goto L_0x01bd
        L_0x01ac:
            if (r1 != 0) goto L_0x01bd
            float r1 = r0.crossProgress
            float r1 = r1 - r3
            r0.crossProgress = r1
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 >= 0) goto L_0x01ba
            r0.crossProgress = r2
            goto L_0x01bd
        L_0x01ba:
            r18.invalidate()
        L_0x01bd:
            float r1 = r0.crossProgress
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x0290
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r1 = r1[r12]
            int r1 = r1.getIntrinsicWidth()
            float r1 = (float) r1
            float r1 = r1 / r9
            float r1 = r10 - r1
            int r1 = (int) r1
            android.graphics.drawable.Drawable[] r2 = r0.icon
            r2 = r2[r12]
            int r2 = r2.getIntrinsicHeight()
            int r2 = r2 / r13
            float r2 = (float) r2
            float r2 = r11 - r2
            int r2 = (int) r2
            float r1 = (float) r1
            r3 = 1090519040(0x41000000, float:8.0)
            float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r3)
            float r1 = r1 + r5
            float r5 = r0.crossOffset
            float r14 = r1 + r5
            float r1 = (float) r2
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r3)
            float r15 = r1 + r2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r1 = (float) r1
            float r1 = r14 - r1
            r2 = 1099431936(0x41880000, float:17.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            float r5 = r0.crossProgress
            float r5 = r4.getInterpolation(r5)
            float r3 = r3 * r5
            float r16 = r1 + r3
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r1 = (float) r1
            float r2 = r0.crossProgress
            float r2 = r4.getInterpolation(r2)
            float r1 = r1 * r2
            float r17 = r15 + r1
            r2 = 0
            r3 = 0
            int r1 = r18.getMeasuredWidth()
            float r4 = (float) r1
            int r1 = r18.getMeasuredHeight()
            float r5 = (float) r1
            r6 = 255(0xff, float:3.57E-43)
            r7 = 31
            r1 = r19
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r2 = r1[r12]
            r1 = r1[r12]
            int r1 = r1.getIntrinsicWidth()
            float r1 = (float) r1
            float r1 = r1 / r9
            float r1 = r10 - r1
            int r1 = (int) r1
            android.graphics.drawable.Drawable[] r3 = r0.icon
            r3 = r3[r12]
            int r3 = r3.getIntrinsicHeight()
            int r3 = r3 / r13
            float r3 = (float) r3
            float r3 = r11 - r3
            int r3 = (int) r3
            android.graphics.drawable.Drawable[] r4 = r0.icon
            r4 = r4[r12]
            int r4 = r4.getIntrinsicWidth()
            int r4 = r4 / r13
            float r4 = (float) r4
            float r10 = r10 + r4
            int r4 = (int) r10
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r12]
            int r5 = r5.getIntrinsicHeight()
            int r5 = r5 / r13
            float r5 = (float) r5
            float r11 = r11 + r5
            int r5 = (int) r11
            r2.setBounds(r1, r3, r4, r5)
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r1 = r1[r12]
            r1.draw(r8)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r1 = (float) r1
            float r3 = r15 - r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r1 = (float) r1
            float r5 = r17 - r1
            android.graphics.Paint r6 = r0.xRefPaint
            r1 = r19
            r2 = r14
            r4 = r16
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.crossPaint
            r3 = r15
            r5 = r17
            r1.drawLine(r2, r3, r4, r5, r6)
            r19.restore()
            goto L_0x02ce
        L_0x0290:
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r2 = r1[r12]
            r1 = r1[r12]
            int r1 = r1.getIntrinsicWidth()
            float r1 = (float) r1
            float r1 = r1 / r9
            float r1 = r10 - r1
            int r1 = (int) r1
            android.graphics.drawable.Drawable[] r3 = r0.icon
            r3 = r3[r12]
            int r3 = r3.getIntrinsicHeight()
            int r3 = r3 / r13
            float r3 = (float) r3
            float r3 = r11 - r3
            int r3 = (int) r3
            android.graphics.drawable.Drawable[] r4 = r0.icon
            r4 = r4[r12]
            int r4 = r4.getIntrinsicWidth()
            int r4 = r4 / r13
            float r4 = (float) r4
            float r10 = r10 + r4
            int r4 = (int) r10
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r12]
            int r5 = r5.getIntrinsicHeight()
            int r5 = r5 / r13
            float r5 = (float) r5
            float r11 = r11 + r5
            int r5 = (int) r11
            r2.setBounds(r1, r3, r4, r5)
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r1 = r1[r12]
            r1.draw(r8)
        L_0x02ce:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPToggleButton.onDraw(android.graphics.Canvas):void");
    }

    public void setBackgroundColor(int i, int i2) {
        this.backgroundCheck1 = i;
        this.backgroundCheck2 = i2;
        this.backgroundColor = ColorUtils.blendARGB(i, i2, this.checkedProgress);
        invalidate();
    }

    public void setData(int i, int i2, int i3, String str, boolean z, boolean z2) {
        setData(i, i2, i3, 1.0f, true, str, z, z2);
    }

    public void setEnabled(boolean z, boolean z2) {
        super.setEnabled(z);
        float f = 1.0f;
        if (z2) {
            ViewPropertyAnimator animate = animate();
            if (!z) {
                f = 0.5f;
            }
            animate.alpha(f).setDuration(180).start();
            return;
        }
        clearAnimation();
        if (!z) {
            f = 0.5f;
        }
        setAlpha(f);
    }

    public void setData(int i, int i2, int i3, float f, boolean z, String str, boolean z2, boolean z3) {
        String str2;
        if (getVisibility() != 0) {
            setVisibility(0);
            z3 = false;
        }
        if (this.currentIconRes != i || this.currentIconColor != i2 || ((!this.checkable && this.currentBackgroundColor != i3) || (str2 = this.currentText) == null || !str2.equals(str) || z2 != this.drawCross)) {
            if (this.rippleDrawable == null || z) {
                if (Color.alpha(i3) != 255 || ((double) AndroidUtilities.computePerceivedBrightness(i3)) <= 0.5d) {
                    Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(this.radius), 0, ColorUtils.setAlphaComponent(-1, (int) (f * 76.5f)));
                    this.rippleDrawable = createSimpleSelectorCircleDrawable;
                    createSimpleSelectorCircleDrawable.setCallback(this);
                } else {
                    Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(this.radius), 0, ColorUtils.setAlphaComponent(-16777216, (int) (f * 25.5f)));
                    this.rippleDrawable = createSimpleSelectorCircleDrawable2;
                    createSimpleSelectorCircleDrawable2.setCallback(this);
                }
            }
            ValueAnimator valueAnimator = this.replaceAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.animateBackground = this.currentBackgroundColor != i3;
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
            if (!z3) {
                if (i != 0) {
                    this.icon[0] = ContextCompat.getDrawable(getContext(), i).mutate();
                    this.icon[0].setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
                }
                this.crossPaint.setColor(i2);
                if (!this.checkable) {
                    this.backgroundColor = i3;
                }
                this.textView[0].setText(str);
                this.crossProgress = this.drawCross ? 1.0f : 0.0f;
                this.iconChangeColor = false;
                this.replaceProgress = 0.0f;
                invalidate();
                return;
            }
            if (!z4 && i != 0) {
                this.icon[1] = ContextCompat.getDrawable(getContext(), i).mutate();
                this.icon[1].setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
            }
            if (!this.checkable) {
                this.animateToBackgroundColor = i3;
            }
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
            ofFloat.addUpdateListener(new VoIPToggleButton$$ExternalSyntheticLambda1(this, z5));
            this.replaceAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    VoIPToggleButton voIPToggleButton = VoIPToggleButton.this;
                    voIPToggleButton.replaceAnimator = null;
                    if (z5) {
                        TextView[] textViewArr = voIPToggleButton.textView;
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
                    if (!VoIPToggleButton.this.checkable) {
                        VoIPToggleButton voIPToggleButton2 = VoIPToggleButton.this;
                        voIPToggleButton2.backgroundColor = voIPToggleButton2.animateToBackgroundColor;
                    }
                    VoIPToggleButton voIPToggleButton3 = VoIPToggleButton.this;
                    voIPToggleButton3.replaceProgress = 0.0f;
                    voIPToggleButton3.invalidate();
                }
            });
            this.replaceAnimator.setDuration(150).start();
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setData$0(boolean z, ValueAnimator valueAnimator) {
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

    public void setCheckableForAccessibility(boolean z) {
        this.checkableForAccessibility = z;
    }

    public void setCheckable(boolean z) {
        this.checkable = z;
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checked != z) {
            this.checked = z;
            if (this.checkable) {
                float f = 1.0f;
                if (z2) {
                    ValueAnimator valueAnimator = this.checkAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllListeners();
                        this.checkAnimator.cancel();
                    }
                    float[] fArr = new float[2];
                    fArr[0] = this.checkedProgress;
                    if (!this.checked) {
                        f = 0.0f;
                    }
                    fArr[1] = f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                    this.checkAnimator = ofFloat;
                    ofFloat.addUpdateListener(new VoIPToggleButton$$ExternalSyntheticLambda0(this));
                    this.checkAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            VoIPToggleButton voIPToggleButton = VoIPToggleButton.this;
                            float unused = voIPToggleButton.checkedProgress = voIPToggleButton.checked ? 1.0f : 0.0f;
                            VoIPToggleButton voIPToggleButton2 = VoIPToggleButton.this;
                            voIPToggleButton2.setBackgroundColor(voIPToggleButton2.backgroundCheck1, VoIPToggleButton.this.backgroundCheck2);
                        }
                    });
                    this.checkAnimator.setDuration(150);
                    this.checkAnimator.start();
                    return;
                }
                if (!z) {
                    f = 0.0f;
                }
                this.checkedProgress = f;
                setBackgroundColor(this.backgroundCheck1, this.backgroundCheck2);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setChecked$1(ValueAnimator valueAnimator) {
        this.checkedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        setBackgroundColor(this.backgroundCheck1, this.backgroundCheck2);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setText(this.currentText);
        if (this.checkable || this.checkableForAccessibility) {
            accessibilityNodeInfo.setClassName(ToggleButton.class.getName());
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(this.checked);
            return;
        }
        accessibilityNodeInfo.setClassName(Button.class.getName());
    }

    public void shakeView() {
        AndroidUtilities.shakeView(this.textView[0], 2.0f, 0);
        AndroidUtilities.shakeView(this.textView[1], 2.0f, 0);
    }

    public void showText(boolean z, boolean z2) {
        float f = 1.0f;
        if (z2) {
            if (!z) {
                f = 0.0f;
            }
            if (this.textLayoutContainer.getAlpha() != f) {
                this.textLayoutContainer.animate().alpha(f).start();
                return;
            }
            return;
        }
        this.textLayoutContainer.animate().cancel();
        FrameLayout frameLayout = this.textLayoutContainer;
        if (!z) {
            f = 0.0f;
        }
        frameLayout.setAlpha(f);
    }
}
