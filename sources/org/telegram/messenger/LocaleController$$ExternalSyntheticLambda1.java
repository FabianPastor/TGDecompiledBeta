package org.telegram.messenger;

public final /* synthetic */ class LocaleController$$ExternalSyntheticLambda1 implements Runnable {
    public static final /* synthetic */ LocaleController$$ExternalSyntheticLambda1 INSTANCE = new LocaleController$$ExternalSyntheticLambda1();

    private /* synthetic */ LocaleController$$ExternalSyntheticLambda1() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
    }
}
