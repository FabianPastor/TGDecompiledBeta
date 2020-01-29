package org.telegram.ui.Cells;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class EditTextSettingsCell extends FrameLayout {
    private boolean needDivider;
    private EditTextBoldCursor textView;

    public EditTextSettingsCell(Context context) {
        super(context);
        this.textView = new EditTextBoldCursor(context);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable((Drawable) null);
        this.textView.setPadding(0, 0, 0, 0);
        EditTextBoldCursor editTextBoldCursor = this.textView;
        editTextBoldCursor.setInputType(editTextBoldCursor.getInputType() | 16384);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (!LocaleController.isRTL ? 3 : i) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(42.0f), NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
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
        setWillNotDraw(!z);
    }

    public void setTextAndHint(String str, String str2, boolean z) {
        this.textView.setText(str);
        this.textView.setHint(str2);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        setEnabled(z);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
