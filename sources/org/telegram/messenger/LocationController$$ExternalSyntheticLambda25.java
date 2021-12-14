package org.telegram.messenger;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda25 implements Runnable {
    public static final /* synthetic */ LocationController$$ExternalSyntheticLambda25 INSTANCE = new LocationController$$ExternalSyntheticLambda25();

    private /* synthetic */ LocationController$$ExternalSyntheticLambda25() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.newLocationAvailable, new Object[0]);
    }
}
