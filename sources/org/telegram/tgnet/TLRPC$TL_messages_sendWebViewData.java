package org.telegram.tgnet;

public class TLRPC$TL_messages_sendWebViewData extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputUser bot;
    public String button_text;
    public String data;
    public long random_id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.bot.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt64(this.random_id);
        abstractSerializedData.writeString(this.button_text);
        abstractSerializedData.writeString(this.data);
    }
}