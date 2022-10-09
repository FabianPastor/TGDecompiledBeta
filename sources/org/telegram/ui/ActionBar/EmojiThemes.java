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
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class EmojiThemes {
    private static final String[] previewColorKeys = {"chat_inBubble", "chat_outBubble", "featuredStickers_addButton", "chat_wallpaper", "chat_wallpaper_gradient_to", "key_chat_wallpaper_gradient_to2", "key_chat_wallpaper_gradient_to3", "chat_wallpaper_gradient_rotation"};
    public String emoji;
    public ArrayList<ThemeItem> items = new ArrayList<>();
    public boolean showAsDefaultStub;

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
        String string = sharedPreferences.getString("lastDayCustomTheme", null);
        int i2 = sharedPreferences.getInt("lastDayCustomThemeAccentId", -1);
        int i3 = 99;
        String str = "Blue";
        if (string == null || Theme.getTheme(string) == null) {
            string = sharedPreferences.getString("lastDayTheme", str);
            Theme.ThemeInfo theme = Theme.getTheme(string);
            if (theme == null) {
                string = str;
                i2 = 99;
            } else {
                i2 = theme.currentAccentId;
            }
            sharedPreferences.edit().putString("lastDayCustomTheme", string).apply();
        } else if (i2 == -1) {
            i2 = Theme.getTheme(string).lastAccentId;
        }
        if (i2 != -1) {
            str = string;
            i3 = i2;
        }
        String string2 = sharedPreferences.getString("lastDarkCustomTheme", null);
        int i4 = sharedPreferences.getInt("lastDarkCustomThemeAccentId", -1);
        String str2 = "Dark Blue";
        if (string2 == null || Theme.getTheme(string2) == null) {
            string2 = sharedPreferences.getString("lastDarkTheme", str2);
            Theme.ThemeInfo theme2 = Theme.getTheme(string2);
            if (theme2 == null) {
                string2 = str2;
                i4 = 0;
            } else {
                i4 = theme2.currentAccentId;
            }
            sharedPreferences.edit().putString("lastDarkCustomTheme", string2).apply();
        } else if (i4 == -1) {
            i4 = Theme.getTheme(str).lastAccentId;
        }
        if (i4 != -1) {
            str2 = string2;
            i = i4;
        }
        ThemeItem themeItem = new ThemeItem();
        themeItem.themeInfo = Theme.getTheme(str);
        themeItem.accentId = i3;
        emojiThemes.items.add(themeItem);
        emojiThemes.items.add(null);
        ThemeItem themeItem2 = new ThemeItem();
        themeItem2.themeInfo = Theme.getTheme(str2);
        themeItem2.accentId = i;
        emojiThemes.items.add(themeItem2);
        emojiThemes.items.add(null);
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

    public static EmojiThemes createHomeQrTheme() {
        EmojiThemes emojiThemes = new EmojiThemes();
        emojiThemes.emoji = "🏠";
        ThemeItem themeItem = new ThemeItem();
        themeItem.themeInfo = Theme.getTheme("Blue");
        themeItem.accentId = 99;
        emojiThemes.items.add(themeItem);
        ThemeItem themeItem2 = new ThemeItem();
        themeItem2.themeInfo = Theme.getTheme("Dark Blue");
        themeItem2.accentId = 0;
        emojiThemes.items.add(themeItem2);
        return emojiThemes;
    }

    public void initColors() {
        getPreviewColors(0, 0);
        getPreviewColors(0, 1);
    }

    public String getEmoticon() {
        return this.emoji;
    }

    public TLRPC$TL_theme getTlTheme(int i) {
        return this.items.get(i).tlTheme;
    }

    public TLRPC$WallPaper getWallpaper(int i) {
        TLRPC$TL_theme tlTheme;
        int i2 = this.items.get(i).settingsIndex;
        if (i2 < 0 || (tlTheme = getTlTheme(i)) == null) {
            return null;
        }
        return tlTheme.settings.get(i2).wallpaper;
    }

    public String getWallpaperLink(int i) {
        return this.items.get(i).wallpaperLink;
    }

    public int getSettingsIndex(int i) {
        return this.items.get(i).settingsIndex;
    }

    public HashMap<String, Integer> getPreviewColors(int i, int i2) {
        Theme.ThemeAccent themeAccent;
        HashMap<String, Integer> hashMap = this.items.get(i2).currentPreviewColors;
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
        HashMap<String, Integer> hashMap2 = new HashMap<>();
        String[] strArr = new String[1];
        if (themeInfo.pathToFile != null) {
            hashMap2.putAll(Theme.getThemeFileValues(new File(themeInfo.pathToFile), null, strArr));
        } else {
            String str = themeInfo.assetName;
            if (str != null) {
                hashMap2.putAll(Theme.getThemeFileValues(null, str, strArr));
            }
        }
        int i3 = 0;
        this.items.get(i2).wallpaperLink = strArr[0];
        if (themeAccent != null) {
            HashMap<String, Integer> hashMap3 = new HashMap<>(hashMap2);
            themeAccent.fillAccentColors(hashMap2, hashMap3);
            hashMap2.clear();
            hashMap2 = hashMap3;
        }
        HashMap<String, String> fallbackKeys = Theme.getFallbackKeys();
        this.items.get(i2).currentPreviewColors = new HashMap<>();
        while (true) {
            String[] strArr2 = previewColorKeys;
            if (i3 < strArr2.length) {
                String str2 = strArr2[i3];
                this.items.get(i2).currentPreviewColors.put(str2, hashMap2.get(str2));
                if (!this.items.get(i2).currentPreviewColors.containsKey(str2)) {
                    hashMap2.put(str2, hashMap2.get(fallbackKeys.get(str2)));
                }
                i3++;
            } else {
                hashMap2.clear();
                return this.items.get(i2).currentPreviewColors;
            }
        }
    }

    public HashMap<String, Integer> createColors(int i, int i2) {
        Theme.ThemeAccent themeAccent;
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
        HashMap<String, Integer> hashMap = new HashMap<>();
        String[] strArr = new String[1];
        if (themeInfo.pathToFile != null) {
            hashMap.putAll(Theme.getThemeFileValues(new File(themeInfo.pathToFile), null, strArr));
        } else {
            String str = themeInfo.assetName;
            if (str != null) {
                hashMap.putAll(Theme.getThemeFileValues(null, str, strArr));
            }
        }
        this.items.get(i2).wallpaperLink = strArr[0];
        if (themeAccent != null) {
            HashMap<String, Integer> hashMap2 = new HashMap<>(hashMap);
            themeAccent.fillAccentColors(hashMap, hashMap2);
            hashMap.clear();
            hashMap = hashMap2;
        }
        for (Map.Entry<String, String> entry : Theme.getFallbackKeys().entrySet()) {
            String key = entry.getKey();
            if (!hashMap.containsKey(key)) {
                hashMap.put(key, hashMap.get(entry.getValue()));
            }
        }
        for (Map.Entry<String, Integer> entry2 : Theme.getDefaultColors().entrySet()) {
            if (!hashMap.containsKey(entry2.getKey())) {
                hashMap.put(entry2.getKey(), entry2.getValue());
            }
        }
        return hashMap;
    }

    public Theme.ThemeInfo getThemeInfo(int i) {
        return this.items.get(i).themeInfo;
    }

    public void loadWallpaper(int i, final ResultCallback<Pair<Long, Bitmap>> resultCallback) {
        final TLRPC$WallPaper wallpaper = getWallpaper(i);
        if (wallpaper != null) {
            final long j = getTlTheme(i).id;
            ChatThemeController.getWallpaperBitmap(j, new ResultCallback() { // from class: org.telegram.ui.ActionBar.EmojiThemes$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.ResultCallback
                public final void onComplete(Object obj) {
                    EmojiThemes.lambda$loadWallpaper$1(ResultCallback.this, j, wallpaper, (Bitmap) obj);
                }

                @Override // org.telegram.tgnet.ResultCallback
                public /* synthetic */ void onError(TLRPC$TL_error tLRPC$TL_error) {
                    ResultCallback.CC.$default$onError(this, tLRPC$TL_error);
                }
            });
        } else if (resultCallback == null) {
        } else {
            resultCallback.onComplete(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadWallpaper$1(final ResultCallback resultCallback, final long j, TLRPC$WallPaper tLRPC$WallPaper, Bitmap bitmap) {
        if (bitmap != null && resultCallback != null) {
            resultCallback.onComplete(new Pair(Long.valueOf(j), bitmap));
            return;
        }
        ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$WallPaper.document);
        ImageReceiver imageReceiver = new ImageReceiver();
        Point point = AndroidUtilities.displaySize;
        int min = Math.min(point.x, point.y);
        Point point2 = AndroidUtilities.displaySize;
        int max = Math.max(point2.x, point2.y);
        imageReceiver.setImage(forDocument, ((int) (min / AndroidUtilities.density)) + "_" + ((int) (max / AndroidUtilities.density)) + "_f", null, ".jpg", tLRPC$WallPaper, 1);
        imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.ui.ActionBar.EmojiThemes$$ExternalSyntheticLambda1
            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public final void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                EmojiThemes.lambda$loadWallpaper$0(ResultCallback.this, j, imageReceiver2, z, z2, z3);
            }

            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver2) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
            }
        });
        ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadWallpaper$0(ResultCallback resultCallback, long j, ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
        if (!z || bitmapSafe == null) {
            return;
        }
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

    public void loadWallpaperThumb(int i, final ResultCallback<Pair<Long, Bitmap>> resultCallback) {
        TLRPC$WallPaper wallpaper = getWallpaper(i);
        if (wallpaper == null) {
            if (resultCallback == null) {
                return;
            }
            resultCallback.onComplete(null);
            return;
        }
        final long j = getTlTheme(i).id;
        Bitmap wallpaperThumbBitmap = ChatThemeController.getWallpaperThumbBitmap(j);
        final File wallpaperThumbFile = getWallpaperThumbFile(j);
        if (wallpaperThumbBitmap == null && wallpaperThumbFile.exists() && wallpaperThumbFile.length() > 0) {
            try {
                wallpaperThumbBitmap = BitmapFactory.decodeFile(wallpaperThumbFile.getAbsolutePath());
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (wallpaperThumbBitmap != null) {
            if (resultCallback == null) {
                return;
            }
            resultCallback.onComplete(new Pair<>(Long.valueOf(j), wallpaperThumbBitmap));
            return;
        }
        TLRPC$Document tLRPC$Document = wallpaper.document;
        if (tLRPC$Document == null) {
            if (resultCallback == null) {
                return;
            }
            resultCallback.onComplete(new Pair<>(Long.valueOf(j), null));
            return;
        }
        ImageLocation forDocument = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 140), wallpaper.document);
        ImageReceiver imageReceiver = new ImageReceiver();
        imageReceiver.setImage(forDocument, "120_140", null, null, null, 1);
        imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.ui.ActionBar.EmojiThemes$$ExternalSyntheticLambda2
            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public final void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                EmojiThemes.lambda$loadWallpaperThumb$3(ResultCallback.this, j, wallpaperThumbFile, imageReceiver2, z, z2, z3);
            }

            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver2) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
            }
        });
        ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadWallpaperThumb$3(ResultCallback resultCallback, long j, final File file, ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
        if (!z || bitmapSafe == null || bitmapSafe.bitmap.isRecycled()) {
            return;
        }
        final Bitmap bitmap = bitmapSafe.bitmap;
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
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ActionBar.EmojiThemes$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiThemes.lambda$loadWallpaperThumb$2(file, bitmap);
                }
            });
        } else if (resultCallback == null) {
        } else {
            resultCallback.onComplete(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadWallpaperThumb$2(File file, Bitmap bitmap) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 87, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void preloadWallpaper() {
        loadWallpaperThumb(0, null);
        loadWallpaperThumb(1, null);
        loadWallpaper(0, null);
        loadWallpaper(1, null);
    }

    private File getWallpaperThumbFile(long j) {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        return new File(filesDirFixed, "wallpaper_thumb_" + j + ".png");
    }

    public static Theme.ThemeInfo getDefaultThemeInfo(boolean z) {
        String string;
        Theme.ThemeInfo currentNightTheme = z ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
        if (z != currentNightTheme.isDark()) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
            String str = "Dark Blue";
            if (z) {
                string = sharedPreferences.getString("lastDarkTheme", str);
            } else {
                string = sharedPreferences.getString("lastDayTheme", "Blue");
            }
            currentNightTheme = Theme.getTheme(string);
            if (currentNightTheme == null) {
                if (!z) {
                    str = "Blue";
                }
                currentNightTheme = Theme.getTheme(str);
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
                HashMap<String, Integer> previewColors = getPreviewColors(i, i2);
                Integer num = previewColors.get("chat_inBubble");
                if (num == null) {
                    num = Integer.valueOf(Theme.getDefaultColor("chat_inBubble"));
                }
                this.items.get(i2).inBubbleColor = num.intValue();
                Integer num2 = previewColors.get("chat_outBubble");
                if (num2 == null) {
                    num2 = Integer.valueOf(Theme.getDefaultColor("chat_outBubble"));
                }
                this.items.get(i2).outBubbleColor = num2.intValue();
                Integer num3 = previewColors.get("featuredStickers_addButton");
                if (num3 == null) {
                    num3 = Integer.valueOf(Theme.getDefaultColor("featuredStickers_addButton"));
                }
                this.items.get(i2).outLineColor = num3.intValue();
                Integer num4 = previewColors.get("chat_wallpaper");
                if (num4 == null) {
                    this.items.get(i2).patternBgColor = 0;
                } else {
                    this.items.get(i2).patternBgColor = num4.intValue();
                }
                Integer num5 = previewColors.get("chat_wallpaper_gradient_to");
                if (num5 == null) {
                    this.items.get(i2).patternBgGradientColor1 = 0;
                } else {
                    this.items.get(i2).patternBgGradientColor1 = num5.intValue();
                }
                Integer num6 = previewColors.get("key_chat_wallpaper_gradient_to2");
                if (num6 == null) {
                    this.items.get(i2).patternBgGradientColor2 = 0;
                } else {
                    this.items.get(i2).patternBgGradientColor2 = num6.intValue();
                }
                Integer num7 = previewColors.get("key_chat_wallpaper_gradient_to3");
                if (num7 == null) {
                    this.items.get(i2).patternBgGradientColor3 = 0;
                } else {
                    this.items.get(i2).patternBgGradientColor3 = num7.intValue();
                }
                Integer num8 = previewColors.get("chat_wallpaper_gradient_rotation");
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
        if (themeInfo == null) {
            return;
        }
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
        if (themeInfo.getKey().equals("Dark Blue") && i == 0) {
            return;
        }
        boolean isDark = themeInfo.isDark();
        ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit().putString(isDark ? "lastDarkCustomTheme" : "lastDayCustomTheme", themeInfo.getKey()).putInt(isDark ? "lastDarkCustomThemeAccentId" : "lastDayCustomThemeAccentId", i).apply();
    }

    /* loaded from: classes3.dex */
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
        TLRPC$TL_theme tlTheme;
        private String wallpaperLink;
    }
}
