package com.innovation4you.napking.app;

import android.app.Application;

import com.innovation4you.napking.data.NapKingRepository;
import com.innovation4you.napking.data.provider.NapKingStaticDataProvider;

public class NapKingApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		NapKingRepository.init(new NapKingStaticDataProvider(this));
	}
}
