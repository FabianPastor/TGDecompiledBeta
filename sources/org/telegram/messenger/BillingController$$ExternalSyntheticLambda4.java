package org.telegram.messenger;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda4 implements Runnable {
    public static final /* synthetic */ BillingController$$ExternalSyntheticLambda4 INSTANCE = new BillingController$$ExternalSyntheticLambda4();

    private /* synthetic */ BillingController$$ExternalSyntheticLambda4() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.billingProductDetailsUpdated, new Object[0]);
    }
}
