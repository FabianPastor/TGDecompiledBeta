package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColorSpanUnderline;
import org.telegram.ui.Components.LayoutHelper;

public class StickerSetNameCell extends FrameLayout {
    private ImageView buttonView;
    private boolean empty;
    private boolean isEmoji;
    private TextView textView;
    private TextView urlTextView;

    public StickerSetNameCell(Context context, boolean z) {
        this(context, z, false);
    }

    public StickerSetNameCell(Context context, boolean z, boolean z2) {
        super(context);
        FrameLayout.LayoutParams layoutParams;
        FrameLayout.LayoutParams layoutParams2;
        FrameLayout.LayoutParams layoutParams3;
        this.isEmoji = z;
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("chat_emojiPanelStickerSetName"));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setSingleLine(true);
        if (z2) {
            layoutParams = LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388659, z ? 15.0f : 17.0f, 4.0f, 57.0f, 0.0f);
        } else {
            layoutParams = LayoutHelper.createFrame(-2, -2.0f, 51, z ? 15.0f : 17.0f, 4.0f, 57.0f, 0.0f);
        }
        addView(this.textView, layoutParams);
        TextView textView3 = new TextView(context);
        this.urlTextView = textView3;
        textView3.setTextColor(Theme.getColor("chat_emojiPanelStickerSetName"));
        this.urlTextView.setTextSize(1, 12.0f);
        this.urlTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.urlTextView.setSingleLine(true);
        this.urlTextView.setVisibility(4);
        if (z2) {
            layoutParams2 = LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388661, 17.0f, 6.0f, 17.0f, 0.0f);
        } else {
            layoutParams2 = LayoutHelper.createFrame(-2, -2.0f, 53, 17.0f, 6.0f, 17.0f, 0.0f);
        }
        addView(this.urlTextView, layoutParams2);
        ImageView imageView = new ImageView(context);
        this.buttonView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.buttonView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelStickerSetNameIcon"), PorterDuff.Mode.MULTIPLY));
        if (z2) {
            layoutParams3 = LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388661, 0.0f, 0.0f, 16.0f, 0.0f);
        } else {
            layoutParams3 = LayoutHelper.createFrame(24, 24.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f);
        }
        addView(this.buttonView, layoutParams3);
    }

    public void setUrl(CharSequence charSequence, int i) {
        if (charSequence != null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
            try {
                spannableStringBuilder.setSpan(new ColorSpanUnderline(Theme.getColor("chat_emojiPanelStickerSetNameHighlight")), 0, i, 33);
                spannableStringBuilder.setSpan(new ColorSpanUnderline(Theme.getColor("chat_emojiPanelStickerSetName")), i, charSequence.length(), 33);
            } catch (Exception unused) {
            }
            this.urlTextView.setText(spannableStringBuilder);
            this.urlTextView.setVisibility(0);
            return;
        }
        this.urlTextView.setVisibility(8);
    }

    public void setText(CharSequence charSequence, int i) {
        setText(charSequence, i, 0, 0);
    }

    public void setText(CharSequence charSequence, int i, int i2, int i3) {
        if (charSequence == null) {
            this.empty = true;
            this.textView.setText("");
            this.buttonView.setVisibility(4);
            return;
        }
        if (i3 != 0) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
            try {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("chat_emojiPanelStickerSetNameHighlight")), i2, i3 + i2, 33);
            } catch (Exception unused) {
            }
            this.textView.setText(spannableStringBuilder);
        } else {
            TextView textView2 = this.textView;
            textView2.setText(Emoji.replaceEmoji(charSequence, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
        }
        if (i != 0) {
            this.buttonView.setImageResource(i);
            this.buttonView.setVisibility(0);
            return;
        }
        this.buttonView.setVisibility(4);
    }

    public void setOnIconClickListener(View.OnClickListener onClickListener) {
        this.buttonView.setOnClickListener(onClickListener);
    }

    public void invalidate() {
        this.textView.invalidate();
        super.invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.empty) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(1, NUM));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.isEmoji ? 28.0f : 24.0f), NUM));
        }
    }

    /* access modifiers changed from: protected */
    public void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
        if (view == this.urlTextView) {
            i2 += this.textView.getMeasuredWidth() + AndroidUtilities.dp(16.0f);
        }
        super.measureChildWithMargins(view, i, i2, i3, i4);
    }
}
