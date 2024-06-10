package com.example.music.activity;

import static android.graphics.Color.TRANSPARENT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.music.R;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.ActivityForgotPasswordBinding;
import com.example.music.model.User;
import com.example.music.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ForgotPasswordActivity extends BaseActivity {
    ActivityForgotPasswordBinding mActivityForgotPasswordBinding;
    private List<User> mListUser;
    String emailVerification;
    int idUserVerification;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://musicbasic-27309-default-rtdb.firebaseio.com");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(mActivityForgotPasswordBinding.getRoot());

        mActivityForgotPasswordBinding.btnVerification.setOnClickListener(v -> onClickVerification());
        mActivityForgotPasswordBinding.imgBackVeritification.setOnClickListener(v -> onBackPressed());
        mActivityForgotPasswordBinding.tvCancelForgotPass.setOnClickListener(v -> finish());
        getListUser();
    }

    private void onClickVerification() {
        String strEmail = mActivityForgotPasswordBinding.edtEmailVerification.getText().toString().trim();
        String strPhone = mActivityForgotPasswordBinding.edtPhoneVerification.getText().toString().trim();
        if (StringUtil.isEmpty(strEmail)) {
            Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập email xác minh!", Toast.LENGTH_SHORT).show();
        } else if (StringUtil.isEmpty(strPhone)) {
            Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập số điện thoại xác minh!", Toast.LENGTH_SHORT).show();
        } else {
            onVerificationSuccess(strEmail,strPhone);
        }
    }

    private void onVerificationSuccess(String strEmail, String strPhone) {
        int id = 0;
        boolean checkEmail = false;
        boolean checkPhone = false;
        strPhone = StringUtil.getStringPhoneNumber(strPhone);
        User userCheck = new User();
        for (User user : mListUser){
            if(user.getEmail().equals(strEmail)){
                checkEmail = true;
                if (user.getPhone().equals(strPhone)) {
                    checkPhone = true;
                    id = user.getId();
                    userCheck = user;
                }
                break;
            }

        }
        if(!checkEmail){
            Toast.makeText(this,"Email không tồn tại!",Toast.LENGTH_LONG).show();

        }
        if(!checkPhone){
            Toast.makeText(this,"Số điện thoại không đúng!",Toast.LENGTH_LONG).show();

        }
        if(checkEmail&&checkPhone){
            Toast.makeText(this,"Xác minh thành công!",Toast.LENGTH_LONG).show();
            emailVerification = userCheck.getEmail();
            idUserVerification = userCheck.getId();
            updateNewPassword();
        }

    }

    private void updateNewPassword() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_change_password);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        dialog.setCancelable(true);

        EditText edtCurrentPassword = dialog.findViewById(R.id.edt_current_password);
        EditText edtNewPassword = dialog.findViewById(R.id.edt_new_password_change);
        EditText edtConfirmChangePassword = dialog.findViewById(R.id.edt_confirm_password_change);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_change_password);
        Button btnChangePassword = dialog.findViewById(R.id.btn_change_password);

        edtCurrentPassword.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strNewPassword = edtNewPassword.getText().toString().trim();
                String strConfirmChangePassword = edtConfirmChangePassword.getText().toString().trim();
                if (StringUtil.isEmpty(strNewPassword)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập mật khẩu mới!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (StringUtil.isEmpty(strConfirmChangePassword)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập xác nhận mật khẩu mới!", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!strNewPassword.equals(strConfirmChangePassword)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Xác nhận mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!StringUtil.isGoodField(strNewPassword)){
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập mật khẩu mới có ít nhất 8 kí tự!", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    editPassword(strNewPassword);
                }
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void editPassword(String strNewPassword) {
        String pathUser = String.valueOf(idUserVerification);
        DatabaseReference reference = firebaseDatabase.getReference("user/"+pathUser+"/password");
        reference.setValue(strNewPassword, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(ForgotPasswordActivity.this,"Đổi mật khẩu thành công!",Toast.LENGTH_SHORT).show();
                GlobalFuntion.startActivity(ForgotPasswordActivity.this, SignInActivity.class);
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
                GlobalFuntion.showToastMessage(ForgotPasswordActivity.this, getString(R.string.msg_get_date_error));
            }
        });
    }
}