package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.Views.EntityView.SelectionView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Rect;

public class TextPaintView extends EntityView {
    private int baseFontSize;
    private EditTextOutline editText;
    private boolean stroke;
    private Swatch swatch;

    /* renamed from: org.telegram.ui.Components.Paint.Views.TextPaintView$1 */
    class C12231 implements TextWatcher {
        private int beforeCursorPosition = null;
        private String text;

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C12231() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            this.text = charSequence.toString();
            this.beforeCursorPosition = i;
        }

        public void afterTextChanged(Editable editable) {
            TextPaintView.this.editText.removeTextChangedListener(this);
            if (TextPaintView.this.editText.getLineCount() > 9) {
                TextPaintView.this.editText.setText(this.text);
                TextPaintView.this.editText.setSelection(this.beforeCursorPosition);
            }
            TextPaintView.this.editText.addTextChangedListener(this);
        }
    }

    public class TextViewSelectionView extends SelectionView {
        public TextViewSelectionView(Context context) {
            super(context);
        }

        protected int pointInsideHandle(float f, float f2) {
            float dp = (float) AndroidUtilities.dp(19.5f);
            float dp2 = ((float) AndroidUtilities.dp(1.0f)) + dp;
            float f3 = dp2 * 2.0f;
            float width = ((float) getWidth()) - f3;
            float height = ((float) getHeight()) - f3;
            float f4 = (height / 2.0f) + dp2;
            if (f > dp2 - dp && f2 > f4 - dp && f < dp2 + dp && f2 < f4 + dp) {
                return 1;
            }
            f3 = dp2 + width;
            if (f <= f3 - dp || f2 <= f4 - dp || f >= f3 + dp || f2 >= f4 + dp) {
                return (f <= dp2 || f >= width || f2 <= dp2 || f2 >= height) ? 0 : 3;
            } else {
                return 2;
            }
        }

        protected void onDraw(Canvas canvas) {
            TextViewSelectionView textViewSelectionView = this;
            Canvas canvas2 = canvas;
            super.onDraw(canvas);
            float dp = (float) AndroidUtilities.dp(3.0f);
            float dp2 = (float) AndroidUtilities.dp(3.0f);
            float dp3 = (float) AndroidUtilities.dp(1.0f);
            float dp4 = (float) AndroidUtilities.dp(4.5f);
            float dp5 = (dp4 + dp3) + ((float) AndroidUtilities.dp(15.0f));
            float f = dp5 * 2.0f;
            float width = ((float) getWidth()) - f;
            float height = ((float) getHeight()) - f;
            float f2 = dp + dp2;
            int floor = (int) Math.floor((double) (width / f2));
            float ceil = (float) Math.ceil((double) (((width - (((float) floor) * f2)) + dp) / 2.0f));
            int i = 0;
            while (i < floor) {
                float f3 = (ceil + dp5) + (((float) i) * f2);
                float f4 = dp3 / 2.0f;
                float f5 = f3 + dp2;
                float f6 = dp5 + f4;
                f = f3;
                int i2 = i;
                float f7 = f5;
                float f8 = ceil;
                ceil = f6;
                int i3 = floor;
                canvas2.drawRect(f, dp5 - f4, f7, ceil, textViewSelectionView.paint);
                float f9 = dp5 + height;
                canvas2.drawRect(f, f9 - f4, f7, f9 + f4, textViewSelectionView.paint);
                i = i2 + 1;
                floor = i3;
                ceil = f8;
            }
            floor = (int) Math.floor((double) (height / f2));
            dp = (float) Math.ceil((double) (((height - (((float) floor) * f2)) + dp) / 2.0f));
            int i4 = 0;
            while (i4 < floor) {
                float f10 = (dp + dp5) + (((float) i4) * f2);
                f3 = dp3 / 2.0f;
                f4 = f10 + dp2;
                float f11 = f10;
                i3 = i4;
                ceil = f4;
                int i5 = floor;
                canvas2.drawRect(dp5 - f3, f11, dp5 + f3, ceil, textViewSelectionView.paint);
                f9 = dp5 + width;
                canvas2.drawRect(f9 - f3, f11, f9 + f3, ceil, textViewSelectionView.paint);
                i4 = i3 + 1;
                floor = i5;
            }
            height = (height / 2.0f) + dp5;
            canvas2.drawCircle(dp5, height, dp4, textViewSelectionView.dotPaint);
            canvas2.drawCircle(dp5, height, dp4, textViewSelectionView.dotStrokePaint);
            dp5 += width;
            canvas2.drawCircle(dp5, height, dp4, textViewSelectionView.dotPaint);
            canvas2.drawCircle(dp5, height, dp4, textViewSelectionView.dotStrokePaint);
        }
    }

    public TextPaintView(Context context, Point point, int i, String str, Swatch swatch, boolean z) {
        super(context, point);
        this.baseFontSize = i;
        this.editText = new EditTextOutline(context);
        this.editText.setBackgroundColor(0);
        this.editText.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
        this.editText.setClickable(false);
        this.editText.setEnabled(false);
        this.editText.setTextSize(0, (float) this.baseFontSize);
        this.editText.setText(str);
        this.editText.setTextColor(swatch.color);
        this.editText.setTypeface(null, 1);
        this.editText.setGravity(17);
        this.editText.setHorizontallyScrolling(false);
        this.editText.setImeOptions(268435456);
        this.editText.setFocusableInTouchMode(true);
        this.editText.setInputType(this.editText.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS);
        addView(this.editText, LayoutHelper.createFrame(-2, -2, 51));
        if (VERSION.SDK_INT >= 23) {
            this.editText.setBreakStrategy(0);
        }
        setSwatch(swatch);
        setStroke(z);
        updatePosition();
        this.editText.addTextChangedListener(new C12231());
    }

    public TextPaintView(Context context, TextPaintView textPaintView, Point point) {
        this(context, point, textPaintView.baseFontSize, textPaintView.getText(), textPaintView.getSwatch(), textPaintView.stroke);
        setRotation(textPaintView.getRotation());
        setScale(textPaintView.getScale());
    }

    public void setMaxWidth(int i) {
        this.editText.setMaxWidth(i);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updatePosition();
    }

    public String getText() {
        return this.editText.getText().toString();
    }

    public void setText(String str) {
        this.editText.setText(str);
    }

    public View getFocusedView() {
        return this.editText;
    }

    public void beginEditing() {
        this.editText.setEnabled(true);
        this.editText.setClickable(true);
        this.editText.requestFocus();
        this.editText.setSelection(this.editText.getText().length());
    }

    public void endEditing() {
        this.editText.clearFocus();
        this.editText.setEnabled(false);
        this.editText.setClickable(false);
        updateSelectionView();
    }

    public Swatch getSwatch() {
        return this.swatch;
    }

    public void setSwatch(Swatch swatch) {
        this.swatch = swatch;
        updateColor();
    }

    public void setStroke(boolean z) {
        this.stroke = z;
        updateColor();
    }

    private void updateColor() {
        if (this.stroke) {
            this.editText.setTextColor(-1);
            this.editText.setStrokeColor(this.swatch.color);
            this.editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
            return;
        }
        this.editText.setTextColor(this.swatch.color);
        this.editText.setStrokeColor(0);
        this.editText.setShadowLayer(8.0f, 0.0f, 2.0f, -NUM);
    }

    protected Rect getSelectionBounds() {
        float scaleX = ((ViewGroup) getParent()).getScaleX();
        float width = (((float) getWidth()) * getScale()) + (((float) AndroidUtilities.dp(46.0f)) / scaleX);
        float height = (((float) getHeight()) * getScale()) + (((float) AndroidUtilities.dp(20.0f)) / scaleX);
        return new Rect((this.position.f24x - (width / 2.0f)) * scaleX, (this.position.f25y - (height / 2.0f)) * scaleX, width * scaleX, height * scaleX);
    }

    protected TextViewSelectionView createSelectionView() {
        return new TextViewSelectionView(getContext());
    }
}
