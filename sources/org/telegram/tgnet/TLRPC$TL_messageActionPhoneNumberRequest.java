package org.telegram.tgnet;

public class TLRPC$TL_messageActionPhoneNumberRequest extends TLRPC$MessageAction {
    public static int constructor = 29007925;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
