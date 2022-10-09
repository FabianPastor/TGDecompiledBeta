package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_emojiStatusUntil extends TLRPC$EmojiStatus {
    public static int constructor = -97474361;
    public long document_id;
    public int until;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.document_id = abstractSerializedData.readInt64(z);
        this.until = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.document_id);
        abstractSerializedData.writeInt32(this.until);
    }
}
