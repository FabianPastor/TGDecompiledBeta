package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;

public class BrightnessControlCell extends FrameLayout {
    private ImageView leftImageView;
    private ImageView rightImageView;
    private SeekBarView seekBarView;

    /* renamed from: org.telegram.ui.Cells.BrightnessControlCell$2 */
    class C19422 implements SeekBarViewDelegate {
        C19422() {
        }

        public void onSeekBarDrag(float f) {
            BrightnessControlCell.this.didChangedValue(f);
        }
    }

    protected void didChangedValue(float f) {
    }

    public BrightnessControlCell(Context context) {
        super(context);
        this.leftImageView = new ImageView(context);
        this.leftImageView.setImageResource(C0446R.drawable.brightness_low);
        addView(this.leftImageView, LayoutHelper.createFrame(24, 24.0f, 51, 17.0f, 12.0f, 0.0f, 0.0f));
        this.seekBarView = new SeekBarView(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.seekBarView.setReportChanges(true);
        this.seekBarView.setDelegate(new C19422());
        addView(this.seekBarView, LayoutHelper.createFrame(-1, 30.0f, 51, 58.0f, 9.0f, 58.0f, 0.0f));
        this.rightImageView = new ImageView(context);
        this.rightImageView.setImageResource(C0446R.drawable.brightness_high);
        addView(this.rightImageView, LayoutHelper.createFrame(24, 24.0f, 53, 0.0f, 12.0f, 17.0f, 0.0f));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.leftImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        this.rightImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
    }

    public void setProgress(float f) {
        this.seekBarView.setProgress(f);
    }
}
