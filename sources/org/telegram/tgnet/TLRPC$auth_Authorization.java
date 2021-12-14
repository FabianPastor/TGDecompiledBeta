package org.telegram.tgnet;

public abstract class TLRPC$auth_Authorization extends TLObject {
    public static TLRPC$auth_Authorization TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$auth_Authorization tLRPC$auth_Authorization;
        if (i != NUM) {
            tLRPC$auth_Authorization = i != NUM ? null : new TLRPC$TL_auth_authorizationSignUpRequired();
        } else {
            tLRPC$auth_Authorization = new TLRPC$TL_auth_authorization();
        }
        if (tLRPC$auth_Authorization != null || !z) {
            if (tLRPC$auth_Authorization != null) {
                tLRPC$auth_Authorization.readParams(abstractSerializedData, z);
            }
            return tLRPC$auth_Authorization;
        }
        throw new RuntimeException(String.format("can't parse magic %x in auth_Authorization", new Object[]{Integer.valueOf(i)}));
    }
}
