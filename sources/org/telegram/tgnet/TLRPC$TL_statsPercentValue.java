package org.telegram.tgnet;

public class TLRPC$TL_statsPercentValue extends TLObject {
    public static int constructor = -NUM;
    public double part;
    public double total;

    public static TLRPC$TL_statsPercentValue TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_statsPercentValue tLRPC$TL_statsPercentValue = new TLRPC$TL_statsPercentValue();
            tLRPC$TL_statsPercentValue.readParams(abstractSerializedData, z);
            return tLRPC$TL_statsPercentValue;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_statsPercentValue", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.part = abstractSerializedData.readDouble(z);
        this.total = abstractSerializedData.readDouble(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeDouble(this.part);
        abstractSerializedData.writeDouble(this.total);
    }
}
