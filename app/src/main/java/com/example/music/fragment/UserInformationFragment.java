package com.example.music.fragment;

import static android.graphics.Color.TRANSPARENT;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.activity.MainActivity;
import com.example.music.activity.SignInActivity;
import com.example.music.activity.SignUpActivity;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.FragmentUserInformationBinding;
import com.example.music.model.Playlist;
import com.example.music.model.Song;
import com.example.music.model.User;
import com.example.music.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserInformationFragment extends Fragment {

    private FragmentUserInformationBinding mFragmentUserInformationBinding;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://musicbasic-27309-default-rtdb.firebaseio.com");
    List<User> mListUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentUserInformationBinding = FragmentUserInformationBinding.inflate(inflater,container,false);
        getCurrentUser();

        initListener();
        return mFragmentUserInformationBinding.getRoot();
    }

    private void initUI(User user) {
        mFragmentUserInformationBinding.edtName.setText(user.getName());
        mFragmentUserInformationBinding.edtPhone.setText(user.getPhone());
        mFragmentUserInformationBinding.edtEmail.setText(user.getEmail());
    }

    private void initListener() {
        mFragmentUserInformationBinding.btnLogOut.setOnClickListener(v->showDialogLogOut());
        mFragmentUserInformationBinding.btnEditPassword.setOnClickListener(v -> clickOnEditPassword());
        mFragmentUserInformationBinding.btnEditInformation.setOnClickListener(v -> clickOnEditInformation());
    }

    private void clickOnEditPassword(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_change_password);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        dialog.setCancelable(false);

        EditText edtCurrentPassword = dialog.findViewById(R.id.edt_current_password);
        EditText edtNewPassword = dialog.findViewById(R.id.edt_new_password_change);
        EditText edtConfirmChangePassword = dialog.findViewById(R.id.edt_confirm_password_change);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_change_password);
        Button btnChangePassword = dialog.findViewById(R.id.btn_change_password);



        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strCurrentPassword = edtCurrentPassword.getText().toString().trim();
                String strNewPassword = edtNewPassword.getText().toString().trim();
                String strConfirmChangePassword = edtConfirmChangePassword.getText().toString().trim();
                if (StringUtil.isEmpty(strCurrentPassword)) {
                    Toast.makeText(getContext(), "Vui lòng nhập mật khẩu hiện tại!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (StringUtil.isEmpty(strNewPassword)) {
                    Toast.makeText(getContext(), "Vui lòng nhập mật khẩu mới!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (StringUtil.isEmpty(strConfirmChangePassword)) {
                    Toast.makeText(getContext(), "Vui lòng nhập xác nhận mật khẩu mới!", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!strNewPassword.equals(strConfirmChangePassword)) {
                    Toast.makeText(getContext(), "Xác nhận mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!StringUtil.isGoodField(strNewPassword)){
                    Toast.makeText(getContext(), "Vui lòng nhập mật khẩu mới có ít nhất 8 kí tự!", Toast.LENGTH_SHORT).show();
                    return;
                }else if(!mListUser.get(0).getPassword().equals(strCurrentPassword)){
                    Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show();
                    return;
                }else if(strNewPassword.equals(strCurrentPassword)){
                    Toast.makeText(getContext(), "Mật khẩu mới không được trùng mật khẩu hiện tại!", Toast.LENGTH_SHORT).show();
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
        String pathUser = String.valueOf(mListUser.get(0).getId()) ;
        DatabaseReference reference = firebaseDatabase.getReference("user/"+pathUser+"/password");
        reference.setValue(strNewPassword, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getContext(),"Đổi mật khẩu thành công!",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void clickOnEditInformation(){
        AlertDialog Dialog = new AlertDialog.Builder(getContext())
                .setTitle("XÁC NHẬN SỬA THÔNG TIN")
                .setMessage("Bạn có chắc chắn muốn sửa thông tin đã nhập?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editInformationUser();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        Dialog.show();
    }
    private void editInformationUser() {
        String strPhone = StringUtil.getStringPhoneNumber(mFragmentUserInformationBinding.edtPhone.getText().toString().trim());
        String strEmail = mFragmentUserInformationBinding.edtEmail.getText().toString().trim();
        String strName = mFragmentUserInformationBinding.edtName.getText().toString().trim();
        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(getContext(), "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show();

        } else if (StringUtil.isEmpty(strPhone)) {
            Toast.makeText(getContext(), "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show();

        } else if (!StringUtil.isValidPhoneNumber(strPhone)){
            Toast.makeText(getContext(), "Số điện thoại không đúng định dạng!", Toast.LENGTH_SHORT).show();

        } else {
            User user = new User(mListUser.get(0).getId(),strName,strPhone,strEmail,mListUser.get(0).getPassword());
            String pathUser = String.valueOf(mListUser.get(0).getId()) ;
            DatabaseReference reference = firebaseDatabase.getReference("user");
            reference.child(pathUser).setValue(user, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Toast.makeText(getContext(),"Đổi thông tin thành công!",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void logOut() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginCheck", false);
        editor.putString("loginEmail", "");
        editor.putString("loginId", "");
        editor.apply();
        Toast.makeText(getContext(),"ĐÃ ĐĂNG XUẤT",Toast.LENGTH_SHORT).show();
        replaceFragment(new HomeFragment());
        MainActivity.mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.my_music).setChecked(true);
    }
    private void showDialogLogOut(){
        AlertDialog Dialog = new AlertDialog.Builder(getContext())
                .setTitle("ĐĂNG XUẤT")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        Dialog.show();
    }
    private void getCurrentUser() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        String getIdCurrent = sharedPreferences.getString("loginId", String.valueOf(Context.MODE_PRIVATE));
        int idCurrent = Integer.parseInt(getIdCurrent);
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
                    if (user.getId()==idCurrent){
                        mListUser.add(user);
                        initUI(user);
                        break;
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getContext(), getString(R.string.msg_get_date_error));
            }
        });

    }
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
}