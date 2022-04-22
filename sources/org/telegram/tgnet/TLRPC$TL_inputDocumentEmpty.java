package org.telegram.tgnet;

public class TLRPC$TL_inputDocumentEmpty extends TLRPC$InputDocument {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
