package org.telegram.ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0488R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

public class WallpaperUpdater {
    private String currentPicturePath;
    private File currentWallpaperPath;
    private WallpaperUpdaterDelegate delegate;
    private Activity parentActivity;
    private File picturePath = null;

    public interface WallpaperUpdaterDelegate {
        void didSelectWallpaper(File file, Bitmap bitmap);

        void needOpenColorPicker();
    }

    public WallpaperUpdater(Activity activity, WallpaperUpdaterDelegate wallpaperUpdaterDelegate) {
        this.parentActivity = activity;
        this.delegate = wallpaperUpdaterDelegate;
        this.currentWallpaperPath = new File(FileLoader.getDirectory(4), Utilities.random.nextInt() + ".jpg");
    }

    public void showAlert(final boolean fromTheme) {
        Builder builder = new Builder(this.parentActivity);
        builder.setItems(fromTheme ? new CharSequence[]{LocaleController.getString("FromCamera", C0488R.string.FromCamera), LocaleController.getString("FromGalley", C0488R.string.FromGalley), LocaleController.getString("SelectColor", C0488R.string.SelectColor), LocaleController.getString("Default", C0488R.string.Default), LocaleController.getString("Cancel", C0488R.string.Cancel)} : new CharSequence[]{LocaleController.getString("FromCamera", C0488R.string.FromCamera), LocaleController.getString("FromGalley", C0488R.string.FromGalley), LocaleController.getString("Cancel", C0488R.string.Cancel)}, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    try {
                        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                        File image = AndroidUtilities.generatePicturePath();
                        if (image != null) {
                            if (VERSION.SDK_INT >= 24) {
                                takePictureIntent.putExtra("output", FileProvider.getUriForFile(WallpaperUpdater.this.parentActivity, "org.telegram.messenger.provider", image));
                                takePictureIntent.addFlags(2);
                                takePictureIntent.addFlags(1);
                            } else {
                                takePictureIntent.putExtra("output", Uri.fromFile(image));
                            }
                            WallpaperUpdater.this.currentPicturePath = image.getAbsolutePath();
                        }
                        WallpaperUpdater.this.parentActivity.startActivityForResult(takePictureIntent, 10);
                    } catch (Throwable e) {
                        try {
                            FileLog.m3e(e);
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                } else if (i == 1) {
                    Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                    photoPickerIntent.setType("image/*");
                    WallpaperUpdater.this.parentActivity.startActivityForResult(photoPickerIntent, 11);
                } else if (!fromTheme) {
                } else {
                    if (i == 2) {
                        WallpaperUpdater.this.delegate.needOpenColorPicker();
                    } else if (i == 3) {
                        WallpaperUpdater.this.delegate.didSelectWallpaper(null, null);
                    }
                }
            }
        });
        builder.show();
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
                            FileLog.m3e(e2);
                            stream = stream2;
                        }
                    }
                    stream = stream2;
                } catch (Exception e3) {
                    e2 = e3;
                    stream = stream2;
                    try {
                        FileLog.m3e(e2);
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Throwable e22) {
                                FileLog.m3e(e22);
                            }
                        }
                        this.currentPicturePath = null;
                    } catch (Throwable th2) {
                        th = th2;
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Throwable e222) {
                                FileLog.m3e(e222);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    stream = stream2;
                    if (stream != null) {
                        stream.close();
                    }
                    throw th;
                }
            } catch (Exception e4) {
                e222 = e4;
                FileLog.m3e(e222);
                if (stream != null) {
                    stream.close();
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
                FileLog.m3e(e2222);
            }
        }
    }
}
