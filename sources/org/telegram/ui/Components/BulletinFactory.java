package org.telegram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.Bulletin;

public final class BulletinFactory {
    private final FrameLayout containerLayout;
    private final BaseFragment fragment;

    public static BulletinFactory of(BaseFragment baseFragment) {
        return new BulletinFactory(baseFragment);
    }

    public static BulletinFactory of(FrameLayout frameLayout) {
        return new BulletinFactory(frameLayout);
    }

    public static boolean canShowBulletin(BaseFragment baseFragment) {
        return (baseFragment == null || baseFragment.getParentActivity() == null || baseFragment.getLayoutContainer() == null) ? false : true;
    }

    public enum FileType {
        PHOTO("PhotoSavedHint", NUM, r5),
        PHOTOS("PhotosSavedHint", r7),
        VIDEO("VideoSavedHint", NUM, r5),
        VIDEOS("VideosSavedHint", r7),
        MEDIA("MediaSavedHint", r7),
        PHOTO_TO_DOWNLOADS("PhotoSavedToDownloadsHint", NUM, r16),
        VIDEO_TO_DOWNLOADS("VideoSavedToDownloadsHint", NUM, r16),
        GIF("GifSavedToDownloadsHint", r5),
        AUDIO("AudioSavedHint", NUM, r11),
        AUDIOS("AudiosSavedHint", r11),
        UNKNOWN("FileSavedHint", NUM, r5),
        UNKNOWNS("FilesSavedHint", r5);
        
        /* access modifiers changed from: private */
        public final Icon icon;
        private final String localeKey;
        private final int localeRes;
        private final boolean plural;

        private FileType(String str, int i, Icon icon2) {
            this.localeKey = str;
            this.localeRes = i;
            this.icon = icon2;
            this.plural = false;
        }

        private FileType(String str, Icon icon2) {
            this.localeKey = str;
            this.icon = icon2;
            this.localeRes = 0;
            this.plural = true;
        }

        /* access modifiers changed from: private */
        public String getText(int i) {
            if (this.plural) {
                return LocaleController.formatPluralString(this.localeKey, i);
            }
            return LocaleController.getString(this.localeKey, this.localeRes);
        }

        private enum Icon {
            SAVED_TO_DOWNLOADS(NUM, 2, "Box", "Arrow"),
            SAVED_TO_GALLERY(NUM, 0, "Box", "Arrow", "Mask", "Arrow 2", "Splash"),
            SAVED_TO_MUSIC(NUM, 2, "Box", "Arrow");
            
            /* access modifiers changed from: private */
            public final String[] layers;
            /* access modifiers changed from: private */
            public final int paddingBottom;
            /* access modifiers changed from: private */
            public final int resId;

            private Icon(int i, int i2, String... strArr) {
                this.resId = i;
                this.paddingBottom = i2;
                this.layers = strArr;
            }
        }
    }

    private BulletinFactory(BaseFragment baseFragment) {
        this.fragment = baseFragment;
        this.containerLayout = null;
    }

    private BulletinFactory(FrameLayout frameLayout) {
        this.containerLayout = frameLayout;
        this.fragment = null;
    }

    public Bulletin createSimpleBulletin(int i, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext());
        lottieLayout.setAnimation(i, 36, 36, new String[0]);
        lottieLayout.textView.setText(str);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 1500);
    }

    public Bulletin createDownloadBulletin(FileType fileType) {
        return createDownloadBulletin(fileType, 1);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i) {
        return createDownloadBulletin(fileType, i, 0, 0);
    }

    public Bulletin createReportSent() {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext());
        lottieLayout.setAnimation(NUM, new String[0]);
        lottieLayout.textView.setText(LocaleController.getString("ReportChatSent", NUM));
        return create(lottieLayout, 1500);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i, int i2, int i3) {
        Bulletin.LottieLayout lottieLayout;
        if (i2 == 0 || i3 == 0) {
            lottieLayout = new Bulletin.LottieLayout(getContext());
        } else {
            lottieLayout = new Bulletin.LottieLayout(getContext(), i2, i3);
        }
        lottieLayout.setAnimation(fileType.icon.resId, fileType.icon.layers);
        lottieLayout.textView.setText(fileType.getText(i));
        if (fileType.icon.paddingBottom != 0) {
            lottieLayout.setIconPaddingBottom(fileType.icon.paddingBottom);
        }
        return create(lottieLayout, 1500);
    }

    public Bulletin createErrorBulletin(String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext());
        lottieLayout.setAnimation(NUM, new String[0]);
        lottieLayout.textView.setText(str);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin() {
        return createCopyLinkBulletin(false);
    }

    public Bulletin createCopyBulletin(String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext());
        lottieLayout.setAnimation(NUM, 36, 36, "NULL ROTATION", "Back", "Front");
        lottieLayout.textView.setText(str);
        return create(lottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin(boolean z) {
        if (z) {
            Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext());
            twoLineLottieLayout.setAnimation(NUM, 36, 36, "Wibe", "Circle");
            twoLineLottieLayout.titleTextView.setText(LocaleController.getString("LinkCopied", NUM));
            twoLineLottieLayout.subtitleTextView.setText(LocaleController.getString("LinkCopiedPrivateInfo", NUM));
            return create(twoLineLottieLayout, 2750);
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext());
        lottieLayout.setAnimation(NUM, 36, 36, "Wibe", "Circle");
        lottieLayout.textView.setText(LocaleController.getString("LinkCopied", NUM));
        return create(lottieLayout, 1500);
    }

    private Bulletin create(Bulletin.Layout layout, int i) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            return Bulletin.make(baseFragment, layout, i);
        }
        return Bulletin.make(this.containerLayout, layout, i);
    }

    private Context getContext() {
        BaseFragment baseFragment = this.fragment;
        return baseFragment != null ? baseFragment.getParentActivity() : this.containerLayout.getContext();
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0085  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createMuteBulletin(org.telegram.ui.ActionBar.BaseFragment r10, int r11) {
        /*
            org.telegram.ui.Components.Bulletin$LottieLayout r0 = new org.telegram.ui.Components.Bulletin$LottieLayout
            android.app.Activity r1 = r10.getParentActivity()
            r0.<init>(r1)
            java.lang.String r1 = "Hours"
            r2 = 2131626586(0x7f0e0a5a, float:1.8880412E38)
            java.lang.String r3 = "NotificationsMutedForHint"
            r4 = 4
            r5 = 3
            r6 = 2
            r7 = 0
            r8 = 1
            if (r11 == 0) goto L_0x0058
            if (r11 == r8) goto L_0x0049
            if (r11 == r6) goto L_0x003a
            if (r11 == r5) goto L_0x0030
            if (r11 != r4) goto L_0x002a
            r11 = 2131626607(0x7f0e0a6f, float:1.8880455E38)
            java.lang.String r1 = "NotificationsUnmutedHint"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r1, r11)
            r1 = 0
            goto L_0x0065
        L_0x002a:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            r10.<init>()
            throw r10
        L_0x0030:
            r11 = 2131626587(0x7f0e0a5b, float:1.8880414E38)
            java.lang.String r1 = "NotificationsMutedHint"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r1, r11)
            goto L_0x0064
        L_0x003a:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            java.lang.String r1 = "Days"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r6)
            r11[r7] = r1
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r3, r2, r11)
            goto L_0x0064
        L_0x0049:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            r9 = 8
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r9)
            r11[r7] = r1
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r3, r2, r11)
            goto L_0x0064
        L_0x0058:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r8)
            r11[r7] = r1
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r3, r2, r11)
        L_0x0064:
            r1 = 1
        L_0x0065:
            if (r1 == 0) goto L_0x0085
            r1 = 2131558448(0x7f0d0030, float:1.8742212E38)
            r2 = 5
            java.lang.String[] r2 = new java.lang.String[r2]
            java.lang.String r3 = "Body Main"
            r2[r7] = r3
            java.lang.String r3 = "Body Top"
            r2[r8] = r3
            java.lang.String r3 = "Line"
            r2[r6] = r3
            java.lang.String r3 = "Curve Big"
            r2[r5] = r3
            java.lang.String r3 = "Curve Small"
            r2[r4] = r3
            r0.setAnimation(r1, r2)
            goto L_0x009d
        L_0x0085:
            r1 = 2131558453(0x7f0d0035, float:1.8742222E38)
            java.lang.String[] r2 = new java.lang.String[r4]
            java.lang.String r3 = "BODY"
            r2[r7] = r3
            java.lang.String r3 = "Wibe Big"
            r2[r8] = r3
            java.lang.String r3 = "Wibe Big 3"
            r2[r6] = r3
            java.lang.String r3 = "Wibe Small"
            r2[r5] = r3
            r0.setAnimation(r1, r2)
        L_0x009d:
            android.widget.TextView r1 = r0.textView
            r1.setText(r11)
            r11 = 1500(0x5dc, float:2.102E-42)
            org.telegram.ui.Components.Bulletin r10 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r10, (org.telegram.ui.Components.Bulletin.Layout) r0, (int) r11)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createMuteBulletin(org.telegram.ui.ActionBar.BaseFragment, int):org.telegram.ui.Components.Bulletin");
    }

    public static Bulletin createMuteBulletin(BaseFragment baseFragment, boolean z) {
        return createMuteBulletin(baseFragment, z ? 3 : 4);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: org.telegram.ui.Components.Bulletin$TwoLineLottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createUnpinAllMessagesBulletin(org.telegram.ui.ActionBar.BaseFragment r8, int r9, boolean r10, java.lang.Runnable r11, java.lang.Runnable r12) {
        /*
            android.app.Activity r0 = r8.getParentActivity()
            if (r0 != 0) goto L_0x000d
            if (r12 == 0) goto L_0x000b
            r12.run()
        L_0x000b:
            r8 = 0
            return r8
        L_0x000d:
            java.lang.String r0 = "Line"
            java.lang.String r1 = "Pin"
            r2 = 0
            r3 = 2
            r4 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r5 = 1
            r6 = 28
            if (r10 == 0) goto L_0x004a
            org.telegram.ui.Components.Bulletin$TwoLineLottieLayout r9 = new org.telegram.ui.Components.Bulletin$TwoLineLottieLayout
            android.app.Activity r10 = r8.getParentActivity()
            r9.<init>(r10)
            java.lang.String[] r10 = new java.lang.String[r3]
            r10[r2] = r1
            r10[r5] = r0
            r9.setAnimation(r4, r6, r6, r10)
            android.widget.TextView r10 = r9.titleTextView
            r0 = 2131627061(0x7f0e0CLASSNAME, float:1.8881376E38)
            java.lang.String r1 = "PinnedMessagesHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r10.setText(r0)
            android.widget.TextView r10 = r9.subtitleTextView
            r0 = 2131627062(0x7f0e0CLASSNAME, float:1.8881378E38)
            java.lang.String r1 = "PinnedMessagesHiddenInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r10.setText(r0)
            goto L_0x0068
        L_0x004a:
            org.telegram.ui.Components.Bulletin$LottieLayout r10 = new org.telegram.ui.Components.Bulletin$LottieLayout
            android.app.Activity r7 = r8.getParentActivity()
            r10.<init>(r7)
            java.lang.String[] r3 = new java.lang.String[r3]
            r3[r2] = r1
            r3[r5] = r0
            r10.setAnimation(r4, r6, r6, r3)
            android.widget.TextView r0 = r10.textView
            java.lang.String r1 = "MessagesUnpinned"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r1, r9)
            r0.setText(r9)
            r9 = r10
        L_0x0068:
            org.telegram.ui.Components.Bulletin$UndoButton r10 = new org.telegram.ui.Components.Bulletin$UndoButton
            android.app.Activity r0 = r8.getParentActivity()
            r10.<init>(r0, r5)
            org.telegram.ui.Components.Bulletin$UndoButton r10 = r10.setUndoAction(r11)
            org.telegram.ui.Components.Bulletin$UndoButton r10 = r10.setDelayedAction(r12)
            r9.setButton(r10)
            r10 = 5000(0x1388, float:7.006E-42)
            org.telegram.ui.Components.Bulletin r8 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r8, (org.telegram.ui.Components.Bulletin.Layout) r9, (int) r10)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createUnpinAllMessagesBulletin(org.telegram.ui.ActionBar.BaseFragment, int, boolean, java.lang.Runnable, java.lang.Runnable):org.telegram.ui.Components.Bulletin");
    }

    public static Bulletin createSaveToGalleryBulletin(BaseFragment baseFragment, boolean z) {
        return of(baseFragment).createDownloadBulletin(z ? FileType.VIDEO : FileType.PHOTO);
    }

    public static Bulletin createSaveToGalleryBulletin(FrameLayout frameLayout, boolean z, int i, int i2) {
        return of(frameLayout).createDownloadBulletin(z ? FileType.VIDEO : FileType.PHOTO, 1, i, i2);
    }

    public static Bulletin createPromoteToAdminBulletin(BaseFragment baseFragment, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity());
        lottieLayout.setAnimation(NUM, "Shield");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserSetAsAdminHint", NUM, str)));
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }

    public static Bulletin createRemoveFromChatBulletin(BaseFragment baseFragment, TLRPC$User tLRPC$User, String str) {
        String str2;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity());
        lottieLayout.setAnimation(NUM, "Hand");
        if (tLRPC$User.deleted) {
            str2 = LocaleController.formatString("HiddenName", NUM, new Object[0]);
        } else {
            str2 = tLRPC$User.first_name;
        }
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserRemovedFromChatHint", NUM, str2, str)));
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }

    public static Bulletin createBanBulletin(BaseFragment baseFragment, boolean z) {
        String str;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity());
        if (z) {
            lottieLayout.setAnimation(NUM, "Hand");
            str = LocaleController.getString("UserBlocked", NUM);
        } else {
            lottieLayout.setAnimation(NUM, "Main", "Finger 1", "Finger 2", "Finger 3", "Finger 4");
            str = LocaleController.getString("UserUnblocked", NUM);
        }
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(str));
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }

    public static Bulletin createCopyLinkBulletin(BaseFragment baseFragment) {
        return of(baseFragment).createCopyLinkBulletin();
    }

    public static Bulletin createCopyLinkBulletin(FrameLayout frameLayout) {
        return of(frameLayout).createCopyLinkBulletin();
    }

    public static Bulletin createPinMessageBulletin(BaseFragment baseFragment) {
        return createPinMessageBulletin(baseFragment, true, (Runnable) null, (Runnable) null);
    }

    public static Bulletin createUnpinMessageBulletin(BaseFragment baseFragment, Runnable runnable, Runnable runnable2) {
        return createPinMessageBulletin(baseFragment, false, runnable, runnable2);
    }

    private static Bulletin createPinMessageBulletin(BaseFragment baseFragment, boolean z, Runnable runnable, Runnable runnable2) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity());
        lottieLayout.setAnimation(z ? NUM : NUM, 28, 28, "Pin", "Line");
        lottieLayout.textView.setText(LocaleController.getString(z ? "MessagePinnedHint" : "MessageUnpinnedHint", z ? NUM : NUM));
        if (!z) {
            lottieLayout.setButton(new Bulletin.UndoButton(baseFragment.getParentActivity(), true).setUndoAction(runnable).setDelayedAction(runnable2));
        }
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, z ? 1500 : 5000);
    }
}
