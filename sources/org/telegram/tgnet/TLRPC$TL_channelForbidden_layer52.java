package org.telegram.tgnet;

public class TLRPC$TL_channelForbidden_layer52 extends TLRPC$TL_channelForbidden {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = (long) abstractSerializedData.readInt32(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.title = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32((int) this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeString(this.title);
    }
}
