package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updatePhoneCallSignalingData extends TLRPC$Update {
    public static int constructor = NUM;
    public byte[] data;
    public long phone_call_id;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.phone_call_id = abstractSerializedData.readInt64(z);
        this.data = abstractSerializedData.readByteArray(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.phone_call_id);
        abstractSerializedData.writeByteArray(this.data);
    }
}
