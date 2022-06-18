package org.telegram.messenger;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda2 implements Runnable {
    public static final /* synthetic */ BillingController$$ExternalSyntheticLambda2 INSTANCE = new BillingController$$ExternalSyntheticLambda2();

    private /* synthetic */ BillingController$$ExternalSyntheticLambda2() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.billingProductDetailsUpdated, new Object[0]);
    }
}
