package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Context;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;

public class StickerSetBulletinLayout extends Bulletin.TwoLineLayout {
    public static final int TYPE_ADDED = 2;
    public static final int TYPE_ADDED_TO_FAVORITES = 5;
    public static final int TYPE_ARCHIVED = 1;
    public static final int TYPE_EMPTY = -1;
    public static final int TYPE_REMOVED = 0;
    public static final int TYPE_REMOVED_FROM_FAVORITES = 4;
    public static final int TYPE_REMOVED_FROM_RECENT = 3;
    public static final int TYPE_REPLACED_TO_FAVORITES = 6;
    public static final int TYPE_REPLACED_TO_FAVORITES_GIFS = 7;

    public @interface Type {
    }

    public StickerSetBulletinLayout(Context context, TLObject setObject, int type) {
        this(context, setObject, type, (TLRPC.Document) null, (Theme.ResourcesProvider) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public StickerSetBulletinLayout(android.content.Context r18, org.telegram.tgnet.TLObject r19, int r20, org.telegram.tgnet.TLRPC.Document r21, org.telegram.ui.ActionBar.Theme.ResourcesProvider r22) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r9 = r19
            r10 = r22
            r0.<init>(r1, r10)
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet
            r11 = 0
            if (r2 == 0) goto L_0x002a
            r2 = r9
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r2
            org.telegram.tgnet.TLRPC$StickerSet r3 = r2.set
            java.util.ArrayList r4 = r2.documents
            if (r4 == 0) goto L_0x0026
            boolean r5 = r4.isEmpty()
            if (r5 != 0) goto L_0x0026
            java.lang.Object r5 = r4.get(r11)
            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC.Document) r5
            goto L_0x0027
        L_0x0026:
            r5 = 0
        L_0x0027:
            r13 = r3
            r12 = r5
            goto L_0x0077
        L_0x002a:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.StickerSetCovered
            if (r2 == 0) goto L_0x004f
            r2 = r9
            org.telegram.tgnet.TLRPC$StickerSetCovered r2 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r2
            org.telegram.tgnet.TLRPC$StickerSet r3 = r2.set
            org.telegram.tgnet.TLRPC$Document r4 = r2.cover
            if (r4 == 0) goto L_0x003a
            org.telegram.tgnet.TLRPC$Document r4 = r2.cover
            goto L_0x004c
        L_0x003a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.covers
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x004b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.covers
            java.lang.Object r4 = r4.get(r11)
            org.telegram.tgnet.TLRPC$Document r4 = (org.telegram.tgnet.TLRPC.Document) r4
            goto L_0x004c
        L_0x004b:
            r4 = 0
        L_0x004c:
            r13 = r3
            r12 = r4
            goto L_0x0077
        L_0x004f:
            if (r21 != 0) goto L_0x0073
            if (r9 == 0) goto L_0x0073
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r2 != 0) goto L_0x0058
            goto L_0x0073
        L_0x0058:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Invalid type of the given setObject: "
            r3.append(r4)
            java.lang.Class r4 = r19.getClass()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        L_0x0073:
            r3 = 0
            r12 = r21
            r13 = r3
        L_0x0077:
            r14 = 1
            if (r12 == 0) goto L_0x0109
            r2 = 90
            if (r13 != 0) goto L_0x0080
            r3 = 0
            goto L_0x0086
        L_0x0080:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r13.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r2)
        L_0x0086:
            if (r3 != 0) goto L_0x008b
            r3 = r12
            r15 = r3
            goto L_0x008c
        L_0x008b:
            r15 = r3
        L_0x008c:
            boolean r3 = r15 instanceof org.telegram.tgnet.TLRPC.Document
            if (r3 == 0) goto L_0x009d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r12.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r2)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r2, (org.telegram.tgnet.TLRPC.Document) r12)
            r8 = r2
            goto L_0x00bd
        L_0x009d:
            r2 = r15
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2
            r3 = 0
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.StickerSetCovered
            if (r4 == 0) goto L_0x00ad
            r4 = r9
            org.telegram.tgnet.TLRPC$StickerSetCovered r4 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r4
            org.telegram.tgnet.TLRPC$StickerSet r4 = r4.set
            int r3 = r4.thumb_version
            goto L_0x00b8
        L_0x00ad:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet
            if (r4 == 0) goto L_0x00b8
            r4 = r9
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r4
            org.telegram.tgnet.TLRPC$StickerSet r4 = r4.set
            int r3 = r4.thumb_version
        L_0x00b8:
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForSticker(r2, r12, r3)
            r8 = r4
        L_0x00bd:
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC.Document
            if (r2 == 0) goto L_0x00c7
            boolean r2 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r12, r14)
            if (r2 != 0) goto L_0x00f6
        L_0x00c7:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoSticker(r12)
            if (r2 != 0) goto L_0x00f6
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r12)
            if (r2 == 0) goto L_0x00d4
            goto L_0x00f6
        L_0x00d4:
            if (r8 == 0) goto L_0x00e8
            int r2 = r8.imageType
            if (r2 != r14) goto L_0x00e8
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r6 = 0
            java.lang.String r4 = "50_50"
            java.lang.String r5 = "tgs"
            r3 = r8
            r7 = r19
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
            goto L_0x0108
        L_0x00e8:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r6 = 0
            java.lang.String r4 = "50_50"
            java.lang.String r5 = "webp"
            r3 = r8
            r7 = r19
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
            goto L_0x0108
        L_0x00f6:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r12)
            r6 = 0
            r7 = 0
            java.lang.String r4 = "50_50"
            r5 = r8
            r16 = r8
            r8 = r19
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (org.telegram.messenger.ImageLocation) r5, (java.lang.String) r6, (int) r7, (java.lang.Object) r8)
        L_0x0108:
            goto L_0x0115
        L_0x0109:
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            r3 = 0
            r4 = 0
            r6 = 0
            java.lang.String r5 = "webp"
            r7 = r19
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r7)
        L_0x0115:
            r2 = 8
            switch(r20) {
                case 0: goto L_0x0301;
                case 1: goto L_0x02b7;
                case 2: goto L_0x026b;
                case 3: goto L_0x0256;
                case 4: goto L_0x0241;
                case 5: goto L_0x022c;
                case 6: goto L_0x01a4;
                case 7: goto L_0x011c;
                default: goto L_0x011a;
            }
        L_0x011a:
            goto L_0x034b
        L_0x011c:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isPremium()
            r3 = 2131626411(0x7f0e09ab, float:1.8880057E38)
            java.lang.String r4 = "LimitReachedFavoriteGifs"
            if (r2 != 0) goto L_0x0179
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            boolean r2 = r2.premiumLocked
            if (r2 != 0) goto L_0x0179
            android.widget.TextView r2 = r0.titleTextView
            java.lang.Object[] r5 = new java.lang.Object[r14]
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r6 = r6.savedGifsLimitDefault
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r11] = r6
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            r2.setText(r3)
            r2 = 2131626412(0x7f0e09ac, float:1.888006E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r4 = r4.savedGifsLimitPremium
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r11] = r4
            java.lang.String r4 = "LimitReachedFavoriteGifsSubtitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda1
            r3.<init>(r1)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceSingleTag(r2, r3)
            android.widget.TextView r3 = r0.subtitleTextView
            r3.setText(r2)
            goto L_0x034b
        L_0x0179:
            android.widget.TextView r2 = r0.titleTextView
            java.lang.Object[] r5 = new java.lang.Object[r14]
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r6 = r6.savedGifsLimitPremium
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r11] = r6
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            r2.setText(r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r3 = 2131626413(0x7f0e09ad, float:1.8880061E38)
            java.lang.Object[] r4 = new java.lang.Object[r11]
            java.lang.String r5 = "LimitReachedFavoriteGifsSubtitlePremium"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            r2.setText(r3)
            goto L_0x034b
        L_0x01a4:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isPremium()
            r3 = 2131626414(0x7f0e09ae, float:1.8880064E38)
            java.lang.String r4 = "LimitReachedFavoriteStickers"
            if (r2 != 0) goto L_0x0201
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            boolean r2 = r2.premiumLocked
            if (r2 != 0) goto L_0x0201
            android.widget.TextView r2 = r0.titleTextView
            java.lang.Object[] r5 = new java.lang.Object[r14]
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r6 = r6.stickersFavedLimitDefault
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r11] = r6
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            r2.setText(r3)
            r2 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r4 = r4.stickersFavedLimitPremium
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r11] = r4
            java.lang.String r4 = "LimitReachedFavoriteStickersSubtitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda0
            r3.<init>(r1)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceSingleTag(r2, r3)
            android.widget.TextView r3 = r0.subtitleTextView
            r3.setText(r2)
            goto L_0x034b
        L_0x0201:
            android.widget.TextView r2 = r0.titleTextView
            java.lang.Object[] r5 = new java.lang.Object[r14]
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r6 = r6.stickersFavedLimitPremium
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r11] = r6
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            r2.setText(r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r3 = 2131626416(0x7f0e09b0, float:1.8880068E38)
            java.lang.Object[] r4 = new java.lang.Object[r11]
            java.lang.String r5 = "LimitReachedFavoriteStickersSubtitlePremium"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            r2.setText(r3)
            goto L_0x034b
        L_0x022c:
            android.widget.TextView r3 = r0.titleTextView
            r4 = 2131624303(0x7f0e016f, float:1.8875782E38)
            java.lang.String r5 = "AddedToFavorites"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
            android.widget.TextView r3 = r0.subtitleTextView
            r3.setVisibility(r2)
            goto L_0x034b
        L_0x0241:
            android.widget.TextView r3 = r0.titleTextView
            r4 = 2131627905(0x7f0e0var_, float:1.8883088E38)
            java.lang.String r5 = "RemovedFromFavorites"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
            android.widget.TextView r3 = r0.subtitleTextView
            r3.setVisibility(r2)
            goto L_0x034b
        L_0x0256:
            android.widget.TextView r3 = r0.titleTextView
            r4 = 2131627906(0x7f0e0var_, float:1.888309E38)
            java.lang.String r5 = "RemovedFromRecent"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
            android.widget.TextView r3 = r0.subtitleTextView
            r3.setVisibility(r2)
            goto L_0x034b
        L_0x026b:
            boolean r2 = r13.masks
            if (r2 == 0) goto L_0x0293
            android.widget.TextView r2 = r0.titleTextView
            r3 = 2131624267(0x7f0e014b, float:1.8875709E38)
            java.lang.String r4 = "AddMasksInstalled"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r3 = 2131624268(0x7f0e014c, float:1.887571E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            java.lang.String r5 = r13.title
            r4[r11] = r5
            java.lang.String r5 = "AddMasksInstalledInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            r2.setText(r3)
            goto L_0x034b
        L_0x0293:
            android.widget.TextView r2 = r0.titleTextView
            r3 = 2131624284(0x7f0e015c, float:1.8875743E38)
            java.lang.String r4 = "AddStickersInstalled"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r3 = 2131624285(0x7f0e015d, float:1.8875745E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            java.lang.String r5 = r13.title
            r4[r11] = r5
            java.lang.String r5 = "AddStickersInstalledInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            r2.setText(r3)
            goto L_0x034b
        L_0x02b7:
            boolean r2 = r13.masks
            if (r2 == 0) goto L_0x02de
            android.widget.TextView r2 = r0.titleTextView
            r3 = 2131626545(0x7f0e0a31, float:1.888033E38)
            java.lang.String r4 = "MasksArchived"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r3 = 2131626546(0x7f0e0a32, float:1.8880331E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            java.lang.String r5 = r13.title
            r4[r11] = r5
            java.lang.String r5 = "MasksArchivedInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            r2.setText(r3)
            goto L_0x034b
        L_0x02de:
            android.widget.TextView r2 = r0.titleTextView
            r3 = 2131628445(0x7f0e119d, float:1.8884183E38)
            java.lang.String r4 = "StickersArchived"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r3 = 2131628446(0x7f0e119e, float:1.8884185E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            java.lang.String r5 = r13.title
            r4[r11] = r5
            java.lang.String r5 = "StickersArchivedInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            r2.setText(r3)
            goto L_0x034b
        L_0x0301:
            boolean r2 = r13.masks
            if (r2 == 0) goto L_0x0328
            android.widget.TextView r2 = r0.titleTextView
            r3 = 2131626554(0x7f0e0a3a, float:1.8880347E38)
            java.lang.String r4 = "MasksRemoved"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r3 = 2131626555(0x7f0e0a3b, float:1.888035E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            java.lang.String r5 = r13.title
            r4[r11] = r5
            java.lang.String r5 = "MasksRemovedInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            r2.setText(r3)
            goto L_0x034b
        L_0x0328:
            android.widget.TextView r2 = r0.titleTextView
            r3 = 2131628452(0x7f0e11a4, float:1.8884197E38)
            java.lang.String r4 = "StickersRemoved"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            android.widget.TextView r2 = r0.subtitleTextView
            r3 = 2131628453(0x7f0e11a5, float:1.88842E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            java.lang.String r5 = r13.title
            r4[r11] = r5
            java.lang.String r5 = "StickersRemovedInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            r2.setText(r3)
        L_0x034b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerSetBulletinLayout.<init>(android.content.Context, org.telegram.tgnet.TLObject, int, org.telegram.tgnet.TLRPC$Document, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    static /* synthetic */ void lambda$new$0(Context context) {
        Activity activity = AndroidUtilities.findActivity(context);
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new PremiumPreviewFragment(LimitReachedBottomSheet.limitTypeToServerString(10)));
        }
    }

    static /* synthetic */ void lambda$new$1(Context context) {
        Activity activity = AndroidUtilities.findActivity(context);
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new PremiumPreviewFragment(LimitReachedBottomSheet.limitTypeToServerString(9)));
        }
    }
}
