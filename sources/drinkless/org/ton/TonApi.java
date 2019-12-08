package drinkless.org.ton;

public class TonApi {

    public static abstract class Object {
        public abstract int getConstructor();

        public native String toString();
    }

    public static class AccountAddress extends Object {
        public static final int CONSTRUCTOR = NUM;
        public String accountAddress;

        public int getConstructor() {
            return NUM;
        }

        public AccountAddress(String str) {
            this.accountAddress = str;
        }
    }

    public static class Bip39Hints extends Object {
        public static final int CONSTRUCTOR = NUM;
        public String[] words;

        public int getConstructor() {
            return NUM;
        }

        public Bip39Hints(String[] strArr) {
            this.words = strArr;
        }
    }

    public static class Config extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public String blockchainName;
        public String config;
        public boolean ignoreCache;
        public boolean useCallbacksForNetwork;

        public int getConstructor() {
            return -NUM;
        }

        public Config(String str, String str2, boolean z, boolean z2) {
            this.config = str;
            this.blockchainName = str2;
            this.useCallbacksForNetwork = z;
            this.ignoreCache = z2;
        }
    }

    public static class Data extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public byte[] bytes;

        public int getConstructor() {
            return -NUM;
        }

        public Data(byte[] bArr) {
            this.bytes = bArr;
        }
    }

    public static class Error extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public int code;
        public String message;

        public int getConstructor() {
            return -NUM;
        }

        public Error(int i, String str) {
            this.code = i;
            this.message = str;
        }
    }

    public static class ExportedEncryptedKey extends Object {
        public static final int CONSTRUCTOR = NUM;
        public byte[] data;

        public int getConstructor() {
            return NUM;
        }

        public ExportedEncryptedKey(byte[] bArr) {
            this.data = bArr;
        }
    }

    public static class ExportedKey extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public String[] wordList;

        public int getConstructor() {
            return -NUM;
        }

        public ExportedKey(String[] strArr) {
            this.wordList = strArr;
        }
    }

    public static class ExportedPemKey extends Object {
        public static final int CONSTRUCTOR = NUM;
        public String pem;

        public int getConstructor() {
            return NUM;
        }

        public ExportedPemKey(String str) {
            this.pem = str;
        }
    }

    public static class Fees extends Object {
        public static final int CONSTRUCTOR = NUM;
        public long fwdFee;
        public long gasFee;
        public long inFwdFee;
        public long storageFee;

        public int getConstructor() {
            return NUM;
        }

        public Fees(long j, long j2, long j3, long j4) {
            this.inFwdFee = j;
            this.storageFee = j2;
            this.gasFee = j3;
            this.fwdFee = j4;
        }
    }

    public static abstract class Function extends Object {
        public native String toString();
    }

    public static abstract class GenericAccountState extends Object {
    }

    public static abstract class InputKey extends Object {
    }

    public static class InternalTransactionId extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public byte[] hash;
        public long lt;

        public int getConstructor() {
            return -NUM;
        }

        public InternalTransactionId(long j, byte[] bArr) {
            this.lt = j;
            this.hash = bArr;
        }
    }

    public static class Key extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public String publicKey;
        public byte[] secret;

        public int getConstructor() {
            return -NUM;
        }

        public Key(String str, byte[] bArr) {
            this.publicKey = str;
            this.secret = bArr;
        }
    }

    public static abstract class KeyStoreType extends Object {
    }

    public static class LiteServerInfo extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public long capabilities;
        public long now;
        public int version;

        public int getConstructor() {
            return -NUM;
        }

        public LiteServerInfo(long j, int i, long j2) {
            this.now = j;
            this.version = i;
            this.capabilities = j2;
        }
    }

    public static abstract class LogStream extends Object {
    }

    public static class LogTags extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public String[] tags;

        public int getConstructor() {
            return -NUM;
        }

        public LogTags(String[] strArr) {
            this.tags = strArr;
        }
    }

    public static class LogVerbosityLevel extends Object {
        public static final int CONSTRUCTOR = NUM;
        public int verbosityLevel;

        public int getConstructor() {
            return NUM;
        }

        public LogVerbosityLevel(int i) {
            this.verbosityLevel = i;
        }
    }

    public static class Ok extends Object {
        public static final int CONSTRUCTOR = -NUM;

        public int getConstructor() {
            return -NUM;
        }
    }

    public static class Options extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public Config config;
        public KeyStoreType keystoreType;

        public int getConstructor() {
            return -NUM;
        }

        public Options(Config config, KeyStoreType keyStoreType) {
            this.config = config;
            this.keystoreType = keyStoreType;
        }
    }

    public static class OptionsConfigInfo extends Object {
        public static final int CONSTRUCTOR = NUM;
        public long defaultWalletId;

        public int getConstructor() {
            return NUM;
        }

        public OptionsConfigInfo(long j) {
            this.defaultWalletId = j;
        }
    }

    public static class QueryFees extends Object {
        public static final int CONSTRUCTOR = NUM;
        public Fees destinationFees;
        public Fees sourceFees;

        public int getConstructor() {
            return NUM;
        }

        public QueryFees(Fees fees, Fees fees2) {
            this.sourceFees = fees;
            this.destinationFees = fees2;
        }
    }

    public static class QueryInfo extends Object {
        public static final int CONSTRUCTOR = NUM;
        public byte[] bodyHash;
        public long id;
        public long validUntil;

        public int getConstructor() {
            return NUM;
        }

        public QueryInfo(long j, long j2, byte[] bArr) {
            this.id = j;
            this.validUntil = j2;
            this.bodyHash = bArr;
        }
    }

    public static class RawAccountState extends Object {
        public static final int CONSTRUCTOR = NUM;
        public long balance;
        public byte[] code;
        public byte[] data;
        public byte[] frozenHash;
        public InternalTransactionId lastTransactionId;
        public long syncUtime;

        public int getConstructor() {
            return NUM;
        }

        public RawAccountState(long j, byte[] bArr, byte[] bArr2, InternalTransactionId internalTransactionId, byte[] bArr3, long j2) {
            this.balance = j;
            this.code = bArr;
            this.data = bArr2;
            this.lastTransactionId = internalTransactionId;
            this.frozenHash = bArr3;
            this.syncUtime = j2;
        }
    }

    public static class RawInitialAccountState extends Object {
        public static final int CONSTRUCTOR = NUM;
        public byte[] code;
        public byte[] data;

        public int getConstructor() {
            return NUM;
        }

        public RawInitialAccountState(byte[] bArr, byte[] bArr2) {
            this.code = bArr;
            this.data = bArr2;
        }
    }

    public static class RawMessage extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public byte[] bodyHash;
        public long createdLt;
        public String destination;
        public long fwdFee;
        public long ihrFee;
        public byte[] message;
        public String source;
        public long value;

        public int getConstructor() {
            return -NUM;
        }

        public RawMessage(String str, String str2, long j, long j2, long j3, long j4, byte[] bArr, byte[] bArr2) {
            this.source = str;
            this.destination = str2;
            this.value = j;
            this.fwdFee = j2;
            this.ihrFee = j3;
            this.createdLt = j4;
            this.bodyHash = bArr;
            this.message = bArr2;
        }
    }

    public static class RawTransaction extends Object {
        public static final int CONSTRUCTOR = NUM;
        public byte[] data;
        public long fee;
        public RawMessage inMsg;
        public long otherFee;
        public RawMessage[] outMsgs;
        public long storageFee;
        public InternalTransactionId transactionId;
        public long utime;

        public int getConstructor() {
            return NUM;
        }

        public RawTransaction(long j, byte[] bArr, InternalTransactionId internalTransactionId, long j2, long j3, long j4, RawMessage rawMessage, RawMessage[] rawMessageArr) {
            this.utime = j;
            this.data = bArr;
            this.transactionId = internalTransactionId;
            this.fee = j2;
            this.storageFee = j3;
            this.otherFee = j4;
            this.inMsg = rawMessage;
            this.outMsgs = rawMessageArr;
        }
    }

    public static class RawTransactions extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public InternalTransactionId previousTransactionId;
        public RawTransaction[] transactions;

        public int getConstructor() {
            return -NUM;
        }

        public RawTransactions(RawTransaction[] rawTransactionArr, InternalTransactionId internalTransactionId) {
            this.transactions = rawTransactionArr;
            this.previousTransactionId = internalTransactionId;
        }
    }

    public static class SendGramsResult extends Object {
        public static final int CONSTRUCTOR = NUM;
        public byte[] bodyHash;
        public long sentUntil;

        public int getConstructor() {
            return NUM;
        }

        public SendGramsResult(long j, byte[] bArr) {
            this.sentUntil = j;
            this.bodyHash = bArr;
        }
    }

    public static class SmcInfo extends Object {
        public static final int CONSTRUCTOR = NUM;
        public long id;

        public int getConstructor() {
            return NUM;
        }

        public SmcInfo(long j) {
            this.id = j;
        }
    }

    public static abstract class SmcMethodId extends Object {
    }

    public static class SmcRunResult extends Object {
        public static final int CONSTRUCTOR = NUM;
        public int exitCode;
        public long gasUsed;
        public TvmStackEntry[] stack;

        public int getConstructor() {
            return NUM;
        }

        public SmcRunResult(long j, TvmStackEntry[] tvmStackEntryArr, int i) {
            this.gasUsed = j;
            this.stack = tvmStackEntryArr;
            this.exitCode = i;
        }
    }

    public static abstract class SyncState extends Object {
    }

    public static class TestGiverAccountState extends Object {
        public static final int CONSTRUCTOR = NUM;
        public long balance;
        public InternalTransactionId lastTransactionId;
        public int seqno;
        public long syncUtime;

        public int getConstructor() {
            return NUM;
        }

        public TestGiverAccountState(long j, int i, InternalTransactionId internalTransactionId, long j2) {
            this.balance = j;
            this.seqno = i;
            this.lastTransactionId = internalTransactionId;
            this.syncUtime = j2;
        }
    }

    public static class TestWalletAccountState extends Object {
        public static final int CONSTRUCTOR = NUM;
        public long balance;
        public InternalTransactionId lastTransactionId;
        public int seqno;
        public long syncUtime;

        public int getConstructor() {
            return NUM;
        }

        public TestWalletAccountState(long j, int i, InternalTransactionId internalTransactionId, long j2) {
            this.balance = j;
            this.seqno = i;
            this.lastTransactionId = internalTransactionId;
            this.syncUtime = j2;
        }
    }

    public static class TestWalletInitialAccountState extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public String publicKey;

        public int getConstructor() {
            return -NUM;
        }

        public TestWalletInitialAccountState(String str) {
            this.publicKey = str;
        }
    }

    public static class TvmCell extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public String bytes;

        public int getConstructor() {
            return -NUM;
        }

        public TvmCell(String str) {
            this.bytes = str;
        }
    }

    public static class TvmNumberDecimal extends Object {
        public static final int CONSTRUCTOR = NUM;
        public String number;

        public int getConstructor() {
            return NUM;
        }

        public TvmNumberDecimal(String str) {
            this.number = str;
        }
    }

    public static class TvmSlice extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public String bytes;

        public int getConstructor() {
            return -NUM;
        }

        public TvmSlice(String str) {
            this.bytes = str;
        }
    }

    public static abstract class TvmStackEntry extends Object {
    }

    public static class UninitedAccountState extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public long balance;
        public byte[] frozenHash;
        public InternalTransactionId lastTransactionId;
        public long syncUtime;

        public int getConstructor() {
            return -NUM;
        }

        public UninitedAccountState(long j, InternalTransactionId internalTransactionId, byte[] bArr, long j2) {
            this.balance = j;
            this.lastTransactionId = internalTransactionId;
            this.frozenHash = bArr;
            this.syncUtime = j2;
        }
    }

    public static class UnpackedAccountAddress extends Object {
        public static final int CONSTRUCTOR = NUM;
        public byte[] addr;
        public boolean bounceable;
        public boolean testnet;
        public int workchainId;

        public int getConstructor() {
            return NUM;
        }

        public UnpackedAccountAddress(int i, boolean z, boolean z2, byte[] bArr) {
            this.workchainId = i;
            this.bounceable = z;
            this.testnet = z2;
            this.addr = bArr;
        }
    }

    public static abstract class Update extends Object {
    }

    public static class WalletAccountState extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public long balance;
        public InternalTransactionId lastTransactionId;
        public int seqno;
        public long syncUtime;

        public int getConstructor() {
            return -NUM;
        }

        public WalletAccountState(long j, int i, InternalTransactionId internalTransactionId, long j2) {
            this.balance = j;
            this.seqno = i;
            this.lastTransactionId = internalTransactionId;
            this.syncUtime = j2;
        }
    }

    public static class WalletInitialAccountState extends Object {
        public static final int CONSTRUCTOR = -NUM;
        public String publicKey;

        public int getConstructor() {
            return -NUM;
        }

        public WalletInitialAccountState(String str) {
            this.publicKey = str;
        }
    }

    public static class WalletV3AccountState extends Object {
        public static final int CONSTRUCTOR = NUM;
        public long balance;
        public InternalTransactionId lastTransactionId;
        public int seqno;
        public long syncUtime;
        public long walletId;

        public int getConstructor() {
            return NUM;
        }

        public WalletV3AccountState(long j, long j2, int i, InternalTransactionId internalTransactionId, long j3) {
            this.balance = j;
            this.walletId = j2;
            this.seqno = i;
            this.lastTransactionId = internalTransactionId;
            this.syncUtime = j3;
        }
    }

    public static class WalletV3InitialAccountState extends Object {
        public static final int CONSTRUCTOR = NUM;
        public String publicKey;
        public long walletId;

        public int getConstructor() {
            return NUM;
        }

        public WalletV3InitialAccountState(String str, long j) {
            this.publicKey = str;
            this.walletId = j;
        }
    }

    public static class AddLogMessage extends Function {
        public static final int CONSTRUCTOR = NUM;
        public String text;
        public int verbosityLevel;

        public int getConstructor() {
            return NUM;
        }

        public AddLogMessage(int i, String str) {
            this.verbosityLevel = i;
            this.text = str;
        }
    }

    public static class ChangeLocalPassword extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public InputKey inputKey;
        public byte[] newLocalPassword;

        public int getConstructor() {
            return -NUM;
        }

        public ChangeLocalPassword(InputKey inputKey, byte[] bArr) {
            this.inputKey = inputKey;
            this.newLocalPassword = bArr;
        }
    }

    public static class Close extends Function {
        public static final int CONSTRUCTOR = -NUM;

        public int getConstructor() {
            return -NUM;
        }
    }

    public static class CreateNewKey extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public byte[] localPassword;
        public byte[] mnemonicPassword;
        public byte[] randomExtraSeed;

        public int getConstructor() {
            return -NUM;
        }

        public CreateNewKey(byte[] bArr, byte[] bArr2, byte[] bArr3) {
            this.localPassword = bArr;
            this.mnemonicPassword = bArr2;
            this.randomExtraSeed = bArr3;
        }
    }

    public static class Decrypt extends Function {
        public static final int CONSTRUCTOR = NUM;
        public byte[] encryptedData;
        public byte[] secret;

        public int getConstructor() {
            return NUM;
        }

        public Decrypt(byte[] bArr, byte[] bArr2) {
            this.encryptedData = bArr;
            this.secret = bArr2;
        }
    }

    public static class DeleteAllKeys extends Function {
        public static final int CONSTRUCTOR = NUM;

        public int getConstructor() {
            return NUM;
        }
    }

    public static class DeleteKey extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public Key key;

        public int getConstructor() {
            return -NUM;
        }

        public DeleteKey(Key key) {
            this.key = key;
        }
    }

    public static class Encrypt extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public byte[] decryptedData;
        public byte[] secret;

        public int getConstructor() {
            return -NUM;
        }

        public Encrypt(byte[] bArr, byte[] bArr2) {
            this.decryptedData = bArr;
            this.secret = bArr2;
        }
    }

    public static class ExportEncryptedKey extends Function {
        public static final int CONSTRUCTOR = NUM;
        public InputKey inputKey;
        public byte[] keyPassword;

        public int getConstructor() {
            return NUM;
        }

        public ExportEncryptedKey(InputKey inputKey, byte[] bArr) {
            this.inputKey = inputKey;
            this.keyPassword = bArr;
        }
    }

    public static class ExportKey extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public InputKey inputKey;

        public int getConstructor() {
            return -NUM;
        }

        public ExportKey(InputKey inputKey) {
            this.inputKey = inputKey;
        }
    }

    public static class ExportPemKey extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public InputKey inputKey;
        public byte[] keyPassword;

        public int getConstructor() {
            return -NUM;
        }

        public ExportPemKey(InputKey inputKey, byte[] bArr) {
            this.inputKey = inputKey;
            this.keyPassword = bArr;
        }
    }

    public static class GenericAccountStateRaw extends GenericAccountState {
        public static final int CONSTRUCTOR = -NUM;
        public RawAccountState accountState;

        public int getConstructor() {
            return -NUM;
        }

        public GenericAccountStateRaw(RawAccountState rawAccountState) {
            this.accountState = rawAccountState;
        }
    }

    public static class GenericAccountStateTestGiver extends GenericAccountState {
        public static final int CONSTRUCTOR = NUM;
        public TestGiverAccountState accountState;

        public int getConstructor() {
            return NUM;
        }

        public GenericAccountStateTestGiver(TestGiverAccountState testGiverAccountState) {
            this.accountState = testGiverAccountState;
        }
    }

    public static class GenericAccountStateTestWallet extends GenericAccountState {
        public static final int CONSTRUCTOR = -NUM;
        public TestWalletAccountState accountState;

        public int getConstructor() {
            return -NUM;
        }

        public GenericAccountStateTestWallet(TestWalletAccountState testWalletAccountState) {
            this.accountState = testWalletAccountState;
        }
    }

    public static class GenericAccountStateUninited extends GenericAccountState {
        public static final int CONSTRUCTOR = -NUM;
        public UninitedAccountState accountState;

        public int getConstructor() {
            return -NUM;
        }

        public GenericAccountStateUninited(UninitedAccountState uninitedAccountState) {
            this.accountState = uninitedAccountState;
        }
    }

    public static class GenericAccountStateWallet extends GenericAccountState {
        public static final int CONSTRUCTOR = NUM;
        public WalletAccountState accountState;

        public int getConstructor() {
            return NUM;
        }

        public GenericAccountStateWallet(WalletAccountState walletAccountState) {
            this.accountState = walletAccountState;
        }
    }

    public static class GenericAccountStateWalletV3 extends GenericAccountState {
        public static final int CONSTRUCTOR = -NUM;
        public WalletV3AccountState accountState;

        public int getConstructor() {
            return -NUM;
        }

        public GenericAccountStateWalletV3(WalletV3AccountState walletV3AccountState) {
            this.accountState = walletV3AccountState;
        }
    }

    public static class GenericCreateSendGramsQuery extends Function {
        public static final int CONSTRUCTOR = NUM;
        public boolean allowSendToUninited;
        public long amount;
        public AccountAddress destination;
        public byte[] message;
        public InputKey privateKey;
        public AccountAddress source;
        public int timeout;

        public int getConstructor() {
            return NUM;
        }

        public GenericCreateSendGramsQuery(InputKey inputKey, AccountAddress accountAddress, AccountAddress accountAddress2, long j, int i, boolean z, byte[] bArr) {
            this.privateKey = inputKey;
            this.source = accountAddress;
            this.destination = accountAddress2;
            this.amount = j;
            this.timeout = i;
            this.allowSendToUninited = z;
            this.message = bArr;
        }
    }

    public static class GenericGetAccountState extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public AccountAddress accountAddress;

        public int getConstructor() {
            return -NUM;
        }

        public GenericGetAccountState(AccountAddress accountAddress) {
            this.accountAddress = accountAddress;
        }
    }

    public static class GenericSendGrams extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public boolean allowSendToUninited;
        public long amount;
        public AccountAddress destination;
        public byte[] message;
        public InputKey privateKey;
        public AccountAddress source;
        public int timeout;

        public int getConstructor() {
            return -NUM;
        }

        public GenericSendGrams(InputKey inputKey, AccountAddress accountAddress, AccountAddress accountAddress2, long j, int i, boolean z, byte[] bArr) {
            this.privateKey = inputKey;
            this.source = accountAddress;
            this.destination = accountAddress2;
            this.amount = j;
            this.timeout = i;
            this.allowSendToUninited = z;
            this.message = bArr;
        }
    }

    public static class GetBip39Hints extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public String prefix;

        public int getConstructor() {
            return -NUM;
        }

        public GetBip39Hints(String str) {
            this.prefix = str;
        }
    }

    public static class GetLogStream extends Function {
        public static final int CONSTRUCTOR = NUM;

        public int getConstructor() {
            return NUM;
        }
    }

    public static class GetLogTagVerbosityLevel extends Function {
        public static final int CONSTRUCTOR = NUM;
        public String tag;

        public int getConstructor() {
            return NUM;
        }

        public GetLogTagVerbosityLevel(String str) {
            this.tag = str;
        }
    }

    public static class GetLogTags extends Function {
        public static final int CONSTRUCTOR = -NUM;

        public int getConstructor() {
            return -NUM;
        }
    }

    public static class GetLogVerbosityLevel extends Function {
        public static final int CONSTRUCTOR = NUM;

        public int getConstructor() {
            return NUM;
        }
    }

    public static class ImportEncryptedKey extends Function {
        public static final int CONSTRUCTOR = NUM;
        public ExportedEncryptedKey exportedEncryptedKey;
        public byte[] keyPassword;
        public byte[] localPassword;

        public int getConstructor() {
            return NUM;
        }

        public ImportEncryptedKey(byte[] bArr, byte[] bArr2, ExportedEncryptedKey exportedEncryptedKey) {
            this.localPassword = bArr;
            this.keyPassword = bArr2;
            this.exportedEncryptedKey = exportedEncryptedKey;
        }
    }

    public static class ImportKey extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public ExportedKey exportedKey;
        public byte[] localPassword;
        public byte[] mnemonicPassword;

        public int getConstructor() {
            return -NUM;
        }

        public ImportKey(byte[] bArr, byte[] bArr2, ExportedKey exportedKey) {
            this.localPassword = bArr;
            this.mnemonicPassword = bArr2;
            this.exportedKey = exportedKey;
        }
    }

    public static class ImportPemKey extends Function {
        public static final int CONSTRUCTOR = 76385617;
        public ExportedPemKey exportedKey;
        public byte[] keyPassword;
        public byte[] localPassword;

        public int getConstructor() {
            return 76385617;
        }

        public ImportPemKey(byte[] bArr, byte[] bArr2, ExportedPemKey exportedPemKey) {
            this.localPassword = bArr;
            this.keyPassword = bArr2;
            this.exportedKey = exportedPemKey;
        }
    }

    public static class Init extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public Options options;

        public int getConstructor() {
            return -NUM;
        }

        public Init(Options options) {
            this.options = options;
        }
    }

    public static class InputKeyFake extends InputKey {
        public static final int CONSTRUCTOR = -NUM;

        public int getConstructor() {
            return -NUM;
        }
    }

    public static class InputKeyRegular extends InputKey {
        public static final int CONSTRUCTOR = -NUM;
        public Key key;
        public byte[] localPassword;

        public int getConstructor() {
            return -NUM;
        }

        public InputKeyRegular(Key key, byte[] bArr) {
            this.key = key;
            this.localPassword = bArr;
        }
    }

    public static class Kdf extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public int iterations;
        public byte[] password;
        public byte[] salt;

        public int getConstructor() {
            return -NUM;
        }

        public Kdf(byte[] bArr, byte[] bArr2, int i) {
            this.password = bArr;
            this.salt = bArr2;
            this.iterations = i;
        }
    }

    public static class KeyStoreTypeDirectory extends KeyStoreType {
        public static final int CONSTRUCTOR = -NUM;
        public String directory;

        public int getConstructor() {
            return -NUM;
        }

        public KeyStoreTypeDirectory(String str) {
            this.directory = str;
        }
    }

    public static class KeyStoreTypeInMemory extends KeyStoreType {
        public static final int CONSTRUCTOR = -NUM;

        public int getConstructor() {
            return -NUM;
        }
    }

    public static class LiteServerGetInfo extends Function {
        public static final int CONSTRUCTOR = NUM;

        public int getConstructor() {
            return NUM;
        }
    }

    public static class LogStreamDefault extends LogStream {
        public static final int CONSTRUCTOR = NUM;

        public int getConstructor() {
            return NUM;
        }
    }

    public static class LogStreamEmpty extends LogStream {
        public static final int CONSTRUCTOR = -NUM;

        public int getConstructor() {
            return -NUM;
        }
    }

    public static class LogStreamFile extends LogStream {
        public static final int CONSTRUCTOR = -NUM;
        public long maxFileSize;
        public String path;

        public int getConstructor() {
            return -NUM;
        }

        public LogStreamFile(String str, long j) {
            this.path = str;
            this.maxFileSize = j;
        }
    }

    public static class OnLiteServerQueryError extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public Error error;
        public long id;

        public int getConstructor() {
            return -NUM;
        }

        public OnLiteServerQueryError(long j, Error error) {
            this.id = j;
            this.error = error;
        }
    }

    public static class OnLiteServerQueryResult extends Function {
        public static final int CONSTRUCTOR = NUM;
        public byte[] bytes;
        public long id;

        public int getConstructor() {
            return NUM;
        }

        public OnLiteServerQueryResult(long j, byte[] bArr) {
            this.id = j;
            this.bytes = bArr;
        }
    }

    public static class OptionsSetConfig extends Function {
        public static final int CONSTRUCTOR = NUM;
        public Config config;

        public int getConstructor() {
            return NUM;
        }

        public OptionsSetConfig(Config config) {
            this.config = config;
        }
    }

    public static class OptionsValidateConfig extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public Config config;

        public int getConstructor() {
            return -NUM;
        }

        public OptionsValidateConfig(Config config) {
            this.config = config;
        }
    }

    public static class PackAccountAddress extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public UnpackedAccountAddress accountAddress;

        public int getConstructor() {
            return -NUM;
        }

        public PackAccountAddress(UnpackedAccountAddress unpackedAccountAddress) {
            this.accountAddress = unpackedAccountAddress;
        }
    }

    public static class QueryEstimateFees extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public long id;
        public boolean ignoreChksig;

        public int getConstructor() {
            return -NUM;
        }

        public QueryEstimateFees(long j, boolean z) {
            this.id = j;
            this.ignoreChksig = z;
        }
    }

    public static class QueryForget extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public long id;

        public int getConstructor() {
            return -NUM;
        }

        public QueryForget(long j) {
            this.id = j;
        }
    }

    public static class QueryGetInfo extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public long id;

        public int getConstructor() {
            return -NUM;
        }

        public QueryGetInfo(long j) {
            this.id = j;
        }
    }

    public static class QuerySend extends Function {
        public static final int CONSTRUCTOR = NUM;
        public long id;

        public int getConstructor() {
            return NUM;
        }

        public QuerySend(long j) {
            this.id = j;
        }
    }

    public static class RawCreateAndSendMessage extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public byte[] data;
        public AccountAddress destination;
        public byte[] initialAccountState;

        public int getConstructor() {
            return -NUM;
        }

        public RawCreateAndSendMessage(AccountAddress accountAddress, byte[] bArr, byte[] bArr2) {
            this.destination = accountAddress;
            this.initialAccountState = bArr;
            this.data = bArr2;
        }
    }

    public static class RawCreateQuery extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public byte[] body;
        public AccountAddress destination;
        public byte[] initCode;
        public byte[] initData;

        public int getConstructor() {
            return -NUM;
        }

        public RawCreateQuery(AccountAddress accountAddress, byte[] bArr, byte[] bArr2, byte[] bArr3) {
            this.destination = accountAddress;
            this.initCode = bArr;
            this.initData = bArr2;
            this.body = bArr3;
        }
    }

    public static class RawGetAccountAddress extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public RawInitialAccountState inititalAccountState;

        public int getConstructor() {
            return -NUM;
        }

        public RawGetAccountAddress(RawInitialAccountState rawInitialAccountState) {
            this.inititalAccountState = rawInitialAccountState;
        }
    }

    public static class RawGetAccountState extends Function {
        public static final int CONSTRUCTOR = NUM;
        public AccountAddress accountAddress;

        public int getConstructor() {
            return NUM;
        }

        public RawGetAccountState(AccountAddress accountAddress) {
            this.accountAddress = accountAddress;
        }
    }

    public static class RawGetTransactions extends Function {
        public static final int CONSTRUCTOR = NUM;
        public AccountAddress accountAddress;
        public InternalTransactionId fromTransactionId;

        public int getConstructor() {
            return NUM;
        }

        public RawGetTransactions(AccountAddress accountAddress, InternalTransactionId internalTransactionId) {
            this.accountAddress = accountAddress;
            this.fromTransactionId = internalTransactionId;
        }
    }

    public static class RawSendMessage extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public byte[] body;

        public int getConstructor() {
            return -NUM;
        }

        public RawSendMessage(byte[] bArr) {
            this.body = bArr;
        }
    }

    public static class RunTests extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public String dir;

        public int getConstructor() {
            return -NUM;
        }

        public RunTests(String str) {
            this.dir = str;
        }
    }

    public static class SetLogStream extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public LogStream logStream;

        public int getConstructor() {
            return -NUM;
        }

        public SetLogStream(LogStream logStream) {
            this.logStream = logStream;
        }
    }

    public static class SetLogTagVerbosityLevel extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public int newVerbosityLevel;
        public String tag;

        public int getConstructor() {
            return -NUM;
        }

        public SetLogTagVerbosityLevel(String str, int i) {
            this.tag = str;
            this.newVerbosityLevel = i;
        }
    }

    public static class SetLogVerbosityLevel extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public int newVerbosityLevel;

        public int getConstructor() {
            return -NUM;
        }

        public SetLogVerbosityLevel(int i) {
            this.newVerbosityLevel = i;
        }
    }

    public static class SmcGetCode extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public long id;

        public int getConstructor() {
            return -NUM;
        }

        public SmcGetCode(long j) {
            this.id = j;
        }
    }

    public static class SmcGetData extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public long id;

        public int getConstructor() {
            return -NUM;
        }

        public SmcGetData(long j) {
            this.id = j;
        }
    }

    public static class SmcGetState extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public long id;

        public int getConstructor() {
            return -NUM;
        }

        public SmcGetState(long j) {
            this.id = j;
        }
    }

    public static class SmcLoad extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public AccountAddress accountAddress;

        public int getConstructor() {
            return -NUM;
        }

        public SmcLoad(AccountAddress accountAddress) {
            this.accountAddress = accountAddress;
        }
    }

    public static class SmcMethodIdName extends SmcMethodId {
        public static final int CONSTRUCTOR = -NUM;
        public String name;

        public int getConstructor() {
            return -NUM;
        }

        public SmcMethodIdName(String str) {
            this.name = str;
        }
    }

    public static class SmcMethodIdNumber extends SmcMethodId {
        public static final int CONSTRUCTOR = -NUM;
        public int number;

        public int getConstructor() {
            return -NUM;
        }

        public SmcMethodIdNumber(int i) {
            this.number = i;
        }
    }

    public static class SmcRunGetMethod extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public long id;
        public SmcMethodId method;
        public TvmStackEntry[] stack;

        public int getConstructor() {
            return -NUM;
        }

        public SmcRunGetMethod(long j, SmcMethodId smcMethodId, TvmStackEntry[] tvmStackEntryArr) {
            this.id = j;
            this.method = smcMethodId;
            this.stack = tvmStackEntryArr;
        }
    }

    public static class Sync extends Function {
        public static final int CONSTRUCTOR = -NUM;

        public int getConstructor() {
            return -NUM;
        }
    }

    public static class SyncStateDone extends SyncState {
        public static final int CONSTRUCTOR = NUM;

        public int getConstructor() {
            return NUM;
        }
    }

    public static class SyncStateInProgress extends SyncState {
        public static final int CONSTRUCTOR = NUM;
        public int currentSeqno;
        public int fromSeqno;
        public int toSeqno;

        public int getConstructor() {
            return NUM;
        }

        public SyncStateInProgress(int i, int i2, int i3) {
            this.fromSeqno = i;
            this.toSeqno = i2;
            this.currentSeqno = i3;
        }
    }

    public static class TestGiverGetAccountAddress extends Function {
        public static final int CONSTRUCTOR = -NUM;

        public int getConstructor() {
            return -NUM;
        }
    }

    public static class TestGiverGetAccountState extends Function {
        public static final int CONSTRUCTOR = NUM;

        public int getConstructor() {
            return NUM;
        }
    }

    public static class TestGiverSendGrams extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public long amount;
        public AccountAddress destination;
        public byte[] message;
        public int seqno;

        public int getConstructor() {
            return -NUM;
        }

        public TestGiverSendGrams(AccountAddress accountAddress, int i, long j, byte[] bArr) {
            this.destination = accountAddress;
            this.seqno = i;
            this.amount = j;
            this.message = bArr;
        }
    }

    public static class TestWalletGetAccountAddress extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public TestWalletInitialAccountState inititalAccountState;

        public int getConstructor() {
            return -NUM;
        }

        public TestWalletGetAccountAddress(TestWalletInitialAccountState testWalletInitialAccountState) {
            this.inititalAccountState = testWalletInitialAccountState;
        }
    }

    public static class TestWalletGetAccountState extends Function {
        public static final int CONSTRUCTOR = NUM;
        public AccountAddress accountAddress;

        public int getConstructor() {
            return NUM;
        }

        public TestWalletGetAccountState(AccountAddress accountAddress) {
            this.accountAddress = accountAddress;
        }
    }

    public static class TestWalletInit extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public InputKey privateKey;

        public int getConstructor() {
            return -NUM;
        }

        public TestWalletInit(InputKey inputKey) {
            this.privateKey = inputKey;
        }
    }

    public static class TestWalletSendGrams extends Function {
        public static final int CONSTRUCTOR = NUM;
        public long amount;
        public AccountAddress destination;
        public byte[] message;
        public InputKey privateKey;
        public int seqno;

        public int getConstructor() {
            return NUM;
        }

        public TestWalletSendGrams(InputKey inputKey, AccountAddress accountAddress, int i, long j, byte[] bArr) {
            this.privateKey = inputKey;
            this.destination = accountAddress;
            this.seqno = i;
            this.amount = j;
            this.message = bArr;
        }
    }

    public static class TvmStackEntryCell extends TvmStackEntry {
        public static final int CONSTRUCTOR = NUM;
        public TvmCell cell;

        public int getConstructor() {
            return NUM;
        }

        public TvmStackEntryCell(TvmCell tvmCell) {
            this.cell = tvmCell;
        }
    }

    public static class TvmStackEntryNumber extends TvmStackEntry {
        public static final int CONSTRUCTOR = NUM;
        public TvmNumberDecimal number;

        public int getConstructor() {
            return NUM;
        }

        public TvmStackEntryNumber(TvmNumberDecimal tvmNumberDecimal) {
            this.number = tvmNumberDecimal;
        }
    }

    public static class TvmStackEntrySlice extends TvmStackEntry {
        public static final int CONSTRUCTOR = NUM;
        public TvmSlice slice;

        public int getConstructor() {
            return NUM;
        }

        public TvmStackEntrySlice(TvmSlice tvmSlice) {
            this.slice = tvmSlice;
        }
    }

    public static class TvmStackEntryUnsupported extends TvmStackEntry {
        public static final int CONSTRUCTOR = NUM;

        public int getConstructor() {
            return NUM;
        }
    }

    public static class UnpackAccountAddress extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public String accountAddress;

        public int getConstructor() {
            return -NUM;
        }

        public UnpackAccountAddress(String str) {
            this.accountAddress = str;
        }
    }

    public static class UpdateSendLiteServerQuery extends Update {
        public static final int CONSTRUCTOR = -NUM;
        public byte[] data;
        public long id;

        public int getConstructor() {
            return -NUM;
        }

        public UpdateSendLiteServerQuery(long j, byte[] bArr) {
            this.id = j;
            this.data = bArr;
        }
    }

    public static class UpdateSyncState extends Update {
        public static final int CONSTRUCTOR = NUM;
        public SyncState syncState;

        public int getConstructor() {
            return NUM;
        }

        public UpdateSyncState(SyncState syncState) {
            this.syncState = syncState;
        }
    }

    public static class WalletGetAccountAddress extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public WalletInitialAccountState inititalAccountState;

        public int getConstructor() {
            return -NUM;
        }

        public WalletGetAccountAddress(WalletInitialAccountState walletInitialAccountState) {
            this.inititalAccountState = walletInitialAccountState;
        }
    }

    public static class WalletGetAccountState extends Function {
        public static final int CONSTRUCTOR = NUM;
        public AccountAddress accountAddress;

        public int getConstructor() {
            return NUM;
        }

        public WalletGetAccountState(AccountAddress accountAddress) {
            this.accountAddress = accountAddress;
        }
    }

    public static class WalletInit extends Function {
        public static final int CONSTRUCTOR = -NUM;
        public InputKey privateKey;

        public int getConstructor() {
            return -NUM;
        }

        public WalletInit(InputKey inputKey) {
            this.privateKey = inputKey;
        }
    }

    public static class WalletSendGrams extends Function {
        public static final int CONSTRUCTOR = NUM;
        public long amount;
        public AccountAddress destination;
        public byte[] message;
        public InputKey privateKey;
        public int seqno;
        public long validUntil;

        public int getConstructor() {
            return NUM;
        }

        public WalletSendGrams(InputKey inputKey, AccountAddress accountAddress, int i, long j, long j2, byte[] bArr) {
            this.privateKey = inputKey;
            this.destination = accountAddress;
            this.seqno = i;
            this.validUntil = j;
            this.amount = j2;
            this.message = bArr;
        }
    }

    public static class WalletV3GetAccountAddress extends Function {
        public static final int CONSTRUCTOR = NUM;
        public WalletV3InitialAccountState inititalAccountState;

        public int getConstructor() {
            return NUM;
        }

        public WalletV3GetAccountAddress(WalletV3InitialAccountState walletV3InitialAccountState) {
            this.inititalAccountState = walletV3InitialAccountState;
        }
    }
}
