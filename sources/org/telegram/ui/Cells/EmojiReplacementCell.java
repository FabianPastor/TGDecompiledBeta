package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class EmojiReplacementCell extends FrameLayout {
    private String emoji;
    private ImageView imageView;

    public EmojiReplacementCell(Context context) {
        super(context);
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        addView(this.imageView, LayoutHelper.createFrame(42, 42.0f, 1, 0.0f, 5.0f, 0.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec((AndroidUtilities.dp(52.0f) + getPaddingLeft()) + getPaddingRight(), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), NUM));
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
            background.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_stickersHintPanel"), Mode.MULTIPLY));
        }
    }

    public String getEmoji() {
        return this.emoji;
    }

    public void invalidate() {
        super.invalidate();
        this.imageView.invalidate();
    }
}
