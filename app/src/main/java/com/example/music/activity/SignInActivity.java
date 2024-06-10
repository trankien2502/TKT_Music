package com.example.music.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.music.R;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.ActivitySignInBinding;
import com.example.music.model.User;
import com.example.music.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SignInActivity extends BaseActivity {
    private ActivitySignInBinding mActivitySignInBinding;
    private final FirebaseAuth fireAuth = FirebaseAuth.getInstance();
    private List<User> mListUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(mActivitySignInBinding.getRoot());

        mActivitySignInBinding.layoutSignUp.setOnClickListener(
                v -> GlobalFuntion.startActivity(SignInActivity.this, SignUpActivity.class));

        mActivitySignInBinding.btnSignIn.setOnClickListener(v -> onClickValidateSignIn());
        mActivitySignInBinding.tvForgotPassword.setOnClickListener(v -> GlobalFuntion.startActivity(SignInActivity.this,ForgotPasswordActivity.class));
        getListUser();
    }

    private void onClickForgotPassword() {

    }

    private void onClickValidateSignIn() {
        String strEmail = mActivitySignInBinding.edtEmail.getText().toString().trim();
        String strPassword = mActivitySignInBinding.edtPassword.getText().toString().trim();
        if (StringUtil.isEmpty(strEmail)) {
            Toast.makeText(SignInActivity.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
        } else if (StringUtil.isEmpty(strPassword)) {
            Toast.makeText(SignInActivity.this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
        } else if (!StringUtil.isValidEmail(strEmail)) {
            Toast.makeText(SignInActivity.this, "Vui lòng nhập đúng định dạng email!", Toast.LENGTH_SHORT).show();
        } else {
            checkUser(strEmail,strPassword);

        }
    }
    private void checkUser(String strEmail, String strPassword){
        int id = 0;
        boolean checkEmail = false;
        boolean checkPassword = false;
        User userCheck = new User();
        for (User user : mListUser){
            if(user.getEmail().equals(strEmail)){
                checkEmail = true;
                if (user.getPassword().equals(strPassword)) {
                    checkPassword = true;
                    id = user.getId();
                    userCheck = user;
                }
            }

        }
        if(!checkEmail){
            Toast.makeText(this,"Email không tồn tại!",Toast.LENGTH_LONG).show();
        }
        if(!checkPassword){
            Toast.makeText(this,"Mật khẩu không đúng!",Toast.LENGTH_LONG).show();
        }
        if(checkEmail&&checkPassword){
            Toast.makeText(this,"Đăng nhập thành công!",Toast.LENGTH_LONG).show();
            GlobalFuntion.startActivity(SignInActivity.this, MainActivity.class);
            saveData(userCheck.getEmail(),userCheck.getId());
            finishAffinity();
        }
    }
    public void saveData(String email, int id) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginCheck", true);
        editor.putString("loginEmail", email);
        editor.putString("loginId", String.valueOf(id));
        editor.apply();
    }
    private void signInUser(String strEmail, String strPassword) {
        showProgressDialog(true);

        fireAuth.signInWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = fireAuth.getCurrentUser();
                        if (user != null) {
//                            User userObject = new User(user.getEmail(), password);
//                            if (user.getEmail() != null && user.getEmail().contains(Constant.ADMIN_EMAIL_FORMAT)) {
//                                userObject.setAdmin(true);
//                            }
//                            DataStoreManager.setUser(userObject);
                            GlobalFuntion.startActivity(SignInActivity.this,MainActivity.class);
                            finishAffinity();
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, "Đăng nhập thất bại, vui lòng thử lại!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getListUser() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://musicbasic-27309-default-rtdb.firebaseio.com");
        DatabaseReference reference = firebaseDatabase.getReference("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListUser = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user == null) {
                        return;
                    }

                    mListUser.add(user);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(SignInActivity.this, getString(R.string.msg_get_date_error));
            }
        });

    }
}