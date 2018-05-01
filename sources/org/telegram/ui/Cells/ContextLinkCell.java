package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.PhotoViewer;

public class ContextLinkCell extends View implements FileDownloadProgressListener {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GEO = 8;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_PHOTO = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private int TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    private boolean buttonPressed;
    private int buttonState;
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private PhotoSize currentPhotoObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY = AndroidUtilities.dp(27.0f);
    private Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    private BotInlineResult inlineResult;
    private long lastUpdateTime;
    private LetterDrawable letterDrawable = new LetterDrawable();
    private ImageReceiver linkImageView = new ImageReceiver(this);
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private RadialProgress radialProgress = new RadialProgress(this);
    private float scale;
    private boolean scaled;
    private StaticLayout titleLayout;
    private int titleY = AndroidUtilities.dp(7.0f);

    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public ContextLinkCell(Context context) {
        super(context);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int i, int i2) {
        Throwable e;
        char c;
        char c2;
        StaticLayout staticLayout;
        PhotoSize closestPhotoSizeWithSize;
        String str;
        int i3;
        TLObject tLObject;
        TLObject tLObject2;
        String str2;
        int i4;
        DocumentAttribute documentAttribute;
        int[] inlineResultWidthAndHeight;
        int i5;
        String str3;
        String str4;
        String str5;
        StringBuilder stringBuilder;
        float f;
        int dp;
        int i6 = 0;
        this.drawLinkImageView = false;
        this.descriptionLayout = null;
        this.titleLayout = null;
        this.linkLayout = null;
        this.currentPhotoObject = null;
        this.linkY = AndroidUtilities.dp(27.0f);
        if (this.inlineResult == null && r1.documentAttach == null) {
            setMeasuredDimension(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
            return;
        }
        ArrayList arrayList;
        boolean z;
        int size = MeasureSpec.getSize(i);
        int dp2 = (size - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(8.0f);
        if (r1.documentAttach != null) {
            arrayList = new ArrayList();
            arrayList.add(r1.documentAttach.thumb);
        } else {
            arrayList = (r1.inlineResult == null || r1.inlineResult.photo == null) ? null : new ArrayList(r1.inlineResult.photo.sizes);
        }
        if (r1.mediaWebpage || r1.inlineResult == null) {
            z = true;
            float f2 = 4.0f;
        } else {
            if (r1.inlineResult.title != null) {
                try {
                    r1.titleLayout = new StaticLayout(TextUtils.ellipsize(Emoji.replaceEmoji(r1.inlineResult.title.replace('\n', ' '), Theme.chat_contextResult_titleTextPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), Theme.chat_contextResult_titleTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_titleTextPaint.measureText(r1.inlineResult.title)), dp2), TruncateAt.END), Theme.chat_contextResult_titleTextPaint, dp2 + AndroidUtilities.dp(4.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
                r1.letterDrawable.setTitle(r1.inlineResult.title);
            }
            if (r1.inlineResult.description != null) {
                try {
                    c = '\n';
                    c2 = ' ';
                    z = true;
                    try {
                        r1.descriptionLayout = ChatMessageCell.generateStaticLayout(Emoji.replaceEmoji(r1.inlineResult.description, Theme.chat_contextResult_descriptionTextPaint.getFontMetricsInt(), AndroidUtilities.dp(13.0f), false), Theme.chat_contextResult_descriptionTextPaint, dp2, dp2, 0, 3);
                        if (r1.descriptionLayout.getLineCount() > 0) {
                            r1.linkY = (r1.descriptionY + r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - z)) + AndroidUtilities.dp(1.0f);
                        }
                    } catch (Exception e3) {
                        e2 = e3;
                        FileLog.m3e(e2);
                        if (r1.inlineResult.url == null) {
                            try {
                                staticLayout = staticLayout;
                                try {
                                    r1.linkLayout = new StaticLayout(TextUtils.ellipsize(r1.inlineResult.url.replace(c, c2), Theme.chat_contextResult_descriptionTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_descriptionTextPaint.measureText(r1.inlineResult.url)), dp2), TruncateAt.MIDDLE), Theme.chat_contextResult_descriptionTextPaint, dp2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                } catch (Exception e4) {
                                    e2 = e4;
                                    FileLog.m3e(e2);
                                    if (r1.documentAttach != null) {
                                        r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize(), z);
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 80);
                                    } else if (MessageObject.isGifDocument(r1.documentAttach)) {
                                        r1.currentPhotoObject = r1.documentAttach.thumb;
                                    } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                                        r1.currentPhotoObject = r1.documentAttach.thumb;
                                        str = "webp";
                                        closestPhotoSizeWithSize = null;
                                        if (r1.inlineResult == null) {
                                            i3 = size;
                                            tLObject = null;
                                        } else if (r1.inlineResult.type.startsWith("gif")) {
                                            if (r1.inlineResult.type.equals("photo")) {
                                                tLObject2 = r1.inlineResult.thumb instanceof TL_webDocument ? (TL_webDocument) r1.inlineResult.thumb : (TL_webDocument) r1.inlineResult.content;
                                                tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                                if (tLObject2 != null) {
                                                }
                                                i3 = size;
                                                tLObject = tLObject2;
                                            }
                                            tLObject2 = null;
                                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                            if (tLObject2 != null) {
                                            }
                                            i3 = size;
                                            tLObject = tLObject2;
                                        } else {
                                            if (r1.documentAttachType != 2) {
                                                tLObject2 = (TL_webDocument) r1.inlineResult.content;
                                                r1.documentAttachType = 2;
                                                tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                                if (tLObject2 != null) {
                                                }
                                                i3 = size;
                                                tLObject = tLObject2;
                                            }
                                            tLObject2 = null;
                                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                            if (tLObject2 != null) {
                                            }
                                            i3 = size;
                                            tLObject = tLObject2;
                                        }
                                        str2 = null;
                                        if (r1.documentAttach != null) {
                                            i4 = 0;
                                            while (i4 < r1.documentAttach.attributes.size()) {
                                                documentAttribute = (DocumentAttribute) r1.documentAttach.attributes.get(i4);
                                                if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                                                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                                                        i4++;
                                                    }
                                                }
                                                i4 = documentAttribute.f36w;
                                                size = documentAttribute.f35h;
                                            }
                                        }
                                        i4 = 0;
                                        size = i4;
                                        if (r1.currentPhotoObject == null) {
                                            if (closestPhotoSizeWithSize != null) {
                                                closestPhotoSizeWithSize.size = -1;
                                            }
                                            i4 = r1.currentPhotoObject.f43w;
                                            size = r1.currentPhotoObject.f42h;
                                        } else if (r1.inlineResult != null) {
                                            inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                            i5 = inlineResultWidthAndHeight[0];
                                            size = inlineResultWidthAndHeight[1];
                                            i4 = i5;
                                        }
                                        i4 = AndroidUtilities.dp(80.0f);
                                        size = i4;
                                        str3 = "52_52_b";
                                        if (r1.mediaWebpage) {
                                            str4 = "52_52";
                                            str5 = str3;
                                        } else {
                                            i4 = (int) (((float) i4) / (((float) size) / ((float) AndroidUtilities.dp(80.0f))));
                                            if (r1.documentAttachType == 2) {
                                                str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(str4);
                                                stringBuilder.append("_b");
                                                str5 = stringBuilder.toString();
                                            } else {
                                                str4 = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                str5 = str4;
                                            }
                                        }
                                        r1.linkImageView.setAspectFit(r1.documentAttachType != 6);
                                        if (r1.documentAttachType == 2) {
                                            if (r1.currentPhotoObject != null) {
                                                r1.linkImageView.setImage(tLObject, str2, str4, null, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, -1, str, 1);
                                            } else {
                                                r1.linkImageView.setImage(r1.currentPhotoObject.location, str4, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, r1.currentPhotoObject.size, str, 0);
                                            }
                                        } else if (r1.documentAttach != null) {
                                            r1.linkImageView.setImage(tLObject, str2, null, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, -1, str, 1);
                                        } else {
                                            r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, r1.documentAttach.size, str, 0);
                                        }
                                        r1.drawLinkImageView = true;
                                        if (r1.mediaWebpage) {
                                            i6 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                            i6 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                            i6 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                            setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i6) + AndroidUtilities.dp(16.0f)) + r1.needDivider);
                                            i6 = AndroidUtilities.dp(52.0f);
                                            if (LocaleController.isRTL) {
                                                f = 8.0f;
                                                i4 = AndroidUtilities.dp(8.0f);
                                            } else {
                                                f = 8.0f;
                                                i4 = (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - i6;
                                            }
                                            r1.letterDrawable.setBounds(i4, AndroidUtilities.dp(f), i4 + i6, AndroidUtilities.dp(60.0f));
                                            r1.linkImageView.setImageCoords(i4, AndroidUtilities.dp(f), i6, i6);
                                            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
                                        } else {
                                            i4 = MeasureSpec.getSize(i2);
                                            if (i4 == 0) {
                                                i4 = AndroidUtilities.dp(100.0f);
                                            }
                                            size = i4;
                                            i4 = i3;
                                            setMeasuredDimension(i4, size);
                                            dp2 = (i4 - AndroidUtilities.dp(24.0f)) / 2;
                                            dp = (size - AndroidUtilities.dp(24.0f)) / 2;
                                            r1.radialProgress.setProgressRect(dp2, dp, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + dp);
                                            r1.linkImageView.setImageCoords(0, 0, i4, size);
                                        }
                                    } else {
                                        r1.currentPhotoObject = r1.documentAttach.thumb;
                                    }
                                    closestPhotoSizeWithSize = null;
                                    str = null;
                                    if (r1.inlineResult == null) {
                                        i3 = size;
                                        tLObject = null;
                                    } else if (r1.inlineResult.type.startsWith("gif")) {
                                        if (r1.inlineResult.type.equals("photo")) {
                                            if (r1.inlineResult.thumb instanceof TL_webDocument) {
                                            }
                                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                            if (tLObject2 != null) {
                                            }
                                            i3 = size;
                                            tLObject = tLObject2;
                                        }
                                        tLObject2 = null;
                                        tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                        if (tLObject2 != null) {
                                        }
                                        i3 = size;
                                        tLObject = tLObject2;
                                    } else {
                                        if (r1.documentAttachType != 2) {
                                            tLObject2 = (TL_webDocument) r1.inlineResult.content;
                                            r1.documentAttachType = 2;
                                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                            if (tLObject2 != null) {
                                            }
                                            i3 = size;
                                            tLObject = tLObject2;
                                        }
                                        tLObject2 = null;
                                        tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                        if (tLObject2 != null) {
                                        }
                                        i3 = size;
                                        tLObject = tLObject2;
                                    }
                                    str2 = null;
                                    if (r1.documentAttach != null) {
                                        i4 = 0;
                                        while (i4 < r1.documentAttach.attributes.size()) {
                                            documentAttribute = (DocumentAttribute) r1.documentAttach.attributes.get(i4);
                                            if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                                                if (documentAttribute instanceof TL_documentAttributeVideo) {
                                                    i4++;
                                                }
                                            }
                                            i4 = documentAttribute.f36w;
                                            size = documentAttribute.f35h;
                                        }
                                    }
                                    i4 = 0;
                                    size = i4;
                                    if (r1.currentPhotoObject == null) {
                                        if (closestPhotoSizeWithSize != null) {
                                            closestPhotoSizeWithSize.size = -1;
                                        }
                                        i4 = r1.currentPhotoObject.f43w;
                                        size = r1.currentPhotoObject.f42h;
                                    } else if (r1.inlineResult != null) {
                                        inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                        i5 = inlineResultWidthAndHeight[0];
                                        size = inlineResultWidthAndHeight[1];
                                        i4 = i5;
                                    }
                                    i4 = AndroidUtilities.dp(80.0f);
                                    size = i4;
                                    str3 = "52_52_b";
                                    if (r1.mediaWebpage) {
                                        i4 = (int) (((float) i4) / (((float) size) / ((float) AndroidUtilities.dp(80.0f))));
                                        if (r1.documentAttachType == 2) {
                                            str4 = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            str5 = str4;
                                        } else {
                                            str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(str4);
                                            stringBuilder.append("_b");
                                            str5 = stringBuilder.toString();
                                        }
                                    } else {
                                        str4 = "52_52";
                                        str5 = str3;
                                    }
                                    if (r1.documentAttachType != 6) {
                                    }
                                    r1.linkImageView.setAspectFit(r1.documentAttachType != 6);
                                    if (r1.documentAttachType == 2) {
                                        if (r1.documentAttach != null) {
                                            if (r1.currentPhotoObject != null) {
                                            }
                                            r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, r1.documentAttach.size, str, 0);
                                        } else {
                                            if (r1.currentPhotoObject != null) {
                                            }
                                            r1.linkImageView.setImage(tLObject, str2, null, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, -1, str, 1);
                                        }
                                    } else if (r1.currentPhotoObject != null) {
                                        if (closestPhotoSizeWithSize != null) {
                                        }
                                        r1.linkImageView.setImage(r1.currentPhotoObject.location, str4, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, r1.currentPhotoObject.size, str, 0);
                                    } else {
                                        if (closestPhotoSizeWithSize != null) {
                                        }
                                        r1.linkImageView.setImage(tLObject, str2, str4, null, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, -1, str, 1);
                                    }
                                    r1.drawLinkImageView = true;
                                    if (r1.mediaWebpage) {
                                        i4 = MeasureSpec.getSize(i2);
                                        if (i4 == 0) {
                                            i4 = AndroidUtilities.dp(100.0f);
                                        }
                                        size = i4;
                                        i4 = i3;
                                        setMeasuredDimension(i4, size);
                                        dp2 = (i4 - AndroidUtilities.dp(24.0f)) / 2;
                                        dp = (size - AndroidUtilities.dp(24.0f)) / 2;
                                        r1.radialProgress.setProgressRect(dp2, dp, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + dp);
                                        r1.linkImageView.setImageCoords(0, 0, i4, size);
                                    } else {
                                        i6 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                        i6 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                        i6 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                        setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i6) + AndroidUtilities.dp(16.0f)) + r1.needDivider);
                                        i6 = AndroidUtilities.dp(52.0f);
                                        if (LocaleController.isRTL) {
                                            f = 8.0f;
                                            i4 = (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - i6;
                                        } else {
                                            f = 8.0f;
                                            i4 = AndroidUtilities.dp(8.0f);
                                        }
                                        r1.letterDrawable.setBounds(i4, AndroidUtilities.dp(f), i4 + i6, AndroidUtilities.dp(60.0f));
                                        r1.linkImageView.setImageCoords(i4, AndroidUtilities.dp(f), i6, i6);
                                        r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
                                    }
                                }
                            } catch (Exception e5) {
                                e2 = e5;
                                FileLog.m3e(e2);
                                if (r1.documentAttach != null) {
                                    r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize(), z);
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 80);
                                } else if (MessageObject.isGifDocument(r1.documentAttach)) {
                                    r1.currentPhotoObject = r1.documentAttach.thumb;
                                } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                                    r1.currentPhotoObject = r1.documentAttach.thumb;
                                } else {
                                    r1.currentPhotoObject = r1.documentAttach.thumb;
                                    str = "webp";
                                    closestPhotoSizeWithSize = null;
                                    if (r1.inlineResult == null) {
                                        i3 = size;
                                        tLObject = null;
                                    } else if (r1.inlineResult.type.startsWith("gif")) {
                                        if (r1.documentAttachType != 2) {
                                            tLObject2 = (TL_webDocument) r1.inlineResult.content;
                                            r1.documentAttachType = 2;
                                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                            if (tLObject2 != null) {
                                            }
                                            i3 = size;
                                            tLObject = tLObject2;
                                        }
                                        tLObject2 = null;
                                        tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                        if (tLObject2 != null) {
                                        }
                                        i3 = size;
                                        tLObject = tLObject2;
                                    } else {
                                        if (r1.inlineResult.type.equals("photo")) {
                                            if (r1.inlineResult.thumb instanceof TL_webDocument) {
                                            }
                                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                            if (tLObject2 != null) {
                                            }
                                            i3 = size;
                                            tLObject = tLObject2;
                                        }
                                        tLObject2 = null;
                                        tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                        if (tLObject2 != null) {
                                        }
                                        i3 = size;
                                        tLObject = tLObject2;
                                    }
                                    str2 = null;
                                    if (r1.documentAttach != null) {
                                        i4 = 0;
                                        while (i4 < r1.documentAttach.attributes.size()) {
                                            documentAttribute = (DocumentAttribute) r1.documentAttach.attributes.get(i4);
                                            if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                                                if (documentAttribute instanceof TL_documentAttributeVideo) {
                                                    i4++;
                                                }
                                            }
                                            i4 = documentAttribute.f36w;
                                            size = documentAttribute.f35h;
                                        }
                                    }
                                    i4 = 0;
                                    size = i4;
                                    if (r1.currentPhotoObject == null) {
                                        if (closestPhotoSizeWithSize != null) {
                                            closestPhotoSizeWithSize.size = -1;
                                        }
                                        i4 = r1.currentPhotoObject.f43w;
                                        size = r1.currentPhotoObject.f42h;
                                    } else if (r1.inlineResult != null) {
                                        inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                        i5 = inlineResultWidthAndHeight[0];
                                        size = inlineResultWidthAndHeight[1];
                                        i4 = i5;
                                    }
                                    i4 = AndroidUtilities.dp(80.0f);
                                    size = i4;
                                    str3 = "52_52_b";
                                    if (r1.mediaWebpage) {
                                        str4 = "52_52";
                                        str5 = str3;
                                    } else {
                                        i4 = (int) (((float) i4) / (((float) size) / ((float) AndroidUtilities.dp(80.0f))));
                                        if (r1.documentAttachType == 2) {
                                            str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(str4);
                                            stringBuilder.append("_b");
                                            str5 = stringBuilder.toString();
                                        } else {
                                            str4 = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            str5 = str4;
                                        }
                                    }
                                    if (r1.documentAttachType != 6) {
                                    }
                                    r1.linkImageView.setAspectFit(r1.documentAttachType != 6);
                                    if (r1.documentAttachType == 2) {
                                        if (r1.currentPhotoObject != null) {
                                            if (closestPhotoSizeWithSize != null) {
                                            }
                                            r1.linkImageView.setImage(tLObject, str2, str4, null, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, -1, str, 1);
                                        } else {
                                            if (closestPhotoSizeWithSize != null) {
                                            }
                                            r1.linkImageView.setImage(r1.currentPhotoObject.location, str4, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, r1.currentPhotoObject.size, str, 0);
                                        }
                                    } else if (r1.documentAttach != null) {
                                        if (r1.currentPhotoObject != null) {
                                        }
                                        r1.linkImageView.setImage(tLObject, str2, null, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, -1, str, 1);
                                    } else {
                                        if (r1.currentPhotoObject != null) {
                                        }
                                        r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, r1.documentAttach.size, str, 0);
                                    }
                                    r1.drawLinkImageView = true;
                                    if (r1.mediaWebpage) {
                                        i6 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                        i6 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                        i6 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                        setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i6) + AndroidUtilities.dp(16.0f)) + r1.needDivider);
                                        i6 = AndroidUtilities.dp(52.0f);
                                        if (LocaleController.isRTL) {
                                            f = 8.0f;
                                            i4 = AndroidUtilities.dp(8.0f);
                                        } else {
                                            f = 8.0f;
                                            i4 = (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - i6;
                                        }
                                        r1.letterDrawable.setBounds(i4, AndroidUtilities.dp(f), i4 + i6, AndroidUtilities.dp(60.0f));
                                        r1.linkImageView.setImageCoords(i4, AndroidUtilities.dp(f), i6, i6);
                                        r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
                                    } else {
                                        i4 = MeasureSpec.getSize(i2);
                                        if (i4 == 0) {
                                            i4 = AndroidUtilities.dp(100.0f);
                                        }
                                        size = i4;
                                        i4 = i3;
                                        setMeasuredDimension(i4, size);
                                        dp2 = (i4 - AndroidUtilities.dp(24.0f)) / 2;
                                        dp = (size - AndroidUtilities.dp(24.0f)) / 2;
                                        r1.radialProgress.setProgressRect(dp2, dp, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + dp);
                                        r1.linkImageView.setImageCoords(0, 0, i4, size);
                                    }
                                }
                                closestPhotoSizeWithSize = null;
                                str = null;
                                if (r1.inlineResult == null) {
                                    i3 = size;
                                    tLObject = null;
                                } else if (r1.inlineResult.type.startsWith("gif")) {
                                    if (r1.inlineResult.type.equals("photo")) {
                                        if (r1.inlineResult.thumb instanceof TL_webDocument) {
                                        }
                                        tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                        if (tLObject2 != null) {
                                        }
                                        i3 = size;
                                        tLObject = tLObject2;
                                    }
                                    tLObject2 = null;
                                    tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                    if (tLObject2 != null) {
                                    }
                                    i3 = size;
                                    tLObject = tLObject2;
                                } else {
                                    if (r1.documentAttachType != 2) {
                                        tLObject2 = (TL_webDocument) r1.inlineResult.content;
                                        r1.documentAttachType = 2;
                                        tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                        if (tLObject2 != null) {
                                        }
                                        i3 = size;
                                        tLObject = tLObject2;
                                    }
                                    tLObject2 = null;
                                    tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                    if (tLObject2 != null) {
                                    }
                                    i3 = size;
                                    tLObject = tLObject2;
                                }
                                str2 = null;
                                if (r1.documentAttach != null) {
                                    i4 = 0;
                                    while (i4 < r1.documentAttach.attributes.size()) {
                                        documentAttribute = (DocumentAttribute) r1.documentAttach.attributes.get(i4);
                                        if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                                            if (documentAttribute instanceof TL_documentAttributeVideo) {
                                                i4++;
                                            }
                                        }
                                        i4 = documentAttribute.f36w;
                                        size = documentAttribute.f35h;
                                    }
                                }
                                i4 = 0;
                                size = i4;
                                if (r1.currentPhotoObject == null) {
                                    if (closestPhotoSizeWithSize != null) {
                                        closestPhotoSizeWithSize.size = -1;
                                    }
                                    i4 = r1.currentPhotoObject.f43w;
                                    size = r1.currentPhotoObject.f42h;
                                } else if (r1.inlineResult != null) {
                                    inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                    i5 = inlineResultWidthAndHeight[0];
                                    size = inlineResultWidthAndHeight[1];
                                    i4 = i5;
                                }
                                i4 = AndroidUtilities.dp(80.0f);
                                size = i4;
                                str3 = "52_52_b";
                                if (r1.mediaWebpage) {
                                    i4 = (int) (((float) i4) / (((float) size) / ((float) AndroidUtilities.dp(80.0f))));
                                    if (r1.documentAttachType == 2) {
                                        str4 = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                        str5 = str4;
                                    } else {
                                        str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(str4);
                                        stringBuilder.append("_b");
                                        str5 = stringBuilder.toString();
                                    }
                                } else {
                                    str4 = "52_52";
                                    str5 = str3;
                                }
                                if (r1.documentAttachType != 6) {
                                }
                                r1.linkImageView.setAspectFit(r1.documentAttachType != 6);
                                if (r1.documentAttachType == 2) {
                                    if (r1.documentAttach != null) {
                                        if (r1.currentPhotoObject != null) {
                                        }
                                        r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, r1.documentAttach.size, str, 0);
                                    } else {
                                        if (r1.currentPhotoObject != null) {
                                        }
                                        r1.linkImageView.setImage(tLObject, str2, null, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, -1, str, 1);
                                    }
                                } else if (r1.currentPhotoObject != null) {
                                    if (closestPhotoSizeWithSize != null) {
                                    }
                                    r1.linkImageView.setImage(r1.currentPhotoObject.location, str4, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, r1.currentPhotoObject.size, str, 0);
                                } else {
                                    if (closestPhotoSizeWithSize != null) {
                                    }
                                    r1.linkImageView.setImage(tLObject, str2, str4, null, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, -1, str, 1);
                                }
                                r1.drawLinkImageView = true;
                                if (r1.mediaWebpage) {
                                    i4 = MeasureSpec.getSize(i2);
                                    if (i4 == 0) {
                                        i4 = AndroidUtilities.dp(100.0f);
                                    }
                                    size = i4;
                                    i4 = i3;
                                    setMeasuredDimension(i4, size);
                                    dp2 = (i4 - AndroidUtilities.dp(24.0f)) / 2;
                                    dp = (size - AndroidUtilities.dp(24.0f)) / 2;
                                    r1.radialProgress.setProgressRect(dp2, dp, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + dp);
                                    r1.linkImageView.setImageCoords(0, 0, i4, size);
                                } else {
                                    i6 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                    i6 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                    i6 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                    setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i6) + AndroidUtilities.dp(16.0f)) + r1.needDivider);
                                    i6 = AndroidUtilities.dp(52.0f);
                                    if (LocaleController.isRTL) {
                                        f = 8.0f;
                                        i4 = (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - i6;
                                    } else {
                                        f = 8.0f;
                                        i4 = AndroidUtilities.dp(8.0f);
                                    }
                                    r1.letterDrawable.setBounds(i4, AndroidUtilities.dp(f), i4 + i6, AndroidUtilities.dp(60.0f));
                                    r1.linkImageView.setImageCoords(i4, AndroidUtilities.dp(f), i6, i6);
                                    r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
                                }
                            }
                        }
                        if (r1.documentAttach != null) {
                            r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize(), z);
                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 80);
                        } else if (MessageObject.isGifDocument(r1.documentAttach)) {
                            r1.currentPhotoObject = r1.documentAttach.thumb;
                        } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                            r1.currentPhotoObject = r1.documentAttach.thumb;
                            str = "webp";
                            closestPhotoSizeWithSize = null;
                            if (r1.inlineResult == null) {
                                i3 = size;
                                tLObject = null;
                            } else if (r1.inlineResult.type.startsWith("gif")) {
                                if (r1.documentAttachType != 2) {
                                    tLObject2 = (TL_webDocument) r1.inlineResult.content;
                                    r1.documentAttachType = 2;
                                    tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                    if (tLObject2 != null) {
                                    }
                                    i3 = size;
                                    tLObject = tLObject2;
                                }
                                tLObject2 = null;
                                tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                if (tLObject2 != null) {
                                }
                                i3 = size;
                                tLObject = tLObject2;
                            } else {
                                if (r1.inlineResult.type.equals("photo")) {
                                    if (r1.inlineResult.thumb instanceof TL_webDocument) {
                                    }
                                    tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                    if (tLObject2 != null) {
                                    }
                                    i3 = size;
                                    tLObject = tLObject2;
                                }
                                tLObject2 = null;
                                tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                if (tLObject2 != null) {
                                }
                                i3 = size;
                                tLObject = tLObject2;
                            }
                            str2 = null;
                            if (r1.documentAttach != null) {
                                i4 = 0;
                                while (i4 < r1.documentAttach.attributes.size()) {
                                    documentAttribute = (DocumentAttribute) r1.documentAttach.attributes.get(i4);
                                    if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                                        if (documentAttribute instanceof TL_documentAttributeVideo) {
                                            i4++;
                                        }
                                    }
                                    i4 = documentAttribute.f36w;
                                    size = documentAttribute.f35h;
                                }
                            }
                            i4 = 0;
                            size = i4;
                            if (r1.currentPhotoObject == null) {
                                if (closestPhotoSizeWithSize != null) {
                                    closestPhotoSizeWithSize.size = -1;
                                }
                                i4 = r1.currentPhotoObject.f43w;
                                size = r1.currentPhotoObject.f42h;
                            } else if (r1.inlineResult != null) {
                                inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                i5 = inlineResultWidthAndHeight[0];
                                size = inlineResultWidthAndHeight[1];
                                i4 = i5;
                            }
                            i4 = AndroidUtilities.dp(80.0f);
                            size = i4;
                            str3 = "52_52_b";
                            if (r1.mediaWebpage) {
                                str4 = "52_52";
                                str5 = str3;
                            } else {
                                i4 = (int) (((float) i4) / (((float) size) / ((float) AndroidUtilities.dp(80.0f))));
                                if (r1.documentAttachType == 2) {
                                    str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(str4);
                                    stringBuilder.append("_b");
                                    str5 = stringBuilder.toString();
                                } else {
                                    str4 = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                    str5 = str4;
                                }
                            }
                            if (r1.documentAttachType != 6) {
                            }
                            r1.linkImageView.setAspectFit(r1.documentAttachType != 6);
                            if (r1.documentAttachType == 2) {
                                if (r1.currentPhotoObject != null) {
                                    if (closestPhotoSizeWithSize != null) {
                                    }
                                    r1.linkImageView.setImage(tLObject, str2, str4, null, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, -1, str, 1);
                                } else {
                                    if (closestPhotoSizeWithSize != null) {
                                    }
                                    r1.linkImageView.setImage(r1.currentPhotoObject.location, str4, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, r1.currentPhotoObject.size, str, 0);
                                }
                            } else if (r1.documentAttach != null) {
                                if (r1.currentPhotoObject != null) {
                                }
                                r1.linkImageView.setImage(tLObject, str2, null, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, -1, str, 1);
                            } else {
                                if (r1.currentPhotoObject != null) {
                                }
                                r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, r1.documentAttach.size, str, 0);
                            }
                            r1.drawLinkImageView = true;
                            if (r1.mediaWebpage) {
                                i6 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                i6 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                i6 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i6) + AndroidUtilities.dp(16.0f)) + r1.needDivider);
                                i6 = AndroidUtilities.dp(52.0f);
                                if (LocaleController.isRTL) {
                                    f = 8.0f;
                                    i4 = AndroidUtilities.dp(8.0f);
                                } else {
                                    f = 8.0f;
                                    i4 = (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - i6;
                                }
                                r1.letterDrawable.setBounds(i4, AndroidUtilities.dp(f), i4 + i6, AndroidUtilities.dp(60.0f));
                                r1.linkImageView.setImageCoords(i4, AndroidUtilities.dp(f), i6, i6);
                                r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
                            } else {
                                i4 = MeasureSpec.getSize(i2);
                                if (i4 == 0) {
                                    i4 = AndroidUtilities.dp(100.0f);
                                }
                                size = i4;
                                i4 = i3;
                                setMeasuredDimension(i4, size);
                                dp2 = (i4 - AndroidUtilities.dp(24.0f)) / 2;
                                dp = (size - AndroidUtilities.dp(24.0f)) / 2;
                                r1.radialProgress.setProgressRect(dp2, dp, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + dp);
                                r1.linkImageView.setImageCoords(0, 0, i4, size);
                            }
                        } else {
                            r1.currentPhotoObject = r1.documentAttach.thumb;
                        }
                        closestPhotoSizeWithSize = null;
                        str = null;
                        if (r1.inlineResult == null) {
                            i3 = size;
                            tLObject = null;
                        } else if (r1.inlineResult.type.startsWith("gif")) {
                            if (r1.inlineResult.type.equals("photo")) {
                                if (r1.inlineResult.thumb instanceof TL_webDocument) {
                                }
                                tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                if (tLObject2 != null) {
                                }
                                i3 = size;
                                tLObject = tLObject2;
                            }
                            tLObject2 = null;
                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                            if (tLObject2 != null) {
                            }
                            i3 = size;
                            tLObject = tLObject2;
                        } else {
                            if (r1.documentAttachType != 2) {
                                tLObject2 = (TL_webDocument) r1.inlineResult.content;
                                r1.documentAttachType = 2;
                                tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                if (tLObject2 != null) {
                                }
                                i3 = size;
                                tLObject = tLObject2;
                            }
                            tLObject2 = null;
                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                            if (tLObject2 != null) {
                            }
                            i3 = size;
                            tLObject = tLObject2;
                        }
                        str2 = null;
                        if (r1.documentAttach != null) {
                            i4 = 0;
                            while (i4 < r1.documentAttach.attributes.size()) {
                                documentAttribute = (DocumentAttribute) r1.documentAttach.attributes.get(i4);
                                if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                                        i4++;
                                    }
                                }
                                i4 = documentAttribute.f36w;
                                size = documentAttribute.f35h;
                            }
                        }
                        i4 = 0;
                        size = i4;
                        if (r1.currentPhotoObject == null) {
                            if (closestPhotoSizeWithSize != null) {
                                closestPhotoSizeWithSize.size = -1;
                            }
                            i4 = r1.currentPhotoObject.f43w;
                            size = r1.currentPhotoObject.f42h;
                        } else if (r1.inlineResult != null) {
                            inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                            i5 = inlineResultWidthAndHeight[0];
                            size = inlineResultWidthAndHeight[1];
                            i4 = i5;
                        }
                        i4 = AndroidUtilities.dp(80.0f);
                        size = i4;
                        str3 = "52_52_b";
                        if (r1.mediaWebpage) {
                            i4 = (int) (((float) i4) / (((float) size) / ((float) AndroidUtilities.dp(80.0f))));
                            if (r1.documentAttachType == 2) {
                                str4 = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                str5 = str4;
                            } else {
                                str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(str4);
                                stringBuilder.append("_b");
                                str5 = stringBuilder.toString();
                            }
                        } else {
                            str4 = "52_52";
                            str5 = str3;
                        }
                        if (r1.documentAttachType != 6) {
                        }
                        r1.linkImageView.setAspectFit(r1.documentAttachType != 6);
                        if (r1.documentAttachType == 2) {
                            if (r1.documentAttach != null) {
                                if (r1.currentPhotoObject != null) {
                                }
                                r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, r1.documentAttach.size, str, 0);
                            } else {
                                if (r1.currentPhotoObject != null) {
                                }
                                r1.linkImageView.setImage(tLObject, str2, null, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, -1, str, 1);
                            }
                        } else if (r1.currentPhotoObject != null) {
                            if (closestPhotoSizeWithSize != null) {
                            }
                            r1.linkImageView.setImage(r1.currentPhotoObject.location, str4, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, r1.currentPhotoObject.size, str, 0);
                        } else {
                            if (closestPhotoSizeWithSize != null) {
                            }
                            r1.linkImageView.setImage(tLObject, str2, str4, null, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, -1, str, 1);
                        }
                        r1.drawLinkImageView = true;
                        if (r1.mediaWebpage) {
                            i4 = MeasureSpec.getSize(i2);
                            if (i4 == 0) {
                                i4 = AndroidUtilities.dp(100.0f);
                            }
                            size = i4;
                            i4 = i3;
                            setMeasuredDimension(i4, size);
                            dp2 = (i4 - AndroidUtilities.dp(24.0f)) / 2;
                            dp = (size - AndroidUtilities.dp(24.0f)) / 2;
                            r1.radialProgress.setProgressRect(dp2, dp, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + dp);
                            r1.linkImageView.setImageCoords(0, 0, i4, size);
                        } else {
                            i6 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                            i6 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                            i6 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                            setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i6) + AndroidUtilities.dp(16.0f)) + r1.needDivider);
                            i6 = AndroidUtilities.dp(52.0f);
                            if (LocaleController.isRTL) {
                                f = 8.0f;
                                i4 = (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - i6;
                            } else {
                                f = 8.0f;
                                i4 = AndroidUtilities.dp(8.0f);
                            }
                            r1.letterDrawable.setBounds(i4, AndroidUtilities.dp(f), i4 + i6, AndroidUtilities.dp(60.0f));
                            r1.linkImageView.setImageCoords(i4, AndroidUtilities.dp(f), i6, i6);
                            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
                        }
                    }
                } catch (Exception e6) {
                    e2 = e6;
                    c = '\n';
                    c2 = ' ';
                    z = true;
                    FileLog.m3e(e2);
                    if (r1.inlineResult.url == null) {
                        staticLayout = staticLayout;
                        r1.linkLayout = new StaticLayout(TextUtils.ellipsize(r1.inlineResult.url.replace(c, c2), Theme.chat_contextResult_descriptionTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_descriptionTextPaint.measureText(r1.inlineResult.url)), dp2), TruncateAt.MIDDLE), Theme.chat_contextResult_descriptionTextPaint, dp2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    }
                    if (r1.documentAttach != null) {
                        r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize(), z);
                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 80);
                    } else if (MessageObject.isGifDocument(r1.documentAttach)) {
                        r1.currentPhotoObject = r1.documentAttach.thumb;
                    } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                        r1.currentPhotoObject = r1.documentAttach.thumb;
                    } else {
                        r1.currentPhotoObject = r1.documentAttach.thumb;
                        str = "webp";
                        closestPhotoSizeWithSize = null;
                        if (r1.inlineResult == null) {
                            i3 = size;
                            tLObject = null;
                        } else if (r1.inlineResult.type.startsWith("gif")) {
                            if (r1.documentAttachType != 2) {
                                tLObject2 = (TL_webDocument) r1.inlineResult.content;
                                r1.documentAttachType = 2;
                                tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                if (tLObject2 != null) {
                                }
                                i3 = size;
                                tLObject = tLObject2;
                            }
                            tLObject2 = null;
                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                            if (tLObject2 != null) {
                            }
                            i3 = size;
                            tLObject = tLObject2;
                        } else {
                            if (r1.inlineResult.type.equals("photo")) {
                                if (r1.inlineResult.thumb instanceof TL_webDocument) {
                                }
                                tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                if (tLObject2 != null) {
                                }
                                i3 = size;
                                tLObject = tLObject2;
                            }
                            tLObject2 = null;
                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                            if (tLObject2 != null) {
                            }
                            i3 = size;
                            tLObject = tLObject2;
                        }
                        str2 = null;
                        if (r1.documentAttach != null) {
                            i4 = 0;
                            while (i4 < r1.documentAttach.attributes.size()) {
                                documentAttribute = (DocumentAttribute) r1.documentAttach.attributes.get(i4);
                                if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                                        i4++;
                                    }
                                }
                                i4 = documentAttribute.f36w;
                                size = documentAttribute.f35h;
                            }
                        }
                        i4 = 0;
                        size = i4;
                        if (r1.currentPhotoObject == null) {
                            if (closestPhotoSizeWithSize != null) {
                                closestPhotoSizeWithSize.size = -1;
                            }
                            i4 = r1.currentPhotoObject.f43w;
                            size = r1.currentPhotoObject.f42h;
                        } else if (r1.inlineResult != null) {
                            inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                            i5 = inlineResultWidthAndHeight[0];
                            size = inlineResultWidthAndHeight[1];
                            i4 = i5;
                        }
                        i4 = AndroidUtilities.dp(80.0f);
                        size = i4;
                        str3 = "52_52_b";
                        if (r1.mediaWebpage) {
                            str4 = "52_52";
                            str5 = str3;
                        } else {
                            i4 = (int) (((float) i4) / (((float) size) / ((float) AndroidUtilities.dp(80.0f))));
                            if (r1.documentAttachType == 2) {
                                str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(str4);
                                stringBuilder.append("_b");
                                str5 = stringBuilder.toString();
                            } else {
                                str4 = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                str5 = str4;
                            }
                        }
                        if (r1.documentAttachType != 6) {
                        }
                        r1.linkImageView.setAspectFit(r1.documentAttachType != 6);
                        if (r1.documentAttachType == 2) {
                            if (r1.currentPhotoObject != null) {
                                if (closestPhotoSizeWithSize != null) {
                                }
                                r1.linkImageView.setImage(tLObject, str2, str4, null, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, -1, str, 1);
                            } else {
                                if (closestPhotoSizeWithSize != null) {
                                }
                                r1.linkImageView.setImage(r1.currentPhotoObject.location, str4, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, r1.currentPhotoObject.size, str, 0);
                            }
                        } else if (r1.documentAttach != null) {
                            if (r1.currentPhotoObject != null) {
                            }
                            r1.linkImageView.setImage(tLObject, str2, null, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, -1, str, 1);
                        } else {
                            if (r1.currentPhotoObject != null) {
                            }
                            r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, r1.documentAttach.size, str, 0);
                        }
                        r1.drawLinkImageView = true;
                        if (r1.mediaWebpage) {
                            i6 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                            i6 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                            i6 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                            setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i6) + AndroidUtilities.dp(16.0f)) + r1.needDivider);
                            i6 = AndroidUtilities.dp(52.0f);
                            if (LocaleController.isRTL) {
                                f = 8.0f;
                                i4 = AndroidUtilities.dp(8.0f);
                            } else {
                                f = 8.0f;
                                i4 = (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - i6;
                            }
                            r1.letterDrawable.setBounds(i4, AndroidUtilities.dp(f), i4 + i6, AndroidUtilities.dp(60.0f));
                            r1.linkImageView.setImageCoords(i4, AndroidUtilities.dp(f), i6, i6);
                            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
                        } else {
                            i4 = MeasureSpec.getSize(i2);
                            if (i4 == 0) {
                                i4 = AndroidUtilities.dp(100.0f);
                            }
                            size = i4;
                            i4 = i3;
                            setMeasuredDimension(i4, size);
                            dp2 = (i4 - AndroidUtilities.dp(24.0f)) / 2;
                            dp = (size - AndroidUtilities.dp(24.0f)) / 2;
                            r1.radialProgress.setProgressRect(dp2, dp, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + dp);
                            r1.linkImageView.setImageCoords(0, 0, i4, size);
                        }
                    }
                    closestPhotoSizeWithSize = null;
                    str = null;
                    if (r1.inlineResult == null) {
                        i3 = size;
                        tLObject = null;
                    } else if (r1.inlineResult.type.startsWith("gif")) {
                        if (r1.inlineResult.type.equals("photo")) {
                            if (r1.inlineResult.thumb instanceof TL_webDocument) {
                            }
                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                            if (tLObject2 != null) {
                            }
                            i3 = size;
                            tLObject = tLObject2;
                        }
                        tLObject2 = null;
                        tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                        if (tLObject2 != null) {
                        }
                        i3 = size;
                        tLObject = tLObject2;
                    } else {
                        if (r1.documentAttachType != 2) {
                            tLObject2 = (TL_webDocument) r1.inlineResult.content;
                            r1.documentAttachType = 2;
                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                            if (tLObject2 != null) {
                            }
                            i3 = size;
                            tLObject = tLObject2;
                        }
                        tLObject2 = null;
                        tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                        if (tLObject2 != null) {
                        }
                        i3 = size;
                        tLObject = tLObject2;
                    }
                    str2 = null;
                    if (r1.documentAttach != null) {
                        i4 = 0;
                        while (i4 < r1.documentAttach.attributes.size()) {
                            documentAttribute = (DocumentAttribute) r1.documentAttach.attributes.get(i4);
                            if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                                if (documentAttribute instanceof TL_documentAttributeVideo) {
                                    i4++;
                                }
                            }
                            i4 = documentAttribute.f36w;
                            size = documentAttribute.f35h;
                        }
                    }
                    i4 = 0;
                    size = i4;
                    if (r1.currentPhotoObject == null) {
                        if (closestPhotoSizeWithSize != null) {
                            closestPhotoSizeWithSize.size = -1;
                        }
                        i4 = r1.currentPhotoObject.f43w;
                        size = r1.currentPhotoObject.f42h;
                    } else if (r1.inlineResult != null) {
                        inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                        i5 = inlineResultWidthAndHeight[0];
                        size = inlineResultWidthAndHeight[1];
                        i4 = i5;
                    }
                    i4 = AndroidUtilities.dp(80.0f);
                    size = i4;
                    str3 = "52_52_b";
                    if (r1.mediaWebpage) {
                        i4 = (int) (((float) i4) / (((float) size) / ((float) AndroidUtilities.dp(80.0f))));
                        if (r1.documentAttachType == 2) {
                            str4 = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                            str5 = str4;
                        } else {
                            str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str4);
                            stringBuilder.append("_b");
                            str5 = stringBuilder.toString();
                        }
                    } else {
                        str4 = "52_52";
                        str5 = str3;
                    }
                    if (r1.documentAttachType != 6) {
                    }
                    r1.linkImageView.setAspectFit(r1.documentAttachType != 6);
                    if (r1.documentAttachType == 2) {
                        if (r1.documentAttach != null) {
                            if (r1.currentPhotoObject != null) {
                            }
                            r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, r1.documentAttach.size, str, 0);
                        } else {
                            if (r1.currentPhotoObject != null) {
                            }
                            r1.linkImageView.setImage(tLObject, str2, null, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, -1, str, 1);
                        }
                    } else if (r1.currentPhotoObject != null) {
                        if (closestPhotoSizeWithSize != null) {
                        }
                        r1.linkImageView.setImage(r1.currentPhotoObject.location, str4, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, r1.currentPhotoObject.size, str, 0);
                    } else {
                        if (closestPhotoSizeWithSize != null) {
                        }
                        r1.linkImageView.setImage(tLObject, str2, str4, null, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, -1, str, 1);
                    }
                    r1.drawLinkImageView = true;
                    if (r1.mediaWebpage) {
                        i4 = MeasureSpec.getSize(i2);
                        if (i4 == 0) {
                            i4 = AndroidUtilities.dp(100.0f);
                        }
                        size = i4;
                        i4 = i3;
                        setMeasuredDimension(i4, size);
                        dp2 = (i4 - AndroidUtilities.dp(24.0f)) / 2;
                        dp = (size - AndroidUtilities.dp(24.0f)) / 2;
                        r1.radialProgress.setProgressRect(dp2, dp, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + dp);
                        r1.linkImageView.setImageCoords(0, 0, i4, size);
                    } else {
                        i6 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                        i6 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                        i6 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                        setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i6) + AndroidUtilities.dp(16.0f)) + r1.needDivider);
                        i6 = AndroidUtilities.dp(52.0f);
                        if (LocaleController.isRTL) {
                            f = 8.0f;
                            i4 = (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - i6;
                        } else {
                            f = 8.0f;
                            i4 = AndroidUtilities.dp(8.0f);
                        }
                        r1.letterDrawable.setBounds(i4, AndroidUtilities.dp(f), i4 + i6, AndroidUtilities.dp(60.0f));
                        r1.linkImageView.setImageCoords(i4, AndroidUtilities.dp(f), i6, i6);
                        r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
                    }
                }
            }
            c = '\n';
            c2 = ' ';
            z = true;
            if (r1.inlineResult.url == null) {
                staticLayout = staticLayout;
                r1.linkLayout = new StaticLayout(TextUtils.ellipsize(r1.inlineResult.url.replace(c, c2), Theme.chat_contextResult_descriptionTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_descriptionTextPaint.measureText(r1.inlineResult.url)), dp2), TruncateAt.MIDDLE), Theme.chat_contextResult_descriptionTextPaint, dp2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }
        if (r1.documentAttach != null) {
            if (MessageObject.isGifDocument(r1.documentAttach)) {
                r1.currentPhotoObject = r1.documentAttach.thumb;
            } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                r1.currentPhotoObject = r1.documentAttach.thumb;
                str = "webp";
                closestPhotoSizeWithSize = null;
                if (r1.inlineResult == null) {
                    if ((r1.inlineResult.content instanceof TL_webDocument) && r1.inlineResult.type != null) {
                        if (r1.inlineResult.type.startsWith("gif")) {
                            if (r1.documentAttachType != 2) {
                                tLObject2 = (TL_webDocument) r1.inlineResult.content;
                                r1.documentAttachType = 2;
                                if (tLObject2 == null && (r1.inlineResult.thumb instanceof TL_webDocument)) {
                                    tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                                }
                                if (tLObject2 != null && r1.currentPhotoObject == null && closestPhotoSizeWithSize == null && ((r1.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue) || (r1.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo))) {
                                    double d = r1.inlineResult.send_message.geo.lat;
                                    double d2 = r1.inlineResult.send_message.geo._long;
                                    r10 = new Object[5];
                                    i3 = size;
                                    r10[2] = Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                                    r10[3] = Double.valueOf(d);
                                    r10[4] = Double.valueOf(d2);
                                    str2 = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", r10);
                                    tLObject = tLObject2;
                                    if (r1.documentAttach != null) {
                                        i4 = 0;
                                        while (i4 < r1.documentAttach.attributes.size()) {
                                            documentAttribute = (DocumentAttribute) r1.documentAttach.attributes.get(i4);
                                            if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                                                if (documentAttribute instanceof TL_documentAttributeVideo) {
                                                    i4++;
                                                }
                                            }
                                            i4 = documentAttribute.f36w;
                                            size = documentAttribute.f35h;
                                        }
                                    }
                                    i4 = 0;
                                    size = i4;
                                    if (i4 == 0 || r4 == 0) {
                                        if (r1.currentPhotoObject == null) {
                                            if (closestPhotoSizeWithSize != null) {
                                                closestPhotoSizeWithSize.size = -1;
                                            }
                                            i4 = r1.currentPhotoObject.f43w;
                                            size = r1.currentPhotoObject.f42h;
                                        } else if (r1.inlineResult != null) {
                                            inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                            i5 = inlineResultWidthAndHeight[0];
                                            size = inlineResultWidthAndHeight[1];
                                            i4 = i5;
                                        }
                                    }
                                    if (i4 == 0 || r4 == 0) {
                                        i4 = AndroidUtilities.dp(80.0f);
                                        size = i4;
                                    }
                                    if (!(r1.documentAttach == null && r1.currentPhotoObject == null && tLObject == null && str2 == null)) {
                                        str3 = "52_52_b";
                                        if (r1.mediaWebpage) {
                                            i4 = (int) (((float) i4) / (((float) size) / ((float) AndroidUtilities.dp(80.0f))));
                                            if (r1.documentAttachType == 2) {
                                                str4 = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                str5 = str4;
                                            } else {
                                                str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(str4);
                                                stringBuilder.append("_b");
                                                str5 = stringBuilder.toString();
                                            }
                                        } else {
                                            str4 = "52_52";
                                            str5 = str3;
                                        }
                                        if (r1.documentAttachType != 6) {
                                        }
                                        r1.linkImageView.setAspectFit(r1.documentAttachType != 6);
                                        if (r1.documentAttachType == 2) {
                                            if (r1.documentAttach != null) {
                                                if (r1.currentPhotoObject != null) {
                                                }
                                                r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, r1.documentAttach.size, str, 0);
                                            } else {
                                                if (r1.currentPhotoObject != null) {
                                                }
                                                r1.linkImageView.setImage(tLObject, str2, null, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, -1, str, 1);
                                            }
                                        } else if (r1.currentPhotoObject != null) {
                                            if (closestPhotoSizeWithSize != null) {
                                            }
                                            r1.linkImageView.setImage(r1.currentPhotoObject.location, str4, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, r1.currentPhotoObject.size, str, 0);
                                        } else {
                                            if (closestPhotoSizeWithSize != null) {
                                            }
                                            r1.linkImageView.setImage(tLObject, str2, str4, null, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, -1, str, 1);
                                        }
                                        r1.drawLinkImageView = true;
                                    }
                                    if (r1.mediaWebpage) {
                                        i4 = MeasureSpec.getSize(i2);
                                        if (i4 == 0) {
                                            i4 = AndroidUtilities.dp(100.0f);
                                        }
                                        size = i4;
                                        i4 = i3;
                                        setMeasuredDimension(i4, size);
                                        dp2 = (i4 - AndroidUtilities.dp(24.0f)) / 2;
                                        dp = (size - AndroidUtilities.dp(24.0f)) / 2;
                                        r1.radialProgress.setProgressRect(dp2, dp, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + dp);
                                        r1.linkImageView.setImageCoords(0, 0, i4, size);
                                    } else {
                                        if (!(r1.titleLayout == null || r1.titleLayout.getLineCount() == 0)) {
                                            i6 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                        }
                                        if (!(r1.descriptionLayout == null || r1.descriptionLayout.getLineCount() == 0)) {
                                            i6 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                        }
                                        if (r1.linkLayout != null && r1.linkLayout.getLineCount() > 0) {
                                            i6 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                        }
                                        setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i6) + AndroidUtilities.dp(16.0f)) + r1.needDivider);
                                        i6 = AndroidUtilities.dp(52.0f);
                                        if (LocaleController.isRTL) {
                                            f = 8.0f;
                                            i4 = (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - i6;
                                        } else {
                                            f = 8.0f;
                                            i4 = AndroidUtilities.dp(8.0f);
                                        }
                                        r1.letterDrawable.setBounds(i4, AndroidUtilities.dp(f), i4 + i6, AndroidUtilities.dp(60.0f));
                                        r1.linkImageView.setImageCoords(i4, AndroidUtilities.dp(f), i6, i6);
                                        if (r1.documentAttachType == 3 || r1.documentAttachType == 5) {
                                            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
                                        }
                                    }
                                }
                                i3 = size;
                                tLObject = tLObject2;
                            }
                        } else if (r1.inlineResult.type.equals("photo")) {
                            if (r1.inlineResult.thumb instanceof TL_webDocument) {
                            }
                            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                            if (tLObject2 != null) {
                            }
                            i3 = size;
                            tLObject = tLObject2;
                        }
                    }
                    tLObject2 = null;
                    tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                    if (tLObject2 != null) {
                    }
                    i3 = size;
                    tLObject = tLObject2;
                } else {
                    i3 = size;
                    tLObject = null;
                }
                str2 = null;
                if (r1.documentAttach != null) {
                    i4 = 0;
                    while (i4 < r1.documentAttach.attributes.size()) {
                        documentAttribute = (DocumentAttribute) r1.documentAttach.attributes.get(i4);
                        if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                            if (documentAttribute instanceof TL_documentAttributeVideo) {
                                i4++;
                            }
                        }
                        i4 = documentAttribute.f36w;
                        size = documentAttribute.f35h;
                    }
                }
                i4 = 0;
                size = i4;
                if (r1.currentPhotoObject == null) {
                    if (closestPhotoSizeWithSize != null) {
                        closestPhotoSizeWithSize.size = -1;
                    }
                    i4 = r1.currentPhotoObject.f43w;
                    size = r1.currentPhotoObject.f42h;
                } else if (r1.inlineResult != null) {
                    inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                    i5 = inlineResultWidthAndHeight[0];
                    size = inlineResultWidthAndHeight[1];
                    i4 = i5;
                }
                i4 = AndroidUtilities.dp(80.0f);
                size = i4;
                str3 = "52_52_b";
                if (r1.mediaWebpage) {
                    str4 = "52_52";
                    str5 = str3;
                } else {
                    i4 = (int) (((float) i4) / (((float) size) / ((float) AndroidUtilities.dp(80.0f))));
                    if (r1.documentAttachType == 2) {
                        str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str4);
                        stringBuilder.append("_b");
                        str5 = stringBuilder.toString();
                    } else {
                        str4 = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                        str5 = str4;
                    }
                }
                if (r1.documentAttachType != 6) {
                }
                r1.linkImageView.setAspectFit(r1.documentAttachType != 6);
                if (r1.documentAttachType == 2) {
                    if (r1.currentPhotoObject != null) {
                        if (closestPhotoSizeWithSize != null) {
                        }
                        r1.linkImageView.setImage(tLObject, str2, str4, null, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, -1, str, 1);
                    } else {
                        if (closestPhotoSizeWithSize != null) {
                        }
                        r1.linkImageView.setImage(r1.currentPhotoObject.location, str4, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, r1.currentPhotoObject.size, str, 0);
                    }
                } else if (r1.documentAttach != null) {
                    if (r1.currentPhotoObject != null) {
                    }
                    r1.linkImageView.setImage(tLObject, str2, null, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, -1, str, 1);
                } else {
                    if (r1.currentPhotoObject != null) {
                    }
                    r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, r1.documentAttach.size, str, 0);
                }
                r1.drawLinkImageView = true;
                if (r1.mediaWebpage) {
                    i6 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                    i6 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                    i6 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                    setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i6) + AndroidUtilities.dp(16.0f)) + r1.needDivider);
                    i6 = AndroidUtilities.dp(52.0f);
                    if (LocaleController.isRTL) {
                        f = 8.0f;
                        i4 = AndroidUtilities.dp(8.0f);
                    } else {
                        f = 8.0f;
                        i4 = (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - i6;
                    }
                    r1.letterDrawable.setBounds(i4, AndroidUtilities.dp(f), i4 + i6, AndroidUtilities.dp(60.0f));
                    r1.linkImageView.setImageCoords(i4, AndroidUtilities.dp(f), i6, i6);
                    r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
                } else {
                    i4 = MeasureSpec.getSize(i2);
                    if (i4 == 0) {
                        i4 = AndroidUtilities.dp(100.0f);
                    }
                    size = i4;
                    i4 = i3;
                    setMeasuredDimension(i4, size);
                    dp2 = (i4 - AndroidUtilities.dp(24.0f)) / 2;
                    dp = (size - AndroidUtilities.dp(24.0f)) / 2;
                    r1.radialProgress.setProgressRect(dp2, dp, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + dp);
                    r1.linkImageView.setImageCoords(0, 0, i4, size);
                }
            } else if (!(r1.documentAttachType == 5 || r1.documentAttachType == 3)) {
                r1.currentPhotoObject = r1.documentAttach.thumb;
            }
        } else if (!(r1.inlineResult == null || r1.inlineResult.photo == null)) {
            r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize(), z);
            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 80);
        }
        closestPhotoSizeWithSize = null;
        str = null;
        if (r1.inlineResult == null) {
            i3 = size;
            tLObject = null;
        } else if (r1.inlineResult.type.startsWith("gif")) {
            if (r1.inlineResult.type.equals("photo")) {
                if (r1.inlineResult.thumb instanceof TL_webDocument) {
                }
                tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                if (tLObject2 != null) {
                }
                i3 = size;
                tLObject = tLObject2;
            }
            tLObject2 = null;
            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
            if (tLObject2 != null) {
            }
            i3 = size;
            tLObject = tLObject2;
        } else {
            if (r1.documentAttachType != 2) {
                tLObject2 = (TL_webDocument) r1.inlineResult.content;
                r1.documentAttachType = 2;
                tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
                if (tLObject2 != null) {
                }
                i3 = size;
                tLObject = tLObject2;
            }
            tLObject2 = null;
            tLObject2 = (TL_webDocument) r1.inlineResult.thumb;
            if (tLObject2 != null) {
            }
            i3 = size;
            tLObject = tLObject2;
        }
        str2 = null;
        if (r1.documentAttach != null) {
            i4 = 0;
            while (i4 < r1.documentAttach.attributes.size()) {
                documentAttribute = (DocumentAttribute) r1.documentAttach.attributes.get(i4);
                if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                        i4++;
                    }
                }
                i4 = documentAttribute.f36w;
                size = documentAttribute.f35h;
            }
        }
        i4 = 0;
        size = i4;
        if (r1.currentPhotoObject == null) {
            if (closestPhotoSizeWithSize != null) {
                closestPhotoSizeWithSize.size = -1;
            }
            i4 = r1.currentPhotoObject.f43w;
            size = r1.currentPhotoObject.f42h;
        } else if (r1.inlineResult != null) {
            inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
            i5 = inlineResultWidthAndHeight[0];
            size = inlineResultWidthAndHeight[1];
            i4 = i5;
        }
        i4 = AndroidUtilities.dp(80.0f);
        size = i4;
        str3 = "52_52_b";
        if (r1.mediaWebpage) {
            i4 = (int) (((float) i4) / (((float) size) / ((float) AndroidUtilities.dp(80.0f))));
            if (r1.documentAttachType == 2) {
                str4 = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                str5 = str4;
            } else {
                str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) i4) / AndroidUtilities.density)), Integer.valueOf(80)});
                stringBuilder = new StringBuilder();
                stringBuilder.append(str4);
                stringBuilder.append("_b");
                str5 = stringBuilder.toString();
            }
        } else {
            str4 = "52_52";
            str5 = str3;
        }
        if (r1.documentAttachType != 6) {
        }
        r1.linkImageView.setAspectFit(r1.documentAttachType != 6);
        if (r1.documentAttachType == 2) {
            if (r1.documentAttach != null) {
                if (r1.currentPhotoObject != null) {
                }
                r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, r1.documentAttach.size, str, 0);
            } else {
                if (r1.currentPhotoObject != null) {
                }
                r1.linkImageView.setImage(tLObject, str2, null, null, r1.currentPhotoObject != null ? null : r1.currentPhotoObject.location, str4, -1, str, 1);
            }
        } else if (r1.currentPhotoObject != null) {
            if (closestPhotoSizeWithSize != null) {
            }
            r1.linkImageView.setImage(r1.currentPhotoObject.location, str4, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, r1.currentPhotoObject.size, str, 0);
        } else {
            if (closestPhotoSizeWithSize != null) {
            }
            r1.linkImageView.setImage(tLObject, str2, str4, null, closestPhotoSizeWithSize != null ? null : closestPhotoSizeWithSize.location, str5, -1, str, 1);
        }
        r1.drawLinkImageView = true;
        if (r1.mediaWebpage) {
            i4 = MeasureSpec.getSize(i2);
            if (i4 == 0) {
                i4 = AndroidUtilities.dp(100.0f);
            }
            size = i4;
            i4 = i3;
            setMeasuredDimension(i4, size);
            dp2 = (i4 - AndroidUtilities.dp(24.0f)) / 2;
            dp = (size - AndroidUtilities.dp(24.0f)) / 2;
            r1.radialProgress.setProgressRect(dp2, dp, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + dp);
            r1.linkImageView.setImageCoords(0, 0, i4, size);
        } else {
            i6 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
            i6 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
            i6 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
            setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i6) + AndroidUtilities.dp(16.0f)) + r1.needDivider);
            i6 = AndroidUtilities.dp(52.0f);
            if (LocaleController.isRTL) {
                f = 8.0f;
                i4 = (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - i6;
            } else {
                f = 8.0f;
                i4 = AndroidUtilities.dp(8.0f);
            }
            r1.letterDrawable.setBounds(i4, AndroidUtilities.dp(f), i4 + i6, AndroidUtilities.dp(60.0f));
            r1.linkImageView.setImageCoords(i4, AndroidUtilities.dp(f), i6, i6);
            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
        }
    }

    private void setAttachType() {
        this.currentMessageObject = null;
        this.documentAttachType = 0;
        if (this.documentAttach != null) {
            if (MessageObject.isGifDocument(this.documentAttach)) {
                this.documentAttachType = 2;
            } else if (MessageObject.isStickerDocument(this.documentAttach)) {
                this.documentAttachType = 6;
            } else if (MessageObject.isMusicDocument(this.documentAttach)) {
                this.documentAttachType = 5;
            } else if (MessageObject.isVoiceDocument(this.documentAttach)) {
                this.documentAttachType = 3;
            }
        } else if (this.inlineResult != null) {
            if (this.inlineResult.photo != null) {
                this.documentAttachType = 7;
            } else if (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                this.documentAttachType = 5;
            } else if (this.inlineResult.type.equals("voice")) {
                this.documentAttachType = 3;
            }
        }
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            Message tL_message = new TL_message();
            tL_message.out = true;
            tL_message.id = -Utilities.random.nextInt();
            tL_message.to_id = new TL_peerUser();
            Peer peer = tL_message.to_id;
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            tL_message.from_id = clientUserId;
            peer.user_id = clientUserId;
            tL_message.date = (int) (System.currentTimeMillis() / 1000);
            tL_message.message = TtmlNode.ANONYMOUS_REGION_ID;
            tL_message.media = new TL_messageMediaDocument();
            MessageMedia messageMedia = tL_message.media;
            messageMedia.flags |= 3;
            tL_message.media.document = new TL_document();
            tL_message.flags |= 768;
            if (this.documentAttach != null) {
                tL_message.media.document = this.documentAttach;
                tL_message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                String httpUrlExtension = ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg");
                tL_message.media.document.id = 0;
                tL_message.media.document.access_hash = 0;
                tL_message.media.document.date = tL_message.date;
                Document document = tL_message.media.document;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("audio/");
                stringBuilder.append(httpUrlExtension);
                document.mime_type = stringBuilder.toString();
                tL_message.media.document.size = 0;
                tL_message.media.document.thumb = new TL_photoSizeEmpty();
                tL_message.media.document.thumb.type = "s";
                tL_message.media.document.dc_id = 0;
                TL_documentAttributeAudio tL_documentAttributeAudio = new TL_documentAttributeAudio();
                tL_documentAttributeAudio.duration = MessageObject.getInlineResultDuration(this.inlineResult);
                tL_documentAttributeAudio.title = this.inlineResult.title != null ? this.inlineResult.title : TtmlNode.ANONYMOUS_REGION_ID;
                tL_documentAttributeAudio.performer = this.inlineResult.description != null ? this.inlineResult.description : TtmlNode.ANONYMOUS_REGION_ID;
                tL_documentAttributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    tL_documentAttributeAudio.voice = true;
                }
                tL_message.media.document.attributes.add(tL_documentAttributeAudio);
                TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(Utilities.MD5(this.inlineResult.content.url));
                stringBuilder2.append(".");
                stringBuilder2.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg"));
                tL_documentAttributeFilename.file_name = stringBuilder2.toString();
                tL_message.media.document.attributes.add(tL_documentAttributeFilename);
                File directory = FileLoader.getDirectory(4);
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(Utilities.MD5(this.inlineResult.content.url));
                stringBuilder3.append(".");
                stringBuilder3.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg"));
                tL_message.attachPath = new File(directory, stringBuilder3.toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(this.currentAccount, tL_message, false);
        }
    }

    public void setLink(BotInlineResult botInlineResult, boolean z, boolean z2, boolean z3) {
        this.needDivider = z2;
        this.needShadow = z3;
        this.inlineResult = botInlineResult;
        if (this.inlineResult == null || this.inlineResult.document == null) {
            this.documentAttach = null;
        } else {
            this.documentAttach = this.inlineResult.document;
        }
        this.mediaWebpage = z;
        setAttachType();
        requestLayout();
        updateButtonState(null);
    }

    public void setGif(Document document, boolean z) {
        this.needDivider = z;
        this.needShadow = false;
        this.inlineResult = null;
        this.documentAttach = document;
        this.mediaWebpage = true;
        setAttachType();
        requestLayout();
        updateButtonState(false);
    }

    public boolean isSticker() {
        return this.documentAttachType == 6;
    }

    public boolean showingBitmap() {
        return this.linkImageView.getBitmap() != null;
    }

    public Document getDocument() {
        return this.documentAttach;
    }

    public ImageReceiver getPhotoImage() {
        return this.linkImageView;
    }

    public void setScaled(boolean z) {
        this.scaled = z;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView && this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false);
        }
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!(this.mediaWebpage || this.delegate == null)) {
            if (this.inlineResult != null) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                AndroidUtilities.dp(48.0f);
                boolean z = true;
                if (this.documentAttachType != 3) {
                    if (this.documentAttachType != 5) {
                        if (!(this.inlineResult == null || this.inlineResult.content == null || TextUtils.isEmpty(this.inlineResult.content.url))) {
                            if (motionEvent.getAction() == 0) {
                                if (this.letterDrawable.getBounds().contains(x, y)) {
                                    this.buttonPressed = true;
                                    if (!z) {
                                        z = super.onTouchEvent(motionEvent);
                                    }
                                    return z;
                                }
                            } else if (this.buttonPressed) {
                                if (motionEvent.getAction() == 1) {
                                    this.buttonPressed = false;
                                    playSoundEffect(0);
                                    this.delegate.didPressedImage(this);
                                } else if (motionEvent.getAction() == 3) {
                                    this.buttonPressed = false;
                                } else if (motionEvent.getAction() == 2 && !this.letterDrawable.getBounds().contains(x, y)) {
                                    this.buttonPressed = false;
                                }
                            }
                        }
                        z = false;
                        if (z) {
                            z = super.onTouchEvent(motionEvent);
                        }
                        return z;
                    }
                }
                boolean contains = this.letterDrawable.getBounds().contains(x, y);
                if (motionEvent.getAction() == 0) {
                    if (contains) {
                        this.buttonPressed = true;
                        invalidate();
                        this.radialProgress.swapBackground(getDrawableForCurrentState());
                        if (z) {
                            z = super.onTouchEvent(motionEvent);
                        }
                        return z;
                    }
                } else if (this.buttonPressed) {
                    if (motionEvent.getAction() == 1) {
                        this.buttonPressed = false;
                        playSoundEffect(0);
                        didPressedButton();
                        invalidate();
                    } else if (motionEvent.getAction() == 3) {
                        this.buttonPressed = false;
                        invalidate();
                    } else if (motionEvent.getAction() == 2 && !contains) {
                        this.buttonPressed = false;
                        invalidate();
                    }
                    this.radialProgress.swapBackground(getDrawableForCurrentState());
                }
                z = false;
                if (z) {
                    z = super.onTouchEvent(motionEvent);
                }
                return z;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    private void didPressedButton() {
        if (this.documentAttachType != 3 && this.documentAttachType != 5) {
            return;
        }
        if (this.buttonState == 0) {
            if (MediaController.getInstance().playMessage(this.currentMessageObject)) {
                this.buttonState = 1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == 1) {
            if (MediaController.getInstance().pauseMessage(this.currentMessageObject)) {
                this.buttonState = 0;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == 2) {
            this.radialProgress.setProgress(0.0f, false);
            if (this.documentAttach != null) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
            } else if (this.inlineResult.content instanceof TL_webDocument) {
                FileLoader.getInstance(this.currentAccount).loadFile((TL_webDocument) this.inlineResult.content, true, 1);
            }
            this.buttonState = 4;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
            invalidate();
        } else if (this.buttonState == 4) {
            if (this.documentAttach != null) {
                FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            } else if (this.inlineResult.content instanceof TL_webDocument) {
                FileLoader.getInstance(this.currentAccount).cancelLoadFile((TL_webDocument) this.inlineResult.content);
            }
            this.buttonState = 2;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
            invalidate();
        }
    }

    protected void onDraw(Canvas canvas) {
        float f = 8.0f;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.linkLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.linkY);
            this.linkLayout.draw(canvas);
            canvas.restore();
        }
        int intrinsicWidth;
        int intrinsicHeight;
        int imageX;
        if (!this.mediaWebpage) {
            if (this.documentAttachType != 3) {
                if (this.documentAttachType != 5) {
                    int imageY;
                    if (this.inlineResult != null && this.inlineResult.type.equals("file")) {
                        intrinsicWidth = Theme.chat_inlineResultFile.getIntrinsicWidth();
                        intrinsicHeight = Theme.chat_inlineResultFile.getIntrinsicHeight();
                        imageX = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth) / 2);
                        imageY = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - intrinsicHeight) / 2);
                        canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                        Theme.chat_inlineResultFile.setBounds(imageX, imageY, intrinsicWidth + imageX, intrinsicHeight + imageY);
                        Theme.chat_inlineResultFile.draw(canvas);
                    } else if (this.inlineResult != null && (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO) || this.inlineResult.type.equals("voice"))) {
                        intrinsicWidth = Theme.chat_inlineResultAudio.getIntrinsicWidth();
                        intrinsicHeight = Theme.chat_inlineResultAudio.getIntrinsicHeight();
                        imageX = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth) / 2);
                        imageY = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - intrinsicHeight) / 2);
                        canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                        Theme.chat_inlineResultAudio.setBounds(imageX, imageY, intrinsicWidth + imageX, intrinsicHeight + imageY);
                        Theme.chat_inlineResultAudio.draw(canvas);
                    } else if (this.inlineResult == null || !(this.inlineResult.type.equals("venue") || this.inlineResult.type.equals("geo"))) {
                        this.letterDrawable.draw(canvas);
                    } else {
                        intrinsicWidth = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                        intrinsicHeight = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                        imageX = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth) / 2);
                        imageY = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - intrinsicHeight) / 2);
                        canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                        Theme.chat_inlineResultLocation.setBounds(imageX, imageY, intrinsicWidth + imageX, intrinsicHeight + imageY);
                        Theme.chat_inlineResultLocation.draw(canvas);
                    }
                }
            }
            this.radialProgress.setProgressColor(Theme.getColor(this.buttonPressed ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
            this.radialProgress.draw(canvas);
        } else if (this.inlineResult != null && ((this.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo) || (this.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue))) {
            intrinsicWidth = Theme.chat_inlineResultLocation.getIntrinsicWidth();
            int intrinsicHeight2 = Theme.chat_inlineResultLocation.getIntrinsicHeight();
            intrinsicHeight = this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - intrinsicWidth) / 2);
            imageX = this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - intrinsicHeight2) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + this.linkImageView.getImageWidth()), (float) (this.linkImageView.getImageY() + this.linkImageView.getImageHeight()), LetterDrawable.paint);
            Theme.chat_inlineResultLocation.setBounds(intrinsicHeight, imageX, intrinsicWidth + intrinsicHeight, intrinsicHeight2 + imageX);
            Theme.chat_inlineResultLocation.draw(canvas);
        }
        if (this.drawLinkImageView) {
            if (this.inlineResult != null) {
                this.linkImageView.setVisible(PhotoViewer.isShowingImage(this.inlineResult) ^ 1, false);
            }
            canvas.save();
            if ((this.scaled && this.scale != 0.8f) || !(this.scaled || this.scale == 1.0f)) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                this.lastUpdateTime = currentTimeMillis;
                if (!this.scaled || this.scale == 0.8f) {
                    this.scale += ((float) j) / 400.0f;
                    if (this.scale > 1.0f) {
                        this.scale = 1.0f;
                    }
                } else {
                    this.scale -= ((float) j) / 400.0f;
                    if (this.scale < 0.8f) {
                        this.scale = 0.8f;
                    }
                }
                invalidate();
            }
            canvas.scale(this.scale, this.scale, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
            this.linkImageView.draw(canvas);
            canvas.restore();
        }
        if (this.mediaWebpage && (this.documentAttachType == 7 || this.documentAttachType == 2)) {
            this.radialProgress.draw(canvas);
        }
        if (this.needDivider && !this.mediaWebpage) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            } else {
                canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
        if (this.needShadow) {
            Theme.chat_contextResult_shadowUnderSwitchDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(3.0f));
            Theme.chat_contextResult_shadowUnderSwitchDrawable.draw(canvas);
        }
    }

    private Drawable getDrawableForCurrentState() {
        Drawable drawable = null;
        if (this.documentAttachType != 3) {
            if (this.documentAttachType != 5) {
                if (this.buttonState == 1) {
                    drawable = Theme.chat_photoStatesDrawables[5][0];
                }
                return drawable;
            }
        }
        if (this.buttonState == -1) {
            return null;
        }
        this.radialProgress.setAlphaForPrevious(false);
        return Theme.chat_fileStatesDrawable[this.buttonState + 5][this.buttonPressed];
    }

    public void updateButtonState(boolean z) {
        String attachFileName;
        File pathToAttach;
        StringBuilder stringBuilder;
        float f;
        Float fileProgress;
        boolean isLoadingFile;
        boolean isPlayingMessage;
        if (this.documentAttachType != 5) {
            if (this.documentAttachType != 3) {
                if (this.mediaWebpage) {
                    if (this.inlineResult != null) {
                        if (this.inlineResult.document instanceof TL_document) {
                            attachFileName = FileLoader.getAttachFileName(this.inlineResult.document);
                            pathToAttach = FileLoader.getPathToAttach(this.inlineResult.document);
                        } else if (this.inlineResult.photo instanceof TL_photo) {
                            this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
                            attachFileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                            pathToAttach = FileLoader.getPathToAttach(this.currentPhotoObject);
                        } else if (this.inlineResult.content instanceof TL_webDocument) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(Utilities.MD5(this.inlineResult.content.url));
                            stringBuilder.append(".");
                            stringBuilder.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, "jpg"));
                            attachFileName = stringBuilder.toString();
                            pathToAttach = new File(FileLoader.getDirectory(4), attachFileName);
                        } else if (this.inlineResult.thumb instanceof TL_webDocument) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(Utilities.MD5(this.inlineResult.thumb.url));
                            stringBuilder.append(".");
                            stringBuilder.append(ImageLoader.getHttpUrlExtension(this.inlineResult.thumb.url, "jpg"));
                            attachFileName = stringBuilder.toString();
                            pathToAttach = new File(FileLoader.getDirectory(4), attachFileName);
                        }
                        if (TextUtils.isEmpty(attachFileName)) {
                            if (pathToAttach.exists()) {
                                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(attachFileName, this);
                                f = 0.0f;
                                if (this.documentAttachType != 5) {
                                    if (this.documentAttachType != 3) {
                                        this.buttonState = 1;
                                        fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                                        if (fileProgress != null) {
                                            f = fileProgress.floatValue();
                                        }
                                        this.radialProgress.setProgress(f, false);
                                        this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                                        invalidate();
                                    }
                                }
                                if (this.documentAttach == null) {
                                    isLoadingFile = FileLoader.getInstance(this.currentAccount).isLoadingFile(attachFileName);
                                } else {
                                    isLoadingFile = ImageLoader.getInstance().isLoadingHttpFile(attachFileName);
                                }
                                if (isLoadingFile) {
                                    this.buttonState = 2;
                                    this.radialProgress.setProgress(0.0f, z);
                                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                                } else {
                                    this.buttonState = 4;
                                    fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                                    if (fileProgress == null) {
                                        this.radialProgress.setProgress(fileProgress.floatValue(), z);
                                    } else {
                                        this.radialProgress.setProgress(0.0f, z);
                                    }
                                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                                }
                                invalidate();
                            } else {
                                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                                if (this.documentAttachType != 5) {
                                    if (this.documentAttachType != 3) {
                                        this.buttonState = -1;
                                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                                        invalidate();
                                    }
                                }
                                isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                                if (isPlayingMessage) {
                                    if (isPlayingMessage || !MediaController.getInstance().isMessagePaused()) {
                                        this.buttonState = 1;
                                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                                        invalidate();
                                    }
                                }
                                this.buttonState = 0;
                                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                                invalidate();
                            }
                            return;
                        }
                        this.radialProgress.setBackground(null, false, false);
                    } else if (this.documentAttach != null) {
                        attachFileName = FileLoader.getAttachFileName(this.documentAttach);
                        pathToAttach = FileLoader.getPathToAttach(this.documentAttach);
                        if (TextUtils.isEmpty(attachFileName)) {
                            if (pathToAttach.exists()) {
                                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                                if (this.documentAttachType != 5) {
                                    if (this.documentAttachType != 3) {
                                        this.buttonState = -1;
                                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                                        invalidate();
                                    }
                                }
                                isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                                if (isPlayingMessage) {
                                    if (isPlayingMessage) {
                                    }
                                    this.buttonState = 1;
                                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                                    invalidate();
                                }
                                this.buttonState = 0;
                                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                                invalidate();
                            } else {
                                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(attachFileName, this);
                                f = 0.0f;
                                if (this.documentAttachType != 5) {
                                    if (this.documentAttachType != 3) {
                                        this.buttonState = 1;
                                        fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                                        if (fileProgress != null) {
                                            f = fileProgress.floatValue();
                                        }
                                        this.radialProgress.setProgress(f, false);
                                        this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                                        invalidate();
                                    }
                                }
                                if (this.documentAttach == null) {
                                    isLoadingFile = ImageLoader.getInstance().isLoadingHttpFile(attachFileName);
                                } else {
                                    isLoadingFile = FileLoader.getInstance(this.currentAccount).isLoadingFile(attachFileName);
                                }
                                if (isLoadingFile) {
                                    this.buttonState = 4;
                                    fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                                    if (fileProgress == null) {
                                        this.radialProgress.setProgress(0.0f, z);
                                    } else {
                                        this.radialProgress.setProgress(fileProgress.floatValue(), z);
                                    }
                                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                                } else {
                                    this.buttonState = 2;
                                    this.radialProgress.setProgress(0.0f, z);
                                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                                }
                                invalidate();
                            }
                            return;
                        }
                        this.radialProgress.setBackground(null, false, false);
                    }
                }
                attachFileName = null;
                pathToAttach = attachFileName;
                if (TextUtils.isEmpty(attachFileName)) {
                    this.radialProgress.setBackground(null, false, false);
                }
                if (pathToAttach.exists()) {
                    DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(attachFileName, this);
                    f = 0.0f;
                    if (this.documentAttachType != 5) {
                        if (this.documentAttachType != 3) {
                            this.buttonState = 1;
                            fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                            if (fileProgress != null) {
                                f = fileProgress.floatValue();
                            }
                            this.radialProgress.setProgress(f, false);
                            this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                            invalidate();
                        }
                    }
                    if (this.documentAttach == null) {
                        isLoadingFile = FileLoader.getInstance(this.currentAccount).isLoadingFile(attachFileName);
                    } else {
                        isLoadingFile = ImageLoader.getInstance().isLoadingHttpFile(attachFileName);
                    }
                    if (isLoadingFile) {
                        this.buttonState = 2;
                        this.radialProgress.setProgress(0.0f, z);
                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                    } else {
                        this.buttonState = 4;
                        fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                        if (fileProgress == null) {
                            this.radialProgress.setProgress(fileProgress.floatValue(), z);
                        } else {
                            this.radialProgress.setProgress(0.0f, z);
                        }
                        this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                    }
                    invalidate();
                } else {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                    if (this.documentAttachType != 5) {
                        if (this.documentAttachType != 3) {
                            this.buttonState = -1;
                            this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                            invalidate();
                        }
                    }
                    isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                    if (isPlayingMessage) {
                        if (isPlayingMessage) {
                        }
                        this.buttonState = 1;
                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                        invalidate();
                    }
                    this.buttonState = 0;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                    invalidate();
                }
                return;
            }
        }
        if (this.documentAttach != null) {
            attachFileName = FileLoader.getAttachFileName(this.documentAttach);
            pathToAttach = FileLoader.getPathToAttach(this.documentAttach);
        } else {
            if (this.inlineResult.content instanceof TL_webDocument) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(Utilities.MD5(this.inlineResult.content.url));
                stringBuilder.append(".");
                stringBuilder.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg"));
                attachFileName = stringBuilder.toString();
                pathToAttach = new File(FileLoader.getDirectory(4), attachFileName);
            }
            attachFileName = null;
            pathToAttach = attachFileName;
        }
        if (TextUtils.isEmpty(attachFileName)) {
            if (pathToAttach.exists()) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                if (this.documentAttachType != 5) {
                    if (this.documentAttachType != 3) {
                        this.buttonState = -1;
                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                        invalidate();
                    }
                }
                isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (isPlayingMessage) {
                    if (isPlayingMessage) {
                    }
                    this.buttonState = 1;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                    invalidate();
                }
                this.buttonState = 0;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                invalidate();
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(attachFileName, this);
                f = 0.0f;
                if (this.documentAttachType != 5) {
                    if (this.documentAttachType != 3) {
                        this.buttonState = 1;
                        fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                        if (fileProgress != null) {
                            f = fileProgress.floatValue();
                        }
                        this.radialProgress.setProgress(f, false);
                        this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                        invalidate();
                    }
                }
                if (this.documentAttach == null) {
                    isLoadingFile = ImageLoader.getInstance().isLoadingHttpFile(attachFileName);
                } else {
                    isLoadingFile = FileLoader.getInstance(this.currentAccount).isLoadingFile(attachFileName);
                }
                if (isLoadingFile) {
                    this.buttonState = 4;
                    fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                    if (fileProgress == null) {
                        this.radialProgress.setProgress(0.0f, z);
                    } else {
                        this.radialProgress.setProgress(fileProgress.floatValue(), z);
                    }
                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                } else {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, z);
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                }
                invalidate();
            }
            return;
        }
        this.radialProgress.setBackground(null, false, false);
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public BotInlineResult getResult() {
        return this.inlineResult;
    }

    public void onFailedDownload(String str) {
        updateButtonState(null);
    }

    public void onSuccessDownload(String str) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(true);
    }

    public void onProgressDownload(String str, float f) {
        this.radialProgress.setProgress(f, true);
        if (this.documentAttachType != 3) {
            if (this.documentAttachType != 5) {
                if (this.buttonState != 1) {
                    updateButtonState(false);
                    return;
                }
                return;
            }
        }
        if (this.buttonState != 4) {
            updateButtonState(false);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
