package org.telegram.tgnet;

public class TLRPC$TL_channelParticipantCreator_layer118 extends TLRPC$TL_channelParticipantCreator {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.user_id = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.rank = abstractSerializedData.readString(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32(this.user_id);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.rank);
        }
    }
}
