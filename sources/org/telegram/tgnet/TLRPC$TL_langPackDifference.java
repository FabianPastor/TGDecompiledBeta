package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class TLRPC$TL_langPackDifference extends TLObject {
    public static int constructor = -NUM;
    public int from_version;
    public String lang_code;
    public ArrayList<TLRPC$LangPackString> strings = new ArrayList<>();
    public int version;

    public static TLRPC$TL_langPackDifference TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_langPackDifference", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_langPackDifference tLRPC$TL_langPackDifference = new TLRPC$TL_langPackDifference();
        tLRPC$TL_langPackDifference.readParams(abstractSerializedData, z);
        return tLRPC$TL_langPackDifference;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.lang_code = abstractSerializedData.readString(z);
        this.from_version = abstractSerializedData.readInt32(z);
        this.version = abstractSerializedData.readInt32(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
            }
            return;
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt322; i++) {
            TLRPC$LangPackString TLdeserialize = TLRPC$LangPackString.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize == null) {
                return;
            }
            this.strings.add(TLdeserialize);
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.lang_code);
        abstractSerializedData.writeInt32(this.from_version);
        abstractSerializedData.writeInt32(this.version);
        abstractSerializedData.writeInt32(NUM);
        int size = this.strings.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.strings.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
