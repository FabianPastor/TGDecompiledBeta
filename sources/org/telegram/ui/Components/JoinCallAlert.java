package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
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
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupCallJoinAs;
import org.telegram.tgnet.TLRPC$TL_phone_joinAsPeers;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.ShareDialogCell;
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
                } else if (MessageObject.getPeerId(cachedChats.get(i2)) == j) {
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
                addView(this.textView[i], LayoutHelper.createFrame(-2, -2.0f, 17, 24.0f, 0.0f, 24.0f, 0.0f));
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

    public static void checkFewUsers(Context context, long j, AccountInstance accountInstance, MessagesStorage.BooleanCallback booleanCallback) {
        if (lastCachedAccount != accountInstance.getCurrentAccount() || lastCacheDid != j || cachedChats == null || SystemClock.elapsedRealtime() - lastCacheTime >= 240000) {
            AlertDialog alertDialog = new AlertDialog(context, 3);
            TLRPC$TL_phone_getGroupCallJoinAs tLRPC$TL_phone_getGroupCallJoinAs = new TLRPC$TL_phone_getGroupCallJoinAs();
            tLRPC$TL_phone_getGroupCallJoinAs.peer = accountInstance.getMessagesController().getInputPeer(j);
            alertDialog.setOnCancelListener(new JoinCallAlert$$ExternalSyntheticLambda1(accountInstance, accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupCallJoinAs, new JoinCallAlert$$ExternalSyntheticLambda6(alertDialog, j, accountInstance, booleanCallback))));
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$checkFewUsers$0(AlertDialog alertDialog, TLObject tLObject, long j, AccountInstance accountInstance, MessagesStorage.BooleanCallback booleanCallback) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            TLRPC$TL_phone_joinAsPeers tLRPC$TL_phone_joinAsPeers = (TLRPC$TL_phone_joinAsPeers) tLObject;
            cachedChats = tLRPC$TL_phone_joinAsPeers.peers;
            lastCacheDid = j;
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

    public static void open(Context context, long j, AccountInstance accountInstance, BaseFragment baseFragment, int i, TLRPC$Peer tLRPC$Peer, JoinCallAlertDelegate joinCallAlertDelegate) {
        long j2 = j;
        JoinCallAlertDelegate joinCallAlertDelegate2 = joinCallAlertDelegate;
        if (context != null && joinCallAlertDelegate2 != null) {
            if (lastCachedAccount != accountInstance.getCurrentAccount() || lastCacheDid != j2 || cachedChats == null || SystemClock.elapsedRealtime() - lastCacheTime >= 300000) {
                AlertDialog alertDialog = new AlertDialog(context, 3);
                TLRPC$TL_phone_getGroupCallJoinAs tLRPC$TL_phone_getGroupCallJoinAs = new TLRPC$TL_phone_getGroupCallJoinAs();
                tLRPC$TL_phone_getGroupCallJoinAs.peer = accountInstance.getMessagesController().getInputPeer(j2);
                AccountInstance accountInstance2 = accountInstance;
                alertDialog.setOnCancelListener(new JoinCallAlert$$ExternalSyntheticLambda0(accountInstance2, accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupCallJoinAs, new JoinCallAlert$$ExternalSyntheticLambda7(alertDialog, accountInstance2, joinCallAlertDelegate, j, context, baseFragment, i, tLRPC$Peer))));
                try {
                    alertDialog.showDelayed(500);
                } catch (Exception unused) {
                }
            } else if (cachedChats.size() != 1 || i == 0) {
                showAlert(context, j, cachedChats, baseFragment, i, tLRPC$Peer, joinCallAlertDelegate);
            } else {
                joinCallAlertDelegate2.didSelectChat(accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(cachedChats.get(0))), false, false);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$open$3(AlertDialog alertDialog, TLObject tLObject, AccountInstance accountInstance, JoinCallAlertDelegate joinCallAlertDelegate, long j, Context context, BaseFragment baseFragment, int i, TLRPC$Peer tLRPC$Peer) {
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
            lastCacheDid = j;
            lastCacheTime = SystemClock.elapsedRealtime();
            lastCachedAccount = accountInstance.getCurrentAccount();
            accountInstance.getMessagesController().putChats(tLRPC$TL_phone_joinAsPeers.chats, false);
            accountInstance.getMessagesController().putUsers(tLRPC$TL_phone_joinAsPeers.users, false);
            showAlert(context, j, tLRPC$TL_phone_joinAsPeers.peers, baseFragment, i, tLRPC$Peer, joinCallAlertDelegate);
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
    private JoinCallAlert(android.content.Context r20, long r21, java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r23, int r24, org.telegram.tgnet.TLRPC$Peer r25, org.telegram.ui.Components.JoinCallAlert.JoinCallAlertDelegate r26) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r24
            r3 = r26
            r4 = 0
            r0.<init>(r1, r4)
            r0.setApplyBottomPadding(r4)
            java.util.ArrayList r5 = new java.util.ArrayList
            r6 = r23
            r5.<init>(r6)
            r0.chats = r5
            r0.delegate = r3
            r0.currentType = r2
            android.content.res.Resources r5 = r20.getResources()
            r6 = 2131166087(0x7var_, float:1.794641E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0.shadowDrawable = r5
            r6 = 2
            if (r2 != r6) goto L_0x00a3
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r5 == 0) goto L_0x005f
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            long r7 = r5.getSelfId()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r5 = r0.chats
            int r5 = r5.size()
            r9 = 0
        L_0x0045:
            if (r9 >= r5) goto L_0x0090
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r10 = r0.chats
            java.lang.Object r10 = r10.get(r9)
            org.telegram.tgnet.TLRPC$Peer r10 = (org.telegram.tgnet.TLRPC$Peer) r10
            long r11 = org.telegram.messenger.MessageObject.getPeerId(r10)
            int r13 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r13 != 0) goto L_0x005c
            r0.currentPeer = r10
            r0.selectedPeer = r10
            goto L_0x0090
        L_0x005c:
            int r9 = r9 + 1
            goto L_0x0045
        L_0x005f:
            if (r25 == 0) goto L_0x0086
            long r7 = org.telegram.messenger.MessageObject.getPeerId(r25)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r5 = r0.chats
            int r5 = r5.size()
            r9 = 0
        L_0x006c:
            if (r9 >= r5) goto L_0x0090
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r10 = r0.chats
            java.lang.Object r10 = r10.get(r9)
            org.telegram.tgnet.TLRPC$Peer r10 = (org.telegram.tgnet.TLRPC$Peer) r10
            long r11 = org.telegram.messenger.MessageObject.getPeerId(r10)
            int r13 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r13 != 0) goto L_0x0083
            r0.currentPeer = r10
            r0.selectedPeer = r10
            goto L_0x0090
        L_0x0083:
            int r9 = r9 + 1
            goto L_0x006c
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
            if (r5 != 0) goto L_0x00d6
            org.telegram.ui.Components.JoinCallAlert$1 r5 = new org.telegram.ui.Components.JoinCallAlert$1
            r5.<init>(r1)
            r5.setOrientation(r7)
            androidx.core.widget.NestedScrollView r8 = new androidx.core.widget.NestedScrollView
            r8.<init>(r1)
            r8.addView(r5)
            r0.setCustomView(r8)
            goto L_0x00e7
        L_0x00d6:
            org.telegram.ui.Components.JoinCallAlert$2 r5 = new org.telegram.ui.Components.JoinCallAlert$2
            r5.<init>(r1)
            r0.containerView = r5
            r5.setWillNotDraw(r4)
            android.view.ViewGroup r8 = r0.containerView
            int r9 = r0.backgroundPaddingLeft
            r8.setPadding(r9, r4, r9, r4)
        L_0x00e7:
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            r9 = r21
            long r9 = -r9
            java.lang.Long r9 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r8 = r8.getChat(r9)
            org.telegram.ui.Components.JoinCallAlert$3 r9 = new org.telegram.ui.Components.JoinCallAlert$3
            r9.<init>(r1)
            r0.listView = r9
            androidx.recyclerview.widget.LinearLayoutManager r10 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r11 = r19.getContext()
            int r12 = r0.currentType
            if (r12 != 0) goto L_0x010b
            r12 = 0
            goto L_0x010c
        L_0x010b:
            r12 = 1
        L_0x010c:
            r10.<init>(r11, r12, r4)
            r9.setLayoutManager(r10)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$ListAdapter r10 = new org.telegram.ui.Components.JoinCallAlert$ListAdapter
            r10.<init>(r1)
            r9.setAdapter(r10)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r9.setVerticalScrollBarEnabled(r4)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r9.setClipToPadding(r4)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r9.setEnabled(r7)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            java.lang.String r10 = "dialogScrollGlow"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r9.setGlowColor(r10)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$4 r10 = new org.telegram.ui.Components.JoinCallAlert$4
            r10.<init>()
            r9.setOnScrollListener(r10)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda8 r10 = new org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda8
            r10.<init>(r0, r8)
            r9.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r10)
            if (r2 == 0) goto L_0x0161
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r10 = -1
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            r12 = 51
            r13 = 0
            r14 = 1120403456(0x42CLASSNAME, float:100.0)
            r15 = 0
            r16 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r9, r10)
            goto L_0x0175
        L_0x0161:
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r9.setSelectorDrawableColor(r4)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r10 = 1092616192(0x41200000, float:10.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r9.setPadding(r11, r4, r10, r4)
        L_0x0175:
            if (r2 != 0) goto L_0x019f
            org.telegram.ui.Components.RLottieImageView r9 = new org.telegram.ui.Components.RLottieImageView
            r9.<init>(r1)
            r9.setAutoRepeat(r7)
            r10 = 2131558528(0x7f0d0080, float:1.8742374E38)
            r11 = 120(0x78, float:1.68E-43)
            r9.setAnimation(r10, r11, r11)
            r9.playAnimation()
            r12 = 160(0xa0, float:2.24E-43)
            r13 = 160(0xa0, float:2.24E-43)
            r14 = 49
            r15 = 17
            r16 = 8
            r17 = 17
            r18 = 0
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r5.addView(r9, r10)
        L_0x019f:
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r1)
            r0.textView = r9
            java.lang.String r10 = "fonts/rmedium.ttf"
            android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
            r9.setTypeface(r10)
            android.widget.TextView r9 = r0.textView
            r10 = 1101004800(0x41a00000, float:20.0)
            r9.setTextSize(r7, r10)
            if (r2 != r6) goto L_0x01c4
            android.widget.TextView r9 = r0.textView
            java.lang.String r10 = "voipgroup_nameText"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r9.setTextColor(r10)
            goto L_0x01cf
        L_0x01c4:
            android.widget.TextView r9 = r0.textView
            java.lang.String r10 = "dialogTextBlack"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r9.setTextColor(r10)
        L_0x01cf:
            android.widget.TextView r9 = r0.textView
            r9.setSingleLine(r7)
            android.widget.TextView r9 = r0.textView
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r9.setEllipsize(r10)
            if (r2 != 0) goto L_0x0216
            boolean r9 = org.telegram.messenger.ChatObject.isChannelOrGiga(r8)
            if (r9 == 0) goto L_0x01f2
            android.widget.TextView r9 = r0.textView
            r10 = 2131628006(0x7f0e0fe6, float:1.8883292E38)
            java.lang.String r11 = "StartVoipChannelTitle"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.setText(r10)
            goto L_0x0200
        L_0x01f2:
            android.widget.TextView r9 = r0.textView
            r10 = 2131628010(0x7f0e0fea, float:1.88833E38)
            java.lang.String r11 = "StartVoipChatTitle"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.setText(r10)
        L_0x0200:
            android.widget.TextView r9 = r0.textView
            r10 = -2
            r11 = -2
            r12 = 49
            r13 = 23
            r14 = 16
            r15 = 23
            r16 = 0
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
            r5.addView(r9, r10)
            goto L_0x0260
        L_0x0216:
            if (r2 != r6) goto L_0x0227
            android.widget.TextView r9 = r0.textView
            r10 = 2131628587(0x7f0e122b, float:1.888447E38)
            java.lang.String r11 = "VoipGroupDisplayAs"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.setText(r10)
            goto L_0x024a
        L_0x0227:
            boolean r9 = org.telegram.messenger.ChatObject.isChannelOrGiga(r8)
            if (r9 == 0) goto L_0x023c
            android.widget.TextView r9 = r0.textView
            r10 = 2131628517(0x7f0e11e5, float:1.8884329E38)
            java.lang.String r11 = "VoipChannelJoinAs"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.setText(r10)
            goto L_0x024a
        L_0x023c:
            android.widget.TextView r9 = r0.textView
            r10 = 2131628604(0x7f0e123c, float:1.8884505E38)
            java.lang.String r11 = "VoipGroupJoinAs"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.setText(r10)
        L_0x024a:
            android.widget.TextView r9 = r0.textView
            r10 = -2
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 51
            r13 = 1102577664(0x41b80000, float:23.0)
            r14 = 1090519040(0x41000000, float:8.0)
            r15 = 1102577664(0x41b80000, float:23.0)
            r16 = 0
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r9, r10)
        L_0x0260:
            android.widget.TextView r9 = new android.widget.TextView
            android.content.Context r10 = r19.getContext()
            r9.<init>(r10)
            r0.messageTextView = r9
            if (r2 != r6) goto L_0x0277
            java.lang.String r6 = "voipgroup_lastSeenText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r9.setTextColor(r6)
            goto L_0x0280
        L_0x0277:
            java.lang.String r6 = "dialogTextGray3"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r9.setTextColor(r6)
        L_0x0280:
            android.widget.TextView r6 = r0.messageTextView
            r9 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r7, r9)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r6 = r0.chats
            int r6 = r6.size()
            r9 = 0
        L_0x028e:
            if (r9 >= r6) goto L_0x02c0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r10 = r0.chats
            java.lang.Object r10 = r10.get(r9)
            org.telegram.tgnet.TLRPC$Peer r10 = (org.telegram.tgnet.TLRPC$Peer) r10
            long r10 = org.telegram.messenger.MessageObject.getPeerId(r10)
            r12 = 0
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 >= 0) goto L_0x02bd
            int r12 = r0.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            long r10 = -r10
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r10 = r12.getChat(r10)
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r11 == 0) goto L_0x02bb
            boolean r10 = r10.megagroup
            if (r10 == 0) goto L_0x02bd
        L_0x02bb:
            r6 = 1
            goto L_0x02c1
        L_0x02bd:
            int r9 = r9 + 1
            goto L_0x028e
        L_0x02c0:
            r6 = 0
        L_0x02c1:
            android.widget.TextView r9 = r0.messageTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r10 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r10.<init>()
            r9.setMovementMethod(r10)
            android.widget.TextView r9 = r0.messageTextView
            java.lang.String r10 = "dialogTextLink"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r9.setLinkTextColor(r10)
            r9 = 5
            if (r2 != 0) goto L_0x0343
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r10 == 0) goto L_0x02f5
            boolean r10 = r8.megagroup
            if (r10 != 0) goto L_0x02f5
            r10 = 2131628531(0x7f0e11f3, float:1.8884357E38)
            java.lang.String r11 = "VoipChannelStart2"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r6.append(r10)
            goto L_0x0301
        L_0x02f5:
            r10 = 2131628642(0x7f0e1262, float:1.8884582E38)
            java.lang.String r11 = "VoipGroupStart2"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r6.append(r10)
        L_0x0301:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r10 = r0.chats
            int r10 = r10.size()
            if (r10 <= r7) goto L_0x031b
            java.lang.String r10 = "\n\n"
            r6.append(r10)
            r10 = 2131628544(0x7f0e1200, float:1.8884384E38)
            java.lang.String r11 = "VoipChatDisplayedAs"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r6.append(r10)
            goto L_0x0322
        L_0x031b:
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            r11 = 8
            r10.setVisibility(r11)
        L_0x0322:
            android.widget.TextView r10 = r0.messageTextView
            r10.setText(r6)
            android.widget.TextView r6 = r0.messageTextView
            r10 = 49
            r6.setGravity(r10)
            android.widget.TextView r6 = r0.messageTextView
            r10 = -2
            r11 = -2
            r12 = 49
            r13 = 23
            r14 = 0
            r15 = 23
            r16 = 5
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
            r5.addView(r6, r10)
            goto L_0x0385
        L_0x0343:
            if (r6 == 0) goto L_0x0354
            android.widget.TextView r6 = r0.messageTextView
            r10 = 2131628645(0x7f0e1265, float:1.8884589E38)
            java.lang.String r11 = "VoipGroupStartAsInfoGroup"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r6.setText(r10)
            goto L_0x0362
        L_0x0354:
            android.widget.TextView r6 = r0.messageTextView
            r10 = 2131628644(0x7f0e1264, float:1.8884586E38)
            java.lang.String r11 = "VoipGroupStartAsInfo"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r6.setText(r10)
        L_0x0362:
            android.widget.TextView r6 = r0.messageTextView
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x036a
            r10 = 5
            goto L_0x036b
        L_0x036a:
            r10 = 3
        L_0x036b:
            r10 = r10 | 48
            r6.setGravity(r10)
            android.widget.TextView r6 = r0.messageTextView
            r10 = -2
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 51
            r13 = 1102577664(0x41b80000, float:23.0)
            r14 = 0
            r15 = 1102577664(0x41b80000, float:23.0)
            r16 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r6, r10)
        L_0x0385:
            if (r2 != 0) goto L_0x03a5
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r6 = r0.chats
            int r6 = r6.size()
            if (r6 >= r9) goto L_0x0394
            r6 = -2
            r9 = -2
            goto L_0x0396
        L_0x0394:
            r6 = -1
            r9 = -1
        L_0x0396:
            r10 = 95
            r11 = 49
            r12 = 0
            r13 = 6
            r14 = 0
            r15 = 0
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
            r5.addView(r2, r6)
        L_0x03a5:
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r2 = new org.telegram.ui.Components.JoinCallAlert$BottomSheetCell
            r2.<init>(r1, r4)
            r0.doneButton = r2
            android.view.View r2 = r2.background
            org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda3 r6 = new org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda3
            r6.<init>(r0, r3)
            r2.setOnClickListener(r6)
            int r2 = r0.currentType
            if (r2 != 0) goto L_0x041d
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r2 = r0.doneButton
            r9 = -1
            r10 = 50
            r11 = 51
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
            r5.addView(r2, r3)
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r2 = new org.telegram.ui.Components.JoinCallAlert$BottomSheetCell
            r2.<init>(r1, r7)
            boolean r1 = org.telegram.messenger.ChatObject.isChannelOrGiga(r8)
            if (r1 == 0) goto L_0x03e6
            r1 = 2131628527(0x7f0e11ef, float:1.888435E38)
            java.lang.String r3 = "VoipChannelScheduleVoiceChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r2.setText(r1, r4)
            goto L_0x03f2
        L_0x03e6:
            r1 = 2131628634(0x7f0e125a, float:1.8884566E38)
            java.lang.String r3 = "VoipGroupScheduleVoiceChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r2.setText(r1, r4)
        L_0x03f2:
            android.view.View r1 = r2.background
            org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda2
            r3.<init>(r0)
            r1.setOnClickListener(r3)
            r1 = -1
            r3 = 50
            r6 = 51
            r7 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r20 = r1
            r21 = r3
            r22 = r6
            r23 = r7
            r24 = r9
            r25 = r10
            r26 = r11
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
            r5.addView(r2, r1)
            goto L_0x043d
        L_0x041d:
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r1 = r0.doneButton
            r2 = -1
            r3 = 1112014848(0x42480000, float:50.0)
            r6 = 83
            r7 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r20 = r2
            r21 = r3
            r22 = r6
            r23 = r7
            r24 = r9
            r25 = r10
            r26 = r11
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r5.addView(r1, r2)
        L_0x043d:
            r0.updateDoneButton(r4, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinCallAlert.<init>(android.content.Context, long, java.util.ArrayList, int, org.telegram.tgnet.TLRPC$Peer, org.telegram.ui.Components.JoinCallAlert$JoinCallAlertDelegate):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(TLRPC$Chat tLRPC$Chat, View view, int i) {
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
                updateDoneButton(true, tLRPC$Chat);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(JoinCallAlertDelegate joinCallAlertDelegate, View view) {
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
    public /* synthetic */ void lambda$new$8(View view) {
        this.selectAfterDismiss = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(this.selectedPeer));
        this.schedule = true;
        dismiss();
    }

    private void updateDoneButton(boolean z, TLRPC$Chat tLRPC$Chat) {
        if (this.currentType != 0) {
            long peerId = MessageObject.getPeerId(this.selectedPeer);
            if (DialogObject.isUserDialog(peerId)) {
                this.doneButton.setText(LocaleController.formatString("VoipGroupContinueAs", NUM, UserObject.getFirstName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerId)))), z);
                return;
            }
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-peerId));
            BottomSheetCell bottomSheetCell = this.doneButton;
            Object[] objArr = new Object[1];
            objArr[0] = chat != null ? chat.title : "";
            bottomSheetCell.setText(LocaleController.formatString("VoipGroupContinueAs", NUM, objArr), z);
        } else if (ChatObject.isChannelOrGiga(tLRPC$Chat)) {
            this.doneButton.setText(LocaleController.formatString("VoipChannelStartVoiceChat", NUM, new Object[0]), z);
        } else {
            this.doneButton.setText(LocaleController.formatString("VoipGroupStartVoiceChat", NUM, new Object[0]), z);
        }
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
                if (r9 != 0) goto L_0x0026
                org.telegram.ui.Cells.ShareDialogCell r9 = new org.telegram.ui.Cells.ShareDialogCell
                android.content.Context r0 = r8.context
                r1 = 0
                r9.<init>(r0, r10, r1)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r10 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = 1117782016(0x42a00000, float:80.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r1 = 1120403456(0x42CLASSNAME, float:100.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r10.<init>((int) r0, (int) r1)
                r9.setLayoutParams(r10)
                goto L_0x003e
            L_0x0026:
                org.telegram.ui.Cells.GroupCreateUserCell r9 = new org.telegram.ui.Cells.GroupCreateUserCell
                android.content.Context r3 = r8.context
                r4 = 2
                r5 = 0
                r6 = 0
                org.telegram.ui.Components.JoinCallAlert r0 = org.telegram.ui.Components.JoinCallAlert.this
                int r0 = r0.currentType
                if (r0 != r10) goto L_0x0038
                r10 = 1
                r7 = 1
                goto L_0x003a
            L_0x0038:
                r10 = 0
                r7 = 0
            L_0x003a:
                r2 = r9
                r2.<init>(r3, r4, r5, r6, r7)
            L_0x003e:
                org.telegram.ui.Components.RecyclerListView$Holder r10 = new org.telegram.ui.Components.RecyclerListView$Holder
                r10.<init>(r9)
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinCallAlert.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            viewHolder.getAdapterPosition();
            long peerId = MessageObject.getPeerId(JoinCallAlert.this.selectedPeer);
            View view = viewHolder.itemView;
            boolean z = true;
            if (view instanceof GroupCreateUserCell) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
                Object object = groupCreateUserCell.getObject();
                long j = 0;
                if (object != null) {
                    if (object instanceof TLRPC$Chat) {
                        j = -((TLRPC$Chat) object).id;
                    } else {
                        j = ((TLRPC$User) object).id;
                    }
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
            long peerId = MessageObject.getPeerId((TLRPC$Peer) JoinCallAlert.this.chats.get(i));
            if (peerId > 0) {
                tLObject = MessagesController.getInstance(JoinCallAlert.this.currentAccount).getUser(Long.valueOf(peerId));
                str = LocaleController.getString("VoipGroupPersonalAccount", NUM);
            } else {
                tLObject = MessagesController.getInstance(JoinCallAlert.this.currentAccount).getChat(Long.valueOf(-peerId));
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
