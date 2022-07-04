package org.telegram.tgnet;

public abstract class TLRPC$SecureValueType extends TLObject {
    public static TLRPC$SecureValueType TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$SecureValueType tLRPC$SecureValueType;
        switch (i) {
            case -1995211763:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypeBankStatement();
                break;
            case -1954007928:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypeRentalAgreement();
                break;
            case -1908627474:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypeEmail();
                break;
            case -1717268701:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypeInternalPassport();
                break;
            case -1713143702:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypePassportRegistration();
                break;
            case -1658158621:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypePersonalDetails();
                break;
            case -1596951477:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypeIdentityCard();
                break;
            case -1289704741:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypePhone();
                break;
            case -874308058:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypeAddress();
                break;
            case -368907213:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypeTemporaryRegistration();
                break;
            case -63531698:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypeUtilityBill();
                break;
            case 115615172:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypeDriverLicense();
                break;
            case 1034709504:
                tLRPC$SecureValueType = new TLRPC$TL_secureValueTypePassport();
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
        throw new RuntimeException(String.format("can't parse magic %x in SecureValueType", new Object[]{Integer.valueOf(i)}));
    }
}
