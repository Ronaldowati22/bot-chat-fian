package com.bot.botapakah;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.json.*;

import java.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@LineMessageHandler
public class BotApakahApplication extends SpringBootServletInitializer {

    String pesan_dikirim;

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BotApakahApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BotApakahApplication.class, args);
    }

    @EventMapping
    public void handleTextEvent(MessageEvent<TextMessageContent> messageEvent){
        String pesan = messageEvent.getMessage().getText().toLowerCase();
        String[] pesanSplit = pesan.split(" ");
        int panjang = pesanSplit.length;

        compare(pesanSplit);

        if(pesanSplit[0].equals("apakah")){
            String jawaban = getRandomJawaban();
            String replyToken = messageEvent.getReplyToken();
            balasChatDenganRandomJawaban(replyToken, jawaban + panjang);
        }else{
            String replyToken = messageEvent.getReplyToken();
            balasChatDenganRandomJawaban(replyToken, pesan_dikirim);
        }
    }

    private String getRandomJawaban(){
        String jawaban = "";
        int random = new Random().nextInt();
        if(random%2==0){
            jawaban = "Ya";
        } else{
            jawaban = "Nggak";
        }
        return jawaban;
    }

    private void compare(String[] Message){
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("./data.csv"))) {
            String line;
            if((line = br.readLine()) == null){
                
            }else{
                while ((line = br.readLine()) != null) {
                    // String[] values = line.split(";");
                    records.add(line.split(";"));
                }
                String[][] array = new String[records.size()][0];
                records.toArray(array);

                for(int i=0;i<array.length;i++){
                    int batas_minimal=0;
                    String[] keyword = array[i][0].split(" ");
                    for(int j=0;j<keyword.length;j++){
                        for(int k=0;k<Message.length;k++){
                            if(keyword[j].equals(Message[k])){
                                batas_minimal=batas_minimal+1;
                            }
                        }
                    }
                    if(batas_minimal>=Integer.parseInt(array[i][1])){
                        pesan(array[i][2]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pesan(String pesan){
        this.pesan_dikirim=pesan;
    }

    private void balasChatDenganRandomJawaban(String replyToken, String jawaban){
        TextMessage jawabanDalamBentukTextMessage = new TextMessage(jawaban);
        try {
            lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, jawabanDalamBentukTextMessage))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Ada error saat ingin membalas chat");
        }
    }

}
