package org.telegram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoPickerAlbumsCell
  extends FrameLayout
{
  private MediaController.AlbumEntry[] albumEntries = new MediaController.AlbumEntry[4];
  private AlbumView[] albumViews = new AlbumView[4];
  private int albumsCount;
  private PhotoPickerAlbumsCellDelegate delegate;
  
  public PhotoPickerAlbumsCell(Context paramContext)
  {
    super(paramContext);
    for (int i = 0; i < 4; i++)
    {
      this.albumViews[i] = new AlbumView(paramContext);
      addView(this.albumViews[i]);
      this.albumViews[i].setVisibility(4);
      this.albumViews[i].setTag(Integer.valueOf(i));
      this.albumViews[i].setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (PhotoPickerAlbumsCell.this.delegate != null) {
            PhotoPickerAlbumsCell.this.delegate.didSelectAlbum(PhotoPickerAlbumsCell.this.albumEntries[((Integer)paramAnonymousView.getTag()).intValue()]);
          }
        }
      });
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (AndroidUtilities.isTablet()) {}
    for (paramInt2 = (AndroidUtilities.dp(490.0F) - (this.albumsCount + 1) * AndroidUtilities.dp(4.0F)) / this.albumsCount;; paramInt2 = (AndroidUtilities.displaySize.x - (this.albumsCount + 1) * AndroidUtilities.dp(4.0F)) / this.albumsCount) {
      for (int i = 0; i < this.albumsCount; i++)
      {
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.albumViews[i].getLayoutParams();
        localLayoutParams.topMargin = AndroidUtilities.dp(4.0F);
        localLayoutParams.leftMargin = ((AndroidUtilities.dp(4.0F) + paramInt2) * i);
        localLayoutParams.width = paramInt2;
        localLayoutParams.height = paramInt2;
        localLayoutParams.gravity = 51;
        this.albumViews[i].setLayoutParams(localLayoutParams);
      }
    }
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0F) + paramInt2, NUM));
  }
  
  public void setAlbum(int paramInt, MediaController.AlbumEntry paramAlbumEntry)
  {
    this.albumEntries[paramInt] = paramAlbumEntry;
    AlbumView localAlbumView;
    if (paramAlbumEntry != null)
    {
      localAlbumView = this.albumViews[paramInt];
      localAlbumView.imageView.setOrientation(0, true);
      if ((paramAlbumEntry.coverPhoto != null) && (paramAlbumEntry.coverPhoto.path != null))
      {
        localAlbumView.imageView.setOrientation(paramAlbumEntry.coverPhoto.orientation, true);
        if (paramAlbumEntry.coverPhoto.isVideo)
        {
          localAlbumView.imageView.setImage("vthumb://" + paramAlbumEntry.coverPhoto.imageId + ":" + paramAlbumEntry.coverPhoto.path, null, getContext().getResources().getDrawable(NUM));
          localAlbumView.nameTextView.setText(paramAlbumEntry.bucketName);
          localAlbumView.countTextView.setText(String.format("%d", new Object[] { Integer.valueOf(paramAlbumEntry.photos.size()) }));
        }
      }
    }
    for (;;)
    {
      return;
      localAlbumView.imageView.setImage("thumb://" + paramAlbumEntry.coverPhoto.imageId + ":" + paramAlbumEntry.coverPhoto.path, null, getContext().getResources().getDrawable(NUM));
      break;
      localAlbumView.imageView.setImageResource(NUM);
      break;
      this.albumViews[paramInt].setVisibility(4);
    }
  }
  
  public void setAlbumsCount(int paramInt)
  {
    int i = 0;
    if (i < this.albumViews.length)
    {
      AlbumView localAlbumView = this.albumViews[i];
      if (i < paramInt) {}
      for (int j = 0;; j = 4)
      {
        localAlbumView.setVisibility(j);
        i++;
        break;
      }
    }
    this.albumsCount = paramInt;
  }
  
  public void setDelegate(PhotoPickerAlbumsCellDelegate paramPhotoPickerAlbumsCellDelegate)
  {
    this.delegate = paramPhotoPickerAlbumsCellDelegate;
  }
  
  private class AlbumView
    extends FrameLayout
  {
    private TextView countTextView;
    private BackupImageView imageView;
    private TextView nameTextView;
    private View selector;
    
    public AlbumView(Context paramContext)
    {
      super();
      this.imageView = new BackupImageView(paramContext);
      addView(this.imageView, LayoutHelper.createFrame(-1, -1.0F));
      this$1 = new LinearLayout(paramContext);
      PhotoPickerAlbumsCell.this.setOrientation(0);
      PhotoPickerAlbumsCell.this.setBackgroundColor(NUM);
      addView(PhotoPickerAlbumsCell.this, LayoutHelper.createFrame(-1, 28, 83));
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextSize(1, 13.0F);
      this.nameTextView.setTextColor(-1);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setGravity(16);
      PhotoPickerAlbumsCell.this.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, 1.0F, 8, 0, 0, 0));
      this.countTextView = new TextView(paramContext);
      this.countTextView.setTextSize(1, 13.0F);
      this.countTextView.setTextColor(-5592406);
      this.countTextView.setSingleLine(true);
      this.countTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.countTextView.setMaxLines(1);
      this.countTextView.setGravity(16);
      PhotoPickerAlbumsCell.this.addView(this.countTextView, LayoutHelper.createLinear(-2, -1, 4.0F, 0.0F, 4.0F, 0.0F));
      this.selector = new View(paramContext);
      this.selector.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      addView(this.selector, LayoutHelper.createFrame(-1, -1.0F));
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      if (Build.VERSION.SDK_INT >= 21) {
        this.selector.drawableHotspotChanged(paramMotionEvent.getX(), paramMotionEvent.getY());
      }
      return super.onTouchEvent(paramMotionEvent);
    }
  }
  
  public static abstract interface PhotoPickerAlbumsCellDelegate
  {
    public abstract void didSelectAlbum(MediaController.AlbumEntry paramAlbumEntry);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/PhotoPickerAlbumsCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */