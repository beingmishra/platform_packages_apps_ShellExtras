/*
 * Copyright (C) 2017 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shell.extras;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import android.util.Log;

import com.shell.extras.navigation.BottomNavigationViewCustom;
import com.shell.extras.tabs.Lockscreen;
import com.shell.extras.tabs.Misc;
import com.shell.extras.tabs.Navigation;
import com.shell.extras.tabs.Statusbar;
import com.shell.extras.tabs.System;

public class ShellExtras extends SettingsPreferenceFragment {

    public ShellExtras() {
    }

    MenuItem menuitem;

    PagerAdapter mPagerAdapter;
    
    private Toast mTapToast;

    public static final String ABOUT_PACKAGE_NAME = "com.pearl.about";
    public static Intent INTENT_ABOUT = new Intent(Intent.ACTION_MAIN)
            .setClassName(ABOUT_PACKAGE_NAME, ABOUT_PACKAGE_NAME + ".MainActivity");

    public static final String UPDATER_PACKAGE_NAME = "com.pearlos.pearlot";
    public static Intent INTENT_UPDATER = new Intent(Intent.ACTION_MAIN)
            .setClassName(UPDATER_PACKAGE_NAME, UPDATER_PACKAGE_NAME + ".UpdatesActivity");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.shellextras, container, false);

        final BottomNavigationViewCustom navigation = view.findViewById(R.id.navigation);

        final ViewPager viewPager = view.findViewById(R.id.viewpager);

        navigation.setBackground(new ColorDrawable(getResources().getColor(R.color.BottomBarBackgroundColor)));

        mPagerAdapter = new PagerAdapter(getFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationViewCustom.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.system:
                        viewPager.setCurrentItem(0);
			Log.v("In onNavigationItemSelected","System fragment selected with verbose");
                        return true;
                    case R.id.lockscreen:
                        viewPager.setCurrentItem(1);
                        Log.d("In onNavigationItemSelected","LockScreen fragment selected with debug");
                        return true;
                    case R.id.statusbar:
                        viewPager.setCurrentItem(2);
                        return true;
                    case R.id.navigation:
                        viewPager.setCurrentItem(3);
                        return true;
                    case R.id.misc:
                        viewPager.setCurrentItem(4);
                        return true;
                }
                return false;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(menuitem != null) {
                    menuitem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                menuitem = navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    class PagerAdapter extends FragmentPagerAdapter {

        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            frags[0] = new System();
            frags[1] = new Lockscreen();
            frags[2] = new Statusbar();
            frags[3] = new Navigation();
            frags[4] = new Misc();
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private String[] getTitles() {
        String titleString[];
        titleString = new String[]{
                getString(R.string.bottom_nav_system_title),
                getString(R.string.bottom_nav_lockscreen_title),
                getString(R.string.bottom_nav_statusbar_title),
                getString(R.string.bottom_nav_navigation_title),
                getString(R.string.bottom_nav_misc_title)
        };

        return titleString;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SHELLEXTRAS;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 0, 0, R.string.dialog_team_title);
        menu.add(0, 1, 0, R.string.dialog_updater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                if (INTENT_ABOUT != null) {
                    try {
                        startActivity(INTENT_ABOUT);
                    } catch (ActivityNotFoundException activityNotFound) {
                        mTapToast = Toast.makeText(getContext(),"Aww it seems like you dont have the app installed",Toast.LENGTH_LONG);
                        mTapToast.show();
                    }
                 }
            case 1:
                if (INTENT_UPDATER != null) {
                    try {
                        startActivity(INTENT_UPDATER);
                        mTapToast = Toast.makeText(getContext(),"Welcome, to Pearl Updater",Toast.LENGTH_SHORT);
                        mTapToast.show();
                        Log.v("Intent_Updater","Sucess");
                    } catch (ActivityNotFoundException activityNotFound) {
                        Log.v("Intent_Updater","Failed");
                    }
                }
                return true;
            default:
                return false;
        }
    }

    public static void showDialog(Fragment context, DialogFragment dialog) {
        FragmentTransaction ft = context.getChildFragmentManager().beginTransaction();
        Fragment prev = context.getChildFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialog.show(ft, "dialog");
    }
}
