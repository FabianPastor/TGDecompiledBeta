package org.telegram.tgnet;

public class TLRPC$TL_chatInviteEmpty_layer122 extends TLRPC$TL_chatInviteExported {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
