package org.telegram.tgnet;

public abstract class TLRPC$UrlAuthResult extends TLObject {
    public static TLRPC$UrlAuthResult TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$UrlAuthResult tLRPC$UrlAuthResult;
        if (i == -NUM) {
            tLRPC$UrlAuthResult = new TLRPC$TL_urlAuthResultAccepted();
        } else if (i != -NUM) {
            tLRPC$UrlAuthResult = i != -NUM ? null : new TLRPC$TL_urlAuthResultDefault();
        } else {
            tLRPC$UrlAuthResult = new TLRPC$TL_urlAuthResultRequest();
        }
        if (tLRPC$UrlAuthResult != null || !z) {
            if (tLRPC$UrlAuthResult != null) {
                tLRPC$UrlAuthResult.readParams(abstractSerializedData, z);
            }
            return tLRPC$UrlAuthResult;
        }
        throw new RuntimeException(String.format("can't parse magic %x in UrlAuthResult", new Object[]{Integer.valueOf(i)}));
    }
}
