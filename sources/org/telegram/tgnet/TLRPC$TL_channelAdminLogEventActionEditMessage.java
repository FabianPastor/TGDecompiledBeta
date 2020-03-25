package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionEditMessage extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.prev_message = TLRPC$Message.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.new_message = TLRPC$Message.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.prev_message.serializeToStream(abstractSerializedData);
        this.new_message.serializeToStream(abstractSerializedData);
    }
}
