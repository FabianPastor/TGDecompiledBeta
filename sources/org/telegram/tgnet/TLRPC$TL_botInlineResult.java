package org.telegram.tgnet;

public class TLRPC$TL_botInlineResult extends TLRPC$BotInlineResult {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.id = abstractSerializedData.readString(z);
        this.type = abstractSerializedData.readString(z);
        if ((this.flags & 2) != 0) {
            this.title = abstractSerializedData.readString(z);
        }
        if ((this.flags & 4) != 0) {
            this.description = abstractSerializedData.readString(z);
        }
        if ((this.flags & 8) != 0) {
            this.url = abstractSerializedData.readString(z);
        }
        if ((this.flags & 16) != 0) {
            this.thumb = TLRPC$WebDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 32) != 0) {
            this.content = TLRPC$WebDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        this.send_message = TLRPC$BotInlineMessage.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeString(this.id);
        abstractSerializedData.writeString(this.type);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.title);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeString(this.description);
        }
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeString(this.url);
        }
        if ((this.flags & 16) != 0) {
            this.thumb.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 32) != 0) {
            this.content.serializeToStream(abstractSerializedData);
        }
        this.send_message.serializeToStream(abstractSerializedData);
    }
}
