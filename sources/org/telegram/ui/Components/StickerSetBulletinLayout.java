package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.Bulletin;

@SuppressLint({"ViewConstructor"})
public class StickerSetBulletinLayout extends Bulletin.TwoLineLayout {
    public StickerSetBulletinLayout(Context context, TLObject tLObject, int i) {
        this(context, tLObject, i, (TLRPC$Document) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x01ef  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public StickerSetBulletinLayout(android.content.Context r14, org.telegram.tgnet.TLObject r15, int r16, org.telegram.tgnet.TLRPC$Document r17) {
        /*
            r13 = this;
            r0 = r13
            r7 = r15
            r8 = r16
            r13.<init>(r14)
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
            r2 = 0
            r9 = 0
            if (r1 == 0) goto L_0x0026
            r1 = r7
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r1
            org.telegram.tgnet.TLRPC$StickerSet r3 = r1.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r1.documents
            if (r1 == 0) goto L_0x0023
            boolean r4 = r1.isEmpty()
            if (r4 != 0) goto L_0x0023
            java.lang.Object r1 = r1.get(r9)
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC$Document) r1
            goto L_0x0024
        L_0x0023:
            r1 = r2
        L_0x0024:
            r10 = r3
            goto L_0x0049
        L_0x0026:
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r1 == 0) goto L_0x0046
            r1 = r7
            org.telegram.tgnet.TLRPC$StickerSetCovered r1 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r1
            org.telegram.tgnet.TLRPC$StickerSet r3 = r1.set
            org.telegram.tgnet.TLRPC$Document r4 = r1.cover
            if (r4 == 0) goto L_0x0035
            r1 = r4
            goto L_0x0024
        L_0x0035:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r1.covers
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0023
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r1.covers
            java.lang.Object r1 = r1.get(r9)
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC$Document) r1
            goto L_0x0024
        L_0x0046:
            r1 = r17
            r10 = r2
        L_0x0049:
            if (r1 == 0) goto L_0x01ef
            r11 = 1
            if (r1 == 0) goto L_0x00b1
            r3 = 90
            if (r10 != 0) goto L_0x0053
            goto L_0x0059
        L_0x0053:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r10.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3)
        L_0x0059:
            if (r2 != 0) goto L_0x005c
            r2 = r1
        L_0x005c:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$Document
            if (r4 == 0) goto L_0x006b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r1.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r1)
            goto L_0x0071
        L_0x006b:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForSticker(r2, r1)
        L_0x0071:
            r5 = r2
            if (r4 == 0) goto L_0x008f
            boolean r2 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r11)
            if (r2 == 0) goto L_0x008f
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            r6 = 0
            r12 = 0
            java.lang.String r4 = "50_50"
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r12
            r7 = r15
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (int) r6, (java.lang.Object) r7)
            goto L_0x00bc
        L_0x008f:
            if (r5 == 0) goto L_0x00a3
            int r1 = r5.imageType
            if (r1 != r11) goto L_0x00a3
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r6 = 0
            java.lang.String r3 = "50_50"
            java.lang.String r4 = "tgs"
            r2 = r5
            r5 = r6
            r6 = r15
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x00bc
        L_0x00a3:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r6 = 0
            java.lang.String r3 = "50_50"
            java.lang.String r4 = "webp"
            r2 = r5
            r5 = r6
            r6 = r15
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x00bc
        L_0x00b1:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r2 = 0
            r3 = 0
            r5 = 0
            java.lang.String r4 = "webp"
            r6 = r15
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
        L_0x00bc:
            if (r8 == 0) goto L_0x01a5
            if (r8 == r11) goto L_0x015b
            r1 = 2
            if (r8 == r1) goto L_0x010f
            r1 = 3
            r2 = 8
            if (r8 == r1) goto L_0x00fa
            r1 = 4
            if (r8 == r1) goto L_0x00e5
            r1 = 5
            if (r8 == r1) goto L_0x00d0
            goto L_0x01ee
        L_0x00d0:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131624229(0x7f0e0125, float:1.8875632E38)
            java.lang.String r4 = "AddedToFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x01ee
        L_0x00e5:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627074(0x7f0e0CLASSNAME, float:1.8881402E38)
            java.lang.String r4 = "RemovedFromFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x01ee
        L_0x00fa:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r4 = "RemovedFromRecent"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x01ee
        L_0x010f:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x0137
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624197(0x7f0e0105, float:1.8875567E38)
            java.lang.String r3 = "AddMasksInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624198(0x7f0e0106, float:1.8875569E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "AddMasksInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x01ee
        L_0x0137:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624214(0x7f0e0116, float:1.8875601E38)
            java.lang.String r3 = "AddStickersInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624215(0x7f0e0117, float:1.8875603E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "AddStickersInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x01ee
        L_0x015b:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x0182
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626018(0x7f0e0822, float:1.887926E38)
            java.lang.String r3 = "MasksArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626019(0x7f0e0823, float:1.8879262E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "MasksArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x01ee
        L_0x0182:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131627489(0x7f0e0de1, float:1.8882244E38)
            java.lang.String r3 = "StickersArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131627490(0x7f0e0de2, float:1.8882246E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "StickersArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x01ee
        L_0x01a5:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x01cc
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626027(0x7f0e082b, float:1.8879279E38)
            java.lang.String r3 = "MasksRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626028(0x7f0e082c, float:1.887928E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "MasksRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x01ee
        L_0x01cc:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131627496(0x7f0e0de8, float:1.8882258E38)
            java.lang.String r3 = "StickersRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131627497(0x7f0e0de9, float:1.888226E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "StickersRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
        L_0x01ee:
            return
        L_0x01ef:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Invalid type of the given setObject: "
            r2.append(r3)
            java.lang.Class r3 = r15.getClass()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            goto L_0x020b
        L_0x020a:
            throw r1
        L_0x020b:
            goto L_0x020a
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerSetBulletinLayout.<init>(android.content.Context, org.telegram.tgnet.TLObject, int, org.telegram.tgnet.TLRPC$Document):void");
    }
}
