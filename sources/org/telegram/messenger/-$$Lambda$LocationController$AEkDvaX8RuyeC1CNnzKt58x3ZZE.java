package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$AEkDvaX8RuyeC1CNnzKt58x3ZZE implements Runnable {
    public static final /* synthetic */ -$$Lambda$LocationController$AEkDvaX8RuyeC1CNnzKt58x3ZZE INSTANCE = new -$$Lambda$LocationController$AEkDvaX8RuyeC1CNnzKt58x3ZZE();

    private /* synthetic */ -$$Lambda$LocationController$AEkDvaX8RuyeC1CNnzKt58x3ZZE() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.newLocationAvailable, new Object[0]);
    }
}
