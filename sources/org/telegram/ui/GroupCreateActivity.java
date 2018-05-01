package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.Editable;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.FastScrollAdapter;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class GroupCreateActivity extends BaseFragment implements OnClickListener, NotificationCenterDelegate {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList();
    private int chatId;
    private int chatType = 0;
    private int containerHeight;
    private GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private GroupCreateActivityDelegate delegate;
    private View doneButton;
    private boolean doneButtonVisible;
    private EditTextBoldCursor editText;
    private EmptyTextProgressView emptyView;
    private int fieldY;
    private boolean ignoreScrollEvent;
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

    /* renamed from: org.telegram.ui.GroupCreateActivity$5 */
    class C13955 implements Callback {
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

        C13955() {
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$6 */
    class C13966 implements OnEditorActionListener {
        C13966() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            return (i != 6 || GroupCreateActivity.this.onDonePressed() == null) ? null : true;
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$7 */
    class C13977 implements OnKeyListener {
        private boolean wasEmpty;

        C13977() {
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (i == 67) {
                boolean z = true;
                if (keyEvent.getAction() == 0) {
                    if (GroupCreateActivity.this.editText.length() != 0) {
                        z = false;
                    }
                    this.wasEmpty = z;
                } else if (keyEvent.getAction() == 1 && this.wasEmpty != 0 && GroupCreateActivity.this.allSpans.isEmpty() == 0) {
                    GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan) GroupCreateActivity.this.allSpans.get(GroupCreateActivity.this.allSpans.size() - 1));
                    GroupCreateActivity.this.updateHint();
                    GroupCreateActivity.this.checkVisibleRows();
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$8 */
    class C13988 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C13988() {
        }

        public void afterTextChanged(Editable editable) {
            if (GroupCreateActivity.this.editText.length() != null) {
                GroupCreateActivity.this.searching = true;
                GroupCreateActivity.this.searchWas = true;
                GroupCreateActivity.this.adapter.setSearching(true);
                GroupCreateActivity.this.itemDecoration.setSearching(true);
                GroupCreateActivity.this.adapter.searchDialogs(GroupCreateActivity.this.editText.getText().toString());
                GroupCreateActivity.this.listView.setFastScrollVisible(false);
                GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
                GroupCreateActivity.this.emptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
                return;
            }
            GroupCreateActivity.this.closeSearch();
        }
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

        /* renamed from: org.telegram.ui.GroupCreateActivity$SpansContainer$1 */
        class C14031 extends AnimatorListenerAdapter {
            C14031() {
            }

            public void onAnimationEnd(Animator animator) {
                SpansContainer.this.addingSpan = null;
                SpansContainer.this.currentAnimation = null;
                SpansContainer.this.animationStarted = false;
                GroupCreateActivity.this.editText.setAllowDrawCursor(true);
            }
        }

        public SpansContainer(Context context) {
            super(context);
        }

        protected void onMeasure(int i, int i2) {
            SpansContainer spansContainer = this;
            int childCount = getChildCount();
            int size = MeasureSpec.getSize(i);
            int dp = size - AndroidUtilities.dp(32.0f);
            float f = 12.0f;
            int dp2 = AndroidUtilities.dp(12.0f);
            int dp3 = AndroidUtilities.dp(12.0f);
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            while (i3 < childCount) {
                View childAt = getChildAt(i3);
                if (childAt instanceof GroupCreateSpan) {
                    childAt.measure(MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                    if (childAt != spansContainer.removingSpan && childAt.getMeasuredWidth() + i4 > dp) {
                        dp2 += childAt.getMeasuredHeight() + AndroidUtilities.dp(f);
                        i4 = 0;
                    }
                    if (childAt.getMeasuredWidth() + i5 > dp) {
                        dp3 += childAt.getMeasuredHeight() + AndroidUtilities.dp(f);
                        i5 = 0;
                    }
                    int dp4 = AndroidUtilities.dp(16.0f) + i4;
                    if (!spansContainer.animationStarted) {
                        if (childAt == spansContainer.removingSpan) {
                            childAt.setTranslationX((float) (AndroidUtilities.dp(16.0f) + i5));
                            childAt.setTranslationY((float) dp3);
                        } else if (spansContainer.removingSpan != null) {
                            if (childAt.getTranslationX() != ((float) dp4)) {
                                spansContainer.animators.add(ObjectAnimator.ofFloat(childAt, "translationX", new float[]{r8}));
                            }
                            if (childAt.getTranslationY() != ((float) dp2)) {
                                spansContainer.animators.add(ObjectAnimator.ofFloat(childAt, "translationY", new float[]{r8}));
                            }
                        } else {
                            childAt.setTranslationX((float) dp4);
                            childAt.setTranslationY((float) dp2);
                        }
                    }
                    if (childAt != spansContainer.removingSpan) {
                        i4 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    i5 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
                i3++;
                f = 12.0f;
            }
            if (AndroidUtilities.isTablet()) {
                childCount = AndroidUtilities.dp(366.0f) / 3;
            } else {
                childCount = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(164.0f)) / 3;
            }
            if (dp - i4 < childCount) {
                dp2 += AndroidUtilities.dp(44.0f);
                i4 = 0;
            }
            if (dp - i5 < childCount) {
                dp3 += AndroidUtilities.dp(44.0f);
            }
            GroupCreateActivity.this.editText.measure(MeasureSpec.makeMeasureSpec(dp - i4, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            if (!spansContainer.animationStarted) {
                dp3 += AndroidUtilities.dp(44.0f);
                i4 += AndroidUtilities.dp(16.0f);
                GroupCreateActivity.this.fieldY = dp2;
                if (spansContainer.currentAnimation != null) {
                    boolean z;
                    if (GroupCreateActivity.this.containerHeight != dp2 + AndroidUtilities.dp(44.0f)) {
                        spansContainer.animators.add(ObjectAnimator.ofInt(GroupCreateActivity.this, "containerHeight", new int[]{dp2}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationX() != ((float) i4)) {
                        spansContainer.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationX", new float[]{r3}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationY() != ((float) GroupCreateActivity.this.fieldY)) {
                        ArrayList arrayList = spansContainer.animators;
                        float[] fArr = new float[1];
                        z = false;
                        fArr[0] = (float) GroupCreateActivity.this.fieldY;
                        arrayList.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationY", fArr));
                    } else {
                        z = false;
                    }
                    GroupCreateActivity.this.editText.setAllowDrawCursor(z);
                    spansContainer.currentAnimation.playTogether(spansContainer.animators);
                    spansContainer.currentAnimation.start();
                    spansContainer.animationStarted = true;
                } else {
                    GroupCreateActivity.this.containerHeight = dp3;
                    GroupCreateActivity.this.editText.setTranslationX((float) i4);
                    GroupCreateActivity.this.editText.setTranslationY((float) GroupCreateActivity.this.fieldY);
                }
            } else if (!(spansContainer.currentAnimation == null || GroupCreateActivity.this.ignoreScrollEvent || spansContainer.removingSpan != null)) {
                GroupCreateActivity.this.editText.bringPointIntoView(GroupCreateActivity.this.editText.getSelectionStart());
            }
            setMeasuredDimension(size, GroupCreateActivity.this.containerHeight);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            z = getChildCount();
            for (boolean z2 = false; z2 < z; z2++) {
                i3 = getChildAt(z2);
                i3.layout(0, 0, i3.getMeasuredWidth(), i3.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan groupCreateSpan) {
            GroupCreateActivity.this.allSpans.add(groupCreateSpan);
            GroupCreateActivity.this.selectedContacts.put(groupCreateSpan.getUid(), groupCreateSpan);
            GroupCreateActivity.this.editText.setHintVisible(false);
            if (this.currentAnimation != null) {
                this.currentAnimation.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new C14031());
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
            if (this.currentAnimation != null) {
                this.currentAnimation.setupEndValues();
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
                    if (GroupCreateActivity.this.allSpans.isEmpty() != null) {
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

    /* renamed from: org.telegram.ui.GroupCreateActivity$1 */
    class C21391 extends ActionBarMenuOnItemClick {
        C21391() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                GroupCreateActivity.this.finishFragment();
            } else if (i == 1) {
                GroupCreateActivity.this.onDonePressed();
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$9 */
    class C21419 implements OnItemClickListener {
        C21419() {
        }

        public void onItemClick(View view, int i) {
            if ((view instanceof GroupCreateUserCell) != 0) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
                User user = groupCreateUserCell.getUser();
                if (user != 0) {
                    int i2 = GroupCreateActivity.this.selectedContacts.indexOfKey(user.id) >= 0 ? 1 : 0;
                    if (i2 != 0) {
                        GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan) GroupCreateActivity.this.selectedContacts.get(user.id));
                    } else if (GroupCreateActivity.this.maxCount != 0 && GroupCreateActivity.this.selectedContacts.size() == GroupCreateActivity.this.maxCount) {
                        return;
                    } else {
                        if (GroupCreateActivity.this.chatType == 0 && GroupCreateActivity.this.selectedContacts.size() == MessagesController.getInstance(GroupCreateActivity.this.currentAccount).maxGroupCount) {
                            view = new Builder(GroupCreateActivity.this.getParentActivity());
                            view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                            view.setMessage(LocaleController.getString("SoftUserLimitAlert", C0446R.string.SoftUserLimitAlert));
                            view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                            GroupCreateActivity.this.showDialog(view.create());
                            return;
                        }
                        MessagesController.getInstance(GroupCreateActivity.this.currentAccount).putUser(user, GroupCreateActivity.this.searching ^ true);
                        GroupCreateSpan groupCreateSpan = new GroupCreateSpan(GroupCreateActivity.this.editText.getContext(), user);
                        GroupCreateActivity.this.spansContainer.addSpan(groupCreateSpan);
                        groupCreateSpan.setOnClickListener(GroupCreateActivity.this);
                    }
                    GroupCreateActivity.this.updateHint();
                    if (GroupCreateActivity.this.searching == 0) {
                        if (GroupCreateActivity.this.searchWas == 0) {
                            groupCreateUserCell.setChecked(i2 ^ 1, true);
                            if (GroupCreateActivity.this.editText.length() > null) {
                                GroupCreateActivity.this.editText.setText(null);
                            }
                        }
                    }
                    AndroidUtilities.showKeyboard(GroupCreateActivity.this.editText);
                    if (GroupCreateActivity.this.editText.length() > null) {
                        GroupCreateActivity.this.editText.setText(null);
                    }
                }
            }
        }
    }

    public class GroupCreateAdapter extends FastScrollAdapter {
        private ArrayList<User> contacts = new ArrayList();
        private Context context;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<User> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;
        private boolean searching;

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public GroupCreateAdapter(Context context) {
            this.context = context;
            context = ContactsController.getInstance(GroupCreateActivity.this.currentAccount).contacts;
            for (int i = 0; i < context.size(); i++) {
                User user = MessagesController.getInstance(GroupCreateActivity.this.currentAccount).getUser(Integer.valueOf(((TL_contact) context.get(i)).user_id));
                if (!(user == null || user.self)) {
                    if (!user.deleted) {
                        this.contacts.add(user);
                    }
                }
            }
            this.searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate(GroupCreateActivity.this) {
                public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                }

                public void onDataSetChanged() {
                    GroupCreateAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public void setSearching(boolean z) {
            if (this.searching != z) {
                this.searching = z;
                notifyDataSetChanged();
            }
        }

        public String getLetter(int i) {
            if (i >= 0) {
                if (i < this.contacts.size()) {
                    User user = (User) this.contacts.get(i);
                    if (user == null) {
                        return null;
                    }
                    if (LocaleController.nameDisplayOrder == 1) {
                        if (!TextUtils.isEmpty(user.first_name)) {
                            return user.first_name.substring(0, 1).toUpperCase();
                        }
                        if (!TextUtils.isEmpty(user.last_name)) {
                            return user.last_name.substring(0, 1).toUpperCase();
                        }
                    } else if (!TextUtils.isEmpty(user.last_name)) {
                        return user.last_name.substring(0, 1).toUpperCase();
                    } else {
                        if (!TextUtils.isEmpty(user.first_name)) {
                            return user.first_name.substring(0, 1).toUpperCase();
                        }
                    }
                    return TtmlNode.ANONYMOUS_REGION_ID;
                }
            }
            return null;
        }

        public int getItemCount() {
            if (!this.searching) {
                return this.contacts.size();
            }
            int size = this.searchResult.size();
            int size2 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size2 != 0) {
                size += size2 + 1;
            }
            return size;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new GroupCreateUserCell(this.context, true);
            } else {
                viewGroup = new GroupCreateSectionCell(this.context);
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(org.telegram.messenger.support.widget.RecyclerView.ViewHolder r9, int r10) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r8 = this;
            r0 = r9.getItemViewType();
            if (r0 == 0) goto L_0x00f6;
        L_0x0006:
            r9 = r9.itemView;
            r9 = (org.telegram.ui.Cells.GroupCreateUserCell) r9;
            r0 = r8.searching;
            r1 = 0;
            r2 = 1;
            if (r0 == 0) goto L_0x00d4;
        L_0x0010:
            r0 = r8.searchResult;
            r0 = r0.size();
            r3 = r8.searchAdapterHelper;
            r3 = r3.getGlobalSearch();
            r3 = r3.size();
            if (r10 < 0) goto L_0x002d;
        L_0x0022:
            if (r10 >= r0) goto L_0x002d;
        L_0x0024:
            r3 = r8.searchResult;
            r3 = r3.get(r10);
            r3 = (org.telegram.tgnet.TLRPC.User) r3;
            goto L_0x0043;
        L_0x002d:
            if (r10 <= r0) goto L_0x0042;
        L_0x002f:
            r3 = r3 + r0;
            if (r10 > r3) goto L_0x0042;
        L_0x0032:
            r3 = r8.searchAdapterHelper;
            r3 = r3.getGlobalSearch();
            r4 = r10 - r0;
            r4 = r4 - r2;
            r3 = r3.get(r4);
            r3 = (org.telegram.tgnet.TLRPC.User) r3;
            goto L_0x0043;
        L_0x0042:
            r3 = r1;
        L_0x0043:
            if (r3 == 0) goto L_0x00dd;
        L_0x0045:
            if (r10 >= r0) goto L_0x007a;
        L_0x0047:
            r0 = r8.searchResultNames;
            r10 = r0.get(r10);
            r10 = (java.lang.CharSequence) r10;
            if (r10 == 0) goto L_0x00de;
        L_0x0051:
            r0 = r3.username;
            r0 = android.text.TextUtils.isEmpty(r0);
            if (r0 != 0) goto L_0x00de;
        L_0x0059:
            r0 = r10.toString();
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "@";
            r4.append(r5);
            r5 = r3.username;
            r4.append(r5);
            r4 = r4.toString();
            r0 = r0.startsWith(r4);
            if (r0 == 0) goto L_0x00de;
        L_0x0076:
            r7 = r1;
            r1 = r10;
            r10 = r7;
            goto L_0x00de;
        L_0x007a:
            if (r10 <= r0) goto L_0x00dd;
        L_0x007c:
            r10 = r3.username;
            r10 = android.text.TextUtils.isEmpty(r10);
            if (r10 != 0) goto L_0x00dd;
        L_0x0084:
            r10 = r8.searchAdapterHelper;
            r10 = r10.getLastFoundUsername();
            r0 = "@";
            r0 = r10.startsWith(r0);
            if (r0 == 0) goto L_0x0096;
        L_0x0092:
            r10 = r10.substring(r2);
        L_0x0096:
            r0 = new android.text.SpannableStringBuilder;	 Catch:{ Exception -> 0x00d1 }
            r0.<init>();	 Catch:{ Exception -> 0x00d1 }
            r4 = "@";	 Catch:{ Exception -> 0x00d1 }
            r0.append(r4);	 Catch:{ Exception -> 0x00d1 }
            r4 = r3.username;	 Catch:{ Exception -> 0x00d1 }
            r0.append(r4);	 Catch:{ Exception -> 0x00d1 }
            r4 = r3.username;	 Catch:{ Exception -> 0x00d1 }
            r4 = r4.toLowerCase();	 Catch:{ Exception -> 0x00d1 }
            r4 = r4.indexOf(r10);	 Catch:{ Exception -> 0x00d1 }
            r5 = -1;	 Catch:{ Exception -> 0x00d1 }
            if (r4 == r5) goto L_0x00ce;	 Catch:{ Exception -> 0x00d1 }
        L_0x00b2:
            r10 = r10.length();	 Catch:{ Exception -> 0x00d1 }
            if (r4 != 0) goto L_0x00bb;	 Catch:{ Exception -> 0x00d1 }
        L_0x00b8:
            r10 = r10 + 1;	 Catch:{ Exception -> 0x00d1 }
            goto L_0x00bd;	 Catch:{ Exception -> 0x00d1 }
        L_0x00bb:
            r4 = r4 + 1;	 Catch:{ Exception -> 0x00d1 }
        L_0x00bd:
            r5 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x00d1 }
            r6 = "windowBackgroundWhiteBlueText4";	 Catch:{ Exception -> 0x00d1 }
            r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);	 Catch:{ Exception -> 0x00d1 }
            r5.<init>(r6);	 Catch:{ Exception -> 0x00d1 }
            r10 = r10 + r4;	 Catch:{ Exception -> 0x00d1 }
            r6 = 33;	 Catch:{ Exception -> 0x00d1 }
            r0.setSpan(r5, r4, r10, r6);	 Catch:{ Exception -> 0x00d1 }
        L_0x00ce:
            r10 = r1;
            r1 = r0;
            goto L_0x00de;
        L_0x00d1:
            r10 = r3.username;
            goto L_0x0076;
        L_0x00d4:
            r0 = r8.contacts;
            r10 = r0.get(r10);
            r3 = r10;
            r3 = (org.telegram.tgnet.TLRPC.User) r3;
        L_0x00dd:
            r10 = r1;
        L_0x00de:
            r9.setUser(r3, r10, r1);
            r10 = org.telegram.ui.GroupCreateActivity.this;
            r10 = r10.selectedContacts;
            r0 = r3.id;
            r10 = r10.indexOfKey(r0);
            r0 = 0;
            if (r10 < 0) goto L_0x00f1;
        L_0x00f0:
            goto L_0x00f2;
        L_0x00f1:
            r2 = r0;
        L_0x00f2:
            r9.setChecked(r2, r0);
            goto L_0x010a;
        L_0x00f6:
            r9 = r9.itemView;
            r9 = (org.telegram.ui.Cells.GroupCreateSectionCell) r9;
            r10 = r8.searching;
            if (r10 == 0) goto L_0x010a;
        L_0x00fe:
            r10 = "GlobalSearch";
            r0 = NUM; // 0x7f0c02fc float:1.8610742E38 double:1.053097776E-314;
            r10 = org.telegram.messenger.LocaleController.getString(r10, r0);
            r9.setText(r10);
        L_0x010a:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.onBindViewHolder(org.telegram.messenger.support.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (this.searching && i == this.searchResult.size()) {
                return 0;
            }
            return 1;
        }

        public int getPositionForScrollProgress(float f) {
            return (int) (((float) getItemCount()) * f);
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof GroupCreateUserCell) {
                ((GroupCreateUserCell) viewHolder.itemView).recycle();
            }
        }

        public void searchDialogs(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.queryServerSearch(null, true, false, false, false, 0, false);
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {

                /* renamed from: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$2$1 */
                class C14001 implements Runnable {

                    /* renamed from: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$2$1$1 */
                    class C13991 implements Runnable {
                        C13991() {
                        }

                        public void run() {
                            String toLowerCase = str.trim().toLowerCase();
                            if (toLowerCase.length() == 0) {
                                GroupCreateAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                                return;
                            }
                            String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
                            if (toLowerCase.equals(translitString) || translitString.length() == 0) {
                                translitString = null;
                            }
                            int i = 0;
                            String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
                            strArr[0] = toLowerCase;
                            if (translitString != null) {
                                strArr[1] = translitString;
                            }
                            ArrayList arrayList = new ArrayList();
                            ArrayList arrayList2 = new ArrayList();
                            int i2 = 0;
                            while (i2 < GroupCreateAdapter.this.contacts.size()) {
                                User user = (User) GroupCreateAdapter.this.contacts.get(i2);
                                String toLowerCase2 = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                                String translitString2 = LocaleController.getInstance().getTranslitString(toLowerCase2);
                                if (toLowerCase2.equals(translitString2)) {
                                    translitString2 = null;
                                }
                                int length = strArr.length;
                                int i3 = i;
                                int i4 = i3;
                                while (i3 < length) {
                                    StringBuilder stringBuilder;
                                    String str = strArr[i3];
                                    if (!toLowerCase2.startsWith(str)) {
                                        StringBuilder stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append(" ");
                                        stringBuilder2.append(str);
                                        if (!toLowerCase2.contains(stringBuilder2.toString())) {
                                            if (translitString2 != null) {
                                                if (!translitString2.startsWith(str)) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(" ");
                                                    stringBuilder.append(str);
                                                    if (translitString2.contains(stringBuilder.toString())) {
                                                    }
                                                }
                                            }
                                            if (user.username != null && user.username.startsWith(str)) {
                                                i4 = 2;
                                            }
                                            if (i4 == 0) {
                                                if (i4 != 1) {
                                                    arrayList2.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, str));
                                                } else {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("@");
                                                    stringBuilder.append(user.username);
                                                    String stringBuilder3 = stringBuilder.toString();
                                                    StringBuilder stringBuilder4 = new StringBuilder();
                                                    stringBuilder4.append("@");
                                                    stringBuilder4.append(str);
                                                    arrayList2.add(AndroidUtilities.generateSearchName(stringBuilder3, null, stringBuilder4.toString()));
                                                }
                                                arrayList.add(user);
                                                i2++;
                                                i = 0;
                                            } else {
                                                i3++;
                                            }
                                        }
                                    }
                                    i4 = 1;
                                    if (i4 == 0) {
                                        i3++;
                                    } else {
                                        if (i4 != 1) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("@");
                                            stringBuilder.append(user.username);
                                            String stringBuilder32 = stringBuilder.toString();
                                            StringBuilder stringBuilder42 = new StringBuilder();
                                            stringBuilder42.append("@");
                                            stringBuilder42.append(str);
                                            arrayList2.add(AndroidUtilities.generateSearchName(stringBuilder32, null, stringBuilder42.toString()));
                                        } else {
                                            arrayList2.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, str));
                                        }
                                        arrayList.add(user);
                                        i2++;
                                        i = 0;
                                    }
                                }
                                i2++;
                                i = 0;
                            }
                            GroupCreateAdapter.this.updateSearchResults(arrayList, arrayList2);
                        }
                    }

                    C14001() {
                    }

                    public void run() {
                        GroupCreateAdapter.this.searchAdapterHelper.queryServerSearch(str, true, false, false, false, 0, false);
                        Utilities.searchQueue.postRunnable(new C13991());
                    }
                }

                public void run() {
                    try {
                        GroupCreateAdapter.this.searchTimer.cancel();
                        GroupCreateAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    AndroidUtilities.runOnUIThread(new C14001());
                }
            }, 200, 300);
        }

        private void updateSearchResults(final ArrayList<User> arrayList, final ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    GroupCreateAdapter.this.searchResult = arrayList;
                    GroupCreateAdapter.this.searchResultNames = arrayList2;
                    GroupCreateAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    public GroupCreateActivity(Bundle bundle) {
        super(bundle);
        this.chatType = bundle.getInt("chatType", 0);
        this.isAlwaysShare = bundle.getBoolean("isAlwaysShare", false);
        this.isNeverShare = bundle.getBoolean("isNeverShare", false);
        this.isGroup = bundle.getBoolean("isGroup", false);
        this.chatId = bundle.getInt("chatId");
        this.maxCount = this.chatType == null ? MessagesController.getInstance(this.currentAccount).maxMegagroupCount : MessagesController.getInstance(this.currentAccount).maxBroadcastCount;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
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
        if (this.currentDeletingSpan != null) {
            this.currentDeletingSpan.cancelDeleteAnimation();
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
        int i = 2;
        this.doneButtonVisible = this.chatType == 2;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.chatType == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAddMembers", C0446R.string.ChannelAddMembers));
        } else if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", C0446R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", C0446R.string.AlwaysShareWithTitle));
            }
        } else if (!this.isNeverShare) {
            String str;
            int i2;
            ActionBar actionBar = this.actionBar;
            if (this.chatType == 0) {
                str = "NewGroup";
                i2 = C0446R.string.NewGroup;
            } else {
                str = "NewBroadcastList";
                i2 = C0446R.string.NewBroadcastList;
            }
            actionBar.setTitle(LocaleController.getString(str, i2));
        } else if (this.isGroup) {
            this.actionBar.setTitle(LocaleController.getString("NeverAllow", C0446R.string.NeverAllow));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", C0446R.string.NeverShareWithTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C21391());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        if (this.chatType != 2) {
            this.doneButton.setScaleX(0.0f);
            this.doneButton.setScaleY(0.0f);
            this.doneButton.setAlpha(0.0f);
        }
        this.fragmentView = new ViewGroup(context) {
            protected void onMeasure(int i, int i2) {
                int dp;
                i = MeasureSpec.getSize(i);
                i2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(i, i2);
                if (!AndroidUtilities.isTablet()) {
                    if (i2 <= i) {
                        dp = AndroidUtilities.dp(56.0f);
                        GroupCreateActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                        GroupCreateActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                        GroupCreateActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                    }
                }
                dp = AndroidUtilities.dp(144.0f);
                GroupCreateActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                GroupCreateActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                GroupCreateActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                GroupCreateActivity.this.scrollView.layout(0, 0, GroupCreateActivity.this.scrollView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight());
                GroupCreateActivity.this.listView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.listView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.listView.getMeasuredHeight());
                GroupCreateActivity.this.emptyView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.emptyView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.emptyView.getMeasuredHeight());
            }

            protected boolean drawChild(Canvas canvas, View view, long j) {
                j = super.drawChild(canvas, view, j);
                if (view == GroupCreateActivity.this.listView || view == GroupCreateActivity.this.emptyView) {
                    GroupCreateActivity.this.parentLayout.drawHeaderShadow(canvas, GroupCreateActivity.this.scrollView.getMeasuredHeight());
                }
                return j;
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
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_windowBackgroundWhite));
        viewGroup.addView(this.scrollView);
        this.spansContainer = new SpansContainer(context);
        this.scrollView.addView(this.spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        this.editText = new EditTextBoldCursor(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (GroupCreateActivity.this.currentDeletingSpan != null) {
                    GroupCreateActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateActivity.this.currentDeletingSpan = null;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setCursorColor(Theme.getColor(Theme.key_groupcreate_cursor));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(268435462);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        if (this.chatType == 2) {
            this.editText.setHintText(LocaleController.getString("AddMutual", C0446R.string.AddMutual));
        } else if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.editText.setHintText(LocaleController.getString("AlwaysAllowPlaceholder", C0446R.string.AlwaysAllowPlaceholder));
            } else {
                this.editText.setHintText(LocaleController.getString("AlwaysShareWithPlaceholder", C0446R.string.AlwaysShareWithPlaceholder));
            }
        } else if (!this.isNeverShare) {
            this.editText.setHintText(LocaleController.getString("SendMessageTo", C0446R.string.SendMessageTo));
        } else if (this.isGroup) {
            this.editText.setHintText(LocaleController.getString("NeverAllowPlaceholder", C0446R.string.NeverAllowPlaceholder));
        } else {
            this.editText.setHintText(LocaleController.getString("NeverShareWithPlaceholder", C0446R.string.NeverShareWithPlaceholder));
        }
        this.editText.setCustomSelectionActionModeCallback(new C13955());
        this.editText.setOnEditorActionListener(new C13966());
        this.editText.setOnKeyListener(new C13977());
        this.editText.addTextChangedListener(new C13988());
        this.emptyView = new EmptyTextProgressView(context);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", C0446R.string.NoContacts));
        viewGroup.addView(this.emptyView);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.listView = new RecyclerListView(context);
        this.listView.setFastScrollEnabled();
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView = this.listView;
        Adapter groupCreateAdapter = new GroupCreateAdapter(context);
        this.adapter = groupCreateAdapter;
        recyclerListView.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        context = this.listView;
        if (LocaleController.isRTL) {
            i = 1;
        }
        context.setVerticalScrollbarPosition(i);
        context = this.listView;
        ItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.itemDecoration = groupCreateDividerItemDecoration;
        context.addItemDecoration(groupCreateDividerItemDecoration);
        viewGroup.addView(this.listView);
        this.listView.setOnItemClickListener(new C21419());
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(GroupCreateActivity.this.editText);
                }
            }
        });
        updateHint();
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.editText != null) {
            this.editText.requestFocus();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.contactsDidLoaded) {
            if (this.emptyView != 0) {
                this.emptyView.showTextView();
            }
            if (this.adapter != 0) {
                this.adapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            if (this.listView != 0) {
                i = 0;
                i2 = ((Integer) objArr[0]).intValue();
                objArr = this.listView.getChildCount();
                if ((i2 & 2) != 0 || (i2 & 1) != 0 || (i2 & 4) != 0) {
                    while (i < objArr) {
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

    @Keep
    public void setContainerHeight(int i) {
        this.containerHeight = i;
        if (this.spansContainer != 0) {
            this.spansContainer.requestLayout();
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
                User user = groupCreateUserCell.getUser();
                if (user != null) {
                    groupCreateUserCell.setChecked(this.selectedContacts.indexOfKey(user.id) >= 0, true);
                }
            }
        }
    }

    private boolean onDonePressed() {
        int i = 0;
        ArrayList arrayList;
        if (this.chatType == 2) {
            arrayList = new ArrayList();
            for (int i2 = 0; i2 < this.selectedContacts.size(); i2++) {
                InputUser inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedContacts.keyAt(i2))));
                if (inputUser != null) {
                    arrayList.add(inputUser);
                }
            }
            MessagesController.getInstance(this.currentAccount).addUsersToChannel(this.chatId, arrayList, null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", this.chatId);
            presentFragment(new ChatActivity(bundle), true);
        } else {
            if (this.doneButtonVisible) {
                if (this.selectedContacts.size() != 0) {
                    arrayList = new ArrayList();
                    while (i < this.selectedContacts.size()) {
                        arrayList.add(Integer.valueOf(this.selectedContacts.keyAt(i)));
                        i++;
                    }
                    if (!this.isAlwaysShare) {
                        if (!this.isNeverShare) {
                            Bundle bundle2 = new Bundle();
                            bundle2.putIntegerArrayList("result", arrayList);
                            bundle2.putInt("chatType", this.chatType);
                            presentFragment(new GroupCreateFinalActivity(bundle2));
                        }
                    }
                    if (this.delegate != null) {
                        this.delegate.didSelectUsers(arrayList);
                    }
                    finishFragment();
                }
            }
            return false;
        }
        return true;
    }

    private void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.itemDecoration.setSearching(false);
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", C0446R.string.NoContacts));
    }

    private void updateHint() {
        if (!(this.isAlwaysShare || this.isNeverShare)) {
            if (this.chatType == 2) {
                this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
            } else if (this.selectedContacts.size() == 0) {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", C0446R.string.MembersCountZero, LocaleController.formatPluralString("Members", this.maxCount)));
            } else {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCount", C0446R.string.MembersCount, Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount)));
            }
        }
        if (this.chatType == 2) {
            return;
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (this.doneButtonVisible && this.allSpans.isEmpty()) {
            if (this.currentDoneButtonAnimation != null) {
                this.currentDoneButtonAnimation.cancel();
            }
            this.currentDoneButtonAnimation = new AnimatorSet();
            animatorSet = this.currentDoneButtonAnimation;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[]{0.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = false;
        } else if (!this.doneButtonVisible && !this.allSpans.isEmpty()) {
            if (this.currentDoneButtonAnimation != null) {
                this.currentDoneButtonAnimation.cancel();
            }
            this.currentDoneButtonAnimation = new AnimatorSet();
            animatorSet = this.currentDoneButtonAnimation;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[]{1.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[]{1.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = true;
        }
    }

    public void setDelegate(GroupCreateActivityDelegate groupCreateActivityDelegate) {
        this.delegate = groupCreateActivityDelegate;
    }

    public ThemeDescription[] getThemeDescriptions() {
        AnonymousClass11 anonymousClass11 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (GroupCreateActivity.this.listView != null) {
                    int childCount = GroupCreateActivity.this.listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = GroupCreateActivity.this.listView.getChildAt(i);
                        if (childAt instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) childAt).update(0);
                        }
                    }
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[37];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[6] = new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[14] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[15] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_groupcreate_hintText);
        themeDescriptionArr[16] = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_groupcreate_cursor);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, Theme.key_graySection);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, null, null, null, Theme.key_groupcreate_sectionShadow);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_groupcreate_checkbox);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_groupcreate_checkboxCheck);
        themeDescriptionArr[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_onlineText);
        themeDescriptionArr[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_offlineText);
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        AnonymousClass11 anonymousClass112 = anonymousClass11;
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, anonymousClass112, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, anonymousClass112, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, anonymousClass112, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, anonymousClass112, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, anonymousClass112, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, anonymousClass112, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, anonymousClass112, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[33] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_avatar_backgroundGroupCreateSpanBlue);
        themeDescriptionArr[34] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanBackground);
        themeDescriptionArr[35] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanText);
        themeDescriptionArr[36] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_avatar_backgroundBlue);
        return themeDescriptionArr;
    }
}
