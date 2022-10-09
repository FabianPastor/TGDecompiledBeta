package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$SecureValueType extends TLObject {
    public static TLRPC$SecureValueType TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$SecureValueType tLRPC$SecureValueType;
        switch (i) {
            case -1995211763:
                tLRPC$SecureValueType = new TLRPC$SecureValueType() { // from class: org.telegram.tgnet.TLRPC$TL_secureValueTypeBankStatement
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1954007928:
                tLRPC$SecureValueType = new TLRPC$SecureValueType() { // from class: org.telegram.tgnet.TLRPC$TL_secureValueTypeRentalAgreement
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1908627474:
                tLRPC$SecureValueType = new TLRPC$SecureValueType() { // from class: org.telegram.tgnet.TLRPC$TL_secureValueTypeEmail
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1717268701:
                tLRPC$SecureValueType = new TLRPC$SecureValueType() { // from class: org.telegram.tgnet.TLRPC$TL_secureValueTypeInternalPassport
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1713143702:
                tLRPC$SecureValueType = new TLRPC$SecureValueType() { // from class: org.telegram.tgnet.TLRPC$TL_secureValueTypePassportRegistration
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1658158621:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypePersonalDetails();
                break;
            case -1596951477:
                tLRPC$SecureValueType = new TLRPC$SecureValueType() { // from class: org.telegram.tgnet.TLRPC$TL_secureValueTypeIdentityCard
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1289704741:
                tLRPC$SecureValueType = new TLRPC$SecureValueType() { // from class: org.telegram.tgnet.TLRPC$TL_secureValueTypePhone
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -874308058:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypeAddress();
                break;
            case -368907213:
                tLRPC$SecureValueType = new TLRPC$SecureValueType() { // from class: org.telegram.tgnet.TLRPC$TL_secureValueTypeTemporaryRegistration
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -63531698:
                tLRPC$SecureValueType = new TLRPC$SecureValueType() { // from class: org.telegram.tgnet.TLRPC$TL_secureValueTypeUtilityBill
                    public static int constructor = -63531698;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 115615172:
                tLRPC$SecureValueType = new TLRPC$SecureValueType() { // from class: org.telegram.tgnet.TLRPC$TL_secureValueTypeDriverLicense
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1034709504:
                tLRPC$SecureValueType = new TLRPC$SecureValueType() { // from class: org.telegram.tgnet.TLRPC$TL_secureValueTypePassport
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            default:
                tLRPC$SecureValueType = null;
                break;
        }
        if (tLRPC$SecureValueType != null || !z) {
            if (tLRPC$SecureValueType != null) {
                tLRPC$SecureValueType.readParams(abstractSerializedData, z);
            }
            return tLRPC$SecureValueType;
        }
        throw new RuntimeException(String.format("can't parse magic %x in SecureValueType", Integer.valueOf(i)));
    }
}
