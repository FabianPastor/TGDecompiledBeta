package org.telegram.tgnet;

public class TLRPC$TL_updateChatParticipants extends TLRPC$Update {
    public static int constructor = NUM;
    public TLRPC$ChatParticipants participants;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.participants = TLRPC$ChatParticipants.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.participants.serializeToStream(abstractSerializedData);
    }
}
