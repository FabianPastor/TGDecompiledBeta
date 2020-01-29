package org.telegram.ui.Cells;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
    /* access modifiers changed from: private */
    public boolean showNextButton;
    private EditTextBoldCursor textView;
    private SimpleTextView textView2;

    /* access modifiers changed from: protected */
    public boolean drawDivider() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onEditTextDraw(EditTextBoldCursor editTextBoldCursor, Canvas canvas) {
    }

    public PollEditTextCell(Context context, View.OnClickListener onClickListener) {
        super(context);
        this.textView = new EditTextBoldCursor(context) {
            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
                if (PollEditTextCell.this.showNextButton) {
                    editorInfo.imeOptions &= -NUM;
                }
                return onCreateInputConnection;
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                PollEditTextCell.this.onEditTextDraw(this, canvas);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (!isEnabled()) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.textView.setTextSize(1, 16.0f);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable((Drawable) null);
        this.textView.setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
        EditTextBoldCursor editTextBoldCursor = this.textView;
        editTextBoldCursor.setImeOptions(editTextBoldCursor.getImeOptions() | NUM);
        EditTextBoldCursor editTextBoldCursor2 = this.textView;
        editTextBoldCursor2.setInputType(editTextBoldCursor2.getInputType() | 16384);
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, (!LocaleController.isRTL || onClickListener == null) ? 21.0f : 58.0f, 0.0f, (LocaleController.isRTL || onClickListener == null) ? 21.0f : 58.0f, 0.0f));
        if (onClickListener != null) {
            this.deleteImageView = new ImageView(context);
            this.deleteImageView.setFocusable(false);
            this.deleteImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.deleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.deleteImageView.setImageResource(NUM);
            this.deleteImageView.setOnClickListener(onClickListener);
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText"), PorterDuff.Mode.MULTIPLY));
            this.deleteImageView.setContentDescription(LocaleController.getString("Delete", NUM));
            addView(this.deleteImageView, LayoutHelper.createFrame(48, 50.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 3.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 3.0f, 0.0f));
            this.textView2 = new SimpleTextView(getContext());
            this.textView2.setTextSize(13);
            this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            addView(this.textView2, LayoutHelper.createFrame(48, 24.0f, (LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 20.0f : 0.0f, 43.0f, LocaleController.isRTL ? 0.0f : 20.0f, 0.0f));
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(((size - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp((this.textView2 == null || this.textView.getBackground() != null) ? 42.0f : 79.0f), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        int measuredHeight = this.textView.getMeasuredHeight();
        setMeasuredDimension(size, Math.max(AndroidUtilities.dp(50.0f), this.textView.getMeasuredHeight()) + (this.needDivider ? 1 : 0));
        SimpleTextView simpleTextView2 = this.textView2;
        if (simpleTextView2 != null) {
            simpleTextView2.setAlpha(measuredHeight >= AndroidUtilities.dp(52.0f) ? 1.0f : 0.0f);
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

    public void setText(CharSequence charSequence, boolean z) {
        this.textView.setText(charSequence);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setTextAndHint(CharSequence charSequence, String str, boolean z) {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.setTag((Object) null);
        }
        this.textView.setText(charSequence);
        if (!TextUtils.isEmpty(charSequence)) {
            EditTextBoldCursor editTextBoldCursor = this.textView;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
        }
        this.textView.setHint(str);
        this.needDivider = z;
        setWillNotDraw(!z);
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

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider && drawDivider()) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
