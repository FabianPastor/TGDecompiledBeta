package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$auth_Authorization extends TLObject {
    public static TLRPC$auth_Authorization TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$auth_Authorization tLRPC$TL_auth_authorization;
        if (i != NUM) {
            tLRPC$TL_auth_authorization = i != NUM ? null : new TLRPC$TL_auth_authorizationSignUpRequired();
        } else {
            tLRPC$TL_auth_authorization = new TLRPC$TL_auth_authorization();
        }
        if (tLRPC$TL_auth_authorization != null || !z) {
            if (tLRPC$TL_auth_authorization != null) {
                tLRPC$TL_auth_authorization.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_auth_authorization;
        }
        throw new RuntimeException(String.format("can't parse magic %x in auth_Authorization", Integer.valueOf(i)));
    }
}
