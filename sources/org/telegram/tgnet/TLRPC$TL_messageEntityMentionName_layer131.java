package org.telegram.tgnet;

public class TLRPC$TL_messageEntityMentionName_layer131 extends TLRPC$TL_messageEntityMentionName {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.offset = abstractSerializedData.readInt32(z);
        this.length = abstractSerializedData.readInt32(z);
        this.user_id = (long) abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.offset);
        abstractSerializedData.writeInt32(this.length);
        abstractSerializedData.writeInt32((int) this.user_id);
    }
}
