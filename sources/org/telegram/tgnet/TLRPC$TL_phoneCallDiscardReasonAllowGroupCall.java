package org.telegram.tgnet;

public class TLRPC$TL_phoneCallDiscardReasonAllowGroupCall extends TLRPC$PhoneCallDiscardReason {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.encrypted_key = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeByteArray(this.encrypted_key);
    }
}
