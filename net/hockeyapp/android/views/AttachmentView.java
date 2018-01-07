package net.hockeyapp.android.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import net.hockeyapp.android.R;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.ImageUtils;
import net.hockeyapp.android.utils.Util;

@SuppressLint({"ViewConstructor"})
public class AttachmentView extends FrameLayout {
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
    public AttachmentView(Context context, ViewGroup parent, Uri attachmentUri, boolean removable) {
        super(context);
        this.mContext = context;
        this.mParent = parent;
        this.mAttachment = null;
        this.mAttachmentUri = attachmentUri;
        this.mFilename = attachmentUri.getLastPathSegment();
        calculateDimensions(10);
        initializeView(context, removable);
        this.mTextView.setText(this.mFilename);
        this.mTextView.setContentDescription(this.mTextView.getText());
        AsyncTaskUtils.execute(new AsyncTask<Void, Void, Bitmap>() {
            protected Bitmap doInBackground(Void... args) {
                return AttachmentView.this.loadImageThumbnail();
            }

            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    AttachmentView.this.configureViewForThumbnail(bitmap, false);
                } else {
                    AttachmentView.this.configureViewForPlaceholder(false);
                }
            }
        });
    }

    public AttachmentView(Context context, ViewGroup parent, FeedbackAttachment attachment, boolean removable) {
        super(context);
        this.mContext = context;
        this.mParent = parent;
        this.mAttachment = attachment;
        this.mAttachmentUri = null;
        this.mFilename = attachment.getFilename();
        calculateDimensions(40);
        initializeView(context, removable);
        this.mOrientation = 1;
        this.mTextView.setText(R.string.hockeyapp_feedback_attachment_loading);
        this.mTextView.setContentDescription(this.mTextView.getText());
        configureViewForPlaceholder(false);
    }

    public FeedbackAttachment getAttachment() {
        return this.mAttachment;
    }

    public Uri getAttachmentUri() {
        return this.mAttachmentUri;
    }

    public int getWidthPortrait() {
        return this.mWidthPortrait;
    }

    public int getMaxHeightPortrait() {
        return this.mMaxHeightPortrait;
    }

    public int getWidthLandscape() {
        return this.mWidthLandscape;
    }

    public int getMaxHeightLandscape() {
        return this.mMaxHeightLandscape;
    }

    public int getGap() {
        return this.mGap;
    }

    public int getEffectiveMaxHeight() {
        return this.mOrientation == 0 ? this.mMaxHeightLandscape : this.mMaxHeightPortrait;
    }

    public void remove() {
        Util.announceForAccessibility(this.mParent, this.mContext.getString(R.string.hockeyapp_feedback_attachment_removed));
        this.mParent.removeView(this);
    }

    public void setImage(Bitmap bitmap, int orientation) {
        this.mTextView.setText(this.mFilename);
        this.mTextView.setContentDescription(this.mTextView.getText());
        this.mOrientation = orientation;
        if (bitmap == null) {
            configureViewForPlaceholder(true);
        } else {
            configureViewForThumbnail(bitmap, true);
        }
    }

    public void signalImageLoadingError() {
        this.mTextView.setText(R.string.hockeyapp_feedback_attachment_error);
        this.mTextView.setContentDescription(this.mTextView.getText());
    }

    private void calculateDimensions(int marginDip) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        this.mGap = Math.round(TypedValue.applyDimension(1, 10.0f, metrics));
        int layoutMargin = Math.round(TypedValue.applyDimension(1, (float) marginDip, metrics));
        int displayWidth = metrics.widthPixels;
        int parentWidthLandscape = (displayWidth - (layoutMargin * 2)) - this.mGap;
        this.mWidthPortrait = ((displayWidth - (layoutMargin * 2)) - (this.mGap * 2)) / 3;
        this.mWidthLandscape = parentWidthLandscape / 2;
        this.mMaxHeightPortrait = this.mWidthPortrait * 2;
        this.mMaxHeightLandscape = this.mWidthLandscape;
    }

    private void initializeView(Context context, boolean removable) {
        setLayoutParams(new LayoutParams(-2, -2, 80));
        setPadding(0, this.mGap, 0, 0);
        Util.announceForAccessibility(this.mParent, this.mContext.getString(R.string.hockeyapp_feedback_attachment_added));
        this.mImageView = new ImageView(context);
        LinearLayout bottomView = new LinearLayout(context);
        bottomView.setLayoutParams(new LayoutParams(-1, -2, 80));
        bottomView.setGravity(8388611);
        bottomView.setOrientation(1);
        bottomView.setBackgroundColor(Color.parseColor("#80262626"));
        this.mTextView = new TextView(context);
        this.mTextView.setLayoutParams(new LayoutParams(-1, -2, 17));
        this.mTextView.setGravity(17);
        this.mTextView.setTextColor(context.getResources().getColor(R.color.hockeyapp_text_white));
        this.mTextView.setSingleLine();
        this.mTextView.setEllipsize(TruncateAt.MIDDLE);
        if (removable) {
            ImageButton imageButton = new ImageButton(context);
            imageButton.setLayoutParams(new LayoutParams(-1, -2, 80));
            imageButton.setAdjustViewBounds(true);
            imageButton.setImageDrawable(getSystemIcon("ic_menu_delete"));
            imageButton.setBackgroundResource(0);
            imageButton.setContentDescription(context.getString(R.string.hockeyapp_feedback_attachment_remove_description));
            imageButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    AttachmentView.this.remove();
                }
            });
            imageButton.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Util.announceForAccessibility(AttachmentView.this.mTextView, AttachmentView.this.mTextView.getText());
                    }
                }
            });
            bottomView.addView(imageButton);
        }
        bottomView.addView(this.mTextView);
        addView(this.mImageView);
        addView(bottomView);
    }

    private void configureViewForThumbnail(Bitmap bitmap, final boolean openOnClick) {
        int width = this.mOrientation == 0 ? this.mWidthLandscape : this.mWidthPortrait;
        int height = this.mOrientation == 0 ? this.mMaxHeightLandscape : this.mMaxHeightPortrait;
        this.mTextView.setMaxWidth(width);
        this.mTextView.setMinWidth(width);
        this.mImageView.setLayoutParams(new LayoutParams(-2, -2));
        this.mImageView.setAdjustViewBounds(true);
        this.mImageView.setMinimumWidth(width);
        this.mImageView.setMaxWidth(width);
        this.mImageView.setMaxHeight(height);
        this.mImageView.setScaleType(ScaleType.CENTER_INSIDE);
        this.mImageView.setImageBitmap(bitmap);
        this.mImageView.setContentDescription(this.mTextView.getText());
        this.mImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (openOnClick) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setDataAndType(AttachmentView.this.mAttachmentUri, "image/*");
                    AttachmentView.this.mContext.startActivity(intent);
                }
            }
        });
    }

    private void configureViewForPlaceholder(final boolean openOnClick) {
        this.mTextView.setMaxWidth(this.mWidthPortrait);
        this.mTextView.setMinWidth(this.mWidthPortrait);
        this.mImageView.setLayoutParams(new LayoutParams(-2, -2));
        this.mImageView.setAdjustViewBounds(false);
        this.mImageView.setBackgroundColor(Color.parseColor("#eeeeee"));
        this.mImageView.setMinimumHeight((int) (((float) this.mWidthPortrait) * 1.2f));
        this.mImageView.setMinimumWidth(this.mWidthPortrait);
        this.mImageView.setScaleType(ScaleType.FIT_CENTER);
        this.mImageView.setImageDrawable(getSystemIcon("ic_menu_attachment"));
        this.mImageView.setContentDescription(this.mTextView.getText());
        this.mImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (openOnClick) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setDataAndType(AttachmentView.this.mAttachmentUri, "*/*");
                    AttachmentView.this.mContext.startActivity(intent);
                }
            }
        });
    }

    private Bitmap loadImageThumbnail() {
        try {
            this.mOrientation = ImageUtils.determineOrientation(this.mContext, this.mAttachmentUri);
            return ImageUtils.decodeSampledBitmap(this.mContext, this.mAttachmentUri, this.mOrientation == 0 ? this.mWidthLandscape : this.mWidthPortrait, this.mOrientation == 0 ? this.mMaxHeightLandscape : this.mMaxHeightPortrait);
        } catch (Throwable th) {
            return null;
        }
    }

    private Drawable getSystemIcon(String name) {
        if (VERSION.SDK_INT >= 21) {
            return getResources().getDrawable(getResources().getIdentifier(name, "drawable", "android"), this.mContext.getTheme());
        }
        return getResources().getDrawable(getResources().getIdentifier(name, "drawable", "android"));
    }
}
