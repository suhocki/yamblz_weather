package tljfn.yamblzweather.ui.start;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;

import javax.inject.Inject;

import arch.ui.BaseFragment;
import arch.ui.NavigationController;
import arch.util.AutoClearedValue;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import tljfn.yamblzweather.R;
import tljfn.yamblzweather.databinding.FragmentStartBinding;

/**
 * Created by Maksim Sukhotski on 7/9/2017.
 */

public class StartFragment extends BaseFragment {
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private StartViewModel startViewModel;

    @Inject
    NavigationController navigationController;

    @Override
    public void onPause() {
        super.onPause();
        disposable.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        disposable.add(startViewModel.getWeather()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(startViewModel.weather::setValue, startViewModel::onError));
    }

    @NonNull
    @Override
    public Integer getLayoutRes() {
        return R.layout.fragment_start;
    }

    @Override
    public String getToolbarTitle() {
        return "Стартовый экран";
    }

    @Override
    public Integer getDrawerMode() {
        return DrawerLayout.LOCK_MODE_UNLOCKED;
    }

    @Override
    public void onViewModelAttach() {
        startViewModel = ViewModelProviders.of(this, viewModelFactory).get(StartViewModel.class);
    }

    @Override
    public void onBindingCreated(AutoClearedValue binding) {
        FragmentStartBinding startBinding = (FragmentStartBinding) binding.get();

        startViewModel.weather.observe(this, weather -> {
            startBinding.setWeather(weather);
            startBinding.executePendingBindings();
        });

        startBinding.setOnRefreshListener(startViewModel::updateWeather);
        startBinding.setOnChooseCityCallback(() -> navigationController.navigateToChooseCity(getActivity(),
                new NavigationController.GooglePlacesExceptionCallback() {
                    @Override
                    public void onGooglePlacesRepairs(String message) {
                        // todo handle error
                    }

                    @Override
                    public void onGooglePlacesNotAvailable(String message) {
                        // todo handle error
                    }
                }));
    }
}
