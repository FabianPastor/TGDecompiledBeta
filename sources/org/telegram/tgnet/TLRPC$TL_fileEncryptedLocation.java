package org.telegram.tgnet;

public class TLRPC$TL_fileEncryptedLocation extends TLRPC$FileLocation {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.dc_id = abstractSerializedData.readInt32(z);
        this.volume_id = abstractSerializedData.readInt64(z);
        this.local_id = abstractSerializedData.readInt32(z);
        this.secret = abstractSerializedData.readInt64(z);
        this.key = abstractSerializedData.readByteArray(z);
        this.iv = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.dc_id);
        abstractSerializedData.writeInt64(this.volume_id);
        abstractSerializedData.writeInt32(this.local_id);
        abstractSerializedData.writeInt64(this.secret);
        abstractSerializedData.writeByteArray(this.key);
        abstractSerializedData.writeByteArray(this.iv);
    }
}
