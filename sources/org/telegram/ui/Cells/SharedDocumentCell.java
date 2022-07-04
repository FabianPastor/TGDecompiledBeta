package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.FilteredSearchView;

public class SharedDocumentCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
    public static final int VIEW_TYPE_DEFAULT = 0;
    public static final int VIEW_TYPE_GLOBAL_SEARCH = 2;
    public static final int VIEW_TYPE_PICKER = 1;
    private int TAG;
    private CharSequence caption;
    private TextView captionTextView;
    private CheckBox2 checkBox;
    private int currentAccount;
    private TextView dateTextView;
    private SpannableStringBuilder dotSpan;
    private long downloadedSize;
    private boolean drawDownloadIcon;
    float enterAlpha;
    /* access modifiers changed from: private */
    public TextView extTextView;
    FlickerLoadingView globalGradientView;
    boolean ignoreRequestLayout;
    private boolean loaded;
    private boolean loading;
    private MessageObject message;
    private TextView nameTextView;
    private boolean needDivider;
    /* access modifiers changed from: private */
    public ImageView placeholderImageView;
    private LineProgressView progressView;
    private final Theme.ResourcesProvider resourcesProvider;
    public TextView rightDateTextView;
    private RLottieDrawable statusDrawable;
    private RLottieImageView statusImageView;
    /* access modifiers changed from: private */
    public BackupImageView thumbImageView;
    private int viewType;

    public SharedDocumentCell(Context context) {
        this(context, 0);
    }

    public SharedDocumentCell(Context context, int viewType2) {
        this(context, viewType2, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SharedDocumentCell(Context context, int viewType2, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        int i = viewType2;
        this.drawDownloadIcon = true;
        int i2 = UserConfig.selectedAccount;
        this.currentAccount = i2;
        this.enterAlpha = 1.0f;
        this.resourcesProvider = resourcesProvider2;
        this.viewType = i;
        this.TAG = DownloadController.getInstance(i2).generateObserverTag();
        ImageView imageView = new ImageView(context2);
        this.placeholderImageView = imageView;
        if (i == 1) {
            addView(imageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 12.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
        } else {
            addView(imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        }
        TextView textView = new TextView(context2);
        this.extTextView = textView;
        textView.setTextColor(getThemedColor("files_iconText"));
        this.extTextView.setTextSize(1, 14.0f);
        this.extTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.extTextView.setLines(1);
        this.extTextView.setMaxLines(1);
        this.extTextView.setSingleLine(true);
        this.extTextView.setGravity(17);
        this.extTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.extTextView.setImportantForAccessibility(2);
        if (i == 1) {
            addView(this.extTextView, LayoutHelper.createFrame(32, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 20.0f, 28.0f, LocaleController.isRTL ? 20.0f : 0.0f, 0.0f));
        } else {
            addView(this.extTextView, LayoutHelper.createFrame(32, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 22.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        }
        AnonymousClass1 r14 = new BackupImageView(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                float alpha;
                if (SharedDocumentCell.this.thumbImageView.getImageReceiver().hasBitmapImage()) {
                    alpha = 1.0f - SharedDocumentCell.this.thumbImageView.getImageReceiver().getCurrentAlpha();
                } else {
                    alpha = 1.0f;
                }
                SharedDocumentCell.this.extTextView.setAlpha(alpha);
                SharedDocumentCell.this.placeholderImageView.setAlpha(alpha);
                super.onDraw(canvas);
            }
        };
        this.thumbImageView = r14;
        r14.setRoundRadius(AndroidUtilities.dp(4.0f));
        if (i == 1) {
            addView(this.thumbImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        } else {
            addView(this.thumbImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        }
        TextView textView2 = new TextView(context2);
        this.nameTextView = textView2;
        textView2.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        if (i == 1) {
            this.nameTextView.setLines(1);
            this.nameTextView.setMaxLines(1);
            this.nameTextView.setSingleLine(true);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 9.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else if (i == 2) {
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16.0f : 72.0f, 5.0f, LocaleController.isRTL ? 72.0f : 16.0f, 0.0f));
            TextView textView3 = new TextView(context2);
            this.rightDateTextView = textView3;
            textView3.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
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
            textView4.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
            this.captionTextView.setLines(1);
            this.captionTextView.setMaxLines(1);
            this.captionTextView.setSingleLine(true);
            this.captionTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.captionTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.captionTextView.setTextSize(1, 13.0f);
            addView(this.captionTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 30.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
            this.captionTextView.setVisibility(8);
        } else {
            this.nameTextView.setMaxLines(1);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 5.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        }
        this.statusDrawable = new RLottieDrawable(NUM, "download_arrow", AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), true, (int[]) null);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.statusImageView = rLottieImageView;
        rLottieImageView.setAnimation(this.statusDrawable);
        this.statusImageView.setVisibility(4);
        this.statusImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("sharedMedia_startStopLoadIcon"), PorterDuff.Mode.MULTIPLY));
        if (i == 1) {
            addView(this.statusImageView, LayoutHelper.createFrame(14, 14.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 70.0f, 37.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else {
            addView(this.statusImageView, LayoutHelper.createFrame(14, 14.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 70.0f, 33.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        }
        TextView textView5 = new TextView(context2);
        this.dateTextView = textView5;
        textView5.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
        this.dateTextView.setLines(1);
        this.dateTextView.setMaxLines(1);
        this.dateTextView.setSingleLine(true);
        this.dateTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.dateTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        if (i == 1) {
            this.dateTextView.setTextSize(1, 13.0f);
            addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 34.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else {
            this.dateTextView.setTextSize(1, 13.0f);
            addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 30.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        }
        LineProgressView lineProgressView = new LineProgressView(context2);
        this.progressView = lineProgressView;
        lineProgressView.setProgressColor(getThemedColor("sharedMedia_startStopLoadIcon"));
        addView(this.progressView, LayoutHelper.createFrame(-1, 2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 72.0f, 54.0f, LocaleController.isRTL ? 72.0f : 0.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context2, 21);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(2);
        if (i == 1) {
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 38.0f, 36.0f, LocaleController.isRTL ? 38.0f : 0.0f, 0.0f));
        } else {
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 33.0f, 28.0f, LocaleController.isRTL ? 33.0f : 0.0f, 0.0f));
        }
        if (i == 2) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(".");
            this.dotSpan = spannableStringBuilder;
            spannableStringBuilder.setSpan(new DotDividerSpan(), 0, 1, 0);
        }
    }

    public void setDrawDownloadIcon(boolean value) {
        this.drawDownloadIcon = value;
    }

    public void setTextAndValueAndTypeAndThumb(String text, String value, String type, String thumb, int resId, boolean divider) {
        String iconKey;
        String backKey;
        this.nameTextView.setText(text);
        this.dateTextView.setText(value);
        if (type != null) {
            this.extTextView.setVisibility(0);
            this.extTextView.setText(type.toLowerCase());
        } else {
            this.extTextView.setVisibility(4);
        }
        this.needDivider = divider;
        if (resId == 0) {
            this.placeholderImageView.setImageResource(AndroidUtilities.getThumbForNameOrMime(text, type, false));
            this.placeholderImageView.setVisibility(0);
        } else {
            this.placeholderImageView.setVisibility(4);
        }
        if (thumb == null && resId == 0) {
            this.extTextView.setAlpha(1.0f);
            this.placeholderImageView.setAlpha(1.0f);
            this.thumbImageView.setImageBitmap((Bitmap) null);
            this.thumbImageView.setVisibility(4);
        } else {
            if (thumb != null) {
                this.thumbImageView.setImage(thumb, "42_42", (Drawable) null);
            } else {
                Drawable drawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(42.0f), resId);
                if (resId == NUM) {
                    backKey = "chat_attachLocationBackground";
                    iconKey = "chat_attachLocationIcon";
                } else if (resId == NUM) {
                    backKey = "chat_attachContactBackground";
                    iconKey = "chat_attachContactIcon";
                } else if (resId == NUM) {
                    backKey = "chat_attachAudioBackground";
                    iconKey = "chat_attachAudioIcon";
                } else if (resId == NUM) {
                    backKey = "chat_attachGalleryBackground";
                    iconKey = "chat_attachGalleryIcon";
                } else {
                    backKey = "files_folderIconBackground";
                    iconKey = "files_folderIcon";
                }
                Theme.setCombinedDrawableColor(drawable, getThemedColor(backKey), false);
                Theme.setCombinedDrawableColor(drawable, getThemedColor(iconKey), true);
                this.thumbImageView.setImageDrawable(drawable);
            }
            this.thumbImageView.setVisibility(0);
        }
        setWillNotDraw(!this.needDivider);
    }

    public void setPhotoEntry(MediaController.PhotoEntry entry) {
        String path;
        if (entry.thumbPath != null) {
            this.thumbImageView.setImage(entry.thumbPath, (String) null, Theme.chat_attachEmptyDrawable);
            path = entry.thumbPath;
        } else if (entry.path != null) {
            if (entry.isVideo) {
                this.thumbImageView.setOrientation(0, true);
                BackupImageView backupImageView = this.thumbImageView;
                backupImageView.setImage("vthumb://" + entry.imageId + ":" + entry.path, (String) null, Theme.chat_attachEmptyDrawable);
            } else {
                this.thumbImageView.setOrientation(entry.orientation, true);
                BackupImageView backupImageView2 = this.thumbImageView;
                backupImageView2.setImage("thumb://" + entry.imageId + ":" + entry.path, (String) null, Theme.chat_attachEmptyDrawable);
            }
            path = entry.path;
        } else {
            this.thumbImageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
            path = "";
        }
        File file = new File(path);
        this.nameTextView.setText(file.getName());
        String fileExtension = FileLoader.getFileExtension(file);
        this.extTextView.setVisibility(8);
        StringBuilder builder = new StringBuilder();
        if (!(entry.width == 0 || entry.height == 0)) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(String.format(Locale.US, "%dx%d", new Object[]{Integer.valueOf(entry.width), Integer.valueOf(entry.height)}));
        }
        if (entry.isVideo) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(AndroidUtilities.formatShortDuration(entry.duration));
        }
        if (entry.size != 0) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(AndroidUtilities.formatFileSize(entry.size));
        }
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(LocaleController.getInstance().formatterStats.format(entry.dateTaken));
        this.dateTextView.setText(builder);
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
            updateFileExistIcon(false);
        }
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    /* JADX WARNING: Removed duplicated region for block: B:93:0x022c  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x025e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setDocument(org.telegram.messenger.MessageObject r25, boolean r26) {
        /*
            r24 = this;
            r0 = r24
            r11 = r25
            org.telegram.messenger.MessageObject r1 = r0.message
            r13 = 0
            if (r1 == 0) goto L_0x0017
            if (r11 == 0) goto L_0x0017
            int r1 = r1.getId()
            int r2 = r25.getId()
            if (r1 == r2) goto L_0x0017
            r1 = 1
            goto L_0x0018
        L_0x0017:
            r1 = 0
        L_0x0018:
            r14 = r1
            r15 = r26
            r0.needDivider = r15
            r0.message = r11
            r0.loaded = r13
            r0.loading = r13
            if (r14 != 0) goto L_0x0029
            r1 = 0
            r0.downloadedSize = r1
        L_0x0029:
            org.telegram.tgnet.TLRPC$Document r10 = r25.getDocument()
            r1 = 4
            r2 = 0
            r3 = 1065353216(0x3var_, float:1.0)
            java.lang.String r4 = ""
            if (r10 == 0) goto L_0x0268
            r5 = 0
            boolean r6 = r25.isMusic()
            if (r6 == 0) goto L_0x0088
            r6 = 0
        L_0x003d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r10.attributes
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x0088
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r10.attributes
            java.lang.Object r7 = r7.get(r6)
            org.telegram.tgnet.TLRPC$DocumentAttribute r7 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r7
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio
            if (r8 == 0) goto L_0x0085
            java.lang.String r8 = r7.performer
            if (r8 == 0) goto L_0x005d
            java.lang.String r8 = r7.performer
            int r8 = r8.length()
            if (r8 != 0) goto L_0x0069
        L_0x005d:
            java.lang.String r8 = r7.title
            if (r8 == 0) goto L_0x0085
            java.lang.String r8 = r7.title
            int r8 = r8.length()
            if (r8 == 0) goto L_0x0085
        L_0x0069:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = r25.getMusicAuthor()
            r8.append(r9)
            java.lang.String r9 = " - "
            r8.append(r9)
            java.lang.String r9 = r25.getMusicTitle()
            r8.append(r9)
            java.lang.String r5 = r8.toString()
        L_0x0085:
            int r6 = r6 + 1
            goto L_0x003d
        L_0x0088:
            r6 = 0
            boolean r7 = r25.isVideo()
            if (r7 != 0) goto L_0x00a1
            org.telegram.tgnet.TLRPC$Message r7 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r7 != 0) goto L_0x00a1
            boolean r7 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r10)
            if (r7 != 0) goto L_0x00a1
            java.lang.String r6 = org.telegram.messenger.FileLoader.getDocumentFileName(r10)
        L_0x00a1:
            boolean r7 = android.text.TextUtils.isEmpty(r6)
            if (r7 == 0) goto L_0x0112
            java.lang.String r7 = r10.mime_type
            if (r7 == 0) goto L_0x0112
            java.lang.String r7 = r10.mime_type
            java.lang.String r8 = "video"
            boolean r7 = r7.startsWith(r8)
            r8 = 2131624483(0x7f0e0223, float:1.8876147E38)
            java.lang.String r9 = "AttachGif"
            if (r7 == 0) goto L_0x00d1
            boolean r7 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r10)
            if (r7 == 0) goto L_0x00c6
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r12 = r6
            goto L_0x0113
        L_0x00c6:
            r7 = 2131624508(0x7f0e023c, float:1.8876198E38)
            java.lang.String r8 = "AttachVideo"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r12 = r6
            goto L_0x0113
        L_0x00d1:
            java.lang.String r7 = r10.mime_type
            java.lang.String r12 = "image"
            boolean r7 = r7.startsWith(r12)
            if (r7 == 0) goto L_0x00f2
            boolean r7 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r10)
            if (r7 == 0) goto L_0x00e7
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r12 = r6
            goto L_0x0113
        L_0x00e7:
            r7 = 2131624502(0x7f0e0236, float:1.8876186E38)
            java.lang.String r8 = "AttachPhoto"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r12 = r6
            goto L_0x0113
        L_0x00f2:
            java.lang.String r7 = r10.mime_type
            java.lang.String r8 = "audio"
            boolean r7 = r7.startsWith(r8)
            if (r7 == 0) goto L_0x0107
            r7 = 2131624476(0x7f0e021c, float:1.8876133E38)
            java.lang.String r8 = "AttachAudio"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r12 = r6
            goto L_0x0113
        L_0x0107:
            r7 = 2131624481(0x7f0e0221, float:1.8876143E38)
            java.lang.String r8 = "AttachDocument"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r12 = r6
            goto L_0x0113
        L_0x0112:
            r12 = r6
        L_0x0113:
            if (r5 != 0) goto L_0x0118
            r5 = r12
            r9 = r5
            goto L_0x0119
        L_0x0118:
            r9 = r5
        L_0x0119:
            java.util.ArrayList<java.lang.String> r5 = r11.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r0.resourcesProvider
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r9, (java.util.ArrayList<java.lang.String>) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            if (r8 == 0) goto L_0x0129
            android.widget.TextView r5 = r0.nameTextView
            r5.setText(r8)
            goto L_0x012e
        L_0x0129:
            android.widget.TextView r5 = r0.nameTextView
            r5.setText(r9)
        L_0x012e:
            android.widget.ImageView r5 = r0.placeholderImageView
            r5.setVisibility(r13)
            android.widget.TextView r5 = r0.extTextView
            r5.setVisibility(r13)
            android.widget.ImageView r5 = r0.placeholderImageView
            java.lang.String r6 = r10.mime_type
            int r6 = org.telegram.messenger.AndroidUtilities.getThumbForNameOrMime(r12, r6, r13)
            r5.setImageResource(r6)
            android.widget.TextView r5 = r0.extTextView
            r6 = 46
            int r6 = r12.lastIndexOf(r6)
            r17 = r6
            r7 = -1
            if (r6 != r7) goto L_0x0151
            goto L_0x015b
        L_0x0151:
            int r4 = r17 + 1
            java.lang.String r4 = r12.substring(r4)
            java.lang.String r4 = r4.toLowerCase()
        L_0x015b:
            r5.setText(r4)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r10.thumbs
            r5 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r10.thumbs
            r6 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            if (r7 != r4) goto L_0x0173
            r4 = 0
            r6 = r4
            goto L_0x0174
        L_0x0173:
            r6 = r4
        L_0x0174:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty
            if (r4 != 0) goto L_0x01fb
            if (r7 != 0) goto L_0x0184
            r13 = r6
            r19 = r7
            r18 = r8
            r16 = r9
            r11 = r10
            goto L_0x0203
        L_0x0184:
            org.telegram.ui.Components.BackupImageView r1 = r0.thumbImageView
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            if (r6 != 0) goto L_0x018e
            r2 = 1
            goto L_0x018f
        L_0x018e:
            r2 = 0
        L_0x018f:
            r1.setNeedsQualityThumb(r2)
            org.telegram.ui.Components.BackupImageView r1 = r0.thumbImageView
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            if (r6 != 0) goto L_0x019c
            r2 = 1
            goto L_0x019d
        L_0x019c:
            r2 = 0
        L_0x019d:
            r1.setShouldGenerateQualityThumb(r2)
            org.telegram.ui.Components.BackupImageView r1 = r0.thumbImageView
            r1.setVisibility(r13)
            android.graphics.drawable.BitmapDrawable r1 = r11.strippedThumb
            if (r1 == 0) goto L_0x01d7
            org.telegram.ui.Components.BackupImageView r1 = r0.thumbImageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r6, (org.telegram.tgnet.TLRPC.Document) r10)
            r4 = 0
            r5 = 0
            android.graphics.drawable.BitmapDrawable r3 = r11.strippedThumb
            r18 = 0
            r19 = 0
            r20 = 1
            java.lang.String r21 = "40_40"
            r22 = r3
            r3 = r21
            r13 = r6
            r6 = r22
            r23 = r7
            r7 = r18
            r18 = r8
            r8 = r19
            r16 = r9
            r9 = r20
            r11 = r10
            r10 = r25
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r19 = r23
            goto L_0x0217
        L_0x01d7:
            r13 = r6
            r23 = r7
            r18 = r8
            r16 = r9
            r11 = r10
            org.telegram.ui.Components.BackupImageView r1 = r0.thumbImageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r13, (org.telegram.tgnet.TLRPC.Document) r11)
            r10 = r23
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r10, (org.telegram.tgnet.TLRPC.Document) r11)
            r6 = 0
            r7 = 0
            r9 = 1
            java.lang.String r3 = "40_40"
            java.lang.String r5 = "40_40_b"
            r19 = r10
            r10 = r25
            r1.setImage(r2, r3, r4, r5, r6, r7, r9, r10)
            goto L_0x0217
        L_0x01fb:
            r13 = r6
            r19 = r7
            r18 = r8
            r16 = r9
            r11 = r10
        L_0x0203:
            org.telegram.ui.Components.BackupImageView r4 = r0.thumbImageView
            r4.setVisibility(r1)
            org.telegram.ui.Components.BackupImageView r1 = r0.thumbImageView
            r1.setImageBitmap(r2)
            android.widget.TextView r1 = r0.extTextView
            r1.setAlpha(r3)
            android.widget.ImageView r1 = r0.placeholderImageView
            r1.setAlpha(r3)
        L_0x0217:
            r24.updateDateView()
            boolean r1 = r25.hasHighlightedWords()
            if (r1 == 0) goto L_0x025e
            org.telegram.messenger.MessageObject r1 = r0.message
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x025e
            org.telegram.messenger.MessageObject r1 = r0.message
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            java.lang.String r2 = "\n"
            java.lang.String r3 = " "
            java.lang.String r1 = r1.replace(r2, r3)
            java.lang.String r2 = " +"
            java.lang.String r1 = r1.replaceAll(r2, r3)
            java.lang.String r1 = r1.trim()
            org.telegram.messenger.MessageObject r2 = r0.message
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r0.resourcesProvider
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r1, (java.util.ArrayList<java.lang.String>) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
            r0.caption = r2
            android.widget.TextView r3 = r0.captionTextView
            if (r3 == 0) goto L_0x025d
            if (r2 != 0) goto L_0x0259
            r9 = 8
            goto L_0x025a
        L_0x0259:
            r9 = 0
        L_0x025a:
            r3.setVisibility(r9)
        L_0x025d:
            goto L_0x0267
        L_0x025e:
            android.widget.TextView r1 = r0.captionTextView
            if (r1 == 0) goto L_0x0267
            r5 = 8
            r1.setVisibility(r5)
        L_0x0267:
            goto L_0x02a2
        L_0x0268:
            r11 = r10
            r5 = 8
            android.widget.TextView r6 = r0.nameTextView
            r6.setText(r4)
            android.widget.TextView r6 = r0.extTextView
            r6.setText(r4)
            android.widget.TextView r6 = r0.dateTextView
            r6.setText(r4)
            android.widget.ImageView r4 = r0.placeholderImageView
            r6 = 0
            r4.setVisibility(r6)
            android.widget.TextView r4 = r0.extTextView
            r4.setVisibility(r6)
            android.widget.TextView r4 = r0.extTextView
            r4.setAlpha(r3)
            android.widget.ImageView r4 = r0.placeholderImageView
            r4.setAlpha(r3)
            org.telegram.ui.Components.BackupImageView r3 = r0.thumbImageView
            r3.setVisibility(r1)
            org.telegram.ui.Components.BackupImageView r1 = r0.thumbImageView
            r1.setImageBitmap(r2)
            r0.caption = r2
            android.widget.TextView r1 = r0.captionTextView
            if (r1 == 0) goto L_0x02a2
            r1.setVisibility(r5)
        L_0x02a2:
            boolean r1 = r0.needDivider
            r2 = 1
            r1 = r1 ^ r2
            r0.setWillNotDraw(r1)
            org.telegram.ui.Components.LineProgressView r1 = r0.progressView
            r2 = 0
            r3 = 0
            r1.setProgress(r2, r3)
            r0.updateFileExistIcon(r14)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedDocumentCell.setDocument(org.telegram.messenger.MessageObject, boolean):void");
    }

    private void updateDateView() {
        String fileSize;
        MessageObject messageObject = this.message;
        if (messageObject != null && messageObject.getDocument() != null) {
            long date = ((long) this.message.messageOwner.date) * 1000;
            if (this.downloadedSize == 0) {
                fileSize = AndroidUtilities.formatFileSize(this.message.getDocument().size);
            } else {
                fileSize = String.format(Locale.ENGLISH, "%s / %s", new Object[]{AndroidUtilities.formatFileSize(this.downloadedSize), AndroidUtilities.formatFileSize(this.message.getDocument().size)});
            }
            if (this.viewType == 2) {
                this.dateTextView.setText(new SpannableStringBuilder().append(fileSize).append(' ').append(this.dotSpan).append(' ').append(FilteredSearchView.createFromInfoString(this.message)));
                this.rightDateTextView.setText(LocaleController.stringForMessageListDate((long) this.message.messageOwner.date));
                return;
            }
            this.dateTextView.setText(String.format("%s, %s", new Object[]{fileSize, LocaleController.formatString("formatDateAtTime", NUM, LocaleController.getInstance().formatterYear.format(new Date(date)), LocaleController.getInstance().formatterDay.format(new Date(date)))}));
        }
    }

    public void updateFileExistIcon(boolean animated) {
        if (animated && Build.VERSION.SDK_INT >= 19) {
            TransitionSet transition = new TransitionSet();
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(150);
            transition.addTransition(new Fade().setDuration(150)).addTransition(changeBounds);
            transition.setOrdering(0);
            transition.setInterpolator(CubicBezierInterpolator.DEFAULT);
            TransitionManager.beginDelayedTransition(this, transition);
        }
        MessageObject messageObject = this.message;
        float f = 72.0f;
        float f2 = 8.0f;
        if (messageObject == null || messageObject.messageOwner.media == null) {
            this.loading = false;
            this.loaded = true;
            this.progressView.setVisibility(4);
            this.progressView.setProgress(0.0f, false);
            this.statusImageView.setVisibility(4);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.dateTextView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : 72.0f);
                if (!LocaleController.isRTL) {
                    f = 8.0f;
                }
                layoutParams.rightMargin = AndroidUtilities.dp(f);
                this.dateTextView.requestLayout();
            }
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            return;
        }
        this.loaded = false;
        if (this.message.attachPathExists || this.message.mediaExists || !this.drawDownloadIcon) {
            this.statusImageView.setVisibility(4);
            this.progressView.setVisibility(4);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.dateTextView.getLayoutParams();
            if (layoutParams2 != null) {
                layoutParams2.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : 72.0f);
                if (!LocaleController.isRTL) {
                    f = 8.0f;
                }
                layoutParams2.rightMargin = AndroidUtilities.dp(f);
                this.dateTextView.requestLayout();
            }
            this.loading = false;
            this.loaded = true;
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            return;
        }
        String fileName = FileLoader.getAttachFileName(this.message.getDocument());
        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.message, this);
        this.loading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
        this.statusImageView.setVisibility(0);
        int i = 15;
        this.statusDrawable.setCustomEndFrame(this.loading ? 15 : 0);
        this.statusDrawable.setPlayInDirectionOfCustomEndFrame(true);
        if (animated) {
            this.statusImageView.playAnimation();
        } else {
            RLottieDrawable rLottieDrawable = this.statusDrawable;
            if (!this.loading) {
                i = 0;
            }
            rLottieDrawable.setCurrentFrame(i);
            this.statusImageView.invalidate();
        }
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.dateTextView.getLayoutParams();
        if (layoutParams3 != null) {
            layoutParams3.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : 86.0f);
            if (LocaleController.isRTL) {
                f2 = 86.0f;
            }
            layoutParams3.rightMargin = AndroidUtilities.dp(f2);
            this.dateTextView.requestLayout();
        }
        if (this.loading) {
            this.progressView.setVisibility(0);
            Float progress = ImageLoader.getInstance().getFileProgress(fileName);
            if (progress == null) {
                progress = Float.valueOf(0.0f);
            }
            this.progressView.setProgress(progress.floatValue(), false);
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i = this.viewType;
        if (i == 1) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        } else if (i == 0) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
            int h = AndroidUtilities.dp(34.0f) + this.nameTextView.getMeasuredHeight() + (this.needDivider ? 1 : 0);
            if (!(this.caption == null || this.captionTextView == null || !this.message.hasHighlightedWords())) {
                this.ignoreRequestLayout = true;
                this.captionTextView.setText(AndroidUtilities.ellipsizeCenterEnd(this.caption, this.message.highlightedWords.get(0), this.captionTextView.getMeasuredWidth(), this.captionTextView.getPaint(), 130));
                this.ignoreRequestLayout = false;
                h += this.captionTextView.getMeasuredHeight() + AndroidUtilities.dp(3.0f);
            }
            setMeasuredDimension(getMeasuredWidth(), h);
        }
    }

    public void requestLayout() {
        if (!this.ignoreRequestLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        TextView textView;
        super.onLayout(changed, left, top, right, bottom);
        if (!(this.viewType == 1 || this.nameTextView.getLineCount() > 1 || (textView = this.captionTextView) == null)) {
            textView.getVisibility();
        }
        int y = this.nameTextView.getMeasuredHeight() - AndroidUtilities.dp(22.0f);
        TextView textView2 = this.captionTextView;
        if (textView2 != null && textView2.getVisibility() == 0) {
            TextView textView3 = this.captionTextView;
            textView3.layout(textView3.getLeft(), this.captionTextView.getTop() + y, this.captionTextView.getRight(), this.captionTextView.getBottom() + y);
            y += this.captionTextView.getMeasuredHeight() + AndroidUtilities.dp(3.0f);
        }
        TextView textView4 = this.dateTextView;
        textView4.layout(textView4.getLeft(), this.dateTextView.getTop() + y, this.dateTextView.getRight(), this.dateTextView.getBottom() + y);
        RLottieImageView rLottieImageView = this.statusImageView;
        rLottieImageView.layout(rLottieImageView.getLeft(), this.statusImageView.getTop() + y, this.statusImageView.getRight(), this.statusImageView.getBottom() + y);
        LineProgressView lineProgressView = this.progressView;
        lineProgressView.layout(lineProgressView.getLeft(), (getMeasuredHeight() - this.progressView.getMeasuredHeight()) - (this.needDivider ? 1 : 0), this.progressView.getRight(), getMeasuredHeight() - (this.needDivider ? 1 : 0));
    }

    public void onFailedDownload(String name, boolean canceled) {
        updateFileExistIcon(true);
        this.downloadedSize = 0;
        updateDateView();
    }

    public void onSuccessDownload(String name) {
        this.progressView.setProgress(1.0f, true);
        updateFileExistIcon(true);
        this.downloadedSize = 0;
        updateDateView();
    }

    public void onProgressDownload(String fileName, long downloadedSize2, long totalSize) {
        if (this.progressView.getVisibility() != 0) {
            updateFileExistIcon(true);
        }
        this.downloadedSize = downloadedSize2;
        updateDateView();
        this.progressView.setProgress(Math.min(1.0f, ((float) downloadedSize2) / ((float) totalSize)), true);
    }

    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.checkBox.isChecked()) {
            info.setCheckable(true);
            info.setChecked(this.checkBox.isChecked());
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void setGlobalGradientView(FlickerLoadingView globalGradientView2) {
        this.globalGradientView = globalGradientView2;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.enterAlpha == 1.0f || this.globalGradientView == null) {
            super.dispatchDraw(canvas);
            drawDivider(canvas);
            return;
        }
        canvas.saveLayerAlpha(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), (int) ((1.0f - this.enterAlpha) * 255.0f), 31);
        this.globalGradientView.setViewType(3);
        this.globalGradientView.updateColors();
        this.globalGradientView.updateGradient();
        this.globalGradientView.draw(canvas);
        canvas.restore();
        canvas.saveLayerAlpha(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), (int) (this.enterAlpha * 255.0f), 31);
        super.dispatchDraw(canvas);
        drawDivider(canvas);
        canvas.restore();
    }

    private void drawDivider(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public void setEnterAnimationAlpha(float alpha) {
        if (this.enterAlpha != alpha) {
            this.enterAlpha = alpha;
            invalidate();
        }
    }
}
