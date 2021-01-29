package org.telegram.tgnet;

public class TLRPC$TL_messages_discardEncryption extends TLObject {
    public static int constructor = -NUM;
    public int chat_id;
    public boolean delete_history;
    public int flags;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.delete_history ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt32(this.chat_id);
    }
}
