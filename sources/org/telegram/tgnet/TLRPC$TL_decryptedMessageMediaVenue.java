package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_decryptedMessageMediaVenue extends TLRPC$DecryptedMessageMedia {
    public static int constructor = -NUM;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.lat = abstractSerializedData.readDouble(z);
        this._long = abstractSerializedData.readDouble(z);
        this.title = abstractSerializedData.readString(z);
        this.address = abstractSerializedData.readString(z);
        this.provider = abstractSerializedData.readString(z);
        this.venue_id = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeDouble(this.lat);
        abstractSerializedData.writeDouble(this._long);
        abstractSerializedData.writeString(this.title);
        abstractSerializedData.writeString(this.address);
        abstractSerializedData.writeString(this.provider);
        abstractSerializedData.writeString(this.venue_id);
    }
}
