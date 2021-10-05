package org.telegram.ui.ActionBar;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$TL_chatTheme;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_themeSettings;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.ui.ActionBar.Theme;

public class ChatTheme {
    private final TLRPC$TL_chatTheme chatThemeObject;
    private HashMap<String, Integer> darkCurrentColors;
    private String darkWallpaperLink;
    public final boolean isDefault;
    private HashMap<String, Integer> lightCurrentColors;
    private String lightWallpaperLink;

    public ChatTheme(TLRPC$TL_chatTheme tLRPC$TL_chatTheme, boolean z) {
        this.chatThemeObject = tLRPC$TL_chatTheme;
        this.isDefault = z;
    }

    public void initColors() {
        getCurrentColors(0, false);
        getCurrentColors(0, true);
    }

    public String getEmoticon() {
        return this.chatThemeObject.emoticon;
    }

    public TLRPC$TL_theme getTlTheme(boolean z) {
        return (TLRPC$TL_theme) (z ? this.chatThemeObject.dark_theme : this.chatThemeObject.theme);
    }

    public TLRPC$WallPaper getWallpaper(boolean z) {
        return getTlTheme(z).settings.wallpaper;
    }

    public String getWallpaperLink(boolean z) {
        return z ? this.darkWallpaperLink : this.lightWallpaperLink;
    }

    public HashMap<String, Integer> getCurrentColors(int i, boolean z) {
        HashMap<String, Integer> hashMap = z ? this.darkCurrentColors : this.lightCurrentColors;
        if (hashMap != null) {
            return hashMap;
        }
        TLRPC$TL_theme tlTheme = getTlTheme(z);
        Theme.ThemeInfo themeInfo = new Theme.ThemeInfo(Theme.getTheme(Theme.getBaseThemeKey(tlTheme.settings)));
        Theme.ThemeAccent createNewAccent = themeInfo.createNewAccent(tlTheme, i, true);
        themeInfo.setCurrentAccentId(createNewAccent.id);
        HashMap hashMap2 = new HashMap();
        String[] strArr = new String[1];
        if (themeInfo.pathToFile != null) {
            hashMap2.putAll(Theme.getThemeFileValues(new File(themeInfo.pathToFile), (String) null, strArr));
        } else {
            String str = themeInfo.assetName;
            if (str != null) {
                hashMap2.putAll(Theme.getThemeFileValues((File) null, str, strArr));
            }
        }
        if (z) {
            this.darkWallpaperLink = strArr[0];
        } else {
            this.lightWallpaperLink = strArr[0];
        }
        HashMap<String, Integer> hashMap3 = new HashMap<>(hashMap2);
        createNewAccent.fillAccentColors(hashMap2, hashMap3);
        if (!z) {
            hashMap3.put("chat_messageTextOut", -14606047);
        }
        for (Map.Entry next : Theme.getFallbackKeys().entrySet()) {
            String str2 = (String) next.getKey();
            if (!hashMap3.containsKey(str2)) {
                hashMap3.put(str2, hashMap3.get(next.getValue()));
            }
        }
        for (Map.Entry next2 : Theme.getDefaultColors().entrySet()) {
            if (!hashMap3.containsKey(next2.getKey())) {
                hashMap3.put((String) next2.getKey(), (Integer) next2.getValue());
            }
        }
        if (z) {
            this.darkCurrentColors = hashMap3;
        } else {
            this.lightCurrentColors = hashMap3;
        }
        return hashMap3;
    }

    public void loadWallpaper(boolean z, ResultCallback<Pair<Long, Bitmap>> resultCallback) {
        TLRPC$WallPaper wallpaper = getWallpaper(z);
        if (wallpaper != null) {
            long j = getTlTheme(z).id;
            ChatThemeController.getWallpaperBitmap(j, new ChatTheme$$ExternalSyntheticLambda3(resultCallback, j, wallpaper));
        } else if (resultCallback != null) {
            resultCallback.onComplete(null);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadWallpaper$1(ResultCallback resultCallback, long j, TLRPC$WallPaper tLRPC$WallPaper, Bitmap bitmap) {
        String str;
        if (bitmap == null || resultCallback == null) {
            ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$WallPaper.document);
            ImageReceiver imageReceiver = new ImageReceiver();
            if (SharedConfig.getDevicePerformanceClass() == 0) {
                Point point = AndroidUtilities.displaySize;
                int min = Math.min(point.x, point.y);
                Point point2 = AndroidUtilities.displaySize;
                int max = Math.max(point2.x, point2.y);
                str = ((int) (((float) min) / AndroidUtilities.density)) + "_" + ((int) (((float) max) / AndroidUtilities.density)) + "_f";
            } else {
                str = ((int) (1080.0f / AndroidUtilities.density)) + "_" + ((int) (1920.0f / AndroidUtilities.density)) + "_f";
            }
            imageReceiver.setImage(forDocument, str, (Drawable) null, ".jpg", tLRPC$WallPaper, 1);
            imageReceiver.setDelegate(new ChatTheme$$ExternalSyntheticLambda1(resultCallback, j));
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
            return;
        }
        resultCallback.onComplete(new Pair(Long.valueOf(j), bitmap));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadWallpaper$0(ResultCallback resultCallback, long j, ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
        if (z && bitmapSafe != null) {
            Bitmap bitmap = bitmapSafe.bitmap;
            if (bitmap == null) {
                Drawable drawable = bitmapSafe.drawable;
                if (drawable instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                }
            }
            if (resultCallback != null) {
                resultCallback.onComplete(new Pair(Long.valueOf(j), bitmap));
            }
            ChatThemeController.saveWallpaperBitmap(bitmap, j);
        }
    }

    public void loadWallpaperThumb(boolean z, ResultCallback<Pair<Long, Bitmap>> resultCallback) {
        TLRPC$WallPaper wallpaper = getWallpaper(z);
        if (wallpaper != null) {
            long j = getTlTheme(z).id;
            Bitmap wallpaperThumbBitmap = ChatThemeController.getWallpaperThumbBitmap(j);
            File wallpaperThumbFile = getWallpaperThumbFile(j);
            if (wallpaperThumbBitmap == null && wallpaperThumbFile.exists() && wallpaperThumbFile.length() > 0) {
                try {
                    wallpaperThumbBitmap = BitmapFactory.decodeFile(wallpaperThumbFile.getAbsolutePath());
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            if (wallpaperThumbBitmap == null) {
                ImageLocation forDocument = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(wallpaper.document.thumbs, 120), wallpaper.document);
                ImageReceiver imageReceiver = new ImageReceiver();
                imageReceiver.setImage(forDocument, "120_80", (Drawable) null, (String) null, (Object) null, 1);
                imageReceiver.setDelegate(new ChatTheme$$ExternalSyntheticLambda2(resultCallback, j, wallpaperThumbFile));
                ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
            } else if (resultCallback != null) {
                resultCallback.onComplete(new Pair(Long.valueOf(j), wallpaperThumbBitmap));
            }
        } else if (resultCallback != null) {
            resultCallback.onComplete(null);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadWallpaperThumb$3(ResultCallback resultCallback, long j, File file, ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
        if (z && bitmapSafe != null) {
            Bitmap bitmap = bitmapSafe.bitmap;
            if (bitmap == null) {
                Drawable drawable = bitmapSafe.drawable;
                if (drawable instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                }
            }
            if (bitmap != null) {
                if (resultCallback != null) {
                    resultCallback.onComplete(new Pair(Long.valueOf(j), bitmap));
                }
                Utilities.globalQueue.postRunnable(new ChatTheme$$ExternalSyntheticLambda0(file, bitmap));
            } else if (resultCallback != null) {
                resultCallback.onComplete(null);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0014 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$loadWallpaperThumb$2(java.io.File r2, android.graphics.Bitmap r3) {
        /*
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0015 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x0015 }
            android.graphics.Bitmap$CompressFormat r2 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ all -> 0x0010 }
            r1 = 87
            r3.compress(r2, r1, r0)     // Catch:{ all -> 0x0010 }
            r0.close()     // Catch:{ Exception -> 0x0015 }
            goto L_0x0019
        L_0x0010:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x0014 }
        L_0x0014:
            throw r2     // Catch:{ Exception -> 0x0015 }
        L_0x0015:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0019:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ChatTheme.lambda$loadWallpaperThumb$2(java.io.File, android.graphics.Bitmap):void");
    }

    public void preloadWallpaper() {
        loadWallpaperThumb(false, (ResultCallback<Pair<Long, Bitmap>>) null);
        loadWallpaperThumb(true, (ResultCallback<Pair<Long, Bitmap>>) null);
        loadWallpaper(false, (ResultCallback<Pair<Long, Bitmap>>) null);
        loadWallpaper(true, (ResultCallback<Pair<Long, Bitmap>>) null);
    }

    private File getWallpaperThumbFile(long j) {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        return new File(filesDirFixed, "wallpaper_thumb_" + j + ".png");
    }

    public static Theme.ThemeInfo getDefaultThemeInfo(boolean z) {
        String str;
        Theme.ThemeInfo currentNightTheme = z ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
        if (z != currentNightTheme.isDark()) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
            String str2 = "Dark Blue";
            if (z) {
                str = sharedPreferences.getString("lastNightTheme", str2);
            } else {
                str = sharedPreferences.getString("lastDayTheme", "Blue");
            }
            currentNightTheme = Theme.getTheme(str);
            if (currentNightTheme == null) {
                if (!z) {
                    str2 = "Blue";
                }
                currentNightTheme = Theme.getTheme(str2);
            }
        }
        return new Theme.ThemeInfo(currentNightTheme);
    }

    public static ChatTheme getDefault() {
        Theme.ThemeInfo defaultThemeInfo = getDefaultThemeInfo(true);
        fillTlTheme(defaultThemeInfo);
        Theme.ThemeInfo defaultThemeInfo2 = getDefaultThemeInfo(false);
        fillTlTheme(defaultThemeInfo2);
        TLRPC$TL_chatTheme tLRPC$TL_chatTheme = new TLRPC$TL_chatTheme();
        tLRPC$TL_chatTheme.emoticon = "❌";
        tLRPC$TL_chatTheme.dark_theme = defaultThemeInfo.info;
        tLRPC$TL_chatTheme.theme = defaultThemeInfo2.info;
        ChatTheme chatTheme = new ChatTheme(tLRPC$TL_chatTheme, true);
        chatTheme.darkCurrentColors = getCurrentColors(defaultThemeInfo);
        chatTheme.lightCurrentColors = getCurrentColors(defaultThemeInfo2);
        return chatTheme;
    }

    private static void fillTlTheme(Theme.ThemeInfo themeInfo) {
        if (themeInfo.info == null) {
            themeInfo.info = new TLRPC$TL_theme();
        }
        TLRPC$TL_theme tLRPC$TL_theme = themeInfo.info;
        if (tLRPC$TL_theme.settings == null) {
            tLRPC$TL_theme.settings = new TLRPC$TL_themeSettings();
        }
        ArrayList<Integer> arrayList = new ArrayList<>();
        Theme.ThemeAccent accent = themeInfo.getAccent(false);
        if (accent != null) {
            int i = accent.myMessagesAccentColor;
            if (i != 0) {
                arrayList.add(Integer.valueOf(i));
            }
            int i2 = accent.myMessagesGradientAccentColor1;
            if (i2 != 0) {
                arrayList.add(Integer.valueOf(i2));
            }
            int i3 = accent.myMessagesGradientAccentColor2;
            if (i3 != 0) {
                arrayList.add(Integer.valueOf(i3));
            }
            int i4 = accent.myMessagesGradientAccentColor3;
            if (i4 != 0) {
                arrayList.add(Integer.valueOf(i4));
            }
        }
        themeInfo.info.settings.message_colors = arrayList;
    }

    private static HashMap<String, Integer> getCurrentColors(Theme.ThemeInfo themeInfo) {
        HashMap hashMap = new HashMap();
        if (themeInfo.pathToFile != null) {
            hashMap.putAll(Theme.getThemeFileValues(new File(themeInfo.pathToFile), (String) null, (String[]) null));
        } else {
            String str = themeInfo.assetName;
            if (str != null) {
                hashMap.putAll(Theme.getThemeFileValues((File) null, str, (String[]) null));
            }
        }
        HashMap<String, Integer> hashMap2 = new HashMap<>(hashMap);
        Theme.ThemeAccent accent = themeInfo.getAccent(false);
        if (accent != null) {
            accent.fillAccentColors(hashMap, hashMap2);
        }
        return hashMap2;
    }
}
