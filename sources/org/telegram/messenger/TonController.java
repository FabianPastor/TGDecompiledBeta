package org.telegram.messenger;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.text.TextUtils;
import android.util.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import drinkless.org.ton.Client;
import drinkless.org.ton.TonApi;
import java.io.File;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.security.auth.x500.X500Principal;
import org.telegram.messenger.TonController;
import org.telegram.ui.Wallet.WalletConfigLoader;

@TargetApi(18)
public class TonController extends BaseController {
    public static final int CIPHER_INIT_FAILED = 0;
    public static final int CIPHER_INIT_KEY_INVALIDATED = 2;
    public static final int CIPHER_INIT_OK = 1;
    public static final int CONFIG_TYPE_JSON = 1;
    public static final int CONFIG_TYPE_URL = 0;
    private static volatile TonController[] Instance = new TonController[3];
    public static final int KEY_PROTECTION_TYPE_BIOMETRIC = 2;
    public static final int KEY_PROTECTION_TYPE_LOCKSCREEN = 1;
    public static final int KEY_PROTECTION_TYPE_NONE = 0;
    private static Cipher cipher;
    private static KeyPairGenerator keyGenerator;
    private static KeyStore keyStore;
    private TonApi.AccountAddress accountAddress;
    private TonApi.GenericAccountState cachedAccountState;
    private ArrayList<TonApi.RawTransaction> cachedTransactions = new ArrayList<>();
    private Client client;
    private byte[] creatingDataForLaterEncrypt;
    private String creatingEncryptedData;
    private byte[] creatingPasscodeSalt;
    private int creatingPasscodeType;
    private String creatingPublicKey;
    private String currentSetConfig;
    private String currentSetConfigName;
    private boolean initied;
    private boolean isPrealodingWallet;
    private File keyDirectoty;
    private Runnable onPendingTransactionsEmpty;
    private ArrayList<TonApi.RawTransaction> pendingTransactions = new ArrayList<>();
    private Runnable shortPollRunnable;
    private boolean shortPollingInProgress;
    private int syncProgress;
    private SharedPreferences tonCache;
    private GetTransactionsCallback uiTransactionCallback;
    private long walletId;
    private boolean walletLoaded;

    public interface AccountStateCallback {
        void run(TonApi.GenericAccountState genericAccountState);
    }

    public interface BooleanCallback {
        void run(boolean z);
    }

    public interface BytesCallback {
        void run(byte[] bArr);
    }

    public interface DangerousCallback {
        void run(TonApi.InputKey inputKey);
    }

    public interface ErrorCallback {
        void run(String str, TonApi.Error error);
    }

    public interface FeeCallback {
        void run(long j);
    }

    public interface GetTransactionsCallback {
        void run(boolean z, ArrayList<TonApi.RawTransaction> arrayList);
    }

    public interface StringCallback {
        void run(String str);
    }

    public interface TonLibCallback {
        void run(Object obj);
    }

    public interface WordsCallback {
        void run(String[] strArr);
    }

    static {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load((KeyStore.LoadStoreParameter) null);
            if (Build.VERSION.SDK_INT >= 23) {
                keyGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
                return;
            }
            keyGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static TonController getInstance(int i) {
        TonController tonController = Instance[i];
        if (tonController == null) {
            synchronized (TonController.class) {
                tonController = Instance[i];
                if (tonController == null) {
                    TonController[] tonControllerArr = Instance;
                    TonController tonController2 = new TonController(i);
                    tonControllerArr[i] = tonController2;
                    tonController = tonController2;
                }
            }
        }
        return tonController;
    }

    public TonController(int i) {
        super(i);
        Context context = ApplicationLoader.applicationContext;
        this.tonCache = context.getSharedPreferences("tonCache" + i, 0);
        loadCache();
        loadTonConfigFromUrl();
    }

    private void loadCache() {
        TonApi.RawMessage rawMessage;
        TonApi.RawMessage[] rawMessageArr;
        String str = null;
        try {
            if (this.tonCache.getBoolean("hasCache", false)) {
                this.walletLoaded = true;
                int i = this.tonCache.getInt("state.type", 0);
                long j = this.tonCache.getLong("state.balance", 0);
                int i2 = this.tonCache.getInt("state.seqno", 0);
                long j2 = this.tonCache.getLong("state.walletId", 0);
                int i3 = i;
                TonApi.InternalTransactionId internalTransactionId = new TonApi.InternalTransactionId(this.tonCache.getLong("transaction.lt", 0), Base64.decode(this.tonCache.getString("transaction.hash", (String) null), 0));
                long j3 = this.tonCache.getLong("syncUtime", 0);
                if (i3 == 0) {
                    TonApi.RawAccountState rawAccountState = r10;
                    TonApi.RawAccountState rawAccountState2 = new TonApi.RawAccountState(j, (byte[]) null, (byte[]) null, internalTransactionId, (byte[]) null, j3);
                    this.cachedAccountState = new TonApi.GenericAccountStateRaw(rawAccountState);
                } else {
                    int i4 = i3;
                    if (i4 == 1) {
                        this.cachedAccountState = new TonApi.GenericAccountStateTestWallet(new TonApi.TestWalletAccountState(j, i2, internalTransactionId, j3));
                    } else if (i4 == 2) {
                        this.cachedAccountState = new TonApi.GenericAccountStateTestGiver(new TonApi.TestGiverAccountState(j, i2, internalTransactionId, j3));
                    } else if (i4 == 3) {
                        this.cachedAccountState = new TonApi.GenericAccountStateUninited(new TonApi.UninitedAccountState(j, internalTransactionId, (byte[]) null, j3));
                    } else if (i4 == 4) {
                        this.cachedAccountState = new TonApi.GenericAccountStateWallet(new TonApi.WalletAccountState(j, i2, internalTransactionId, j3));
                    } else if (i4 == 5) {
                        this.cachedAccountState = new TonApi.GenericAccountStateWalletV3(new TonApi.WalletV3AccountState(j, j2, i2, internalTransactionId, j3));
                    }
                }
                int i5 = this.tonCache.getInt("transactionsCount", 0);
                for (int i6 = 0; i6 < i5; i6++) {
                    String str2 = "transaction" + i6 + ".";
                    if (this.tonCache.contains(str2 + "inMsg.source")) {
                        rawMessage = new TonApi.RawMessage(this.tonCache.getString(str2 + "inMsg.source", (String) null), this.tonCache.getString(str2 + "inMsg.destination", (String) null), this.tonCache.getLong(str2 + "inMsg.value", 0), this.tonCache.getLong(str2 + "inMsg.fwdFee", 0), this.tonCache.getLong(str2 + "inMsg.ihrFee", 0), this.tonCache.getLong(str2 + "inMsg.createdLt", 0), Base64.decode(this.tonCache.getString(str2 + "inMsg.bodyHash", (String) null), 0), Base64.decode(this.tonCache.getString(str2 + "inMsg.message", (String) null), 0));
                    } else {
                        rawMessage = null;
                    }
                    if (this.tonCache.contains(str2 + "outMsgCount")) {
                        TonApi.RawMessage[] rawMessageArr2 = new TonApi.RawMessage[this.tonCache.getInt(str2 + "outMsgCount", 0)];
                        for (int i7 = 0; i7 < rawMessageArr2.length; i7++) {
                            String str3 = str2 + "outMsg" + i7 + ".";
                            rawMessageArr2[i7] = new TonApi.RawMessage(this.tonCache.getString(str3 + "source", (String) null), this.tonCache.getString(str3 + "destination", (String) null), this.tonCache.getLong(str3 + "value", 0), this.tonCache.getLong(str3 + "fwdFee", 0), this.tonCache.getLong(str3 + "ihrFee", 0), this.tonCache.getLong(str3 + "createdLt", 0), Base64.decode(this.tonCache.getString(str3 + "bodyHash", (String) null), 0), Base64.decode(this.tonCache.getString(str3 + "message", (String) null), 0));
                        }
                        rawMessageArr = rawMessageArr2;
                    } else {
                        rawMessageArr = null;
                    }
                    this.cachedTransactions.add(new TonApi.RawTransaction(this.tonCache.getLong(str2 + "utime", 0), Base64.decode(this.tonCache.getString(str2 + "data", (String) null), 0), new TonApi.InternalTransactionId(this.tonCache.getLong(str2 + "lt", 0), Base64.decode(this.tonCache.getString(str2 + "hash", (String) null), 0)), this.tonCache.getLong(str2 + "fee", 0), this.tonCache.getLong(str2 + "storageFee", 0), this.tonCache.getLong(str2 + "otherFee", 0), rawMessage, rawMessageArr));
                }
                int i8 = this.tonCache.getInt("pendingCount", 0);
                int i9 = 0;
                while (i9 < i8) {
                    String str4 = "pending" + i9 + ".";
                    int i10 = i9;
                    this.pendingTransactions.add(new TonApi.RawTransaction(this.tonCache.getLong(str4 + "utime", 0), new byte[0], new TonApi.InternalTransactionId(), 0, 0, 0, new TonApi.RawMessage(this.tonCache.getString(str4 + "inMsg.source", str), this.tonCache.getString(str4 + "inMsg.destination", str), this.tonCache.getLong(str4 + "inMsg.value", 0), 0, 0, 0, Base64.decode(this.tonCache.getString(str4 + "inMsg.bodyHash", (String) null), 0), Base64.decode(this.tonCache.getString(str4 + "inMsg.message", (String) null), 0)), new TonApi.RawMessage[0]));
                    i9 = i10 + 1;
                    str = null;
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            this.cachedAccountState = null;
            this.cachedTransactions.clear();
        }
    }

    private void saveCache() {
        long j;
        TonApi.InternalTransactionId internalTransactionId;
        int i;
        long j2;
        int i2;
        TonController tonController = this;
        if (tonController.cachedAccountState != null) {
            try {
                SharedPreferences.Editor edit = tonController.tonCache.edit();
                edit.clear();
                long j3 = 0;
                if (tonController.cachedAccountState instanceof TonApi.GenericAccountStateRaw) {
                    TonApi.GenericAccountStateRaw genericAccountStateRaw = (TonApi.GenericAccountStateRaw) tonController.cachedAccountState;
                    long j4 = genericAccountStateRaw.accountState.balance;
                    TonApi.InternalTransactionId internalTransactionId2 = genericAccountStateRaw.accountState.lastTransactionId;
                    j = genericAccountStateRaw.accountState.syncUtime;
                    i = 0;
                    internalTransactionId = internalTransactionId2;
                    j2 = j4;
                    i2 = 0;
                } else if (tonController.cachedAccountState instanceof TonApi.GenericAccountStateTestWallet) {
                    TonApi.GenericAccountStateTestWallet genericAccountStateTestWallet = (TonApi.GenericAccountStateTestWallet) tonController.cachedAccountState;
                    long j5 = genericAccountStateTestWallet.accountState.balance;
                    int i3 = genericAccountStateTestWallet.accountState.seqno;
                    TonApi.InternalTransactionId internalTransactionId3 = genericAccountStateTestWallet.accountState.lastTransactionId;
                    j = genericAccountStateTestWallet.accountState.syncUtime;
                    internalTransactionId = internalTransactionId3;
                    i = i3;
                    j2 = j5;
                    i2 = 1;
                } else if (tonController.cachedAccountState instanceof TonApi.GenericAccountStateTestGiver) {
                    TonApi.GenericAccountStateTestGiver genericAccountStateTestGiver = (TonApi.GenericAccountStateTestGiver) tonController.cachedAccountState;
                    i2 = 2;
                    j2 = genericAccountStateTestGiver.accountState.balance;
                    i = genericAccountStateTestGiver.accountState.seqno;
                    internalTransactionId = genericAccountStateTestGiver.accountState.lastTransactionId;
                    j = genericAccountStateTestGiver.accountState.syncUtime;
                } else if (tonController.cachedAccountState instanceof TonApi.GenericAccountStateUninited) {
                    TonApi.GenericAccountStateUninited genericAccountStateUninited = (TonApi.GenericAccountStateUninited) tonController.cachedAccountState;
                    i2 = 3;
                    j2 = genericAccountStateUninited.accountState.balance;
                    TonApi.InternalTransactionId internalTransactionId4 = genericAccountStateUninited.accountState.lastTransactionId;
                    j = genericAccountStateUninited.accountState.syncUtime;
                    internalTransactionId = internalTransactionId4;
                    i = 0;
                } else if (tonController.cachedAccountState instanceof TonApi.GenericAccountStateWallet) {
                    TonApi.GenericAccountStateWallet genericAccountStateWallet = (TonApi.GenericAccountStateWallet) tonController.cachedAccountState;
                    i2 = 4;
                    j2 = genericAccountStateWallet.accountState.balance;
                    i = genericAccountStateWallet.accountState.seqno;
                    internalTransactionId = genericAccountStateWallet.accountState.lastTransactionId;
                    j = genericAccountStateWallet.accountState.syncUtime;
                } else if (tonController.cachedAccountState instanceof TonApi.GenericAccountStateWalletV3) {
                    TonApi.GenericAccountStateWalletV3 genericAccountStateWalletV3 = (TonApi.GenericAccountStateWalletV3) tonController.cachedAccountState;
                    long j6 = genericAccountStateWalletV3.accountState.balance;
                    int i4 = genericAccountStateWalletV3.accountState.seqno;
                    TonApi.InternalTransactionId internalTransactionId5 = genericAccountStateWalletV3.accountState.lastTransactionId;
                    long j7 = genericAccountStateWalletV3.accountState.syncUtime;
                    i = i4;
                    internalTransactionId = internalTransactionId5;
                    j3 = genericAccountStateWalletV3.accountState.walletId;
                    j2 = j6;
                    j = j7;
                    i2 = 5;
                } else {
                    return;
                }
                edit.putInt("state.type", i2);
                edit.putLong("state.balance", j2);
                edit.putInt("state.seqno", i);
                edit.putLong("state.walletId", j3);
                edit.putLong("transaction.lt", internalTransactionId.lt);
                edit.putString("transaction.hash", Base64.encodeToString(internalTransactionId.hash, 0));
                edit.putLong("syncUtime", j);
                int min = Math.min(10, tonController.cachedTransactions.size());
                edit.putInt("transactionsCount", min);
                for (int i5 = 0; i5 < min; i5++) {
                    String str = "transaction" + i5 + ".";
                    TonApi.RawTransaction rawTransaction = tonController.cachedTransactions.get(i5);
                    if (rawTransaction.inMsg != null) {
                        edit.putString(str + "inMsg.source", rawTransaction.inMsg.source);
                        edit.putString(str + "inMsg.destination", rawTransaction.inMsg.destination);
                        edit.putLong(str + "inMsg.value", rawTransaction.inMsg.value);
                        edit.putLong(str + "inMsg.fwdFee", rawTransaction.inMsg.fwdFee);
                        edit.putLong(str + "inMsg.ihrFee", rawTransaction.inMsg.ihrFee);
                        edit.putLong(str + "inMsg.createdLt", rawTransaction.inMsg.createdLt);
                        edit.putString(str + "inMsg.bodyHash", Base64.encodeToString(rawTransaction.inMsg.bodyHash, 0));
                        edit.putString(str + "inMsg.message", Base64.encodeToString(rawTransaction.inMsg.message, 0));
                    }
                    if (rawTransaction.outMsgs != null) {
                        int length = rawTransaction.outMsgs.length;
                        edit.putInt(str + "outMsgCount", length);
                        for (int i6 = 0; i6 < length; i6++) {
                            String str2 = str + "outMsg" + i6 + ".";
                            edit.putString(str2 + "source", rawTransaction.outMsgs[i6].source);
                            edit.putString(str2 + "destination", rawTransaction.outMsgs[i6].destination);
                            edit.putLong(str2 + "value", rawTransaction.outMsgs[i6].value);
                            edit.putLong(str2 + "fwdFee", rawTransaction.outMsgs[i6].fwdFee);
                            edit.putLong(str2 + "ihrFee", rawTransaction.outMsgs[i6].ihrFee);
                            edit.putLong(str2 + "createdLt", rawTransaction.outMsgs[i6].createdLt);
                            edit.putString(str2 + "bodyHash", Base64.encodeToString(rawTransaction.outMsgs[i6].bodyHash, 0));
                            edit.putString(str2 + "message", Base64.encodeToString(rawTransaction.outMsgs[i6].message, 0));
                        }
                    }
                    edit.putLong(str + "utime", rawTransaction.utime);
                    edit.putString(str + "data", Base64.encodeToString(rawTransaction.data, 0));
                    edit.putLong(str + "lt", rawTransaction.transactionId.lt);
                    edit.putString(str + "hash", Base64.encodeToString(rawTransaction.transactionId.hash, 0));
                    edit.putLong(str + "fee", rawTransaction.fee);
                    edit.putLong(str + "storageFee", rawTransaction.storageFee);
                    edit.putLong(str + "otherFee", rawTransaction.otherFee);
                }
                int size = tonController.pendingTransactions.size();
                edit.putInt("pendingCount", size);
                int i7 = 0;
                while (i7 < size) {
                    String str3 = "pending" + i7 + ".";
                    TonApi.RawTransaction rawTransaction2 = tonController.pendingTransactions.get(i7);
                    edit.putString(str3 + "inMsg.source", rawTransaction2.inMsg.source);
                    edit.putString(str3 + "inMsg.destination", rawTransaction2.inMsg.destination);
                    edit.putLong(str3 + "inMsg.value", rawTransaction2.inMsg.value);
                    edit.putString(str3 + "inMsg.bodyHash", Base64.encodeToString(rawTransaction2.inMsg.bodyHash, 0));
                    edit.putString(str3 + "inMsg.message", Base64.encodeToString(rawTransaction2.inMsg.message, 0));
                    edit.putLong(str3 + "utime", rawTransaction2.utime);
                    edit.putLong(str3 + "validUntil", rawTransaction2.otherFee);
                    i7++;
                    tonController = this;
                    size = size;
                }
                edit.putBoolean("hasCache", true);
                edit.commit();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void cleanup() {
        try {
            keyStore.deleteEntry(getUserConfig().tonKeyName);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (this.keyDirectoty != null) {
            initTonLib();
            sendRequest((TonApi.Function) new TonApi.DeleteAllKeys(), true);
        }
        cancelShortPoll();
        this.tonCache.edit().clear().commit();
        this.isPrealodingWallet = false;
        this.accountAddress = null;
        this.cachedTransactions.clear();
        this.pendingTransactions.clear();
        this.cachedAccountState = null;
        this.creatingDataForLaterEncrypt = null;
        this.creatingEncryptedData = null;
        this.creatingPublicKey = null;
        this.creatingPasscodeType = -1;
        this.creatingPasscodeSalt = null;
        this.walletLoaded = false;
    }

    public ArrayList<TonApi.RawTransaction> getCachedTransactions() {
        return this.cachedTransactions;
    }

    public ArrayList<TonApi.RawTransaction> getPendingTransactions() {
        return this.pendingTransactions;
    }

    public TonApi.GenericAccountState getCachedAccountState() {
        return this.cachedAccountState;
    }

    private boolean createKeyPair(boolean z) {
        if (Build.VERSION.SDK_INT >= 23) {
            int i = 0;
            while (i < 2) {
                try {
                    KeyGenParameterSpec.Builder keySize = new KeyGenParameterSpec.Builder(getUserConfig().tonKeyName, 3).setDigests(new String[]{"SHA-1", "SHA-256"}).setEncryptionPaddings(new String[]{"OAEPPadding"}).setKeySize(2048);
                    if (i == 0 && Build.VERSION.SDK_INT >= 28) {
                        keySize.setIsStrongBoxBacked(true);
                    }
                    if (((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).isDeviceSecure()) {
                        keySize.setUserAuthenticationRequired(true);
                        if (!z) {
                            keySize.setUserAuthenticationValidityDurationSeconds(15);
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            keySize.setInvalidatedByBiometricEnrollment(true);
                        }
                    }
                    keyGenerator.initialize(keySize.build());
                    keyGenerator.generateKeyPair();
                    return true;
                } catch (Throwable th) {
                    FileLog.e(th);
                    i++;
                }
            }
        } else {
            try {
                Calendar instance = Calendar.getInstance();
                Calendar instance2 = Calendar.getInstance();
                instance2.add(1, 30);
                keyGenerator.initialize(new KeyPairGeneratorSpec.Builder(ApplicationLoader.applicationContext).setAlias(getUserConfig().tonKeyName).setSubject(new X500Principal("CN=Telegram, O=Telegram C=UAE")).setSerialNumber(BigInteger.ONE).setStartDate(instance.getTime()).setEndDate(instance2.getTime()).build());
                keyGenerator.generateKeyPair();
                return true;
            } catch (Throwable unused) {
            }
        }
        return false;
    }

    public int getKeyProtectionType() {
        if (Build.VERSION.SDK_INT < 23) {
            return 0;
        }
        try {
            Key key = keyStore.getKey(getUserConfig().tonKeyName, (char[]) null);
            KeyInfo keyInfo = (KeyInfo) KeyFactory.getInstance(key.getAlgorithm(), "AndroidKeyStore").getKeySpec(key, KeyInfo.class);
            if (keyInfo.isUserAuthenticationRequired()) {
                return keyInfo.getUserAuthenticationValidityDurationSeconds() > 0 ? 1 : 2;
            }
            return 0;
        } catch (Exception unused) {
            return 0;
        }
    }

    private int initCipher(int i) {
        if (i == 1) {
            PublicKey publicKey = keyStore.getCertificate(getUserConfig().tonKeyName).getPublicKey();
            PublicKey generatePublic = KeyFactory.getInstance(publicKey.getAlgorithm()).generatePublic(new X509EncodedKeySpec(publicKey.getEncoded()));
            if (Build.VERSION.SDK_INT >= 23) {
                cipher.init(i, generatePublic, new OAEPParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT));
            } else {
                cipher.init(i, generatePublic);
            }
        } else if (i != 2) {
            return 0;
        } else {
            try {
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(getUserConfig().tonKeyName, (char[]) null);
                if (Build.VERSION.SDK_INT >= 23) {
                    cipher.init(i, privateKey, new OAEPParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT));
                } else {
                    cipher.init(i, privateKey);
                }
            } catch (Exception e) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (e instanceof KeyPermanentlyInvalidatedException) {
                        return 2;
                    }
                } else if (e instanceof InvalidKeyException) {
                    try {
                        if (!keyStore.containsAlias(getUserConfig().tonKeyName)) {
                            return 2;
                        }
                    } catch (Exception unused) {
                    }
                }
                if (e instanceof UnrecoverableKeyException) {
                    return 2;
                }
                FileLog.e((Throwable) e);
                return 0;
            }
        }
        return 1;
    }

    private boolean isKeyCreated(boolean z) {
        try {
            return keyStore.containsAlias(getUserConfig().tonKeyName) || createKeyPair(z);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    private String encrypt(byte[] bArr) {
        try {
            if (initCipher(1) == 1) {
                return Base64.encodeToString(cipher.doFinal(bArr), 2);
            }
            return null;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public byte[] decrypt(String str, Cipher cipher2) {
        if (cipher2 == null) {
            try {
                initCipher(2);
                cipher2 = cipher;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return null;
            }
        }
        return cipher2.doFinal(Base64.decode(str, 2));
    }

    public Cipher getCipherForDecrypt() {
        try {
            OAEPParameterSpec oAEPParameterSpec = new OAEPParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
            cipher.init(2, (PrivateKey) keyStore.getKey(getUserConfig().tonKeyName, (char[]) null), oAEPParameterSpec);
            return cipher;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    private Object sendRequest(TonApi.Function function, boolean z) {
        return sendRequest(function, (TonLibCallback) null, z);
    }

    private void sendRequest(TonApi.Function function, TonLibCallback tonLibCallback) {
        sendRequest(function, tonLibCallback, false);
    }

    private Object sendRequest(TonApi.Function function, TonLibCallback tonLibCallback, boolean z) {
        return new Object();
    }

    private static /* synthetic */ void lambda$sendRequest$0(TonLibCallback tonLibCallback, TonApi.Function function, Object[] objArr, CountDownLatch countDownLatch, TonApi.Object object) {
        if (tonLibCallback != null) {
            tonLibCallback.run(object);
            return;
        }
        if (object instanceof TonApi.Error) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("TonApi query " + function + " error " + ((TonApi.Error) object).message);
            }
            objArr[0] = object;
        } else {
            objArr[0] = object;
        }
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    private boolean initTonLib() {
        if (this.initied) {
            return true;
        }
        TonApi.Config config = getConfig();
        Object sendRequest = sendRequest((TonApi.Function) new TonApi.OptionsValidateConfig(config), true);
        if (!(sendRequest instanceof TonApi.OptionsConfigInfo)) {
            return false;
        }
        this.walletId = ((TonApi.OptionsConfigInfo) sendRequest).defaultWalletId;
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        this.keyDirectoty = new File(filesDirFixed, "ton" + this.currentAccount);
        this.keyDirectoty.mkdirs();
        this.currentSetConfig = config.config;
        this.currentSetConfigName = config.blockchainName;
        boolean z = sendRequest((TonApi.Function) new TonApi.Init(new TonApi.Options(config, new TonApi.KeyStoreTypeDirectory(this.keyDirectoty.getAbsolutePath()))), true) instanceof TonApi.Ok;
        this.initied = z;
        return z;
    }

    private TonApi.Config getConfig() {
        UserConfig userConfig = getUserConfig();
        if (userConfig.walletConfigType == 0) {
            return new TonApi.Config(userConfig.walletConfigFromUrl, userConfig.walletBlockchainName, !BuildVars.TON_WALLET_STANDALONE, false);
        }
        return new TonApi.Config(userConfig.walletConfig, userConfig.walletBlockchainName, !BuildVars.TON_WALLET_STANDALONE, false);
    }

    public boolean onTonConfigUpdated() {
        if (!this.initied) {
            return true;
        }
        TonApi.Config config = getConfig();
        if (TextUtils.equals(this.currentSetConfig, config.config) && TextUtils.equals(this.currentSetConfigName, config.blockchainName)) {
            return true;
        }
        Object sendRequest = sendRequest((TonApi.Function) new TonApi.OptionsValidateConfig(config), true);
        if (!(sendRequest instanceof TonApi.OptionsConfigInfo)) {
            return false;
        }
        this.walletId = ((TonApi.OptionsConfigInfo) sendRequest).defaultWalletId;
        this.currentSetConfig = config.config;
        this.currentSetConfigName = config.blockchainName;
        sendRequest((TonApi.Function) new TonApi.OptionsSetConfig(config), false);
        return true;
    }

    private void onFinishWalletCreate(String[] strArr, WordsCallback wordsCallback, byte[] bArr, TonApi.Key key) {
        AndroidUtilities.runOnUIThread(new Runnable(key, bArr, strArr, wordsCallback) {
            private final /* synthetic */ TonApi.Key f$1;
            private final /* synthetic */ byte[] f$2;
            private final /* synthetic */ String[] f$3;
            private final /* synthetic */ TonController.WordsCallback f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                TonController.this.lambda$onFinishWalletCreate$1$TonController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$onFinishWalletCreate$1$TonController(TonApi.Key key, byte[] bArr, String[] strArr, WordsCallback wordsCallback) {
        preloadWallet(key.publicKey);
        int length = bArr.length + 3 + key.secret.length;
        int i = length % 16;
        if (i != 0) {
            i = 16 - i;
            length += i;
        }
        byte[] bArr2 = new byte[length];
        boolean z = false;
        bArr2[0] = (byte) i;
        bArr2[1] = 111;
        bArr2[2] = 107;
        System.arraycopy(bArr, 0, bArr2, 3, bArr.length);
        byte[] bArr3 = key.secret;
        System.arraycopy(bArr3, 0, bArr2, bArr.length + 3, bArr3.length);
        if (i != 0) {
            byte[] bArr4 = new byte[i];
            Utilities.random.nextBytes(bArr4);
            System.arraycopy(bArr4, 0, bArr2, bArr.length + 3 + key.secret.length, bArr4.length);
        }
        this.creatingPublicKey = key.publicKey;
        if (getKeyProtectionType() != 0) {
            this.creatingEncryptedData = encrypt(bArr2);
            Arrays.fill(bArr2, (byte) 0);
            Arrays.fill(key.secret, (byte) 0);
            Arrays.fill(bArr, (byte) 0);
            if (strArr == null) {
                z = true;
            }
            saveWalletKeys(z);
        } else {
            this.creatingDataForLaterEncrypt = bArr2;
        }
        wordsCallback.run(strArr);
    }

    private TonApi.Error getTonApiErrorSafe(Object obj) {
        if (obj instanceof TonApi.Error) {
            return (TonApi.Error) obj;
        }
        return null;
    }

    public static TonApi.InternalTransactionId getLastTransactionId(TonApi.GenericAccountState genericAccountState) {
        if (genericAccountState instanceof TonApi.GenericAccountStateRaw) {
            return ((TonApi.GenericAccountStateRaw) genericAccountState).accountState.lastTransactionId;
        }
        if (genericAccountState instanceof TonApi.GenericAccountStateTestWallet) {
            return ((TonApi.GenericAccountStateTestWallet) genericAccountState).accountState.lastTransactionId;
        }
        if (genericAccountState instanceof TonApi.GenericAccountStateTestGiver) {
            return ((TonApi.GenericAccountStateTestGiver) genericAccountState).accountState.lastTransactionId;
        }
        if (genericAccountState instanceof TonApi.GenericAccountStateUninited) {
            return ((TonApi.GenericAccountStateUninited) genericAccountState).accountState.lastTransactionId;
        }
        if (genericAccountState instanceof TonApi.GenericAccountStateWallet) {
            return ((TonApi.GenericAccountStateWallet) genericAccountState).accountState.lastTransactionId;
        }
        if (genericAccountState instanceof TonApi.GenericAccountStateWalletV3) {
            return ((TonApi.GenericAccountStateWalletV3) genericAccountState).accountState.lastTransactionId;
        }
        return null;
    }

    public static long getLastSyncTime(TonApi.GenericAccountState genericAccountState) {
        if (genericAccountState instanceof TonApi.GenericAccountStateRaw) {
            return ((TonApi.GenericAccountStateRaw) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof TonApi.GenericAccountStateTestWallet) {
            return ((TonApi.GenericAccountStateTestWallet) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof TonApi.GenericAccountStateTestGiver) {
            return ((TonApi.GenericAccountStateTestGiver) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof TonApi.GenericAccountStateUninited) {
            return ((TonApi.GenericAccountStateUninited) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof TonApi.GenericAccountStateWallet) {
            return ((TonApi.GenericAccountStateWallet) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof TonApi.GenericAccountStateWalletV3) {
            return ((TonApi.GenericAccountStateWalletV3) genericAccountState).accountState.syncUtime;
        }
        return 0;
    }

    public static long getBalance(TonApi.GenericAccountState genericAccountState) {
        long j;
        if (genericAccountState instanceof TonApi.GenericAccountStateRaw) {
            j = ((TonApi.GenericAccountStateRaw) genericAccountState).accountState.balance;
        } else if (genericAccountState instanceof TonApi.GenericAccountStateTestWallet) {
            j = ((TonApi.GenericAccountStateTestWallet) genericAccountState).accountState.balance;
        } else if (genericAccountState instanceof TonApi.GenericAccountStateTestGiver) {
            j = ((TonApi.GenericAccountStateTestGiver) genericAccountState).accountState.balance;
        } else if (genericAccountState instanceof TonApi.GenericAccountStateUninited) {
            j = ((TonApi.GenericAccountStateUninited) genericAccountState).accountState.balance;
        } else if (genericAccountState instanceof TonApi.GenericAccountStateWallet) {
            j = ((TonApi.GenericAccountStateWallet) genericAccountState).accountState.balance;
        } else {
            j = genericAccountState instanceof TonApi.GenericAccountStateWalletV3 ? ((TonApi.GenericAccountStateWalletV3) genericAccountState).accountState.balance : 0;
        }
        if (j >= 0) {
            return j;
        }
        return 0;
    }

    public boolean isValidWalletAddress(String str) {
        return sendRequest((TonApi.Function) new TonApi.UnpackAccountAddress(str), true) instanceof TonApi.UnpackedAccountAddress;
    }

    public void isKeyStoreInvalidated(BooleanCallback booleanCallback) {
        Utilities.globalQueue.postRunnable(new Runnable(booleanCallback) {
            private final /* synthetic */ TonController.BooleanCallback f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TonController.this.lambda$isKeyStoreInvalidated$3$TonController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$isKeyStoreInvalidated$3$TonController(BooleanCallback booleanCallback) {
        AndroidUtilities.runOnUIThread(new Runnable(initCipher(2) == 2) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TonController.BooleanCallback.this.run(this.f$1);
            }
        });
    }

    public void createWallet(String[] strArr, boolean z, WordsCallback wordsCallback, ErrorCallback errorCallback) {
        AndroidUtilities.getTonWalletSalt(this.currentAccount, new BytesCallback(errorCallback, z, strArr, wordsCallback) {
            private final /* synthetic */ TonController.ErrorCallback f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ String[] f$3;
            private final /* synthetic */ TonController.WordsCallback f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run(byte[] bArr) {
                TonController.this.lambda$createWallet$11$TonController(this.f$1, this.f$2, this.f$3, this.f$4, bArr);
            }
        });
    }

    public /* synthetic */ void lambda$createWallet$11$TonController(ErrorCallback errorCallback, boolean z, String[] strArr, WordsCallback wordsCallback, byte[] bArr) {
        Utilities.globalQueue.postRunnable(new Runnable(bArr, errorCallback, z, strArr, wordsCallback) {
            private final /* synthetic */ byte[] f$1;
            private final /* synthetic */ TonController.ErrorCallback f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ String[] f$4;
            private final /* synthetic */ TonController.WordsCallback f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                TonController.this.lambda$null$10$TonController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$10$TonController(byte[] bArr, ErrorCallback errorCallback, boolean z, String[] strArr, WordsCallback wordsCallback) {
        Object obj;
        if (bArr == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TonController.ErrorCallback.this.run("SALT_GET_FAIL", (TonApi.Error) null);
                }
            });
        } else if (initTonLib()) {
            sendRequest((TonApi.Function) new TonApi.DeleteAllKeys(), true);
            if (keyStore == null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        TonController.ErrorCallback.this.run("KEYSTORE_FAIL", (TonApi.Error) null);
                    }
                });
                return;
            }
            cleanup();
            UserConfig userConfig = getUserConfig();
            userConfig.tonKeyName = "walletKey" + Utilities.random.nextLong();
            if (!isKeyCreated(z)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        TonController.ErrorCallback.this.run("KEYSTORE_FAIL", (TonApi.Error) null);
                    }
                });
                return;
            }
            byte[] bArr2 = new byte[64];
            byte[] bArr3 = new byte[32];
            Utilities.random.nextBytes(bArr2);
            Utilities.random.nextBytes(bArr3);
            if (bArr.length == 32) {
                System.arraycopy(bArr, 0, bArr2, 32, 32);
                Arrays.fill(bArr, (byte) 0);
            }
            if (strArr == null) {
                obj = sendRequest((TonApi.Function) new TonApi.CreateNewKey(bArr2, new byte[0], bArr3), true);
            } else {
                obj = sendRequest((TonApi.Function) new TonApi.ImportKey(bArr2, new byte[0], new TonApi.ExportedKey(strArr)), true);
            }
            if (obj instanceof TonApi.Key) {
                TonApi.Key key = (TonApi.Key) obj;
                if (strArr == null) {
                    Object sendRequest = sendRequest((TonApi.Function) new TonApi.ExportKey(new TonApi.InputKeyRegular(key, bArr2)), true);
                    if (sendRequest instanceof TonApi.ExportedKey) {
                        onFinishWalletCreate(((TonApi.ExportedKey) sendRequest).wordList, wordsCallback, bArr2, key);
                    } else {
                        AndroidUtilities.runOnUIThread(new Runnable(errorCallback, sendRequest) {
                            private final /* synthetic */ TonController.ErrorCallback f$1;
                            private final /* synthetic */ Object f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                TonController.this.lambda$null$6$TonController(this.f$1, this.f$2);
                            }
                        });
                    }
                } else {
                    onFinishWalletCreate((String[]) null, wordsCallback, bArr2, key);
                }
            } else {
                AndroidUtilities.runOnUIThread(new Runnable(errorCallback, obj) {
                    private final /* synthetic */ TonController.ErrorCallback f$1;
                    private final /* synthetic */ Object f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        TonController.this.lambda$null$7$TonController(this.f$1, this.f$2);
                    }
                });
            }
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TonController.ErrorCallback.this.run("TONLIB_INIT_FAIL", (TonApi.Error) null);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$6$TonController(ErrorCallback errorCallback, Object obj) {
        errorCallback.run("TONLIB_FAIL", getTonApiErrorSafe(obj));
    }

    public /* synthetic */ void lambda$null$7$TonController(ErrorCallback errorCallback, Object obj) {
        errorCallback.run("TONLIB_FAIL", getTonApiErrorSafe(obj));
    }

    public void setUserPasscode(String str, int i, Runnable runnable) {
        Utilities.globalQueue.postRunnable(new Runnable(i, str, runnable) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ Runnable f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TonController.this.lambda$setUserPasscode$12$TonController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$setUserPasscode$12$TonController(int i, String str, Runnable runnable) {
        this.creatingPasscodeType = i;
        this.creatingPasscodeSalt = new byte[32];
        Utilities.random.nextBytes(this.creatingPasscodeSalt);
        byte[] computePBKDF2 = Utilities.computePBKDF2(str.getBytes(), this.creatingPasscodeSalt);
        byte[] bArr = new byte[32];
        byte[] bArr2 = new byte[32];
        System.arraycopy(computePBKDF2, 0, bArr, 0, bArr.length);
        System.arraycopy(computePBKDF2, bArr.length, bArr2, 0, bArr2.length);
        byte[] bArr3 = this.creatingDataForLaterEncrypt;
        Utilities.aesIgeEncryptionByteArray(bArr3, bArr, bArr2, true, false, 0, bArr3.length);
        this.creatingEncryptedData = encrypt(this.creatingDataForLaterEncrypt);
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void finishSettingUserPasscode() {
        byte[] bArr = this.creatingDataForLaterEncrypt;
        if (bArr != null) {
            Arrays.fill(bArr, (byte) 0);
            this.creatingDataForLaterEncrypt = null;
        }
    }

    public boolean isWaitingForUserPasscode() {
        return this.creatingDataForLaterEncrypt != null;
    }

    public void clearPendingCache() {
        this.pendingTransactions.clear();
        saveCache();
    }

    public void saveWalletKeys(boolean z) {
        UserConfig userConfig = getUserConfig();
        String str = this.creatingEncryptedData;
        if (str != null) {
            userConfig.tonEncryptedData = str;
            userConfig.tonPublicKey = this.creatingPublicKey;
            userConfig.tonPasscodeType = this.creatingPasscodeType;
            userConfig.tonPasscodeSalt = this.creatingPasscodeSalt;
            this.creatingEncryptedData = null;
            this.creatingPublicKey = null;
            this.creatingPasscodeType = -1;
            this.creatingPasscodeSalt = null;
        }
        userConfig.tonCreationFinished = z;
        userConfig.saveConfig(false);
    }

    public Bitmap createTonQR(Context context, String str, Bitmap bitmap) {
        try {
            HashMap hashMap = new HashMap();
            hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hashMap.put(EncodeHintType.MARGIN, 0);
            return new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, 768, 768, hashMap, bitmap, context);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x005a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void shareBitmap(android.app.Activity r4, android.view.View r5, java.lang.String r6) {
        /*
            android.widget.ImageView r5 = (android.widget.ImageView) r5     // Catch:{ Exception -> 0x007c }
            android.graphics.drawable.Drawable r5 = r5.getDrawable()     // Catch:{ Exception -> 0x007c }
            android.graphics.drawable.BitmapDrawable r5 = (android.graphics.drawable.BitmapDrawable) r5     // Catch:{ Exception -> 0x007c }
            java.io.File r0 = org.telegram.messenger.AndroidUtilities.getSharingDirectory()     // Catch:{ Exception -> 0x007c }
            r0.mkdirs()     // Catch:{ Exception -> 0x007c }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x007c }
            java.lang.String r2 = "qr.jpg"
            r1.<init>(r0, r2)     // Catch:{ Exception -> 0x007c }
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x007c }
            java.lang.String r2 = r1.getAbsolutePath()     // Catch:{ Exception -> 0x007c }
            r0.<init>(r2)     // Catch:{ Exception -> 0x007c }
            android.graphics.Bitmap r5 = r5.getBitmap()     // Catch:{ Exception -> 0x007c }
            android.graphics.Bitmap$CompressFormat r2 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x007c }
            r3 = 87
            r5.compress(r2, r3, r0)     // Catch:{ Exception -> 0x007c }
            r0.close()     // Catch:{ Exception -> 0x007c }
            android.content.Intent r5 = new android.content.Intent     // Catch:{ Exception -> 0x007c }
            java.lang.String r0 = "android.intent.action.SEND"
            r5.<init>(r0)     // Catch:{ Exception -> 0x007c }
            java.lang.String r0 = "image/jpeg"
            r5.setType(r0)     // Catch:{ Exception -> 0x007c }
            boolean r0 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x007c }
            if (r0 != 0) goto L_0x0044
            java.lang.String r0 = "android.intent.extra.TEXT"
            r5.putExtra(r0, r6)     // Catch:{ Exception -> 0x007c }
        L_0x0044:
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x007c }
            r0 = 24
            java.lang.String r2 = "android.intent.extra.STREAM"
            if (r6 < r0) goto L_0x0062
            java.lang.String r6 = "org.telegram.messenger.provider"
            android.net.Uri r6 = androidx.core.content.FileProvider.getUriForFile(r4, r6, r1)     // Catch:{ Exception -> 0x005a }
            r5.putExtra(r2, r6)     // Catch:{ Exception -> 0x005a }
            r6 = 1
            r5.setFlags(r6)     // Catch:{ Exception -> 0x005a }
            goto L_0x0069
        L_0x005a:
            android.net.Uri r6 = android.net.Uri.fromFile(r1)     // Catch:{ Exception -> 0x007c }
            r5.putExtra(r2, r6)     // Catch:{ Exception -> 0x007c }
            goto L_0x0069
        L_0x0062:
            android.net.Uri r6 = android.net.Uri.fromFile(r1)     // Catch:{ Exception -> 0x007c }
            r5.putExtra(r2, r6)     // Catch:{ Exception -> 0x007c }
        L_0x0069:
            java.lang.String r6 = "WalletShareQr"
            r0 = 2131627106(0x7f0e0CLASSNAME, float:1.8881467E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r0)     // Catch:{ Exception -> 0x007c }
            android.content.Intent r5 = android.content.Intent.createChooser(r5, r6)     // Catch:{ Exception -> 0x007c }
            r6 = 500(0x1f4, float:7.0E-43)
            r4.startActivityForResult(r5, r6)     // Catch:{ Exception -> 0x007c }
            goto L_0x0080
        L_0x007c:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x0080:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.TonController.shareBitmap(android.app.Activity, android.view.View, java.lang.String):void");
    }

    public String[] getHintWords() {
        initTonLib();
        Object sendRequest = sendRequest((TonApi.Function) new TonApi.GetBip39Hints(), true);
        if (sendRequest instanceof TonApi.Bip39Hints) {
            return ((TonApi.Bip39Hints) sendRequest).words;
        }
        return null;
    }

    public String getWalletAddress(String str) {
        TonApi.AccountAddress accountAddress2 = this.accountAddress;
        if (accountAddress2 != null) {
            return accountAddress2.accountAddress;
        }
        initTonLib();
        Object sendRequest = sendRequest((TonApi.Function) new TonApi.WalletV3GetAccountAddress(new TonApi.WalletV3InitialAccountState(str, this.walletId)), true);
        if (!(sendRequest instanceof TonApi.AccountAddress)) {
            return null;
        }
        TonApi.AccountAddress accountAddress3 = (TonApi.AccountAddress) sendRequest;
        this.accountAddress = accountAddress3;
        return accountAddress3.accountAddress;
    }

    public void checkPendingTransactionsForFailure(TonApi.GenericAccountState genericAccountState) {
        if (genericAccountState != null && !this.pendingTransactions.isEmpty()) {
            long lastSyncTime = getLastSyncTime(genericAccountState);
            int size = this.pendingTransactions.size();
            int i = 0;
            boolean z = false;
            while (i < size) {
                if (this.pendingTransactions.get(i).otherFee <= lastSyncTime) {
                    this.pendingTransactions.remove(i);
                    size--;
                    i--;
                    z = true;
                }
                i++;
            }
            if (this.onPendingTransactionsEmpty != null && this.pendingTransactions.isEmpty()) {
                this.onPendingTransactionsEmpty.run();
                this.onPendingTransactionsEmpty = null;
            }
            if (z) {
                saveCache();
                getNotificationCenter().postNotificationName(NotificationCenter.walletPendingTransactionsChanged, new Object[0]);
            }
        }
    }

    public void setTransactionCallback(GetTransactionsCallback getTransactionsCallback) {
        this.uiTransactionCallback = getTransactionsCallback;
    }

    public void getTransactions(boolean z, TonApi.InternalTransactionId internalTransactionId) {
        getTransactions(z, internalTransactionId, (Runnable) null);
    }

    private void getTransactions(boolean z, TonApi.InternalTransactionId internalTransactionId, Runnable runnable) {
        sendRequest((TonApi.Function) new TonApi.RawGetTransactions(this.accountAddress, internalTransactionId), (TonLibCallback) new TonLibCallback(z, runnable) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ Runnable f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(Object obj) {
                TonController.this.lambda$getTransactions$15$TonController(this.f$1, this.f$2, obj);
            }
        });
    }

    public /* synthetic */ void lambda$getTransactions$15$TonController(boolean z, Runnable runnable, Object obj) {
        if (obj instanceof TonApi.RawTransactions) {
            AndroidUtilities.runOnUIThread(new Runnable(new ArrayList(Arrays.asList(((TonApi.RawTransactions) obj).transactions)), z, runnable) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ Runnable f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    TonController.this.lambda$null$13$TonController(this.f$1, this.f$2, this.f$3);
                }
            });
        } else if (runnable != null || this.uiTransactionCallback != null) {
            AndroidUtilities.runOnUIThread(new Runnable(runnable, z) {
                private final /* synthetic */ Runnable f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    TonController.this.lambda$null$14$TonController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$13$TonController(ArrayList arrayList, boolean z, Runnable runnable) {
        this.walletLoaded = true;
        boolean z2 = false;
        if (!this.pendingTransactions.isEmpty()) {
            int size = this.pendingTransactions.size();
            int i = 0;
            boolean z3 = false;
            while (i < size) {
                TonApi.RawTransaction rawTransaction = this.pendingTransactions.get(i);
                int size2 = arrayList.size();
                int i2 = 0;
                while (true) {
                    if (i2 < size2) {
                        TonApi.RawMessage rawMessage = ((TonApi.RawTransaction) arrayList.get(i2)).inMsg;
                        if (rawMessage != null && Arrays.equals(rawTransaction.inMsg.bodyHash, rawMessage.bodyHash)) {
                            this.pendingTransactions.remove(i);
                            size--;
                            i--;
                            z3 = true;
                            break;
                        }
                        i2++;
                    } else {
                        break;
                    }
                }
                i++;
            }
            z2 = z3;
        }
        if (this.onPendingTransactionsEmpty != null && this.pendingTransactions.isEmpty()) {
            this.onPendingTransactionsEmpty.run();
            this.onPendingTransactionsEmpty = null;
        }
        if (z) {
            this.cachedTransactions.clear();
            this.cachedTransactions.addAll(arrayList);
            saveCache();
        } else if (z2) {
            saveCache();
        }
        if (runnable != null) {
            runnable.run();
        }
        GetTransactionsCallback getTransactionsCallback = this.uiTransactionCallback;
        if (getTransactionsCallback != null) {
            getTransactionsCallback.run(z, arrayList);
        }
    }

    public /* synthetic */ void lambda$null$14$TonController(Runnable runnable, boolean z) {
        if (runnable != null) {
            runnable.run();
        }
        GetTransactionsCallback getTransactionsCallback = this.uiTransactionCallback;
        if (getTransactionsCallback != null) {
            getTransactionsCallback.run(z, (ArrayList<TonApi.RawTransaction>) null);
        }
    }

    private void preloadWallet(String str) {
        if (!this.isPrealodingWallet) {
            this.isPrealodingWallet = true;
            getWalletAddress(str);
            getAccountState(new AccountStateCallback() {
                public final void run(TonApi.GenericAccountState genericAccountState) {
                    TonController.this.lambda$preloadWallet$17$TonController(genericAccountState);
                }
            });
        }
    }

    public /* synthetic */ void lambda$preloadWallet$17$TonController(TonApi.GenericAccountState genericAccountState) {
        if (genericAccountState == null) {
            this.isPrealodingWallet = false;
        } else {
            getTransactions(true, getLastTransactionId(genericAccountState), new Runnable() {
                public final void run() {
                    TonController.this.lambda$null$16$TonController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$16$TonController() {
        this.isPrealodingWallet = false;
    }

    public boolean isWalletLoaded() {
        return this.walletLoaded;
    }

    public int getSyncProgress() {
        return this.syncProgress;
    }

    public void getAccountState(AccountStateCallback accountStateCallback) {
        sendRequest((TonApi.Function) new TonApi.GenericGetAccountState(this.accountAddress), (TonLibCallback) new TonLibCallback(accountStateCallback) {
            private final /* synthetic */ TonController.AccountStateCallback f$1;

            {
                this.f$1 = r2;
            }

            public final void run(Object obj) {
                TonController.this.lambda$getAccountState$20$TonController(this.f$1, obj);
            }
        });
    }

    public /* synthetic */ void lambda$getAccountState$20$TonController(AccountStateCallback accountStateCallback, Object obj) {
        if (obj instanceof TonApi.GenericAccountState) {
            AndroidUtilities.runOnUIThread(new Runnable(accountStateCallback, obj) {
                private final /* synthetic */ TonController.AccountStateCallback f$1;
                private final /* synthetic */ Object f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    TonController.this.lambda$null$18$TonController(this.f$1, this.f$2);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TonController.AccountStateCallback.this.run((TonApi.GenericAccountState) null);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$18$TonController(AccountStateCallback accountStateCallback, Object obj) {
        TonApi.GenericAccountState genericAccountState = (TonApi.GenericAccountState) obj;
        this.cachedAccountState = genericAccountState;
        accountStateCallback.run(genericAccountState);
    }

    private TonApi.InputKey decryptTonData(String str, Cipher cipher2, Runnable runnable, ErrorCallback errorCallback, boolean z) {
        ErrorCallback errorCallback2 = errorCallback;
        UserConfig userConfig = getUserConfig();
        byte[] decrypt = decrypt(userConfig.tonEncryptedData, cipher2);
        if (decrypt == null || decrypt.length <= 3) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TonController.ErrorCallback.this.run("KEYSTORE_FAIL", (TonApi.Error) null);
                }
            });
            return null;
        }
        if (userConfig.tonPasscodeType != -1) {
            byte[] computePBKDF2 = Utilities.computePBKDF2(str.getBytes(), userConfig.tonPasscodeSalt);
            byte[] bArr = new byte[32];
            byte[] bArr2 = new byte[32];
            System.arraycopy(computePBKDF2, 0, bArr, 0, bArr.length);
            System.arraycopy(computePBKDF2, bArr.length, bArr2, 0, bArr2.length);
            Utilities.aesIgeEncryptionByteArray(decrypt, bArr, bArr2, false, false, 0, decrypt.length);
        }
        if (decrypt[1] == 111 && decrypt[2] == 107) {
            if (!TextUtils.isEmpty(str) && runnable != null) {
                AndroidUtilities.runOnUIThread(runnable);
            }
            byte[] bArr3 = new byte[64];
            byte[] bArr4 = new byte[(((decrypt.length - 64) - decrypt[0]) - 3)];
            System.arraycopy(decrypt, 3, bArr3, 0, bArr3.length);
            System.arraycopy(decrypt, bArr3.length + 3, bArr4, 0, bArr4.length);
            if (z) {
                this.creatingDataForLaterEncrypt = decrypt;
                this.creatingPublicKey = userConfig.tonPublicKey;
            }
            return new TonApi.InputKeyRegular(new TonApi.Key(userConfig.tonPublicKey, bArr4), bArr3);
        }
        if (!TextUtils.isEmpty(str)) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TonController.ErrorCallback.this.run("PASSCODE_INVALID", (TonApi.Error) null);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TonController.ErrorCallback.this.run("KEYSTORE_FAIL_DECRYPT", (TonApi.Error) null);
                }
            });
        }
        return null;
    }

    public void prepareForPasscodeChange(String str, Runnable runnable, ErrorCallback errorCallback) {
        Utilities.globalQueue.postRunnable(new Runnable(str, errorCallback, runnable) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ TonController.ErrorCallback f$2;
            private final /* synthetic */ Runnable f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TonController.this.lambda$prepareForPasscodeChange$24$TonController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$prepareForPasscodeChange$24$TonController(String str, ErrorCallback errorCallback, Runnable runnable) {
        if (decryptTonData(str, (Cipher) null, (Runnable) null, errorCallback, true) != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public void getSecretWords(String str, Cipher cipher2, WordsCallback wordsCallback, ErrorCallback errorCallback) {
        Utilities.globalQueue.postRunnable(new Runnable(str, cipher2, errorCallback, wordsCallback) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ Cipher f$2;
            private final /* synthetic */ TonController.ErrorCallback f$3;
            private final /* synthetic */ TonController.WordsCallback f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                TonController.this.lambda$getSecretWords$28$TonController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$getSecretWords$28$TonController(String str, Cipher cipher2, ErrorCallback errorCallback, WordsCallback wordsCallback) {
        initTonLib();
        TonApi.InputKey decryptTonData = decryptTonData(str, cipher2, (Runnable) null, errorCallback, false);
        if (decryptTonData != null) {
            sendRequest((TonApi.Function) new TonApi.ExportKey(decryptTonData), (TonLibCallback) new TonLibCallback(wordsCallback, errorCallback) {
                private final /* synthetic */ TonController.WordsCallback f$1;
                private final /* synthetic */ TonController.ErrorCallback f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(Object obj) {
                    TonController.this.lambda$null$27$TonController(this.f$1, this.f$2, obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$27$TonController(WordsCallback wordsCallback, ErrorCallback errorCallback, Object obj) {
        if (obj instanceof TonApi.ExportedKey) {
            AndroidUtilities.runOnUIThread(new Runnable((TonApi.ExportedKey) obj) {
                private final /* synthetic */ TonApi.ExportedKey f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TonController.WordsCallback.this.run(this.f$1.wordList);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(errorCallback, obj) {
                private final /* synthetic */ TonController.ErrorCallback f$1;
                private final /* synthetic */ Object f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    TonController.this.lambda$null$26$TonController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$26$TonController(ErrorCallback errorCallback, Object obj) {
        errorCallback.run("TONLIB_FAIL", getTonApiErrorSafe(obj));
    }

    public void getSendFee(String str, String str2, long j, String str3, FeeCallback feeCallback) {
        Utilities.globalQueue.postRunnable(new Runnable(str, str2, j, str3, feeCallback) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ long f$3;
            private final /* synthetic */ String f$4;
            private final /* synthetic */ TonController.FeeCallback f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
            }

            public final void run() {
                TonController.this.lambda$getSendFee$34$TonController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$getSendFee$34$TonController(String str, String str2, long j, String str3, FeeCallback feeCallback) {
        String str4 = str;
        String str5 = str2;
        sendRequest((TonApi.Function) new TonApi.GenericCreateSendGramsQuery(new TonApi.InputKeyFake(), new TonApi.AccountAddress(str), new TonApi.AccountAddress(str2), j, 0, true, str3 != null ? str3.getBytes() : new byte[0]), (TonLibCallback) new TonLibCallback(feeCallback) {
            private final /* synthetic */ TonController.FeeCallback f$1;

            {
                this.f$1 = r2;
            }

            public final void run(Object obj) {
                TonController.this.lambda$null$33$TonController(this.f$1, obj);
            }
        });
    }

    public /* synthetic */ void lambda$null$33$TonController(FeeCallback feeCallback, Object obj) {
        if (obj instanceof TonApi.QueryInfo) {
            sendRequest((TonApi.Function) new TonApi.QueryEstimateFees(((TonApi.QueryInfo) obj).id, true), (TonLibCallback) new TonLibCallback() {
                public final void run(Object obj) {
                    TonController.lambda$null$31(TonController.FeeCallback.this, obj);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TonController.FeeCallback.this.run(0);
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$31(FeeCallback feeCallback, Object obj) {
        if (obj instanceof TonApi.QueryFees) {
            TonApi.Fees fees = ((TonApi.QueryFees) obj).sourceFees;
            AndroidUtilities.runOnUIThread(new Runnable(fees.fwdFee + fees.gasFee + fees.inFwdFee + fees.storageFee) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TonController.FeeCallback.this.run(this.f$1);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TonController.FeeCallback.this.run(0);
            }
        });
    }

    public void sendGrams(String str, Cipher cipher2, TonApi.InputKey inputKey, String str2, String str3, long j, String str4, Runnable runnable, Runnable runnable2, Runnable runnable3, DangerousCallback dangerousCallback, ErrorCallback errorCallback) {
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        $$Lambda$TonController$pCFvgPRO7LoJVVjLo13UdJcy_fw r0 = r1;
        $$Lambda$TonController$pCFvgPRO7LoJVVjLo13UdJcy_fw r1 = new Runnable(inputKey, str, cipher2, runnable, errorCallback, str2, str3, j, str4, runnable3, runnable2, dangerousCallback) {
            private final /* synthetic */ TonApi.InputKey f$1;
            private final /* synthetic */ Runnable f$10;
            private final /* synthetic */ Runnable f$11;
            private final /* synthetic */ TonController.DangerousCallback f$12;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ Cipher f$3;
            private final /* synthetic */ Runnable f$4;
            private final /* synthetic */ TonController.ErrorCallback f$5;
            private final /* synthetic */ String f$6;
            private final /* synthetic */ String f$7;
            private final /* synthetic */ long f$8;
            private final /* synthetic */ String f$9;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
                this.f$9 = r11;
                this.f$10 = r12;
                this.f$11 = r13;
                this.f$12 = r14;
            }

            public final void run() {
                TonController.this.lambda$sendGrams$40$TonController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
            }
        };
        dispatchQueue.postRunnable(r0);
    }

    public /* synthetic */ void lambda$sendGrams$40$TonController(TonApi.InputKey inputKey, String str, Cipher cipher2, Runnable runnable, ErrorCallback errorCallback, String str2, String str3, long j, String str4, Runnable runnable2, Runnable runnable3, DangerousCallback dangerousCallback) {
        TonApi.InputKey decryptTonData = inputKey == null ? decryptTonData(str, cipher2, runnable, errorCallback, false) : inputKey;
        if (decryptTonData != null) {
            long j2 = j;
            sendRequest((TonApi.Function) new TonApi.GenericCreateSendGramsQuery(decryptTonData, new TonApi.AccountAddress(str2), new TonApi.AccountAddress(str3), j2, 0, true, str4 != null ? str4.getBytes() : new byte[0]), (TonLibCallback) new TonLibCallback(str2, str3, j2, str4, runnable2, runnable3, errorCallback, dangerousCallback, decryptTonData) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ String f$4;
                private final /* synthetic */ Runnable f$5;
                private final /* synthetic */ Runnable f$6;
                private final /* synthetic */ TonController.ErrorCallback f$7;
                private final /* synthetic */ TonController.DangerousCallback f$8;
                private final /* synthetic */ TonApi.InputKey f$9;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                    this.f$7 = r9;
                    this.f$8 = r10;
                    this.f$9 = r11;
                }

                public final void run(Object obj) {
                    TonController.this.lambda$null$39$TonController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$39$TonController(String str, String str2, long j, String str3, Runnable runnable, Runnable runnable2, ErrorCallback errorCallback, DangerousCallback dangerousCallback, TonApi.InputKey inputKey, Object obj) {
        Object obj2 = obj;
        if (obj2 instanceof TonApi.QueryInfo) {
            TonApi.QueryInfo queryInfo = (TonApi.QueryInfo) obj2;
            sendRequest((TonApi.Function) new TonApi.QuerySend(queryInfo.id), (TonLibCallback) new TonLibCallback(str, str2, j, str3, queryInfo, runnable, runnable2, errorCallback) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ String f$4;
                private final /* synthetic */ TonApi.QueryInfo f$5;
                private final /* synthetic */ Runnable f$6;
                private final /* synthetic */ Runnable f$7;
                private final /* synthetic */ TonController.ErrorCallback f$8;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                    this.f$7 = r9;
                    this.f$8 = r10;
                }

                public final void run(Object obj) {
                    TonController.this.lambda$null$37$TonController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, obj);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(obj, dangerousCallback, inputKey, errorCallback) {
            private final /* synthetic */ Object f$1;
            private final /* synthetic */ TonController.DangerousCallback f$2;
            private final /* synthetic */ TonApi.InputKey f$3;
            private final /* synthetic */ TonController.ErrorCallback f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                TonController.this.lambda$null$38$TonController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$37$TonController(String str, String str2, long j, String str3, TonApi.QueryInfo queryInfo, Runnable runnable, Runnable runnable2, ErrorCallback errorCallback, Object obj) {
        Object obj2 = obj;
        if (obj2 instanceof TonApi.Ok) {
            AndroidUtilities.runOnUIThread(new Runnable(str, str2, j, str3, queryInfo, runnable, runnable2) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ String f$4;
                private final /* synthetic */ TonApi.QueryInfo f$5;
                private final /* synthetic */ Runnable f$6;
                private final /* synthetic */ Runnable f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                    this.f$7 = r9;
                }

                public final void run() {
                    TonController.this.lambda$null$35$TonController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(errorCallback, obj2) {
            private final /* synthetic */ TonController.ErrorCallback f$1;
            private final /* synthetic */ Object f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TonController.this.lambda$null$36$TonController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$35$TonController(String str, String str2, long j, String str3, TonApi.QueryInfo queryInfo, Runnable runnable, Runnable runnable2) {
        TonApi.QueryInfo queryInfo2 = queryInfo;
        TonApi.RawMessage rawMessage = new TonApi.RawMessage();
        rawMessage.source = str;
        rawMessage.destination = str2;
        rawMessage.value = -j;
        rawMessage.message = str3 != null ? str3.getBytes() : new byte[0];
        rawMessage.bodyHash = queryInfo2.bodyHash;
        this.pendingTransactions.add(0, new TonApi.RawTransaction(System.currentTimeMillis() / 1000, new byte[0], new TonApi.InternalTransactionId(), 0, 0, queryInfo2.validUntil, rawMessage, new TonApi.RawMessage[0]));
        saveCache();
        getNotificationCenter().postNotificationName(NotificationCenter.walletPendingTransactionsChanged, new Object[0]);
        this.onPendingTransactionsEmpty = runnable;
        runnable2.run();
    }

    public /* synthetic */ void lambda$null$36$TonController(ErrorCallback errorCallback, Object obj) {
        errorCallback.run("TONLIB_FAIL", getTonApiErrorSafe(obj));
    }

    public /* synthetic */ void lambda$null$38$TonController(Object obj, DangerousCallback dangerousCallback, TonApi.InputKey inputKey, ErrorCallback errorCallback) {
        TonApi.Error tonApiErrorSafe = getTonApiErrorSafe(obj);
        if (tonApiErrorSafe == null || !tonApiErrorSafe.message.startsWith("DANGEROUS_TRANSACTION")) {
            errorCallback.run("TONLIB_FAIL", tonApiErrorSafe);
        } else {
            dangerousCallback.run(inputKey);
        }
    }

    /* access modifiers changed from: private */
    public void runShortPolling() {
        if (!this.shortPollingInProgress && !this.pendingTransactions.isEmpty()) {
            this.shortPollingInProgress = true;
            getAccountState(new AccountStateCallback(this.cachedAccountState) {
                private final /* synthetic */ TonApi.GenericAccountState f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TonApi.GenericAccountState genericAccountState) {
                    TonController.this.lambda$runShortPolling$42$TonController(this.f$1, genericAccountState);
                }
            });
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0018, code lost:
        if (r8.lt == r2.lt) goto L_0x001c;
     */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x001f  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x002c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$runShortPolling$42$TonController(drinkless.org.ton.TonApi.GenericAccountState r8, drinkless.org.ton.TonApi.GenericAccountState r9) {
        /*
            r7 = this;
            r0 = 1
            r1 = 0
            if (r9 == 0) goto L_0x001c
            if (r8 == 0) goto L_0x001a
            drinkless.org.ton.TonApi$InternalTransactionId r8 = getLastTransactionId(r8)
            drinkless.org.ton.TonApi$InternalTransactionId r2 = getLastTransactionId(r9)
            if (r8 == 0) goto L_0x001a
            if (r2 == 0) goto L_0x001a
            long r3 = r8.lt
            long r5 = r2.lt
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 == 0) goto L_0x001c
        L_0x001a:
            r8 = 1
            goto L_0x001d
        L_0x001c:
            r8 = 0
        L_0x001d:
            if (r8 == 0) goto L_0x002c
            drinkless.org.ton.TonApi$InternalTransactionId r8 = getLastTransactionId(r9)
            org.telegram.messenger.-$$Lambda$TonController$1e_N16tO-4nWvlG6WbYTOXkO55E r1 = new org.telegram.messenger.-$$Lambda$TonController$1e_N16tO-4nWvlG6WbYTOXkO55E
            r1.<init>(r9)
            r7.getTransactions(r0, r8, r1)
            goto L_0x003f
        L_0x002c:
            r8 = 0
            r7.shortPollRunnable = r8
            r7.shortPollingInProgress = r1
            r7.checkPendingTransactionsForFailure(r9)
            java.util.ArrayList<drinkless.org.ton.TonApi$RawTransaction> r8 = r7.pendingTransactions
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x003f
            r7.scheduleShortPoll()
        L_0x003f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.TonController.lambda$runShortPolling$42$TonController(drinkless.org.ton.TonApi$GenericAccountState, drinkless.org.ton.TonApi$GenericAccountState):void");
    }

    public /* synthetic */ void lambda$null$41$TonController(TonApi.GenericAccountState genericAccountState) {
        this.shortPollRunnable = null;
        this.shortPollingInProgress = false;
        checkPendingTransactionsForFailure(genericAccountState);
        if (!this.pendingTransactions.isEmpty()) {
            scheduleShortPoll();
        }
    }

    public void scheduleShortPoll() {
        if (this.shortPollRunnable == null) {
            $$Lambda$TonController$y5gHwqWFihVH4vITInmi9Ihzbk r0 = new Runnable() {
                public final void run() {
                    TonController.this.runShortPolling();
                }
            };
            this.shortPollRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 3000);
        }
    }

    public void cancelShortPoll() {
        Runnable runnable = this.shortPollRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.shortPollRunnable = null;
        }
        this.onPendingTransactionsEmpty = null;
        this.shortPollingInProgress = false;
    }

    public boolean hasPendingTransactions() {
        return !this.pendingTransactions.isEmpty();
    }

    public static CharSequence formatCurrency(long j) {
        if (j == 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder(String.format(Locale.US, "%s%d.%09d", new Object[]{j < 0 ? "-" : "", Long.valueOf(Math.abs(j / NUM)), Long.valueOf(Math.abs(j % NUM))}));
        while (sb.length() > 1 && sb.charAt(sb.length() - 1) == '0' && sb.charAt(sb.length() - 2) != '.') {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb;
    }

    private void loadTonConfigFromUrl() {
        UserConfig userConfig = getUserConfig();
        if (userConfig.walletConfigType == 0) {
            WalletConfigLoader.loadConfig(userConfig.walletConfigUrl, new StringCallback(userConfig) {
                private final /* synthetic */ UserConfig f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(String str) {
                    TonController.this.lambda$loadTonConfigFromUrl$43$TonController(this.f$1, str);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadTonConfigFromUrl$43$TonController(UserConfig userConfig, String str) {
        if (!TextUtils.isEmpty(str) && !TextUtils.equals(userConfig.walletConfigFromUrl, str)) {
            userConfig.walletConfigFromUrl = str;
            userConfig.saveConfig(false);
            onTonConfigUpdated();
        }
    }
}
