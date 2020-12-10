package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionDeleteMessage extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = NUM;
    public TLRPC$Message message;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.message = TLRPC$Message.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.message.serializeToStream(abstractSerializedData);
    }
}
