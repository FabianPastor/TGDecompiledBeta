package org.telegram.tgnet;

public class TLRPC$TL_inputWebFileLocation extends TLRPC$InputWebFileLocation {
    public static int constructor = -NUM;
    public long access_hash;
    public String url;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.url = abstractSerializedData.readString(z);
        this.access_hash = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.url);
        abstractSerializedData.writeInt64(this.access_hash);
    }
}
