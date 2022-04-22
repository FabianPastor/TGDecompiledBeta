package org.telegram.tgnet;

public class TLRPC$TL_webAuthorization extends TLObject {
    public static int constructor = -NUM;
    public long bot_id;
    public String browser;
    public int date_active;
    public int date_created;
    public String domain;
    public long hash;
    public String ip;
    public String platform;
    public String region;

    public static TLRPC$TL_webAuthorization TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization = new TLRPC$TL_webAuthorization();
            tLRPC$TL_webAuthorization.readParams(abstractSerializedData, z);
            return tLRPC$TL_webAuthorization;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_webAuthorization", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.hash = abstractSerializedData.readInt64(z);
        this.bot_id = abstractSerializedData.readInt64(z);
        this.domain = abstractSerializedData.readString(z);
        this.browser = abstractSerializedData.readString(z);
        this.platform = abstractSerializedData.readString(z);
        this.date_created = abstractSerializedData.readInt32(z);
        this.date_active = abstractSerializedData.readInt32(z);
        this.ip = abstractSerializedData.readString(z);
        this.region = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.hash);
        abstractSerializedData.writeInt64(this.bot_id);
        abstractSerializedData.writeString(this.domain);
        abstractSerializedData.writeString(this.browser);
        abstractSerializedData.writeString(this.platform);
        abstractSerializedData.writeInt32(this.date_created);
        abstractSerializedData.writeInt32(this.date_active);
        abstractSerializedData.writeString(this.ip);
        abstractSerializedData.writeString(this.region);
    }
}
