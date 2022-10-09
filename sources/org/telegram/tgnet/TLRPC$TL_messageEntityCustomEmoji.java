package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messageEntityCustomEmoji extends TLRPC$MessageEntity {
    public static int constructor = -NUM;
    public TLRPC$Document document;
    public long document_id;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.offset = abstractSerializedData.readInt32(z);
        this.length = abstractSerializedData.readInt32(z);
        this.document_id = abstractSerializedData.readInt64(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.offset);
        abstractSerializedData.writeInt32(this.length);
        abstractSerializedData.writeInt64(this.document_id);
    }
}
