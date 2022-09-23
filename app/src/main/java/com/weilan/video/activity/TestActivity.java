package com.weilan.video.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.weilan.video.R;
import com.yunbao.live.bean.DiceType;
import com.yunbao.live.game.GameDiceCore;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher_test);

        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGameBox();
            }
        });
    }

    /*
     * 骰子游戏
     * */
    boolean clickGameBtn;
    public void openGameBox(){
//        if(!clickGameBtn){
//            clickGameBtn = true;
//            GameDiceCore gameDiceCore = GameDiceCore.getCore(this,"0","23490");
//            gameDiceCore.getbatch();
//            gameDiceCore.openGameBox(getSupportFragmentManager());
//            gameDiceCore.setGameDiceCoreLister(new GameDiceCore.GameDiceCoreLister() {
//                @Override
//                public void onDialogFragmentHide() {
//                    clickGameBtn = false;
//                }
//            });
//        }
    }
}
