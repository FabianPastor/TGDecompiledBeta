package org.telegram.tgnet;

public class TLRPC$TL_theme extends TLRPC$Theme {
    public static int constructor = 42930452;
    public long access_hash;
    public boolean creator;
    public TLRPC$Document document;
    public int flags;
    public long id;
    public int installs_count;
    public boolean isDefault;
    public TLRPC$TL_themeSettings settings;
    public String slug;
    public String title;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.creator = (readInt32 & 1) != 0;
        if ((this.flags & 2) == 0) {
            z2 = false;
        }
        this.isDefault = z2;
        this.id = abstractSerializedData.readInt64(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.slug = abstractSerializedData.readString(z);
        this.title = abstractSerializedData.readString(z);
        if ((this.flags & 4) != 0) {
            this.document = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 8) != 0) {
            this.settings = TLRPC$TL_themeSettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        this.installs_count = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.creator ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.isDefault ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeString(this.slug);
        abstractSerializedData.writeString(this.title);
        if ((this.flags & 4) != 0) {
            this.document.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 8) != 0) {
            this.settings.serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(this.installs_count);
    }
}
