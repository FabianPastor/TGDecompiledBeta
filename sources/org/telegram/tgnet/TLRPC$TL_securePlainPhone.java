package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_securePlainPhone extends TLRPC$SecurePlainData {
    public static int constructor = NUM;
    public String phone;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.phone = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.phone);
    }
}
