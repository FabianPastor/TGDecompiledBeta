package org.telegram.tgnet;

public class TLRPC$TL_messages_updateDialogFilter extends TLObject {
    public static int constructor = NUM;
    public TLRPC$TL_dialogFilter filter;
    public int flags;
    public int id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32(this.id);
        if ((this.flags & 1) != 0) {
            this.filter.serializeToStream(abstractSerializedData);
        }
    }
}
