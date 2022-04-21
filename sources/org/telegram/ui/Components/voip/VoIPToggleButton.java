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
import org.telegram.ui.Components.RLottieImageView;

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
    private RLottieImageView lottieImageView;
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

    public VoIPToggleButton(Context context, float radius2) {
        super(context);
        this.circlePaint = new Paint(1);
        this.drawBackground = true;
        this.icon = new Drawable[2];
        this.textView = new TextView[2];
        this.crossPaint = new Paint(1);
        this.xRefPaint = new Paint(1);
        this.bitmapPaint = new Paint(1);
        this.radius = radius2;
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
            this.textLayoutContainer.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 4.0f + radius2, 0.0f, 0.0f));
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

    public void setTextSize(int size) {
        for (int i = 0; i < 2; i++) {
            this.textView[i].setTextSize(1, (float) size);
        }
    }

    public void setDrawBackground(boolean value) {
        this.drawBackground = value;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01c7  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x029e  */
    /* JADX WARNING: Removed duplicated region for block: B:74:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0043  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r21) {
        /*
            r20 = this;
            r0 = r20
            r8 = r21
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
            int r1 = r20.getWidth()
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
            float r12 = r1 / r9
            boolean r1 = r0.drawBackground
            if (r1 == 0) goto L_0x0050
            float r1 = r0.radius
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 / r9
            android.graphics.Paint r3 = r0.circlePaint
            r8.drawCircle(r10, r11, r1, r3)
        L_0x0050:
            android.graphics.drawable.Drawable r1 = r0.rippleDrawable
            r13 = 0
            if (r1 != 0) goto L_0x0066
            float r1 = r0.radius
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r1, r13, r3)
            r0.rippleDrawable = r1
            r1.setCallback(r0)
        L_0x0066:
            android.graphics.drawable.Drawable r1 = r0.rippleDrawable
            float r3 = r10 - r12
            int r3 = (int) r3
            float r4 = r11 - r12
            int r4 = (int) r4
            float r5 = r10 + r12
            int r5 = (int) r5
            float r6 = r11 + r12
            int r6 = (int) r6
            r1.setBounds(r3, r4, r5, r6)
            android.graphics.drawable.Drawable r1 = r0.rippleDrawable
            r1.draw(r8)
            int r1 = r0.currentIconRes
            if (r1 == 0) goto L_0x02de
            boolean r1 = r0.drawCross
            r3 = 255(0xff, float:3.57E-43)
            r4 = 1065353216(0x3var_, float:1.0)
            r14 = 2
            if (r1 != 0) goto L_0x0141
            float r1 = r0.crossProgress
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0091
            goto L_0x0141
        L_0x0091:
            r1 = 0
        L_0x0092:
            float r5 = r0.replaceProgress
            r6 = 1
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x00a0
            boolean r5 = r0.iconChangeColor
            if (r5 == 0) goto L_0x009e
            goto L_0x00a0
        L_0x009e:
            r5 = 2
            goto L_0x00a1
        L_0x00a0:
            r5 = 1
        L_0x00a1:
            if (r1 >= r5) goto L_0x02de
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r1]
            if (r5 == 0) goto L_0x013b
            r21.save()
            float r5 = r0.replaceProgress
            int r7 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r7 == 0) goto L_0x00d4
            boolean r7 = r0.iconChangeColor
            if (r7 != 0) goto L_0x00d4
            android.graphics.drawable.Drawable[] r7 = r0.icon
            r15 = r7[r13]
            if (r15 == 0) goto L_0x00d4
            r6 = r7[r6]
            if (r6 == 0) goto L_0x00d4
            if (r1 != 0) goto L_0x00c4
            float r5 = r4 - r5
        L_0x00c4:
            r8.scale(r5, r5, r10, r11)
            android.graphics.drawable.Drawable[] r6 = r0.icon
            r6 = r6[r1]
            r7 = 1132396544(0x437var_, float:255.0)
            float r7 = r7 * r5
            int r7 = (int) r7
            r6.setAlpha(r7)
            goto L_0x00fa
        L_0x00d4:
            boolean r6 = r0.iconChangeColor
            if (r6 == 0) goto L_0x00f3
            int r6 = r0.replaceColorFrom
            int r7 = r0.currentIconColor
            int r5 = androidx.core.graphics.ColorUtils.blendARGB(r6, r7, r5)
            android.graphics.drawable.Drawable[] r6 = r0.icon
            r6 = r6[r1]
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r5, r15)
            r6.setColorFilter(r7)
            android.graphics.Paint r6 = r0.crossPaint
            r6.setColor(r5)
        L_0x00f3:
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r1]
            r5.setAlpha(r3)
        L_0x00fa:
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
            int r7 = r7 / r14
            float r7 = (float) r7
            float r7 = r11 - r7
            int r7 = (int) r7
            android.graphics.drawable.Drawable[] r15 = r0.icon
            r15 = r15[r1]
            int r15 = r15.getIntrinsicWidth()
            int r15 = r15 / r14
            float r15 = (float) r15
            float r15 = r15 + r10
            int r15 = (int) r15
            android.graphics.drawable.Drawable[] r9 = r0.icon
            r9 = r9[r1]
            int r9 = r9.getIntrinsicHeight()
            int r9 = r9 / r14
            float r9 = (float) r9
            float r9 = r9 + r11
            int r9 = (int) r9
            r6.setBounds(r5, r7, r15, r9)
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r1]
            r5.draw(r8)
            r21.restore()
        L_0x013b:
            int r1 = r1 + 1
            r9 = 1073741824(0x40000000, float:2.0)
            goto L_0x0092
        L_0x0141:
            boolean r1 = r0.iconChangeColor
            if (r1 == 0) goto L_0x0162
            int r1 = r0.replaceColorFrom
            int r5 = r0.currentIconColor
            float r6 = r0.replaceProgress
            int r1 = androidx.core.graphics.ColorUtils.blendARGB(r1, r5, r6)
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r13]
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r1, r7)
            r5.setColorFilter(r6)
            android.graphics.Paint r5 = r0.crossPaint
            r5.setColor(r1)
        L_0x0162:
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r1 = r1[r13]
            r1.setAlpha(r3)
            float r1 = r0.replaceProgress
            int r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x018e
            boolean r5 = r0.iconChangeColor
            if (r5 == 0) goto L_0x018e
            int r5 = r0.replaceColorFrom
            int r6 = r0.currentIconColor
            int r1 = androidx.core.graphics.ColorUtils.blendARGB(r5, r6, r1)
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r13]
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r1, r7)
            r5.setColorFilter(r6)
            android.graphics.Paint r5 = r0.crossPaint
            r5.setColor(r1)
        L_0x018e:
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r1 = r1[r13]
            r1.setAlpha(r3)
            boolean r1 = r0.drawCross
            r3 = 1034147594(0x3da3d70a, float:0.08)
            if (r1 == 0) goto L_0x01b0
            float r5 = r0.crossProgress
            int r6 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x01b0
            float r5 = r5 + r3
            r0.crossProgress = r5
            int r1 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r1 <= 0) goto L_0x01ac
            r0.crossProgress = r4
            goto L_0x01c1
        L_0x01ac:
            r20.invalidate()
            goto L_0x01c1
        L_0x01b0:
            if (r1 != 0) goto L_0x01c1
            float r1 = r0.crossProgress
            float r1 = r1 - r3
            r0.crossProgress = r1
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 >= 0) goto L_0x01be
            r0.crossProgress = r2
            goto L_0x01c1
        L_0x01be:
            r20.invalidate()
        L_0x01c1:
            float r1 = r0.crossProgress
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x029e
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r1 = r1[r13]
            int r1 = r1.getIntrinsicWidth()
            float r1 = (float) r1
            r2 = 1073741824(0x40000000, float:2.0)
            float r1 = r1 / r2
            float r1 = r10 - r1
            int r9 = (int) r1
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r1 = r1[r13]
            int r1 = r1.getIntrinsicHeight()
            int r1 = r1 / r14
            float r1 = (float) r1
            float r1 = r11 - r1
            int r15 = (int) r1
            float r1 = (float) r9
            r2 = 1090519040(0x41000000, float:8.0)
            float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
            float r1 = r1 + r3
            float r3 = r0.crossOffset
            float r16 = r1 + r3
            float r1 = (float) r15
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
            float r17 = r1 + r2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r1 = (float) r1
            float r1 = r16 - r1
            r2 = 1099431936(0x41880000, float:17.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            float r5 = r0.crossProgress
            float r4 = r4.getInterpolation(r5)
            float r3 = r3 * r4
            float r18 = r1 + r3
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r1 = (float) r1
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            float r3 = r0.crossProgress
            float r2 = r2.getInterpolation(r3)
            float r1 = r1 * r2
            float r19 = r17 + r1
            r2 = 0
            r3 = 0
            int r1 = r20.getMeasuredWidth()
            float r4 = (float) r1
            int r1 = r20.getMeasuredHeight()
            float r5 = (float) r1
            r6 = 255(0xff, float:3.57E-43)
            r7 = 31
            r1 = r21
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r2 = r1[r13]
            r1 = r1[r13]
            int r1 = r1.getIntrinsicWidth()
            float r1 = (float) r1
            r3 = 1073741824(0x40000000, float:2.0)
            float r1 = r1 / r3
            float r1 = r10 - r1
            int r1 = (int) r1
            android.graphics.drawable.Drawable[] r3 = r0.icon
            r3 = r3[r13]
            int r3 = r3.getIntrinsicHeight()
            int r3 = r3 / r14
            float r3 = (float) r3
            float r3 = r11 - r3
            int r3 = (int) r3
            android.graphics.drawable.Drawable[] r4 = r0.icon
            r4 = r4[r13]
            int r4 = r4.getIntrinsicWidth()
            int r4 = r4 / r14
            float r4 = (float) r4
            float r4 = r4 + r10
            int r4 = (int) r4
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r13]
            int r5 = r5.getIntrinsicHeight()
            int r5 = r5 / r14
            float r5 = (float) r5
            float r5 = r5 + r11
            int r5 = (int) r5
            r2.setBounds(r1, r3, r4, r5)
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r1 = r1[r13]
            r1.draw(r8)
            r1 = 1073741824(0x40000000, float:2.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r2 = (float) r2
            float r3 = r17 - r2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r5 = r19 - r1
            android.graphics.Paint r6 = r0.xRefPaint
            r1 = r21
            r2 = r16
            r4 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.crossPaint
            r3 = r17
            r5 = r19
            r1.drawLine(r2, r3, r4, r5, r6)
            r21.restore()
            goto L_0x02de
        L_0x029e:
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r2 = r1[r13]
            r1 = r1[r13]
            int r1 = r1.getIntrinsicWidth()
            float r1 = (float) r1
            r3 = 1073741824(0x40000000, float:2.0)
            float r1 = r1 / r3
            float r1 = r10 - r1
            int r1 = (int) r1
            android.graphics.drawable.Drawable[] r3 = r0.icon
            r3 = r3[r13]
            int r3 = r3.getIntrinsicHeight()
            int r3 = r3 / r14
            float r3 = (float) r3
            float r3 = r11 - r3
            int r3 = (int) r3
            android.graphics.drawable.Drawable[] r4 = r0.icon
            r4 = r4[r13]
            int r4 = r4.getIntrinsicWidth()
            int r4 = r4 / r14
            float r4 = (float) r4
            float r4 = r4 + r10
            int r4 = (int) r4
            android.graphics.drawable.Drawable[] r5 = r0.icon
            r5 = r5[r13]
            int r5 = r5.getIntrinsicHeight()
            int r5 = r5 / r14
            float r5 = (float) r5
            float r5 = r5 + r11
            int r5 = (int) r5
            r2.setBounds(r1, r3, r4, r5)
            android.graphics.drawable.Drawable[] r1 = r0.icon
            r1 = r1[r13]
            r1.draw(r8)
        L_0x02de:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPToggleButton.onDraw(android.graphics.Canvas):void");
    }

    public void setBackgroundColor(int backgroundColor2, int backgroundColorChecked) {
        this.backgroundCheck1 = backgroundColor2;
        this.backgroundCheck2 = backgroundColorChecked;
        this.backgroundColor = ColorUtils.blendARGB(backgroundColor2, backgroundColorChecked, this.checkedProgress);
        invalidate();
    }

    public void setData(int iconRes, int iconColor, int backgroundColor2, String text, boolean cross, boolean animated) {
        setData(iconRes, iconColor, backgroundColor2, 1.0f, true, text, cross, animated);
    }

    public void setEnabled(boolean enabled, boolean animated) {
        super.setEnabled(enabled);
        float f = 1.0f;
        if (animated) {
            ViewPropertyAnimator animate = animate();
            if (!enabled) {
                f = 0.5f;
            }
            animate.alpha(f).setDuration(180).start();
            return;
        }
        clearAnimation();
        if (!enabled) {
            f = 0.5f;
        }
        setAlpha(f);
    }

    public void setData(int iconRes, int iconColor, int backgroundColor2, float selectorAlpha, boolean recreateRipple, String text, boolean cross, boolean animated) {
        String str;
        if (getVisibility() != 0) {
            animated = false;
            setVisibility(0);
        }
        if (this.currentIconRes != iconRes || this.currentIconColor != iconColor || ((!this.checkable && this.currentBackgroundColor != backgroundColor2) || (str = this.currentText) == null || !str.equals(text) || cross != this.drawCross)) {
            if (this.rippleDrawable == null || recreateRipple) {
                if (Color.alpha(backgroundColor2) != 255 || ((double) AndroidUtilities.computePerceivedBrightness(backgroundColor2)) <= 0.5d) {
                    Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(this.radius), 0, ColorUtils.setAlphaComponent(-1, (int) (76.5f * selectorAlpha)));
                    this.rippleDrawable = createSimpleSelectorCircleDrawable;
                    createSimpleSelectorCircleDrawable.setCallback(this);
                } else {
                    Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(this.radius), 0, ColorUtils.setAlphaComponent(-16777216, (int) (25.5f * selectorAlpha)));
                    this.rippleDrawable = createSimpleSelectorCircleDrawable2;
                    createSimpleSelectorCircleDrawable2.setCallback(this);
                }
            }
            ValueAnimator valueAnimator = this.replaceAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.animateBackground = this.currentBackgroundColor != backgroundColor2;
            boolean z = this.currentIconRes == iconRes;
            this.iconChangeColor = z;
            if (z) {
                this.replaceColorFrom = this.currentIconColor;
            }
            this.currentIconRes = iconRes;
            this.currentIconColor = iconColor;
            this.currentBackgroundColor = backgroundColor2;
            this.currentText = text;
            this.drawCross = cross;
            if (!animated) {
                if (iconRes != 0) {
                    this.icon[0] = ContextCompat.getDrawable(getContext(), iconRes).mutate();
                    this.icon[0].setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
                }
                this.crossPaint.setColor(iconColor);
                if (!this.checkable) {
                    this.backgroundColor = backgroundColor2;
                }
                this.textView[0].setText(text);
                this.crossProgress = this.drawCross ? 1.0f : 0.0f;
                this.iconChangeColor = false;
                this.replaceProgress = 0.0f;
                invalidate();
                return;
            }
            if (!z && iconRes != 0) {
                this.icon[1] = ContextCompat.getDrawable(getContext(), iconRes).mutate();
                this.icon[1].setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
            }
            if (!this.checkable) {
                this.animateToBackgroundColor = backgroundColor2;
            }
            final boolean animateText = !this.textView[0].getText().toString().equals(text);
            if (!animateText) {
                this.textView[0].setText(text);
            } else {
                this.textView[1].setText(text);
                this.textView[1].setVisibility(0);
                this.textView[1].setAlpha(0.0f);
                this.textView[1].setScaleX(0.0f);
                this.textView[1].setScaleY(0.0f);
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.replaceAnimator = ofFloat;
            ofFloat.addUpdateListener(new VoIPToggleButton$$ExternalSyntheticLambda1(this, animateText));
            this.replaceAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    VoIPToggleButton.this.replaceAnimator = null;
                    if (animateText) {
                        TextView tv = VoIPToggleButton.this.textView[0];
                        VoIPToggleButton.this.textView[0] = VoIPToggleButton.this.textView[1];
                        VoIPToggleButton.this.textView[1] = tv;
                        VoIPToggleButton.this.textView[1].setVisibility(8);
                    }
                    if (!VoIPToggleButton.this.iconChangeColor && VoIPToggleButton.this.icon[1] != null) {
                        VoIPToggleButton.this.icon[0] = VoIPToggleButton.this.icon[1];
                        VoIPToggleButton.this.icon[1] = null;
                    }
                    boolean unused = VoIPToggleButton.this.iconChangeColor = false;
                    if (!VoIPToggleButton.this.checkable) {
                        VoIPToggleButton voIPToggleButton = VoIPToggleButton.this;
                        voIPToggleButton.backgroundColor = voIPToggleButton.animateToBackgroundColor;
                    }
                    VoIPToggleButton.this.replaceProgress = 0.0f;
                    VoIPToggleButton.this.invalidate();
                }
            });
            this.replaceAnimator.setDuration(150).start();
            invalidate();
        }
    }

    /* renamed from: lambda$setData$0$org-telegram-ui-Components-voip-VoIPToggleButton  reason: not valid java name */
    public /* synthetic */ void m4591xb3bc3dbb(boolean animateText, ValueAnimator valueAnimator) {
        this.replaceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (animateText) {
            this.textView[0].setAlpha(1.0f - this.replaceProgress);
            this.textView[0].setScaleX(1.0f - this.replaceProgress);
            this.textView[0].setScaleY(1.0f - this.replaceProgress);
            this.textView[1].setAlpha(this.replaceProgress);
            this.textView[1].setScaleX(this.replaceProgress);
            this.textView[1].setScaleY(this.replaceProgress);
        }
    }

    public void setCrossOffset(float crossOffset2) {
        this.crossOffset = crossOffset2;
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

    public void setCheckableForAccessibility(boolean checkableForAccessibility2) {
        this.checkableForAccessibility = checkableForAccessibility2;
    }

    public void setCheckable(boolean checkable2) {
        this.checkable = checkable2;
    }

    public void setChecked(boolean value, boolean animated) {
        if (this.checked != value) {
            this.checked = value;
            if (this.checkable) {
                float f = 1.0f;
                if (animated) {
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
                        public void onAnimationEnd(Animator animation) {
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
                if (!value) {
                    f = 0.0f;
                }
                this.checkedProgress = f;
                setBackgroundColor(this.backgroundCheck1, this.backgroundCheck2);
            }
        }
    }

    /* renamed from: lambda$setChecked$1$org-telegram-ui-Components-voip-VoIPToggleButton  reason: not valid java name */
    public /* synthetic */ void m4590xb7677e67(ValueAnimator valueAnimator) {
        this.checkedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        setBackgroundColor(this.backgroundCheck1, this.backgroundCheck2);
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setText(this.currentText);
        if (this.checkable || this.checkableForAccessibility) {
            info.setClassName(ToggleButton.class.getName());
            info.setCheckable(true);
            info.setChecked(this.checked);
            return;
        }
        info.setClassName(Button.class.getName());
    }

    public void shakeView() {
        AndroidUtilities.shakeView(this.textView[0], 2.0f, 0);
        AndroidUtilities.shakeView(this.textView[1], 2.0f, 0);
    }

    public void showText(boolean show, boolean animated) {
        float a = 1.0f;
        if (animated) {
            if (!show) {
                a = 0.0f;
            }
            if (this.textLayoutContainer.getAlpha() != a) {
                this.textLayoutContainer.animate().alpha(a).start();
                return;
            }
            return;
        }
        this.textLayoutContainer.animate().cancel();
        FrameLayout frameLayout = this.textLayoutContainer;
        if (!show) {
            a = 0.0f;
        }
        frameLayout.setAlpha(a);
    }
}
