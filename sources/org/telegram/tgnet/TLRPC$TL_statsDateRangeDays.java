package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_statsDateRangeDays extends TLObject {
    public static int constructor = -NUM;
    public int max_date;
    public int min_date;

    public static TLRPC$TL_statsDateRangeDays TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_statsDateRangeDays", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_statsDateRangeDays tLRPC$TL_statsDateRangeDays = new TLRPC$TL_statsDateRangeDays();
        tLRPC$TL_statsDateRangeDays.readParams(abstractSerializedData, z);
        return tLRPC$TL_statsDateRangeDays;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.min_date = abstractSerializedData.readInt32(z);
        this.max_date = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.min_date);
        abstractSerializedData.writeInt32(this.max_date);
    }
}
