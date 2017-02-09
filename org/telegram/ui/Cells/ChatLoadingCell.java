package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.google.android.gms.common.ConnectionResult;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class ChatLoadingCell extends FrameLayout {
    private FrameLayout frameLayout;
    private ProgressBar progressBar;

    public ChatLoadingCell(Context context) {
        super(context);
        this.frameLayout = new FrameLayout(context);
        this.frameLayout.setBackgroundResource(R.drawable.system_loader);
        this.frameLayout.getBackground().setColorFilter(Theme.colorFilter);
        addView(this.frameLayout, LayoutHelper.createFrame(36, 36, 17));
        ProgressBar progressBar = new ProgressBar(context);
        try {
            progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading_animation));
        } catch (Exception e) {
        }
        progressBar.setIndeterminate(true);
        AndroidUtilities.setProgressBarAnimationDuration(progressBar, ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED);
        this.frameLayout.addView(progressBar, LayoutHelper.createFrame(32, 32, 17));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
    }

    public void setProgressVisible(boolean value) {
        this.frameLayout.setVisibility(value ? 0 : 4);
    }

    public ProgressBar getProgressBar() {
        return this.progressBar;
    }
}
