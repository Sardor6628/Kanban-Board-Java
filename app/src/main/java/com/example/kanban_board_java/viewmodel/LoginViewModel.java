package com.example.kanban_board_java.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.kanban_board_java.utils.resource.Resource;
import com.example.kanban_board_java.data.response.UserResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.ExperimentalCoroutinesApi;

@ExperimentalCoroutinesApi
@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<Resource<UserResponse>> _googleSignIn = new MutableLiveData<>();

    @Inject
    public LoginViewModel(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public LiveData<Resource<UserResponse>> googleSignInNetworkState() {
        return _googleSignIn;
    }

    public void signInWithGoogle(AuthCredential authCredential) {
        Thread newThread = new Thread(() -> {
            _googleSignIn.postValue(Resource.loading());
            firebaseAuth.signInWithCredential(authCredential).addOnSuccessListener(authResult -> {
                if (authResult != null) {
                    String uid = authResult.getUser().getUid();
                    String name = authResult.getUser().getDisplayName();
                    UserResponse user = new UserResponse(uid, name);
                    _googleSignIn.postValue(Resource.success(user));
                } else {
                    _googleSignIn.postValue(Resource.error("User not found after authentication"));
                }
            }).addOnFailureListener(e -> {
                _googleSignIn.postValue(Resource.error(e.getMessage()));
            });
        });
        newThread.start();
    }

    public void signInWithEmail(String email, String password) {
        Thread newThread = new Thread(() -> {
            _googleSignIn.postValue(Resource.loading());
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();
                                String name = firebaseUser.getDisplayName();
                                UserResponse user = new UserResponse(uid, name);
                                _googleSignIn.postValue(Resource.success(user));
                            } else {
                                _googleSignIn.postValue(Resource.error("Something Went Wrong"));
                            }
                        } else {
                            _googleSignIn.postValue(Resource.error("Something Went Wrong"));
                        }
                    })
                    .addOnFailureListener(e -> _googleSignIn.postValue(Resource.error(e.getMessage())));
        });
        newThread.start();
    }

    public void signUpWithEmail(String email, String password) {
        Thread newThread = new Thread(() -> {
            _googleSignIn.postValue(Resource.loading());
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            String name = firebaseUser.getDisplayName();
                            UserResponse user = new UserResponse(uid, name);
                            _googleSignIn.postValue(Resource.success(user));
                        } else {
                            _googleSignIn.postValue(Resource.error("Something Went Wrong"));
                        }
                    })
                    .addOnFailureListener(e -> _googleSignIn.postValue(Resource.error(e.getMessage())));
        });
        newThread.start();
    }
}