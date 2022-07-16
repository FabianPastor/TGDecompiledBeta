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
        this(context, tLObject, i, (TLRPC$Document) null, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0100 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0101  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public StickerSetBulletinLayout(android.content.Context r13, org.telegram.tgnet.TLObject r14, int r15, org.telegram.tgnet.TLRPC$Document r16, org.telegram.ui.ActionBar.Theme.ResourcesProvider r17) {
        /*
            r12 = this;
            r0 = r12
            r1 = r13
            r8 = r14
            r2 = r17
            r12.<init>(r13, r2)
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
            r3 = 0
            r9 = 0
            if (r2 == 0) goto L_0x0027
            r4 = r8
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r4
            org.telegram.tgnet.TLRPC$StickerSet r5 = r4.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r4.documents
            if (r4 == 0) goto L_0x0024
            boolean r6 = r4.isEmpty()
            if (r6 != 0) goto L_0x0024
            java.lang.Object r4 = r4.get(r9)
            org.telegram.tgnet.TLRPC$Document r4 = (org.telegram.tgnet.TLRPC$Document) r4
            goto L_0x0025
        L_0x0024:
            r4 = r3
        L_0x0025:
            r10 = r5
            goto L_0x006e
        L_0x0027:
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r4 == 0) goto L_0x0047
            r4 = r8
            org.telegram.tgnet.TLRPC$StickerSetCovered r4 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r4
            org.telegram.tgnet.TLRPC$StickerSet r5 = r4.set
            org.telegram.tgnet.TLRPC$Document r6 = r4.cover
            if (r6 == 0) goto L_0x0036
            r4 = r6
            goto L_0x0025
        L_0x0036:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r4.covers
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x0024
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r4.covers
            java.lang.Object r4 = r4.get(r9)
            org.telegram.tgnet.TLRPC$Document r4 = (org.telegram.tgnet.TLRPC$Document) r4
            goto L_0x0025
        L_0x0047:
            if (r16 != 0) goto L_0x006b
            if (r8 == 0) goto L_0x006b
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r4 != 0) goto L_0x0050
            goto L_0x006b
        L_0x0050:
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
        L_0x006b:
            r4 = r16
            r10 = r3
        L_0x006e:
            r11 = 1
            if (r4 == 0) goto L_0x00f3
            r5 = 90
            if (r10 != 0) goto L_0x0076
            goto L_0x007c
        L_0x0076:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r10.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r5)
        L_0x007c:
            if (r3 != 0) goto L_0x007f
            r3 = r4
        L_0x007f:
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC$Document
            if (r6 == 0) goto L_0x008f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r4.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r5)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r4)
        L_0x008d:
            r5 = r2
            goto L_0x00ad
        L_0x008f:
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r5 == 0) goto L_0x009d
            r2 = r8
            org.telegram.tgnet.TLRPC$StickerSetCovered r2 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r2
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            int r2 = r2.thumb_version
            goto L_0x00a8
        L_0x009d:
            if (r2 == 0) goto L_0x00a7
            r2 = r8
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r2
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            int r2 = r2.thumb_version
            goto L_0x00a8
        L_0x00a7:
            r2 = 0
        L_0x00a8:
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForSticker(r3, r4, r2)
            goto L_0x008d
        L_0x00ad:
            if (r6 == 0) goto L_0x00b5
            boolean r2 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r11)
            if (r2 != 0) goto L_0x00e4
        L_0x00b5:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoSticker(r4)
            if (r2 != 0) goto L_0x00e4
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r4)
            if (r2 == 0) goto L_0x00c2
            goto L_0x00e4
        L_0x00c2:
            if (r5 == 0) goto L_0x00d6
            int r2 = r5.imageType
            if (r2 != r11) goto L_0x00d6
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r6 = 0
            java.lang.String r4 = "50_50"
            java.lang.String r7 = "tgs"
            r3 = r5
            r5 = r7
            r7 = r14
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
            goto L_0x00fe
        L_0x00d6:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r6 = 0
            java.lang.String r4 = "50_50"
            java.lang.String r7 = "webp"
            r3 = r5
            r5 = r7
            r7 = r14
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
            goto L_0x00fe
        L_0x00e4:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r4)
            r6 = 0
            r7 = 0
            java.lang.String r4 = "50_50"
            r8 = r14
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (org.telegram.messenger.ImageLocation) r5, (java.lang.String) r6, (int) r7, (java.lang.Object) r8)
            goto L_0x00fe
        L_0x00f3:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r3 = 0
            r4 = 0
            r6 = 0
            java.lang.String r5 = "webp"
            r7 = r14
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
        L_0x00fe:
            if (r10 != 0) goto L_0x0101
            return
        L_0x0101:
            r2 = 8
            switch(r15) {
                case 0: goto L_0x0316;
                case 1: goto L_0x02cb;
                case 2: goto L_0x0257;
                case 3: goto L_0x0242;
                case 4: goto L_0x022d;
                case 5: goto L_0x0218;
                case 6: goto L_0x0190;
                case 7: goto L_0x0108;
                default: goto L_0x0106;
            }
        L_0x0106:
            goto L_0x0386
        L_0x0108:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isPremium()
            r3 = 2131626448(0x7f0e09d0, float:1.8880132E38)
            java.lang.String r4 = "LimitReachedFavoriteGifs"
            if (r2 != 0) goto L_0x0165
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            boolean r2 = r2.premiumLocked
            if (r2 != 0) goto L_0x0165
            android.widget.TextView r2 = r0.titleTextView
            java.lang.Object[] r5 = new java.lang.Object[r11]
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r6 = r6.savedGifsLimitDefault
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r9] = r6
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            r2.setText(r3)
            r2 = 2131626449(0x7f0e09d1, float:1.8880135E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r4 = r4.savedGifsLimitPremium
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r9] = r4
            java.lang.String r4 = "LimitReachedFavoriteGifsSubtitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda1
            r3.<init>(r13)
            java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.replaceSingleTag(r2, r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r2.setText(r1)
            goto L_0x0386
        L_0x0165:
            android.widget.TextView r1 = r0.titleTextView
            java.lang.Object[] r2 = new java.lang.Object[r11]
            int r5 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r5 = r5.savedGifsLimitPremium
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2[r9] = r5
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626450(0x7f0e09d2, float:1.8880137E38)
            java.lang.Object[] r3 = new java.lang.Object[r9]
            java.lang.String r4 = "LimitReachedFavoriteGifsSubtitlePremium"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0386
        L_0x0190:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isPremium()
            r3 = 2131626451(0x7f0e09d3, float:1.8880139E38)
            java.lang.String r4 = "LimitReachedFavoriteStickers"
            if (r2 != 0) goto L_0x01ed
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            boolean r2 = r2.premiumLocked
            if (r2 != 0) goto L_0x01ed
            android.widget.TextView r2 = r0.titleTextView
            java.lang.Object[] r5 = new java.lang.Object[r11]
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r6 = r6.stickersFavedLimitDefault
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r9] = r6
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            r2.setText(r3)
            r2 = 2131626452(0x7f0e09d4, float:1.888014E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r4 = r4.stickersFavedLimitPremium
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r9] = r4
            java.lang.String r4 = "LimitReachedFavoriteStickersSubtitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda0
            r3.<init>(r13)
            java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.replaceSingleTag(r2, r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r2.setText(r1)
            goto L_0x0386
        L_0x01ed:
            android.widget.TextView r1 = r0.titleTextView
            java.lang.Object[] r2 = new java.lang.Object[r11]
            int r5 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r5 = r5.stickersFavedLimitPremium
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2[r9] = r5
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626453(0x7f0e09d5, float:1.8880143E38)
            java.lang.Object[] r3 = new java.lang.Object[r9]
            java.lang.String r4 = "LimitReachedFavoriteStickersSubtitlePremium"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0386
        L_0x0218:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131624310(0x7f0e0176, float:1.8875796E38)
            java.lang.String r4 = "AddedToFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x0386
        L_0x022d:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627955(0x7f0e0fb3, float:1.888319E38)
            java.lang.String r4 = "RemovedFromFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x0386
        L_0x0242:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627956(0x7f0e0fb4, float:1.8883191E38)
            java.lang.String r4 = "RemovedFromRecent"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x0386
        L_0x0257:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x027f
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624274(0x7f0e0152, float:1.8875723E38)
            java.lang.String r3 = "AddMasksInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624275(0x7f0e0153, float:1.8875725E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "AddMasksInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0386
        L_0x027f:
            boolean r1 = r10.emojis
            if (r1 == 0) goto L_0x02a7
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624270(0x7f0e014e, float:1.8875715E38)
            java.lang.String r3 = "AddEmojiInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624271(0x7f0e014f, float:1.8875717E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "AddEmojiInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0386
        L_0x02a7:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624291(0x7f0e0163, float:1.8875758E38)
            java.lang.String r3 = "AddStickersInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624292(0x7f0e0164, float:1.887576E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "AddStickersInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0386
        L_0x02cb:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x02f3
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626582(0x7f0e0a56, float:1.8880404E38)
            java.lang.String r3 = "MasksArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626583(0x7f0e0a57, float:1.8880406E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "MasksArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0386
        L_0x02f3:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131628495(0x7f0e11cf, float:1.8884284E38)
            java.lang.String r3 = "StickersArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131628496(0x7f0e11d0, float:1.8884286E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "StickersArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0386
        L_0x0316:
            boolean r1 = r10.masks
            if (r1 == 0) goto L_0x033d
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626591(0x7f0e0a5f, float:1.8880423E38)
            java.lang.String r3 = "MasksRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626592(0x7f0e0a60, float:1.8880425E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "MasksRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0386
        L_0x033d:
            boolean r1 = r10.emojis
            if (r1 == 0) goto L_0x0364
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131625639(0x7f0e06a7, float:1.8878492E38)
            java.lang.String r3 = "EmojiRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131625640(0x7f0e06a8, float:1.8878494E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "EmojiRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x0386
        L_0x0364:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131628502(0x7f0e11d6, float:1.8884298E38)
            java.lang.String r3 = "StickersRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131628503(0x7f0e11d7, float:1.88843E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = r10.title
            r3[r9] = r4
            java.lang.String r4 = "StickersRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
        L_0x0386:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerSetBulletinLayout.<init>(android.content.Context, org.telegram.tgnet.TLObject, int, org.telegram.tgnet.TLRPC$Document, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$0(Context context) {
        Activity findActivity = AndroidUtilities.findActivity(context);
        if (findActivity instanceof LaunchActivity) {
            ((LaunchActivity) findActivity).lambda$runLinkRequest$59(new PremiumPreviewFragment(LimitReachedBottomSheet.limitTypeToServerString(10)));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$1(Context context) {
        Activity findActivity = AndroidUtilities.findActivity(context);
        if (findActivity instanceof LaunchActivity) {
            ((LaunchActivity) findActivity).lambda$runLinkRequest$59(new PremiumPreviewFragment(LimitReachedBottomSheet.limitTypeToServerString(9)));
        }
    }
}
