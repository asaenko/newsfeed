/*
 * Copyright 2012-2014 Daniel Serdyukov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elegion.newsfeed.fragment;

import android.accounts.Account;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.elegion.newsfeed.AppDelegate;
import com.elegion.newsfeed.R;

/**
 * @author Daniel Serdyukov
 */
public class SwipeToRefreshList extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SyncStatusObserver {

    private SwipeRefreshLayout mRefresher;

    private ListView mListView;

    private Object mSyncMonitor;

    private Account mAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fmt_swipe_to_refresh_list, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        return (mRefresher = (SwipeRefreshLayout) view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRefresher.setColorScheme(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light
        );
        mAccount = new Account(getString(R.string.app_name), AppDelegate.ACCOUNT_TYPE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRefresher.setOnRefreshListener(this);
        mSyncMonitor = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, this);
    }

    @Override
    public void onPause() {
        mRefresher.setOnRefreshListener(null);
        ContentResolver.removeStatusChangeListener(mSyncMonitor);
        super.onPause();
    }

    @Override
    public final void onRefresh() {
        onRefresh(mAccount);
    }

    @Override
    public final void onStatusChanged(int which) {
        mRefresher.post(new Runnable() {
            @Override
            public void run() {
                onSyncStatusChanged(mAccount, ContentResolver.isSyncActive(mAccount, AppDelegate.CONTENT_AUTHORITY));
            }
        });
    }

    protected void onRefresh(Account account) {

    }

    protected void onSyncStatusChanged(Account account, boolean isSyncActive) {

    }

    protected void setRefreshing(boolean refreshing) {
        mRefresher.setRefreshing(refreshing);
    }

    public void setListAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

}
