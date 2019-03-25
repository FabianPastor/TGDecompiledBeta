package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class StickerEmojiCell extends FrameLayout {
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private float alpha = 1.0f;
    private boolean changingAlpha;
    private int currentAccount = UserConfig.selectedAccount;
    private TextView emojiTextView;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private Object parentObject;
    private boolean recent;
    private float scale;
    private boolean scaled;
    private Document sticker;
    private long time;

    public StickerEmojiCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        addView(this.imageView, LayoutHelper.createFrame(66, 66, 17));
        this.emojiTextView = new TextView(context);
        this.emojiTextView.setTextSize(1, 16.0f);
        addView(this.emojiTextView, LayoutHelper.createFrame(28, 28, 85));
        setFocusable(true);
    }

    public Document getSticker() {
        return this.sticker;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public boolean isRecent() {
        return this.recent;
    }

    public void setRecent(boolean value) {
        this.recent = value;
    }

    public void setSticker(Document document, Object parent, boolean showEmoji) {
        setSticker(document, parent, null, showEmoji);
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x007e  */
    public void setSticker(org.telegram.tgnet.TLRPC.Document r12, java.lang.Object r13, java.lang.String r14, boolean r15) {
        /*
        r11 = this;
        r2 = 0;
        r10 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r9 = 0;
        if (r12 == 0) goto L_0x003d;
    L_0x0006:
        r11.sticker = r12;
        r11.parentObject = r13;
        r0 = r12.thumbs;
        r3 = 90;
        r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3);
        if (r1 == 0) goto L_0x001f;
    L_0x0014:
        r0 = r11.imageView;
        r3 = "webp";
        r5 = r11.parentObject;
        r4 = r2;
        r0.setImage(r1, r2, r3, r4, r5);
    L_0x001f:
        if (r14 == 0) goto L_0x003e;
    L_0x0021:
        r0 = r11.emojiTextView;
        r2 = r11.emojiTextView;
        r2 = r2.getPaint();
        r2 = r2.getFontMetricsInt();
        r3 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r2 = org.telegram.messenger.Emoji.replaceEmoji(r14, r2, r3, r9);
        r0.setText(r2);
        r0 = r11.emojiTextView;
        r0.setVisibility(r9);
    L_0x003d:
        return;
    L_0x003e:
        if (r15 == 0) goto L_0x00ac;
    L_0x0040:
        r8 = 0;
        r6 = 0;
    L_0x0042:
        r0 = r12.attributes;
        r0 = r0.size();
        if (r6 >= r0) goto L_0x007c;
    L_0x004a:
        r0 = r12.attributes;
        r7 = r0.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r7;
        r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
        if (r0 == 0) goto L_0x00a9;
    L_0x0056:
        r0 = r7.alt;
        if (r0 == 0) goto L_0x007c;
    L_0x005a:
        r0 = r7.alt;
        r0 = r0.length();
        if (r0 <= 0) goto L_0x007c;
    L_0x0062:
        r0 = r11.emojiTextView;
        r2 = r7.alt;
        r3 = r11.emojiTextView;
        r3 = r3.getPaint();
        r3 = r3.getFontMetricsInt();
        r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r4, r9);
        r0.setText(r2);
        r8 = 1;
    L_0x007c:
        if (r8 != 0) goto L_0x00a3;
    L_0x007e:
        r0 = r11.emojiTextView;
        r2 = r11.currentAccount;
        r2 = org.telegram.messenger.DataQuery.getInstance(r2);
        r3 = r11.sticker;
        r4 = r3.id;
        r2 = r2.getEmojiForSticker(r4);
        r3 = r11.emojiTextView;
        r3 = r3.getPaint();
        r3 = r3.getFontMetricsInt();
        r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r4, r9);
        r0.setText(r2);
    L_0x00a3:
        r0 = r11.emojiTextView;
        r0.setVisibility(r9);
        goto L_0x003d;
    L_0x00a9:
        r6 = r6 + 1;
        goto L_0x0042;
    L_0x00ac:
        r0 = r11.emojiTextView;
        r2 = 4;
        r0.setVisibility(r2);
        goto L_0x003d;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.StickerEmojiCell.setSticker(org.telegram.tgnet.TLRPC$Document, java.lang.Object, java.lang.String, boolean):void");
    }

    public void disable() {
        this.changingAlpha = true;
        this.alpha = 0.5f;
        this.time = 0;
        this.imageView.getImageReceiver().setAlpha(this.alpha);
        this.imageView.invalidate();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setScaled(boolean value) {
        this.scaled = value;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean isDisabled() {
        return this.changingAlpha;
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }

    public void invalidate() {
        this.emojiTextView.invalidate();
        super.invalidate();
    }

    /* Access modifiers changed, original: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.imageView && (this.changingAlpha || ((this.scaled && this.scale != 0.8f) || !(this.scaled || this.scale == 1.0f)))) {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (this.changingAlpha) {
                this.time += dt;
                if (this.time > 1050) {
                    this.time = 1050;
                }
                this.alpha = 0.5f + (interpolator.getInterpolation(((float) this.time) / 1050.0f) * 0.5f);
                if (this.alpha >= 1.0f) {
                    this.changingAlpha = false;
                    this.alpha = 1.0f;
                }
                this.imageView.getImageReceiver().setAlpha(this.alpha);
            } else if (!this.scaled || this.scale == 0.8f) {
                this.scale += ((float) dt) / 400.0f;
                if (this.scale > 1.0f) {
                    this.scale = 1.0f;
                }
            } else {
                this.scale -= ((float) dt) / 400.0f;
                if (this.scale < 0.8f) {
                    this.scale = 0.8f;
                }
            }
            this.imageView.setScaleX(this.scale);
            this.imageView.setScaleY(this.scale);
            this.imageView.invalidate();
            invalidate();
        }
        return result;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        String descr = LocaleController.getString("AttachSticker", NUM);
        for (int a = 0; a < this.sticker.attributes.size(); a++) {
            DocumentAttribute attribute = (DocumentAttribute) this.sticker.attributes.get(a);
            if (attribute instanceof TL_documentAttributeSticker) {
                if (attribute.alt != null && attribute.alt.length() > 0) {
                    this.emojiTextView.setText(Emoji.replaceEmoji(attribute.alt, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                    descr = attribute.alt + " " + descr;
                }
                info.setContentDescription(descr);
                info.setEnabled(true);
            }
        }
        info.setContentDescription(descr);
        info.setEnabled(true);
    }
}
