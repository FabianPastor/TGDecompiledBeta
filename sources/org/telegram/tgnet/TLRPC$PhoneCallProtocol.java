package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$PhoneCallProtocol extends TLObject {
    public int flags;
    public ArrayList<String> library_versions = new ArrayList<>();
    public int max_layer;
    public int min_layer;
    public boolean udp_p2p;
    public boolean udp_reflector;

    public static TLRPC$PhoneCallProtocol TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PhoneCallProtocol tLRPC$PhoneCallProtocol;
        if (i != -NUM) {
            tLRPC$PhoneCallProtocol = i != -58224696 ? null : new TLRPC$TL_phoneCallProtocol();
        } else {
            tLRPC$PhoneCallProtocol = new TLRPC$TL_phoneCallProtocol_layer110();
        }
        if (tLRPC$PhoneCallProtocol != null || !z) {
            if (tLRPC$PhoneCallProtocol != null) {
                tLRPC$PhoneCallProtocol.readParams(abstractSerializedData, z);
            }
            return tLRPC$PhoneCallProtocol;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PhoneCallProtocol", new Object[]{Integer.valueOf(i)}));
    }
}
