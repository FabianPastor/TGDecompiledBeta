package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_updateTheme extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputDocument document;
    public int flags;
    public String format;
    public TLRPC$TL_inputThemeSettings settings;
    public String slug;
    public TLRPC$InputTheme theme;
    public String title;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Theme.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeString(this.format);
        this.theme.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.slug);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.title);
        }
        if ((this.flags & 4) != 0) {
            this.document.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 8) != 0) {
            this.settings.serializeToStream(abstractSerializedData);
        }
    }
}
