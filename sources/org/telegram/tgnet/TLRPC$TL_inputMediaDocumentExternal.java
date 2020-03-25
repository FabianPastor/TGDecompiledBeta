package org.telegram.tgnet;

public class TLRPC$TL_inputMediaDocumentExternal extends TLRPC$InputMedia {
    public static int constructor = -78455655;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.url = abstractSerializedData.readString(z);
        if ((this.flags & 1) != 0) {
            this.ttl_seconds = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeString(this.url);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.ttl_seconds);
        }
    }
}
