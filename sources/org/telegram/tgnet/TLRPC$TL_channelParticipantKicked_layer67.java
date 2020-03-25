package org.telegram.tgnet;

public class TLRPC$TL_channelParticipantKicked_layer67 extends TLRPC$ChannelParticipant {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt32(z);
        this.kicked_by = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.user_id);
        abstractSerializedData.writeInt32(this.kicked_by);
        abstractSerializedData.writeInt32(this.date);
    }
}
