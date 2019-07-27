package com.example.alterego;

import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, TextToSpeech.OnInitListener {
    // 音声入力用
    SpeechRecognizer sr;

    // 音声合成用
    TextToSpeech tts = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button button1 = (Button) findViewById(R.id.button_start);
        //button1.setOnClickListener( this );

        tts = new TextToSpeech(this, this);

    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // something to do
                break;
            case MotionEvent.ACTION_UP:
                // 音声認識APIに自作リスナをセット
                sr = SpeechRecognizer.createSpeechRecognizer(this);
                sr.setRecognitionListener(new MyRecognitionListener());

                // インテントを作成
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());

                // 入力言語のロケールを設定
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString());
                //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString());
                //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINA.toString());

                // 音声認識APIにインテントを処理させる
                sr.startListening(intent);
                break;
            case MotionEvent.ACTION_MOVE:
                // something to do
                break;
            case MotionEvent.ACTION_CANCEL:
                // something to do
                break;
        }
        return false;
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            // 音声合成の設定を行う

            float pitch = 1.0f; // 音の高低
            float rate = 1.0f; // 話すスピード
            Locale locale = Locale.JAPAN; // 対象言語のロケール
            // ※ロケールの一覧表
            //   http://docs.oracle.com/javase/jp/1.5.0/api/java/util/Locale.html

            tts.setPitch(pitch);
            tts.setSpeechRate(rate);
            tts.setLanguage(locale);


            TextView textView = findViewById(R.id.text_view);
            textView.setText("こんにちは！");

            // 音声合成して発音
            if(tts.isSpeaking()) {
                tts.stop();
            }
            tts.speak("こんにちは！", TextToSpeech.QUEUE_FLUSH, null);
            ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_happy);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if( tts != null ) {
            // 破棄
            tts.shutdown();
        }
    }

    // 音声認識のリスナ
    class MyRecognitionListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            if(error == 9) {
                Toast.makeText(getApplicationContext(), "設定からマイクの権限を与えて下さい！", Toast.LENGTH_LONG).show();
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_sad);
            } else if(error == 7) {
                Toast.makeText(getApplicationContext(), "うまく聞き取れなかったよ！", Toast.LENGTH_LONG).show();
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_sad);
            } else {
                Toast.makeText(getApplicationContext(), "エラー： " + error, Toast.LENGTH_LONG).show();
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_sad);
            }
            // エラーコードの一覧表
            // http://developer.android.com/intl/ja/reference/android/speech/SpeechRecognizer.html#ERROR_AUDIO
            // 認識結果の候補が存在しなかった場合や，RECORD_AUDIOのパーミッションが不足している場合など
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(), "話して", Toast.LENGTH_LONG).show();
            ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_normal);
        }

        @Override
        public void onResults(Bundle results) {
            // 結果を受け取る
            ArrayList<String> candidates = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String s = candidates.get(0);

            if (s.matches(".*何でも.*")) {
                s = "ん？今何でもするって言ったよね？";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_angry);
            }
            if (s.matches(".*おはよう.*")) {
                s = "おはようございます．\n今日も一日頑張りましょう！";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_happy);
            }
            if (s.matches("Good morning") || s.matches("グッドモーニング") || s.matches("モーニング")) {
                tts.setLanguage(Locale.US);
                s = "Let's do our best today !";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_happy);
            }
            if (s.matches(".*可愛い.*") || s.matches(".*かわいい.*")) {
                s = "ありがとうございます！";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_sad);
            }
            if (s.matches(".*長谷川.*")) {
                s = "ハセガワ タツヒト\n情報・メディア工学講座 講師\nデータマイニングやモバイル・ユビキタス，教育支援システムに関する研究を行っています．";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_happy);
            }
            if (s.matches(".*福井大学.*")) {
                s = "福井大学\n1949年に設立された国立大学の一つです．\n私の生みの親が在籍しています．";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_angry);
            }
            if (s.matches(".*君.*") || s.matches(".*あなた.*") || s.matches(".*名前.*")) {
                s = "私はアルトと言います．\nあなたのお手伝いをさせていただきます．\nよろしくお願いいたします！";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_happy);
            }
            if (s.matches(".*疲れた.*")) {
                s = "ここが踏ん張りどころです．\n頑張りましょう！";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_angry);
            }
            if (s.matches(".*近大高専.*")) {
                s = "三重県名張市にある私立高専の一つです．私の生みの親がここを卒業しました．";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_normal);
            }
            if (s.matches(".*フシギダネ.*")) {
                s = "フシギダネ\nたねポケモン\n高さ 0.7メートル．重さ 6.9キログラム\n生まれた時から背中に植物の種があって少しずつ大きく育つ．";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.fushigidane);
            }
            if (s.matches(".*ソース.*") || s.matches(".*コード.*")) {
                s = "私のソースコードにアクセスします．";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_normal);
                source();
            }
            if (s.matches(".*YouTube.*") || s.matches(".*ようつべ.*")) {
                s = "YouTubeにアクセスします．";
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_normal);
                youtube();
            }
            if (s.matches(".*Google.*")) {
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_normal);
                google(s);
                s="Googleにアクセスします．";
            }
            if (s.matches(".*Wiki.*") || s.matches(".*Wikipedia.*")) {
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_normal);
                wikipedia(s);
                s="Wikipediaにアクセスします．";
            }
            if (s.matches(".*Twitter.*") || s.matches(".*呟く.*")) {
                ((ImageView) findViewById(R.id.imageView_Alt)).setImageResource(R.drawable.girl_normal);
                twitter();
                s="Twitterにアクセスします．";
            }

            //結果を表示
            //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            TextView textView = findViewById(R.id.text_view);
            textView.setText(s);

            if (s != "Let's do our best today !") {
                tts.setLanguage(Locale.JAPAN);
            }
            // 音声合成して発音
            if(tts.isSpeaking()) {
                tts.stop();
            }
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        public void youtube() {
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));
            ResolveInfo defaultResInfo = getPackageManager().resolveActivity(browser, PackageManager.MATCH_DEFAULT_ONLY);
            if (defaultResInfo != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));
                intent.setPackage(defaultResInfo.activityInfo.packageName);
                try {
                    startActivity(intent);
                    return;
                } catch (ActivityNotFoundException e) {
                    // resolveActivity はデフフォルトアプリが設定されていない場合、
                    // activity resolver などが含まれる ResolveInfo を返しうる。
                }
            }
        }
        public void source() {
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/75u2u"));
            ResolveInfo defaultResInfo = getPackageManager().resolveActivity(browser, PackageManager.MATCH_DEFAULT_ONLY);
            if (defaultResInfo != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/75u2u"));
                intent.setPackage(defaultResInfo.activityInfo.packageName);
                try {
                    startActivity(intent);
                    return;
                } catch (ActivityNotFoundException e) {
                    // resolveActivity はデフフォルトアプリが設定されていない場合、
                    // activity resolver などが含まれる ResolveInfo を返しうる。
                }
            }
        }
        public void google(String s) {
            String word = s.replace("Google", "");
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + word));
            ResolveInfo defaultResInfo = getPackageManager().resolveActivity(browser, PackageManager.MATCH_DEFAULT_ONLY);
            if (defaultResInfo != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + word));
                intent.setPackage(defaultResInfo.activityInfo.packageName);
                try {
                    startActivity(intent);
                    return;
                } catch (ActivityNotFoundException e) {
                    // resolveActivity はデフフォルトアプリが設定されていない場合、
                    // activity resolver などが含まれる ResolveInfo を返しうる。
                }
            }
        }
        public void wikipedia(String s) {
            String word = s.replace("Wikipedia", "");
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ja.wikipedia.org/wiki/" + word));
            ResolveInfo defaultResInfo = getPackageManager().resolveActivity(browser, PackageManager.MATCH_DEFAULT_ONLY);
            if (defaultResInfo != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ja.wikipedia.org/wiki/" + word));
                intent.setPackage(defaultResInfo.activityInfo.packageName);
                try {
                    startActivity(intent);
                    return;
                } catch (ActivityNotFoundException e) {
                    // resolveActivity はデフフォルトアプリが設定されていない場合、
                    // activity resolver などが含まれる ResolveInfo を返しうる。
                }
            }
        }
        public void twitter() {
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/crane_memory"));
            ResolveInfo defaultResInfo = getPackageManager().resolveActivity(browser, PackageManager.MATCH_DEFAULT_ONLY);
            if (defaultResInfo != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/crane_memory"));
                intent.setPackage(defaultResInfo.activityInfo.packageName);
                try {
                    startActivity(intent);
                    return;
                } catch (ActivityNotFoundException e) {
                    // resolveActivity はデフフォルトアプリが設定されていない場合、
                    // activity resolver などが含まれる ResolveInfo を返しうる。
                }
            }
        }

    }
}