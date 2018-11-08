package org.telegram.p005ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0541R;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.SeekBarView;

/* renamed from: org.telegram.ui.Cells.BrightnessControlCell */
public class BrightnessControlCell extends FrameLayout {
    private ImageView leftImageView;
    private ImageView rightImageView;
    private SeekBarView seekBarView;

    public BrightnessControlCell(Context context) {
        super(context);
        this.leftImageView = new ImageView(context);
        this.leftImageView.setImageResource(C0541R.drawable.brightness_low);
        addView(this.leftImageView, LayoutHelper.createFrame(24, 24.0f, 51, 17.0f, 12.0f, 0.0f, 0.0f));
        this.seekBarView = new SeekBarView(context) {
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(event);
            }
        };
        this.seekBarView.setReportChanges(true);
        this.seekBarView.setDelegate(new BrightnessControlCell$$Lambda$0(this));
        addView(this.seekBarView, LayoutHelper.createFrame(-1, 30.0f, 51, 58.0f, 9.0f, 58.0f, 0.0f));
        this.rightImageView = new ImageView(context);
        this.rightImageView.setImageResource(C0541R.drawable.brightness_high);
        addView(this.rightImageView, LayoutHelper.createFrame(24, 24.0f, 53, 0.0f, 12.0f, 17.0f, 0.0f));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.leftImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        this.rightImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
    }

    protected void didChangedValue(float value) {
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(48.0f), NUM));
    }

    public void setProgress(float value) {
        this.seekBarView.setProgress(value);
    }
}
