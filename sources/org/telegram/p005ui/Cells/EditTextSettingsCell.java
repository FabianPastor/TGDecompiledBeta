package org.telegram.p005ui.Cells;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.LayoutHelper;

/* renamed from: org.telegram.ui.Cells.EditTextSettingsCell */
public class EditTextSettingsCell extends FrameLayout {
    private boolean needDivider;
    private EditTextBoldCursor textView;

    public EditTextSettingsCell(Context context) {
        int i = 3;
        super(context);
        this.textView = new EditTextBoldCursor(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable(null);
        this.textView.setPadding(0, 0, 0, 0);
        this.textView.setInputType(this.textView.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i | 48, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (this.needDivider ? 1 : 0) + AndroidUtilities.m9dp(50.0f));
        this.textView.measure(MeasureSpec.makeMeasureSpec(((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.m9dp(34.0f), NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }

    public EditTextBoldCursor getTextView() {
        return this.textView;
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
        this.textView.setText(text);
        this.textView.setHint(hint);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setEnabled(boolean value, ArrayList<Animator> arrayList) {
        setEnabled(value);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.m9dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
