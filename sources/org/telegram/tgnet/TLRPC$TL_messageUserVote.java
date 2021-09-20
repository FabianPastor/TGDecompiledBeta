package org.telegram.tgnet;

public class TLRPC$TL_messageUserVote extends TLRPC$MessageUserVote {
    public static int constructor = NUM;
    public byte[] option;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt64(z);
        this.option = abstractSerializedData.readByteArray(z);
        this.date = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.user_id);
        abstractSerializedData.writeByteArray(this.option);
        abstractSerializedData.writeInt32(this.date);
    }
}
