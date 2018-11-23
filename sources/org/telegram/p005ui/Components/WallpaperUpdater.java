package org.telegram.p005ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.p000v4.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;

/* renamed from: org.telegram.ui.Components.WallpaperUpdater */
public class WallpaperUpdater {
    private String currentPicturePath;
    private File currentWallpaperPath;
    private WallpaperUpdaterDelegate delegate;
    private Activity parentActivity;
    private File picturePath = null;

    /* renamed from: org.telegram.ui.Components.WallpaperUpdater$WallpaperUpdaterDelegate */
    public interface WallpaperUpdaterDelegate {
        void didSelectWallpaper(File file, Bitmap bitmap);

        void needOpenColorPicker();
    }

    public WallpaperUpdater(Activity activity, WallpaperUpdaterDelegate wallpaperUpdaterDelegate) {
        this.parentActivity = activity;
        this.delegate = wallpaperUpdaterDelegate;
        this.currentWallpaperPath = new File(FileLoader.getDirectory(4), Utilities.random.nextInt() + ".jpg");
    }

    public void showAlert(boolean fromTheme) {
        Builder builder = new Builder(this.parentActivity);
        builder.setItems(fromTheme ? new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("SelectColor", R.string.SelectColor), LocaleController.getString("Default", R.string.Default), LocaleController.getString("Cancel", R.string.Cancel)} : new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("Cancel", R.string.Cancel)}, new WallpaperUpdater$$Lambda$0(this, fromTheme));
        builder.show();
    }

    final /* synthetic */ void lambda$showAlert$0$WallpaperUpdater(boolean fromTheme, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                File image = AndroidUtilities.generatePicturePath();
                if (image != null) {
                    if (VERSION.SDK_INT >= 24) {
                        takePictureIntent.putExtra("output", FileProvider.getUriForFile(this.parentActivity, "org.telegram.messenger.beta.provider", image));
                        takePictureIntent.addFlags(2);
                        takePictureIntent.addFlags(1);
                    } else {
                        takePictureIntent.putExtra("output", Uri.fromFile(image));
                    }
                    this.currentPicturePath = image.getAbsolutePath();
                }
                this.parentActivity.startActivityForResult(takePictureIntent, 10);
            } catch (Throwable e) {
                try {
                    FileLog.m13e(e);
                } catch (Throwable e2) {
                    FileLog.m13e(e2);
                }
            }
        } else if (i == 1) {
            Intent photoPickerIntent = new Intent("android.intent.action.PICK");
            photoPickerIntent.setType("image/*");
            this.parentActivity.startActivityForResult(photoPickerIntent, 11);
        } else if (!fromTheme) {
        } else {
            if (i == 2) {
                this.delegate.needOpenColorPicker();
            } else if (i == 3) {
                this.delegate.didSelectWallpaper(null, null);
            }
        }
    }

    public void cleanup() {
        this.currentWallpaperPath.delete();
    }

    public File getCurrentWallpaperPath() {
        return this.currentWallpaperPath;
    }

    public String getCurrentPicturePath() {
        return this.currentPicturePath;
    }

    public void setCurrentPicturePath(String value) {
        this.currentPicturePath = value;
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0056 A:{SYNTHETIC, Splitter: B:26:0x0056} */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x004a A:{SYNTHETIC, Splitter: B:20:0x004a} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Throwable e;
        Throwable th;
        if (resultCode != -1) {
            return;
        }
        Point screenSize;
        Bitmap bitmap;
        if (requestCode == 10) {
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            FileOutputStream stream = null;
            try {
                screenSize = AndroidUtilities.getRealScreenSize();
                bitmap = ImageLoader.loadBitmap(this.currentPicturePath, null, (float) screenSize.x, (float) screenSize.y, true);
                FileOutputStream stream2 = new FileOutputStream(this.currentWallpaperPath);
                try {
                    bitmap.compress(CompressFormat.JPEG, 87, stream2);
                    this.delegate.didSelectWallpaper(this.currentWallpaperPath, bitmap);
                    if (stream2 != null) {
                        try {
                            stream2.close();
                        } catch (Throwable e2) {
                            FileLog.m13e(e2);
                            stream = stream2;
                        }
                    }
                    stream = stream2;
                } catch (Exception e3) {
                    e2 = e3;
                    stream = stream2;
                    try {
                        FileLog.m13e(e2);
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Throwable e22) {
                                FileLog.m13e(e22);
                            }
                        }
                        this.currentPicturePath = null;
                    } catch (Throwable th2) {
                        th = th2;
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Throwable e222) {
                                FileLog.m13e(e222);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    stream = stream2;
                    if (stream != null) {
                    }
                    throw th;
                }
            } catch (Exception e4) {
                e222 = e4;
                FileLog.m13e(e222);
                if (stream != null) {
                }
                this.currentPicturePath = null;
            }
            this.currentPicturePath = null;
        } else if (requestCode == 11 && data != null && data.getData() != null) {
            try {
                screenSize = AndroidUtilities.getRealScreenSize();
                bitmap = ImageLoader.loadBitmap(null, data.getData(), (float) screenSize.x, (float) screenSize.y, true);
                bitmap.compress(CompressFormat.JPEG, 87, new FileOutputStream(this.currentWallpaperPath));
                this.delegate.didSelectWallpaper(this.currentWallpaperPath, bitmap);
            } catch (Throwable e2222) {
                FileLog.m13e(e2222);
            }
        }
    }
}
