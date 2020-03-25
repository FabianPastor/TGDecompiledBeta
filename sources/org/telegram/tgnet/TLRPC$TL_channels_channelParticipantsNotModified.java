package org.telegram.tgnet;

public class TLRPC$TL_channels_channelParticipantsNotModified extends TLRPC$channels_ChannelParticipants {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
