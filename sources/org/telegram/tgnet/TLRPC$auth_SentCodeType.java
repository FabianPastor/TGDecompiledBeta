package org.telegram.tgnet;

public abstract class TLRPC$auth_SentCodeType extends TLObject {
    public boolean apple_signin_allowed;
    public String email_pattern;
    public int flags;
    public boolean google_signin_allowed;
    public int length;
    public int next_phone_login_date;
    public String pattern;
    public String prefix;

    public static TLRPC$auth_SentCodeType TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$auth_SentCodeType tLRPC$auth_SentCodeType;
        switch (i) {
            case -2113903484:
                tLRPC$auth_SentCodeType = new TLRPC$TL_auth_sentCodeTypeMissedCall();
                break;
            case -1521934870:
                tLRPC$auth_SentCodeType = new TLRPC$TL_auth_sentCodeTypeSetUpEmailRequired();
                break;
            case -1425815847:
                tLRPC$auth_SentCodeType = new TLRPC$TL_auth_sentCodeTypeFlashCall();
                break;
            case -1073693790:
                tLRPC$auth_SentCodeType = new TLRPC$TL_auth_sentCodeTypeSms();
                break;
            case 1035688326:
                tLRPC$auth_SentCodeType = new TLRPC$TL_auth_sentCodeTypeApp();
                break;
            case 1398007207:
                tLRPC$auth_SentCodeType = new TLRPC$TL_auth_sentCodeTypeCall();
                break;
            case 1511364673:
                tLRPC$auth_SentCodeType = new TLRPC$TL_auth_sentCodeTypeEmailCode();
                break;
            default:
                tLRPC$auth_SentCodeType = null;
                break;
        }
        if (tLRPC$auth_SentCodeType != null || !z) {
            if (tLRPC$auth_SentCodeType != null) {
                tLRPC$auth_SentCodeType.readParams(abstractSerializedData, z);
            }
            return tLRPC$auth_SentCodeType;
        }
        throw new RuntimeException(String.format("can't parse magic %x in auth_SentCodeType", new Object[]{Integer.valueOf(i)}));
    }
}
