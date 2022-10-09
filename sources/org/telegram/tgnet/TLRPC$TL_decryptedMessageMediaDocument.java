package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_decryptedMessageMediaDocument extends TLRPC$DecryptedMessageMedia {
    public static int constructor = NUM;
    public byte[] thumb;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.thumb = abstractSerializedData.readByteArray(z);
        this.thumb_w = abstractSerializedData.readInt32(z);
        this.thumb_h = abstractSerializedData.readInt32(z);
        this.mime_type = abstractSerializedData.readString(z);
        this.size = abstractSerializedData.readInt32(z);
        this.key = abstractSerializedData.readByteArray(z);
        this.iv = abstractSerializedData.readByteArray(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
            }
            return;
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt322; i++) {
            TLRPC$DocumentAttribute TLdeserialize = TLRPC$DocumentAttribute.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize == null) {
                return;
            }
            this.attributes.add(TLdeserialize);
        }
        this.caption = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeByteArray(this.thumb);
        abstractSerializedData.writeInt32(this.thumb_w);
        abstractSerializedData.writeInt32(this.thumb_h);
        abstractSerializedData.writeString(this.mime_type);
        abstractSerializedData.writeInt32((int) this.size);
        abstractSerializedData.writeByteArray(this.key);
        abstractSerializedData.writeByteArray(this.iv);
        abstractSerializedData.writeInt32(NUM);
        int size = this.attributes.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.attributes.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeString(this.caption);
    }
}
