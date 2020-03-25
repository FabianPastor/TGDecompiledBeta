package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_contacts_importedContacts extends TLObject {
    public static int constructor = NUM;
    public ArrayList<TLRPC$TL_importedContact> imported = new ArrayList<>();
    public ArrayList<TLRPC$TL_popularContact> popular_invites = new ArrayList<>();
    public ArrayList<Long> retry_contacts = new ArrayList<>();
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$TL_contacts_importedContacts TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_contacts_importedContacts tLRPC$TL_contacts_importedContacts = new TLRPC$TL_contacts_importedContacts();
            tLRPC$TL_contacts_importedContacts.readParams(abstractSerializedData, z);
            return tLRPC$TL_contacts_importedContacts;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_contacts_importedContacts", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            int i2 = 0;
            while (i2 < readInt322) {
                TLRPC$TL_importedContact TLdeserialize = TLRPC$TL_importedContact.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.imported.add(TLdeserialize);
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
                    TLRPC$TL_popularContact TLdeserialize2 = TLRPC$TL_popularContact.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.popular_invites.add(TLdeserialize2);
                        i3++;
                    } else {
                        return;
                    }
                }
                int readInt325 = abstractSerializedData.readInt32(z);
                if (readInt325 == NUM) {
                    int readInt326 = abstractSerializedData.readInt32(z);
                    for (int i4 = 0; i4 < readInt326; i4++) {
                        this.retry_contacts.add(Long.valueOf(abstractSerializedData.readInt64(z)));
                    }
                    int readInt327 = abstractSerializedData.readInt32(z);
                    if (readInt327 == NUM) {
                        int readInt328 = abstractSerializedData.readInt32(z);
                        while (i < readInt328) {
                            TLRPC$User TLdeserialize3 = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                            if (TLdeserialize3 != null) {
                                this.users.add(TLdeserialize3);
                                i++;
                            } else {
                                return;
                            }
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
        abstractSerializedData.writeInt32(NUM);
        int size = this.imported.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.imported.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.popular_invites.size();
        abstractSerializedData.writeInt32(size2);
        for (int i2 = 0; i2 < size2; i2++) {
            this.popular_invites.get(i2).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size3 = this.retry_contacts.size();
        abstractSerializedData.writeInt32(size3);
        for (int i3 = 0; i3 < size3; i3++) {
            abstractSerializedData.writeInt64(this.retry_contacts.get(i3).longValue());
        }
        abstractSerializedData.writeInt32(NUM);
        int size4 = this.users.size();
        abstractSerializedData.writeInt32(size4);
        for (int i4 = 0; i4 < size4; i4++) {
            this.users.get(i4).serializeToStream(abstractSerializedData);
        }
    }
}
