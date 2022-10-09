package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$InputStorePaymentPurpose extends TLObject {
    public static TLRPC$InputStorePaymentPurpose TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputStorePaymentPurpose tLRPC$TL_inputStorePaymentPremiumSubscription;
        if (i == -NUM) {
            tLRPC$TL_inputStorePaymentPremiumSubscription = new TLRPC$TL_inputStorePaymentPremiumSubscription();
        } else {
            tLRPC$TL_inputStorePaymentPremiumSubscription = i != NUM ? null : new TLRPC$TL_inputStorePaymentGiftPremium();
        }
        if (tLRPC$TL_inputStorePaymentPremiumSubscription != null || !z) {
            if (tLRPC$TL_inputStorePaymentPremiumSubscription != null) {
                tLRPC$TL_inputStorePaymentPremiumSubscription.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_inputStorePaymentPremiumSubscription;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputStorePaymentPurpose", Integer.valueOf(i)));
    }
}
