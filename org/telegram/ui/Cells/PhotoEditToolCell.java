package org.telegram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoEditToolCell extends FrameLayout {
    private ImageView iconImage;
    private TextView nameTextView;
    private TextView valueTextView;
    private int width;

    public PhotoEditToolCell(Context context, int w) {
        super(context);
        this.width = w;
        this.iconImage = new ImageView(context);
        this.iconImage.setScaleType(ScaleType.CENTER);
        addView(this.iconImage, LayoutHelper.createFrame(-1, -1.0f, 49, 0.0f, 0.0f, 5.0f, 12.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setGravity(17);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(1, 10.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 5.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(-9649153);
        this.valueTextView.setTextSize(1, 11.0f);
        this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 52.0f, 3.0f, 0.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(this.width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW), NUM));
    }

    public void setIconAndTextAndValue(int resId, String text, float value) {
        this.iconImage.setImageResource(resId);
        this.nameTextView.setText(text.toUpperCase());
        if (value == 0.0f) {
            this.valueTextView.setText("");
        } else if (value > 0.0f) {
            this.valueTextView.setText("+" + ((int) value));
        } else {
            this.valueTextView.setText("" + ((int) value));
        }
    }

    public void setIconAndTextAndValue(int resId, String text, String value) {
        this.iconImage.setImageResource(resId);
        this.nameTextView.setText(text.toUpperCase());
        this.valueTextView.setText(value);
    }
}
