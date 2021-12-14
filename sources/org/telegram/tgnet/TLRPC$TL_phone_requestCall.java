package org.telegram.tgnet;

public class TLRPC$TL_phone_requestCall extends TLObject {
    public static int constructor = NUM;
    public int flags;
    public byte[] g_a_hash;
    public TLRPC$TL_phoneCallProtocol protocol;
    public int random_id;
    public TLRPC$InputUser user_id;
    public boolean video;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_phone_phoneCall.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.video ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.user_id.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.random_id);
        abstractSerializedData.writeByteArray(this.g_a_hash);
        this.protocol.serializeToStream(abstractSerializedData);
    }
}
