package com.hyeji.stockstrading;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ScrapingThread extends Thread {
    private String htmlUrl = "https://finance.naver.com/item/main.naver?code=006800";  // 미래에셋
    private boolean running=true;
    private Document document;
    private Bundle bundle;
    private Handler handler;
    private Message message;
    private int loopCount;


    public ScrapingThread(Handler handler) {
        this.handler = handler;
        this.loopCount = 0;
        this.bundle = new Bundle();
    }


    @Override
    public void run() {
        super.run();

        while (running) {
            try {
                document = Jsoup.connect(htmlUrl).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loopCount++;
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
            System.out.println("전일 대비:" + vsyesterday);
            System.out.println("현재 시간:" + str[0] + str[1]);
            System.out.println("수신 횟수:" + loopCount);

            bundle.putString("CUR_VAL", currentValue);
            bundle.putString("START_VAL", startValue);
            bundle.putString("HIGH_VAL", highValue);
            bundle.putString("LOW_VAL", lowValue);
            bundle.putString("VOLUME", volume);
            bundle.putString("CUR_TIME", currentTime);
            bundle.putString("UP_DOWN", vsyesterday + "(" + stype + ")");
            bundle.putString("COUNT", String.valueOf(loopCount));


            message = handler.obtainMessage();
            message.setData(bundle);
            handler.sendMessage(message);
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if ( loopCount >= 50) {
                running = false;
            }
        }
    }
}
