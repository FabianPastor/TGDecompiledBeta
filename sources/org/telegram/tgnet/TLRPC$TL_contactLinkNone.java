package org.telegram.tgnet;

public class TLRPC$TL_contactLinkNone extends TLRPC$ContactLink_layer101 {
    public static int constructor = -17968211;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
