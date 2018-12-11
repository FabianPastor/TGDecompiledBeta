package org.telegram.p005ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RadialProgressView;

/* renamed from: org.telegram.ui.Cells.ChatLoadingCell */
public class ChatLoadingCell extends FrameLayout {
    private FrameLayout frameLayout;
    private RadialProgressView progressBar;

    public ChatLoadingCell(Context context) {
        super(context);
        this.frameLayout = new FrameLayout(context);
        this.frameLayout.setBackgroundResource(CLASSNAMER.drawable.system_loader);
        this.frameLayout.getBackground().setColorFilter(Theme.colorFilter);
        addView(this.frameLayout, LayoutHelper.createFrame(36, 36, 17));
        this.progressBar = new RadialProgressView(context);
        this.progressBar.setSize(AndroidUtilities.m9dp(28.0f));
        this.progressBar.setProgressColor(Theme.getColor(Theme.key_chat_serviceText));
        this.frameLayout.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(44.0f), NUM));
    }

    public void setProgressVisible(boolean value) {
        this.frameLayout.setVisibility(value ? 0 : 4);
    }
}
