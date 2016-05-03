package com.innovation4you.napking.app;

import android.app.Application;

import com.innovation4you.napking.data.NapKingService;
import com.innovation4you.napking.data.provider.NapKingStaticDataProvider;

public class NapKingApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		NapKingService.init(new NapKingStaticDataProvider());
	}
}
