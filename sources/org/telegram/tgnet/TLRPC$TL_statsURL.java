package org.telegram.tgnet;

public class TLRPC$TL_statsURL extends TLObject {
    public static int constructor = NUM;
    public String url;

    public static TLRPC$TL_statsURL TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_statsURL tLRPC$TL_statsURL = new TLRPC$TL_statsURL();
            tLRPC$TL_statsURL.readParams(abstractSerializedData, z);
            return tLRPC$TL_statsURL;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_statsURL", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.url = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.url);
    }
}
