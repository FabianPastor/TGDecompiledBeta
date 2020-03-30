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
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Keep;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.FloatingActionMode;
import org.telegram.ui.ActionBar.FloatingToolbar;

public class EditTextBoldCursor extends EditText {
    private static Class editorClass;
    private static Method getVerticalOffsetMethod;
    private static Field mCursorDrawableResField;
    private static Field mEditor;
    private static Field mScrollYField;
    private static Field mShowCursorField;
    private int activeLineColor;
    private boolean allowDrawCursor = true;
    /* access modifiers changed from: private */
    public View attachedToWindow;
    private boolean currentDrawHintAsHeader;
    private int cursorSize;
    private float cursorWidth = 2.0f;
    private Object editor;
    private int errorLineColor;
    private TextPaint errorPaint;
    private CharSequence errorText;
    private boolean fixed;
    /* access modifiers changed from: private */
    public FloatingActionMode floatingActionMode;
    private FloatingToolbar floatingToolbar;
    private ViewTreeObserver.OnPreDrawListener floatingToolbarPreDrawListener;
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
    private Runnable invalidateRunnable = new Runnable() {
        public void run() {
            EditTextBoldCursor.this.invalidate();
            if (EditTextBoldCursor.this.attachedToWindow != null) {
                AndroidUtilities.runOnUIThread(this, 500);
            }
        }
    };
    private long lastUpdateTime;
    private int lineColor;
    private Paint linePaint;
    private float lineSpacingExtra;
    private float lineY;
    private ViewTreeObserver.OnPreDrawListener listenerFixer;
    private Rect mTempRect;
    private boolean nextSetTextAnimated;
    private Rect rect = new Rect();
    private int scrollY;
    private boolean supportRtlHint;
    private boolean transformHintToHeader;
    private View windowView;

    /* access modifiers changed from: protected */
    public void extendActionMode(ActionMode actionMode, Menu menu) {
    }

    /* access modifiers changed from: protected */
    public int getActionModeStyle() {
        return 1;
    }

    @TargetApi(26)
    public int getAutofillType() {
        return 0;
    }

    @TargetApi(23)
    private class ActionModeCallback2Wrapper extends ActionMode.Callback2 {
        private final ActionMode.Callback mWrapped;

        public ActionModeCallback2Wrapper(ActionMode.Callback callback) {
            this.mWrapped = callback;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return this.mWrapped.onCreateActionMode(actionMode, menu);
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(actionMode, menu);
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return this.mWrapped.onActionItemClicked(actionMode, menuItem);
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            this.mWrapped.onDestroyActionMode(actionMode);
            EditTextBoldCursor.this.cleanupFloatingActionModeViews();
            FloatingActionMode unused = EditTextBoldCursor.this.floatingActionMode = null;
        }

        public void onGetContentRect(ActionMode actionMode, View view, Rect rect) {
            ActionMode.Callback callback = this.mWrapped;
            if (callback instanceof ActionMode.Callback2) {
                ((ActionMode.Callback2) callback).onGetContentRect(actionMode, view, rect);
            } else {
                super.onGetContentRect(actionMode, view, rect);
            }
        }
    }

    public EditTextBoldCursor(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= 26) {
            setImportantForAutofill(2);
        }
        init();
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(18:0|(1:2)|(3:3|4|(1:6))|7|9|10|(1:12)|15|16|(1:18)|19|20|21|(1:23)|24|(1:26)|27|29) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:20:0x009f */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00a3 A[Catch:{ all -> 0x00c0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00b4 A[Catch:{ all -> 0x00c0 }] */
    @android.annotation.SuppressLint({"PrivateApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void init() {
        /*
            r8 = this;
            java.lang.String r0 = "mShowCursor"
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>()
            r8.linePaint = r1
            android.text.TextPaint r1 = new android.text.TextPaint
            r2 = 1
            r1.<init>(r2)
            r8.errorPaint = r1
            r3 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r1.setTextSize(r3)
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 2
            r4 = 26
            if (r1 < r4) goto L_0x0025
            r8.setImportantForAutofill(r3)
        L_0x0025:
            java.lang.reflect.Field r1 = mScrollYField     // Catch:{ all -> 0x0036 }
            if (r1 != 0) goto L_0x0036
            java.lang.Class<android.view.View> r1 = android.view.View.class
            java.lang.String r4 = "mScrollY"
            java.lang.reflect.Field r1 = r1.getDeclaredField(r4)     // Catch:{ all -> 0x0036 }
            mScrollYField = r1     // Catch:{ all -> 0x0036 }
            r1.setAccessible(r2)     // Catch:{ all -> 0x0036 }
        L_0x0036:
            r1 = 0
            java.lang.Class r4 = editorClass     // Catch:{ all -> 0x0078 }
            if (r4 != 0) goto L_0x007c
            java.lang.Class<android.widget.TextView> r4 = android.widget.TextView.class
            java.lang.String r5 = "mEditor"
            java.lang.reflect.Field r4 = r4.getDeclaredField(r5)     // Catch:{ all -> 0x0078 }
            mEditor = r4     // Catch:{ all -> 0x0078 }
            r4.setAccessible(r2)     // Catch:{ all -> 0x0078 }
            java.lang.String r4 = "android.widget.Editor"
            java.lang.Class r4 = java.lang.Class.forName(r4)     // Catch:{ all -> 0x0078 }
            editorClass = r4     // Catch:{ all -> 0x0078 }
            java.lang.reflect.Field r4 = r4.getDeclaredField(r0)     // Catch:{ all -> 0x0078 }
            mShowCursorField = r4     // Catch:{ all -> 0x0078 }
            r4.setAccessible(r2)     // Catch:{ all -> 0x0078 }
            java.lang.Class<android.widget.TextView> r4 = android.widget.TextView.class
            java.lang.String r5 = "getVerticalOffset"
            java.lang.Class[] r6 = new java.lang.Class[r2]     // Catch:{ all -> 0x0078 }
            java.lang.Class r7 = java.lang.Boolean.TYPE     // Catch:{ all -> 0x0078 }
            r6[r1] = r7     // Catch:{ all -> 0x0078 }
            java.lang.reflect.Method r4 = r4.getDeclaredMethod(r5, r6)     // Catch:{ all -> 0x0078 }
            getVerticalOffsetMethod = r4     // Catch:{ all -> 0x0078 }
            r4.setAccessible(r2)     // Catch:{ all -> 0x0078 }
            java.lang.Class r4 = editorClass     // Catch:{ all -> 0x0078 }
            java.lang.reflect.Field r0 = r4.getDeclaredField(r0)     // Catch:{ all -> 0x0078 }
            mShowCursorField = r0     // Catch:{ all -> 0x0078 }
            r0.setAccessible(r2)     // Catch:{ all -> 0x0078 }
            goto L_0x007c
        L_0x0078:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x007c:
            android.graphics.drawable.GradientDrawable r0 = new android.graphics.drawable.GradientDrawable     // Catch:{ all -> 0x009f }
            android.graphics.drawable.GradientDrawable$Orientation r4 = android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM     // Catch:{ all -> 0x009f }
            int[] r3 = new int[r3]     // Catch:{ all -> 0x009f }
            r5 = -11230757(0xfffffffffvar_a1db, float:-2.8263674E38)
            r3[r1] = r5     // Catch:{ all -> 0x009f }
            r3[r2] = r5     // Catch:{ all -> 0x009f }
            r0.<init>(r4, r3)     // Catch:{ all -> 0x009f }
            r8.gradientDrawable = r0     // Catch:{ all -> 0x009f }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x009f }
            r3 = 29
            if (r1 < r3) goto L_0x0097
            r8.setTextCursorDrawable(r0)     // Catch:{ all -> 0x009f }
        L_0x0097:
            java.lang.reflect.Field r0 = mEditor     // Catch:{ all -> 0x009f }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ all -> 0x009f }
            r8.editor = r0     // Catch:{ all -> 0x009f }
        L_0x009f:
            java.lang.reflect.Field r0 = mCursorDrawableResField     // Catch:{ all -> 0x00c0 }
            if (r0 != 0) goto L_0x00b0
            java.lang.Class<android.widget.TextView> r0 = android.widget.TextView.class
            java.lang.String r1 = "mCursorDrawableRes"
            java.lang.reflect.Field r0 = r0.getDeclaredField(r1)     // Catch:{ all -> 0x00c0 }
            mCursorDrawableResField = r0     // Catch:{ all -> 0x00c0 }
            r0.setAccessible(r2)     // Catch:{ all -> 0x00c0 }
        L_0x00b0:
            java.lang.reflect.Field r0 = mCursorDrawableResField     // Catch:{ all -> 0x00c0 }
            if (r0 == 0) goto L_0x00c0
            java.lang.reflect.Field r0 = mCursorDrawableResField     // Catch:{ all -> 0x00c0 }
            r1 = 2131165376(0x7var_c0, float:1.7944967E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x00c0 }
            r0.set(r8, r1)     // Catch:{ all -> 0x00c0 }
        L_0x00c0:
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r8.cursorSize = r0
            return
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
                    Field declaredField = TextView.class.getDeclaredField("mEditor");
                    mEditor = declaredField;
                    declaredField.setAccessible(true);
                    this.editor = mEditor.get(this);
                }
                if (this.listenerFixer == null) {
                    Method declaredMethod = editorClass.getDeclaredMethod("getPositionListener", new Class[0]);
                    declaredMethod.setAccessible(true);
                    this.listenerFixer = (ViewTreeObserver.OnPreDrawListener) declaredMethod.invoke(this.editor, new Object[0]);
                }
                ViewTreeObserver.OnPreDrawListener onPreDrawListener = this.listenerFixer;
                onPreDrawListener.getClass();
                AndroidUtilities.runOnUIThread(new Runnable(onPreDrawListener) {
                    private final /* synthetic */ ViewTreeObserver.OnPreDrawListener f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        this.f$0.onPreDraw();
                    }
                }, 500);
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
        this.errorPaint.setColor(i);
        invalidate();
    }

    public void setLineColors(int i, int i2, int i3) {
        this.lineColor = i;
        this.activeLineColor = i2;
        this.errorLineColor = i3;
        this.errorPaint.setColor(i3);
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

    public boolean requestFocus(int i, Rect rect2) {
        return super.requestFocus(i, rect2);
    }

    public boolean hasErrorText() {
        return !TextUtils.isEmpty(this.errorText);
    }

    public StaticLayout getErrorLayout(int i) {
        if (TextUtils.isEmpty(this.errorText)) {
            return null;
        }
        return new StaticLayout(this.errorText, this.errorPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    public float getLineY() {
        return this.lineY;
    }

    public void setSupportRtlHint(boolean z) {
        this.supportRtlHint = z;
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (i != i3) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
        super.setText(charSequence, bufferType);
        checkHeaderVisibility(this.nextSetTextAnimated);
        this.nextSetTextAnimated = false;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.hintLayout != null) {
            this.lineY = (((float) (getMeasuredHeight() - this.hintLayout.getHeight())) / 2.0f) + ((float) this.hintLayout.getHeight()) + ((float) AndroidUtilities.dp(6.0f));
        }
    }

    public void setHintText(CharSequence charSequence) {
        if (charSequence == null) {
            charSequence = "";
        }
        this.hintLayout = new StaticLayout(charSequence, getPaint(), AndroidUtilities.dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    public Layout getHintLayoutEx() {
        return this.hintLayout;
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect2) {
        super.onFocusChanged(z, i, rect2);
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
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.headerTransformAnimation = animatorSet2;
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
        int i2 = this.scrollY;
        if (i2 != Integer.MAX_VALUE) {
            return -i2;
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        int i2;
        float f;
        Canvas canvas2 = canvas;
        int extendedPaddingTop = getExtendedPaddingTop();
        this.scrollY = Integer.MAX_VALUE;
        try {
            this.scrollY = mScrollYField.getInt(this);
            mScrollYField.set(this, 0);
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
        int i3 = this.scrollY;
        if (i3 != Integer.MAX_VALUE) {
            try {
                mScrollYField.set(this, Integer.valueOf(i3));
            } catch (Exception unused3) {
            }
        }
        canvas.restore();
        if ((length() == 0 || this.transformHintToHeader) && this.hintLayout != null && (this.hintVisible || this.hintAlpha != 0.0f)) {
            if ((this.hintVisible && this.hintAlpha != 1.0f) || (!this.hintVisible && this.hintAlpha != 0.0f)) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                if (j < 0 || j > 17) {
                    j = 17;
                }
                this.lastUpdateTime = currentTimeMillis;
                if (this.hintVisible) {
                    float f2 = this.hintAlpha + (((float) j) / 150.0f);
                    this.hintAlpha = f2;
                    if (f2 > 1.0f) {
                        this.hintAlpha = 1.0f;
                    }
                } else {
                    float f3 = this.hintAlpha - (((float) j) / 150.0f);
                    this.hintAlpha = f3;
                    if (f3 < 0.0f) {
                        this.hintAlpha = 0.0f;
                    }
                }
                invalidate();
            }
            int color = getPaint().getColor();
            canvas.save();
            float lineLeft = this.hintLayout.getLineLeft(0);
            float lineWidth = this.hintLayout.getLineWidth(0);
            int i4 = lineLeft != 0.0f ? (int) (((float) 0) - lineLeft) : 0;
            if (!this.supportRtlHint || !LocaleController.isRTL) {
                canvas2.translate((float) (i4 + getScrollX()), (this.lineY - ((float) this.hintLayout.getHeight())) - ((float) AndroidUtilities.dp(6.0f)));
            } else {
                canvas2.translate(((float) (i4 + getScrollX())) + (((float) getMeasuredWidth()) - lineWidth), (this.lineY - ((float) this.hintLayout.getHeight())) - ((float) AndroidUtilities.dp(6.0f)));
            }
            if (this.transformHintToHeader) {
                float f4 = 1.0f - (this.headerAnimationProgress * 0.3f);
                float f5 = ((float) (-AndroidUtilities.dp(22.0f))) * this.headerAnimationProgress;
                int red = Color.red(this.headerHintColor);
                int green = Color.green(this.headerHintColor);
                int blue = Color.blue(this.headerHintColor);
                int alpha = Color.alpha(this.headerHintColor);
                int red2 = Color.red(this.hintColor);
                int green2 = Color.green(this.hintColor);
                int blue2 = Color.blue(this.hintColor);
                int alpha2 = Color.alpha(this.hintColor);
                if (!this.supportRtlHint || !LocaleController.isRTL) {
                    f = 0.0f;
                    if (lineLeft != 0.0f) {
                        canvas2.translate(lineLeft * (1.0f - f4), 0.0f);
                    }
                } else {
                    float f6 = lineWidth + lineLeft;
                    f = 0.0f;
                    canvas2.translate(f6 - (f6 * f4), 0.0f);
                }
                canvas2.scale(f4, f4);
                canvas2.translate(f, f5);
                TextPaint paint = getPaint();
                float f7 = this.headerAnimationProgress;
                paint.setColor(Color.argb((int) (((float) alpha2) + (((float) (alpha - alpha2)) * f7)), (int) (((float) red2) + (((float) (red - red2)) * f7)), (int) (((float) green2) + (((float) (green - green2)) * f7)), (int) (((float) blue2) + (((float) (blue - blue2)) * f7))));
            } else {
                getPaint().setColor(this.hintColor);
                getPaint().setAlpha((int) (this.hintAlpha * 255.0f * (((float) Color.alpha(this.hintColor)) / 255.0f)));
            }
            this.hintLayout.draw(canvas2);
            getPaint().setColor(color);
            canvas.restore();
        }
        try {
            if (this.allowDrawCursor && mShowCursorField != null) {
                if ((SystemClock.uptimeMillis() - mShowCursorField.getLong(this.editor)) % 1000 < 500 && isFocused()) {
                    canvas.save();
                    if (getVerticalOffsetMethod != null) {
                        if ((getGravity() & 112) != 48) {
                            i2 = ((Integer) getVerticalOffsetMethod.invoke(this, new Object[]{true})).intValue();
                            canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + i2));
                            Layout layout = getLayout();
                            int lineForOffset = layout.getLineForOffset(getSelectionStart());
                            int lineCount = layout.getLineCount();
                            updateCursorPosition();
                            Rect bounds = this.gradientDrawable.getBounds();
                            this.rect.left = bounds.left;
                            this.rect.right = bounds.left + AndroidUtilities.dp(this.cursorWidth);
                            this.rect.bottom = bounds.bottom;
                            this.rect.top = bounds.top;
                            if (this.lineSpacingExtra != 0.0f && lineForOffset < lineCount - 1) {
                                Rect rect2 = this.rect;
                                rect2.bottom = (int) (((float) rect2.bottom) - this.lineSpacingExtra);
                            }
                            this.rect.top = this.rect.centerY() - (this.cursorSize / 2);
                            this.rect.bottom = this.rect.top + this.cursorSize;
                            this.gradientDrawable.setBounds(this.rect);
                            this.gradientDrawable.draw(canvas2);
                            canvas.restore();
                        }
                    } else if ((getGravity() & 112) != 48) {
                        i2 = getTotalPaddingTop() - getExtendedPaddingTop();
                        canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + i2));
                        Layout layout2 = getLayout();
                        int lineForOffset2 = layout2.getLineForOffset(getSelectionStart());
                        int lineCount2 = layout2.getLineCount();
                        updateCursorPosition();
                        Rect bounds2 = this.gradientDrawable.getBounds();
                        this.rect.left = bounds2.left;
                        this.rect.right = bounds2.left + AndroidUtilities.dp(this.cursorWidth);
                        this.rect.bottom = bounds2.bottom;
                        this.rect.top = bounds2.top;
                        Rect rect22 = this.rect;
                        rect22.bottom = (int) (((float) rect22.bottom) - this.lineSpacingExtra);
                        this.rect.top = this.rect.centerY() - (this.cursorSize / 2);
                        this.rect.bottom = this.rect.top + this.cursorSize;
                        this.gradientDrawable.setBounds(this.rect);
                        this.gradientDrawable.draw(canvas2);
                        canvas.restore();
                    }
                    i2 = 0;
                    canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + i2));
                    Layout layout22 = getLayout();
                    int lineForOffset22 = layout22.getLineForOffset(getSelectionStart());
                    int lineCount22 = layout22.getLineCount();
                    updateCursorPosition();
                    Rect bounds22 = this.gradientDrawable.getBounds();
                    this.rect.left = bounds22.left;
                    this.rect.right = bounds22.left + AndroidUtilities.dp(this.cursorWidth);
                    this.rect.bottom = bounds22.bottom;
                    this.rect.top = bounds22.top;
                    Rect rect222 = this.rect;
                    rect222.bottom = (int) (((float) rect222.bottom) - this.lineSpacingExtra);
                    this.rect.top = this.rect.centerY() - (this.cursorSize / 2);
                    this.rect.bottom = this.rect.top + this.cursorSize;
                    this.gradientDrawable.setBounds(this.rect);
                    this.gradientDrawable.draw(canvas2);
                    canvas.restore();
                }
            }
        } catch (Throwable unused4) {
        }
        if (this.lineColor != 0 && this.hintLayout != null) {
            if (!TextUtils.isEmpty(this.errorText)) {
                this.linePaint.setColor(this.errorLineColor);
                i = AndroidUtilities.dp(2.0f);
            } else if (isFocused()) {
                this.linePaint.setColor(this.activeLineColor);
                i = AndroidUtilities.dp(2.0f);
            } else {
                this.linePaint.setColor(this.lineColor);
                i = AndroidUtilities.dp(1.0f);
            }
            canvas.drawRect((float) getScrollX(), (float) ((int) this.lineY), (float) (getScrollX() + getMeasuredWidth()), this.lineY + ((float) i), this.linePaint);
        }
    }

    public void setWindowView(View view) {
        this.windowView = view;
    }

    private boolean updateCursorPosition() {
        Layout layout = getLayout();
        int selectionStart = getSelectionStart();
        int lineForOffset = layout.getLineForOffset(selectionStart);
        updateCursorPosition(layout.getLineTop(lineForOffset), layout.getLineTop(lineForOffset + 1), layout.getPrimaryHorizontal(selectionStart));
        return true;
    }

    private int clampHorizontalPosition(Drawable drawable, float f) {
        int i;
        float max = Math.max(0.5f, f - 0.5f);
        if (this.mTempRect == null) {
            this.mTempRect = new Rect();
        }
        int i2 = 0;
        if (drawable != null) {
            drawable.getPadding(this.mTempRect);
            i2 = drawable.getIntrinsicWidth();
        } else {
            this.mTempRect.setEmpty();
        }
        int scrollX = getScrollX();
        float f2 = max - ((float) scrollX);
        int width = (getWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        float f3 = (float) width;
        if (f2 >= f3 - 1.0f) {
            return (width + scrollX) - (i2 - this.mTempRect.right);
        }
        if (Math.abs(f2) <= 1.0f || (TextUtils.isEmpty(getText()) && ((float) (1048576 - scrollX)) <= f3 + 1.0f && max <= 1.0f)) {
            i = this.mTempRect.left;
        } else {
            scrollX = (int) max;
            i = this.mTempRect.left;
        }
        return scrollX - i;
    }

    private void updateCursorPosition(int i, int i2, float f) {
        int clampHorizontalPosition = clampHorizontalPosition(this.gradientDrawable, f);
        int dp = AndroidUtilities.dp(this.cursorWidth);
        GradientDrawable gradientDrawable2 = this.gradientDrawable;
        Rect rect2 = this.mTempRect;
        gradientDrawable2.setBounds(clampHorizontalPosition, i - rect2.top, dp + clampHorizontalPosition, i2 + rect2.bottom);
    }

    public float getLineSpacingExtra() {
        return super.getLineSpacingExtra();
    }

    /* access modifiers changed from: private */
    public void cleanupFloatingActionModeViews() {
        FloatingToolbar floatingToolbar2 = this.floatingToolbar;
        if (floatingToolbar2 != null) {
            floatingToolbar2.dismiss();
            this.floatingToolbar = null;
        }
        if (this.floatingToolbarPreDrawListener != null) {
            getViewTreeObserver().removeOnPreDrawListener(this.floatingToolbarPreDrawListener);
            this.floatingToolbarPreDrawListener = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = getRootView();
        AndroidUtilities.runOnUIThread(this.invalidateRunnable);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = null;
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (Build.VERSION.SDK_INT < 23 || (this.windowView == null && this.attachedToWindow == null)) {
            return super.startActionMode(callback);
        }
        FloatingActionMode floatingActionMode2 = this.floatingActionMode;
        if (floatingActionMode2 != null) {
            floatingActionMode2.finish();
        }
        cleanupFloatingActionModeViews();
        Context context = getContext();
        View view = this.windowView;
        if (view == null) {
            view = this.attachedToWindow;
        }
        this.floatingToolbar = new FloatingToolbar(context, view, getActionModeStyle());
        this.floatingActionMode = new FloatingActionMode(getContext(), new ActionModeCallback2Wrapper(callback), this, this.floatingToolbar);
        this.floatingToolbarPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            public final boolean onPreDraw() {
                return EditTextBoldCursor.this.lambda$startActionMode$0$EditTextBoldCursor();
            }
        };
        FloatingActionMode floatingActionMode3 = this.floatingActionMode;
        callback.onCreateActionMode(floatingActionMode3, floatingActionMode3.getMenu());
        FloatingActionMode floatingActionMode4 = this.floatingActionMode;
        extendActionMode(floatingActionMode4, floatingActionMode4.getMenu());
        this.floatingActionMode.invalidate();
        getViewTreeObserver().addOnPreDrawListener(this.floatingToolbarPreDrawListener);
        invalidate();
        return this.floatingActionMode;
    }

    public /* synthetic */ boolean lambda$startActionMode$0$EditTextBoldCursor() {
        FloatingActionMode floatingActionMode2 = this.floatingActionMode;
        if (floatingActionMode2 == null) {
            return true;
        }
        floatingActionMode2.updateViewLocationInWindow();
        return true;
    }

    public ActionMode startActionMode(ActionMode.Callback callback, int i) {
        if (Build.VERSION.SDK_INT < 23 || (this.windowView == null && this.attachedToWindow == null)) {
            return super.startActionMode(callback, i);
        }
        return startActionMode(callback);
    }

    public void setSelection(int i, int i2) {
        try {
            super.setSelection(i, i2);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void setSelection(int i) {
        try {
            super.setSelection(i);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
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
