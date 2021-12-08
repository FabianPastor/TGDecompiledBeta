package org.telegram.messenger;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda17 implements Runnable {
    public static final /* synthetic */ LocationController$$ExternalSyntheticLambda17 INSTANCE = new LocationController$$ExternalSyntheticLambda17();

    private /* synthetic */ LocationController$$ExternalSyntheticLambda17() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.newLocationAvailable, new Object[0]);
    }
}
