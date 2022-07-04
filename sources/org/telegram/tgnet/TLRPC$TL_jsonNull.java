package org.telegram.tgnet;

public class TLRPC$TL_jsonNull extends TLRPC$JSONValue {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
