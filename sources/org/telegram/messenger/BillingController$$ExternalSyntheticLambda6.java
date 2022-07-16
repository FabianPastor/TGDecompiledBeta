package org.telegram.messenger;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda6 implements Runnable {
    public static final /* synthetic */ BillingController$$ExternalSyntheticLambda6 INSTANCE = new BillingController$$ExternalSyntheticLambda6();

    private /* synthetic */ BillingController$$ExternalSyntheticLambda6() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.billingProductDetailsUpdated, new Object[0]);
    }
}
