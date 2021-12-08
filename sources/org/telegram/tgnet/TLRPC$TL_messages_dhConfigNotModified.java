package org.telegram.tgnet;

public class TLRPC$TL_messages_dhConfigNotModified extends TLRPC$messages_DhConfig {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.random = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeByteArray(this.random);
    }
}
