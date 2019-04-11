package club.cppcss.catordog3;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button startGameButton;
    private TextView scoreTextView;
    private TextView highScoreTextView;
    private ImageView animalImageView;
    private Button catButton;
    private Button dogButton;

    private int score = 0;
    private int highScore = 0;

    private String currentAnimal = "";
    private String animalChosen = "";

    private Drawable[] catDrawables;
    private Drawable[] dogDrawables;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startGameButton = findViewById(R.id.startGameButton);
        scoreTextView = findViewById(R.id.scoreTextView);
        highScoreTextView = findViewById(R.id.highScoreTextView);
        animalImageView = findViewById(R.id.animalImageView);
        dogButton = findViewById(R.id.dogButton);
        catButton = findViewById(R.id.catButton);

        catButton.setVisibility(View.INVISIBLE);
        dogButton.setVisibility(View.INVISIBLE);

        catDrawables = getDrawablesFromAssetFolder("cats");
        dogDrawables = getDrawablesFromAssetFolder("dogs");

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        highScore = pref.getInt("highScore", 0);
        highScoreTextView.setText("High Score: " + highScore);
    }

    private Drawable[] getDrawablesFromAssetFolder(String folderName) {
        try {
            AssetManager assetManager = getAssets();
            String[] images = assetManager.list(folderName);
            Drawable[] drawables = new Drawable[images.length];

            InputStream inputStream;

            for (int i = 0; i < images.length; i++) {
                inputStream = getAssets().open(folderName + "/" + images[i]);
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                drawables[i] = drawable;
            }

            return drawables;
        } catch (IOException e) {
            System.out.println("ERROR: " + e);
            return null;
        }
    }

    public void startGame(View v) {
        score = 0;
        scoreTextView.setText("Score: 0");
        catButton.setVisibility(View.VISIBLE);
        dogButton.setVisibility(View.VISIBLE);
        startGameButton.setVisibility(View.INVISIBLE);

        countdown();
    }

    private void setNewAnimal() {
        Random random = new Random();
        if(random.nextFloat() > 0.5) {
            // set dog
            animalImageView.setImageDrawable(dogDrawables[random.nextInt(dogDrawables.length)]);
            currentAnimal = "dog";
        } else {
            // set cat
            animalImageView.setImageDrawable(catDrawables[random.nextInt(catDrawables.length)]);
            currentAnimal = "cat";
        }
    }

    private void countdown() {
        animalChosen = "";
        setNewAnimal();
        new CountDownTimer(3001, 1000) {

            public void onTick(long millisUntilFinished) {
               if(animalChosen.equals(currentAnimal)) {
                   score++;
                   scoreTextView.setText("Score: " + score);
                   this.cancel();
                   countdown();
               } else if(!animalChosen.equals("")) {
                   gameOver();
                   this.cancel();
               }
            }

            public void onFinish() {
                gameOver();
            }
        }.start();
    }

    private void gameOver() {
        if(score > highScore) {
            highScore = score;
            highScoreTextView.setText("High Score: " + highScore);
            editor.putInt("highScore", score); // Storing integer
            editor.commit();
        }
        catButton.setVisibility(View.INVISIBLE);
        dogButton.setVisibility(View.INVISIBLE);
        startGameButton.setVisibility(View.VISIBLE);
        animalImageView.setImageResource(R.drawable.gameover);
    }

    public void dogButtonClicked(View v) {
        animalChosen = "dog";
    }

    public void catButtonClicked(View v) {
        animalChosen = "cat";
    }
}
