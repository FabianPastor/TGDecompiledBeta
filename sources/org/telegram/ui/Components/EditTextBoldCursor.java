package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.FloatingActionMode;
import org.telegram.ui.ActionBar.FloatingToolbar;
import org.telegram.ui.ActionBar.Theme;

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
    private StaticLayout errorLayout;
    private int errorLineColor;
    private TextPaint errorPaint;
    private CharSequence errorText;
    private boolean fixed;
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
    private Drawable mCursorDrawable;
    private Rect mTempRect;
    private boolean nextSetTextAnimated;
    private Rect rect = new Rect();
    private int scrollY;
    private boolean supportRtlHint;
    private boolean transformHintToHeader;
    private View windowView;

    private class ActionModeCallback2Wrapper extends ActionMode.Callback2 {
        private final ActionMode.Callback mWrapped;

        public ActionModeCallback2Wrapper(ActionMode.Callback wrapped) {
            this.mWrapped = wrapped;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return this.mWrapped.onCreateActionMode(mode, menu);
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(mode, menu);
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return this.mWrapped.onActionItemClicked(mode, item);
        }

        public void onDestroyActionMode(ActionMode mode) {
            this.mWrapped.onDestroyActionMode(mode);
            EditTextBoldCursor.this.cleanupFloatingActionModeViews();
            EditTextBoldCursor.this.floatingActionMode = null;
        }

        public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
            ActionMode.Callback callback = this.mWrapped;
            if (callback instanceof ActionMode.Callback2) {
                ((ActionMode.Callback2) callback).onGetContentRect(mode, view, outRect);
            } else {
                super.onGetContentRect(mode, view, outRect);
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
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape()) {
            public void draw(Canvas canvas) {
                super.draw(canvas);
                boolean unused = EditTextBoldCursor.this.cursorDrawn = true;
            }
        };
        shapeDrawable.getPaint().setColor(0);
        return shapeDrawable;
    }

    public int getAutofillType() {
        return 0;
    }

    private void init() {
        this.linePaint = new Paint();
        TextPaint textPaint = new TextPaint(1);
        this.errorPaint = textPaint;
        textPaint.setTextSize((float) AndroidUtilities.dp(11.0f));
        if (Build.VERSION.SDK_INT >= 26) {
            setImportantForAutofill(2);
        }
        if (Build.VERSION.SDK_INT >= 29) {
            AnonymousClass3 r0 = new ShapeDrawable() {
                public void draw(Canvas canvas) {
                    if (EditTextBoldCursor.this.drawInMaim) {
                        boolean unused = EditTextBoldCursor.this.cursorDrawn = true;
                    } else {
                        super.draw(canvas);
                    }
                }

                public int getIntrinsicHeight() {
                    return AndroidUtilities.dp((float) (EditTextBoldCursor.this.cursorSize + 20));
                }

                public int getIntrinsicWidth() {
                    return AndroidUtilities.dp(EditTextBoldCursor.this.cursorWidth);
                }
            };
            this.cursorDrawable = r0;
            r0.setShape(new RectShape());
            this.gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{-11230757, -11230757});
            setTextCursorDrawable(this.cursorDrawable);
        }
        try {
            if (!mScrollYGet && mScrollYField == null) {
                mScrollYGet = true;
                Field declaredField = View.class.getDeclaredField("mScrollY");
                mScrollYField = declaredField;
                declaredField.setAccessible(true);
            }
        } catch (Throwable th) {
        }
        try {
            if (editorClass == null) {
                Field declaredField2 = TextView.class.getDeclaredField("mEditor");
                mEditor = declaredField2;
                declaredField2.setAccessible(true);
                Class<?> cls = Class.forName("android.widget.Editor");
                editorClass = cls;
                try {
                    Field declaredField3 = cls.getDeclaredField("mShowCursor");
                    mShowCursorField = declaredField3;
                    declaredField3.setAccessible(true);
                } catch (Exception e) {
                }
                Class<TextView> cls2 = TextView.class;
                Method declaredMethod = cls2.getDeclaredMethod("getVerticalOffset", new Class[]{Boolean.TYPE});
                getVerticalOffsetMethod = declaredMethod;
                declaredMethod.setAccessible(true);
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        if (this.cursorDrawable == null) {
            try {
                this.gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{-11230757, -11230757});
                if (Build.VERSION.SDK_INT >= 29) {
                    setTextCursorDrawable(this.gradientDrawable);
                }
                this.editor = mEditor.get(this);
            } catch (Throwable th2) {
            }
            try {
                if (mCursorDrawableResField == null) {
                    Field declaredField4 = TextView.class.getDeclaredField("mCursorDrawableRes");
                    mCursorDrawableResField = declaredField4;
                    declaredField4.setAccessible(true);
                }
                Field field = mCursorDrawableResField;
                if (field != null) {
                    field.set(this, NUM);
                }
            } catch (Throwable th3) {
            }
        }
        this.cursorSize = AndroidUtilities.dp(24.0f);
    }

    public void fixHandleView(boolean reset) {
        if (reset) {
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
                    Method initDrawablesMethod = editorClass.getDeclaredMethod("getPositionListener", new Class[0]);
                    initDrawablesMethod.setAccessible(true);
                    this.listenerFixer = (ViewTreeObserver.OnPreDrawListener) initDrawablesMethod.invoke(this.editor, new Object[0]);
                }
                ViewTreeObserver.OnPreDrawListener onPreDrawListener = this.listenerFixer;
                onPreDrawListener.getClass();
                AndroidUtilities.runOnUIThread(new EditTextBoldCursor$$ExternalSyntheticLambda1(onPreDrawListener), 500);
            } catch (Throwable th) {
            }
            this.fixed = true;
        }
    }

    public void setTransformHintToHeader(boolean value) {
        if (this.transformHintToHeader != value) {
            this.transformHintToHeader = value;
            AnimatorSet animatorSet = this.headerTransformAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.headerTransformAnimation = null;
            }
        }
    }

    public void setAllowDrawCursor(boolean value) {
        this.allowDrawCursor = value;
        invalidate();
    }

    public void setCursorWidth(float width) {
        this.cursorWidth = width;
    }

    public void setCursorColor(int color) {
        ShapeDrawable shapeDrawable = this.cursorDrawable;
        if (shapeDrawable != null) {
            shapeDrawable.getPaint().setColor(color);
        }
        GradientDrawable gradientDrawable2 = this.gradientDrawable;
        if (gradientDrawable2 != null) {
            gradientDrawable2.setColor(color);
        }
        invalidate();
    }

    public void setCursorSize(int value) {
        this.cursorSize = value;
    }

    public void setErrorLineColor(int error) {
        this.errorLineColor = error;
        this.errorPaint.setColor(error);
        invalidate();
    }

    public void setLineColors(int color, int active, int error) {
        this.lineColor = color;
        this.activeLineColor = active;
        this.errorLineColor = error;
        this.errorPaint.setColor(error);
        invalidate();
    }

    public void setHintVisible(boolean value) {
        if (this.hintVisible != value) {
            this.lastUpdateTime = System.currentTimeMillis();
            this.hintVisible = value;
            invalidate();
        }
    }

    public void setHintColor(int value) {
        this.hintColor = value;
        invalidate();
    }

    public void setHeaderHintColor(int value) {
        this.headerHintColor = value;
        invalidate();
    }

    public void setNextSetTextAnimated(boolean value) {
        this.nextSetTextAnimated = value;
    }

    public void setErrorText(CharSequence text) {
        if (!TextUtils.equals(text, this.errorText)) {
            this.errorText = text;
            requestLayout();
        }
    }

    public boolean hasErrorText() {
        return !TextUtils.isEmpty(this.errorText);
    }

    public StaticLayout getErrorLayout(int width) {
        if (TextUtils.isEmpty(this.errorText)) {
            return null;
        }
        return new StaticLayout(this.errorText, this.errorPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    public float getLineY() {
        return this.lineY;
    }

    public void setSupportRtlHint(boolean value) {
        this.supportRtlHint = value;
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
        if (horiz != oldHoriz) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    public void setText(CharSequence text, TextView.BufferType type) {
        super.setText(text, type);
        checkHeaderVisibility(this.nextSetTextAnimated);
        this.nextSetTextAnimated = false;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int currentSize = getMeasuredHeight() + (getMeasuredWidth() << 16);
        if (this.hintLayout != null) {
            if (this.lastSize != currentSize) {
                setHintText(this.hint);
            }
            this.lineY = (((float) (getMeasuredHeight() - this.hintLayout.getHeight())) / 2.0f) + ((float) this.hintLayout.getHeight()) + ((float) AndroidUtilities.dp(6.0f));
        }
        this.lastSize = currentSize;
    }

    public void setHintText(CharSequence text) {
        setHintText(text, false);
    }

    public void setHintText(CharSequence text, boolean animated) {
        if (text == null) {
            text = "";
        }
        if (getMeasuredWidth() == 0) {
            animated = false;
        }
        if (animated) {
            if (this.hintAnimator == null) {
                this.hintAnimator = new SubstringLayoutAnimator(this);
            }
            this.hintAnimator.create(this.hintLayout, this.hint, text, getPaint());
        } else {
            SubstringLayoutAnimator substringLayoutAnimator = this.hintAnimator;
            if (substringLayoutAnimator != null) {
                substringLayoutAnimator.cancel();
            }
        }
        this.hint = text;
        if (getMeasuredWidth() != 0) {
            text = TextUtils.ellipsize(text, getPaint(), (float) getMeasuredWidth(), TextUtils.TruncateAt.END);
            StaticLayout staticLayout = this.hintLayout;
            if (staticLayout != null && TextUtils.equals(staticLayout.getText(), text)) {
                return;
            }
        }
        this.hintLayout = new StaticLayout(text, getPaint(), AndroidUtilities.dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    public Layout getHintLayoutEx() {
        return this.hintLayout;
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        try {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        checkHeaderVisibility(true);
    }

    private void checkHeaderVisibility(boolean animated) {
        boolean newHintHeader = this.transformHintToHeader && (isFocused() || getText().length() > 0);
        if (this.currentDrawHintAsHeader != newHintHeader) {
            AnimatorSet animatorSet = this.headerTransformAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.headerTransformAnimation = null;
            }
            this.currentDrawHintAsHeader = newHintHeader;
            float f = 1.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.headerTransformAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[1];
                float[] fArr = new float[1];
                if (!newHintHeader) {
                    f = 0.0f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(this, "headerAnimationProgress", fArr);
                animatorSet2.playTogether(animatorArr);
                this.headerTransformAnimation.setDuration(200);
                this.headerTransformAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.headerTransformAnimation.start();
            } else {
                if (!newHintHeader) {
                    f = 0.0f;
                }
                this.headerAnimationProgress = f;
            }
            invalidate();
        }
    }

    public void setHeaderAnimationProgress(float value) {
        this.headerAnimationProgress = value;
        invalidate();
    }

    public float getHeaderAnimationProgress() {
        return this.headerAnimationProgress;
    }

    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        this.lineSpacingExtra = add;
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
        boolean showCursor;
        Object obj;
        int i;
        boolean z;
        float hintWidth;
        Canvas canvas2 = canvas;
        if ((length() == 0 || this.transformHintToHeader) && this.hintLayout != null && ((z = this.hintVisible) || this.hintAlpha != 0.0f)) {
            if ((z && this.hintAlpha != 1.0f) || (!z && this.hintAlpha != 0.0f)) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                if (dt < 0 || dt > 17) {
                    dt = 17;
                }
                this.lastUpdateTime = newTime;
                if (this.hintVisible) {
                    float f = this.hintAlpha + (((float) dt) / 150.0f);
                    this.hintAlpha = f;
                    if (f > 1.0f) {
                        this.hintAlpha = 1.0f;
                    }
                } else {
                    float f2 = this.hintAlpha - (((float) dt) / 150.0f);
                    this.hintAlpha = f2;
                    if (f2 < 0.0f) {
                        this.hintAlpha = 0.0f;
                    }
                }
                invalidate();
            }
            int oldColor = getPaint().getColor();
            canvas.save();
            int left = 0;
            float lineLeft = this.hintLayout.getLineLeft(0);
            float hintWidth2 = this.hintLayout.getLineWidth(0);
            if (lineLeft != 0.0f) {
                left = (int) (((float) 0) - lineLeft);
            }
            if (!this.supportRtlHint || !LocaleController.isRTL) {
                canvas2.translate((float) (getScrollX() + left), (this.lineY - ((float) this.hintLayout.getHeight())) - ((float) AndroidUtilities.dp2(7.0f)));
            } else {
                canvas2.translate(((float) (getScrollX() + left)) + (((float) getMeasuredWidth()) - hintWidth2), (this.lineY - ((float) this.hintLayout.getHeight())) - ((float) AndroidUtilities.dp(7.0f)));
            }
            if (this.transformHintToHeader) {
                float scale = 1.0f - (this.headerAnimationProgress * 0.3f);
                float translation = ((float) (-AndroidUtilities.dp(22.0f))) * this.headerAnimationProgress;
                int rF = Color.red(this.headerHintColor);
                int gF = Color.green(this.headerHintColor);
                int bF = Color.blue(this.headerHintColor);
                int aF = Color.alpha(this.headerHintColor);
                int rS = Color.red(this.hintColor);
                int gS = Color.green(this.hintColor);
                int bS = Color.blue(this.hintColor);
                int aS = Color.alpha(this.hintColor);
                int i2 = left;
                if (this.supportRtlHint == 0 || !LocaleController.isRTL) {
                    hintWidth = 0.0f;
                    if (lineLeft != 0.0f) {
                        canvas2.translate(lineLeft * (1.0f - scale), 0.0f);
                    }
                } else {
                    float f3 = (hintWidth2 + lineLeft) - ((hintWidth2 + lineLeft) * scale);
                    float f4 = hintWidth2;
                    hintWidth = 0.0f;
                    canvas2.translate(f3, 0.0f);
                }
                canvas2.scale(scale, scale);
                canvas2.translate(hintWidth, translation);
                TextPaint paint = getPaint();
                float f5 = (float) aS;
                float f6 = lineLeft;
                int i3 = aS;
                float f7 = this.headerAnimationProgress;
                float f8 = scale;
                float f9 = translation;
                int i4 = gS;
                paint.setColor(Color.argb((int) (f5 + (((float) (aF - aS)) * f7)), (int) (((float) rS) + (((float) (rF - rS)) * f7)), (int) (((float) gS) + (((float) (gF - gS)) * f7)), (int) (((float) bS) + (((float) (bF - bS)) * f7))));
            } else {
                float var_ = lineLeft;
                float var_ = hintWidth2;
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
            getPaint().setColor(oldColor);
            canvas.restore();
        }
        int topPadding = getExtendedPaddingTop();
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
        canvas2.translate(0.0f, (float) topPadding);
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
        if (!(field2 == null || (i = this.scrollY) == Integer.MAX_VALUE)) {
            try {
                field2.set(this, Integer.valueOf(i));
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
                    showCursor = this.cursorDrawn;
                    this.cursorDrawn = false;
                } else {
                    showCursor = (SystemClock.uptimeMillis() - field3.getLong(obj)) % 1000 < 500 && isFocused();
                }
                if (this.allowDrawCursor && showCursor) {
                    canvas.save();
                    int voffsetCursor = 0;
                    if (getVerticalOffsetMethod != null) {
                        if ((getGravity() & 112) != 48) {
                            voffsetCursor = ((Integer) getVerticalOffsetMethod.invoke(this, new Object[]{true})).intValue();
                        }
                    } else if ((getGravity() & 112) != 48) {
                        voffsetCursor = getTotalPaddingTop() - getExtendedPaddingTop();
                    }
                    canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + voffsetCursor));
                    Layout layout = getLayout();
                    int line = layout.getLineForOffset(getSelectionStart());
                    int lineCount = layout.getLineCount();
                    updateCursorPosition();
                    Rect bounds = this.gradientDrawable.getBounds();
                    this.rect.left = bounds.left;
                    this.rect.right = bounds.left + AndroidUtilities.dp(this.cursorWidth);
                    this.rect.bottom = bounds.bottom;
                    this.rect.top = bounds.top;
                    if (this.lineSpacingExtra != 0.0f && line < lineCount - 1) {
                        Rect rect2 = this.rect;
                        rect2.bottom = (int) (((float) rect2.bottom) - this.lineSpacingExtra);
                    }
                    Rect rect3 = this.rect;
                    rect3.top = rect3.centerY() - (this.cursorSize / 2);
                    Rect rect4 = this.rect;
                    rect4.bottom = rect4.top + this.cursorSize;
                    this.gradientDrawable.setBounds(this.rect);
                    this.gradientDrawable.draw(canvas2);
                    canvas.restore();
                }
            } catch (Throwable exception) {
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    throw new RuntimeException(exception);
                }
            }
        } else if (this.cursorDrawn) {
            try {
                canvas.save();
                int voffsetCursor2 = 0;
                if (getVerticalOffsetMethod != null) {
                    if ((getGravity() & 112) != 48) {
                        voffsetCursor2 = ((Integer) getVerticalOffsetMethod.invoke(this, new Object[]{true})).intValue();
                    }
                } else if ((getGravity() & 112) != 48) {
                    voffsetCursor2 = getTotalPaddingTop() - getExtendedPaddingTop();
                }
                canvas2.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + voffsetCursor2));
                Layout layout2 = getLayout();
                int line2 = layout2.getLineForOffset(getSelectionStart());
                int lineCount2 = layout2.getLineCount();
                updateCursorPosition();
                Rect bounds2 = this.gradientDrawable.getBounds();
                this.rect.left = bounds2.left;
                this.rect.right = bounds2.left + AndroidUtilities.dp(this.cursorWidth);
                this.rect.bottom = bounds2.bottom;
                this.rect.top = bounds2.top;
                if (this.lineSpacingExtra != 0.0f && line2 < lineCount2 - 1) {
                    Rect rect5 = this.rect;
                    rect5.bottom = (int) (((float) rect5.bottom) - this.lineSpacingExtra);
                }
                Rect rect6 = this.rect;
                rect6.top = rect6.centerY() - (this.cursorSize / 2);
                Rect rect7 = this.rect;
                rect7.bottom = rect7.top + this.cursorSize;
                this.gradientDrawable.setBounds(this.rect);
                this.gradientDrawable.draw(canvas2);
                canvas.restore();
                this.cursorDrawn = false;
            } catch (Throwable exception2) {
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    throw new RuntimeException(exception2);
                }
            }
        }
        if (this.lineColor != 0 && this.hintLayout != null) {
            if (!TextUtils.isEmpty(this.errorText)) {
                this.linePaint.setColor(this.errorLineColor);
                int dp = AndroidUtilities.dp(2.0f);
            } else if (isFocused() != 0) {
                this.linePaint.setColor(this.activeLineColor);
                int dp2 = AndroidUtilities.dp(2.0f);
            } else {
                this.linePaint.setColor(this.lineColor);
                AndroidUtilities.dp(1.0f);
            }
        }
    }

    public void setWindowView(View view) {
        this.windowView = view;
    }

    private boolean updateCursorPosition() {
        Layout layout = getLayout();
        int offset = getSelectionStart();
        int line = layout.getLineForOffset(offset);
        updateCursorPosition(layout.getLineTop(line), layout.getLineTop(line + 1), layout.getPrimaryHorizontal(offset));
        this.lastText = layout.getText();
        this.lastOffset = offset;
        return true;
    }

    private int clampHorizontalPosition(Drawable drawable, float horizontal) {
        float horizontal2 = Math.max(0.5f, horizontal - 0.5f);
        if (this.mTempRect == null) {
            this.mTempRect = new Rect();
        }
        int drawableWidth = 0;
        if (drawable != null) {
            drawable.getPadding(this.mTempRect);
            drawableWidth = drawable.getIntrinsicWidth();
        } else {
            this.mTempRect.setEmpty();
        }
        int scrollX = getScrollX();
        float horizontalDiff = horizontal2 - ((float) scrollX);
        int viewClippedWidth = (getWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        if (horizontalDiff >= ((float) viewClippedWidth) - 1.0f) {
            return (viewClippedWidth + scrollX) - (drawableWidth - this.mTempRect.right);
        }
        if (Math.abs(horizontalDiff) <= 1.0f || (TextUtils.isEmpty(getText()) && ((float) (1048576 - scrollX)) <= ((float) viewClippedWidth) + 1.0f && horizontal2 <= 1.0f)) {
            return scrollX - this.mTempRect.left;
        }
        return ((int) horizontal2) - this.mTempRect.left;
    }

    private void updateCursorPosition(int top, int bottom, float horizontal) {
        int left = clampHorizontalPosition(this.gradientDrawable, horizontal);
        this.gradientDrawable.setBounds(left, top - this.mTempRect.top, left + AndroidUtilities.dp(this.cursorWidth), this.mTempRect.bottom + bottom);
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
        this.floatingToolbar = new FloatingToolbar(context, view, getActionModeStyle(), getResourcesProvider());
        this.floatingActionMode = new FloatingActionMode(getContext(), new ActionModeCallback2Wrapper(callback), this, this.floatingToolbar);
        this.floatingToolbarPreDrawListener = new EditTextBoldCursor$$ExternalSyntheticLambda0(this);
        FloatingActionMode floatingActionMode3 = this.floatingActionMode;
        callback.onCreateActionMode(floatingActionMode3, floatingActionMode3.getMenu());
        FloatingActionMode floatingActionMode4 = this.floatingActionMode;
        extendActionMode(floatingActionMode4, floatingActionMode4.getMenu());
        this.floatingActionMode.invalidate();
        getViewTreeObserver().addOnPreDrawListener(this.floatingToolbarPreDrawListener);
        invalidate();
        return this.floatingActionMode;
    }

    /* renamed from: lambda$startActionMode$0$org-telegram-ui-Components-EditTextBoldCursor  reason: not valid java name */
    public /* synthetic */ boolean m2229x60d1var_() {
        FloatingActionMode floatingActionMode2 = this.floatingActionMode;
        if (floatingActionMode2 == null) {
            return true;
        }
        floatingActionMode2.updateViewLocationInWindow();
        return true;
    }

    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        if (Build.VERSION.SDK_INT < 23 || (this.windowView == null && this.attachedToWindow == null)) {
            return super.startActionMode(callback, type);
        }
        return startActionMode(callback);
    }

    /* access modifiers changed from: protected */
    public void extendActionMode(ActionMode actionMode, Menu menu) {
    }

    /* access modifiers changed from: protected */
    public int getActionModeStyle() {
        return 1;
    }

    public void hideActionMode() {
        cleanupFloatingActionModeViews();
    }

    public void setSelection(int start, int stop) {
        try {
            super.setSelection(start, stop);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void setSelection(int index) {
        try {
            super.setSelection(index);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.EditText");
        if (this.hintLayout != null) {
            AccessibilityNodeInfoCompat.wrap(info).setHintText(this.hintLayout.getText());
        }
    }

    /* access modifiers changed from: protected */
    public Theme.ResourcesProvider getResourcesProvider() {
        return null;
    }
}
