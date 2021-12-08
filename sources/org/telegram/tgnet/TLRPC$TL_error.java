package org.telegram.tgnet;

public class TLRPC$TL_error extends TLObject {
    public static int constructor = -NUM;
    public int code;
    public String text;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.code = abstractSerializedData.readInt32(z);
        this.text = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.code);
        abstractSerializedData.writeString(this.text);
    }
}
