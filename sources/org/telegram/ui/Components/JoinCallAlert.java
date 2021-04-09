package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupCallJoinAs;
import org.telegram.tgnet.TLRPC$TL_phone_joinAsPeers;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.RecyclerListView;

public class JoinCallAlert extends BottomSheet {
    private static ArrayList<TLRPC$Peer> cachedChats;
    private static long lastCacheDid;
    private static long lastCacheTime;
    private static int lastCachedAccount;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Peer> chats;
    private TLRPC$Peer currentPeer;
    /* access modifiers changed from: private */
    public int currentType;
    private JoinCallAlertDelegate delegate;
    private BottomSheetCell doneButton;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public TextView messageTextView;
    private boolean schedule;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    private TLRPC$InputPeer selectAfterDismiss;
    /* access modifiers changed from: private */
    public TLRPC$Peer selectedPeer;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private TextView textView;

    public interface JoinCallAlertDelegate {
        void didSelectChat(TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public static void resetCache() {
        cachedChats = null;
    }

    public static void processDeletedChat(int i, long j) {
        ArrayList<TLRPC$Peer> arrayList;
        if (lastCachedAccount == i && (arrayList = cachedChats) != null && j <= 0) {
            int i2 = 0;
            int size = arrayList.size();
            while (true) {
                if (i2 >= size) {
                    break;
                } else if (((long) MessageObject.getPeerId(cachedChats.get(i2))) == j) {
                    cachedChats.remove(i2);
                    break;
                } else {
                    i2++;
                }
            }
            if (cachedChats.isEmpty()) {
                cachedChats = null;
            }
        }
    }

    public class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public View background;
        private boolean hasBackground;
        /* access modifiers changed from: private */
        public TextView[] textView = new TextView[2];

        public BottomSheetCell(Context context, boolean z) {
            super(context);
            this.hasBackground = !z;
            setBackground((Drawable) null);
            View view = new View(context);
            this.background = view;
            if (this.hasBackground) {
                view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            }
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, z ? 0.0f : 16.0f, 16.0f, 16.0f));
            for (int i = 0; i < 2; i++) {
                this.textView[i] = new TextView(context);
                this.textView[i].setLines(1);
                this.textView[i].setSingleLine(true);
                this.textView[i].setGravity(1);
                this.textView[i].setEllipsize(TextUtils.TruncateAt.END);
                this.textView[i].setGravity(17);
                if (this.hasBackground) {
                    this.textView[i].setTextColor(Theme.getColor("featuredStickers_buttonText"));
                    this.textView[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                } else {
                    this.textView[i].setTextColor(Theme.getColor("featuredStickers_addButton"));
                }
                this.textView[i].setTextSize(1, 14.0f);
                this.textView[i].setPadding(0, 0, 0, this.hasBackground ? 0 : AndroidUtilities.dp(13.0f));
                addView(this.textView[i], LayoutHelper.createFrame(-2, -2, 17));
                if (i == 1) {
                    this.textView[i].setAlpha(0.0f);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.hasBackground ? 80.0f : 50.0f), NUM));
        }

        public void setText(CharSequence charSequence, boolean z) {
            if (!z) {
                this.textView[0].setText(charSequence);
                return;
            }
            this.textView[1].setText(charSequence);
            boolean unused = JoinCallAlert.this.animationInProgress = true;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(180);
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.textView[0], View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.textView[0], View.TRANSLATION_Y, new float[]{0.0f, (float) (-AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofFloat(this.textView[1], View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.textView[1], View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(10.0f), 0.0f})});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    boolean unused = JoinCallAlert.this.animationInProgress = false;
                    TextView textView = BottomSheetCell.this.textView[0];
                    BottomSheetCell.this.textView[0] = BottomSheetCell.this.textView[1];
                    BottomSheetCell.this.textView[1] = textView;
                }
            });
            animatorSet.start();
        }
    }

    public static void checkFewUsers(Context context, int i, AccountInstance accountInstance, MessagesStorage.BooleanCallback booleanCallback) {
        if (lastCachedAccount != accountInstance.getCurrentAccount() || lastCacheDid != ((long) i) || cachedChats == null || SystemClock.elapsedRealtime() - lastCacheTime >= 240000) {
            AlertDialog alertDialog = new AlertDialog(context, 3);
            TLRPC$TL_phone_getGroupCallJoinAs tLRPC$TL_phone_getGroupCallJoinAs = new TLRPC$TL_phone_getGroupCallJoinAs();
            tLRPC$TL_phone_getGroupCallJoinAs.peer = accountInstance.getMessagesController().getInputPeer(i);
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupCallJoinAs, new RequestDelegate(i, accountInstance, booleanCallback) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ AccountInstance f$2;
                public final /* synthetic */ MessagesStorage.BooleanCallback f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(tLObject, this.f$1, this.f$2, this.f$3) {
                        public final /* synthetic */ TLObject f$1;
                        public final /* synthetic */ int f$2;
                        public final /* synthetic */ AccountInstance f$3;
                        public final /* synthetic */ MessagesStorage.BooleanCallback f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run() {
                            JoinCallAlert.lambda$null$0(AlertDialog.this, this.f$1, this.f$2, this.f$3, this.f$4);
                        }
                    });
                }
            })) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    AccountInstance.this.getConnectionsManager().cancelRequest(this.f$1, true);
                }
            });
            try {
                alertDialog.showDelayed(500);
            } catch (Exception unused) {
            }
        } else {
            boolean z = true;
            if (cachedChats.size() != 1) {
                z = false;
            }
            booleanCallback.run(z);
        }
    }

    static /* synthetic */ void lambda$null$0(AlertDialog alertDialog, TLObject tLObject, int i, AccountInstance accountInstance, MessagesStorage.BooleanCallback booleanCallback) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            TLRPC$TL_phone_joinAsPeers tLRPC$TL_phone_joinAsPeers = (TLRPC$TL_phone_joinAsPeers) tLObject;
            cachedChats = tLRPC$TL_phone_joinAsPeers.peers;
            lastCacheDid = (long) i;
            lastCacheTime = SystemClock.elapsedRealtime();
            lastCachedAccount = accountInstance.getCurrentAccount();
            boolean z = false;
            accountInstance.getMessagesController().putChats(tLRPC$TL_phone_joinAsPeers.chats, false);
            accountInstance.getMessagesController().putUsers(tLRPC$TL_phone_joinAsPeers.users, false);
            if (tLRPC$TL_phone_joinAsPeers.peers.size() == 1) {
                z = true;
            }
            booleanCallback.run(z);
        }
    }

    public static void open(Context context, int i, AccountInstance accountInstance, BaseFragment baseFragment, int i2, TLRPC$Peer tLRPC$Peer, JoinCallAlertDelegate joinCallAlertDelegate) {
        int i3 = i;
        JoinCallAlertDelegate joinCallAlertDelegate2 = joinCallAlertDelegate;
        if (context != null && joinCallAlertDelegate2 != null) {
            if (lastCachedAccount == accountInstance.getCurrentAccount()) {
                long j = (long) i3;
                if (lastCacheDid == j && cachedChats != null && SystemClock.elapsedRealtime() - lastCacheTime < 300000) {
                    if (cachedChats.size() == 1) {
                        joinCallAlertDelegate2.didSelectChat(accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(cachedChats.get(0))), false, false);
                        return;
                    } else {
                        showAlert(context, j, cachedChats, baseFragment, i2, tLRPC$Peer, joinCallAlertDelegate);
                        return;
                    }
                }
            }
            AlertDialog alertDialog = new AlertDialog(context, 3);
            TLRPC$TL_phone_getGroupCallJoinAs tLRPC$TL_phone_getGroupCallJoinAs = new TLRPC$TL_phone_getGroupCallJoinAs();
            tLRPC$TL_phone_getGroupCallJoinAs.peer = accountInstance.getMessagesController().getInputPeer(i);
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupCallJoinAs, new RequestDelegate(accountInstance, joinCallAlertDelegate, i, context, baseFragment, i2, tLRPC$Peer) {
                public final /* synthetic */ AccountInstance f$1;
                public final /* synthetic */ JoinCallAlert.JoinCallAlertDelegate f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ Context f$4;
                public final /* synthetic */ BaseFragment f$5;
                public final /* synthetic */ int f$6;
                public final /* synthetic */ TLRPC$Peer f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(tLObject, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7) {
                        public final /* synthetic */ TLObject f$1;
                        public final /* synthetic */ AccountInstance f$2;
                        public final /* synthetic */ JoinCallAlert.JoinCallAlertDelegate f$3;
                        public final /* synthetic */ int f$4;
                        public final /* synthetic */ Context f$5;
                        public final /* synthetic */ BaseFragment f$6;
                        public final /* synthetic */ int f$7;
                        public final /* synthetic */ TLRPC$Peer f$8;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                            this.f$7 = r8;
                            this.f$8 = r9;
                        }

                        public final void run() {
                            JoinCallAlert.lambda$null$3(AlertDialog.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
                        }
                    });
                }
            })) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    AccountInstance.this.getConnectionsManager().cancelRequest(this.f$1, true);
                }
            });
            try {
                alertDialog.showDelayed(500);
            } catch (Exception unused) {
            }
        }
    }

    static /* synthetic */ void lambda$null$3(AlertDialog alertDialog, TLObject tLObject, AccountInstance accountInstance, JoinCallAlertDelegate joinCallAlertDelegate, int i, Context context, BaseFragment baseFragment, int i2, TLRPC$Peer tLRPC$Peer) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            TLRPC$TL_phone_joinAsPeers tLRPC$TL_phone_joinAsPeers = (TLRPC$TL_phone_joinAsPeers) tLObject;
            if (tLRPC$TL_phone_joinAsPeers.peers.size() == 1) {
                JoinCallAlertDelegate joinCallAlertDelegate2 = joinCallAlertDelegate;
                joinCallAlertDelegate.didSelectChat(accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(tLRPC$TL_phone_joinAsPeers.peers.get(0))), false, false);
                return;
            }
            JoinCallAlertDelegate joinCallAlertDelegate3 = joinCallAlertDelegate;
            cachedChats = tLRPC$TL_phone_joinAsPeers.peers;
            long j = (long) i;
            lastCacheDid = j;
            lastCacheTime = SystemClock.elapsedRealtime();
            lastCachedAccount = accountInstance.getCurrentAccount();
            accountInstance.getMessagesController().putChats(tLRPC$TL_phone_joinAsPeers.chats, false);
            accountInstance.getMessagesController().putUsers(tLRPC$TL_phone_joinAsPeers.users, false);
            showAlert(context, j, tLRPC$TL_phone_joinAsPeers.peers, baseFragment, i2, tLRPC$Peer, joinCallAlertDelegate);
        }
    }

    private static void showAlert(Context context, long j, ArrayList<TLRPC$Peer> arrayList, BaseFragment baseFragment, int i, TLRPC$Peer tLRPC$Peer, JoinCallAlertDelegate joinCallAlertDelegate) {
        BaseFragment baseFragment2 = baseFragment;
        JoinCallAlert joinCallAlert = new JoinCallAlert(context, j, arrayList, i, tLRPC$Peer, joinCallAlertDelegate);
        if (baseFragment2 == null) {
            joinCallAlert.show();
        } else if (baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(joinCallAlert);
        }
    }

    /* JADX WARNING: type inference failed for: r5v7, types: [android.widget.LinearLayout, org.telegram.ui.Components.JoinCallAlert$1, android.view.View] */
    /* JADX WARNING: Illegal instructions before constructor call */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private JoinCallAlert(android.content.Context r19, long r20, java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r22, int r23, org.telegram.tgnet.TLRPC$Peer r24, org.telegram.ui.Components.JoinCallAlert.JoinCallAlertDelegate r25) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r23
            r3 = r25
            r4 = 0
            r0.<init>(r1, r4)
            r0.setApplyBottomPadding(r4)
            java.util.ArrayList r5 = new java.util.ArrayList
            r6 = r22
            r5.<init>(r6)
            r0.chats = r5
            r0.delegate = r3
            r0.currentType = r2
            android.content.res.Resources r5 = r19.getResources()
            r6 = 2131166018(0x7var_, float:1.794627E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0.shadowDrawable = r5
            r6 = 2
            if (r2 != r6) goto L_0x00a3
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r5 == 0) goto L_0x005d
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r5 = r5.getSelfId()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r7 = r0.chats
            int r7 = r7.size()
            r8 = 0
        L_0x0045:
            if (r8 >= r7) goto L_0x0090
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r9 = r0.chats
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$Peer r9 = (org.telegram.tgnet.TLRPC$Peer) r9
            int r10 = org.telegram.messenger.MessageObject.getPeerId(r9)
            if (r10 != r5) goto L_0x005a
            r0.currentPeer = r9
            r0.selectedPeer = r9
            goto L_0x0090
        L_0x005a:
            int r8 = r8 + 1
            goto L_0x0045
        L_0x005d:
            if (r24 == 0) goto L_0x0086
            int r5 = org.telegram.messenger.MessageObject.getPeerId(r24)
            long r7 = (long) r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r5 = r0.chats
            int r5 = r5.size()
            r9 = 0
        L_0x006b:
            if (r9 >= r5) goto L_0x0090
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r10 = r0.chats
            java.lang.Object r10 = r10.get(r9)
            org.telegram.tgnet.TLRPC$Peer r10 = (org.telegram.tgnet.TLRPC$Peer) r10
            int r11 = org.telegram.messenger.MessageObject.getPeerId(r10)
            long r11 = (long) r11
            int r13 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r13 != 0) goto L_0x0083
            r0.currentPeer = r10
            r0.selectedPeer = r10
            goto L_0x0090
        L_0x0083:
            int r9 = r9 + 1
            goto L_0x006b
        L_0x0086:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r5 = r0.chats
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$Peer r5 = (org.telegram.tgnet.TLRPC$Peer) r5
            r0.selectedPeer = r5
        L_0x0090:
            android.graphics.drawable.Drawable r5 = r0.shadowDrawable
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "voipgroup_inviteMembersBackground"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r5.setColorFilter(r7)
            goto L_0x00bd
        L_0x00a3:
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "dialogBackground"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r5.setColorFilter(r7)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r5 = r0.chats
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$Peer r5 = (org.telegram.tgnet.TLRPC$Peer) r5
            r0.selectedPeer = r5
        L_0x00bd:
            int r5 = r0.currentType
            r7 = 1
            if (r5 != 0) goto L_0x00ce
            org.telegram.ui.Components.JoinCallAlert$1 r5 = new org.telegram.ui.Components.JoinCallAlert$1
            r5.<init>(r1)
            r5.setOrientation(r7)
            r0.setCustomView(r5)
            goto L_0x00df
        L_0x00ce:
            org.telegram.ui.Components.JoinCallAlert$2 r5 = new org.telegram.ui.Components.JoinCallAlert$2
            r5.<init>(r1)
            r0.containerView = r5
            r5.setWillNotDraw(r4)
            android.view.ViewGroup r8 = r0.containerView
            int r9 = r0.backgroundPaddingLeft
            r8.setPadding(r9, r4, r9, r4)
        L_0x00df:
            org.telegram.ui.Components.JoinCallAlert$3 r8 = new org.telegram.ui.Components.JoinCallAlert$3
            r8.<init>(r1)
            r0.listView = r8
            androidx.recyclerview.widget.LinearLayoutManager r9 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r10 = r18.getContext()
            int r11 = r0.currentType
            if (r11 != 0) goto L_0x00f2
            r11 = 0
            goto L_0x00f3
        L_0x00f2:
            r11 = 1
        L_0x00f3:
            r9.<init>(r10, r11, r4)
            r8.setLayoutManager(r9)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$ListAdapter r9 = new org.telegram.ui.Components.JoinCallAlert$ListAdapter
            r9.<init>(r1)
            r8.setAdapter(r9)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r8.setVerticalScrollBarEnabled(r4)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r8.setClipToPadding(r4)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r8.setEnabled(r7)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            java.lang.String r9 = "dialogScrollGlow"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setGlowColor(r9)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$4 r9 = new org.telegram.ui.Components.JoinCallAlert$4
            r9.<init>()
            r8.setOnScrollListener(r9)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.Components.-$$Lambda$JoinCallAlert$l2zFmX37oe83ibKiGYwwo2B7i5E r9 = new org.telegram.ui.Components.-$$Lambda$JoinCallAlert$l2zFmX37oe83ibKiGYwwo2B7i5E
            r9.<init>()
            r8.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r9)
            if (r2 == 0) goto L_0x0148
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r9 = -1
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            r11 = 51
            r12 = 0
            r13 = 1120403456(0x42CLASSNAME, float:100.0)
            r14 = 0
            r15 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r5.addView(r8, r9)
            goto L_0x015c
        L_0x0148:
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r8.setSelectorDrawableColor(r4)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r9 = 1092616192(0x41200000, float:10.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r8.setPadding(r10, r4, r9, r4)
        L_0x015c:
            if (r2 != 0) goto L_0x0186
            org.telegram.ui.Components.RLottieImageView r8 = new org.telegram.ui.Components.RLottieImageView
            r8.<init>(r1)
            r8.setAutoRepeat(r7)
            r9 = 2131558504(0x7f0d0068, float:1.8742326E38)
            r10 = 120(0x78, float:1.68E-43)
            r8.setAnimation(r9, r10, r10)
            r8.playAnimation()
            r11 = 160(0xa0, float:2.24E-43)
            r12 = 160(0xa0, float:2.24E-43)
            r13 = 49
            r14 = 17
            r15 = 8
            r16 = 17
            r17 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
            r5.addView(r8, r9)
        L_0x0186:
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            r0.textView = r8
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r8.setTypeface(r9)
            android.widget.TextView r8 = r0.textView
            r9 = 1101004800(0x41a00000, float:20.0)
            r8.setTextSize(r7, r9)
            if (r2 != r6) goto L_0x01ab
            android.widget.TextView r8 = r0.textView
            java.lang.String r9 = "voipgroup_nameText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setTextColor(r9)
            goto L_0x01b6
        L_0x01ab:
            android.widget.TextView r8 = r0.textView
            java.lang.String r9 = "dialogTextBlack"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setTextColor(r9)
        L_0x01b6:
            android.widget.TextView r8 = r0.textView
            r8.setSingleLine(r7)
            android.widget.TextView r8 = r0.textView
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r8.setEllipsize(r9)
            if (r2 != 0) goto L_0x01e7
            android.widget.TextView r8 = r0.textView
            r9 = 2131627509(0x7f0e0df5, float:1.8882284E38)
            java.lang.String r10 = "StartVoipChatTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setText(r9)
            android.widget.TextView r8 = r0.textView
            r9 = -2
            r10 = -2
            r11 = 49
            r12 = 23
            r13 = 16
            r14 = 23
            r15 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
            r5.addView(r8, r9)
            goto L_0x021b
        L_0x01e7:
            if (r2 != r6) goto L_0x01f8
            android.widget.TextView r8 = r0.textView
            r9 = 2131628007(0x7f0e0fe7, float:1.8883295E38)
            java.lang.String r10 = "VoipGroupDisplayAs"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setText(r9)
            goto L_0x0206
        L_0x01f8:
            android.widget.TextView r8 = r0.textView
            r9 = 2131628024(0x7f0e0ff8, float:1.888333E38)
            java.lang.String r10 = "VoipGroupJoinAs"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setText(r9)
        L_0x0206:
            android.widget.TextView r8 = r0.textView
            r9 = -2
            r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r11 = 51
            r12 = 1102577664(0x41b80000, float:23.0)
            r13 = 1090519040(0x41000000, float:8.0)
            r14 = 1102577664(0x41b80000, float:23.0)
            r15 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r5.addView(r8, r9)
        L_0x021b:
            android.widget.TextView r8 = new android.widget.TextView
            android.content.Context r9 = r18.getContext()
            r8.<init>(r9)
            r0.messageTextView = r8
            if (r2 != r6) goto L_0x0232
            java.lang.String r6 = "voipgroup_lastSeenText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r8.setTextColor(r6)
            goto L_0x023b
        L_0x0232:
            java.lang.String r6 = "dialogTextGray3"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r8.setTextColor(r6)
        L_0x023b:
            android.widget.TextView r6 = r0.messageTextView
            r8 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r7, r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r6 = r0.chats
            int r6 = r6.size()
            r8 = 0
        L_0x0249:
            if (r8 >= r6) goto L_0x0277
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r9 = r0.chats
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$Peer r9 = (org.telegram.tgnet.TLRPC$Peer) r9
            int r9 = org.telegram.messenger.MessageObject.getPeerId(r9)
            if (r9 >= 0) goto L_0x0274
            int r10 = r0.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r9 = -r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r9 = r10.getChat(r9)
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r10 == 0) goto L_0x0272
            boolean r9 = r9.megagroup
            if (r9 == 0) goto L_0x0274
        L_0x0272:
            r6 = 1
            goto L_0x0278
        L_0x0274:
            int r8 = r8 + 1
            goto L_0x0249
        L_0x0277:
            r6 = 0
        L_0x0278:
            android.widget.TextView r8 = r0.messageTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r9 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r9.<init>()
            r8.setMovementMethod(r9)
            android.widget.TextView r8 = r0.messageTextView
            java.lang.String r9 = "dialogTextLink"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setLinkTextColor(r9)
            r8 = 5
            if (r2 != 0) goto L_0x02e4
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            r9 = r20
            long r9 = -r9
            int r10 = (int) r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r9)
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r9 == 0) goto L_0x02bb
            boolean r6 = r6.megagroup
            if (r6 != 0) goto L_0x02bb
            android.widget.TextView r6 = r0.messageTextView
            r9 = 2131627973(0x7f0e0fc5, float:1.8883226E38)
            java.lang.String r10 = "VoipChannelStart"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
            goto L_0x02c9
        L_0x02bb:
            android.widget.TextView r6 = r0.messageTextView
            r9 = 2131628063(0x7f0e101f, float:1.8883408E38)
            java.lang.String r10 = "VoipGroupStart"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
        L_0x02c9:
            android.widget.TextView r6 = r0.messageTextView
            r9 = 49
            r6.setGravity(r9)
            android.widget.TextView r6 = r0.messageTextView
            r9 = -2
            r10 = -2
            r11 = 49
            r12 = 23
            r13 = 0
            r14 = 23
            r15 = 5
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
            r5.addView(r6, r9)
            goto L_0x0326
        L_0x02e4:
            if (r6 == 0) goto L_0x02f5
            android.widget.TextView r6 = r0.messageTextView
            r9 = 2131628066(0x7f0e1022, float:1.8883414E38)
            java.lang.String r10 = "VoipGroupStartAsInfoGroup"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
            goto L_0x0303
        L_0x02f5:
            android.widget.TextView r6 = r0.messageTextView
            r9 = 2131628065(0x7f0e1021, float:1.8883412E38)
            java.lang.String r10 = "VoipGroupStartAsInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
        L_0x0303:
            android.widget.TextView r6 = r0.messageTextView
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x030b
            r9 = 5
            goto L_0x030c
        L_0x030b:
            r9 = 3
        L_0x030c:
            r9 = r9 | 48
            r6.setGravity(r9)
            android.widget.TextView r6 = r0.messageTextView
            r9 = -2
            r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r11 = 51
            r12 = 1102577664(0x41b80000, float:23.0)
            r13 = 0
            r14 = 1102577664(0x41b80000, float:23.0)
            r15 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r5.addView(r6, r9)
        L_0x0326:
            if (r2 != 0) goto L_0x0346
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r6 = r0.chats
            int r6 = r6.size()
            if (r6 >= r8) goto L_0x0335
            r6 = -2
            r8 = -2
            goto L_0x0337
        L_0x0335:
            r6 = -1
            r8 = -1
        L_0x0337:
            r9 = 95
            r10 = 49
            r11 = 0
            r12 = 6
            r13 = 0
            r14 = 0
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
            r5.addView(r2, r6)
        L_0x0346:
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r2 = new org.telegram.ui.Components.JoinCallAlert$BottomSheetCell
            r2.<init>(r1, r4)
            r0.doneButton = r2
            android.view.View r2 = r2.background
            org.telegram.ui.Components.-$$Lambda$JoinCallAlert$ul4eawG8oeqXzN8nO-ml4ryVJ2Q r6 = new org.telegram.ui.Components.-$$Lambda$JoinCallAlert$ul4eawG8oeqXzN8nO-ml4ryVJ2Q
            r6.<init>(r3)
            r2.setOnClickListener(r6)
            int r2 = r0.currentType
            if (r2 != 0) goto L_0x03ab
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r2 = r0.doneButton
            r8 = -1
            r9 = 50
            r10 = 51
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
            r5.addView(r2, r3)
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r2 = new org.telegram.ui.Components.JoinCallAlert$BottomSheetCell
            r2.<init>(r1, r7)
            r1 = 2131628055(0x7f0e1017, float:1.8883392E38)
            java.lang.String r3 = "VoipGroupScheduleVoiceChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r2.setText(r1, r4)
            android.view.View r1 = r2.background
            org.telegram.ui.Components.-$$Lambda$JoinCallAlert$wsab-0gfbksu6pdnDfHIR8V_vjs r3 = new org.telegram.ui.Components.-$$Lambda$JoinCallAlert$wsab-0gfbksu6pdnDfHIR8V_vjs
            r3.<init>()
            r1.setOnClickListener(r3)
            r1 = -1
            r3 = 50
            r6 = 51
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r19 = r1
            r20 = r3
            r21 = r6
            r22 = r7
            r23 = r8
            r24 = r9
            r25 = r10
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r5.addView(r2, r1)
            goto L_0x03cb
        L_0x03ab:
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r1 = r0.doneButton
            r2 = -1
            r3 = 1112014848(0x42480000, float:50.0)
            r6 = 83
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r19 = r2
            r20 = r3
            r21 = r6
            r22 = r7
            r23 = r8
            r24 = r9
            r25 = r10
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r5.addView(r1, r2)
        L_0x03cb:
            r0.updateDoneButton(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinCallAlert.<init>(android.content.Context, long, java.util.ArrayList, int, org.telegram.tgnet.TLRPC$Peer, org.telegram.ui.Components.JoinCallAlert$JoinCallAlertDelegate):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$6 */
    public /* synthetic */ void lambda$new$6$JoinCallAlert(View view, int i) {
        if (!this.animationInProgress && this.chats.get(i) != this.selectedPeer) {
            this.selectedPeer = this.chats.get(i);
            boolean z = view instanceof GroupCreateUserCell;
            if (z) {
                ((GroupCreateUserCell) view).setChecked(true, true);
            } else if (view instanceof ShareDialogCell) {
                ((ShareDialogCell) view).setChecked(true, true);
                view.invalidate();
            }
            int childCount = this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt != view) {
                    if (z) {
                        ((GroupCreateUserCell) childAt).setChecked(false, true);
                    } else if (view instanceof ShareDialogCell) {
                        ((ShareDialogCell) childAt).setChecked(false, true);
                    }
                }
            }
            if (this.currentType != 0) {
                updateDoneButton(true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$7 */
    public /* synthetic */ void lambda$new$7$JoinCallAlert(JoinCallAlertDelegate joinCallAlertDelegate, View view) {
        TLRPC$InputPeer inputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(this.selectedPeer));
        if (this.currentType != 2) {
            this.selectAfterDismiss = inputPeer;
        } else if (this.selectedPeer != this.currentPeer) {
            boolean z = true;
            if (this.chats.size() <= 1) {
                z = false;
            }
            joinCallAlertDelegate.didSelectChat(inputPeer, z, false);
        }
        dismiss();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$8 */
    public /* synthetic */ void lambda$new$8$JoinCallAlert(View view) {
        this.selectAfterDismiss = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(this.selectedPeer));
        this.schedule = true;
        dismiss();
    }

    private void updateDoneButton(boolean z) {
        if (this.currentType == 0) {
            this.doneButton.setText(LocaleController.formatString("VoipGroupStartVoiceChat", NUM, new Object[0]), z);
            return;
        }
        int peerId = MessageObject.getPeerId(this.selectedPeer);
        if (peerId > 0) {
            this.doneButton.setText(LocaleController.formatString("VoipGroupContinueAs", NUM, UserObject.getFirstName(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(peerId)))), z);
            return;
        }
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-peerId));
        BottomSheetCell bottomSheetCell = this.doneButton;
        Object[] objArr = new Object[1];
        objArr[0] = chat != null ? chat.title : "";
        bottomSheetCell.setText(LocaleController.formatString("VoipGroupContinueAs", NUM, objArr), z);
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.currentType != 0) {
            if (this.listView.getChildCount() <= 0) {
                RecyclerListView recyclerListView = this.listView;
                int paddingTop = recyclerListView.getPaddingTop();
                this.scrollOffsetY = paddingTop;
                recyclerListView.setTopGlowOffset(paddingTop);
                this.containerView.invalidate();
                return;
            }
            int i = 0;
            View childAt = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
            int top = childAt.getTop() - AndroidUtilities.dp(9.0f);
            if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
                i = top;
            }
            if (this.scrollOffsetY != i) {
                this.textView.setTranslationY((float) (AndroidUtilities.dp(19.0f) + top));
                this.messageTextView.setTranslationY((float) (top + AndroidUtilities.dp(56.0f)));
                RecyclerListView recyclerListView2 = this.listView;
                this.scrollOffsetY = i;
                recyclerListView2.setTopGlowOffset(i);
                this.containerView.invalidate();
            }
        }
    }

    public void dismissInternal() {
        super.dismissInternal();
        TLRPC$InputPeer tLRPC$InputPeer = this.selectAfterDismiss;
        if (tLRPC$InputPeer != null) {
            JoinCallAlertDelegate joinCallAlertDelegate = this.delegate;
            boolean z = true;
            if (this.chats.size() <= 1) {
                z = false;
            }
            joinCallAlertDelegate.didSelectChat(tLRPC$InputPeer, z, this.schedule);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return JoinCallAlert.this.chats.size();
        }

        /* JADX WARNING: type inference failed for: r9v5, types: [android.view.View, org.telegram.ui.Cells.ShareDialogCell] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r9, int r10) {
            /*
                r8 = this;
                org.telegram.ui.Components.JoinCallAlert r9 = org.telegram.ui.Components.JoinCallAlert.this
                int r9 = r9.currentType
                r10 = 2
                if (r9 != 0) goto L_0x0025
                org.telegram.ui.Cells.ShareDialogCell r9 = new org.telegram.ui.Cells.ShareDialogCell
                android.content.Context r0 = r8.context
                r9.<init>(r0, r10)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r10 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = 1117782016(0x42a00000, float:80.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r1 = 1120403456(0x42CLASSNAME, float:100.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r10.<init>((int) r0, (int) r1)
                r9.setLayoutParams(r10)
                goto L_0x003d
            L_0x0025:
                org.telegram.ui.Cells.GroupCreateUserCell r9 = new org.telegram.ui.Cells.GroupCreateUserCell
                android.content.Context r3 = r8.context
                r4 = 2
                r5 = 0
                r6 = 0
                org.telegram.ui.Components.JoinCallAlert r0 = org.telegram.ui.Components.JoinCallAlert.this
                int r0 = r0.currentType
                if (r0 != r10) goto L_0x0037
                r10 = 1
                r7 = 1
                goto L_0x0039
            L_0x0037:
                r10 = 0
                r7 = 0
            L_0x0039:
                r2 = r9
                r2.<init>(r3, r4, r5, r6, r7)
            L_0x003d:
                org.telegram.ui.Components.RecyclerListView$Holder r10 = new org.telegram.ui.Components.RecyclerListView$Holder
                r10.<init>(r9)
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinCallAlert.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int i;
            viewHolder.getAdapterPosition();
            long peerId = (long) MessageObject.getPeerId(JoinCallAlert.this.selectedPeer);
            View view = viewHolder.itemView;
            boolean z = true;
            if (view instanceof GroupCreateUserCell) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
                Object object = groupCreateUserCell.getObject();
                long j = 0;
                if (object != null) {
                    if (object instanceof TLRPC$Chat) {
                        i = -((TLRPC$Chat) object).id;
                    } else {
                        i = ((TLRPC$User) object).id;
                    }
                    j = (long) i;
                }
                if (peerId != j) {
                    z = false;
                }
                groupCreateUserCell.setChecked(z, false);
                return;
            }
            ShareDialogCell shareDialogCell = (ShareDialogCell) view;
            if (peerId != shareDialogCell.getCurrentDialog()) {
                z = false;
            }
            shareDialogCell.setChecked(z, false);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            TLObject tLObject;
            int peerId = MessageObject.getPeerId((TLRPC$Peer) JoinCallAlert.this.chats.get(i));
            if (peerId > 0) {
                tLObject = MessagesController.getInstance(JoinCallAlert.this.currentAccount).getUser(Integer.valueOf(peerId));
                str = LocaleController.getString("VoipGroupPersonalAccount", NUM);
            } else {
                tLObject = MessagesController.getInstance(JoinCallAlert.this.currentAccount).getChat(Integer.valueOf(-peerId));
                str = null;
            }
            boolean z = false;
            if (JoinCallAlert.this.currentType == 0) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) viewHolder.itemView;
                if (peerId == MessageObject.getPeerId(JoinCallAlert.this.selectedPeer)) {
                    z = true;
                }
                shareDialogCell.setDialog(peerId, z, (CharSequence) null);
                return;
            }
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
            if (i != getItemCount() - 1) {
                z = true;
            }
            groupCreateUserCell.setObject(tLObject, (CharSequence) null, str, z);
        }
    }
}
