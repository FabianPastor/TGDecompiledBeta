package org.telegram.tgnet;

public class TLRPC$TL_encryptedFile_layer142 extends TLRPC$EncryptedFile {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt64(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.size = (long) abstractSerializedData.readInt32(z);
        this.dc_id = abstractSerializedData.readInt32(z);
        this.key_fingerprint = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeInt32((int) this.size);
        abstractSerializedData.writeInt32(this.dc_id);
        abstractSerializedData.writeInt32(this.key_fingerprint);
    }
}
