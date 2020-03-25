package org.telegram.tgnet;

public class TLRPC$TL_peerSettings extends TLObject {
    public static int constructor = -NUM;
    public boolean add_contact;
    public boolean block_contact;
    public int flags;
    public boolean need_contacts_exception;
    public boolean report_geo;
    public boolean report_spam;
    public boolean share_contact;

    public static TLRPC$TL_peerSettings TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_peerSettings tLRPC$TL_peerSettings = new TLRPC$TL_peerSettings();
            tLRPC$TL_peerSettings.readParams(abstractSerializedData, z);
            return tLRPC$TL_peerSettings;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_peerSettings", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.report_spam = (readInt32 & 1) != 0;
        this.add_contact = (this.flags & 2) != 0;
        this.block_contact = (this.flags & 4) != 0;
        this.share_contact = (this.flags & 8) != 0;
        this.need_contacts_exception = (this.flags & 16) != 0;
        if ((this.flags & 32) == 0) {
            z2 = false;
        }
        this.report_geo = z2;
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.report_spam ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.add_contact ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.block_contact ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.share_contact ? i3 | 8 : i3 & -9;
        this.flags = i4;
        int i5 = this.need_contacts_exception ? i4 | 16 : i4 & -17;
        this.flags = i5;
        int i6 = this.report_geo ? i5 | 32 : i5 & -33;
        this.flags = i6;
        abstractSerializedData.writeInt32(i6);
    }
}
