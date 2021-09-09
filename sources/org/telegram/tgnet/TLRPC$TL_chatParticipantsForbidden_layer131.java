package org.telegram.tgnet;

public class TLRPC$TL_chatParticipantsForbidden_layer131 extends TLRPC$TL_chatParticipantsForbidden {
    public static int constructor = -57668565;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.chat_id = (long) abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.self_participant = TLRPC$ChatParticipant.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32((int) this.chat_id);
        if ((this.flags & 1) != 0) {
            this.self_participant.serializeToStream(abstractSerializedData);
        }
    }
}
