package org.telegram.tgnet;

public class TLRPC$TL_channels_deleteUserHistory extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputChannel channel;
    public TLRPC$InputUser user_id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_messages_affectedHistory.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.channel.serializeToStream(abstractSerializedData);
        this.user_id.serializeToStream(abstractSerializedData);
    }
}
