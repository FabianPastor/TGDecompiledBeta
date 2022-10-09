package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updates_getDifference extends TLObject {
    public static int constructor = NUM;
    public int date;
    public int flags;
    public int pts;
    public int pts_total_limit;
    public int qts;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$updates_Difference.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32(this.pts);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.pts_total_limit);
        }
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.qts);
    }
}
