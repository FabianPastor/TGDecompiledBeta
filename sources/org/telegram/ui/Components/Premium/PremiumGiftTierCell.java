package org.telegram.ui.Components.Premium;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet;

public class PremiumGiftTierCell extends ViewGroup {
    private CheckBox2 checkBox;
    protected TextView discountView;
    private int leftPaddingToTextDp = 24;
    private TextView pricePerMonthView;
    private TextView priceTotalView;
    protected GiftPremiumBottomSheet.GiftTier tier;
    private TextView titleView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PremiumGiftTierCell(Context context) {
        super(context);
        Context context2 = context;
        CheckBox2 checkBox2 = new CheckBox2(context2, 24);
        this.checkBox = checkBox2;
        checkBox2.setDrawBackgroundAsArc(10);
        this.checkBox.setColor("radioBackground", "radioBackground", "checkboxCheck");
        addView(this.checkBox);
        TextView textView = new TextView(context2);
        this.titleView = textView;
        textView.setTextSize(1, 16.0f);
        this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        int i = 5;
        addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 8.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.discountView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.discountView.setTextColor(-1);
        this.discountView.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        this.discountView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.discountView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 80, 0.0f, 0.0f, 0.0f, 8.0f));
        TextView textView3 = new TextView(context2);
        this.pricePerMonthView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.pricePerMonthView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        addView(this.pricePerMonthView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 80, 0.0f, 0.0f, 0.0f, 8.0f));
        TextView textView4 = new TextView(context2);
        this.priceTotalView = textView4;
        textView4.setTextSize(1, 16.0f);
        this.priceTotalView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        addView(this.priceTotalView, LayoutHelper.createFrame(-2, -2, 8388613));
        setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        setClipToPadding(false);
        setWillNotDraw(false);
    }

    public void setProgressDelegate(CheckBoxBase.ProgressDelegate progressDelegate) {
        this.checkBox.setProgressDelegate(progressDelegate);
    }

    public void setCirclePaintProvider(GenericProvider<Void, Paint> genericProvider) {
        this.checkBox.setCirclePaintProvider(genericProvider);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        Rect rect = AndroidUtilities.rectTmp2;
        rect.set(AndroidUtilities.dp(8.0f) + getPaddingLeft(), (int) (((float) (getMeasuredHeight() - this.checkBox.getMeasuredHeight())) / 2.0f), 0, 0);
        checkRtlAndLayout(this.checkBox);
        rect.set(((getMeasuredWidth() - this.priceTotalView.getMeasuredWidth()) - AndroidUtilities.dp(16.0f)) - getPaddingRight(), (int) (((float) (getMeasuredHeight() - this.priceTotalView.getMeasuredHeight())) / 2.0f), 0, 0);
        checkRtlAndLayout(this.priceTotalView);
        rect.set(AndroidUtilities.dp((float) (this.leftPaddingToTextDp + 8)) + this.checkBox.getMeasuredWidth() + getPaddingLeft(), getPaddingTop(), 0, 0);
        checkRtlAndLayout(this.titleView);
        rect.set(AndroidUtilities.dp((float) (this.leftPaddingToTextDp + 8)) + this.checkBox.getMeasuredWidth() + getPaddingLeft(), (getMeasuredHeight() - this.discountView.getMeasuredHeight()) - getPaddingBottom(), 0, 0);
        checkRtlAndLayout(this.discountView);
        rect.set(AndroidUtilities.dp((float) (this.leftPaddingToTextDp + 8 + 6)) + this.checkBox.getMeasuredWidth() + this.discountView.getMeasuredWidth() + getPaddingLeft(), (getMeasuredHeight() - this.discountView.getMeasuredHeight()) - getPaddingBottom(), 0, 0);
        checkRtlAndLayout(this.pricePerMonthView);
    }

    private void checkRtlAndLayout(View view) {
        Rect rect = AndroidUtilities.rectTmp2;
        rect.right = rect.left + view.getMeasuredWidth();
        int measuredHeight = rect.top + view.getMeasuredHeight();
        rect.bottom = measuredHeight;
        if (LocaleController.isRTL) {
            int i = rect.right;
            rect.right = rect.left;
            rect.left = i;
        }
        view.layout(rect.left, rect.top, rect.right, measuredHeight);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = AndroidUtilities.dp(68.0f);
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(28.0f), NUM);
        this.checkBox.measure(makeMeasureSpec, makeMeasureSpec);
        this.priceTotalView.measure(View.MeasureSpec.makeMeasureSpec(size - this.checkBox.getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        this.titleView.measure(View.MeasureSpec.makeMeasureSpec((size - this.checkBox.getMeasuredWidth()) - this.priceTotalView.getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        this.discountView.measure(View.MeasureSpec.makeMeasureSpec((size - this.checkBox.getMeasuredWidth()) - this.priceTotalView.getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        this.pricePerMonthView.measure(View.MeasureSpec.makeMeasureSpec(((size - this.checkBox.getMeasuredWidth()) - this.priceTotalView.getMeasuredWidth()) - this.discountView.getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        setMeasuredDimension(size, dp);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    @SuppressLint({"SetTextI18n"})
    public void bind(GiftPremiumBottomSheet.GiftTier giftTier) {
        this.tier = giftTier;
        this.titleView.setText(LocaleController.formatPluralString("Months", giftTier.getMonths(), new Object[0]));
        this.discountView.setText(LocaleController.formatString(NUM, Integer.valueOf(giftTier.getDiscount())));
        this.pricePerMonthView.setText(LocaleController.formatString(NUM, giftTier.getFormattedPricePerMonth()));
        this.priceTotalView.setText(giftTier.getFormattedPrice());
        requestLayout();
    }
}
