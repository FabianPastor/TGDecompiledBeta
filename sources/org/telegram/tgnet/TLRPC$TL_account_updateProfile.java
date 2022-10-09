package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_updateProfile extends TLObject {
    public static int constructor = NUM;
    public String about;
    public String first_name;
    public int flags;
    public String last_name;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$User.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.first_name);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.last_name);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeString(this.about);
        }
    }
}
