package org.telegram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import androidx.core.util.Preconditions;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.Bulletin;

public final class BulletinFactory {
    private final FrameLayout containerLayout;
    private final BaseFragment fragment;

    public static BulletinFactory of(BaseFragment baseFragment) {
        Preconditions.checkNotNull(baseFragment);
        return new BulletinFactory(baseFragment);
    }

    public static BulletinFactory of(FrameLayout frameLayout) {
        Preconditions.checkNotNull(frameLayout);
        return new BulletinFactory(frameLayout);
    }

    public static boolean canShowBulletin(BaseFragment baseFragment) {
        return (baseFragment == null || baseFragment.getParentActivity() == null || baseFragment.getLayoutContainer() == null) ? false : true;
    }

    public enum FileType {
        PHOTO("PhotoSavedHint", NUM),
        PHOTO_TO_DOWNLOADS("PhotoSavedToDownloadsHint", NUM),
        PHOTOS("PhotosSavedHint"),
        VIDEO("VideoSavedHint", NUM),
        VIDEO_TO_DOWNLOADS("VideoSavedToDownloadsHint", NUM),
        VIDEOS("VideosSavedHint"),
        AUDIO("AudioSavedHint", NUM),
        AUDIOS("AudiosSavedHint"),
        GIF("GifSavedToDownloadsHint", NUM),
        MEDIA("MediaSavedHint"),
        UNKNOWN("FileSavedHint", NUM),
        UNKNOWNS("FilesSavedHint");
        
        private final String localeKey;
        private final int localeRes;
        private final boolean plural;

        private FileType(String str, int i) {
            this.localeKey = str;
            this.localeRes = i;
            this.plural = false;
        }

        private FileType(String str) {
            this.localeKey = str;
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
    }

    private BulletinFactory(BaseFragment baseFragment) {
        this.fragment = baseFragment;
        this.containerLayout = null;
    }

    private BulletinFactory(FrameLayout frameLayout) {
        this.containerLayout = frameLayout;
        this.fragment = null;
    }

    public Bulletin createDownloadBulletin(FileType fileType) {
        return createDownloadBulletin(fileType, 1);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i) {
        return createDownloadBulletin(fileType, i, 0, 0);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i, int i2, int i3) {
        Bulletin.LottieLayout lottieLayout;
        if (i2 == 0 || i3 == 0) {
            lottieLayout = new Bulletin.LottieLayout(getContext());
        } else {
            lottieLayout = new Bulletin.LottieLayout(getContext(), i2, i3);
        }
        lottieLayout.setAnimation(NUM, "Box", "Arrow", "Mask", "Arrow 2", "Splash");
        lottieLayout.textView.setText(fileType.getText(i));
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

    /* JADX WARNING: Removed duplicated region for block: B:15:0x006e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x008c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createMuteBulletin(org.telegram.ui.ActionBar.BaseFragment r10, int r11) {
        /*
            boolean r0 = canShowBulletin(r10)
            androidx.core.util.Preconditions.checkArgument(r0)
            org.telegram.ui.Components.Bulletin$LottieLayout r0 = new org.telegram.ui.Components.Bulletin$LottieLayout
            android.app.Activity r1 = r10.getParentActivity()
            r0.<init>(r1)
            java.lang.String r1 = "Hours"
            r2 = 2131626274(0x7f0e0922, float:1.887978E38)
            java.lang.String r3 = "NotificationsMutedForHint"
            r4 = 4
            r5 = 3
            r6 = 2
            r7 = 0
            r8 = 1
            if (r11 == 0) goto L_0x005f
            if (r11 == r8) goto L_0x0050
            if (r11 == r6) goto L_0x0041
            if (r11 == r5) goto L_0x0037
            if (r11 != r4) goto L_0x0031
            r11 = 2131626295(0x7f0e0937, float:1.8879822E38)
            java.lang.String r1 = "NotificationsUnmutedHint"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r1, r11)
            r1 = 0
            goto L_0x006c
        L_0x0031:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            r10.<init>()
            throw r10
        L_0x0037:
            r11 = 2131626275(0x7f0e0923, float:1.8879782E38)
            java.lang.String r1 = "NotificationsMutedHint"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r1, r11)
            goto L_0x006b
        L_0x0041:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            java.lang.String r1 = "Days"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r6)
            r11[r7] = r1
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r3, r2, r11)
            goto L_0x006b
        L_0x0050:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            r9 = 8
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r9)
            r11[r7] = r1
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r3, r2, r11)
            goto L_0x006b
        L_0x005f:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r8)
            r11[r7] = r1
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r3, r2, r11)
        L_0x006b:
            r1 = 1
        L_0x006c:
            if (r1 == 0) goto L_0x008c
            r1 = 2131558426(0x7f0d001a, float:1.8742167E38)
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
            goto L_0x00a4
        L_0x008c:
            r1 = 2131558428(0x7f0d001c, float:1.8742172E38)
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
        L_0x00a4:
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: org.telegram.ui.Components.Bulletin$TwoLineLottieLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: org.telegram.ui.Components.Bulletin$LottieLayout} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.Components.Bulletin createUnpinAllMessagesBulletin(org.telegram.ui.ActionBar.BaseFragment r7, int r8, boolean r9, java.lang.Runnable r10, java.lang.Runnable r11) {
        /*
            boolean r0 = canShowBulletin(r7)
            androidx.core.util.Preconditions.checkArgument(r0)
            java.lang.String r0 = "Line"
            java.lang.String r1 = "Pin"
            r2 = 0
            r3 = 2
            r4 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r5 = 1
            if (r9 == 0) goto L_0x0042
            org.telegram.ui.Components.Bulletin$TwoLineLottieLayout r8 = new org.telegram.ui.Components.Bulletin$TwoLineLottieLayout
            android.app.Activity r9 = r7.getParentActivity()
            r8.<init>(r9)
            java.lang.String[] r9 = new java.lang.String[r3]
            r9[r2] = r1
            r9[r5] = r0
            r8.setAnimation(r4, r9)
            android.widget.TextView r9 = r8.titleTextView
            r0 = 2131626709(0x7f0e0ad5, float:1.8880662E38)
            java.lang.String r1 = "PinnedMessagesHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r9.setText(r0)
            android.widget.TextView r9 = r8.subtitleTextView
            r0 = 2131626710(0x7f0e0ad6, float:1.8880664E38)
            java.lang.String r1 = "PinnedMessagesHiddenInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r9.setText(r0)
            goto L_0x0060
        L_0x0042:
            org.telegram.ui.Components.Bulletin$LottieLayout r9 = new org.telegram.ui.Components.Bulletin$LottieLayout
            android.app.Activity r6 = r7.getParentActivity()
            r9.<init>(r6)
            java.lang.String[] r3 = new java.lang.String[r3]
            r3[r2] = r1
            r3[r5] = r0
            r9.setAnimation(r4, r3)
            android.widget.TextView r0 = r9.textView
            java.lang.String r1 = "MessagesUnpinned"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r1, r8)
            r0.setText(r8)
            r8 = r9
        L_0x0060:
            org.telegram.ui.Components.Bulletin$UndoButton r9 = new org.telegram.ui.Components.Bulletin$UndoButton
            android.app.Activity r0 = r7.getParentActivity()
            r9.<init>(r0, r5)
            r9.setUndoAction(r10)
            r9.setDelayedAction(r11)
            r8.setButton(r9)
            r9 = 5000(0x1388, float:7.006E-42)
            org.telegram.ui.Components.Bulletin r7 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r7, (org.telegram.ui.Components.Bulletin.Layout) r8, (int) r9)
            return r7
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
        Preconditions.checkArgument(canShowBulletin(baseFragment));
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity());
        lottieLayout.setAnimation(NUM, "Shield");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserSetAsAdminHint", NUM, str)));
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }

    public static Bulletin createPinMessageBulletin(BaseFragment baseFragment) {
        return createPinMessageBulletin(baseFragment, true, (Runnable) null, (Runnable) null);
    }

    public static Bulletin createUnpinMessageBulletin(BaseFragment baseFragment, Runnable runnable, Runnable runnable2) {
        return createPinMessageBulletin(baseFragment, false, runnable, runnable2);
    }

    private static Bulletin createPinMessageBulletin(BaseFragment baseFragment, boolean z, Runnable runnable, Runnable runnable2) {
        Preconditions.checkArgument(canShowBulletin(baseFragment));
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity());
        lottieLayout.setAnimation(z ? NUM : NUM, "Pin", "Line");
        lottieLayout.textView.setText(LocaleController.getString(z ? "MessagePinnedHint" : "MessageUnpinnedHint", z ? NUM : NUM));
        if (!z) {
            Bulletin.UndoButton undoButton = new Bulletin.UndoButton(baseFragment.getParentActivity(), true);
            undoButton.setUndoAction(runnable);
            undoButton.setDelayedAction(runnable2);
            lottieLayout.setButton(undoButton);
        }
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, z ? 1500 : 5000);
    }
}
