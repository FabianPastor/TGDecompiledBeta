package org.telegram.tgnet;

public class TLRPC$TL_userStatusLastMonth extends TLRPC$UserStatus {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
