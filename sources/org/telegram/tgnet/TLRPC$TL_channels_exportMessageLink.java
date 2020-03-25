package org.telegram.tgnet;

public class TLRPC$TL_channels_exportMessageLink extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputChannel channel;
    public int id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_exportedMessageLink.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.channel.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.id);
    }
}
