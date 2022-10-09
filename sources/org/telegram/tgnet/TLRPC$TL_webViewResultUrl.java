package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_webViewResultUrl extends TLObject {
    public static int constructor = NUM;
    public long query_id;
    public String url;

    public static TLRPC$TL_webViewResultUrl TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_webViewResultUrl", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = new TLRPC$TL_webViewResultUrl();
        tLRPC$TL_webViewResultUrl.readParams(abstractSerializedData, z);
        return tLRPC$TL_webViewResultUrl;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.query_id = abstractSerializedData.readInt64(z);
        this.url = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.query_id);
        abstractSerializedData.writeString(this.url);
    }
}
