package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$PrivacyKey extends TLObject {
    public static TLRPC$PrivacyKey TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PrivacyKey tLRPC$PrivacyKey;
        switch (i) {
            case -1777000467:
                tLRPC$PrivacyKey = new TLRPC$PrivacyKey() { // from class: org.telegram.tgnet.TLRPC$TL_privacyKeyProfilePhoto
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1137792208:
                tLRPC$PrivacyKey = new TLRPC$PrivacyKey() { // from class: org.telegram.tgnet.TLRPC$TL_privacyKeyStatusTimestamp
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -778378131:
                tLRPC$PrivacyKey = new TLRPC$PrivacyKey() { // from class: org.telegram.tgnet.TLRPC$TL_privacyKeyPhoneNumber
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 110621716:
                tLRPC$PrivacyKey = new TLRPC$PrivacyKey() { // from class: org.telegram.tgnet.TLRPC$TL_privacyKeyVoiceMessages
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 961092808:
                tLRPC$PrivacyKey = new TLRPC$PrivacyKey() { // from class: org.telegram.tgnet.TLRPC$TL_privacyKeyPhoneP2P
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1030105979:
                tLRPC$PrivacyKey = new TLRPC$PrivacyKey() { // from class: org.telegram.tgnet.TLRPC$TL_privacyKeyPhoneCall
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1124062251:
                tLRPC$PrivacyKey = new TLRPC$PrivacyKey() { // from class: org.telegram.tgnet.TLRPC$TL_privacyKeyAddedByPhone
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1343122938:
                tLRPC$PrivacyKey = new TLRPC$PrivacyKey() { // from class: org.telegram.tgnet.TLRPC$TL_privacyKeyChatInvite
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1777096355:
                tLRPC$PrivacyKey = new TLRPC$PrivacyKey() { // from class: org.telegram.tgnet.TLRPC$TL_privacyKeyForwards
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            default:
                tLRPC$PrivacyKey = null;
                break;
        }
        if (tLRPC$PrivacyKey != null || !z) {
            if (tLRPC$PrivacyKey != null) {
                tLRPC$PrivacyKey.readParams(abstractSerializedData, z);
            }
            return tLRPC$PrivacyKey;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PrivacyKey", Integer.valueOf(i)));
    }
}
