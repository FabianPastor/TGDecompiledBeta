package org.telegram.tgnet;

public class TLRPC$TL_auth_authorization extends TLRPC$auth_Authorization {
    public static int constructor = -NUM;
    public int flags;
    public int tmp_sessions;
    public TLRPC$User user;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            this.tmp_sessions = abstractSerializedData.readInt32(z);
        }
        this.user = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.tmp_sessions);
        }
        this.user.serializeToStream(abstractSerializedData);
    }
}
