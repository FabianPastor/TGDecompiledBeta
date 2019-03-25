package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.support.annotation.Keep;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
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

    public EditTextBoldCursor(Context context) {
        super(context);
        init();
    }

    @SuppressLint({"PrivateApi"})
    private void init() {
        this.linePaint = new Paint();
        this.errorPaint = new TextPaint(1);
        this.errorPaint.setTextSize((float) AndroidUtilities.dp(11.0f));
        try {
            if (mScrollYField == null) {
                mScrollYField = View.class.getDeclaredField("mScrollY");
                mScrollYField.setAccessible(true);
            }
        } catch (Throwable th) {
        }
        try {
            if (mCursorDrawableResField == null) {
                mCursorDrawableResField = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableResField.setAccessible(true);
            }
        } catch (Throwable th2) {
        }
        try {
            if (editorClass == null) {
                mEditor = TextView.class.getDeclaredField("mEditor");
                mEditor.setAccessible(true);
                editorClass = Class.forName("android.widget.Editor");
                mShowCursorField = editorClass.getDeclaredField("mShowCursor");
                mShowCursorField.setAccessible(true);
                if (VERSION.SDK_INT >= 28) {
                    mCursorDrawableField = editorClass.getDeclaredField("mDrawableForCursor");
                    mCursorDrawableField.setAccessible(true);
                } else {
                    mCursorDrawableField = editorClass.getDeclaredField("mCursorDrawable");
                    mCursorDrawableField.setAccessible(true);
                }
                getVerticalOffsetMethod = TextView.class.getDeclaredMethod("getVerticalOffset", new Class[]{Boolean.TYPE});
                getVerticalOffsetMethod.setAccessible(true);
                mShowCursorField = editorClass.getDeclaredField("mShowCursor");
                mShowCursorField.setAccessible(true);
                getVerticalOffsetMethod = TextView.class.getDeclaredMethod("getVerticalOffset", new Class[]{Boolean.TYPE});
                getVerticalOffsetMethod.setAccessible(true);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        try {
            this.gradientDrawable = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{-11230757, -11230757});
            this.editor = mEditor.get(this);
            if (mCursorDrawableField != null) {
                mCursorDrawableResField.set(this, Integer.valueOf(NUM));
            }
        } catch (Throwable th3) {
        }
        this.cursorSize = AndroidUtilities.dp(24.0f);
    }

    @SuppressLint({"PrivateApi"})
    public void fixHandleView(boolean reset) {
        if (reset) {
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
                    Method initDrawablesMethod = editorClass.getDeclaredMethod("getPositionListener", new Class[0]);
                    initDrawablesMethod.setAccessible(true);
                    this.listenerFixer = (OnPreDrawListener) initDrawablesMethod.invoke(this.editor, new Object[0]);
                }
                OnPreDrawListener onPreDrawListener = this.listenerFixer;
                onPreDrawListener.getClass();
                AndroidUtilities.runOnUIThread(EditTextBoldCursor$$Lambda$0.get$Lambda(onPreDrawListener), 500);
            } catch (Throwable th) {
            }
            this.fixed = true;
        }
    }

    public void setTransformHintToHeader(boolean value) {
        if (this.transformHintToHeader != value) {
            this.transformHintToHeader = value;
            if (this.headerTransformAnimation != null) {
                this.headerTransformAnimation.cancel();
                this.headerTransformAnimation = null;
            }
        }
    }

    public void setAllowDrawCursor(boolean value) {
        this.allowDrawCursor = value;
    }

    public void setCursorWidth(float width) {
        this.cursorWidth = width;
    }

    public void setCursorColor(int color) {
        this.gradientDrawable.setColor(color);
        invalidate();
    }

    public void setCursorSize(int value) {
        this.cursorSize = value;
    }

    public void setErrorLineColor(int error) {
        this.errorLineColor = error;
        this.errorPaint.setColor(this.errorLineColor);
        invalidate();
    }

    public void setLineColors(int color, int active, int error) {
        this.lineColor = color;
        this.activeLineColor = active;
        this.errorLineColor = error;
        this.errorPaint.setColor(this.errorLineColor);
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
        return new StaticLayout(this.errorText, this.errorPaint, width, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    public float getLineY() {
        return this.lineY;
    }

    public void setSupportRtlHint(boolean value) {
        this.supportRtlHint = value;
    }

    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        checkHeaderVisibility(this.nextSetTextAnimated);
        this.nextSetTextAnimated = false;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.hintLayout != null) {
            this.lineY = ((((float) (getMeasuredHeight() - this.hintLayout.getHeight())) / 2.0f) + ((float) this.hintLayout.getHeight())) + ((float) AndroidUtilities.dp(6.0f));
        }
    }

    public void setHintText(String text) {
        this.hintLayout = new StaticLayout(text, getPaint(), AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    /* Access modifiers changed, original: protected */
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        checkHeaderVisibility(true);
    }

    private void checkHeaderVisibility(boolean animated) {
        boolean newHintHeader;
        float f = 1.0f;
        if (!this.transformHintToHeader || (!isFocused() && getText().length() <= 0)) {
            newHintHeader = false;
        } else {
            newHintHeader = true;
        }
        if (this.currentDrawHintAsHeader != newHintHeader) {
            if (this.headerTransformAnimation != null) {
                this.headerTransformAnimation.cancel();
                this.headerTransformAnimation = null;
            }
            this.currentDrawHintAsHeader = newHintHeader;
            if (animated) {
                float f2;
                this.headerTransformAnimation = new AnimatorSet();
                AnimatorSet animatorSet = this.headerTransformAnimation;
                Animator[] animatorArr = new Animator[1];
                String str = "headerAnimationProgress";
                float[] fArr = new float[1];
                if (newHintHeader) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.0f;
                }
                fArr[0] = f2;
                animatorArr[0] = ObjectAnimator.ofFloat(this, str, fArr);
                animatorSet.playTogether(animatorArr);
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

    @Keep
    public void setHeaderAnimationProgress(float value) {
        this.headerAnimationProgress = value;
        invalidate();
    }

    @Keep
    public float getHeaderAnimationProgress() {
        return this.headerAnimationProgress;
    }

    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        this.lineSpacingExtra = add;
    }

    public int getExtendedPaddingTop() {
        if (this.ignoreTopCount == 0) {
            return super.getExtendedPaddingTop();
        }
        this.ignoreTopCount--;
        return 0;
    }

    public int getExtendedPaddingBottom() {
        if (this.ignoreBottomCount == 0) {
            return super.getExtendedPaddingBottom();
        }
        this.ignoreBottomCount--;
        return this.scrollY != Integer.MAX_VALUE ? -this.scrollY : 0;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        int topPadding = getExtendedPaddingTop();
        this.scrollY = Integer.MAX_VALUE;
        try {
            this.scrollY = mScrollYField.getInt(this);
            mScrollYField.set(this, Integer.valueOf(0));
        } catch (Exception e) {
        }
        this.ignoreTopCount = 1;
        this.ignoreBottomCount = 1;
        canvas.save();
        canvas.translate(0.0f, (float) topPadding);
        try {
            super.onDraw(canvas);
        } catch (Exception e2) {
        }
        if (this.scrollY != Integer.MAX_VALUE) {
            try {
                mScrollYField.set(this, Integer.valueOf(this.scrollY));
            } catch (Exception e3) {
            }
        }
        canvas.restore();
        if ((length() == 0 || this.transformHintToHeader) && this.hintLayout != null && (this.hintVisible || this.hintAlpha != 0.0f)) {
            if ((this.hintVisible && this.hintAlpha != 1.0f) || !(this.hintVisible || this.hintAlpha == 0.0f)) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                if (dt < 0 || dt > 17) {
                    dt = 17;
                }
                this.lastUpdateTime = newTime;
                if (this.hintVisible) {
                    this.hintAlpha += ((float) dt) / 150.0f;
                    if (this.hintAlpha > 1.0f) {
                        this.hintAlpha = 1.0f;
                    }
                } else {
                    this.hintAlpha -= ((float) dt) / 150.0f;
                    if (this.hintAlpha < 0.0f) {
                        this.hintAlpha = 0.0f;
                    }
                }
                invalidate();
            }
            int oldColor = getPaint().getColor();
            canvas.save();
            int left = 0;
            float lineLeft = this.hintLayout.getLineLeft(0);
            float hintWidth = this.hintLayout.getLineWidth(0);
            if (lineLeft != 0.0f) {
                left = (int) (((float) 0) - lineLeft);
            }
            if (this.supportRtlHint && LocaleController.isRTL) {
                canvas.translate(((float) (getScrollX() + left)) + (((float) getMeasuredWidth()) - hintWidth), (this.lineY - ((float) this.hintLayout.getHeight())) - ((float) AndroidUtilities.dp(6.0f)));
            } else {
                canvas.translate((float) (getScrollX() + left), (this.lineY - ((float) this.hintLayout.getHeight())) - ((float) AndroidUtilities.dp(6.0f)));
            }
            if (this.transformHintToHeader) {
                float scale = 1.0f - (0.3f * this.headerAnimationProgress);
                float translation = ((float) (-AndroidUtilities.dp(22.0f))) * this.headerAnimationProgress;
                int rF = Color.red(this.headerHintColor);
                int gF = Color.green(this.headerHintColor);
                int bF = Color.blue(this.headerHintColor);
                int aF = Color.alpha(this.headerHintColor);
                int rS = Color.red(this.hintColor);
                int gS = Color.green(this.hintColor);
                int bS = Color.blue(this.hintColor);
                int aS = Color.alpha(this.hintColor);
                if (this.supportRtlHint && LocaleController.isRTL) {
                    canvas.translate((hintWidth + lineLeft) - ((hintWidth + lineLeft) * scale), 0.0f);
                } else if (lineLeft != 0.0f) {
                    canvas.translate((1.0f - scale) * lineLeft, 0.0f);
                }
                canvas.scale(scale, scale);
                canvas.translate(0.0f, translation);
                getPaint().setColor(Color.argb((int) (((float) aS) + (((float) (aF - aS)) * this.headerAnimationProgress)), (int) (((float) rS) + (((float) (rF - rS)) * this.headerAnimationProgress)), (int) (((float) gS) + (((float) (gF - gS)) * this.headerAnimationProgress)), (int) (((float) bS) + (((float) (bF - bS)) * this.headerAnimationProgress))));
            } else {
                getPaint().setColor(this.hintColor);
                getPaint().setAlpha((int) ((255.0f * this.hintAlpha) * (((float) Color.alpha(this.hintColor)) / 255.0f)));
            }
            this.hintLayout.draw(canvas);
            getPaint().setColor(oldColor);
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
                    boolean showCursor = (SystemClock.uptimeMillis() - mShowCursorField.getLong(this.editor)) % 1000 < 500 && isFocused();
                    if (showCursor) {
                        canvas.save();
                        int voffsetCursor = 0;
                        if ((getGravity() & 112) != 48) {
                            voffsetCursor = ((Integer) getVerticalOffsetMethod.invoke(this, new Object[]{Boolean.valueOf(true)})).intValue();
                        }
                        canvas.translate((float) getPaddingLeft(), (float) (getExtendedPaddingTop() + voffsetCursor));
                        Layout layout = getLayout();
                        int line = layout.getLineForOffset(getSelectionStart());
                        int lineCount = layout.getLineCount();
                        Rect bounds = this.mCursorDrawable.getBounds();
                        this.rect.left = bounds.left;
                        this.rect.right = bounds.left + AndroidUtilities.dp(this.cursorWidth);
                        this.rect.bottom = bounds.bottom;
                        this.rect.top = bounds.top;
                        if (this.lineSpacingExtra != 0.0f && line < lineCount - 1) {
                            Rect rect = this.rect;
                            rect.bottom = (int) (((float) rect.bottom) - this.lineSpacingExtra);
                        }
                        this.rect.top = this.rect.centerY() - (this.cursorSize / 2);
                        this.rect.bottom = this.rect.top + this.cursorSize;
                        this.gradientDrawable.setBounds(this.rect);
                        this.gradientDrawable.draw(canvas);
                        canvas.restore();
                    }
                } else {
                    return;
                }
            }
        } catch (Throwable th) {
        }
        if (this.lineColor != 0 && this.hintLayout != null) {
            int h;
            if (!TextUtils.isEmpty(this.errorText)) {
                this.linePaint.setColor(this.errorLineColor);
                h = AndroidUtilities.dp(2.0f);
            } else if (isFocused()) {
                this.linePaint.setColor(this.activeLineColor);
                h = AndroidUtilities.dp(2.0f);
            } else {
                this.linePaint.setColor(this.lineColor);
                h = AndroidUtilities.dp(1.0f);
            }
            canvas.drawRect((float) getScrollX(), (float) ((int) this.lineY), (float) (getScrollX() + getMeasuredWidth()), ((float) h) + this.lineY, this.linePaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.EditText");
        if (this.hintLayout != null) {
            info.setContentDescription(this.hintLayout.getText());
        }
    }
}
