package org.telegram.tgnet;

public class TLRPC$TL_sendMessageUploadAudioAction_old extends TLRPC$TL_sendMessageUploadAudioAction {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
