package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;

@SuppressLint({"ViewConstructor"})
public class StickerSetBulletinLayout extends Bulletin.TwoLineLayout {
    public StickerSetBulletinLayout(Context context, TLObject tLObject, int i) {
        this(context, tLObject, 1, i, (TLRPC$Document) null, (Theme.ResourcesProvider) null);
    }

    public StickerSetBulletinLayout(Context context, TLObject tLObject, int i, TLRPC$Document tLRPC$Document, Theme.ResourcesProvider resourcesProvider) {
        this(context, tLObject, 1, i, tLRPC$Document, resourcesProvider);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:125:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x022b  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x023f  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0253  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0267  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x02e8  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0332  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public StickerSetBulletinLayout(android.content.Context r14, org.telegram.tgnet.TLObject r15, int r16, int r17, org.telegram.tgnet.TLRPC$Document r18, org.telegram.ui.ActionBar.Theme.ResourcesProvider r19) {
        /*
            r13 = this;
            r0 = r13
            r1 = r14
            r8 = r15
            r9 = r16
            r2 = r19
            r13.<init>(r14, r2)
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
            r3 = 0
            r10 = 0
            if (r2 == 0) goto L_0x0028
            r4 = r8
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r4
            org.telegram.tgnet.TLRPC$StickerSet r5 = r4.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r4.documents
            if (r4 == 0) goto L_0x0026
            boolean r6 = r4.isEmpty()
            if (r6 != 0) goto L_0x0026
            java.lang.Object r4 = r4.get(r10)
            org.telegram.tgnet.TLRPC$Document r4 = (org.telegram.tgnet.TLRPC$Document) r4
            goto L_0x006f
        L_0x0026:
            r4 = r3
            goto L_0x006f
        L_0x0028:
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r4 == 0) goto L_0x0048
            r4 = r8
            org.telegram.tgnet.TLRPC$StickerSetCovered r4 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r4
            org.telegram.tgnet.TLRPC$StickerSet r5 = r4.set
            org.telegram.tgnet.TLRPC$Document r6 = r4.cover
            if (r6 == 0) goto L_0x0037
            r4 = r6
            goto L_0x006f
        L_0x0037:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r4.covers
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x0026
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r4.covers
            java.lang.Object r4 = r4.get(r10)
            org.telegram.tgnet.TLRPC$Document r4 = (org.telegram.tgnet.TLRPC$Document) r4
            goto L_0x006f
        L_0x0048:
            if (r18 != 0) goto L_0x006c
            if (r8 == 0) goto L_0x006c
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r4 != 0) goto L_0x0051
            goto L_0x006c
        L_0x0051:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Invalid type of the given setObject: "
            r2.append(r3)
            java.lang.Class r3 = r15.getClass()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x006c:
            r4 = r18
            r5 = r3
        L_0x006f:
            r11 = 1
            if (r5 != 0) goto L_0x0086
            if (r4 == 0) goto L_0x0086
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r6)
            org.telegram.tgnet.TLRPC$InputStickerSet r7 = org.telegram.messenger.MessageObject.getInputStickerSet((org.telegram.tgnet.TLRPC$Document) r4)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r6 = r6.getStickerSet(r7, r11)
            if (r6 == 0) goto L_0x0086
            org.telegram.tgnet.TLRPC$StickerSet r5 = r6.set
        L_0x0086:
            r12 = r5
            if (r4 == 0) goto L_0x010b
            r5 = 90
            if (r12 != 0) goto L_0x008e
            goto L_0x0094
        L_0x008e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r12.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r5)
        L_0x0094:
            if (r3 != 0) goto L_0x0097
            r3 = r4
        L_0x0097:
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC$Document
            if (r6 == 0) goto L_0x00a7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r4.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r5)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r4)
        L_0x00a5:
            r5 = r2
            goto L_0x00c5
        L_0x00a7:
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r5 == 0) goto L_0x00b5
            r2 = r8
            org.telegram.tgnet.TLRPC$StickerSetCovered r2 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r2
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            int r2 = r2.thumb_version
            goto L_0x00c0
        L_0x00b5:
            if (r2 == 0) goto L_0x00bf
            r2 = r8
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r2
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            int r2 = r2.thumb_version
            goto L_0x00c0
        L_0x00bf:
            r2 = 0
        L_0x00c0:
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForSticker(r3, r4, r2)
            goto L_0x00a5
        L_0x00c5:
            if (r6 == 0) goto L_0x00cd
            boolean r2 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r11)
            if (r2 != 0) goto L_0x00fc
        L_0x00cd:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoSticker(r4)
            if (r2 != 0) goto L_0x00fc
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r4)
            if (r2 == 0) goto L_0x00da
            goto L_0x00fc
        L_0x00da:
            if (r5 == 0) goto L_0x00ee
            int r2 = r5.imageType
            if (r2 != r11) goto L_0x00ee
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r6 = 0
            java.lang.String r4 = "50_50"
            java.lang.String r7 = "tgs"
            r3 = r5
            r5 = r7
            r7 = r15
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
            goto L_0x0116
        L_0x00ee:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r6 = 0
            java.lang.String r4 = "50_50"
            java.lang.String r7 = "webp"
            r3 = r5
            r5 = r7
            r7 = r15
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
            goto L_0x0116
        L_0x00fc:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r4)
            r6 = 0
            r7 = 0
            java.lang.String r4 = "50_50"
            r8 = r15
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (org.telegram.messenger.ImageLocation) r5, (java.lang.String) r6, (int) r7, (java.lang.Object) r8)
            goto L_0x0116
        L_0x010b:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r3 = 0
            r4 = 0
            r6 = 0
            java.lang.String r5 = "webp"
            r7 = r15
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
        L_0x0116:
            r2 = 8
            switch(r17) {
                case 0: goto L_0x0332;
                case 1: goto L_0x02e8;
                case 2: goto L_0x0267;
                case 3: goto L_0x0253;
                case 4: goto L_0x023f;
                case 5: goto L_0x022b;
                case 6: goto L_0x01a4;
                case 7: goto L_0x011d;
                default: goto L_0x011b;
            }
        L_0x011b:
            goto L_0x03ae
        L_0x011d:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isPremium()
            java.lang.String r3 = "LimitReachedFavoriteGifs"
            if (r2 != 0) goto L_0x0178
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            boolean r2 = r2.premiumLocked
            if (r2 != 0) goto L_0x0178
            android.widget.TextView r2 = r0.titleTextView
            int r4 = org.telegram.messenger.R.string.LimitReachedFavoriteGifs
            java.lang.Object[] r5 = new java.lang.Object[r11]
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r6 = r6.savedGifsLimitDefault
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r10] = r6
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)
            r2.setText(r3)
            int r2 = org.telegram.messenger.R.string.LimitReachedFavoriteGifsSubtitle
            java.lang.Object[] r3 = new java.lang.Object[r11]
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r4 = r4.savedGifsLimitPremium
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r10] = r4
            java.lang.String r4 = "LimitReachedFavoriteGifsSubtitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda1
            r3.<init>(r14)
            java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.replaceSingleTag(r2, r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r2.setText(r1)
            goto L_0x03ae
        L_0x0178:
            android.widget.TextView r1 = r0.titleTextView
            int r2 = org.telegram.messenger.R.string.LimitReachedFavoriteGifs
            java.lang.Object[] r4 = new java.lang.Object[r11]
            int r5 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r5 = r5.savedGifsLimitPremium
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r10] = r5
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r4)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            int r2 = org.telegram.messenger.R.string.LimitReachedFavoriteGifsSubtitlePremium
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = "LimitReachedFavoriteGifsSubtitlePremium"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x03ae
        L_0x01a4:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isPremium()
            java.lang.String r3 = "LimitReachedFavoriteStickers"
            if (r2 != 0) goto L_0x01ff
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            boolean r2 = r2.premiumLocked
            if (r2 != 0) goto L_0x01ff
            android.widget.TextView r2 = r0.titleTextView
            int r4 = org.telegram.messenger.R.string.LimitReachedFavoriteStickers
            java.lang.Object[] r5 = new java.lang.Object[r11]
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r6 = r6.stickersFavedLimitDefault
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r10] = r6
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)
            r2.setText(r3)
            int r2 = org.telegram.messenger.R.string.LimitReachedFavoriteStickersSubtitle
            java.lang.Object[] r3 = new java.lang.Object[r11]
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r4 = r4.stickersFavedLimitPremium
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r10] = r4
            java.lang.String r4 = "LimitReachedFavoriteStickersSubtitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda0
            r3.<init>(r14)
            java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.replaceSingleTag(r2, r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r2.setText(r1)
            goto L_0x03ae
        L_0x01ff:
            android.widget.TextView r1 = r0.titleTextView
            int r2 = org.telegram.messenger.R.string.LimitReachedFavoriteStickers
            java.lang.Object[] r4 = new java.lang.Object[r11]
            int r5 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r5 = r5.stickersFavedLimitPremium
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r10] = r5
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r4)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            int r2 = org.telegram.messenger.R.string.LimitReachedFavoriteStickersSubtitlePremium
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = "LimitReachedFavoriteStickersSubtitlePremium"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x03ae
        L_0x022b:
            android.widget.TextView r1 = r0.titleTextView
            int r3 = org.telegram.messenger.R.string.AddedToFavorites
            java.lang.String r4 = "AddedToFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x03ae
        L_0x023f:
            android.widget.TextView r1 = r0.titleTextView
            int r3 = org.telegram.messenger.R.string.RemovedFromFavorites
            java.lang.String r4 = "RemovedFromFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x03ae
        L_0x0253:
            android.widget.TextView r1 = r0.titleTextView
            int r3 = org.telegram.messenger.R.string.RemovedFromRecent
            java.lang.String r4 = "RemovedFromRecent"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x03ae
        L_0x0267:
            if (r12 == 0) goto L_0x03ae
            boolean r1 = r12.masks
            if (r1 == 0) goto L_0x028f
            android.widget.TextView r1 = r0.titleTextView
            int r2 = org.telegram.messenger.R.string.AddMasksInstalled
            java.lang.String r3 = "AddMasksInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            int r2 = org.telegram.messenger.R.string.AddMasksInstalledInfo
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r12.title
            r3[r10] = r4
            java.lang.String r4 = "AddMasksInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x03ae
        L_0x028f:
            boolean r1 = r12.emojis
            if (r1 == 0) goto L_0x02c6
            android.widget.TextView r1 = r0.titleTextView
            int r2 = org.telegram.messenger.R.string.AddEmojiInstalled
            java.lang.String r3 = "AddEmojiInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            if (r9 <= r11) goto L_0x02b1
            android.widget.TextView r1 = r0.subtitleTextView
            java.lang.Object[] r2 = new java.lang.Object[r10]
            java.lang.String r3 = "AddEmojiMultipleInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r9, r2)
            r1.setText(r2)
            goto L_0x03ae
        L_0x02b1:
            android.widget.TextView r1 = r0.subtitleTextView
            int r2 = org.telegram.messenger.R.string.AddEmojiInstalledInfo
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r12.title
            r3[r10] = r4
            java.lang.String r4 = "AddEmojiInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x03ae
        L_0x02c6:
            android.widget.TextView r1 = r0.titleTextView
            int r2 = org.telegram.messenger.R.string.AddStickersInstalled
            java.lang.String r3 = "AddStickersInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            int r2 = org.telegram.messenger.R.string.AddStickersInstalledInfo
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r12.title
            r3[r10] = r4
            java.lang.String r4 = "AddStickersInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x03ae
        L_0x02e8:
            if (r12 == 0) goto L_0x03ae
            boolean r1 = r12.masks
            if (r1 == 0) goto L_0x0310
            android.widget.TextView r1 = r0.titleTextView
            int r2 = org.telegram.messenger.R.string.MasksArchived
            java.lang.String r3 = "MasksArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            int r2 = org.telegram.messenger.R.string.MasksArchivedInfo
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r12.title
            r3[r10] = r4
            java.lang.String r4 = "MasksArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x03ae
        L_0x0310:
            android.widget.TextView r1 = r0.titleTextView
            int r2 = org.telegram.messenger.R.string.StickersArchived
            java.lang.String r3 = "StickersArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            int r2 = org.telegram.messenger.R.string.StickersArchivedInfo
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r12.title
            r3[r10] = r4
            java.lang.String r4 = "StickersArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x03ae
        L_0x0332:
            if (r12 == 0) goto L_0x03ae
            boolean r1 = r12.masks
            if (r1 == 0) goto L_0x0359
            android.widget.TextView r1 = r0.titleTextView
            int r2 = org.telegram.messenger.R.string.MasksRemoved
            java.lang.String r3 = "MasksRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            int r2 = org.telegram.messenger.R.string.MasksRemovedInfo
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r12.title
            r3[r10] = r4
            java.lang.String r4 = "MasksRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x03ae
        L_0x0359:
            boolean r1 = r12.emojis
            if (r1 == 0) goto L_0x038e
            android.widget.TextView r1 = r0.titleTextView
            int r2 = org.telegram.messenger.R.string.EmojiRemoved
            java.lang.String r3 = "EmojiRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            if (r9 <= r11) goto L_0x037a
            android.widget.TextView r1 = r0.subtitleTextView
            java.lang.Object[] r2 = new java.lang.Object[r10]
            java.lang.String r3 = "EmojiRemovedMultipleInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r9, r2)
            r1.setText(r2)
            goto L_0x03ae
        L_0x037a:
            android.widget.TextView r1 = r0.subtitleTextView
            int r2 = org.telegram.messenger.R.string.EmojiRemovedInfo
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r12.title
            r3[r10] = r4
            java.lang.String r4 = "EmojiRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x03ae
        L_0x038e:
            android.widget.TextView r1 = r0.titleTextView
            int r2 = org.telegram.messenger.R.string.StickersRemoved
            java.lang.String r3 = "StickersRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            int r2 = org.telegram.messenger.R.string.StickersRemovedInfo
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r12.title
            r3[r10] = r4
            java.lang.String r4 = "StickersRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
        L_0x03ae:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerSetBulletinLayout.<init>(android.content.Context, org.telegram.tgnet.TLObject, int, int, org.telegram.tgnet.TLRPC$Document, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$0(Context context) {
        Activity findActivity = AndroidUtilities.findActivity(context);
        if (findActivity instanceof LaunchActivity) {
            ((LaunchActivity) findActivity).lambda$runLinkRequest$62(new PremiumPreviewFragment(LimitReachedBottomSheet.limitTypeToServerString(10)));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$1(Context context) {
        Activity findActivity = AndroidUtilities.findActivity(context);
        if (findActivity instanceof LaunchActivity) {
            ((LaunchActivity) findActivity).lambda$runLinkRequest$62(new PremiumPreviewFragment(LimitReachedBottomSheet.limitTypeToServerString(9)));
        }
    }
}
