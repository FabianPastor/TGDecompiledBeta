package org.telegram.tgnet;

public class TLRPC$TL_account_wallPapersNotModified extends TLRPC$account_WallPapers {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
