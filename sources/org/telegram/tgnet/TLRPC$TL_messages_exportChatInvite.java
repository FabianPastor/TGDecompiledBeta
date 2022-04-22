package org.telegram.tgnet;

public class TLRPC$TL_messages_exportChatInvite extends TLObject {
    public static int constructor = -NUM;
    public int expire_date;
    public int flags;
    public boolean legacy_revoke_permanent;
    public TLRPC$InputPeer peer;
    public boolean request_needed;
    public String title;
    public int usage_limit;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$ExportedChatInvite.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.legacy_revoke_permanent ? this.flags | 4 : this.flags & -5;
        this.flags = i;
        int i2 = this.request_needed ? i | 8 : i & -9;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        this.peer.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.expire_date);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.usage_limit);
        }
        if ((this.flags & 16) != 0) {
            abstractSerializedData.writeString(this.title);
        }
    }
}
