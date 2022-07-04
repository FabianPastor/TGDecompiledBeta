package org.telegram.tgnet;

public class TLRPC$TL_messageEntityCustomEmoji extends TLRPC$MessageEntity {
    public static int constructor = -NUM;
    public long document_id;
    public int length;
    public int offset;
    public TLRPC$InputStickerSet stickerset;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.offset = abstractSerializedData.readInt32(z);
        this.length = abstractSerializedData.readInt32(z);
        this.stickerset = TLRPC$InputStickerSet.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.document_id = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.offset);
        abstractSerializedData.writeInt32(this.length);
        this.stickerset.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt64(this.document_id);
    }
}
