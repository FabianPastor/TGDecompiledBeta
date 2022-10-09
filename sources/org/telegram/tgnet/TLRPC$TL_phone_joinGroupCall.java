package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_phone_joinGroupCall extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$TL_inputGroupCall call;
    public int flags;
    public String invite_hash;
    public TLRPC$InputPeer join_as;
    public boolean muted;
    public TLRPC$TL_dataJSON params;
    public boolean video_stopped;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.muted ? this.flags | 1 : this.flags & (-2);
        this.flags = i;
        int i2 = this.video_stopped ? i | 4 : i & (-5);
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        this.call.serializeToStream(abstractSerializedData);
        this.join_as.serializeToStream(abstractSerializedData);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.invite_hash);
        }
        this.params.serializeToStream(abstractSerializedData);
    }
}
