package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_sendVerifyEmailCode extends TLObject {
    public static int constructor = -NUM;
    public String email;
    public TLRPC$EmailVerifyPurpose purpose;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_account_sentEmailCode.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.purpose.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.email);
    }
}
