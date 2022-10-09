package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_phone_toggleGroupCallSettings extends TLObject {
    public static int constructor = NUM;
    public TLRPC$TL_inputGroupCall call;
    public int flags;
    public boolean join_muted;
    public boolean reset_invite_hash;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.reset_invite_hash ? this.flags | 2 : this.flags & (-3);
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.call.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeBool(this.join_muted);
        }
    }
}
