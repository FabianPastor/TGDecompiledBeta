package org.telegram.p005ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.ColorSpanUnderline;
import org.telegram.p005ui.Components.LayoutHelper;

/* renamed from: org.telegram.ui.Cells.StickerSetNameCell */
public class StickerSetNameCell extends FrameLayout {
    private ImageView buttonView;
    private boolean empty;
    private TextView textView;
    private TextView urlTextView;

    public StickerSetNameCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelStickerSetName));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setSingleLine(true);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 17.0f, 4.0f, 57.0f, 0.0f));
        this.urlTextView = new TextView(context);
        this.urlTextView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelStickerSetName));
        this.urlTextView.setTextSize(1, 12.0f);
        this.urlTextView.setEllipsize(TruncateAt.END);
        this.urlTextView.setSingleLine(true);
        this.urlTextView.setVisibility(4);
        addView(this.urlTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 17.0f, 6.0f, 17.0f, 0.0f));
        this.buttonView = new ImageView(context);
        this.buttonView.setScaleType(ScaleType.CENTER);
        this.buttonView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelStickerSetNameIcon), Mode.MULTIPLY));
        addView(this.buttonView, LayoutHelper.createFrame(24, 24.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
    }

    public void setUrl(CharSequence text, int searchLength) {
        if (text != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            try {
                builder.setSpan(new ColorSpanUnderline(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), 0, searchLength, 33);
                builder.setSpan(new ColorSpanUnderline(Theme.getColor(Theme.key_chat_emojiPanelStickerSetName)), searchLength, text.length(), 33);
            } catch (Exception e) {
            }
            this.urlTextView.setText(builder);
            this.urlTextView.setVisibility(0);
            return;
        }
        this.urlTextView.setVisibility(8);
    }

    public void setText(CharSequence text, int resId) {
        setText(text, resId, 0, 0);
    }

    public void setText(CharSequence text, int resId, int index, int searchLength) {
        if (text == null) {
            this.empty = true;
            this.textView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.buttonView.setVisibility(4);
            return;
        }
        if (searchLength != 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            try {
                builder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), index, index + searchLength, 33);
            } catch (Exception e) {
            }
            this.textView.setText(builder);
        } else {
            this.textView.setText(Emoji.replaceEmoji(text, this.textView.getPaint().getFontMetricsInt(), AndroidUtilities.m10dp(14.0f), false));
        }
        if (resId != 0) {
            this.buttonView.setImageResource(resId);
            this.buttonView.setVisibility(0);
            return;
        }
        this.buttonView.setVisibility(4);
    }

    public void setOnIconClickListener(OnClickListener onIconClickListener) {
        this.buttonView.setOnClickListener(onIconClickListener);
    }

    public void invalidate() {
        this.textView.invalidate();
        super.invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.empty) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(1, NUM));
        } else {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(24.0f), NUM));
        }
    }
}
