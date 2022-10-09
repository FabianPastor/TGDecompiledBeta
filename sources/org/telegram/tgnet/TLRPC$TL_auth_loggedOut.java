package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_auth_loggedOut extends TLObject {
    public static int constructor = -NUM;
    public int flags;
    public byte[] future_auth_token;

    public static TLRPC$TL_auth_loggedOut TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_auth_loggedOut", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_auth_loggedOut tLRPC$TL_auth_loggedOut = new TLRPC$TL_auth_loggedOut();
        tLRPC$TL_auth_loggedOut.readParams(abstractSerializedData, z);
        return tLRPC$TL_auth_loggedOut;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            this.future_auth_token = abstractSerializedData.readByteArray(z);
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeByteArray(this.future_auth_token);
        }
    }
}
