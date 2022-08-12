package org.telegram.ui.Components;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.PremiumPreviewFragment;

public final class BulletinFactory {
    private final FrameLayout containerLayout;
    /* access modifiers changed from: private */
    public final BaseFragment fragment;
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
        PHOTO("PhotoSavedHint", r4, r13),
        PHOTOS("PhotosSavedHint", r13),
        VIDEO("VideoSavedHint", R.string.VideoSavedHint, r13),
        VIDEOS("VideosSavedHint", r13),
        MEDIA("MediaSavedHint", r13),
        PHOTO_TO_DOWNLOADS("PhotoSavedToDownloadsHint", r18, r13),
        VIDEO_TO_DOWNLOADS("VideoSavedToDownloadsHint", R.string.VideoSavedToDownloadsHint, r14),
        GIF("GifSavedHint", R.string.GifSavedHint, Icon.SAVED_TO_GIFS),
        GIF_TO_DOWNLOADS("GifSavedToDownloadsHint", R.string.GifSavedToDownloadsHint, r14),
        AUDIO("AudioSavedHint", r28, r9),
        AUDIOS("AudiosSavedHint", r9),
        UNKNOWN("FileSavedHint", R.string.FileSavedHint, r16),
        UNKNOWNS("FilesSavedHint", r16);
        
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
            SAVED_TO_DOWNLOADS(R.raw.ic_download, 2, "Box", "Arrow"),
            SAVED_TO_GALLERY(R.raw.ic_save_to_gallery, 0, "Box", "Arrow", "Mask", "Arrow 2", "Splash"),
            SAVED_TO_MUSIC(R.raw.ic_save_to_music, 2, "Box", "Arrow"),
            SAVED_TO_GIFS(R.raw.ic_save_to_gifs, 0, "gif");
            
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

    public Bulletin createSimpleBulletin(int i, CharSequence charSequence, CharSequence charSequence2, Runnable runnable) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setAnimation(i, 36, 36, new String[0]);
        lottieLayout.textView.setTextSize(1, 14.0f);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(3);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.setButton(new Bulletin.UndoButton(getContext(), true, this.resourcesProvider).setText(charSequence2).setUndoAction(runnable));
        return create(lottieLayout, 1500);
    }

    public Bulletin createEmojiBulletin(TLRPC$Document tLRPC$Document, CharSequence charSequence, CharSequence charSequence2, Runnable runnable) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setAnimation(tLRPC$Document, 36, 36, new String[0]);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setTextSize(1, 14.0f);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(3);
        lottieLayout.setButton(new Bulletin.UndoButton(getContext(), true, this.resourcesProvider).setText(charSequence2).setUndoAction(runnable));
        return create(lottieLayout, 2750);
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
        lottieLayout.setAnimation(R.raw.chats_infotip, new String[0]);
        lottieLayout.textView.setText(LocaleController.getString("ReportChatSent", R.string.ReportChatSent));
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
        lottieLayout.setAnimation(R.raw.chats_infotip, new String[0]);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 1500);
    }

    public Bulletin createRestrictVoiceMessagesPremiumBulletin() {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), (Theme.ResourcesProvider) null);
        lottieLayout.setAnimation(R.raw.voip_muted, new String[0]);
        String string = LocaleController.getString(R.string.PrivacyVoiceMessagesPremiumOnly);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
        int indexOf = string.indexOf(42);
        int lastIndexOf = string.lastIndexOf(42);
        spannableStringBuilder.replace(indexOf, lastIndexOf + 1, string.substring(indexOf + 1, lastIndexOf));
        spannableStringBuilder.setSpan(new ClickableSpan() {
            public void onClick(View view) {
                BulletinFactory.this.fragment.presentFragment(new PremiumPreviewFragment("settings"));
            }

            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setUnderlineText(false);
            }
        }, indexOf, lastIndexOf - 1, 33);
        lottieLayout.textView.setText(spannableStringBuilder);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 2750);
    }

    public Bulletin createErrorBulletinSubtitle(CharSequence charSequence, CharSequence charSequence2, Theme.ResourcesProvider resourcesProvider2) {
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), resourcesProvider2);
        twoLineLottieLayout.setAnimation(R.raw.chats_infotip, new String[0]);
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
        lottieLayout.setAnimation(R.raw.copy, 36, 36, "NULL ROTATION", "Back", "Front");
        lottieLayout.textView.setText(str);
        return create(lottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin(boolean z, Theme.ResourcesProvider resourcesProvider2) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        if (z) {
            Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), resourcesProvider2);
            twoLineLottieLayout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
            twoLineLottieLayout.titleTextView.setText(LocaleController.getString("LinkCopied", R.string.LinkCopied));
            twoLineLottieLayout.subtitleTextView.setText(LocaleController.getString("LinkCopiedPrivateInfo", R.string.LinkCopiedPrivateInfo));
            return create(twoLineLottieLayout, 2750);
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider2);
        lottieLayout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
        lottieLayout.textView.setText(LocaleController.getString("LinkCopied", R.string.LinkCopied));
        return create(lottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin(String str, Theme.ResourcesProvider resourcesProvider2) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider2);
        lottieLayout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
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

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x008b  */
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
            java.lang.String r4 = "NotificationsMutedForHint"
            r5 = 2
            r6 = 0
            r7 = 1
            if (r11 == 0) goto L_0x006f
            if (r11 == r7) goto L_0x005c
            if (r11 == r5) goto L_0x0049
            if (r11 == r3) goto L_0x0040
            if (r11 == r2) goto L_0x0035
            if (r11 != r1) goto L_0x002f
            int r11 = org.telegram.messenger.R.string.NotificationsMutedForHint
            java.lang.Object[] r13 = new java.lang.Object[r7]
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatTTLString(r12)
            r13[r6] = r12
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r4, r11, r13)
            r12 = 1
            goto L_0x0080
        L_0x002f:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            r10.<init>()
            throw r10
        L_0x0035:
            int r11 = org.telegram.messenger.R.string.NotificationsUnmutedHint
            java.lang.String r12 = "NotificationsUnmutedHint"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r12 = 0
            r13 = 0
            goto L_0x0081
        L_0x0040:
            int r11 = org.telegram.messenger.R.string.NotificationsMutedHint
            java.lang.String r12 = "NotificationsMutedHint"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            goto L_0x007f
        L_0x0049:
            int r11 = org.telegram.messenger.R.string.NotificationsMutedForHint
            java.lang.Object[] r12 = new java.lang.Object[r7]
            java.lang.Object[] r13 = new java.lang.Object[r6]
            java.lang.String r8 = "Days"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralString(r8, r5, r13)
            r12[r6] = r13
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r4, r11, r12)
            goto L_0x007f
        L_0x005c:
            int r11 = org.telegram.messenger.R.string.NotificationsMutedForHint
            java.lang.Object[] r12 = new java.lang.Object[r7]
            r8 = 8
            java.lang.Object[] r9 = new java.lang.Object[r6]
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralString(r13, r8, r9)
            r12[r6] = r13
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r4, r11, r12)
            goto L_0x007f
        L_0x006f:
            int r11 = org.telegram.messenger.R.string.NotificationsMutedForHint
            java.lang.Object[] r12 = new java.lang.Object[r7]
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralString(r13, r7, r8)
            r12[r6] = r13
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r4, r11, r12)
        L_0x007f:
            r12 = 0
        L_0x0080:
            r13 = 1
        L_0x0081:
            if (r12 == 0) goto L_0x008b
            int r12 = org.telegram.messenger.R.raw.mute_for
            java.lang.String[] r13 = new java.lang.String[r6]
            r0.setAnimation(r12, r13)
            goto L_0x00c0
        L_0x008b:
            if (r13 == 0) goto L_0x00a9
            int r12 = org.telegram.messenger.R.raw.ic_mute
            java.lang.String[] r13 = new java.lang.String[r1]
            java.lang.String r1 = "Body Main"
            r13[r6] = r1
            java.lang.String r1 = "Body Top"
            r13[r7] = r1
            java.lang.String r1 = "Line"
            r13[r5] = r1
            java.lang.String r1 = "Curve Big"
            r13[r3] = r1
            java.lang.String r1 = "Curve Small"
            r13[r2] = r1
            r0.setAnimation(r12, r13)
            goto L_0x00c0
        L_0x00a9:
            int r12 = org.telegram.messenger.R.raw.ic_unmute
            java.lang.String[] r13 = new java.lang.String[r2]
            java.lang.String r1 = "BODY"
            r13[r6] = r1
            java.lang.String r1 = "Wibe Big"
            r13[r7] = r1
            java.lang.String r1 = "Wibe Big 3"
            r13[r5] = r1
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: org.telegram.ui.Components.Bulletin$TwoLineLottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createUnpinAllMessagesBulletin(org.telegram.ui.ActionBar.BaseFragment r7, int r8, boolean r9, java.lang.Runnable r10, java.lang.Runnable r11, org.telegram.ui.ActionBar.Theme.ResourcesProvider r12) {
        /*
            android.app.Activity r0 = r7.getParentActivity()
            if (r0 != 0) goto L_0x000d
            if (r11 == 0) goto L_0x000b
            r11.run()
        L_0x000b:
            r7 = 0
            return r7
        L_0x000d:
            java.lang.String r0 = "Line"
            java.lang.String r1 = "Pin"
            r2 = 2
            r3 = 1
            r4 = 0
            r5 = 28
            if (r9 == 0) goto L_0x0047
            org.telegram.ui.Components.Bulletin$TwoLineLottieLayout r8 = new org.telegram.ui.Components.Bulletin$TwoLineLottieLayout
            android.app.Activity r9 = r7.getParentActivity()
            r8.<init>(r9, r12)
            int r9 = org.telegram.messenger.R.raw.ic_unpin
            java.lang.String[] r2 = new java.lang.String[r2]
            r2[r4] = r1
            r2[r3] = r0
            r8.setAnimation(r9, r5, r5, r2)
            android.widget.TextView r9 = r8.titleTextView
            int r0 = org.telegram.messenger.R.string.PinnedMessagesHidden
            java.lang.String r1 = "PinnedMessagesHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r9.setText(r0)
            android.widget.TextView r9 = r8.subtitleTextView
            int r0 = org.telegram.messenger.R.string.PinnedMessagesHiddenInfo
            java.lang.String r1 = "PinnedMessagesHiddenInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r9.setText(r0)
            goto L_0x0069
        L_0x0047:
            org.telegram.ui.Components.Bulletin$LottieLayout r9 = new org.telegram.ui.Components.Bulletin$LottieLayout
            android.app.Activity r6 = r7.getParentActivity()
            r9.<init>(r6, r12)
            int r6 = org.telegram.messenger.R.raw.ic_unpin
            java.lang.String[] r2 = new java.lang.String[r2]
            r2[r4] = r1
            r2[r3] = r0
            r9.setAnimation((int) r6, (int) r5, (int) r5, (java.lang.String[]) r2)
            android.widget.TextView r0 = r9.textView
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = "MessagesUnpinned"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r2, r8, r1)
            r0.setText(r8)
            r8 = r9
        L_0x0069:
            org.telegram.ui.Components.Bulletin$UndoButton r9 = new org.telegram.ui.Components.Bulletin$UndoButton
            android.app.Activity r0 = r7.getParentActivity()
            r9.<init>(r0, r3, r12)
            org.telegram.ui.Components.Bulletin$UndoButton r9 = r9.setUndoAction(r10)
            org.telegram.ui.Components.Bulletin$UndoButton r9 = r9.setDelayedAction(r11)
            r8.setButton(r9)
            r9 = 5000(0x1388, float:7.006E-42)
            org.telegram.ui.Components.Bulletin r7 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r7, (org.telegram.ui.Components.Bulletin.Layout) r8, (int) r9)
            return r7
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
        lottieLayout.setAnimation(R.raw.ic_admin, "Shield");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserSetAsAdminHint", R.string.UserSetAsAdminHint, str)));
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }

    public static Bulletin createAddedAsAdminBulletin(BaseFragment baseFragment, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        lottieLayout.setAnimation(R.raw.ic_admin, "Shield");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserAddedAsAdminHint", R.string.UserAddedAsAdminHint, str)));
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x00aa  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createInviteSentBulletin(android.content.Context r3, android.widget.FrameLayout r4, int r5, long r6, int r8, int r9, int r10) {
        /*
            org.telegram.ui.Components.Bulletin$LottieLayout r8 = new org.telegram.ui.Components.Bulletin$LottieLayout
            r0 = 0
            r8.<init>(r3, r0, r9, r10)
            r3 = 300(0x12c, float:4.2E-43)
            r9 = 1
            r10 = 30
            r0 = 0
            if (r5 > r9) goto L_0x0081
            int r5 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
            long r1 = r5.clientUserId
            int r5 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r5 != 0) goto L_0x002f
            int r3 = org.telegram.messenger.R.string.InvLinkToSavedMessages
            java.lang.String r5 = "InvLinkToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            int r5 = org.telegram.messenger.R.raw.saved_messages
            java.lang.String[] r6 = new java.lang.String[r0]
            r8.setAnimation((int) r5, (int) r10, (int) r10, (java.lang.String[]) r6)
            r5 = -1
            goto L_0x00a3
        L_0x002f:
            boolean r5 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r5 == 0) goto L_0x0057
            int r5 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            long r6 = -r6
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
            int r6 = org.telegram.messenger.R.string.InvLinkToGroup
            java.lang.Object[] r7 = new java.lang.Object[r9]
            java.lang.String r5 = r5.title
            r7[r0] = r5
            java.lang.String r5 = "InvLinkToGroup"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7)
            android.text.SpannableStringBuilder r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5)
            goto L_0x0079
        L_0x0057:
            int r5 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            int r6 = org.telegram.messenger.R.string.InvLinkToUser
            java.lang.Object[] r7 = new java.lang.Object[r9]
            java.lang.String r5 = org.telegram.messenger.UserObject.getFirstName(r5)
            r7[r0] = r5
            java.lang.String r5 = "InvLinkToUser"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7)
            android.text.SpannableStringBuilder r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5)
        L_0x0079:
            int r6 = org.telegram.messenger.R.raw.forward
            java.lang.String[] r7 = new java.lang.String[r0]
            r8.setAnimation((int) r6, (int) r10, (int) r10, (java.lang.String[]) r7)
            goto L_0x00a0
        L_0x0081:
            int r6 = org.telegram.messenger.R.string.InvLinkToChats
            java.lang.Object[] r7 = new java.lang.Object[r9]
            java.lang.Object[] r9 = new java.lang.Object[r0]
            java.lang.String r1 = "Chats"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r1, r5, r9)
            r7[r0] = r5
            java.lang.String r5 = "InvLinkToChats"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7)
            android.text.SpannableStringBuilder r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5)
            int r6 = org.telegram.messenger.R.raw.forward
            java.lang.String[] r7 = new java.lang.String[r0]
            r8.setAnimation((int) r6, (int) r10, (int) r10, (java.lang.String[]) r7)
        L_0x00a0:
            r3 = r5
            r5 = 300(0x12c, float:4.2E-43)
        L_0x00a3:
            android.widget.TextView r6 = r8.textView
            r6.setText(r3)
            if (r5 <= 0) goto L_0x00b3
            org.telegram.ui.Components.BulletinFactory$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.Components.BulletinFactory$$ExternalSyntheticLambda1
            r3.<init>(r8)
            long r5 = (long) r5
            r8.postDelayed(r3, r5)
        L_0x00b3:
            r3 = 1500(0x5dc, float:2.102E-42)
            org.telegram.ui.Components.Bulletin r3 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r4, (org.telegram.ui.Components.Bulletin.Layout) r8, (int) r3)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createInviteSentBulletin(android.content.Context, android.widget.FrameLayout, int, long, int, int, int):org.telegram.ui.Components.Bulletin");
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x00ff  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createForwardedBulletin(android.content.Context r4, android.widget.FrameLayout r5, int r6, long r7, int r9, int r10, int r11) {
        /*
            org.telegram.ui.Components.Bulletin$LottieLayout r0 = new org.telegram.ui.Components.Bulletin$LottieLayout
            r1 = 0
            r0.<init>(r4, r1, r10, r11)
            r4 = 300(0x12c, float:4.2E-43)
            r10 = 30
            r11 = 0
            r1 = 1
            if (r6 > r1) goto L_0x00bd
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)
            long r2 = r6.clientUserId
            int r6 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r6 != 0) goto L_0x003f
            if (r9 > r1) goto L_0x0029
            int r4 = org.telegram.messenger.R.string.FwdMessageToSavedMessages
            java.lang.String r6 = "FwdMessageToSavedMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            android.text.SpannableStringBuilder r4 = org.telegram.messenger.AndroidUtilities.replaceTags(r4)
            goto L_0x0035
        L_0x0029:
            int r4 = org.telegram.messenger.R.string.FwdMessagesToSavedMessages
            java.lang.String r6 = "FwdMessagesToSavedMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            android.text.SpannableStringBuilder r4 = org.telegram.messenger.AndroidUtilities.replaceTags(r4)
        L_0x0035:
            int r6 = org.telegram.messenger.R.raw.saved_messages
            java.lang.String[] r7 = new java.lang.String[r11]
            r0.setAnimation((int) r6, (int) r10, (int) r10, (java.lang.String[]) r7)
            r6 = -1
            goto L_0x00f8
        L_0x003f:
            boolean r6 = org.telegram.messenger.DialogObject.isChatDialog(r7)
            if (r6 == 0) goto L_0x007c
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            long r7 = -r7
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            if (r9 > r1) goto L_0x0069
            int r7 = org.telegram.messenger.R.string.FwdMessageToGroup
            java.lang.Object[] r8 = new java.lang.Object[r1]
            java.lang.String r6 = r6.title
            r8[r11] = r6
            java.lang.String r6 = "FwdMessageToGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r7, r8)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            goto L_0x00b5
        L_0x0069:
            int r7 = org.telegram.messenger.R.string.FwdMessagesToGroup
            java.lang.Object[] r8 = new java.lang.Object[r1]
            java.lang.String r6 = r6.title
            r8[r11] = r6
            java.lang.String r6 = "FwdMessagesToGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r7, r8)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            goto L_0x00b5
        L_0x007c:
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            if (r9 > r1) goto L_0x00a1
            int r7 = org.telegram.messenger.R.string.FwdMessageToUser
            java.lang.Object[] r8 = new java.lang.Object[r1]
            java.lang.String r6 = org.telegram.messenger.UserObject.getFirstName(r6)
            r8[r11] = r6
            java.lang.String r6 = "FwdMessageToUser"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r7, r8)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            goto L_0x00b5
        L_0x00a1:
            int r7 = org.telegram.messenger.R.string.FwdMessagesToUser
            java.lang.Object[] r8 = new java.lang.Object[r1]
            java.lang.String r6 = org.telegram.messenger.UserObject.getFirstName(r6)
            r8[r11] = r6
            java.lang.String r6 = "FwdMessagesToUser"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r7, r8)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
        L_0x00b5:
            int r7 = org.telegram.messenger.R.raw.forward
            java.lang.String[] r8 = new java.lang.String[r11]
            r0.setAnimation((int) r7, (int) r10, (int) r10, (java.lang.String[]) r8)
            goto L_0x00f5
        L_0x00bd:
            java.lang.String r7 = "Chats"
            if (r9 > r1) goto L_0x00d8
            int r8 = org.telegram.messenger.R.string.FwdMessageToChats
            java.lang.Object[] r9 = new java.lang.Object[r1]
            java.lang.Object[] r1 = new java.lang.Object[r11]
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6, r1)
            r9[r11] = r6
            java.lang.String r6 = "FwdMessageToChats"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r8, r9)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            goto L_0x00ee
        L_0x00d8:
            int r8 = org.telegram.messenger.R.string.FwdMessagesToChats
            java.lang.Object[] r9 = new java.lang.Object[r1]
            java.lang.Object[] r1 = new java.lang.Object[r11]
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6, r1)
            r9[r11] = r6
            java.lang.String r6 = "FwdMessagesToChats"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r8, r9)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
        L_0x00ee:
            int r7 = org.telegram.messenger.R.raw.forward
            java.lang.String[] r8 = new java.lang.String[r11]
            r0.setAnimation((int) r7, (int) r10, (int) r10, (java.lang.String[]) r8)
        L_0x00f5:
            r4 = r6
            r6 = 300(0x12c, float:4.2E-43)
        L_0x00f8:
            android.widget.TextView r7 = r0.textView
            r7.setText(r4)
            if (r6 <= 0) goto L_0x0108
            org.telegram.ui.Components.BulletinFactory$$ExternalSyntheticLambda0 r4 = new org.telegram.ui.Components.BulletinFactory$$ExternalSyntheticLambda0
            r4.<init>(r0)
            long r6 = (long) r6
            r0.postDelayed(r4, r6)
        L_0x0108:
            r4 = 1500(0x5dc, float:2.102E-42)
            org.telegram.ui.Components.Bulletin r4 = org.telegram.ui.Components.Bulletin.make((android.widget.FrameLayout) r5, (org.telegram.ui.Components.Bulletin.Layout) r0, (int) r4)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createForwardedBulletin(android.content.Context, android.widget.FrameLayout, int, long, int, int, int):org.telegram.ui.Components.Bulletin");
    }

    public static Bulletin createRemoveFromChatBulletin(BaseFragment baseFragment, TLRPC$User tLRPC$User, String str) {
        String str2;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        lottieLayout.setAnimation(R.raw.ic_ban, "Hand");
        if (tLRPC$User.deleted) {
            str2 = LocaleController.formatString("HiddenName", R.string.HiddenName, new Object[0]);
        } else {
            str2 = tLRPC$User.first_name;
        }
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserRemovedFromChatHint", R.string.UserRemovedFromChatHint, str2, str)));
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }

    public static Bulletin createBanBulletin(BaseFragment baseFragment, boolean z) {
        String str;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        if (z) {
            lottieLayout.setAnimation(R.raw.ic_ban, "Hand");
            str = LocaleController.getString("UserBlocked", R.string.UserBlocked);
        } else {
            lottieLayout.setAnimation(R.raw.ic_unban, "Main", "Finger 1", "Finger 2", "Finger 3", "Finger 4");
            str = LocaleController.getString("UserUnblocked", R.string.UserUnblocked);
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
        lottieLayout.setAnimation(z ? R.raw.ic_pin : R.raw.ic_unpin, 28, 28, "Pin", "Line");
        lottieLayout.textView.setText(LocaleController.getString(z ? "MessagePinnedHint" : "MessageUnpinnedHint", z ? R.string.MessagePinnedHint : R.string.MessageUnpinnedHint));
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
            str = LocaleController.getString("SoundOnHint", R.string.SoundOnHint);
        } else if (i == 1) {
            str = LocaleController.getString("SoundOffHint", R.string.SoundOffHint);
            z = false;
        } else {
            throw new IllegalArgumentException();
        }
        if (z) {
            lottieLayout.setAnimation(R.raw.sound_on, new String[0]);
        } else {
            lottieLayout.setAnimation(R.raw.sound_off, new String[0]);
        }
        lottieLayout.textView.setText(str);
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }
}
