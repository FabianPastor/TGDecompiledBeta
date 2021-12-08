package org.telegram.messenger;

public class SecureDocumentKey {
    public byte[] file_iv;
    public byte[] file_key;

    public SecureDocumentKey(byte[] key, byte[] iv) {
        this.file_key = key;
        this.file_iv = iv;
    }
}
