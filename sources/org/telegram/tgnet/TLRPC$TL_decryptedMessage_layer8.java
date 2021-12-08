package org.telegram.tgnet;

public class TLRPC$TL_decryptedMessage_layer8 extends TLRPC$TL_decryptedMessage {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.random_id = abstractSerializedData.readInt64(z);
        this.random_bytes = abstractSerializedData.readByteArray(z);
        this.message = abstractSerializedData.readString(z);
        this.media = TLRPC$DecryptedMessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.random_id);
        abstractSerializedData.writeByteArray(this.random_bytes);
        abstractSerializedData.writeString(this.message);
        this.media.serializeToStream(abstractSerializedData);
    }
}
