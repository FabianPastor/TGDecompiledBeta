package org.telegram.tgnet;

public class TLRPC$TL_decryptedMessageMediaAudio_layer8 extends TLRPC$TL_decryptedMessageMediaAudio {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.duration = abstractSerializedData.readInt32(z);
        this.size = (long) abstractSerializedData.readInt32(z);
        this.key = abstractSerializedData.readByteArray(z);
        this.iv = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.duration);
        abstractSerializedData.writeInt32((int) this.size);
        abstractSerializedData.writeByteArray(this.key);
        abstractSerializedData.writeByteArray(this.iv);
    }
}
