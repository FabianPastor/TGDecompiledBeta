package org.telegram.tgnet;

public class TLRPC$TL_updateUserBlocked extends TLRPC$Update {
    public static int constructor = -NUM;
    public boolean blocked;
    public int user_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt32(z);
        this.blocked = abstractSerializedData.readBool(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.user_id);
        abstractSerializedData.writeBool(this.blocked);
    }
}
