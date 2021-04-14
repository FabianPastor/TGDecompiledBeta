package org.telegram.tgnet;

public class TLRPC$TL_phone_joinGroupCall extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$TL_inputGroupCall call;
    public int flags;
    public String invite_hash;
    public TLRPC$InputPeer join_as;
    public boolean muted;
    public TLRPC$TL_dataJSON params;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.muted ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.call.serializeToStream(abstractSerializedData);
        this.join_as.serializeToStream(abstractSerializedData);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.invite_hash);
        }
        this.params.serializeToStream(abstractSerializedData);
    }
}
