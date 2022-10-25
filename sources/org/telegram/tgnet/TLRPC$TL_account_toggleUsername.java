package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_toggleUsername extends TLObject {
    public static int constructor = NUM;
    public boolean active;
    public String username;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.username);
        abstractSerializedData.writeBool(this.active);
    }
}
