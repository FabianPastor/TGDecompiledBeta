package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$PrivacyRule extends TLObject {
    public static TLRPC$PrivacyRule TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PrivacyRule tLRPC$PrivacyRule;
        switch (i) {
            case -1955338397:
                tLRPC$PrivacyRule = new TLRPC$PrivacyRule() { // from class: org.telegram.tgnet.TLRPC$TL_privacyValueDisallowAll
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1198497870:
                tLRPC$PrivacyRule = new TLRPC$TL_privacyValueAllowUsers();
                break;
            case -463335103:
                tLRPC$PrivacyRule = new TLRPC$TL_privacyValueDisallowUsers();
                break;
            case -125240806:
                tLRPC$PrivacyRule = new TLRPC$PrivacyRule() { // from class: org.telegram.tgnet.TLRPC$TL_privacyValueDisallowContacts
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -123988:
                tLRPC$PrivacyRule = new TLRPC$PrivacyRule() { // from class: org.telegram.tgnet.TLRPC$TL_privacyValueAllowContacts
                    public static int constructor = -123988;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1103656293:
                tLRPC$PrivacyRule = new TLRPC$TL_privacyValueDisallowChatParticipants();
                break;
            case 1698855810:
                tLRPC$PrivacyRule = new TLRPC$PrivacyRule() { // from class: org.telegram.tgnet.TLRPC$TL_privacyValueAllowAll
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1796427406:
                tLRPC$PrivacyRule = new TLRPC$TL_privacyValueAllowChatParticipants();
                break;
            default:
                tLRPC$PrivacyRule = null;
                break;
        }
        if (tLRPC$PrivacyRule != null || !z) {
            if (tLRPC$PrivacyRule != null) {
                tLRPC$PrivacyRule.readParams(abstractSerializedData, z);
            }
            return tLRPC$PrivacyRule;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PrivacyRule", Integer.valueOf(i)));
    }
}
