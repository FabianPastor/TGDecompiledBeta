package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class StickerSetGroupInfoCell extends LinearLayout {
    private TextView addButton;
    private boolean isLast;

    public StickerSetGroupInfoCell(Context context) {
        super(context);
        setOrientation(1);
        TextView infoTextView = new TextView(context);
        infoTextView.setTextColor(Theme.getColor("chat_emojiPanelTrendingDescription"));
        infoTextView.setTextSize(1, 14.0f);
        infoTextView.setText(LocaleController.getString("GroupStickersInfo", NUM));
        addView(infoTextView, LayoutHelper.createLinear(-1, -2, 51, 17, 4, 17, 0));
        TextView textView = new TextView(context);
        this.addButton = textView;
        textView.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
        this.addButton.setGravity(17);
        this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.addButton.setTextSize(1, 14.0f);
        this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.addButton.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 4.0f));
        this.addButton.setText(LocaleController.getString("ChooseStickerSet", NUM).toUpperCase());
        addView(this.addButton, LayoutHelper.createLinear(-2, 28, 51, 17, 10, 14, 8));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View parent;
        int height;
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), heightMeasureSpec);
        if (this.isLast && (parent = (View) getParent()) != null && getMeasuredHeight() < (height = ((parent.getMeasuredHeight() - parent.getPaddingBottom()) - parent.getPaddingTop()) - AndroidUtilities.dp(24.0f))) {
            setMeasuredDimension(getMeasuredWidth(), height);
        }
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        this.addButton.setOnClickListener(onClickListener);
    }

    public void setIsLast(boolean last) {
        this.isLast = last;
        requestLayout();
    }
}
