package org.telegram.tgnet;

public class TLRPC$TL_jsonNumber extends TLRPC$JSONValue {
    public static int constructor = NUM;
    public double value;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.value = abstractSerializedData.readDouble(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeDouble(this.value);
    }
}
