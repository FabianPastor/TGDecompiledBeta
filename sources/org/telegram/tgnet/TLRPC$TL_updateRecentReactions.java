package org.telegram.tgnet;

public class TLRPC$TL_updateRecentReactions extends TLRPC$Update {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
