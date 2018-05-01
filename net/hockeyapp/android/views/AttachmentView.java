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
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.hockeyapp.android.R.color;
import net.hockeyapp.android.R.string;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.ImageUtils;
import net.hockeyapp.android.utils.Util;

@SuppressLint({"ViewConstructor"})
public class AttachmentView
  extends FrameLayout
{
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
  
  @SuppressLint({"StaticFieldLeak"})
  public AttachmentView(Context paramContext, ViewGroup paramViewGroup, Uri paramUri, boolean paramBoolean)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mParent = paramViewGroup;
    this.mAttachment = null;
    this.mAttachmentUri = paramUri;
    this.mFilename = paramUri.getLastPathSegment();
    calculateDimensions(10);
    initializeView(paramContext, paramBoolean);
    this.mTextView.setText(this.mFilename);
    this.mTextView.setContentDescription(this.mTextView.getText());
    AsyncTaskUtils.execute(new AsyncTask()
    {
      protected Bitmap doInBackground(Void... paramAnonymousVarArgs)
      {
        return AttachmentView.this.loadImageThumbnail();
      }
      
      protected void onPostExecute(Bitmap paramAnonymousBitmap)
      {
        if (paramAnonymousBitmap != null) {
          AttachmentView.this.configureViewForThumbnail(paramAnonymousBitmap, false);
        }
        for (;;)
        {
          return;
          AttachmentView.this.configureViewForPlaceholder(false);
        }
      }
    });
  }
  
  public AttachmentView(Context paramContext, ViewGroup paramViewGroup, FeedbackAttachment paramFeedbackAttachment, boolean paramBoolean)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mParent = paramViewGroup;
    this.mAttachment = paramFeedbackAttachment;
    this.mAttachmentUri = null;
    this.mFilename = paramFeedbackAttachment.getFilename();
    calculateDimensions(40);
    initializeView(paramContext, paramBoolean);
    this.mOrientation = 1;
    this.mTextView.setText(R.string.hockeyapp_feedback_attachment_loading);
    this.mTextView.setContentDescription(this.mTextView.getText());
    configureViewForPlaceholder(false);
  }
  
  private void calculateDimensions(int paramInt)
  {
    DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
    this.mGap = Math.round(TypedValue.applyDimension(1, 10.0F, localDisplayMetrics));
    int i = Math.round(TypedValue.applyDimension(1, paramInt, localDisplayMetrics));
    int j = localDisplayMetrics.widthPixels;
    int k = this.mGap;
    paramInt = this.mGap;
    this.mWidthPortrait = ((j - i * 2 - k * 2) / 3);
    this.mWidthLandscape = ((j - i * 2 - paramInt) / 2);
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
    this.mImageView.setContentDescription(this.mTextView.getText());
    this.mImageView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (!paramBoolean) {}
        for (;;)
        {
          return;
          paramAnonymousView = new Intent();
          paramAnonymousView.setAction("android.intent.action.VIEW");
          paramAnonymousView.setDataAndType(AttachmentView.this.mAttachmentUri, "*/*");
          AttachmentView.this.mContext.startActivity(paramAnonymousView);
        }
      }
    });
  }
  
  private void configureViewForThumbnail(Bitmap paramBitmap, final boolean paramBoolean)
  {
    int i;
    if (this.mOrientation == 0)
    {
      i = this.mWidthLandscape;
      if (this.mOrientation != 0) {
        break label149;
      }
    }
    label149:
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
      this.mImageView.setContentDescription(this.mTextView.getText());
      this.mImageView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (!paramBoolean) {}
          for (;;)
          {
            return;
            paramAnonymousView = new Intent();
            paramAnonymousView.setAction("android.intent.action.VIEW");
            paramAnonymousView.setDataAndType(AttachmentView.this.mAttachmentUri, "image/*");
            AttachmentView.this.mContext.startActivity(paramAnonymousView);
          }
        }
      });
      return;
      i = this.mWidthPortrait;
      break;
    }
  }
  
  private Drawable getSystemIcon(String paramString)
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    for (paramString = getResources().getDrawable(getResources().getIdentifier(paramString, "drawable", "android"), this.mContext.getTheme());; paramString = getResources().getDrawable(getResources().getIdentifier(paramString, "drawable", "android"))) {
      return paramString;
    }
  }
  
  private void initializeView(Context paramContext, boolean paramBoolean)
  {
    setLayoutParams(new FrameLayout.LayoutParams(-2, -2, 80));
    setPadding(0, this.mGap, 0, 0);
    Util.announceForAccessibility(this.mParent, this.mContext.getString(R.string.hockeyapp_feedback_attachment_added));
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
      ImageButton localImageButton = new ImageButton(paramContext);
      localImageButton.setLayoutParams(new FrameLayout.LayoutParams(-1, -2, 80));
      localImageButton.setAdjustViewBounds(true);
      localImageButton.setImageDrawable(getSystemIcon("ic_menu_delete"));
      localImageButton.setBackgroundResource(0);
      localImageButton.setContentDescription(paramContext.getString(R.string.hockeyapp_feedback_attachment_remove_description));
      localImageButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          AttachmentView.this.remove();
        }
      });
      localImageButton.setOnFocusChangeListener(new View.OnFocusChangeListener()
      {
        public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
        {
          if (paramAnonymousBoolean) {
            Util.announceForAccessibility(AttachmentView.this.mTextView, AttachmentView.this.mTextView.getText());
          }
        }
      });
      localLinearLayout.addView(localImageButton);
    }
    localLinearLayout.addView(this.mTextView);
    addView(this.mImageView);
    addView(localLinearLayout);
  }
  
  private Bitmap loadImageThumbnail()
  {
    for (;;)
    {
      try
      {
        this.mOrientation = ImageUtils.determineOrientation(this.mContext, this.mAttachmentUri);
        if (this.mOrientation != 0) {
          continue;
        }
        i = this.mWidthLandscape;
        if (this.mOrientation != 0) {
          continue;
        }
        j = this.mMaxHeightLandscape;
        localBitmap = ImageUtils.decodeSampledBitmap(this.mContext, this.mAttachmentUri, i, j);
      }
      catch (Throwable localThrowable)
      {
        int i;
        int j;
        Bitmap localBitmap;
        Object localObject = null;
        continue;
      }
      return localBitmap;
      i = this.mWidthPortrait;
      continue;
      j = this.mMaxHeightPortrait;
    }
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
    if (this.mOrientation == 0) {}
    for (int i = this.mMaxHeightLandscape;; i = this.mMaxHeightPortrait) {
      return i;
    }
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
    Util.announceForAccessibility(this.mParent, this.mContext.getString(R.string.hockeyapp_feedback_attachment_removed));
    this.mParent.removeView(this);
  }
  
  public void setImage(Bitmap paramBitmap, int paramInt)
  {
    this.mTextView.setText(this.mFilename);
    this.mTextView.setContentDescription(this.mTextView.getText());
    this.mOrientation = paramInt;
    if (paramBitmap == null) {
      configureViewForPlaceholder(true);
    }
    for (;;)
    {
      return;
      configureViewForThumbnail(paramBitmap, true);
    }
  }
  
  public void signalImageLoadingError()
  {
    this.mTextView.setText(R.string.hockeyapp_feedback_attachment_error);
    this.mTextView.setContentDescription(this.mTextView.getText());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/views/AttachmentView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */