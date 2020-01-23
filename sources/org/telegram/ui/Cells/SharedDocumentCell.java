package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.Date;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;

public class SharedDocumentCell extends FrameLayout implements FileDownloadProgressListener {
    private int TAG;
    private CheckBox2 checkBox;
    private int currentAccount;
    private TextView dateTextView;
    private TextView extTextView;
    private boolean isPickerCell;
    private boolean loaded;
    private boolean loading;
    private MessageObject message;
    private TextView nameTextView;
    private boolean needDivider;
    private ImageView placeholderImageView;
    private LineProgressView progressView;
    private ImageView statusImageView;
    private BackupImageView thumbImageView;

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    public SharedDocumentCell(Context context) {
        this(context, false);
    }

    public SharedDocumentCell(Context context, boolean z) {
        Context context2 = context;
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.isPickerCell = z;
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        this.placeholderImageView = new ImageView(context2);
        int i = 5;
        if (this.isPickerCell) {
            addView(this.placeholderImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 12.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
        } else {
            addView(this.placeholderImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        }
        this.extTextView = new TextView(context2);
        this.extTextView.setTextColor(Theme.getColor("files_iconText"));
        this.extTextView.setTextSize(1, 14.0f);
        String str = "fonts/rmedium.ttf";
        this.extTextView.setTypeface(AndroidUtilities.getTypeface(str));
        this.extTextView.setLines(1);
        this.extTextView.setMaxLines(1);
        this.extTextView.setSingleLine(true);
        this.extTextView.setGravity(17);
        this.extTextView.setEllipsize(TruncateAt.END);
        this.extTextView.setImportantForAccessibility(2);
        if (this.isPickerCell) {
            addView(this.extTextView, LayoutHelper.createFrame(32, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 20.0f, 28.0f, LocaleController.isRTL ? 20.0f : 0.0f, 0.0f));
        } else {
            addView(this.extTextView, LayoutHelper.createFrame(32, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 22.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        }
        this.thumbImageView = new BackupImageView(context2) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                float f = 1.0f;
                if (SharedDocumentCell.this.thumbImageView.getImageReceiver().hasBitmapImage()) {
                    f = 1.0f - SharedDocumentCell.this.thumbImageView.getImageReceiver().getCurrentAlpha();
                }
                SharedDocumentCell.this.extTextView.setAlpha(f);
                SharedDocumentCell.this.placeholderImageView.setAlpha(f);
                super.onDraw(canvas);
            }
        };
        this.thumbImageView.setRoundRadius(AndroidUtilities.dp(4.0f));
        if (this.isPickerCell) {
            addView(this.thumbImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        } else {
            addView(this.thumbImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        }
        this.nameTextView = new TextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(str));
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        if (this.isPickerCell) {
            this.nameTextView.setLines(1);
            this.nameTextView.setMaxLines(1);
            this.nameTextView.setSingleLine(true);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 9.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else {
            this.nameTextView.setMaxLines(2);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 5.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        }
        this.statusImageView = new ImageView(context2);
        this.statusImageView.setVisibility(4);
        this.statusImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("sharedMedia_startStopLoadIcon"), Mode.MULTIPLY));
        if (this.isPickerCell) {
            addView(this.statusImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 39.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else {
            addView(this.statusImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 35.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        }
        this.dateTextView = new TextView(context2);
        this.dateTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        this.dateTextView.setLines(1);
        this.dateTextView.setMaxLines(1);
        this.dateTextView.setSingleLine(true);
        this.dateTextView.setEllipsize(TruncateAt.END);
        this.dateTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        if (this.isPickerCell) {
            this.dateTextView.setTextSize(1, 13.0f);
            addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 34.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else {
            this.dateTextView.setTextSize(1, 14.0f);
            addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 30.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        }
        this.progressView = new LineProgressView(context2);
        this.progressView.setProgressColor(Theme.getColor("sharedMedia_startStopLoadIcon"));
        addView(this.progressView, LayoutHelper.createFrame(-1, 2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 72.0f, 54.0f, LocaleController.isRTL ? 72.0f : 0.0f, 0.0f));
        this.checkBox = new CheckBox2(context2, 21);
        this.checkBox.setVisibility(4);
        this.checkBox.setColor(null, "windowBackgroundWhite", "checkboxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(2);
        CheckBox2 checkBox2;
        if (this.isPickerCell) {
            checkBox2 = this.checkBox;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(checkBox2, LayoutHelper.createFrame(24, 24.0f, i | 48, LocaleController.isRTL ? 0.0f : 38.0f, 36.0f, LocaleController.isRTL ? 38.0f : 0.0f, 0.0f));
            return;
        }
        checkBox2 = this.checkBox;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(checkBox2, LayoutHelper.createFrame(24, 24.0f, i | 48, LocaleController.isRTL ? 0.0f : 33.0f, 28.0f, LocaleController.isRTL ? 33.0f : 0.0f, 0.0f));
    }

    public void setTextAndValueAndTypeAndThumb(String str, String str2, String str3, String str4, int i, boolean z) {
        this.nameTextView.setText(str);
        this.dateTextView.setText(str2);
        if (str3 != null) {
            this.extTextView.setVisibility(0);
            this.extTextView.setText(str3.toLowerCase());
        } else {
            this.extTextView.setVisibility(4);
        }
        this.needDivider = z;
        if (i == 0) {
            this.placeholderImageView.setImageResource(AndroidUtilities.getThumbForNameOrMime(str, str3, false));
            this.placeholderImageView.setVisibility(0);
        } else {
            this.placeholderImageView.setVisibility(4);
        }
        if (str4 == null && i == 0) {
            this.extTextView.setAlpha(1.0f);
            this.placeholderImageView.setAlpha(1.0f);
            this.thumbImageView.setImageBitmap(null);
            this.thumbImageView.setVisibility(4);
        } else {
            if (str4 != null) {
                this.thumbImageView.setImage(str4, "42_42", null);
            } else {
                CombinedDrawable createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(42.0f), i);
                if (i == NUM) {
                    str3 = "chat_attachLocationBackground";
                    str4 = "chat_attachLocationIcon";
                } else if (i == NUM) {
                    str3 = "chat_attachContactBackground";
                    str4 = "chat_attachContactIcon";
                } else if (i == NUM) {
                    str3 = "chat_attachAudioBackground";
                    str4 = "chat_attachAudioIcon";
                } else if (i == NUM) {
                    str3 = "chat_attachGalleryBackground";
                    str4 = "chat_attachGalleryIcon";
                } else {
                    str3 = "files_folderIconBackground";
                    str4 = "files_folderIcon";
                }
                Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, Theme.getColor(str3), false);
                Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, Theme.getColor(str4), true);
                this.thumbImageView.setImageDrawable(createCircleDrawableWithIcon);
            }
            this.thumbImageView.setVisibility(0);
        }
        setWillNotDraw(1 ^ this.needDivider);
    }

    public void setPhotoEntry(PhotoEntry photoEntry) {
        String str = photoEntry.thumbPath;
        if (str != null) {
            this.thumbImageView.setImage(str, null, Theme.chat_attachEmptyDrawable);
            str = photoEntry.thumbPath;
        } else if (photoEntry.path != null) {
            String str2 = ":";
            BackupImageView backupImageView;
            StringBuilder stringBuilder;
            if (photoEntry.isVideo) {
                this.thumbImageView.setOrientation(0, true);
                backupImageView = this.thumbImageView;
                stringBuilder = new StringBuilder();
                stringBuilder.append("vthumb://");
                stringBuilder.append(photoEntry.imageId);
                stringBuilder.append(str2);
                stringBuilder.append(photoEntry.path);
                backupImageView.setImage(stringBuilder.toString(), null, Theme.chat_attachEmptyDrawable);
            } else {
                this.thumbImageView.setOrientation(photoEntry.orientation, true);
                backupImageView = this.thumbImageView;
                stringBuilder = new StringBuilder();
                stringBuilder.append("thumb://");
                stringBuilder.append(photoEntry.imageId);
                stringBuilder.append(str2);
                stringBuilder.append(photoEntry.path);
                backupImageView.setImage(stringBuilder.toString(), null, Theme.chat_attachEmptyDrawable);
            }
            str = photoEntry.path;
        } else {
            this.thumbImageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
            str = "";
        }
        File file = new File(str);
        this.nameTextView.setText(file.getName());
        FileLoader.getFileExtension(file);
        StringBuilder stringBuilder2 = new StringBuilder();
        this.extTextView.setVisibility(8);
        String str3 = ", ";
        if (!(photoEntry.width == 0 || photoEntry.height == 0)) {
            if (stringBuilder2.length() > 0) {
                stringBuilder2.append(str3);
            }
            stringBuilder2.append(String.format(Locale.US, "%dx%d", new Object[]{Integer.valueOf(photoEntry.width), Integer.valueOf(photoEntry.height)}));
        }
        if (photoEntry.isVideo) {
            if (stringBuilder2.length() > 0) {
                stringBuilder2.append(str3);
            }
            stringBuilder2.append(AndroidUtilities.formatShortDuration(photoEntry.duration));
        }
        if (photoEntry.size != 0) {
            if (stringBuilder2.length() > 0) {
                stringBuilder2.append(str3);
            }
            stringBuilder2.append(AndroidUtilities.formatFileSize(photoEntry.size));
        }
        if (stringBuilder2.length() > 0) {
            stringBuilder2.append(str3);
        }
        stringBuilder2.append(LocaleController.getInstance().formatterStats.format(photoEntry.dateTaken));
        this.dateTextView.setText(stringBuilder2);
        this.placeholderImageView.setVisibility(8);
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.progressView.getVisibility() == 0) {
            updateFileExistIcon();
        }
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
    }

    public void setDocument(MessageObject messageObject, boolean z) {
        MessageObject messageObject2 = messageObject;
        this.needDivider = z;
        this.message = messageObject2;
        this.loaded = false;
        this.loading = false;
        Document document = messageObject.getDocument();
        CharSequence charSequence = "";
        if (messageObject2 == null || document == null) {
            this.nameTextView.setText(charSequence);
            this.extTextView.setText(charSequence);
            this.dateTextView.setText(charSequence);
            this.placeholderImageView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.extTextView.setAlpha(1.0f);
            this.placeholderImageView.setAlpha(1.0f);
            this.thumbImageView.setVisibility(4);
            this.thumbImageView.setImageBitmap(null);
        } else {
            CharSequence charSequence2 = null;
            if (messageObject.isMusic()) {
                for (int i = 0; i < document.attributes.size(); i++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (documentAttribute instanceof TL_documentAttributeAudio) {
                        String str = documentAttribute.performer;
                        if (str == null || str.length() == 0) {
                            String str2 = documentAttribute.title;
                            if (str2 != null) {
                                if (str2.length() == 0) {
                                }
                            }
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(messageObject.getMusicAuthor());
                        stringBuilder.append(" - ");
                        stringBuilder.append(messageObject.getMusicTitle());
                        charSequence2 = stringBuilder.toString();
                    }
                }
            }
            String documentFileName = FileLoader.getDocumentFileName(document);
            if (charSequence2 == null) {
                charSequence2 = documentFileName;
            }
            this.nameTextView.setText(charSequence2);
            this.placeholderImageView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.placeholderImageView.setImageResource(AndroidUtilities.getThumbForNameOrMime(documentFileName, document.mime_type, false));
            TextView textView = this.extTextView;
            int lastIndexOf = documentFileName.lastIndexOf(46);
            if (lastIndexOf != -1) {
                charSequence = documentFileName.substring(lastIndexOf + 1).toLowerCase();
            }
            textView.setText(charSequence);
            PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
            PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 40);
            if (closestPhotoSizeWithSize2 == closestPhotoSizeWithSize) {
                closestPhotoSizeWithSize = null;
            }
            if ((closestPhotoSizeWithSize2 instanceof TL_photoSizeEmpty) || closestPhotoSizeWithSize2 == null) {
                this.thumbImageView.setVisibility(4);
                this.thumbImageView.setImageBitmap(null);
                this.extTextView.setAlpha(1.0f);
                this.placeholderImageView.setAlpha(1.0f);
            } else {
                this.thumbImageView.getImageReceiver().setNeedsQualityThumb(closestPhotoSizeWithSize == null);
                this.thumbImageView.getImageReceiver().setShouldGenerateQualityThumb(closestPhotoSizeWithSize == null);
                this.thumbImageView.setVisibility(0);
                this.thumbImageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "40_40", ImageLocation.getForDocument(closestPhotoSizeWithSize2, document), "40_40_b", null, 0, 1, messageObject);
            }
            long j = ((long) messageObject2.messageOwner.date) * 1000;
            TextView textView2 = this.dateTextView;
            Object[] objArr = new Object[2];
            objArr[0] = AndroidUtilities.formatFileSize((long) document.size);
            objArr[1] = LocaleController.formatString("formatDateAtTime", NUM, LocaleController.getInstance().formatterYear.format(new Date(j)), LocaleController.getInstance().formatterDay.format(new Date(j)));
            textView2.setText(String.format("%s, %s", objArr));
        }
        setWillNotDraw(this.needDivider ^ 1);
        this.progressView.setProgress(0.0f, false);
        updateFileExistIcon();
    }

    public void updateFileExistIcon() {
        MessageObject messageObject = this.message;
        if (messageObject == null || messageObject.messageOwner.media == null) {
            this.loading = false;
            this.loaded = true;
            this.progressView.setVisibility(4);
            this.progressView.setProgress(0.0f, false);
            this.statusImageView.setVisibility(4);
            this.dateTextView.setPadding(0, 0, 0, 0);
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            return;
        }
        this.loaded = false;
        if (messageObject.attachPathExists || messageObject.mediaExists) {
            this.statusImageView.setVisibility(4);
            this.progressView.setVisibility(4);
            this.dateTextView.setPadding(0, 0, 0, 0);
            this.loading = false;
            this.loaded = true;
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            return;
        }
        String attachFileName = FileLoader.getAttachFileName(messageObject.getDocument());
        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(attachFileName, this.message, this);
        this.loading = FileLoader.getInstance(this.currentAccount).isLoadingFile(attachFileName);
        this.statusImageView.setVisibility(0);
        this.statusImageView.setImageResource(this.loading ? NUM : NUM);
        this.dateTextView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(14.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : 0, 0);
        if (this.loading) {
            this.progressView.setVisibility(0);
            Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
            if (fileProgress == null) {
                fileProgress = Float.valueOf(0.0f);
            }
            this.progressView.setProgress(fileProgress.floatValue(), false);
            return;
        }
        this.progressView.setVisibility(4);
    }

    public MessageObject getMessage() {
        return this.message;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean isLoading() {
        return this.loading;
    }

    public BackupImageView getImageView() {
        return this.thumbImageView;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        if (this.isPickerCell) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + this.needDivider, NUM));
            return;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
        setMeasuredDimension(getMeasuredWidth(), (AndroidUtilities.dp(34.0f) + this.nameTextView.getMeasuredHeight()) + this.needDivider);
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (!this.isPickerCell && this.nameTextView.getLineCount() > 1) {
            int measuredHeight = this.nameTextView.getMeasuredHeight() - AndroidUtilities.dp(22.0f);
            TextView textView = this.dateTextView;
            textView.layout(textView.getLeft(), this.dateTextView.getTop() + measuredHeight, this.dateTextView.getRight(), this.dateTextView.getBottom() + measuredHeight);
            ImageView imageView = this.statusImageView;
            imageView.layout(imageView.getLeft(), this.statusImageView.getTop() + measuredHeight, this.statusImageView.getRight(), measuredHeight + this.statusImageView.getBottom());
            LineProgressView lineProgressView = this.progressView;
            lineProgressView.layout(lineProgressView.getLeft(), (getMeasuredHeight() - this.progressView.getMeasuredHeight()) - this.needDivider, this.progressView.getRight(), getMeasuredHeight() - this.needDivider);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onFailedDownload(String str, boolean z) {
        updateFileExistIcon();
    }

    public void onSuccessDownload(String str) {
        this.progressView.setProgress(1.0f, true);
        updateFileExistIcon();
    }

    public void onProgressDownload(String str, long j, long j2) {
        if (this.progressView.getVisibility() != 0) {
            updateFileExistIcon();
        }
        this.progressView.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(true);
        }
    }
}
