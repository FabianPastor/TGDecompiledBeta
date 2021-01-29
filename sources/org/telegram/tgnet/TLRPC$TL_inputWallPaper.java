package org.telegram.tgnet;

public class TLRPC$TL_inputWallPaper extends TLRPC$InputWallPaper {
    public static int constructor = -NUM;
    public long access_hash;
    public long id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt64(z);
        this.access_hash = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
    }
}
