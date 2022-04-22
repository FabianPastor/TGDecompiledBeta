package org.telegram.tgnet;

public class TLRPC$TL_inputChannelEmpty extends TLRPC$InputChannel {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
