package com.example.music.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.ActivitySignUpBinding;
import com.example.music.model.Song;
import com.example.music.model.User;
import com.example.music.utils.StringUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends BaseActivity {

    private ActivitySignUpBinding mActivitySignUpBinding;
    private final FirebaseAuth fireAuth = FirebaseAuth.getInstance();
    private List<User> mListUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(mActivitySignUpBinding.getRoot());


        mActivitySignUpBinding.imgBack.setOnClickListener(v -> onBackPressed());
        mActivitySignUpBinding.layoutSignIn.setOnClickListener(v -> finish());
        mActivitySignUpBinding.btnSignUp.setOnClickListener(v -> onClickValidateSignUp());
        getListUser();

    }

    private void onClickValidateSignUp() {
        String strEmail = mActivitySignUpBinding.edtEmail.getText().toString().trim();
        String strPassword = mActivitySignUpBinding.edtPassword.getText().toString().trim();
        String strPasswordConfirm = mActivitySignUpBinding.edtPasswordConfirm.getText().toString().trim();
        if (StringUtil.isEmpty(strEmail)) {
            Toast.makeText(SignUpActivity.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
        } else if (StringUtil.isEmpty(strPassword)) {
            Toast.makeText(SignUpActivity.this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
        } else if (StringUtil.isEmpty(strPasswordConfirm)) {
            Toast.makeText(SignUpActivity.this, "Vui lòng nhập xác nhận mật khẩu!", Toast.LENGTH_SHORT).show();
        } else if (!StringUtil.isGoodField(strPassword)){
            Toast.makeText(SignUpActivity.this, "Vui lòng nhập mật khẩu có ít nhất 8 kí tự!", Toast.LENGTH_SHORT).show();
        } else if (!strPassword.equals(strPasswordConfirm)) {
            Toast.makeText(SignUpActivity.this, "Xác nhận mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
        }else if (!StringUtil.isValidEmail(strEmail)) {
            Toast.makeText(SignUpActivity.this, "Vui lòng nhập đúng định dạng email!", Toast.LENGTH_SHORT).show();
        } else {
            //signUpUser2(strEmail, strPassword);
            checkUser(strEmail,strPassword);
        }
    }

    private void signUpUser(String strEmail, String strPassword) {
        showProgressDialog(true);

        fireAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgressDialog(false);
                        if (task.isSuccessful()) {
                            FirebaseUser user = fireAuth.getCurrentUser();
                            if (user != null) {
//                            
                                GlobalFuntion.startActivity(SignUpActivity.this,MainActivity.class);
                                finishAffinity();
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Đăng ký không thành công, vui lòng thử lại!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void checkUser(String strEmail, String strPassword){
        int id = 0;
        boolean checkEmail = true;
        for (User user : mListUser){
            if(user.getEmail().equals(strEmail)){
                checkEmail = false;
            }
            id = user.getId();
        }
        if(checkEmail){
            signUpUser2(id,strEmail,strPassword);
        }else {
            Toast.makeText(this,"Email đã tồn tại",Toast.LENGTH_LONG).show();
        }
    }
    private void signUpUser2(int id,String strEmail, String strPassword) {
        showProgressDialog(true);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://musicbasic-27309-default-rtdb.firebaseio.com");
        DatabaseReference reference = firebaseDatabase.getReference("user");
        User user = new User(id+1,strEmail,strPassword);
        String pathOb = String.valueOf(id+1);

        reference.child(pathOb).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                showProgressDialog(false);
                Toast.makeText(SignUpActivity.this, "Đăng ký thành công!",
                        Toast.LENGTH_SHORT).show();
                //saveData(user.getEmail(),user.getId());
                GlobalFuntion.startActivity(SignUpActivity.this,SignInActivity.class);
                finishAffinity();
            }
        });
    }
    public void checkLoginStatus() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        Boolean getCheck = sharedPreferences.getBoolean("loginCheck", Boolean.valueOf(String.valueOf(Context.MODE_PRIVATE)));
        String getEmail = sharedPreferences.getString("loginEmail", String.valueOf(Context.MODE_PRIVATE));
        if(getCheck){
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
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
    public void logOut() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginCheck", false);
        editor.putString("loginEmail", "");
        editor.apply();
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
                GlobalFuntion.showToastMessage(SignUpActivity.this, getString(R.string.msg_get_date_error));
            }
        });

    }

}