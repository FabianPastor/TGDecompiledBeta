package org.telegram.tgnet;

public class TLRPC$TL_chatAdminWithInvites extends TLObject {
    public static int constructor = -NUM;
    public int admin_id;
    public int invites_count;
    public int revoked_invites_count;

    public static TLRPC$TL_chatAdminWithInvites TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_chatAdminWithInvites tLRPC$TL_chatAdminWithInvites = new TLRPC$TL_chatAdminWithInvites();
            tLRPC$TL_chatAdminWithInvites.readParams(abstractSerializedData, z);
            return tLRPC$TL_chatAdminWithInvites;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_chatAdminWithInvites", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.admin_id = abstractSerializedData.readInt32(z);
        this.invites_count = abstractSerializedData.readInt32(z);
        this.revoked_invites_count = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.admin_id);
        abstractSerializedData.writeInt32(this.invites_count);
        abstractSerializedData.writeInt32(this.revoked_invites_count);
    }
}
