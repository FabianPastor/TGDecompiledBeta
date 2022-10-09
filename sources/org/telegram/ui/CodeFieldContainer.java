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
/* loaded from: classes3.dex */
public class CodeFieldContainer extends LinearLayout {
    Paint bitmapPaint;
    public CodeNumberField[] codeField;
    public boolean ignoreOnTextChange;
    public boolean isFocusSuppressed;
    Paint paint;
    float strokeWidth;

    protected void processNextPressed() {
    }

    public CodeFieldContainer(Context context) {
        super(context);
        this.paint = new Paint(1);
        this.bitmapPaint = new Paint(1);
        this.paint.setStyle(Paint.Style.STROKE);
        setOrientation(0);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        Paint paint = this.paint;
        float dp = AndroidUtilities.dp(1.5f);
        this.strokeWidth = dp;
        paint.setStrokeWidth(dp);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof CodeNumberField) {
                CodeNumberField codeNumberField = (CodeNumberField) childAt;
                if (!this.isFocusSuppressed) {
                    if (childAt.isFocused()) {
                        codeNumberField.animateFocusedProgress(1.0f);
                    } else if (!childAt.isFocused()) {
                        codeNumberField.animateFocusedProgress(0.0f);
                    }
                }
                float successProgress = codeNumberField.getSuccessProgress();
                this.paint.setColor(ColorUtils.blendARGB(ColorUtils.blendARGB(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), codeNumberField.getFocusedProgress()), Theme.getColor("dialogTextRed"), codeNumberField.getErrorProgress()), Theme.getColor("checkbox"), successProgress));
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom());
                float f = this.strokeWidth;
                rectF.inset(f, f);
                if (successProgress != 0.0f) {
                    float f2 = -Math.max(0.0f, this.strokeWidth * (codeNumberField.getSuccessScaleProgress() - 1.0f));
                    rectF.inset(f2, f2);
                }
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.paint);
            }
        }
        super.dispatchDraw(canvas);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (view instanceof CodeNumberField) {
            CodeNumberField codeNumberField = (CodeNumberField) view;
            canvas.save();
            float f = codeNumberField.enterAnimation;
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(view.getX(), view.getY(), view.getX() + view.getMeasuredWidth(), view.getY() + view.getMeasuredHeight());
            float f2 = this.strokeWidth;
            rectF.inset(f2, f2);
            canvas.clipRect(rectF);
            if (codeNumberField.replaceAnimation) {
                float f3 = (f * 0.5f) + 0.5f;
                view.setAlpha(f);
                canvas.scale(f3, f3, codeNumberField.getX() + (codeNumberField.getMeasuredWidth() / 2.0f), codeNumberField.getY() + (codeNumberField.getMeasuredHeight() / 2.0f));
            } else {
                view.setAlpha(1.0f);
                canvas.translate(0.0f, view.getMeasuredHeight() * (1.0f - f));
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
            canvas.scale(f6, f6, codeNumberField.getX() + (codeNumberField.getMeasuredWidth() / 2.0f), codeNumberField.getY() + (codeNumberField.getMeasuredHeight() / 2.0f));
            this.bitmapPaint.setAlpha((int) (f5 * 255.0f));
            canvas.drawBitmap(codeNumberField.exitBitmap, codeNumberField.getX(), codeNumberField.getY(), this.bitmapPaint);
            canvas.restore();
            return true;
        }
        return super.drawChild(canvas, view, j);
    }

    public void setNumbersCount(final int i, int i2) {
        int i3;
        int i4;
        CodeNumberField[] codeNumberFieldArr = this.codeField;
        int i5 = 0;
        if (codeNumberFieldArr == null || codeNumberFieldArr.length != i) {
            this.codeField = new CodeNumberField[i];
            final int i6 = 0;
            while (i6 < i) {
                this.codeField[i6] = new CodeNumberField(getContext()) { // from class: org.telegram.ui.CodeFieldContainer.1
                    @Override // android.view.View
                    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                        int i7;
                        int i8 = 0;
                        if (keyEvent.getKeyCode() == 4) {
                            return false;
                        }
                        int keyCode = keyEvent.getKeyCode();
                        if (keyEvent.getAction() == 1) {
                            if (keyCode == 67 && CodeFieldContainer.this.codeField[i6].length() == 1) {
                                CodeFieldContainer.this.codeField[i6].startExitAnimation();
                                CodeFieldContainer.this.codeField[i6].setText("");
                                return true;
                            } else if (keyCode != 67 || CodeFieldContainer.this.codeField[i6].length() != 0 || (i7 = i6) <= 0) {
                                if (keyCode >= 7 && keyCode <= 16) {
                                    String num = Integer.toString(keyCode - 7);
                                    if (CodeFieldContainer.this.codeField[i6].getText() != null && num.equals(CodeFieldContainer.this.codeField[i6].getText().toString())) {
                                        int i9 = i6;
                                        if (i9 >= i - 1) {
                                            CodeFieldContainer.this.processNextPressed();
                                        } else {
                                            CodeFieldContainer.this.codeField[i9 + 1].requestFocus();
                                        }
                                        return true;
                                    }
                                    if (CodeFieldContainer.this.codeField[i6].length() > 0) {
                                        CodeFieldContainer.this.codeField[i6].startExitAnimation();
                                    }
                                    CodeFieldContainer.this.codeField[i6].setText(num);
                                }
                                return true;
                            } else {
                                CodeNumberField[] codeNumberFieldArr2 = CodeFieldContainer.this.codeField;
                                codeNumberFieldArr2[i7 - 1].setSelection(codeNumberFieldArr2[i7 - 1].length());
                                while (true) {
                                    int i10 = i6;
                                    if (i8 >= i10) {
                                        CodeFieldContainer.this.codeField[i10 - 1].startExitAnimation();
                                        CodeFieldContainer.this.codeField[i6 - 1].setText("");
                                        return true;
                                    }
                                    if (i8 == i10 - 1) {
                                        CodeFieldContainer.this.codeField[i10 - 1].requestFocus();
                                    } else {
                                        CodeFieldContainer.this.codeField[i8].clearFocus();
                                    }
                                    i8++;
                                }
                            }
                        } else {
                            return isFocused();
                        }
                    }
                };
                this.codeField[i6].setImeOptions(NUM);
                this.codeField[i6].setTextSize(1, 20.0f);
                this.codeField[i6].setMaxLines(1);
                this.codeField[i6].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.codeField[i6].setPadding(0, 0, 0, 0);
                this.codeField[i6].setGravity(17);
                if (i2 == 3) {
                    this.codeField[i6].setEnabled(false);
                    this.codeField[i6].setInputType(0);
                    this.codeField[i6].setVisibility(8);
                } else {
                    this.codeField[i6].setInputType(3);
                }
                int i7 = 10;
                if (i2 == 10) {
                    i3 = 42;
                    i4 = 47;
                } else if (i2 == 11) {
                    i7 = 5;
                    i3 = 28;
                    i4 = 34;
                } else {
                    i7 = 7;
                    i3 = 34;
                    i4 = 42;
                }
                addView(this.codeField[i6], LayoutHelper.createLinear(i3, i4, 1, 0, 0, i6 != i + (-1) ? i7 : 0, 0));
                this.codeField[i6].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.CodeFieldContainer.2
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence charSequence, int i8, int i9, int i10) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence charSequence, int i8, int i9, int i10) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable editable) {
                        int length;
                        if (!CodeFieldContainer.this.ignoreOnTextChange && (length = editable.length()) >= 1) {
                            int i8 = i6;
                            if (length > 1) {
                                String obj = editable.toString();
                                CodeFieldContainer.this.ignoreOnTextChange = true;
                                for (int i9 = 0; i9 < Math.min(i - i6, length); i9++) {
                                    if (i9 == 0) {
                                        editable.replace(0, length, obj.substring(i9, i9 + 1));
                                    } else {
                                        i8++;
                                        CodeFieldContainer.this.codeField[i6 + i9].setText(obj.substring(i9, i9 + 1));
                                    }
                                }
                                CodeFieldContainer.this.ignoreOnTextChange = false;
                            }
                            if (i8 != i - 1) {
                                CodeNumberField[] codeNumberFieldArr2 = CodeFieldContainer.this.codeField;
                                int i10 = i8 + 1;
                                codeNumberFieldArr2[i10].setSelection(codeNumberFieldArr2[i10].length());
                                CodeFieldContainer.this.codeField[i10].requestFocus();
                            }
                            int i11 = i;
                            if ((i8 != i11 - 1 && (i8 != i11 - 2 || length < 2)) || CodeFieldContainer.this.getCode().length() != i) {
                                return;
                            }
                            CodeFieldContainer.this.processNextPressed();
                        }
                    }
                });
                this.codeField[i6].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.CodeFieldContainer$$ExternalSyntheticLambda0
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView, int i8, KeyEvent keyEvent) {
                        boolean lambda$setNumbersCount$0;
                        lambda$setNumbersCount$0 = CodeFieldContainer.this.lambda$setNumbersCount$0(textView, i8, keyEvent);
                        return lambda$setNumbersCount$0;
                    }
                });
                i6++;
            }
            return;
        }
        while (true) {
            CodeNumberField[] codeNumberFieldArr2 = this.codeField;
            if (i5 >= codeNumberFieldArr2.length) {
                return;
            }
            codeNumberFieldArr2[i5].setText("");
            i5++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setNumbersCount$0(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            processNextPressed();
            return true;
        }
        return false;
    }

    public String getCode() {
        if (this.codeField == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            CodeNumberField[] codeNumberFieldArr = this.codeField;
            if (i < codeNumberFieldArr.length) {
                sb.append(PhoneFormat.stripExceptNumbers(codeNumberFieldArr[i].getText().toString()));
                i++;
            } else {
                return sb.toString();
            }
        }
    }

    public void setCode(String str) {
        this.codeField[0].setText(str);
    }

    public void setText(String str) {
        setText(str, false);
    }

    public void setText(String str, boolean z) {
        int i = 0;
        if (z) {
            int i2 = 0;
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeField;
                if (i2 >= codeNumberFieldArr.length) {
                    break;
                } else if (codeNumberFieldArr[i2].isFocused()) {
                    i = i2;
                    break;
                } else {
                    i2++;
                }
            }
        }
        for (int i3 = i; i3 < Math.min(this.codeField.length, str.length() + i); i3++) {
            this.codeField[i3].setText(Character.toString(str.charAt(i3 - i)));
        }
    }
}
