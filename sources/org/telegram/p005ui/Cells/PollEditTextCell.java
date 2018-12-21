package org.telegram.p005ui.Cells;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.google.android.exoplayer2.CLASSNAMEC;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.p005ui.ActionBar.SimpleTextView;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.LayoutHelper;

/* renamed from: org.telegram.ui.Cells.PollEditTextCell */
public class PollEditTextCell extends FrameLayout {
    private ImageView deleteImageView;
    private boolean needDivider;
    private EditTextBoldCursor textView;
    private SimpleTextView textView2;

    public PollEditTextCell(Context context, OnClickListener onDelete) {
        int i;
        float f;
        float f2;
        int i2 = 3;
        super(context);
        this.textView = new EditTextBoldCursor(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable(null);
        this.textView.setPadding(0, AndroidUtilities.m9dp(14.0f), 0, AndroidUtilities.m9dp(14.0f));
        this.textView.setImeOptions(this.textView.getImeOptions() | CLASSNAMEC.ENCODING_PCM_MU_LAW);
        this.textView.setInputType(this.textView.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS);
        View view = this.textView;
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
        addView(view, LayoutHelper.createFrame(-1, -2.0f, i, f, 0.0f, f2, 0.0f));
        if (onDelete != null) {
            int i3;
            this.deleteImageView = new ImageView(context);
            this.deleteImageView.setFocusable(false);
            this.deleteImageView.setScaleType(ScaleType.CENTER);
            this.deleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), Mode.MULTIPLY));
            this.deleteImageView.setImageResource(R.drawable.msg_panel_clear);
            this.deleteImageView.setOnClickListener(onDelete);
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText), Mode.MULTIPLY));
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
            View view2 = this.textView2;
            if (!LocaleController.isRTL) {
                i2 = 5;
            }
            addView(view2, LayoutHelper.createFrame(48, 24.0f, i2 | 48, LocaleController.isRTL ? 20.0f : 0.0f, 43.0f, LocaleController.isRTL ? 0.0f : 20.0f, 0.0f));
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (this.deleteImageView != null) {
            this.deleteImageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(48.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(48.0f), NUM));
            this.textView2.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(48.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(24.0f), NUM));
        }
        this.textView.measure(MeasureSpec.makeMeasureSpec(((width - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.m9dp(this.deleteImageView != null ? 79.0f : 42.0f), NUM), MeasureSpec.makeMeasureSpec(0, 0));
        int h = this.textView.getMeasuredHeight();
        int max = Math.max(AndroidUtilities.m9dp(50.0f), this.textView.getMeasuredHeight());
        if (this.needDivider) {
            i = 1;
        } else {
            i = 0;
        }
        setMeasuredDimension(width, i + max);
        if (this.textView2 != null) {
            this.textView2.setAlpha(h >= AndroidUtilities.m9dp(52.0f) ? 1.0f : 0.0f);
        }
    }

    public void callOnDelete() {
        if (this.deleteImageView != null) {
            this.deleteImageView.callOnClick();
        }
    }

    public EditTextBoldCursor getTextView() {
        return this.textView;
    }

    public void addTextWatcher(TextWatcher watcher) {
        this.textView.addTextChangedListener(watcher);
    }

    protected boolean drawDivider() {
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

    protected void onDraw(Canvas canvas) {
        if (this.needDivider && drawDivider()) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.m9dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
