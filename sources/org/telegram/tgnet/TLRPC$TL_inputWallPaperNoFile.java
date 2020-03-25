package org.telegram.tgnet;

public class TLRPC$TL_inputWallPaperNoFile extends TLRPC$InputWallPaper {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
