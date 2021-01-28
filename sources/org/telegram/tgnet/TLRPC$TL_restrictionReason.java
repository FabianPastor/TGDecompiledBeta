package org.telegram.tgnet;

public class TLRPC$TL_restrictionReason extends TLObject {
    public static int constructor = -NUM;
    public String platform;
    public String reason;
    public String text;

    public static TLRPC$TL_restrictionReason TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_restrictionReason tLRPC$TL_restrictionReason = new TLRPC$TL_restrictionReason();
            tLRPC$TL_restrictionReason.readParams(abstractSerializedData, z);
            return tLRPC$TL_restrictionReason;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_restrictionReason", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.platform = abstractSerializedData.readString(z);
        this.reason = abstractSerializedData.readString(z);
        this.text = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.platform);
        abstractSerializedData.writeString(this.reason);
        abstractSerializedData.writeString(this.text);
    }
}
