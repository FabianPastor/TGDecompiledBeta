package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_auth_sentCode extends TLObject {
    public static int constructor = NUM;
    public int flags;
    public TLRPC$auth_CodeType next_type;
    public String phone_code_hash;
    public int timeout;
    public TLRPC$auth_SentCodeType type;

    public static TLRPC$TL_auth_sentCode TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_auth_sentCode", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_auth_sentCode tLRPC$TL_auth_sentCode = new TLRPC$TL_auth_sentCode();
        tLRPC$TL_auth_sentCode.readParams(abstractSerializedData, z);
        return tLRPC$TL_auth_sentCode;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.type = TLRPC$auth_SentCodeType.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.phone_code_hash = abstractSerializedData.readString(z);
        if ((this.flags & 2) != 0) {
            this.next_type = TLRPC$auth_CodeType.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 4) != 0) {
            this.timeout = abstractSerializedData.readInt32(z);
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.type.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.phone_code_hash);
        if ((this.flags & 2) != 0) {
            this.next_type.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeInt32(this.timeout);
        }
    }
}
