package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_payments_clearSavedInfo extends TLObject {
    public static int constructor = -NUM;
    public boolean credentials;
    public int flags;
    public boolean info;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.credentials ? this.flags | 1 : this.flags & (-2);
        this.flags = i;
        int i2 = this.info ? i | 2 : i & (-3);
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
    }
}
