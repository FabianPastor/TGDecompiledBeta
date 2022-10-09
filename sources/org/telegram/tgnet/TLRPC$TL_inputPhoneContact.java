package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_inputPhoneContact extends TLObject {
    public static int constructor = -NUM;
    public long client_id;
    public String first_name;
    public String last_name;
    public String phone;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.client_id = abstractSerializedData.readInt64(z);
        this.phone = abstractSerializedData.readString(z);
        this.first_name = abstractSerializedData.readString(z);
        this.last_name = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.client_id);
        abstractSerializedData.writeString(this.phone);
        abstractSerializedData.writeString(this.first_name);
        abstractSerializedData.writeString(this.last_name);
    }
}
