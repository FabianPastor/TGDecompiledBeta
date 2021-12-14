package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.messenger.NotificationCenter;

public class StickerImageView extends BackupImageView implements NotificationCenter.NotificationCenterDelegate {
    int currentAccount;
    int stickerNum;
    String stickerPackName = "tg_placeholders_android";

    public StickerImageView(Context context, int i) {
        super(context);
        this.currentAccount = i;
    }

    public void setStickerNum(int i) {
        this.stickerNum = i;
    }

    public void setStickerPackName(String str) {
        this.stickerPackName = str;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSticker();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.diceStickersDidLoad) {
            if (this.stickerPackName.equals(objArr[0])) {
                setSticker();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001e, code lost:
        r1 = r6.documents.size();
        r2 = r7.stickerNum;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setSticker() {
        /*
            r7 = this;
            int r0 = r7.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.lang.String r1 = r7.stickerPackName
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r0.getStickerSetByName(r1)
            if (r0 != 0) goto L_0x001a
            int r0 = r7.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.lang.String r1 = r7.stickerPackName
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r0.getStickerSetByEmojiOrName(r1)
        L_0x001a:
            r6 = r0
            r0 = 0
            if (r6 == 0) goto L_0x0031
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r6.documents
            int r1 = r1.size()
            int r2 = r7.stickerNum
            if (r1 <= r2) goto L_0x0031
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r6.documents
            java.lang.Object r1 = r1.get(r2)
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC$Document) r1
            goto L_0x0032
        L_0x0031:
            r1 = r0
        L_0x0032:
            if (r1 == 0) goto L_0x003f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs
            r2 = 1045220557(0x3e4ccccd, float:0.2)
            java.lang.String r3 = "emptyListPlaceholder"
            org.telegram.messenger.SvgHelper$SvgDrawable r0 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) r0, (java.lang.String) r3, (float) r2)
        L_0x003f:
            r5 = r0
            if (r5 == 0) goto L_0x0047
            r0 = 512(0x200, float:7.175E-43)
            r5.overrideWidthAndHeight(r0, r0)
        L_0x0047:
            if (r1 == 0) goto L_0x0056
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.String r3 = "130_130"
            java.lang.String r4 = "tgs"
            r1 = r7
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x0067
        L_0x0056:
            int r0 = r7.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.lang.String r1 = r7.stickerPackName
            r2 = 0
            if (r6 != 0) goto L_0x0063
            r3 = 1
            goto L_0x0064
        L_0x0063:
            r3 = 0
        L_0x0064:
            r0.loadStickersByEmojiOrName(r1, r2, r3)
        L_0x0067:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerImageView.setSticker():void");
    }
}
