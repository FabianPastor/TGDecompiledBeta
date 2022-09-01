package org.telegram.tgnet;

public class TLRPC$TL_account_verifyEmail extends TLObject {
    public static int constructor = 53322959;
    public TLRPC$EmailVerifyPurpose purpose;
    public TLRPC$EmailVerification verification;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$account_EmailVerified.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.purpose.serializeToStream(abstractSerializedData);
        this.verification.serializeToStream(abstractSerializedData);
    }
}
