package org.telegram.tgnet;

public class TLRPC$TL_channels_getSponsoredMessages extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputChannel channel;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_messages_sponsoredMessages.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.channel.serializeToStream(abstractSerializedData);
    }
}
