package com.example.postit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.postit.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    static String TAG = "Login Activity";

    private ActivityProfileBinding binding;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        bindLoginButton();
        bindSignUpButton();

    }

    private void bindSignUpButton() {
        binding.signUpButton.setOnClickListener((v) -> {

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initViews();
    }

    private void initViews() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            binding.idEditText.setText(null);
            binding.idEditText.setEnabled(true);
            binding.passwordEditText.setEnabled(true);
            binding.idEditText.setText("");
            binding.passwordEditText.setText("");
            binding.loginButton.setText("로그인하기");
        } else {
            binding.idEditText.setText(user.getEmail());
            binding.idEditText.setEnabled(false);
            binding.passwordEditText.setText("******");
            binding.passwordEditText.setEnabled(false);
            binding.loginButton.setText("로그아웃하기");
        }
    }

    private boolean checkLogin() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null;
    }

    void updateUI(FirebaseUser user) {
        if (user == null) {
            binding.idEditText.setText(null);
            binding.idEditText.setEnabled(true);
            binding.passwordEditText.setEnabled(true);
            binding.idEditText.setText("");
            binding.passwordEditText.setText("");
            binding.loginButton.setText("로그인하기");
        } else {
            binding.idEditText.setText(user.getEmail());
            binding.idEditText.setEnabled(false);
            binding.passwordEditText.setText("******");
            binding.passwordEditText.setEnabled(false);
            binding.loginButton.setText("로그아웃하기");
        }
    }

    void bindLoginButton() {
        binding.loginButton.setOnClickListener((v) -> {
            if (auth.getCurrentUser() == null) {
                String id = binding.idEditText.getEditableText().toString();
                String password = binding.passwordEditText.getEditableText().toString();
                if (id.equals("")) {
                    return;
                }
                if (password.equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                requestLogin(id, password);
            } else {
                binding.progressView.setVisibility(View.VISIBLE);
                auth.signOut();
                updateUI(null);
                binding.progressView.setVisibility(View.GONE);
            }
        });
    }

    void requestLogin(String id, String password) {
        binding.progressView.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(id, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        binding.progressView.setVisibility(View.GONE);
                    }
                });
    }
//
//    private void bindSaveButton() {
//        binding.infoSaveButton.setOnClickListener((v)->{
//            if(!checkLogin()) return;
//            String name = binding.nameEditText.getText().toString();
//            String email = auth.getCurrentUser().getEmail();
//            int age = Integer.parseInt(binding.ageEditText.getText().toString());
//            int checkedRadioButtonId = binding.sexSelectGroup.getCheckedRadioButtonId();
//            int sex = -1;
//            RadioButton br = (RadioButton) (findViewById(checkedRadioButtonId));
//            if (br.getTag().equals("m")) {
//                sex=1;
//            }else{
//                sex=2;
//            }
//            ChildUser childUser = new ChildUser(name,email,age,sex);
//            String key = auth.getCurrentUser().getUid();
//            registerUserInfo(childUser,key);
//        });
//    }
//
//    private void registerUserInfo(ChildUser user, String key) {
//        db.collection("users").document(key)
//                .set(user)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("upload!!", "DocumentSnapshot successfully written!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("upload!!", "Error writing document", e);
//                    }
//                });
//    }
//
//    private void bindLoginButton() {
//        binding.loginButton.setOnClickListener((v) -> {
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            if (user == null) {
//                loginStart();
//            } else {
//                AuthUI.getInstance()
//                        .signOut(this)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            public void onComplete(@NonNull Task<Void> task) {
//                                binding.idTextView.setText("로그아웃 됨");
//                                binding.loginButton.setText("로그인 하기");
//                            }
//                        });
//            }
//        });
//
//    }
//    // 로그인 결과 콜백 등록
//    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
//            new FirebaseAuthUIActivityResultContract(),
//            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
//                @Override
//                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
//                    onSignInResult(result);
//                }
//            }
//    );
//    // 로그인 결과 콜백 함수
//    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
//        IdpResponse response = result.getIdpResponse();
//        if (result.getResultCode() == RESULT_OK) {
//            // Successfully signed in
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            binding.idTextView.setText(user.getEmail());
//            binding.loginButton.setText("로그아웃 하기");
//        } else {
//            // Sign in failed. If response is null the user canceled the
//            // sign-in flow using the back button. Otherwise check
//            // response.getError().getErrorCode() and handle the error.
//            // ...
//            Toast.makeText(this,"로그인에 실패했습니다.",Toast.LENGTH_SHORT).show();
//
//        }
//    }
//    //로그인 화면 시작
//    void loginStart() {
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build());
//
//        // Create and launch sign-in intent
//        Intent signInIntent = AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .setIsSmartLockEnabled(false)
//                .build();
//
//        signInLauncher.launch(signInIntent);
//    }
}
