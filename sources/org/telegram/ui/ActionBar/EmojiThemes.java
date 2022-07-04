package org.telegram.ui.ActionBar;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import java.io.File;
import java.io.FileOutputStream;
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
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class EmojiThemes {
    private static final String[] previewColorKeys = {"chat_inBubble", "chat_outBubble", "featuredStickers_addButton", "chat_wallpaper", "chat_wallpaper_gradient_to", "key_chat_wallpaper_gradient_to2", "key_chat_wallpaper_gradient_to3", "chat_wallpaper_gradient_rotation"};
    int currentIndex = 0;
    public String emoji;
    public ArrayList<ThemeItem> items = new ArrayList<>();
    public boolean showAsDefaultStub;

    public static class ThemeItem {
        public int accentId = -1;
        public HashMap<String, Integer> currentPreviewColors;
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
        TLRPC.TL_theme tlTheme;
        /* access modifiers changed from: private */
        public String wallpaperLink;
    }

    private EmojiThemes() {
    }

    public EmojiThemes(TLRPC.TL_theme chatThemeObject, boolean isDefault) {
        this.showAsDefaultStub = isDefault;
        this.emoji = chatThemeObject.emoticon;
        if (!isDefault) {
            ThemeItem lightTheme = new ThemeItem();
            lightTheme.tlTheme = chatThemeObject;
            lightTheme.settingsIndex = 0;
            this.items.add(lightTheme);
            ThemeItem darkTheme = new ThemeItem();
            darkTheme.tlTheme = chatThemeObject;
            darkTheme.settingsIndex = 1;
            this.items.add(darkTheme);
        }
    }

    public static EmojiThemes createPreviewFullTheme(TLRPC.TL_theme tl_theme) {
        EmojiThemes chatTheme = new EmojiThemes();
        chatTheme.emoji = tl_theme.emoticon;
        for (int i = 0; i < tl_theme.settings.size(); i++) {
            ThemeItem theme = new ThemeItem();
            theme.tlTheme = tl_theme;
            theme.settingsIndex = i;
            chatTheme.items.add(theme);
        }
        return chatTheme;
    }

    public static EmojiThemes createChatThemesDefault() {
        EmojiThemes themeItem = new EmojiThemes();
        themeItem.emoji = "❌";
        themeItem.showAsDefaultStub = true;
        ThemeItem lightTheme = new ThemeItem();
        lightTheme.themeInfo = getDefaultThemeInfo(true);
        themeItem.items.add(lightTheme);
        ThemeItem darkTheme = new ThemeItem();
        darkTheme.themeInfo = getDefaultThemeInfo(false);
        themeItem.items.add(darkTheme);
        return themeItem;
    }

    public static EmojiThemes createPreviewCustom() {
        EmojiThemes themeItem = new EmojiThemes();
        themeItem.emoji = "🎨";
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        String lastDayCustomTheme = preferences.getString("lastDayCustomTheme", (String) null);
        int dayAccentId = preferences.getInt("lastDayCustomThemeAccentId", -1);
        if (lastDayCustomTheme == null || Theme.getTheme(lastDayCustomTheme) == null) {
            lastDayCustomTheme = preferences.getString("lastDayTheme", "Blue");
            Theme.ThemeInfo themeInfo = Theme.getTheme(lastDayCustomTheme);
            if (themeInfo == null) {
                lastDayCustomTheme = "Blue";
                dayAccentId = 99;
            } else {
                dayAccentId = themeInfo.currentAccentId;
            }
            preferences.edit().putString("lastDayCustomTheme", lastDayCustomTheme).apply();
        } else if (dayAccentId == -1) {
            dayAccentId = Theme.getTheme(lastDayCustomTheme).lastAccentId;
        }
        if (dayAccentId == -1) {
            lastDayCustomTheme = "Blue";
            dayAccentId = 99;
        }
        String lastDarkCustomTheme = preferences.getString("lastDarkCustomTheme", (String) null);
        int darkAccentId = preferences.getInt("lastDarkCustomThemeAccentId", -1);
        if (lastDarkCustomTheme == null || Theme.getTheme(lastDarkCustomTheme) == null) {
            lastDarkCustomTheme = preferences.getString("lastDarkTheme", "Dark Blue");
            Theme.ThemeInfo themeInfo2 = Theme.getTheme(lastDarkCustomTheme);
            if (themeInfo2 == null) {
                lastDarkCustomTheme = "Dark Blue";
                darkAccentId = 0;
            } else {
                darkAccentId = themeInfo2.currentAccentId;
            }
            preferences.edit().putString("lastDarkCustomTheme", lastDarkCustomTheme).apply();
        } else if (darkAccentId == -1) {
            darkAccentId = Theme.getTheme(lastDayCustomTheme).lastAccentId;
        }
        if (darkAccentId == -1) {
            lastDarkCustomTheme = "Dark Blue";
            darkAccentId = 0;
        }
        ThemeItem lightTheme = new ThemeItem();
        lightTheme.themeInfo = Theme.getTheme(lastDayCustomTheme);
        lightTheme.accentId = dayAccentId;
        themeItem.items.add(lightTheme);
        themeItem.items.add((Object) null);
        ThemeItem darkTheme = new ThemeItem();
        darkTheme.themeInfo = Theme.getTheme(lastDarkCustomTheme);
        darkTheme.accentId = darkAccentId;
        themeItem.items.add(darkTheme);
        themeItem.items.add((Object) null);
        return themeItem;
    }

    public static EmojiThemes createHomePreviewTheme() {
        EmojiThemes themeItem = new EmojiThemes();
        themeItem.emoji = "🏠";
        ThemeItem blue = new ThemeItem();
        blue.themeInfo = Theme.getTheme("Blue");
        blue.accentId = 99;
        themeItem.items.add(blue);
        ThemeItem day = new ThemeItem();
        day.themeInfo = Theme.getTheme("Day");
        day.accentId = 9;
        themeItem.items.add(day);
        ThemeItem night = new ThemeItem();
        night.themeInfo = Theme.getTheme("Night");
        night.accentId = 0;
        themeItem.items.add(night);
        ThemeItem nightBlue = new ThemeItem();
        nightBlue.themeInfo = Theme.getTheme("Dark Blue");
        nightBlue.accentId = 0;
        themeItem.items.add(nightBlue);
        return themeItem;
    }

    public static EmojiThemes createHomeQrTheme() {
        EmojiThemes themeItem = new EmojiThemes();
        themeItem.emoji = "🏠";
        ThemeItem blue = new ThemeItem();
        blue.themeInfo = Theme.getTheme("Blue");
        blue.accentId = 99;
        themeItem.items.add(blue);
        ThemeItem nightBlue = new ThemeItem();
        nightBlue.themeInfo = Theme.getTheme("Dark Blue");
        nightBlue.accentId = 0;
        themeItem.items.add(nightBlue);
        return themeItem;
    }

    public void initColors() {
        getPreviewColors(0, 0);
        getPreviewColors(0, 1);
    }

    public String getEmoticon() {
        return this.emoji;
    }

    public TLRPC.TL_theme getTlTheme(int index) {
        return this.items.get(index).tlTheme;
    }

    public TLRPC.WallPaper getWallpaper(int index) {
        TLRPC.TL_theme tlTheme;
        int settingsIndex = this.items.get(index).settingsIndex;
        if (settingsIndex < 0 || (tlTheme = getTlTheme(index)) == null) {
            return null;
        }
        return tlTheme.settings.get(settingsIndex).wallpaper;
    }

    public String getWallpaperLink(int index) {
        return this.items.get(index).wallpaperLink;
    }

    public int getSettingsIndex(int index) {
        return this.items.get(index).settingsIndex;
    }

    public HashMap<String, Integer> getPreviewColors(int currentAccount, int index) {
        HashMap<String, Integer> currentColors;
        HashMap<String, Integer> currentColors2 = this.items.get(index).currentPreviewColors;
        if (currentColors2 != null) {
            return currentColors2;
        }
        Theme.ThemeInfo themeInfo = getThemeInfo(index);
        Theme.ThemeAccent accent = null;
        if (themeInfo == null) {
            int settingsIndex = getSettingsIndex(index);
            TLRPC.TL_theme tlTheme = getTlTheme(index);
            themeInfo = new Theme.ThemeInfo(Theme.getTheme(Theme.getBaseThemeKey(tlTheme.settings.get(settingsIndex))));
            accent = themeInfo.createNewAccent(tlTheme, currentAccount, true, settingsIndex);
            themeInfo.setCurrentAccentId(accent.id);
        } else if (themeInfo.themeAccentsMap != null) {
            accent = themeInfo.themeAccentsMap.get(this.items.get(index).accentId);
        }
        HashMap<String, Integer> currentColorsNoAccent = new HashMap<>();
        String[] wallpaperLink = new String[1];
        if (themeInfo.pathToFile != null) {
            currentColorsNoAccent.putAll(Theme.getThemeFileValues(new File(themeInfo.pathToFile), (String) null, wallpaperLink));
        } else if (themeInfo.assetName != null) {
            currentColorsNoAccent.putAll(Theme.getThemeFileValues((File) null, themeInfo.assetName, wallpaperLink));
        }
        String unused = this.items.get(index).wallpaperLink = wallpaperLink[0];
        if (accent != null) {
            currentColors = new HashMap<>(currentColorsNoAccent);
            accent.fillAccentColors(currentColorsNoAccent, currentColors);
            currentColorsNoAccent.clear();
        } else {
            currentColors = currentColorsNoAccent;
        }
        HashMap<String, String> fallbackKeys = Theme.getFallbackKeys();
        this.items.get(index).currentPreviewColors = new HashMap<>();
        int i = 0;
        while (true) {
            String[] strArr = previewColorKeys;
            if (i < strArr.length) {
                String key = strArr[i];
                this.items.get(index).currentPreviewColors.put(key, currentColors.get(key));
                if (!this.items.get(index).currentPreviewColors.containsKey(key)) {
                    currentColors.put(key, currentColors.get(fallbackKeys.get(key)));
                }
                i++;
            } else {
                currentColors.clear();
                return this.items.get(index).currentPreviewColors;
            }
        }
    }

    public HashMap<String, Integer> createColors(int currentAccount, int index) {
        HashMap<String, Integer> currentColors;
        Theme.ThemeInfo themeInfo = getThemeInfo(index);
        Theme.ThemeAccent accent = null;
        if (themeInfo == null) {
            int settingsIndex = getSettingsIndex(index);
            TLRPC.TL_theme tlTheme = getTlTheme(index);
            themeInfo = new Theme.ThemeInfo(Theme.getTheme(Theme.getBaseThemeKey(tlTheme.settings.get(settingsIndex))));
            accent = themeInfo.createNewAccent(tlTheme, currentAccount, true, settingsIndex);
            themeInfo.setCurrentAccentId(accent.id);
        } else if (themeInfo.themeAccentsMap != null) {
            accent = themeInfo.themeAccentsMap.get(this.items.get(index).accentId);
        }
        HashMap<String, Integer> currentColorsNoAccent = new HashMap<>();
        String[] wallpaperLink = new String[1];
        if (themeInfo.pathToFile != null) {
            currentColorsNoAccent.putAll(Theme.getThemeFileValues(new File(themeInfo.pathToFile), (String) null, wallpaperLink));
        } else if (themeInfo.assetName != null) {
            currentColorsNoAccent.putAll(Theme.getThemeFileValues((File) null, themeInfo.assetName, wallpaperLink));
        }
        String unused = this.items.get(index).wallpaperLink = wallpaperLink[0];
        if (accent != null) {
            currentColors = new HashMap<>(currentColorsNoAccent);
            accent.fillAccentColors(currentColorsNoAccent, currentColors);
            currentColorsNoAccent.clear();
        } else {
            currentColors = currentColorsNoAccent;
        }
        for (Map.Entry<String, String> fallbackEntry : Theme.getFallbackKeys().entrySet()) {
            String colorKey = fallbackEntry.getKey();
            if (!currentColors.containsKey(colorKey)) {
                currentColors.put(colorKey, currentColors.get(fallbackEntry.getValue()));
            }
        }
        for (Map.Entry<String, Integer> entry : Theme.getDefaultColors().entrySet()) {
            if (!currentColors.containsKey(entry.getKey())) {
                currentColors.put(entry.getKey(), entry.getValue());
            }
        }
        return currentColors;
    }

    public Theme.ThemeInfo getThemeInfo(int index) {
        return this.items.get(index).themeInfo;
    }

    public void loadWallpaper(int index, ResultCallback<Pair<Long, Bitmap>> callback) {
        TLRPC.WallPaper wallPaper = getWallpaper(index);
        if (wallPaper != null) {
            long themeId = getTlTheme(index).id;
            ChatThemeController.getWallpaperBitmap(themeId, new EmojiThemes$$ExternalSyntheticLambda3(callback, themeId, wallPaper));
        } else if (callback != null) {
            callback.onComplete(null);
        }
    }

    static /* synthetic */ void lambda$loadWallpaper$1(ResultCallback callback, long themeId, TLRPC.WallPaper wallPaper, Bitmap cachedBitmap) {
        ResultCallback resultCallback = callback;
        Bitmap bitmap = cachedBitmap;
        if (bitmap == null || resultCallback == null) {
            ImageLocation imageLocation = ImageLocation.getForDocument(wallPaper.document);
            ImageReceiver imageReceiver = new ImageReceiver();
            int w = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            int h = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            imageReceiver.setImage(imageLocation, ((int) (((float) w) / AndroidUtilities.density)) + "_" + ((int) (((float) h) / AndroidUtilities.density)) + "_f", (Drawable) null, ".jpg", wallPaper, 1);
            imageReceiver.setDelegate(new EmojiThemes$$ExternalSyntheticLambda1(callback, themeId));
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
            return;
        }
        callback.onComplete(new Pair(Long.valueOf(themeId), bitmap));
    }

    static /* synthetic */ void lambda$loadWallpaper$0(ResultCallback callback, long themeId, ImageReceiver receiver, boolean set, boolean thumb, boolean memCache) {
        ImageReceiver.BitmapHolder holder = receiver.getBitmapSafe();
        if (set && holder != null) {
            Bitmap bitmap = holder.bitmap;
            if (bitmap == null && (holder.drawable instanceof BitmapDrawable)) {
                bitmap = ((BitmapDrawable) holder.drawable).getBitmap();
            }
            if (callback != null) {
                callback.onComplete(new Pair(Long.valueOf(themeId), bitmap));
            }
            ChatThemeController.saveWallpaperBitmap(bitmap, themeId);
        }
    }

    public void loadWallpaperThumb(int index, ResultCallback<Pair<Long, Bitmap>> callback) {
        ResultCallback<Pair<Long, Bitmap>> resultCallback = callback;
        TLRPC.WallPaper wallpaper = getWallpaper(index);
        if (wallpaper != null) {
            long themeId = getTlTheme(index).id;
            Bitmap bitmap = ChatThemeController.getWallpaperThumbBitmap(themeId);
            File file = getWallpaperThumbFile(themeId);
            if (bitmap == null && file.exists() && file.length() > 0) {
                try {
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            if (bitmap != null) {
                if (resultCallback != null) {
                    resultCallback.onComplete(new Pair(Long.valueOf(themeId), bitmap));
                }
            } else if (wallpaper.document != null) {
                TLRPC.PhotoSize thumbSize = FileLoader.getClosestPhotoSizeWithSize(wallpaper.document.thumbs, 140);
                ImageLocation imageLocation = ImageLocation.getForDocument(thumbSize, wallpaper.document);
                ImageReceiver imageReceiver = new ImageReceiver();
                TLRPC.PhotoSize photoSize = thumbSize;
                ImageReceiver imageReceiver2 = imageReceiver;
                imageReceiver.setImage(imageLocation, "120_140", (Drawable) null, (String) null, (Object) null, 1);
                imageReceiver2.setDelegate(new EmojiThemes$$ExternalSyntheticLambda2(resultCallback, themeId, file));
                ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver2);
            } else if (resultCallback != null) {
                resultCallback.onComplete(new Pair(Long.valueOf(themeId), (Object) null));
            }
        } else if (resultCallback != null) {
            resultCallback.onComplete(null);
        }
    }

    static /* synthetic */ void lambda$loadWallpaperThumb$3(ResultCallback callback, long themeId, File file, ImageReceiver receiver, boolean set, boolean thumb, boolean memCache) {
        ImageReceiver.BitmapHolder holder = receiver.getBitmapSafe();
        if (set && holder != null && !holder.bitmap.isRecycled()) {
            Bitmap resultBitmap = holder.bitmap;
            if (resultBitmap == null && (holder.drawable instanceof BitmapDrawable)) {
                resultBitmap = ((BitmapDrawable) holder.drawable).getBitmap();
            }
            if (resultBitmap != null) {
                if (callback != null) {
                    callback.onComplete(new Pair(Long.valueOf(themeId), resultBitmap));
                }
                Utilities.globalQueue.postRunnable(new EmojiThemes$$ExternalSyntheticLambda0(file, resultBitmap));
            } else if (callback != null) {
                callback.onComplete(null);
            }
        }
    }

    static /* synthetic */ void lambda$loadWallpaperThumb$2(File file, Bitmap saveBitmap) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            saveBitmap.compress(Bitmap.CompressFormat.PNG, 87, outputStream);
            outputStream.close();
            return;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return;
        } catch (Throwable th) {
        }
        throw th;
    }

    public void preloadWallpaper() {
        loadWallpaperThumb(0, (ResultCallback<Pair<Long, Bitmap>>) null);
        loadWallpaperThumb(1, (ResultCallback<Pair<Long, Bitmap>>) null);
        loadWallpaper(0, (ResultCallback<Pair<Long, Bitmap>>) null);
        loadWallpaper(1, (ResultCallback<Pair<Long, Bitmap>>) null);
    }

    private File getWallpaperThumbFile(long themeId) {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        return new File(filesDirFixed, "wallpaper_thumb_" + themeId + ".png");
    }

    public static Theme.ThemeInfo getDefaultThemeInfo(boolean isDark) {
        String lastThemeName;
        Theme.ThemeInfo themeInfo = isDark ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
        if (isDark != themeInfo.isDark()) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
            String str = "Dark Blue";
            if (isDark) {
                lastThemeName = preferences.getString("lastDarkTheme", str);
            } else {
                lastThemeName = preferences.getString("lastDayTheme", "Blue");
            }
            themeInfo = Theme.getTheme(lastThemeName);
            if (themeInfo == null) {
                if (!isDark) {
                    str = "Blue";
                }
                themeInfo = Theme.getTheme(str);
            }
        }
        return new Theme.ThemeInfo(themeInfo);
    }

    public static void fillTlTheme(Theme.ThemeInfo themeInfo) {
        if (themeInfo.info == null) {
            themeInfo.info = new TLRPC.TL_theme();
        }
    }

    public static HashMap<String, Integer> getPreviewColors(Theme.ThemeInfo themeInfo) {
        HashMap<String, Integer> currentColorsNoAccent = new HashMap<>();
        if (themeInfo.pathToFile != null) {
            currentColorsNoAccent.putAll(Theme.getThemeFileValues(new File(themeInfo.pathToFile), (String) null, (String[]) null));
        } else if (themeInfo.assetName != null) {
            currentColorsNoAccent.putAll(Theme.getThemeFileValues((File) null, themeInfo.assetName, (String[]) null));
        }
        HashMap<String, Integer> currentColors = new HashMap<>(currentColorsNoAccent);
        Theme.ThemeAccent themeAccent = themeInfo.getAccent(false);
        if (themeAccent != null) {
            themeAccent.fillAccentColors(currentColorsNoAccent, currentColors);
        }
        return currentColors;
    }

    public int getAccentId(int themeIndex) {
        return this.items.get(themeIndex).accentId;
    }

    public void loadPreviewColors(int currentAccount) {
        for (int i = 0; i < this.items.size(); i++) {
            if (this.items.get(i) != null) {
                HashMap<String, Integer> colorsMap = getPreviewColors(currentAccount, i);
                Integer color = colorsMap.get("chat_inBubble");
                if (color == null) {
                    color = Integer.valueOf(Theme.getDefaultColor("chat_inBubble"));
                }
                this.items.get(i).inBubbleColor = color.intValue();
                Integer color2 = colorsMap.get("chat_outBubble");
                if (color2 == null) {
                    color2 = Integer.valueOf(Theme.getDefaultColor("chat_outBubble"));
                }
                this.items.get(i).outBubbleColor = color2.intValue();
                Integer color3 = colorsMap.get("featuredStickers_addButton");
                if (color3 == null) {
                    color3 = Integer.valueOf(Theme.getDefaultColor("featuredStickers_addButton"));
                }
                this.items.get(i).outLineColor = color3.intValue();
                Integer color4 = colorsMap.get("chat_wallpaper");
                if (color4 == null) {
                    this.items.get(i).patternBgColor = 0;
                } else {
                    this.items.get(i).patternBgColor = color4.intValue();
                }
                Integer color5 = colorsMap.get("chat_wallpaper_gradient_to");
                if (color5 == null) {
                    this.items.get(i).patternBgGradientColor1 = 0;
                } else {
                    this.items.get(i).patternBgGradientColor1 = color5.intValue();
                }
                Integer color6 = colorsMap.get("key_chat_wallpaper_gradient_to2");
                if (color6 == null) {
                    this.items.get(i).patternBgGradientColor2 = 0;
                } else {
                    this.items.get(i).patternBgGradientColor2 = color6.intValue();
                }
                Integer color7 = colorsMap.get("key_chat_wallpaper_gradient_to3");
                if (color7 == null) {
                    this.items.get(i).patternBgGradientColor3 = 0;
                } else {
                    this.items.get(i).patternBgGradientColor3 = color7.intValue();
                }
                Integer color8 = colorsMap.get("chat_wallpaper_gradient_rotation");
                if (color8 == null) {
                    this.items.get(i).patternBgRotation = 0;
                } else {
                    this.items.get(i).patternBgRotation = color8.intValue();
                }
                if (this.items.get(i).themeInfo != null && this.items.get(i).themeInfo.getKey().equals("Blue")) {
                    if ((this.items.get(i).accentId >= 0 ? this.items.get(i).accentId : this.items.get(i).themeInfo.currentAccentId) == 99) {
                        this.items.get(i).patternBgColor = -2368069;
                        this.items.get(i).patternBgGradientColor1 = -9722489;
                        this.items.get(i).patternBgGradientColor2 = -2762611;
                        this.items.get(i).patternBgGradientColor3 = -7817084;
                    }
                }
            }
        }
    }

    public ThemeItem getThemeItem(int index) {
        return this.items.get(index);
    }

    public static void saveCustomTheme(Theme.ThemeInfo themeInfo, int accentId) {
        Theme.ThemeAccent accent;
        if (themeInfo != null) {
            if (accentId >= 0 && themeInfo.themeAccentsMap != null && ((accent = themeInfo.themeAccentsMap.get(accentId)) == null || accent.isDefault)) {
                return;
            }
            if (themeInfo.getKey().equals("Blue") && accentId == 99) {
                return;
            }
            if (themeInfo.getKey().equals("Day") && accentId == 9) {
                return;
            }
            if (themeInfo.getKey().equals("Night") && accentId == 0) {
                return;
            }
            if (!themeInfo.getKey().equals("Dark Blue") || accentId != 0) {
                boolean dark = themeInfo.isDark();
                ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit().putString(dark ? "lastDarkCustomTheme" : "lastDayCustomTheme", themeInfo.getKey()).putInt(dark ? "lastDarkCustomThemeAccentId" : "lastDayCustomThemeAccentId", accentId).apply();
            }
        }
    }
}
