package org.telegram.tgnet;

public class TLRPC$TL_channels_checkUsername extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputChannel channel;
    public String username;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.channel.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.username);
    }
}
