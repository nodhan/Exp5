package com.nodhan.exp5;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

class Mp3Filter implements FilenameFilter {

    static String FILE_EXT;

    @Override
    public boolean accept(File dir, String name) {
        return (name.endsWith(FILE_EXT));
    }
}

public class MainActivity extends AppCompatActivity {
    Button play, stop;
    TextView message;
    ListView listView;
    ArrayList<String> arr;
    MediaPlayer mediaPlayer;
    File[] songs;
    String SD_PATH;
    String FILE_EXT;
    int songListID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arr = new ArrayList<>();
        mediaPlayer = new MediaPlayer();
        SD_PATH = System.getenv("SECONDARY_STORAGE") + "/Music/English/";
        FILE_EXT = ".mp3";
        listView = (ListView) findViewById(R.id.listView);
        message = (TextView) findViewById(R.id.message);
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);
        changeVisibility(false);

        message.setTextColor(Color.BLACK);
        Mp3Filter.FILE_EXT = FILE_EXT;
        songs = new File(SD_PATH).listFiles(new Mp3Filter());

        if (songs.length > 0) {
            for (File song : songs) {
                arr.add(song.getName().substring(0, song.getName().length() - 4));
            }
            ArrayAdapter<String> adapt = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arr) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                    return view;
                }
            };
            listView.setAdapter(adapt);

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                startPlaying(position);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        play.setText(R.string.pause);
                    } else {
                        mediaPlayer.pause();
                        play.setText(R.string.play);
                    }
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setText(R.string.play);
                mediaPlayer.stop();
                changeVisibility(false);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (songListID < songs.length - 1) {
                    startPlaying(++songListID);
                } else {
                    startPlaying(0);
                }
            }
        });
    }

    /***
     * Plays the selected song
     *
     * @param position - id of the song in list
     */
    private void startPlaying(int position) {
        play.setText(R.string.pause);
        mediaPlayer.reset();
        String name = arr.get(position);
        try {
            mediaPlayer.setDataSource(SD_PATH + name + FILE_EXT);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        message.setText(new StringBuilder("Now Playing: \n").append(name));
        songListID = position;
        changeVisibility(true);
        mediaPlayer.start();
    }

    /***
     * Helper function to change visibility of buttons
     *
     * @param b if true <b>visible</b> else <b>invisible</b>
     */
    private void changeVisibility(boolean b) {
        int view = b ? View.VISIBLE : View.INVISIBLE;
        play.setVisibility(view);
        stop.setVisibility(view);
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.pause();
        play.setText(R.string.play);
        super.onBackPressed();
    }
}
