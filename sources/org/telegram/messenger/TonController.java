package org.telegram.messenger;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec.Builder;
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
import drinkless.org.ton.TonApi.AccountAddress;
import drinkless.org.ton.TonApi.Bip39Hints;
import drinkless.org.ton.TonApi.Config;
import drinkless.org.ton.TonApi.CreateNewKey;
import drinkless.org.ton.TonApi.DeleteAllKeys;
import drinkless.org.ton.TonApi.Error;
import drinkless.org.ton.TonApi.ExportKey;
import drinkless.org.ton.TonApi.ExportedKey;
import drinkless.org.ton.TonApi.Fees;
import drinkless.org.ton.TonApi.Function;
import drinkless.org.ton.TonApi.GenericAccountState;
import drinkless.org.ton.TonApi.GenericAccountStateRaw;
import drinkless.org.ton.TonApi.GenericAccountStateTestGiver;
import drinkless.org.ton.TonApi.GenericAccountStateTestWallet;
import drinkless.org.ton.TonApi.GenericAccountStateUninited;
import drinkless.org.ton.TonApi.GenericAccountStateWallet;
import drinkless.org.ton.TonApi.GenericAccountStateWalletV3;
import drinkless.org.ton.TonApi.GenericCreateSendGramsQuery;
import drinkless.org.ton.TonApi.GenericGetAccountState;
import drinkless.org.ton.TonApi.GetBip39Hints;
import drinkless.org.ton.TonApi.ImportKey;
import drinkless.org.ton.TonApi.Init;
import drinkless.org.ton.TonApi.InputKey;
import drinkless.org.ton.TonApi.InputKeyFake;
import drinkless.org.ton.TonApi.InputKeyRegular;
import drinkless.org.ton.TonApi.InternalTransactionId;
import drinkless.org.ton.TonApi.KeyStoreTypeDirectory;
import drinkless.org.ton.TonApi.Object;
import drinkless.org.ton.TonApi.Ok;
import drinkless.org.ton.TonApi.Options;
import drinkless.org.ton.TonApi.OptionsConfigInfo;
import drinkless.org.ton.TonApi.OptionsSetConfig;
import drinkless.org.ton.TonApi.OptionsValidateConfig;
import drinkless.org.ton.TonApi.QueryEstimateFees;
import drinkless.org.ton.TonApi.QueryFees;
import drinkless.org.ton.TonApi.QueryInfo;
import drinkless.org.ton.TonApi.QuerySend;
import drinkless.org.ton.TonApi.RawAccountState;
import drinkless.org.ton.TonApi.RawGetTransactions;
import drinkless.org.ton.TonApi.RawMessage;
import drinkless.org.ton.TonApi.RawTransaction;
import drinkless.org.ton.TonApi.RawTransactions;
import drinkless.org.ton.TonApi.TestGiverAccountState;
import drinkless.org.ton.TonApi.TestWalletAccountState;
import drinkless.org.ton.TonApi.UninitedAccountState;
import drinkless.org.ton.TonApi.UnpackAccountAddress;
import drinkless.org.ton.TonApi.UnpackedAccountAddress;
import drinkless.org.ton.TonApi.WalletAccountState;
import drinkless.org.ton.TonApi.WalletV3AccountState;
import drinkless.org.ton.TonApi.WalletV3GetAccountAddress;
import drinkless.org.ton.TonApi.WalletV3InitialAccountState;
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
import javax.crypto.spec.PSource.PSpecified;
import javax.security.auth.x500.X500Principal;
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
    private AccountAddress accountAddress;
    private GenericAccountState cachedAccountState;
    private ArrayList<RawTransaction> cachedTransactions = new ArrayList();
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
    private ArrayList<RawTransaction> pendingTransactions = new ArrayList();
    private Runnable shortPollRunnable;
    private boolean shortPollingInProgress;
    private int syncProgress;
    private SharedPreferences tonCache;
    private GetTransactionsCallback uiTransactionCallback;
    private long walletId;
    private boolean walletLoaded;

    public interface AccountStateCallback {
        void run(GenericAccountState genericAccountState);
    }

    public interface BooleanCallback {
        void run(boolean z);
    }

    public interface BytesCallback {
        void run(byte[] bArr);
    }

    public interface DangerousCallback {
        void run(InputKey inputKey);
    }

    public interface ErrorCallback {
        void run(String str, Error error);
    }

    public interface FeeCallback {
        void run(long j);
    }

    public interface GetTransactionsCallback {
        void run(boolean z, ArrayList<RawTransaction> arrayList);
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
        String str = "AndroidKeyStore";
        try {
            keyStore = KeyStore.getInstance(str);
            keyStore.load(null);
            String str2 = "RSA";
            if (VERSION.SDK_INT >= 23) {
                keyGenerator = KeyPairGenerator.getInstance(str2, str);
                cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
                return;
            }
            keyGenerator = KeyPairGenerator.getInstance(str2, str);
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (Exception e) {
            FileLog.e(e);
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("tonCache");
        stringBuilder.append(i);
        this.tonCache = context.getSharedPreferences(stringBuilder.toString(), 0);
        loadCache();
        loadTonConfigFromUrl();
    }

    private void loadCache() {
        String str = "outMsgCount";
        String str2 = null;
        try {
            if (this.tonCache.getBoolean("hasCache", false)) {
                String str3;
                String str4;
                String str5;
                String str6;
                String str7;
                String str8;
                String str9;
                StringBuilder stringBuilder;
                String string;
                String string2;
                long j;
                byte[] decode;
                this.walletLoaded = true;
                int i = this.tonCache.getInt("state.type", 0);
                long j2 = this.tonCache.getLong("state.balance", 0);
                int i2 = this.tonCache.getInt("state.seqno", 0);
                long j3 = this.tonCache.getLong("state.walletId", 0);
                int i3 = i;
                InternalTransactionId internalTransactionId = new InternalTransactionId(this.tonCache.getLong("transaction.lt", 0), Base64.decode(this.tonCache.getString("transaction.hash", null), 0));
                long j4 = this.tonCache.getLong("syncUtime", 0);
                if (i3 == 0) {
                    RawAccountState rawAccountState = r10;
                    RawAccountState rawAccountState2 = new RawAccountState(j2, null, null, internalTransactionId, null, j4);
                    this.cachedAccountState = new GenericAccountStateRaw(rawAccountState);
                } else {
                    int i4 = i3;
                    if (i4 == 1) {
                        this.cachedAccountState = new GenericAccountStateTestWallet(new TestWalletAccountState(j2, i2, internalTransactionId, j4));
                    } else if (i4 == 2) {
                        this.cachedAccountState = new GenericAccountStateTestGiver(new TestGiverAccountState(j2, i2, internalTransactionId, j4));
                    } else if (i4 == 3) {
                        this.cachedAccountState = new GenericAccountStateUninited(new UninitedAccountState(j2, internalTransactionId, null, j4));
                    } else if (i4 == 4) {
                        this.cachedAccountState = new GenericAccountStateWallet(new WalletAccountState(j2, i2, internalTransactionId, j4));
                    } else if (i4 == 5) {
                        this.cachedAccountState = new GenericAccountStateWalletV3(new WalletV3AccountState(j2, j3, i2, internalTransactionId, j4));
                    }
                }
                int i5 = this.tonCache.getInt("transactionsCount", 0);
                i = 0;
                while (true) {
                    str3 = "inMsg.message";
                    str4 = "inMsg.bodyHash";
                    str5 = "inMsg.value";
                    str6 = "utime";
                    str7 = "inMsg.destination";
                    str8 = "inMsg.source";
                    str9 = ".";
                    if (i >= i5) {
                        break;
                    }
                    SharedPreferences sharedPreferences;
                    StringBuilder stringBuilder2;
                    StringBuilder stringBuilder3;
                    StringBuilder stringBuilder4;
                    long j5;
                    long j6;
                    long j7;
                    StringBuilder stringBuilder5;
                    RawMessage rawMessage;
                    RawMessage[] rawMessageArr;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("transaction");
                    stringBuilder.append(i);
                    stringBuilder.append(str9);
                    String stringBuilder6 = stringBuilder.toString();
                    SharedPreferences sharedPreferences2 = this.tonCache;
                    StringBuilder stringBuilder7 = new StringBuilder();
                    stringBuilder7.append(stringBuilder6);
                    stringBuilder7.append(str8);
                    if (sharedPreferences2.contains(stringBuilder7.toString())) {
                        SharedPreferences sharedPreferences3 = this.tonCache;
                        StringBuilder stringBuilder8 = new StringBuilder();
                        stringBuilder8.append(stringBuilder6);
                        stringBuilder8.append(str8);
                        string = sharedPreferences3.getString(stringBuilder8.toString(), null);
                        sharedPreferences = this.tonCache;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(stringBuilder6);
                        stringBuilder2.append(str7);
                        string2 = sharedPreferences.getString(stringBuilder2.toString(), null);
                        sharedPreferences = this.tonCache;
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(stringBuilder6);
                        stringBuilder3.append(str5);
                        j = sharedPreferences.getLong(stringBuilder3.toString(), 0);
                        sharedPreferences = this.tonCache;
                        stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(stringBuilder6);
                        stringBuilder4.append("inMsg.fwdFee");
                        j5 = sharedPreferences.getLong(stringBuilder4.toString(), 0);
                        sharedPreferences = this.tonCache;
                        stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(stringBuilder6);
                        stringBuilder4.append("inMsg.ihrFee");
                        j6 = sharedPreferences.getLong(stringBuilder4.toString(), 0);
                        sharedPreferences = this.tonCache;
                        stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(stringBuilder6);
                        stringBuilder4.append("inMsg.createdLt");
                        j7 = sharedPreferences.getLong(stringBuilder4.toString(), 0);
                        sharedPreferences = this.tonCache;
                        stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(stringBuilder6);
                        stringBuilder4.append(str4);
                        decode = Base64.decode(sharedPreferences.getString(stringBuilder4.toString(), null), 0);
                        sharedPreferences = this.tonCache;
                        stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(stringBuilder6);
                        stringBuilder5.append(str3);
                        rawMessage = new RawMessage(string, string2, j, j5, j6, j7, decode, Base64.decode(sharedPreferences.getString(stringBuilder5.toString(), null), 0));
                    } else {
                        rawMessage = null;
                    }
                    sharedPreferences = this.tonCache;
                    StringBuilder stringBuilder9 = new StringBuilder();
                    stringBuilder9.append(stringBuilder6);
                    stringBuilder9.append(str);
                    if (sharedPreferences.contains(stringBuilder9.toString())) {
                        sharedPreferences = this.tonCache;
                        stringBuilder9 = new StringBuilder();
                        stringBuilder9.append(stringBuilder6);
                        stringBuilder9.append(str);
                        RawMessage[] rawMessageArr2 = new RawMessage[sharedPreferences.getInt(stringBuilder9.toString(), 0)];
                        for (int i6 = 0; i6 < rawMessageArr2.length; i6++) {
                            stringBuilder5 = new StringBuilder();
                            stringBuilder5.append(stringBuilder6);
                            stringBuilder5.append("outMsg");
                            stringBuilder5.append(i6);
                            stringBuilder5.append(str9);
                            str4 = stringBuilder5.toString();
                            SharedPreferences sharedPreferences4 = this.tonCache;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(str4);
                            stringBuilder2.append("source");
                            string = sharedPreferences4.getString(stringBuilder2.toString(), null);
                            sharedPreferences4 = this.tonCache;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(str4);
                            stringBuilder2.append("destination");
                            string2 = sharedPreferences4.getString(stringBuilder2.toString(), null);
                            sharedPreferences4 = this.tonCache;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(str4);
                            stringBuilder2.append("value");
                            j = sharedPreferences4.getLong(stringBuilder2.toString(), 0);
                            sharedPreferences4 = this.tonCache;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(str4);
                            stringBuilder2.append("fwdFee");
                            j5 = sharedPreferences4.getLong(stringBuilder2.toString(), 0);
                            sharedPreferences4 = this.tonCache;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(str4);
                            stringBuilder2.append("ihrFee");
                            j6 = sharedPreferences4.getLong(stringBuilder2.toString(), 0);
                            sharedPreferences4 = this.tonCache;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(str4);
                            stringBuilder2.append("createdLt");
                            j7 = sharedPreferences4.getLong(stringBuilder2.toString(), 0);
                            sharedPreferences4 = this.tonCache;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(str4);
                            stringBuilder2.append("bodyHash");
                            decode = Base64.decode(sharedPreferences4.getString(stringBuilder2.toString(), null), 0);
                            sharedPreferences4 = this.tonCache;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(str4);
                            stringBuilder2.append("message");
                            rawMessageArr2[i6] = new RawMessage(string, string2, j, j5, j6, j7, decode, Base64.decode(sharedPreferences4.getString(stringBuilder2.toString(), null), 0));
                        }
                        rawMessageArr = rawMessageArr2;
                    } else {
                        rawMessageArr = null;
                    }
                    SharedPreferences sharedPreferences5 = this.tonCache;
                    stringBuilder5 = new StringBuilder();
                    stringBuilder5.append(stringBuilder6);
                    stringBuilder5.append(str6);
                    long j8 = sharedPreferences5.getLong(stringBuilder5.toString(), 0);
                    sharedPreferences5 = this.tonCache;
                    stringBuilder5 = new StringBuilder();
                    stringBuilder5.append(stringBuilder6);
                    stringBuilder5.append("data");
                    byte[] decode2 = Base64.decode(sharedPreferences5.getString(stringBuilder5.toString(), null), 0);
                    SharedPreferences sharedPreferences6 = this.tonCache;
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(stringBuilder6);
                    stringBuilder4.append("lt");
                    long j9 = sharedPreferences6.getLong(stringBuilder4.toString(), 0);
                    SharedPreferences sharedPreferences7 = this.tonCache;
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder6);
                    stringBuilder3.append("hash");
                    internalTransactionId = new InternalTransactionId(j9, Base64.decode(sharedPreferences7.getString(stringBuilder3.toString(), null), 0));
                    sharedPreferences6 = this.tonCache;
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(stringBuilder6);
                    stringBuilder4.append("fee");
                    long j10 = sharedPreferences6.getLong(stringBuilder4.toString(), 0);
                    sharedPreferences6 = this.tonCache;
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(stringBuilder6);
                    stringBuilder4.append("storageFee");
                    long j11 = sharedPreferences6.getLong(stringBuilder4.toString(), 0);
                    sharedPreferences6 = this.tonCache;
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(stringBuilder6);
                    stringBuilder4.append("otherFee");
                    this.cachedTransactions.add(new RawTransaction(j8, decode2, internalTransactionId, j10, j11, sharedPreferences6.getLong(stringBuilder4.toString(), 0), rawMessage, rawMessageArr));
                    i++;
                }
                int i7 = this.tonCache.getInt("pendingCount", 0);
                i5 = 0;
                while (i5 < i7) {
                    StringBuilder stringBuilder10 = new StringBuilder();
                    stringBuilder10.append("pending");
                    stringBuilder10.append(i5);
                    stringBuilder10.append(str9);
                    String stringBuilder11 = stringBuilder10.toString();
                    SharedPreferences sharedPreferences8 = this.tonCache;
                    StringBuilder stringBuilder12 = new StringBuilder();
                    stringBuilder12.append(stringBuilder11);
                    stringBuilder12.append(str8);
                    string = sharedPreferences8.getString(stringBuilder12.toString(), str2);
                    sharedPreferences8 = this.tonCache;
                    stringBuilder12 = new StringBuilder();
                    stringBuilder12.append(stringBuilder11);
                    stringBuilder12.append(str7);
                    string2 = sharedPreferences8.getString(stringBuilder12.toString(), str2);
                    sharedPreferences8 = this.tonCache;
                    stringBuilder12 = new StringBuilder();
                    stringBuilder12.append(stringBuilder11);
                    stringBuilder12.append(str5);
                    i3 = i5;
                    j = sharedPreferences8.getLong(stringBuilder12.toString(), 0);
                    SharedPreferences sharedPreferences9 = this.tonCache;
                    StringBuilder stringBuilder13 = new StringBuilder();
                    stringBuilder13.append(stringBuilder11);
                    stringBuilder13.append(str4);
                    decode = Base64.decode(sharedPreferences9.getString(stringBuilder13.toString(), null), 0);
                    sharedPreferences9 = this.tonCache;
                    stringBuilder13 = new StringBuilder();
                    stringBuilder13.append(stringBuilder11);
                    stringBuilder13.append(str3);
                    RawMessage rawMessage2 = new RawMessage(string, string2, j, 0, 0, 0, decode, Base64.decode(sharedPreferences9.getString(stringBuilder13.toString(), null), 0));
                    SharedPreferences sharedPreferences10 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder11);
                    stringBuilder.append(str6);
                    this.pendingTransactions.add(new RawTransaction(sharedPreferences10.getLong(stringBuilder.toString(), 0), new byte[0], new InternalTransactionId(), 0, 0, 0, rawMessage2, new RawMessage[0]));
                    i5 = i3 + 1;
                    str2 = null;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            this.cachedAccountState = null;
            this.cachedTransactions.clear();
        }
    }

    private void saveCache() {
        TonController tonController = this;
        if (tonController.cachedAccountState != null) {
            try {
                long j;
                int i;
                InternalTransactionId internalTransactionId;
                long j2;
                int i2;
                int i3;
                String str;
                String str2;
                String str3;
                String str4;
                String str5;
                String str6;
                String str7;
                StringBuilder stringBuilder;
                StringBuilder stringBuilder2;
                Editor edit = tonController.tonCache.edit();
                edit.clear();
                long j3 = 0;
                long j4;
                InternalTransactionId internalTransactionId2;
                InternalTransactionId internalTransactionId3;
                if (tonController.cachedAccountState instanceof GenericAccountStateRaw) {
                    GenericAccountStateRaw genericAccountStateRaw = (GenericAccountStateRaw) tonController.cachedAccountState;
                    j4 = genericAccountStateRaw.accountState.balance;
                    internalTransactionId2 = genericAccountStateRaw.accountState.lastTransactionId;
                    j = genericAccountStateRaw.accountState.syncUtime;
                    i = 0;
                    internalTransactionId = internalTransactionId2;
                    j2 = j4;
                    i2 = 0;
                } else if (tonController.cachedAccountState instanceof GenericAccountStateTestWallet) {
                    GenericAccountStateTestWallet genericAccountStateTestWallet = (GenericAccountStateTestWallet) tonController.cachedAccountState;
                    j4 = genericAccountStateTestWallet.accountState.balance;
                    int i4 = genericAccountStateTestWallet.accountState.seqno;
                    internalTransactionId3 = genericAccountStateTestWallet.accountState.lastTransactionId;
                    j = genericAccountStateTestWallet.accountState.syncUtime;
                    internalTransactionId = internalTransactionId3;
                    i = i4;
                    j2 = j4;
                    i2 = 1;
                } else if (tonController.cachedAccountState instanceof GenericAccountStateTestGiver) {
                    GenericAccountStateTestGiver genericAccountStateTestGiver = (GenericAccountStateTestGiver) tonController.cachedAccountState;
                    i2 = 2;
                    j2 = genericAccountStateTestGiver.accountState.balance;
                    i = genericAccountStateTestGiver.accountState.seqno;
                    internalTransactionId = genericAccountStateTestGiver.accountState.lastTransactionId;
                    j = genericAccountStateTestGiver.accountState.syncUtime;
                } else if (tonController.cachedAccountState instanceof GenericAccountStateUninited) {
                    GenericAccountStateUninited genericAccountStateUninited = (GenericAccountStateUninited) tonController.cachedAccountState;
                    i2 = 3;
                    j2 = genericAccountStateUninited.accountState.balance;
                    internalTransactionId3 = genericAccountStateUninited.accountState.lastTransactionId;
                    j = genericAccountStateUninited.accountState.syncUtime;
                    internalTransactionId = internalTransactionId3;
                    i = 0;
                } else if (tonController.cachedAccountState instanceof GenericAccountStateWallet) {
                    GenericAccountStateWallet genericAccountStateWallet = (GenericAccountStateWallet) tonController.cachedAccountState;
                    i2 = 4;
                    j2 = genericAccountStateWallet.accountState.balance;
                    i = genericAccountStateWallet.accountState.seqno;
                    internalTransactionId = genericAccountStateWallet.accountState.lastTransactionId;
                    j = genericAccountStateWallet.accountState.syncUtime;
                } else if (tonController.cachedAccountState instanceof GenericAccountStateWalletV3) {
                    GenericAccountStateWalletV3 genericAccountStateWalletV3 = (GenericAccountStateWalletV3) tonController.cachedAccountState;
                    j4 = genericAccountStateWalletV3.accountState.balance;
                    i3 = genericAccountStateWalletV3.accountState.seqno;
                    internalTransactionId2 = genericAccountStateWalletV3.accountState.lastTransactionId;
                    long j5 = genericAccountStateWalletV3.accountState.syncUtime;
                    i = i3;
                    internalTransactionId = internalTransactionId2;
                    j3 = genericAccountStateWalletV3.accountState.walletId;
                    j2 = j4;
                    j = j5;
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
                int i5 = 0;
                while (true) {
                    str = "inMsg.message";
                    str2 = "inMsg.bodyHash";
                    str3 = "inMsg.value";
                    str4 = "inMsg.destination";
                    str5 = "utime";
                    str6 = "inMsg.source";
                    str7 = ".";
                    if (i5 >= min) {
                        break;
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("transaction");
                    stringBuilder.append(i5);
                    stringBuilder.append(str7);
                    String stringBuilder3 = stringBuilder.toString();
                    RawTransaction rawTransaction = (RawTransaction) tonController.cachedTransactions.get(i5);
                    if (rawTransaction.inMsg != null) {
                        StringBuilder stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(stringBuilder3);
                        stringBuilder4.append(str6);
                        edit.putString(stringBuilder4.toString(), rawTransaction.inMsg.source);
                        StringBuilder stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(stringBuilder3);
                        stringBuilder5.append(str4);
                        edit.putString(stringBuilder5.toString(), rawTransaction.inMsg.destination);
                        StringBuilder stringBuilder6 = new StringBuilder();
                        stringBuilder6.append(stringBuilder3);
                        stringBuilder6.append(str3);
                        edit.putLong(stringBuilder6.toString(), rawTransaction.inMsg.value);
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(stringBuilder3);
                        stringBuilder2.append("inMsg.fwdFee");
                        edit.putLong(stringBuilder2.toString(), rawTransaction.inMsg.fwdFee);
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(stringBuilder3);
                        stringBuilder2.append("inMsg.ihrFee");
                        edit.putLong(stringBuilder2.toString(), rawTransaction.inMsg.ihrFee);
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(stringBuilder3);
                        stringBuilder2.append("inMsg.createdLt");
                        edit.putLong(stringBuilder2.toString(), rawTransaction.inMsg.createdLt);
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(stringBuilder3);
                        stringBuilder2.append(str2);
                        edit.putString(stringBuilder2.toString(), Base64.encodeToString(rawTransaction.inMsg.bodyHash, 0));
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(stringBuilder3);
                        stringBuilder2.append(str);
                        edit.putString(stringBuilder2.toString(), Base64.encodeToString(rawTransaction.inMsg.message, 0));
                    }
                    if (rawTransaction.outMsgs != null) {
                        i3 = rawTransaction.outMsgs.length;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(stringBuilder3);
                        stringBuilder2.append("outMsgCount");
                        edit.putInt(stringBuilder2.toString(), i3);
                        for (int i6 = 0; i6 < i3; i6++) {
                            StringBuilder stringBuilder7 = new StringBuilder();
                            stringBuilder7.append(stringBuilder3);
                            stringBuilder7.append("outMsg");
                            stringBuilder7.append(i6);
                            stringBuilder7.append(str7);
                            String stringBuilder8 = stringBuilder7.toString();
                            StringBuilder stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(stringBuilder8);
                            stringBuilder9.append("source");
                            edit.putString(stringBuilder9.toString(), rawTransaction.outMsgs[i6].source);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(stringBuilder8);
                            stringBuilder9.append("destination");
                            edit.putString(stringBuilder9.toString(), rawTransaction.outMsgs[i6].destination);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(stringBuilder8);
                            stringBuilder9.append("value");
                            edit.putLong(stringBuilder9.toString(), rawTransaction.outMsgs[i6].value);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(stringBuilder8);
                            stringBuilder9.append("fwdFee");
                            edit.putLong(stringBuilder9.toString(), rawTransaction.outMsgs[i6].fwdFee);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(stringBuilder8);
                            stringBuilder9.append("ihrFee");
                            edit.putLong(stringBuilder9.toString(), rawTransaction.outMsgs[i6].ihrFee);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(stringBuilder8);
                            stringBuilder9.append("createdLt");
                            edit.putLong(stringBuilder9.toString(), rawTransaction.outMsgs[i6].createdLt);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(stringBuilder8);
                            stringBuilder9.append("bodyHash");
                            edit.putString(stringBuilder9.toString(), Base64.encodeToString(rawTransaction.outMsgs[i6].bodyHash, 0));
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(stringBuilder8);
                            stringBuilder9.append("message");
                            edit.putString(stringBuilder9.toString(), Base64.encodeToString(rawTransaction.outMsgs[i6].message, 0));
                        }
                    }
                    StringBuilder stringBuilder10 = new StringBuilder();
                    stringBuilder10.append(stringBuilder3);
                    stringBuilder10.append(str5);
                    edit.putLong(stringBuilder10.toString(), rawTransaction.utime);
                    stringBuilder10 = new StringBuilder();
                    stringBuilder10.append(stringBuilder3);
                    stringBuilder10.append("data");
                    edit.putString(stringBuilder10.toString(), Base64.encodeToString(rawTransaction.data, 0));
                    stringBuilder10 = new StringBuilder();
                    stringBuilder10.append(stringBuilder3);
                    stringBuilder10.append("lt");
                    edit.putLong(stringBuilder10.toString(), rawTransaction.transactionId.lt);
                    stringBuilder10 = new StringBuilder();
                    stringBuilder10.append(stringBuilder3);
                    stringBuilder10.append("hash");
                    edit.putString(stringBuilder10.toString(), Base64.encodeToString(rawTransaction.transactionId.hash, 0));
                    stringBuilder10 = new StringBuilder();
                    stringBuilder10.append(stringBuilder3);
                    stringBuilder10.append("fee");
                    edit.putLong(stringBuilder10.toString(), rawTransaction.fee);
                    stringBuilder10 = new StringBuilder();
                    stringBuilder10.append(stringBuilder3);
                    stringBuilder10.append("storageFee");
                    edit.putLong(stringBuilder10.toString(), rawTransaction.storageFee);
                    stringBuilder10 = new StringBuilder();
                    stringBuilder10.append(stringBuilder3);
                    stringBuilder10.append("otherFee");
                    edit.putLong(stringBuilder10.toString(), rawTransaction.otherFee);
                    i5++;
                }
                min = tonController.pendingTransactions.size();
                edit.putInt("pendingCount", min);
                i5 = 0;
                while (i5 < min) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("pending");
                    stringBuilder2.append(i5);
                    stringBuilder2.append(str7);
                    String stringBuilder11 = stringBuilder2.toString();
                    RawTransaction rawTransaction2 = (RawTransaction) tonController.pendingTransactions.get(i5);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder11);
                    stringBuilder.append(str6);
                    edit.putString(stringBuilder.toString(), rawTransaction2.inMsg.source);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder11);
                    stringBuilder.append(str4);
                    edit.putString(stringBuilder.toString(), rawTransaction2.inMsg.destination);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder11);
                    stringBuilder.append(str3);
                    int i7 = min;
                    edit.putLong(stringBuilder.toString(), rawTransaction2.inMsg.value);
                    StringBuilder stringBuilder12 = new StringBuilder();
                    stringBuilder12.append(stringBuilder11);
                    stringBuilder12.append(str2);
                    edit.putString(stringBuilder12.toString(), Base64.encodeToString(rawTransaction2.inMsg.bodyHash, 0));
                    stringBuilder12 = new StringBuilder();
                    stringBuilder12.append(stringBuilder11);
                    stringBuilder12.append(str);
                    edit.putString(stringBuilder12.toString(), Base64.encodeToString(rawTransaction2.inMsg.message, 0));
                    stringBuilder12 = new StringBuilder();
                    stringBuilder12.append(stringBuilder11);
                    stringBuilder12.append(str5);
                    edit.putLong(stringBuilder12.toString(), rawTransaction2.utime);
                    stringBuilder12 = new StringBuilder();
                    stringBuilder12.append(stringBuilder11);
                    stringBuilder12.append("validUntil");
                    edit.putLong(stringBuilder12.toString(), rawTransaction2.otherFee);
                    i5++;
                    tonController = this;
                    min = i7;
                }
                edit.putBoolean("hasCache", true);
                edit.commit();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void cleanup() {
        try {
            keyStore.deleteEntry(getUserConfig().tonKeyName);
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (this.keyDirectoty != null) {
            initTonLib();
            sendRequest(new DeleteAllKeys(), true);
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

    public ArrayList<RawTransaction> getCachedTransactions() {
        return this.cachedTransactions;
    }

    public ArrayList<RawTransaction> getPendingTransactions() {
        return this.pendingTransactions;
    }

    public GenericAccountState getCachedAccountState() {
        return this.cachedAccountState;
    }

    private boolean createKeyPair(boolean z) {
        if (VERSION.SDK_INT >= 23) {
            int i = 0;
            while (i < 2) {
                try {
                    Builder keySize = new Builder(getUserConfig().tonKeyName, 3).setDigests(new String[]{"SHA-1", "SHA-256"}).setEncryptionPaddings(new String[]{"OAEPPadding"}).setKeySize(2048);
                    if (i == 0 && VERSION.SDK_INT >= 28) {
                        keySize.setIsStrongBoxBacked(true);
                    }
                    if (((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).isDeviceSecure()) {
                        keySize.setUserAuthenticationRequired(true);
                        if (!z) {
                            keySize.setUserAuthenticationValidityDurationSeconds(15);
                        }
                        if (VERSION.SDK_INT >= 24) {
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
        if (VERSION.SDK_INT >= 23) {
            try {
                Key key = keyStore.getKey(getUserConfig().tonKeyName, null);
                KeyInfo keyInfo = (KeyInfo) KeyFactory.getInstance(key.getAlgorithm(), "AndroidKeyStore").getKeySpec(key, KeyInfo.class);
                if (keyInfo.isUserAuthenticationRequired()) {
                    return keyInfo.getUserAuthenticationValidityDurationSeconds() > 0 ? 1 : 2;
                }
            } catch (Exception unused) {
            }
        }
        return 0;
    }

    private int initCipher(int i) {
        String str = "MGF1";
        String str2 = "SHA-1";
        if (i == 1) {
            PublicKey publicKey = keyStore.getCertificate(getUserConfig().tonKeyName).getPublicKey();
            publicKey = KeyFactory.getInstance(publicKey.getAlgorithm()).generatePublic(new X509EncodedKeySpec(publicKey.getEncoded()));
            if (VERSION.SDK_INT >= 23) {
                cipher.init(i, publicKey, new OAEPParameterSpec(str2, str, MGF1ParameterSpec.SHA1, PSpecified.DEFAULT));
            } else {
                cipher.init(i, publicKey);
            }
        } else if (i != 2) {
            return 0;
        } else {
            try {
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(getUserConfig().tonKeyName, null);
                if (VERSION.SDK_INT >= 23) {
                    cipher.init(i, privateKey, new OAEPParameterSpec(str2, str, MGF1ParameterSpec.SHA1, PSpecified.DEFAULT));
                } else {
                    cipher.init(i, privateKey);
                }
            } catch (Exception e) {
                if (VERSION.SDK_INT >= 23) {
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
                FileLog.e(e);
                return 0;
            }
        }
        return 1;
    }

    private boolean isKeyCreated(boolean z) {
        boolean z2 = false;
        try {
            if (keyStore.containsAlias(getUserConfig().tonKeyName) || createKeyPair(z)) {
                z2 = true;
            }
            return z2;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    private String encrypt(byte[] bArr) {
        try {
            if (initCipher(1) == 1) {
                return Base64.encodeToString(cipher.doFinal(bArr), 2);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return null;
    }

    public byte[] decrypt(String str, Cipher cipher) {
        if (cipher == null) {
            try {
                initCipher(2);
                cipher = cipher;
            } catch (Exception e) {
                FileLog.e(e);
                return null;
            }
        }
        return cipher.doFinal(Base64.decode(str, 2));
    }

    public Cipher getCipherForDecrypt() {
        Cipher cipher = null;
        try {
            cipher.init(2, (PrivateKey) keyStore.getKey(getUserConfig().tonKeyName, cipher), new OAEPParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, PSpecified.DEFAULT));
            return cipher;
        } catch (Exception e) {
            FileLog.e(e);
            return cipher;
        }
    }

    private Object sendRequest(Function function, boolean z) {
        return sendRequest(function, null, z);
    }

    private void sendRequest(Function function, TonLibCallback tonLibCallback) {
        sendRequest(function, tonLibCallback, false);
    }

    private Object sendRequest(Function function, TonLibCallback tonLibCallback, boolean z) {
        return new Object();
    }

    private static /* synthetic */ void lambda$sendRequest$0(TonLibCallback tonLibCallback, Function function, Object[] objArr, CountDownLatch countDownLatch, Object object) {
        if (tonLibCallback != null) {
            tonLibCallback.run(object);
            return;
        }
        if (object instanceof Error) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("TonApi query ");
                stringBuilder.append(function);
                stringBuilder.append(" error ");
                stringBuilder.append(((Error) object).message);
                FileLog.e(stringBuilder.toString());
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
        Config config = getConfig();
        Object sendRequest = sendRequest(new OptionsValidateConfig(config), true);
        if (!(sendRequest instanceof OptionsConfigInfo)) {
            return false;
        }
        this.walletId = ((OptionsConfigInfo) sendRequest).defaultWalletId;
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ton");
        stringBuilder.append(this.currentAccount);
        this.keyDirectoty = new File(filesDirFixed, stringBuilder.toString());
        this.keyDirectoty.mkdirs();
        this.currentSetConfig = config.config;
        this.currentSetConfigName = config.blockchainName;
        boolean z = sendRequest(new Init(new Options(config, new KeyStoreTypeDirectory(this.keyDirectoty.getAbsolutePath()))), true) instanceof Ok;
        this.initied = z;
        return z;
    }

    private Config getConfig() {
        UserConfig userConfig = getUserConfig();
        if (userConfig.walletConfigType == 0) {
            return new Config(userConfig.walletConfigFromUrl, userConfig.walletBlockchainName, BuildVars.TON_WALLET_STANDALONE ^ 1, false);
        }
        return new Config(userConfig.walletConfig, userConfig.walletBlockchainName, BuildVars.TON_WALLET_STANDALONE ^ 1, false);
    }

    public boolean onTonConfigUpdated() {
        if (!this.initied) {
            return true;
        }
        Config config = getConfig();
        if (TextUtils.equals(this.currentSetConfig, config.config) && TextUtils.equals(this.currentSetConfigName, config.blockchainName)) {
            return true;
        }
        Object sendRequest = sendRequest(new OptionsValidateConfig(config), true);
        if (!(sendRequest instanceof OptionsConfigInfo)) {
            return false;
        }
        this.walletId = ((OptionsConfigInfo) sendRequest).defaultWalletId;
        this.currentSetConfig = config.config;
        this.currentSetConfigName = config.blockchainName;
        sendRequest(new OptionsSetConfig(config), false);
        return true;
    }

    private void onFinishWalletCreate(String[] strArr, WordsCallback wordsCallback, byte[] bArr, TonApi.Key key) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$uMjPBMNMREKI79Tm2gV7lkxaL0k(this, key, bArr, strArr, wordsCallback));
    }

    public /* synthetic */ void lambda$onFinishWalletCreate$1$TonController(TonApi.Key key, byte[] bArr, String[] strArr, WordsCallback wordsCallback) {
        preloadWallet(key.publicKey);
        int length = (bArr.length + 3) + key.secret.length;
        int i = length % 16;
        if (i != 0) {
            i = 16 - i;
            length += i;
        }
        byte[] bArr2 = new byte[length];
        boolean z = false;
        bArr2[0] = (byte) i;
        bArr2[1] = (byte) 111;
        bArr2[2] = (byte) 107;
        System.arraycopy(bArr, 0, bArr2, 3, bArr.length);
        byte[] bArr3 = key.secret;
        System.arraycopy(bArr3, 0, bArr2, bArr.length + 3, bArr3.length);
        if (i != 0) {
            byte[] bArr4 = new byte[i];
            Utilities.random.nextBytes(bArr4);
            System.arraycopy(bArr4, 0, bArr2, (bArr.length + 3) + key.secret.length, bArr4.length);
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

    private Error getTonApiErrorSafe(Object obj) {
        return obj instanceof Error ? (Error) obj : null;
    }

    public static InternalTransactionId getLastTransactionId(GenericAccountState genericAccountState) {
        if (genericAccountState instanceof GenericAccountStateRaw) {
            return ((GenericAccountStateRaw) genericAccountState).accountState.lastTransactionId;
        }
        if (genericAccountState instanceof GenericAccountStateTestWallet) {
            return ((GenericAccountStateTestWallet) genericAccountState).accountState.lastTransactionId;
        }
        if (genericAccountState instanceof GenericAccountStateTestGiver) {
            return ((GenericAccountStateTestGiver) genericAccountState).accountState.lastTransactionId;
        }
        if (genericAccountState instanceof GenericAccountStateUninited) {
            return ((GenericAccountStateUninited) genericAccountState).accountState.lastTransactionId;
        }
        if (genericAccountState instanceof GenericAccountStateWallet) {
            return ((GenericAccountStateWallet) genericAccountState).accountState.lastTransactionId;
        }
        return genericAccountState instanceof GenericAccountStateWalletV3 ? ((GenericAccountStateWalletV3) genericAccountState).accountState.lastTransactionId : null;
    }

    public static long getLastSyncTime(GenericAccountState genericAccountState) {
        if (genericAccountState instanceof GenericAccountStateRaw) {
            return ((GenericAccountStateRaw) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof GenericAccountStateTestWallet) {
            return ((GenericAccountStateTestWallet) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof GenericAccountStateTestGiver) {
            return ((GenericAccountStateTestGiver) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof GenericAccountStateUninited) {
            return ((GenericAccountStateUninited) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof GenericAccountStateWallet) {
            return ((GenericAccountStateWallet) genericAccountState).accountState.syncUtime;
        }
        return genericAccountState instanceof GenericAccountStateWalletV3 ? ((GenericAccountStateWalletV3) genericAccountState).accountState.syncUtime : 0;
    }

    public static long getBalance(GenericAccountState genericAccountState) {
        long j = genericAccountState instanceof GenericAccountStateRaw ? ((GenericAccountStateRaw) genericAccountState).accountState.balance : genericAccountState instanceof GenericAccountStateTestWallet ? ((GenericAccountStateTestWallet) genericAccountState).accountState.balance : genericAccountState instanceof GenericAccountStateTestGiver ? ((GenericAccountStateTestGiver) genericAccountState).accountState.balance : genericAccountState instanceof GenericAccountStateUninited ? ((GenericAccountStateUninited) genericAccountState).accountState.balance : genericAccountState instanceof GenericAccountStateWallet ? ((GenericAccountStateWallet) genericAccountState).accountState.balance : genericAccountState instanceof GenericAccountStateWalletV3 ? ((GenericAccountStateWalletV3) genericAccountState).accountState.balance : 0;
        if (j >= 0) {
            return j;
        }
        return 0;
    }

    public boolean isValidWalletAddress(String str) {
        return sendRequest(new UnpackAccountAddress(str), true) instanceof UnpackedAccountAddress;
    }

    public void isKeyStoreInvalidated(BooleanCallback booleanCallback) {
        Utilities.globalQueue.postRunnable(new -$$Lambda$TonController$4dT3xvzVSa-pPW-MT9gNBpw3wns(this, booleanCallback));
    }

    public /* synthetic */ void lambda$isKeyStoreInvalidated$3$TonController(BooleanCallback booleanCallback) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$_OEE2ksoduiuTHFpGjk8mvDcehg(booleanCallback, initCipher(2) == 2));
    }

    public void createWallet(String[] strArr, boolean z, WordsCallback wordsCallback, ErrorCallback errorCallback) {
        AndroidUtilities.getTonWalletSalt(this.currentAccount, new -$$Lambda$TonController$tgWf0krnuU6EvdvBZHK6lr1x_oY(this, errorCallback, z, strArr, wordsCallback));
    }

    public /* synthetic */ void lambda$createWallet$11$TonController(ErrorCallback errorCallback, boolean z, String[] strArr, WordsCallback wordsCallback, byte[] bArr) {
        Utilities.globalQueue.postRunnable(new -$$Lambda$TonController$hxM5pU9LVqYIxBytdUBYfYV0Abc(this, bArr, errorCallback, z, strArr, wordsCallback));
    }

    public /* synthetic */ void lambda$null$10$TonController(byte[] bArr, ErrorCallback errorCallback, boolean z, String[] strArr, WordsCallback wordsCallback) {
        if (bArr == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$lln4mzoHvpPox4RxMdcn111AjXs(errorCallback));
        } else if (initTonLib()) {
            sendRequest(new DeleteAllKeys(), true);
            if (keyStore == null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$P7kBEUm9Jb2MlUdB8cJNocpP8-E(errorCallback));
                return;
            }
            cleanup();
            UserConfig userConfig = getUserConfig();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("walletKey");
            stringBuilder.append(Utilities.random.nextLong());
            userConfig.tonKeyName = stringBuilder.toString();
            if (isKeyCreated(z)) {
                Object sendRequest;
                byte[] bArr2 = new byte[64];
                byte[] bArr3 = new byte[32];
                Utilities.random.nextBytes(bArr2);
                Utilities.random.nextBytes(bArr3);
                if (bArr.length == 32) {
                    System.arraycopy(bArr, 0, bArr2, 32, 32);
                    Arrays.fill(bArr, (byte) 0);
                }
                if (strArr == null) {
                    sendRequest = sendRequest(new CreateNewKey(bArr2, new byte[0], bArr3), true);
                } else {
                    sendRequest = sendRequest(new ImportKey(bArr2, new byte[0], new ExportedKey(strArr)), true);
                }
                if (sendRequest instanceof TonApi.Key) {
                    TonApi.Key key = (TonApi.Key) sendRequest;
                    if (strArr == null) {
                        Object sendRequest2 = sendRequest(new ExportKey(new InputKeyRegular(key, bArr2)), true);
                        if (sendRequest2 instanceof ExportedKey) {
                            onFinishWalletCreate(((ExportedKey) sendRequest2).wordList, wordsCallback, bArr2, key);
                        } else {
                            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$-Sq-PWfPKNVqcUgyq-JbOy4U1qk(this, errorCallback, sendRequest2));
                        }
                    } else {
                        onFinishWalletCreate(null, wordsCallback, bArr2, key);
                    }
                } else {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$BMAFS-SG30u4Ck0HTZEt6Zm-kpA(this, errorCallback, sendRequest));
                }
            } else {
                AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$NgvzVS9b7-YyWM6nBhNg0XjjecU(errorCallback));
            }
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$ANDCmubqTiuFsobBDpZALRnuNC4(errorCallback));
        }
    }

    public /* synthetic */ void lambda$null$6$TonController(ErrorCallback errorCallback, Object obj) {
        errorCallback.run("TONLIB_FAIL", getTonApiErrorSafe(obj));
    }

    public /* synthetic */ void lambda$null$7$TonController(ErrorCallback errorCallback, Object obj) {
        errorCallback.run("TONLIB_FAIL", getTonApiErrorSafe(obj));
    }

    public void setUserPasscode(String str, int i, Runnable runnable) {
        Utilities.globalQueue.postRunnable(new -$$Lambda$TonController$z22FAcE5iCnNUyw8kDQErp5pHfQ(this, i, str, runnable));
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
            hashMap.put(EncodeHintType.MARGIN, Integer.valueOf(0));
            return new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, 768, 768, hashMap, bitmap, context);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x005a */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    public static void shareBitmap(android.app.Activity r4, android.view.View r5, java.lang.String r6) {
        /*
        r5 = (android.widget.ImageView) r5;	 Catch:{ Exception -> 0x007c }
        r5 = r5.getDrawable();	 Catch:{ Exception -> 0x007c }
        r5 = (android.graphics.drawable.BitmapDrawable) r5;	 Catch:{ Exception -> 0x007c }
        r0 = org.telegram.messenger.AndroidUtilities.getSharingDirectory();	 Catch:{ Exception -> 0x007c }
        r0.mkdirs();	 Catch:{ Exception -> 0x007c }
        r1 = new java.io.File;	 Catch:{ Exception -> 0x007c }
        r2 = "qr.jpg";
        r1.<init>(r0, r2);	 Catch:{ Exception -> 0x007c }
        r0 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x007c }
        r2 = r1.getAbsolutePath();	 Catch:{ Exception -> 0x007c }
        r0.<init>(r2);	 Catch:{ Exception -> 0x007c }
        r5 = r5.getBitmap();	 Catch:{ Exception -> 0x007c }
        r2 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x007c }
        r3 = 87;
        r5.compress(r2, r3, r0);	 Catch:{ Exception -> 0x007c }
        r0.close();	 Catch:{ Exception -> 0x007c }
        r5 = new android.content.Intent;	 Catch:{ Exception -> 0x007c }
        r0 = "android.intent.action.SEND";
        r5.<init>(r0);	 Catch:{ Exception -> 0x007c }
        r0 = "image/jpeg";
        r5.setType(r0);	 Catch:{ Exception -> 0x007c }
        r0 = android.text.TextUtils.isEmpty(r6);	 Catch:{ Exception -> 0x007c }
        if (r0 != 0) goto L_0x0044;
    L_0x003f:
        r0 = "android.intent.extra.TEXT";
        r5.putExtra(r0, r6);	 Catch:{ Exception -> 0x007c }
    L_0x0044:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x007c }
        r0 = 24;
        r2 = "android.intent.extra.STREAM";
        if (r6 < r0) goto L_0x0062;
    L_0x004c:
        r6 = "org.telegram.messenger.beta.provider";
        r6 = androidx.core.content.FileProvider.getUriForFile(r4, r6, r1);	 Catch:{ Exception -> 0x005a }
        r5.putExtra(r2, r6);	 Catch:{ Exception -> 0x005a }
        r6 = 1;
        r5.setFlags(r6);	 Catch:{ Exception -> 0x005a }
        goto L_0x0069;
    L_0x005a:
        r6 = android.net.Uri.fromFile(r1);	 Catch:{ Exception -> 0x007c }
        r5.putExtra(r2, r6);	 Catch:{ Exception -> 0x007c }
        goto L_0x0069;
    L_0x0062:
        r6 = android.net.Uri.fromFile(r1);	 Catch:{ Exception -> 0x007c }
        r5.putExtra(r2, r6);	 Catch:{ Exception -> 0x007c }
    L_0x0069:
        r6 = "WalletShareQr";
        r0 = NUM; // 0x7f0e0CLASSNAME float:1.8881514E38 double:1.053163734E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r0);	 Catch:{ Exception -> 0x007c }
        r5 = android.content.Intent.createChooser(r5, r6);	 Catch:{ Exception -> 0x007c }
        r6 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r4.startActivityForResult(r5, r6);	 Catch:{ Exception -> 0x007c }
        goto L_0x0080;
    L_0x007c:
        r4 = move-exception;
        org.telegram.messenger.FileLog.e(r4);
    L_0x0080:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.TonController.shareBitmap(android.app.Activity, android.view.View, java.lang.String):void");
    }

    public String[] getHintWords() {
        initTonLib();
        Object sendRequest = sendRequest(new GetBip39Hints(), true);
        return sendRequest instanceof Bip39Hints ? ((Bip39Hints) sendRequest).words : null;
    }

    public String getWalletAddress(String str) {
        AccountAddress accountAddress = this.accountAddress;
        if (accountAddress != null) {
            return accountAddress.accountAddress;
        }
        initTonLib();
        Object sendRequest = sendRequest(new WalletV3GetAccountAddress(new WalletV3InitialAccountState(str, this.walletId)), true);
        if (!(sendRequest instanceof AccountAddress)) {
            return null;
        }
        AccountAddress accountAddress2 = (AccountAddress) sendRequest;
        this.accountAddress = accountAddress2;
        return accountAddress2.accountAddress;
    }

    public void checkPendingTransactionsForFailure(GenericAccountState genericAccountState) {
        if (genericAccountState != null && !this.pendingTransactions.isEmpty()) {
            long lastSyncTime = getLastSyncTime(genericAccountState);
            int size = this.pendingTransactions.size();
            int i = 0;
            Object obj = null;
            while (i < size) {
                if (((RawTransaction) this.pendingTransactions.get(i)).otherFee <= lastSyncTime) {
                    this.pendingTransactions.remove(i);
                    size--;
                    i--;
                    obj = 1;
                }
                i++;
            }
            if (this.onPendingTransactionsEmpty != null && this.pendingTransactions.isEmpty()) {
                this.onPendingTransactionsEmpty.run();
                this.onPendingTransactionsEmpty = null;
            }
            if (obj != null) {
                saveCache();
                getNotificationCenter().postNotificationName(NotificationCenter.walletPendingTransactionsChanged, new Object[0]);
            }
        }
    }

    public void setTransactionCallback(GetTransactionsCallback getTransactionsCallback) {
        this.uiTransactionCallback = getTransactionsCallback;
    }

    public void getTransactions(boolean z, InternalTransactionId internalTransactionId) {
        getTransactions(z, internalTransactionId, null);
    }

    private void getTransactions(boolean z, InternalTransactionId internalTransactionId, Runnable runnable) {
        sendRequest(new RawGetTransactions(this.accountAddress, internalTransactionId), new -$$Lambda$TonController$BJplM7TBHaoagaFxa2S8jmt1XHQ(this, z, runnable));
    }

    public /* synthetic */ void lambda$getTransactions$15$TonController(boolean z, Runnable runnable, Object obj) {
        if (obj instanceof RawTransactions) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$J-LtiyVAK6-s6HMNlP7AFP51bNM(this, new ArrayList(Arrays.asList(((RawTransactions) obj).transactions)), z, runnable));
        } else if (runnable != null || this.uiTransactionCallback != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$5uPQeMcdeNbcwu75v419pdUsHIA(this, runnable, z));
        }
    }

    public /* synthetic */ void lambda$null$13$TonController(ArrayList arrayList, boolean z, Runnable runnable) {
        this.walletLoaded = true;
        Object obj = null;
        if (!this.pendingTransactions.isEmpty()) {
            int size = this.pendingTransactions.size();
            int i = 0;
            Object obj2 = null;
            while (i < size) {
                RawTransaction rawTransaction = (RawTransaction) this.pendingTransactions.get(i);
                int size2 = arrayList.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    RawMessage rawMessage = ((RawTransaction) arrayList.get(i2)).inMsg;
                    if (rawMessage != null && Arrays.equals(rawTransaction.inMsg.bodyHash, rawMessage.bodyHash)) {
                        this.pendingTransactions.remove(i);
                        size--;
                        i--;
                        obj2 = 1;
                        break;
                    }
                }
                i++;
            }
            obj = obj2;
        }
        if (this.onPendingTransactionsEmpty != null && this.pendingTransactions.isEmpty()) {
            this.onPendingTransactionsEmpty.run();
            this.onPendingTransactionsEmpty = null;
        }
        if (z) {
            this.cachedTransactions.clear();
            this.cachedTransactions.addAll(arrayList);
            saveCache();
        } else if (obj != null) {
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
            getTransactionsCallback.run(z, null);
        }
    }

    private void preloadWallet(String str) {
        if (!this.isPrealodingWallet) {
            this.isPrealodingWallet = true;
            getWalletAddress(str);
            getAccountState(new -$$Lambda$TonController$gJbFAzuiSMm6DxM-5Qbx9QIhA_Q(this));
        }
    }

    public /* synthetic */ void lambda$preloadWallet$17$TonController(GenericAccountState genericAccountState) {
        if (genericAccountState == null) {
            this.isPrealodingWallet = false;
        } else {
            getTransactions(true, getLastTransactionId(genericAccountState), new -$$Lambda$TonController$QJgWom4shvmiclz7ycmuuua1G_g(this));
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
        sendRequest(new GenericGetAccountState(this.accountAddress), new -$$Lambda$TonController$lFlasxIPj0pe3vPrwkuvuEnSFGk(this, accountStateCallback));
    }

    public /* synthetic */ void lambda$getAccountState$20$TonController(AccountStateCallback accountStateCallback, Object obj) {
        if (obj instanceof GenericAccountState) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$ZC-1xr9i8Xz4R9b0Ipt-ntBBs6Y(this, accountStateCallback, obj));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$37rcZVVB24mwu_62dzh_KprPJRM(accountStateCallback));
        }
    }

    public /* synthetic */ void lambda$null$18$TonController(AccountStateCallback accountStateCallback, Object obj) {
        GenericAccountState genericAccountState = (GenericAccountState) obj;
        this.cachedAccountState = genericAccountState;
        accountStateCallback.run(genericAccountState);
    }

    private InputKey decryptTonData(String str, Cipher cipher, Runnable runnable, ErrorCallback errorCallback, boolean z) {
        ErrorCallback errorCallback2 = errorCallback;
        UserConfig userConfig = getUserConfig();
        byte[] decrypt = decrypt(userConfig.tonEncryptedData, cipher);
        if (decrypt == null || decrypt.length <= 3) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$KVuT1XGAhJAd-9p7lcZAZLdA18A(errorCallback2));
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
        if (decrypt[1] == (byte) 111 && decrypt[2] == (byte) 107) {
            if (!(TextUtils.isEmpty(str) || runnable == null)) {
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
            return new InputKeyRegular(new TonApi.Key(userConfig.tonPublicKey, bArr4), bArr3);
        }
        if (TextUtils.isEmpty(str)) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$HBNfR0kbdSvbRiQ3yhmIB2tL69g(errorCallback2));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$-YETmygdyjyHumNBoQ3cxTfex40(errorCallback2));
        }
        return null;
    }

    public void prepareForPasscodeChange(String str, Runnable runnable, ErrorCallback errorCallback) {
        Utilities.globalQueue.postRunnable(new -$$Lambda$TonController$0QYEaLYvGIfwyA5t_rPmcAajkf0(this, str, errorCallback, runnable));
    }

    public /* synthetic */ void lambda$prepareForPasscodeChange$24$TonController(String str, ErrorCallback errorCallback, Runnable runnable) {
        if (decryptTonData(str, null, null, errorCallback, true) != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public void getSecretWords(String str, Cipher cipher, WordsCallback wordsCallback, ErrorCallback errorCallback) {
        Utilities.globalQueue.postRunnable(new -$$Lambda$TonController$Genh6d-D8nPvdFbitLergEttZ8o(this, str, cipher, errorCallback, wordsCallback));
    }

    public /* synthetic */ void lambda$getSecretWords$28$TonController(String str, Cipher cipher, ErrorCallback errorCallback, WordsCallback wordsCallback) {
        initTonLib();
        InputKey decryptTonData = decryptTonData(str, cipher, null, errorCallback, false);
        if (decryptTonData != null) {
            sendRequest(new ExportKey(decryptTonData), new -$$Lambda$TonController$3vvHOu5RFS7QEN90b6dOIO6uIHM(this, wordsCallback, errorCallback));
        }
    }

    public /* synthetic */ void lambda$null$27$TonController(WordsCallback wordsCallback, ErrorCallback errorCallback, Object obj) {
        if (obj instanceof ExportedKey) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$oAriRjh11c2Pb8qwcXa_NXDnoD8(wordsCallback, (ExportedKey) obj));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$x01Zt0IdL5udS9k5Xt_mQzML-7s(this, errorCallback, obj));
        }
    }

    public /* synthetic */ void lambda$null$26$TonController(ErrorCallback errorCallback, Object obj) {
        errorCallback.run("TONLIB_FAIL", getTonApiErrorSafe(obj));
    }

    public void getSendFee(String str, String str2, long j, String str3, FeeCallback feeCallback) {
        Utilities.globalQueue.postRunnable(new -$$Lambda$TonController$Fi-LPQ1eWOgf-5nsADaZt1litCc(this, str, str2, j, str3, feeCallback));
    }

    public /* synthetic */ void lambda$getSendFee$34$TonController(String str, String str2, long j, String str3, FeeCallback feeCallback) {
        String str4 = str;
        str4 = str2;
        sendRequest(new GenericCreateSendGramsQuery(new InputKeyFake(), new AccountAddress(str), new AccountAddress(str2), j, 0, true, str3 != null ? str3.getBytes() : new byte[0]), new -$$Lambda$TonController$qR3Tgb351JrkevNaqYs6rgZrxoI(this, feeCallback));
    }

    public /* synthetic */ void lambda$null$33$TonController(FeeCallback feeCallback, Object obj) {
        if (obj instanceof QueryInfo) {
            sendRequest(new QueryEstimateFees(((QueryInfo) obj).id, true), new -$$Lambda$TonController$df_RsxATpf0GQpenv3l4j2PC1Tw(feeCallback));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$jW6wtjLesOuYuNx0zBEds91eiMM(feeCallback));
        }
    }

    static /* synthetic */ void lambda$null$31(FeeCallback feeCallback, Object obj) {
        if (obj instanceof QueryFees) {
            Fees fees = ((QueryFees) obj).sourceFees;
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$-kt7s_A0jMhl4hCL2zTDpVn7PtY(feeCallback, ((fees.fwdFee + fees.gasFee) + fees.inFwdFee) + fees.storageFee));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$1_Iq1FPXTAyXlSAJwQTDt3Yuu3A(feeCallback));
    }

    public void sendGrams(String str, Cipher cipher, InputKey inputKey, String str2, String str3, long j, String str4, Runnable runnable, Runnable runnable2, Runnable runnable3, DangerousCallback dangerousCallback, ErrorCallback errorCallback) {
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        -$$Lambda$TonController$pCFvgPRO7LoJVVjLo13UdJcy_fw -__lambda_toncontroller_pcfvgpro7lojvvjlo13udjcy_fw = r1;
        -$$Lambda$TonController$pCFvgPRO7LoJVVjLo13UdJcy_fw -__lambda_toncontroller_pcfvgpro7lojvvjlo13udjcy_fw2 = new -$$Lambda$TonController$pCFvgPRO7LoJVVjLo13UdJcy_fw(this, inputKey, str, cipher, runnable, errorCallback, str2, str3, j, str4, runnable3, runnable2, dangerousCallback);
        dispatchQueue.postRunnable(-__lambda_toncontroller_pcfvgpro7lojvvjlo13udjcy_fw);
    }

    public /* synthetic */ void lambda$sendGrams$40$TonController(InputKey inputKey, String str, Cipher cipher, Runnable runnable, ErrorCallback errorCallback, String str2, String str3, long j, String str4, Runnable runnable2, Runnable runnable3, DangerousCallback dangerousCallback) {
        InputKey decryptTonData = inputKey == null ? decryptTonData(str, cipher, runnable, errorCallback, false) : inputKey;
        if (decryptTonData != null) {
            long j2 = j;
            Function genericCreateSendGramsQuery = new GenericCreateSendGramsQuery(decryptTonData, new AccountAddress(str2), new AccountAddress(str3), j2, 0, true, str4 != null ? str4.getBytes() : new byte[0]);
            TonLibCallback -__lambda_toncontroller_s5yblrm_eah0urwn_n6fuvaxvz0 = new -$$Lambda$TonController$S5YblRM_eaH0urwn_n6FUVAxVZ0(this, str2, str3, j2, str4, runnable2, runnable3, errorCallback, dangerousCallback, decryptTonData);
            sendRequest(genericCreateSendGramsQuery, -__lambda_toncontroller_s5yblrm_eah0urwn_n6fuvaxvz0);
        }
    }

    public /* synthetic */ void lambda$null$39$TonController(String str, String str2, long j, String str3, Runnable runnable, Runnable runnable2, ErrorCallback errorCallback, DangerousCallback dangerousCallback, InputKey inputKey, Object obj) {
        Object obj2 = obj;
        if (obj2 instanceof QueryInfo) {
            QueryInfo queryInfo = (QueryInfo) obj2;
            Function querySend = new QuerySend(queryInfo.id);
            TonLibCallback -__lambda_toncontroller_z-13g23zsh_gfnxypbtmompovk4 = new -$$Lambda$TonController$z-13G23zsH_GfNxYPBtMOmpOvK4(this, str, str2, j, str3, queryInfo, runnable, runnable2, errorCallback);
            sendRequest(querySend, -__lambda_toncontroller_z-13g23zsh_gfnxypbtmompovk4);
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$bMO-MD6JPh5oaQurli6G2MeAsRY(this, obj, dangerousCallback, inputKey, errorCallback));
    }

    public /* synthetic */ void lambda$null$37$TonController(String str, String str2, long j, String str3, QueryInfo queryInfo, Runnable runnable, Runnable runnable2, ErrorCallback errorCallback, Object obj) {
        Object obj2 = obj;
        if (obj2 instanceof Ok) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$CuyudYXTq33MGPIBUZ0M6s-OBAc(this, str, str2, j, str3, queryInfo, runnable, runnable2));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$gw9gFHyWSEcegrBijUnpaJmp1dI(this, errorCallback, obj2));
    }

    public /* synthetic */ void lambda$null$35$TonController(String str, String str2, long j, String str3, QueryInfo queryInfo, Runnable runnable, Runnable runnable2) {
        QueryInfo queryInfo2 = queryInfo;
        RawMessage rawMessage = new RawMessage();
        rawMessage.source = str;
        rawMessage.destination = str2;
        rawMessage.value = -j;
        rawMessage.message = str3 != null ? str3.getBytes() : new byte[0];
        rawMessage.bodyHash = queryInfo2.bodyHash;
        this.pendingTransactions.add(0, new RawTransaction(System.currentTimeMillis() / 1000, new byte[0], new InternalTransactionId(), 0, 0, queryInfo2.validUntil, rawMessage, new RawMessage[0]));
        saveCache();
        getNotificationCenter().postNotificationName(NotificationCenter.walletPendingTransactionsChanged, new Object[0]);
        this.onPendingTransactionsEmpty = runnable;
        runnable2.run();
    }

    public /* synthetic */ void lambda$null$36$TonController(ErrorCallback errorCallback, Object obj) {
        errorCallback.run("TONLIB_FAIL", getTonApiErrorSafe(obj));
    }

    public /* synthetic */ void lambda$null$38$TonController(Object obj, DangerousCallback dangerousCallback, InputKey inputKey, ErrorCallback errorCallback) {
        Error tonApiErrorSafe = getTonApiErrorSafe(obj);
        if (tonApiErrorSafe == null || !tonApiErrorSafe.message.startsWith("DANGEROUS_TRANSACTION")) {
            errorCallback.run("TONLIB_FAIL", tonApiErrorSafe);
        } else {
            dangerousCallback.run(inputKey);
        }
    }

    private void runShortPolling() {
        if (!this.shortPollingInProgress && !this.pendingTransactions.isEmpty()) {
            this.shortPollingInProgress = true;
            getAccountState(new -$$Lambda$TonController$ZwQvnl7h5w2BAiAFZkDfeaJCd5c(this, this.cachedAccountState));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x001f  */
    /* JADX WARNING: Missing block: B:7:0x0018, code skipped:
            if (r8.lt == r2.lt) goto L_0x001c;
     */
    public /* synthetic */ void lambda$runShortPolling$42$TonController(drinkless.org.ton.TonApi.GenericAccountState r8, drinkless.org.ton.TonApi.GenericAccountState r9) {
        /*
        r7 = this;
        r0 = 1;
        r1 = 0;
        if (r9 == 0) goto L_0x001c;
    L_0x0004:
        if (r8 == 0) goto L_0x001a;
    L_0x0006:
        r8 = getLastTransactionId(r8);
        r2 = getLastTransactionId(r9);
        if (r8 == 0) goto L_0x001a;
    L_0x0010:
        if (r2 == 0) goto L_0x001a;
    L_0x0012:
        r3 = r8.lt;
        r5 = r2.lt;
        r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r8 == 0) goto L_0x001c;
    L_0x001a:
        r8 = 1;
        goto L_0x001d;
    L_0x001c:
        r8 = 0;
    L_0x001d:
        if (r8 == 0) goto L_0x002c;
    L_0x001f:
        r8 = getLastTransactionId(r9);
        r1 = new org.telegram.messenger.-$$Lambda$TonController$1e_N16tO-4nWvlG6WbYTOXkO55E;
        r1.<init>(r7, r9);
        r7.getTransactions(r0, r8, r1);
        goto L_0x003f;
    L_0x002c:
        r8 = 0;
        r7.shortPollRunnable = r8;
        r7.shortPollingInProgress = r1;
        r7.checkPendingTransactionsForFailure(r9);
        r8 = r7.pendingTransactions;
        r8 = r8.isEmpty();
        if (r8 != 0) goto L_0x003f;
    L_0x003c:
        r7.scheduleShortPoll();
    L_0x003f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.TonController.lambda$runShortPolling$42$TonController(drinkless.org.ton.TonApi$GenericAccountState, drinkless.org.ton.TonApi$GenericAccountState):void");
    }

    public /* synthetic */ void lambda$null$41$TonController(GenericAccountState genericAccountState) {
        this.shortPollRunnable = null;
        this.shortPollingInProgress = false;
        checkPendingTransactionsForFailure(genericAccountState);
        if (!this.pendingTransactions.isEmpty()) {
            scheduleShortPoll();
        }
    }

    public void scheduleShortPoll() {
        if (this.shortPollRunnable == null) {
            -$$Lambda$TonController$y5gHwqWFihVH-4vITInmi9Ihzbk -__lambda_toncontroller_y5ghwqwfihvh-4vitinmi9ihzbk = new -$$Lambda$TonController$y5gHwqWFihVH-4vITInmi9Ihzbk(this);
            this.shortPollRunnable = -__lambda_toncontroller_y5ghwqwfihvh-4vitinmi9ihzbk;
            AndroidUtilities.runOnUIThread(-__lambda_toncontroller_y5ghwqwfihvh-4vitinmi9ihzbk, 3000);
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
        return this.pendingTransactions.isEmpty() ^ 1;
    }

    public static CharSequence formatCurrency(long j) {
        if (j == 0) {
            return "0";
        }
        String str = j < 0 ? "-" : "";
        StringBuilder stringBuilder = new StringBuilder(String.format(Locale.US, "%s%d.%09d", new Object[]{str, Long.valueOf(Math.abs(j / NUM)), Long.valueOf(Math.abs(j % NUM))}));
        while (stringBuilder.length() > 1 && stringBuilder.charAt(stringBuilder.length() - 1) == '0' && stringBuilder.charAt(stringBuilder.length() - 2) != '.') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder;
    }

    private void loadTonConfigFromUrl() {
        UserConfig userConfig = getUserConfig();
        if (userConfig.walletConfigType == 0) {
            WalletConfigLoader.loadConfig(userConfig.walletConfigUrl, new -$$Lambda$TonController$t6QAy4FxSz0TIxSldHqjrzP73cU(this, userConfig));
        }
    }

    public /* synthetic */ void lambda$loadTonConfigFromUrl$43$TonController(UserConfig userConfig, String str) {
        if (!(TextUtils.isEmpty(str) || TextUtils.equals(userConfig.walletConfigFromUrl, str))) {
            userConfig.walletConfigFromUrl = str;
            userConfig.saveConfig(false);
            onTonConfigUpdated();
        }
    }
}
