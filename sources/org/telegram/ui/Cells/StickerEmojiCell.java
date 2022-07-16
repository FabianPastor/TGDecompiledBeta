package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.RecyclerListViewWithOverlayDraw;
import org.telegram.ui.Components.Premium.PremiumLockIconView;

public class StickerEmojiCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, RecyclerListViewWithOverlayDraw.OverlayView {
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private float alpha = 1.0f;
    private boolean changingAlpha;
    private int currentAccount = UserConfig.selectedAccount;
    private String currentEmoji;
    private boolean drawInParentView;
    private TextView emojiTextView;
    private boolean fromEmojiPanel;
    private ImageReceiver imageView;
    private boolean isPremiumSticker;
    private long lastUpdateTime;
    private Object parentObject;
    private float premiumAlpha = 1.0f;
    private PremiumLockIconView premiumIconView;
    private boolean recent;
    private float scale;
    private boolean scaled;
    private boolean showPremiumLock;
    private TLRPC$Document sticker;
    private SendMessagesHelper.ImportingSticker stickerPath;
    private long time;

    public StickerEmojiCell(Context context, boolean z) {
        super(context);
        this.fromEmojiPanel = z;
        ImageReceiver imageReceiver = new ImageReceiver();
        this.imageView = imageReceiver;
        imageReceiver.setAspectFit(true);
        this.imageView.setLayerNum(1);
        TextView textView = new TextView(context);
        this.emojiTextView = textView;
        textView.setTextSize(1, 16.0f);
        new Paint(1).setColor(Theme.getColor("featuredStickers_addButton"));
        PremiumLockIconView premiumLockIconView = new PremiumLockIconView(context, PremiumLockIconView.TYPE_STICKERS_PREMIUM_LOCKED);
        this.premiumIconView = premiumLockIconView;
        premiumLockIconView.setImageReceiver(this.imageView);
        this.premiumIconView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        this.premiumIconView.setImageReceiver(this.imageView);
        addView(this.premiumIconView, LayoutHelper.createFrame(24, 24.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        setFocusable(true);
    }

    public TLRPC$Document getSticker() {
        return this.sticker;
    }

    public SendMessagesHelper.ImportingSticker getStickerPath() {
        SendMessagesHelper.ImportingSticker importingSticker = this.stickerPath;
        if (importingSticker == null || !importingSticker.validated) {
            return null;
        }
        return importingSticker;
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
        setSticker(tLRPC$Document, (SendMessagesHelper.ImportingSticker) null, obj, (String) null, z);
    }

    public void setSticker(SendMessagesHelper.ImportingSticker importingSticker) {
        String str = importingSticker.emoji;
        setSticker((TLRPC$Document) null, importingSticker, (Object) null, str, str != null);
    }

    public MessageObject.SendAnimationData getSendAnimationData() {
        ImageReceiver imageReceiver = this.imageView;
        if (!imageReceiver.hasNotThumb()) {
            return null;
        }
        MessageObject.SendAnimationData sendAnimationData = new MessageObject.SendAnimationData();
        int[] iArr = new int[2];
        getLocationInWindow(iArr);
        sendAnimationData.x = imageReceiver.getCenterX() + ((float) iArr[0]);
        sendAnimationData.y = imageReceiver.getCenterY() + ((float) iArr[1]);
        sendAnimationData.width = imageReceiver.getImageWidth();
        sendAnimationData.height = imageReceiver.getImageHeight();
        return sendAnimationData;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:62:0x01e4, code lost:
        r7 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSticker(org.telegram.tgnet.TLRPC$Document r32, org.telegram.messenger.SendMessagesHelper.ImportingSticker r33, java.lang.Object r34, java.lang.String r35, boolean r36) {
        /*
            r31 = this;
            r0 = r31
            r1 = r32
            r2 = r33
            r3 = r35
            r0.currentEmoji = r3
            boolean r4 = org.telegram.messenger.MessageObject.isPremiumSticker(r32)
            r0.isPremiumSticker = r4
            r5 = 0
            java.lang.Integer r15 = java.lang.Integer.valueOf(r5)
            r0.drawInParentView = r5
            if (r4 == 0) goto L_0x0029
            org.telegram.ui.Components.Premium.PremiumLockIconView r4 = r0.premiumIconView
            java.lang.String r6 = "windowBackgroundWhite"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setColor(r6)
            org.telegram.ui.Components.Premium.PremiumLockIconView r4 = r0.premiumIconView
            r4.setWaitingImage()
        L_0x0029:
            r4 = 4
            r6 = 1065353216(0x3var_, float:1.0)
            r17 = 1098907648(0x41800000, float:16.0)
            if (r2 == 0) goto L_0x00a7
            r0.stickerPath = r2
            boolean r1 = r2.validated
            java.lang.String r7 = "tgs"
            r8 = 0
            java.lang.String r9 = "dialogBackgroundGray"
            if (r1 == 0) goto L_0x0061
            org.telegram.messenger.ImageReceiver r1 = r0.imageView
            java.lang.String r10 = r2.path
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForPath(r10)
            r11 = 0
            r12 = 0
            org.telegram.messenger.SvgHelper$SvgDrawable r13 = org.telegram.messenger.DocumentObject.getSvgRectThumb(r9, r6)
            r18 = 0
            boolean r2 = r2.animated
            if (r2 == 0) goto L_0x0051
            r14 = r7
            goto L_0x0052
        L_0x0051:
            r14 = r8
        L_0x0052:
            r16 = 1
            java.lang.String r8 = "80_80"
            r6 = r1
            r7 = r10
            r9 = r11
            r10 = r12
            r11 = r13
            r12 = r18
            r6.setImage(r7, r8, r9, r10, r11, r12, r14, r15, r16)
            goto L_0x0082
        L_0x0061:
            org.telegram.messenger.ImageReceiver r1 = r0.imageView
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            org.telegram.messenger.SvgHelper$SvgDrawable r14 = org.telegram.messenger.DocumentObject.getSvgRectThumb(r9, r6)
            r18 = 0
            boolean r2 = r2.animated
            if (r2 == 0) goto L_0x0073
            r2 = r7
            goto L_0x0074
        L_0x0073:
            r2 = r8
        L_0x0074:
            r16 = 1
            r6 = r1
            r7 = r10
            r8 = r11
            r9 = r12
            r10 = r13
            r11 = r14
            r12 = r18
            r14 = r2
            r6.setImage(r7, r8, r9, r10, r11, r12, r14, r15, r16)
        L_0x0082:
            if (r3 == 0) goto L_0x00a0
            android.widget.TextView r1 = r0.emojiTextView
            android.text.TextPaint r2 = r1.getPaint()
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r3, r2, r4, r5)
            r1.setText(r2)
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r5)
            goto L_0x0217
        L_0x00a0:
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r4)
            goto L_0x0217
        L_0x00a7:
            if (r1 == 0) goto L_0x0217
            r0.sticker = r1
            r2 = r34
            r0.parentObject = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r1.thumbs
            r7 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r7)
            boolean r7 = r0.fromEmojiPanel
            if (r7 == 0) goto L_0x00be
            java.lang.String r8 = "emptyListPlaceholder"
            goto L_0x00c0
        L_0x00be:
            java.lang.String r8 = "windowBackgroundGray"
        L_0x00c0:
            if (r7 == 0) goto L_0x00c5
            r6 = 1045220557(0x3e4ccccd, float:0.2)
        L_0x00c5:
            org.telegram.messenger.SvgHelper$SvgDrawable r12 = org.telegram.messenger.DocumentObject.getSvgThumb((org.telegram.tgnet.TLRPC$Document) r1, (java.lang.String) r8, (float) r6)
            boolean r6 = r0.fromEmojiPanel
            if (r6 == 0) goto L_0x00d0
            java.lang.String r6 = "66_66_pcache_compress"
            goto L_0x00d2
        L_0x00d0:
            java.lang.String r6 = "66_66"
        L_0x00d2:
            r20 = r6
            boolean r6 = org.telegram.messenger.MessageObject.canAutoplayAnimatedSticker(r32)
            r7 = 1
            if (r6 == 0) goto L_0x0138
            boolean r6 = r0.fromEmojiPanel
            if (r6 == 0) goto L_0x00e1
            r0.drawInParentView = r7
        L_0x00e1:
            if (r12 == 0) goto L_0x0106
            org.telegram.messenger.ImageReceiver r6 = r0.imageView
            org.telegram.messenger.ImageLocation r19 = org.telegram.messenger.ImageLocation.getForDocument(r32)
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r1)
            r22 = 0
            r23 = 0
            r24 = 0
            r26 = 0
            r28 = 0
            java.lang.Object r2 = r0.parentObject
            r30 = 1
            r18 = r6
            r25 = r12
            r29 = r2
            r18.setImage(r19, r20, r21, r22, r23, r24, r25, r26, r28, r29, r30)
            goto L_0x018b
        L_0x0106:
            if (r2 == 0) goto L_0x0122
            org.telegram.messenger.ImageReceiver r6 = r0.imageView
            org.telegram.messenger.ImageLocation r19 = org.telegram.messenger.ImageLocation.getForDocument(r32)
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r1)
            r22 = 0
            r23 = 0
            java.lang.Object r2 = r0.parentObject
            r25 = 1
            r18 = r6
            r24 = r2
            r18.setImage((org.telegram.messenger.ImageLocation) r19, (java.lang.String) r20, (org.telegram.messenger.ImageLocation) r21, (java.lang.String) r22, (java.lang.String) r23, (java.lang.Object) r24, (int) r25)
            goto L_0x018b
        L_0x0122:
            org.telegram.messenger.ImageReceiver r2 = r0.imageView
            org.telegram.messenger.ImageLocation r19 = org.telegram.messenger.ImageLocation.getForDocument(r32)
            r21 = 0
            r22 = 0
            java.lang.Object r6 = r0.parentObject
            r24 = 1
            r18 = r2
            r23 = r6
            r18.setImage(r19, r20, r21, r22, r23, r24)
            goto L_0x018b
        L_0x0138:
            if (r12 == 0) goto L_0x015e
            if (r2 == 0) goto L_0x014d
            org.telegram.messenger.ImageReceiver r9 = r0.imageView
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r1)
            java.lang.Object r14 = r0.parentObject
            r15 = 1
            java.lang.String r13 = "webp"
            r11 = r20
            r9.setImage(r10, r11, r12, r13, r14, r15)
            goto L_0x018b
        L_0x014d:
            org.telegram.messenger.ImageReceiver r9 = r0.imageView
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r32)
            java.lang.Object r14 = r0.parentObject
            r15 = 1
            java.lang.String r13 = "webp"
            r11 = r20
            r9.setImage(r10, r11, r12, r13, r14, r15)
            goto L_0x018b
        L_0x015e:
            if (r2 == 0) goto L_0x0176
            org.telegram.messenger.ImageReceiver r6 = r0.imageView
            org.telegram.messenger.ImageLocation r19 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r1)
            r21 = 0
            java.lang.Object r2 = r0.parentObject
            r24 = 1
            java.lang.String r22 = "webp"
            r18 = r6
            r23 = r2
            r18.setImage(r19, r20, r21, r22, r23, r24)
            goto L_0x018b
        L_0x0176:
            org.telegram.messenger.ImageReceiver r2 = r0.imageView
            org.telegram.messenger.ImageLocation r19 = org.telegram.messenger.ImageLocation.getForDocument(r32)
            r21 = 0
            java.lang.Object r6 = r0.parentObject
            r24 = 1
            java.lang.String r22 = "webp"
            r18 = r2
            r23 = r6
            r18.setImage(r19, r20, r21, r22, r23, r24)
        L_0x018b:
            if (r3 == 0) goto L_0x01a8
            android.widget.TextView r1 = r0.emojiTextView
            android.text.TextPaint r2 = r1.getPaint()
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r3, r2, r4, r5)
            r1.setText(r2)
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r5)
            goto L_0x0217
        L_0x01a8:
            if (r36 == 0) goto L_0x0212
            r2 = 0
        L_0x01ab:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x01e4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r3
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            if (r4 == 0) goto L_0x01e1
            java.lang.String r1 = r3.alt
            if (r1 == 0) goto L_0x01e4
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x01e4
            android.widget.TextView r1 = r0.emojiTextView
            java.lang.String r2 = r3.alt
            android.text.TextPaint r3 = r1.getPaint()
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r4, r5)
            r1.setText(r2)
            goto L_0x01e5
        L_0x01e1:
            int r2 = r2 + 1
            goto L_0x01ab
        L_0x01e4:
            r7 = 0
        L_0x01e5:
            if (r7 != 0) goto L_0x020c
            android.widget.TextView r1 = r0.emojiTextView
            int r2 = r0.currentAccount
            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r2)
            org.telegram.tgnet.TLRPC$Document r3 = r0.sticker
            long r3 = r3.id
            java.lang.String r2 = r2.getEmojiForSticker(r3)
            android.widget.TextView r3 = r0.emojiTextView
            android.text.TextPaint r3 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r4, r5)
            r1.setText(r2)
        L_0x020c:
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r5)
            goto L_0x0217
        L_0x0212:
            android.widget.TextView r1 = r0.emojiTextView
            r1.setVisibility(r4)
        L_0x0217:
            r0.updatePremiumStatus(r5)
            org.telegram.messenger.ImageReceiver r1 = r0.imageView
            float r2 = r0.alpha
            float r3 = r0.premiumAlpha
            float r2 = r2 * r3
            r1.setAlpha(r2)
            boolean r1 = r0.drawInParentView
            if (r1 == 0) goto L_0x0235
            org.telegram.messenger.ImageReceiver r1 = r0.imageView
            android.view.ViewParent r2 = r31.getParent()
            android.view.View r2 = (android.view.View) r2
            r1.setParentView(r2)
            goto L_0x023a
        L_0x0235:
            org.telegram.messenger.ImageReceiver r1 = r0.imageView
            r1.setParentView(r0)
        L_0x023a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.StickerEmojiCell.setSticker(org.telegram.tgnet.TLRPC$Document, org.telegram.messenger.SendMessagesHelper$ImportingSticker, java.lang.Object, java.lang.String, boolean):void");
    }

    private void updatePremiumStatus(boolean z) {
        if (this.isPremiumSticker) {
            this.showPremiumLock = true;
        } else {
            this.showPremiumLock = false;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.premiumIconView.getLayoutParams();
        if (!UserConfig.getInstance(this.currentAccount).isPremium()) {
            int dp = AndroidUtilities.dp(24.0f);
            layoutParams.width = dp;
            layoutParams.height = dp;
            layoutParams.gravity = 81;
            layoutParams.rightMargin = 0;
            layoutParams.bottomMargin = 0;
            this.premiumIconView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        } else {
            int dp2 = AndroidUtilities.dp(16.0f);
            layoutParams.width = dp2;
            layoutParams.height = dp2;
            layoutParams.gravity = 85;
            layoutParams.bottomMargin = AndroidUtilities.dp(8.0f);
            layoutParams.rightMargin = AndroidUtilities.dp(8.0f);
            this.premiumIconView.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
        }
        this.premiumIconView.setLocked(true ^ UserConfig.getInstance(this.currentAccount).isPremium());
        AndroidUtilities.updateViewVisibilityAnimated(this.premiumIconView, this.showPremiumLock, 0.9f, z);
        invalidate();
    }

    public void disable() {
        this.changingAlpha = true;
        this.alpha = 0.5f;
        this.time = 0;
        this.imageView.setAlpha(0.5f * this.premiumAlpha);
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
        return this.imageView.getBitmap() != null;
    }

    public ImageReceiver getImageView() {
        return this.imageView;
    }

    public void invalidate() {
        if (this.drawInParentView && getParent() != null) {
            ((View) getParent()).invalidate();
        }
        this.emojiTextView.invalidate();
        super.invalidate();
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

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawInParentView) {
            this.imageView.setParentView((View) getParent());
        } else {
            this.imageView.setParentView(this);
        }
        this.imageView.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageView.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            updatePremiumStatus(true);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!this.drawInParentView) {
            drawInternal(this, canvas);
        }
    }

    public void preDraw(View view, Canvas canvas) {
        if (this.drawInParentView) {
            drawInternal(view, canvas);
        }
    }

    private void drawInternal(View view, Canvas canvas) {
        boolean z;
        if (this.changingAlpha || ((z && this.scale != 0.8f) || (!(z = this.scaled) && this.scale != 1.0f))) {
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (this.changingAlpha) {
                long j2 = this.time + j;
                this.time = j2;
                if (j2 > 1050) {
                    this.time = 1050;
                }
                float interpolation = (interpolator.getInterpolation(((float) this.time) / 150.0f) * 0.5f) + 0.5f;
                this.alpha = interpolation;
                if (interpolation >= 1.0f) {
                    this.changingAlpha = false;
                    this.alpha = 1.0f;
                }
                this.imageView.setAlpha(this.alpha * this.premiumAlpha);
            } else {
                if (this.scaled) {
                    float f = this.scale;
                    if (f != 0.8f) {
                        float f2 = f - (((float) j) / 400.0f);
                        this.scale = f2;
                        if (f2 < 0.8f) {
                            this.scale = 0.8f;
                        }
                    }
                }
                float f3 = this.scale + (((float) j) / 400.0f);
                this.scale = f3;
                if (f3 > 1.0f) {
                    this.scale = 1.0f;
                }
            }
            view.invalidate();
        }
        int min = Math.min(AndroidUtilities.dp(66.0f), Math.min(getMeasuredHeight(), getMeasuredWidth()));
        float measuredWidth = (float) (getMeasuredWidth() >> 1);
        float f4 = (float) min;
        float f5 = f4 / 2.0f;
        float measuredHeight = (float) (getMeasuredHeight() >> 1);
        this.imageView.setImageCoords(measuredWidth - f5, measuredHeight - f5, f4, f4);
        this.imageView.setAlpha(this.alpha * this.premiumAlpha);
        if (this.scale != 1.0f) {
            canvas.save();
            float f6 = this.scale;
            canvas.scale(f6, f6, measuredWidth, measuredHeight);
            this.imageView.draw(canvas);
            canvas.restore();
            return;
        }
        this.imageView.draw(canvas);
    }
}
