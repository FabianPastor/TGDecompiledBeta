package org.telegram.tgnet;

public class TLRPC$TL_inputChatUploadedPhoto extends TLRPC$InputChatPhoto {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.file = TLRPC$InputFile.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.file.serializeToStream(abstractSerializedData);
    }
}
