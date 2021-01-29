package org.telegram.tgnet;

public class TLRPC$TL_messages_deletePhoneCallHistory extends TLObject {
    public static int constructor = -NUM;
    public int flags;
    public boolean revoke;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_messages_affectedFoundMessages.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.revoke ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
    }
}
