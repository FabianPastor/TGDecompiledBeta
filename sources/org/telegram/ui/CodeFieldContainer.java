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
    public static final int TYPE_PASSCODE = 10;
    Paint bitmapPaint = new Paint(1);
    public CodeNumberField[] codeField;
    public boolean ignoreOnTextChange;
    public boolean isFocusSuppressed;
    Paint paint = new Paint(1);
    float strokeWidth;

    public CodeFieldContainer(Context context) {
        super(context);
        this.paint.setStyle(Paint.Style.STROKE);
        setOrientation(0);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Paint paint2 = this.paint;
        float dp = (float) AndroidUtilities.dp(1.5f);
        this.strokeWidth = dp;
        paint2.setStrokeWidth(dp);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof CodeNumberField) {
                CodeNumberField codeField2 = (CodeNumberField) child;
                if (!this.isFocusSuppressed) {
                    if (child.isFocused()) {
                        codeField2.animateFocusedProgress(1.0f);
                    } else if (!child.isFocused()) {
                        codeField2.animateFocusedProgress(0.0f);
                    }
                }
                float successProgress = codeField2.getSuccessProgress();
                this.paint.setColor(ColorUtils.blendARGB(ColorUtils.blendARGB(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), codeField2.getFocusedProgress()), Theme.getColor("dialogTextRed"), codeField2.getErrorProgress()), Theme.getColor("checkbox"), successProgress));
                AndroidUtilities.rectTmp.set((float) child.getLeft(), (float) child.getTop(), (float) child.getRight(), (float) child.getBottom());
                RectF rectF = AndroidUtilities.rectTmp;
                float f = this.strokeWidth;
                rectF.inset(f, f);
                if (successProgress != 0.0f) {
                    float offset = -Math.max(0.0f, this.strokeWidth * (codeField2.getSuccessScaleProgress() - 1.0f));
                    AndroidUtilities.rectTmp.inset(offset, offset);
                }
                canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
            }
        }
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (!(child instanceof CodeNumberField)) {
            return super.drawChild(canvas, child, drawingTime);
        }
        CodeNumberField field = (CodeNumberField) child;
        canvas.save();
        float progress = ((CodeNumberField) child).enterAnimation;
        AndroidUtilities.rectTmp.set(child.getX(), child.getY(), child.getX() + ((float) child.getMeasuredWidth()), child.getY() + ((float) child.getMeasuredHeight()));
        RectF rectF = AndroidUtilities.rectTmp;
        float f = this.strokeWidth;
        rectF.inset(f, f);
        canvas.clipRect(AndroidUtilities.rectTmp);
        if (field.replaceAnimation) {
            float s = (progress * 0.5f) + 0.5f;
            child.setAlpha(progress);
            canvas.scale(s, s, field.getX() + (((float) field.getMeasuredWidth()) / 2.0f), field.getY() + (((float) field.getMeasuredHeight()) / 2.0f));
        } else {
            child.setAlpha(1.0f);
            canvas.translate(0.0f, ((float) child.getMeasuredHeight()) * (1.0f - progress));
        }
        super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        float exitProgress = field.exitAnimation;
        if (exitProgress >= 1.0f) {
            return true;
        }
        canvas.save();
        float s2 = ((1.0f - exitProgress) * 0.5f) + 0.5f;
        canvas.scale(s2, s2, field.getX() + (((float) field.getMeasuredWidth()) / 2.0f), field.getY() + (((float) field.getMeasuredHeight()) / 2.0f));
        this.bitmapPaint.setAlpha((int) ((1.0f - exitProgress) * 255.0f));
        canvas.drawBitmap(field.exitBitmap, field.getX(), field.getY(), this.bitmapPaint);
        canvas.restore();
        return true;
    }

    public void setNumbersCount(int length, int currentType) {
        int gapSize;
        int height;
        int width;
        final int i = length;
        int i2 = currentType;
        CodeNumberField[] codeNumberFieldArr = this.codeField;
        if (codeNumberFieldArr == null || codeNumberFieldArr.length != i) {
            this.codeField = new CodeNumberField[i];
            int a = 0;
            while (a < i) {
                final int num = a;
                this.codeField[a] = new CodeNumberField(getContext()) {
                    public boolean dispatchKeyEvent(KeyEvent event) {
                        if (event.getKeyCode() == 4) {
                            return false;
                        }
                        int keyCode = event.getKeyCode();
                        if (event.getAction() != 1) {
                            return isFocused();
                        }
                        if (keyCode == 67 && CodeFieldContainer.this.codeField[num].length() == 1) {
                            CodeFieldContainer.this.codeField[num].startExitAnimation();
                            CodeFieldContainer.this.codeField[num].setText("");
                            return true;
                        } else if (keyCode == 67 && CodeFieldContainer.this.codeField[num].length() == 0 && num > 0) {
                            CodeFieldContainer.this.codeField[num - 1].setSelection(CodeFieldContainer.this.codeField[num - 1].length());
                            int i = 0;
                            while (true) {
                                int i2 = num;
                                if (i < i2) {
                                    if (i == i2 - 1) {
                                        CodeFieldContainer.this.codeField[num - 1].requestFocus();
                                    } else {
                                        CodeFieldContainer.this.codeField[i].clearFocus();
                                    }
                                    i++;
                                } else {
                                    CodeFieldContainer.this.codeField[num - 1].startExitAnimation();
                                    CodeFieldContainer.this.codeField[num - 1].setText("");
                                    return true;
                                }
                            }
                        } else {
                            if (keyCode >= 7 && keyCode <= 16) {
                                String str = Integer.toString(keyCode - 7);
                                if (CodeFieldContainer.this.codeField[num].getText() == null || !str.equals(CodeFieldContainer.this.codeField[num].getText().toString())) {
                                    if (CodeFieldContainer.this.codeField[num].length() > 0) {
                                        CodeFieldContainer.this.codeField[num].startExitAnimation();
                                    }
                                    CodeFieldContainer.this.codeField[num].setText(str);
                                } else {
                                    if (num >= i - 1) {
                                        CodeFieldContainer.this.processNextPressed();
                                    } else {
                                        CodeFieldContainer.this.codeField[num + 1].requestFocus();
                                    }
                                    return true;
                                }
                            }
                            return true;
                        }
                    }
                };
                this.codeField[a].setImeOptions(NUM);
                this.codeField[a].setTextSize(1, 20.0f);
                this.codeField[a].setMaxLines(1);
                this.codeField[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.codeField[a].setPadding(0, 0, 0, 0);
                this.codeField[a].setGravity(17);
                if (i2 == 3) {
                    this.codeField[a].setEnabled(false);
                    this.codeField[a].setInputType(0);
                    this.codeField[a].setVisibility(8);
                } else {
                    this.codeField[a].setInputType(3);
                }
                if (i2 == 10) {
                    width = 42;
                    height = 47;
                    gapSize = 10;
                } else if (i2 == 11) {
                    width = 28;
                    height = 34;
                    gapSize = 5;
                } else {
                    width = 34;
                    height = 42;
                    gapSize = 7;
                }
                addView(this.codeField[a], LayoutHelper.createLinear(width, height, 1, 0, 0, a != i + -1 ? gapSize : 0, 0));
                this.codeField[a].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        int len;
                        if (!CodeFieldContainer.this.ignoreOnTextChange && (len = s.length()) >= 1) {
                            int n = num;
                            if (len > 1) {
                                String text = s.toString();
                                CodeFieldContainer.this.ignoreOnTextChange = true;
                                for (int a = 0; a < Math.min(i - num, len); a++) {
                                    if (a == 0) {
                                        s.replace(0, len, text.substring(a, a + 1));
                                    } else {
                                        n++;
                                        CodeFieldContainer.this.codeField[num + a].setText(text.substring(a, a + 1));
                                    }
                                }
                                CodeFieldContainer.this.ignoreOnTextChange = false;
                            }
                            if (n != i - 1) {
                                CodeFieldContainer.this.codeField[n + 1].setSelection(CodeFieldContainer.this.codeField[n + 1].length());
                                CodeFieldContainer.this.codeField[n + 1].requestFocus();
                            }
                            int i = i;
                            if ((n == i - 1 || (n == i - 2 && len >= 2)) && CodeFieldContainer.this.getCode().length() == i) {
                                CodeFieldContainer.this.processNextPressed();
                            }
                        }
                    }
                });
                this.codeField[a].setOnEditorActionListener(new CodeFieldContainer$$ExternalSyntheticLambda0(this));
                a++;
                i2 = currentType;
            }
            return;
        }
        int a2 = 0;
        while (true) {
            CodeNumberField[] codeNumberFieldArr2 = this.codeField;
            if (a2 < codeNumberFieldArr2.length) {
                codeNumberFieldArr2[a2].setText("");
                a2++;
            } else {
                return;
            }
        }
    }

    /* renamed from: lambda$setNumbersCount$0$org-telegram-ui-CodeFieldContainer  reason: not valid java name */
    public /* synthetic */ boolean m3325lambda$setNumbersCount$0$orgtelegramuiCodeFieldContainer(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        processNextPressed();
        return true;
    }

    /* access modifiers changed from: protected */
    public void processNextPressed() {
    }

    public String getCode() {
        if (this.codeField == null) {
            return "";
        }
        StringBuilder codeBuilder = new StringBuilder();
        int a = 0;
        while (true) {
            CodeNumberField[] codeNumberFieldArr = this.codeField;
            if (a >= codeNumberFieldArr.length) {
                return codeBuilder.toString();
            }
            codeBuilder.append(PhoneFormat.stripExceptNumbers(codeNumberFieldArr[a].getText().toString()));
            a++;
        }
    }

    public void setCode(String savedCode) {
        this.codeField[0].setText(savedCode);
    }

    public void setText(String code) {
        setText(code, false);
    }

    public void setText(String code, boolean fromPaste) {
        int startFrom = 0;
        if (fromPaste) {
            int i = 0;
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeField;
                if (i >= codeNumberFieldArr.length) {
                    break;
                } else if (codeNumberFieldArr[i].isFocused()) {
                    startFrom = i;
                    break;
                } else {
                    i++;
                }
            }
        }
        for (int i2 = startFrom; i2 < Math.min(this.codeField.length, code.length() + startFrom); i2++) {
            this.codeField[i2].setText(Character.toString(code.charAt(i2 - startFrom)));
        }
    }
}
