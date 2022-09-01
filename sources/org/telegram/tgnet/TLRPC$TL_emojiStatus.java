package org.telegram.tgnet;

public class TLRPC$TL_emojiStatus extends TLRPC$EmojiStatus {
    public static int constructor = -NUM;
    public long document_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.document_id = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.document_id);
    }
}
