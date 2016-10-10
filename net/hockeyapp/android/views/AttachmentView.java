package net.hockeyapp.android.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.R.color;
import net.hockeyapp.android.R.string;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.utils.ImageUtils;

@SuppressLint({"ViewConstructor"})
public class AttachmentView
  extends FrameLayout
{
  private static final int IMAGES_PER_ROW_LANDSCAPE = 2;
  private static final int IMAGES_PER_ROW_PORTRAIT = 3;
  private final FeedbackAttachment mAttachment;
  private final Uri mAttachmentUri;
  private final Context mContext;
  private final String mFilename;
  private int mGap;
  private ImageView mImageView;
  private int mMaxHeightLandscape;
  private int mMaxHeightPortrait;
  private int mOrientation;
  private final ViewGroup mParent;
  private TextView mTextView;
  private int mWidthLandscape;
  private int mWidthPortrait;
  
  public AttachmentView(Context paramContext, ViewGroup paramViewGroup, Uri paramUri, boolean paramBoolean)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mParent = paramViewGroup;
    this.mAttachment = null;
    this.mAttachmentUri = paramUri;
    this.mFilename = paramUri.getLastPathSegment();
    calculateDimensions(20);
    initializeView(paramContext, paramBoolean);
    this.mTextView.setText(this.mFilename);
    new AsyncTask()
    {
      protected Bitmap doInBackground(Void... paramAnonymousVarArgs)
      {
        return AttachmentView.this.loadImageThumbnail();
      }
      
      protected void onPostExecute(Bitmap paramAnonymousBitmap)
      {
        if (paramAnonymousBitmap != null)
        {
          AttachmentView.this.configureViewForThumbnail(paramAnonymousBitmap, false);
          return;
        }
        AttachmentView.this.configureViewForPlaceholder(false);
      }
    }.execute(new Void[0]);
  }
  
  public AttachmentView(Context paramContext, ViewGroup paramViewGroup, FeedbackAttachment paramFeedbackAttachment, boolean paramBoolean)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mParent = paramViewGroup;
    this.mAttachment = paramFeedbackAttachment;
    this.mAttachmentUri = Uri.fromFile(new File(Constants.getHockeyAppStorageDir(), paramFeedbackAttachment.getCacheId()));
    this.mFilename = paramFeedbackAttachment.getFilename();
    calculateDimensions(30);
    initializeView(paramContext, paramBoolean);
    this.mOrientation = 0;
    this.mTextView.setText(R.string.hockeyapp_feedback_attachment_loading);
    configureViewForPlaceholder(false);
  }
  
  private void calculateDimensions(int paramInt)
  {
    DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
    this.mGap = Math.round(TypedValue.applyDimension(1, 10.0F, localDisplayMetrics));
    paramInt = Math.round(TypedValue.applyDimension(1, paramInt, localDisplayMetrics));
    int i = localDisplayMetrics.widthPixels;
    int j = this.mGap;
    int k = this.mGap;
    this.mWidthPortrait = ((i - paramInt * 2 - j * 2) / 3);
    this.mWidthLandscape = ((i - paramInt * 2 - k) / 2);
    this.mMaxHeightPortrait = (this.mWidthPortrait * 2);
    this.mMaxHeightLandscape = this.mWidthLandscape;
  }
  
  private void configureViewForPlaceholder(final boolean paramBoolean)
  {
    this.mTextView.setMaxWidth(this.mWidthPortrait);
    this.mTextView.setMinWidth(this.mWidthPortrait);
    this.mImageView.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
    this.mImageView.setAdjustViewBounds(false);
    this.mImageView.setBackgroundColor(Color.parseColor("#eeeeee"));
    this.mImageView.setMinimumHeight((int)(this.mWidthPortrait * 1.2F));
    this.mImageView.setMinimumWidth(this.mWidthPortrait);
    this.mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    this.mImageView.setImageDrawable(getSystemIcon("ic_menu_attachment"));
    this.mImageView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (!paramBoolean) {
          return;
        }
        paramAnonymousView = new Intent();
        paramAnonymousView.setAction("android.intent.action.VIEW");
        paramAnonymousView.setDataAndType(AttachmentView.this.mAttachmentUri, "*/*");
        AttachmentView.this.mContext.startActivity(paramAnonymousView);
      }
    });
  }
  
  private void configureViewForThumbnail(Bitmap paramBitmap, final boolean paramBoolean)
  {
    int i;
    if (this.mOrientation == 1)
    {
      i = this.mWidthLandscape;
      if (this.mOrientation != 1) {
        break label137;
      }
    }
    label137:
    for (int j = this.mMaxHeightLandscape;; j = this.mMaxHeightPortrait)
    {
      this.mTextView.setMaxWidth(i);
      this.mTextView.setMinWidth(i);
      this.mImageView.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
      this.mImageView.setAdjustViewBounds(true);
      this.mImageView.setMinimumWidth(i);
      this.mImageView.setMaxWidth(i);
      this.mImageView.setMaxHeight(j);
      this.mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      this.mImageView.setImageBitmap(paramBitmap);
      this.mImageView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (!paramBoolean) {
            return;
          }
          paramAnonymousView = new Intent();
          paramAnonymousView.setAction("android.intent.action.VIEW");
          paramAnonymousView.setDataAndType(AttachmentView.this.mAttachmentUri, "image/*");
          AttachmentView.this.mContext.startActivity(paramAnonymousView);
        }
      });
      return;
      i = this.mWidthPortrait;
      break;
    }
  }
  
  private Drawable getSystemIcon(String paramString)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return getResources().getDrawable(getResources().getIdentifier(paramString, "drawable", "android"), this.mContext.getTheme());
    }
    return getResources().getDrawable(getResources().getIdentifier(paramString, "drawable", "android"));
  }
  
  private void initializeView(Context paramContext, boolean paramBoolean)
  {
    setLayoutParams(new FrameLayout.LayoutParams(-2, -2, 80));
    setPadding(0, this.mGap, 0, 0);
    this.mImageView = new ImageView(paramContext);
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    localLinearLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -2, 80));
    localLinearLayout.setGravity(8388611);
    localLinearLayout.setOrientation(1);
    localLinearLayout.setBackgroundColor(Color.parseColor("#80262626"));
    this.mTextView = new TextView(paramContext);
    this.mTextView.setLayoutParams(new FrameLayout.LayoutParams(-1, -2, 17));
    this.mTextView.setGravity(17);
    this.mTextView.setTextColor(paramContext.getResources().getColor(R.color.hockeyapp_text_white));
    this.mTextView.setSingleLine();
    this.mTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
    if (paramBoolean)
    {
      paramContext = new ImageButton(paramContext);
      paramContext.setLayoutParams(new FrameLayout.LayoutParams(-1, -2, 80));
      paramContext.setAdjustViewBounds(true);
      paramContext.setImageDrawable(getSystemIcon("ic_menu_delete"));
      paramContext.setBackgroundResource(0);
      paramContext.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          AttachmentView.this.remove();
        }
      });
      localLinearLayout.addView(paramContext);
    }
    localLinearLayout.addView(this.mTextView);
    addView(this.mImageView);
    addView(localLinearLayout);
  }
  
  private Bitmap loadImageThumbnail()
  {
    try
    {
      this.mOrientation = ImageUtils.determineOrientation(this.mContext, this.mAttachmentUri);
      int i;
      if (this.mOrientation == 1)
      {
        i = this.mWidthLandscape;
        if (this.mOrientation != 1) {
          break label63;
        }
      }
      label63:
      for (int j = this.mMaxHeightLandscape;; j = this.mMaxHeightPortrait)
      {
        return ImageUtils.decodeSampledBitmap(this.mContext, this.mAttachmentUri, i, j);
        i = this.mWidthPortrait;
        break;
      }
      return null;
    }
    catch (Throwable localThrowable) {}
  }
  
  public FeedbackAttachment getAttachment()
  {
    return this.mAttachment;
  }
  
  public Uri getAttachmentUri()
  {
    return this.mAttachmentUri;
  }
  
  public int getEffectiveMaxHeight()
  {
    if (this.mOrientation == 1) {
      return this.mMaxHeightLandscape;
    }
    return this.mMaxHeightPortrait;
  }
  
  public int getGap()
  {
    return this.mGap;
  }
  
  public int getMaxHeightLandscape()
  {
    return this.mMaxHeightLandscape;
  }
  
  public int getMaxHeightPortrait()
  {
    return this.mMaxHeightPortrait;
  }
  
  public int getWidthLandscape()
  {
    return this.mWidthLandscape;
  }
  
  public int getWidthPortrait()
  {
    return this.mWidthPortrait;
  }
  
  public void remove()
  {
    this.mParent.removeView(this);
  }
  
  public void setImage(Bitmap paramBitmap, int paramInt)
  {
    this.mTextView.setText(this.mFilename);
    this.mOrientation = paramInt;
    if (paramBitmap == null)
    {
      configureViewForPlaceholder(true);
      return;
    }
    configureViewForThumbnail(paramBitmap, true);
  }
  
  public void signalImageLoadingError()
  {
    this.mTextView.setText(R.string.hockeyapp_feedback_attachment_error);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/views/AttachmentView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */