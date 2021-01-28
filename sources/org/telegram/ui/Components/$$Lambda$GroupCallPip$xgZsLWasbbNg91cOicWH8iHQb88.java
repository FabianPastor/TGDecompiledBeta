package org.telegram.ui.Components;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.ui.Components.-$$Lambda$GroupCallPip$xgZsLWasbbNg91cOicWH8iHQb88  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$GroupCallPip$xgZsLWasbbNg91cOicWH8iHQb88 implements Runnable {
    public static final /* synthetic */ $$Lambda$GroupCallPip$xgZsLWasbbNg91cOicWH8iHQb88 INSTANCE = new $$Lambda$GroupCallPip$xgZsLWasbbNg91cOicWH8iHQb88();

    private /* synthetic */ $$Lambda$GroupCallPip$xgZsLWasbbNg91cOicWH8iHQb88() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
    }
}
