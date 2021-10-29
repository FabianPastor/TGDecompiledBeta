package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;

@SuppressLint({"ViewConstructor"})
public class StickerSetBulletinLayout extends Bulletin.TwoLineLayout {
    public StickerSetBulletinLayout(Context context, TLObject tLObject, int i) {
        this(context, tLObject, i, (TLRPC$Document) null, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00e7  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00f4  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01db  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public StickerSetBulletinLayout(android.content.Context r13, org.telegram.tgnet.TLObject r14, int r15, org.telegram.tgnet.TLRPC$Document r16, org.telegram.ui.ActionBar.Theme.ResourcesProvider r17) {
        /*
            r12 = this;
            r0 = r12
            r7 = r14
            r8 = r15
            r1 = r13
            r2 = r17
            r12.<init>(r13, r2)
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
            r2 = 0
            r9 = 0
            if (r1 == 0) goto L_0x0028
            r3 = r7
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r3
            org.telegram.tgnet.TLRPC$StickerSet r4 = r3.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r3 = r3.documents
            if (r3 == 0) goto L_0x0025
            boolean r5 = r3.isEmpty()
            if (r5 != 0) goto L_0x0025
            java.lang.Object r3 = r3.get(r9)
            org.telegram.tgnet.TLRPC$Document r3 = (org.telegram.tgnet.TLRPC$Document) r3
            goto L_0x0026
        L_0x0025:
            r3 = r2
        L_0x0026:
            r10 = r4
            goto L_0x006f
        L_0x0028:
            boolean r3 = r7 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r3 == 0) goto L_0x0048
            r3 = r7
            org.telegram.tgnet.TLRPC$StickerSetCovered r3 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r3
            org.telegram.tgnet.TLRPC$StickerSet r4 = r3.set
            org.telegram.tgnet.TLRPC$Document r5 = r3.cover
            if (r5 == 0) goto L_0x0037
            r3 = r5
            goto L_0x0026
        L_0x0037:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r5 = r3.covers
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0025
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r3 = r3.covers
            java.lang.Object r3 = r3.get(r9)
            org.telegram.tgnet.TLRPC$Document r3 = (org.telegram.tgnet.TLRPC$Document) r3
            goto L_0x0026
        L_0x0048:
            if (r16 != 0) goto L_0x006c
            if (r7 == 0) goto L_0x006c
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r3 != 0) goto L_0x0051
            goto L_0x006c
        L_0x0051:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Invalid type of the given setObject: "
            r2.append(r3)
            java.lang.Class r3 = r14.getClass()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x006c:
            r3 = r16
            r10 = r2
        L_0x006f:
            r11 = 1
            if (r3 == 0) goto L_0x00e7
            r4 = 90
            if (r10 != 0) goto L_0x0077
            goto L_0x007d
        L_0x0077:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r10.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r4)
        L_0x007d:
            if (r2 != 0) goto L_0x0080
            r2 = r3
        L_0x0080:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$Document
            if (r5 == 0) goto L_0x0090
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r4)
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r1, (org.telegram.tgnet.TLRPC$Document) r3)
        L_0x008e:
            r4 = r1
            goto L_0x00ae
        L_0x0090:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r4 == 0) goto L_0x009e
            r1 = r7
            org.telegram.tgnet.TLRPC$StickerSetCovered r1 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r1
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set
            int r1 = r1.thumb_version
            goto L_0x00a9
        L_0x009e:
            if (r1 == 0) goto L_0x00a8
            r1 = r7
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r1
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set
            int r1 = r1.thumb_version
            goto L_0x00a9
        L_0x00a8:
            r1 = 0
        L_0x00a9:
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForSticker(r2, r3, r1)
            goto L_0x008e
        L_0x00ae:
            if (r5 == 0) goto L_0x00c5
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r3, r11)
            if (r1 == 0) goto L_0x00c5
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            r5 = 0
            r6 = 0
            java.lang.String r3 = "50_50"
            r7 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (int) r6, (java.lang.Object) r7)
            goto L_0x00f2
        L_0x00c5:
            if (r4 == 0) goto L_0x00d9
            int r1 = r4.imageType
            if (r1 != r11) goto L_0x00d9
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r5 = 0
            java.lang.String r3 = "50_50"
            java.lang.String r6 = "tgs"
            r2 = r4
            r4 = r6
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x00f2
        L_0x00d9:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r5 = 0
            java.lang.String r3 = "50_50"
            java.lang.String r6 = "webp"
            r2 = r4
            r4 = r6
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x00f2
        L_0x00e7:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r2 = 0
            r3 = 0
            r5 = 0
            java.lang.String r4 = "webp"
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
        L_0x00f2:
            if (r8 == 0) goto L_0x01db
            if (r8 == r11) goto L_0x0191
            r1 = 2
            if (r8 == r1) goto L_0x0145
            r1 = 3
            r2 = 8
            if (r8 == r1) goto L_0x0130
            r1 = 4
            if (r8 == r1) goto L_0x011b
            r1 = 5
            if (r8 == r1) goto L_0x0106
            goto L_0x0224
        L_0x0106:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131624242(0x7f0e0132, float:1.8875658E38)
            java.lang.String r4 = "AddedToFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x0224
        L_0x011b:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627381(0x7f0e0d75, float:1.8882025E38)
            java.lang.String r4 = "RemovedFromFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x0224
        L_0x0130:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627382(0x7f0e0d76, float:1.8882027E38)
            java.lang.String r4 = "RemovedFromRecent"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x0224
        L_0x0145:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x016d
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624207(0x7f0e010f, float:1.8875587E38)
            java.lang.String r3 = "AddMasksInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624208(0x7f0e0110, float:1.887559E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "AddMasksInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0224
        L_0x016d:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624224(0x7f0e0120, float:1.8875622E38)
            java.lang.String r3 = "AddStickersInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624225(0x7f0e0121, float:1.8875624E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "AddStickersInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0224
        L_0x0191:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x01b8
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626185(0x7f0e08c9, float:1.88796E38)
            java.lang.String r3 = "MasksArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626186(0x7f0e08ca, float:1.8879601E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "MasksArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0224
        L_0x01b8:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131627864(0x7f0e0var_, float:1.8883004E38)
            java.lang.String r3 = "StickersArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131627865(0x7f0e0var_, float:1.8883006E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "StickersArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0224
        L_0x01db:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x0202
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626194(0x7f0e08d2, float:1.8879617E38)
            java.lang.String r3 = "MasksRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626195(0x7f0e08d3, float:1.887962E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "MasksRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0224
        L_0x0202:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131627871(0x7f0e0f5f, float:1.8883019E38)
            java.lang.String r3 = "StickersRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131627872(0x7f0e0var_, float:1.888302E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "StickersRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
        L_0x0224:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerSetBulletinLayout.<init>(android.content.Context, org.telegram.tgnet.TLObject, int, org.telegram.tgnet.TLRPC$Document, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }
}
