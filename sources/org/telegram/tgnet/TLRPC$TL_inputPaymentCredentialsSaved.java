package org.telegram.tgnet;

public class TLRPC$TL_inputPaymentCredentialsSaved extends TLRPC$InputPaymentCredentials {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readString(z);
        this.tmp_password = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.id);
        abstractSerializedData.writeByteArray(this.tmp_password);
    }
}
