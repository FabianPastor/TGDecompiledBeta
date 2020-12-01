package org.telegram.ui;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.ui.-$$Lambda$ChatUsersActivity$K-A8JKetamRsULgrcC-GMH6wHNM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatUsersActivity$KA8JKetamRsULgrcCGMH6wHNM implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatUsersActivity$KA8JKetamRsULgrcCGMH6wHNM INSTANCE = new $$Lambda$ChatUsersActivity$KA8JKetamRsULgrcCGMH6wHNM();

    private /* synthetic */ $$Lambda$ChatUsersActivity$KA8JKetamRsULgrcCGMH6wHNM() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
    }
}
