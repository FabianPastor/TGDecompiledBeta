package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_urlAuthResultRequest extends TLRPC$UrlAuthResult {
    public static int constructor = -NUM;
    public TLRPC$User bot;
    public String domain;
    public int flags;
    public boolean request_write_access;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.request_write_access = z2;
        this.bot = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.domain = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.request_write_access ? this.flags | 1 : this.flags & (-2);
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.bot.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.domain);
    }
}
