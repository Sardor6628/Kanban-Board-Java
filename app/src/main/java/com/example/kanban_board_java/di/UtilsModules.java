package com.example.kanban_board_java.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kanban_board_java.app.AppConstant;
import com.example.kanban_board_java.utils.PreferenceUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import kotlinx.coroutines.ExperimentalCoroutinesApi;

import javax.inject.Singleton;

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent.class)
public class UtilsModules {

    @Singleton
    @Provides
    public SharedPreferences provideSharedPreferences(@ApplicationContext Context context) {
        return context.getSharedPreferences(AppConstant.PREF_KEY, Context.MODE_PRIVATE);
    }

    @Singleton
    @Provides
    public PreferenceUtils providePrefUtils(SharedPreferences preferences) {
        return new PreferenceUtils(preferences);
    }

    @Singleton
    @Provides
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Singleton
    @Provides
    public FirebaseFirestore provideFirebaseStore() {
        return FirebaseFirestore.getInstance();
    }
}

