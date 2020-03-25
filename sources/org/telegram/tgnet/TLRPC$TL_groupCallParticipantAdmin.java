package org.telegram.tgnet;

public class TLRPC$TL_groupCallParticipantAdmin extends TLRPC$GroupCallParticipant {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt32(z);
        this.member_tag_hash = abstractSerializedData.readByteArray(z);
        this.streams = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.user_id);
        abstractSerializedData.writeByteArray(this.member_tag_hash);
        abstractSerializedData.writeByteArray(this.streams);
    }
}
