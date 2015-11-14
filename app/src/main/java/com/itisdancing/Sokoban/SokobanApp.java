package com.itisdancing.Sokoban;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class SokobanApp extends Activity implements OnClickListener
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.home_new_button).setOnClickListener(this);
        findViewById(R.id.home_continue_button).setOnClickListener(this);
        findViewById(R.id.home_level_select_button).setOnClickListener(this);
        findViewById(R.id.home_high_score_button).setOnClickListener(this);
        findViewById(R.id.settings_button).setOnClickListener(this);
        findViewById(R.id.about_button).setOnClickListener(this);
    }

    public void onClick(View v) {
      Intent game = new Intent(this, SokobanGame.class);
      Intent scores = new Intent(this, SokobanScores.class);
      switch(v.getId()) {
        case R.id.home_new_button:
          game.putExtra(SokobanGame.KEY_LEVEL, 0);
          startActivity(game);
          break;
        case R.id.home_continue_button:
          game.putExtra(SokobanGame.KEY_LEVEL, -1);
          startActivity(game);
          break;
        case R.id.home_level_select_button:
          game.putExtra(SokobanGame.KEY_LEVEL, 1);
          startActivity(game);
          break;
        case R.id.home_high_score_button:
          startActivity(scores);
        case R.id.settings_button:
          //write PreferenceActivity codes
        case R.id.about_us_button:
            new AlertDialog.Builder(this).setTitle(R.string.about_us).setMessage("Member:xxx").setNeutralButton("OK", null).show();
      }
    }
}
