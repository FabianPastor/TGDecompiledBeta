package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
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

    public ContextLinkCell(Context context) {
        super(context);
    }

    @android.annotation.SuppressLint({"DrawAllocation"})
    protected void onMeasure(int r53, int r54) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r45_3 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) in PHI: PHI: (r45_2 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) = (r45_1 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>), (r45_0 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>), (r45_0 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>), (r45_3 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) binds: {(r45_1 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>)=B:7:0x0064, (r45_0 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>)=B:106:0x03e2, (r45_0 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>)=B:108:0x03ea, (r45_3 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>)=B:109:0x03ec}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r52 = this;
        r4 = 0;
        r0 = r52;
        r0.drawLinkImageView = r4;
        r4 = 0;
        r0 = r52;
        r0.descriptionLayout = r4;
        r4 = 0;
        r0 = r52;
        r0.titleLayout = r4;
        r4 = 0;
        r0 = r52;
        r0.linkLayout = r4;
        r4 = 0;
        r0 = r52;
        r0.currentPhotoObject = r4;
        r4 = NUM; // 0x41d80000 float:27.0 double:5.457818764E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r52;
        r0.linkY = r4;
        r0 = r52;
        r4 = r0.inlineResult;
        if (r4 != 0) goto L_0x0041;
    L_0x0029:
        r0 = r52;
        r4 = r0.documentAttach;
        if (r4 != 0) goto L_0x0041;
    L_0x002f:
        r4 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0 = r52;
        r0.setMeasuredDimension(r4, r6);
    L_0x0040:
        return;
    L_0x0041:
        r47 = android.view.View.MeasureSpec.getSize(r53);
        r4 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r47 - r4;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r44 = r4 - r6;
        r35 = 0;
        r45 = 0;
        r16 = 0;
        r17 = 0;
        r0 = r52;
        r4 = r0.documentAttach;
        if (r4 == 0) goto L_0x03de;
    L_0x0064:
        r45 = new java.util.ArrayList;
        r45.<init>();
        r0 = r52;
        r4 = r0.documentAttach;
        r4 = r4.thumb;
        r0 = r45;
        r0.add(r4);
    L_0x0074:
        r0 = r52;
        r4 = r0.mediaWebpage;
        if (r4 != 0) goto L_0x019b;
    L_0x007a:
        r0 = r52;
        r4 = r0.inlineResult;
        if (r4 == 0) goto L_0x019b;
    L_0x0080:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.title;
        if (r4 == 0) goto L_0x00f2;
    L_0x0088:
        r4 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x03fd }
        r0 = r52;	 Catch:{ Exception -> 0x03fd }
        r6 = r0.inlineResult;	 Catch:{ Exception -> 0x03fd }
        r6 = r6.title;	 Catch:{ Exception -> 0x03fd }
        r4 = r4.measureText(r6);	 Catch:{ Exception -> 0x03fd }
        r8 = (double) r4;	 Catch:{ Exception -> 0x03fd }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x03fd }
        r0 = (int) r8;	 Catch:{ Exception -> 0x03fd }
        r49 = r0;	 Catch:{ Exception -> 0x03fd }
        r0 = r52;	 Catch:{ Exception -> 0x03fd }
        r4 = r0.inlineResult;	 Catch:{ Exception -> 0x03fd }
        r4 = r4.title;	 Catch:{ Exception -> 0x03fd }
        r6 = 10;	 Catch:{ Exception -> 0x03fd }
        r8 = 32;	 Catch:{ Exception -> 0x03fd }
        r4 = r4.replace(r6, r8);	 Catch:{ Exception -> 0x03fd }
        r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x03fd }
        r6 = r6.getFontMetricsInt();	 Catch:{ Exception -> 0x03fd }
        r8 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;	 Catch:{ Exception -> 0x03fd }
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x03fd }
        r9 = 0;	 Catch:{ Exception -> 0x03fd }
        r4 = org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r8, r9);	 Catch:{ Exception -> 0x03fd }
        r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x03fd }
        r0 = r49;	 Catch:{ Exception -> 0x03fd }
        r1 = r44;	 Catch:{ Exception -> 0x03fd }
        r8 = java.lang.Math.min(r0, r1);	 Catch:{ Exception -> 0x03fd }
        r8 = (float) r8;	 Catch:{ Exception -> 0x03fd }
        r9 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x03fd }
        r5 = android.text.TextUtils.ellipsize(r4, r6, r8, r9);	 Catch:{ Exception -> 0x03fd }
        r4 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x03fd }
        r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x03fd }
        r8 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;	 Catch:{ Exception -> 0x03fd }
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x03fd }
        r7 = r44 + r8;	 Catch:{ Exception -> 0x03fd }
        r8 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x03fd }
        r9 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x03fd }
        r10 = 0;	 Catch:{ Exception -> 0x03fd }
        r11 = 0;	 Catch:{ Exception -> 0x03fd }
        r4.<init>(r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x03fd }
        r0 = r52;	 Catch:{ Exception -> 0x03fd }
        r0.titleLayout = r4;	 Catch:{ Exception -> 0x03fd }
    L_0x00e5:
        r0 = r52;
        r4 = r0.letterDrawable;
        r0 = r52;
        r6 = r0.inlineResult;
        r6 = r6.title;
        r4.setTitle(r6);
    L_0x00f2:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.description;
        if (r4 == 0) goto L_0x014d;
    L_0x00fa:
        r0 = r52;	 Catch:{ Exception -> 0x0403 }
        r4 = r0.inlineResult;	 Catch:{ Exception -> 0x0403 }
        r4 = r4.description;	 Catch:{ Exception -> 0x0403 }
        r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0403 }
        r6 = r6.getFontMetricsInt();	 Catch:{ Exception -> 0x0403 }
        r8 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;	 Catch:{ Exception -> 0x0403 }
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x0403 }
        r9 = 0;	 Catch:{ Exception -> 0x0403 }
        r6 = org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r8, r9);	 Catch:{ Exception -> 0x0403 }
        r7 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0403 }
        r10 = 0;	 Catch:{ Exception -> 0x0403 }
        r11 = 3;	 Catch:{ Exception -> 0x0403 }
        r8 = r44;	 Catch:{ Exception -> 0x0403 }
        r9 = r44;	 Catch:{ Exception -> 0x0403 }
        r4 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0403 }
        r0 = r52;	 Catch:{ Exception -> 0x0403 }
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x0403 }
        r0 = r52;	 Catch:{ Exception -> 0x0403 }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0403 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0403 }
        if (r4 <= 0) goto L_0x014d;	 Catch:{ Exception -> 0x0403 }
    L_0x012b:
        r0 = r52;	 Catch:{ Exception -> 0x0403 }
        r4 = r0.descriptionY;	 Catch:{ Exception -> 0x0403 }
        r0 = r52;	 Catch:{ Exception -> 0x0403 }
        r6 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0403 }
        r0 = r52;	 Catch:{ Exception -> 0x0403 }
        r8 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0403 }
        r8 = r8.getLineCount();	 Catch:{ Exception -> 0x0403 }
        r8 = r8 + -1;	 Catch:{ Exception -> 0x0403 }
        r6 = r6.getLineBottom(r8);	 Catch:{ Exception -> 0x0403 }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x0403 }
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0403 }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0403 }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x0403 }
        r0 = r52;	 Catch:{ Exception -> 0x0403 }
        r0.linkY = r4;	 Catch:{ Exception -> 0x0403 }
    L_0x014d:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.url;
        if (r4 == 0) goto L_0x019b;
    L_0x0155:
        r4 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0409 }
        r0 = r52;	 Catch:{ Exception -> 0x0409 }
        r6 = r0.inlineResult;	 Catch:{ Exception -> 0x0409 }
        r6 = r6.url;	 Catch:{ Exception -> 0x0409 }
        r4 = r4.measureText(r6);	 Catch:{ Exception -> 0x0409 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0409 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0409 }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0409 }
        r49 = r0;	 Catch:{ Exception -> 0x0409 }
        r0 = r52;	 Catch:{ Exception -> 0x0409 }
        r4 = r0.inlineResult;	 Catch:{ Exception -> 0x0409 }
        r4 = r4.url;	 Catch:{ Exception -> 0x0409 }
        r6 = 10;	 Catch:{ Exception -> 0x0409 }
        r8 = 32;	 Catch:{ Exception -> 0x0409 }
        r4 = r4.replace(r6, r8);	 Catch:{ Exception -> 0x0409 }
        r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0409 }
        r0 = r49;	 Catch:{ Exception -> 0x0409 }
        r1 = r44;	 Catch:{ Exception -> 0x0409 }
        r8 = java.lang.Math.min(r0, r1);	 Catch:{ Exception -> 0x0409 }
        r8 = (float) r8;	 Catch:{ Exception -> 0x0409 }
        r9 = android.text.TextUtils.TruncateAt.MIDDLE;	 Catch:{ Exception -> 0x0409 }
        r7 = android.text.TextUtils.ellipsize(r4, r6, r8, r9);	 Catch:{ Exception -> 0x0409 }
        r6 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0409 }
        r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0409 }
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0409 }
        r11 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0409 }
        r12 = 0;	 Catch:{ Exception -> 0x0409 }
        r13 = 0;	 Catch:{ Exception -> 0x0409 }
        r9 = r44;	 Catch:{ Exception -> 0x0409 }
        r6.<init>(r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0409 }
        r0 = r52;	 Catch:{ Exception -> 0x0409 }
        r0.linkLayout = r6;	 Catch:{ Exception -> 0x0409 }
    L_0x019b:
        r14 = 0;
        r0 = r52;
        r4 = r0.documentAttach;
        if (r4 == 0) goto L_0x0442;
    L_0x01a2:
        r0 = r52;
        r4 = r0.documentAttach;
        r4 = org.telegram.messenger.MessageObject.isGifDocument(r4);
        if (r4 == 0) goto L_0x040f;
    L_0x01ac:
        r0 = r52;
        r4 = r0.documentAttach;
        r4 = r4.thumb;
        r0 = r52;
        r0.currentPhotoObject = r4;
    L_0x01b6:
        r0 = r52;
        r4 = r0.inlineResult;
        if (r4 == 0) goto L_0x027f;
    L_0x01bc:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.content;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r4 == 0) goto L_0x01f3;
    L_0x01c6:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.type;
        if (r4 == 0) goto L_0x01f3;
    L_0x01ce:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.type;
        r6 = "gif";
        r4 = r4.startsWith(r6);
        if (r4 == 0) goto L_0x0473;
    L_0x01dd:
        r0 = r52;
        r4 = r0.documentAttachType;
        r6 = 2;
        if (r4 == r6) goto L_0x01f3;
    L_0x01e4:
        r0 = r52;
        r4 = r0.inlineResult;
        r0 = r4.content;
        r16 = r0;
        r16 = (org.telegram.tgnet.TLRPC.TL_webDocument) r16;
        r4 = 2;
        r0 = r52;
        r0.documentAttachType = r4;
    L_0x01f3:
        if (r16 != 0) goto L_0x0209;
    L_0x01f5:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.thumb;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r4 == 0) goto L_0x0209;
    L_0x01ff:
        r0 = r52;
        r4 = r0.inlineResult;
        r0 = r4.thumb;
        r16 = r0;
        r16 = (org.telegram.tgnet.TLRPC.TL_webDocument) r16;
    L_0x0209:
        if (r16 != 0) goto L_0x027f;
    L_0x020b:
        r0 = r52;
        r4 = r0.currentPhotoObject;
        if (r4 != 0) goto L_0x027f;
    L_0x0211:
        if (r35 != 0) goto L_0x027f;
    L_0x0213:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.send_message;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
        if (r4 != 0) goto L_0x0227;
    L_0x021d:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.send_message;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
        if (r4 == 0) goto L_0x027f;
    L_0x0227:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.send_message;
        r4 = r4.geo;
        r0 = r4.lat;
        r40 = r0;
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.send_message;
        r4 = r4.geo;
        r0 = r4._long;
        r42 = r0;
        r4 = java.util.Locale.US;
        r6 = "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false";
        r8 = 5;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Double.valueOf(r40);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Double.valueOf(r42);
        r8[r9] = r10;
        r9 = 2;
        r10 = 2;
        r11 = org.telegram.messenger.AndroidUtilities.density;
        r0 = (double) r11;
        r18 = r0;
        r18 = java.lang.Math.ceil(r18);
        r0 = r18;
        r11 = (int) r0;
        r10 = java.lang.Math.min(r10, r11);
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r9 = 3;
        r10 = java.lang.Double.valueOf(r40);
        r8[r9] = r10;
        r9 = 4;
        r10 = java.lang.Double.valueOf(r42);
        r8[r9] = r10;
        r17 = java.lang.String.format(r4, r6, r8);
    L_0x027f:
        r48 = 0;
        r37 = 0;
        r0 = r52;
        r4 = r0.documentAttach;
        if (r4 == 0) goto L_0x02bf;
    L_0x0289:
        r34 = 0;
    L_0x028b:
        r0 = r52;
        r4 = r0.documentAttach;
        r4 = r4.attributes;
        r4 = r4.size();
        r0 = r34;
        if (r0 >= r4) goto L_0x02bf;
    L_0x0299:
        r0 = r52;
        r4 = r0.documentAttach;
        r4 = r4.attributes;
        r0 = r34;
        r33 = r4.get(r0);
        r33 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r33;
        r0 = r33;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 != 0) goto L_0x02b3;
    L_0x02ad:
        r0 = r33;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x04a4;
    L_0x02b3:
        r0 = r33;
        r0 = r0.f36w;
        r48 = r0;
        r0 = r33;
        r0 = r0.f35h;
        r37 = r0;
    L_0x02bf:
        if (r48 == 0) goto L_0x02c3;
    L_0x02c1:
        if (r37 != 0) goto L_0x02e0;
    L_0x02c3:
        r0 = r52;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x04a8;
    L_0x02c9:
        if (r35 == 0) goto L_0x02d0;
    L_0x02cb:
        r4 = -1;
        r0 = r35;
        r0.size = r4;
    L_0x02d0:
        r0 = r52;
        r4 = r0.currentPhotoObject;
        r0 = r4.f43w;
        r48 = r0;
        r0 = r52;
        r4 = r0.currentPhotoObject;
        r0 = r4.f42h;
        r37 = r0;
    L_0x02e0:
        if (r48 == 0) goto L_0x02e4;
    L_0x02e2:
        if (r37 != 0) goto L_0x02ec;
    L_0x02e4:
        r4 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r37 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r48 = r37;
    L_0x02ec:
        r0 = r52;
        r4 = r0.documentAttach;
        if (r4 != 0) goto L_0x02fc;
    L_0x02f2:
        r0 = r52;
        r4 = r0.currentPhotoObject;
        if (r4 != 0) goto L_0x02fc;
    L_0x02f8:
        if (r16 != 0) goto L_0x02fc;
    L_0x02fa:
        if (r17 == 0) goto L_0x0383;
    L_0x02fc:
        r22 = "52_52_b";
        r0 = r52;
        r4 = r0.mediaWebpage;
        if (r4 == 0) goto L_0x04f7;
    L_0x0305:
        r0 = r48;
        r4 = (float) r0;
        r0 = r37;
        r6 = (float) r0;
        r8 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = (float) r8;
        r6 = r6 / r8;
        r4 = r4 / r6;
        r0 = (int) r4;
        r49 = r0;
        r0 = r52;
        r4 = r0.documentAttachType;
        r6 = 2;
        if (r4 != r6) goto L_0x04be;
    L_0x031e:
        r4 = java.util.Locale.US;
        r6 = "%d_%d_b";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r0 = r49;
        r10 = (float) r0;
        r11 = org.telegram.messenger.AndroidUtilities.density;
        r10 = r10 / r11;
        r10 = (int) r10;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r9 = 1;
        r10 = 80;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r12 = java.lang.String.format(r4, r6, r8);
        r22 = r12;
    L_0x0343:
        r0 = r52;
        r6 = r0.linkImageView;
        r0 = r52;
        r4 = r0.documentAttachType;
        r8 = 6;
        if (r4 != r8) goto L_0x04fc;
    L_0x034e:
        r4 = 1;
    L_0x034f:
        r6.setAspectFit(r4);
        r0 = r52;
        r4 = r0.documentAttachType;
        r6 = 2;
        if (r4 != r6) goto L_0x0528;
    L_0x0359:
        r0 = r52;
        r4 = r0.documentAttach;
        if (r4 == 0) goto L_0x0502;
    L_0x035f:
        r0 = r52;
        r8 = r0.linkImageView;
        r0 = r52;
        r9 = r0.documentAttach;
        r10 = 0;
        r0 = r52;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x04ff;
    L_0x036e:
        r0 = r52;
        r4 = r0.currentPhotoObject;
        r11 = r4.location;
    L_0x0374:
        r0 = r52;
        r4 = r0.documentAttach;
        r13 = r4.size;
        r15 = 0;
        r8.setImage(r9, r10, r11, r12, r13, r14, r15);
    L_0x037e:
        r4 = 1;
        r0 = r52;
        r0.drawLinkImageView = r4;
    L_0x0383:
        r0 = r52;
        r4 = r0.mediaWebpage;
        if (r4 == 0) goto L_0x0580;
    L_0x0389:
        r49 = r47;
        r38 = android.view.View.MeasureSpec.getSize(r54);
        if (r38 != 0) goto L_0x0397;
    L_0x0391:
        r4 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r38 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x0397:
        r0 = r52;
        r1 = r49;
        r2 = r38;
        r0.setMeasuredDimension(r1, r2);
        r4 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r49 - r4;
        r50 = r4 / 2;
        r4 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r38 - r4;
        r51 = r4 / 2;
        r0 = r52;
        r4 = r0.radialProgress;
        r6 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r50;
        r8 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = r8 + r51;
        r0 = r50;
        r1 = r51;
        r4.setProgressRect(r0, r1, r6, r8);
        r0 = r52;
        r4 = r0.linkImageView;
        r6 = 0;
        r8 = 0;
        r0 = r49;
        r1 = r38;
        r4.setImageCoords(r6, r8, r0, r1);
        goto L_0x0040;
    L_0x03de:
        r0 = r52;
        r4 = r0.inlineResult;
        if (r4 == 0) goto L_0x0074;
    L_0x03e4:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.photo;
        if (r4 == 0) goto L_0x0074;
    L_0x03ec:
        r45 = new java.util.ArrayList;
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.photo;
        r4 = r4.sizes;
        r0 = r45;
        r0.<init>(r4);
        goto L_0x0074;
    L_0x03fd:
        r36 = move-exception;
        org.telegram.messenger.FileLog.m3e(r36);
        goto L_0x00e5;
    L_0x0403:
        r36 = move-exception;
        org.telegram.messenger.FileLog.m3e(r36);
        goto L_0x014d;
    L_0x0409:
        r36 = move-exception;
        org.telegram.messenger.FileLog.m3e(r36);
        goto L_0x019b;
    L_0x040f:
        r0 = r52;
        r4 = r0.documentAttach;
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r4);
        if (r4 == 0) goto L_0x0428;
    L_0x0419:
        r0 = r52;
        r4 = r0.documentAttach;
        r4 = r4.thumb;
        r0 = r52;
        r0.currentPhotoObject = r4;
        r14 = "webp";
        goto L_0x01b6;
    L_0x0428:
        r0 = r52;
        r4 = r0.documentAttachType;
        r6 = 5;
        if (r4 == r6) goto L_0x01b6;
    L_0x042f:
        r0 = r52;
        r4 = r0.documentAttachType;
        r6 = 3;
        if (r4 == r6) goto L_0x01b6;
    L_0x0436:
        r0 = r52;
        r4 = r0.documentAttach;
        r4 = r4.thumb;
        r0 = r52;
        r0.currentPhotoObject = r4;
        goto L_0x01b6;
    L_0x0442:
        r0 = r52;
        r4 = r0.inlineResult;
        if (r4 == 0) goto L_0x01b6;
    L_0x0448:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.photo;
        if (r4 == 0) goto L_0x01b6;
    L_0x0450:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r6 = 1;
        r0 = r45;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r4, r6);
        r0 = r52;
        r0.currentPhotoObject = r4;
        r4 = 80;
        r0 = r45;
        r35 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r4);
        r0 = r52;
        r4 = r0.currentPhotoObject;
        r0 = r35;
        if (r0 != r4) goto L_0x01b6;
    L_0x046f:
        r35 = 0;
        goto L_0x01b6;
    L_0x0473:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.type;
        r6 = "photo";
        r4 = r4.equals(r6);
        if (r4 == 0) goto L_0x01f3;
    L_0x0482:
        r0 = r52;
        r4 = r0.inlineResult;
        r4 = r4.thumb;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r4 == 0) goto L_0x0498;
    L_0x048c:
        r0 = r52;
        r4 = r0.inlineResult;
        r0 = r4.thumb;
        r16 = r0;
        r16 = (org.telegram.tgnet.TLRPC.TL_webDocument) r16;
        goto L_0x01f3;
    L_0x0498:
        r0 = r52;
        r4 = r0.inlineResult;
        r0 = r4.content;
        r16 = r0;
        r16 = (org.telegram.tgnet.TLRPC.TL_webDocument) r16;
        goto L_0x01f3;
    L_0x04a4:
        r34 = r34 + 1;
        goto L_0x028b;
    L_0x04a8:
        r0 = r52;
        r4 = r0.inlineResult;
        if (r4 == 0) goto L_0x02e0;
    L_0x04ae:
        r0 = r52;
        r4 = r0.inlineResult;
        r46 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r4);
        r4 = 0;
        r48 = r46[r4];
        r4 = 1;
        r37 = r46[r4];
        goto L_0x02e0;
    L_0x04be:
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r0 = r49;
        r10 = (float) r0;
        r11 = org.telegram.messenger.AndroidUtilities.density;
        r10 = r10 / r11;
        r10 = (int) r10;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r9 = 1;
        r10 = 80;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r12 = java.lang.String.format(r4, r6, r8);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r12);
        r6 = "_b";
        r4 = r4.append(r6);
        r22 = r4.toString();
        goto L_0x0343;
    L_0x04f7:
        r12 = "52_52";
        goto L_0x0343;
    L_0x04fc:
        r4 = 0;
        goto L_0x034f;
    L_0x04ff:
        r11 = 0;
        goto L_0x0374;
    L_0x0502:
        r0 = r52;
        r15 = r0.linkImageView;
        r18 = 0;
        r19 = 0;
        r0 = r52;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x0525;
    L_0x0510:
        r0 = r52;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r20 = r0;
    L_0x0518:
        r22 = -1;
        r24 = 1;
        r21 = r12;
        r23 = r14;
        r15.setImage(r16, r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x037e;
    L_0x0525:
        r20 = 0;
        goto L_0x0518;
    L_0x0528:
        r0 = r52;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x055a;
    L_0x052e:
        r0 = r52;
        r0 = r0.linkImageView;
        r18 = r0;
        r0 = r52;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r19 = r0;
        if (r35 == 0) goto L_0x0557;
    L_0x053e:
        r0 = r35;
        r0 = r0.location;
        r21 = r0;
    L_0x0544:
        r0 = r52;
        r4 = r0.currentPhotoObject;
        r0 = r4.size;
        r23 = r0;
        r25 = 0;
        r20 = r12;
        r24 = r14;
        r18.setImage(r19, r20, r21, r22, r23, r24, r25);
        goto L_0x037e;
    L_0x0557:
        r21 = 0;
        goto L_0x0544;
    L_0x055a:
        r0 = r52;
        r0 = r0.linkImageView;
        r23 = r0;
        r27 = 0;
        if (r35 == 0) goto L_0x057d;
    L_0x0564:
        r0 = r35;
        r0 = r0.location;
        r28 = r0;
    L_0x056a:
        r30 = -1;
        r32 = 1;
        r24 = r16;
        r25 = r17;
        r26 = r12;
        r29 = r22;
        r31 = r14;
        r23.setImage(r24, r25, r26, r27, r28, r29, r30, r31, r32);
        goto L_0x037e;
    L_0x057d:
        r28 = 0;
        goto L_0x056a;
    L_0x0580:
        r38 = 0;
        r0 = r52;
        r4 = r0.titleLayout;
        if (r4 == 0) goto L_0x05a6;
    L_0x0588:
        r0 = r52;
        r4 = r0.titleLayout;
        r4 = r4.getLineCount();
        if (r4 == 0) goto L_0x05a6;
    L_0x0592:
        r0 = r52;
        r4 = r0.titleLayout;
        r0 = r52;
        r6 = r0.titleLayout;
        r6 = r6.getLineCount();
        r6 = r6 + -1;
        r4 = r4.getLineBottom(r6);
        r38 = r38 + r4;
    L_0x05a6:
        r0 = r52;
        r4 = r0.descriptionLayout;
        if (r4 == 0) goto L_0x05ca;
    L_0x05ac:
        r0 = r52;
        r4 = r0.descriptionLayout;
        r4 = r4.getLineCount();
        if (r4 == 0) goto L_0x05ca;
    L_0x05b6:
        r0 = r52;
        r4 = r0.descriptionLayout;
        r0 = r52;
        r6 = r0.descriptionLayout;
        r6 = r6.getLineCount();
        r6 = r6 + -1;
        r4 = r4.getLineBottom(r6);
        r38 = r38 + r4;
    L_0x05ca:
        r0 = r52;
        r4 = r0.linkLayout;
        if (r4 == 0) goto L_0x05ee;
    L_0x05d0:
        r0 = r52;
        r4 = r0.linkLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x05ee;
    L_0x05da:
        r0 = r52;
        r4 = r0.linkLayout;
        r0 = r52;
        r6 = r0.linkLayout;
        r6 = r6.getLineCount();
        r6 = r6 + -1;
        r4 = r4.getLineBottom(r6);
        r38 = r38 + r4;
    L_0x05ee:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r38;
        r38 = java.lang.Math.max(r4, r0);
        r6 = android.view.View.MeasureSpec.getSize(r53);
        r4 = NUM; // 0x42880000 float:68.0 double:5.514805956E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r8 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = r8 + r38;
        r8 = java.lang.Math.max(r4, r8);
        r0 = r52;
        r4 = r0.needDivider;
        if (r4 == 0) goto L_0x0691;
    L_0x0616:
        r4 = 1;
    L_0x0617:
        r4 = r4 + r8;
        r0 = r52;
        r0.setMeasuredDimension(r6, r4);
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r39 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x0693;
    L_0x0627:
        r4 = android.view.View.MeasureSpec.getSize(r53);
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r50 = r4 - r39;
    L_0x0634:
        r0 = r52;
        r4 = r0.letterDrawable;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r8 = r50 + r39;
        r9 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r50;
        r4.setBounds(r0, r6, r8, r9);
        r0 = r52;
        r4 = r0.linkImageView;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0 = r50;
        r1 = r39;
        r2 = r39;
        r4.setImageCoords(r0, r6, r1, r2);
        r0 = r52;
        r4 = r0.documentAttachType;
        r6 = 3;
        if (r4 == r6) goto L_0x066c;
    L_0x0665:
        r0 = r52;
        r4 = r0.documentAttachType;
        r6 = 5;
        if (r4 != r6) goto L_0x0040;
    L_0x066c:
        r0 = r52;
        r4 = r0.radialProgress;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r50;
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r9 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = r9 + r50;
        r10 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r4.setProgressRect(r6, r8, r9, r10);
        goto L_0x0040;
    L_0x0691:
        r4 = 0;
        goto L_0x0617;
    L_0x0693:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r50 = org.telegram.messenger.AndroidUtilities.dp(r4);
        goto L_0x0634;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.onMeasure(int, int):void");
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
            TL_message message = new TL_message();
            message.out = true;
            message.id = -Utilities.random.nextInt();
            message.to_id = new TL_peerUser();
            Peer peer = message.to_id;
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            message.from_id = clientUserId;
            peer.user_id = clientUserId;
            message.date = (int) (System.currentTimeMillis() / 1000);
            message.message = TtmlNode.ANONYMOUS_REGION_ID;
            message.media = new TL_messageMediaDocument();
            MessageMedia messageMedia = message.media;
            messageMedia.flags |= 3;
            message.media.document = new TL_document();
            message.flags |= 768;
            if (this.documentAttach != null) {
                message.media.document = this.documentAttach;
                message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                String str;
                String ext = ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg");
                message.media.document.id = 0;
                message.media.document.access_hash = 0;
                message.media.document.date = message.date;
                message.media.document.mime_type = "audio/" + ext;
                message.media.document.size = 0;
                message.media.document.thumb = new TL_photoSizeEmpty();
                message.media.document.thumb.type = "s";
                message.media.document.dc_id = 0;
                TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
                attributeAudio.duration = MessageObject.getInlineResultDuration(this.inlineResult);
                attributeAudio.title = this.inlineResult.title != null ? this.inlineResult.title : TtmlNode.ANONYMOUS_REGION_ID;
                attributeAudio.performer = this.inlineResult.description != null ? this.inlineResult.description : TtmlNode.ANONYMOUS_REGION_ID;
                attributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    attributeAudio.voice = true;
                }
                message.media.document.attributes.add(attributeAudio);
                TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                fileName.file_name = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg");
                message.media.document.attributes.add(fileName);
                File directory = FileLoader.getDirectory(4);
                StringBuilder append = new StringBuilder().append(Utilities.MD5(this.inlineResult.content.url)).append(".");
                String str2 = this.inlineResult.content.url;
                if (this.documentAttachType == 5) {
                    str = "mp3";
                } else {
                    str = "ogg";
                }
                message.attachPath = new File(directory, append.append(ImageLoader.getHttpUrlExtension(str2, str)).toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(this.currentAccount, message, false);
        }
    }

    public void setLink(BotInlineResult contextResult, boolean media, boolean divider, boolean shadow) {
        this.needDivider = divider;
        this.needShadow = shadow;
        this.inlineResult = contextResult;
        if (this.inlineResult == null || this.inlineResult.document == null) {
            this.documentAttach = null;
        } else {
            this.documentAttach = this.inlineResult.document;
        }
        this.mediaWebpage = media;
        setAttachType();
        requestLayout();
        updateButtonState(false);
    }

    public void setGif(Document document, boolean divider) {
        this.needDivider = divider;
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

    public void setScaled(boolean value) {
        this.scaled = value;
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

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mediaWebpage || this.delegate == null || this.inlineResult == null) {
            return super.onTouchEvent(event);
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result = false;
        int side = AndroidUtilities.dp(48.0f);
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            boolean area = this.letterDrawable.getBounds().contains(x, y);
            if (event.getAction() == 0) {
                if (area) {
                    this.buttonPressed = true;
                    invalidate();
                    result = true;
                    this.radialProgress.swapBackground(getDrawableForCurrentState());
                }
            } else if (this.buttonPressed) {
                if (event.getAction() == 1) {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    didPressedButton();
                    invalidate();
                } else if (event.getAction() == 3) {
                    this.buttonPressed = false;
                    invalidate();
                } else if (event.getAction() == 2 && !area) {
                    this.buttonPressed = false;
                    invalidate();
                }
                this.radialProgress.swapBackground(getDrawableForCurrentState());
            }
        } else if (!(this.inlineResult == null || this.inlineResult.content == null || TextUtils.isEmpty(this.inlineResult.content.url))) {
            if (event.getAction() == 0) {
                if (this.letterDrawable.getBounds().contains(x, y)) {
                    this.buttonPressed = true;
                    result = true;
                }
            } else if (this.buttonPressed) {
                if (event.getAction() == 1) {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    this.delegate.didPressedImage(this);
                } else if (event.getAction() == 3) {
                    this.buttonPressed = false;
                } else if (event.getAction() == 2 && !this.letterDrawable.getBounds().contains(x, y)) {
                    this.buttonPressed = false;
                }
            }
        }
        if (result) {
            return result;
        }
        return super.onTouchEvent(event);
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
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.linkY);
            this.linkLayout.draw(canvas);
            canvas.restore();
        }
        int w;
        int h;
        int x;
        int y;
        if (this.mediaWebpage) {
            if (this.inlineResult != null && ((this.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo) || (this.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue))) {
                w = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                h = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                x = this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - w) / 2);
                y = this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - h) / 2);
                canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + this.linkImageView.getImageWidth()), (float) (this.linkImageView.getImageY() + this.linkImageView.getImageHeight()), LetterDrawable.paint);
                Theme.chat_inlineResultLocation.setBounds(x, y, x + w, y + h);
                Theme.chat_inlineResultLocation.draw(canvas);
            }
        } else if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            this.radialProgress.setProgressColor(Theme.getColor(this.buttonPressed ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
            this.radialProgress.draw(canvas);
        } else if (this.inlineResult != null && this.inlineResult.type.equals("file")) {
            w = Theme.chat_inlineResultFile.getIntrinsicWidth();
            h = Theme.chat_inlineResultFile.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.chat_inlineResultFile.setBounds(x, y, x + w, y + h);
            Theme.chat_inlineResultFile.draw(canvas);
        } else if (this.inlineResult != null && (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO) || this.inlineResult.type.equals("voice"))) {
            w = Theme.chat_inlineResultAudio.getIntrinsicWidth();
            h = Theme.chat_inlineResultAudio.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.chat_inlineResultAudio.setBounds(x, y, x + w, y + h);
            Theme.chat_inlineResultAudio.draw(canvas);
        } else if (this.inlineResult == null || !(this.inlineResult.type.equals("venue") || this.inlineResult.type.equals("geo"))) {
            this.letterDrawable.draw(canvas);
        } else {
            w = Theme.chat_inlineResultLocation.getIntrinsicWidth();
            h = Theme.chat_inlineResultLocation.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.chat_inlineResultLocation.setBounds(x, y, x + w, y + h);
            Theme.chat_inlineResultLocation.draw(canvas);
        }
        if (this.drawLinkImageView) {
            if (this.inlineResult != null) {
                this.linkImageView.setVisible(!PhotoViewer.isShowingImage(this.inlineResult), false);
            }
            canvas.save();
            if ((this.scaled && this.scale != 0.8f) || !(this.scaled || this.scale == 1.0f)) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                if (!this.scaled || this.scale == 0.8f) {
                    this.scale += ((float) dt) / 400.0f;
                    if (this.scale > 1.0f) {
                        this.scale = 1.0f;
                    }
                } else {
                    this.scale -= ((float) dt) / 400.0f;
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
        int i = 1;
        if (this.documentAttachType != 3 && this.documentAttachType != 5) {
            return this.buttonState == 1 ? Theme.chat_photoStatesDrawables[5][0] : null;
        } else {
            if (this.buttonState == -1) {
                return null;
            }
            this.radialProgress.setAlphaForPrevious(false);
            Drawable[] drawableArr = Theme.chat_fileStatesDrawable[this.buttonState + 5];
            if (!this.buttonPressed) {
                i = 0;
            }
            return drawableArr[i];
        }
    }

    public void updateButtonState(boolean animated) {
        float setProgress = 0.0f;
        String fileName = null;
        File cacheFile = null;
        if (this.documentAttachType == 5 || this.documentAttachType == 3) {
            if (this.documentAttach != null) {
                fileName = FileLoader.getAttachFileName(this.documentAttach);
                cacheFile = FileLoader.getPathToAttach(this.documentAttach);
            } else if (this.inlineResult.content instanceof TL_webDocument) {
                fileName = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg");
                cacheFile = new File(FileLoader.getDirectory(4), fileName);
            }
        } else if (this.mediaWebpage) {
            if (this.inlineResult != null) {
                if (this.inlineResult.document instanceof TL_document) {
                    fileName = FileLoader.getAttachFileName(this.inlineResult.document);
                    cacheFile = FileLoader.getPathToAttach(this.inlineResult.document);
                } else if (this.inlineResult.photo instanceof TL_photo) {
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
                    fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                    cacheFile = FileLoader.getPathToAttach(this.currentPhotoObject);
                } else if (this.inlineResult.content instanceof TL_webDocument) {
                    fileName = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, "jpg");
                    cacheFile = new File(FileLoader.getDirectory(4), fileName);
                } else if (this.inlineResult.thumb instanceof TL_webDocument) {
                    fileName = Utilities.MD5(this.inlineResult.thumb.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.thumb.url, "jpg");
                    cacheFile = new File(FileLoader.getDirectory(4), fileName);
                }
            } else if (this.documentAttach != null) {
                fileName = FileLoader.getAttachFileName(this.documentAttach);
                cacheFile = FileLoader.getPathToAttach(this.documentAttach);
            }
        }
        if (TextUtils.isEmpty(fileName)) {
            this.radialProgress.setBackground(null, false, false);
        } else if (cacheFile.exists()) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            if (this.documentAttachType == 5 || this.documentAttachType == 3) {
                boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
            } else {
                this.buttonState = -1;
            }
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
            invalidate();
        } else {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
            Float progress;
            if (this.documentAttachType == 5 || this.documentAttachType == 3) {
                boolean isLoading;
                if (this.documentAttach != null) {
                    isLoading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
                } else {
                    isLoading = ImageLoader.getInstance().isLoadingHttpFile(fileName);
                }
                if (isLoading) {
                    this.buttonState = 4;
                    progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                } else {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, animated);
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                }
            } else {
                this.buttonState = 1;
                progress = ImageLoader.getInstance().getFileProgress(fileName);
                if (progress != null) {
                    setProgress = progress.floatValue();
                }
                this.radialProgress.setProgress(setProgress, false);
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
            }
            invalidate();
        }
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public BotInlineResult getResult() {
        return this.inlineResult;
    }

    public void onFailedDownload(String fileName) {
        updateButtonState(false);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(true);
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, true);
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            if (this.buttonState != 4) {
                updateButtonState(false);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
