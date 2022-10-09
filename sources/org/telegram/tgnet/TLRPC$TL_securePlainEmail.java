package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_securePlainEmail extends TLRPC$SecurePlainData {
    public static int constructor = NUM;
    public String email;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.email = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.email);
    }
}
