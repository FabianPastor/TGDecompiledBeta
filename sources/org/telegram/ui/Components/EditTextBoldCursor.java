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
import org.telegram.messenger.BuildVars;
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
    ShapeDrawable cursorDrawable;
    /* access modifiers changed from: private */
    public boolean cursorDrawn;
    /* access modifiers changed from: private */
    public int cursorSize;
    /* access modifiers changed from: private */
    public float cursorWidth = 2.0f;
    boolean drawInMaim;
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
    int lastOffset = -1;
    private int lastSize;
    CharSequence lastText;
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
        if (this.cursorDrawable != null) {
            return super.getTextCursorDrawable();
        }
        AnonymousClass2 r0 = new ShapeDrawable(new RectShape()) {
            public void draw(Canvas canvas) {
                super.draw(canvas);
                boolean unused = EditTextBoldCursor.this.cursorDrawn = true;
            }
        };
        r0.getPaint().setColor(0);
        return r0;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(9:29|30|(1:32)|33|34|35|(1:37)|38|(1:40)) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:34:0x00c2 */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00c6 A[Catch:{ all -> 0x00e1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00d7 A[Catch:{ all -> 0x00e1 }] */
    @android.annotation.SuppressLint({"PrivateApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void init() {
        /*
            r8 = this;
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            r8.linePaint = r0
            android.text.TextPaint r0 = new android.text.TextPaint
            r1 = 1
            r0.<init>(r1)
            r8.errorPaint = r0
            r2 = 1093664768(0x41300000, float:11.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r2)
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 2
            r3 = 26
            if (r0 < r3) goto L_0x0023
            r8.setImportantForAutofill(r2)
        L_0x0023:
            r3 = 29
            if (r0 < r3) goto L_0x0049
            org.telegram.ui.Components.EditTextBoldCursor$3 r0 = new org.telegram.ui.Components.EditTextBoldCursor$3
            r0.<init>()
            r8.cursorDrawable = r0
            android.graphics.drawable.shapes.RectShape r4 = new android.graphics.drawable.shapes.RectShape
            r4.<init>()
            r0.setShape(r4)
            android.graphics.drawable.GradientDrawable r0 = new android.graphics.drawable.GradientDrawable
            android.graphics.drawable.GradientDrawable$Orientation r4 = android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM
            int[] r5 = new int[r2]
            r5 = {-11230757, -11230757} // fill-array
            r0.<init>(r4, r5)
            r8.gradientDrawable = r0
            android.graphics.drawable.ShapeDrawable r0 = r8.cursorDrawable
            r8.setTextCursorDrawable(r0)
        L_0x0049:
            boolean r0 = mScrollYGet     // Catch:{ all -> 0x0060 }
            if (r0 != 0) goto L_0x0060
            java.lang.reflect.Field r0 = mScrollYField     // Catch:{ all -> 0x0060 }
            if (r0 != 0) goto L_0x0060
            mScrollYGet = r1     // Catch:{ all -> 0x0060 }
            java.lang.Class<android.view.View> r0 = android.view.View.class
            java.lang.String r4 = "mScrollY"
            java.lang.reflect.Field r0 = r0.getDeclaredField(r4)     // Catch:{ all -> 0x0060 }
            mScrollYField = r0     // Catch:{ all -> 0x0060 }
            r0.setAccessible(r1)     // Catch:{ all -> 0x0060 }
        L_0x0060:
            r0 = 0
            java.lang.Class r4 = editorClass     // Catch:{ all -> 0x0099 }
            if (r4 != 0) goto L_0x009d
            java.lang.Class<android.widget.TextView> r4 = android.widget.TextView.class
            java.lang.String r5 = "mEditor"
            java.lang.reflect.Field r4 = r4.getDeclaredField(r5)     // Catch:{ all -> 0x0099 }
            mEditor = r4     // Catch:{ all -> 0x0099 }
            r4.setAccessible(r1)     // Catch:{ all -> 0x0099 }
            java.lang.String r4 = "android.widget.Editor"
            java.lang.Class r4 = java.lang.Class.forName(r4)     // Catch:{ all -> 0x0099 }
            editorClass = r4     // Catch:{ all -> 0x0099 }
            java.lang.String r5 = "mShowCursor"
            java.lang.reflect.Field r4 = r4.getDeclaredField(r5)     // Catch:{ Exception -> 0x0085 }
            mShowCursorField = r4     // Catch:{ Exception -> 0x0085 }
            r4.setAccessible(r1)     // Catch:{ Exception -> 0x0085 }
        L_0x0085:
            java.lang.Class<android.widget.TextView> r4 = android.widget.TextView.class
            java.lang.String r5 = "getVerticalOffset"
            java.lang.Class[] r6 = new java.lang.Class[r1]     // Catch:{ all -> 0x0099 }
            java.lang.Class r7 = java.lang.Boolean.TYPE     // Catch:{ all -> 0x0099 }
            r6[r0] = r7     // Catch:{ all -> 0x0099 }
            java.lang.reflect.Method r4 = r4.getDeclaredMethod(r5, r6)     // Catch:{ all -> 0x0099 }
            getVerticalOffsetMethod = r4     // Catch:{ all -> 0x0099 }
            r4.setAccessible(r1)     // Catch:{ all -> 0x0099 }
            goto L_0x009d
        L_0x0099:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x009d:
            android.graphics.drawable.ShapeDrawable r4 = r8.cursorDrawable
            if (r4 != 0) goto L_0x00e1
            android.graphics.drawable.GradientDrawable r4 = new android.graphics.drawable.GradientDrawable     // Catch:{ all -> 0x00c2 }
            android.graphics.drawable.GradientDrawable$Orientation r5 = android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM     // Catch:{ all -> 0x00c2 }
            int[] r2 = new int[r2]     // Catch:{ all -> 0x00c2 }
            r6 = -11230757(0xfffffffffvar_a1db, float:-2.8263674E38)
            r2[r0] = r6     // Catch:{ all -> 0x00c2 }
            r2[r1] = r6     // Catch:{ all -> 0x00c2 }
            r4.<init>(r5, r2)     // Catch:{ all -> 0x00c2 }
            r8.gradientDrawable = r4     // Catch:{ all -> 0x00c2 }
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x00c2 }
            if (r0 < r3) goto L_0x00ba
            r8.setTextCursorDrawable(r4)     // Catch:{ all -> 0x00c2 }
        L_0x00ba:
            java.lang.reflect.Field r0 = mEditor     // Catch:{ all -> 0x00c2 }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ all -> 0x00c2 }
            r8.editor = r0     // Catch:{ all -> 0x00c2 }
        L_0x00c2:
            java.lang.reflect.Field r0 = mCursorDrawableResField     // Catch:{ all -> 0x00e1 }
            if (r0 != 0) goto L_0x00d3
            java.lang.Class<android.widget.TextView> r0 = android.widget.TextView.class
            java.lang.String r2 = "mCursorDrawableRes"
            java.lang.reflect.Field r0 = r0.getDeclaredField(r2)     // Catch:{ all -> 0x00e1 }
            mCursorDrawableResField = r0     // Catch:{ all -> 0x00e1 }
            r0.setAccessible(r1)     // Catch:{ all -> 0x00e1 }
        L_0x00d3:
            java.lang.reflect.Field r0 = mCursorDrawableResField     // Catch:{ all -> 0x00e1 }
            if (r0 == 0) goto L_0x00e1
            r1 = 2131165395(0x7var_d3, float:1.7945006E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x00e1 }
            r0.set(r8, r1)     // Catch:{ all -> 0x00e1 }
        L_0x00e1:
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
        ShapeDrawable shapeDrawable = this.cursorDrawable;
        if (shapeDrawable != null) {
            shapeDrawable.getPaint().setColor(i);
        }
        GradientDrawable gradientDrawable2 = this.gradientDrawable;
        if (gradientDrawable2 != null) {
            gradientDrawable2.setColor(i);
        }
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
        int measuredHeight = getMeasuredHeight() + (getMeasuredWidth() << 16);
        if (this.hintLayout != null) {
            if (this.lastSize != measuredHeight) {
                setHintText(this.hint);
            }
            this.lineY = (((float) (getMeasuredHeight() - this.hintLayout.getHeight())) / 2.0f) + ((float) this.hintLayout.getHeight()) + ((float) AndroidUtilities.dp(6.0f));
        }
        this.lastSize = measuredHeight;
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
        try {
            super.onFocusChanged(z, i, rect2);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
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
        boolean z;
        int i3;
        float f2;
        Object obj;
        int i4;
        boolean z2;
        float f3;
        Canvas canvas2 = canvas;
        if ((length() == 0 || this.transformHintToHeader) && this.hintLayout != null && ((z2 = this.hintVisible) || this.hintAlpha != 0.0f)) {
            if ((z2 && this.hintAlpha != 1.0f) || (!z2 && this.hintAlpha != 0.0f)) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                if (j < 0 || j > 17) {
                    j = 17;
                }
                this.lastUpdateTime = currentTimeMillis;
                if (this.hintVisible) {
                    float f4 = this.hintAlpha + (((float) j) / 150.0f);
                    this.hintAlpha = f4;
                    if (f4 > 1.0f) {
                        this.hintAlpha = 1.0f;
                    }
                } else {
                    float f5 = this.hintAlpha - (((float) j) / 150.0f);
                    this.hintAlpha = f5;
                    if (f5 < 0.0f) {
                        this.hintAlpha = 0.0f;
                    }
                }
                invalidate();
            }
            int color = getPaint().getColor();
            canvas.save();
            float lineLeft = this.hintLayout.getLineLeft(0);
            float lineWidth = this.hintLayout.getLineWidth(0);
            int i5 = lineLeft != 0.0f ? (int) (((float) 0) - lineLeft) : 0;
            if (!this.supportRtlHint || !LocaleController.isRTL) {
                canvas2.translate((float) (i5 + getScrollX()), (this.lineY - ((float) this.hintLayout.getHeight())) - ((float) AndroidUtilities.dp(6.0f)));
            } else {
                canvas2.translate(((float) (i5 + getScrollX())) + (((float) getMeasuredWidth()) - lineWidth), (this.lineY - ((float) this.hintLayout.getHeight())) - ((float) AndroidUtilities.dp(6.0f)));
            }
            if (this.transformHintToHeader) {
                float f6 = 1.0f - (this.headerAnimationProgress * 0.3f);
                float f7 = ((float) (-AndroidUtilities.dp(22.0f))) * this.headerAnimationProgress;
                int red = Color.red(this.headerHintColor);
                int green = Color.green(this.headerHintColor);
                int blue = Color.blue(this.headerHintColor);
                int alpha = Color.alpha(this.headerHintColor);
                int red2 = Color.red(this.hintColor);
                int green2 = Color.green(this.hintColor);
                int blue2 = Color.blue(this.hintColor);
                int alpha2 = Color.alpha(this.hintColor);
                if (!this.supportRtlHint || !LocaleController.isRTL) {
                    f3 = 0.0f;
                    if (lineLeft != 0.0f) {
                        canvas2.translate(lineLeft * (1.0f - f6), 0.0f);
                    }
                } else {
                    float f8 = lineWidth + lineLeft;
                    f3 = 0.0f;
                    canvas2.translate(f8 - (f8 * f6), 0.0f);
                }
                canvas2.scale(f6, f6);
                canvas2.translate(f3, f7);
                TextPaint paint = getPaint();
                float f9 = this.headerAnimationProgress;
                paint.setColor(Color.argb((int) (((float) alpha2) + (((float) (alpha - alpha2)) * f9)), (int) (((float) red2) + (((float) (red - red2)) * f9)), (int) (((float) green2) + (((float) (green - green2)) * f9)), (int) (((float) blue2) + (((float) (blue - blue2)) * f9))));
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
        } catch (Exception e) {
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                throw new RuntimeException(e);
            }
        }
        this.ignoreTopCount = 1;
        this.ignoreBottomCount = 1;
        canvas.save();
        canvas2.translate(0.0f, (float) extendedPaddingTop);
        try {
            this.drawInMaim = true;
            super.onDraw(canvas);
            this.drawInMaim = false;
        } catch (Exception e2) {
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                throw new RuntimeException(e2);
            }
        }
        Field field2 = mScrollYField;
        if (!(field2 == null || (i4 = this.scrollY) == Integer.MAX_VALUE)) {
            try {
                field2.set(this, Integer.valueOf(i4));
            } catch (Exception e3) {
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    throw new RuntimeException(e3);
                }
            }
        }
        canvas.restore();
        if (this.cursorDrawable == null) {
            try {
                Field field3 = mShowCursorField;
                if (field3 == null || (obj = this.editor) == null) {
                    z = this.cursorDrawn;
                    this.cursorDrawn = false;
                } else {
                    z = (SystemClock.uptimeMillis() - field3.getLong(obj)) % 1000 < 500 && isFocused();
                }
                if (this.allowDrawCursor && z) {
                    canvas.save();
                    if (getVerticalOffsetMethod != null) {
                        if ((getGravity() & 112) != 48) {
                            i3 = ((Integer) getVerticalOffsetMethod.invoke(this, new Object[]{Boolean.TRUE})).intValue();
                            canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + i3));
                            Layout layout = getLayout();
                            int lineForOffset = layout.getLineForOffset(getSelectionStart());
                            int lineCount = layout.getLineCount();
                            updateCursorPosition();
                            Rect bounds = this.gradientDrawable.getBounds();
                            Rect rect2 = this.rect;
                            rect2.left = bounds.left;
                            rect2.right = bounds.left + AndroidUtilities.dp(this.cursorWidth);
                            Rect rect3 = this.rect;
                            int i6 = bounds.bottom;
                            rect3.bottom = i6;
                            rect3.top = bounds.top;
                            f2 = this.lineSpacingExtra;
                            if (f2 != 0.0f && lineForOffset < lineCount - 1) {
                                rect3.bottom = (int) (((float) i6) - f2);
                            }
                            int centerY = rect3.centerY();
                            int i7 = this.cursorSize;
                            rect3.top = centerY - (i7 / 2);
                            Rect rect4 = this.rect;
                            rect4.bottom = rect4.top + i7;
                            this.gradientDrawable.setBounds(rect4);
                            this.gradientDrawable.draw(canvas2);
                            canvas.restore();
                        }
                    } else if ((getGravity() & 112) != 48) {
                        i3 = getTotalPaddingTop() - getExtendedPaddingTop();
                        canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + i3));
                        Layout layout2 = getLayout();
                        int lineForOffset2 = layout2.getLineForOffset(getSelectionStart());
                        int lineCount2 = layout2.getLineCount();
                        updateCursorPosition();
                        Rect bounds2 = this.gradientDrawable.getBounds();
                        Rect rect22 = this.rect;
                        rect22.left = bounds2.left;
                        rect22.right = bounds2.left + AndroidUtilities.dp(this.cursorWidth);
                        Rect rect32 = this.rect;
                        int i62 = bounds2.bottom;
                        rect32.bottom = i62;
                        rect32.top = bounds2.top;
                        f2 = this.lineSpacingExtra;
                        rect32.bottom = (int) (((float) i62) - f2);
                        int centerY2 = rect32.centerY();
                        int i72 = this.cursorSize;
                        rect32.top = centerY2 - (i72 / 2);
                        Rect rect42 = this.rect;
                        rect42.bottom = rect42.top + i72;
                        this.gradientDrawable.setBounds(rect42);
                        this.gradientDrawable.draw(canvas2);
                        canvas.restore();
                    }
                    i3 = 0;
                    canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + i3));
                    Layout layout22 = getLayout();
                    int lineForOffset22 = layout22.getLineForOffset(getSelectionStart());
                    int lineCount22 = layout22.getLineCount();
                    updateCursorPosition();
                    Rect bounds22 = this.gradientDrawable.getBounds();
                    Rect rect222 = this.rect;
                    rect222.left = bounds22.left;
                    rect222.right = bounds22.left + AndroidUtilities.dp(this.cursorWidth);
                    Rect rect322 = this.rect;
                    int i622 = bounds22.bottom;
                    rect322.bottom = i622;
                    rect322.top = bounds22.top;
                    f2 = this.lineSpacingExtra;
                    rect322.bottom = (int) (((float) i622) - f2);
                    int centerY22 = rect322.centerY();
                    int i722 = this.cursorSize;
                    rect322.top = centerY22 - (i722 / 2);
                    Rect rect422 = this.rect;
                    rect422.bottom = rect422.top + i722;
                    this.gradientDrawable.setBounds(rect422);
                    this.gradientDrawable.draw(canvas2);
                    canvas.restore();
                }
            } catch (Throwable th) {
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    throw new RuntimeException(th);
                }
            }
        } else if (this.cursorDrawn) {
            try {
                canvas.save();
                if (getVerticalOffsetMethod != null) {
                    if ((getGravity() & 112) != 48) {
                        i2 = ((Integer) getVerticalOffsetMethod.invoke(this, new Object[]{Boolean.TRUE})).intValue();
                        canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + i2));
                        Layout layout3 = getLayout();
                        int lineForOffset3 = layout3.getLineForOffset(getSelectionStart());
                        int lineCount3 = layout3.getLineCount();
                        updateCursorPosition();
                        Rect bounds3 = this.gradientDrawable.getBounds();
                        Rect rect5 = this.rect;
                        rect5.left = bounds3.left;
                        rect5.right = bounds3.left + AndroidUtilities.dp(this.cursorWidth);
                        Rect rect6 = this.rect;
                        int i8 = bounds3.bottom;
                        rect6.bottom = i8;
                        rect6.top = bounds3.top;
                        f = this.lineSpacingExtra;
                        if (f != 0.0f && lineForOffset3 < lineCount3 - 1) {
                            rect6.bottom = (int) (((float) i8) - f);
                        }
                        int centerY3 = rect6.centerY();
                        int i9 = this.cursorSize;
                        rect6.top = centerY3 - (i9 / 2);
                        Rect rect7 = this.rect;
                        rect7.bottom = rect7.top + i9;
                        this.gradientDrawable.setBounds(rect7);
                        this.gradientDrawable.draw(canvas2);
                        canvas.restore();
                        this.cursorDrawn = false;
                    }
                } else if ((getGravity() & 112) != 48) {
                    i2 = getTotalPaddingTop() - getExtendedPaddingTop();
                    canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + i2));
                    Layout layout32 = getLayout();
                    int lineForOffset32 = layout32.getLineForOffset(getSelectionStart());
                    int lineCount32 = layout32.getLineCount();
                    updateCursorPosition();
                    Rect bounds32 = this.gradientDrawable.getBounds();
                    Rect rect52 = this.rect;
                    rect52.left = bounds32.left;
                    rect52.right = bounds32.left + AndroidUtilities.dp(this.cursorWidth);
                    Rect rect62 = this.rect;
                    int i82 = bounds32.bottom;
                    rect62.bottom = i82;
                    rect62.top = bounds32.top;
                    f = this.lineSpacingExtra;
                    rect62.bottom = (int) (((float) i82) - f);
                    int centerY32 = rect62.centerY();
                    int i92 = this.cursorSize;
                    rect62.top = centerY32 - (i92 / 2);
                    Rect rect72 = this.rect;
                    rect72.bottom = rect72.top + i92;
                    this.gradientDrawable.setBounds(rect72);
                    this.gradientDrawable.draw(canvas2);
                    canvas.restore();
                    this.cursorDrawn = false;
                }
                i2 = 0;
                canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + i2));
                Layout layout322 = getLayout();
                int lineForOffset322 = layout322.getLineForOffset(getSelectionStart());
                int lineCount322 = layout322.getLineCount();
                updateCursorPosition();
                Rect bounds322 = this.gradientDrawable.getBounds();
                Rect rect522 = this.rect;
                rect522.left = bounds322.left;
                rect522.right = bounds322.left + AndroidUtilities.dp(this.cursorWidth);
                Rect rect622 = this.rect;
                int i822 = bounds322.bottom;
                rect622.bottom = i822;
                rect622.top = bounds322.top;
                f = this.lineSpacingExtra;
                rect622.bottom = (int) (((float) i822) - f);
                int centerY322 = rect622.centerY();
                int i922 = this.cursorSize;
                rect622.top = centerY322 - (i922 / 2);
                Rect rect722 = this.rect;
                rect722.bottom = rect722.top + i922;
                this.gradientDrawable.setBounds(rect722);
                this.gradientDrawable.draw(canvas2);
                canvas.restore();
                this.cursorDrawn = false;
            } catch (Throwable th2) {
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    throw new RuntimeException(th2);
                }
            }
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
        this.lastText = layout.getText();
        this.lastOffset = selectionStart;
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
