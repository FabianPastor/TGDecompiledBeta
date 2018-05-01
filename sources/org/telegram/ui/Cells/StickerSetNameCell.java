package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

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

    public void setUrl(java.lang.CharSequence r6, int r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r5 = this;
        if (r6 == 0) goto L_0x0035;
    L_0x0002:
        r0 = new android.text.SpannableStringBuilder;
        r0.<init>(r6);
        r1 = 0;
        r2 = new org.telegram.ui.Components.ColorSpanUnderline;	 Catch:{ Exception -> 0x002a }
        r3 = "windowBackgroundWhiteBlueText4";	 Catch:{ Exception -> 0x002a }
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);	 Catch:{ Exception -> 0x002a }
        r2.<init>(r3);	 Catch:{ Exception -> 0x002a }
        r3 = 33;	 Catch:{ Exception -> 0x002a }
        r0.setSpan(r2, r1, r7, r3);	 Catch:{ Exception -> 0x002a }
        r2 = new org.telegram.ui.Components.ColorSpanUnderline;	 Catch:{ Exception -> 0x002a }
        r4 = "chat_emojiPanelStickerSetName";	 Catch:{ Exception -> 0x002a }
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);	 Catch:{ Exception -> 0x002a }
        r2.<init>(r4);	 Catch:{ Exception -> 0x002a }
        r6 = r6.length();	 Catch:{ Exception -> 0x002a }
        r0.setSpan(r2, r7, r6, r3);	 Catch:{ Exception -> 0x002a }
    L_0x002a:
        r6 = r5.urlTextView;
        r6.setText(r0);
        r6 = r5.urlTextView;
        r6.setVisibility(r1);
        goto L_0x003c;
    L_0x0035:
        r6 = r5.urlTextView;
        r7 = 8;
        r6.setVisibility(r7);
    L_0x003c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.StickerSetNameCell.setUrl(java.lang.CharSequence, int):void");
    }

    public void setText(CharSequence charSequence, int i) {
        setText(charSequence, i, 0, 0);
    }

    public void setText(java.lang.CharSequence r5, int r6, int r7, int r8) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r4 = this;
        r0 = 4;
        if (r5 != 0) goto L_0x0013;
    L_0x0003:
        r5 = 1;
        r4.empty = r5;
        r5 = r4.textView;
        r6 = "";
        r5.setText(r6);
        r5 = r4.buttonView;
        r5.setVisibility(r0);
        goto L_0x005d;
    L_0x0013:
        r1 = 0;
        if (r8 == 0) goto L_0x0032;
    L_0x0016:
        r2 = new android.text.SpannableStringBuilder;
        r2.<init>(r5);
        r5 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x002c }
        r3 = "windowBackgroundWhiteBlueText4";	 Catch:{ Exception -> 0x002c }
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);	 Catch:{ Exception -> 0x002c }
        r5.<init>(r3);	 Catch:{ Exception -> 0x002c }
        r8 = r8 + r7;	 Catch:{ Exception -> 0x002c }
        r3 = 33;	 Catch:{ Exception -> 0x002c }
        r2.setSpan(r5, r7, r8, r3);	 Catch:{ Exception -> 0x002c }
    L_0x002c:
        r5 = r4.textView;
        r5.setText(r2);
        goto L_0x004b;
    L_0x0032:
        r7 = r4.textView;
        r8 = r4.textView;
        r8 = r8.getPaint();
        r8 = r8.getFontMetricsInt();
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r5 = org.telegram.messenger.Emoji.replaceEmoji(r5, r8, r2, r1);
        r7.setText(r5);
    L_0x004b:
        if (r6 == 0) goto L_0x0058;
    L_0x004d:
        r5 = r4.buttonView;
        r5.setImageResource(r6);
        r5 = r4.buttonView;
        r5.setVisibility(r1);
        goto L_0x005d;
    L_0x0058:
        r5 = r4.buttonView;
        r5.setVisibility(r0);
    L_0x005d:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.StickerSetNameCell.setText(java.lang.CharSequence, int, int, int):void");
    }

    public void setOnIconClickListener(OnClickListener onClickListener) {
        this.buttonView.setOnClickListener(onClickListener);
    }

    public void invalidate() {
        this.textView.invalidate();
        super.invalidate();
    }

    protected void onMeasure(int i, int i2) {
        if (this.empty != 0) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(1, NUM));
        } else {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM));
        }
    }
}
