package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class LocationDirectionCell extends FrameLayout {
    private SimpleTextView buttonTextView;
    private FrameLayout frameLayout;
    private final Theme.ResourcesProvider resourcesProvider;

    public LocationDirectionCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.frameLayout = frameLayout2;
        frameLayout2.setBackground(Theme.AdaptiveRipple.filledRect(getThemedColor("featuredStickers_addButton"), 4.0f));
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 48.0f, 51, 16.0f, 10.0f, 16.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.buttonTextView = simpleTextView;
        simpleTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setDrawablePadding(AndroidUtilities.dp(8.0f));
        this.buttonTextView.setTextColor(getThemedColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(14);
        this.buttonTextView.setText(LocaleController.getString("Directions", NUM));
        this.buttonTextView.setLeftDrawable(NUM);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.frameLayout.addView(this.buttonTextView, LayoutHelper.createFrame(-1, -1.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(73.0f), NUM));
    }

    public void setOnButtonClick(View.OnClickListener onClickListener) {
        this.frameLayout.setOnClickListener(onClickListener);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
