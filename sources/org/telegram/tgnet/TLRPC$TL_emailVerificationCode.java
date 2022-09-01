package org.telegram.tgnet;

public class TLRPC$TL_emailVerificationCode extends TLRPC$EmailVerification {
    public static int constructor = -NUM;
    public String code;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.code = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.code);
    }
}
