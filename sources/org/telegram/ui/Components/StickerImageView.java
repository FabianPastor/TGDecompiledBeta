package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.messenger.NotificationCenter;

public class StickerImageView extends BackupImageView implements NotificationCenter.NotificationCenterDelegate {
    int currentAccount;
    int stickerNum;
    String stickerPackName = "tg_placeholders_android";

    public StickerImageView(Context context, int currentAccount2) {
        super(context);
        this.currentAccount = currentAccount2;
    }

    public void setStickerNum(int stickerNum2) {
        this.stickerNum = stickerNum2;
    }

    public void setStickerPackName(String stickerPackName2) {
        this.stickerPackName = stickerPackName2;
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.diceStickersDidLoad) {
            if (this.stickerPackName.equals(args[0])) {
                setSticker();
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setSticker() {
        /*
            r11 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            int r3 = r11.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            java.lang.String r4 = r11.stickerPackName
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r3.getStickerSetByName(r4)
            if (r2 != 0) goto L_0x001d
            int r3 = r11.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            java.lang.String r4 = r11.stickerPackName
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r3.getStickerSetByEmojiOrName(r4)
        L_0x001d:
            if (r2 == 0) goto L_0x0034
            java.util.ArrayList r3 = r2.documents
            int r3 = r3.size()
            int r4 = r11.stickerNum
            if (r3 <= r4) goto L_0x0034
            java.util.ArrayList r3 = r2.documents
            int r4 = r11.stickerNum
            java.lang.Object r3 = r3.get(r4)
            r1 = r3
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC.Document) r1
        L_0x0034:
            java.lang.String r0 = "130_130"
            r3 = 0
            if (r1 == 0) goto L_0x0046
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r1.thumbs
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            java.lang.String r6 = "emptyListPlaceholder"
            org.telegram.messenger.SvgHelper$SvgDrawable r3 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC.PhotoSize>) r4, (java.lang.String) r6, (float) r5)
            r9 = r3
            goto L_0x0047
        L_0x0046:
            r9 = r3
        L_0x0047:
            if (r9 == 0) goto L_0x004e
            r3 = 512(0x200, float:7.175E-43)
            r9.overrideWidthAndHeight(r3, r3)
        L_0x004e:
            if (r1 == 0) goto L_0x005f
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.String r6 = "tgs"
            r3 = r11
            r4 = r10
            r5 = r0
            r7 = r9
            r8 = r2
            r3.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (java.lang.String) r6, (android.graphics.drawable.Drawable) r7, (java.lang.Object) r8)
            goto L_0x0070
        L_0x005f:
            int r3 = r11.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            java.lang.String r4 = r11.stickerPackName
            r5 = 0
            if (r2 != 0) goto L_0x006c
            r6 = 1
            goto L_0x006d
        L_0x006c:
            r6 = 0
        L_0x006d:
            r3.loadStickersByEmojiOrName(r4, r5, r6)
        L_0x0070:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerImageView.setSticker():void");
    }
}
