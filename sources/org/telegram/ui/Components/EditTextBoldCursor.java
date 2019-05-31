package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import androidx.annotation.Keep;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;

public class EditTextBoldCursor extends EditText {
    private static Class editorClass;
    private static Method getVerticalOffsetMethod;
    private static Field mCursorDrawableField;
    private static Field mCursorDrawableResField;
    private static Field mEditor;
    private static Field mScrollYField;
    private static Field mShowCursorField;
    private int activeLineColor;
    private boolean allowDrawCursor = true;
    private boolean currentDrawHintAsHeader;
    private int cursorSize;
    private float cursorWidth = 2.0f;
    private Object editor;
    private StaticLayout errorLayout;
    private int errorLineColor;
    private TextPaint errorPaint;
    private CharSequence errorText;
    private boolean fixed;
    private GradientDrawable gradientDrawable;
    private float headerAnimationProgress;
    private int headerHintColor;
    private AnimatorSet headerTransformAnimation;
    private float hintAlpha = 1.0f;
    private int hintColor;
    private StaticLayout hintLayout;
    private boolean hintVisible = true;
    private int ignoreBottomCount;
    private int ignoreTopCount;
    private long lastUpdateTime;
    private int lineColor;
    private Paint linePaint;
    private float lineSpacingExtra;
    private float lineY;
    private OnPreDrawListener listenerFixer;
    private Drawable mCursorDrawable;
    private boolean nextSetTextAnimated;
    private Rect rect = new Rect();
    private int scrollY;
    private boolean supportRtlHint;
    private boolean transformHintToHeader;

    @TargetApi(26)
    public int getAutofillType() {
        return 0;
    }

    public EditTextBoldCursor(Context context) {
        super(context);
        if (VERSION.SDK_INT >= 26) {
            setImportantForAutofill(2);
        }
        init();
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0040 A:{Catch:{ Throwable -> 0x004f }} */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0054 A:{Catch:{ Throwable -> 0x00d1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00f3 A:{Catch:{ Throwable -> 0x00ff }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x003c */
    /* JADX WARNING: Failed to process nested try/catch */
    @android.annotation.SuppressLint({"PrivateApi"})
    private void init() {
        /*
        r8 = this;
        r0 = "getVerticalOffset";
        r1 = "mShowCursor";
        r2 = new android.graphics.Paint;
        r2.<init>();
        r8.linePaint = r2;
        r2 = new android.text.TextPaint;
        r3 = 1;
        r2.<init>(r3);
        r8.errorPaint = r2;
        r2 = r8.errorPaint;
        r4 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r2.setTextSize(r4);
        r2 = android.os.Build.VERSION.SDK_INT;
        r4 = 2;
        r5 = 26;
        if (r2 < r5) goto L_0x0029;
    L_0x0026:
        r8.setImportantForAutofill(r4);
    L_0x0029:
        r2 = mScrollYField;	 Catch:{ Throwable -> 0x003c }
        if (r2 != 0) goto L_0x003c;
    L_0x002d:
        r2 = android.view.View.class;
        r5 = "mScrollY";
        r2 = r2.getDeclaredField(r5);	 Catch:{ Throwable -> 0x003c }
        mScrollYField = r2;	 Catch:{ Throwable -> 0x003c }
        r2 = mScrollYField;	 Catch:{ Throwable -> 0x003c }
        r2.setAccessible(r3);	 Catch:{ Throwable -> 0x003c }
    L_0x003c:
        r2 = mCursorDrawableResField;	 Catch:{ Throwable -> 0x004f }
        if (r2 != 0) goto L_0x004f;
    L_0x0040:
        r2 = android.widget.TextView.class;
        r5 = "mCursorDrawableRes";
        r2 = r2.getDeclaredField(r5);	 Catch:{ Throwable -> 0x004f }
        mCursorDrawableResField = r2;	 Catch:{ Throwable -> 0x004f }
        r2 = mCursorDrawableResField;	 Catch:{ Throwable -> 0x004f }
        r2.setAccessible(r3);	 Catch:{ Throwable -> 0x004f }
    L_0x004f:
        r2 = 0;
        r5 = editorClass;	 Catch:{ Throwable -> 0x00d1 }
        if (r5 != 0) goto L_0x00d5;
    L_0x0054:
        r5 = android.widget.TextView.class;
        r6 = "mEditor";
        r5 = r5.getDeclaredField(r6);	 Catch:{ Throwable -> 0x00d1 }
        mEditor = r5;	 Catch:{ Throwable -> 0x00d1 }
        r5 = mEditor;	 Catch:{ Throwable -> 0x00d1 }
        r5.setAccessible(r3);	 Catch:{ Throwable -> 0x00d1 }
        r5 = "android.widget.Editor";
        r5 = java.lang.Class.forName(r5);	 Catch:{ Throwable -> 0x00d1 }
        editorClass = r5;	 Catch:{ Throwable -> 0x00d1 }
        r5 = editorClass;	 Catch:{ Throwable -> 0x00d1 }
        r5 = r5.getDeclaredField(r1);	 Catch:{ Throwable -> 0x00d1 }
        mShowCursorField = r5;	 Catch:{ Throwable -> 0x00d1 }
        r5 = mShowCursorField;	 Catch:{ Throwable -> 0x00d1 }
        r5.setAccessible(r3);	 Catch:{ Throwable -> 0x00d1 }
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x00d1 }
        r6 = 28;
        if (r5 < r6) goto L_0x008e;
    L_0x007e:
        r5 = editorClass;	 Catch:{ Throwable -> 0x00d1 }
        r6 = "mDrawableForCursor";
        r5 = r5.getDeclaredField(r6);	 Catch:{ Throwable -> 0x00d1 }
        mCursorDrawableField = r5;	 Catch:{ Throwable -> 0x00d1 }
        r5 = mCursorDrawableField;	 Catch:{ Throwable -> 0x00d1 }
        r5.setAccessible(r3);	 Catch:{ Throwable -> 0x00d1 }
        goto L_0x009d;
    L_0x008e:
        r5 = editorClass;	 Catch:{ Throwable -> 0x00d1 }
        r6 = "mCursorDrawable";
        r5 = r5.getDeclaredField(r6);	 Catch:{ Throwable -> 0x00d1 }
        mCursorDrawableField = r5;	 Catch:{ Throwable -> 0x00d1 }
        r5 = mCursorDrawableField;	 Catch:{ Throwable -> 0x00d1 }
        r5.setAccessible(r3);	 Catch:{ Throwable -> 0x00d1 }
    L_0x009d:
        r5 = android.widget.TextView.class;
        r6 = new java.lang.Class[r3];	 Catch:{ Throwable -> 0x00d1 }
        r7 = java.lang.Boolean.TYPE;	 Catch:{ Throwable -> 0x00d1 }
        r6[r2] = r7;	 Catch:{ Throwable -> 0x00d1 }
        r5 = r5.getDeclaredMethod(r0, r6);	 Catch:{ Throwable -> 0x00d1 }
        getVerticalOffsetMethod = r5;	 Catch:{ Throwable -> 0x00d1 }
        r5 = getVerticalOffsetMethod;	 Catch:{ Throwable -> 0x00d1 }
        r5.setAccessible(r3);	 Catch:{ Throwable -> 0x00d1 }
        r5 = editorClass;	 Catch:{ Throwable -> 0x00d1 }
        r1 = r5.getDeclaredField(r1);	 Catch:{ Throwable -> 0x00d1 }
        mShowCursorField = r1;	 Catch:{ Throwable -> 0x00d1 }
        r1 = mShowCursorField;	 Catch:{ Throwable -> 0x00d1 }
        r1.setAccessible(r3);	 Catch:{ Throwable -> 0x00d1 }
        r1 = android.widget.TextView.class;
        r5 = new java.lang.Class[r3];	 Catch:{ Throwable -> 0x00d1 }
        r6 = java.lang.Boolean.TYPE;	 Catch:{ Throwable -> 0x00d1 }
        r5[r2] = r6;	 Catch:{ Throwable -> 0x00d1 }
        r0 = r1.getDeclaredMethod(r0, r5);	 Catch:{ Throwable -> 0x00d1 }
        getVerticalOffsetMethod = r0;	 Catch:{ Throwable -> 0x00d1 }
        r0 = getVerticalOffsetMethod;	 Catch:{ Throwable -> 0x00d1 }
        r0.setAccessible(r3);	 Catch:{ Throwable -> 0x00d1 }
        goto L_0x00d5;
    L_0x00d1:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00d5:
        r0 = new android.graphics.drawable.GradientDrawable;	 Catch:{ Throwable -> 0x00ff }
        r1 = android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM;	 Catch:{ Throwable -> 0x00ff }
        r4 = new int[r4];	 Catch:{ Throwable -> 0x00ff }
        r5 = -11230757; // 0xfffffffffvar_a1db float:-2.8263674E38 double:NaN;
        r4[r2] = r5;	 Catch:{ Throwable -> 0x00ff }
        r4[r3] = r5;	 Catch:{ Throwable -> 0x00ff }
        r0.<init>(r1, r4);	 Catch:{ Throwable -> 0x00ff }
        r8.gradientDrawable = r0;	 Catch:{ Throwable -> 0x00ff }
        r0 = mEditor;	 Catch:{ Throwable -> 0x00ff }
        r0 = r0.get(r8);	 Catch:{ Throwable -> 0x00ff }
        r8.editor = r0;	 Catch:{ Throwable -> 0x00ff }
        r0 = mCursorDrawableField;	 Catch:{ Throwable -> 0x00ff }
        if (r0 == 0) goto L_0x00ff;
    L_0x00f3:
        r0 = mCursorDrawableResField;	 Catch:{ Throwable -> 0x00ff }
        r1 = NUM; // 0x7var_d2 float:1.7945004E38 double:1.052935607E-314;
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Throwable -> 0x00ff }
        r0.set(r8, r1);	 Catch:{ Throwable -> 0x00ff }
    L_0x00ff:
        r0 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r8.cursorSize = r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EditTextBoldCursor.init():void");
    }

    @SuppressLint({"PrivateApi"})
    public void fixHandleView(boolean z) {
        if (z) {
            this.fixed = false;
        } else if (!this.fixed) {
            try {
                if (editorClass == null) {
                    editorClass = Class.forName("android.widget.Editor");
                    mEditor = TextView.class.getDeclaredField("mEditor");
                    mEditor.setAccessible(true);
                    this.editor = mEditor.get(this);
                }
                if (this.listenerFixer == null) {
                    Method declaredMethod = editorClass.getDeclaredMethod("getPositionListener", new Class[0]);
                    declaredMethod.setAccessible(true);
                    this.listenerFixer = (OnPreDrawListener) declaredMethod.invoke(this.editor, new Object[0]);
                }
                OnPreDrawListener onPreDrawListener = this.listenerFixer;
                onPreDrawListener.getClass();
                AndroidUtilities.runOnUIThread(new -$$Lambda$qzh_QoBZ7K2XdUWK2VAJcGTe1OY(onPreDrawListener), 500);
            } catch (Throwable unused) {
            }
            this.fixed = true;
        }
    }

    public void setTransformHintToHeader(boolean z) {
        if (this.transformHintToHeader != z) {
            this.transformHintToHeader = z;
            AnimatorSet animatorSet = this.headerTransformAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.headerTransformAnimation = null;
            }
        }
    }

    public void setAllowDrawCursor(boolean z) {
        this.allowDrawCursor = z;
        invalidate();
    }

    public void setCursorWidth(float f) {
        this.cursorWidth = f;
    }

    public void setCursorColor(int i) {
        this.gradientDrawable.setColor(i);
        invalidate();
    }

    public void setCursorSize(int i) {
        this.cursorSize = i;
    }

    public void setErrorLineColor(int i) {
        this.errorLineColor = i;
        this.errorPaint.setColor(this.errorLineColor);
        invalidate();
    }

    public void setLineColors(int i, int i2, int i3) {
        this.lineColor = i;
        this.activeLineColor = i2;
        this.errorLineColor = i3;
        this.errorPaint.setColor(this.errorLineColor);
        invalidate();
    }

    public void setHintVisible(boolean z) {
        if (this.hintVisible != z) {
            this.lastUpdateTime = System.currentTimeMillis();
            this.hintVisible = z;
            invalidate();
        }
    }

    public void setHintColor(int i) {
        this.hintColor = i;
        invalidate();
    }

    public void setHeaderHintColor(int i) {
        this.headerHintColor = i;
        invalidate();
    }

    public void setNextSetTextAnimated(boolean z) {
        this.nextSetTextAnimated = z;
    }

    public void setErrorText(CharSequence charSequence) {
        if (!TextUtils.equals(charSequence, this.errorText)) {
            this.errorText = charSequence;
            requestLayout();
        }
    }

    public boolean requestFocus(int i, Rect rect) {
        return super.requestFocus(i, rect);
    }

    public boolean hasErrorText() {
        return TextUtils.isEmpty(this.errorText) ^ 1;
    }

    public StaticLayout getErrorLayout(int i) {
        if (TextUtils.isEmpty(this.errorText)) {
            return null;
        }
        return new StaticLayout(this.errorText, this.errorPaint, i, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    public float getLineY() {
        return this.lineY;
    }

    public void setSupportRtlHint(boolean z) {
        this.supportRtlHint = z;
    }

    public void setText(CharSequence charSequence, BufferType bufferType) {
        super.setText(charSequence, bufferType);
        checkHeaderVisibility(this.nextSetTextAnimated);
        this.nextSetTextAnimated = false;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.hintLayout != null) {
            this.lineY = ((((float) (getMeasuredHeight() - this.hintLayout.getHeight())) / 2.0f) + ((float) this.hintLayout.getHeight())) + ((float) AndroidUtilities.dp(6.0f));
        }
    }

    public void setHintText(String str) {
        this.hintLayout = new StaticLayout(str, getPaint(), AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    /* Access modifiers changed, original: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        checkHeaderVisibility(true);
    }

    private void checkHeaderVisibility(boolean z) {
        boolean z2 = this.transformHintToHeader && (isFocused() || getText().length() > 0);
        if (this.currentDrawHintAsHeader != z2) {
            AnimatorSet animatorSet = this.headerTransformAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.headerTransformAnimation = null;
            }
            this.currentDrawHintAsHeader = z2;
            float f = 1.0f;
            if (z) {
                this.headerTransformAnimation = new AnimatorSet();
                AnimatorSet animatorSet2 = this.headerTransformAnimation;
                Animator[] animatorArr = new Animator[1];
                float[] fArr = new float[1];
                if (!z2) {
                    f = 0.0f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(this, "headerAnimationProgress", fArr);
                animatorSet2.playTogether(animatorArr);
                this.headerTransformAnimation.setDuration(200);
                this.headerTransformAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.headerTransformAnimation.start();
            } else {
                if (!z2) {
                    f = 0.0f;
                }
                this.headerAnimationProgress = f;
            }
            invalidate();
        }
    }

    @Keep
    public void setHeaderAnimationProgress(float f) {
        this.headerAnimationProgress = f;
        invalidate();
    }

    @Keep
    public float getHeaderAnimationProgress() {
        return this.headerAnimationProgress;
    }

    public void setLineSpacing(float f, float f2) {
        super.setLineSpacing(f, f2);
        this.lineSpacingExtra = f;
    }

    public int getExtendedPaddingTop() {
        int i = this.ignoreTopCount;
        if (i == 0) {
            return super.getExtendedPaddingTop();
        }
        this.ignoreTopCount = i - 1;
        return 0;
    }

    public int getExtendedPaddingBottom() {
        int i = this.ignoreBottomCount;
        if (i == 0) {
            return super.getExtendedPaddingBottom();
        }
        this.ignoreBottomCount = i - 1;
        i = this.scrollY;
        return i != Integer.MAX_VALUE ? -i : 0;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        int alpha;
        Canvas canvas2 = canvas;
        int extendedPaddingTop = getExtendedPaddingTop();
        this.scrollY = Integer.MAX_VALUE;
        try {
            this.scrollY = mScrollYField.getInt(this);
            mScrollYField.set(this, Integer.valueOf(0));
        } catch (Exception unused) {
        }
        this.ignoreTopCount = 1;
        this.ignoreBottomCount = 1;
        canvas.save();
        canvas2.translate(0.0f, (float) extendedPaddingTop);
        try {
            super.onDraw(canvas);
        } catch (Exception unused2) {
        }
        extendedPaddingTop = this.scrollY;
        if (extendedPaddingTop != Integer.MAX_VALUE) {
            try {
                mScrollYField.set(this, Integer.valueOf(extendedPaddingTop));
            } catch (Exception unused3) {
            }
        }
        canvas.restore();
        if ((length() == 0 || this.transformHintToHeader) && this.hintLayout != null && (this.hintVisible || this.hintAlpha != 0.0f)) {
            if ((this.hintVisible && this.hintAlpha != 1.0f) || !(this.hintVisible || this.hintAlpha == 0.0f)) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                if (j < 0 || j > 17) {
                    j = 17;
                }
                this.lastUpdateTime = currentTimeMillis;
                if (this.hintVisible) {
                    this.hintAlpha += ((float) j) / 150.0f;
                    if (this.hintAlpha > 1.0f) {
                        this.hintAlpha = 1.0f;
                    }
                } else {
                    this.hintAlpha -= ((float) j) / 150.0f;
                    if (this.hintAlpha < 0.0f) {
                        this.hintAlpha = 0.0f;
                    }
                }
                invalidate();
            }
            extendedPaddingTop = getPaint().getColor();
            canvas.save();
            float lineLeft = this.hintLayout.getLineLeft(0);
            float lineWidth = this.hintLayout.getLineWidth(0);
            int i = lineLeft != 0.0f ? (int) (((float) null) - lineLeft) : 0;
            if (this.supportRtlHint && LocaleController.isRTL) {
                canvas2.translate(((float) (i + getScrollX())) + (((float) getMeasuredWidth()) - lineWidth), (this.lineY - ((float) this.hintLayout.getHeight())) - ((float) AndroidUtilities.dp(6.0f)));
            } else {
                canvas2.translate((float) (i + getScrollX()), (this.lineY - ((float) this.hintLayout.getHeight())) - ((float) AndroidUtilities.dp(6.0f)));
            }
            if (this.transformHintToHeader) {
                float f;
                float f2 = 1.0f - (this.headerAnimationProgress * 0.3f);
                float f3 = ((float) (-AndroidUtilities.dp(22.0f))) * this.headerAnimationProgress;
                int red = Color.red(this.headerHintColor);
                int green = Color.green(this.headerHintColor);
                int blue = Color.blue(this.headerHintColor);
                int alpha2 = Color.alpha(this.headerHintColor);
                int red2 = Color.red(this.hintColor);
                int green2 = Color.green(this.hintColor);
                int blue2 = Color.blue(this.hintColor);
                alpha = Color.alpha(this.hintColor);
                if (this.supportRtlHint && LocaleController.isRTL) {
                    lineWidth += lineLeft;
                    f = 0.0f;
                    canvas2.translate(lineWidth - (lineWidth * f2), 0.0f);
                } else {
                    f = 0.0f;
                    if (lineLeft != 0.0f) {
                        canvas2.translate(lineLeft * (1.0f - f2), 0.0f);
                    }
                }
                canvas2.scale(f2, f2);
                canvas2.translate(f, f3);
                TextPaint paint = getPaint();
                lineLeft = (float) alpha;
                float f4 = (float) (alpha2 - alpha);
                lineWidth = this.headerAnimationProgress;
                paint.setColor(Color.argb((int) (lineLeft + (f4 * lineWidth)), (int) (((float) red2) + (((float) (red - red2)) * lineWidth)), (int) (((float) green2) + (((float) (green - green2)) * lineWidth)), (int) (((float) blue2) + (((float) (blue - blue2)) * lineWidth))));
            } else {
                getPaint().setColor(this.hintColor);
                getPaint().setAlpha((int) ((this.hintAlpha * 255.0f) * (((float) Color.alpha(this.hintColor)) / 255.0f)));
            }
            this.hintLayout.draw(canvas2);
            getPaint().setColor(extendedPaddingTop);
            canvas.restore();
        }
        try {
            if (this.allowDrawCursor && mShowCursorField != null) {
                if (this.mCursorDrawable == null) {
                    if (VERSION.SDK_INT >= 28) {
                        this.mCursorDrawable = (Drawable) mCursorDrawableField.get(this.editor);
                    } else {
                        this.mCursorDrawable = ((Drawable[]) mCursorDrawableField.get(this.editor))[0];
                    }
                }
                if (this.mCursorDrawable != null) {
                    Object obj = ((SystemClock.uptimeMillis() - mShowCursorField.getLong(this.editor)) % 1000 >= 500 || !isFocused()) ? null : 1;
                    if (obj != null) {
                        canvas.save();
                        canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + ((getGravity() & 112) != 48 ? ((Integer) getVerticalOffsetMethod.invoke(this, new Object[]{Boolean.valueOf(true)})).intValue() : 0)));
                        Layout layout = getLayout();
                        alpha = layout.getLineForOffset(getSelectionStart());
                        extendedPaddingTop = layout.getLineCount();
                        Rect bounds = this.mCursorDrawable.getBounds();
                        this.rect.left = bounds.left;
                        this.rect.right = bounds.left + AndroidUtilities.dp(this.cursorWidth);
                        this.rect.bottom = bounds.bottom;
                        this.rect.top = bounds.top;
                        if (this.lineSpacingExtra != 0.0f && alpha < extendedPaddingTop - 1) {
                            Rect rect = this.rect;
                            rect.bottom = (int) (((float) rect.bottom) - this.lineSpacingExtra);
                        }
                        this.rect.top = this.rect.centerY() - (this.cursorSize / 2);
                        this.rect.bottom = this.rect.top + this.cursorSize;
                        this.gradientDrawable.setBounds(this.rect);
                        this.gradientDrawable.draw(canvas2);
                        canvas.restore();
                    }
                } else {
                    return;
                }
            }
        } catch (Throwable unused4) {
        }
        if (!(this.lineColor == 0 || this.hintLayout == null)) {
            if (!TextUtils.isEmpty(this.errorText)) {
                this.linePaint.setColor(this.errorLineColor);
                extendedPaddingTop = AndroidUtilities.dp(2.0f);
            } else if (isFocused()) {
                this.linePaint.setColor(this.activeLineColor);
                extendedPaddingTop = AndroidUtilities.dp(2.0f);
            } else {
                this.linePaint.setColor(this.lineColor);
                extendedPaddingTop = AndroidUtilities.dp(1.0f);
            }
            canvas.drawRect((float) getScrollX(), (float) ((int) this.lineY), (float) (getScrollX() + getMeasuredWidth()), this.lineY + ((float) extendedPaddingTop), this.linePaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.EditText");
        StaticLayout staticLayout = this.hintLayout;
        if (staticLayout != null) {
            accessibilityNodeInfo.setContentDescription(staticLayout.getText());
        }
    }
}
