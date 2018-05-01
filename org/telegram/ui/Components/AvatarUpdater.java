package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.media.ExifInterface;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC.FileLocation;
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
  private int currentAccount = UserConfig.selectedAccount;
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
    for (;;)
    {
      return;
      this.smallPhoto = ImageLoader.scaleAndSaveImage(paramBitmap, 100.0F, 100.0F, 80, false);
      this.bigPhoto = ImageLoader.scaleAndSaveImage(paramBitmap, 800.0F, 800.0F, 80, false, 320, 320);
      paramBitmap.recycle();
      if ((this.bigPhoto != null) && (this.smallPhoto != null)) {
        if (this.returnOnly)
        {
          if (this.delegate != null) {
            this.delegate.didUploadedPhoto(null, this.smallPhoto, this.bigPhoto);
          }
        }
        else
        {
          UserConfig.getInstance(this.currentAccount).saveConfig(false);
          this.uploadingAvatar = (FileLoader.getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg");
          NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
          NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailUpload);
          FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingAvatar, false, true, 16777216);
        }
      }
    }
  }
  
  private void startCrop(String paramString, Uri paramUri)
  {
    for (;;)
    {
      try
      {
        localLaunchActivity = (LaunchActivity)this.parentFragment.getParentActivity();
        if (localLaunchActivity == null) {
          return;
        }
      }
      catch (Exception localException)
      {
        LaunchActivity localLaunchActivity;
        Bundle localBundle;
        PhotoCropActivity localPhotoCropActivity;
        FileLog.e(localException);
        processBitmap(ImageLoader.loadBitmap(paramString, paramUri, 800.0F, 800.0F, true));
        continue;
        if (paramUri == null) {
          continue;
        }
        localBundle.putParcelable("photoUri", paramUri);
        continue;
      }
      localBundle = new android/os/Bundle;
      localBundle.<init>();
      if (paramString == null) {
        continue;
      }
      localBundle.putString("photoPath", paramString);
      localPhotoCropActivity = new org/telegram/ui/PhotoCropActivity;
      localPhotoCropActivity.<init>(localBundle);
      localPhotoCropActivity.setDelegate(this);
      localLaunchActivity.presentFragment(localPhotoCropActivity);
    }
  }
  
  public void clear()
  {
    if (this.uploadingAvatar != null) {
      this.clearAfterUpdate = true;
    }
    for (;;)
    {
      return;
      this.parentFragment = null;
      this.delegate = null;
    }
  }
  
  public void didFinishEdit(Bitmap paramBitmap)
  {
    processBitmap(paramBitmap);
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.FileDidUpload)
    {
      String str = (String)paramVarArgs[0];
      if ((this.uploadingAvatar != null) && (str.equals(this.uploadingAvatar)))
      {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
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
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.FileDidFailUpload)
      {
        paramVarArgs = (String)paramVarArgs[0];
        if ((this.uploadingAvatar != null) && (paramVarArgs.equals(this.uploadingAvatar)))
        {
          NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
          NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
          this.uploadingAvatar = null;
          if (this.clearAfterUpdate)
          {
            this.parentFragment = null;
            this.delegate = null;
          }
        }
      }
    }
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
        paramIntent = new android/support/media/ExifInterface;
        paramIntent.<init>(this.currentPicturePath);
        int i = paramIntent.getAttributeInt("Orientation", 1);
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
          FileLog.e(paramIntent);
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
        
        public boolean canScrollAway()
        {
          return false;
        }
        
        public void sendButtonPressed(int paramAnonymousInt, VideoEditedInfo paramAnonymousVideoEditedInfo)
        {
          paramAnonymousVideoEditedInfo = null;
          MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)paramIntent.get(0);
          if (localPhotoEntry.imagePath != null) {
            paramAnonymousVideoEditedInfo = localPhotoEntry.imagePath;
          }
          for (;;)
          {
            paramAnonymousVideoEditedInfo = ImageLoader.loadBitmap(paramAnonymousVideoEditedInfo, null, 800.0F, 800.0F, true);
            AvatarUpdater.this.processBitmap(paramAnonymousVideoEditedInfo);
            return;
            if (localPhotoEntry.path != null) {
              paramAnonymousVideoEditedInfo = localPhotoEntry.path;
            }
          }
        }
      }, null);
      AndroidUtilities.addMediaToGallery(this.currentPicturePath);
      this.currentPicturePath = null;
    }
    for (;;)
    {
      return;
      paramInt1 = 90;
      break;
      paramInt1 = 180;
      break;
      paramInt1 = 270;
      break;
      if ((paramInt1 == 14) && (paramIntent != null) && (paramIntent.getData() != null)) {
        startCrop(null, paramIntent.getData());
      }
    }
  }
  
  public void openCamera()
  {
    if ((this.parentFragment == null) || (this.parentFragment.getParentActivity() == null)) {}
    for (;;)
    {
      return;
      try
      {
        if ((Build.VERSION.SDK_INT < 23) || (this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0)) {
          break;
        }
        this.parentFragment.getParentActivity().requestPermissions(new String[] { "android.permission.CAMERA" }, 19);
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    }
    Intent localIntent = new android/content/Intent;
    localIntent.<init>("android.media.action.IMAGE_CAPTURE");
    File localFile = AndroidUtilities.generatePicturePath();
    if (localFile != null)
    {
      if (Build.VERSION.SDK_INT < 24) {
        break label157;
      }
      localIntent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.beta.provider", localFile));
      localIntent.addFlags(2);
      localIntent.addFlags(1);
    }
    for (;;)
    {
      this.currentPicturePath = localFile.getAbsolutePath();
      this.parentFragment.startActivityForResult(localIntent, 13);
      break;
      label157:
      localIntent.putExtra("output", Uri.fromFile(localFile));
    }
  }
  
  public void openGallery()
  {
    if (this.parentFragment == null) {}
    for (;;)
    {
      return;
      if ((Build.VERSION.SDK_INT >= 23) && (this.parentFragment != null) && (this.parentFragment.getParentActivity() != null) && (this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
      {
        this.parentFragment.getParentActivity().requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 4);
      }
      else
      {
        PhotoAlbumPickerActivity localPhotoAlbumPickerActivity = new PhotoAlbumPickerActivity(true, false, false, null);
        localPhotoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate()
        {
          public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> paramAnonymousArrayList)
          {
            if (!paramAnonymousArrayList.isEmpty())
            {
              paramAnonymousArrayList = ImageLoader.loadBitmap(((SendMessagesHelper.SendingMediaInfo)paramAnonymousArrayList.get(0)).path, null, 800.0F, 800.0F, true);
              AvatarUpdater.this.processBitmap(paramAnonymousArrayList);
            }
          }
          
          public void startPhotoSelectActivity()
          {
            try
            {
              Intent localIntent = new android/content/Intent;
              localIntent.<init>("android.intent.action.GET_CONTENT");
              localIntent.setType("image/*");
              AvatarUpdater.this.parentFragment.startActivityForResult(localIntent, 14);
              return;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                FileLog.e(localException);
              }
            }
          }
        });
        this.parentFragment.presentFragment(localPhotoAlbumPickerActivity);
      }
    }
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