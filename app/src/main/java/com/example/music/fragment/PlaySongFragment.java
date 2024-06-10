package com.example.music.fragment;

import static android.graphics.Color.TRANSPARENT;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.music.R;
import com.example.music.activity.SignInActivity;
import com.example.music.adapter.PlaylistGridAdapter;
import com.example.music.constant.Constant;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.FragmentPlaySongBinding;
import com.example.music.listener.IOnClickPlaylistItemListener;
import com.example.music.model.Playlist;
import com.example.music.model.Song;
import com.example.music.service.MusicService;
import com.example.music.utils.AppUtil;
import com.example.music.utils.GlideUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.ToIntBiFunction;

@SuppressLint("NonConstantResourceId")
public class PlaySongFragment extends Fragment implements View.OnClickListener {

    private FragmentPlaySongBinding mFragmentPlaySongBinding;
    private Timer mTimer;
    private int mAction;
    private boolean isFavourite = false;
    private List<Playlist> mListPlaylist;
    private List<Song> mListSongFavourite;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://musicbasic-27309-default-rtdb.firebaseio.com");
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0);
            handleMusicAction();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPlaySongBinding = FragmentPlaySongBinding.inflate(inflater, container, false);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }
        if(checkLoginStatus()){
            getPlaylistOfUser();
            getFavouriteSongOfUser();

        }
        initControl();
        showInforSong();
        mAction = MusicService.mAction;
        handleMusicAction();

        return mFragmentPlaySongBinding.getRoot();
    }

    private void initControl() {

        mTimer = new Timer();

        mFragmentPlaySongBinding.imgPrevious.setOnClickListener(this);
        mFragmentPlaySongBinding.imgPlay.setOnClickListener(this);
        mFragmentPlaySongBinding.imgNext.setOnClickListener(this);
        mFragmentPlaySongBinding.imgShuffle.setOnClickListener(this);
        mFragmentPlaySongBinding.imgRepeat.setOnClickListener(this);
        mFragmentPlaySongBinding.imgAddPlaylist.setOnClickListener(this);
        mFragmentPlaySongBinding.imgFavourite.setOnClickListener(this);

        mFragmentPlaySongBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicService.mPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
    }
    public boolean checkLoginStatus() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        Boolean getCheck = sharedPreferences.getBoolean("loginCheck", Boolean.valueOf(String.valueOf(Context.MODE_PRIVATE)));
        return getCheck;
    }
    private void showDialogSignInCheck(){
        AlertDialog Dialog = new AlertDialog.Builder(getContext())
                .setTitle("ĐĂNG NHẬP")
                .setMessage("Để sử dụng tính năng này cần có tài khoản. Bạn có muốn đăng nhập?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GlobalFuntion.startActivity(getActivity(), SignInActivity.class);
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

    private void showInforSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        mFragmentPlaySongBinding.tvSongName.setText(currentSong.getTitle());
        mFragmentPlaySongBinding.tvArtist.setText(currentSong.getArtist());
        GlideUtils.loadUrl(currentSong.getImage(), mFragmentPlaySongBinding.imgSong);
        if (isFavourite) {
            mFragmentPlaySongBinding.imgFavourite.setImageResource(R.drawable.ic_favourite);
        }else{
            mFragmentPlaySongBinding.imgFavourite.setImageResource(R.drawable.ic_favourite_none);
        }


    }

    private void handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return;
        }
        switch (mAction) {
            case Constant.PREVIOUS:
            case Constant.NEXT:
                stopAnimationPlayMusic();
                showInforSong();
                break;

            case Constant.PLAY:
                showInforSong();
                if (MusicService.isPlaying) {
                    startAnimationPlayMusic();
                }
                showSeekBar();
                showStatusButtonPlay();
                break;

            case Constant.PAUSE:
                stopAnimationPlayMusic();
                showSeekBar();
                showStatusButtonPlay();
                break;

            case Constant.RESUME:
                startAnimationPlayMusic();
                showSeekBar();
                showStatusButtonPlay();
                break;
        }
    }

    private void startAnimationPlayMusic() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mFragmentPlaySongBinding.imgSong.animate().rotationBy(360).withEndAction(this).setDuration(15000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };
        mFragmentPlaySongBinding.imgSong.animate().rotationBy(360).withEndAction(runnable).setDuration(15000)
                .setInterpolator(new LinearInterpolator()).start();
    }

    private void stopAnimationPlayMusic() {
        mFragmentPlaySongBinding.imgSong.animate().cancel();
    }

    public void showSeekBar() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    if (MusicService.mPlayer == null) {
                        return;
                    }
                    mFragmentPlaySongBinding.tvTimeCurrent.setText(AppUtil.getTime(MusicService.mPlayer.getCurrentPosition()));
                    mFragmentPlaySongBinding.tvTimeMax.setText(AppUtil.getTime(MusicService.mLengthSong));
                    mFragmentPlaySongBinding.seekbar.setMax(MusicService.mLengthSong);
                    mFragmentPlaySongBinding.seekbar.setProgress(MusicService.mPlayer.getCurrentPosition());
                });
            }
        }, 0, 1000);
    }

    private void showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_pause_black);
        } else {
            mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_play_black);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_previous:
                clickOnPrevButton();
                break;

            case R.id.img_play:
                clickOnPlayButton();
                break;

            case R.id.img_next:
                clickOnNextButton();
                break;
            case R.id.img_shuffle:
                clickOnShuffleButton();
                break;
            case R.id.img_repeat:
                clickOnRepeatButton();
                break;
            case R.id.img_add_playlist:
                clickOnAddSongToPlaylistButton();
                break;
            case R.id.img_favourite:
                clickOnFavouriteButton();
                break;
            default:
                break;
        }
    }

    private void clickOnRepeatButton() {
        switch (MusicService.mRepeatMode){
            case Constant.REPEAT_NONE:
                MusicService.mRepeatMode = Constant.REPEAT;
                mFragmentPlaySongBinding.imgRepeat.setColorFilter(R.color.xam2);
                Toast.makeText(getContext(), "Repeat All!",Toast.LENGTH_SHORT).show();
                break;
            case Constant.REPEAT:
                MusicService.mRepeatMode = Constant.REPEAT_ONE;
                mFragmentPlaySongBinding.imgRepeat.setImageResource(R.drawable.img_repeat_one_black);
                mFragmentPlaySongBinding.imgRepeat.setColorFilter(R.color.xam2);
                Toast.makeText(getContext(), "Repeat One!",Toast.LENGTH_SHORT).show();
                break;
            case Constant.REPEAT_ONE:
                MusicService.mRepeatMode = Constant.REPEAT_NONE;
                mFragmentPlaySongBinding.imgRepeat.setImageResource(R.drawable.img_repeat_black);
                mFragmentPlaySongBinding.imgRepeat.setColorFilter(R.color.gray);
                Toast.makeText(getContext(), "No Repeat!",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void clickOnFavouriteButton(){

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        String getIdCurrent = sharedPreferences.getString("loginId", String.valueOf(Context.MODE_PRIVATE));
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        if (!checkLoginStatus()){
            showDialogSignInCheck();
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        String pathSong = String.valueOf(currentSong.getId());
        DatabaseReference reference;
        if (isFavourite){
            isFavourite = false;
            reference = firebaseDatabase.getReference("user/"+getIdCurrent+"/list_favourite/"+pathSong);
            reference.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Toast.makeText(getContext(),"Đã xóa khỏi playlist yêu thích!",Toast.LENGTH_SHORT).show();
                }
            });
            showInforSong();

        }
        else{
            isFavourite = true;
            reference = firebaseDatabase.getReference("user/"+getIdCurrent+"/list_favourite");
            reference.child(pathSong).setValue(currentSong, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Toast.makeText(getContext(),"Đã thêm vào playlist yêu thích!",Toast.LENGTH_SHORT).show();
                }
            });
            showInforSong();
        }

    }
    private void clickOnAddSongToPlaylistButton(){
        if (!checkLoginStatus()){
            showDialogSignInCheck();
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        final int[] idPlaylistSelected = new int[1];
        idPlaylistSelected[0] = -1;
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_choose_playlist_to_add_song);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        dialog.setCancelable(false);

        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvPlaylistNameChoose = dialog.findViewById(R.id.tv_playlist_name_choose);
        RecyclerView recyclerViewChoose = dialog.findViewById(R.id.rcv_playlist_choose);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_choose);
        Button btnAddPlaylist = dialog.findViewById(R.id.btn_choose_playlist);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerViewChoose.setLayoutManager(gridLayoutManager);
        if (mListPlaylist.size()==0){
            tvTitle.setText("Bạn chưa có playlist nào! Vui lòng tạo thêm playlist!");
            btnCancel.setText("Thoát");
            btnAddPlaylist.setVisibility(View.GONE);
        }
        PlaylistGridAdapter playlistGridAdapter =new PlaylistGridAdapter(mListPlaylist, new IOnClickPlaylistItemListener() {
            @Override
            public void onClickPlaylistItemListener(Playlist playlist) {
                tvPlaylistNameChoose.setText(playlist.getName());
                tvPlaylistNameChoose.setVisibility(View.VISIBLE);
                idPlaylistSelected[0] = playlist.getId();
            }
        });
        recyclerViewChoose.setAdapter(playlistGridAdapter);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
//                String getIdCurrent = sharedPreferences.getString("loginId", String.valueOf(Context.MODE_PRIVATE));
//                int idCurrent = Integer.parseInt(getIdCurrent);
                //String pathIdUserCurrent = String.valueOf(idCurrent);
                if(idPlaylistSelected[0]==-1){
                    Toast.makeText(getContext(),"Bạn chưa chọn playlist!",Toast.LENGTH_SHORT).show();
                    return;
                }

                String pathPlaylist = String.valueOf(idPlaylistSelected[0]);
                String pathSongCurrent = String.valueOf(currentSong.getId());
                DatabaseReference reference = firebaseDatabase.getReference("playlist/"+pathPlaylist+"/list");
                reference.child(pathSongCurrent).setValue(currentSong, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getContext(),"Them thanh cong!",Toast.LENGTH_SHORT).show();
                    }
                });
//
                dialog.dismiss();
            }



        });
        dialog.show();

    }




    private void clickOnShuffleButton() {
        if (MusicService.isShuffle){
            MusicService.isShuffle = false;
            Toast.makeText(getContext(), "Shuffle Mode Off!",Toast.LENGTH_SHORT).show();
            mFragmentPlaySongBinding.imgShuffle.setColorFilter(R.color.xam2);
        }
        else{
            MusicService.isShuffle = true;
            Toast.makeText(getContext(), "Shuffle Mode On!",Toast.LENGTH_SHORT).show();
            mFragmentPlaySongBinding.imgShuffle.setColorFilter(R.color.gray);
        }


    }

    private void clickOnPrevButton() {
        GlobalFuntion.startMusicService(getActivity(), Constant.PREVIOUS, MusicService.mSongPosition);
    }

    private void clickOnNextButton() {
        GlobalFuntion.startMusicService(getActivity(), Constant.NEXT, MusicService.mSongPosition);
    }

    private void clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFuntion.startMusicService(getActivity(), Constant.PAUSE, MusicService.mSongPosition);
        } else {
            GlobalFuntion.startMusicService(getActivity(), Constant.RESUME, MusicService.mSongPosition);
        }
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getFavouriteSongOfUser(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        String getIdCurrent = sharedPreferences.getString("loginId", String.valueOf(Context.MODE_PRIVATE));
        //int idCurrent = Integer.parseInt(getIdCurrent);
        DatabaseReference reference = firebaseDatabase.getReference("user/"+getIdCurrent+"/list_favourite");
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListSongFavourite= new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }

                    //mListSongFavourite.add(song);
                    if (song.getId()==currentSong.getId()){
                        isFavourite = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
