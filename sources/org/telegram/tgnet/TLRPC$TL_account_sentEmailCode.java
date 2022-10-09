package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_sentEmailCode extends TLObject {
    public static int constructor = -NUM;
    public String email_pattern;
    public int length;

    public static TLRPC$TL_account_sentEmailCode TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_account_sentEmailCode", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_account_sentEmailCode tLRPC$TL_account_sentEmailCode = new TLRPC$TL_account_sentEmailCode();
        tLRPC$TL_account_sentEmailCode.readParams(abstractSerializedData, z);
        return tLRPC$TL_account_sentEmailCode;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.email_pattern = abstractSerializedData.readString(z);
        this.length = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.email_pattern);
        abstractSerializedData.writeInt32(this.length);
    }
}
