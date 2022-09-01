package org.telegram.tgnet;

public class TLRPC$TL_reactionEmoji extends TLRPC$Reaction {
    public static int constructor = NUM;
    public String emoticon;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.emoticon = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.emoticon);
    }
}
