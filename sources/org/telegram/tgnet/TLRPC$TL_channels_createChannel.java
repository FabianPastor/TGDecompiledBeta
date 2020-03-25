package org.telegram.tgnet;

public class TLRPC$TL_channels_createChannel extends TLObject {
    public static int constructor = NUM;
    public String about;
    public String address;
    public boolean broadcast;
    public int flags;
    public TLRPC$InputGeoPoint geo_point;
    public boolean megagroup;
    public String title;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.broadcast ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.megagroup ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeString(this.title);
        abstractSerializedData.writeString(this.about);
        if ((this.flags & 4) != 0) {
            this.geo_point.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeString(this.address);
        }
    }
}
