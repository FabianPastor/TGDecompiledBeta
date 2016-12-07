package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.LayoutHelper;

public class LocationPoweredCell extends FrameLayout {
    public LocationPoweredCell(Context context) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(context);
        addView(linearLayout, LayoutHelper.createFrame(-2, -2, 17));
        TextView textView = new TextView(context);
        textView.setTextSize(1, 16.0f);
        textView.setTextColor(-6710887);
        textView.setText("Powered by");
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.foursquare);
        imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        linearLayout.addView(imageView, LayoutHelper.createLinear(35, -2));
        textView = new TextView(context);
        textView.setTextSize(1, 16.0f);
        textView.setTextColor(-6710887);
        textView.setText("Foursquare");
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
    }
}
