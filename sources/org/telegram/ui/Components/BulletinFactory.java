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
        GIF("GifSavedHint", NUM, Icon.SAVED_TO_GIFS),
        GIF_TO_DOWNLOADS("GifSavedToDownloadsHint", NUM, r5),
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
                return LocaleController.formatPluralString(this.localeKey, i, new Object[0]);
            }
            return LocaleController.getString(this.localeKey, this.localeRes);
        }

        private enum Icon {
            SAVED_TO_DOWNLOADS(NUM, 2, "Box", "Arrow"),
            SAVED_TO_GALLERY(NUM, 0, "Box", "Arrow", "Mask", "Arrow 2", "Splash"),
            SAVED_TO_MUSIC(NUM, 2, "Box", "Arrow"),
            SAVED_TO_GIFS(NUM, 0, "gif");
            
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

    public Bulletin createErrorBulletinSubtitle(CharSequence charSequence, CharSequence charSequence2, Theme.ResourcesProvider resourcesProvider2) {
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), resourcesProvider2);
        twoLineLottieLayout.setAnimation(NUM, new String[0]);
        twoLineLottieLayout.titleTextView.setText(charSequence);
        twoLineLottieLayout.subtitleTextView.setText(charSequence2);
        return create(twoLineLottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin() {
        return createCopyLinkBulletin(false, this.resourcesProvider);
    }

    public Bulletin createCopyBulletin(String str) {
        return createCopyBulletin(str, (Theme.ResourcesProvider) null);
    }

    public Bulletin createCopyBulletin(String str, Theme.ResourcesProvider resourcesProvider2) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), (Theme.ResourcesProvider) null);
        lottieLayout.setAnimation(NUM, 36, 36, "NULL ROTATION", "Back", "Front");
        lottieLayout.textView.setText(str);
        return create(lottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin(boolean z, Theme.ResourcesProvider resourcesProvider2) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
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

    public Bulletin createCopyLinkBulletin(String str, Theme.ResourcesProvider resourcesProvider2) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider2);
        lottieLayout.setAnimation(NUM, 36, 36, "Wibe", "Circle");
        lottieLayout.textView.setText(str);
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
        return createMuteBulletin(baseFragment, i, 0, (Theme.ResourcesProvider) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0089  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createMuteBulletin(org.telegram.ui.ActionBar.BaseFragment r10, int r11, int r12, org.telegram.ui.ActionBar.Theme.ResourcesProvider r13) {
        /*
            org.telegram.ui.Components.Bulletin$LottieLayout r0 = new org.telegram.ui.Components.Bulletin$LottieLayout
            android.app.Activity r1 = r10.getParentActivity()
            r0.<init>(r1, r13)
            java.lang.String r13 = "Hours"
            r1 = 5
            r2 = 4
            r3 = 3
            r4 = 2131627036(0x7f0e0c1c, float:1.8881325E38)
            java.lang.String r5 = "NotificationsMutedForHint"
            r6 = 2
            r7 = 0
            r8 = 1
            if (r11 == 0) goto L_0x006e
            if (r11 == r8) goto L_0x005d
            if (r11 == r6) goto L_0x004c
            if (r11 == r3) goto L_0x0042
            if (r11 == r2) goto L_0x0036
            if (r11 != r1) goto L_0x0030
            java.lang.Object[] r11 = new java.lang.Object[r8]
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatTTLString(r12)
            r11[r7] = r12
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r5, r4, r11)
            r12 = 1
            goto L_0x007d
        L_0x0030:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            r10.<init>()
            throw r10
        L_0x0036:
            r11 = 2131627061(0x7f0e0CLASSNAME, float:1.8881376E38)
            java.lang.String r12 = "NotificationsUnmutedHint"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r12 = 0
            r13 = 0
            goto L_0x007e
        L_0x0042:
            r11 = 2131627037(0x7f0e0c1d, float:1.8881327E38)
            java.lang.String r12 = "NotificationsMutedHint"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            goto L_0x007c
        L_0x004c:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            java.lang.Object[] r12 = new java.lang.Object[r7]
            java.lang.String r13 = "Days"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r13, r6, r12)
            r11[r7] = r12
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r5, r4, r11)
            goto L_0x007c
        L_0x005d:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            r12 = 8
            java.lang.Object[] r9 = new java.lang.Object[r7]
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r13, r12, r9)
            r11[r7] = r12
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r5, r4, r11)
            goto L_0x007c
        L_0x006e:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            java.lang.Object[] r12 = new java.lang.Object[r7]
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r13, r8, r12)
            r11[r7] = r12
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r5, r4, r11)
        L_0x007c:
            r12 = 0
        L_0x007d:
            r13 = 1
        L_0x007e:
            if (r12 == 0) goto L_0x0089
            r12 = 2131558483(0x7f0d0053, float:1.8742283E38)
            java.lang.String[] r13 = new java.lang.String[r7]
            r0.setAnimation(r12, r13)
            goto L_0x00c0
        L_0x0089:
            if (r13 == 0) goto L_0x00a8
            r12 = 2131558462(0x7f0d003e, float:1.874224E38)
            java.lang.String[] r13 = new java.lang.String[r1]
            java.lang.String r1 = "Body Main"
            r13[r7] = r1
            java.lang.String r1 = "Body Top"
            r13[r8] = r1
            java.lang.String r1 = "Line"
            r13[r6] = r1
            java.lang.String r1 = "Curve Big"
            r13[r3] = r1
            java.lang.String r1 = "Curve Small"
            r13[r2] = r1
            r0.setAnimation(r12, r13)
            goto L_0x00c0
        L_0x00a8:
            r12 = 2131558468(0x7f0d0044, float:1.8742253E38)
            java.lang.String[] r13 = new java.lang.String[r2]
            java.lang.String r1 = "BODY"
            r13[r7] = r1
            java.lang.String r1 = "Wibe Big"
            r13[r8] = r1
            java.lang.String r1 = "Wibe Big 3"
            r13[r6] = r1
            java.lang.String r1 = "Wibe Small"
            r13[r3] = r1
            r0.setAnimation(r12, r13)
        L_0x00c0:
            android.widget.TextView r12 = r0.textView
            r12.setText(r11)
            r11 = 1500(0x5dc, float:2.102E-42)
            org.telegram.ui.Components.Bulletin r10 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r10, (org.telegram.ui.Components.Bulletin.Layout) r0, (int) r11)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createMuteBulletin(org.telegram.ui.ActionBar.BaseFragment, int, int, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.Components.Bulletin");
    }

    public static Bulletin createMuteBulletin(BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        return createMuteBulletin(baseFragment, z ? 3 : 4, 0, resourcesProvider2);
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
            r2 = 2
            r3 = 2131558469(0x7f0d0045, float:1.8742255E38)
            r4 = 1
            r5 = 0
            r6 = 28
            if (r10 == 0) goto L_0x004a
            org.telegram.ui.Components.Bulletin$TwoLineLottieLayout r9 = new org.telegram.ui.Components.Bulletin$TwoLineLottieLayout
            android.app.Activity r10 = r8.getParentActivity()
            r9.<init>(r10, r13)
            java.lang.String[] r10 = new java.lang.String[r2]
            r10[r5] = r1
            r10[r4] = r0
            r9.setAnimation(r3, r6, r6, r10)
            android.widget.TextView r10 = r9.titleTextView
            r0 = 2131627545(0x7f0e0e19, float:1.8882357E38)
            java.lang.String r1 = "PinnedMessagesHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r10.setText(r0)
            android.widget.TextView r10 = r9.subtitleTextView
            r0 = 2131627546(0x7f0e0e1a, float:1.888236E38)
            java.lang.String r1 = "PinnedMessagesHiddenInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r10.setText(r0)
            goto L_0x006a
        L_0x004a:
            org.telegram.ui.Components.Bulletin$LottieLayout r10 = new org.telegram.ui.Components.Bulletin$LottieLayout
            android.app.Activity r7 = r8.getParentActivity()
            r10.<init>(r7, r13)
            java.lang.String[] r2 = new java.lang.String[r2]
            r2[r5] = r1
            r2[r4] = r0
            r10.setAnimation(r3, r6, r6, r2)
            android.widget.TextView r0 = r10.textView
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = "MessagesUnpinned"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r2, r9, r1)
            r0.setText(r9)
            r9 = r10
        L_0x006a:
            org.telegram.ui.Components.Bulletin$UndoButton r10 = new org.telegram.ui.Components.Bulletin$UndoButton
            android.app.Activity r0 = r8.getParentActivity()
            r10.<init>(r0, r4, r13)
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
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        lottieLayout.setAnimation(NUM, "Shield");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserSetAsAdminHint", NUM, str)));
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }

    public static Bulletin createAddedAsAdminBulletin(BaseFragment baseFragment, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        lottieLayout.setAnimation(NUM, "Shield");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserAddedAsAdminHint", NUM, str)));
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x0107  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createForwardedBulletin(android.content.Context r5, android.widget.FrameLayout r6, int r7, long r8, int r10, int r11, int r12) {
        /*
            org.telegram.ui.Components.Bulletin$LottieLayout r0 = new org.telegram.ui.Components.Bulletin$LottieLayout
            r1 = 0
            r0.<init>(r5, r1, r11, r12)
            r5 = 300(0x12c, float:4.2E-43)
            r11 = 2131558452(0x7f0d0034, float:1.874222E38)
            r12 = 30
            r1 = 0
            r2 = 1
            if (r7 > r2) goto L_0x00c5
            int r7 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
            long r3 = r7.clientUserId
            int r7 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r7 != 0) goto L_0x0045
            if (r10 > r2) goto L_0x002d
            r5 = 2131626030(0x7f0e082e, float:1.8879285E38)
            java.lang.String r7 = "FwdMessageToSavedMessages"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            android.text.SpannableStringBuilder r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5)
            goto L_0x003a
        L_0x002d:
            r5 = 2131626034(0x7f0e0832, float:1.8879293E38)
            java.lang.String r7 = "FwdMessagesToSavedMessages"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            android.text.SpannableStringBuilder r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5)
        L_0x003a:
            r7 = 2131558513(0x7f0d0071, float:1.8742344E38)
            java.lang.String[] r8 = new java.lang.String[r1]
            r0.setAnimation(r7, r12, r12, r8)
            r7 = -1
            goto L_0x0100
        L_0x0045:
            boolean r7 = org.telegram.messenger.DialogObject.isChatDialog(r8)
            if (r7 == 0) goto L_0x0084
            int r7 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            long r8 = -r8
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r8)
            if (r10 > r2) goto L_0x0070
            r8 = 2131626029(0x7f0e082d, float:1.8879283E38)
            java.lang.Object[] r9 = new java.lang.Object[r2]
            java.lang.String r7 = r7.title
            r9[r1] = r7
            java.lang.String r7 = "FwdMessageToGroup"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r9)
            android.text.SpannableStringBuilder r7 = org.telegram.messenger.AndroidUtilities.replaceTags(r7)
            goto L_0x00bf
        L_0x0070:
            r8 = 2131626033(0x7f0e0831, float:1.887929E38)
            java.lang.Object[] r9 = new java.lang.Object[r2]
            java.lang.String r7 = r7.title
            r9[r1] = r7
            java.lang.String r7 = "FwdMessagesToGroup"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r9)
            android.text.SpannableStringBuilder r7 = org.telegram.messenger.AndroidUtilities.replaceTags(r7)
            goto L_0x00bf
        L_0x0084:
            int r7 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r8)
            if (r10 > r2) goto L_0x00aa
            r8 = 2131626031(0x7f0e082f, float:1.8879287E38)
            java.lang.Object[] r9 = new java.lang.Object[r2]
            java.lang.String r7 = org.telegram.messenger.UserObject.getFirstName(r7)
            r9[r1] = r7
            java.lang.String r7 = "FwdMessageToUser"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r9)
            android.text.SpannableStringBuilder r7 = org.telegram.messenger.AndroidUtilities.replaceTags(r7)
            goto L_0x00bf
        L_0x00aa:
            r8 = 2131626035(0x7f0e0833, float:1.8879295E38)
            java.lang.Object[] r9 = new java.lang.Object[r2]
            java.lang.String r7 = org.telegram.messenger.UserObject.getFirstName(r7)
            r9[r1] = r7
            java.lang.String r7 = "FwdMessagesToUser"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r9)
            android.text.SpannableStringBuilder r7 = org.telegram.messenger.AndroidUtilities.replaceTags(r7)
        L_0x00bf:
            java.lang.String[] r8 = new java.lang.String[r1]
            r0.setAnimation(r11, r12, r12, r8)
            goto L_0x00fd
        L_0x00c5:
            java.lang.String r8 = "Chats"
            if (r10 > r2) goto L_0x00e1
            r9 = 2131626028(0x7f0e082c, float:1.887928E38)
            java.lang.Object[] r10 = new java.lang.Object[r2]
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r8, r7, r2)
            r10[r1] = r7
            java.lang.String r7 = "FwdMessageToChats"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r9, r10)
            android.text.SpannableStringBuilder r7 = org.telegram.messenger.AndroidUtilities.replaceTags(r7)
            goto L_0x00f8
        L_0x00e1:
            r9 = 2131626032(0x7f0e0830, float:1.8879289E38)
            java.lang.Object[] r10 = new java.lang.Object[r2]
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r8, r7, r2)
            r10[r1] = r7
            java.lang.String r7 = "FwdMessagesToChats"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r9, r10)
            android.text.SpannableStringBuilder r7 = org.telegram.messenger.AndroidUtilities.replaceTags(r7)
        L_0x00f8:
            java.lang.String[] r8 = new java.lang.String[r1]
            r0.setAnimation(r11, r12, r12, r8)
        L_0x00fd:
            r5 = r7
            r7 = 300(0x12c, float:4.2E-43)
        L_0x0100:
            android.widget.TextView r8 = r0.textView
            r8.setText(r5)
            if (r7 <= 0) goto L_0x0110
            org.telegram.ui.Components.BulletinFactory$$ExternalSyntheticLambda0 r5 = new org.telegram.ui.Components.BulletinFactory$$ExternalSyntheticLambda0
            r5.<init>(r0)
            long r7 = (long) r7
            r0.postDelayed(r5, r7)
        L_0x0110:
            r5 = 1500(0x5dc, float:2.102E-42)
            org.telegram.ui.Components.Bulletin r5 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r6, (org.telegram.ui.Components.Bulletin.Layout) r0, (int) r5)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createForwardedBulletin(android.content.Context, android.widget.FrameLayout, int, long, int, int, int):org.telegram.ui.Components.Bulletin");
    }

    public static Bulletin createRemoveFromChatBulletin(BaseFragment baseFragment, TLRPC$User tLRPC$User, String str) {
        String str2;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
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
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
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

    public static Bulletin createSoundEnabledBulletin(BaseFragment baseFragment, int i, Theme.ResourcesProvider resourcesProvider2) {
        String str;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), resourcesProvider2);
        boolean z = true;
        if (i == 0) {
            str = LocaleController.getString("SoundOnHint", NUM);
        } else if (i == 1) {
            str = LocaleController.getString("SoundOffHint", NUM);
            z = false;
        } else {
            throw new IllegalArgumentException();
        }
        if (z) {
            lottieLayout.setAnimation(NUM, new String[0]);
        } else {
            lottieLayout.setAnimation(NUM, new String[0]);
        }
        lottieLayout.textView.setText(str);
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }
}
