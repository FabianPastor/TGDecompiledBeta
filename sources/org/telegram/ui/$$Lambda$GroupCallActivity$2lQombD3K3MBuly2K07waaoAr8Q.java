package org.telegram.ui;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.ui.-$$Lambda$GroupCallActivity$2lQombD3K3MBuly2K07waaoAr8Q  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$GroupCallActivity$2lQombD3K3MBuly2K07waaoAr8Q implements Runnable {
    public static final /* synthetic */ $$Lambda$GroupCallActivity$2lQombD3K3MBuly2K07waaoAr8Q INSTANCE = new $$Lambda$GroupCallActivity$2lQombD3K3MBuly2K07waaoAr8Q();

    private /* synthetic */ $$Lambda$GroupCallActivity$2lQombD3K3MBuly2K07waaoAr8Q() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
    }
}
