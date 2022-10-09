package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_saveSecureValue extends TLObject {
    public static int constructor = -NUM;
    public long secure_secret_id;
    public TLRPC$TL_inputSecureValue value;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_secureValue.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.value.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt64(this.secure_secret_id);
    }
}
