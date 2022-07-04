package org.telegram.tgnet;

public class TLRPC$TL_webViewResultUrl extends TLObject {
    public static int constructor = NUM;
    public long query_id;
    public String url;

    public static TLRPC$TL_webViewResultUrl TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = new TLRPC$TL_webViewResultUrl();
            tLRPC$TL_webViewResultUrl.readParams(abstractSerializedData, z);
            return tLRPC$TL_webViewResultUrl;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_webViewResultUrl", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.query_id = abstractSerializedData.readInt64(z);
        this.url = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.query_id);
        abstractSerializedData.writeString(this.url);
    }
}
