package org.telegram.tgnet;

public class TLRPC$TL_urlAuthResultAccepted extends TLRPC$UrlAuthResult {
    public static int constructor = -NUM;
    public String url;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.url = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.url);
    }
}
