package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Rect;

public class TextPaintView extends EntityView {
    private int baseFontSize;
    /* access modifiers changed from: private */
    public EditTextOutline editText;
    private boolean stroke;
    private Swatch swatch;

    public TextPaintView(Context context, Point point, int i, String str, Swatch swatch2, boolean z) {
        super(context, point);
        this.baseFontSize = i;
        EditTextOutline editTextOutline = new EditTextOutline(context);
        this.editText = editTextOutline;
        editTextOutline.setBackgroundColor(0);
        this.editText.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
        this.editText.setClickable(false);
        this.editText.setEnabled(false);
        this.editText.setTextSize(0, (float) this.baseFontSize);
        this.editText.setText(str);
        this.editText.setTextColor(swatch2.color);
        this.editText.setTypeface((Typeface) null, 1);
        this.editText.setGravity(17);
        this.editText.setHorizontallyScrolling(false);
        this.editText.setImeOptions(NUM);
        this.editText.setFocusableInTouchMode(true);
        EditTextOutline editTextOutline2 = this.editText;
        editTextOutline2.setInputType(editTextOutline2.getInputType() | 16384);
        addView(this.editText, LayoutHelper.createFrame(-2, -2, 51));
        if (Build.VERSION.SDK_INT >= 23) {
            this.editText.setBreakStrategy(0);
        }
        setSwatch(swatch2);
        setStroke(z);
        updatePosition();
        this.editText.addTextChangedListener(new TextWatcher() {
            private int beforeCursorPosition = 0;
            private String text;

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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
        });
    }

    public TextPaintView(Context context, TextPaintView textPaintView, Point point) {
        this(context, point, textPaintView.baseFontSize, textPaintView.getText(), textPaintView.getSwatch(), textPaintView.stroke);
        setRotation(textPaintView.getRotation());
        setScale(textPaintView.getScale());
    }

    public void setMaxWidth(int i) {
        this.editText.setMaxWidth(i);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
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
        EditTextOutline editTextOutline = this.editText;
        editTextOutline.setSelection(editTextOutline.getText().length());
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

    public void setSwatch(Swatch swatch2) {
        this.swatch = swatch2;
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

    /* access modifiers changed from: protected */
    public Rect getSelectionBounds() {
        float scaleX = ((ViewGroup) getParent()).getScaleX();
        float width = (((float) getWidth()) * getScale()) + (((float) AndroidUtilities.dp(46.0f)) / scaleX);
        float height = (((float) getHeight()) * getScale()) + (((float) AndroidUtilities.dp(20.0f)) / scaleX);
        Point point = this.position;
        return new Rect((point.x - (width / 2.0f)) * scaleX, (point.y - (height / 2.0f)) * scaleX, width * scaleX, height * scaleX);
    }

    /* access modifiers changed from: protected */
    public TextViewSelectionView createSelectionView() {
        return new TextViewSelectionView(this, getContext());
    }

    public class TextViewSelectionView extends EntityView.SelectionView {
        public TextViewSelectionView(TextPaintView textPaintView, Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public int pointInsideHandle(float f, float f2) {
            float dp = (float) AndroidUtilities.dp(19.5f);
            float dp2 = ((float) AndroidUtilities.dp(1.0f)) + dp;
            float f3 = dp2 * 2.0f;
            float width = ((float) getWidth()) - f3;
            float height = ((float) getHeight()) - f3;
            float f4 = (height / 2.0f) + dp2;
            if (f > dp2 - dp && f2 > f4 - dp && f < dp2 + dp && f2 < f4 + dp) {
                return 1;
            }
            float f5 = dp2 + width;
            if (f <= f5 - dp || f2 <= f4 - dp || f >= f5 + dp || f2 >= f4 + dp) {
                return (f <= dp2 || f >= width || f2 <= dp2 || f2 >= height) ? 0 : 3;
            }
            return 2;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            super.onDraw(canvas);
            float dp = (float) AndroidUtilities.dp(3.0f);
            float dp2 = (float) AndroidUtilities.dp(3.0f);
            float dp3 = (float) AndroidUtilities.dp(1.0f);
            float dp4 = (float) AndroidUtilities.dp(4.5f);
            float dp5 = dp4 + dp3 + ((float) AndroidUtilities.dp(15.0f));
            float f = dp5 * 2.0f;
            float width = ((float) getWidth()) - f;
            float height = ((float) getHeight()) - f;
            float f2 = dp + dp2;
            int floor = (int) Math.floor((double) (width / f2));
            float ceil = (float) Math.ceil((double) (((width - (((float) floor) * f2)) + dp) / 2.0f));
            int i = 0;
            while (i < floor) {
                float f3 = ceil + dp5 + (((float) i) * f2);
                float f4 = dp3 / 2.0f;
                float f5 = f3;
                int i2 = i;
                float f6 = f3 + dp2;
                float f7 = ceil;
                float f8 = dp5 + f4;
                canvas.drawRect(f5, dp5 - f4, f6, f8, this.paint);
                float f9 = dp5 + height;
                canvas.drawRect(f5, f9 - f4, f6, f9 + f4, this.paint);
                i = i2 + 1;
                floor = floor;
                ceil = f7;
            }
            int floor2 = (int) Math.floor((double) (height / f2));
            float ceil2 = (float) Math.ceil((double) (((height - (((float) floor2) * f2)) + dp) / 2.0f));
            int i3 = 0;
            while (i3 < floor2) {
                float var_ = ceil2 + dp5 + (((float) i3) * f2);
                float var_ = dp3 / 2.0f;
                float var_ = var_;
                int i4 = i3;
                float var_ = var_ + dp2;
                canvas.drawRect(dp5 - var_, var_, dp5 + var_, var_, this.paint);
                float var_ = dp5 + width;
                canvas.drawRect(var_ - var_, var_, var_ + var_, var_, this.paint);
                i3 = i4 + 1;
                floor2 = floor2;
            }
            float var_ = (height / 2.0f) + dp5;
            canvas2.drawCircle(dp5, var_, dp4, this.dotPaint);
            canvas2.drawCircle(dp5, var_, dp4, this.dotStrokePaint);
            float var_ = dp5 + width;
            canvas2.drawCircle(var_, var_, dp4, this.dotPaint);
            canvas2.drawCircle(var_, var_, dp4, this.dotStrokePaint);
        }
    }
}
