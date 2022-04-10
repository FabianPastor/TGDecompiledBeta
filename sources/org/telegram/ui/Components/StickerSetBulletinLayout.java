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
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00ed  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00fa  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01e1  */
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
            if (r3 == 0) goto L_0x00ed
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
            if (r5 == 0) goto L_0x00b6
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r3, r11)
            if (r1 != 0) goto L_0x00bc
        L_0x00b6:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoSticker(r3)
            if (r1 == 0) goto L_0x00cb
        L_0x00bc:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            r5 = 0
            r6 = 0
            java.lang.String r3 = "50_50"
            r7 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (int) r6, (java.lang.Object) r7)
            goto L_0x00f8
        L_0x00cb:
            if (r4 == 0) goto L_0x00df
            int r1 = r4.imageType
            if (r1 != r11) goto L_0x00df
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r5 = 0
            java.lang.String r3 = "50_50"
            java.lang.String r6 = "tgs"
            r2 = r4
            r4 = r6
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x00f8
        L_0x00df:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r5 = 0
            java.lang.String r3 = "50_50"
            java.lang.String r6 = "webp"
            r2 = r4
            r4 = r6
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x00f8
        L_0x00ed:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r2 = 0
            r3 = 0
            r5 = 0
            java.lang.String r4 = "webp"
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
        L_0x00f8:
            if (r8 == 0) goto L_0x01e1
            if (r8 == r11) goto L_0x0197
            r1 = 2
            if (r8 == r1) goto L_0x014b
            r1 = 3
            r2 = 8
            if (r8 == r1) goto L_0x0136
            r1 = 4
            if (r8 == r1) goto L_0x0121
            r1 = 5
            if (r8 == r1) goto L_0x010c
            goto L_0x022a
        L_0x010c:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131624262(0x7f0e0146, float:1.8875699E38)
            java.lang.String r4 = "AddedToFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x022a
        L_0x0121:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627696(0x7f0e0eb0, float:1.8882664E38)
            java.lang.String r4 = "RemovedFromFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x022a
        L_0x0136:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627697(0x7f0e0eb1, float:1.8882666E38)
            java.lang.String r4 = "RemovedFromRecent"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x022a
        L_0x014b:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x0173
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624226(0x7f0e0122, float:1.8875626E38)
            java.lang.String r3 = "AddMasksInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624227(0x7f0e0123, float:1.8875628E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "AddMasksInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x022a
        L_0x0173:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624243(0x7f0e0133, float:1.887566E38)
            java.lang.String r3 = "AddStickersInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624244(0x7f0e0134, float:1.8875662E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "AddStickersInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x022a
        L_0x0197:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x01be
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626379(0x7f0e098b, float:1.8879993E38)
            java.lang.String r3 = "MasksArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626380(0x7f0e098c, float:1.8879995E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "MasksArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x022a
        L_0x01be:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131628216(0x7f0e10b8, float:1.8883718E38)
            java.lang.String r3 = "StickersArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131628217(0x7f0e10b9, float:1.888372E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "StickersArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x022a
        L_0x01e1:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x0208
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626388(0x7f0e0994, float:1.888001E38)
            java.lang.String r3 = "MasksRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626389(0x7f0e0995, float:1.8880013E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "MasksRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x022a
        L_0x0208:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131628223(0x7f0e10bf, float:1.8883733E38)
            java.lang.String r3 = "StickersRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131628224(0x7f0e10c0, float:1.8883735E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "StickersRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
        L_0x022a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerSetBulletinLayout.<init>(android.content.Context, org.telegram.tgnet.TLObject, int, org.telegram.tgnet.TLRPC$Document, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }
}
