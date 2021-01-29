package org.telegram.tgnet;

public class TLRPC$TL_channelParticipantCreator_layer103 extends TLRPC$TL_channelParticipantCreator {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.user_id);
    }
}
