package org.telegram.tgnet;

public class TLRPC$TL_messages_getAttachMenuBot extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputUser bot;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_attachMenuBotsBot.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.bot.serializeToStream(abstractSerializedData);
    }
}
