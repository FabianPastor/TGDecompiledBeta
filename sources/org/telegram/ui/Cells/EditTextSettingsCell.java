package org.telegram.ui.Cells;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class EditTextSettingsCell extends FrameLayout {
    private boolean needDivider;
    private EditText textView;

    public EditTextSettingsCell(Context context) {
        super(context);
        this.textView = new EditText(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable(null);
        this.textView.setPadding(0, 0, 0, 0);
        this.textView.setInputType(this.textView.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS);
        context = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(context, LayoutHelper.createFrame(-1, -1.0f, i | 48, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(NUM) + this.needDivider);
        i = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(NUM);
        i2 = i / 2;
        this.textView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }

    public TextView getTextView() {
        return this.textView;
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
        this.textView.setText(str);
        this.textView.setHint(str2);
        this.needDivider = z;
        setWillNotDraw(z ^ 1);
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        setEnabled(z);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
