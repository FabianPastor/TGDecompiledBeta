package org.telegram.tgnet;

public class TLRPC$TL_account_emailVerifiedLogin extends TLRPC$account_EmailVerified {
    public static int constructor = -NUM;
    public String email;
    public TLRPC$TL_auth_sentCode sent_code;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.email = abstractSerializedData.readString(z);
        this.sent_code = TLRPC$TL_auth_sentCode.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.email);
        this.sent_code.serializeToStream(abstractSerializedData);
    }
}
