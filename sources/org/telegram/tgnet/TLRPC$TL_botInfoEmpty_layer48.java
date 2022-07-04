package org.telegram.tgnet;

public class TLRPC$TL_botInfoEmpty_layer48 extends TLRPC$TL_botInfo {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
