package org.telegram.tgnet;

public class TLRPC$TL_channels_viewSponsoredMessage extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputChannel channel;
    public byte[] random_id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.channel.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeByteArray(this.random_id);
    }
}
