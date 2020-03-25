package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_account_authorizationForm extends TLObject {
    public static int constructor = -NUM;
    public ArrayList<TLRPC$SecureValueError> errors = new ArrayList<>();
    public int flags;
    public String privacy_policy_url;
    public ArrayList<TLRPC$SecureRequiredType> required_types = new ArrayList<>();
    public ArrayList<TLRPC$User> users = new ArrayList<>();
    public ArrayList<TLRPC$TL_secureValue> values = new ArrayList<>();

    public static TLRPC$TL_account_authorizationForm TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm = new TLRPC$TL_account_authorizationForm();
            tLRPC$TL_account_authorizationForm.readParams(abstractSerializedData, z);
            return tLRPC$TL_account_authorizationForm;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_account_authorizationForm", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            int i2 = 0;
            while (i2 < readInt322) {
                TLRPC$SecureRequiredType TLdeserialize = TLRPC$SecureRequiredType.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.required_types.add(TLdeserialize);
                    i2++;
                } else {
                    return;
                }
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 == NUM) {
                int readInt324 = abstractSerializedData.readInt32(z);
                int i3 = 0;
                while (i3 < readInt324) {
                    TLRPC$TL_secureValue TLdeserialize2 = TLRPC$TL_secureValue.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.values.add(TLdeserialize2);
                        i3++;
                    } else {
                        return;
                    }
                }
                int readInt325 = abstractSerializedData.readInt32(z);
                if (readInt325 == NUM) {
                    int readInt326 = abstractSerializedData.readInt32(z);
                    int i4 = 0;
                    while (i4 < readInt326) {
                        TLRPC$SecureValueError TLdeserialize3 = TLRPC$SecureValueError.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                        if (TLdeserialize3 != null) {
                            this.errors.add(TLdeserialize3);
                            i4++;
                        } else {
                            return;
                        }
                    }
                    int readInt327 = abstractSerializedData.readInt32(z);
                    if (readInt327 == NUM) {
                        int readInt328 = abstractSerializedData.readInt32(z);
                        while (i < readInt328) {
                            TLRPC$User TLdeserialize4 = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                            if (TLdeserialize4 != null) {
                                this.users.add(TLdeserialize4);
                                i++;
                            } else {
                                return;
                            }
                        }
                        if ((this.flags & 1) != 0) {
                            this.privacy_policy_url = abstractSerializedData.readString(z);
                        }
                    } else if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt327)}));
                    }
                } else if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt325)}));
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt323)}));
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32(NUM);
        int size = this.required_types.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.required_types.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.values.size();
        abstractSerializedData.writeInt32(size2);
        for (int i2 = 0; i2 < size2; i2++) {
            this.values.get(i2).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size3 = this.errors.size();
        abstractSerializedData.writeInt32(size3);
        for (int i3 = 0; i3 < size3; i3++) {
            this.errors.get(i3).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size4 = this.users.size();
        abstractSerializedData.writeInt32(size4);
        for (int i4 = 0; i4 < size4; i4++) {
            this.users.get(i4).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.privacy_policy_url);
        }
    }
}
