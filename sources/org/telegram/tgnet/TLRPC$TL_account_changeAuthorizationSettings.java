package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_changeAuthorizationSettings extends TLObject {
    public static int constructor = NUM;
    public boolean call_requests_disabled;
    public boolean encrypted_requests_disabled;
    public int flags;
    public long hash;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt64(this.hash);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeBool(this.encrypted_requests_disabled);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeBool(this.call_requests_disabled);
        }
    }
}
