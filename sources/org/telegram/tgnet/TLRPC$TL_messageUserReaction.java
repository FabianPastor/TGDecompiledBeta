package org.telegram.tgnet;

public class TLRPC$TL_messageUserReaction extends TLObject {
    public static int constructor = -NUM;
    public String reaction;
    public long user_id;

    public static TLRPC$TL_messageUserReaction TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_messageUserReaction tLRPC$TL_messageUserReaction = new TLRPC$TL_messageUserReaction();
            tLRPC$TL_messageUserReaction.readParams(abstractSerializedData, z);
            return tLRPC$TL_messageUserReaction;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_messageUserReaction", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt64(z);
        this.reaction = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.user_id);
        abstractSerializedData.writeString(this.reaction);
    }
}
