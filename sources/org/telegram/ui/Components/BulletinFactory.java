package org.telegram.ui.Components;

import android.widget.FrameLayout;
import androidx.core.util.Preconditions;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.Bulletin;

public final class BulletinFactory {
    public static boolean canShowBulletin(BaseFragment baseFragment) {
        return (baseFragment == null || baseFragment.getParentActivity() == null) ? false : true;
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
            r2 = 2131626154(0x7f0e08aa, float:1.8879536E38)
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
            r11 = 2131626174(0x7f0e08be, float:1.8879577E38)
            java.lang.String r1 = "NotificationsUnmutedHint"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r1, r11)
            r1 = 0
            goto L_0x006c
        L_0x0031:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            r10.<init>()
            throw r10
        L_0x0037:
            r11 = 2131626155(0x7f0e08ab, float:1.8879538E38)
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
            r1 = 2131558424(0x7f0d0018, float:1.8742163E38)
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
            r1 = 2131558425(0x7f0d0019, float:1.8742165E38)
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

    public static Bulletin createDeleteMessagesBulletin(BaseFragment baseFragment, int i) {
        Preconditions.checkArgument(canShowBulletin(baseFragment));
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity());
        lottieLayout.setAnimation(NUM, "Envelope", "Cover", "Bucket");
        lottieLayout.textView.setText(LocaleController.formatPluralString("MessagesDeletedHint", i));
        return Bulletin.make(baseFragment, (Bulletin.Layout) lottieLayout, 1500);
    }

    public static Bulletin createSaveToGalleryBulletin(FrameLayout frameLayout, boolean z, int i, int i2) {
        Preconditions.checkNotNull(frameLayout);
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(frameLayout.getContext(), i, i2);
        lottieLayout.imageView.setAnimation(NUM, 28, 28);
        lottieLayout.setAnimation(NUM, "Box", "Arrow", "Mask", "Arrow 2", "Splash");
        if (z) {
            lottieLayout.textView.setText(LocaleController.getString("VideoSavedHint", NUM));
        } else {
            lottieLayout.textView.setText(LocaleController.getString("PhotoSavedHint", NUM));
        }
        return Bulletin.make(frameLayout, (Bulletin.Layout) lottieLayout, 1500);
    }
}
