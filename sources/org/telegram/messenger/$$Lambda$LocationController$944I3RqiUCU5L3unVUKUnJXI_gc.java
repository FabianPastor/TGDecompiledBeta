package org.telegram.messenger;

/* renamed from: org.telegram.messenger.-$$Lambda$LocationController$944I3RqiUCU5L3unVUKUnJXI_gc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$LocationController$944I3RqiUCU5L3unVUKUnJXI_gc implements Runnable {
    public static final /* synthetic */ $$Lambda$LocationController$944I3RqiUCU5L3unVUKUnJXI_gc INSTANCE = new $$Lambda$LocationController$944I3RqiUCU5L3unVUKUnJXI_gc();

    private /* synthetic */ $$Lambda$LocationController$944I3RqiUCU5L3unVUKUnJXI_gc() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.newLocationAvailable, new Object[0]);
    }
}
