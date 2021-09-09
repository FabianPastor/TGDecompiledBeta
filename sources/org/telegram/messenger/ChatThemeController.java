package org.telegram.messenger;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC$TL_account_getChatThemes;
import org.telegram.tgnet.TLRPC$TL_chatTheme;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_setChatTheme;
import org.telegram.ui.ActionBar.ChatTheme;

public class ChatThemeController extends BaseController {
    private static volatile List<ChatTheme> allChatThemes = null;
    private static volatile DispatchQueue chatThemeQueue = new DispatchQueue("stageQueue");
    private static final ChatThemeController[] instances = new ChatThemeController[3];
    private static volatile long lastReloadTimeMs = 0;
    private static final long reloadTimeoutMs = 7200000;
    private static final HashMap<Long, Bitmap> themeIdWallpaperMap = new HashMap<>();
    private static final HashMap<Long, Bitmap> themeIdWallpaperThumbMap = new HashMap<>();
    private static volatile int themesHash;
    private final LongSparseArray<String> dialogEmoticonsMap = new LongSparseArray<>();

    public static void init() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        themesHash = sharedPreferences.getInt("hash", 0);
        lastReloadTimeMs = sharedPreferences.getLong("lastReload", 0);
        allChatThemes = getAllChatThemesFromPrefs();
        Emoji.preloadEmoji("❌");
        if (!allChatThemes.isEmpty()) {
            for (ChatTheme emoticon : allChatThemes) {
                Emoji.preloadEmoji(emoticon.getEmoticon());
            }
            return;
        }
        Emoji.preloadEmoji("🦁");
        Emoji.preloadEmoji("⛄");
        Emoji.preloadEmoji("💎");
        Emoji.preloadEmoji("👨‍🏫");
        Emoji.preloadEmoji("🌷");
        Emoji.preloadEmoji("🔮");
        Emoji.preloadEmoji("🎄");
        Emoji.preloadEmoji("🎮");
    }

    public static void requestAllChatThemes(ResultCallback<List<ChatTheme>> resultCallback, boolean z) {
        if (themesHash == 0 || lastReloadTimeMs == 0) {
            init();
        }
        boolean z2 = System.currentTimeMillis() - lastReloadTimeMs > 7200000;
        if (allChatThemes == null || allChatThemes.isEmpty() || z2) {
            TLRPC$TL_account_getChatThemes tLRPC$TL_account_getChatThemes = new TLRPC$TL_account_getChatThemes();
            tLRPC$TL_account_getChatThemes.hash = themesHash;
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_account_getChatThemes, new ChatThemeController$$ExternalSyntheticLambda5(resultCallback, z));
            return;
        }
        chatThemeQueue.postRunnable(new ChatThemeController$$ExternalSyntheticLambda4(z, resultCallback));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$requestAllChatThemes$2(org.telegram.tgnet.TLObject r7, org.telegram.tgnet.ResultCallback r8, org.telegram.tgnet.TLRPC$TL_error r9, boolean r10) {
        /*
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_account_chatThemes
            r1 = 0
            if (r0 == 0) goto L_0x0090
            org.telegram.tgnet.TLRPC$TL_account_chatThemes r7 = (org.telegram.tgnet.TLRPC$TL_account_chatThemes) r7
            int r9 = r7.hash
            themesHash = r9
            long r2 = java.lang.System.currentTimeMillis()
            lastReloadTimeMs = r2
            android.content.SharedPreferences r9 = getSharedPreferences()
            android.content.SharedPreferences$Editor r9 = r9.edit()
            r9.clear()
            int r0 = themesHash
            java.lang.String r2 = "hash"
            r9.putInt(r2, r0)
            long r2 = lastReloadTimeMs
            java.lang.String r0 = "lastReload"
            r9.putLong(r0, r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatTheme> r0 = r7.themes
            int r0 = r0.size()
            java.lang.String r2 = "count"
            r9.putInt(r2, r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatTheme> r2 = r7.themes
            int r2 = r2.size()
            r0.<init>(r2)
            r2 = 0
        L_0x0041:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatTheme> r3 = r7.themes
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x008c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatTheme> r3 = r7.themes
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$TL_chatTheme r3 = (org.telegram.tgnet.TLRPC$TL_chatTheme) r3
            java.lang.String r4 = r3.emoticon
            org.telegram.messenger.Emoji.preloadEmoji(r4)
            org.telegram.tgnet.SerializedData r4 = new org.telegram.tgnet.SerializedData
            int r5 = r3.getObjectSize()
            r4.<init>((int) r5)
            r3.serializeToStream(r4)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "theme_"
            r5.append(r6)
            r5.append(r2)
            java.lang.String r5 = r5.toString()
            byte[] r4 = r4.toByteArray()
            java.lang.String r4 = org.telegram.messenger.Utilities.bytesToHex(r4)
            r9.putString(r5, r4)
            org.telegram.ui.ActionBar.ChatTheme r4 = new org.telegram.ui.ActionBar.ChatTheme
            r4.<init>(r3, r1)
            r4.preloadWallpaper()
            r0.add(r4)
            int r2 = r2 + 1
            goto L_0x0041
        L_0x008c:
            r9.apply()
            goto L_0x0098
        L_0x0090:
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_account_chatThemesNotModified
            if (r7 == 0) goto L_0x009a
            java.util.List r0 = getAllChatThemesFromPrefs()
        L_0x0098:
            r7 = 0
            goto L_0x00a4
        L_0x009a:
            r0 = 0
            org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda2 r7 = new org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda2
            r7.<init>(r8, r9)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7)
            r7 = 1
        L_0x00a4:
            if (r7 != 0) goto L_0x00d2
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>(r0)
            allChatThemes = r7
            if (r10 == 0) goto L_0x00b6
            org.telegram.ui.ActionBar.ChatTheme r7 = org.telegram.ui.ActionBar.ChatTheme.getDefault()
            r0.add(r1, r7)
        L_0x00b6:
            java.util.Iterator r7 = r0.iterator()
        L_0x00ba:
            boolean r9 = r7.hasNext()
            if (r9 == 0) goto L_0x00ca
            java.lang.Object r9 = r7.next()
            org.telegram.ui.ActionBar.ChatTheme r9 = (org.telegram.ui.ActionBar.ChatTheme) r9
            r9.initColors()
            goto L_0x00ba
        L_0x00ca:
            org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda0 r7 = new org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda0
            r7.<init>(r8, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7)
        L_0x00d2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatThemeController.lambda$requestAllChatThemes$2(org.telegram.tgnet.TLObject, org.telegram.tgnet.ResultCallback, org.telegram.tgnet.TLRPC$TL_error, boolean):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$requestAllChatThemes$5(boolean z, ResultCallback resultCallback) {
        ArrayList<ChatTheme> arrayList = new ArrayList<>(allChatThemes);
        if (z) {
            arrayList.add(0, ChatTheme.getDefault());
        }
        for (ChatTheme initColors : arrayList) {
            initColors.initColors();
        }
        AndroidUtilities.runOnUIThread(new ChatThemeController$$ExternalSyntheticLambda1(resultCallback, arrayList));
    }

    private static SharedPreferences getSharedPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences("chatthemeconfig", 0);
    }

    private static List<ChatTheme> getAllChatThemesFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        int i = sharedPreferences.getInt("count", 0);
        ArrayList arrayList = new ArrayList(i);
        for (int i2 = 0; i2 < i; i2++) {
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(sharedPreferences.getString("theme_" + i2, "")));
            try {
                TLRPC$TL_chatTheme TLdeserialize = TLRPC$TL_chatTheme.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                if (TLdeserialize != null) {
                    arrayList.add(new ChatTheme(TLdeserialize, false));
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        return arrayList;
    }

    public static void requestChatTheme(final String str, final ResultCallback<ChatTheme> resultCallback) {
        if (TextUtils.isEmpty(str)) {
            resultCallback.onComplete(null);
        } else {
            requestAllChatThemes(new ResultCallback<List<ChatTheme>>() {
                public /* bridge */ /* synthetic */ void onError(Throwable th) {
                    ResultCallback.CC.$default$onError((ResultCallback) this, th);
                }

                public void onComplete(List<ChatTheme> list) {
                    for (ChatTheme next : list) {
                        if (str.equals(next.getEmoticon())) {
                            next.initColors();
                            resultCallback.onComplete(next);
                            return;
                        }
                    }
                }

                public void onError(TLRPC$TL_error tLRPC$TL_error) {
                    resultCallback.onComplete(null);
                }
            }, false);
        }
    }

    public static ChatThemeController getInstance(int i) {
        ChatThemeController[] chatThemeControllerArr = instances;
        ChatThemeController chatThemeController = chatThemeControllerArr[i];
        if (chatThemeController == null) {
            synchronized (ChatThemeController.class) {
                chatThemeController = chatThemeControllerArr[i];
                if (chatThemeController == null) {
                    chatThemeController = new ChatThemeController(i);
                    chatThemeControllerArr[i] = chatThemeController;
                }
            }
        }
        return chatThemeController;
    }

    public ChatThemeController(int i) {
        super(i);
    }

    public void setDialogTheme(long j, String str, boolean z) {
        if (!TextUtils.equals(this.dialogEmoticonsMap.get(j), str)) {
            if (str == null) {
                this.dialogEmoticonsMap.delete(j);
            } else {
                this.dialogEmoticonsMap.put(j, str);
            }
            SharedPreferences.Editor edit = getSharedPreferences().edit();
            edit.putString("chatTheme_" + this.currentAccount + "_" + j, str).apply();
            if (z) {
                TLRPC$TL_messages_setChatTheme tLRPC$TL_messages_setChatTheme = new TLRPC$TL_messages_setChatTheme();
                if (str == null) {
                    str = "";
                }
                tLRPC$TL_messages_setChatTheme.emoticon = str;
                tLRPC$TL_messages_setChatTheme.peer = getMessagesController().getInputPeer((long) ((int) j));
                getConnectionsManager().sendRequest(tLRPC$TL_messages_setChatTheme, (RequestDelegate) null);
            }
        }
    }

    public ChatTheme getDialogTheme(long j) {
        String str = this.dialogEmoticonsMap.get(j);
        if (str == null) {
            SharedPreferences sharedPreferences = getSharedPreferences();
            str = sharedPreferences.getString("chatTheme_" + this.currentAccount + "_" + j, (String) null);
            this.dialogEmoticonsMap.put(j, str);
        }
        if (str != null) {
            for (ChatTheme next : allChatThemes) {
                if (str.equals(next.getEmoticon())) {
                    return next;
                }
            }
        }
        return null;
    }

    public static void preloadAllWallpaperImages(boolean z) {
        for (ChatTheme next : allChatThemes) {
            if (!themeIdWallpaperMap.containsKey(Long.valueOf(next.getTlTheme(z).id))) {
                next.loadWallpaper(z, ChatThemeController$$ExternalSyntheticLambda6.INSTANCE);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$preloadAllWallpaperImages$6(Pair pair) {
        if (pair != null) {
            themeIdWallpaperMap.put((Long) pair.first, (Bitmap) pair.second);
        }
    }

    public static void preloadAllWallpaperThumbs(boolean z) {
        for (ChatTheme next : allChatThemes) {
            if (!themeIdWallpaperThumbMap.containsKey(Long.valueOf(next.getTlTheme(z).id))) {
                next.loadWallpaperThumb(z, ChatThemeController$$ExternalSyntheticLambda7.INSTANCE);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$preloadAllWallpaperThumbs$7(Pair pair) {
        if (pair != null) {
            themeIdWallpaperThumbMap.put((Long) pair.first, (Bitmap) pair.second);
        }
    }

    public static void clearWallpaperImages() {
        themeIdWallpaperMap.clear();
    }

    public static void clearWallpaperThumbImages() {
        themeIdWallpaperThumbMap.clear();
    }

    public static Bitmap getWallpaperBitmap(long j) {
        return themeIdWallpaperMap.get(Long.valueOf(j));
    }

    public static Bitmap getWallpaperThumbBitmap(long j) {
        return themeIdWallpaperThumbMap.get(Long.valueOf(j));
    }

    public void clearCache() {
        themesHash = 0;
        lastReloadTimeMs = 0;
        getSharedPreferences().edit().clear().apply();
    }
}
