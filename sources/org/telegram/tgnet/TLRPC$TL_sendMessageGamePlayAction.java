package org.telegram.tgnet;

public class TLRPC$TL_sendMessageGamePlayAction extends TLRPC$SendMessageAction {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}