package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$auth_CodeType extends TLObject {
    public static TLRPC$auth_CodeType TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$auth_CodeType tLRPC$auth_CodeType;
        switch (i) {
            case -702884114:
                tLRPC$auth_CodeType = new TLRPC$auth_CodeType() { // from class: org.telegram.tgnet.TLRPC$TL_auth_codeTypeMissedCall
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 577556219:
                tLRPC$auth_CodeType = new TLRPC$auth_CodeType() { // from class: org.telegram.tgnet.TLRPC$TL_auth_codeTypeFlashCall
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1923290508:
                tLRPC$auth_CodeType = new TLRPC$auth_CodeType() { // from class: org.telegram.tgnet.TLRPC$TL_auth_codeTypeSms
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1948046307:
                tLRPC$auth_CodeType = new TLRPC$auth_CodeType() { // from class: org.telegram.tgnet.TLRPC$TL_auth_codeTypeCall
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            default:
                tLRPC$auth_CodeType = null;
                break;
        }
        if (tLRPC$auth_CodeType != null || !z) {
            if (tLRPC$auth_CodeType != null) {
                tLRPC$auth_CodeType.readParams(abstractSerializedData, z);
            }
            return tLRPC$auth_CodeType;
        }
        throw new RuntimeException(String.format("can't parse magic %x in auth_CodeType", Integer.valueOf(i)));
    }
}
