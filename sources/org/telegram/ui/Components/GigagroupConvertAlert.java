package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class GigagroupConvertAlert extends BottomSheet {
    protected void onCancel() {
        throw null;
    }

    protected void onCovert() {
        throw null;
    }

    /* loaded from: classes3.dex */
    public static class BottomSheetCell extends FrameLayout {
        private View background;
        private TextView textView;

        public BottomSheetCell(Context context) {
            super(context);
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 4.0f));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(17);
            this.textView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
        }
    }

    public GigagroupConvertAlert(final Context context, final BaseFragment baseFragment) {
        super(context, true);
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        setCustomView(linearLayout);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        rLottieImageView.setAutoRepeat(true);
        rLottieImageView.setAnimation(R.raw.utyan_gigagroup, 120, 120);
        rLottieImageView.playAnimation();
        linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(160, 160, 49, 17, 30, 17, 0));
        TextView textView = new TextView(context);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 24.0f);
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setText(LocaleController.getString("GigagroupConvertTitle", R.string.GigagroupConvertTitle));
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 49, 17, 18, 17, 0));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-2, -2, 1, 0, 12, 0, 0));
        int i = 0;
        while (true) {
            int i2 = 3;
            if (i < 3) {
                LinearLayout linearLayout3 = new LinearLayout(context);
                linearLayout3.setOrientation(0);
                linearLayout2.addView(linearLayout3, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
                ImageView imageView = new ImageView(context);
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogTextGray3"), PorterDuff.Mode.MULTIPLY));
                imageView.setImageResource(R.drawable.list_circle);
                TextView textView2 = new TextView(context);
                textView2.setTextSize(1, 15.0f);
                textView2.setTextColor(Theme.getColor("dialogTextGray3"));
                textView2.setGravity((LocaleController.isRTL ? 5 : i2) | 16);
                textView2.setMaxWidth(AndroidUtilities.dp(260.0f));
                if (i == 0) {
                    textView2.setText(LocaleController.getString("GigagroupConvertInfo1", R.string.GigagroupConvertInfo1));
                } else if (i == 1) {
                    textView2.setText(LocaleController.getString("GigagroupConvertInfo2", R.string.GigagroupConvertInfo2));
                } else if (i == 2) {
                    textView2.setText(LocaleController.getString("GigagroupConvertInfo3", R.string.GigagroupConvertInfo3));
                }
                if (LocaleController.isRTL) {
                    linearLayout3.addView(textView2, LayoutHelper.createLinear(-2, -2));
                    linearLayout3.addView(imageView, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                } else {
                    linearLayout3.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                    linearLayout3.addView(textView2, LayoutHelper.createLinear(-2, -2));
                }
                i++;
            } else {
                BottomSheetCell bottomSheetCell = new BottomSheetCell(context);
                bottomSheetCell.setBackground(null);
                bottomSheetCell.setText(LocaleController.getString("GigagroupConvertProcessButton", R.string.GigagroupConvertProcessButton));
                bottomSheetCell.background.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GigagroupConvertAlert$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        GigagroupConvertAlert.this.lambda$new$1(context, baseFragment, view);
                    }
                });
                linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 50, 51, 0, 29, 0, 0));
                TextView textView3 = new TextView(context);
                textView3.setTextSize(1, 14.0f);
                textView3.setTextColor(Theme.getColor("dialogTextBlue2"));
                textView3.setText(LocaleController.getString("GigagroupConvertCancelButton", R.string.GigagroupConvertCancelButton));
                textView3.setGravity(17);
                linearLayout.addView(textView3, LayoutHelper.createLinear(-2, 48, 49, 17, 0, 17, 16));
                textView3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GigagroupConvertAlert$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        GigagroupConvertAlert.this.lambda$new$2(view);
                    }
                });
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Context context, BaseFragment baseFragment, View view) {
        dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("GigagroupConvertAlertTitle", R.string.GigagroupConvertAlertTitle));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("GigagroupConvertAlertText", R.string.GigagroupConvertAlertText)));
        builder.setPositiveButton(LocaleController.getString("GigagroupConvertAlertConver", R.string.GigagroupConvertAlertConver), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.GigagroupConvertAlert$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                GigagroupConvertAlert.this.lambda$new$0(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        baseFragment.showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DialogInterface dialogInterface, int i) {
        onCovert();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        onCancel();
        dismiss();
    }
}
