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
        char cek = pesan.charAt(pesan.length()-1);//variable penampung karakter terakhir (?)
        Boolean lanjut=false;
        //uji coba
        System.out.println("Isi Pesan :"+pesan);//Cek isi pesan
        System.out.println("Panjang Pesan :"+pesan.length());//Melihat panjang isi pesan
        System.out.println("Harusnya si tanda tanya :"+cek);//Cek isi variable cek, apakah tanda tanya atau bukan.

        pesan_dikirim="";//inisialisasi variable pesan dikirim menjadi kosong

        //percabangan pemberian jawaban
        switch(pesan) {
            case "/rules"://untuk petunjuk penggunaan bot chat
                String rules="Berikut aturan untuk menggunakan E-Custumer Service IT Telkom Purwokerto\n1. Untuk pertanyaan diakhiri dengan tanda tanya '?'.\n2. Menggunakan bahasa yang baku, jangan bahasa gaul ya kak.\n3. Jika tidak ada yang mau ditanyakan lagi bisa ketik 'Terima Kasih'.";
                balasChat(replyToken, rules, "null");
            break;
            case "terima kasih"://memberikan balasan ucapan terima kasih untuk user 
                String terimakasih="Terima kasih telah menggunakan E-Costumer Service IT Telkom Purwokerto.\nJika ada yang mau ditanyakan lagi bisa tanya disini ka atau datang dan kunjungi social media kami\n\nKawasan Pendidikan Telkom Jl. DI. Panjaitan No. 128 Purwokerto 53147, Jawa Tengah.\nFan Page: It Telkom Purwokerto\nInstagram: @join_ittp\nLine ID: @join_ittp\nWA/SMS/Telp: 081228319222\nHunting: (0281) – 641629, Fax : (0281) 641630";
                balasChat(replyToken, terimakasih, "null");
            break;
            case "ya"://mengkonfirmasi jawaban yang diberikan bot
                if(pesan2=="true"){
                    balasChat(replyToken, pesan_dua+pesan_tutup, ""+gambar);
                    pesan2="";
                    pesan_dua="";
                    gambar="";
                    ya="";
                }else{
                    String error="Mohon untuk memperhatikan bahasa yang anda gunakan.\nUntuk informasi lebih lanjut, anda bisa membaca aturan yang ditentukan.\nSilahkan ketik '/rules', Terima Kasih.";
                    balasChat(replyToken, error, "null");
                }
            break;
            default://selain jawaban diatas maka akan diproses oleh percabangan dibawah
                if(cek=='?'){
                    compare_aiml(pesan);
                    lanjut=true;
                }else{
                    String tandatanya="Mohon untuk memberi tanda tanya '?' dan pastikan bahwa tidak ada huruf / character dibelakang tanda tanya.";
                    balasChat(replyToken, tandatanya, "null");
                }

                if(lanjut==true && ya=="yes"){
                    balasChat(replyToken, pesan_dikirim+pesan_tutup, ""+gambar);
                    ya="";
                }else{
                    balasChat(replyToken, pesan_dikirim, "null");
                }
            break;
        }
    }

    private void compare_aiml(String pesan){
        String cleartext = pesan.substring(0, pesan.length()-1);
        System.out.println("Hasil clear text : "+cleartext);
        String[] pesanSplit = cleartext.split(" ");
        ArrayList<String[]> records = new ArrayList<>();
        
        try {
            File file = new File("sample.aiml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
		
            document.getDocumentElement().normalize();
		
            System.out.println("Root element :" + document.getDocumentElement().getNodeName());
            NodeList nodeList = document.getElementsByTagName("idcase");
		
            for (int index = 0; index < nodeList.getLength(); index++) {
                Node node = nodeList.item(index);
                int batas_minimal=0;
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    
                    String[] keyword = eElement.getElementsByTagName("keyword").item(0).getTextContent().split(" ");
                    
                    for(int j=0;j<keyword.length;j++){//melakukan pengecekan keyword dari data.csv
                        System.out.println("Keyword Array ke " + index + " : "+keyword[j]);

                        for(int k=0;k<pesanSplit.length;k++){//melakukan pembandingan keyword dari data dengan pertanyaan user
                            if(keyword[j].equals(pesanSplit[k])){
                                batas_minimal=batas_minimal+1;
                                System.out.println("Isi pesan : " +pesanSplit[k]);
                            }
                        }
                        System.out.println("Jumlah Batas : " +batas_minimal);
                    }
                    
                    if(batas_minimal>=Integer.parseInt(eElement.getElementsByTagName("minimal").item(0).getTextContent())){//pengecekan persamaan antara pertanyaan yang user ajukan dengan database
                        if(eElement.getElementsByTagName("repeat").item(0).getTextContent().equals("Yes")){
                            String hasil = eElement.getElementsByTagName("pesan").item(0).getTextContent().replace("<>","\n");//mengambil data pesan pada database kemudian mengganti <> dengan \n sebagai pengganti enter
                            //int idpesan = Integer.parseInt(eElement.getElementsByTagName("case").item(0).getTextContent());//mengambil id pesan pada database
                            //System.out.print("Harusnya id pesan 2:"+idpesan);
                            String hasil2 = eElement.getElementsByTagName("repeat").item(0).getTextContent().replace("<>","\n");//mengambil data pesan dengan id tertentu
                            String img = eElement.getElementsByTagName("image").item(0).getTextContent();//menggambil link gambar pada database
                            System.out.print("Harusnya hasil pesan 2:"+hasil2);

                            pesan(hasil);//inisialisasi pesan
                            pesangambar(img);//inisialisasi link gambar
                            System.out.print("Harusnya link gambar :"+img);
                            pesankedua(hasil2);//inisialisasi pesan ke 2
                            pesan2="true";
                        }else{
                            String hasil = eElement.getElementsByTagName("repeat").item(0).getTextContent().replace("<>","\n");
                            String img = eElement.getElementsByTagName("image").item(0).getTextContent();
                            System.out.print("Harusnya link gambar :"+img); 
                            pesangambar(img);
                            pesan(hasil);
                            ya="yes";
                        }
                        break;
                    }else{//handling error jika pertanyaan tidak ada yang sesuai
                        String error="Mohon untuk memperhatikan bahasa yang anda gunakan.\nUntuk informasi lebih lanjut, anda bisa membaca aturan yang ditentukan.\nSilahkan ketik '/rules', Terima Kasih.";
                        pesan(error);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    //membandingkan text user dengan keyword yang ada pada database
    /*private void compare(String isi_kiriman){
        String cleartext = isi_kiriman.substring(0, isi_kiriman.length()-1);
        System.out.println("Hasil clear text : "+cleartext);
        String[] pesanSplit = cleartext.split(" ");
        List<String[]> records = new ArrayList<>();

        //membaca file data.csv dan dibandingkan dengan jawaban user
        try (BufferedReader br = new BufferedReader(new FileReader("./data.csv"))) {
            String line;
            if((line = br.readLine()) == null){
                
            }else{
                while ((line = br.readLine()) != null) {
                    records.add(line.split(";"));//memisahkan data.csv dengan pembatasnya ';'
                }
                String[][] array = new String[records.size()][0];//inisialisasi array dengan jumlah baris sebanyak record.size()
                records.toArray(array);//memasukan hasil pembacaan data.csv kedalam array

                for(int i=0;i<array.length;i++){//perulangan untuk pembacaan perbaris dari data.csv yang sudah dirubah ke array
                    int batas_minimal=0;
                    String[] keyword = array[i][1].split(" ");
                    System.out.println("Isi Array : "+array[i]);
                    
                    for(int j=0;j<keyword.length;j++){//melakukan pengecekan keyword dari data.csv
                        System.out.println("Keyword Array ke " + i + " : "+keyword[j]);

                        for(int k=0;k<pesanSplit.length;k++){//melakukan pembandingan keyword dari data dengan pertanyaan user
                            if(keyword[j].equals(pesanSplit[k])){
                                batas_minimal=batas_minimal+1;
                                System.out.println("Isi pesan : " +pesanSplit[k]);
                            }
                        }
                        System.out.println("Jumlah Batas : " +batas_minimal);
                    }
                    if(batas_minimal>=Integer.parseInt(array[i][2])){//pengecekan persamaan antara pertanyaan yang user ajukan dengan database
                        if(array[i][4].equals("Yes")){
                            String hasil = array[i][3].replace("<>","\n");//mengambil data pesan pada database kemudian mengganti <> dengan \n sebagai pengganti enter
                            int idpesan = Integer.parseInt(array[i][5]);//mengambil id pesan pada database
                            System.out.print("Harusnya id pesan 2:"+idpesan);
                            String hasil2 = array[idpesan-1][3].replace("<>","\n");//mengambil data pesan dengan id tertentu
                            String img = array[idpesan-1][6];//menggambil link gambar pada database
                            System.out.print("Harusnya hasil pesan 2:"+hasil2);

                            pesan(hasil);//inisialisasi pesan
                            pesangambar(img);//inisialisasi link gambar
                            System.out.print("Harusnya link gambar :"+img);
                            pesankedua(hasil2);//inisialisasi pesan ke 2
                            pesan2="true";
                        }else{
                            String hasil = array[i][3].replace("<>","\n");
                            String img = array[i][6];
                            System.out.print("Harusnya link gambar :"+img); 
                            pesangambar(img);
                            pesan(hasil);
                            ya="yes";
                        }
                        break;
                    }else{//handling error jika pertanyaan tidak ada yang sesuai
                        String error="Mohon untuk memperhatikan bahasa yang anda gunakan.\nUntuk informasi lebih lanjut, anda bisa membaca aturan yang ditentukan.\nSilahkan ketik '/rules', Terima Kasih.";
                        pesan(error);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void pesan(String pesan){
        this.pesan_dikirim=pesan;
    }

    private void pesangambar(String gambar){
        this.gambar=gambar;
    }

    private void pesankedua(String pesan){
        this.pesan_dua=pesan;
    }

    private void balasChat(String replyToken, String jawaban, String gambar){
        //percabangan untuk database yang memiliki link gambar
        if(gambar.equals("null")){//percabangan untuk mengirim pesansaja
            TextMessage jawabanDalamBentukTextMessage = new TextMessage(jawaban);
            try {
                lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, jawabanDalamBentukTextMessage))
                    .get();
            } catch (InterruptedException|ExecutionException e) {
                System.out.println("Ada error saat ingin membalas chat biasa");
            }
        }else{//percabangan untuk mengirim pesan beserta gambar
            TextMessage jawabanDalamBentukTextMessage = new TextMessage(jawaban);
            ImageMessage jawabanGambar = new ImageMessage(gambar,gambar);
            System.out.println("Harusnya gambar :"+gambar);
            List<Message> multipesan=new ArrayList<>();
            Set<String> userid = new HashSet<>();
    
            userid.add(id);
            multipesan.add(jawabanDalamBentukTextMessage);
            multipesan.add(jawabanGambar);
    
            Multicast multi = new Multicast(userid,multipesan);
            try {
                lineMessagingClient
                        .multicast(multi)
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Ada error saat ingin membalas chat gambar");
            }
        }
    }

}
