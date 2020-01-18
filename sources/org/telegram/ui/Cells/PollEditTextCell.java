package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class PollEditTextCell extends FrameLayout {
    private CheckBox2 checkBox;
    private AnimatorSet checkBoxAnimation;
    private ImageView deleteImageView;
    private ImageView moveImageView;
    private boolean needDivider;
    private boolean showNextButton;
    private EditTextBoldCursor textView;
    private SimpleTextView textView2;

    /* Access modifiers changed, original: protected */
    public boolean drawDivider() {
        return true;
    }

    /* Access modifiers changed, original: protected */
    public boolean isChecked(PollEditTextCell pollEditTextCell) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public void onEditTextDraw(EditTextBoldCursor editTextBoldCursor, Canvas canvas) {
    }

    /* Access modifiers changed, original: protected */
    public boolean shouldShowCheckBox() {
        return false;
    }

    public PollEditTextCell(Context context, OnClickListener onClickListener) {
        Context context2 = context;
        OnClickListener onClickListener2 = onClickListener;
        super(context);
        this.textView = new EditTextBoldCursor(context2) {
            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
                if (PollEditTextCell.this.showNextButton) {
                    editorInfo.imeOptions &= -NUM;
                }
                return onCreateInputConnection;
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                PollEditTextCell.this.onEditTextDraw(this, canvas);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (isEnabled()) {
                    return super.onTouchEvent(motionEvent);
                }
                return false;
            }
        };
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.textView.setTextSize(1, 16.0f);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable(null);
        EditTextBoldCursor editTextBoldCursor = this.textView;
        editTextBoldCursor.setImeOptions(editTextBoldCursor.getImeOptions() | NUM);
        editTextBoldCursor = this.textView;
        editTextBoldCursor.setInputType(editTextBoldCursor.getInputType() | 16384);
        if (onClickListener2 != null) {
            editTextBoldCursor = this.textView;
            int i2 = (LocaleController.isRTL ? 5 : 3) | 16;
            float f = 64.0f;
            float f2 = LocaleController.isRTL ? 58.0f : 64.0f;
            if (!LocaleController.isRTL) {
                f = 58.0f;
            }
            addView(editTextBoldCursor, LayoutHelper.createFrame(-1, -2.0f, i2, f2, 0.0f, f, 0.0f));
            this.moveImageView = new ImageView(context2);
            this.moveImageView.setFocusable(false);
            this.moveImageView.setScaleType(ScaleType.CENTER);
            this.moveImageView.setImageResource(NUM);
            String str = "windowBackgroundWhiteGrayIcon";
            this.moveImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            addView(this.moveImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, 6.0f, 2.0f, 6.0f, 0.0f));
            this.deleteImageView = new ImageView(context2);
            this.deleteImageView.setFocusable(false);
            this.deleteImageView.setScaleType(ScaleType.CENTER);
            this.deleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.deleteImageView.setImageResource(NUM);
            this.deleteImageView.setOnClickListener(onClickListener2);
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            this.deleteImageView.setContentDescription(LocaleController.getString("Delete", NUM));
            addView(this.deleteImageView, LayoutHelper.createFrame(48, 50.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 3.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 3.0f, 0.0f));
            this.textView2 = new SimpleTextView(context2);
            this.textView2.setTextSize(13);
            this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            addView(this.textView2, LayoutHelper.createFrame(48, 24.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 20.0f : 0.0f, 43.0f, LocaleController.isRTL ? 0.0f : 20.0f, 0.0f));
            this.checkBox = new CheckBox2(context2, 21);
            this.checkBox.setColor(null, str, "checkboxCheck");
            this.checkBox.setDrawUnchecked(true);
            this.checkBox.setChecked(true, false);
            this.checkBox.setAlpha(0.0f);
            this.checkBox.setDrawBackgroundAsArc(8);
            CheckBox2 checkBox2 = this.checkBox;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(checkBox2, LayoutHelper.createFrame(48, 48.0f, i | 48, 6.0f, 2.0f, 6.0f, 0.0f));
            this.checkBox.setOnClickListener(new -$$Lambda$PollEditTextCell$KFMPMXq2QBW-7KDo8Cvt5s2cCnQ(this));
            return;
        }
        EditTextBoldCursor editTextBoldCursor2 = this.textView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(editTextBoldCursor2, LayoutHelper.createFrame(-1, -2.0f, i | 16, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    public /* synthetic */ void lambda$new$0$PollEditTextCell(View view) {
        if (this.checkBox.getTag() != null) {
            onCheckBoxClick(this, this.checkBox.isChecked() ^ 1);
        }
    }

    public void createErrorTextView() {
        this.textView2 = new SimpleTextView(getContext());
        this.textView2.setTextSize(13);
        int i = 3;
        this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        SimpleTextView simpleTextView = this.textView2;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(simpleTextView, LayoutHelper.createFrame(48, 24.0f, i | 48, LocaleController.isRTL ? 20.0f : 0.0f, 17.0f, LocaleController.isRTL ? 0.0f : 20.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }
        imageView = this.moveImageView;
        if (imageView != null) {
            imageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }
        EditTextBoldCursor editTextBoldCursor = this.textView;
        int paddingLeft = (i - getPaddingLeft()) - getPaddingRight();
        float f = (this.textView2 == null || this.textView.getBackground() != null) ? 42.0f : 122.0f;
        editTextBoldCursor.measure(MeasureSpec.makeMeasureSpec(paddingLeft - AndroidUtilities.dp(f), NUM), MeasureSpec.makeMeasureSpec(0, 0));
        i2 = this.textView.getMeasuredHeight();
        setMeasuredDimension(i, Math.max(AndroidUtilities.dp(50.0f), this.textView.getMeasuredHeight()) + this.needDivider);
        SimpleTextView simpleTextView2 = this.textView2;
        if (simpleTextView2 != null) {
            simpleTextView2.setAlpha(i2 >= AndroidUtilities.dp(52.0f) ? 1.0f : 0.0f);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.checkBox != null) {
            setShowCheckBox(shouldShowCheckBox(), false);
            this.checkBox.setChecked(isChecked(this), false);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onCheckBoxClick(PollEditTextCell pollEditTextCell, boolean z) {
        this.checkBox.setChecked(z, true);
    }

    public void callOnDelete() {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.callOnClick();
        }
    }

    public void setShowNextButton(boolean z) {
        this.showNextButton = z;
    }

    public EditTextBoldCursor getTextView() {
        return this.textView;
    }

    public CheckBox2 getCheckBox() {
        return this.checkBox;
    }

    public void addTextWatcher(TextWatcher textWatcher) {
        this.textView.addTextChangedListener(textWatcher);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public String getText() {
        return this.textView.getText().toString();
    }

    public int length() {
        return this.textView.length();
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setShowCheckBox(boolean z, boolean z2) {
        if (z != (this.checkBox.getTag() != null)) {
            AnimatorSet animatorSet = this.checkBoxAnimation;
            Object obj = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.checkBoxAnimation = null;
            }
            CheckBox2 checkBox2 = this.checkBox;
            if (z) {
                obj = Integer.valueOf(1);
            }
            checkBox2.setTag(obj);
            float f = 1.0f;
            if (z2) {
                this.checkBoxAnimation = new AnimatorSet();
                AnimatorSet animatorSet2 = this.checkBoxAnimation;
                Animator[] animatorArr = new Animator[2];
                CheckBox2 checkBox22 = this.checkBox;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(checkBox22, property, fArr);
                ImageView imageView = this.moveImageView;
                property = View.ALPHA;
                fArr = new float[1];
                if (z) {
                    f = 0.0f;
                }
                fArr[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView, property, fArr);
                animatorSet2.playTogether(animatorArr);
                this.checkBoxAnimation.setDuration(180);
                this.checkBoxAnimation.start();
            } else {
                this.checkBox.setAlpha(z ? 1.0f : 0.0f);
                ImageView imageView2 = this.moveImageView;
                if (z) {
                    f = 0.0f;
                }
                imageView2.setAlpha(f);
            }
        }
    }

    public void setText(CharSequence charSequence, boolean z) {
        this.textView.setText(charSequence);
        this.needDivider = z;
        setWillNotDraw(z ^ 1);
    }

    public void setTextAndHint(CharSequence charSequence, String str, boolean z) {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.setTag(null);
        }
        this.textView.setText(charSequence);
        if (!TextUtils.isEmpty(charSequence)) {
            EditTextBoldCursor editTextBoldCursor = this.textView;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
        }
        this.textView.setHint(str);
        this.needDivider = z;
        setWillNotDraw(z ^ 1);
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        setEnabled(z);
    }

    public void setText2(String str) {
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.setText(str);
        }
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider && drawDivider()) {
            float f;
            int dp;
            float f2 = 63.0f;
            if (LocaleController.isRTL) {
                f = 0.0f;
            } else {
                f = (float) AndroidUtilities.dp(this.moveImageView != null ? 63.0f : 20.0f);
            }
            float measuredHeight = (float) (getMeasuredHeight() - 1);
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                if (this.moveImageView == null) {
                    f2 = 20.0f;
                }
                dp = AndroidUtilities.dp(f2);
            } else {
                dp = 0;
            }
            canvas.drawLine(f, measuredHeight, (float) (measuredWidth - dp), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
