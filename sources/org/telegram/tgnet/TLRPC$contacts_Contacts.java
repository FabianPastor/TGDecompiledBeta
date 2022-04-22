package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$contacts_Contacts extends TLObject {
    public ArrayList<TLRPC$TL_contact> contacts = new ArrayList<>();
    public int saved_count;
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$contacts_Contacts TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$contacts_Contacts tLRPC$contacts_Contacts;
        if (i != -NUM) {
            tLRPC$contacts_Contacts = i != -NUM ? null : new TLRPC$TL_contacts_contacts();
        } else {
            tLRPC$contacts_Contacts = new TLRPC$TL_contacts_contactsNotModified();
        }
        if (tLRPC$contacts_Contacts != null || !z) {
            if (tLRPC$contacts_Contacts != null) {
                tLRPC$contacts_Contacts.readParams(abstractSerializedData, z);
            }
            return tLRPC$contacts_Contacts;
        }
        throw new RuntimeException(String.format("can't parse magic %x in contacts_Contacts", new Object[]{Integer.valueOf(i)}));
    }
}
