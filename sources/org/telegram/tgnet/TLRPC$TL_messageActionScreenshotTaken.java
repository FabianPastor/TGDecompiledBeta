package org.telegram.tgnet;

public class TLRPC$TL_messageActionScreenshotTaken extends TLRPC$MessageAction {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
