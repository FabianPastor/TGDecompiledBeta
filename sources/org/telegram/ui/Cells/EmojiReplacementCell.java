package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class EmojiReplacementCell extends FrameLayout {
    private String emoji;
    private ImageView imageView;
    private final Theme.ResourcesProvider resourcesProvider;

    public EmojiReplacementCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.imageView, LayoutHelper.createFrame(42, 42.0f, 1, 0.0f, 5.0f, 0.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(52.0f) + getPaddingLeft() + getPaddingRight(), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), NUM));
    }

    public void setEmoji(String str, int i) {
        this.emoji = str;
        this.imageView.setImageDrawable(Emoji.getEmojiBigDrawable(str));
        if (i == -1) {
            setBackgroundResource(NUM);
            setPadding(AndroidUtilities.dp(7.0f), 0, 0, 0);
        } else if (i == 0) {
            setBackgroundResource(NUM);
            setPadding(0, 0, 0, 0);
        } else if (i == 1) {
            setBackgroundResource(NUM);
            setPadding(0, 0, AndroidUtilities.dp(7.0f), 0);
        } else if (i == 2) {
            setBackgroundResource(NUM);
            setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        }
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(230);
            background.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_stickersHintPanel"), PorterDuff.Mode.MULTIPLY));
        }
    }

    public String getEmoji() {
        return this.emoji;
    }

    public void invalidate() {
        super.invalidate();
        this.imageView.invalidate();
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
