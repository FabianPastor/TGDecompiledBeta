package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_secureRequiredType extends TLRPC$SecureRequiredType {
    public static int constructor = -NUM;
    public int flags;
    public boolean native_names;
    public boolean selfie_required;
    public boolean translation_required;
    public TLRPC$SecureValueType type;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.native_names = (readInt32 & 1) != 0;
        this.selfie_required = (readInt32 & 2) != 0;
        if ((readInt32 & 4) != 0) {
            z2 = true;
        }
        this.translation_required = z2;
        this.type = TLRPC$SecureValueType.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.native_names ? this.flags | 1 : this.flags & (-2);
        this.flags = i;
        int i2 = this.selfie_required ? i | 2 : i & (-3);
        this.flags = i2;
        int i3 = this.translation_required ? i2 | 4 : i2 & (-5);
        this.flags = i3;
        abstractSerializedData.writeInt32(i3);
        this.type.serializeToStream(abstractSerializedData);
    }
}
