package org.telegram.tgnet;

public class TLRPC$TL_jsonBool extends TLRPC$JSONValue {
    public static int constructor = -NUM;
    public boolean value;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.value = abstractSerializedData.readBool(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeBool(this.value);
    }
}
