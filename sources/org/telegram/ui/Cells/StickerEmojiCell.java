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
        this.imageView.setLayerNum(1);
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

    public void setRecent(boolean z) {
        this.recent = z;
    }

    public void setSticker(Document document, Object obj, boolean z) {
        setSticker(document, obj, null, z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00c9  */
    public void setSticker(org.telegram.tgnet.TLRPC.Document r19, java.lang.Object r20, java.lang.String r21, boolean r22) {
        /*
        r18 = this;
        r0 = r18;
        r1 = r19;
        r2 = r21;
        if (r1 == 0) goto L_0x00fa;
    L_0x0008:
        r0.sticker = r1;
        r3 = r20;
        r0.parentObject = r3;
        r3 = r1.thumbs;
        r4 = 90;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4);
        r4 = org.telegram.messenger.MessageObject.canAutoplayAnimatedSticker(r19);
        if (r4 == 0) goto L_0x0045;
    L_0x001c:
        if (r3 == 0) goto L_0x0032;
    L_0x001e:
        r5 = r0.imageView;
        r6 = org.telegram.messenger.ImageLocation.getForDocument(r19);
        r8 = org.telegram.messenger.ImageLocation.getForDocument(r3, r1);
        r9 = 0;
        r10 = 0;
        r11 = r0.parentObject;
        r7 = "80_80";
        r5.setImage(r6, r7, r8, r9, r10, r11);
        goto L_0x0068;
    L_0x0032:
        r12 = r0.imageView;
        r13 = org.telegram.messenger.ImageLocation.getForDocument(r19);
        r15 = 0;
        r16 = 0;
        r3 = r0.parentObject;
        r14 = "80_80";
        r17 = r3;
        r12.setImage(r13, r14, r15, r16, r17);
        goto L_0x0068;
    L_0x0045:
        if (r3 == 0) goto L_0x0058;
    L_0x0047:
        r4 = r0.imageView;
        r5 = org.telegram.messenger.ImageLocation.getForDocument(r3, r1);
        r6 = 0;
        r8 = 0;
        r9 = r0.parentObject;
        r7 = "webp";
        r4.setImage(r5, r6, r7, r8, r9);
        goto L_0x0068;
    L_0x0058:
        r10 = r0.imageView;
        r11 = org.telegram.messenger.ImageLocation.getForDocument(r19);
        r12 = 0;
        r14 = 0;
        r15 = r0.parentObject;
        r13 = "webp";
        r10.setImage(r11, r12, r13, r14, r15);
    L_0x0068:
        r3 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4 = 0;
        if (r2 == 0) goto L_0x0089;
    L_0x006d:
        r1 = r0.emojiTextView;
        r5 = r1.getPaint();
        r5 = r5.getFontMetricsInt();
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r5, r3, r4);
        r1.setText(r2);
        r1 = r0.emojiTextView;
        r1.setVisibility(r4);
        goto L_0x00fa;
    L_0x0089:
        if (r22 == 0) goto L_0x00f4;
    L_0x008b:
        r2 = 0;
    L_0x008c:
        r5 = r1.attributes;
        r5 = r5.size();
        if (r2 >= r5) goto L_0x00c6;
    L_0x0094:
        r5 = r1.attributes;
        r5 = r5.get(r2);
        r5 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r5;
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
        if (r6 == 0) goto L_0x00c3;
    L_0x00a0:
        r1 = r5.alt;
        if (r1 == 0) goto L_0x00c6;
    L_0x00a4:
        r1 = r1.length();
        if (r1 <= 0) goto L_0x00c6;
    L_0x00aa:
        r1 = r0.emojiTextView;
        r2 = r5.alt;
        r5 = r1.getPaint();
        r5 = r5.getFontMetricsInt();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r5, r6, r4);
        r1.setText(r2);
        r1 = 1;
        goto L_0x00c7;
    L_0x00c3:
        r2 = r2 + 1;
        goto L_0x008c;
    L_0x00c6:
        r1 = 0;
    L_0x00c7:
        if (r1 != 0) goto L_0x00ee;
    L_0x00c9:
        r1 = r0.emojiTextView;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MediaDataController.getInstance(r2);
        r5 = r0.sticker;
        r5 = r5.id;
        r2 = r2.getEmojiForSticker(r5);
        r5 = r0.emojiTextView;
        r5 = r5.getPaint();
        r5 = r5.getFontMetricsInt();
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r5, r3, r4);
        r1.setText(r2);
    L_0x00ee:
        r1 = r0.emojiTextView;
        r1.setVisibility(r4);
        goto L_0x00fa;
    L_0x00f4:
        r1 = r0.emojiTextView;
        r2 = 4;
        r1.setVisibility(r2);
    L_0x00fa:
        return;
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

    public void setScaled(boolean z) {
        this.scaled = z;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean isDisabled() {
        return this.changingAlpha;
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public void invalidate() {
        this.emojiTextView.invalidate();
        super.invalidate();
    }

    /* Access modifiers changed, original: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && (this.changingAlpha || ((this.scaled && this.scale != 0.8f) || !(this.scaled || this.scale == 1.0f)))) {
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (this.changingAlpha) {
                this.time += j2;
                if (this.time > 1050) {
                    this.time = 1050;
                }
                this.alpha = (interpolator.getInterpolation(((float) this.time) / 1050.0f) * 0.5f) + 0.5f;
                if (this.alpha >= 1.0f) {
                    this.changingAlpha = false;
                    this.alpha = 1.0f;
                }
                this.imageView.getImageReceiver().setAlpha(this.alpha);
            } else {
                if (this.scaled) {
                    float f = this.scale;
                    if (f != 0.8f) {
                        this.scale = f - (((float) j2) / 400.0f);
                        if (this.scale < 0.8f) {
                            this.scale = 0.8f;
                        }
                    }
                }
                this.scale += ((float) j2) / 400.0f;
                if (this.scale > 1.0f) {
                    this.scale = 1.0f;
                }
            }
            this.imageView.setScaleX(this.scale);
            this.imageView.setScaleY(this.scale);
            this.imageView.invalidate();
            invalidate();
        }
        return drawChild;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        CharSequence string = LocaleController.getString("AttachSticker", NUM);
        for (int i = 0; i < this.sticker.attributes.size(); i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) this.sticker.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                String str = documentAttribute.alt;
                if (str != null && str.length() > 0) {
                    TextView textView = this.emojiTextView;
                    textView.setText(Emoji.replaceEmoji(documentAttribute.alt, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(documentAttribute.alt);
                    stringBuilder.append(" ");
                    stringBuilder.append(string);
                    string = stringBuilder.toString();
                }
                accessibilityNodeInfo.setContentDescription(string);
                accessibilityNodeInfo.setEnabled(true);
            }
        }
        accessibilityNodeInfo.setContentDescription(string);
        accessibilityNodeInfo.setEnabled(true);
    }
}
