package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_exportedMessageLink;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;

public class ShareAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public EditTextEmoji commentTextView;
    private boolean copyLinkOnEnd;
    private ShareAlertDelegate delegate;
    private TLRPC$TL_exportedMessageLink exportedMessageLink;
    private FrameLayout frameLayout;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout2;
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    private int hasPoll;
    private boolean isChannel;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    private String linkToCopy;
    /* access modifiers changed from: private */
    public ShareDialogsAdapter listAdapter;
    private boolean loadingLink;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    /* access modifiers changed from: private */
    public TextView pickerBottomLayout;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public ShareSearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public EmptyTextProgressView searchEmptyView;
    private View selectedCountView;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC$Dialog> selectedDialogs = new LongSparseArray<>();
    private ArrayList<MessageObject> sendingMessageObjects;
    private String sendingText;
    /* access modifiers changed from: private */
    public View[] shadow = new View[2];
    /* access modifiers changed from: private */
    public AnimatorSet[] shadowAnimation = new AnimatorSet[2];
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public int topBeforeSwitch;
    /* access modifiers changed from: private */
    public FrameLayout writeButtonContainer;

    public interface ShareAlertDelegate {
        void didShare();
    }

    static /* synthetic */ boolean lambda$new$4(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    private class SearchField extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private View searchBackground;
        /* access modifiers changed from: private */
        public EditTextBoldCursor searchEditText;
        private ImageView searchIconImageView;

        public SearchField(Context context) {
            super(context);
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("dialogSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView3.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ShareAlert.SearchField.this.lambda$new$0$ShareAlert$SearchField(view);
                }
            });
            AnonymousClass1 r0 = new EditTextBoldCursor(context, ShareAlert.this) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setLocation(obtain.getRawX(), obtain.getRawY() - ShareAlert.this.containerView.getTranslationY());
                    if (obtain.getAction() == 1) {
                        obtain.setAction(3);
                    }
                    ShareAlert.this.gridView.dispatchTouchEvent(obtain);
                    obtain.recycle();
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
            this.searchEditText = r0;
            r0.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor("dialogSearchHint"));
            this.searchEditText.setTextColor(Theme.getColor("dialogSearchText"));
            this.searchEditText.setBackgroundDrawable((Drawable) null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            this.searchEditText.setHint(LocaleController.getString("ShareSendTo", NUM));
            this.searchEditText.setCursorColor(Theme.getColor("featuredStickers_addedIcon"));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(ShareAlert.this) {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    boolean z = true;
                    boolean z2 = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                        z = false;
                    }
                    if (z2 != z) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (z2) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(z2 ? 1.0f : 0.1f);
                        if (!z2) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    String obj = SearchField.this.searchEditText.getText().toString();
                    if (obj.length() != 0) {
                        if (ShareAlert.this.searchEmptyView != null) {
                            ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
                        }
                    } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                        int access$600 = ShareAlert.this.getCurrentTop();
                        ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", NUM));
                        ShareAlert.this.searchEmptyView.showTextView();
                        ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
                        ShareAlert.this.listAdapter.notifyDataSetChanged();
                        if (access$600 > 0) {
                            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -access$600);
                        }
                    }
                    if (ShareAlert.this.searchAdapter != null) {
                        ShareAlert.this.searchAdapter.searchDialogs(obj);
                    }
                }
            });
            this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return ShareAlert.SearchField.this.lambda$new$1$ShareAlert$SearchField(textView, i, keyEvent);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$ShareAlert$SearchField(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public /* synthetic */ boolean lambda$new$1$ShareAlert$SearchField(TextView textView, int i, KeyEvent keyEvent) {
            if (keyEvent == null) {
                return false;
            }
            if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
                return false;
            }
            AndroidUtilities.hideKeyboard(this.searchEditText);
            return false;
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }
    }

    public static ShareAlert createShareAlert(Context context, MessageObject messageObject, String str, boolean z, String str2, boolean z2) {
        ArrayList arrayList;
        if (messageObject != null) {
            arrayList = new ArrayList();
            arrayList.add(messageObject);
        } else {
            arrayList = null;
        }
        return new ShareAlert(context, arrayList, str, z, str2, z2);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ShareAlert(android.content.Context r23, java.util.ArrayList<org.telegram.messenger.MessageObject> r24, java.lang.String r25, boolean r26, java.lang.String r27, boolean r28) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r2 = r24
            r3 = r26
            r4 = 1
            r0.<init>(r1, r4)
            r5 = 2
            android.view.View[] r6 = new android.view.View[r5]
            r0.shadow = r6
            android.animation.AnimatorSet[] r6 = new android.animation.AnimatorSet[r5]
            r0.shadowAnimation = r6
            android.util.LongSparseArray r6 = new android.util.LongSparseArray
            r6.<init>()
            r0.selectedDialogs = r6
            android.graphics.RectF r6 = new android.graphics.RectF
            r6.<init>()
            r0.rect = r6
            android.graphics.Paint r6 = new android.graphics.Paint
            r6.<init>(r4)
            r0.paint = r6
            android.text.TextPaint r6 = new android.text.TextPaint
            r6.<init>(r4)
            r0.textPaint = r6
            android.content.res.Resources r6 = r23.getResources()
            r7 = 2131165917(0x7var_dd, float:1.7946065E38)
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            r0.shadowDrawable = r6
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "dialogBackground"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r9, r10)
            r6.setColorFilter(r7)
            r6 = r28
            r0.isFullscreen = r6
            r6 = r27
            r0.linkToCopy = r6
            r0.sendingMessageObjects = r2
            org.telegram.ui.Components.ShareAlert$ShareSearchAdapter r6 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter
            r6.<init>(r1)
            r0.searchAdapter = r6
            r0.isChannel = r3
            r6 = r25
            r0.sendingText = r6
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r0.sendingMessageObjects
            r7 = 0
            if (r6 == 0) goto L_0x0094
            int r6 = r6.size()
            r9 = 0
        L_0x0073:
            if (r9 >= r6) goto L_0x0094
            java.util.ArrayList<org.telegram.messenger.MessageObject> r10 = r0.sendingMessageObjects
            java.lang.Object r10 = r10.get(r9)
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            boolean r11 = r10.isPoll()
            if (r11 == 0) goto L_0x0091
            boolean r10 = r10.isPublicPoll()
            if (r10 == 0) goto L_0x008b
            r10 = 2
            goto L_0x008c
        L_0x008b:
            r10 = 1
        L_0x008c:
            r0.hasPoll = r10
            if (r10 != r5) goto L_0x0091
            goto L_0x0094
        L_0x0091:
            int r9 = r9 + 1
            goto L_0x0073
        L_0x0094:
            if (r3 == 0) goto L_0x00cf
            r0.loadingLink = r4
            org.telegram.tgnet.TLRPC$TL_channels_exportMessageLink r3 = new org.telegram.tgnet.TLRPC$TL_channels_exportMessageLink
            r3.<init>()
            java.lang.Object r6 = r2.get(r7)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            int r6 = r6.getId()
            r3.id = r6
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            java.lang.Object r2 = r2.get(r7)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.to_id
            int r2 = r2.channel_id
            org.telegram.tgnet.TLRPC$InputChannel r2 = r6.getInputChannel((int) r2)
            r3.channel = r2
            int r2 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.Components.-$$Lambda$ShareAlert$-zt1k1mc1Pf5YI45UjblkHkDCiI r6 = new org.telegram.ui.Components.-$$Lambda$ShareAlert$-zt1k1mc1Pf5YI45UjblkHkDCiI
            r6.<init>(r1)
            r2.sendRequest(r3, r6)
        L_0x00cf:
            org.telegram.ui.Components.ShareAlert$1 r2 = new org.telegram.ui.Components.ShareAlert$1
            r2.<init>(r1, r7)
            r0.containerView = r2
            r2.setWillNotDraw(r7)
            android.view.ViewGroup r3 = r0.containerView
            int r6 = r0.backgroundPaddingLeft
            r3.setPadding(r6, r7, r6, r7)
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            r0.frameLayout = r3
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r3.setBackgroundColor(r6)
            org.telegram.ui.Components.ShareAlert$SearchField r3 = new org.telegram.ui.Components.ShareAlert$SearchField
            r3.<init>(r1)
            android.widget.FrameLayout r6 = r0.frameLayout
            r9 = -1
            r10 = 51
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r9, r10)
            r6.addView(r3, r11)
            org.telegram.ui.Components.ShareAlert$2 r6 = new org.telegram.ui.Components.ShareAlert$2
            r6.<init>(r1)
            r0.gridView = r6
            r11 = 13
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r6.setTag(r11)
            org.telegram.ui.Components.RecyclerListView r6 = r0.gridView
            r11 = 1111490560(0x42400000, float:48.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.setPadding(r7, r7, r7, r12)
            org.telegram.ui.Components.RecyclerListView r6 = r0.gridView
            r6.setClipToPadding(r7)
            org.telegram.ui.Components.RecyclerListView r6 = r0.gridView
            androidx.recyclerview.widget.GridLayoutManager r12 = new androidx.recyclerview.widget.GridLayoutManager
            android.content.Context r13 = r22.getContext()
            r14 = 4
            r12.<init>(r13, r14)
            r0.layoutManager = r12
            r6.setLayoutManager(r12)
            androidx.recyclerview.widget.GridLayoutManager r6 = r0.layoutManager
            org.telegram.ui.Components.ShareAlert$3 r12 = new org.telegram.ui.Components.ShareAlert$3
            r12.<init>()
            r6.setSpanSizeLookup(r12)
            org.telegram.ui.Components.RecyclerListView r6 = r0.gridView
            r6.setHorizontalScrollBarEnabled(r7)
            org.telegram.ui.Components.RecyclerListView r6 = r0.gridView
            r6.setVerticalScrollBarEnabled(r7)
            org.telegram.ui.Components.RecyclerListView r6 = r0.gridView
            org.telegram.ui.Components.ShareAlert$4 r12 = new org.telegram.ui.Components.ShareAlert$4
            r12.<init>(r0)
            r6.addItemDecoration(r12)
            android.view.ViewGroup r6 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r12 = r0.gridView
            r15 = -1
            r16 = -1082130432(0xffffffffbvar_, float:-1.0)
            r17 = 51
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r6.addView(r12, r13)
            org.telegram.ui.Components.RecyclerListView r6 = r0.gridView
            org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter r12 = new org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter
            r12.<init>(r1)
            r0.listAdapter = r12
            r6.setAdapter(r12)
            org.telegram.ui.Components.RecyclerListView r6 = r0.gridView
            java.lang.String r12 = "dialogScrollGlow"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r6.setGlowColor(r12)
            org.telegram.ui.Components.RecyclerListView r6 = r0.gridView
            org.telegram.ui.Components.-$$Lambda$ShareAlert$W5k8hWsyZg9DfQTgiUPSh2_mvaQ r12 = new org.telegram.ui.Components.-$$Lambda$ShareAlert$W5k8hWsyZg9DfQTgiUPSh2_mvaQ
            r12.<init>(r3)
            r6.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r12)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            org.telegram.ui.Components.ShareAlert$5 r6 = new org.telegram.ui.Components.ShareAlert$5
            r6.<init>()
            r3.setOnScrollListener(r6)
            org.telegram.ui.Components.EmptyTextProgressView r3 = new org.telegram.ui.Components.EmptyTextProgressView
            r3.<init>(r1)
            r0.searchEmptyView = r3
            r3.setShowAtCenter(r4)
            org.telegram.ui.Components.EmptyTextProgressView r3 = r0.searchEmptyView
            r3.showTextView()
            org.telegram.ui.Components.EmptyTextProgressView r3 = r0.searchEmptyView
            r6 = 2131625933(0x7f0e07cd, float:1.8879088E38)
            java.lang.String r12 = "NoChats"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r3.setText(r6)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            org.telegram.ui.Components.EmptyTextProgressView r6 = r0.searchEmptyView
            r3.setEmptyView(r6)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.EmptyTextProgressView r6 = r0.searchEmptyView
            r19 = 1112539136(0x42500000, float:52.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r3.addView(r6, r12)
            android.widget.FrameLayout$LayoutParams r3 = new android.widget.FrameLayout$LayoutParams
            int r6 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r3.<init>(r9, r6, r10)
            r6 = 1114112000(0x42680000, float:58.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.topMargin = r6
            android.view.View[] r6 = r0.shadow
            android.view.View r12 = new android.view.View
            r12.<init>(r1)
            r6[r7] = r12
            android.view.View[] r6 = r0.shadow
            r6 = r6[r7]
            java.lang.String r12 = "dialogShadowLine"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r6.setBackgroundColor(r13)
            android.view.View[] r6 = r0.shadow
            r6 = r6[r7]
            r13 = 0
            r6.setAlpha(r13)
            android.view.View[] r6 = r0.shadow
            r6 = r6[r7]
            java.lang.Integer r15 = java.lang.Integer.valueOf(r4)
            r6.setTag(r15)
            android.view.ViewGroup r6 = r0.containerView
            android.view.View[] r15 = r0.shadow
            r15 = r15[r7]
            r6.addView(r15, r3)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.FrameLayout r6 = r0.frameLayout
            r15 = 58
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r15, r10)
            r3.addView(r6, r10)
            android.widget.FrameLayout$LayoutParams r3 = new android.widget.FrameLayout$LayoutParams
            int r6 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r10 = 83
            r3.<init>(r9, r6, r10)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r3.bottomMargin = r6
            android.view.View[] r6 = r0.shadow
            android.view.View r11 = new android.view.View
            r11.<init>(r1)
            r6[r4] = r11
            android.view.View[] r6 = r0.shadow
            r6 = r6[r4]
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r6.setBackgroundColor(r11)
            android.view.ViewGroup r6 = r0.containerView
            android.view.View[] r11 = r0.shadow
            r11 = r11[r4]
            r6.addView(r11, r3)
            boolean r3 = r0.isChannel
            r6 = 48
            java.lang.String r11 = "fonts/rmedium.ttf"
            if (r3 != 0) goto L_0x0255
            java.lang.String r3 = r0.linkToCopy
            if (r3 == 0) goto L_0x024d
            goto L_0x0255
        L_0x024d:
            android.view.View[] r3 = r0.shadow
            r3 = r3[r4]
            r3.setAlpha(r13)
            goto L_0x02c5
        L_0x0255:
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r0.pickerBottomLayout = r3
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            java.lang.String r15 = "listSelectorSDK21"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createSelectorWithBackgroundDrawable(r12, r15)
            r3.setBackgroundDrawable(r12)
            android.widget.TextView r3 = r0.pickerBottomLayout
            java.lang.String r12 = "dialogTextBlue2"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r3.setTextColor(r12)
            android.widget.TextView r3 = r0.pickerBottomLayout
            r12 = 1096810496(0x41600000, float:14.0)
            r3.setTextSize(r4, r12)
            android.widget.TextView r3 = r0.pickerBottomLayout
            r12 = 1099956224(0x41900000, float:18.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r3.setPadding(r15, r7, r12, r7)
            android.widget.TextView r3 = r0.pickerBottomLayout
            android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
            r3.setTypeface(r12)
            android.widget.TextView r3 = r0.pickerBottomLayout
            r12 = 17
            r3.setGravity(r12)
            android.widget.TextView r3 = r0.pickerBottomLayout
            r12 = 2131624881(0x7f0e03b1, float:1.8876954E38)
            java.lang.String r15 = "CopyLink"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            java.lang.String r12 = r12.toUpperCase()
            r3.setText(r12)
            android.widget.TextView r3 = r0.pickerBottomLayout
            org.telegram.ui.Components.-$$Lambda$ShareAlert$Y4YbUqwWCug5T2hNA6sxQRQyLJ0 r12 = new org.telegram.ui.Components.-$$Lambda$ShareAlert$Y4YbUqwWCug5T2hNA6sxQRQyLJ0
            r12.<init>()
            r3.setOnClickListener(r12)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.TextView r12 = r0.pickerBottomLayout
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r6, r10)
            r3.addView(r12, r15)
        L_0x02c5:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            r0.frameLayout2 = r3
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r3.setBackgroundColor(r8)
            android.widget.FrameLayout r3 = r0.frameLayout2
            r3.setAlpha(r13)
            android.widget.FrameLayout r3 = r0.frameLayout2
            r3.setVisibility(r14)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.FrameLayout r8 = r0.frameLayout2
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r6, r10)
            r3.addView(r8, r6)
            android.widget.FrameLayout r3 = r0.frameLayout2
            org.telegram.ui.Components.-$$Lambda$ShareAlert$e30oQsfKYZ5WdxxCC6xZg65lanw r6 = org.telegram.ui.Components.$$Lambda$ShareAlert$e30oQsfKYZ5WdxxCC6xZg65lanw.INSTANCE
            r3.setOnTouchListener(r6)
            org.telegram.ui.Components.EditTextEmoji r3 = new org.telegram.ui.Components.EditTextEmoji
            r6 = 0
            r3.<init>(r1, r2, r6, r4)
            r0.commentTextView = r3
            r2 = 2131626922(0x7f0e0baa, float:1.8881094E38)
            java.lang.String r6 = "ShareComment"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r3.setHint(r2)
            org.telegram.ui.Components.EditTextEmoji r2 = r0.commentTextView
            r2.onResume()
            org.telegram.ui.Components.EditTextEmoji r2 = r0.commentTextView
            org.telegram.ui.Components.EditTextBoldCursor r2 = r2.getEditText()
            r2.setMaxLines(r4)
            r2.setSingleLine(r4)
            android.widget.FrameLayout r2 = r0.frameLayout2
            org.telegram.ui.Components.EditTextEmoji r3 = r0.commentTextView
            r15 = -1
            r16 = -1082130432(0xffffffffbvar_, float:-1.0)
            r17 = 51
            r18 = 0
            r19 = 0
            r20 = 1118306304(0x42a80000, float:84.0)
            r21 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r2.addView(r3, r6)
            org.telegram.ui.Components.ShareAlert$6 r2 = new org.telegram.ui.Components.ShareAlert$6
            r2.<init>(r1)
            r0.writeButtonContainer = r2
            r2.setFocusable(r4)
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            r2.setFocusableInTouchMode(r4)
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            r2.setVisibility(r14)
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            r3 = 1045220557(0x3e4ccccd, float:0.2)
            r2.setScaleX(r3)
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            r2.setScaleY(r3)
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            r2.setAlpha(r13)
            android.view.ViewGroup r2 = r0.containerView
            android.widget.FrameLayout r6 = r0.writeButtonContainer
            r14 = 60
            r15 = 1114636288(0x42700000, float:60.0)
            r16 = 85
            r17 = 0
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
            r20 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r2.addView(r6, r8)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r6 = 1113587712(0x42600000, float:56.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
            java.lang.String r9 = "dialogFloatingButton"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            int r12 = android.os.Build.VERSION.SDK_INT
            r14 = 21
            if (r12 < r14) goto L_0x0382
            java.lang.String r9 = "dialogFloatingButtonPressed"
        L_0x0382:
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r8, r10, r9)
            int r9 = android.os.Build.VERSION.SDK_INT
            if (r9 >= r14) goto L_0x03ba
            android.content.res.Resources r9 = r23.getResources()
            r10 = 2131165412(0x7var_e4, float:1.794504E38)
            android.graphics.drawable.Drawable r9 = r9.getDrawable(r10)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
            r12 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.MULTIPLY
            r10.<init>(r12, r15)
            r9.setColorFilter(r10)
            org.telegram.ui.Components.CombinedDrawable r10 = new org.telegram.ui.Components.CombinedDrawable
            r10.<init>(r9, r8, r7, r7)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r10.setIconSize(r8, r9)
            r8 = r10
        L_0x03ba:
            r2.setBackgroundDrawable(r8)
            r8 = 2131165267(0x7var_, float:1.7944746E38)
            r2.setImageResource(r8)
            r2.setImportantForAccessibility(r5)
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "dialogFloatingIcon"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r8, r9)
            r2.setColorFilter(r5)
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r5)
            int r5 = android.os.Build.VERSION.SDK_INT
            if (r5 < r14) goto L_0x03e7
            org.telegram.ui.Components.ShareAlert$7 r5 = new org.telegram.ui.Components.ShareAlert$7
            r5.<init>(r0)
            r2.setOutlineProvider(r5)
        L_0x03e7:
            android.widget.FrameLayout r5 = r0.writeButtonContainer
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r14) goto L_0x03f2
            r8 = 56
            r15 = 56
            goto L_0x03f6
        L_0x03f2:
            r8 = 60
            r15 = 60
        L_0x03f6:
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r14) goto L_0x03fd
            r16 = 1113587712(0x42600000, float:56.0)
            goto L_0x0401
        L_0x03fd:
            r6 = 1114636288(0x42700000, float:60.0)
            r16 = 1114636288(0x42700000, float:60.0)
        L_0x0401:
            r17 = 51
            int r6 = android.os.Build.VERSION.SDK_INT
            if (r6 < r14) goto L_0x040c
            r6 = 1073741824(0x40000000, float:2.0)
            r18 = 1073741824(0x40000000, float:2.0)
            goto L_0x040e
        L_0x040c:
            r18 = 0
        L_0x040e:
            r19 = 0
            r20 = 0
            r21 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r5.addView(r2, r6)
            org.telegram.ui.Components.-$$Lambda$ShareAlert$7m2JWNxMvSKoRyVHsLYwsIAMWC0 r5 = new org.telegram.ui.Components.-$$Lambda$ShareAlert$7m2JWNxMvSKoRyVHsLYwsIAMWC0
            r5.<init>()
            r2.setOnClickListener(r5)
            android.text.TextPaint r2 = r0.textPaint
            r5 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r2.setTextSize(r5)
            android.text.TextPaint r2 = r0.textPaint
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
            r2.setTypeface(r5)
            org.telegram.ui.Components.ShareAlert$8 r2 = new org.telegram.ui.Components.ShareAlert$8
            r2.<init>(r1)
            r0.selectedCountView = r2
            r2.setAlpha(r13)
            android.view.View r1 = r0.selectedCountView
            r1.setScaleX(r3)
            android.view.View r1 = r0.selectedCountView
            r1.setScaleY(r3)
            android.view.ViewGroup r1 = r0.containerView
            android.view.View r2 = r0.selectedCountView
            r8 = 42
            r9 = 1103101952(0x41CLASSNAME, float:24.0)
            r10 = 85
            r11 = 0
            r12 = 0
            r13 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r14 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r1.addView(r2, r3)
            r0.updateSelectedCount(r7)
            boolean[] r1 = org.telegram.ui.DialogsActivity.dialogsLoaded
            int r2 = r0.currentAccount
            boolean r1 = r1[r2]
            if (r1 != 0) goto L_0x0486
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r2)
            r2 = 100
            r1.loadDialogs(r7, r7, r2, r4)
            int r1 = r0.currentAccount
            org.telegram.messenger.ContactsController r1 = org.telegram.messenger.ContactsController.getInstance(r1)
            r1.checkInviteText()
            boolean[] r1 = org.telegram.ui.DialogsActivity.dialogsLoaded
            int r2 = r0.currentAccount
            r1[r2] = r4
        L_0x0486:
            org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter r1 = r0.listAdapter
            java.util.ArrayList r1 = r1.dialogs
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x049d
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r1.addObserver(r0, r2)
        L_0x049d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.<init>(android.content.Context, java.util.ArrayList, java.lang.String, boolean, java.lang.String, boolean):void");
    }

    public /* synthetic */ void lambda$new$1$ShareAlert(Context context, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, context) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ Context f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ShareAlert.this.lambda$null$0$ShareAlert(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$ShareAlert(TLObject tLObject, Context context) {
        if (tLObject != null) {
            this.exportedMessageLink = (TLRPC$TL_exportedMessageLink) tLObject;
            if (this.copyLinkOnEnd) {
                copyLink(context);
            }
        }
        this.loadingLink = false;
    }

    public /* synthetic */ void lambda$new$2$ShareAlert(SearchField searchField, View view, int i) {
        TLRPC$Dialog tLRPC$Dialog;
        int i2;
        if (i >= 0) {
            RecyclerView.Adapter adapter = this.gridView.getAdapter();
            ShareDialogsAdapter shareDialogsAdapter = this.listAdapter;
            if (adapter == shareDialogsAdapter) {
                tLRPC$Dialog = shareDialogsAdapter.getItem(i);
            } else {
                tLRPC$Dialog = this.searchAdapter.getItem(i);
            }
            if (tLRPC$Dialog != null) {
                if (this.hasPoll != 0 && (i2 = (int) tLRPC$Dialog.id) < 0) {
                    TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i2));
                    boolean z = ChatObject.isChannel(chat) && this.hasPoll == 2 && !chat.megagroup;
                    if (z || !ChatObject.canSendPolls(chat)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(LocaleController.getString("SendMessageTitle", NUM));
                        if (z) {
                            builder.setMessage(LocaleController.getString("PublicPollCantForward", NUM));
                        } else if (ChatObject.isActionBannedByDefault(chat, 10)) {
                            builder.setMessage(LocaleController.getString("ErrorSendRestrictedPollsAll", NUM));
                        } else {
                            builder.setMessage(LocaleController.getString("ErrorSendRestrictedPolls", NUM));
                        }
                        builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                        builder.show();
                        return;
                    }
                }
                ShareDialogCell shareDialogCell = (ShareDialogCell) view;
                if (this.selectedDialogs.indexOfKey(tLRPC$Dialog.id) >= 0) {
                    this.selectedDialogs.remove(tLRPC$Dialog.id);
                    shareDialogCell.setChecked(false, true);
                    updateSelectedCount(1);
                    return;
                }
                this.selectedDialogs.put(tLRPC$Dialog.id, tLRPC$Dialog);
                shareDialogCell.setChecked(true, true);
                updateSelectedCount(2);
                int i3 = UserConfig.getInstance(this.currentAccount).clientUserId;
                if (this.gridView.getAdapter() == this.searchAdapter) {
                    TLRPC$Dialog tLRPC$Dialog2 = (TLRPC$Dialog) this.listAdapter.dialogsMap.get(tLRPC$Dialog.id);
                    if (tLRPC$Dialog2 == null) {
                        this.listAdapter.dialogsMap.put(tLRPC$Dialog.id, tLRPC$Dialog);
                        this.listAdapter.dialogs.add(this.listAdapter.dialogs.isEmpty() ^ true ? 1 : 0, tLRPC$Dialog);
                    } else if (tLRPC$Dialog2.id != ((long) i3)) {
                        this.listAdapter.dialogs.remove(tLRPC$Dialog2);
                        this.listAdapter.dialogs.add(this.listAdapter.dialogs.isEmpty() ^ true ? 1 : 0, tLRPC$Dialog2);
                    }
                    searchField.searchEditText.setText("");
                    this.gridView.setAdapter(this.listAdapter);
                    searchField.hideKeyboard();
                }
            }
        }
    }

    public /* synthetic */ void lambda$new$3$ShareAlert(View view) {
        if (this.selectedDialogs.size() != 0) {
            return;
        }
        if (this.isChannel || this.linkToCopy != null) {
            if (this.linkToCopy != null || !this.loadingLink) {
                copyLink(getContext());
            } else {
                this.copyLinkOnEnd = true;
                Toast.makeText(getContext(), LocaleController.getString("Loading", NUM), 0).show();
            }
            dismiss();
        }
    }

    public /* synthetic */ void lambda$new$5$ShareAlert(View view) {
        int i = 0;
        int i2 = 0;
        while (i2 < this.selectedDialogs.size()) {
            if (!AlertsCreator.checkSlowMode(getContext(), this.currentAccount, this.selectedDialogs.keyAt(i2), this.frameLayout2.getTag() != null && this.commentTextView.length() > 0)) {
                i2++;
            } else {
                return;
            }
        }
        if (this.sendingMessageObjects != null) {
            while (i < this.selectedDialogs.size()) {
                long keyAt = this.selectedDialogs.keyAt(i);
                if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), keyAt, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingMessageObjects, keyAt, true, 0);
                i++;
            }
        } else if (this.sendingText != null) {
            while (i < this.selectedDialogs.size()) {
                long keyAt2 = this.selectedDialogs.keyAt(i);
                if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), keyAt2, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingText, keyAt2, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                i++;
            }
        }
        ShareAlertDelegate shareAlertDelegate = this.delegate;
        if (shareAlertDelegate != null) {
            shareAlertDelegate.didShare();
        }
        dismiss();
    }

    /* access modifiers changed from: private */
    public int getCurrentTop() {
        if (this.gridView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View childAt = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(childAt);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.gridView.getPaddingTop();
        if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
            i = childAt.getTop();
        }
        return paddingTop - i;
    }

    public void dismissInternal() {
        super.dismissInternal();
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public void onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            super.onBackPressed();
        } else {
            this.commentTextView.hidePopup(true);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.dialogsNeedReload) {
            ShareDialogsAdapter shareDialogsAdapter = this.listAdapter;
            if (shareDialogsAdapter != null) {
                shareDialogsAdapter.fetchDialogs();
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void updateLayout() {
        if (this.gridView.getChildCount() > 0) {
            View childAt = this.gridView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(childAt);
            int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
            int i = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
            if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                runShadowAnimation(0, true);
                top = i;
            } else {
                runShadowAnimation(0, false);
            }
            if (this.scrollOffsetY != top) {
                RecyclerListView recyclerListView = this.gridView;
                this.scrollOffsetY = top;
                recyclerListView.setTopGlowOffset(top);
                this.frameLayout.setTranslationY((float) this.scrollOffsetY);
                this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
                this.containerView.invalidate();
            }
        }
    }

    private void runShadowAnimation(final int i, final boolean z) {
        if ((z && this.shadow[i].getTag() != null) || (!z && this.shadow[i].getTag() == null)) {
            this.shadow[i].setTag(z ? null : 1);
            if (z) {
                this.shadow[i].setVisibility(0);
            }
            AnimatorSet[] animatorSetArr = this.shadowAnimation;
            if (animatorSetArr[i] != null) {
                animatorSetArr[i].cancel();
            }
            this.shadowAnimation[i] = new AnimatorSet();
            AnimatorSet animatorSet2 = this.shadowAnimation[i];
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow[i];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation[i].setDuration(150);
            this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ShareAlert.this.shadowAnimation[i] != null && ShareAlert.this.shadowAnimation[i].equals(animator)) {
                        if (!z) {
                            ShareAlert.this.shadow[i].setVisibility(4);
                        }
                        ShareAlert.this.shadowAnimation[i] = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ShareAlert.this.shadowAnimation[i] != null && ShareAlert.this.shadowAnimation[i].equals(animator)) {
                        ShareAlert.this.shadowAnimation[i] = null;
                    }
                }
            });
            this.shadowAnimation[i].start();
        }
    }

    private void copyLink(Context context) {
        if (this.exportedMessageLink != null || this.linkToCopy != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.linkToCopy != null ? this.linkToCopy : this.exportedMessageLink.link));
                if (this.exportedMessageLink == null || !this.exportedMessageLink.link.contains("/c/")) {
                    Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("LinkCopied", NUM), 0).show();
                } else {
                    Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("LinkCopiedPrivate", NUM), 0).show();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private boolean showCommentTextView(final boolean z) {
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.frameLayout2.setTag(z ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (z) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        TextView textView = this.pickerBottomLayout;
        if (textView != null) {
            ViewCompat.setImportantForAccessibility(textView, z ? 4 : 1);
        }
        this.animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        FrameLayout frameLayout3 = this.frameLayout2;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout3, property, fArr));
        FrameLayout frameLayout4 = this.writeButtonContainer;
        Property property2 = View.SCALE_X;
        float[] fArr2 = new float[1];
        float f2 = 0.2f;
        fArr2[0] = z ? 1.0f : 0.2f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout4, property2, fArr2));
        FrameLayout frameLayout5 = this.writeButtonContainer;
        Property property3 = View.SCALE_Y;
        float[] fArr3 = new float[1];
        fArr3[0] = z ? 1.0f : 0.2f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout5, property3, fArr3));
        FrameLayout frameLayout6 = this.writeButtonContainer;
        Property property4 = View.ALPHA;
        float[] fArr4 = new float[1];
        fArr4[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout6, property4, fArr4));
        View view = this.selectedCountView;
        Property property5 = View.SCALE_X;
        float[] fArr5 = new float[1];
        fArr5[0] = z ? 1.0f : 0.2f;
        arrayList.add(ObjectAnimator.ofFloat(view, property5, fArr5));
        View view2 = this.selectedCountView;
        Property property6 = View.SCALE_Y;
        float[] fArr6 = new float[1];
        if (z) {
            f2 = 1.0f;
        }
        fArr6[0] = f2;
        arrayList.add(ObjectAnimator.ofFloat(view2, property6, fArr6));
        View view3 = this.selectedCountView;
        Property property7 = View.ALPHA;
        float[] fArr7 = new float[1];
        fArr7[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(view3, property7, fArr7));
        TextView textView2 = this.pickerBottomLayout;
        if (textView2 == null || textView2.getVisibility() != 0) {
            View view4 = this.shadow[1];
            Property property8 = View.ALPHA;
            float[] fArr8 = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr8[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(view4, property8, fArr8));
        }
        this.animatorSet.playTogether(arrayList);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(180);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(ShareAlert.this.animatorSet)) {
                    if (!z) {
                        ShareAlert.this.frameLayout2.setVisibility(4);
                        ShareAlert.this.writeButtonContainer.setVisibility(4);
                    }
                    AnimatorSet unused = ShareAlert.this.animatorSet = null;
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (animator.equals(ShareAlert.this.animatorSet)) {
                    AnimatorSet unused = ShareAlert.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
        return true;
    }

    public void updateSelectedCount(int i) {
        if (this.selectedDialogs.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true) || i == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            return;
        }
        this.selectedCountView.setPivotX((float) AndroidUtilities.dp(21.0f));
        this.selectedCountView.setPivotY((float) AndroidUtilities.dp(12.0f));
        AnimatorSet animatorSet2 = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.selectedCountView;
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.1f;
        fArr[0] = i == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        View view2 = this.selectedCountView;
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (i != 1) {
            f = 0.9f;
        }
        fArr2[0] = f;
        fArr2[1] = 1.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(view2, property2, fArr2);
        animatorSet2.playTogether(animatorArr);
        animatorSet2.setInterpolator(new OvershootInterpolator());
        animatorSet2.setDuration(180);
        animatorSet2.start();
    }

    public void dismiss() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
        }
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }

    private class ShareDialogsAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        /* access modifiers changed from: private */
        public ArrayList<TLRPC$Dialog> dialogs = new ArrayList<>();
        /* access modifiers changed from: private */
        public LongSparseArray<TLRPC$Dialog> dialogsMap = new LongSparseArray<>();

        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        public ShareDialogsAdapter(Context context2) {
            this.context = context2;
            fetchDialogs();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:8:0x006c, code lost:
            r5 = r4.id;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void fetchDialogs() {
            /*
                r9 = this;
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r9.dialogs
                r0.clear()
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r9.dialogsMap
                r0.clear()
                org.telegram.ui.Components.ShareAlert r0 = org.telegram.ui.Components.ShareAlert.this
                int r0 = r0.currentAccount
                org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
                int r0 = r0.clientUserId
                org.telegram.ui.Components.ShareAlert r1 = org.telegram.ui.Components.ShareAlert.this
                int r1 = r1.currentAccount
                org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r1.dialogsForward
                boolean r1 = r1.isEmpty()
                r2 = 0
                if (r1 != 0) goto L_0x0047
                org.telegram.ui.Components.ShareAlert r1 = org.telegram.ui.Components.ShareAlert.this
                int r1 = r1.currentAccount
                org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r1.dialogsForward
                java.lang.Object r1 = r1.get(r2)
                org.telegram.tgnet.TLRPC$Dialog r1 = (org.telegram.tgnet.TLRPC$Dialog) r1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r3 = r9.dialogs
                r3.add(r1)
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r3 = r9.dialogsMap
                long r4 = r1.id
                r3.put(r4, r1)
            L_0x0047:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                org.telegram.ui.Components.ShareAlert r3 = org.telegram.ui.Components.ShareAlert.this
                int r3 = r3.currentAccount
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
                java.util.ArrayList r3 = r3.getAllDialogs()
            L_0x005a:
                int r4 = r3.size()
                if (r2 >= r4) goto L_0x00da
                java.lang.Object r4 = r3.get(r2)
                org.telegram.tgnet.TLRPC$Dialog r4 = (org.telegram.tgnet.TLRPC$Dialog) r4
                boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_dialog
                if (r5 != 0) goto L_0x006c
                goto L_0x00d7
            L_0x006c:
                long r5 = r4.id
                int r7 = (int) r5
                if (r7 != r0) goto L_0x0072
                goto L_0x00d7
            L_0x0072:
                r8 = 32
                long r5 = r5 >> r8
                int r6 = (int) r5
                if (r7 == 0) goto L_0x00d7
                r5 = 1
                if (r6 == r5) goto L_0x00d7
                if (r7 <= 0) goto L_0x0092
                int r6 = r4.folder_id
                if (r6 != r5) goto L_0x0085
                r1.add(r4)
                goto L_0x008a
            L_0x0085:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r5 = r9.dialogs
                r5.add(r4)
            L_0x008a:
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r9.dialogsMap
                long r6 = r4.id
                r5.put(r6, r4)
                goto L_0x00d7
            L_0x0092:
                org.telegram.ui.Components.ShareAlert r6 = org.telegram.ui.Components.ShareAlert.this
                int r6 = r6.currentAccount
                org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
                int r7 = -r7
                java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
                org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
                if (r6 == 0) goto L_0x00d7
                boolean r7 = org.telegram.messenger.ChatObject.isNotInChat(r6)
                if (r7 != 0) goto L_0x00d7
                boolean r7 = org.telegram.messenger.ChatObject.isChannel(r6)
                if (r7 == 0) goto L_0x00c3
                boolean r7 = r6.creator
                if (r7 != 0) goto L_0x00c3
                org.telegram.tgnet.TLRPC$TL_chatAdminRights r7 = r6.admin_rights
                if (r7 == 0) goto L_0x00bf
                boolean r7 = r7.post_messages
                if (r7 != 0) goto L_0x00c3
            L_0x00bf:
                boolean r6 = r6.megagroup
                if (r6 == 0) goto L_0x00d7
            L_0x00c3:
                int r6 = r4.folder_id
                if (r6 != r5) goto L_0x00cb
                r1.add(r4)
                goto L_0x00d0
            L_0x00cb:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r5 = r9.dialogs
                r5.add(r4)
            L_0x00d0:
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r9.dialogsMap
                long r6 = r4.id
                r5.put(r6, r4)
            L_0x00d7:
                int r2 = r2 + 1
                goto L_0x005a
            L_0x00da:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r9.dialogs
                r0.addAll(r1)
                r9.notifyDataSetChanged()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareDialogsAdapter.fetchDialogs():void");
        }

        public int getItemCount() {
            int size = this.dialogs.size();
            return size != 0 ? size + 1 : size;
        }

        public TLRPC$Dialog getItem(int i) {
            int i2 = i - 1;
            if (i2 < 0 || i2 >= this.dialogs.size()) {
                return null;
            }
            return this.dialogs.get(i2);
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new View(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            } else {
                view = new ShareDialogCell(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) viewHolder.itemView;
                TLRPC$Dialog item = getItem(i);
                shareDialogCell.setDialog((int) item.id, ShareAlert.this.selectedDialogs.indexOfKey(item.id) >= 0, (CharSequence) null);
            }
        }
    }

    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int lastSearchId;
        private String lastSearchText;
        private ArrayList<DialogSearchResult> searchResult = new ArrayList<>();
        private Runnable searchRunnable;

        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        private class DialogSearchResult {
            public int date;
            public TLRPC$Dialog dialog;
            public CharSequence name;
            public TLObject object;

            private DialogSearchResult(ShareSearchAdapter shareSearchAdapter) {
                this.dialog = new TLRPC$TL_dialog();
            }
        }

        public ShareSearchAdapter(Context context2) {
            this.context = context2;
        }

        /* access modifiers changed from: private */
        /* renamed from: searchDialogsInternal */
        public void lambda$searchDialogs$3$ShareAlert$ShareSearchAdapter(String str, int i) {
            MessagesStorage.getInstance(ShareAlert.this.currentAccount).getStorageQueue().postRunnable(new Runnable(str, i) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* JADX WARNING: type inference failed for: r11v24 */
        /* JADX WARNING: type inference failed for: r11v26 */
        /* JADX WARNING: type inference failed for: r11v30 */
        /* JADX WARNING: type inference failed for: r11v32 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:176:0x040b A[Catch:{ Exception -> 0x0427 }, LOOP:7: B:148:0x0355->B:176:0x040b, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:194:0x0164 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:215:0x039f A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x01d5 A[Catch:{ Exception -> 0x0427 }, LOOP:2: B:46:0x0111->B:77:0x01d5, LOOP_END] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(java.lang.String r21, int r22) {
            /*
                r20 = this;
                r1 = r20
                java.lang.String r0 = r21.trim()     // Catch:{ Exception -> 0x0427 }
                java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0427 }
                int r2 = r0.length()     // Catch:{ Exception -> 0x0427 }
                r3 = -1
                if (r2 != 0) goto L_0x001e
                r1.lastSearchId = r3     // Catch:{ Exception -> 0x0427 }
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0427 }
                r0.<init>()     // Catch:{ Exception -> 0x0427 }
                int r2 = r1.lastSearchId     // Catch:{ Exception -> 0x0427 }
                r1.updateSearchResults(r0, r2)     // Catch:{ Exception -> 0x0427 }
                return
            L_0x001e:
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0427 }
                java.lang.String r2 = r2.getTranslitString(r0)     // Catch:{ Exception -> 0x0427 }
                boolean r4 = r0.equals(r2)     // Catch:{ Exception -> 0x0427 }
                r5 = 0
                if (r4 != 0) goto L_0x0033
                int r4 = r2.length()     // Catch:{ Exception -> 0x0427 }
                if (r4 != 0) goto L_0x0034
            L_0x0033:
                r2 = r5
            L_0x0034:
                r4 = 1
                r6 = 0
                if (r2 == 0) goto L_0x003a
                r7 = 1
                goto L_0x003b
            L_0x003a:
                r7 = 0
            L_0x003b:
                int r7 = r7 + r4
                java.lang.String[] r8 = new java.lang.String[r7]     // Catch:{ Exception -> 0x0427 }
                r8[r6] = r0     // Catch:{ Exception -> 0x0427 }
                if (r2 == 0) goto L_0x0044
                r8[r4] = r2     // Catch:{ Exception -> 0x0427 }
            L_0x0044:
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0427 }
                r0.<init>()     // Catch:{ Exception -> 0x0427 }
                java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0427 }
                r2.<init>()     // Catch:{ Exception -> 0x0427 }
                android.util.LongSparseArray r9 = new android.util.LongSparseArray     // Catch:{ Exception -> 0x0427 }
                r9.<init>()     // Catch:{ Exception -> 0x0427 }
                org.telegram.ui.Components.ShareAlert r10 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0427 }
                int r10 = r10.currentAccount     // Catch:{ Exception -> 0x0427 }
                org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r10)     // Catch:{ Exception -> 0x0427 }
                org.telegram.SQLite.SQLiteDatabase r10 = r10.getDatabase()     // Catch:{ Exception -> 0x0427 }
                java.lang.String r11 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400"
                java.lang.Object[] r12 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0427 }
                org.telegram.SQLite.SQLiteCursor r10 = r10.queryFinalized(r11, r12)     // Catch:{ Exception -> 0x0427 }
            L_0x0069:
                boolean r11 = r10.next()     // Catch:{ Exception -> 0x0427 }
                if (r11 == 0) goto L_0x00b1
                long r11 = r10.longValue(r6)     // Catch:{ Exception -> 0x0427 }
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$DialogSearchResult r13 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$DialogSearchResult     // Catch:{ Exception -> 0x0427 }
                r13.<init>()     // Catch:{ Exception -> 0x0427 }
                int r14 = r10.intValue(r4)     // Catch:{ Exception -> 0x0427 }
                r13.date = r14     // Catch:{ Exception -> 0x0427 }
                r9.put(r11, r13)     // Catch:{ Exception -> 0x0427 }
                int r13 = (int) r11     // Catch:{ Exception -> 0x0427 }
                r14 = 32
                long r11 = r11 >> r14
                int r12 = (int) r11     // Catch:{ Exception -> 0x0427 }
                if (r13 == 0) goto L_0x0069
                if (r12 == r4) goto L_0x0069
                if (r13 <= 0) goto L_0x009e
                java.lang.Integer r11 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x0427 }
                boolean r11 = r0.contains(r11)     // Catch:{ Exception -> 0x0427 }
                if (r11 != 0) goto L_0x0069
                java.lang.Integer r11 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x0427 }
                r0.add(r11)     // Catch:{ Exception -> 0x0427 }
                goto L_0x0069
            L_0x009e:
                int r11 = -r13
                java.lang.Integer r12 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x0427 }
                boolean r12 = r2.contains(r12)     // Catch:{ Exception -> 0x0427 }
                if (r12 != 0) goto L_0x0069
                java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x0427 }
                r2.add(r11)     // Catch:{ Exception -> 0x0427 }
                goto L_0x0069
            L_0x00b1:
                r10.dispose()     // Catch:{ Exception -> 0x0427 }
                boolean r10 = r0.isEmpty()     // Catch:{ Exception -> 0x0427 }
                java.lang.String r11 = ";;;"
                java.lang.String r12 = ","
                java.lang.String r13 = "@"
                java.lang.String r15 = " "
                if (r10 != 0) goto L_0x01f4
                org.telegram.ui.Components.ShareAlert r10 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0427 }
                int r10 = r10.currentAccount     // Catch:{ Exception -> 0x0427 }
                org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r10)     // Catch:{ Exception -> 0x0427 }
                org.telegram.SQLite.SQLiteDatabase r10 = r10.getDatabase()     // Catch:{ Exception -> 0x0427 }
                java.util.Locale r5 = java.util.Locale.US     // Catch:{ Exception -> 0x0427 }
                java.lang.String r3 = "SELECT data, status, name FROM users WHERE uid IN(%s)"
                java.lang.Object[] r14 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x0427 }
                java.lang.String r0 = android.text.TextUtils.join(r12, r0)     // Catch:{ Exception -> 0x0427 }
                r14[r6] = r0     // Catch:{ Exception -> 0x0427 }
                java.lang.String r0 = java.lang.String.format(r5, r3, r14)     // Catch:{ Exception -> 0x0427 }
                java.lang.Object[] r3 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0427 }
                org.telegram.SQLite.SQLiteCursor r0 = r10.queryFinalized(r0, r3)     // Catch:{ Exception -> 0x0427 }
                r3 = 0
            L_0x00e7:
                boolean r5 = r0.next()     // Catch:{ Exception -> 0x0427 }
                if (r5 == 0) goto L_0x01ec
                r5 = 2
                java.lang.String r10 = r0.stringValue(r5)     // Catch:{ Exception -> 0x0427 }
                org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0427 }
                java.lang.String r5 = r5.getTranslitString(r10)     // Catch:{ Exception -> 0x0427 }
                boolean r14 = r10.equals(r5)     // Catch:{ Exception -> 0x0427 }
                if (r14 == 0) goto L_0x0101
                r5 = 0
            L_0x0101:
                int r14 = r10.lastIndexOf(r11)     // Catch:{ Exception -> 0x0427 }
                r4 = -1
                if (r14 == r4) goto L_0x010f
                int r14 = r14 + 3
                java.lang.String r4 = r10.substring(r14)     // Catch:{ Exception -> 0x0427 }
                goto L_0x0110
            L_0x010f:
                r4 = 0
            L_0x0110:
                r14 = 0
            L_0x0111:
                if (r6 >= r7) goto L_0x01e0
                r17 = r14
                r14 = r8[r6]     // Catch:{ Exception -> 0x0427 }
                boolean r18 = r10.startsWith(r14)     // Catch:{ Exception -> 0x0427 }
                if (r18 != 0) goto L_0x015f
                r18 = r11
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0427 }
                r11.<init>()     // Catch:{ Exception -> 0x0427 }
                r11.append(r15)     // Catch:{ Exception -> 0x0427 }
                r11.append(r14)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0427 }
                boolean r11 = r10.contains(r11)     // Catch:{ Exception -> 0x0427 }
                if (r11 != 0) goto L_0x0161
                if (r5 == 0) goto L_0x0152
                boolean r11 = r5.startsWith(r14)     // Catch:{ Exception -> 0x0427 }
                if (r11 != 0) goto L_0x0161
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0427 }
                r11.<init>()     // Catch:{ Exception -> 0x0427 }
                r11.append(r15)     // Catch:{ Exception -> 0x0427 }
                r11.append(r14)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0427 }
                boolean r11 = r5.contains(r11)     // Catch:{ Exception -> 0x0427 }
                if (r11 == 0) goto L_0x0152
                goto L_0x0161
            L_0x0152:
                if (r4 == 0) goto L_0x015c
                boolean r11 = r4.startsWith(r14)     // Catch:{ Exception -> 0x0427 }
                if (r11 == 0) goto L_0x015c
                r11 = 2
                goto L_0x0162
            L_0x015c:
                r11 = r17
                goto L_0x0162
            L_0x015f:
                r18 = r11
            L_0x0161:
                r11 = 1
            L_0x0162:
                if (r11 == 0) goto L_0x01d5
                r4 = 0
                org.telegram.tgnet.NativeByteBuffer r5 = r0.byteBufferValue(r4)     // Catch:{ Exception -> 0x0427 }
                if (r5 == 0) goto L_0x01d2
                int r6 = r5.readInt32(r4)     // Catch:{ Exception -> 0x0427 }
                org.telegram.tgnet.TLRPC$User r6 = org.telegram.tgnet.TLRPC$User.TLdeserialize(r5, r6, r4)     // Catch:{ Exception -> 0x0427 }
                r5.reuse()     // Catch:{ Exception -> 0x0427 }
                int r4 = r6.id     // Catch:{ Exception -> 0x0427 }
                long r4 = (long) r4     // Catch:{ Exception -> 0x0427 }
                java.lang.Object r4 = r9.get(r4)     // Catch:{ Exception -> 0x0427 }
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$DialogSearchResult r4 = (org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r4     // Catch:{ Exception -> 0x0427 }
                org.telegram.tgnet.TLRPC$UserStatus r5 = r6.status     // Catch:{ Exception -> 0x0427 }
                if (r5 == 0) goto L_0x018f
                org.telegram.tgnet.TLRPC$UserStatus r5 = r6.status     // Catch:{ Exception -> 0x0427 }
                r19 = r9
                r10 = 1
                int r9 = r0.intValue(r10)     // Catch:{ Exception -> 0x0427 }
                r5.expires = r9     // Catch:{ Exception -> 0x0427 }
                goto L_0x0191
            L_0x018f:
                r19 = r9
            L_0x0191:
                r5 = 1
                if (r11 != r5) goto L_0x019f
                java.lang.String r5 = r6.first_name     // Catch:{ Exception -> 0x0427 }
                java.lang.String r9 = r6.last_name     // Catch:{ Exception -> 0x0427 }
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r9, r14)     // Catch:{ Exception -> 0x0427 }
                r4.name = r5     // Catch:{ Exception -> 0x0427 }
                goto L_0x01c6
            L_0x019f:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0427 }
                r5.<init>()     // Catch:{ Exception -> 0x0427 }
                r5.append(r13)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r9 = r6.username     // Catch:{ Exception -> 0x0427 }
                r5.append(r9)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0427 }
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0427 }
                r9.<init>()     // Catch:{ Exception -> 0x0427 }
                r9.append(r13)     // Catch:{ Exception -> 0x0427 }
                r9.append(r14)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0427 }
                r10 = 0
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r10, r9)     // Catch:{ Exception -> 0x0427 }
                r4.name = r5     // Catch:{ Exception -> 0x0427 }
            L_0x01c6:
                r4.object = r6     // Catch:{ Exception -> 0x0427 }
                org.telegram.tgnet.TLRPC$Dialog r4 = r4.dialog     // Catch:{ Exception -> 0x0427 }
                int r5 = r6.id     // Catch:{ Exception -> 0x0427 }
                long r5 = (long) r5     // Catch:{ Exception -> 0x0427 }
                r4.id = r5     // Catch:{ Exception -> 0x0427 }
                int r3 = r3 + 1
                goto L_0x01e4
            L_0x01d2:
                r19 = r9
                goto L_0x01e4
            L_0x01d5:
                r17 = r4
                r19 = r9
                int r6 = r6 + 1
                r14 = r11
                r11 = r18
                goto L_0x0111
            L_0x01e0:
                r19 = r9
                r18 = r11
            L_0x01e4:
                r11 = r18
                r9 = r19
                r4 = 1
                r6 = 0
                goto L_0x00e7
            L_0x01ec:
                r19 = r9
                r18 = r11
                r0.dispose()     // Catch:{ Exception -> 0x0427 }
                goto L_0x01f9
            L_0x01f4:
                r19 = r9
                r18 = r11
                r3 = 0
            L_0x01f9:
                boolean r0 = r2.isEmpty()     // Catch:{ Exception -> 0x0427 }
                if (r0 != 0) goto L_0x02e0
                org.telegram.ui.Components.ShareAlert r0 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0427 }
                int r0 = r0.currentAccount     // Catch:{ Exception -> 0x0427 }
                org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x0427 }
                org.telegram.SQLite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x0427 }
                java.util.Locale r4 = java.util.Locale.US     // Catch:{ Exception -> 0x0427 }
                java.lang.String r5 = "SELECT data, name FROM chats WHERE uid IN(%s)"
                r6 = 1
                java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0427 }
                java.lang.String r2 = android.text.TextUtils.join(r12, r2)     // Catch:{ Exception -> 0x0427 }
                r6 = 0
                r9[r6] = r2     // Catch:{ Exception -> 0x0427 }
                java.lang.String r2 = java.lang.String.format(r4, r5, r9)     // Catch:{ Exception -> 0x0427 }
                java.lang.Object[] r4 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0427 }
                org.telegram.SQLite.SQLiteCursor r0 = r0.queryFinalized(r2, r4)     // Catch:{ Exception -> 0x0427 }
            L_0x0225:
                boolean r2 = r0.next()     // Catch:{ Exception -> 0x0427 }
                if (r2 == 0) goto L_0x02da
                r2 = 1
                java.lang.String r4 = r0.stringValue(r2)     // Catch:{ Exception -> 0x0427 }
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0427 }
                java.lang.String r2 = r2.getTranslitString(r4)     // Catch:{ Exception -> 0x0427 }
                boolean r5 = r4.equals(r2)     // Catch:{ Exception -> 0x0427 }
                if (r5 == 0) goto L_0x023f
                r2 = 0
            L_0x023f:
                r5 = 0
            L_0x0240:
                if (r5 >= r7) goto L_0x02d4
                r6 = r8[r5]     // Catch:{ Exception -> 0x0427 }
                boolean r9 = r4.startsWith(r6)     // Catch:{ Exception -> 0x0427 }
                if (r9 != 0) goto L_0x0280
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0427 }
                r9.<init>()     // Catch:{ Exception -> 0x0427 }
                r9.append(r15)     // Catch:{ Exception -> 0x0427 }
                r9.append(r6)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0427 }
                boolean r9 = r4.contains(r9)     // Catch:{ Exception -> 0x0427 }
                if (r9 != 0) goto L_0x0280
                if (r2 == 0) goto L_0x027d
                boolean r9 = r2.startsWith(r6)     // Catch:{ Exception -> 0x0427 }
                if (r9 != 0) goto L_0x0280
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0427 }
                r9.<init>()     // Catch:{ Exception -> 0x0427 }
                r9.append(r15)     // Catch:{ Exception -> 0x0427 }
                r9.append(r6)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0427 }
                boolean r9 = r2.contains(r9)     // Catch:{ Exception -> 0x0427 }
                if (r9 == 0) goto L_0x027d
                goto L_0x0280
            L_0x027d:
                int r5 = r5 + 1
                goto L_0x0240
            L_0x0280:
                r2 = 0
                org.telegram.tgnet.NativeByteBuffer r4 = r0.byteBufferValue(r2)     // Catch:{ Exception -> 0x0427 }
                if (r4 == 0) goto L_0x02d4
                int r5 = r4.readInt32(r2)     // Catch:{ Exception -> 0x0427 }
                org.telegram.tgnet.TLRPC$Chat r5 = org.telegram.tgnet.TLRPC$Chat.TLdeserialize(r4, r5, r2)     // Catch:{ Exception -> 0x0427 }
                r4.reuse()     // Catch:{ Exception -> 0x0427 }
                if (r5 == 0) goto L_0x02d4
                boolean r2 = org.telegram.messenger.ChatObject.isNotInChat(r5)     // Catch:{ Exception -> 0x0427 }
                if (r2 != 0) goto L_0x02d4
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r5)     // Catch:{ Exception -> 0x0427 }
                if (r2 == 0) goto L_0x02b2
                boolean r2 = r5.creator     // Catch:{ Exception -> 0x0427 }
                if (r2 != 0) goto L_0x02b2
                org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r5.admin_rights     // Catch:{ Exception -> 0x0427 }
                if (r2 == 0) goto L_0x02ae
                org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r5.admin_rights     // Catch:{ Exception -> 0x0427 }
                boolean r2 = r2.post_messages     // Catch:{ Exception -> 0x0427 }
                if (r2 != 0) goto L_0x02b2
            L_0x02ae:
                boolean r2 = r5.megagroup     // Catch:{ Exception -> 0x0427 }
                if (r2 == 0) goto L_0x02d4
            L_0x02b2:
                int r2 = r5.id     // Catch:{ Exception -> 0x0427 }
                long r9 = (long) r2     // Catch:{ Exception -> 0x0427 }
                long r9 = -r9
                r2 = r19
                java.lang.Object r4 = r2.get(r9)     // Catch:{ Exception -> 0x0427 }
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$DialogSearchResult r4 = (org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r4     // Catch:{ Exception -> 0x0427 }
                java.lang.String r9 = r5.title     // Catch:{ Exception -> 0x0427 }
                r10 = 0
                java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r10, r6)     // Catch:{ Exception -> 0x0427 }
                r4.name = r6     // Catch:{ Exception -> 0x0427 }
                r4.object = r5     // Catch:{ Exception -> 0x0427 }
                org.telegram.tgnet.TLRPC$Dialog r4 = r4.dialog     // Catch:{ Exception -> 0x0427 }
                int r5 = r5.id     // Catch:{ Exception -> 0x0427 }
                int r5 = -r5
                long r5 = (long) r5     // Catch:{ Exception -> 0x0427 }
                r4.id = r5     // Catch:{ Exception -> 0x0427 }
                int r3 = r3 + 1
                goto L_0x02d6
            L_0x02d4:
                r2 = r19
            L_0x02d6:
                r19 = r2
                goto L_0x0225
            L_0x02da:
                r2 = r19
                r0.dispose()     // Catch:{ Exception -> 0x0427 }
                goto L_0x02e2
            L_0x02e0:
                r2 = r19
            L_0x02e2:
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0427 }
                r0.<init>(r3)     // Catch:{ Exception -> 0x0427 }
                r3 = 0
            L_0x02e8:
                int r4 = r2.size()     // Catch:{ Exception -> 0x0427 }
                if (r3 >= r4) goto L_0x0302
                java.lang.Object r4 = r2.valueAt(r3)     // Catch:{ Exception -> 0x0427 }
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$DialogSearchResult r4 = (org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r4     // Catch:{ Exception -> 0x0427 }
                org.telegram.tgnet.TLObject r5 = r4.object     // Catch:{ Exception -> 0x0427 }
                if (r5 == 0) goto L_0x02ff
                java.lang.CharSequence r5 = r4.name     // Catch:{ Exception -> 0x0427 }
                if (r5 == 0) goto L_0x02ff
                r0.add(r4)     // Catch:{ Exception -> 0x0427 }
            L_0x02ff:
                int r3 = r3 + 1
                goto L_0x02e8
            L_0x0302:
                org.telegram.ui.Components.ShareAlert r3 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0427 }
                int r3 = r3.currentAccount     // Catch:{ Exception -> 0x0427 }
                org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r3)     // Catch:{ Exception -> 0x0427 }
                org.telegram.SQLite.SQLiteDatabase r3 = r3.getDatabase()     // Catch:{ Exception -> 0x0427 }
                java.lang.String r4 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid"
                r5 = 0
                java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0427 }
                org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r6)     // Catch:{ Exception -> 0x0427 }
            L_0x0319:
                boolean r4 = r3.next()     // Catch:{ Exception -> 0x0427 }
                if (r4 == 0) goto L_0x0419
                r4 = 3
                int r4 = r3.intValue(r4)     // Catch:{ Exception -> 0x0427 }
                long r4 = (long) r4     // Catch:{ Exception -> 0x0427 }
                int r4 = r2.indexOfKey(r4)     // Catch:{ Exception -> 0x0427 }
                if (r4 < 0) goto L_0x032c
                goto L_0x0319
            L_0x032c:
                r5 = 2
                java.lang.String r4 = r3.stringValue(r5)     // Catch:{ Exception -> 0x0427 }
                org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0427 }
                java.lang.String r6 = r6.getTranslitString(r4)     // Catch:{ Exception -> 0x0427 }
                boolean r9 = r4.equals(r6)     // Catch:{ Exception -> 0x0427 }
                if (r9 == 0) goto L_0x0341
                r10 = 0
                goto L_0x0342
            L_0x0341:
                r10 = r6
            L_0x0342:
                r6 = r18
                int r9 = r4.lastIndexOf(r6)     // Catch:{ Exception -> 0x0427 }
                r11 = -1
                if (r9 == r11) goto L_0x0352
                int r9 = r9 + 3
                java.lang.String r9 = r4.substring(r9)     // Catch:{ Exception -> 0x0427 }
                goto L_0x0353
            L_0x0352:
                r9 = 0
            L_0x0353:
                r12 = 0
                r14 = 0
            L_0x0355:
                if (r12 >= r7) goto L_0x0413
                r5 = r8[r12]     // Catch:{ Exception -> 0x0427 }
                boolean r16 = r4.startsWith(r5)     // Catch:{ Exception -> 0x0427 }
                if (r16 != 0) goto L_0x039c
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0427 }
                r11.<init>()     // Catch:{ Exception -> 0x0427 }
                r11.append(r15)     // Catch:{ Exception -> 0x0427 }
                r11.append(r5)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0427 }
                boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x0427 }
                if (r11 != 0) goto L_0x039c
                if (r10 == 0) goto L_0x0392
                boolean r11 = r10.startsWith(r5)     // Catch:{ Exception -> 0x0427 }
                if (r11 != 0) goto L_0x039c
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0427 }
                r11.<init>()     // Catch:{ Exception -> 0x0427 }
                r11.append(r15)     // Catch:{ Exception -> 0x0427 }
                r11.append(r5)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0427 }
                boolean r11 = r10.contains(r11)     // Catch:{ Exception -> 0x0427 }
                if (r11 == 0) goto L_0x0392
                goto L_0x039c
            L_0x0392:
                if (r9 == 0) goto L_0x039d
                boolean r11 = r9.startsWith(r5)     // Catch:{ Exception -> 0x0427 }
                if (r11 == 0) goto L_0x039d
                r14 = 2
                goto L_0x039d
            L_0x039c:
                r14 = 1
            L_0x039d:
                if (r14 == 0) goto L_0x040b
                r11 = 0
                org.telegram.tgnet.NativeByteBuffer r4 = r3.byteBufferValue(r11)     // Catch:{ Exception -> 0x0427 }
                if (r4 == 0) goto L_0x0408
                int r9 = r4.readInt32(r11)     // Catch:{ Exception -> 0x0427 }
                org.telegram.tgnet.TLRPC$User r9 = org.telegram.tgnet.TLRPC$User.TLdeserialize(r4, r9, r11)     // Catch:{ Exception -> 0x0427 }
                r4.reuse()     // Catch:{ Exception -> 0x0427 }
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$DialogSearchResult r4 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$DialogSearchResult     // Catch:{ Exception -> 0x0427 }
                r10 = 0
                r4.<init>()     // Catch:{ Exception -> 0x0427 }
                org.telegram.tgnet.TLRPC$UserStatus r10 = r9.status     // Catch:{ Exception -> 0x0427 }
                if (r10 == 0) goto L_0x03c4
                org.telegram.tgnet.TLRPC$UserStatus r10 = r9.status     // Catch:{ Exception -> 0x0427 }
                r12 = 1
                int r11 = r3.intValue(r12)     // Catch:{ Exception -> 0x0427 }
                r10.expires = r11     // Catch:{ Exception -> 0x0427 }
            L_0x03c4:
                org.telegram.tgnet.TLRPC$Dialog r10 = r4.dialog     // Catch:{ Exception -> 0x0427 }
                int r11 = r9.id     // Catch:{ Exception -> 0x0427 }
                long r11 = (long) r11     // Catch:{ Exception -> 0x0427 }
                r10.id = r11     // Catch:{ Exception -> 0x0427 }
                r4.object = r9     // Catch:{ Exception -> 0x0427 }
                r11 = 1
                if (r14 != r11) goto L_0x03dc
                java.lang.String r10 = r9.first_name     // Catch:{ Exception -> 0x0427 }
                java.lang.String r9 = r9.last_name     // Catch:{ Exception -> 0x0427 }
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r10, r9, r5)     // Catch:{ Exception -> 0x0427 }
                r4.name = r5     // Catch:{ Exception -> 0x0427 }
                r10 = 0
                goto L_0x0403
            L_0x03dc:
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0427 }
                r10.<init>()     // Catch:{ Exception -> 0x0427 }
                r10.append(r13)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r9 = r9.username     // Catch:{ Exception -> 0x0427 }
                r10.append(r9)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r9 = r10.toString()     // Catch:{ Exception -> 0x0427 }
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0427 }
                r10.<init>()     // Catch:{ Exception -> 0x0427 }
                r10.append(r13)     // Catch:{ Exception -> 0x0427 }
                r10.append(r5)     // Catch:{ Exception -> 0x0427 }
                java.lang.String r5 = r10.toString()     // Catch:{ Exception -> 0x0427 }
                r10 = 0
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r10, r5)     // Catch:{ Exception -> 0x0427 }
                r4.name = r5     // Catch:{ Exception -> 0x0427 }
            L_0x0403:
                r0.add(r4)     // Catch:{ Exception -> 0x0427 }
                r5 = r10
                goto L_0x0415
            L_0x0408:
                r11 = 1
                r5 = 0
                goto L_0x0415
            L_0x040b:
                r5 = 0
                r11 = 1
                int r12 = r12 + 1
                r5 = 2
                r11 = -1
                goto L_0x0355
            L_0x0413:
                r5 = 0
                r11 = 1
            L_0x0415:
                r18 = r6
                goto L_0x0319
            L_0x0419:
                r3.dispose()     // Catch:{ Exception -> 0x0427 }
                org.telegram.ui.Components.-$$Lambda$ShareAlert$ShareSearchAdapter$yOcjYAtW1X0Tew0aYHc9oPG3azM r2 = org.telegram.ui.Components.$$Lambda$ShareAlert$ShareSearchAdapter$yOcjYAtW1X0Tew0aYHc9oPG3azM.INSTANCE     // Catch:{ Exception -> 0x0427 }
                java.util.Collections.sort(r0, r2)     // Catch:{ Exception -> 0x0427 }
                r2 = r22
                r1.updateSearchResults(r0, r2)     // Catch:{ Exception -> 0x0427 }
                goto L_0x042b
            L_0x0427:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x042b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(java.lang.String, int):void");
        }

        static /* synthetic */ int lambda$null$0(DialogSearchResult dialogSearchResult, DialogSearchResult dialogSearchResult2) {
            int i = dialogSearchResult.date;
            int i2 = dialogSearchResult2.date;
            if (i < i2) {
                return 1;
            }
            return i > i2 ? -1 : 0;
        }

        private void updateSearchResults(ArrayList<DialogSearchResult> arrayList, int i) {
            AndroidUtilities.runOnUIThread(new Runnable(i, arrayList) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.lambda$updateSearchResults$2$ShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$2$ShareAlert$ShareSearchAdapter(int i, ArrayList arrayList) {
            boolean z;
            if (i == this.lastSearchId) {
                if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                    ShareAlert shareAlert = ShareAlert.this;
                    int unused = shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
                    ShareAlert.this.searchAdapter.notifyDataSetChanged();
                }
                int i2 = 0;
                while (true) {
                    z = true;
                    if (i2 >= arrayList.size()) {
                        break;
                    }
                    TLObject tLObject = ((DialogSearchResult) arrayList.get(i2)).object;
                    if (tLObject instanceof TLRPC$User) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putUser((TLRPC$User) tLObject, true);
                    } else if (tLObject instanceof TLRPC$Chat) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putChat((TLRPC$Chat) tLObject, true);
                    }
                    i2++;
                }
                boolean z2 = !this.searchResult.isEmpty() && arrayList.isEmpty();
                if (!this.searchResult.isEmpty() || !arrayList.isEmpty()) {
                    z = false;
                }
                if (z2) {
                    ShareAlert shareAlert2 = ShareAlert.this;
                    int unused2 = shareAlert2.topBeforeSwitch = shareAlert2.getCurrentTop();
                }
                this.searchResult = arrayList;
                notifyDataSetChanged();
                if (!z && !z2 && ShareAlert.this.topBeforeSwitch > 0) {
                    ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
                    int unused3 = ShareAlert.this.topBeforeSwitch = -1000;
                }
                ShareAlert.this.searchEmptyView.showTextView();
            }
        }

        public void searchDialogs(String str) {
            if (str == null || !str.equals(this.lastSearchText)) {
                this.lastSearchText = str;
                if (this.searchRunnable != null) {
                    Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                    this.searchRunnable = null;
                }
                if (TextUtils.isEmpty(str)) {
                    this.searchResult.clear();
                    ShareAlert shareAlert = ShareAlert.this;
                    int unused = shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    this.lastSearchId = -1;
                    notifyDataSetChanged();
                    return;
                }
                int i = this.lastSearchId + 1;
                this.lastSearchId = i;
                this.searchRunnable = new Runnable(str, i) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        ShareAlert.ShareSearchAdapter.this.lambda$searchDialogs$3$ShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                    }
                };
                Utilities.searchQueue.postRunnable(this.searchRunnable, 300);
            }
        }

        public int getItemCount() {
            int size = this.searchResult.size();
            return size != 0 ? size + 1 : size;
        }

        public TLRPC$Dialog getItem(int i) {
            int i2 = i - 1;
            if (i2 < 0 || i2 >= this.searchResult.size()) {
                return null;
            }
            return this.searchResult.get(i2).dialog;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new View(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            } else {
                view = new ShareDialogCell(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) viewHolder.itemView;
                boolean z = true;
                DialogSearchResult dialogSearchResult = this.searchResult.get(i - 1);
                int i2 = (int) dialogSearchResult.dialog.id;
                if (ShareAlert.this.selectedDialogs.indexOfKey(dialogSearchResult.dialog.id) < 0) {
                    z = false;
                }
                shareDialogCell.setDialog(i2, z, dialogSearchResult.name);
            }
        }
    }
}
