package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_simpleWebViewResultUrl extends TLObject {
    public static int constructor = -NUM;
    public String url;

    public static TLRPC$TL_simpleWebViewResultUrl TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_simpleWebViewResultUrl", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_simpleWebViewResultUrl tLRPC$TL_simpleWebViewResultUrl = new TLRPC$TL_simpleWebViewResultUrl();
        tLRPC$TL_simpleWebViewResultUrl.readParams(abstractSerializedData, z);
        return tLRPC$TL_simpleWebViewResultUrl;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.url = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.url);
    }
}
