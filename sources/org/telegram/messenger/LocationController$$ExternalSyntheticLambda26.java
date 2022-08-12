package org.telegram.messenger;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda26 implements Runnable {
    public static final /* synthetic */ LocationController$$ExternalSyntheticLambda26 INSTANCE = new LocationController$$ExternalSyntheticLambda26();

    private /* synthetic */ LocationController$$ExternalSyntheticLambda26() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.newLocationAvailable, new Object[0]);
    }
}
