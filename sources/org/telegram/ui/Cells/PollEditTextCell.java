package org.telegram.ui.Cells;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextWatcher;
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
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class PollEditTextCell extends FrameLayout {
    private ImageView deleteImageView;
    private boolean needDivider;
    private boolean showNextButton;
    private EditTextBoldCursor textView;
    private SimpleTextView textView2;

    /* Access modifiers changed, original: protected */
    public boolean drawDivider() {
        return true;
    }

    public PollEditTextCell(Context context, OnClickListener onClickListener) {
        super(context);
        this.textView = new EditTextBoldCursor(context) {
            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
                if (PollEditTextCell.this.showNextButton) {
                    editorInfo.imeOptions &= -NUM;
                }
                return onCreateInputConnection;
            }
        };
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.textView.setTextSize(1, 16.0f);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable(null);
        this.textView.setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
        EditTextBoldCursor editTextBoldCursor = this.textView;
        editTextBoldCursor.setImeOptions(editTextBoldCursor.getImeOptions() | NUM);
        editTextBoldCursor = this.textView;
        editTextBoldCursor.setInputType(editTextBoldCursor.getInputType() | 16384);
        editTextBoldCursor = this.textView;
        int i2 = (LocaleController.isRTL ? 5 : 3) | 16;
        float f = (!LocaleController.isRTL || onClickListener == null) ? 21.0f : 58.0f;
        float f2 = (LocaleController.isRTL || onClickListener == null) ? 21.0f : 58.0f;
        addView(editTextBoldCursor, LayoutHelper.createFrame(-1, -2.0f, i2, f, 0.0f, f2, 0.0f));
        if (onClickListener != null) {
            this.deleteImageView = new ImageView(context);
            this.deleteImageView.setFocusable(false);
            this.deleteImageView.setScaleType(ScaleType.CENTER);
            this.deleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), Mode.MULTIPLY));
            this.deleteImageView.setImageResource(NUM);
            this.deleteImageView.setOnClickListener(onClickListener);
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText"), Mode.MULTIPLY));
            this.deleteImageView.setContentDescription(LocaleController.getString("Delete", NUM));
            addView(this.deleteImageView, LayoutHelper.createFrame(48, 50.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 3.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 3.0f, 0.0f));
            this.textView2 = new SimpleTextView(getContext());
            this.textView2.setTextSize(13);
            this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            SimpleTextView simpleTextView = this.textView2;
            if (LocaleController.isRTL) {
                i = 3;
            }
            addView(simpleTextView, LayoutHelper.createFrame(48, 24.0f, i | 48, LocaleController.isRTL ? 20.0f : 0.0f, 43.0f, LocaleController.isRTL ? 0.0f : 20.0f, 0.0f));
        }
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
            this.textView2.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        this.textView.measure(MeasureSpec.makeMeasureSpec(((i - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(this.deleteImageView != null ? 79.0f : 42.0f), NUM), MeasureSpec.makeMeasureSpec(0, 0));
        i2 = this.textView.getMeasuredHeight();
        setMeasuredDimension(i, Math.max(AndroidUtilities.dp(50.0f), this.textView.getMeasuredHeight()) + this.needDivider);
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.setAlpha(i2 >= AndroidUtilities.dp(52.0f) ? 1.0f : 0.0f);
        }
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

    public void addTextWatcher(TextWatcher textWatcher) {
        this.textView.addTextChangedListener(textWatcher);
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

    public void setText(String str, boolean z) {
        this.textView.setText(str);
        this.needDivider = z;
        setWillNotDraw(z ^ 1);
    }

    public void setTextAndHint(String str, String str2, boolean z) {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.setTag(null);
        }
        this.textView.setText(str);
        this.textView.setHint(str2);
        this.needDivider = z;
        setWillNotDraw(z ^ 1);
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        setEnabled(z);
    }

    public void setText2(String str) {
        this.textView2.setText(str);
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider && drawDivider()) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
