package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.FastScrollAdapter;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.TypefaceSpan;

public class GroupCreateActivity extends BaseFragment implements NotificationCenterDelegate, OnClickListener {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private boolean addToGroup;
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList();
    private int channelId;
    private int chatId;
    private int chatType = 0;
    private int containerHeight;
    private GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private GroupCreateActivityDelegate delegate;
    private ContactsAddActivityDelegate delegate2;
    private boolean doneButtonVisible;
    private EditTextBoldCursor editText;
    private EmptyTextProgressView emptyView;
    private int fieldY;
    private ImageView floatingButton;
    private boolean ignoreScrollEvent;
    private SparseArray<TLObject> ignoreUsers;
    private ChatFull info;
    private boolean isAlwaysShare;
    private boolean isGroup;
    private boolean isNeverShare;
    private GroupCreateDividerItemDecoration itemDecoration;
    private RecyclerListView listView;
    private int maxCount = MessagesController.getInstance(this.currentAccount).maxMegagroupCount;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private SparseArray<GroupCreateSpan> selectedContacts = new SparseArray();
    private SpansContainer spansContainer;

    public interface ContactsAddActivityDelegate {

        public final /* synthetic */ class -CC {
            public static void $default$needAddBot(ContactsAddActivityDelegate contactsAddActivityDelegate, User user) {
            }
        }

        void didSelectUsers(ArrayList<User> arrayList, int i);

        void needAddBot(User user);
    }

    public interface GroupCreateActivityDelegate {
        void didSelectUsers(ArrayList<Integer> arrayList);
    }

    private class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList();
        private AnimatorSet currentAnimation;
        private View removingSpan;

        public SpansContainer(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            String str;
            String str2;
            int childCount = getChildCount();
            int size = MeasureSpec.getSize(i);
            int dp = size - AndroidUtilities.dp(26.0f);
            int dp2 = AndroidUtilities.dp(10.0f);
            int dp3 = AndroidUtilities.dp(10.0f);
            int i3 = dp2;
            int i4 = 0;
            dp2 = 0;
            int i5 = 0;
            while (true) {
                str = "translationY";
                str2 = "translationX";
                if (i4 >= childCount) {
                    break;
                }
                View childAt = getChildAt(i4);
                if (childAt instanceof GroupCreateSpan) {
                    childAt.measure(MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                    if (childAt != this.removingSpan && childAt.getMeasuredWidth() + dp2 > dp) {
                        i3 += childAt.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        dp2 = 0;
                    }
                    if (childAt.getMeasuredWidth() + i5 > dp) {
                        dp3 += childAt.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        i5 = 0;
                    }
                    int dp4 = AndroidUtilities.dp(13.0f) + dp2;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (childAt == view) {
                            childAt.setTranslationX((float) (AndroidUtilities.dp(13.0f) + i5));
                            childAt.setTranslationY((float) dp3);
                        } else if (view != null) {
                            float f = (float) dp4;
                            int i6;
                            if (childAt.getTranslationX() != f) {
                                ArrayList arrayList = this.animators;
                                float[] fArr = new float[1];
                                i6 = 0;
                                fArr[0] = f;
                                arrayList.add(ObjectAnimator.ofFloat(childAt, str2, fArr));
                            } else {
                                i6 = 0;
                            }
                            if (childAt.getTranslationY() != ((float) i3)) {
                                this.animators.add(ObjectAnimator.ofFloat(childAt, str, new float[]{r11}));
                            }
                        } else {
                            childAt.setTranslationX((float) dp4);
                            childAt.setTranslationY((float) i3);
                        }
                    }
                    if (childAt != this.removingSpan) {
                        dp2 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    i5 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
                i4++;
            }
            if (AndroidUtilities.isTablet()) {
                childCount = AndroidUtilities.dp(372.0f) / 3;
            } else {
                Point point = AndroidUtilities.displaySize;
                childCount = (Math.min(point.x, point.y) - AndroidUtilities.dp(158.0f)) / 3;
            }
            if (dp - dp2 < childCount) {
                i3 += AndroidUtilities.dp(40.0f);
                dp2 = 0;
            }
            if (dp - i5 < childCount) {
                dp3 += AndroidUtilities.dp(40.0f);
            }
            GroupCreateActivity.this.editText.measure(MeasureSpec.makeMeasureSpec(dp - dp2, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            if (!this.animationStarted) {
                dp3 += AndroidUtilities.dp(42.0f);
                dp2 += AndroidUtilities.dp(16.0f);
                GroupCreateActivity.this.fieldY = i3;
                if (this.currentAnimation != null) {
                    boolean z;
                    if (GroupCreateActivity.this.containerHeight != i3 + AndroidUtilities.dp(42.0f)) {
                        this.animators.add(ObjectAnimator.ofInt(GroupCreateActivity.this, "containerHeight", new int[]{i3}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationX() != ((float) dp2)) {
                        this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, str2, new float[]{r3}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationY() != ((float) GroupCreateActivity.this.fieldY)) {
                        ArrayList arrayList2 = this.animators;
                        EditTextBoldCursor access$000 = GroupCreateActivity.this.editText;
                        float[] fArr2 = new float[1];
                        z = false;
                        fArr2[0] = (float) GroupCreateActivity.this.fieldY;
                        arrayList2.add(ObjectAnimator.ofFloat(access$000, str, fArr2));
                    } else {
                        z = false;
                    }
                    GroupCreateActivity.this.editText.setAllowDrawCursor(z);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    GroupCreateActivity.this.containerHeight = dp3;
                    GroupCreateActivity.this.editText.setTranslationX((float) dp2);
                    GroupCreateActivity.this.editText.setTranslationY((float) GroupCreateActivity.this.fieldY);
                }
            } else if (!(this.currentAnimation == null || GroupCreateActivity.this.ignoreScrollEvent || this.removingSpan != null)) {
                GroupCreateActivity.this.editText.bringPointIntoView(GroupCreateActivity.this.editText.getSelectionStart());
            }
            setMeasuredDimension(size, GroupCreateActivity.this.containerHeight);
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            for (i2 = 0; i2 < childCount; i2++) {
                View childAt = getChildAt(i2);
                childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan groupCreateSpan) {
            GroupCreateActivity.this.allSpans.add(groupCreateSpan);
            GroupCreateActivity.this.selectedContacts.put(groupCreateSpan.getUid(), groupCreateSpan);
            GroupCreateActivity.this.editText.setHintVisible(false);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.addingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                }
            });
            this.currentAnimation.setDuration(150);
            this.addingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[]{0.0f, 1.0f}));
            addView(groupCreateSpan);
        }

        public void removeSpan(final GroupCreateSpan groupCreateSpan) {
            GroupCreateActivity.this.ignoreScrollEvent = true;
            GroupCreateActivity.this.selectedContacts.remove(groupCreateSpan.getUid());
            GroupCreateActivity.this.allSpans.remove(groupCreateSpan);
            groupCreateSpan.setOnClickListener(null);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(groupCreateSpan);
                    SpansContainer.this.removingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                    if (GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150);
            this.removingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    public class GroupCreateAdapter extends FastScrollAdapter {
        private ArrayList<TLObject> contacts = new ArrayList();
        private Context context;
        private int inviteViaLink;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<TLObject> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Runnable searchRunnable;
        private boolean searching;
        private int usersStartRow;

        public GroupCreateAdapter(Context context) {
            int i;
            this.context = context;
            ArrayList arrayList = ContactsController.getInstance(GroupCreateActivity.this.currentAccount).contacts;
            for (i = 0; i < arrayList.size(); i++) {
                User user = MessagesController.getInstance(GroupCreateActivity.this.currentAccount).getUser(Integer.valueOf(((TL_contact) arrayList.get(i)).user_id));
                if (!(user == null || user.self || user.deleted)) {
                    this.contacts.add(user);
                }
            }
            if (GroupCreateActivity.this.isNeverShare || GroupCreateActivity.this.isAlwaysShare) {
                arrayList = GroupCreateActivity.this.getMessagesController().getAllDialogs();
                i = arrayList.size();
                for (int i2 = 0; i2 < i; i2++) {
                    int i3 = (int) ((Dialog) arrayList.get(i2)).id;
                    if (i3 < 0) {
                        Chat chat = GroupCreateActivity.this.getMessagesController().getChat(Integer.valueOf(-i3));
                        if (chat != null && chat.migrated_to == null && (!ChatObject.isChannel(chat) || chat.megagroup)) {
                            this.contacts.add(chat);
                        }
                    }
                }
                Collections.sort(this.contacts, new Comparator<TLObject>(GroupCreateActivity.this) {
                    private String getName(TLObject tLObject) {
                        if (!(tLObject instanceof User)) {
                            return ((Chat) tLObject).title;
                        }
                        User user = (User) tLObject;
                        return ContactsController.formatName(user.first_name, user.last_name);
                    }

                    public int compare(TLObject tLObject, TLObject tLObject2) {
                        return getName(tLObject).compareTo(getName(tLObject2));
                    }
                });
            }
            this.searchAdapterHelper = new SearchAdapterHelper(false);
            this.searchAdapterHelper.setDelegate(new -$$Lambda$GroupCreateActivity$GroupCreateAdapter$E5cKkbpq2FJnJZ0QVy8Qf2zhCrM(this));
        }

        public /* synthetic */ void lambda$new$0$GroupCreateActivity$GroupCreateAdapter() {
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress()) {
                GroupCreateActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public void setSearching(boolean z) {
            if (this.searching != z) {
                this.searching = z;
                notifyDataSetChanged();
            }
        }

        public String getLetter(int i) {
            if (!this.searching && i >= this.usersStartRow) {
                int size = this.contacts.size();
                int i2 = this.usersStartRow;
                if (i < size + i2) {
                    CharSequence charSequence;
                    CharSequence charSequence2;
                    TLObject tLObject = (TLObject) this.contacts.get(i - i2);
                    String str = "";
                    if (tLObject instanceof User) {
                        User user = (User) tLObject;
                        charSequence = user.first_name;
                        charSequence2 = user.last_name;
                    } else {
                        charSequence = ((Chat) tLObject).title;
                        charSequence2 = str;
                    }
                    if (LocaleController.nameDisplayOrder == 1) {
                        if (!TextUtils.isEmpty(charSequence)) {
                            return charSequence.substring(0, 1).toUpperCase();
                        }
                        if (!TextUtils.isEmpty(charSequence2)) {
                            return charSequence2.substring(0, 1).toUpperCase();
                        }
                    } else if (!TextUtils.isEmpty(charSequence2)) {
                        return charSequence2.substring(0, 1).toUpperCase();
                    } else {
                        if (!TextUtils.isEmpty(charSequence)) {
                            return charSequence.substring(0, 1).toUpperCase();
                        }
                    }
                    return str;
                }
            }
            return null;
        }

        public int getItemCount() {
            int size;
            if (this.searching) {
                size = this.searchResult.size();
                int size2 = this.searchAdapterHelper.getLocalServerSearch().size();
                int size3 = this.searchAdapterHelper.getGlobalSearch().size();
                size += size2;
                if (size3 != 0) {
                    size += size3 + 1;
                }
                return size;
            }
            size = this.contacts.size();
            if (GroupCreateActivity.this.addToGroup) {
                if (GroupCreateActivity.this.chatId != 0) {
                    this.inviteViaLink = ChatObject.canUserDoAdminAction(MessagesController.getInstance(GroupCreateActivity.this.currentAccount).getChat(Integer.valueOf(GroupCreateActivity.this.chatId)), 3);
                } else {
                    int i = 0;
                    if (GroupCreateActivity.this.channelId != 0) {
                        Chat chat = MessagesController.getInstance(GroupCreateActivity.this.currentAccount).getChat(Integer.valueOf(GroupCreateActivity.this.channelId));
                        if (ChatObject.canUserDoAdminAction(chat, 3) && TextUtils.isEmpty(chat.username)) {
                            i = 2;
                        }
                        this.inviteViaLink = i;
                    } else {
                        this.inviteViaLink = 0;
                    }
                }
                if (this.inviteViaLink != 0) {
                    this.usersStartRow = 1;
                    size++;
                }
            }
            return size;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View groupCreateSectionCell;
            if (i == 0) {
                groupCreateSectionCell = new GroupCreateSectionCell(this.context);
            } else if (i != 1) {
                groupCreateSectionCell = new TextCell(this.context);
            } else {
                groupCreateSectionCell = new GroupCreateUserCell(this.context, true, 0);
            }
            return new Holder(groupCreateSectionCell);
        }

        /* JADX WARNING: Removed duplicated region for block: B:62:0x013c  */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x0137  */
        /* JADX WARNING: Removed duplicated region for block: B:83:? A:{SYNTHETIC, RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x0149  */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
            r10 = this;
            r0 = r11.getItemViewType();
            if (r0 == 0) goto L_0x017a;
        L_0x0006:
            r1 = 0;
            r2 = 1;
            if (r0 == r2) goto L_0x0036;
        L_0x000a:
            r12 = 2;
            if (r0 == r12) goto L_0x000f;
        L_0x000d:
            goto L_0x018e;
        L_0x000f:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.TextCell) r11;
            r0 = r10.inviteViaLink;
            r2 = NUM; // 0x7var_ float:1.7945661E38 double:1.052935767E-314;
            if (r0 != r12) goto L_0x0028;
        L_0x001a:
            r12 = NUM; // 0x7f0d0237 float:1.8743265E38 double:1.0531300577E-314;
            r0 = "ChannelInviteViaLink";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setTextAndIcon(r12, r2, r1);
            goto L_0x018e;
        L_0x0028:
            r12 = NUM; // 0x7f0d053a float:1.8744829E38 double:1.0531304386E-314;
            r0 = "InviteToGroupByLink";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setTextAndIcon(r12, r2, r1);
            goto L_0x018e;
        L_0x0036:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.GroupCreateUserCell) r11;
            r0 = r10.searching;
            r3 = 0;
            if (r0 == 0) goto L_0x0123;
        L_0x003f:
            r0 = r10.searchResult;
            r0 = r0.size();
            r4 = r10.searchAdapterHelper;
            r4 = r4.getGlobalSearch();
            r4 = r4.size();
            r5 = r10.searchAdapterHelper;
            r5 = r5.getLocalServerSearch();
            r5 = r5.size();
            if (r12 < 0) goto L_0x0066;
        L_0x005b:
            if (r12 >= r0) goto L_0x0066;
        L_0x005d:
            r4 = r10.searchResult;
            r4 = r4.get(r12);
            r4 = (org.telegram.tgnet.TLObject) r4;
            goto L_0x0095;
        L_0x0066:
            if (r12 < r0) goto L_0x007b;
        L_0x0068:
            r6 = r5 + r0;
            if (r12 >= r6) goto L_0x007b;
        L_0x006c:
            r4 = r10.searchAdapterHelper;
            r4 = r4.getLocalServerSearch();
            r5 = r12 - r0;
            r4 = r4.get(r5);
            r4 = (org.telegram.tgnet.TLObject) r4;
            goto L_0x0095;
        L_0x007b:
            r6 = r0 + r5;
            if (r12 <= r6) goto L_0x0094;
        L_0x007f:
            r4 = r4 + r0;
            r4 = r4 + r5;
            if (r12 > r4) goto L_0x0094;
        L_0x0083:
            r4 = r10.searchAdapterHelper;
            r4 = r4.getGlobalSearch();
            r6 = r12 - r0;
            r6 = r6 - r5;
            r6 = r6 - r2;
            r4 = r4.get(r6);
            r4 = (org.telegram.tgnet.TLObject) r4;
            goto L_0x0095;
        L_0x0094:
            r4 = r3;
        L_0x0095:
            if (r4 == 0) goto L_0x012f;
        L_0x0097:
            r5 = r4 instanceof org.telegram.tgnet.TLRPC.User;
            if (r5 == 0) goto L_0x00a1;
        L_0x009b:
            r5 = r4;
            r5 = (org.telegram.tgnet.TLRPC.User) r5;
            r5 = r5.username;
            goto L_0x00a6;
        L_0x00a1:
            r5 = r4;
            r5 = (org.telegram.tgnet.TLRPC.Chat) r5;
            r5 = r5.username;
        L_0x00a6:
            r6 = "@";
            if (r12 >= r0) goto L_0x00d7;
        L_0x00aa:
            r0 = r10.searchResultNames;
            r12 = r0.get(r12);
            r12 = (java.lang.CharSequence) r12;
            if (r12 == 0) goto L_0x0130;
        L_0x00b4:
            r0 = android.text.TextUtils.isEmpty(r5);
            if (r0 != 0) goto L_0x0130;
        L_0x00ba:
            r0 = r12.toString();
            r7 = new java.lang.StringBuilder;
            r7.<init>();
            r7.append(r6);
            r7.append(r5);
            r5 = r7.toString();
            r0 = r0.startsWith(r5);
            if (r0 == 0) goto L_0x0130;
        L_0x00d3:
            r9 = r3;
            r3 = r12;
            r12 = r9;
            goto L_0x0130;
        L_0x00d7:
            if (r12 <= r0) goto L_0x012f;
        L_0x00d9:
            r12 = android.text.TextUtils.isEmpty(r5);
            if (r12 != 0) goto L_0x012f;
        L_0x00df:
            r12 = r10.searchAdapterHelper;
            r12 = r12.getLastFoundUsername();
            r0 = r12.startsWith(r6);
            if (r0 == 0) goto L_0x00ef;
        L_0x00eb:
            r12 = r12.substring(r2);
        L_0x00ef:
            r0 = new android.text.SpannableStringBuilder;	 Catch:{ Exception -> 0x0120 }
            r0.<init>();	 Catch:{ Exception -> 0x0120 }
            r0.append(r6);	 Catch:{ Exception -> 0x0120 }
            r0.append(r5);	 Catch:{ Exception -> 0x0120 }
            r6 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r5, r12);	 Catch:{ Exception -> 0x0120 }
            r7 = -1;
            if (r6 == r7) goto L_0x011d;
        L_0x0101:
            r12 = r12.length();	 Catch:{ Exception -> 0x0120 }
            if (r6 != 0) goto L_0x010a;
        L_0x0107:
            r12 = r12 + 1;
            goto L_0x010c;
        L_0x010a:
            r6 = r6 + 1;
        L_0x010c:
            r7 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x0120 }
            r8 = "windowBackgroundWhiteBlueText4";
            r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);	 Catch:{ Exception -> 0x0120 }
            r7.<init>(r8);	 Catch:{ Exception -> 0x0120 }
            r12 = r12 + r6;
            r8 = 33;
            r0.setSpan(r7, r6, r12, r8);	 Catch:{ Exception -> 0x0120 }
        L_0x011d:
            r12 = r3;
            r3 = r0;
            goto L_0x0130;
        L_0x0120:
            r12 = r3;
            r3 = r5;
            goto L_0x0130;
        L_0x0123:
            r0 = r10.contacts;
            r4 = r10.usersStartRow;
            r12 = r12 - r4;
            r12 = r0.get(r12);
            r4 = r12;
            r4 = (org.telegram.tgnet.TLObject) r4;
        L_0x012f:
            r12 = r3;
        L_0x0130:
            r11.setObject(r4, r12, r3);
            r12 = r4 instanceof org.telegram.tgnet.TLRPC.User;
            if (r12 == 0) goto L_0x013c;
        L_0x0137:
            r4 = (org.telegram.tgnet.TLRPC.User) r4;
            r12 = r4.id;
            goto L_0x0147;
        L_0x013c:
            r12 = r4 instanceof org.telegram.tgnet.TLRPC.Chat;
            if (r12 == 0) goto L_0x0146;
        L_0x0140:
            r4 = (org.telegram.tgnet.TLRPC.Chat) r4;
            r12 = r4.id;
            r12 = -r12;
            goto L_0x0147;
        L_0x0146:
            r12 = 0;
        L_0x0147:
            if (r12 == 0) goto L_0x018e;
        L_0x0149:
            r0 = org.telegram.ui.GroupCreateActivity.this;
            r0 = r0.ignoreUsers;
            if (r0 == 0) goto L_0x0164;
        L_0x0151:
            r0 = org.telegram.ui.GroupCreateActivity.this;
            r0 = r0.ignoreUsers;
            r0 = r0.indexOfKey(r12);
            if (r0 < 0) goto L_0x0164;
        L_0x015d:
            r11.setChecked(r2, r1);
            r11.setCheckBoxEnabled(r1);
            goto L_0x018e;
        L_0x0164:
            r0 = org.telegram.ui.GroupCreateActivity.this;
            r0 = r0.selectedContacts;
            r12 = r0.indexOfKey(r12);
            if (r12 < 0) goto L_0x0172;
        L_0x0170:
            r12 = 1;
            goto L_0x0173;
        L_0x0172:
            r12 = 0;
        L_0x0173:
            r11.setChecked(r12, r1);
            r11.setCheckBoxEnabled(r2);
            goto L_0x018e;
        L_0x017a:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.GroupCreateSectionCell) r11;
            r12 = r10.searching;
            if (r12 == 0) goto L_0x018e;
        L_0x0182:
            r12 = NUM; // 0x7f0d04da float:1.8744634E38 double:1.053130391E-314;
            r0 = "GlobalSearch";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
        L_0x018e:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (this.searching) {
                if (i == this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size()) {
                    return 0;
                }
                return 1;
            } else if (this.inviteViaLink == 0 || i != 0) {
                return 1;
            } else {
                return 2;
            }
        }

        public int getPositionForScrollProgress(float f) {
            return (int) (((float) getItemCount()) * f);
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof GroupCreateUserCell) {
                ((GroupCreateUserCell) view).recycle();
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            if (GroupCreateActivity.this.ignoreUsers == null) {
                return true;
            }
            View view = viewHolder.itemView;
            if (!(view instanceof GroupCreateUserCell)) {
                return true;
            }
            TLObject object = ((GroupCreateUserCell) view).getObject();
            if (!(object instanceof User)) {
                return true;
            }
            if (GroupCreateActivity.this.ignoreUsers.indexOfKey(((User) object).id) < 0) {
                return true;
            }
            return false;
        }

        public void searchDialogs(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults(null);
                SearchAdapterHelper searchAdapterHelper = this.searchAdapterHelper;
                boolean z = GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare;
                searchAdapterHelper.queryServerSearch(null, true, z, false, false, 0, false, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            -$$Lambda$GroupCreateActivity$GroupCreateAdapter$9OX1wBUGkwo4l1vSorMnDVBQeb4 -__lambda_groupcreateactivity_groupcreateadapter_9ox1wbugkwo4l1vsormndvbqeb4 = new -$$Lambda$GroupCreateActivity$GroupCreateAdapter$9OX1wBUGkwo4l1vSorMnDVBQeb4(this, str);
            this.searchRunnable = -__lambda_groupcreateactivity_groupcreateadapter_9ox1wbugkwo4l1vsormndvbqeb4;
            dispatchQueue.postRunnable(-__lambda_groupcreateactivity_groupcreateadapter_9ox1wbugkwo4l1vsormndvbqeb4, 300);
        }

        public /* synthetic */ void lambda$searchDialogs$3$GroupCreateActivity$GroupCreateAdapter(String str) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$GroupCreateActivity$GroupCreateAdapter$IOkwPcXbp_V5IdLHY-WyIlJIYZI(this, str));
        }

        public /* synthetic */ void lambda$null$2$GroupCreateActivity$GroupCreateAdapter(String str) {
            SearchAdapterHelper searchAdapterHelper = this.searchAdapterHelper;
            boolean z = GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare;
            searchAdapterHelper.queryServerSearch(str, true, z, true, false, 0, false, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            -$$Lambda$GroupCreateActivity$GroupCreateAdapter$0GX4ESbcO5iM0ilImNc3QCLvsKs -__lambda_groupcreateactivity_groupcreateadapter_0gx4esbco5im0ilimnc3qclvsks = new -$$Lambda$GroupCreateActivity$GroupCreateAdapter$0GX4ESbcO5iM0ilImNc3QCLvsKs(this, str);
            this.searchRunnable = -__lambda_groupcreateactivity_groupcreateadapter_0gx4esbco5im0ilimnc3qclvsks;
            dispatchQueue.postRunnable(-__lambda_groupcreateactivity_groupcreateadapter_0gx4esbco5im0ilimnc3qclvsks);
        }

        /* JADX WARNING: Removed duplicated region for block: B:51:0x012a A:{LOOP_END, LOOP:1: B:27:0x008d->B:51:0x012a} */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x00da A:{SYNTHETIC} */
        /* JADX WARNING: Missing block: B:36:0x00c9, code skipped:
            if (r12.contains(r4.toString()) != false) goto L_0x00d7;
     */
        public /* synthetic */ void lambda$null$1$GroupCreateActivity$GroupCreateAdapter(java.lang.String r18) {
            /*
            r17 = this;
            r0 = r17;
            r1 = r18.trim();
            r1 = r1.toLowerCase();
            r2 = r1.length();
            if (r2 != 0) goto L_0x001e;
        L_0x0010:
            r1 = new java.util.ArrayList;
            r1.<init>();
            r2 = new java.util.ArrayList;
            r2.<init>();
            r0.updateSearchResults(r1, r2);
            return;
        L_0x001e:
            r2 = org.telegram.messenger.LocaleController.getInstance();
            r2 = r2.getTranslitString(r1);
            r3 = r1.equals(r2);
            if (r3 != 0) goto L_0x0032;
        L_0x002c:
            r3 = r2.length();
            if (r3 != 0) goto L_0x0033;
        L_0x0032:
            r2 = 0;
        L_0x0033:
            r3 = 0;
            r5 = 1;
            if (r2 == 0) goto L_0x0039;
        L_0x0037:
            r6 = 1;
            goto L_0x003a;
        L_0x0039:
            r6 = 0;
        L_0x003a:
            r6 = r6 + r5;
            r6 = new java.lang.String[r6];
            r6[r3] = r1;
            if (r2 == 0) goto L_0x0043;
        L_0x0041:
            r6[r5] = r2;
        L_0x0043:
            r1 = new java.util.ArrayList;
            r1.<init>();
            r2 = new java.util.ArrayList;
            r2.<init>();
            r7 = 0;
        L_0x004e:
            r8 = r0.contacts;
            r8 = r8.size();
            if (r7 >= r8) goto L_0x013a;
        L_0x0056:
            r8 = r0.contacts;
            r8 = r8.get(r7);
            r8 = (org.telegram.tgnet.TLObject) r8;
            r9 = r8 instanceof org.telegram.tgnet.TLRPC.User;
            if (r9 == 0) goto L_0x0074;
        L_0x0062:
            r10 = r8;
            r10 = (org.telegram.tgnet.TLRPC.User) r10;
            r11 = r10.first_name;
            r12 = r10.last_name;
            r11 = org.telegram.messenger.ContactsController.formatName(r11, r12);
            r11 = r11.toLowerCase();
            r10 = r10.username;
            goto L_0x007b;
        L_0x0074:
            r10 = r8;
            r10 = (org.telegram.tgnet.TLRPC.Chat) r10;
            r11 = r10.title;
            r10 = r10.username;
        L_0x007b:
            r12 = org.telegram.messenger.LocaleController.getInstance();
            r12 = r12.getTranslitString(r11);
            r13 = r11.equals(r12);
            if (r13 == 0) goto L_0x008a;
        L_0x0089:
            r12 = 0;
        L_0x008a:
            r13 = r6.length;
            r14 = 0;
            r15 = 0;
        L_0x008d:
            if (r14 >= r13) goto L_0x0132;
        L_0x008f:
            r3 = r6[r14];
            r16 = r11.startsWith(r3);
            if (r16 != 0) goto L_0x00d7;
        L_0x0097:
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = " ";
            r4.append(r5);
            r4.append(r3);
            r4 = r4.toString();
            r4 = r11.contains(r4);
            if (r4 != 0) goto L_0x00d7;
        L_0x00ae:
            if (r12 == 0) goto L_0x00cc;
        L_0x00b0:
            r4 = r12.startsWith(r3);
            if (r4 != 0) goto L_0x00d7;
        L_0x00b6:
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r4.append(r5);
            r4.append(r3);
            r4 = r4.toString();
            r4 = r12.contains(r4);
            if (r4 == 0) goto L_0x00cc;
        L_0x00cb:
            goto L_0x00d7;
        L_0x00cc:
            if (r10 == 0) goto L_0x00d8;
        L_0x00ce:
            r4 = r10.startsWith(r3);
            if (r4 == 0) goto L_0x00d8;
        L_0x00d4:
            r4 = 2;
            r15 = 2;
            goto L_0x00d8;
        L_0x00d7:
            r15 = 1;
        L_0x00d8:
            if (r15 == 0) goto L_0x012a;
        L_0x00da:
            r4 = 1;
            if (r15 != r4) goto L_0x00fd;
        L_0x00dd:
            if (r9 == 0) goto L_0x00ee;
        L_0x00df:
            r5 = r8;
            r5 = (org.telegram.tgnet.TLRPC.User) r5;
            r9 = r5.first_name;
            r5 = r5.last_name;
            r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r5, r3);
            r2.add(r3);
            goto L_0x00fb;
        L_0x00ee:
            r5 = r8;
            r5 = (org.telegram.tgnet.TLRPC.Chat) r5;
            r5 = r5.title;
            r9 = 0;
            r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r9, r3);
            r2.add(r3);
        L_0x00fb:
            r9 = 0;
            goto L_0x0125;
        L_0x00fd:
            r5 = new java.lang.StringBuilder;
            r5.<init>();
            r9 = "@";
            r5.append(r9);
            r5.append(r10);
            r5 = r5.toString();
            r10 = new java.lang.StringBuilder;
            r10.<init>();
            r10.append(r9);
            r10.append(r3);
            r3 = r10.toString();
            r9 = 0;
            r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r9, r3);
            r2.add(r3);
        L_0x0125:
            r1.add(r8);
            r3 = r9;
            goto L_0x0134;
        L_0x012a:
            r3 = 0;
            r4 = 1;
            r14 = r14 + 1;
            r3 = 0;
            r5 = 1;
            goto L_0x008d;
        L_0x0132:
            r3 = 0;
            r4 = 1;
        L_0x0134:
            r7 = r7 + 1;
            r3 = 0;
            r5 = 1;
            goto L_0x004e;
        L_0x013a:
            r0.updateSearchResults(r1, r2);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter.lambda$null$1$GroupCreateActivity$GroupCreateAdapter(java.lang.String):void");
        }

        private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$GroupCreateActivity$GroupCreateAdapter$0S9I3EfzonmjHs092-WOmXkLo2c(this, arrayList, arrayList2));
        }

        public /* synthetic */ void lambda$updateSearchResults$4$GroupCreateActivity$GroupCreateAdapter(ArrayList arrayList, ArrayList arrayList2) {
            this.searchRunnable = null;
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            this.searchAdapterHelper.mergeResults(this.searchResult);
            if (this.searching && !this.searchAdapterHelper.isSearchInProgress()) {
                GroupCreateActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }
    }

    public GroupCreateActivity(Bundle bundle) {
        super(bundle);
        this.chatType = bundle.getInt("chatType", 0);
        this.isAlwaysShare = bundle.getBoolean("isAlwaysShare", false);
        this.isNeverShare = bundle.getBoolean("isNeverShare", false);
        this.addToGroup = bundle.getBoolean("addToGroup", false);
        this.isGroup = bundle.getBoolean("isGroup", false);
        this.chatId = bundle.getInt("chatId");
        this.channelId = bundle.getInt("channelId");
        if (this.isAlwaysShare || this.isNeverShare || this.addToGroup) {
            this.maxCount = 0;
        } else {
            this.maxCount = this.chatType == 0 ? MessagesController.getInstance(this.currentAccount).maxMegagroupCount : MessagesController.getInstance(this.currentAccount).maxBroadcastCount;
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onClick(View view) {
        GroupCreateSpan groupCreateSpan = (GroupCreateSpan) view;
        if (groupCreateSpan.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(groupCreateSpan);
            updateHint();
            checkVisibleRows();
            return;
        }
        GroupCreateSpan groupCreateSpan2 = this.currentDeletingSpan;
        if (groupCreateSpan2 != null) {
            groupCreateSpan2.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = groupCreateSpan;
        groupCreateSpan.startDeleteAnimation();
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.doneButtonVisible = this.chatType == 2;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.chatType;
        if (i == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAddMembers", NUM));
        } else if (this.addToGroup) {
            this.actionBar.setTitle(LocaleController.getString("GroupAddMembers", NUM));
        } else if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", NUM));
            }
        } else if (!this.isNeverShare) {
            String str;
            ActionBar actionBar = this.actionBar;
            if (i == 0) {
                i = NUM;
                str = "NewGroup";
            } else {
                i = NUM;
                str = "NewBroadcastList";
            }
            actionBar.setTitle(LocaleController.getString(str, i));
        } else if (this.isGroup) {
            this.actionBar.setTitle(LocaleController.getString("NeverAllow", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    GroupCreateActivity.this.finishFragment();
                } else if (i == 1) {
                    GroupCreateActivity.this.onDonePressed(true);
                }
            }
        });
        this.fragmentView = new ViewGroup(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int dp;
                i = MeasureSpec.getSize(i);
                i2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(i, i2);
                float f = 56.0f;
                if (AndroidUtilities.isTablet() || i2 > i) {
                    dp = AndroidUtilities.dp(144.0f);
                } else {
                    dp = AndroidUtilities.dp(56.0f);
                }
                GroupCreateActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                GroupCreateActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                GroupCreateActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                if (GroupCreateActivity.this.floatingButton != null) {
                    if (VERSION.SDK_INT < 21) {
                        f = 60.0f;
                    }
                    i = AndroidUtilities.dp(f);
                    GroupCreateActivity.this.floatingButton.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i, NUM));
                }
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                GroupCreateActivity.this.scrollView.layout(0, 0, GroupCreateActivity.this.scrollView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight());
                GroupCreateActivity.this.listView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.listView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.listView.getMeasuredHeight());
                GroupCreateActivity.this.emptyView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.emptyView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.emptyView.getMeasuredHeight());
                if (GroupCreateActivity.this.floatingButton != null) {
                    int dp = LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : ((i3 - i) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredWidth();
                    i4 = ((i4 - i2) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredHeight();
                    GroupCreateActivity.this.floatingButton.layout(dp, i4, GroupCreateActivity.this.floatingButton.getMeasuredWidth() + dp, GroupCreateActivity.this.floatingButton.getMeasuredHeight() + i4);
                }
            }

            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == GroupCreateActivity.this.listView || view == GroupCreateActivity.this.emptyView) {
                    GroupCreateActivity.this.parentLayout.drawHeaderShadow(canvas, GroupCreateActivity.this.scrollView.getMeasuredHeight());
                }
                return drawChild;
            }
        };
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        this.scrollView = new ScrollView(context) {
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                if (GroupCreateActivity.this.ignoreScrollEvent) {
                    GroupCreateActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
                rect.top += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rect.bottom += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }
        };
        this.scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
        viewGroup.addView(this.scrollView);
        this.spansContainer = new SpansContainer(context);
        this.scrollView.addView(this.spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        this.spansContainer.setOnClickListener(new -$$Lambda$GroupCreateActivity$eUWXrWcPgL8v-iB3AHPMNXDrIzs(this));
        this.editText = new EditTextBoldCursor(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (GroupCreateActivity.this.currentDeletingSpan != null) {
                    GroupCreateActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateActivity.this.currentDeletingSpan = null;
                }
                if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.editText.setTextSize(1, 16.0f);
        this.editText.setHintColor(Theme.getColor("groupcreate_hintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setCursorColor(Theme.getColor("groupcreate_cursor"));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(NUM);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        if (this.chatType == 2) {
            this.editText.setHintText(LocaleController.getString("AddMutual", NUM));
        } else if (this.addToGroup) {
            this.editText.setHintText(LocaleController.getString("SearchForPeople", NUM));
        } else if (this.isAlwaysShare || this.isNeverShare) {
            this.editText.setHintText(LocaleController.getString("SearchForPeopleAndGroups", NUM));
        } else {
            this.editText.setHintText(LocaleController.getString("SendMessageTo", NUM));
        }
        this.editText.setCustomSelectionActionModeCallback(new Callback() {
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }
        });
        this.editText.setOnEditorActionListener(new -$$Lambda$GroupCreateActivity$gD5dQDpB0G04wuqUX611txNyFto(this));
        this.editText.setOnKeyListener(new OnKeyListener() {
            private boolean wasEmpty;

            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == 67) {
                    boolean z = true;
                    if (keyEvent.getAction() == 0) {
                        if (GroupCreateActivity.this.editText.length() != 0) {
                            z = false;
                        }
                        this.wasEmpty = z;
                    } else if (keyEvent.getAction() == 1 && this.wasEmpty && !GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan) GroupCreateActivity.this.allSpans.get(GroupCreateActivity.this.allSpans.size() - 1));
                        GroupCreateActivity.this.updateHint();
                        GroupCreateActivity.this.checkVisibleRows();
                        return true;
                    }
                }
                return false;
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (GroupCreateActivity.this.editText.length() != 0) {
                    if (!GroupCreateActivity.this.adapter.searching) {
                        GroupCreateActivity.this.searching = true;
                        GroupCreateActivity.this.searchWas = true;
                        GroupCreateActivity.this.adapter.setSearching(true);
                        GroupCreateActivity.this.itemDecoration.setSearching(true);
                        GroupCreateActivity.this.listView.setFastScrollVisible(false);
                        GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
                        GroupCreateActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
                        GroupCreateActivity.this.emptyView.showProgress();
                    }
                    GroupCreateActivity.this.adapter.searchDialogs(GroupCreateActivity.this.editText.getText().toString());
                    return;
                }
                GroupCreateActivity.this.closeSearch();
            }
        });
        this.emptyView = new EmptyTextProgressView(context);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        viewGroup.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.listView = new RecyclerListView(context);
        this.listView.setFastScrollEnabled();
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView = this.listView;
        GroupCreateAdapter groupCreateAdapter = new GroupCreateAdapter(context);
        this.adapter = groupCreateAdapter;
        recyclerListView.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView2 = this.listView;
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.itemDecoration = groupCreateDividerItemDecoration;
        recyclerListView2.addItemDecoration(groupCreateDividerItemDecoration);
        viewGroup.addView(this.listView);
        this.listView.setOnItemClickListener(new -$$Lambda$GroupCreateActivity$6yZ3Pg9mYqhNWQKcnf5So14xft8(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(GroupCreateActivity.this.editText);
                }
            }
        });
        this.floatingButton = new ImageView(context);
        this.floatingButton.setScaleType(ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), Mode.MULTIPLY));
        if (this.isNeverShare || this.isAlwaysShare || this.addToGroup) {
            this.floatingButton.setImageResource(NUM);
        } else {
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setArrowRotation(180);
            this.floatingButton.setImageDrawable(backDrawable);
        }
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            String str2 = "translationZ";
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, str2, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, str2, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(stateListAnimator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        viewGroup.addView(this.floatingButton);
        this.floatingButton.setOnClickListener(new -$$Lambda$GroupCreateActivity$Lid5KkPKTwmHxkGJugR-qNCJxvA(this));
        if (this.chatType != 2) {
            this.floatingButton.setVisibility(4);
            this.floatingButton.setScaleX(0.0f);
            this.floatingButton.setScaleY(0.0f);
            this.floatingButton.setAlpha(0.0f);
        }
        this.floatingButton.setContentDescription(LocaleController.getString("Next", NUM));
        updateHint();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$GroupCreateActivity(View view) {
        this.editText.clearFocus();
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    public /* synthetic */ boolean lambda$createView$1$GroupCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        return i == 6 && onDonePressed(true);
    }

    public /* synthetic */ void lambda$createView$3$GroupCreateActivity(View view, int i) {
        if (i == 0 && this.adapter.inviteViaLink != 0 && !this.adapter.searching) {
            int i2 = this.chatId;
            if (i2 == 0) {
                i2 = this.channelId;
            }
            Chat chat = getMessagesController().getChat(Integer.valueOf(i2));
            if (chat == null || !chat.has_geo || TextUtils.isEmpty(chat.username)) {
                presentFragment(new GroupInviteActivity(i2));
            } else {
                ChatEditTypeActivity chatEditTypeActivity = new ChatEditTypeActivity(i2, true);
                chatEditTypeActivity.setInfo(this.info);
                presentFragment(chatEditTypeActivity);
            }
        } else if (view instanceof GroupCreateUserCell) {
            int i3;
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
            TLObject object = groupCreateUserCell.getObject();
            boolean z = object instanceof User;
            if (z) {
                i3 = ((User) object).id;
            } else if (object instanceof Chat) {
                i3 = -((Chat) object).id;
            }
            SparseArray sparseArray = this.ignoreUsers;
            if (sparseArray == null || sparseArray.indexOfKey(i3) < 0) {
                int i4 = this.selectedContacts.indexOfKey(i3) >= 0 ? 1 : 0;
                if (i4 != 0) {
                    this.spansContainer.removeSpan((GroupCreateSpan) this.selectedContacts.get(i3));
                } else if (this.maxCount == 0 || this.selectedContacts.size() != this.maxCount) {
                    String str = "OK";
                    String str2 = "AppName";
                    if (this.chatType == 0 && this.selectedContacts.size() == MessagesController.getInstance(this.currentAccount).maxGroupCount) {
                        Builder builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString(str2, NUM));
                        builder.setMessage(LocaleController.getString("SoftUserLimitAlert", NUM));
                        builder.setPositiveButton(LocaleController.getString(str, NUM), null);
                        showDialog(builder.create());
                        return;
                    }
                    if (z) {
                        User user = (User) object;
                        if (this.addToGroup && user.bot) {
                            if (this.channelId == 0 && user.bot_nochats) {
                                try {
                                    Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", NUM), 0).show();
                                } catch (Exception e) {
                                    FileLog.e(e);
                                }
                                return;
                            } else if (this.channelId != 0) {
                                Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.channelId));
                                Builder builder2 = new Builder(getParentActivity());
                                if (ChatObject.canAddAdmins(chat2)) {
                                    builder2.setTitle(LocaleController.getString(str2, NUM));
                                    builder2.setMessage(LocaleController.getString("AddBotAsAdmin", NUM));
                                    builder2.setPositiveButton(LocaleController.getString("MakeAdmin", NUM), new -$$Lambda$GroupCreateActivity$bXnsoXnrEPbjWVw2M7-hHqrkQF8(this, user));
                                    builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                                } else {
                                    builder2.setMessage(LocaleController.getString("CantAddBotAsAdmin", NUM));
                                    builder2.setPositiveButton(LocaleController.getString(str, NUM), null);
                                }
                                showDialog(builder2.create());
                                return;
                            }
                        }
                        MessagesController.getInstance(this.currentAccount).putUser(user, this.searching ^ 1);
                    } else if (object instanceof Chat) {
                        MessagesController.getInstance(this.currentAccount).putChat((Chat) object, this.searching ^ 1);
                    }
                    GroupCreateSpan groupCreateSpan = new GroupCreateSpan(this.editText.getContext(), object);
                    this.spansContainer.addSpan(groupCreateSpan);
                    groupCreateSpan.setOnClickListener(this);
                } else {
                    return;
                }
                updateHint();
                if (this.searching || this.searchWas) {
                    AndroidUtilities.showKeyboard(this.editText);
                } else {
                    groupCreateUserCell.setChecked(i4 ^ 1, true);
                }
                if (this.editText.length() > 0) {
                    this.editText.setText(null);
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$2$GroupCreateActivity(User user, DialogInterface dialogInterface, int i) {
        this.delegate2.needAddBot(user);
        if (this.editText.length() > 0) {
            this.editText.setText(null);
        }
    }

    public /* synthetic */ void lambda$createView$4$GroupCreateActivity(View view) {
        onDonePressed(true);
    }

    public void onResume() {
        super.onResume();
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.contactsDidLoad) {
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
            GroupCreateAdapter groupCreateAdapter = this.adapter;
            if (groupCreateAdapter != null) {
                groupCreateAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                i = 0;
                i2 = ((Integer) objArr[0]).intValue();
                int childCount = this.listView.getChildCount();
                if ((i2 & 2) != 0 || (i2 & 1) != 0 || (i2 & 4) != 0) {
                    while (i < childCount) {
                        View childAt = this.listView.getChildAt(i);
                        if (childAt instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) childAt).update(i2);
                        }
                        i++;
                    }
                }
            }
        } else if (i == NotificationCenter.chatDidCreated) {
            removeSelfFromStack();
        }
    }

    public void setIgnoreUsers(SparseArray<TLObject> sparseArray) {
        this.ignoreUsers = sparseArray;
    }

    public void setInfo(ChatFull chatFull) {
        this.info = chatFull;
    }

    @Keep
    public void setContainerHeight(int i) {
        this.containerHeight = i;
        SpansContainer spansContainer = this.spansContainer;
        if (spansContainer != null) {
            spansContainer.requestLayout();
        }
    }

    public int getContainerHeight() {
        return this.containerHeight;
    }

    private void checkVisibleRows() {
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof GroupCreateUserCell) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) childAt;
                TLObject object = groupCreateUserCell.getObject();
                int i2 = object instanceof User ? ((User) object).id : object instanceof Chat ? -((Chat) object).id : 0;
                if (i2 != 0) {
                    SparseArray sparseArray = this.ignoreUsers;
                    if (sparseArray == null || sparseArray.indexOfKey(i2) < 0) {
                        groupCreateUserCell.setChecked(this.selectedContacts.indexOfKey(i2) >= 0, true);
                        groupCreateUserCell.setCheckBoxEnabled(true);
                    } else {
                        groupCreateUserCell.setChecked(true, false);
                        groupCreateUserCell.setCheckBoxEnabled(false);
                    }
                }
            }
        }
    }

    private void onAddToGroupDone(int i) {
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.selectedContacts.size(); i2++) {
            arrayList.add(getMessagesController().getUser(Integer.valueOf(this.selectedContacts.keyAt(i2))));
        }
        ContactsAddActivityDelegate contactsAddActivityDelegate = this.delegate2;
        if (contactsAddActivityDelegate != null) {
            contactsAddActivityDelegate.didSelectUsers(arrayList, i);
        }
        finishFragment();
    }

    private boolean onDonePressed(boolean z) {
        int i = 0;
        if (this.selectedContacts.size() == 0 && this.chatType != 2) {
            return false;
        }
        ArrayList arrayList;
        if (z && this.addToGroup) {
            if (getParentActivity() == null) {
                return false;
            }
            Builder builder = new Builder(getParentActivity());
            String str = "Members";
            if (this.selectedContacts.size() == 1) {
                builder.setTitle(LocaleController.getString("AddOneMemberAlertTitle", NUM));
            } else {
                builder.setTitle(LocaleController.formatString("AddMembersAlertTitle", NUM, LocaleController.formatPluralString(str, this.selectedContacts.size())));
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i2 = 0; i2 < this.selectedContacts.size(); i2++) {
                User user = getMessagesController().getUser(Integer.valueOf(this.selectedContacts.keyAt(i2)));
                if (user != null) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    String str2 = "**";
                    stringBuilder.append(str2);
                    stringBuilder.append(ContactsController.formatName(user.first_name, user.last_name));
                    stringBuilder.append(str2);
                }
            }
            MessagesController messagesController = getMessagesController();
            int i3 = this.chatId;
            if (i3 == 0) {
                i3 = this.channelId;
            }
            Chat chat = messagesController.getChat(Integer.valueOf(i3));
            String str3 = "AddMembersAlertNamesText";
            if (this.selectedContacts.size() > 5) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString(str3, NUM, LocaleController.formatPluralString(str, this.selectedContacts.size()), chat.title)));
                String format = String.format("%d", new Object[]{Integer.valueOf(this.selectedContacts.size())});
                int indexOf = TextUtils.indexOf(spannableStringBuilder, format);
                if (indexOf >= 0) {
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), indexOf, format.length() + indexOf, 33);
                }
                builder.setMessage(spannableStringBuilder);
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(str3, NUM, stringBuilder, chat.title)));
            }
            CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
            if (!ChatObject.isChannel(chat)) {
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(1);
                checkBoxCellArr[0] = new CheckBoxCell(getParentActivity(), 1);
                checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                checkBoxCellArr[0].setMultiline(true);
                String str4 = "";
                if (this.selectedContacts.size() == 1) {
                    User user2 = getMessagesController().getUser(Integer.valueOf(this.selectedContacts.keyAt(0)));
                    checkBoxCellArr[0].setText(AndroidUtilities.replaceTags(LocaleController.formatString("AddOneMemberForwardMessages", NUM, UserObject.getFirstName(user2))), str4, true, false);
                } else {
                    checkBoxCellArr[0].setText(LocaleController.getString("AddMembersForwardMessages", NUM), str4, true, false);
                }
                checkBoxCellArr[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                linearLayout.addView(checkBoxCellArr[0], LayoutHelper.createLinear(-1, -2));
                checkBoxCellArr[0].setOnClickListener(new -$$Lambda$GroupCreateActivity$3XN987OICBmldOkyTJTx1cRwAQA(checkBoxCellArr));
                builder.setCustomViewOffset(12);
                builder.setView(linearLayout);
            }
            builder.setPositiveButton(LocaleController.getString("Add", NUM), new -$$Lambda$GroupCreateActivity$R31kPKuAiH7RQVcTaHcTSTgxjkI(this, checkBoxCellArr));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
        } else if (this.chatType == 2) {
            arrayList = new ArrayList();
            for (int i4 = 0; i4 < this.selectedContacts.size(); i4++) {
                InputUser inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedContacts.keyAt(i4))));
                if (inputUser != null) {
                    arrayList.add(inputUser);
                }
            }
            MessagesController.getInstance(this.currentAccount).addUsersToChannel(this.chatId, arrayList, null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", this.chatId);
            presentFragment(new ChatActivity(bundle), true);
        } else if (!this.doneButtonVisible || this.selectedContacts.size() == 0) {
            return false;
        } else {
            if (this.addToGroup) {
                onAddToGroupDone(0);
            } else {
                arrayList = new ArrayList();
                while (i < this.selectedContacts.size()) {
                    arrayList.add(Integer.valueOf(this.selectedContacts.keyAt(i)));
                    i++;
                }
                if (this.isAlwaysShare || this.isNeverShare) {
                    GroupCreateActivityDelegate groupCreateActivityDelegate = this.delegate;
                    if (groupCreateActivityDelegate != null) {
                        groupCreateActivityDelegate.didSelectUsers(arrayList);
                    }
                    finishFragment();
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putIntegerArrayList("result", arrayList);
                    bundle2.putInt("chatType", this.chatType);
                    presentFragment(new GroupCreateFinalActivity(bundle2));
                }
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$onDonePressed$6$GroupCreateActivity(CheckBoxCell[] checkBoxCellArr, DialogInterface dialogInterface, int i) {
        int i2 = 0;
        if (checkBoxCellArr[0] != null && checkBoxCellArr[0].isChecked()) {
            i2 = 100;
        }
        onAddToGroupDone(i2);
    }

    private void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.itemDecoration.setSearching(false);
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
    }

    private void updateHint() {
        if (!(this.isAlwaysShare || this.isNeverShare || this.addToGroup)) {
            String str = "Members";
            if (this.chatType == 2) {
                this.actionBar.setSubtitle(LocaleController.formatPluralString(str, this.selectedContacts.size()));
            } else if (this.selectedContacts.size() == 0) {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", NUM, LocaleController.formatPluralString(str, this.maxCount)));
            } else {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCount", NUM, Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount)));
            }
        }
        if (this.chatType != 2) {
            String str2 = "alpha";
            String str3 = "scaleY";
            String str4 = "scaleX";
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (this.doneButtonVisible && this.allSpans.isEmpty()) {
                animatorSet = this.currentDoneButtonAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                this.currentDoneButtonAnimation = new AnimatorSet();
                animatorSet = this.currentDoneButtonAnimation;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.floatingButton, str4, new float[]{0.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.floatingButton, str3, new float[]{0.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.floatingButton, str2, new float[]{0.0f});
                animatorSet.playTogether(animatorArr);
                this.currentDoneButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        GroupCreateActivity.this.floatingButton.setVisibility(4);
                    }
                });
                this.currentDoneButtonAnimation.setDuration(180);
                this.currentDoneButtonAnimation.start();
                this.doneButtonVisible = false;
            } else if (!this.doneButtonVisible && !this.allSpans.isEmpty()) {
                animatorSet = this.currentDoneButtonAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                this.currentDoneButtonAnimation = new AnimatorSet();
                this.floatingButton.setVisibility(0);
                animatorSet = this.currentDoneButtonAnimation;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.floatingButton, str4, new float[]{1.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.floatingButton, str3, new float[]{1.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.floatingButton, str2, new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
                this.currentDoneButtonAnimation.setDuration(180);
                this.currentDoneButtonAnimation.start();
                this.doneButtonVisible = true;
            }
        }
    }

    public void setDelegate(GroupCreateActivityDelegate groupCreateActivityDelegate) {
        this.delegate = groupCreateActivityDelegate;
    }

    public void setDelegate(ContactsAddActivityDelegate contactsAddActivityDelegate) {
        this.delegate2 = contactsAddActivityDelegate;
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$GroupCreateActivity$hlvy0IowbbkFD_DuH8EGWj1jbRE -__lambda_groupcreateactivity_hlvy0iowbbkfd_duh8egwj1jbre = new -$$Lambda$GroupCreateActivity$hlvy0IowbbkFD_DuH8EGWj1jbRE(this);
        r10 = new ThemeDescription[39];
        r10[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r10[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        r10[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        r10[14] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[15] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "groupcreate_hintText");
        r10[16] = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "groupcreate_cursor");
        r10[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, "graySection");
        r10[18] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, null, null, null, "groupcreate_sectionShadow");
        View view = this.listView;
        int i = ThemeDescription.FLAG_TEXTCOLOR;
        Class[] clsArr = new Class[]{GroupCreateSectionCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r10[19] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "groupcreate_sectionText");
        r10[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, null, null, null, "groupcreate_sectionText");
        view = this.listView;
        i = ThemeDescription.FLAG_TEXTCOLOR;
        clsArr = new Class[]{GroupCreateUserCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        r10[21] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "checkbox");
        r10[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxDisabled");
        r10[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck");
        view = this.listView;
        i = ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{GroupCreateUserCell.class};
        strArr = new String[1];
        strArr[0] = "statusTextView";
        r10[24] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueText");
        r10[25] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, "windowBackgroundWhiteGrayText");
        r10[26] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$GroupCreateActivity$hlvy0IowbbkFD_DuH8EGWj1jbRE -__lambda_groupcreateactivity_hlvy0iowbbkfd_duh8egwj1jbre2 = -__lambda_groupcreateactivity_hlvy0iowbbkfd_duh8egwj1jbre;
        r10[27] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_hlvy0iowbbkfd_duh8egwj1jbre2, "avatar_backgroundRed");
        r10[28] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_hlvy0iowbbkfd_duh8egwj1jbre2, "avatar_backgroundOrange");
        r10[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_hlvy0iowbbkfd_duh8egwj1jbre2, "avatar_backgroundViolet");
        r10[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_hlvy0iowbbkfd_duh8egwj1jbre2, "avatar_backgroundGreen");
        r10[31] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_hlvy0iowbbkfd_duh8egwj1jbre2, "avatar_backgroundCyan");
        r10[32] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_hlvy0iowbbkfd_duh8egwj1jbre2, "avatar_backgroundBlue");
        r10[33] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_hlvy0iowbbkfd_duh8egwj1jbre2, "avatar_backgroundPink");
        r10[34] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "avatar_backgroundGroupCreateSpanBlue");
        r10[35] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanBackground");
        r10[36] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanText");
        r10[37] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanDelete");
        r10[38] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "avatar_backgroundBlue");
        return r10;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$7$GroupCreateActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) childAt).update(0);
                }
            }
        }
    }
}
