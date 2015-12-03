package com.itisdancing.Sokoban;

    /* ---- this class is the homepage, nothing special, but I added two buttons "Settings" and "About"  for my own test and later---- */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.List;

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
      //Intent game = new Intent(this, SokobanGame.class);
      Intent game = new Intent();
      //Intent scores = new Intent(this, SokobanScores.class);
      switch(v.getId()) {
        case R.id.home_new_button:
          game.putExtra(SokobanGame.KEY_LEVEL, 0);
          game.setClass(this, SokobanGame.class);
          startActivity(game);
          break;
        case R.id.home_continue_button:
          game.putExtra(SokobanGame.KEY_LEVEL, -1);
          game.setClass(this, SokobanGame.class);
          startActivity(game);
          break;
        case R.id.home_level_select_button:
          /*game.putExtra(SokobanGame.KEY_LEVEL, 1);
          game.setClass(this, SokobanGame.class);
          startActivity(game);*/
            SharedPreferences prefs = getSharedPreferences(SokobanGame.SHARE_PREF_NAME, MODE_PRIVATE);
            final int maxLevel = prefs.getInt(SokobanGame.PASSED_LEVEL,0);
            String t = "" + maxLevel;
            Log.d("value of maxLevel: ", t);
            List<String> levelList = new ArrayList<String>(maxLevel);
            for (int i = maxLevel+1; i > 0; i--) {
                levelList.add("Level " + i);
            }
            final String[] items = levelList.toArray(new String[maxLevel]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose level");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    Intent game = new Intent();
                    int levelClicked = maxLevel - item ;
                    game.putExtra(SokobanGame.KEY_LEVEL, levelClicked);
                    game.setClass(SokobanApp.this, SokobanGame.class);
                    startActivity(game);
                }
            });
            builder.create().show();
          break;
        case R.id.home_high_score_button:
          Intent scores = new Intent();
          scores.setClass(this, SokobanScores.class);
          startActivity(scores); break;
        case R.id.settings_button:
          //write PreferenceActivity codes
            Intent pref = new Intent(this, PreferenceSetting.class);
            //scores.setClass(this, SokobanScores.class);
            startActivity(pref); break;
        case R.id.about_button:
            new AlertDialog.Builder(this).setTitle(R.string.about)
                    .setMessage("Member: \n Lam Hon Ling (11083586), \n Lee Shing Hin (11115970), \n Tsang Chi Wai (10987971), \n Wong Hon To (11063077)")
                    .setNeutralButton("OK", null).show();
      }
    }
}
