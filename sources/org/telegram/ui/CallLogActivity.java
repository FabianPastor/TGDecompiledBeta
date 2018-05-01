package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhoneCallDiscardReason;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterPhoneCalls;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

public class CallLogActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int TYPE_IN = 1;
    private static final int TYPE_MISSED = 2;
    private static final int TYPE_OUT = 0;
    private OnClickListener callBtnClickListener = new C08491();
    private ArrayList<CallLogRow> calls = new ArrayList();
    private EmptyTextProgressView emptyView;
    private boolean endReached;
    private boolean firstLoaded;
    private ImageView floatingButton;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private Drawable greenDrawable;
    private Drawable greenDrawable2;
    private ImageSpan iconIn;
    private ImageSpan iconMissed;
    private ImageSpan iconOut;
    private User lastCallUser;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loading;
    private int prevPosition;
    private int prevTop;
    private Drawable redDrawable;
    private boolean scrollUpdated;

    /* renamed from: org.telegram.ui.CallLogActivity$1 */
    class C08491 implements OnClickListener {
        C08491() {
        }

        public void onClick(View view) {
            VoIPHelper.startCall(CallLogActivity.this.lastCallUser = ((CallLogRow) view.getTag()).user, CallLogActivity.this.getParentActivity(), null);
        }
    }

    /* renamed from: org.telegram.ui.CallLogActivity$6 */
    class C08526 extends ViewOutlineProvider {
        C08526() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.CallLogActivity$7 */
    class C08537 implements OnClickListener {

        /* renamed from: org.telegram.ui.CallLogActivity$7$1 */
        class C19341 implements ContactsActivityDelegate {
            C19341() {
            }

            public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
                VoIPHelper.startCall(user, CallLogActivity.this.getParentActivity(), null);
            }
        }

        C08537() {
        }

        public void onClick(View view) {
            view = new Bundle();
            view.putBoolean("destroyAfterSelect", true);
            view.putBoolean("returnAsResult", true);
            view.putBoolean("onlyUsers", true);
            BaseFragment contactsActivity = new ContactsActivity(view);
            contactsActivity.setDelegate(new C19341());
            CallLogActivity.this.presentFragment(contactsActivity);
        }
    }

    private class CallLogRow {
        public List<Message> calls;
        public int type;
        public User user;

        private CallLogRow() {
        }
    }

    private class CustomCell extends FrameLayout {
        public CustomCell(Context context) {
            super(context);
        }
    }

    private class ViewItem {
        public ImageView button;
        public ProfileSearchCell cell;

        public ViewItem(ImageView imageView, ProfileSearchCell profileSearchCell) {
            this.button = imageView;
            this.cell = profileSearchCell;
        }
    }

    /* renamed from: org.telegram.ui.CallLogActivity$2 */
    class C19302 extends ActionBarMenuOnItemClick {
        C19302() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                CallLogActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.CallLogActivity$3 */
    class C19313 implements OnItemClickListener {
        C19313() {
        }

        public void onItemClick(View view, int i) {
            if (i >= 0) {
                if (i < CallLogActivity.this.calls.size()) {
                    CallLogRow callLogRow = (CallLogRow) CallLogActivity.this.calls.get(i);
                    i = new Bundle();
                    i.putInt("user_id", callLogRow.user.id);
                    i.putInt("message_id", ((Message) callLogRow.calls.get(0)).id);
                    NotificationCenter.getInstance(CallLogActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    CallLogActivity.this.presentFragment(new ChatActivity(i), 1);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.CallLogActivity$4 */
    class C19324 implements OnItemLongClickListener {
        C19324() {
        }

        public boolean onItemClick(View view, int i) {
            if (i >= 0) {
                if (i < CallLogActivity.this.calls.size()) {
                    final CallLogRow callLogRow = (CallLogRow) CallLogActivity.this.calls.get(i);
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(LocaleController.getString("Delete", C0446R.string.Delete));
                    if (VoIPHelper.canRateCall((TL_messageActionPhoneCall) ((Message) callLogRow.calls.get(0)).action) != null) {
                        arrayList.add(LocaleController.getString("CallMessageReportProblem", C0446R.string.CallMessageReportProblem));
                    }
                    new Builder(CallLogActivity.this.getParentActivity()).setTitle(LocaleController.getString("Calls", C0446R.string.Calls)).setItems((CharSequence[]) arrayList.toArray(new String[arrayList.size()]), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    CallLogActivity.this.confirmAndDelete(callLogRow);
                                    return;
                                case 1:
                                    VoIPHelper.showRateAlert(CallLogActivity.this.getParentActivity(), (TL_messageActionPhoneCall) ((Message) callLogRow.calls.get(0)).action);
                                    return;
                                default:
                                    return;
                            }
                        }
                    }).show();
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.CallLogActivity$5 */
    class C19335 extends OnScrollListener {
        C19335() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            int i3;
            i = CallLogActivity.this.layoutManager.findFirstVisibleItemPosition();
            i2 = 0;
            if (i == -1) {
                i3 = 0;
            } else {
                i3 = Math.abs(CallLogActivity.this.layoutManager.findLastVisibleItemPosition() - i) + 1;
            }
            if (i3 > 0) {
                int itemCount = CallLogActivity.this.listViewAdapter.getItemCount();
                if (!(CallLogActivity.this.endReached || CallLogActivity.this.loading || CallLogActivity.this.calls.isEmpty() || i3 + i < itemCount - 5)) {
                    final CallLogRow callLogRow = (CallLogRow) CallLogActivity.this.calls.get(CallLogActivity.this.calls.size() - 1);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            CallLogActivity.this.getCalls(((Message) callLogRow.calls.get(callLogRow.calls.size() - 1)).id, 100);
                        }
                    });
                }
            }
            if (CallLogActivity.this.floatingButton.getVisibility() != 8) {
                boolean z;
                recyclerView = recyclerView.getChildAt(0);
                recyclerView = recyclerView != null ? recyclerView.getTop() : null;
                if (CallLogActivity.this.prevPosition == i) {
                    i3 = CallLogActivity.this.prevTop - recyclerView;
                    z = recyclerView < CallLogActivity.this.prevTop;
                    if (Math.abs(i3) > 1) {
                    }
                    if (!(i2 == 0 || CallLogActivity.this.scrollUpdated == 0)) {
                        CallLogActivity.this.hideFloatingButton(z);
                    }
                    CallLogActivity.this.prevPosition = i;
                    CallLogActivity.this.prevTop = recyclerView;
                    CallLogActivity.this.scrollUpdated = true;
                }
                z = i > CallLogActivity.this.prevPosition;
                i2 = 1;
                CallLogActivity.this.hideFloatingButton(z);
                CallLogActivity.this.prevPosition = i;
                CallLogActivity.this.prevTop = recyclerView;
                CallLogActivity.this.scrollUpdated = true;
            }
        }
    }

    /* renamed from: org.telegram.ui.CallLogActivity$8 */
    class C19358 implements RequestDelegate {
        C19358() {
        }

        public void run(final TLObject tLObject, final TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (tL_error == null) {
                        int i;
                        SparseArray sparseArray = new SparseArray();
                        messages_Messages messages_messages = (messages_Messages) tLObject;
                        CallLogActivity.this.endReached = messages_messages.messages.isEmpty();
                        for (i = 0; i < messages_messages.users.size(); i++) {
                            User user = (User) messages_messages.users.get(i);
                            sparseArray.put(user.id, user);
                        }
                        CallLogRow callLogRow = CallLogActivity.this.calls.size() > 0 ? (CallLogRow) CallLogActivity.this.calls.get(CallLogActivity.this.calls.size() - 1) : null;
                        for (i = 0; i < messages_messages.messages.size(); i++) {
                            Message message = (Message) messages_messages.messages.get(i);
                            if (message.action != null) {
                                if (!(message.action instanceof TL_messageActionHistoryClear)) {
                                    int i2 = message.from_id != UserConfig.getInstance(CallLogActivity.this.currentAccount).getClientUserId();
                                    PhoneCallDiscardReason phoneCallDiscardReason = message.action.reason;
                                    if (i2 == 1 && ((phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonMissed) || (phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonBusy))) {
                                        i2 = 2;
                                    }
                                    int i3 = message.from_id == UserConfig.getInstance(CallLogActivity.this.currentAccount).getClientUserId() ? message.to_id.user_id : message.from_id;
                                    if (!(callLogRow != null && callLogRow.user.id == i3 && callLogRow.type == i2)) {
                                        if (!(callLogRow == null || CallLogActivity.this.calls.contains(callLogRow))) {
                                            CallLogActivity.this.calls.add(callLogRow);
                                        }
                                        callLogRow = new CallLogRow();
                                        callLogRow.calls = new ArrayList();
                                        callLogRow.user = (User) sparseArray.get(i3);
                                        callLogRow.type = i2;
                                    }
                                    callLogRow.calls.add(message);
                                }
                            }
                        }
                        if (!(callLogRow == null || callLogRow.calls.size() <= 0 || CallLogActivity.this.calls.contains(callLogRow))) {
                            CallLogActivity.this.calls.add(callLogRow);
                        }
                    } else {
                        CallLogActivity.this.endReached = true;
                    }
                    CallLogActivity.this.loading = false;
                    CallLogActivity.this.firstLoaded = true;
                    if (CallLogActivity.this.emptyView != null) {
                        CallLogActivity.this.emptyView.showTextView();
                    }
                    if (CallLogActivity.this.listViewAdapter != null) {
                        CallLogActivity.this.listViewAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() != CallLogActivity.this.calls.size() ? true : null;
        }

        public int getItemCount() {
            int size = CallLogActivity.this.calls.size();
            return (CallLogActivity.this.calls.isEmpty() || CallLogActivity.this.endReached) ? size : size + 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new CustomCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    i = new ProfileSearchCell(this.mContext);
                    i.setPaddingRight(AndroidUtilities.dp(32.0f));
                    viewGroup.addView(i);
                    View imageView = new ImageView(this.mContext);
                    imageView.setImageResource(C0446R.drawable.profile_phone);
                    imageView.setAlpha(214);
                    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), Mode.MULTIPLY));
                    imageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
                    imageView.setScaleType(ScaleType.CENTER);
                    imageView.setOnClickListener(CallLogActivity.this.callBtnClickListener);
                    viewGroup.addView(imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 16, 8.0f, 0.0f, 8.0f, 0.0f));
                    viewGroup.setTag(new ViewItem(imageView, i));
                    break;
                case 1:
                    viewGroup = new LoadingCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    viewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                CharSequence spannableString;
                ViewItem viewItem = (ViewItem) viewHolder.itemView.getTag();
                ProfileSearchCell profileSearchCell = viewItem.cell;
                CallLogRow callLogRow = (CallLogRow) CallLogActivity.this.calls.get(i);
                boolean z = false;
                Message message = (Message) callLogRow.calls.get(0);
                String str = LocaleController.isRTL ? "\u202b" : TtmlNode.ANONYMOUS_REGION_ID;
                StringBuilder stringBuilder;
                if (callLogRow.calls.size() == 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append("  ");
                    stringBuilder.append(LocaleController.formatDateCallLog((long) message.date));
                    spannableString = new SpannableString(stringBuilder.toString());
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append("  (%d) %s");
                    spannableString = new SpannableString(String.format(stringBuilder.toString(), new Object[]{Integer.valueOf(callLogRow.calls.size()), LocaleController.formatDateCallLog((long) message.date)}));
                }
                CharSequence charSequence = spannableString;
                switch (callLogRow.type) {
                    case 0:
                        charSequence.setSpan(CallLogActivity.this.iconOut, str.length(), str.length() + 1, 0);
                        break;
                    case 1:
                        charSequence.setSpan(CallLogActivity.this.iconIn, str.length(), str.length() + 1, 0);
                        break;
                    case 2:
                        charSequence.setSpan(CallLogActivity.this.iconMissed, str.length(), str.length() + 1, 0);
                        break;
                    default:
                        break;
                }
                profileSearchCell.setData(callLogRow.user, null, null, charSequence, false, false);
                if (i != CallLogActivity.this.calls.size() - 1 || CallLogActivity.this.endReached == 0) {
                    z = true;
                }
                profileSearchCell.useSeparator = z;
                viewItem.button.setTag(callLogRow);
            }
        }

        public int getItemViewType(int i) {
            if (i < CallLogActivity.this.calls.size()) {
                return 0;
            }
            return (CallLogActivity.this.endReached || i != CallLogActivity.this.calls.size()) ? 2 : 1;
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.didReceivedNewMessages && this.firstLoaded != 0) {
            i = ((ArrayList) objArr[1]).iterator();
            while (i.hasNext() != 0) {
                MessageObject messageObject = (MessageObject) i.next();
                if (!(messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionPhoneCall) == null)) {
                    CallLogRow callLogRow;
                    objArr = messageObject.messageOwner.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? messageObject.messageOwner.to_id.user_id : messageObject.messageOwner.from_id;
                    int i4 = messageObject.messageOwner.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? 0 : 1;
                    PhoneCallDiscardReason phoneCallDiscardReason = messageObject.messageOwner.action.reason;
                    if (i4 == 1 && ((phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonMissed) || (phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonBusy))) {
                        i4 = 2;
                    }
                    if (this.calls.size() > 0) {
                        callLogRow = (CallLogRow) this.calls.get(0);
                        if (callLogRow.user.id == objArr && callLogRow.type == i4) {
                            callLogRow.calls.add(0, messageObject.messageOwner);
                            this.listViewAdapter.notifyItemChanged(0);
                        }
                    }
                    callLogRow = new CallLogRow();
                    callLogRow.calls = new ArrayList();
                    callLogRow.calls.add(messageObject.messageOwner);
                    callLogRow.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(objArr));
                    callLogRow.type = i4;
                    this.calls.add(0, callLogRow);
                    this.listViewAdapter.notifyItemInserted(0);
                }
            }
        } else if (i == NotificationCenter.messagesDeleted && this.firstLoaded != 0) {
            ArrayList arrayList = (ArrayList) objArr[0];
            i2 = this.calls.iterator();
            while (i2.hasNext() != null) {
                CallLogRow callLogRow2 = (CallLogRow) i2.next();
                Iterator it = callLogRow2.calls.iterator();
                while (it.hasNext()) {
                    if (arrayList.contains(Integer.valueOf(((Message) it.next()).id))) {
                        it.remove();
                        i3 = 1;
                    }
                }
                if (callLogRow2.calls.size() == null) {
                    i2.remove();
                }
            }
            if (i3 != 0 && this.listViewAdapter != 0) {
                this.listViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getCalls(0, 50);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
    }

    public View createView(Context context) {
        this.greenDrawable = getParentActivity().getResources().getDrawable(C0446R.drawable.ic_call_made_green_18dp).mutate();
        this.greenDrawable.setBounds(0, 0, this.greenDrawable.getIntrinsicWidth(), this.greenDrawable.getIntrinsicHeight());
        this.greenDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_calls_callReceivedGreenIcon), Mode.MULTIPLY));
        this.iconOut = new ImageSpan(this.greenDrawable, 0);
        this.greenDrawable2 = getParentActivity().getResources().getDrawable(C0446R.drawable.ic_call_received_green_18dp).mutate();
        this.greenDrawable2.setBounds(0, 0, this.greenDrawable2.getIntrinsicWidth(), this.greenDrawable2.getIntrinsicHeight());
        this.greenDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_calls_callReceivedGreenIcon), Mode.MULTIPLY));
        this.iconIn = new ImageSpan(this.greenDrawable2, 0);
        this.redDrawable = getParentActivity().getResources().getDrawable(C0446R.drawable.ic_call_received_green_18dp).mutate();
        this.redDrawable.setBounds(0, 0, this.redDrawable.getIntrinsicWidth(), this.redDrawable.getIntrinsicHeight());
        this.redDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_calls_callReceivedRedIcon), Mode.MULTIPLY));
        this.iconMissed = new ImageSpan(this.redDrawable, 0);
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Calls", C0446R.string.Calls));
        this.actionBar.setActionBarMenuOnItemClick(new C19302());
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoCallLog", C0446R.string.NoCallLog));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C19313());
        this.listView.setOnItemLongClickListener(new C19324());
        this.listView.setOnScrollListener(new C19335());
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.floatingButton = new ImageView(context);
        this.floatingButton.setVisibility(0);
        this.floatingButton.setScaleType(ScaleType.CENTER);
        float f = 56.0f;
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            context = context.getResources().getDrawable(C0446R.drawable.floating_shadow).mutate();
            context.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(context, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
        this.floatingButton.setImageResource(C0446R.drawable.ic_call_white_24dp);
        if (VERSION.SDK_INT >= 21) {
            context = new StateListAnimator();
            context.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            context.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(context);
            this.floatingButton.setOutlineProvider(new C08526());
        }
        context = this.floatingButton;
        int i = VERSION.SDK_INT >= 21 ? 56 : 60;
        if (VERSION.SDK_INT < 21) {
            f = 60.0f;
        }
        frameLayout.addView(context, LayoutHelper.createFrame(i, f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.floatingButton.setOnClickListener(new C08537());
        return this.fragmentView;
    }

    private void hideFloatingButton(boolean z) {
        if (this.floatingHidden != z) {
            this.floatingHidden = z;
            ImageView imageView = this.floatingButton;
            String str = "translationY";
            float[] fArr = new float[1];
            fArr[0] = this.floatingHidden ? (float) AndroidUtilities.dp(100.0f) : 0.0f;
            ObjectAnimator duration = ObjectAnimator.ofFloat(imageView, str, fArr).setDuration(300);
            duration.setInterpolator(this.floatingInterpolator);
            this.floatingButton.setClickable(z ^ true);
            duration.start();
        }
    }

    private void getCalls(int i, int i2) {
        if (!this.loading) {
            this.loading = true;
            if (!(this.emptyView == null || this.firstLoaded)) {
                this.emptyView.showProgress();
            }
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
            TLObject tL_messages_search = new TL_messages_search();
            tL_messages_search.limit = i2;
            tL_messages_search.peer = new TL_inputPeerEmpty();
            tL_messages_search.filter = new TL_inputMessagesFilterPhoneCalls();
            tL_messages_search.f49q = TtmlNode.ANONYMOUS_REGION_ID;
            tL_messages_search.offset_id = i;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_search, new C19358(), 2), this.classGuid);
        }
    }

    private void confirmAndDelete(final CallLogRow callLogRow) {
        if (getParentActivity() != null) {
            new Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", C0446R.string.AppName)).setMessage(LocaleController.getString("ConfirmDeleteCallLog", C0446R.string.ConfirmDeleteCallLog)).setPositiveButton(LocaleController.getString("Delete", C0446R.string.Delete), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    ArrayList arrayList = new ArrayList();
                    for (Message message : callLogRow.calls) {
                        arrayList.add(Integer.valueOf(message.id));
                    }
                    MessagesController.getInstance(CallLogActivity.this.currentAccount).deleteMessages(arrayList, null, null, 0, false);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null).show().setCanceledOnTouchOutside(true);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i != 101) {
            return;
        }
        if (iArr.length <= null || iArr[null] != null) {
            VoIPHelper.permissionDenied(getParentActivity(), null);
        } else {
            VoIPHelper.startCall(this.lastCallUser, getParentActivity(), null);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        AnonymousClass10 anonymousClass10 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (CallLogActivity.this.listView != null) {
                    int childCount = CallLogActivity.this.listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = CallLogActivity.this.listView.getChildAt(i);
                        if (childAt instanceof ProfileSearchCell) {
                            ((ProfileSearchCell) childAt).update(0);
                        }
                    }
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[32];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LocationCell.class, CustomCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[10] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[12] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[14] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon);
        themeDescriptionArr[15] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground);
        themeDescriptionArr[16] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, Theme.key_chats_name);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        AnonymousClass10 anonymousClass102 = anonymousClass10;
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{this.greenDrawable, this.greenDrawable2, Theme.chat_msgCallUpRedDrawable, Theme.chat_msgCallDownRedDrawable}, null, Theme.key_calls_callReceivedGreenIcon);
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{this.redDrawable, Theme.chat_msgCallUpGreenDrawable, Theme.chat_msgCallDownGreenDrawable}, null, Theme.key_calls_callReceivedRedIcon);
        return themeDescriptionArr;
    }
}
