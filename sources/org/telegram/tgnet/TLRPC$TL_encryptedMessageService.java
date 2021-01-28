package org.telegram.tgnet;

public class TLRPC$TL_encryptedMessageService extends TLRPC$EncryptedMessage {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.random_id = abstractSerializedData.readInt64(z);
        this.chat_id = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
        this.bytes = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.random_id);
        abstractSerializedData.writeInt32(this.chat_id);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeByteArray(this.bytes);
    }
}
