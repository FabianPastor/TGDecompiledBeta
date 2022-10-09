package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_emailVerificationCode extends TLRPC$EmailVerification {
    public static int constructor = -NUM;
    public String code;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.code = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.code);
    }
}
