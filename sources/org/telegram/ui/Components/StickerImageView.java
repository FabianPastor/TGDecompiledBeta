package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.messenger.NotificationCenter;
/* loaded from: classes3.dex */
public class StickerImageView extends BackupImageView implements NotificationCenter.NotificationCenterDelegate {
    int currentAccount;
    int stickerNum;
    String stickerPackName;

    public StickerImageView(Context context, int i) {
        super(context);
        this.stickerPackName = "tg_placeholders_android";
        this.currentAccount = i;
    }

    public void setStickerNum(int i) {
        if (this.stickerNum != i) {
            this.stickerNum = i;
            setSticker();
        }
    }

    public void setStickerPackName(String str) {
        this.stickerPackName = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Components.BackupImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSticker();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Components.BackupImageView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.diceStickersDidLoad) {
            if (!this.stickerPackName.equals((String) objArr[0])) {
                return;
            }
            setSticker();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0042  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0049  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0056  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setSticker() {
        /*
            r7 = this;
            int r0 = r7.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.lang.String r1 = r7.stickerPackName
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r0.getStickerSetByName(r1)
            if (r0 != 0) goto L1a
            int r0 = r7.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.lang.String r1 = r7.stickerPackName
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r0.getStickerSetByEmojiOrName(r1)
        L1a:
            r6 = r0
            r0 = 0
            if (r6 == 0) goto L31
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r6.documents
            int r1 = r1.size()
            int r2 = r7.stickerNum
            if (r1 <= r2) goto L31
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r6.documents
            java.lang.Object r1 = r1.get(r2)
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC$Document) r1
            goto L32
        L31:
            r1 = r0
        L32:
            if (r1 == 0) goto L3f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs
            r2 = 1045220557(0x3e4ccccd, float:0.2)
            java.lang.String r3 = "emptyListPlaceholder"
            org.telegram.messenger.SvgHelper$SvgDrawable r0 = org.telegram.messenger.DocumentObject.getSvgThumb(r0, r3, r2)
        L3f:
            r5 = r0
            if (r5 == 0) goto L47
            r0 = 512(0x200, float:7.175E-43)
            r5.overrideWidthAndHeight(r0, r0)
        L47:
            if (r1 == 0) goto L56
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.String r3 = "130_130"
            java.lang.String r4 = "tgs"
            r1 = r7
            r1.setImage(r2, r3, r4, r5, r6)
            goto L6c
        L56:
            org.telegram.messenger.ImageReceiver r0 = r7.imageReceiver
            r0.clearImage()
            int r0 = r7.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.lang.String r1 = r7.stickerPackName
            r2 = 0
            if (r6 != 0) goto L68
            r3 = 1
            goto L69
        L68:
            r3 = 0
        L69:
            r0.loadStickersByEmojiOrName(r1, r2, r3)
        L6c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerImageView.setSticker():void");
    }
}
