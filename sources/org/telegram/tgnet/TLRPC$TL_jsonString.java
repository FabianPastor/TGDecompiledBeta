package org.telegram.tgnet;

public class TLRPC$TL_jsonString extends TLRPC$JSONValue {
    public static int constructor = -NUM;
    public String value;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.value = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.value);
    }
}
