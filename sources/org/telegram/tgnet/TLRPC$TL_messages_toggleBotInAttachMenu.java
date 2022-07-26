package org.telegram.tgnet;

public class TLRPC$TL_messages_toggleBotInAttachMenu extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputUser bot;
    public boolean enabled;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.bot.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeBool(this.enabled);
    }
}