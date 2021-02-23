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
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
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
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
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
    private static boolean mScrollYGet;
    private static Field mShowCursorField;
    private int activeLineColor;
    private boolean allowDrawCursor = true;
    /* access modifiers changed from: private */
    public View attachedToWindow;
    private boolean currentDrawHintAsHeader;
    /* access modifiers changed from: private */
    public boolean cursorDrawn;
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
    private CharSequence hint;
    private float hintAlpha = 1.0f;
    private SubstringLayoutAnimator hintAnimator;
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

    public Drawable getTextCursorDrawable() {
        AnonymousClass2 r0 = new ShapeDrawable(new RectShape()) {
            public void draw(Canvas canvas) {
                super.draw(canvas);
                boolean unused = EditTextBoldCursor.this.cursorDrawn = true;
            }
        };
        r0.getPaint().setColor(0);
        return r0;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(20:0|(1:2)|3|4|(1:8)|9|11|12|(7:14|15|16|17|18|20|21)|24|25|(1:27)|28|29|30|(1:32)|33|(1:35)|36|38) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x009a */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x009e A[Catch:{ all -> 0x00b9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00af A[Catch:{ all -> 0x00b9 }] */
    @android.annotation.SuppressLint({"PrivateApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void init() {
        /*
            r7 = this;
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            r7.linePaint = r0
            android.text.TextPaint r0 = new android.text.TextPaint
            r1 = 1
            r0.<init>(r1)
            r7.errorPaint = r0
            r2 = 1093664768(0x41300000, float:11.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r2)
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 2
            r3 = 26
            if (r0 < r3) goto L_0x0023
            r7.setImportantForAutofill(r2)
        L_0x0023:
            boolean r0 = mScrollYGet     // Catch:{ all -> 0x003a }
            if (r0 != 0) goto L_0x003a
            java.lang.reflect.Field r0 = mScrollYField     // Catch:{ all -> 0x003a }
            if (r0 != 0) goto L_0x003a
            mScrollYGet = r1     // Catch:{ all -> 0x003a }
            java.lang.Class<android.view.View> r0 = android.view.View.class
            java.lang.String r3 = "mScrollY"
            java.lang.reflect.Field r0 = r0.getDeclaredField(r3)     // Catch:{ all -> 0x003a }
            mScrollYField = r0     // Catch:{ all -> 0x003a }
            r0.setAccessible(r1)     // Catch:{ all -> 0x003a }
        L_0x003a:
            r0 = 0
            java.lang.Class r3 = editorClass     // Catch:{ all -> 0x0073 }
            if (r3 != 0) goto L_0x0077
            java.lang.Class<android.widget.TextView> r3 = android.widget.TextView.class
            java.lang.String r4 = "mEditor"
            java.lang.reflect.Field r3 = r3.getDeclaredField(r4)     // Catch:{ all -> 0x0073 }
            mEditor = r3     // Catch:{ all -> 0x0073 }
            r3.setAccessible(r1)     // Catch:{ all -> 0x0073 }
            java.lang.String r3 = "android.widget.Editor"
            java.lang.Class r3 = java.lang.Class.forName(r3)     // Catch:{ all -> 0x0073 }
            editorClass = r3     // Catch:{ all -> 0x0073 }
            java.lang.String r4 = "mShowCursor"
            java.lang.reflect.Field r3 = r3.getDeclaredField(r4)     // Catch:{ Exception -> 0x005f }
            mShowCursorField = r3     // Catch:{ Exception -> 0x005f }
            r3.setAccessible(r1)     // Catch:{ Exception -> 0x005f }
        L_0x005f:
            java.lang.Class<android.widget.TextView> r3 = android.widget.TextView.class
            java.lang.String r4 = "getVerticalOffset"
            java.lang.Class[] r5 = new java.lang.Class[r1]     // Catch:{ all -> 0x0073 }
            java.lang.Class r6 = java.lang.Boolean.TYPE     // Catch:{ all -> 0x0073 }
            r5[r0] = r6     // Catch:{ all -> 0x0073 }
            java.lang.reflect.Method r3 = r3.getDeclaredMethod(r4, r5)     // Catch:{ all -> 0x0073 }
            getVerticalOffsetMethod = r3     // Catch:{ all -> 0x0073 }
            r3.setAccessible(r1)     // Catch:{ all -> 0x0073 }
            goto L_0x0077
        L_0x0073:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x0077:
            android.graphics.drawable.GradientDrawable r3 = new android.graphics.drawable.GradientDrawable     // Catch:{ all -> 0x009a }
            android.graphics.drawable.GradientDrawable$Orientation r4 = android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM     // Catch:{ all -> 0x009a }
            int[] r2 = new int[r2]     // Catch:{ all -> 0x009a }
            r5 = -11230757(0xfffffffffvar_a1db, float:-2.8263674E38)
            r2[r0] = r5     // Catch:{ all -> 0x009a }
            r2[r1] = r5     // Catch:{ all -> 0x009a }
            r3.<init>(r4, r2)     // Catch:{ all -> 0x009a }
            r7.gradientDrawable = r3     // Catch:{ all -> 0x009a }
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x009a }
            r2 = 29
            if (r0 < r2) goto L_0x0092
            r7.setTextCursorDrawable(r3)     // Catch:{ all -> 0x009a }
        L_0x0092:
            java.lang.reflect.Field r0 = mEditor     // Catch:{ all -> 0x009a }
            java.lang.Object r0 = r0.get(r7)     // Catch:{ all -> 0x009a }
            r7.editor = r0     // Catch:{ all -> 0x009a }
        L_0x009a:
            java.lang.reflect.Field r0 = mCursorDrawableResField     // Catch:{ all -> 0x00b9 }
            if (r0 != 0) goto L_0x00ab
            java.lang.Class<android.widget.TextView> r0 = android.widget.TextView.class
            java.lang.String r2 = "mCursorDrawableRes"
            java.lang.reflect.Field r0 = r0.getDeclaredField(r2)     // Catch:{ all -> 0x00b9 }
            mCursorDrawableResField = r0     // Catch:{ all -> 0x00b9 }
            r0.setAccessible(r1)     // Catch:{ all -> 0x00b9 }
        L_0x00ab:
            java.lang.reflect.Field r0 = mCursorDrawableResField     // Catch:{ all -> 0x00b9 }
            if (r0 == 0) goto L_0x00b9
            r1 = 2131165401(0x7var_d9, float:1.7945018E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x00b9 }
            r0.set(r7, r1)     // Catch:{ all -> 0x00b9 }
        L_0x00b9:
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.cursorSize = r0
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
                    public final /* synthetic */ ViewTreeObserver.OnPreDrawListener f$0;

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
            setHintText(this.hint);
            this.lineY = (((float) (getMeasuredHeight() - this.hintLayout.getHeight())) / 2.0f) + ((float) this.hintLayout.getHeight()) + ((float) AndroidUtilities.dp(6.0f));
        }
    }

    public void setHintText(CharSequence charSequence) {
        setHintText(charSequence, false);
    }

    public void setHintText(CharSequence charSequence, boolean z) {
        if (charSequence == null) {
            charSequence = "";
        }
        if (getMeasuredWidth() == 0) {
            z = false;
        }
        if (z) {
            if (this.hintAnimator == null) {
                this.hintAnimator = new SubstringLayoutAnimator(this);
            }
            this.hintAnimator.create(this.hintLayout, this.hint, charSequence, getPaint());
        } else {
            SubstringLayoutAnimator substringLayoutAnimator = this.hintAnimator;
            if (substringLayoutAnimator != null) {
                substringLayoutAnimator.cancel();
            }
        }
        this.hint = charSequence;
        if (getMeasuredWidth() != 0) {
            charSequence = TextUtils.ellipsize(charSequence, getPaint(), (float) getMeasuredWidth(), TextUtils.TruncateAt.END);
            StaticLayout staticLayout = this.hintLayout;
            if (staticLayout != null && TextUtils.equals(staticLayout.getText(), charSequence)) {
                return;
            }
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
        boolean z;
        int i2;
        float f;
        boolean z2;
        float f2;
        Canvas canvas2 = canvas;
        int extendedPaddingTop = getExtendedPaddingTop();
        this.scrollY = Integer.MAX_VALUE;
        try {
            Field field = mScrollYField;
            if (field != null) {
                this.scrollY = field.getInt(this);
                mScrollYField.set(this, 0);
            } else {
                this.scrollY = getScrollX();
            }
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
        if ((length() == 0 || this.transformHintToHeader) && this.hintLayout != null && ((z2 = this.hintVisible) || this.hintAlpha != 0.0f)) {
            if ((z2 && this.hintAlpha != 1.0f) || (!z2 && this.hintAlpha != 0.0f)) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                if (j < 0 || j > 17) {
                    j = 17;
                }
                this.lastUpdateTime = currentTimeMillis;
                if (this.hintVisible) {
                    float f3 = this.hintAlpha + (((float) j) / 150.0f);
                    this.hintAlpha = f3;
                    if (f3 > 1.0f) {
                        this.hintAlpha = 1.0f;
                    }
                } else {
                    float f4 = this.hintAlpha - (((float) j) / 150.0f);
                    this.hintAlpha = f4;
                    if (f4 < 0.0f) {
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
                float f5 = 1.0f - (this.headerAnimationProgress * 0.3f);
                float f6 = ((float) (-AndroidUtilities.dp(22.0f))) * this.headerAnimationProgress;
                int red = Color.red(this.headerHintColor);
                int green = Color.green(this.headerHintColor);
                int blue = Color.blue(this.headerHintColor);
                int alpha = Color.alpha(this.headerHintColor);
                int red2 = Color.red(this.hintColor);
                int green2 = Color.green(this.hintColor);
                int blue2 = Color.blue(this.hintColor);
                int alpha2 = Color.alpha(this.hintColor);
                if (!this.supportRtlHint || !LocaleController.isRTL) {
                    f2 = 0.0f;
                    if (lineLeft != 0.0f) {
                        canvas2.translate(lineLeft * (1.0f - f5), 0.0f);
                    }
                } else {
                    float f7 = lineWidth + lineLeft;
                    f2 = 0.0f;
                    canvas2.translate(f7 - (f7 * f5), 0.0f);
                }
                canvas2.scale(f5, f5);
                canvas2.translate(f2, f6);
                TextPaint paint = getPaint();
                float f8 = this.headerAnimationProgress;
                paint.setColor(Color.argb((int) (((float) alpha2) + (((float) (alpha - alpha2)) * f8)), (int) (((float) red2) + (((float) (red - red2)) * f8)), (int) (((float) green2) + (((float) (green - green2)) * f8)), (int) (((float) blue2) + (((float) (blue - blue2)) * f8))));
            } else {
                getPaint().setColor(this.hintColor);
                getPaint().setAlpha((int) (this.hintAlpha * 255.0f * (((float) Color.alpha(this.hintColor)) / 255.0f)));
            }
            SubstringLayoutAnimator substringLayoutAnimator = this.hintAnimator;
            if (substringLayoutAnimator == null || !substringLayoutAnimator.animateTextChange) {
                this.hintLayout.draw(canvas2);
            } else {
                canvas.save();
                canvas2.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.hintAnimator.draw(canvas2, getPaint());
                canvas.restore();
            }
            getPaint().setColor(color);
            canvas.restore();
        }
        try {
            Field field2 = mShowCursorField;
            if (field2 != null) {
                z = (SystemClock.uptimeMillis() - field2.getLong(this.editor)) % 1000 < 500 && isFocused();
            } else {
                z = this.cursorDrawn;
                this.cursorDrawn = false;
            }
            if (this.allowDrawCursor && z) {
                canvas.save();
                if (getVerticalOffsetMethod != null) {
                    if ((getGravity() & 112) != 48) {
                        i2 = ((Integer) getVerticalOffsetMethod.invoke(this, new Object[]{Boolean.TRUE})).intValue();
                        canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + i2));
                        Layout layout = getLayout();
                        int lineForOffset = layout.getLineForOffset(getSelectionStart());
                        int lineCount = layout.getLineCount();
                        updateCursorPosition();
                        Rect bounds = this.gradientDrawable.getBounds();
                        Rect rect2 = this.rect;
                        rect2.left = bounds.left;
                        rect2.right = bounds.left + AndroidUtilities.dp(this.cursorWidth);
                        Rect rect3 = this.rect;
                        int i5 = bounds.bottom;
                        rect3.bottom = i5;
                        rect3.top = bounds.top;
                        f = this.lineSpacingExtra;
                        if (f != 0.0f && lineForOffset < lineCount - 1) {
                            rect3.bottom = (int) (((float) i5) - f);
                        }
                        int centerY = rect3.centerY();
                        int i6 = this.cursorSize;
                        rect3.top = centerY - (i6 / 2);
                        Rect rect4 = this.rect;
                        rect4.bottom = rect4.top + i6;
                        this.gradientDrawable.setBounds(rect4);
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
                    Rect rect22 = this.rect;
                    rect22.left = bounds2.left;
                    rect22.right = bounds2.left + AndroidUtilities.dp(this.cursorWidth);
                    Rect rect32 = this.rect;
                    int i52 = bounds2.bottom;
                    rect32.bottom = i52;
                    rect32.top = bounds2.top;
                    f = this.lineSpacingExtra;
                    rect32.bottom = (int) (((float) i52) - f);
                    int centerY2 = rect32.centerY();
                    int i62 = this.cursorSize;
                    rect32.top = centerY2 - (i62 / 2);
                    Rect rect42 = this.rect;
                    rect42.bottom = rect42.top + i62;
                    this.gradientDrawable.setBounds(rect42);
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
                Rect rect222 = this.rect;
                rect222.left = bounds22.left;
                rect222.right = bounds22.left + AndroidUtilities.dp(this.cursorWidth);
                Rect rect322 = this.rect;
                int i522 = bounds22.bottom;
                rect322.bottom = i522;
                rect322.top = bounds22.top;
                f = this.lineSpacingExtra;
                rect322.bottom = (int) (((float) i522) - f);
                int centerY22 = rect322.centerY();
                int i622 = this.cursorSize;
                rect322.top = centerY22 - (i622 / 2);
                Rect rect422 = this.rect;
                rect422.bottom = rect422.top + i622;
                this.gradientDrawable.setBounds(rect422);
                this.gradientDrawable.draw(canvas2);
                canvas.restore();
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
        try {
            super.onAttachedToWindow();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
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
        FloatingActionMode floatingActionMode3 = new FloatingActionMode(getContext(), new ActionModeCallback2Wrapper(callback), this, this.floatingToolbar);
        this.floatingActionMode = floatingActionMode3;
        this.floatingToolbarPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            public final boolean onPreDraw() {
                return EditTextBoldCursor.this.lambda$startActionMode$0$EditTextBoldCursor();
            }
        };
        callback.onCreateActionMode(floatingActionMode3, floatingActionMode3.getMenu());
        FloatingActionMode floatingActionMode4 = this.floatingActionMode;
        extendActionMode(floatingActionMode4, floatingActionMode4.getMenu());
        this.floatingActionMode.invalidate();
        getViewTreeObserver().addOnPreDrawListener(this.floatingToolbarPreDrawListener);
        invalidate();
        return this.floatingActionMode;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startActionMode$0 */
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

    public void hideActionMode() {
        cleanupFloatingActionModeViews();
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
        if (this.hintLayout != null) {
            AccessibilityNodeInfoCompat.wrap(accessibilityNodeInfo).setHintText(this.hintLayout.getText());
        }
    }
}
