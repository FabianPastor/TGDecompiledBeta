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
    private TextView emojiTextView;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private Object parentObject;
    private boolean recent;
    private float scale;
    private boolean scaled;
    private TLRPC$Document sticker;
    private long time;

    public StickerEmojiCell(Context context) {
        super(context);
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
        setSticker(tLRPC$Document, obj, (String) null, z);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00c4, code lost:
        r1 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSticker(org.telegram.tgnet.TLRPC$Document r19, java.lang.Object r20, java.lang.String r21, boolean r22) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r21
            if (r1 == 0) goto L_0x00f8
            r0.sticker = r1
            r3 = r20
            r0.parentObject = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r1.thumbs
            r4 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4)
            boolean r4 = org.telegram.messenger.MessageObject.canAutoplayAnimatedSticker(r19)
            if (r4 == 0) goto L_0x0045
            if (r3 == 0) goto L_0x0032
            org.telegram.ui.Components.BackupImageView r5 = r0.imageView
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForDocument(r19)
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Document) r1)
            r9 = 0
            r10 = 0
            java.lang.Object r11 = r0.parentObject
            java.lang.String r7 = "80_80"
            r5.setImage((org.telegram.messenger.ImageLocation) r6, (java.lang.String) r7, (org.telegram.messenger.ImageLocation) r8, (java.lang.String) r9, (int) r10, (java.lang.Object) r11)
            goto L_0x0066
        L_0x0032:
            org.telegram.ui.Components.BackupImageView r12 = r0.imageView
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForDocument(r19)
            r15 = 0
            r16 = 0
            java.lang.Object r3 = r0.parentObject
            java.lang.String r14 = "80_80"
            r17 = r3
            r12.setImage(r13, r14, r15, r16, r17)
            goto L_0x0066
        L_0x0045:
            if (r3 == 0) goto L_0x0057
            org.telegram.ui.Components.BackupImageView r4 = r0.imageView
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Document) r1)
            r6 = 0
            r8 = 0
            java.lang.Object r9 = r0.parentObject
            java.lang.String r7 = "webp"
            r4.setImage(r5, r6, r7, r8, r9)
            goto L_0x0066
        L_0x0057:
            org.telegram.ui.Components.BackupImageView r10 = r0.imageView
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForDocument(r19)
            r12 = 0
            r14 = 0
            java.lang.Object r15 = r0.parentObject
            java.lang.String r13 = "webp"
            r10.setImage(r11, r12, r13, r14, r15)
        L_0x0066:
            r3 = 1098907648(0x41800000, float:16.0)
            r4 = 0
            if (r2 == 0) goto L_0x0087
            android.widget.TextView r1 = r0.emojiTextView
            android.text.TextPaint r5 = r1.getPaint()
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r5, r3, r4)
            r1.setText(r2)
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r4)
            goto L_0x00f8
        L_0x0087:
            if (r22 == 0) goto L_0x00f2
            r2 = 0
        L_0x008a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r1.attributes
            int r5 = r5.size()
            if (r2 >= r5) goto L_0x00c4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r1.attributes
            java.lang.Object r5 = r5.get(r2)
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r5
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            if (r6 == 0) goto L_0x00c1
            java.lang.String r1 = r5.alt
            if (r1 == 0) goto L_0x00c4
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x00c4
            android.widget.TextView r1 = r0.emojiTextView
            java.lang.String r2 = r5.alt
            android.text.TextPaint r5 = r1.getPaint()
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r5, r6, r4)
            r1.setText(r2)
            r1 = 1
            goto L_0x00c5
        L_0x00c1:
            int r2 = r2 + 1
            goto L_0x008a
        L_0x00c4:
            r1 = 0
        L_0x00c5:
            if (r1 != 0) goto L_0x00ec
            android.widget.TextView r1 = r0.emojiTextView
            int r2 = r0.currentAccount
            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r2)
            org.telegram.tgnet.TLRPC$Document r5 = r0.sticker
            long r5 = r5.id
            java.lang.String r2 = r2.getEmojiForSticker(r5)
            android.widget.TextView r5 = r0.emojiTextView
            android.text.TextPaint r5 = r5.getPaint()
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r5, r3, r4)
            r1.setText(r2)
        L_0x00ec:
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r4)
            goto L_0x00f8
        L_0x00f2:
            android.widget.TextView r1 = r0.emojiTextView
            r2 = 4
            r1.setVisibility(r2)
        L_0x00f8:
            return
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

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && (this.changingAlpha || ((this.scaled && this.scale != 0.8f) || (!this.scaled && this.scale != 1.0f)))) {
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
        accessibilityNodeInfo.setContentDescription(string);
        accessibilityNodeInfo.setEnabled(true);
    }
}
