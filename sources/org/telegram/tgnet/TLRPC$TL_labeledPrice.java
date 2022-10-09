package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_labeledPrice extends TLObject {
    public static int constructor = -NUM;
    public long amount;
    public String label;

    public static TLRPC$TL_labeledPrice TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_labeledPrice", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_labeledPrice tLRPC$TL_labeledPrice = new TLRPC$TL_labeledPrice();
        tLRPC$TL_labeledPrice.readParams(abstractSerializedData, z);
        return tLRPC$TL_labeledPrice;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.label = abstractSerializedData.readString(z);
        this.amount = abstractSerializedData.readInt64(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.label);
        abstractSerializedData.writeInt64(this.amount);
    }
}
