package com.bot.botapakah;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse.MessageContentResponseBuilder;
import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.w3c.dom.Document;
import org.json.*;

import java.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.*;



@SpringBootApplication
@LineMessageHandler
public class BotApakahApplication extends SpringBootServletInitializer {

    String pesan_dikirim="";
    String pesan_dua="";
    String gambar="";
    String pesan2="";
    String id="";
    String ya="";
    String pesan_tutup="\n\nJika tidak ada hal lain yang ingin ditanyakan silahkan balas atau ketik 'Terima Kasih' tanpa petik.";

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BotApakahApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BotApakahApplication.class, args);
    }

    // public static String charRemoveAt(String str, int p) {  
    //     return str.substring(0, p) + str.substring(p + 1);  
    //  }

    @EventMapping
    //Handling penerima text dari user
    public void handleTextEvent(MessageEvent<TextMessageContent> messageEvent){
        String pesan = messageEvent.getMessage().getText().toLowerCase();//Merubah text yang dikirim user menjadi lowercase
        id = messageEvent.getSource().getUserId();//Mengambil id user pengirim
        String replyToken = messageEvent.getReplyToken();//Mengambil token

        Bot bot = new Bot("simplebot","libs/");
        Chat chatSession = new Chat(bot);
        
        String response = chatSession.multisentenceRespond(pesan);
        String responsenospace = response.replaceAll("\\s+\\s+\\s+\\s+\\s+\\s+\\s+\\s+\\s+", "\n\n");
        String[] arrOfStr = responsenospace.split("|", 2);
        System.out.println("Isi Split 1 = "+arrOfStr[0]);
        System.out.println("Isi Split 2 = "+arrOfStr[1]);

        balasChat(replyToken, responsenospace);
    }

    private void balasChat(String replyToken, String jawaban){
        TextMessage jawabanDalamBentukTextMessage = new TextMessage(jawaban);
        try {
            lineMessagingClient
                .replyMessage(new ReplyMessage(replyToken, jawabanDalamBentukTextMessage))
                .get();
        } catch (InterruptedException|ExecutionException e) {
            System.out.println("Ada error saat ingin membalas chat biasa");
        }
    }
    //\n         

}
