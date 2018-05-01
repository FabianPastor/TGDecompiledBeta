package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

@SuppressLint({"NewApi"})
public class PhotoAttachCameraCell extends FrameLayout {
    public PhotoAttachCameraCell(Context context) {
        super(context);
        View imageView = new ImageView(context);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageResource(C0446R.drawable.instant_camera);
        imageView.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        addView(imageView, LayoutHelper.createFrame(80, 80.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
    }
}
