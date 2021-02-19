package org.telegram.tgnet;

public class TLRPC$TL_messages_editExportedChatInvite extends TLObject {
    public static int constructor = 48562110;
    public int expire_date;
    public int flags;
    public String link;
    public TLRPC$InputPeer peer;
    public boolean revoked;
    public int usage_limit;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_ExportedChatInvite.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.revoked ? this.flags | 4 : this.flags & -5;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.link);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.expire_date);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.usage_limit);
        }
    }
}
