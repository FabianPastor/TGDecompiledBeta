package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_inputFile;
import org.telegram.tgnet.TLRPC.TL_secureFile;

public class SecureDocument extends TLObject {
    public byte[] fileHash;
    public byte[] fileSecret;
    public TL_inputFile inputFile;
    public String path;
    public SecureDocumentKey secureDocumentKey;
    public TL_secureFile secureFile;
    public int type;

    public SecureDocument(SecureDocumentKey secureDocumentKey, TL_secureFile tL_secureFile, String str, byte[] bArr, byte[] bArr2) {
        this.secureDocumentKey = secureDocumentKey;
        this.secureFile = tL_secureFile;
        this.path = str;
        this.fileHash = bArr;
        this.fileSecret = bArr2;
    }
}
