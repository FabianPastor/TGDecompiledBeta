package org.telegram.tgnet;

public class TLRPC$TL_emailVerificationGoogle extends TLRPC$EmailVerification {
    public static int constructor = -NUM;
    public String token;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.token = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.token);
    }
}
