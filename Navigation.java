package com.example.voiceguideassistance.Navigation;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voiceguideassistance.R;

import java.util.ArrayList;
import java.util.Locale;

public class Navigation extends AppCompatActivity {

    private TextToSpeech tts;
    private int numberOfClicks;
    private boolean IsInitialVoiceFinshed;
    EditText source, destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        source=findViewById(R.id.source);
        destination=findViewById(R.id.destination);



        IsInitialVoiceFinshed = false ;

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }

                    speak("Welcome to Blind Navigation");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            speak("Tell me your  address? or cancel to close the application ");
                            IsInitialVoiceFinshed = true;
                        }
                    }, 4000);

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

        numberOfClicks = 0;
    }

    private void speak(String welcome_to_voice_mail) {

        tts.speak(welcome_to_voice_mail, TextToSpeech.QUEUE_FLUSH, null, null);

    }
    @Override
    public void onDestroy() {
        if (tts != null) {

            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    public void layoutClicked(View view) {
        if(IsInitialVoiceFinshed) {
            numberOfClicks++;
            listen();
        }


    }

    private void listen() {

        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(Navigation.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }

    }

    private void exitFromApp()
    {
        this.finishAffinity();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && IsInitialVoiceFinshed) {
            IsInitialVoiceFinshed = false;
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result.get(0).equals("cancel")) {
                    speak("Cancelled!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exitFromApp();
                        }
                    }, 4000);

                } else {

                    switch (numberOfClicks) {
                        case 1:
                            String from;
                            from = result.get(0).replaceAll("underscore", "_");
                            from = from.replaceAll("\\s+", "");
                            source.setText(from);
                           // Config.EMAIL = from;
                            //status.setText("Password?");
                            speak("Source?");
                            break;
                        case 2:
                            String pwd;
                            pwd = result.get(0).replaceAll("\\s+","");
                            //Config.PASSWORD = pwd;
                            destination.setText(pwd);
                           // status.setText("Confirm?");
                            speak("Please Confirm the address\n User : " + source.getText().toString() + "your Destination" +destination.getText().toString() + "\nSpeak Yes to confirm");
                            break;
                        default:
                            if(result.get(0).equals("yes")||result.get(0).equals("s")||result.get(0).equalsIgnoreCase("OK"))
                            {
                                /*User user;
                                String username = From.getText().toString();
                                String password = Password.getText().toString();
                                Config.EMAIL = username;
                                Config.PASSWORD = password;*/

                               /* user = new User(username, password);
                                userLocalStore.storeUserData(user);
                                userLocalStore.setUserLoggedIn(true);*/

                                Toast.makeText(this, "I am Here", Toast.LENGTH_SHORT).show();

                               Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.in/maps/dir/"+source.getText().toString().trim()+"/"+destination.getText().toString().trim()));
                               intent.setPackage("com.google.android.apps.maps");
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                /*if (intent.resolveActivity(getPackageManager()) != null) {


                                }*/
                               /* Intent HomeIntent = new Intent(Navigation.this, MainActivity.class);
                                startActivity(HomeIntent);
                                finish();*/
                            }else
                            {
                               // userLocalStore.clearUserData();
                               // status.setText("Mail?");
                                speak("Please provide your Address?");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        numberOfClicks = 0;
                                    }
                                }, 2000);
                            }
                            break;


                    }
                }
            }
            IsInitialVoiceFinshed = true;
        }
    }

}