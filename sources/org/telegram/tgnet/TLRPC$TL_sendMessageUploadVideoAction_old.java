package org.telegram.tgnet;

public class TLRPC$TL_sendMessageUploadVideoAction_old extends TLRPC$TL_sendMessageUploadVideoAction {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
