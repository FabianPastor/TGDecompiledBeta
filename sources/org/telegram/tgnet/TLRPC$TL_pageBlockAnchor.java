package org.telegram.tgnet;

public class TLRPC$TL_pageBlockAnchor extends TLRPC$PageBlock {
    public static int constructor = -NUM;
    public String name;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.name = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.name);
    }
}
