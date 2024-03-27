package com.example.kanban_board_java.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import com.example.kanban_board_java.R;
import com.example.kanban_board_java.app.AppConstant;
import com.example.kanban_board_java.app.BaseActivity;
import com.example.kanban_board_java.app.KanbanBoardApp;
import com.example.kanban_board_java.databinding.ActivityLoginBinding;
import com.example.kanban_board_java.ui.home.HomeActivity;
import com.example.kanban_board_java.viewmodel.LoginViewModel;
import com.example.kanban_board_java.viewmodel.TaskViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import dagger.hilt.android.AndroidEntryPoint;
import kotlinx.coroutines.ExperimentalCoroutinesApi;

@ExperimentalCoroutinesApi
@AndroidEntryPoint
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private GoogleSignInClient googleSignInClient;
    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        googleObserver();
        initGoogleSignInClient();
        addTextChangedWatcher();
        initView();
        updateUI();
        setClick();
    }

    private void initView() {
        status = getIntent().getIntExtra(AppConstant.LOGIN_KEY, 0);
    }

    private void addTextChangedWatcher() {
        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isValidEmail(charSequence)) {
                    binding.etEmail.setError("Enter a valid email");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() < 6) {
                    binding.etPassword.setError("Enter min. 6 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void updateUI() {
        if (status == 0) {
            binding.tvTitle.setText(getString(R.string.sign_in));
            binding.btnSignIn.setText(getString(R.string.sign_in));
            binding.btnSignUpNow.setText(getString(R.string.sign_up_now));
        } else {
            binding.tvTitle.setText(getString(R.string.sign_up));
            binding.btnSignIn.setText(getString(R.string.sign_up));
            binding.btnSignUpNow.setText(getString(R.string.sign_in_hear));
        }
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void signInGoogle() {
        if (googleSignInClient != null) {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        }
    }

    ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent singInData = result.getData();
                    if (singInData != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(singInData);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                getGoogleAuthCredential(account);
                            }
                        } catch (ApiException e) {
                            KanbanBoardApp.getAppInstance().showToast(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        if (googleTokenId != null) {
            viewModel.signInWithGoogle(GoogleAuthProvider.getCredential(googleTokenId, null));
        }
    }

    private void googleObserver() {
        viewModel.googleSignInNetworkState().observe(this, it -> {
            switch (it.getStatus()) {
                case SUCCESS:
                    hideProgress();
                    if (it.getData() != null) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        prefsUtils.setUserId(user.getUid());
                        prefsUtils.setLogin(true);
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case LOADING:
                    showProgress();
                    break;
                default:
                    KanbanBoardApp.getAppInstance().showToast(it.getError().toString());
                    hideProgress();
                    break;
            }
        });
    }

    private void setClick() {
        binding.btnSignIn.setOnClickListener(this);
        binding.btnSignUpNow.setOnClickListener(this);
        binding.btnSignInGoogle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSignIn) {
            if (!binding.etEmail.getText().toString().isEmpty()) {
                if (!binding.etPassword.getText().toString().isEmpty()) {
                    if (status == 0) {
                        viewModel.signInWithEmail(binding.etEmail.getText().toString(), binding.etPassword.getText().toString());
                    } else {
                        viewModel.signUpWithEmail(binding.etEmail.getText().toString(), binding.etPassword.getText().toString());
                    }
                } else {
                    KanbanBoardApp.getAppInstance().showToast("Please enter password");
                }
            } else {
                KanbanBoardApp.getAppInstance().showToast("Please enter email");
            }
        } else if (view.getId() == R.id.btnSignInGoogle) {
            signInGoogle();
        } else if (view.getId() == R.id.btnSignUpNow) {
            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
            if (status == 0) {
                intent.putExtra(AppConstant.LOGIN_KEY, 1);
            } else {
                intent.putExtra(AppConstant.LOGIN_KEY, 0);
            }
            startActivity(intent);
            finish();
        }
    }
}
