package org.telegram.ui.ActionBar;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.util.SparseArray;
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
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.ui.ActionBar.Theme;

public class EmojiThemes {
    String emoji;
    ArrayList<ThemeItem> items = new ArrayList<>();
    public boolean showAsDefaultStub;

    public static class ThemeItem {
        public int accentId = -1;
        public HashMap<String, Integer> currentColors;
        public int inBubbleColor;
        public int outBubbleColor;
        public int outLineColor;
        public int patternBgColor;
        public int patternBgGradientColor1;
        public int patternBgGradientColor2;
        public int patternBgGradientColor3;
        public int patternBgRotation;
        int settingsIndex;
        public Theme.ThemeInfo themeInfo;
        TLRPC$TL_theme tlTheme;
        /* access modifiers changed from: private */
        public String wallpaperLink;
    }

    private EmojiThemes() {
    }

    public EmojiThemes(TLRPC$TL_theme tLRPC$TL_theme, boolean z) {
        this.showAsDefaultStub = z;
        this.emoji = tLRPC$TL_theme.emoticon;
        if (!z) {
            ThemeItem themeItem = new ThemeItem();
            themeItem.tlTheme = tLRPC$TL_theme;
            themeItem.settingsIndex = 0;
            this.items.add(themeItem);
            ThemeItem themeItem2 = new ThemeItem();
            themeItem2.tlTheme = tLRPC$TL_theme;
            themeItem2.settingsIndex = 1;
            this.items.add(themeItem2);
        }
    }

    public static EmojiThemes createPreviewFullTheme(TLRPC$TL_theme tLRPC$TL_theme) {
        EmojiThemes emojiThemes = new EmojiThemes();
        emojiThemes.emoji = tLRPC$TL_theme.emoticon;
        for (int i = 0; i < tLRPC$TL_theme.settings.size(); i++) {
            ThemeItem themeItem = new ThemeItem();
            themeItem.tlTheme = tLRPC$TL_theme;
            themeItem.settingsIndex = i;
            emojiThemes.items.add(themeItem);
        }
        return emojiThemes;
    }

    public static EmojiThemes createChatThemesDefault() {
        EmojiThemes emojiThemes = new EmojiThemes();
        emojiThemes.emoji = "❌";
        emojiThemes.showAsDefaultStub = true;
        ThemeItem themeItem = new ThemeItem();
        themeItem.themeInfo = getDefaultThemeInfo(true);
        emojiThemes.items.add(themeItem);
        ThemeItem themeItem2 = new ThemeItem();
        themeItem2.themeInfo = getDefaultThemeInfo(false);
        emojiThemes.items.add(themeItem2);
        return emojiThemes;
    }

    public static EmojiThemes createPreviewCustom() {
        EmojiThemes emojiThemes = new EmojiThemes();
        emojiThemes.emoji = "🎨";
        int i = 0;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        String string = sharedPreferences.getString("lastDayCustomTheme", (String) null);
        int i2 = sharedPreferences.getInt("lastDayCustomThemeAccentId", -1);
        if (string == null || Theme.getTheme(string) == null) {
            string = sharedPreferences.getString("lastDayTheme", "Blue");
            if (Theme.getTheme(string) == null) {
                i2 = 99;
                string = "Blue";
            }
            sharedPreferences.edit().putString("lastDayCustomTheme", string).apply();
        }
        String string2 = sharedPreferences.getString("lastDarkCustomTheme", (String) null);
        int i3 = sharedPreferences.getInt("lastDarkCustomThemeAccentId", -1);
        if (string2 == null || Theme.getTheme(string2) == null) {
            string2 = sharedPreferences.getString("lastDarkTheme", "Dark Blue");
            if (Theme.getTheme(string2) == null) {
                string2 = "Dark Blue";
            } else {
                i = i3;
            }
            sharedPreferences.edit().putString("lastDarkCustomTheme", string2).apply();
            i3 = i;
        }
        ThemeItem themeItem = new ThemeItem();
        themeItem.themeInfo = Theme.getTheme(string);
        themeItem.accentId = i2;
        emojiThemes.items.add(themeItem);
        emojiThemes.items.add((Object) null);
        ThemeItem themeItem2 = new ThemeItem();
        themeItem2.themeInfo = Theme.getTheme(string2);
        themeItem2.accentId = i3;
        emojiThemes.items.add(themeItem2);
        emojiThemes.items.add((Object) null);
        return emojiThemes;
    }

    public static EmojiThemes createHomePreviewTheme() {
        EmojiThemes emojiThemes = new EmojiThemes();
        emojiThemes.emoji = "🏠";
        ThemeItem themeItem = new ThemeItem();
        themeItem.themeInfo = Theme.getTheme("Blue");
        themeItem.accentId = 99;
        emojiThemes.items.add(themeItem);
        ThemeItem themeItem2 = new ThemeItem();
        themeItem2.themeInfo = Theme.getTheme("Day");
        themeItem2.accentId = 9;
        emojiThemes.items.add(themeItem2);
        ThemeItem themeItem3 = new ThemeItem();
        themeItem3.themeInfo = Theme.getTheme("Night");
        themeItem3.accentId = 0;
        emojiThemes.items.add(themeItem3);
        ThemeItem themeItem4 = new ThemeItem();
        themeItem4.themeInfo = Theme.getTheme("Dark Blue");
        themeItem4.accentId = 0;
        emojiThemes.items.add(themeItem4);
        return emojiThemes;
    }

    public void initColors() {
        getCurrentColors(0, 0);
        getCurrentColors(0, 1);
    }

    public String getEmoticon() {
        return this.emoji;
    }

    public TLRPC$TL_theme getTlTheme(int i) {
        return this.items.get(i).tlTheme;
    }

    public TLRPC$WallPaper getWallpaper(int i) {
        int i2 = this.items.get(i).settingsIndex;
        if (i2 >= 0) {
            return getTlTheme(i).settings.get(i2).wallpaper;
        }
        return null;
    }

    public String getWallpaperLink(int i) {
        return this.items.get(i).wallpaperLink;
    }

    public int getSettingsIndex(int i) {
        return this.items.get(i).settingsIndex;
    }

    public HashMap<String, Integer> getCurrentColors(int i, int i2) {
        Theme.ThemeAccent themeAccent;
        HashMap<String, Integer> hashMap = this.items.get(i2).currentColors;
        if (hashMap != null) {
            return hashMap;
        }
        Theme.ThemeInfo themeInfo = getThemeInfo(i2);
        if (themeInfo == null) {
            int settingsIndex = getSettingsIndex(i2);
            TLRPC$TL_theme tlTheme = getTlTheme(i2);
            Theme.ThemeInfo themeInfo2 = new Theme.ThemeInfo(Theme.getTheme(Theme.getBaseThemeKey(tlTheme.settings.get(settingsIndex))));
            themeAccent = themeInfo2.createNewAccent(tlTheme, i, true, settingsIndex);
            themeInfo2.setCurrentAccentId(themeAccent.id);
            themeInfo = themeInfo2;
        } else {
            SparseArray<Theme.ThemeAccent> sparseArray = themeInfo.themeAccentsMap;
            themeAccent = sparseArray != null ? sparseArray.get(this.items.get(i2).accentId) : null;
        }
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
        String unused = this.items.get(i2).wallpaperLink = strArr[0];
        HashMap<String, Integer> hashMap3 = new HashMap<>(hashMap2);
        if (themeAccent != null) {
            themeAccent.fillAccentColors(hashMap2, hashMap3);
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
        this.items.get(i2).currentColors = hashMap3;
        return hashMap3;
    }

    public Theme.ThemeInfo getThemeInfo(int i) {
        return this.items.get(i).themeInfo;
    }

    public void loadWallpaper(int i, ResultCallback<Pair<Long, Bitmap>> resultCallback) {
        TLRPC$WallPaper wallpaper = getWallpaper(i);
        if (wallpaper != null) {
            long j = getTlTheme(i).id;
            ChatThemeController.getWallpaperBitmap(j, new EmojiThemes$$ExternalSyntheticLambda3(resultCallback, j, wallpaper));
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
            imageReceiver.setDelegate(new EmojiThemes$$ExternalSyntheticLambda1(resultCallback, j));
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

    public void loadWallpaperThumb(int i, ResultCallback<Pair<Long, Bitmap>> resultCallback) {
        TLRPC$WallPaper wallpaper = getWallpaper(i);
        if (wallpaper != null) {
            long j = getTlTheme(i).id;
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
                imageReceiver.setDelegate(new EmojiThemes$$ExternalSyntheticLambda2(resultCallback, j, wallpaperThumbFile));
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
                Utilities.globalQueue.postRunnable(new EmojiThemes$$ExternalSyntheticLambda0(file, bitmap));
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.EmojiThemes.lambda$loadWallpaperThumb$2(java.io.File, android.graphics.Bitmap):void");
    }

    public void preloadWallpaper() {
        loadWallpaperThumb(0, (ResultCallback<Pair<Long, Bitmap>>) null);
        loadWallpaperThumb(1, (ResultCallback<Pair<Long, Bitmap>>) null);
        loadWallpaper(0, (ResultCallback<Pair<Long, Bitmap>>) null);
        loadWallpaper(1, (ResultCallback<Pair<Long, Bitmap>>) null);
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
                str = sharedPreferences.getString("lastDarkTheme", str2);
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

    public int getAccentId(int i) {
        return this.items.get(i).accentId;
    }

    public void loadPreviewColors(int i) {
        for (int i2 = 0; i2 < this.items.size(); i2++) {
            if (this.items.get(i2) != null) {
                HashMap<String, Integer> currentColors = getCurrentColors(i, i2);
                Integer num = currentColors.get("chat_inBubble");
                if (num == null) {
                    num = Integer.valueOf(Theme.getDefaultColor("chat_inBubble"));
                }
                this.items.get(i2).inBubbleColor = num.intValue();
                Integer num2 = currentColors.get("chat_outBubble");
                if (num2 == null) {
                    num2 = Integer.valueOf(Theme.getDefaultColor("chat_outBubble"));
                }
                this.items.get(i2).outBubbleColor = num2.intValue();
                Integer num3 = currentColors.get("featuredStickers_addButton");
                if (num3 == null) {
                    num3 = Integer.valueOf(Theme.getDefaultColor("featuredStickers_addButton"));
                }
                this.items.get(i2).outLineColor = num3.intValue();
                Integer num4 = currentColors.get("chat_wallpaper");
                if (num4 == null) {
                    this.items.get(i2).patternBgColor = 0;
                } else {
                    this.items.get(i2).patternBgColor = num4.intValue();
                }
                Integer num5 = currentColors.get("chat_wallpaper_gradient_to");
                if (num5 == null) {
                    this.items.get(i2).patternBgGradientColor1 = 0;
                } else {
                    this.items.get(i2).patternBgGradientColor1 = num5.intValue();
                }
                Integer num6 = currentColors.get("key_chat_wallpaper_gradient_to2");
                if (num6 == null) {
                    this.items.get(i2).patternBgGradientColor2 = 0;
                } else {
                    this.items.get(i2).patternBgGradientColor2 = num6.intValue();
                }
                Integer num7 = currentColors.get("key_chat_wallpaper_gradient_to3");
                if (num7 == null) {
                    this.items.get(i2).patternBgGradientColor3 = 0;
                } else {
                    this.items.get(i2).patternBgGradientColor3 = num7.intValue();
                }
                Integer num8 = currentColors.get("chat_wallpaper_gradient_rotation");
                if (num8 == null) {
                    this.items.get(i2).patternBgRotation = 0;
                } else {
                    this.items.get(i2).patternBgRotation = num8.intValue();
                }
                if (this.items.get(i2).themeInfo != null && this.items.get(i2).themeInfo.getKey().equals("Blue")) {
                    if ((this.items.get(i2).accentId >= 0 ? this.items.get(i2).accentId : this.items.get(i2).themeInfo.currentAccentId) == 99) {
                        this.items.get(i2).patternBgColor = -2368069;
                        this.items.get(i2).patternBgGradientColor1 = -9722489;
                        this.items.get(i2).patternBgGradientColor2 = -2762611;
                        this.items.get(i2).patternBgGradientColor3 = -7817084;
                    }
                }
            }
        }
    }

    public ThemeItem getThemeItem(int i) {
        return this.items.get(i);
    }

    public static void saveCustomTheme(Theme.ThemeInfo themeInfo, int i) {
        SparseArray<Theme.ThemeAccent> sparseArray;
        Theme.ThemeAccent themeAccent;
        if (themeInfo != null) {
            if (i >= 0 && (sparseArray = themeInfo.themeAccentsMap) != null && ((themeAccent = sparseArray.get(i)) == null || themeAccent.isDefault)) {
                return;
            }
            if (themeInfo.getKey().equals("Blue") && i == 99) {
                return;
            }
            if (themeInfo.getKey().equals("Day") && i == 9) {
                return;
            }
            if (themeInfo.getKey().equals("Night") && i == 0) {
                return;
            }
            if (!themeInfo.getKey().equals("Dark Blue") || i != 0) {
                boolean isDark = themeInfo.isDark();
                ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit().putString(isDark ? "lastDarkCustomTheme" : "lastDayCustomTheme", themeInfo.getKey()).putInt(isDark ? "lastDarkCustomThemeAccentId" : "lastDayCustomThemeAccentId", i).apply();
            }
        }
    }
}
