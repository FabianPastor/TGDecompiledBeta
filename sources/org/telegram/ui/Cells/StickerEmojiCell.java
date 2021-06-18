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
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class StickerEmojiCell extends FrameLayout {
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private float alpha = 1.0f;
    private boolean changingAlpha;
    private int currentAccount = UserConfig.selectedAccount;
    private String currentEmoji;
    private TextView emojiTextView;
    private boolean fromEmojiPanel;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private Object parentObject;
    private boolean recent;
    private float scale;
    private boolean scaled;
    private TLRPC$Document sticker;
    private String stickerPath;
    private long time;

    public StickerEmojiCell(Context context, boolean z) {
        super(context);
        this.fromEmojiPanel = z;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(66, 66, 17));
        TextView textView = new TextView(context);
        this.emojiTextView = textView;
        textView.setTextSize(1, 16.0f);
        addView(this.emojiTextView, LayoutHelper.createFrame(28, 28, 85));
        setFocusable(true);
    }

    public TLRPC$Document getSticker() {
        return this.sticker;
    }

    public String getStickerPath() {
        return this.stickerPath;
    }

    public String getEmoji() {
        return this.currentEmoji;
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

    public void setSticker(TLRPC$Document tLRPC$Document, Object obj, boolean z) {
        setSticker(tLRPC$Document, (String) null, obj, (String) null, z);
    }

    public void setSticker(String str, String str2) {
        setSticker((TLRPC$Document) null, str, (Object) null, str2, str2 != null);
    }

    public MessageObject.SendAnimationData getSendAnimationData() {
        ImageReceiver imageReceiver = this.imageView.getImageReceiver();
        if (!imageReceiver.hasNotThumb()) {
            return null;
        }
        MessageObject.SendAnimationData sendAnimationData = new MessageObject.SendAnimationData();
        int[] iArr = new int[2];
        this.imageView.getLocationInWindow(iArr);
        sendAnimationData.x = imageReceiver.getCenterX() + ((float) iArr[0]);
        sendAnimationData.y = imageReceiver.getCenterY() + ((float) iArr[1]);
        sendAnimationData.width = imageReceiver.getImageWidth();
        sendAnimationData.height = imageReceiver.getImageHeight();
        return sendAnimationData;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0147, code lost:
        r1 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSticker(org.telegram.tgnet.TLRPC$Document r23, java.lang.String r24, java.lang.Object r25, java.lang.String r26, boolean r27) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r2 = r24
            r3 = r26
            r0.currentEmoji = r3
            r4 = 4
            r5 = 1098907648(0x41800000, float:16.0)
            r6 = 0
            if (r2 == 0) goto L_0x003f
            r0.stickerPath = r2
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r7 = 0
            java.lang.String r8 = "80_80"
            r1.setImage(r2, r8, r7)
            if (r3 == 0) goto L_0x0038
            android.widget.TextView r1 = r0.emojiTextView
            android.text.TextPaint r2 = r1.getPaint()
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r3, r2, r4, r6)
            r1.setText(r2)
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r6)
            goto L_0x017a
        L_0x0038:
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r4)
            goto L_0x017a
        L_0x003f:
            if (r1 == 0) goto L_0x017a
            r0.sticker = r1
            r2 = r25
            r0.parentObject = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r1.thumbs
            r7 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r7)
            boolean r7 = r0.fromEmojiPanel
            if (r7 == 0) goto L_0x0056
            java.lang.String r8 = "emptyListPlaceholder"
            goto L_0x0058
        L_0x0056:
            java.lang.String r8 = "windowBackgroundGray"
        L_0x0058:
            if (r7 == 0) goto L_0x005e
            r7 = 1045220557(0x3e4ccccd, float:0.2)
            goto L_0x0060
        L_0x005e:
            r7 = 1065353216(0x3var_, float:1.0)
        L_0x0060:
            org.telegram.messenger.SvgHelper$SvgDrawable r13 = org.telegram.messenger.DocumentObject.getSvgThumb((org.telegram.tgnet.TLRPC$Document) r1, (java.lang.String) r8, (float) r7)
            boolean r7 = org.telegram.messenger.MessageObject.canAutoplayAnimatedSticker(r23)
            if (r7 == 0) goto L_0x00a6
            if (r13 == 0) goto L_0x007c
            org.telegram.ui.Components.BackupImageView r9 = r0.imageView
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r23)
            r12 = 0
            java.lang.Object r14 = r0.parentObject
            java.lang.String r11 = "80_80"
            r9.setImage((org.telegram.messenger.ImageLocation) r10, (java.lang.String) r11, (java.lang.String) r12, (android.graphics.drawable.Drawable) r13, (java.lang.Object) r14)
            goto L_0x00ed
        L_0x007c:
            if (r2 == 0) goto L_0x0096
            org.telegram.ui.Components.BackupImageView r15 = r0.imageView
            org.telegram.messenger.ImageLocation r16 = org.telegram.messenger.ImageLocation.getForDocument(r23)
            org.telegram.messenger.ImageLocation r18 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r1)
            r19 = 0
            r20 = 0
            java.lang.Object r2 = r0.parentObject
            java.lang.String r17 = "80_80"
            r21 = r2
            r15.setImage((org.telegram.messenger.ImageLocation) r16, (java.lang.String) r17, (org.telegram.messenger.ImageLocation) r18, (java.lang.String) r19, (int) r20, (java.lang.Object) r21)
            goto L_0x00ed
        L_0x0096:
            org.telegram.ui.Components.BackupImageView r7 = r0.imageView
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForDocument(r23)
            r10 = 0
            r11 = 0
            java.lang.Object r12 = r0.parentObject
            java.lang.String r9 = "80_80"
            r7.setImage((org.telegram.messenger.ImageLocation) r8, (java.lang.String) r9, (java.lang.String) r10, (android.graphics.drawable.Drawable) r11, (java.lang.Object) r12)
            goto L_0x00ed
        L_0x00a6:
            if (r13 == 0) goto L_0x00c8
            if (r2 == 0) goto L_0x00b9
            org.telegram.ui.Components.BackupImageView r9 = r0.imageView
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r1)
            r11 = 0
            java.lang.Object r14 = r0.parentObject
            java.lang.String r12 = "webp"
            r9.setImage((org.telegram.messenger.ImageLocation) r10, (java.lang.String) r11, (java.lang.String) r12, (android.graphics.drawable.Drawable) r13, (java.lang.Object) r14)
            goto L_0x00ed
        L_0x00b9:
            org.telegram.ui.Components.BackupImageView r9 = r0.imageView
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r23)
            r11 = 0
            java.lang.Object r14 = r0.parentObject
            java.lang.String r12 = "webp"
            r9.setImage((org.telegram.messenger.ImageLocation) r10, (java.lang.String) r11, (java.lang.String) r12, (android.graphics.drawable.Drawable) r13, (java.lang.Object) r14)
            goto L_0x00ed
        L_0x00c8:
            if (r2 == 0) goto L_0x00de
            org.telegram.ui.Components.BackupImageView r15 = r0.imageView
            org.telegram.messenger.ImageLocation r16 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r1)
            r17 = 0
            r19 = 0
            java.lang.Object r2 = r0.parentObject
            java.lang.String r18 = "webp"
            r20 = r2
            r15.setImage((org.telegram.messenger.ImageLocation) r16, (java.lang.String) r17, (java.lang.String) r18, (android.graphics.drawable.Drawable) r19, (java.lang.Object) r20)
            goto L_0x00ed
        L_0x00de:
            org.telegram.ui.Components.BackupImageView r7 = r0.imageView
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForDocument(r23)
            r9 = 0
            r11 = 0
            java.lang.Object r12 = r0.parentObject
            java.lang.String r10 = "webp"
            r7.setImage((org.telegram.messenger.ImageLocation) r8, (java.lang.String) r9, (java.lang.String) r10, (android.graphics.drawable.Drawable) r11, (java.lang.Object) r12)
        L_0x00ed:
            if (r3 == 0) goto L_0x010a
            android.widget.TextView r1 = r0.emojiTextView
            android.text.TextPaint r2 = r1.getPaint()
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r3, r2, r4, r6)
            r1.setText(r2)
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r6)
            goto L_0x017a
        L_0x010a:
            if (r27 == 0) goto L_0x0175
            r2 = 0
        L_0x010d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0147
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r3
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            if (r4 == 0) goto L_0x0144
            java.lang.String r1 = r3.alt
            if (r1 == 0) goto L_0x0147
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0147
            android.widget.TextView r1 = r0.emojiTextView
            java.lang.String r2 = r3.alt
            android.text.TextPaint r3 = r1.getPaint()
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r4, r6)
            r1.setText(r2)
            r1 = 1
            goto L_0x0148
        L_0x0144:
            int r2 = r2 + 1
            goto L_0x010d
        L_0x0147:
            r1 = 0
        L_0x0148:
            if (r1 != 0) goto L_0x016f
            android.widget.TextView r1 = r0.emojiTextView
            int r2 = r0.currentAccount
            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r2)
            org.telegram.tgnet.TLRPC$Document r3 = r0.sticker
            long r3 = r3.id
            java.lang.String r2 = r2.getEmojiForSticker(r3)
            android.widget.TextView r3 = r0.emojiTextView
            android.text.TextPaint r3 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r4, r6)
            r1.setText(r2)
        L_0x016f:
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r6)
            goto L_0x017a
        L_0x0175:
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r4)
        L_0x017a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.StickerEmojiCell.setSticker(org.telegram.tgnet.TLRPC$Document, java.lang.String, java.lang.Object, java.lang.String, boolean):void");
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

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean z;
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && (this.changingAlpha || ((z && this.scale != 0.8f) || (!(z = this.scaled) && this.scale != 1.0f)))) {
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (this.changingAlpha) {
                long j3 = this.time + j2;
                this.time = j3;
                if (j3 > 1050) {
                    this.time = 1050;
                }
                float interpolation = (interpolator.getInterpolation(((float) this.time) / 1050.0f) * 0.5f) + 0.5f;
                this.alpha = interpolation;
                if (interpolation >= 1.0f) {
                    this.changingAlpha = false;
                    this.alpha = 1.0f;
                }
                this.imageView.getImageReceiver().setAlpha(this.alpha);
            } else {
                if (this.scaled) {
                    float f = this.scale;
                    if (f != 0.8f) {
                        float f2 = f - (((float) j2) / 400.0f);
                        this.scale = f2;
                        if (f2 < 0.8f) {
                            this.scale = 0.8f;
                        }
                    }
                }
                float f3 = this.scale + (((float) j2) / 400.0f);
                this.scale = f3;
                if (f3 > 1.0f) {
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
        String string = LocaleController.getString("AttachSticker", NUM);
        if (this.sticker != null) {
            int i = 0;
            while (true) {
                if (i >= this.sticker.attributes.size()) {
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.sticker.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                    String str = tLRPC$DocumentAttribute.alt;
                    if (str != null && str.length() > 0) {
                        TextView textView = this.emojiTextView;
                        textView.setText(Emoji.replaceEmoji(tLRPC$DocumentAttribute.alt, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                        string = tLRPC$DocumentAttribute.alt + " " + string;
                    }
                } else {
                    i++;
                }
            }
        }
        accessibilityNodeInfo.setContentDescription(string);
        accessibilityNodeInfo.setEnabled(true);
    }
}
