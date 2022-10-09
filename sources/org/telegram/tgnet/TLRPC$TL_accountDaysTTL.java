package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_accountDaysTTL extends TLObject {
    public static int constructor = -NUM;
    public int days;

    public static TLRPC$TL_accountDaysTTL TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_accountDaysTTL", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_accountDaysTTL tLRPC$TL_accountDaysTTL = new TLRPC$TL_accountDaysTTL();
        tLRPC$TL_accountDaysTTL.readParams(abstractSerializedData, z);
        return tLRPC$TL_accountDaysTTL;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.days = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.days);
    }
}
