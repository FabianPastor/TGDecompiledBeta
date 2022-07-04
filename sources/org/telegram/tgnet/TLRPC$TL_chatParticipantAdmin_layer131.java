package org.telegram.tgnet;

public class TLRPC$TL_chatParticipantAdmin_layer131 extends TLRPC$TL_chatParticipantAdmin {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = (long) abstractSerializedData.readInt32(z);
        this.inviter_id = (long) abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32((int) this.user_id);
        abstractSerializedData.writeInt32((int) this.inviter_id);
        abstractSerializedData.writeInt32(this.date);
    }
}
