package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$account_EmailVerified extends TLObject {
    public static TLRPC$account_EmailVerified TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_EmailVerified tLRPC$TL_account_emailVerifiedLogin;
        if (i != -NUM) {
            tLRPC$TL_account_emailVerifiedLogin = i != NUM ? null : new TLRPC$account_EmailVerified() { // from class: org.telegram.tgnet.TLRPC$TL_account_emailVerified
                public static int constructor = NUM;
                public String email;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.email = abstractSerializedData2.readString(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeString(this.email);
                }
            };
        } else {
            tLRPC$TL_account_emailVerifiedLogin = new TLRPC$TL_account_emailVerifiedLogin();
        }
        if (tLRPC$TL_account_emailVerifiedLogin != null || !z) {
            if (tLRPC$TL_account_emailVerifiedLogin != null) {
                tLRPC$TL_account_emailVerifiedLogin.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_account_emailVerifiedLogin;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_EmailVerified", Integer.valueOf(i)));
    }
}
