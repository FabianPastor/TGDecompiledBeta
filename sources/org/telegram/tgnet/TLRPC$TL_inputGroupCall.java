package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_inputGroupCall extends TLObject {
    public static int constructor = -NUM;
    public long access_hash;
    public long id;

    public static TLRPC$TL_inputGroupCall TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_inputGroupCall", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_inputGroupCall tLRPC$TL_inputGroupCall = new TLRPC$TL_inputGroupCall();
        tLRPC$TL_inputGroupCall.readParams(abstractSerializedData, z);
        return tLRPC$TL_inputGroupCall;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt64(z);
        this.access_hash = abstractSerializedData.readInt64(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
    }
}
