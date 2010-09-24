package com.nagazuka.mobile.android.goedkooptanken;

import android.app.Application;
import android.content.Context;

public class GoedkoopTankenApp extends Application {

	private static GoedkoopTankenApp instance;
	
	public GoedkoopTankenApp() {
		instance = this;
	}
	
	public static Context getContext() {
		return instance.getApplicationContext();		
	}
}
