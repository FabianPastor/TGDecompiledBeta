package org.telegram.messenger;

public final /* synthetic */ class LocaleController$$ExternalSyntheticLambda10 implements Runnable {
    public static final /* synthetic */ LocaleController$$ExternalSyntheticLambda10 INSTANCE = new LocaleController$$ExternalSyntheticLambda10();

    private /* synthetic */ LocaleController$$ExternalSyntheticLambda10() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
    }
}
