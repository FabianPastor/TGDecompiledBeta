package org.telegram.tgnet;

public class TLRPC$TL_updateGroupCall extends TLRPC$Update {
    public static int constructor = -NUM;
    public TLRPC$GroupCall call;
    public int chat_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.chat_id = abstractSerializedData.readInt32(z);
        this.call = TLRPC$GroupCall.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.chat_id);
        this.call.serializeToStream(abstractSerializedData);
    }
}
