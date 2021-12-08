package org.telegram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;

public final class BulletinFactory {
    public static final int ICON_TYPE_NOT_FOUND = 0;
    public static final int ICON_TYPE_WARNING = 1;
    private final FrameLayout containerLayout;
    private final BaseFragment fragment;
    private final Theme.ResourcesProvider resourcesProvider;

    public static BulletinFactory of(BaseFragment fragment2) {
        return new BulletinFactory(fragment2);
    }

    public static BulletinFactory of(FrameLayout containerLayout2, Theme.ResourcesProvider resourcesProvider2) {
        return new BulletinFactory(containerLayout2, resourcesProvider2);
    }

    public static boolean canShowBulletin(BaseFragment fragment2) {
        return (fragment2 == null || fragment2.getParentActivity() == null || fragment2.getLayoutContainer() == null) ? false : true;
    }

    public enum FileType {
        PHOTO("PhotoSavedHint", NUM, Icon.SAVED_TO_GALLERY),
        PHOTOS("PhotosSavedHint", Icon.SAVED_TO_GALLERY),
        VIDEO("VideoSavedHint", NUM, Icon.SAVED_TO_GALLERY),
        VIDEOS("VideosSavedHint", Icon.SAVED_TO_GALLERY),
        MEDIA("MediaSavedHint", Icon.SAVED_TO_GALLERY),
        PHOTO_TO_DOWNLOADS("PhotoSavedToDownloadsHint", NUM, Icon.SAVED_TO_DOWNLOADS),
        VIDEO_TO_DOWNLOADS("VideoSavedToDownloadsHint", NUM, Icon.SAVED_TO_DOWNLOADS),
        GIF("GifSavedToDownloadsHint", Icon.SAVED_TO_DOWNLOADS),
        AUDIO("AudioSavedHint", NUM, Icon.SAVED_TO_MUSIC),
        AUDIOS("AudiosSavedHint", Icon.SAVED_TO_MUSIC),
        UNKNOWN("FileSavedHint", NUM, Icon.SAVED_TO_DOWNLOADS),
        UNKNOWNS("FilesSavedHint", Icon.SAVED_TO_DOWNLOADS);
        
        /* access modifiers changed from: private */
        public final Icon icon;
        private final String localeKey;
        private final int localeRes;
        private final boolean plural;

        private FileType(String localeKey2, int localeRes2, Icon icon2) {
            this.localeKey = localeKey2;
            this.localeRes = localeRes2;
            this.icon = icon2;
            this.plural = false;
        }

        private FileType(String localeKey2, Icon icon2) {
            this.localeKey = localeKey2;
            this.icon = icon2;
            this.localeRes = 0;
            this.plural = true;
        }

        private String getText() {
            return getText(1);
        }

        /* access modifiers changed from: private */
        public String getText(int amount) {
            if (this.plural) {
                return LocaleController.formatPluralString(this.localeKey, amount);
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

            private Icon(int resId2, int paddingBottom2, String... layers2) {
                this.resId = resId2;
                this.paddingBottom = paddingBottom2;
                this.layers = layers2;
            }
        }
    }

    private BulletinFactory(BaseFragment fragment2) {
        this.fragment = fragment2;
        Theme.ResourcesProvider resourcesProvider2 = null;
        this.containerLayout = null;
        this.resourcesProvider = fragment2 != null ? fragment2.getResourceProvider() : resourcesProvider2;
    }

    private BulletinFactory(FrameLayout containerLayout2, Theme.ResourcesProvider resourcesProvider2) {
        this.containerLayout = containerLayout2;
        this.fragment = null;
        this.resourcesProvider = resourcesProvider2;
    }

    public Bulletin createSimpleBulletin(int iconRawId, String text) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        layout.setAnimation(iconRawId, 36, 36, new String[0]);
        layout.textView.setText(text);
        layout.textView.setSingleLine(false);
        layout.textView.setMaxLines(2);
        return create(layout, 1500);
    }

    public Bulletin createDownloadBulletin(FileType fileType) {
        return createDownloadBulletin(fileType, this.resourcesProvider);
    }

    public Bulletin createDownloadBulletin(FileType fileType, Theme.ResourcesProvider resourcesProvider2) {
        return createDownloadBulletin(fileType, 1, resourcesProvider2);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int filesAmount, Theme.ResourcesProvider resourcesProvider2) {
        return createDownloadBulletin(fileType, filesAmount, 0, 0, resourcesProvider2);
    }

    public Bulletin createReportSent(Theme.ResourcesProvider resourcesProvider2) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext(), resourcesProvider2);
        layout.setAnimation(NUM, new String[0]);
        layout.textView.setText(LocaleController.getString("ReportChatSent", NUM));
        return create(layout, 1500);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int filesAmount, int backgroundColor, int textColor) {
        return createDownloadBulletin(fileType, filesAmount, backgroundColor, textColor, (Theme.ResourcesProvider) null);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int filesAmount, int backgroundColor, int textColor, Theme.ResourcesProvider resourcesProvider2) {
        Bulletin.LottieLayout layout;
        if (backgroundColor == 0 || textColor == 0) {
            layout = new Bulletin.LottieLayout(getContext(), resourcesProvider2);
        } else {
            layout = new Bulletin.LottieLayout(getContext(), resourcesProvider2, backgroundColor, textColor);
        }
        layout.setAnimation(fileType.icon.resId, fileType.icon.layers);
        layout.textView.setText(fileType.getText(filesAmount));
        if (fileType.icon.paddingBottom != 0) {
            layout.setIconPaddingBottom(fileType.icon.paddingBottom);
        }
        return create(layout, 1500);
    }

    public Bulletin createErrorBulletin(CharSequence errorMessage) {
        return createErrorBulletin(errorMessage, (Theme.ResourcesProvider) null);
    }

    public Bulletin createErrorBulletin(CharSequence errorMessage, Theme.ResourcesProvider resourcesProvider2) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext(), resourcesProvider2);
        layout.setAnimation(NUM, new String[0]);
        layout.textView.setText(errorMessage);
        layout.textView.setSingleLine(false);
        layout.textView.setMaxLines(2);
        return create(layout, 1500);
    }

    public Bulletin createCopyLinkBulletin() {
        return createCopyLinkBulletin(false, this.resourcesProvider);
    }

    public Bulletin createCopyBulletin(String message) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext(), (Theme.ResourcesProvider) null);
        layout.setAnimation(NUM, 36, 36, "NULL ROTATION", "Back", "Front");
        layout.textView.setText(message);
        return create(layout, 1500);
    }

    public Bulletin createCopyLinkBulletin(boolean isPrivate, Theme.ResourcesProvider resourcesProvider2) {
        if (isPrivate) {
            Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(getContext(), resourcesProvider2);
            layout.setAnimation(NUM, 36, 36, "Wibe", "Circle");
            layout.titleTextView.setText(LocaleController.getString("LinkCopied", NUM));
            layout.subtitleTextView.setText(LocaleController.getString("LinkCopiedPrivateInfo", NUM));
            return create(layout, 2750);
        }
        Bulletin.LottieLayout layout2 = new Bulletin.LottieLayout(getContext(), resourcesProvider2);
        layout2.setAnimation(NUM, 36, 36, "Wibe", "Circle");
        layout2.textView.setText(LocaleController.getString("LinkCopied", NUM));
        return create(layout2, 1500);
    }

    private Bulletin create(Bulletin.Layout layout, int duration) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            return Bulletin.make(baseFragment, layout, duration);
        }
        return Bulletin.make(this.containerLayout, layout, duration);
    }

    private Context getContext() {
        BaseFragment baseFragment = this.fragment;
        return baseFragment != null ? baseFragment.getParentActivity() : this.containerLayout.getContext();
    }

    public static Bulletin createMuteBulletin(BaseFragment fragment2, int setting) {
        return createMuteBulletin(fragment2, setting, (Theme.ResourcesProvider) null);
    }

    public static Bulletin createMuteBulletin(BaseFragment fragment2, int setting, Theme.ResourcesProvider resourcesProvider2) {
        boolean mute;
        String text;
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment2.getParentActivity(), resourcesProvider2);
        switch (setting) {
            case 0:
                text = LocaleController.formatString("NotificationsMutedForHint", NUM, LocaleController.formatPluralString("Hours", 1));
                mute = true;
                break;
            case 1:
                text = LocaleController.formatString("NotificationsMutedForHint", NUM, LocaleController.formatPluralString("Hours", 8));
                mute = true;
                break;
            case 2:
                text = LocaleController.formatString("NotificationsMutedForHint", NUM, LocaleController.formatPluralString("Days", 2));
                mute = true;
                break;
            case 3:
                text = LocaleController.getString("NotificationsMutedHint", NUM);
                mute = true;
                break;
            case 4:
                text = LocaleController.getString("NotificationsUnmutedHint", NUM);
                mute = false;
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (mute) {
            layout.setAnimation(NUM, "Body Main", "Body Top", "Line", "Curve Big", "Curve Small");
        } else {
            layout.setAnimation(NUM, "BODY", "Wibe Big", "Wibe Big 3", "Wibe Small");
        }
        layout.textView.setText(text);
        return Bulletin.make(fragment2, (Bulletin.Layout) layout, 1500);
    }

    public static Bulletin createMuteBulletin(BaseFragment fragment2, boolean muted, Theme.ResourcesProvider resourcesProvider2) {
        return createMuteBulletin(fragment2, muted ? 3 : 4, resourcesProvider2);
    }

    public static Bulletin createDeleteMessagesBulletin(BaseFragment fragment2, int count, Theme.ResourcesProvider resourcesProvider2) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment2.getParentActivity(), resourcesProvider2);
        layout.setAnimation(NUM, "Envelope", "Cover", "Bucket");
        layout.textView.setText(LocaleController.formatPluralString("MessagesDeletedHint", count));
        return Bulletin.make(fragment2, (Bulletin.Layout) layout, 1500);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: org.telegram.ui.Components.Bulletin$TwoLineLottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createUnpinAllMessagesBulletin(org.telegram.ui.ActionBar.BaseFragment r9, int r10, boolean r11, java.lang.Runnable r12, java.lang.Runnable r13, org.telegram.ui.ActionBar.Theme.ResourcesProvider r14) {
        /*
            android.app.Activity r0 = r9.getParentActivity()
            if (r0 != 0) goto L_0x000d
            if (r13 == 0) goto L_0x000b
            r13.run()
        L_0x000b:
            r0 = 0
            return r0
        L_0x000d:
            java.lang.String r0 = "Line"
            java.lang.String r1 = "Pin"
            r2 = 0
            r3 = 2
            r4 = 2131558460(0x7f0d003c, float:1.8742236E38)
            r5 = 1
            r6 = 28
            if (r11 == 0) goto L_0x004b
            org.telegram.ui.Components.Bulletin$TwoLineLottieLayout r7 = new org.telegram.ui.Components.Bulletin$TwoLineLottieLayout
            android.app.Activity r8 = r9.getParentActivity()
            r7.<init>(r8, r14)
            java.lang.String[] r3 = new java.lang.String[r3]
            r3[r2] = r1
            r3[r5] = r0
            r7.setAnimation(r4, r6, r6, r3)
            android.widget.TextView r0 = r7.titleTextView
            r1 = 2131627205(0x7f0e0cc5, float:1.8881668E38)
            java.lang.String r2 = "PinnedMessagesHidden"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            android.widget.TextView r0 = r7.subtitleTextView
            r1 = 2131627206(0x7f0e0cc6, float:1.888167E38)
            java.lang.String r2 = "PinnedMessagesHiddenInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            r0 = r7
            goto L_0x0069
        L_0x004b:
            org.telegram.ui.Components.Bulletin$LottieLayout r7 = new org.telegram.ui.Components.Bulletin$LottieLayout
            android.app.Activity r8 = r9.getParentActivity()
            r7.<init>(r8, r14)
            java.lang.String[] r3 = new java.lang.String[r3]
            r3[r2] = r1
            r3[r5] = r0
            r7.setAnimation(r4, r6, r6, r3)
            android.widget.TextView r0 = r7.textView
            java.lang.String r1 = "MessagesUnpinned"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r10)
            r0.setText(r1)
            r0 = r7
        L_0x0069:
            org.telegram.ui.Components.Bulletin$UndoButton r1 = new org.telegram.ui.Components.Bulletin$UndoButton
            android.app.Activity r2 = r9.getParentActivity()
            r1.<init>(r2, r5, r14)
            org.telegram.ui.Components.Bulletin$UndoButton r1 = r1.setUndoAction(r12)
            org.telegram.ui.Components.Bulletin$UndoButton r1 = r1.setDelayedAction(r13)
            r0.setButton(r1)
            r1 = 5000(0x1388, float:7.006E-42)
            org.telegram.ui.Components.Bulletin r1 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r9, (org.telegram.ui.Components.Bulletin.Layout) r0, (int) r1)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createUnpinAllMessagesBulletin(org.telegram.ui.ActionBar.BaseFragment, int, boolean, java.lang.Runnable, java.lang.Runnable, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.Components.Bulletin");
    }

    public static Bulletin createSaveToGalleryBulletin(BaseFragment fragment2, boolean video, Theme.ResourcesProvider resourcesProvider2) {
        return of(fragment2).createDownloadBulletin(video ? FileType.VIDEO : FileType.PHOTO, resourcesProvider2);
    }

    public static Bulletin createSaveToGalleryBulletin(FrameLayout containerLayout2, boolean video, int backgroundColor, int textColor) {
        return of(containerLayout2, (Theme.ResourcesProvider) null).createDownloadBulletin(video ? FileType.VIDEO : FileType.PHOTO, 1, backgroundColor, textColor);
    }

    public static Bulletin createPromoteToAdminBulletin(BaseFragment fragment2, String userFirstName) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment2.getParentActivity(), (Theme.ResourcesProvider) null);
        layout.setAnimation(NUM, "Shield");
        layout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserSetAsAdminHint", NUM, userFirstName)));
        return Bulletin.make(fragment2, (Bulletin.Layout) layout, 1500);
    }

    public static Bulletin createRemoveFromChatBulletin(BaseFragment fragment2, TLRPC.User user, String chatName) {
        String name;
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment2.getParentActivity(), (Theme.ResourcesProvider) null);
        layout.setAnimation(NUM, "Hand");
        if (user.deleted) {
            name = LocaleController.formatString("HiddenName", NUM, new Object[0]);
        } else {
            name = user.first_name;
        }
        layout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserRemovedFromChatHint", NUM, name, chatName)));
        return Bulletin.make(fragment2, (Bulletin.Layout) layout, 1500);
    }

    public static Bulletin createBanBulletin(BaseFragment fragment2, boolean banned) {
        String text;
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment2.getParentActivity(), (Theme.ResourcesProvider) null);
        if (banned) {
            layout.setAnimation(NUM, "Hand");
            text = LocaleController.getString("UserBlocked", NUM);
        } else {
            layout.setAnimation(NUM, "Main", "Finger 1", "Finger 2", "Finger 3", "Finger 4");
            text = LocaleController.getString("UserUnblocked", NUM);
        }
        layout.textView.setText(AndroidUtilities.replaceTags(text));
        return Bulletin.make(fragment2, (Bulletin.Layout) layout, 1500);
    }

    public static Bulletin createCopyLinkBulletin(BaseFragment fragment2) {
        return of(fragment2).createCopyLinkBulletin();
    }

    public static Bulletin createCopyLinkBulletin(FrameLayout containerView) {
        return of(containerView, (Theme.ResourcesProvider) null).createCopyLinkBulletin();
    }

    public static Bulletin createPinMessageBulletin(BaseFragment fragment2, Theme.ResourcesProvider resourcesProvider2) {
        return createPinMessageBulletin(fragment2, true, (Runnable) null, (Runnable) null, resourcesProvider2);
    }

    public static Bulletin createUnpinMessageBulletin(BaseFragment fragment2, Runnable undoAction, Runnable delayedAction, Theme.ResourcesProvider resourcesProvider2) {
        return createPinMessageBulletin(fragment2, false, undoAction, delayedAction, resourcesProvider2);
    }

    private static Bulletin createPinMessageBulletin(BaseFragment fragment2, boolean pinned, Runnable undoAction, Runnable delayedAction, Theme.ResourcesProvider resourcesProvider2) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment2.getParentActivity(), resourcesProvider2);
        layout.setAnimation(pinned ? NUM : NUM, 28, 28, "Pin", "Line");
        layout.textView.setText(LocaleController.getString(pinned ? "MessagePinnedHint" : "MessageUnpinnedHint", pinned ? NUM : NUM));
        if (!pinned) {
            layout.setButton(new Bulletin.UndoButton(fragment2.getParentActivity(), true, resourcesProvider2).setUndoAction(undoAction).setDelayedAction(delayedAction));
        }
        return Bulletin.make(fragment2, (Bulletin.Layout) layout, pinned ? 1500 : 5000);
    }
}
