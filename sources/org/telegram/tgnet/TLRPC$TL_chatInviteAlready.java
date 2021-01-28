package org.telegram.tgnet;

public class TLRPC$TL_chatInviteAlready extends TLRPC$ChatInvite {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.chat = TLRPC$Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.chat.serializeToStream(abstractSerializedData);
    }
}
