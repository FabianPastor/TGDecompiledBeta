package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;

public class ChatLoadingCell extends FrameLayout {
    private FrameLayout frameLayout;
    private RadialProgressView progressBar;
    private Theme.ResourcesProvider resourcesProvider;

    public ChatLoadingCell(Context context, View parent, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.frameLayout = frameLayout2;
        frameLayout2.setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(18.0f), this.frameLayout, parent, getThemedPaint("paintChatActionBackground")));
        addView(this.frameLayout, LayoutHelper.createFrame(36, 36, 17));
        RadialProgressView radialProgressView = new RadialProgressView(context, resourcesProvider2);
        this.progressBar = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(getThemedColor("chat_serviceText"));
        this.frameLayout.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
    }

    public void setProgressVisible(boolean value) {
        this.frameLayout.setVisibility(value ? 0 : 4);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Paint paint = resourcesProvider2 != null ? resourcesProvider2.getPaint(paintKey) : null;
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }
}
