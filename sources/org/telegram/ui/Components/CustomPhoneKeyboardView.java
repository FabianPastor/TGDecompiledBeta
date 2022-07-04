package org.telegram.ui.Components;

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
import org.telegram.ui.ActionBar.Theme;

public class CustomPhoneKeyboardView extends ViewGroup {
    private static final int BUTTON_PADDING = 6;
    public static final int KEYBOARD_HEIGHT_DP = 230;
    private static final int SIDE_PADDING = 10;
    private ImageView backButton;
    /* access modifiers changed from: private */
    public Runnable detectLongClick = new CustomPhoneKeyboardView$$ExternalSyntheticLambda3(this);
    private boolean dispatchBackWhenEmpty;
    private EditText editText;
    /* access modifiers changed from: private */
    public Runnable onBackButton = new CustomPhoneKeyboardView$$ExternalSyntheticLambda2(this);
    /* access modifiers changed from: private */
    public boolean postedLongClick;
    /* access modifiers changed from: private */
    public boolean runningLongClick;
    private View[] views = new View[12];

    /* renamed from: lambda$new$0$org-telegram-ui-Components-CustomPhoneKeyboardView  reason: not valid java name */
    public /* synthetic */ void m903lambda$new$0$orgtelegramuiComponentsCustomPhoneKeyboardView() {
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

    /* renamed from: lambda$new$1$org-telegram-ui-Components-CustomPhoneKeyboardView  reason: not valid java name */
    public /* synthetic */ void m904lambda$new$1$orgtelegramuiComponentsCustomPhoneKeyboardView() {
        this.postedLongClick = false;
        this.runningLongClick = true;
        this.onBackButton.run();
    }

    public CustomPhoneKeyboardView(Context context) {
        super(context);
        String symbols;
        int i = 0;
        while (i < 11) {
            if (i != 9) {
                switch (i) {
                    case 1:
                        symbols = "ABC";
                        break;
                    case 2:
                        symbols = "DEF";
                        break;
                    case 3:
                        symbols = "GHI";
                        break;
                    case 4:
                        symbols = "JKL";
                        break;
                    case 5:
                        symbols = "MNO";
                        break;
                    case 6:
                        symbols = "PQRS";
                        break;
                    case 7:
                        symbols = "TUV";
                        break;
                    case 8:
                        symbols = "WXYZ";
                        break;
                    case 10:
                        symbols = "+";
                        break;
                    default:
                        symbols = "";
                        break;
                }
                String num = String.valueOf(i != 10 ? i + 1 : 0);
                this.views[i] = new NumberButtonView(context, num, symbols);
                this.views[i].setOnClickListener(new CustomPhoneKeyboardView$$ExternalSyntheticLambda0(this, num));
                addView(this.views[i]);
            }
            i++;
        }
        final GestureDetectorCompat backDetector = setupBackButtonDetector(context);
        AnonymousClass1 r2 = new ImageView(context) {
            public boolean onTouchEvent(MotionEvent event) {
                if ((event.getAction() == 1 || event.getAction() == 3) && (CustomPhoneKeyboardView.this.postedLongClick || CustomPhoneKeyboardView.this.runningLongClick)) {
                    boolean unused = CustomPhoneKeyboardView.this.postedLongClick = false;
                    boolean unused2 = CustomPhoneKeyboardView.this.runningLongClick = false;
                    removeCallbacks(CustomPhoneKeyboardView.this.detectLongClick);
                    removeCallbacks(CustomPhoneKeyboardView.this.onBackButton);
                }
                super.onTouchEvent(event);
                return backDetector.onTouchEvent(event);
            }
        };
        this.backButton = r2;
        r2.setImageResource(NUM);
        this.backButton.setColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.backButton.setBackground(getButtonDrawable());
        int pad = AndroidUtilities.dp(11.0f);
        this.backButton.setPadding(pad, pad, pad, pad);
        this.backButton.setOnClickListener(CustomPhoneKeyboardView$$ExternalSyntheticLambda1.INSTANCE);
        View[] viewArr = this.views;
        ImageView imageView = this.backButton;
        viewArr[11] = imageView;
        addView(imageView);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-CustomPhoneKeyboardView  reason: not valid java name */
    public /* synthetic */ void m905lambda$new$2$orgtelegramuiComponentsCustomPhoneKeyboardView(String num, View v) {
        if (this.editText != null) {
            performHapticFeedback(3, 2);
            EditText editText2 = this.editText;
            if (editText2 instanceof EditTextBoldCursor) {
                ((EditTextBoldCursor) editText2).setTextWatchersSuppressed(true, false);
            }
            Editable text = this.editText.getText();
            int newSelection = this.editText.getSelectionEnd() == this.editText.length() ? -1 : this.editText.getSelectionStart() + num.length();
            if (this.editText.getSelectionStart() == -1 || this.editText.getSelectionEnd() == -1) {
                this.editText.setText(num);
                EditText editText3 = this.editText;
                editText3.setSelection(editText3.length());
            } else {
                EditText editText4 = this.editText;
                editText4.setText(text.replace(editText4.getSelectionStart(), this.editText.getSelectionEnd(), num));
                EditText editText5 = this.editText;
                editText5.setSelection(newSelection == -1 ? editText5.length() : newSelection);
            }
            EditText editText6 = this.editText;
            if (editText6 instanceof EditTextBoldCursor) {
                ((EditTextBoldCursor) editText6).setTextWatchersSuppressed(false, true);
            }
        }
    }

    static /* synthetic */ void lambda$new$3(View v) {
    }

    public boolean canScrollHorizontally(int direction) {
        return true;
    }

    public void setDispatchBackWhenEmpty(boolean dispatchBackWhenEmpty2) {
        this.dispatchBackWhenEmpty = dispatchBackWhenEmpty2;
    }

    private GestureDetectorCompat setupBackButtonDetector(Context context) {
        final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        return new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent e) {
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

            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if ((CustomPhoneKeyboardView.this.postedLongClick || CustomPhoneKeyboardView.this.runningLongClick) && (Math.abs(distanceX) >= ((float) touchSlop) || Math.abs(distanceY) >= ((float) touchSlop))) {
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
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int btnWidth = (getWidth() - AndroidUtilities.dp(32.0f)) / 3;
        int btnHeight = (getHeight() - AndroidUtilities.dp(42.0f)) / 4;
        for (int i = 0; i < this.views.length; i++) {
            int left = ((AndroidUtilities.dp(6.0f) + btnWidth) * (i % 3)) + AndroidUtilities.dp(10.0f);
            int top = ((AndroidUtilities.dp(6.0f) + btnHeight) * (i / 3)) + AndroidUtilities.dp(10.0f);
            View[] viewArr = this.views;
            if (viewArr[i] != null) {
                viewArr[i].layout(left, top, left + btnWidth, top + btnHeight);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        int btnWidth = (getWidth() - AndroidUtilities.dp(32.0f)) / 3;
        int btnHeight = (getHeight() - AndroidUtilities.dp(42.0f)) / 4;
        for (View v : this.views) {
            if (v != null) {
                v.measure(View.MeasureSpec.makeMeasureSpec(btnWidth, NUM), View.MeasureSpec.makeMeasureSpec(btnHeight, NUM));
            }
        }
    }

    /* access modifiers changed from: private */
    public static Drawable getButtonDrawable() {
        return Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("listSelectorSDK21"), ColorUtils.setAlphaComponent(Theme.getColor("listSelectorSDK21"), 60));
    }

    public void updateColors() {
        this.backButton.setColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"));
        for (View v : this.views) {
            if (v != null) {
                v.setBackground(getButtonDrawable());
                if (v instanceof NumberButtonView) {
                    ((NumberButtonView) v).updateColors();
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

        public NumberButtonView(Context context, String number, String symbols) {
            super(context);
            this.mNumber = number;
            this.mSymbols = symbols;
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
            float symbolsWidth = this.symbolsTextPaint.measureText(this.mSymbols);
            float numberWidth = this.numberTextPaint.measureText(this.mNumber);
            TextPaint textPaint = this.numberTextPaint;
            String str = this.mNumber;
            textPaint.getTextBounds(str, 0, str.length(), this.rect);
            TextPaint textPaint2 = this.symbolsTextPaint;
            String str2 = this.mSymbols;
            textPaint2.getTextBounds(str2, 0, str2.length(), this.rect);
            canvas.drawText(this.mNumber, (((float) getWidth()) * 0.25f) - (numberWidth / 2.0f), (((float) getHeight()) / 2.0f) + (((float) this.rect.height()) / 2.0f), this.numberTextPaint);
            canvas.drawText(this.mSymbols, (((float) getWidth()) * 0.7f) - (symbolsWidth / 2.0f), (((float) getHeight()) / 2.0f) + (((float) this.rect.height()) / 2.0f), this.symbolsTextPaint);
        }
    }
}
