package org.telegram.tgnet;

public class TLRPC$TL_channelMessagesFilterEmpty extends TLRPC$ChannelMessagesFilter {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
