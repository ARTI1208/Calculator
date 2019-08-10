package ru.art2000.calculator.unit_converter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

import ru.art2000.calculator.R;
import ru.art2000.helpers.AndroidHelper;

public class UnitConverterFragment extends Fragment {

    private Context mContext;
    private TabLayout tabLayout;
    private ViewPager pager;
    private View v;
    private FragmentManager fm;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (v == null) {
            v = inflater.inflate(R.layout.unit_layout, null);
            pager = v.findViewById(R.id.pager);
            tabLayout = v.findViewById(R.id.tabs);
            mContext = getActivity();
            setNewAdapter();
        }

        return v;
    }

    public void setNewAdapter() {
        fm = getChildFragmentManager();
        UnitPagerAdapter pagerAdapter = new UnitPagerAdapter();
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < pagerAdapter.fragments.length; i++) {
                    pagerAdapter.fragments[i].isCurrentPage = i == position;
                }
                if (pagerAdapter.fragments[position].adapter != null) {
                    pagerAdapter.fragments[position].adapter.requestFocusForCurrent();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(pager);
    }

    class UnitPagerAdapter extends FragmentStatePagerAdapter {

        String[] categoriesNames;
        UnitPageFragment[] fragments;

        UnitPagerAdapter() {
            super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            categoriesNames = getResources().getStringArray(R.array.unit_converter_categories);
            String[] categoriesEnglish =
                    AndroidHelper.getLocalizedResources(mContext, Locale.ENGLISH)
                            .getStringArray(R.array.unit_converter_categories);
            fragments = new UnitPageFragment[categoriesNames.length];
            for (int i = 0; i < categoriesNames.length; ++i) {
                fragments[i] = UnitPageFragment.newInstance(categoriesEnglish[i].toLowerCase());
            }
            fragments[0].isCurrentPage = true;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return categoriesNames[position];
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return categoriesNames.length;
        }
    }
}
