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
    /* JADX WARNING: Removed duplicated region for block: B:26:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01d8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public StickerSetBulletinLayout(android.content.Context r13, org.telegram.tgnet.TLObject r14, int r15, org.telegram.tgnet.TLRPC$Document r16) {
        /*
            r12 = this;
            r0 = r12
            r7 = r14
            r8 = r15
            r12.<init>(r13)
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
            r2 = 0
            r9 = 0
            if (r1 == 0) goto L_0x0025
            r3 = r7
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r3
            org.telegram.tgnet.TLRPC$StickerSet r4 = r3.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r3 = r3.documents
            if (r3 == 0) goto L_0x0022
            boolean r5 = r3.isEmpty()
            if (r5 != 0) goto L_0x0022
            java.lang.Object r3 = r3.get(r9)
            org.telegram.tgnet.TLRPC$Document r3 = (org.telegram.tgnet.TLRPC$Document) r3
            goto L_0x0023
        L_0x0022:
            r3 = r2
        L_0x0023:
            r10 = r4
            goto L_0x006c
        L_0x0025:
            boolean r3 = r7 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r3 == 0) goto L_0x0045
            r3 = r7
            org.telegram.tgnet.TLRPC$StickerSetCovered r3 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r3
            org.telegram.tgnet.TLRPC$StickerSet r4 = r3.set
            org.telegram.tgnet.TLRPC$Document r5 = r3.cover
            if (r5 == 0) goto L_0x0034
            r3 = r5
            goto L_0x0023
        L_0x0034:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r5 = r3.covers
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0022
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r3 = r3.covers
            java.lang.Object r3 = r3.get(r9)
            org.telegram.tgnet.TLRPC$Document r3 = (org.telegram.tgnet.TLRPC$Document) r3
            goto L_0x0023
        L_0x0045:
            if (r16 != 0) goto L_0x0069
            if (r7 == 0) goto L_0x0069
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r3 != 0) goto L_0x004e
            goto L_0x0069
        L_0x004e:
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
        L_0x0069:
            r3 = r16
            r10 = r2
        L_0x006c:
            r11 = 1
            if (r3 == 0) goto L_0x00e4
            r4 = 90
            if (r10 != 0) goto L_0x0074
            goto L_0x007a
        L_0x0074:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r10.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r4)
        L_0x007a:
            if (r2 != 0) goto L_0x007d
            r2 = r3
        L_0x007d:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$Document
            if (r5 == 0) goto L_0x008d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r4)
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r1, (org.telegram.tgnet.TLRPC$Document) r3)
        L_0x008b:
            r4 = r1
            goto L_0x00ab
        L_0x008d:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r4 == 0) goto L_0x009b
            r1 = r7
            org.telegram.tgnet.TLRPC$StickerSetCovered r1 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r1
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set
            int r1 = r1.thumb_version
            goto L_0x00a6
        L_0x009b:
            if (r1 == 0) goto L_0x00a5
            r1 = r7
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r1
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set
            int r1 = r1.thumb_version
            goto L_0x00a6
        L_0x00a5:
            r1 = 0
        L_0x00a6:
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForSticker(r2, r3, r1)
            goto L_0x008b
        L_0x00ab:
            if (r5 == 0) goto L_0x00c2
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r3, r11)
            if (r1 == 0) goto L_0x00c2
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            r5 = 0
            r6 = 0
            java.lang.String r3 = "50_50"
            r7 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (int) r6, (java.lang.Object) r7)
            goto L_0x00ef
        L_0x00c2:
            if (r4 == 0) goto L_0x00d6
            int r1 = r4.imageType
            if (r1 != r11) goto L_0x00d6
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r5 = 0
            java.lang.String r3 = "50_50"
            java.lang.String r6 = "tgs"
            r2 = r4
            r4 = r6
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x00ef
        L_0x00d6:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r5 = 0
            java.lang.String r3 = "50_50"
            java.lang.String r6 = "webp"
            r2 = r4
            r4 = r6
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x00ef
        L_0x00e4:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r2 = 0
            r3 = 0
            r5 = 0
            java.lang.String r4 = "webp"
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
        L_0x00ef:
            if (r8 == 0) goto L_0x01d8
            if (r8 == r11) goto L_0x018e
            r1 = 2
            if (r8 == r1) goto L_0x0142
            r1 = 3
            r2 = 8
            if (r8 == r1) goto L_0x012d
            r1 = 4
            if (r8 == r1) goto L_0x0118
            r1 = 5
            if (r8 == r1) goto L_0x0103
            goto L_0x0221
        L_0x0103:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131624233(0x7f0e0129, float:1.887564E38)
            java.lang.String r4 = "AddedToFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x0221
        L_0x0118:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627173(0x7f0e0ca5, float:1.8881603E38)
            java.lang.String r4 = "RemovedFromFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x0221
        L_0x012d:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627174(0x7f0e0ca6, float:1.8881605E38)
            java.lang.String r4 = "RemovedFromRecent"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x0221
        L_0x0142:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x016a
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624201(0x7f0e0109, float:1.8875575E38)
            java.lang.String r3 = "AddMasksInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624202(0x7f0e010a, float:1.8875577E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "AddMasksInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0221
        L_0x016a:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624218(0x7f0e011a, float:1.887561E38)
            java.lang.String r3 = "AddStickersInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624219(0x7f0e011b, float:1.8875612E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "AddStickersInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0221
        L_0x018e:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x01b5
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626085(0x7f0e0865, float:1.8879396E38)
            java.lang.String r3 = "MasksArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626086(0x7f0e0866, float:1.8879398E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "MasksArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0221
        L_0x01b5:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131627608(0x7f0e0e58, float:1.8882485E38)
            java.lang.String r3 = "StickersArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131627609(0x7f0e0e59, float:1.8882487E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "StickersArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0221
        L_0x01d8:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x01ff
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626094(0x7f0e086e, float:1.8879414E38)
            java.lang.String r3 = "MasksRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626095(0x7f0e086f, float:1.8879417E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "MasksRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0221
        L_0x01ff:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131627615(0x7f0e0e5f, float:1.88825E38)
            java.lang.String r3 = "StickersRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131627616(0x7f0e0e60, float:1.8882501E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "StickersRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
        L_0x0221:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerSetBulletinLayout.<init>(android.content.Context, org.telegram.tgnet.TLObject, int, org.telegram.tgnet.TLRPC$Document):void");
    }
}
