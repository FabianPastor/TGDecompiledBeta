package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import org.telegram.ui.PhotoCropActivity;
import org.telegram.ui.PhotoCropActivity.PhotoEditActivityDelegate;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;

public class AvatarUpdater
  implements NotificationCenter.NotificationCenterDelegate, PhotoCropActivity.PhotoEditActivityDelegate
{
  private TLRPC.PhotoSize bigPhoto;
  private boolean clearAfterUpdate = false;
  public String currentPicturePath;
  public AvatarUpdaterDelegate delegate;
  public BaseFragment parentFragment = null;
  File picturePath = null;
  public boolean returnOnly = false;
  private TLRPC.PhotoSize smallPhoto;
  public String uploadingAvatar = null;
  
  private void processBitmap(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {}
    do
    {
      do
      {
        return;
        this.smallPhoto = ImageLoader.scaleAndSaveImage(paramBitmap, 100.0F, 100.0F, 80, false);
        this.bigPhoto = ImageLoader.scaleAndSaveImage(paramBitmap, 800.0F, 800.0F, 80, false, 320, 320);
        paramBitmap.recycle();
      } while ((this.bigPhoto == null) || (this.smallPhoto == null));
      if (!this.returnOnly) {
        break;
      }
    } while (this.delegate == null);
    this.delegate.didUploadedPhoto(null, this.smallPhoto, this.bigPhoto);
    return;
    UserConfig.saveConfig(false);
    this.uploadingAvatar = (FileLoader.getInstance().getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg");
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
    FileLoader.getInstance().uploadFile(this.uploadingAvatar, false, true);
  }
  
  private void startCrop(String paramString, Uri paramUri)
  {
    for (;;)
    {
      Object localObject;
      try
      {
        LaunchActivity localLaunchActivity = (LaunchActivity)this.parentFragment.getParentActivity();
        if (localLaunchActivity == null) {
          return;
        }
        localObject = new Bundle();
        if (paramString != null)
        {
          ((Bundle)localObject).putString("photoPath", paramString);
          localObject = new PhotoCropActivity((Bundle)localObject);
          ((PhotoCropActivity)localObject).setDelegate(this);
          localLaunchActivity.presentFragment((BaseFragment)localObject);
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
        processBitmap(ImageLoader.loadBitmap(paramString, paramUri, 800.0F, 800.0F, true));
        return;
      }
      if (paramUri != null) {
        ((Bundle)localObject).putParcelable("photoUri", paramUri);
      }
    }
  }
  
  public void clear()
  {
    if (this.uploadingAvatar != null)
    {
      this.clearAfterUpdate = true;
      return;
    }
    this.parentFragment = null;
    this.delegate = null;
  }
  
  public void didFinishEdit(Bitmap paramBitmap)
  {
    processBitmap(paramBitmap);
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.FileDidUpload)
    {
      String str = (String)paramVarArgs[0];
      if ((this.uploadingAvatar != null) && (str.equals(this.uploadingAvatar)))
      {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailUpload);
        if (this.delegate != null) {
          this.delegate.didUploadedPhoto((TLRPC.InputFile)paramVarArgs[1], this.smallPhoto, this.bigPhoto);
        }
        this.uploadingAvatar = null;
        if (this.clearAfterUpdate)
        {
          this.parentFragment = null;
          this.delegate = null;
        }
      }
    }
    do
    {
      do
      {
        do
        {
          return;
        } while (paramInt != NotificationCenter.FileDidFailUpload);
        paramVarArgs = (String)paramVarArgs[0];
      } while ((this.uploadingAvatar == null) || (!paramVarArgs.equals(this.uploadingAvatar)));
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidUpload);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailUpload);
      this.uploadingAvatar = null;
    } while (!this.clearAfterUpdate);
    this.parentFragment = null;
    this.delegate = null;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, final Intent paramIntent)
  {
    if (paramInt2 == -1)
    {
      if (paramInt1 == 13)
      {
        PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
        paramInt2 = 0;
      }
    }
    else
    {
      try
      {
        int i = new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", 1);
        paramInt1 = paramInt2;
        switch (i)
        {
        default: 
          paramInt1 = paramInt2;
        }
      }
      catch (Exception paramIntent)
      {
        for (;;)
        {
          FileLog.e("tmessages", paramIntent);
          paramInt1 = paramInt2;
        }
      }
      paramIntent = new ArrayList();
      paramIntent.add(new MediaController.PhotoEntry(0, 0, 0L, this.currentPicturePath, paramInt1, false));
      PhotoViewer.getInstance().openPhotoForSelect(paramIntent, 0, 1, new PhotoViewer.EmptyPhotoViewerProvider()
      {
        public boolean allowCaption()
        {
          return false;
        }
        
        public void sendButtonPressed(int paramAnonymousInt)
        {
          Object localObject = null;
          MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)paramIntent.get(0);
          if (localPhotoEntry.imagePath != null) {
            localObject = localPhotoEntry.imagePath;
          }
          for (;;)
          {
            localObject = ImageLoader.loadBitmap((String)localObject, null, 800.0F, 800.0F, true);
            AvatarUpdater.this.processBitmap((Bitmap)localObject);
            return;
            if (localPhotoEntry.path != null) {
              localObject = localPhotoEntry.path;
            }
          }
        }
      }, null);
      AndroidUtilities.addMediaToGallery(this.currentPicturePath);
      this.currentPicturePath = null;
    }
    while ((paramInt1 != 14) || (paramIntent == null) || (paramIntent.getData() == null))
    {
      return;
      paramInt1 = 90;
      break;
      paramInt1 = 180;
      break;
      paramInt1 = 270;
      break;
    }
    startCrop(null, paramIntent.getData());
  }
  
  public void openCamera()
  {
    try
    {
      Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
      File localFile = AndroidUtilities.generatePicturePath();
      if (localFile != null)
      {
        if (Build.VERSION.SDK_INT < 24) {
          break label80;
        }
        localIntent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.beta.provider", localFile));
        localIntent.addFlags(2);
        localIntent.addFlags(1);
      }
      for (;;)
      {
        this.currentPicturePath = localFile.getAbsolutePath();
        this.parentFragment.startActivityForResult(localIntent, 13);
        return;
        label80:
        localIntent.putExtra("output", Uri.fromFile(localFile));
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public void openGallery()
  {
    if ((Build.VERSION.SDK_INT >= 23) && (this.parentFragment != null) && (this.parentFragment.getParentActivity() != null) && (this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
    {
      this.parentFragment.getParentActivity().requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 4);
      return;
    }
    PhotoAlbumPickerActivity localPhotoAlbumPickerActivity = new PhotoAlbumPickerActivity(true, false, false, null);
    localPhotoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate()
    {
      public void didSelectPhotos(ArrayList<String> paramAnonymousArrayList1, ArrayList<String> paramAnonymousArrayList2, ArrayList<ArrayList<TLRPC.InputDocument>> paramAnonymousArrayList, ArrayList<MediaController.SearchImage> paramAnonymousArrayList3)
      {
        if (!paramAnonymousArrayList1.isEmpty())
        {
          paramAnonymousArrayList1 = ImageLoader.loadBitmap((String)paramAnonymousArrayList1.get(0), null, 800.0F, 800.0F, true);
          AvatarUpdater.this.processBitmap(paramAnonymousArrayList1);
        }
      }
      
      public boolean didSelectVideo(String paramAnonymousString)
      {
        return true;
      }
      
      public void startPhotoSelectActivity()
      {
        try
        {
          Intent localIntent = new Intent("android.intent.action.GET_CONTENT");
          localIntent.setType("image/*");
          AvatarUpdater.this.parentFragment.startActivityForResult(localIntent, 14);
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
    this.parentFragment.presentFragment(localPhotoAlbumPickerActivity);
  }
  
  public static abstract interface AvatarUpdaterDelegate
  {
    public abstract void didUploadedPhoto(TLRPC.InputFile paramInputFile, TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/AvatarUpdater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */