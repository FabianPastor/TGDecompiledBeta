package org.telegram.tgnet;

public abstract class TLRPC$InputStorePaymentPurpose extends TLObject {
    public static TLRPC$InputStorePaymentPurpose TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputStorePaymentPurpose tLRPC$InputStorePaymentPurpose;
        if (i != -NUM) {
            tLRPC$InputStorePaymentPurpose = i != NUM ? null : new TLRPC$TL_inputStorePaymentGiftPremium();
        } else {
            tLRPC$InputStorePaymentPurpose = new TLRPC$TL_inputStorePaymentPremiumSubscription();
        }
        if (tLRPC$InputStorePaymentPurpose != null || !z) {
            if (tLRPC$InputStorePaymentPurpose != null) {
                tLRPC$InputStorePaymentPurpose.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputStorePaymentPurpose;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputStorePaymentPurpose", new Object[]{Integer.valueOf(i)}));
    }
}
