package org.telegram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;

public final class BulletinFactory {
    private final FrameLayout containerLayout;
    private final BaseFragment fragment;
    private final Theme.ResourcesProvider resourcesProvider;

    public static BulletinFactory of(BaseFragment baseFragment) {
        return new BulletinFactory(baseFragment);
    }

    public static BulletinFactory of(FrameLayout frameLayout, Theme.ResourcesProvider resourcesProvider2) {
        return new BulletinFactory(frameLayout, resourcesProvider2);
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
        Theme.ResourcesProvider resourcesProvider2 = null;
        this.containerLayout = null;
        this.resourcesProvider = baseFragment != null ? baseFragment.getResourceProvider() : resourcesProvider2;
    }

    private BulletinFactory(FrameLayout frameLayout, Theme.ResourcesProvider resourcesProvider2) {
        this.containerLayout = frameLayout;
        this.fragment = null;
        this.resourcesProvider = resourcesProvider2;
    }

    public Bulletin createSimpleBulletin(int i, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setAnimation(i, 36, 36, new String[0]);
        lottieLayout.textView.setText(str);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 1500);
    }

    public Bulletin createDownloadBulletin(FileType fileType) {
        return createDownloadBulletin(fileType, this.resourcesProvider);
    }

    public Bulletin createDownloadBulletin(FileType fileType, Theme.ResourcesProvider resourcesProvider2) {
        return createDownloadBulletin(fileType, 1, resourcesProvider2);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i, Theme.ResourcesProvider resourcesProvider2) {
        return createDownloadBulletin(fileType, i, 0, 0, resourcesProvider2);
    }

    public Bulletin createReportSent(Theme.ResourcesProvider resourcesProvider2) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider2);
        lottieLayout.setAnimation(NUM, new String[0]);
        lottieLayout.textView.setText(LocaleController.getString("ReportChatSent", NUM));
        return create(lottieLayout, 1500);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i, int i2, int i3) {
        return createDownloadBulletin(fileType, i, i2, i3, (Theme.ResourcesProvider) null);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i, int i2, int i3, Theme.ResourcesProvider resourcesProvider2) {
        Bulletin.LottieLayout lottieLayout;
        if (i2 == 0 || i3 == 0) {
            lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider2);
        } else {
            lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider2, i2, i3);
        }
        lottieLayout.setAnimation(fileType.icon.resId, fileType.icon.layers);
        lottieLayout.textView.setText(fileType.getText(i));
        if (fileType.icon.paddingBottom != 0) {
            lottieLayout.setIconPaddingBottom(fileType.icon.paddingBottom);
        }
        return create(lottieLayout, 1500);
    }

    public Bulletin createErrorBulletin(CharSequence charSequence) {
        return createErrorBulletin(charSequence, (Theme.ResourcesProvider) null);
    }

    public Bulletin createErrorBulletin(CharSequence charSequence, Theme.ResourcesProvider resourcesProvider2) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider2);
        lottieLayout.setAnimation(NUM, new String[0]);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin() {
        return createCopyLinkBulletin(false, this.resourcesProvider);
    }

    public Bulletin createCopyBulletin(String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), (Theme.ResourcesProvider) null);
        lottieLayout.setAnimation(NUM, 36, 36, "NULL ROTATION", "Back", "Front");
        lottieLayout.textView.setText(str);
        return create(lottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin(boolean z, Theme.ResourcesProvider resourcesProvider2) {
        if (z) {
            Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), resourcesProvider2);
            twoLineLottieLayout.setAnimation(NUM, 36, 36, "Wibe", "Circle");
            twoLineLottieLayout.titleTextView.setText(LocaleController.getString("LinkCopied", NUM));
            twoLineLottieLayout.subtitleTextView.setText(LocaleController.getString("LinkCopiedPrivateInfo", NUM));
            return create(twoLineLottieLayout, 2750);
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider2);
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

    public static Bulletin createMuteBulletin(BaseFragment baseFragment, int i) {
        return createMuteBulletin(baseFragment, i, (Theme.ResourcesProvider) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0085  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createMuteBulletin(org.telegram.ui.ActionBar.BaseFragment r9, int r10, org.telegram.ui.ActionBar.Theme.ResourcesProvider r11) {
        /*
            org.telegram.ui.Components.Bulletin$LottieLayout r0 = new org.telegram.ui.Components.Bulletin$LottieLayout
            android.app.Activity r1 = r9.getParentActivity()
            r0.<init>(r1, r11)
            java.lang.String r11 = "Hours"
            r1 = 2131626705(0x7f0e0ad1, float:1.8880654E38)
            java.lang.String r2 = "NotificationsMutedForHint"
            r3 = 4
            r4 = 3
            r5 = 2
            r6 = 0
            r7 = 1
            if (r10 == 0) goto L_0x0058
            if (r10 == r7) goto L_0x0049
            if (r10 == r5) goto L_0x003a
            if (r10 == r4) goto L_0x0030
            if (r10 != r3) goto L_0x002a
            r10 = 2131626726(0x7f0e0ae6, float:1.8880696E38)
            java.lang.String r11 = "NotificationsUnmutedHint"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r11 = 0
            goto L_0x0065
        L_0x002a:
            java.lang.IllegalArgumentException r9 = new java.lang.IllegalArgumentException
            r9.<init>()
            throw r9
        L_0x0030:
            r10 = 2131626706(0x7f0e0ad2, float:1.8880656E38)
            java.lang.String r11 = "NotificationsMutedHint"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            goto L_0x0064
        L_0x003a:
            java.lang.Object[] r10 = new java.lang.Object[r7]
            java.lang.String r11 = "Days"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r5)
            r10[r6] = r11
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatString(r2, r1, r10)
            goto L_0x0064
        L_0x0049:
            java.lang.Object[] r10 = new java.lang.Object[r7]
            r8 = 8
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r8)
            r10[r6] = r11
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatString(r2, r1, r10)
            goto L_0x0064
        L_0x0058:
            java.lang.Object[] r10 = new java.lang.Object[r7]
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r7)
            r10[r6] = r11
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatString(r2, r1, r10)
        L_0x0064:
            r11 = 1
        L_0x0065:
            if (r11 == 0) goto L_0x0085
            r11 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r1 = 5
            java.lang.String[] r1 = new java.lang.String[r1]
            java.lang.String r2 = "Body Main"
            r1[r6] = r2
            java.lang.String r2 = "Body Top"
            r1[r7] = r2
            java.lang.String r2 = "Line"
            r1[r5] = r2
            java.lang.String r2 = "Curve Big"
            r1[r4] = r2
            java.lang.String r2 = "Curve Small"
            r1[r3] = r2
            r0.setAnimation(r11, r1)
            goto L_0x009d
        L_0x0085:
            r11 = 2131558459(0x7f0d003b, float:1.8742234E38)
            java.lang.String[] r1 = new java.lang.String[r3]
            java.lang.String r2 = "BODY"
            r1[r6] = r2
            java.lang.String r2 = "Wibe Big"
            r1[r7] = r2
            java.lang.String r2 = "Wibe Big 3"
            r1[r5] = r2
            java.lang.String r2 = "Wibe Small"
            r1[r4] = r2
            r0.setAnimation(r11, r1)
        L_0x009d:
            android.widget.TextView r11 = r0.textView
            r11.setText(r10)
            r10 = 1500(0x5dc, float:2.102E-42)
            org.telegram.ui.Components.Bulletin r9 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r9, (org.telegram.ui.Components.Bulletin.Layout) r0, (int) r10)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createMuteBulletin(org.telegram.ui.ActionBar.BaseFragment, int, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.Components.Bulletin");
    }

    public static Bulletin createMuteBulletin(BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        return createMuteBulletin(baseFragment, z ? 3 : 4, resourcesProvider2);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: org.telegram.ui.Components.Bulletin$TwoLineLottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createUnpinAllMessagesBulletin(org.telegram.ui.ActionBar.BaseFragment r8, int r9, boolean r10, java.lang.Runnable r11, java.lang.Runnable r12, org.telegram.ui.ActionBar.Theme.ResourcesProvider r13) {
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
            r4 = 2131558460(0x7f0d003c, float:1.8742236E38)
            r5 = 1
            r6 = 28
            if (r10 == 0) goto L_0x004a
            org.telegram.ui.Components.Bulletin$TwoLineLottieLayout r9 = new org.telegram.ui.Components.Bulletin$TwoLineLottieLayout
            android.app.Activity r10 = r8.getParentActivity()
            r9.<init>(r10, r13)
            java.lang.String[] r10 = new java.lang.String[r3]
            r10[r2] = r1
            r10[r5] = r0
            r9.setAnimation(r4, r6, r6, r10)
            android.widget.TextView r10 = r9.titleTextView
            r0 = 2131627184(0x7f0e0cb0, float:1.8881625E38)
            java.lang.String r1 = "PinnedMessagesHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r10.setText(r0)
            android.widget.TextView r10 = r9.subtitleTextView
            r0 = 2131627185(0x7f0e0cb1, float:1.8881627E38)
            java.lang.String r1 = "PinnedMessagesHiddenInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r10.setText(r0)
            goto L_0x0068
        L_0x004a:
            org.telegram.ui.Components.Bulletin$LottieLayout r10 = new org.telegram.ui.Components.Bulletin$LottieLayout
            android.app.Activity r7 = r8.getParentActivity()
            r10.<init>(r7, r13)
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
            r10.<init>(r0, r5, r13)
            org.telegram.ui.Components.Bulletin$UndoButton r10 = r10.setUndoAction(r11)
            org.telegram.ui.Components.Bulletin$UndoButton r10 = r10.setDelayedAction(r12)
            r9.setButton(r10)
            r10 = 5000(0x1388, float:7.006E-42)
            org.telegram.ui.Components.Bulletin r8 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r8, (org.telegram.ui.Components.Bulletin.Layout) r9, (int) r10)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createUnpinAllMessagesBulletin(org.telegram.ui.ActionBar.BaseFragment, int, boolean, java.lang.Runnable, java.lang.Runnable, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.Components.Bulletin");
    }

    public static Bulletin createSaveToGalleryBulletin(BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        return of(baseFragment).createDownloadBulletin(z ? FileType.VIDEO : FileType.PHOTO, resourcesProvider2);
    }

    public static Bulletin createSaveToGalleryBulletin(FrameLayout frameLayout, boolean z, int i, int i2) {
        return of(frameLayout, (Theme.ResourcesProvider) null).createDownloadBulletin(z ? FileType.VIDEO : FileType.PHOTO, 1, i, i2);
    }

    public static Bulletin createPromoteToAdminBulletin(BaseFragment baseFragment, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), (Theme.ResourcesProvider) null);
        lottieLayout.setAnimation(NUM, "Shield");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserSetAsAdminHint", NUM, str)));
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }

    public static Bulletin createRemoveFromChatBulletin(BaseFragment baseFragment, TLRPC$User tLRPC$User, String str) {
        String str2;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), (Theme.ResourcesProvider) null);
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
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), (Theme.ResourcesProvider) null);
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
        return of(frameLayout, (Theme.ResourcesProvider) null).createCopyLinkBulletin();
    }

    public static Bulletin createPinMessageBulletin(BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider2) {
        return createPinMessageBulletin(baseFragment, true, (Runnable) null, (Runnable) null, resourcesProvider2);
    }

    public static Bulletin createUnpinMessageBulletin(BaseFragment baseFragment, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider2) {
        return createPinMessageBulletin(baseFragment, false, runnable, runnable2, resourcesProvider2);
    }

    private static Bulletin createPinMessageBulletin(BaseFragment baseFragment, boolean z, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider2) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), resourcesProvider2);
        lottieLayout.setAnimation(z ? NUM : NUM, 28, 28, "Pin", "Line");
        lottieLayout.textView.setText(LocaleController.getString(z ? "MessagePinnedHint" : "MessageUnpinnedHint", z ? NUM : NUM));
        if (!z) {
            lottieLayout.setButton(new Bulletin.UndoButton(baseFragment.getParentActivity(), true, resourcesProvider2).setUndoAction(runnable).setDelayedAction(runnable2));
        }
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, z ? 1500 : 5000);
    }
}
