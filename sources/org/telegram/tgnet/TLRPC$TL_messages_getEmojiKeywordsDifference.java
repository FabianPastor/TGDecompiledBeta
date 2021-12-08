package org.telegram.tgnet;

public class TLRPC$TL_messages_getEmojiKeywordsDifference extends TLObject {
    public static int constructor = NUM;
    public int from_version;
    public String lang_code;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_emojiKeywordsDifference.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.lang_code);
        abstractSerializedData.writeInt32(this.from_version);
    }
}
