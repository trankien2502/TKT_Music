package com.example.music.fragment;

import static android.graphics.Color.TRANSPARENT;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.activity.MainActivity;
import com.example.music.activity.SignUpActivity;
import com.example.music.adapter.PlaylistGridAdapter;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.FragmentPlaylistBinding;
import com.example.music.databinding.LayoutDialogAddNewPlaylistBinding;
import com.example.music.listener.IOnClickPlaylistItemListener;
import com.example.music.model.Playlist;
import com.example.music.model.Song;
import com.example.music.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {
    FragmentPlaylistBinding mFragmentPlaylistBinding ;
    PlaylistGridAdapter playlistGridAdapter;
    List<Playlist> mListPlaylist;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://musicbasic-27309-default-rtdb.firebaseio.com");


    static int idPlaylistSelected;
    static String namePlaylistSelected;
    private int idPlaylistMax=0;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPlaylistBinding = FragmentPlaylistBinding.inflate(inflater, container, false);
        getPlaylistOfUser();
        initListener();

        return mFragmentPlaylistBinding.getRoot();
    }

    private void initListener() {
        mFragmentPlaylistBinding.imgAddPlaylist.setOnClickListener(v -> addNewPlaylist());
        mFragmentPlaylistBinding.tvEditPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditMode =true;
                initAdapter();
            }
        });
    }

    private void initAdapter(){
        if (isEditMode){
            mFragmentPlaylistBinding.tvThongBaoPlaylist.setVisibility(View.VISIBLE);
            playlistGridAdapter = new PlaylistGridAdapter(mListPlaylist, this::goToEditPlaylist);

        } else {
            mFragmentPlaylistBinding.tvThongBaoPlaylist.setVisibility(View.GONE);
            playlistGridAdapter = new PlaylistGridAdapter(mListPlaylist, this::goToPlaylist);
        }
        mFragmentPlaylistBinding.rcvPlaylist.setAdapter(playlistGridAdapter);
    }
    private void addNewPlaylist() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_new_playlist);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        dialog.setCancelable(false);

        EditText editText = dialog.findViewById(R.id.edt_playlist_name);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnAddPlaylist = dialog.findViewById(R.id.btn_add_playlist);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
                String getIdCurrent = sharedPreferences.getString("loginId", String.valueOf(Context.MODE_PRIVATE));
                int idCurrent = Integer.parseInt(getIdCurrent);
                DatabaseReference reference = firebaseDatabase.getReference("playlist");
                String newNamePlaylist = editText.getText().toString().trim();
                for (Playlist playlist : mListPlaylist){
                    if (playlist.getName().equals(newNamePlaylist)){
                        Toast.makeText(getContext(),"Tên này đã tồn tại!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                int newIdPlaylist = idPlaylistMax+1;
                Playlist playlist = new Playlist(newIdPlaylist,newNamePlaylist,idCurrent);
                String pathPlaylist = String.valueOf(newIdPlaylist);
                reference.child(pathPlaylist).setValue(playlist, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getContext(),"Thêm playlist thành công!",Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }


        });
        dialog.show();

    }
    private void editPlaylist(Playlist playlist) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_edit_playlist);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        dialog.setCancelable(false);

        EditText editText = dialog.findViewById(R.id.edt_playlist_name);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnDelPlaylist = dialog.findViewById(R.id.btn_del_playlist);
        Button btnEditPlaylist = dialog.findViewById(R.id.btn_edit_playlist);

        editText.setText(playlist.getName());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditMode = false;
                initAdapter();
                dialog.dismiss();
            }
        });
        btnEditPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playlist.getId()==0){
                    Toast.makeText(getContext(),"Danh sách yêu thích không thể sửa",Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
                String getIdCurrent = sharedPreferences.getString("loginId", String.valueOf(Context.MODE_PRIVATE));
                int idCurrent = Integer.parseInt(getIdCurrent);
                DatabaseReference reference = firebaseDatabase.getReference("playlist");
                String newNamePlaylist = editText.getText().toString().trim();
                for (Playlist playlist : mListPlaylist){
                    if (playlist.getName().equals(newNamePlaylist)){
                        Toast.makeText(getContext(),"Tên này đã tồn tại!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                String pathPlaylist = String.valueOf(playlist.getId());
                DatabaseReference reference2 = firebaseDatabase.getReference("playlist/"+pathPlaylist+"/name");

                reference2.setValue(newNamePlaylist, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getContext(),"Chỉnh sửa playlist thành công!",Toast.LENGTH_SHORT).show();
                    }
                });
                isEditMode = false;
                initAdapter();
                dialog.dismiss();
            }
        });
        btnDelPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playlist.getId()==0){
                    Toast.makeText(getContext(),"Danh sách yêu thích không thể xóa",Toast.LENGTH_SHORT).show();
                    return;
                }
                String pathCmt = String.valueOf(playlist.getId());
                DatabaseReference reference = firebaseDatabase.getReference("playlist/"+pathCmt);
                AlertDialog Dialog = new AlertDialog.Builder(getContext())
                        .setTitle("XÓA PLAYLIST")
                        .setMessage("Bạn có chắc chắn muốn xóa playlist "+playlist.getName()+" không?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reference.removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(getContext(),"Xóa playlist thành công!",Toast.LENGTH_SHORT).show();

                                    }
                                });
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
                isEditMode = false;
                initAdapter();
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    private void getPlaylistOfUser(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        String getIdCurrent = sharedPreferences.getString("loginId", String.valueOf(Context.MODE_PRIVATE));
        int idCurrent = Integer.parseInt(getIdCurrent);
        DatabaseReference reference = firebaseDatabase.getReference("playlist");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListPlaylist = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Playlist playlist = dataSnapshot.getValue(Playlist.class);
                    if (playlist == null) {
                        return;
                    }
                    if (playlist.getUser_id() == idCurrent){
                        mListPlaylist.add(playlist);
                    }
                    idPlaylistMax = playlist.getId();
                }
                mListPlaylist.add(0,new Playlist(0,"Bài hát yêu thích",idCurrent));
                initUi();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void initUi() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),3);
        mFragmentPlaylistBinding.rcvPlaylist.setLayoutManager(gridLayoutManager);
        if (isEditMode){
            mFragmentPlaylistBinding.tvThongBaoPlaylist.setVisibility(View.VISIBLE);
            playlistGridAdapter = new PlaylistGridAdapter(mListPlaylist, this::goToEditPlaylist);
        }else {
            playlistGridAdapter = new PlaylistGridAdapter(mListPlaylist, this::goToPlaylist);

        }
        mFragmentPlaylistBinding.rcvPlaylist.setAdapter(playlistGridAdapter);
    }

    private void goToEditPlaylist(Playlist playlist) {
        editPlaylist(playlist);
    }


    private void goToPlaylist(Playlist playlist){
        idPlaylistSelected = playlist.getId();
        namePlaylistSelected = playlist.getName();
        replaceFragment(new PlaylistListSongFragment());
        MainActivity.mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.library).setChecked(true);
    }
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
}
