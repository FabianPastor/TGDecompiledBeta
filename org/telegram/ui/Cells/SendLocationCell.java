package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;

public class SendLocationCell extends FrameLayout {
    private SimpleTextView accurateTextView;
    private ImageView imageView;
    private SimpleTextView titleTextView;

    public SendLocationCell(Context context) {
        int i;
        int i2;
        int i3 = 5;
        super(context);
        this.imageView = new ImageView(context);
        this.imageView.setImageResource(R.drawable.pin);
        Drawable circle = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor(Theme.key_location_sendLocationBackground), Theme.getColor(Theme.key_location_sendLocationBackground));
        Drawable drawable = getResources().getDrawable(R.drawable.pin);
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLocationIcon), Mode.MULTIPLY));
        CombinedDrawable combinedDrawable = new CombinedDrawable(circle, drawable);
        combinedDrawable.setCustomSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
        this.imageView.setBackgroundDrawable(combinedDrawable);
        addView(this.imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 17.0f, 13.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
        this.titleTextView = new SimpleTextView(context);
        this.titleTextView.setTextSize(16);
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText7));
        SimpleTextView simpleTextView = this.titleTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        simpleTextView.setGravity(i);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        View view = this.titleTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i2 | 48, LocaleController.isRTL ? 16.0f : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
        this.accurateTextView = new SimpleTextView(context);
        this.accurateTextView.setTextSize(14);
        this.accurateTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        simpleTextView = this.accurateTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        simpleTextView.setGravity(i);
        view = this.accurateTextView;
        if (!LocaleController.isRTL) {
            i3 = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i3 | 48, LocaleController.isRTL ? 16.0f : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
    }

    private ImageView getImageView() {
        return this.imageView;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0f), NUM));
    }

    public void setText(String title, String text) {
        this.titleTextView.setText(title);
        this.accurateTextView.setText(text);
    }
}
