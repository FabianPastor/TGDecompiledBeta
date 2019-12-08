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
import drinkless.org.ton.TonApi.Function;
import drinkless.org.ton.TonApi.GenericAccountState;
import drinkless.org.ton.TonApi.GenericAccountStateRaw;
import drinkless.org.ton.TonApi.GenericAccountStateTestGiver;
import drinkless.org.ton.TonApi.GenericAccountStateTestWallet;
import drinkless.org.ton.TonApi.GenericAccountStateUninited;
import drinkless.org.ton.TonApi.GenericAccountStateWallet;
import drinkless.org.ton.TonApi.GenericGetAccountState;
import drinkless.org.ton.TonApi.GenericSendGrams;
import drinkless.org.ton.TonApi.GetBip39Hints;
import drinkless.org.ton.TonApi.ImportKey;
import drinkless.org.ton.TonApi.Init;
import drinkless.org.ton.TonApi.InputKey;
import drinkless.org.ton.TonApi.InternalTransactionId;
import drinkless.org.ton.TonApi.KeyStoreTypeDirectory;
import drinkless.org.ton.TonApi.LogStreamFile;
import drinkless.org.ton.TonApi.Object;
import drinkless.org.ton.TonApi.Ok;
import drinkless.org.ton.TonApi.OnLiteServerQueryError;
import drinkless.org.ton.TonApi.OnLiteServerQueryResult;
import drinkless.org.ton.TonApi.Options;
import drinkless.org.ton.TonApi.OptionsSetConfig;
import drinkless.org.ton.TonApi.RawAccountState;
import drinkless.org.ton.TonApi.RawGetTransactions;
import drinkless.org.ton.TonApi.RawMessage;
import drinkless.org.ton.TonApi.RawTransaction;
import drinkless.org.ton.TonApi.RawTransactions;
import drinkless.org.ton.TonApi.SendGramsResult;
import drinkless.org.ton.TonApi.SetLogStream;
import drinkless.org.ton.TonApi.TestGiverAccountState;
import drinkless.org.ton.TonApi.TestWalletAccountState;
import drinkless.org.ton.TonApi.UninitedAccountState;
import drinkless.org.ton.TonApi.UnpackAccountAddress;
import drinkless.org.ton.TonApi.UnpackedAccountAddress;
import drinkless.org.ton.TonApi.UpdateSendLiteServerQuery;
import drinkless.org.ton.TonApi.WalletAccountState;
import drinkless.org.ton.TonApi.WalletGetAccountAddress;
import drinkless.org.ton.TonApi.WalletInitialAccountState;
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
import org.telegram.messenger.MessagesStorage.BooleanCallback;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_wallet_getKeySecretSalt;
import org.telegram.tgnet.TLRPC.TL_wallet_liteResponse;
import org.telegram.tgnet.TLRPC.TL_wallet_secretSalt;
import org.telegram.tgnet.TLRPC.TL_wallet_sendLiteRequest;

@TargetApi(18)
public class TonController extends BaseController {
    public static final int CIPHER_INIT_FAILED = 0;
    public static final int CIPHER_INIT_KEY_INVALIDATED = 2;
    public static final int CIPHER_INIT_OK = 1;
    private static volatile TonController[] Instance = new TonController[3];
    public static final int KEY_PROTECTION_TYPE_BIOMETRIC = 2;
    public static final int KEY_PROTECTION_TYPE_LOCKSCREEN = 1;
    public static final int KEY_PROTECTION_TYPE_NONE = 0;
    private static final boolean USE_MTPROTO = true;
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
    private boolean initied;
    private boolean isPrealodingWallet;
    private File keyDirectoty;
    private Runnable onPendingTransactionsEmpty;
    private ArrayList<RawTransaction> pendingTransactions = new ArrayList();
    private Runnable shortPollRunnable;
    private boolean shortPollingInProgress;
    private SharedPreferences tonCache;

    public interface AccountStateCallback {
        void run(GenericAccountState genericAccountState);
    }

    public interface DangerousCallback {
        void run(InputKey inputKey);
    }

    public interface ErrorCallback {
        void run(String str, Error error);
    }

    public interface GetTransactionsCallback {
        void run(ArrayList<RawTransaction> arrayList);
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
        this.client = Client.create(new -$$Lambda$TonController$bsWlPmC-mcv5bw47ilenQ4kKjmI(this), null, null);
        if (BuildVars.LOGS_ENABLED) {
            this.client.send(new SetLogStream(new LogStreamFile(FileLog.getTonlibLogPath(), 5242880)), null);
        }
    }

    public /* synthetic */ void lambda$new$1$TonController(Object object) {
        if (object instanceof UpdateSendLiteServerQuery) {
            UpdateSendLiteServerQuery updateSendLiteServerQuery = (UpdateSendLiteServerQuery) object;
            long j = updateSendLiteServerQuery.id;
            TL_wallet_sendLiteRequest tL_wallet_sendLiteRequest = new TL_wallet_sendLiteRequest();
            tL_wallet_sendLiteRequest.body = updateSendLiteServerQuery.data;
            getConnectionsManager().sendRequest(tL_wallet_sendLiteRequest, new -$$Lambda$TonController$qEOaK4JBeIF9VugRRfBThCKBn3k(this, j), 8);
        }
    }

    public /* synthetic */ void lambda$null$0$TonController(long j, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_wallet_liteResponse) {
            this.client.send(new OnLiteServerQueryResult(j, ((TL_wallet_liteResponse) tLObject).response), null);
        } else if (tL_error != null) {
            this.client.send(new OnLiteServerQueryError(j, new Error(tL_error.code, tL_error.text)), null);
        }
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
                StringBuilder stringBuilder2;
                long j;
                byte[] decode;
                String stringBuilder3;
                StringBuilder stringBuilder4;
                int i = this.tonCache.getInt("state.type", 0);
                long j2 = this.tonCache.getLong("state.balance", 0);
                int i2 = this.tonCache.getInt("state.seqno", 0);
                InternalTransactionId internalTransactionId = new InternalTransactionId(this.tonCache.getLong("transaction.lt", 0), Base64.decode(this.tonCache.getString("transaction.hash", null), 0));
                long j3 = this.tonCache.getLong("syncUtime", 0);
                if (i == 0) {
                    this.cachedAccountState = new GenericAccountStateRaw(new RawAccountState(j2, null, null, internalTransactionId, null, j3));
                } else if (i == 1) {
                    this.cachedAccountState = new GenericAccountStateTestWallet(new TestWalletAccountState(j2, i2, internalTransactionId, j3));
                } else if (i == 2) {
                    this.cachedAccountState = new GenericAccountStateTestGiver(new TestGiverAccountState(j2, i2, internalTransactionId, j3));
                } else if (i == 3) {
                    this.cachedAccountState = new GenericAccountStateUninited(new UninitedAccountState(j2, internalTransactionId, null, j3));
                } else if (i == 4) {
                    this.cachedAccountState = new GenericAccountStateWallet(new WalletAccountState(j2, i2, internalTransactionId, j3));
                }
                i = this.tonCache.getInt("transactionsCount", 0);
                int i3 = 0;
                while (true) {
                    str3 = "inMsg.message";
                    str4 = "inMsg.bodyHash";
                    str5 = "inMsg.value";
                    str6 = "utime";
                    str7 = "inMsg.destination";
                    str8 = "inMsg.source";
                    str9 = ".";
                    if (i3 >= i) {
                        break;
                    }
                    SharedPreferences sharedPreferences;
                    long j4;
                    long j5;
                    long j6;
                    StringBuilder stringBuilder5;
                    RawMessage rawMessage;
                    int i4;
                    RawMessage[] rawMessageArr;
                    StringBuilder stringBuilder6 = new StringBuilder();
                    stringBuilder6.append("transaction");
                    stringBuilder6.append(i3);
                    stringBuilder6.append(str9);
                    String stringBuilder7 = stringBuilder6.toString();
                    SharedPreferences sharedPreferences2 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder7);
                    stringBuilder.append(str8);
                    if (sharedPreferences2.contains(stringBuilder.toString())) {
                        sharedPreferences = this.tonCache;
                        stringBuilder6 = new StringBuilder();
                        stringBuilder6.append(stringBuilder7);
                        stringBuilder6.append(str8);
                        string = sharedPreferences.getString(stringBuilder6.toString(), str2);
                        sharedPreferences = this.tonCache;
                        StringBuilder stringBuilder8 = new StringBuilder();
                        stringBuilder8.append(stringBuilder7);
                        stringBuilder8.append(str7);
                        string2 = sharedPreferences.getString(stringBuilder8.toString(), str2);
                        sharedPreferences = this.tonCache;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(stringBuilder7);
                        stringBuilder2.append(str5);
                        j = sharedPreferences.getLong(stringBuilder2.toString(), 0);
                        sharedPreferences = this.tonCache;
                        StringBuilder stringBuilder9 = new StringBuilder();
                        stringBuilder9.append(stringBuilder7);
                        stringBuilder9.append("inMsg.fwdFee");
                        j4 = sharedPreferences.getLong(stringBuilder9.toString(), 0);
                        sharedPreferences = this.tonCache;
                        stringBuilder9 = new StringBuilder();
                        stringBuilder9.append(stringBuilder7);
                        stringBuilder9.append("inMsg.ihrFee");
                        j5 = sharedPreferences.getLong(stringBuilder9.toString(), 0);
                        sharedPreferences = this.tonCache;
                        stringBuilder9 = new StringBuilder();
                        stringBuilder9.append(stringBuilder7);
                        stringBuilder9.append("inMsg.createdLt");
                        j6 = sharedPreferences.getLong(stringBuilder9.toString(), 0);
                        sharedPreferences = this.tonCache;
                        stringBuilder9 = new StringBuilder();
                        stringBuilder9.append(stringBuilder7);
                        stringBuilder9.append(str4);
                        decode = Base64.decode(sharedPreferences.getString(stringBuilder9.toString(), str2), 0);
                        sharedPreferences = this.tonCache;
                        stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(stringBuilder7);
                        stringBuilder5.append(str3);
                        rawMessage = new RawMessage(string, string2, j, j4, j5, j6, decode, Base64.decode(sharedPreferences.getString(stringBuilder5.toString(), str2), 0));
                    } else {
                        rawMessage = str2;
                    }
                    SharedPreferences sharedPreferences3 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder7);
                    stringBuilder.append(str);
                    if (sharedPreferences3.contains(stringBuilder.toString())) {
                        sharedPreferences3 = this.tonCache;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(stringBuilder7);
                        stringBuilder.append(str);
                        RawMessage[] rawMessageArr2 = new RawMessage[sharedPreferences3.getInt(stringBuilder.toString(), 0)];
                        int i5 = 0;
                        while (i5 < rawMessageArr2.length) {
                            StringBuilder stringBuilder10 = new StringBuilder();
                            stringBuilder10.append(stringBuilder7);
                            stringBuilder10.append("outMsg");
                            stringBuilder10.append(i5);
                            stringBuilder10.append(str9);
                            stringBuilder3 = stringBuilder10.toString();
                            SharedPreferences sharedPreferences4 = this.tonCache;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(stringBuilder3);
                            stringBuilder2.append("source");
                            string = sharedPreferences4.getString(stringBuilder2.toString(), str2);
                            sharedPreferences4 = this.tonCache;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(stringBuilder3);
                            stringBuilder2.append("destination");
                            string2 = sharedPreferences4.getString(stringBuilder2.toString(), str2);
                            sharedPreferences4 = this.tonCache;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(stringBuilder3);
                            stringBuilder2.append("value");
                            i4 = i;
                            j = sharedPreferences4.getLong(stringBuilder2.toString(), 0);
                            SharedPreferences sharedPreferences5 = this.tonCache;
                            stringBuilder4 = new StringBuilder();
                            stringBuilder4.append(stringBuilder3);
                            stringBuilder4.append("fwdFee");
                            j4 = sharedPreferences5.getLong(stringBuilder4.toString(), 0);
                            sharedPreferences5 = this.tonCache;
                            stringBuilder4 = new StringBuilder();
                            stringBuilder4.append(stringBuilder3);
                            stringBuilder4.append("ihrFee");
                            j5 = sharedPreferences5.getLong(stringBuilder4.toString(), 0);
                            sharedPreferences5 = this.tonCache;
                            stringBuilder4 = new StringBuilder();
                            stringBuilder4.append(stringBuilder3);
                            stringBuilder4.append("createdLt");
                            j6 = sharedPreferences5.getLong(stringBuilder4.toString(), 0);
                            sharedPreferences5 = this.tonCache;
                            stringBuilder4 = new StringBuilder();
                            stringBuilder4.append(stringBuilder3);
                            stringBuilder4.append("bodyHash");
                            decode = Base64.decode(sharedPreferences5.getString(stringBuilder4.toString(), null), 0);
                            sharedPreferences5 = this.tonCache;
                            stringBuilder4 = new StringBuilder();
                            stringBuilder4.append(stringBuilder3);
                            stringBuilder4.append("message");
                            rawMessageArr2[i5] = new RawMessage(string, string2, j, j4, j5, j6, decode, Base64.decode(sharedPreferences5.getString(stringBuilder4.toString(), null), 0));
                            i5++;
                            i = i4;
                            str2 = null;
                        }
                        i4 = i;
                        rawMessageArr = rawMessageArr2;
                    } else {
                        i4 = i;
                        rawMessageArr = null;
                    }
                    SharedPreferences sharedPreferences6 = this.tonCache;
                    StringBuilder stringBuilder11 = new StringBuilder();
                    stringBuilder11.append(stringBuilder7);
                    stringBuilder11.append(str6);
                    long j7 = sharedPreferences6.getLong(stringBuilder11.toString(), 0);
                    sharedPreferences6 = this.tonCache;
                    stringBuilder11 = new StringBuilder();
                    stringBuilder11.append(stringBuilder7);
                    stringBuilder11.append("data");
                    byte[] decode2 = Base64.decode(sharedPreferences6.getString(stringBuilder11.toString(), null), 0);
                    sharedPreferences3 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder7);
                    stringBuilder.append("lt");
                    long j8 = sharedPreferences3.getLong(stringBuilder.toString(), 0);
                    sharedPreferences = this.tonCache;
                    stringBuilder5 = new StringBuilder();
                    stringBuilder5.append(stringBuilder7);
                    stringBuilder5.append("hash");
                    InternalTransactionId internalTransactionId2 = new InternalTransactionId(j8, Base64.decode(sharedPreferences.getString(stringBuilder5.toString(), null), 0));
                    sharedPreferences3 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder7);
                    stringBuilder.append("fee");
                    long j9 = sharedPreferences3.getLong(stringBuilder.toString(), 0);
                    sharedPreferences3 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder7);
                    stringBuilder.append("storageFee");
                    long j10 = sharedPreferences3.getLong(stringBuilder.toString(), 0);
                    sharedPreferences3 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder7);
                    stringBuilder.append("otherFee");
                    this.cachedTransactions.add(new RawTransaction(j7, decode2, internalTransactionId2, j9, j10, sharedPreferences3.getLong(stringBuilder.toString(), 0), rawMessage, rawMessageArr));
                    i3++;
                    i = i4;
                    str2 = null;
                }
                int i6 = this.tonCache.getInt("pendingCount", 0);
                int i7 = 0;
                while (i7 < i6) {
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append("pending");
                    stringBuilder4.append(i7);
                    stringBuilder4.append(str9);
                    String stringBuilder12 = stringBuilder4.toString();
                    SharedPreferences sharedPreferences7 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder12);
                    stringBuilder.append(str8);
                    string = sharedPreferences7.getString(stringBuilder.toString(), null);
                    sharedPreferences7 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder12);
                    stringBuilder.append(str7);
                    string2 = sharedPreferences7.getString(stringBuilder.toString(), null);
                    sharedPreferences7 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder12);
                    stringBuilder.append(str5);
                    String str10 = str7;
                    stringBuilder3 = str8;
                    j = sharedPreferences7.getLong(stringBuilder.toString(), 0);
                    sharedPreferences7 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder12);
                    stringBuilder.append(str4);
                    decode = Base64.decode(sharedPreferences7.getString(stringBuilder.toString(), null), 0);
                    sharedPreferences7 = this.tonCache;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder12);
                    stringBuilder.append(str3);
                    RawMessage rawMessage2 = new RawMessage(string, string2, j, 0, 0, 0, decode, Base64.decode(sharedPreferences7.getString(stringBuilder.toString(), null), 0));
                    SharedPreferences sharedPreferences8 = this.tonCache;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(stringBuilder12);
                    stringBuilder2.append(str6);
                    this.pendingTransactions.add(new RawTransaction(sharedPreferences8.getLong(stringBuilder2.toString(), 0), new byte[0], new InternalTransactionId(), 0, 0, 0, rawMessage2, new RawMessage[0]));
                    i7++;
                    str8 = stringBuilder3;
                    str7 = str10;
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
                String str;
                String str2;
                String str3;
                String str4;
                String str5;
                String str6;
                String str7;
                StringBuilder stringBuilder;
                int length;
                StringBuilder stringBuilder2;
                Editor edit = tonController.tonCache.edit();
                edit.clear();
                long j3;
                InternalTransactionId internalTransactionId2;
                if (tonController.cachedAccountState instanceof GenericAccountStateRaw) {
                    GenericAccountStateRaw genericAccountStateRaw = (GenericAccountStateRaw) tonController.cachedAccountState;
                    j3 = genericAccountStateRaw.accountState.balance;
                    InternalTransactionId internalTransactionId3 = genericAccountStateRaw.accountState.lastTransactionId;
                    j = genericAccountStateRaw.accountState.syncUtime;
                    i = 0;
                    internalTransactionId = internalTransactionId3;
                    j2 = j3;
                    i2 = 0;
                } else if (tonController.cachedAccountState instanceof GenericAccountStateTestWallet) {
                    GenericAccountStateTestWallet genericAccountStateTestWallet = (GenericAccountStateTestWallet) tonController.cachedAccountState;
                    j3 = genericAccountStateTestWallet.accountState.balance;
                    int i3 = genericAccountStateTestWallet.accountState.seqno;
                    internalTransactionId2 = genericAccountStateTestWallet.accountState.lastTransactionId;
                    j = genericAccountStateTestWallet.accountState.syncUtime;
                    internalTransactionId = internalTransactionId2;
                    i = i3;
                    j2 = j3;
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
                    internalTransactionId2 = genericAccountStateUninited.accountState.lastTransactionId;
                    j = genericAccountStateUninited.accountState.syncUtime;
                    internalTransactionId = internalTransactionId2;
                    i = 0;
                } else if (tonController.cachedAccountState instanceof GenericAccountStateWallet) {
                    GenericAccountStateWallet genericAccountStateWallet = (GenericAccountStateWallet) tonController.cachedAccountState;
                    i2 = 4;
                    j2 = genericAccountStateWallet.accountState.balance;
                    i = genericAccountStateWallet.accountState.seqno;
                    internalTransactionId = genericAccountStateWallet.accountState.lastTransactionId;
                    j = genericAccountStateWallet.accountState.syncUtime;
                } else {
                    return;
                }
                edit.putInt("state.type", i2);
                edit.putLong("state.balance", j2);
                edit.putInt("state.seqno", i);
                edit.putLong("transaction.lt", internalTransactionId.lt);
                edit.putString("transaction.hash", Base64.encodeToString(internalTransactionId.hash, 0));
                edit.putLong("syncUtime", j);
                int min = Math.min(10, tonController.cachedTransactions.size());
                edit.putInt("transactionsCount", min);
                i2 = 0;
                while (true) {
                    str = "inMsg.message";
                    str2 = "inMsg.bodyHash";
                    str3 = "inMsg.value";
                    str4 = "inMsg.destination";
                    str5 = "utime";
                    str6 = "inMsg.source";
                    str7 = ".";
                    if (i2 >= min) {
                        break;
                    }
                    StringBuilder stringBuilder3;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("transaction");
                    stringBuilder.append(i2);
                    stringBuilder.append(str7);
                    String stringBuilder4 = stringBuilder.toString();
                    RawTransaction rawTransaction = (RawTransaction) tonController.cachedTransactions.get(i2);
                    if (rawTransaction.inMsg != null) {
                        StringBuilder stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(stringBuilder4);
                        stringBuilder5.append(str6);
                        edit.putString(stringBuilder5.toString(), rawTransaction.inMsg.source);
                        StringBuilder stringBuilder6 = new StringBuilder();
                        stringBuilder6.append(stringBuilder4);
                        stringBuilder6.append(str4);
                        edit.putString(stringBuilder6.toString(), rawTransaction.inMsg.destination);
                        StringBuilder stringBuilder7 = new StringBuilder();
                        stringBuilder7.append(stringBuilder4);
                        stringBuilder7.append(str3);
                        edit.putLong(stringBuilder7.toString(), rawTransaction.inMsg.value);
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(stringBuilder4);
                        stringBuilder3.append("inMsg.fwdFee");
                        edit.putLong(stringBuilder3.toString(), rawTransaction.inMsg.fwdFee);
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(stringBuilder4);
                        stringBuilder3.append("inMsg.ihrFee");
                        edit.putLong(stringBuilder3.toString(), rawTransaction.inMsg.ihrFee);
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(stringBuilder4);
                        stringBuilder3.append("inMsg.createdLt");
                        edit.putLong(stringBuilder3.toString(), rawTransaction.inMsg.createdLt);
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(stringBuilder4);
                        stringBuilder3.append(str2);
                        edit.putString(stringBuilder3.toString(), Base64.encodeToString(rawTransaction.inMsg.bodyHash, 0));
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(stringBuilder4);
                        stringBuilder3.append(str);
                        edit.putString(stringBuilder3.toString(), Base64.encodeToString(rawTransaction.inMsg.message, 0));
                    }
                    if (rawTransaction.outMsgs != null) {
                        length = rawTransaction.outMsgs.length;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(stringBuilder4);
                        stringBuilder2.append("outMsgCount");
                        edit.putInt(stringBuilder2.toString(), length);
                        for (int i4 = 0; i4 < length; i4++) {
                            StringBuilder stringBuilder8 = new StringBuilder();
                            stringBuilder8.append(stringBuilder4);
                            stringBuilder8.append("outMsg");
                            stringBuilder8.append(i4);
                            stringBuilder8.append(str7);
                            str = stringBuilder8.toString();
                            StringBuilder stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(str);
                            stringBuilder9.append("source");
                            edit.putString(stringBuilder9.toString(), rawTransaction.outMsgs[i4].source);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(str);
                            stringBuilder9.append("destination");
                            edit.putString(stringBuilder9.toString(), rawTransaction.outMsgs[i4].destination);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(str);
                            stringBuilder9.append("value");
                            edit.putLong(stringBuilder9.toString(), rawTransaction.outMsgs[i4].value);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(str);
                            stringBuilder9.append("fwdFee");
                            edit.putLong(stringBuilder9.toString(), rawTransaction.outMsgs[i4].fwdFee);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(str);
                            stringBuilder9.append("ihrFee");
                            edit.putLong(stringBuilder9.toString(), rawTransaction.outMsgs[i4].ihrFee);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(str);
                            stringBuilder9.append("createdLt");
                            edit.putLong(stringBuilder9.toString(), rawTransaction.outMsgs[i4].createdLt);
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(str);
                            stringBuilder9.append("bodyHash");
                            edit.putString(stringBuilder9.toString(), Base64.encodeToString(rawTransaction.outMsgs[i4].bodyHash, 0));
                            stringBuilder9 = new StringBuilder();
                            stringBuilder9.append(str);
                            stringBuilder9.append("message");
                            edit.putString(stringBuilder9.toString(), Base64.encodeToString(rawTransaction.outMsgs[i4].message, 0));
                        }
                    }
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder4);
                    stringBuilder3.append(str5);
                    edit.putLong(stringBuilder3.toString(), rawTransaction.utime);
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder4);
                    stringBuilder3.append("data");
                    edit.putString(stringBuilder3.toString(), Base64.encodeToString(rawTransaction.data, 0));
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder4);
                    stringBuilder3.append("lt");
                    edit.putLong(stringBuilder3.toString(), rawTransaction.transactionId.lt);
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder4);
                    stringBuilder3.append("hash");
                    edit.putString(stringBuilder3.toString(), Base64.encodeToString(rawTransaction.transactionId.hash, 0));
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder4);
                    stringBuilder3.append("fee");
                    edit.putLong(stringBuilder3.toString(), rawTransaction.fee);
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder4);
                    stringBuilder3.append("storageFee");
                    edit.putLong(stringBuilder3.toString(), rawTransaction.storageFee);
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder4);
                    stringBuilder3.append("otherFee");
                    edit.putLong(stringBuilder3.toString(), rawTransaction.otherFee);
                    i2++;
                }
                min = tonController.pendingTransactions.size();
                edit.putInt("pendingCount", min);
                length = 0;
                while (length < min) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("pending");
                    stringBuilder2.append(length);
                    stringBuilder2.append(str7);
                    String stringBuilder10 = stringBuilder2.toString();
                    RawTransaction rawTransaction2 = (RawTransaction) tonController.pendingTransactions.get(length);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder10);
                    stringBuilder.append(str6);
                    edit.putString(stringBuilder.toString(), rawTransaction2.inMsg.source);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder10);
                    stringBuilder.append(str4);
                    edit.putString(stringBuilder.toString(), rawTransaction2.inMsg.destination);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder10);
                    stringBuilder.append(str3);
                    int i5 = min;
                    edit.putLong(stringBuilder.toString(), rawTransaction2.inMsg.value);
                    StringBuilder stringBuilder11 = new StringBuilder();
                    stringBuilder11.append(stringBuilder10);
                    stringBuilder11.append(str2);
                    edit.putString(stringBuilder11.toString(), Base64.encodeToString(rawTransaction2.inMsg.bodyHash, 0));
                    stringBuilder11 = new StringBuilder();
                    stringBuilder11.append(stringBuilder10);
                    stringBuilder11.append(str);
                    edit.putString(stringBuilder11.toString(), Base64.encodeToString(rawTransaction2.inMsg.message, 0));
                    stringBuilder11 = new StringBuilder();
                    stringBuilder11.append(stringBuilder10);
                    stringBuilder11.append(str5);
                    edit.putLong(stringBuilder11.toString(), rawTransaction2.utime);
                    length++;
                    tonController = this;
                    min = i5;
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
        Object[] objArr = new Object[1];
        CountDownLatch countDownLatch = z ? new CountDownLatch(1) : null;
        this.client.send(function, new -$$Lambda$TonController$v5N5zKbXFLbODGFjRDOM7843r2s(tonLibCallback, function, objArr, countDownLatch), null);
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        return objArr[0];
    }

    static /* synthetic */ void lambda$sendRequest$2(TonLibCallback tonLibCallback, Function function, Object[] objArr, CountDownLatch countDownLatch, Object object) {
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
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ton");
        stringBuilder.append(this.currentAccount);
        this.keyDirectoty = new File(filesDirFixed, stringBuilder.toString());
        this.keyDirectoty.mkdirs();
        boolean z = sendRequest(new Init(new Options(getConfig(), new KeyStoreTypeDirectory(this.keyDirectoty.getAbsolutePath()))), true) instanceof Ok;
        this.initied = z;
        return z;
    }

    private Config getConfig() {
        return new Config(getMessagesController().walletConfig, getMessagesController().walletBlockchainName, true, false);
    }

    /* Access modifiers changed, original: protected */
    public void onTonConfigUpdated() {
        if (this.initied) {
            sendRequest(new OptionsSetConfig(getConfig()), false);
        }
    }

    private void onFinishWalletCreate(String[] strArr, WordsCallback wordsCallback, byte[] bArr, TonApi.Key key) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$iUk-ob6wahaWXuL2VuZK_EKknDQ(this, key, bArr, strArr, wordsCallback));
    }

    public /* synthetic */ void lambda$onFinishWalletCreate$3$TonController(TonApi.Key key, byte[] bArr, String[] strArr, WordsCallback wordsCallback) {
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
        return genericAccountState instanceof GenericAccountStateWallet ? ((GenericAccountStateWallet) genericAccountState).accountState.lastTransactionId : null;
    }

    public static long getBalance(GenericAccountState genericAccountState) {
        long j = genericAccountState instanceof GenericAccountStateRaw ? ((GenericAccountStateRaw) genericAccountState).accountState.balance : genericAccountState instanceof GenericAccountStateTestWallet ? ((GenericAccountStateTestWallet) genericAccountState).accountState.balance : genericAccountState instanceof GenericAccountStateTestGiver ? ((GenericAccountStateTestGiver) genericAccountState).accountState.balance : genericAccountState instanceof GenericAccountStateUninited ? ((GenericAccountStateUninited) genericAccountState).accountState.balance : genericAccountState instanceof GenericAccountStateWallet ? ((GenericAccountStateWallet) genericAccountState).accountState.balance : 0;
        if (j >= 0) {
            return j;
        }
        return 0;
    }

    public boolean isValidWalletAddress(String str) {
        return sendRequest(new UnpackAccountAddress(str), true) instanceof UnpackedAccountAddress;
    }

    public void isKeyStoreInvalidated(BooleanCallback booleanCallback) {
        Utilities.globalQueue.postRunnable(new -$$Lambda$TonController$vCQCM8brXT-96q69kGpxplSzY7s(this, booleanCallback));
    }

    public /* synthetic */ void lambda$isKeyStoreInvalidated$5$TonController(BooleanCallback booleanCallback) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$3uIz0mTgTAuBU5pCmZJJOhE_7YY(booleanCallback, initCipher(2) == 2));
    }

    public void createWallet(String[] strArr, boolean z, WordsCallback wordsCallback, ErrorCallback errorCallback) {
        getConnectionsManager().sendRequest(new TL_wallet_getKeySecretSalt(), new -$$Lambda$TonController$Sp7YTFNvZxWEIiCU697PpZ7K9s8(this, errorCallback, z, strArr, wordsCallback));
    }

    public /* synthetic */ void lambda$createWallet$13$TonController(ErrorCallback errorCallback, boolean z, String[] strArr, WordsCallback wordsCallback, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_wallet_secretSalt) {
            Utilities.globalQueue.postRunnable(new -$$Lambda$TonController$XaUipB1ZHuqlXTVv0aidGpEzZzw(this, tLObject, errorCallback, z, strArr, wordsCallback));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$j-02fxwQTvT3M7lq3c6yrY1npPM(errorCallback));
        }
    }

    public /* synthetic */ void lambda$null$11$TonController(TLObject tLObject, ErrorCallback errorCallback, boolean z, String[] strArr, WordsCallback wordsCallback) {
        TL_wallet_secretSalt tL_wallet_secretSalt = (TL_wallet_secretSalt) tLObject;
        if (initTonLib()) {
            sendRequest(new DeleteAllKeys(), true);
            if (keyStore == null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$0Qs3PCw-0U216Kbo9EpY5a0gfUU(errorCallback));
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
                byte[] bArr = new byte[64];
                byte[] bArr2 = new byte[32];
                Utilities.random.nextBytes(bArr);
                Utilities.random.nextBytes(bArr2);
                System.arraycopy(tL_wallet_secretSalt.salt, 0, bArr, 32, 32);
                Arrays.fill(tL_wallet_secretSalt.salt, (byte) 0);
                if (strArr == null) {
                    sendRequest = sendRequest(new CreateNewKey(bArr, new byte[0], bArr2), true);
                } else {
                    sendRequest = sendRequest(new ImportKey(bArr, new byte[0], new ExportedKey(strArr)), true);
                }
                if (sendRequest instanceof TonApi.Key) {
                    TonApi.Key key = (TonApi.Key) sendRequest;
                    if (strArr == null) {
                        Object sendRequest2 = sendRequest(new ExportKey(new InputKey(key, bArr)), true);
                        if (sendRequest2 instanceof ExportedKey) {
                            onFinishWalletCreate(((ExportedKey) sendRequest2).wordList, wordsCallback, bArr, key);
                        } else {
                            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$o6TLWvPOQs_Ed6E-tlxgN0EmvOk(this, errorCallback, sendRequest2));
                        }
                    } else {
                        onFinishWalletCreate(null, wordsCallback, bArr, key);
                    }
                } else {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$nol4SDV7Xq2g28zLuViVPJF_Hd8(this, errorCallback, sendRequest));
                }
            } else {
                AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$A_GCvar_k9IFPgwfloPcNNJckftE(errorCallback));
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$5ff7B1hIQjt-6fKSdaIpimgRCNM(errorCallback));
    }

    public /* synthetic */ void lambda$null$8$TonController(ErrorCallback errorCallback, Object obj) {
        errorCallback.run("TONLIB_FAIL", getTonApiErrorSafe(obj));
    }

    public /* synthetic */ void lambda$null$9$TonController(ErrorCallback errorCallback, Object obj) {
        errorCallback.run("TONLIB_FAIL", getTonApiErrorSafe(obj));
    }

    public void setUserPasscode(String str, int i, Runnable runnable) {
        Utilities.globalQueue.postRunnable(new -$$Lambda$TonController$VP44BxohUzVQ09a8SeVN_gkBKFk(this, i, str, runnable));
    }

    public /* synthetic */ void lambda$setUserPasscode$14$TonController(int i, String str, Runnable runnable) {
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x006d */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    public static void shareBitmap(android.app.Activity r4, android.view.View r5, java.lang.String r6) {
        /*
        r5 = (android.widget.ImageView) r5;	 Catch:{ Exception -> 0x008f }
        r5 = r5.getDrawable();	 Catch:{ Exception -> 0x008f }
        r5 = (android.graphics.drawable.BitmapDrawable) r5;	 Catch:{ Exception -> 0x008f }
        r0 = new java.io.File;	 Catch:{ Exception -> 0x008f }
        r1 = 4;
        r1 = org.telegram.messenger.FileLoader.getDirectory(r1);	 Catch:{ Exception -> 0x008f }
        r2 = "sharing/";
        r0.<init>(r1, r2);	 Catch:{ Exception -> 0x008f }
        r0.mkdirs();	 Catch:{ Exception -> 0x008f }
        r1 = new java.io.File;	 Catch:{ Exception -> 0x008f }
        r2 = "qr.jpg";
        r1.<init>(r0, r2);	 Catch:{ Exception -> 0x008f }
        r0 = android.net.Uri.fromFile(r1);	 Catch:{ Exception -> 0x008f }
        r0 = org.telegram.messenger.AndroidUtilities.isInternalUri(r0);	 Catch:{ Exception -> 0x008f }
        if (r0 == 0) goto L_0x0029;
    L_0x0028:
        return;
    L_0x0029:
        r0 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x008f }
        r2 = r1.getAbsolutePath();	 Catch:{ Exception -> 0x008f }
        r0.<init>(r2);	 Catch:{ Exception -> 0x008f }
        r5 = r5.getBitmap();	 Catch:{ Exception -> 0x008f }
        r2 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x008f }
        r3 = 87;
        r5.compress(r2, r3, r0);	 Catch:{ Exception -> 0x008f }
        r0.close();	 Catch:{ Exception -> 0x008f }
        r5 = new android.content.Intent;	 Catch:{ Exception -> 0x008f }
        r0 = "android.intent.action.SEND";
        r5.<init>(r0);	 Catch:{ Exception -> 0x008f }
        r0 = "image/jpeg";
        r5.setType(r0);	 Catch:{ Exception -> 0x008f }
        r0 = android.text.TextUtils.isEmpty(r6);	 Catch:{ Exception -> 0x008f }
        if (r0 != 0) goto L_0x0057;
    L_0x0052:
        r0 = "android.intent.extra.TEXT";
        r5.putExtra(r0, r6);	 Catch:{ Exception -> 0x008f }
    L_0x0057:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x008f }
        r0 = 24;
        r2 = "android.intent.extra.STREAM";
        if (r6 < r0) goto L_0x0075;
    L_0x005f:
        r6 = "org.telegram.messenger.beta.provider";
        r6 = androidx.core.content.FileProvider.getUriForFile(r4, r6, r1);	 Catch:{ Exception -> 0x006d }
        r5.putExtra(r2, r6);	 Catch:{ Exception -> 0x006d }
        r6 = 1;
        r5.setFlags(r6);	 Catch:{ Exception -> 0x006d }
        goto L_0x007c;
    L_0x006d:
        r6 = android.net.Uri.fromFile(r1);	 Catch:{ Exception -> 0x008f }
        r5.putExtra(r2, r6);	 Catch:{ Exception -> 0x008f }
        goto L_0x007c;
    L_0x0075:
        r6 = android.net.Uri.fromFile(r1);	 Catch:{ Exception -> 0x008f }
        r5.putExtra(r2, r6);	 Catch:{ Exception -> 0x008f }
    L_0x007c:
        r6 = "WalletShareQr";
        r0 = NUM; // 0x7f0e0bd0 float:1.888117E38 double:1.0531636507E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r0);	 Catch:{ Exception -> 0x008f }
        r5 = android.content.Intent.createChooser(r5, r6);	 Catch:{ Exception -> 0x008f }
        r6 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r4.startActivityForResult(r5, r6);	 Catch:{ Exception -> 0x008f }
        goto L_0x0093;
    L_0x008f:
        r4 = move-exception;
        org.telegram.messenger.FileLog.e(r4);
    L_0x0093:
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
        Object sendRequest = sendRequest(new WalletGetAccountAddress(new WalletInitialAccountState(str)), true);
        if (!(sendRequest instanceof AccountAddress)) {
            return null;
        }
        AccountAddress accountAddress2 = (AccountAddress) sendRequest;
        this.accountAddress = accountAddress2;
        return accountAddress2.accountAddress;
    }

    public void getTransactions(boolean z, InternalTransactionId internalTransactionId, GetTransactionsCallback getTransactionsCallback) {
        sendRequest(new RawGetTransactions(this.accountAddress, internalTransactionId), new -$$Lambda$TonController$xKzbV46Tvstv6HDePaU3KQi61Fc(this, z, getTransactionsCallback));
    }

    public /* synthetic */ void lambda$getTransactions$17$TonController(boolean z, GetTransactionsCallback getTransactionsCallback, Object obj) {
        if (obj instanceof RawTransactions) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$vzgwuH2JLJf2Svz9kAwPm6yC8gw(this, new ArrayList(Arrays.asList(((RawTransactions) obj).transactions)), z, getTransactionsCallback));
        } else if (getTransactionsCallback != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$tSYjV4eYPmxo38iPvsGvtQxADDY(getTransactionsCallback));
        }
    }

    public /* synthetic */ void lambda$null$15$TonController(ArrayList arrayList, boolean z, GetTransactionsCallback getTransactionsCallback) {
        Object obj = null;
        if (!this.pendingTransactions.isEmpty()) {
            int size = this.pendingTransactions.size();
            int i = 0;
            Object obj2 = null;
            while (i < size) {
                int size2 = arrayList.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    RawTransaction rawTransaction = (RawTransaction) this.pendingTransactions.get(i);
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
        if (getTransactionsCallback != null) {
            getTransactionsCallback.run(arrayList);
        }
    }

    private void preloadWallet(String str) {
        if (!this.isPrealodingWallet) {
            this.isPrealodingWallet = true;
            getWalletAddress(str);
            getAccountState(new -$$Lambda$TonController$4sUFBUpjSKVJQ64xFw7yS9hJtZg(this));
        }
    }

    public /* synthetic */ void lambda$preloadWallet$19$TonController(GenericAccountState genericAccountState) {
        if (genericAccountState == null) {
            this.isPrealodingWallet = false;
            getNotificationCenter().postNotificationName(NotificationCenter.finishedWalletPreloading, new Object[0]);
            return;
        }
        getTransactions(true, getLastTransactionId(genericAccountState), new -$$Lambda$TonController$CRZLVtl9XkpTwMIVgNlCLASSNAMEkpyoc(this));
    }

    public /* synthetic */ void lambda$null$18$TonController(ArrayList arrayList) {
        this.isPrealodingWallet = false;
        getNotificationCenter().postNotificationName(NotificationCenter.finishedWalletPreloading, new Object[0]);
    }

    public boolean isPreloadingWallet() {
        return this.isPrealodingWallet;
    }

    public void getAccountState(AccountStateCallback accountStateCallback) {
        sendRequest(new GenericGetAccountState(this.accountAddress), new -$$Lambda$TonController$JFjP4qdxkHS_WpJpPFkh7yR5SjQ(this, accountStateCallback));
    }

    public /* synthetic */ void lambda$getAccountState$22$TonController(AccountStateCallback accountStateCallback, Object obj) {
        if (obj instanceof GenericAccountState) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$wnGpkvpHsIHyzkpFHXBflXm6OU8(this, accountStateCallback, obj));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$lnmcLT7QojSLk-gv4lDyeXZpSI0(accountStateCallback));
        }
    }

    public /* synthetic */ void lambda$null$20$TonController(AccountStateCallback accountStateCallback, Object obj) {
        GenericAccountState genericAccountState = (GenericAccountState) obj;
        this.cachedAccountState = genericAccountState;
        accountStateCallback.run(genericAccountState);
    }

    private InputKey decryptTonData(String str, Cipher cipher, Runnable runnable, ErrorCallback errorCallback, boolean z) {
        ErrorCallback errorCallback2 = errorCallback;
        UserConfig userConfig = getUserConfig();
        byte[] decrypt = decrypt(userConfig.tonEncryptedData, cipher);
        if (decrypt == null || decrypt.length <= 3) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$HBNfR0kbdSvbRiQ3yhmIB2tL69g(errorCallback2));
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
            return new InputKey(new TonApi.Key(userConfig.tonPublicKey, bArr4), bArr3);
        }
        if (TextUtils.isEmpty(str)) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$3-4CkmOmoT2bO3dO2BASk7HXPCU(errorCallback2));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$2OcyRR2ZKiFvj7wZccoJvASoxN8(errorCallback2));
        }
        return null;
    }

    public void prepareForPasscodeChange(String str, Runnable runnable, ErrorCallback errorCallback) {
        Utilities.globalQueue.postRunnable(new -$$Lambda$TonController$nYm5gjV4EcJ_uWgaKe-pQJbVMEs(this, str, errorCallback, runnable));
    }

    public /* synthetic */ void lambda$prepareForPasscodeChange$26$TonController(String str, ErrorCallback errorCallback, Runnable runnable) {
        if (decryptTonData(str, null, null, errorCallback, true) != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public void getSecretWords(String str, Cipher cipher, WordsCallback wordsCallback, ErrorCallback errorCallback) {
        Utilities.globalQueue.postRunnable(new -$$Lambda$TonController$u-aTIq1Y_1XApO7-ytnq99MZSnU(this, str, cipher, errorCallback, wordsCallback));
    }

    public /* synthetic */ void lambda$getSecretWords$30$TonController(String str, Cipher cipher, ErrorCallback errorCallback, WordsCallback wordsCallback) {
        initTonLib();
        InputKey decryptTonData = decryptTonData(str, cipher, null, errorCallback, false);
        if (decryptTonData != null) {
            sendRequest(new ExportKey(decryptTonData), new -$$Lambda$TonController$xCAZdHQd-0Alzthtt7sYgL1zb9o(this, wordsCallback, errorCallback));
        }
    }

    public /* synthetic */ void lambda$null$29$TonController(WordsCallback wordsCallback, ErrorCallback errorCallback, Object obj) {
        if (obj instanceof ExportedKey) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$uBcsIaLrUJcBgPWHX4NCtqMC4Fw(wordsCallback, (ExportedKey) obj));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$znWQMOv27QrJ-VjmODDBlJTP4jU(this, errorCallback, obj));
        }
    }

    public /* synthetic */ void lambda$null$28$TonController(ErrorCallback errorCallback, Object obj) {
        errorCallback.run("TONLIB_FAIL", getTonApiErrorSafe(obj));
    }

    public void sendGrams(String str, Cipher cipher, InputKey inputKey, String str2, String str3, long j, String str4, Runnable runnable, Runnable runnable2, Runnable runnable3, DangerousCallback dangerousCallback, ErrorCallback errorCallback) {
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        -$$Lambda$TonController$LACzhOYWYTXLVgjY9gdId5UtvFo -__lambda_toncontroller_laczhoywytxlvgjy9gdid5utvfo = r1;
        -$$Lambda$TonController$LACzhOYWYTXLVgjY9gdId5UtvFo -__lambda_toncontroller_laczhoywytxlvgjy9gdid5utvfo2 = new -$$Lambda$TonController$LACzhOYWYTXLVgjY9gdId5UtvFo(this, inputKey, str, cipher, runnable, errorCallback, str2, str3, j, str4, runnable3, runnable2, dangerousCallback);
        dispatchQueue.postRunnable(-__lambda_toncontroller_laczhoywytxlvgjy9gdid5utvfo);
    }

    public /* synthetic */ void lambda$sendGrams$34$TonController(InputKey inputKey, String str, Cipher cipher, Runnable runnable, ErrorCallback errorCallback, String str2, String str3, long j, String str4, Runnable runnable2, Runnable runnable3, DangerousCallback dangerousCallback) {
        InputKey decryptTonData = inputKey == null ? decryptTonData(str, cipher, runnable, errorCallback, false) : inputKey;
        if (decryptTonData != null) {
            long j2 = j;
            Function genericSendGrams = new GenericSendGrams(decryptTonData, new AccountAddress(str2), new AccountAddress(str3), j2, 0, true, str4 != null ? str4.getBytes() : new byte[0]);
            TonLibCallback -__lambda_toncontroller_qiferhhjkja_l70bdu_6bt4u0mc = new -$$Lambda$TonController$QIFeRhHjkJA_l70Bdu_6bT4U0mc(this, str2, str3, j2, str4, runnable2, runnable3, dangerousCallback, decryptTonData, errorCallback);
            sendRequest(genericSendGrams, -__lambda_toncontroller_qiferhhjkja_l70bdu_6bt4u0mc);
        }
    }

    public /* synthetic */ void lambda$null$33$TonController(String str, String str2, long j, String str3, Runnable runnable, Runnable runnable2, DangerousCallback dangerousCallback, InputKey inputKey, ErrorCallback errorCallback, Object obj) {
        if (obj instanceof SendGramsResult) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$MwWG4jo1UnBeZTlf4c_Dd_4QCcA(this, obj, str, str2, j, str3, runnable, runnable2));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TonController$TkyDfizMrTGiHx3pu7apJKfwdmM(this, obj, dangerousCallback, inputKey, errorCallback));
        }
    }

    public /* synthetic */ void lambda$null$31$TonController(Object obj, String str, String str2, long j, String str3, Runnable runnable, Runnable runnable2) {
        SendGramsResult sendGramsResult = (SendGramsResult) obj;
        RawMessage rawMessage = new RawMessage();
        rawMessage.source = str;
        rawMessage.destination = str2;
        rawMessage.value = -j;
        rawMessage.message = str3 != null ? str3.getBytes() : new byte[0];
        rawMessage.bodyHash = sendGramsResult.bodyHash;
        this.pendingTransactions.add(0, new RawTransaction(System.currentTimeMillis() / 1000, new byte[0], new InternalTransactionId(), 0, 0, 0, rawMessage, new RawMessage[0]));
        saveCache();
        getNotificationCenter().postNotificationName(NotificationCenter.walletPendingTransactionsChanged, new Object[0]);
        this.onPendingTransactionsEmpty = runnable;
        runnable2.run();
    }

    public /* synthetic */ void lambda$null$32$TonController(Object obj, DangerousCallback dangerousCallback, InputKey inputKey, ErrorCallback errorCallback) {
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
            getAccountState(new -$$Lambda$TonController$RJupFG7ruVYFOeDFSlIAU1gcAD4(this));
        }
    }

    public /* synthetic */ void lambda$runShortPolling$36$TonController(GenericAccountState genericAccountState) {
        if (genericAccountState != null) {
            getTransactions(true, getLastTransactionId(genericAccountState), new -$$Lambda$TonController$UKUZGf4hmg7yXgAQn7dXPjFBr7A(this));
            return;
        }
        this.shortPollRunnable = null;
        this.shortPollingInProgress = false;
        scheduleShortPoll();
    }

    public /* synthetic */ void lambda$null$35$TonController(ArrayList arrayList) {
        this.shortPollRunnable = null;
        this.shortPollingInProgress = false;
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

    public static CharSequence formatCurrency(long j) {
        if (j == 0) {
            return "0";
        }
        Locale locale = Locale.US;
        Object[] objArr = new Object[1];
        double d = (double) j;
        Double.isNaN(d);
        objArr[0] = Double.valueOf(d / 1.0E9d);
        StringBuilder stringBuilder = new StringBuilder(String.format(locale, "%.9f", objArr));
        while (stringBuilder.length() > 1 && stringBuilder.charAt(stringBuilder.length() - 1) == '0' && stringBuilder.charAt(stringBuilder.length() - 2) != '.') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder;
    }
}
