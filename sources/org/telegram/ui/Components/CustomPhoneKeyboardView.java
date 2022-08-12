package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextPaint;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GestureDetectorCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

public class CustomPhoneKeyboardView extends ViewGroup {
    private ImageView backButton;
    /* access modifiers changed from: private */
    public Runnable detectLongClick = new CustomPhoneKeyboardView$$ExternalSyntheticLambda2(this);
    private boolean dispatchBackWhenEmpty;
    private EditText editText;
    /* access modifiers changed from: private */
    public Runnable onBackButton = new CustomPhoneKeyboardView$$ExternalSyntheticLambda3(this);
    /* access modifiers changed from: private */
    public boolean postedLongClick;
    /* access modifiers changed from: private */
    public boolean runningLongClick;
    private View[] views = new View[12];

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$3(View view) {
    }

    public boolean canScrollHorizontally(int i) {
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        EditText editText2 = this.editText;
        if (editText2 == null) {
            return;
        }
        if (editText2.length() != 0 || this.dispatchBackWhenEmpty) {
            performHapticFeedback(3, 2);
            playSoundEffect(0);
            this.editText.dispatchKeyEvent(new KeyEvent(0, 67));
            this.editText.dispatchKeyEvent(new KeyEvent(1, 67));
            if (this.runningLongClick) {
                postDelayed(this.onBackButton, 50);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        this.postedLongClick = false;
        this.runningLongClick = true;
        this.onBackButton.run();
    }

    public CustomPhoneKeyboardView(Context context) {
        super(context);
        String str;
        int i = 0;
        while (i < 11) {
            if (i != 9) {
                switch (i) {
                    case 1:
                        str = "ABC";
                        break;
                    case 2:
                        str = "DEF";
                        break;
                    case 3:
                        str = "GHI";
                        break;
                    case 4:
                        str = "JKL";
                        break;
                    case 5:
                        str = "MNO";
                        break;
                    case 6:
                        str = "PQRS";
                        break;
                    case 7:
                        str = "TUV";
                        break;
                    case 8:
                        str = "WXYZ";
                        break;
                    case 10:
                        str = "+";
                        break;
                    default:
                        str = "";
                        break;
                }
                String valueOf = String.valueOf(i != 10 ? i + 1 : 0);
                this.views[i] = new NumberButtonView(context, valueOf, str);
                this.views[i].setOnClickListener(new CustomPhoneKeyboardView$$ExternalSyntheticLambda0(this, valueOf));
                addView(this.views[i]);
            }
            i++;
        }
        final GestureDetectorCompat gestureDetectorCompat = setupBackButtonDetector(context);
        AnonymousClass1 r1 = new ImageView(context) {
            @SuppressLint({"ClickableViewAccessibility"})
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && (CustomPhoneKeyboardView.this.postedLongClick || CustomPhoneKeyboardView.this.runningLongClick)) {
                    boolean unused = CustomPhoneKeyboardView.this.postedLongClick = false;
                    boolean unused2 = CustomPhoneKeyboardView.this.runningLongClick = false;
                    removeCallbacks(CustomPhoneKeyboardView.this.detectLongClick);
                    removeCallbacks(CustomPhoneKeyboardView.this.onBackButton);
                }
                super.onTouchEvent(motionEvent);
                return gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        };
        this.backButton = r1;
        r1.setImageResource(R.drawable.msg_clear_input);
        this.backButton.setColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.backButton.setBackground(getButtonDrawable());
        int dp = AndroidUtilities.dp(11.0f);
        this.backButton.setPadding(dp, dp, dp, dp);
        this.backButton.setOnClickListener(CustomPhoneKeyboardView$$ExternalSyntheticLambda1.INSTANCE);
        View[] viewArr = this.views;
        ImageView imageView = this.backButton;
        viewArr[11] = imageView;
        addView(imageView);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(String str, View view) {
        if (this.editText != null) {
            performHapticFeedback(3, 2);
            EditText editText2 = this.editText;
            if (editText2 instanceof EditTextBoldCursor) {
                ((EditTextBoldCursor) editText2).setTextWatchersSuppressed(true, false);
            }
            Editable text = this.editText.getText();
            int selectionStart = this.editText.getSelectionEnd() == this.editText.length() ? -1 : this.editText.getSelectionStart() + str.length();
            if (this.editText.getSelectionStart() == -1 || this.editText.getSelectionEnd() == -1) {
                this.editText.setText(str);
                EditText editText3 = this.editText;
                editText3.setSelection(editText3.length());
            } else {
                EditText editText4 = this.editText;
                editText4.setText(text.replace(editText4.getSelectionStart(), this.editText.getSelectionEnd(), str));
                EditText editText5 = this.editText;
                if (selectionStart == -1) {
                    selectionStart = editText5.length();
                }
                editText5.setSelection(selectionStart);
            }
            EditText editText6 = this.editText;
            if (editText6 instanceof EditTextBoldCursor) {
                ((EditTextBoldCursor) editText6).setTextWatchersSuppressed(false, true);
            }
        }
    }

    public void setDispatchBackWhenEmpty(boolean z) {
        this.dispatchBackWhenEmpty = z;
    }

    private GestureDetectorCompat setupBackButtonDetector(Context context) {
        final int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        return new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent motionEvent) {
                if (CustomPhoneKeyboardView.this.postedLongClick) {
                    CustomPhoneKeyboardView customPhoneKeyboardView = CustomPhoneKeyboardView.this;
                    customPhoneKeyboardView.removeCallbacks(customPhoneKeyboardView.detectLongClick);
                }
                boolean unused = CustomPhoneKeyboardView.this.postedLongClick = true;
                CustomPhoneKeyboardView customPhoneKeyboardView2 = CustomPhoneKeyboardView.this;
                customPhoneKeyboardView2.postDelayed(customPhoneKeyboardView2.detectLongClick, 200);
                CustomPhoneKeyboardView.this.onBackButton.run();
                return true;
            }

            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if ((CustomPhoneKeyboardView.this.postedLongClick || CustomPhoneKeyboardView.this.runningLongClick) && (Math.abs(f) >= ((float) scaledTouchSlop) || Math.abs(f2) >= ((float) scaledTouchSlop))) {
                    boolean unused = CustomPhoneKeyboardView.this.postedLongClick = false;
                    boolean unused2 = CustomPhoneKeyboardView.this.runningLongClick = false;
                    CustomPhoneKeyboardView customPhoneKeyboardView = CustomPhoneKeyboardView.this;
                    customPhoneKeyboardView.removeCallbacks(customPhoneKeyboardView.detectLongClick);
                    CustomPhoneKeyboardView customPhoneKeyboardView2 = CustomPhoneKeyboardView.this;
                    customPhoneKeyboardView2.removeCallbacks(customPhoneKeyboardView2.onBackButton);
                }
                return false;
            }
        });
    }

    public void setEditText(EditText editText2) {
        this.editText = editText2;
        this.dispatchBackWhenEmpty = false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int width = (getWidth() - AndroidUtilities.dp(32.0f)) / 3;
        int height = (getHeight() - AndroidUtilities.dp(42.0f)) / 4;
        for (int i5 = 0; i5 < this.views.length; i5++) {
            int dp = ((i5 % 3) * (AndroidUtilities.dp(6.0f) + width)) + AndroidUtilities.dp(10.0f);
            int dp2 = ((i5 / 3) * (AndroidUtilities.dp(6.0f) + height)) + AndroidUtilities.dp(10.0f);
            View[] viewArr = this.views;
            if (viewArr[i5] != null) {
                viewArr[i5].layout(dp, dp2, dp + width, dp2 + height);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        int width = (getWidth() - AndroidUtilities.dp(32.0f)) / 3;
        int height = (getHeight() - AndroidUtilities.dp(42.0f)) / 4;
        for (View view : this.views) {
            if (view != null) {
                view.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
            }
        }
    }

    /* access modifiers changed from: private */
    public static Drawable getButtonDrawable() {
        return Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("listSelectorSDK21"), ColorUtils.setAlphaComponent(Theme.getColor("listSelectorSDK21"), 60));
    }

    public void updateColors() {
        this.backButton.setColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"));
        for (View view : this.views) {
            if (view != null) {
                view.setBackground(getButtonDrawable());
                if (view instanceof NumberButtonView) {
                    ((NumberButtonView) view).updateColors();
                }
            }
        }
    }

    private static final class NumberButtonView extends View {
        private String mNumber;
        private String mSymbols;
        private TextPaint numberTextPaint = new TextPaint(1);
        private Rect rect = new Rect();
        private TextPaint symbolsTextPaint = new TextPaint(1);

        public NumberButtonView(Context context, String str, String str2) {
            super(context);
            this.mNumber = str;
            this.mSymbols = str2;
            this.numberTextPaint.setTextSize((float) AndroidUtilities.dp(24.0f));
            this.symbolsTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            setBackground(CustomPhoneKeyboardView.getButtonDrawable());
            updateColors();
        }

        /* access modifiers changed from: private */
        public void updateColors() {
            this.numberTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.symbolsTextPaint.setColor(Theme.getColor("windowBackgroundWhiteHintText"));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float measureText = this.symbolsTextPaint.measureText(this.mSymbols);
            float measureText2 = this.numberTextPaint.measureText(this.mNumber);
            TextPaint textPaint = this.numberTextPaint;
            String str = this.mNumber;
            textPaint.getTextBounds(str, 0, str.length(), this.rect);
            TextPaint textPaint2 = this.symbolsTextPaint;
            String str2 = this.mSymbols;
            textPaint2.getTextBounds(str2, 0, str2.length(), this.rect);
            canvas.drawText(this.mNumber, (((float) getWidth()) * 0.25f) - (measureText2 / 2.0f), (((float) getHeight()) / 2.0f) + (((float) this.rect.height()) / 2.0f), this.numberTextPaint);
            canvas.drawText(this.mSymbols, (((float) getWidth()) * 0.7f) - (measureText / 2.0f), (((float) getHeight()) / 2.0f) + (((float) this.rect.height()) / 2.0f), this.symbolsTextPaint);
        }
    }
}
