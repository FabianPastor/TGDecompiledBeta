package org.telegram.messenger;

/* renamed from: org.telegram.messenger.-$$Lambda$LocationController$UFu6QmZ4dYgGAgZ8gnaudair6ZY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$LocationController$UFu6QmZ4dYgGAgZ8gnaudair6ZY implements Runnable {
    public static final /* synthetic */ $$Lambda$LocationController$UFu6QmZ4dYgGAgZ8gnaudair6ZY INSTANCE = new $$Lambda$LocationController$UFu6QmZ4dYgGAgZ8gnaudair6ZY();

    private /* synthetic */ $$Lambda$LocationController$UFu6QmZ4dYgGAgZ8gnaudair6ZY() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.newLocationAvailable, new Object[0]);
    }
}
