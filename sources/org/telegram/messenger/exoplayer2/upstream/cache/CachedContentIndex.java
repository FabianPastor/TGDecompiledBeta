package org.telegram.messenger.exoplayer2.upstream.cache;

import android.util.Log;
import android.util.SparseArray;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
    private static final String TAG = "CachedContentIndex";
    private static final int VERSION = 1;
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
        this.encrypt = encrypt;
        if (secretKey != null) {
            Assertions.checkArgument(secretKey.length == 16);
            try {
                this.cipher = getCipher();
                this.secretKeySpec = new SecretKeySpec(secretKey, "AES");
            } catch (GeneralSecurityException e) {
                throw new IllegalStateException(e);
            }
        }
        Assertions.checkState(encrypt ^ 1);
        this.cipher = null;
        this.secretKeySpec = null;
        this.keyToContent = new HashMap();
        this.idToKey = new SparseArray();
        this.atomicFile = new AtomicFile(new File(cacheDir, FILE_NAME));
    }

    public void load() {
        Assertions.checkState(this.changed ^ 1);
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
        if (cachedContent == null) {
            return addNew(key, -1);
        }
        return cachedContent;
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

    public void setContentLength(String key, long length) {
        CachedContent cachedContent = get(key);
        if (cachedContent == null) {
            addNew(key, length);
        } else if (cachedContent.getLength() != length) {
            cachedContent.setLength(length);
            this.changed = true;
        }
    }

    public long getContentLength(String key) {
        CachedContent cachedContent = get(key);
        return cachedContent == null ? -1 : cachedContent.getLength();
    }

    private boolean readFile() {
        Closeable input = null;
        try {
            InputStream inputStream = new BufferedInputStream(this.atomicFile.openRead());
            input = new DataInputStream(inputStream);
            if (input.readInt() != 1) {
                if (input != null) {
                    Util.closeQuietly(input);
                }
                return false;
            }
            if ((input.readInt() & 1) != 0) {
                if (this.cipher == null) {
                    if (input != null) {
                        Util.closeQuietly(input);
                    }
                    return false;
                }
                byte[] initializationVector = new byte[16];
                input.readFully(initializationVector);
                this.cipher.init(2, this.secretKeySpec, new IvParameterSpec(initializationVector));
                input = new DataInputStream(new CipherInputStream(inputStream, this.cipher));
            } else if (this.encrypt) {
                this.changed = true;
            }
            int count = input.readInt();
            int hashCode = 0;
            for (int i = 0; i < count; i++) {
                CachedContent cachedContent = new CachedContent(input);
                add(cachedContent);
                hashCode += cachedContent.headerHashCode();
            }
            if (input.readInt() != hashCode) {
                if (input != null) {
                    Util.closeQuietly(input);
                }
                return false;
            }
            if (input != null) {
                Util.closeQuietly(input);
            }
            return true;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        } catch (FileNotFoundException e2) {
            if (input != null) {
                Util.closeQuietly(input);
            }
            return false;
        } catch (IOException e3) {
            try {
                Log.e(TAG, "Error reading cache content index file.", e3);
                return false;
            } finally {
                if (input != null) {
                    Util.closeQuietly(input);
                }
            }
        }
    }

    private void writeFile() throws CacheException {
        Closeable output = null;
        try {
            DataOutputStream output2;
            OutputStream outputStream = this.atomicFile.startWrite();
            if (this.bufferedOutputStream == null) {
                this.bufferedOutputStream = new ReusableBufferedOutputStream(outputStream);
            } else {
                this.bufferedOutputStream.reset(outputStream);
            }
            output = new DataOutputStream(this.bufferedOutputStream);
            output.writeInt(1);
            output.writeInt(this.encrypt);
            if (this.encrypt) {
                byte[] initializationVector = new byte[16];
                new Random().nextBytes(initializationVector);
                output.write(initializationVector);
                this.cipher.init(1, this.secretKeySpec, new IvParameterSpec(initializationVector));
                output.flush();
                output2 = new DataOutputStream(new CipherOutputStream(this.bufferedOutputStream, this.cipher));
            }
            output2.writeInt(this.keyToContent.size());
            int hashCode = 0;
            for (CachedContent cachedContent : this.keyToContent.values()) {
                cachedContent.writeToStream(output2);
                hashCode += cachedContent.headerHashCode();
            }
            output2.writeInt(hashCode);
            this.atomicFile.endWrite(output2);
            Util.closeQuietly((Closeable) null);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        } catch (Throwable e2) {
            try {
                throw new CacheException(e2);
            } catch (Throwable th) {
                Util.closeQuietly(output);
            }
        }
    }

    private void add(CachedContent cachedContent) {
        this.keyToContent.put(cachedContent.key, cachedContent);
        this.idToKey.put(cachedContent.id, cachedContent.key);
    }

    void addNew(CachedContent cachedContent) {
        add(cachedContent);
        this.changed = true;
    }

    private CachedContent addNew(String key, long length) {
        CachedContent cachedContent = new CachedContent(getNewId(this.idToKey), key, length);
        addNew(cachedContent);
        return cachedContent;
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
            while (id < size) {
                if (id != idToKey.keyAt(id)) {
                    break;
                }
                id++;
            }
        }
        return id;
    }
}
