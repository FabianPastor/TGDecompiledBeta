package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updates_state extends TLObject {
    public static int constructor = -NUM;
    public int date;
    public int pts;
    public int qts;
    public int seq;
    public int unread_count;

    public static TLRPC$TL_updates_state TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_updates_state", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_updates_state tLRPC$TL_updates_state = new TLRPC$TL_updates_state();
        tLRPC$TL_updates_state.readParams(abstractSerializedData, z);
        return tLRPC$TL_updates_state;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.pts = abstractSerializedData.readInt32(z);
        this.qts = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
        this.seq = abstractSerializedData.readInt32(z);
        this.unread_count = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.pts);
        abstractSerializedData.writeInt32(this.qts);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.seq);
        abstractSerializedData.writeInt32(this.unread_count);
    }
}
