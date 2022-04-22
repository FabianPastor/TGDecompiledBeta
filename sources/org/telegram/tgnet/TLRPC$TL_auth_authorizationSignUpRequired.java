package org.telegram.tgnet;

public class TLRPC$TL_auth_authorizationSignUpRequired extends TLRPC$auth_Authorization {
    public static int constructor = NUM;
    public int flags;
    public TLRPC$TL_help_termsOfService terms_of_service;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            this.terms_of_service = TLRPC$TL_help_termsOfService.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            this.terms_of_service.serializeToStream(abstractSerializedData);
        }
    }
}
