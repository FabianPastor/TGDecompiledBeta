package org.telegram.tgnet;

public class TLRPC$TL_channels_editPhoto extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputChannel channel;
    public TLRPC$InputChatPhoto photo;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.channel.serializeToStream(abstractSerializedData);
        this.photo.serializeToStream(abstractSerializedData);
    }
}
