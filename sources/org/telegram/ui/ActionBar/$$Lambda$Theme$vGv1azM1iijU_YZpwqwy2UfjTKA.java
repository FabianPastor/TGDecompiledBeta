package org.telegram.ui.ActionBar;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.ui.ActionBar.-$$Lambda$Theme$vGv1azM1iijU_YZpwqwy2UfjTKA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Theme$vGv1azM1iijU_YZpwqwy2UfjTKA implements Runnable {
    public static final /* synthetic */ $$Lambda$Theme$vGv1azM1iijU_YZpwqwy2UfjTKA INSTANCE = new $$Lambda$Theme$vGv1azM1iijU_YZpwqwy2UfjTKA();

    private /* synthetic */ $$Lambda$Theme$vGv1azM1iijU_YZpwqwy2UfjTKA() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, false);
    }
}
