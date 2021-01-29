package org.telegram.tgnet;

public class TLRPC$TL_chatParticipantAdmin extends TLRPC$ChatParticipant {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt32(z);
        this.inviter_id = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.user_id);
        abstractSerializedData.writeInt32(this.inviter_id);
        abstractSerializedData.writeInt32(this.date);
    }
}
