package com.hyeji.stockstrading;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String htmlUrl = "https://finance.naver.com/item/main.naver?code=006800";  // 미래에셋

    private Bundle bundle;
    private Document document = null;

    TextView textView, textView1, textView2, textView3, textView4, textView5, textView6, textView7;
    Button button;
    Switch aSwitch;
    ImageView imageViewStrategy1;

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        textView7 = findViewById(R.id.textView7);
        aSwitch = findViewById(R.id.switch1);
        button = findViewById(R.id.button);
        imageViewStrategy1 = findViewById(R.id.imageViewStrategy1);
        bundle = new Bundle();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                textView7.setText("수신 횟수 : " + i);
                new Thread() {
                    public void run() {
                        try {
                            document = Jsoup.connect(htmlUrl).get();

                            Elements elem = document.select(".date");
                            String[] str = elem.text().split(" ");

                            Elements todaylist = document.select(".new_totalinfo dl>dd");

                            String currentValue = todaylist.get(3).text().split(" ")[1];
                            String startValue = todaylist.get(5).text().split(" ")[1];
                            String highValue = todaylist.get(6).text().split(" ")[1];
                            String lowValue = todaylist.get(8).text().split(" ")[1];
                            String volume = todaylist.get(10).text().split(" ")[1];

                            String stype = todaylist.get(3).text().split(" ")[3];
                            String vsyesterday = todaylist.get(3).text().split(" ")[4];
                            String currentTime = str[0] + " " + str[1];

                            System.out.println("--------- 주가 정보 ------------------");
                            System.out.println("주가:" + currentValue);
                            System.out.println("시가:" + startValue);
                            System.out.println("고가:" + highValue);
                            System.out.println("저가:" + lowValue);
                            System.out.println("거래량:" + volume);
                            System.out.println("타입:" + stype);
                            System.out.println("전일대비:" + vsyesterday);
                            System.out.println("현재 시간:" + str[0] + str[1]);
                            System.out.println("수신 횟수:" + i);

                            bundle.putString("CUR_VAL", currentValue);
                            bundle.putString("START_VAL", startValue);
                            bundle.putString("HIGH_VAL", highValue);
                            bundle.putString("LOW_VAL", lowValue);
                            bundle.putString("VOLUME", volume);
                            bundle.putString("CUR_TIME", currentTime);
                            bundle.putString("UP_DOWN", vsyesterday + "(" + stype + ")");
                            bundle.putString("COUNT", String.valueOf(i));

                            //bundle.putStringArray();
                            Message msg = handler.obtainMessage();
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                textView7.setText("0 회");  // reset count

                if(isChecked) {
                    System.out.println("--------- 자동 모드 ------------------");
                    button.setVisibility(button.INVISIBLE);
                    ScrapingThread scrapingThread = new ScrapingThread(handler);
                    scrapingThread.start();
                } else {
                    System.out.println("--------- 수동 모드 ------------------");
                    button.setVisibility(button.VISIBLE);
                    document = null;
                }
            }
        });



        imageViewStrategy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "My strategy to show!", Toast.LENGTH_LONG).show();
            }
        });
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            bundle = message.getData();
            textView.setText("현재 가격 : " + bundle.getString("CUR_VAL"));
            textView1.setText("시작 가격 : " + bundle.getString("START_VAL"));
            textView2.setText("최고 가격 : " + bundle.getString("HIGH_VAL"));
            textView3.setText("최저 가격 : " + bundle.getString("LOW_VAL"));
            textView4.setText("거래량 : " + bundle.getString("VOLUME"));
            textView5.setText("제공 시간 : " + bundle.getString("CUR_TIME"));
            textView6.setText("전일 대비 : " + bundle.getString("UP_DOWN"));
            textView7.setText("수신 횟수 : " + bundle.getString("COUNT"));
        }
    };
}