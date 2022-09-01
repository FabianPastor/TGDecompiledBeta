package org.telegram.tgnet;

public class TLRPC$TL_account_getDefaultEmojiStatuses extends TLObject {
    public static int constructor = -NUM;
    public long hash;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$account_EmojiStatuses.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.hash);
    }
}
