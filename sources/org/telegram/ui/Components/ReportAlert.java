package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class ReportAlert extends BottomSheet {
    private BottomSheetCell clearButton;
    private EditTextBoldCursor editText;

    /* access modifiers changed from: protected */
    public void onSend(String str) {
        throw null;
    }

    public static class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public View background;
        private TextView textView;

        public BottomSheetCell(Context context) {
            super(context);
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(17);
            this.textView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
        }
    }

    public ReportAlert(Context context, int i) {
        super(context, true);
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        FrameLayout frameLayout = new FrameLayout(context);
        setCustomView(frameLayout);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        rLottieImageView.setAutoRepeat(true);
        rLottieImageView.setAnimation(NUM, 120, 120);
        rLottieImageView.playAnimation();
        frameLayout.addView(rLottieImageView, LayoutHelper.createFrame(160, 160.0f, 49, 17.0f, 14.0f, 17.0f, 0.0f));
        TextView textView = new TextView(context);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 24.0f);
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        int i2 = 5;
        if (i == 0) {
            textView.setText(LocaleController.getString("ReportTitleSpam", NUM));
        } else if (i == 2) {
            textView.setText(LocaleController.getString("ReportTitleViolence", NUM));
        } else if (i == 3) {
            textView.setText(LocaleController.getString("ReportTitleChild", NUM));
        } else if (i == 4) {
            textView.setText(LocaleController.getString("ReportTitlePornography", NUM));
        } else if (i == 5) {
            textView.setText(LocaleController.getString("ReportChat", NUM));
        }
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 17.0f, 197.0f, 17.0f, 0.0f));
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(Theme.getColor("dialogTextGray3"));
        textView2.setGravity(1);
        textView2.setText(LocaleController.getString("ReportInfo", NUM));
        frameLayout.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, 49, 30.0f, 235.0f, 30.0f, 44.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.editText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setGravity(!LocaleController.isRTL ? 3 : i2);
        this.editText.setInputType(180224);
        this.editText.setImeOptions(6);
        this.editText.setHint(LocaleController.getString("ReportHint", NUM));
        this.editText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.editText.setCursorWidth(1.5f);
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ReportAlert.this.lambda$new$0$ReportAlert(textView, i, keyEvent);
            }
        });
        frameLayout.addView(this.editText, LayoutHelper.createFrame(-1, 36.0f, 51, 17.0f, 305.0f, 17.0f, 0.0f));
        BottomSheetCell bottomSheetCell = new BottomSheetCell(context);
        this.clearButton = bottomSheetCell;
        bottomSheetCell.setBackground((Drawable) null);
        this.clearButton.setText(LocaleController.getString("ReportSend", NUM));
        this.clearButton.background.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ReportAlert.this.lambda$new$1$ReportAlert(view);
            }
        });
        frameLayout.addView(this.clearButton, LayoutHelper.createFrame(-1, 50.0f, 51, 0.0f, 357.0f, 0.0f, 0.0f));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ boolean lambda$new$0$ReportAlert(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.clearButton.background.callOnClick();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ReportAlert(View view) {
        AndroidUtilities.hideKeyboard(this.editText);
        onSend(this.editText.getText().toString());
        dismiss();
    }
}
