package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionChangeLocation extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = NUM;
    public TLRPC$ChannelLocation new_value;
    public TLRPC$ChannelLocation prev_value;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.prev_value = TLRPC$ChannelLocation.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.new_value = TLRPC$ChannelLocation.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.prev_value.serializeToStream(abstractSerializedData);
        this.new_value.serializeToStream(abstractSerializedData);
    }
}
