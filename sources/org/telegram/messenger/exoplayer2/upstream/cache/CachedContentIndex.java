package org.telegram.messenger.exoplayer2.upstream.cache;

import android.util.SparseArray;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.AtomicFile;
import org.telegram.messenger.exoplayer2.util.ReusableBufferedOutputStream;
import org.telegram.messenger.exoplayer2.util.Util;

class CachedContentIndex {
    public static final String FILE_NAME = "cached_content_index.exi";
    private static final int FLAG_ENCRYPTED_INDEX = 1;
    private static final int VERSION = 2;
    private final AtomicFile atomicFile;
    private ReusableBufferedOutputStream bufferedOutputStream;
    private boolean changed;
    private final Cipher cipher;
    private final boolean encrypt;
    private final SparseArray<String> idToKey;
    private final HashMap<String, CachedContent> keyToContent;
    private final SecretKeySpec secretKeySpec;

    public CachedContentIndex(File cacheDir) {
        this(cacheDir, null);
    }

    public CachedContentIndex(File cacheDir, byte[] secretKey) {
        this(cacheDir, secretKey, secretKey != null);
    }

    public CachedContentIndex(File cacheDir, byte[] secretKey, boolean encrypt) {
        GeneralSecurityException e;
        boolean z = true;
        this.encrypt = encrypt;
        if (secretKey != null) {
            if (secretKey.length != 16) {
                z = false;
            }
            Assertions.checkArgument(z);
            try {
                this.cipher = getCipher();
                this.secretKeySpec = new SecretKeySpec(secretKey, "AES");
            } catch (NoSuchAlgorithmException e2) {
                e = e2;
                throw new IllegalStateException(e);
            } catch (NoSuchPaddingException e3) {
                e = e3;
                throw new IllegalStateException(e);
            }
        }
        if (encrypt) {
            z = false;
        }
        Assertions.checkState(z);
        this.cipher = null;
        this.secretKeySpec = null;
        this.keyToContent = new HashMap();
        this.idToKey = new SparseArray();
        this.atomicFile = new AtomicFile(new File(cacheDir, FILE_NAME));
    }

    public void load() {
        Assertions.checkState(!this.changed);
        if (!readFile()) {
            this.atomicFile.delete();
            this.keyToContent.clear();
            this.idToKey.clear();
        }
    }

    public void store() throws CacheException {
        if (this.changed) {
            writeFile();
            this.changed = false;
        }
    }

    public CachedContent getOrAdd(String key) {
        CachedContent cachedContent = (CachedContent) this.keyToContent.get(key);
        return cachedContent == null ? addNew(key) : cachedContent;
    }

    public CachedContent get(String key) {
        return (CachedContent) this.keyToContent.get(key);
    }

    public Collection<CachedContent> getAll() {
        return this.keyToContent.values();
    }

    public int assignIdForKey(String key) {
        return getOrAdd(key).id;
    }

    public String getKeyForId(int id) {
        return (String) this.idToKey.get(id);
    }

    public void maybeRemove(String key) {
        CachedContent cachedContent = (CachedContent) this.keyToContent.get(key);
        if (cachedContent != null && cachedContent.isEmpty() && !cachedContent.isLocked()) {
            this.keyToContent.remove(key);
            this.idToKey.remove(cachedContent.id);
            this.changed = true;
        }
    }

    public void removeEmpty() {
        String[] keys = new String[this.keyToContent.size()];
        this.keyToContent.keySet().toArray(keys);
        for (String key : keys) {
            maybeRemove(key);
        }
    }

    public Set<String> getKeys() {
        return this.keyToContent.keySet();
    }

    public void applyContentMetadataMutations(String key, ContentMetadataMutations mutations) {
        if (getOrAdd(key).applyMetadataMutations(mutations)) {
            this.changed = true;
        }
    }

    public ContentMetadata getContentMetadata(String key) {
        CachedContent cachedContent = get(key);
        return cachedContent != null ? cachedContent.getMetadata() : DefaultContentMetadata.EMPTY;
    }

    private boolean readFile() {
        GeneralSecurityException e;
        Throwable th;
        Closeable closeable = null;
        try {
            InputStream inputStream = new BufferedInputStream(this.atomicFile.openRead());
            Closeable input = new DataInputStream(inputStream);
            try {
                int version = input.readInt();
                if (version < 0 || version > 2) {
                    if (input != null) {
                        Util.closeQuietly(input);
                    }
                    closeable = input;
                    return false;
                }
                if ((input.readInt() & 1) == 0) {
                    if (this.encrypt) {
                        this.changed = true;
                    }
                    closeable = input;
                } else if (this.cipher == null) {
                    if (input != null) {
                        Util.closeQuietly(input);
                    }
                    closeable = input;
                    return false;
                } else {
                    byte[] initializationVector = new byte[16];
                    input.readFully(initializationVector);
                    try {
                        this.cipher.init(2, this.secretKeySpec, new IvParameterSpec(initializationVector));
                        closeable = new DataInputStream(new CipherInputStream(inputStream, this.cipher));
                    } catch (GeneralSecurityException e2) {
                        e = e2;
                        throw new IllegalStateException(e);
                    } catch (GeneralSecurityException e22) {
                        e = e22;
                        throw new IllegalStateException(e);
                    }
                }
                int count = closeable.readInt();
                int hashCode = 0;
                for (int i = 0; i < count; i++) {
                    CachedContent cachedContent = CachedContent.readFromStream(version, closeable);
                    add(cachedContent);
                    hashCode += cachedContent.headerHashCode(version);
                }
                int fileHashCode = closeable.readInt();
                boolean isEOF = closeable.read() == -1;
                if (fileHashCode == hashCode && isEOF) {
                    if (closeable != null) {
                        Util.closeQuietly(closeable);
                    }
                    return true;
                } else if (closeable == null) {
                    return false;
                } else {
                    Util.closeQuietly(closeable);
                    return false;
                }
            } catch (IOException e3) {
                closeable = input;
                if (closeable != null) {
                    return false;
                }
                Util.closeQuietly(closeable);
                return false;
            } catch (Throwable th2) {
                th = th2;
                closeable = input;
                if (closeable != null) {
                    Util.closeQuietly(closeable);
                }
                throw th;
            }
        } catch (IOException e4) {
            if (closeable != null) {
                return false;
            }
            Util.closeQuietly(closeable);
            return false;
        } catch (Throwable th3) {
            th = th3;
            if (closeable != null) {
                Util.closeQuietly(closeable);
            }
            throw th;
        }
    }

    private void writeFile() throws CacheException {
        GeneralSecurityException e;
        Throwable e2;
        Throwable th;
        int flags = 1;
        Closeable closeable = null;
        try {
            OutputStream outputStream = this.atomicFile.startWrite();
            if (this.bufferedOutputStream == null) {
                this.bufferedOutputStream = new ReusableBufferedOutputStream(outputStream);
            } else {
                this.bufferedOutputStream.reset(outputStream);
            }
            DataOutputStream output = new DataOutputStream(this.bufferedOutputStream);
            Object output2;
            try {
                output.writeInt(2);
                if (!this.encrypt) {
                    flags = 0;
                }
                output.writeInt(flags);
                if (this.encrypt) {
                    byte[] initializationVector = new byte[16];
                    new Random().nextBytes(initializationVector);
                    output.write(initializationVector);
                    try {
                        this.cipher.init(1, this.secretKeySpec, new IvParameterSpec(initializationVector));
                        output.flush();
                        closeable = new DataOutputStream(new CipherOutputStream(this.bufferedOutputStream, this.cipher));
                    } catch (GeneralSecurityException e3) {
                        e = e3;
                        throw new IllegalStateException(e);
                    } catch (GeneralSecurityException e32) {
                        e = e32;
                        throw new IllegalStateException(e);
                    }
                }
                output2 = output;
                closeable.writeInt(this.keyToContent.size());
                int hashCode = 0;
                for (CachedContent cachedContent : this.keyToContent.values()) {
                    cachedContent.writeToStream(closeable);
                    hashCode += cachedContent.headerHashCode(2);
                }
                closeable.writeInt(hashCode);
                this.atomicFile.endWrite(closeable);
                Util.closeQuietly((Closeable) null);
            } catch (IOException e4) {
                e2 = e4;
                output2 = output;
                try {
                    throw new CacheException(e2);
                } catch (Throwable th2) {
                    th = th2;
                    Util.closeQuietly(closeable);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                output2 = output;
                Util.closeQuietly(closeable);
                throw th;
            }
        } catch (IOException e5) {
            e2 = e5;
            throw new CacheException(e2);
        }
    }

    private CachedContent addNew(String key) {
        CachedContent cachedContent = new CachedContent(getNewId(this.idToKey), key);
        add(cachedContent);
        this.changed = true;
        return cachedContent;
    }

    private void add(CachedContent cachedContent) {
        this.keyToContent.put(cachedContent.key, cachedContent);
        this.idToKey.put(cachedContent.id, cachedContent.key);
    }

    private static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        if (Util.SDK_INT == 18) {
            try {
                return Cipher.getInstance("AES/CBC/PKCS5PADDING", "BC");
            } catch (Throwable th) {
            }
        }
        return Cipher.getInstance("AES/CBC/PKCS5PADDING");
    }

    public static int getNewId(SparseArray<String> idToKey) {
        int size = idToKey.size();
        int id = size == 0 ? 0 : idToKey.keyAt(size - 1) + 1;
        if (id < 0) {
            id = 0;
            while (id < size && id == idToKey.keyAt(id)) {
                id++;
            }
        }
        return id;
    }
}
