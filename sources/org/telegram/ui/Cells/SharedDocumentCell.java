package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.util.Date;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.FilteredSearchView;

public class SharedDocumentCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
    private int TAG;
    private CharSequence caption;
    private TextView captionTextView;
    private CheckBox2 checkBox;
    private int currentAccount;
    private TextView dateTextView;
    private SpannableStringBuilder dotSpan;
    /* access modifiers changed from: private */
    public TextView extTextView;
    boolean ignoreRequestLayout;
    private boolean loaded;
    private boolean loading;
    private MessageObject message;
    private TextView nameTextView;
    private boolean needDivider;
    /* access modifiers changed from: private */
    public ImageView placeholderImageView;
    private LineProgressView progressView;
    private TextView rightDateTextView;
    private ImageView statusImageView;
    /* access modifiers changed from: private */
    public BackupImageView thumbImageView;
    private int viewType;

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    public SharedDocumentCell(Context context) {
        this(context, 0);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SharedDocumentCell(Context context, int i) {
        super(context);
        Context context2 = context;
        int i2 = i;
        int i3 = UserConfig.selectedAccount;
        this.currentAccount = i3;
        this.viewType = i2;
        this.TAG = DownloadController.getInstance(i3).generateObserverTag();
        ImageView imageView = new ImageView(context2);
        this.placeholderImageView = imageView;
        if (i2 == 1) {
            addView(imageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 12.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
        } else {
            addView(imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        }
        TextView textView = new TextView(context2);
        this.extTextView = textView;
        textView.setTextColor(Theme.getColor("files_iconText"));
        this.extTextView.setTextSize(1, 14.0f);
        this.extTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.extTextView.setLines(1);
        this.extTextView.setMaxLines(1);
        this.extTextView.setSingleLine(true);
        this.extTextView.setGravity(17);
        this.extTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.extTextView.setImportantForAccessibility(2);
        if (i2 == 1) {
            addView(this.extTextView, LayoutHelper.createFrame(32, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 20.0f, 28.0f, LocaleController.isRTL ? 20.0f : 0.0f, 0.0f));
        } else {
            addView(this.extTextView, LayoutHelper.createFrame(32, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 22.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        }
        AnonymousClass1 r12 = new BackupImageView(context2) {
            /* access modifiers changed from: protected */
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
        this.thumbImageView = r12;
        r12.setRoundRadius(AndroidUtilities.dp(4.0f));
        if (i2 == 1) {
            addView(this.thumbImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        } else {
            addView(this.thumbImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        }
        TextView textView2 = new TextView(context2);
        this.nameTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        if (i2 == 1) {
            this.nameTextView.setLines(1);
            this.nameTextView.setMaxLines(1);
            this.nameTextView.setSingleLine(true);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 9.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else if (i2 == 2) {
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16.0f : 72.0f, 5.0f, LocaleController.isRTL ? 72.0f : 16.0f, 0.0f));
            TextView textView3 = new TextView(context2);
            this.rightDateTextView = textView3;
            textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            this.rightDateTextView.setTextSize(1, 14.0f);
            if (!LocaleController.isRTL) {
                linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(-2, -2, 1.0f));
                linearLayout.addView(this.rightDateTextView, LayoutHelper.createLinear(-2, -2, 0.0f, 4, 0, 0, 0));
            } else {
                linearLayout.addView(this.rightDateTextView, LayoutHelper.createLinear(-2, -2, 0.0f));
                linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(-2, -2, 1.0f, 0, 0, 4, 0));
            }
            this.nameTextView.setMaxLines(2);
            TextView textView4 = new TextView(context2);
            this.captionTextView = textView4;
            textView4.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.captionTextView.setLines(1);
            this.captionTextView.setMaxLines(1);
            this.captionTextView.setSingleLine(true);
            this.captionTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.captionTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.captionTextView.setTextSize(1, 13.0f);
            addView(this.captionTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 30.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
            this.captionTextView.setVisibility(8);
        } else {
            this.nameTextView.setMaxLines(2);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 5.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        }
        ImageView imageView2 = new ImageView(context2);
        this.statusImageView = imageView2;
        imageView2.setVisibility(4);
        this.statusImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("sharedMedia_startStopLoadIcon"), PorterDuff.Mode.MULTIPLY));
        if (i2 == 1) {
            addView(this.statusImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 39.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else {
            addView(this.statusImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 35.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        }
        TextView textView5 = new TextView(context2);
        this.dateTextView = textView5;
        textView5.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        this.dateTextView.setLines(1);
        this.dateTextView.setMaxLines(1);
        this.dateTextView.setSingleLine(true);
        this.dateTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.dateTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        if (i2 == 1) {
            this.dateTextView.setTextSize(1, 13.0f);
            addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 34.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else {
            this.dateTextView.setTextSize(1, 13.0f);
            addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 30.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        }
        LineProgressView lineProgressView = new LineProgressView(context2);
        this.progressView = lineProgressView;
        lineProgressView.setProgressColor(Theme.getColor("sharedMedia_startStopLoadIcon"));
        addView(this.progressView, LayoutHelper.createFrame(-1, 2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 72.0f, 54.0f, LocaleController.isRTL ? 72.0f : 0.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context2, 21);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(2);
        if (i2 == 1) {
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 38.0f, 36.0f, LocaleController.isRTL ? 38.0f : 0.0f, 0.0f));
        } else {
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 33.0f, 28.0f, LocaleController.isRTL ? 33.0f : 0.0f, 0.0f));
        }
        if (i2 == 2) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(".");
            this.dotSpan = spannableStringBuilder;
            spannableStringBuilder.setSpan(new ReplacementSpan(this) {
                int color;
                Paint p = new Paint(1);

                public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
                    return AndroidUtilities.dp(3.0f);
                }

                public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
                    if (this.color != paint.getColor()) {
                        this.p.setColor(paint.getColor());
                    }
                    float dpf2 = AndroidUtilities.dpf2(3.0f) / 2.0f;
                    canvas.drawCircle(f + dpf2, (float) ((i5 - i3) / 2), dpf2, this.p);
                }
            }, 0, 1, 0);
        }
    }

    public void setTextAndValueAndTypeAndThumb(String str, String str2, String str3, String str4, int i, boolean z) {
        String str5;
        String str6;
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
            this.thumbImageView.setImageBitmap((Bitmap) null);
            this.thumbImageView.setVisibility(4);
        } else {
            if (str4 != null) {
                this.thumbImageView.setImage(str4, "42_42", (Drawable) null);
            } else {
                CombinedDrawable createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(42.0f), i);
                if (i == NUM) {
                    str6 = "chat_attachLocationBackground";
                    str5 = "chat_attachLocationIcon";
                } else if (i == NUM) {
                    str6 = "chat_attachContactBackground";
                    str5 = "chat_attachContactIcon";
                } else if (i == NUM) {
                    str6 = "chat_attachAudioBackground";
                    str5 = "chat_attachAudioIcon";
                } else if (i == NUM) {
                    str6 = "chat_attachGalleryBackground";
                    str5 = "chat_attachGalleryIcon";
                } else {
                    str6 = "files_folderIconBackground";
                    str5 = "files_folderIcon";
                }
                Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, Theme.getColor(str6), false);
                Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, Theme.getColor(str5), true);
                this.thumbImageView.setImageDrawable(createCircleDrawableWithIcon);
            }
            this.thumbImageView.setVisibility(0);
        }
        setWillNotDraw(true ^ this.needDivider);
    }

    public void setPhotoEntry(MediaController.PhotoEntry photoEntry) {
        String str;
        String str2 = photoEntry.thumbPath;
        if (str2 != null) {
            this.thumbImageView.setImage(str2, (String) null, Theme.chat_attachEmptyDrawable);
            str = photoEntry.thumbPath;
        } else if (photoEntry.path != null) {
            if (photoEntry.isVideo) {
                this.thumbImageView.setOrientation(0, true);
                BackupImageView backupImageView = this.thumbImageView;
                backupImageView.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
            } else {
                this.thumbImageView.setOrientation(photoEntry.orientation, true);
                BackupImageView backupImageView2 = this.thumbImageView;
                backupImageView2.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
            }
            str = photoEntry.path;
        } else {
            this.thumbImageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
            str = "";
        }
        File file = new File(str);
        this.nameTextView.setText(file.getName());
        FileLoader.getFileExtension(file);
        StringBuilder sb = new StringBuilder();
        this.extTextView.setVisibility(8);
        if (!(photoEntry.width == 0 || photoEntry.height == 0)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(String.format(Locale.US, "%dx%d", new Object[]{Integer.valueOf(photoEntry.width), Integer.valueOf(photoEntry.height)}));
        }
        if (photoEntry.isVideo) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(AndroidUtilities.formatShortDuration(photoEntry.duration));
        }
        if (photoEntry.size != 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(AndroidUtilities.formatFileSize(photoEntry.size));
        }
        if (sb.length() > 0) {
            sb.append(", ");
        }
        sb.append(LocaleController.getInstance().formatterStats.format(photoEntry.dateTaken));
        this.dateTextView.setText(sb);
        this.placeholderImageView.setVisibility(8);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    /* access modifiers changed from: protected */
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
        String str;
        String str2;
        MessageObject messageObject2 = messageObject;
        this.needDivider = z;
        this.message = messageObject2;
        this.loaded = false;
        this.loading = false;
        TLRPC$Document document = messageObject.getDocument();
        int i = 8;
        String str3 = "";
        if (messageObject2 == null || document == null) {
            this.nameTextView.setText(str3);
            this.extTextView.setText(str3);
            this.dateTextView.setText(str3);
            this.placeholderImageView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.extTextView.setAlpha(1.0f);
            this.placeholderImageView.setAlpha(1.0f);
            this.thumbImageView.setVisibility(4);
            this.thumbImageView.setImageBitmap((Bitmap) null);
            this.caption = null;
            TextView textView = this.captionTextView;
            if (textView != null) {
                textView.setVisibility(8);
            }
        } else {
            String str4 = null;
            if (messageObject.isMusic()) {
                for (int i2 = 0; i2 < document.attributes.size(); i2++) {
                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute = document.attributes.get(i2);
                    if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) && !(((str = tLRPC$DocumentAttribute.performer) == null || str.length() == 0) && ((str2 = tLRPC$DocumentAttribute.title) == null || str2.length() == 0))) {
                        str4 = messageObject.getMusicAuthor() + " - " + messageObject.getMusicTitle();
                    }
                }
            }
            String documentFileName = FileLoader.getDocumentFileName(document);
            if (str4 == null) {
                str4 = documentFileName;
            }
            CharSequence highlightText = AndroidUtilities.highlightText((CharSequence) str4, messageObject2.highlightedWords);
            if (highlightText != null) {
                this.nameTextView.setText(highlightText);
            } else {
                this.nameTextView.setText(str4);
            }
            this.placeholderImageView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.placeholderImageView.setImageResource(AndroidUtilities.getThumbForNameOrMime(documentFileName, document.mime_type, false));
            TextView textView2 = this.extTextView;
            int lastIndexOf = documentFileName.lastIndexOf(46);
            if (lastIndexOf != -1) {
                str3 = documentFileName.substring(lastIndexOf + 1).toLowerCase();
            }
            textView2.setText(str3);
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 40);
            if (closestPhotoSizeWithSize2 == closestPhotoSizeWithSize) {
                closestPhotoSizeWithSize = null;
            }
            if ((closestPhotoSizeWithSize2 instanceof TLRPC$TL_photoSizeEmpty) || closestPhotoSizeWithSize2 == null) {
                this.thumbImageView.setVisibility(4);
                this.thumbImageView.setImageBitmap((Bitmap) null);
                this.extTextView.setAlpha(1.0f);
                this.placeholderImageView.setAlpha(1.0f);
            } else {
                this.thumbImageView.getImageReceiver().setNeedsQualityThumb(closestPhotoSizeWithSize == null);
                this.thumbImageView.getImageReceiver().setShouldGenerateQualityThumb(closestPhotoSizeWithSize == null);
                this.thumbImageView.setVisibility(0);
                this.thumbImageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "40_40", ImageLocation.getForDocument(closestPhotoSizeWithSize2, document), "40_40_b", (String) null, 0, 1, messageObject);
            }
            long j = ((long) messageObject2.messageOwner.date) * 1000;
            if (this.viewType == 2) {
                this.dateTextView.setText(new SpannableStringBuilder().append(AndroidUtilities.formatFileSize((long) document.size)).append(' ').append(this.dotSpan).append(' ').append(FilteredSearchView.createFromInfoString(messageObject)));
                this.rightDateTextView.setText(LocaleController.stringForMessageListDate((long) messageObject2.messageOwner.date));
            } else {
                this.dateTextView.setText(String.format("%s, %s", new Object[]{AndroidUtilities.formatFileSize((long) document.size), LocaleController.formatString("formatDateAtTime", NUM, LocaleController.getInstance().formatterYear.format(new Date(j)), LocaleController.getInstance().formatterDay.format(new Date(j)))}));
            }
            if (!messageObject.hasHighlightedWords() || TextUtils.isEmpty(this.message.messageOwner.message)) {
                TextView textView3 = this.captionTextView;
                if (textView3 != null) {
                    textView3.setVisibility(8);
                }
            } else {
                CharSequence highlightText2 = AndroidUtilities.highlightText((CharSequence) this.message.messageOwner.message.replace("\n", " ").trim(), this.message.highlightedWords);
                this.caption = highlightText2;
                TextView textView4 = this.captionTextView;
                if (textView4 != null) {
                    if (highlightText2 != null) {
                        i = 0;
                    }
                    textView4.setVisibility(i);
                }
            }
        }
        setWillNotDraw(!this.needDivider);
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.viewType == 1) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
            return;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
        int dp = AndroidUtilities.dp(34.0f) + this.nameTextView.getMeasuredHeight() + (this.needDivider ? 1 : 0);
        if (!(this.caption == null || this.captionTextView == null || !this.message.hasHighlightedWords())) {
            this.ignoreRequestLayout = true;
            this.captionTextView.setText(AndroidUtilities.ellipsizeCenterEnd(this.caption, this.message.highlightedWords.get(0), this.captionTextView.getMeasuredWidth(), this.captionTextView.getPaint()));
            this.ignoreRequestLayout = false;
            dp += this.captionTextView.getMeasuredHeight() + AndroidUtilities.dp(3.0f);
        }
        setMeasuredDimension(getMeasuredWidth(), dp);
    }

    public void requestLayout() {
        if (!this.ignoreRequestLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        TextView textView;
        super.onLayout(z, i, i2, i3, i4);
        if (!(this.viewType == 1 || this.nameTextView.getLineCount() > 1 || (textView = this.captionTextView) == null)) {
            textView.getVisibility();
        }
        int measuredHeight = this.nameTextView.getMeasuredHeight() - AndroidUtilities.dp(22.0f);
        TextView textView2 = this.captionTextView;
        if (textView2 != null && textView2.getVisibility() == 0) {
            TextView textView3 = this.captionTextView;
            textView3.layout(textView3.getLeft(), this.captionTextView.getTop() + measuredHeight, this.captionTextView.getRight(), this.captionTextView.getBottom() + measuredHeight);
            measuredHeight += this.captionTextView.getMeasuredHeight() + AndroidUtilities.dp(3.0f);
        }
        TextView textView4 = this.dateTextView;
        textView4.layout(textView4.getLeft(), this.dateTextView.getTop() + measuredHeight, this.dateTextView.getRight(), this.dateTextView.getBottom() + measuredHeight);
        ImageView imageView = this.statusImageView;
        imageView.layout(imageView.getLeft(), this.statusImageView.getTop() + measuredHeight, this.statusImageView.getRight(), measuredHeight + this.statusImageView.getBottom());
        LineProgressView lineProgressView = this.progressView;
        lineProgressView.layout(lineProgressView.getLeft(), (getMeasuredHeight() - this.progressView.getMeasuredHeight()) - (this.needDivider ? 1 : 0), this.progressView.getRight(), getMeasuredHeight() - (this.needDivider ? 1 : 0));
    }

    /* access modifiers changed from: protected */
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
