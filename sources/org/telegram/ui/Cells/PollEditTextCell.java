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

    public PollEditTextCell(Context context, OnClickListener onDelete) {
        int i;
        float f;
        float f2;
        int i2 = 3;
        super(context);
        this.textView = new EditTextBoldCursor(context) {
            public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
                InputConnection conn = super.onCreateInputConnection(outAttrs);
                if (PollEditTextCell.this.showNextButton) {
                    outAttrs.imeOptions &= -NUM;
                }
                return conn;
            }
        };
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable(null);
        this.textView.setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
        this.textView.setImeOptions(this.textView.getImeOptions() | NUM);
        this.textView.setInputType(this.textView.getInputType() | 16384);
        EditTextBoldCursor editTextBoldCursor = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        i |= 16;
        if (!LocaleController.isRTL || onDelete == null) {
            f = 21.0f;
        } else {
            f = 58.0f;
        }
        if (LocaleController.isRTL || onDelete == null) {
            f2 = 21.0f;
        } else {
            f2 = 58.0f;
        }
        addView(editTextBoldCursor, LayoutHelper.createFrame(-1, -2.0f, i, f, 0.0f, f2, 0.0f));
        if (onDelete != null) {
            int i3;
            this.deleteImageView = new ImageView(context);
            this.deleteImageView.setFocusable(false);
            this.deleteImageView.setScaleType(ScaleType.CENTER);
            this.deleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), Mode.MULTIPLY));
            this.deleteImageView.setImageResource(NUM);
            this.deleteImageView.setOnClickListener(onDelete);
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText"), Mode.MULTIPLY));
            this.deleteImageView.setContentDescription(LocaleController.getString("Delete", NUM));
            addView(this.deleteImageView, LayoutHelper.createFrame(48, 50.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 3.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 3.0f, 0.0f));
            this.textView2 = new SimpleTextView(getContext());
            this.textView2.setTextSize(13);
            SimpleTextView simpleTextView = this.textView2;
            if (LocaleController.isRTL) {
                i3 = 3;
            } else {
                i3 = 5;
            }
            simpleTextView.setGravity(i3 | 48);
            SimpleTextView simpleTextView2 = this.textView2;
            if (!LocaleController.isRTL) {
                i2 = 5;
            }
            addView(simpleTextView2, LayoutHelper.createFrame(48, 24.0f, i2 | 48, LocaleController.isRTL ? 20.0f : 0.0f, 43.0f, LocaleController.isRTL ? 0.0f : 20.0f, 0.0f));
        }
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (this.deleteImageView != null) {
            this.deleteImageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
            this.textView2.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        this.textView.measure(MeasureSpec.makeMeasureSpec(((width - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(this.deleteImageView != null ? 79.0f : 42.0f), NUM), MeasureSpec.makeMeasureSpec(0, 0));
        int h = this.textView.getMeasuredHeight();
        int max = Math.max(AndroidUtilities.dp(50.0f), this.textView.getMeasuredHeight());
        if (this.needDivider) {
            i = 1;
        } else {
            i = 0;
        }
        setMeasuredDimension(width, i + max);
        if (this.textView2 != null) {
            this.textView2.setAlpha(h >= AndroidUtilities.dp(52.0f) ? 1.0f : 0.0f);
        }
    }

    public void callOnDelete() {
        if (this.deleteImageView != null) {
            this.deleteImageView.callOnClick();
        }
    }

    public void setShowNextButton(boolean value) {
        this.showNextButton = value;
    }

    public EditTextBoldCursor getTextView() {
        return this.textView;
    }

    public void addTextWatcher(TextWatcher watcher) {
        this.textView.addTextChangedListener(watcher);
    }

    /* Access modifiers changed, original: protected */
    public boolean drawDivider() {
        return true;
    }

    public String getText() {
        return this.textView.getText().toString();
    }

    public int length() {
        return this.textView.length();
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(String text, boolean divider) {
        this.textView.setText(text);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndHint(String text, String hint, boolean divider) {
        if (this.deleteImageView != null) {
            this.deleteImageView.setTag(null);
        }
        this.textView.setText(text);
        this.textView.setHint(hint);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setEnabled(boolean value, ArrayList<Animator> arrayList) {
        setEnabled(value);
    }

    public void setText2(String text) {
        this.textView2.setText(text);
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
