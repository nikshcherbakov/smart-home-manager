package com.hfad.smarthomemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.view.View;

import com.dd.CircularProgressButton;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import at.grabner.circleprogress.UnitPosition;

public class MainActivity extends Activity {

    private CircleProgressView circleViewHumidity;
    private CircleProgressView circleViewTemperature;
    private Boolean showUnit = true;
    private CircularProgressButton speakButton;
    private String dataFromArduino = "";
    private boolean currentDeviceState = false;
    boolean wasSuccessful = false;
    boolean isRequestForState = false;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    class URLRequest extends AsyncTask<String, Integer, Void> {
        URL myUrl;
        URLConnection myUrlCon;

        @Override
        protected Void doInBackground(String... url) {
            try {
                myUrl = new URL(url[0]);
                myUrlCon = myUrl.openConnection();
                int c;

                // Получить длину содержимого
                InputStream input = myUrlCon.getInputStream();
                long length = myUrlCon.getContentLengthLong();
                if (length == -1)
                    System.out.println("Длина содержимого недоступна");
                else
                    System.out.println("Длина содержимого: " + length);

                if (length != 0) {
                    System.out.println("=== Содержимое ===");



                    while (((c = input.read()) != -1)) {
                        System.out.print((char) c);
                        dataFromArduino = dataFromArduino.concat(Character.toString((char) c));
                    }
                    // Выполняем парсинг строки dataFromArduino
                    int sIndex = dataFromArduino.indexOf("s=") + 2;
                    int indOfLnAfterS = dataFromArduino.indexOf('\n', sIndex);
                    int state = Integer.valueOf(dataFromArduino.substring(sIndex, // Состояние устройства
                            indOfLnAfterS));
                    if (state == 1) {
                        currentDeviceState = true;
                    } else {
                        currentDeviceState = false;
                    }

                    int tIndex = dataFromArduino.indexOf("t=") + 2; // С этого индекса будем начинать копирование
                    int indOfLnAfterT = dataFromArduino.indexOf('\n', tIndex); // Запоминаем, где был первый конец строки '\n'
                    int temperature = Integer.valueOf(dataFromArduino.substring(tIndex, // Температура
                            indOfLnAfterT));

                    int hIndex = dataFromArduino.indexOf("h=") + 2;
                    int indOfLnAfterH = dataFromArduino.indexOf('\n', hIndex);

                    int humidity = Integer.valueOf(dataFromArduino.substring(hIndex, // Влажность
                            indOfLnAfterH));
                    System.out.print("temperatureA=");
                   // System.out.println(temperature);
                    System.out.print("humidityA=");
                    System.out.println(humidity);
                    input.close();
                    wasSuccessful = true;

                    if (isRequestForState) { // Изменяем состояния представлений в соответствии с полученными данными
                        circleViewHumidity.setValueAnimated((float) humidity, 1500);
                        circleViewTemperature.setValueAnimated((float) temperature, 1500);
                    }
                } else {
                    System.out.println("Содержимое недоступно.");
                    wasSuccessful = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dataFromArduino = "";
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isRequestForState) { // Если дананя итерация не требует узнать состояние устройста, то выполняем код ниже
                speakButton.setIndeterminateProgressMode(false);
                if (wasSuccessful) {
                    speakButton.setProgress(100); // Complete
                } else {
                    speakButton.setProgress(-1); // Error
                }
                // Ждем 2 секунды (для анимации)
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        speakButton.setProgress(0);
                        wasSuccessful = false; // Обнуляем флаг
                    }
                }, 2000);
            }
            isRequestForState = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speakButton = (CircularProgressButton) findViewById(R.id.commandButton);
        speakButton.setOnClickListener(onClickListener);

        circleViewHumidity = findViewById(R.id.circleViewHumidity);
        circleViewHumidity.setSeekModeEnabled(false);
        circleViewHumidity.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                circleViewHumidity.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                circleViewHumidity.setUnitVisible(showUnit);
                                break;
                            case SPINNING:
                                circleViewHumidity.setTextMode(TextMode.TEXT); // show text while spinning
                                circleViewHumidity.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );
        circleViewTemperature = (CircleProgressView) findViewById(R.id.circleViewTemperature);
        circleViewTemperature.setSeekModeEnabled(false);
        circleViewTemperature.setOnAnimationStateChangedListener(new AnimationStateChangedListener() {
            @Override
            public void onAnimationStateChanged(AnimationState _animationState) {
                switch (_animationState) {
                    case IDLE:
                    case ANIMATING:
                    case START_ANIMATING_AFTER_SPINNING:
                        circleViewTemperature.setTextMode(TextMode.PERCENT); // show degrees centigrade if not spinning
                        circleViewTemperature.setUnitVisible(showUnit);
                        break;
                    case SPINNING:
                        circleViewTemperature.setTextMode(TextMode.TEXT); // show text while spinning
                        circleViewTemperature.setUnitVisible(false);
                    case END_SPINNING:
                        break;
                    case END_SPINNING_START_ANIMATING:
                        break;

                }
            }
        });
        isRequestForState = true;
        URLRequestSyncronized("http://192.168.0.234/gpio/2"); // TODO
     //   URLRequest URLRequestForState = new URLRequest();
     //   URLRequestForState.execute("http://192.168.1.69/gpio/2");
        System.out.print("currentDeviceState=");
        System.out.println(currentDeviceState);

        // Для обновления предствалений рекурсивно выполняем Handler в фоновом потоке с шагом дискритизации 10 сек.
        final Handler stateChecker = new Handler();
        stateChecker.postDelayed(new Runnable() {
            @Override
            public void run() {
                isRequestForState = true;
                URLRequestSyncronized("http://192.168.0.234/gpio/2"); // TODO
                stateChecker.postDelayed(this, 10000);
            }
        }, 10000);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            speakButton.setIndeterminateProgressMode(true);
            if (speakButton.getProgress() == 0) {
                speakButton.setProgress(50);
            }
            startSpeak();
        }
    };

    public void URLRequestSyncronized(String url) {
        synchronized (this) {
            URLRequest urlRequest = new URLRequest();
            urlRequest.execute(url);
        }
    }

    public void startSpeak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // намерение для вызова формы обработки речи (ОР)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); // сюда он слушает и запоминает
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What should I do?");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE); // вызываем активность ОР
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList commandList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            // для лучшего распознавания английского языка, поставьте в настройках англ. яз как язык системы
            // хотя все то же самое можно проделать и с русскими словами
            if (commandList.contains("Включи светодиод") && !currentDeviceState) {
                URLRequestSyncronized("http://192.168.0.234/gpio/1"); // TODO
             //   URLRequest httpTask = new URLRequest();
             //   httpTask.execute("http://192.168.1.69/gpio/1");
                currentDeviceState = true;
                return;
            }

            if (commandList.contains("Выключи светодиод") && currentDeviceState){
                URLRequestSyncronized("http://192.168.0.234/gpio/0"); // TODO
               // URLRequest httpTask = new URLRequest();
               // httpTask.execute("http://192.168.1.69/gpio/0");
                currentDeviceState = false;
                return;
            }

        }

        // Выводим ошибку на кнопке
        speakButton.setProgress(-1);
        // Затем возвращаем кнопку в обычное состояние
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                speakButton.setProgress(0);
            }
        }, 2000);
        super.onActivityResult(requestCode, resultCode, data);
    }
}