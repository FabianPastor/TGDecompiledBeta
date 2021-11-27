package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class CodeFieldContainer extends LinearLayout {
    Paint bitmapPaint = new Paint(1);
    public CodeNumberField[] codeField;
    public boolean ignoreOnTextChange;
    Paint paint = new Paint(1);
    float strokeWidth;

    /* access modifiers changed from: protected */
    public void processNextPressed() {
    }

    public CodeFieldContainer(Context context) {
        super(context);
        this.paint.setStyle(Paint.Style.STROKE);
        setOrientation(0);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        Paint paint2 = this.paint;
        float dp = (float) AndroidUtilities.dp(1.5f);
        this.strokeWidth = dp;
        paint2.setStrokeWidth(dp);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof CodeNumberField) {
                CodeNumberField codeNumberField = (CodeNumberField) childAt;
                if (childAt.isFocused()) {
                    float f = codeNumberField.focusedProgress;
                    if (f != 1.0f) {
                        float f2 = f + 0.10666667f;
                        codeNumberField.focusedProgress = f2;
                        if (f2 > 1.0f) {
                            codeNumberField.focusedProgress = 1.0f;
                        } else {
                            invalidate();
                        }
                        this.paint.setColor(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), codeNumberField.focusedProgress));
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set((float) childAt.getLeft(), (float) childAt.getTop(), (float) childAt.getRight(), (float) childAt.getBottom());
                        float f3 = this.strokeWidth;
                        rectF.inset(f3, f3);
                        canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                    }
                }
                if (!childAt.isFocused()) {
                    float f4 = codeNumberField.focusedProgress;
                    if (f4 != 0.0f) {
                        float f5 = f4 - 0.10666667f;
                        codeNumberField.focusedProgress = f5;
                        if (f5 < 0.0f) {
                            codeNumberField.focusedProgress = 0.0f;
                        } else {
                            invalidate();
                        }
                    }
                }
                this.paint.setColor(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), codeNumberField.focusedProgress));
                RectF rectF2 = AndroidUtilities.rectTmp;
                rectF2.set((float) childAt.getLeft(), (float) childAt.getTop(), (float) childAt.getRight(), (float) childAt.getBottom());
                float var_ = this.strokeWidth;
                rectF2.inset(var_, var_);
                canvas.drawRoundRect(rectF2, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
            }
        }
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        if (!(view instanceof CodeNumberField)) {
            return super.drawChild(canvas, view, j);
        }
        CodeNumberField codeNumberField = (CodeNumberField) view;
        canvas.save();
        float f = codeNumberField.enterAnimation;
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(view.getX(), view.getY(), view.getX() + ((float) view.getMeasuredWidth()), view.getY() + ((float) view.getMeasuredHeight()));
        float f2 = this.strokeWidth;
        rectF.inset(f2, f2);
        canvas.clipRect(rectF);
        if (codeNumberField.replaceAnimation) {
            float f3 = (f * 0.5f) + 0.5f;
            view.setAlpha(f);
            canvas.scale(f3, f3, codeNumberField.getX() + (((float) codeNumberField.getMeasuredWidth()) / 2.0f), codeNumberField.getY() + (((float) codeNumberField.getMeasuredHeight()) / 2.0f));
        } else {
            view.setAlpha(1.0f);
            canvas.translate(0.0f, ((float) view.getMeasuredHeight()) * (1.0f - f));
        }
        super.drawChild(canvas, view, j);
        canvas.restore();
        float f4 = codeNumberField.exitAnimation;
        if (f4 >= 1.0f) {
            return true;
        }
        canvas.save();
        float f5 = 1.0f - f4;
        float f6 = (f5 * 0.5f) + 0.5f;
        canvas.scale(f6, f6, codeNumberField.getX() + (((float) codeNumberField.getMeasuredWidth()) / 2.0f), codeNumberField.getY() + (((float) codeNumberField.getMeasuredHeight()) / 2.0f));
        this.bitmapPaint.setAlpha((int) (f5 * 255.0f));
        canvas.drawBitmap(codeNumberField.exitBitmap, codeNumberField.getX(), codeNumberField.getY(), this.bitmapPaint);
        canvas.restore();
        return true;
    }

    public void setNumbersCount(final int i, int i2) {
        int i3;
        int i4;
        int i5;
        CodeNumberField[] codeNumberFieldArr = this.codeField;
        int i6 = 0;
        if (codeNumberFieldArr == null || codeNumberFieldArr.length != i) {
            this.codeField = new CodeNumberField[i];
            final int i7 = 0;
            while (i7 < i) {
                this.codeField[i7] = new CodeNumberField(getContext()) {
                    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                        int i;
                        int i2 = 0;
                        if (keyEvent.getKeyCode() == 4) {
                            return false;
                        }
                        int keyCode = keyEvent.getKeyCode();
                        if (keyEvent.getAction() != 1) {
                            return isFocused();
                        }
                        if (keyCode == 67 && CodeFieldContainer.this.codeField[i7].length() == 1) {
                            CodeFieldContainer.this.codeField[i7].startExitAnimation();
                            CodeFieldContainer.this.codeField[i7].setText("");
                            return true;
                        } else if (keyCode == 67 && CodeFieldContainer.this.codeField[i7].length() == 0 && (i = i7) > 0) {
                            CodeNumberField[] codeNumberFieldArr = CodeFieldContainer.this.codeField;
                            codeNumberFieldArr[i - 1].setSelection(codeNumberFieldArr[i - 1].length());
                            while (true) {
                                int i3 = i7;
                                if (i2 < i3) {
                                    if (i2 == i3 - 1) {
                                        CodeFieldContainer.this.codeField[i3 - 1].requestFocus();
                                    } else {
                                        CodeFieldContainer.this.codeField[i2].clearFocus();
                                    }
                                    i2++;
                                } else {
                                    CodeFieldContainer.this.codeField[i3 - 1].startExitAnimation();
                                    CodeFieldContainer.this.codeField[i7 - 1].setText("");
                                    return true;
                                }
                            }
                        } else {
                            if (keyCode >= 7 && keyCode <= 16) {
                                String num = Integer.toString(keyCode - 7);
                                if (CodeFieldContainer.this.codeField[i7].getText() == null || !num.equals(CodeFieldContainer.this.codeField[i7].getText().toString())) {
                                    if (CodeFieldContainer.this.codeField[i7].length() > 0) {
                                        CodeFieldContainer.this.codeField[i7].startExitAnimation();
                                    }
                                    CodeFieldContainer.this.codeField[i7].setText(num);
                                } else {
                                    int i4 = i7;
                                    if (i4 >= i) {
                                        CodeFieldContainer.this.processNextPressed();
                                    } else {
                                        CodeFieldContainer.this.codeField[i4 + 1].requestFocus();
                                    }
                                    return true;
                                }
                            }
                            return true;
                        }
                    }
                };
                this.codeField[i7].setImeOptions(NUM);
                this.codeField[i7].setTextSize(1, 20.0f);
                this.codeField[i7].setMaxLines(1);
                this.codeField[i7].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.codeField[i7].setPadding(0, 0, 0, 0);
                this.codeField[i7].setGravity(17);
                if (i2 == 3) {
                    this.codeField[i7].setEnabled(false);
                    this.codeField[i7].setInputType(0);
                    this.codeField[i7].setVisibility(8);
                } else {
                    this.codeField[i7].setInputType(3);
                }
                if (i2 == 11) {
                    i5 = 5;
                    i4 = 28;
                    i3 = 34;
                } else {
                    i5 = 7;
                    i4 = 34;
                    i3 = 42;
                }
                addView(this.codeField[i7], LayoutHelper.createLinear(i4, i3, 1, 0, 0, i7 != i + -1 ? i5 : 0, 0));
                this.codeField[i7].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        int length;
                        if (!CodeFieldContainer.this.ignoreOnTextChange && (length = editable.length()) >= 1) {
                            if (length > 1) {
                                String obj = editable.toString();
                                CodeFieldContainer.this.ignoreOnTextChange = true;
                                for (int i = 0; i < Math.min(i - i7, length); i++) {
                                    if (i == 0) {
                                        editable.replace(0, length, obj.substring(i, i + 1));
                                    }
                                }
                                CodeFieldContainer.this.ignoreOnTextChange = false;
                            }
                            int i2 = i7;
                            if (i2 != i - 1) {
                                CodeNumberField[] codeNumberFieldArr = CodeFieldContainer.this.codeField;
                                codeNumberFieldArr[i2 + 1].setSelection(codeNumberFieldArr[i2 + 1].length());
                                CodeFieldContainer.this.codeField[i7 + 1].requestFocus();
                            }
                            int i3 = i7;
                            int i4 = i;
                            if ((i3 == i4 - 1 || (i3 == i4 - 2 && length >= 2)) && CodeFieldContainer.this.getCode().length() == i) {
                                CodeFieldContainer.this.processNextPressed();
                            }
                        }
                    }
                });
                this.codeField[i7].setOnEditorActionListener(new CodeFieldContainer$$ExternalSyntheticLambda0(this));
                i7++;
            }
            return;
        }
        while (true) {
            CodeNumberField[] codeNumberFieldArr2 = this.codeField;
            if (i6 < codeNumberFieldArr2.length) {
                codeNumberFieldArr2[i6].setText("");
                i6++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setNumbersCount$0(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        processNextPressed();
        return true;
    }

    public String getCode() {
        if (this.codeField == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            CodeNumberField[] codeNumberFieldArr = this.codeField;
            if (i >= codeNumberFieldArr.length) {
                return sb.toString();
            }
            sb.append(PhoneFormat.stripExceptNumbers(codeNumberFieldArr[i].getText().toString()));
            i++;
        }
    }

    public void setCode(String str) {
        this.codeField[0].setText(str);
    }
}
