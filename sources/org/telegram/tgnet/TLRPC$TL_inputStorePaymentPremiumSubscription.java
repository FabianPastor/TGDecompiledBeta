package org.telegram.tgnet;

public class TLRPC$TL_inputStorePaymentPremiumSubscription extends TLRPC$InputStorePaymentPurpose {
    public static int constructor = -NUM;
    public int flags;
    public boolean restore;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.restore = z2;
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.restore ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
    }
}
