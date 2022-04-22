package org.telegram.ui.Components;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ProfileActivity;

public class BackButtonMenu {

    public static class PulledDialog<T> {
        Class<T> activity;
        TLRPC$Chat chat;
        long dialogId;
        int filterId;
        int folderId;
        int stackIndex;
        TLRPC$User user;
    }

    public static ActionBarPopupWindow show(BaseFragment baseFragment, View view, long j) {
        ArrayList<PulledDialog> arrayList;
        String str;
        BaseFragment baseFragment2 = baseFragment;
        if (baseFragment2 == null) {
            return null;
        }
        ActionBarLayout parentLayout = baseFragment.getParentLayout();
        Activity parentActivity = baseFragment.getParentActivity();
        View fragmentView = baseFragment.getFragmentView();
        if (parentLayout == null || parentActivity == null || fragmentView == null) {
            return null;
        }
        ArrayList<PulledDialog> stackedHistoryDialogs = getStackedHistoryDialogs(baseFragment2, j);
        if (stackedHistoryDialogs.size() <= 0) {
            return null;
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(parentActivity);
        Rect rect = new Rect();
        baseFragment.getParentActivity().getResources().getDrawable(NUM).mutate().getPadding(rect);
        actionBarPopupWindowLayout.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
        AtomicReference atomicReference = new AtomicReference();
        int i = 0;
        while (i < stackedHistoryDialogs.size()) {
            PulledDialog pulledDialog = stackedHistoryDialogs.get(i);
            TLRPC$Chat tLRPC$Chat = pulledDialog.chat;
            TLRPC$User tLRPC$User = pulledDialog.user;
            FrameLayout frameLayout = new FrameLayout(parentActivity);
            frameLayout.setMinimumWidth(AndroidUtilities.dp(200.0f));
            BackupImageView backupImageView = new BackupImageView(parentActivity);
            backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
            frameLayout.addView(backupImageView, LayoutHelper.createFrameRelatively(32.0f, 32.0f, 8388627, 13.0f, 0.0f, 0.0f, 0.0f));
            TextView textView = new TextView(parentActivity);
            textView.setLines(1);
            Activity activity = parentActivity;
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView.setEllipsize(TextUtils.TruncateAt.END);
            frameLayout.addView(textView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388627, 59.0f, 0.0f, 12.0f, 0.0f));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setSmallSize(true);
            if (tLRPC$Chat != null) {
                avatarDrawable.setInfo(tLRPC$Chat);
                backupImageView.setImage(ImageLocation.getForChat(tLRPC$Chat, 1), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$Chat);
                textView.setText(tLRPC$Chat.title);
            } else if (tLRPC$User != null) {
                arrayList = stackedHistoryDialogs;
                if (pulledDialog.activity == ChatActivity.class && UserObject.isUserSelf(tLRPC$User)) {
                    str = LocaleController.getString("SavedMessages", NUM);
                    avatarDrawable.setAvatarType(1);
                    backupImageView.setImageDrawable(avatarDrawable);
                } else if (UserObject.isReplyUser(tLRPC$User)) {
                    str = LocaleController.getString("RepliesTitle", NUM);
                    avatarDrawable.setAvatarType(12);
                    backupImageView.setImageDrawable(avatarDrawable);
                } else if (UserObject.isDeleted(tLRPC$User)) {
                    str = LocaleController.getString("HiddenName", NUM);
                    avatarDrawable.setInfo(tLRPC$User);
                    backupImageView.setImage(ImageLocation.getForUser(tLRPC$User, 1), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$User);
                } else {
                    str = UserObject.getUserName(tLRPC$User);
                    avatarDrawable.setInfo(tLRPC$User);
                    backupImageView.setImage(ImageLocation.getForUser(tLRPC$User, 1), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$User);
                }
                textView.setText(str);
                frameLayout.setBackground(Theme.getSelectorDrawable(Theme.getColor("listSelectorSDK21"), false));
                frameLayout.setOnClickListener(new BackButtonMenu$$ExternalSyntheticLambda0(atomicReference, pulledDialog, parentLayout, baseFragment2));
                actionBarPopupWindowLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 48));
                i++;
                parentActivity = activity;
                stackedHistoryDialogs = arrayList;
            }
            arrayList = stackedHistoryDialogs;
            frameLayout.setBackground(Theme.getSelectorDrawable(Theme.getColor("listSelectorSDK21"), false));
            frameLayout.setOnClickListener(new BackButtonMenu$$ExternalSyntheticLambda0(atomicReference, pulledDialog, parentLayout, baseFragment2));
            actionBarPopupWindowLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 48));
            i++;
            parentActivity = activity;
            stackedHistoryDialogs = arrayList;
        }
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
        atomicReference.set(actionBarPopupWindow);
        actionBarPopupWindow.setPauseNotifications(true);
        actionBarPopupWindow.setDismissAnimationDuration(220);
        actionBarPopupWindow.setOutsideTouchable(true);
        actionBarPopupWindow.setClippingEnabled(true);
        actionBarPopupWindow.setAnimationStyle(NUM);
        actionBarPopupWindow.setFocusable(true);
        actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        actionBarPopupWindow.setInputMethodMode(2);
        actionBarPopupWindow.setSoftInputMode(0);
        actionBarPopupWindow.getContentView().setFocusableInTouchMode(true);
        actionBarPopupWindowLayout.setFitItems(true);
        int dp = AndroidUtilities.dp(8.0f) - rect.left;
        if (AndroidUtilities.isTablet()) {
            int[] iArr = new int[2];
            fragmentView.getLocationInWindow(iArr);
            dp += iArr[0];
        }
        actionBarPopupWindow.showAtLocation(fragmentView, 51, dp, (view.getBottom() - rect.top) - AndroidUtilities.dp(8.0f));
        try {
            fragmentView.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        return actionBarPopupWindow;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$show$0(AtomicReference atomicReference, PulledDialog pulledDialog, ActionBarLayout actionBarLayout, BaseFragment baseFragment, View view) {
        ArrayList<BaseFragment> arrayList;
        int i;
        ArrayList<BaseFragment> arrayList2;
        Long l = null;
        if (atomicReference.get() != null) {
            ((ActionBarPopupWindow) atomicReference.getAndSet((Object) null)).dismiss();
        }
        int i2 = pulledDialog.stackIndex;
        if (i2 >= 0) {
            if (!(actionBarLayout == null || (arrayList2 = actionBarLayout.fragmentsStack) == null || i2 >= arrayList2.size())) {
                BaseFragment baseFragment2 = actionBarLayout.fragmentsStack.get(pulledDialog.stackIndex);
                if (baseFragment2 instanceof ChatActivity) {
                    l = Long.valueOf(((ChatActivity) baseFragment2).getDialogId());
                } else if (baseFragment2 instanceof ProfileActivity) {
                    l = Long.valueOf(((ProfileActivity) baseFragment2).getDialogId());
                }
            }
            if (l != null && l.longValue() != pulledDialog.dialogId) {
                for (int size = actionBarLayout.fragmentsStack.size() - 2; size > pulledDialog.stackIndex; size--) {
                    actionBarLayout.removeFragmentFromStack(size);
                }
            } else if (!(actionBarLayout == null || (arrayList = actionBarLayout.fragmentsStack) == null)) {
                int size2 = arrayList.size() - 2;
                while (true) {
                    i = pulledDialog.stackIndex;
                    if (size2 <= i) {
                        break;
                    }
                    if (size2 >= 0 && size2 < actionBarLayout.fragmentsStack.size()) {
                        actionBarLayout.removeFragmentFromStack(size2);
                    }
                    size2--;
                }
                if (i < actionBarLayout.fragmentsStack.size()) {
                    actionBarLayout.showFragment(pulledDialog.stackIndex);
                    actionBarLayout.closeLastFragment(true);
                    return;
                }
            }
        }
        goToPulledDialog(baseFragment, pulledDialog);
    }

    public static void goToPulledDialog(BaseFragment baseFragment, PulledDialog pulledDialog) {
        if (pulledDialog != null) {
            Class<T> cls = pulledDialog.activity;
            if (cls == ChatActivity.class) {
                Bundle bundle = new Bundle();
                TLRPC$Chat tLRPC$Chat = pulledDialog.chat;
                if (tLRPC$Chat != null) {
                    bundle.putLong("chat_id", tLRPC$Chat.id);
                } else {
                    TLRPC$User tLRPC$User = pulledDialog.user;
                    if (tLRPC$User != null) {
                        bundle.putLong("user_id", tLRPC$User.id);
                    }
                }
                bundle.putInt("dialog_folder_id", pulledDialog.folderId);
                bundle.putInt("dialog_filter_id", pulledDialog.filterId);
                baseFragment.presentFragment(new ChatActivity(bundle), true);
            } else if (cls == ProfileActivity.class) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong("dialog_id", pulledDialog.dialogId);
                baseFragment.presentFragment(new ProfileActivity(bundle2), true);
            }
        }
    }

    public static ArrayList<PulledDialog> getStackedHistoryDialogs(BaseFragment baseFragment, long j) {
        ActionBarLayout parentLayout;
        boolean z;
        int i;
        long j2;
        TLRPC$User tLRPC$User;
        TLRPC$Chat tLRPC$Chat;
        Class cls;
        int i2;
        boolean z2;
        ArrayList<PulledDialog> arrayList = new ArrayList<>();
        if (baseFragment == null || (parentLayout = baseFragment.getParentLayout()) == null) {
            return arrayList;
        }
        ArrayList<BaseFragment> arrayList2 = parentLayout.fragmentsStack;
        ArrayList<PulledDialog> arrayList3 = parentLayout.pulledDialogs;
        if (arrayList2 != null) {
            int size = arrayList2.size();
            for (int i3 = 0; i3 < size; i3++) {
                BaseFragment baseFragment2 = arrayList2.get(i3);
                TLRPC$User tLRPC$User2 = null;
                if (baseFragment2 instanceof ChatActivity) {
                    cls = ChatActivity.class;
                    ChatActivity chatActivity = (ChatActivity) baseFragment2;
                    if (chatActivity.getChatMode() == 0 && !chatActivity.isReport()) {
                        tLRPC$Chat = chatActivity.getCurrentChat();
                        tLRPC$User = chatActivity.getCurrentUser();
                        j2 = chatActivity.getDialogId();
                        i = chatActivity.getDialogFolderId();
                        i2 = chatActivity.getDialogFilterId();
                    }
                } else if (baseFragment2 instanceof ProfileActivity) {
                    Class cls2 = ProfileActivity.class;
                    ProfileActivity profileActivity = (ProfileActivity) baseFragment2;
                    TLRPC$Chat currentChat = profileActivity.getCurrentChat();
                    try {
                        tLRPC$User2 = profileActivity.getUserInfo().user;
                    } catch (Exception unused) {
                    }
                    j2 = profileActivity.getDialogId();
                    i2 = 0;
                    i = 0;
                    TLRPC$Chat tLRPC$Chat2 = currentChat;
                    tLRPC$User = tLRPC$User2;
                    cls = cls2;
                    tLRPC$Chat = tLRPC$Chat2;
                }
                if (j2 != j && (j != 0 || !UserObject.isUserSelf(tLRPC$User))) {
                    int i4 = 0;
                    while (true) {
                        if (i4 >= arrayList.size()) {
                            z2 = false;
                            break;
                        } else if (arrayList.get(i4).dialogId == j2) {
                            z2 = true;
                            break;
                        } else {
                            i4++;
                        }
                    }
                    if (!z2) {
                        PulledDialog pulledDialog = new PulledDialog();
                        pulledDialog.activity = cls;
                        pulledDialog.stackIndex = i3;
                        pulledDialog.chat = tLRPC$Chat;
                        pulledDialog.user = tLRPC$User;
                        pulledDialog.dialogId = j2;
                        pulledDialog.folderId = i;
                        pulledDialog.filterId = i2;
                        if (tLRPC$Chat != null || tLRPC$User != null) {
                            arrayList.add(pulledDialog);
                        }
                    }
                }
            }
        }
        if (arrayList3 != null) {
            Iterator<PulledDialog> it = arrayList3.iterator();
            while (it.hasNext()) {
                PulledDialog next = it.next();
                if (next.dialogId != j) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= arrayList.size()) {
                            z = false;
                            break;
                        } else if (arrayList.get(i5).dialogId == next.dialogId) {
                            z = true;
                            break;
                        } else {
                            i5++;
                        }
                    }
                    if (!z) {
                        arrayList.add(next);
                    }
                }
            }
        }
        Collections.sort(arrayList, BackButtonMenu$$ExternalSyntheticLambda1.INSTANCE);
        return arrayList;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$getStackedHistoryDialogs$1(PulledDialog pulledDialog, PulledDialog pulledDialog2) {
        return pulledDialog2.stackIndex - pulledDialog.stackIndex;
    }

    public static void addToPulledDialogs(BaseFragment baseFragment, int i, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, long j, int i2, int i3) {
        ActionBarLayout parentLayout;
        if ((tLRPC$Chat != null || tLRPC$User != null) && baseFragment != null && (parentLayout = baseFragment.getParentLayout()) != null) {
            if (parentLayout.pulledDialogs == null) {
                parentLayout.pulledDialogs = new ArrayList<>();
            }
            boolean z = false;
            Iterator<PulledDialog> it = parentLayout.pulledDialogs.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().dialogId == j) {
                        z = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!z) {
                PulledDialog pulledDialog = new PulledDialog();
                pulledDialog.activity = ChatActivity.class;
                pulledDialog.stackIndex = i;
                pulledDialog.dialogId = j;
                pulledDialog.filterId = i3;
                pulledDialog.folderId = i2;
                pulledDialog.chat = tLRPC$Chat;
                pulledDialog.user = tLRPC$User;
                parentLayout.pulledDialogs.add(pulledDialog);
            }
        }
    }

    public static void clearPulledDialogs(BaseFragment baseFragment, int i) {
        ActionBarLayout parentLayout;
        if (baseFragment != null && (parentLayout = baseFragment.getParentLayout()) != null && parentLayout.pulledDialogs != null) {
            int i2 = 0;
            while (i2 < parentLayout.pulledDialogs.size()) {
                if (parentLayout.pulledDialogs.get(i2).stackIndex > i) {
                    parentLayout.pulledDialogs.remove(i2);
                    i2--;
                }
                i2++;
            }
        }
    }
}
