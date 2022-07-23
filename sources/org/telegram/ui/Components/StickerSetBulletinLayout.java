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
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0116 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0117  */
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
            if (r2 == 0) goto L_0x0026
            r4 = r8
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r4
            org.telegram.tgnet.TLRPC$StickerSet r5 = r4.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r4.documents
            if (r4 == 0) goto L_0x0024
            boolean r6 = r4.isEmpty()
            if (r6 != 0) goto L_0x0024
            java.lang.Object r4 = r4.get(r9)
            org.telegram.tgnet.TLRPC$Document r4 = (org.telegram.tgnet.TLRPC$Document) r4
            goto L_0x006d
        L_0x0024:
            r4 = r3
            goto L_0x006d
        L_0x0026:
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r4 == 0) goto L_0x0046
            r4 = r8
            org.telegram.tgnet.TLRPC$StickerSetCovered r4 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r4
            org.telegram.tgnet.TLRPC$StickerSet r5 = r4.set
            org.telegram.tgnet.TLRPC$Document r6 = r4.cover
            if (r6 == 0) goto L_0x0035
            r4 = r6
            goto L_0x006d
        L_0x0035:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r4.covers
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x0024
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r4.covers
            java.lang.Object r4 = r4.get(r9)
            org.telegram.tgnet.TLRPC$Document r4 = (org.telegram.tgnet.TLRPC$Document) r4
            goto L_0x006d
        L_0x0046:
            if (r16 != 0) goto L_0x006a
            if (r8 == 0) goto L_0x006a
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r4 != 0) goto L_0x004f
            goto L_0x006a
        L_0x004f:
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
        L_0x006a:
            r4 = r16
            r5 = r3
        L_0x006d:
            r10 = 1
            if (r5 != 0) goto L_0x0084
            if (r4 == 0) goto L_0x0084
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r6)
            org.telegram.tgnet.TLRPC$InputStickerSet r7 = org.telegram.messenger.MessageObject.getInputStickerSet((org.telegram.tgnet.TLRPC$Document) r4)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r6 = r6.getStickerSet(r7, r10)
            if (r6 == 0) goto L_0x0084
            org.telegram.tgnet.TLRPC$StickerSet r5 = r6.set
        L_0x0084:
            r11 = r5
            if (r4 == 0) goto L_0x0109
            r5 = 90
            if (r11 != 0) goto L_0x008c
            goto L_0x0092
        L_0x008c:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r11.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r5)
        L_0x0092:
            if (r3 != 0) goto L_0x0095
            r3 = r4
        L_0x0095:
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC$Document
            if (r6 == 0) goto L_0x00a5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r4.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r5)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r4)
        L_0x00a3:
            r5 = r2
            goto L_0x00c3
        L_0x00a5:
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r5 == 0) goto L_0x00b3
            r2 = r8
            org.telegram.tgnet.TLRPC$StickerSetCovered r2 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r2
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            int r2 = r2.thumb_version
            goto L_0x00be
        L_0x00b3:
            if (r2 == 0) goto L_0x00bd
            r2 = r8
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r2
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            int r2 = r2.thumb_version
            goto L_0x00be
        L_0x00bd:
            r2 = 0
        L_0x00be:
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForSticker(r3, r4, r2)
            goto L_0x00a3
        L_0x00c3:
            if (r6 == 0) goto L_0x00cb
            boolean r2 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r10)
            if (r2 != 0) goto L_0x00fa
        L_0x00cb:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoSticker(r4)
            if (r2 != 0) goto L_0x00fa
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r4)
            if (r2 == 0) goto L_0x00d8
            goto L_0x00fa
        L_0x00d8:
            if (r5 == 0) goto L_0x00ec
            int r2 = r5.imageType
            if (r2 != r10) goto L_0x00ec
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r6 = 0
            java.lang.String r4 = "50_50"
            java.lang.String r7 = "tgs"
            r3 = r5
            r5 = r7
            r7 = r14
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
            goto L_0x0114
        L_0x00ec:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r6 = 0
            java.lang.String r4 = "50_50"
            java.lang.String r7 = "webp"
            r3 = r5
            r5 = r7
            r7 = r14
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
            goto L_0x0114
        L_0x00fa:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r4)
            r6 = 0
            r7 = 0
            java.lang.String r4 = "50_50"
            r8 = r14
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (org.telegram.messenger.ImageLocation) r5, (java.lang.String) r6, (int) r7, (java.lang.Object) r8)
            goto L_0x0114
        L_0x0109:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r3 = 0
            r4 = 0
            r6 = 0
            java.lang.String r5 = "webp"
            r7 = r14
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
        L_0x0114:
            if (r11 != 0) goto L_0x0117
            return
        L_0x0117:
            r2 = 8
            switch(r15) {
                case 0: goto L_0x032c;
                case 1: goto L_0x02e1;
                case 2: goto L_0x026d;
                case 3: goto L_0x0258;
                case 4: goto L_0x0243;
                case 5: goto L_0x022e;
                case 6: goto L_0x01a6;
                case 7: goto L_0x011e;
                default: goto L_0x011c;
            }
        L_0x011c:
            goto L_0x039c
        L_0x011e:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isPremium()
            r3 = 2131626458(0x7f0e09da, float:1.8880153E38)
            java.lang.String r4 = "LimitReachedFavoriteGifs"
            if (r2 != 0) goto L_0x017b
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            boolean r2 = r2.premiumLocked
            if (r2 != 0) goto L_0x017b
            android.widget.TextView r2 = r0.titleTextView
            java.lang.Object[] r5 = new java.lang.Object[r10]
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r6 = r6.savedGifsLimitDefault
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r9] = r6
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            r2.setText(r3)
            r2 = 2131626459(0x7f0e09db, float:1.8880155E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
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
            goto L_0x039c
        L_0x017b:
            android.widget.TextView r1 = r0.titleTextView
            java.lang.Object[] r2 = new java.lang.Object[r10]
            int r5 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r5 = r5.savedGifsLimitPremium
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2[r9] = r5
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.Object[] r3 = new java.lang.Object[r9]
            java.lang.String r4 = "LimitReachedFavoriteGifsSubtitlePremium"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x039c
        L_0x01a6:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isPremium()
            r3 = 2131626461(0x7f0e09dd, float:1.8880159E38)
            java.lang.String r4 = "LimitReachedFavoriteStickers"
            if (r2 != 0) goto L_0x0203
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            boolean r2 = r2.premiumLocked
            if (r2 != 0) goto L_0x0203
            android.widget.TextView r2 = r0.titleTextView
            java.lang.Object[] r5 = new java.lang.Object[r10]
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r6 = r6.stickersFavedLimitDefault
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r9] = r6
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            r2.setText(r3)
            r2 = 2131626462(0x7f0e09de, float:1.888016E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
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
            goto L_0x039c
        L_0x0203:
            android.widget.TextView r1 = r0.titleTextView
            java.lang.Object[] r2 = new java.lang.Object[r10]
            int r5 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r5 = r5.stickersFavedLimitPremium
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2[r9] = r5
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626463(0x7f0e09df, float:1.8880163E38)
            java.lang.Object[] r3 = new java.lang.Object[r9]
            java.lang.String r4 = "LimitReachedFavoriteStickersSubtitlePremium"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x039c
        L_0x022e:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131624312(0x7f0e0178, float:1.88758E38)
            java.lang.String r4 = "AddedToFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x039c
        L_0x0243:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627967(0x7f0e0fbf, float:1.8883213E38)
            java.lang.String r4 = "RemovedFromFavorites"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x039c
        L_0x0258:
            android.widget.TextView r1 = r0.titleTextView
            r3 = 2131627968(0x7f0e0fc0, float:1.8883215E38)
            java.lang.String r4 = "RemovedFromRecent"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            goto L_0x039c
        L_0x026d:
            boolean r1 = r11.masks
            if (r1 == 0) goto L_0x0295
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624275(0x7f0e0153, float:1.8875725E38)
            java.lang.String r3 = "AddMasksInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624276(0x7f0e0154, float:1.8875727E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = r11.title
            r3[r9] = r4
            java.lang.String r4 = "AddMasksInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x039c
        L_0x0295:
            boolean r1 = r11.emojis
            if (r1 == 0) goto L_0x02bd
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624270(0x7f0e014e, float:1.8875715E38)
            java.lang.String r3 = "AddEmojiInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624271(0x7f0e014f, float:1.8875717E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = r11.title
            r3[r9] = r4
            java.lang.String r4 = "AddEmojiInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x039c
        L_0x02bd:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131624292(0x7f0e0164, float:1.887576E38)
            java.lang.String r3 = "AddStickersInstalled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131624293(0x7f0e0165, float:1.8875762E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = r11.title
            r3[r9] = r4
            java.lang.String r4 = "AddStickersInstalledInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x039c
        L_0x02e1:
            boolean r1 = r11.masks
            if (r1 == 0) goto L_0x0309
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626592(0x7f0e0a60, float:1.8880425E38)
            java.lang.String r3 = "MasksArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626593(0x7f0e0a61, float:1.8880427E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = r11.title
            r3[r9] = r4
            java.lang.String r4 = "MasksArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x039c
        L_0x0309:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131628509(0x7f0e11dd, float:1.8884313E38)
            java.lang.String r3 = "StickersArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131628510(0x7f0e11de, float:1.8884315E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = r11.title
            r3[r9] = r4
            java.lang.String r4 = "StickersArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x039c
        L_0x032c:
            boolean r1 = r11.masks
            if (r1 == 0) goto L_0x0353
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131626601(0x7f0e0a69, float:1.8880443E38)
            java.lang.String r3 = "MasksRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626602(0x7f0e0a6a, float:1.8880445E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = r11.title
            r3[r9] = r4
            java.lang.String r4 = "MasksRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x039c
        L_0x0353:
            boolean r1 = r11.emojis
            if (r1 == 0) goto L_0x037a
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131625646(0x7f0e06ae, float:1.8878506E38)
            java.lang.String r3 = "EmojiRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131625647(0x7f0e06af, float:1.8878508E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = r11.title
            r3[r9] = r4
            java.lang.String r4 = "EmojiRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
            goto L_0x039c
        L_0x037a:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131628516(0x7f0e11e4, float:1.8884327E38)
            java.lang.String r3 = "StickersRemoved"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131628517(0x7f0e11e5, float:1.8884329E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = r11.title
            r3[r9] = r4
            java.lang.String r4 = "StickersRemovedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r1.setText(r2)
        L_0x039c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerSetBulletinLayout.<init>(android.content.Context, org.telegram.tgnet.TLObject, int, org.telegram.tgnet.TLRPC$Document, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$0(Context context) {
        Activity findActivity = AndroidUtilities.findActivity(context);
        if (findActivity instanceof LaunchActivity) {
            ((LaunchActivity) findActivity).lambda$runLinkRequest$60(new PremiumPreviewFragment(LimitReachedBottomSheet.limitTypeToServerString(10)));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$1(Context context) {
        Activity findActivity = AndroidUtilities.findActivity(context);
        if (findActivity instanceof LaunchActivity) {
            ((LaunchActivity) findActivity).lambda$runLinkRequest$60(new PremiumPreviewFragment(LimitReachedBottomSheet.limitTypeToServerString(9)));
        }
    }
}
