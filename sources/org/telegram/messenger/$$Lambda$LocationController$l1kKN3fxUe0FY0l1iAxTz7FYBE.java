package org.telegram.messenger;

/* renamed from: org.telegram.messenger.-$$Lambda$LocationController$-l1kKN3fxUe0FY0l1iAxTz7FYBE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$LocationController$l1kKN3fxUe0FY0l1iAxTz7FYBE implements Runnable {
    public static final /* synthetic */ $$Lambda$LocationController$l1kKN3fxUe0FY0l1iAxTz7FYBE INSTANCE = new $$Lambda$LocationController$l1kKN3fxUe0FY0l1iAxTz7FYBE();

    private /* synthetic */ $$Lambda$LocationController$l1kKN3fxUe0FY0l1iAxTz7FYBE() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.newLocationAvailable, new Object[0]);
    }
}
