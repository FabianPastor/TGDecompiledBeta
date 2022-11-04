package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
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
/* loaded from: classes3.dex */
public class TextPaintView extends EntityView {
    private int baseFontSize;
    private int currentType;
    private EditTextOutline editText;
    private Swatch swatch;

    public TextPaintView(Context context, Point point, int i, String str, Swatch swatch, int i2) {
        super(context, point);
        this.baseFontSize = i;
        EditTextOutline editTextOutline = new EditTextOutline(context);
        this.editText = editTextOutline;
        editTextOutline.setBackgroundColor(0);
        this.editText.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
        this.editText.setClickable(false);
        this.editText.setEnabled(false);
        this.editText.setCursorColor(-1);
        this.editText.setTextSize(0, this.baseFontSize);
        this.editText.setText(str);
        this.editText.setTextColor(swatch.color);
        this.editText.setTypeface(null, 1);
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
        setSwatch(swatch);
        setType(i2);
        updatePosition();
        this.editText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.Paint.Views.TextPaintView.1
            private int beforeCursorPosition = 0;
            private String text;

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
                this.text = charSequence.toString();
                this.beforeCursorPosition = i3;
            }

            @Override // android.text.TextWatcher
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
        this(context, point, textPaintView.baseFontSize, textPaintView.getText(), textPaintView.getSwatch(), textPaintView.currentType);
        setRotation(textPaintView.getRotation());
        setScale(textPaintView.getScale());
    }

    public void setMaxWidth(int i) {
        this.editText.setMaxWidth(i);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
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
        EditTextOutline editTextOutline = this.editText;
        editTextOutline.setSelection(editTextOutline.getText().length());
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.TextPaintView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TextPaintView.this.lambda$beginEditing$0();
            }
        }, 300L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$beginEditing$0() {
        AndroidUtilities.showKeyboard(this.editText);
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

    public int getTextSize() {
        return (int) this.editText.getTextSize();
    }

    public void setSwatch(Swatch swatch) {
        this.swatch = swatch;
        updateColor();
    }

    public void setType(int i) {
        this.currentType = i;
        updateColor();
    }

    public int getType() {
        return this.currentType;
    }

    private void updateColor() {
        int i = this.currentType;
        if (i == 0) {
            this.editText.setTextColor(-1);
            this.editText.setStrokeColor(this.swatch.color);
            this.editText.setFrameColor(0);
            this.editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        } else if (i == 1) {
            this.editText.setTextColor(this.swatch.color);
            this.editText.setStrokeColor(0);
            this.editText.setFrameColor(0);
            this.editText.setShadowLayer(5.0f, 0.0f, 1.0f, NUM);
        } else if (i != 2) {
        } else {
            this.editText.setTextColor(-16777216);
            this.editText.setStrokeColor(0);
            this.editText.setFrameColor(this.swatch.color);
            this.editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        }
    }

    @Override // org.telegram.ui.Components.Paint.Views.EntityView
    protected Rect getSelectionBounds() {
        float scaleX = ((ViewGroup) getParent()).getScaleX();
        float measuredWidth = ((getMeasuredWidth() - (this.currentType == 2 ? AndroidUtilities.dp(24.0f) : 0)) * getScale()) + (AndroidUtilities.dp(46.0f) / scaleX);
        float measuredHeight = (getMeasuredHeight() * getScale()) + (AndroidUtilities.dp(20.0f) / scaleX);
        Point point = this.position;
        return new Rect((point.x - (measuredWidth / 2.0f)) * scaleX, (point.y - (measuredHeight / 2.0f)) * scaleX, measuredWidth * scaleX, measuredHeight * scaleX);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Components.Paint.Views.EntityView
    /* renamed from: createSelectionView  reason: collision with other method in class */
    public TextViewSelectionView mo1572createSelectionView() {
        return new TextViewSelectionView(this, getContext());
    }

    /* loaded from: classes3.dex */
    public class TextViewSelectionView extends EntityView.SelectionView {
        public TextViewSelectionView(TextPaintView textPaintView, Context context) {
            super(context);
        }

        @Override // org.telegram.ui.Components.Paint.Views.EntityView.SelectionView
        protected int pointInsideHandle(float f, float f2) {
            float dp = AndroidUtilities.dp(19.5f);
            float dp2 = AndroidUtilities.dp(1.0f) + dp;
            float f3 = dp2 * 2.0f;
            float measuredWidth = getMeasuredWidth() - f3;
            float measuredHeight = getMeasuredHeight() - f3;
            float f4 = (measuredHeight / 2.0f) + dp2;
            if (f <= dp2 - dp || f2 <= f4 - dp || f >= dp2 + dp || f2 >= f4 + dp) {
                float f5 = dp2 + measuredWidth;
                if (f > f5 - dp && f2 > f4 - dp && f < f5 + dp && f2 < f4 + dp) {
                    return 2;
                }
                return (f <= dp2 || f >= measuredWidth || f2 <= dp2 || f2 >= measuredHeight) ? 0 : 3;
            }
            return 1;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float dp = AndroidUtilities.dp(3.0f);
            float dp2 = AndroidUtilities.dp(3.0f);
            float dp3 = AndroidUtilities.dp(1.0f);
            float dp4 = AndroidUtilities.dp(4.5f);
            float dp5 = dp4 + dp3 + AndroidUtilities.dp(15.0f);
            float f = dp5 * 2.0f;
            float measuredWidth = getMeasuredWidth() - f;
            float measuredHeight = getMeasuredHeight() - f;
            float f2 = dp + dp2;
            int floor = (int) Math.floor(measuredWidth / f2);
            float ceil = (float) Math.ceil(((measuredWidth - (floor * f2)) + dp) / 2.0f);
            int i = 0;
            while (i < floor) {
                float f3 = ceil + dp5 + (i * f2);
                float f4 = dp3 / 2.0f;
                float f5 = f3 + dp2;
                canvas.drawRect(f3, dp5 - f4, f5, dp5 + f4, this.paint);
                float f6 = dp5 + measuredHeight;
                canvas.drawRect(f3, f6 - f4, f5, f6 + f4, this.paint);
                i++;
                floor = floor;
                ceil = ceil;
            }
            int floor2 = (int) Math.floor(measuredHeight / f2);
            float ceil2 = (float) Math.ceil(((measuredHeight - (floor2 * f2)) + dp) / 2.0f);
            int i2 = 0;
            while (i2 < floor2) {
                float f7 = ceil2 + dp5 + (i2 * f2);
                float f8 = dp3 / 2.0f;
                float f9 = f7 + dp2;
                canvas.drawRect(dp5 - f8, f7, dp5 + f8, f9, this.paint);
                float var_ = dp5 + measuredWidth;
                canvas.drawRect(var_ - f8, f7, var_ + f8, f9, this.paint);
                i2++;
                floor2 = floor2;
            }
            float var_ = (measuredHeight / 2.0f) + dp5;
            canvas.drawCircle(dp5, var_, dp4, this.dotPaint);
            canvas.drawCircle(dp5, var_, dp4, this.dotStrokePaint);
            float var_ = dp5 + measuredWidth;
            canvas.drawCircle(var_, var_, dp4, this.dotPaint);
            canvas.drawCircle(var_, var_, dp4, this.dotStrokePaint);
        }
    }
}
