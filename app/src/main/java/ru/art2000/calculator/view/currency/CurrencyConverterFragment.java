package ru.art2000.calculator.view.currency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.CurrencyLayoutBinding;
import ru.art2000.calculator.model.currency.CurrencyItem;
import ru.art2000.calculator.model.currency.LoadingState;
import ru.art2000.calculator.view_model.currency.CurrencyConverterModel;
import ru.art2000.extensions.IReplaceable;
import ru.art2000.extensions.NavigationFragment;
import ru.art2000.helpers.AndroidHelper;

public class CurrencyConverterFragment extends NavigationFragment {

    private Context mContext;

    private String titleUpdatedString;

    private CurrencyConverterModel model;
    private CurrencyLayoutBinding binding;
    private CurrencyListAdapter adapter;

    @SuppressLint({"InflateParams", "RestrictedApi"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (binding == null) {

            mContext = requireActivity();
            titleUpdatedString = mContext.getString(R.string.updated);

            model = new ViewModelProvider(this,
                    new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
            ).get(CurrencyConverterModel.class);


            binding = CurrencyLayoutBinding.inflate(inflater);


            binding.toolbar.inflateMenu(R.menu.currencies_converter_menu);

            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            adapter = new CurrencyListAdapter(mContext, model);

            binding.currencyList.setLayoutManager(llm);
            binding.currencyList.setAdapter(adapter);

            binding.currencyList.setEmptyViewGenerator((context, parent, viewType) -> {
                TextView emptyView = new TextView(mContext);
                emptyView.setText(R.string.empty_text_no_currencies_added);
                emptyView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                emptyView.setGravity(Gravity.CENTER);
                return emptyView;
            });

            int colorAccent = AndroidHelper.getColorAttribute(mContext, R.attr.colorAccent);

            binding.refresher.setColorSchemeColors(colorAccent);
            binding.refresher.setProgressViewEndTarget(true,
                    binding.refresher.getProgressViewEndOffset());
            binding.refresher.setOnRefreshListener(model::loadData);

            ActionMenuItemView editMenuItem = binding.getRoot().findViewById(R.id.edit_currencies);
            editMenuItem.getItemData().getIcon().setColorFilter(
                    new PorterDuffColorFilter(colorAccent, PorterDuff.Mode.SRC_ATOP));
            editMenuItem.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CurrenciesSettingsActivity.class);
                startActivity(intent);
            });
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model.getLoadingState().observe(getViewLifecycleOwner(), this::applyLoadingState);
        model.getUpdateDate().observe(getViewLifecycleOwner(), this::setCurrenciesUpdateDate);
        model.getVisibleList().observe(getViewLifecycleOwner(), this::applyData);
    }

    @Override
    public void onReselected() {
        binding.currencyList.smoothScrollToPosition(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.removeEditText();
    }

    @Override
    protected void onShown(@Nullable IReplaceable previousReplaceable) {
        Log.d("CurShown", String.valueOf(model.isFirstUpdateDone()));
        if (!model.isFirstUpdateDone()) {
            model.loadData();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public int getReplaceableId() {
        return R.id.navigation_currency;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_currency;
    }

    @Override
    public int getTitle() {
        return R.string.title_currency;
    }


    private void applyLoadingState(LoadingState loadingState) {
        setRefreshStatus(loadingState == LoadingState.LOADING_STARTED);

        if (loadingState == LoadingState.UNINITIALISED || loadingState == LoadingState.LOADING_ENDED)
            return;

        int messageId;
        switch (loadingState) {
            case LOADING_STARTED:
                messageId = R.string.currencies_update_toast;
                break;
            case NETWORK_ERROR:
                messageId = R.string.currencies_no_internet;
                break;
            default:
                messageId = R.string.currencies_update_failed;
                break;
        }

        Toast.makeText(mContext, messageId, Toast.LENGTH_SHORT).show();
    }

    private void applyData(List<CurrencyItem> currencyItems) {
        adapter.setNewData(currencyItems);
    }

    private void setCurrenciesUpdateDate(String date) {
        binding.toolbar.setTitle(titleUpdatedString + " " + date);
    }

    private void setRefreshStatus(boolean status) {
        if (status && binding.refresher.isRefreshing())
            return;

        binding.refresher.setRefreshing(status);
    }
}