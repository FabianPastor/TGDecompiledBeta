package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

public class LoginOrView extends View {
    private Paint linePaint = new Paint(1);
    private View measureAfter;
    private String string = LocaleController.getString(R.string.LoginOrSingInWithGoogle);
    private Rect textBounds = new Rect();
    private TextPaint textPaint = new TextPaint(1);

    public LoginOrView(Context context) {
        super(context);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        updateColors();
    }

    public void setMeasureAfter(View view) {
        this.measureAfter = view;
    }

    public void updateColors() {
        this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.linePaint.setColor(Theme.getColor("key_sheet_scrollUp"));
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        View view = this.measureAfter;
        if (view != null) {
            i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(view.getMeasuredWidth()), NUM);
        }
        super.onMeasure(i, i2);
        TextPaint textPaint2 = this.textPaint;
        String str = this.string;
        textPaint2.getTextBounds(str, 0, str.length(), this.textBounds);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = this.measureAfter != null ? ((float) ((((getWidth() - this.textBounds.width()) - AndroidUtilities.dp(8.0f)) - this.measureAfter.getPaddingLeft()) - this.measureAfter.getPaddingRight())) / 2.0f : (float) AndroidUtilities.dp(48.0f);
        canvas.drawLine(((((float) (getWidth() - this.textBounds.width())) / 2.0f) - ((float) AndroidUtilities.dp(8.0f))) - width, ((float) getHeight()) / 2.0f, (((float) (getWidth() - this.textBounds.width())) / 2.0f) - ((float) AndroidUtilities.dp(8.0f)), ((float) getHeight()) / 2.0f, this.linePaint);
        canvas.drawLine((((float) (getWidth() + this.textBounds.width())) / 2.0f) + ((float) AndroidUtilities.dp(8.0f)), ((float) getHeight()) / 2.0f, (((float) (getWidth() + this.textBounds.width())) / 2.0f) + ((float) AndroidUtilities.dp(8.0f)) + width, ((float) getHeight()) / 2.0f, this.linePaint);
        canvas.drawText(this.string, ((float) (getWidth() - this.textBounds.width())) / 2.0f, ((float) (getHeight() + this.textBounds.height())) / 2.0f, this.textPaint);
    }
}
