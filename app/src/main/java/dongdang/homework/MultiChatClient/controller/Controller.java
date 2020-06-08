package dongdang.homework.MultiChatClient.controller;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import dongdang.homework.MultiChatClient.util.MetaDataLoader;
import dongdang.homework.MultiChatClient.view.ChatActivity;
import dongdang.homework.MultiChatClient.model.bl.ClientService;
import dongdang.homework.MultiChatClient.view.JoinActivity;

public class Controller {
    private static ClientService clientService = new ClientService();

    public static final String SERVER_IP = MetaDataLoader.getServerAdr();
    public static final int PORT = MetaDataLoader.getPort();
    public static final int filePort = MetaDataLoader.getFilePort();

    //로그인
    public static void serverConncect(String id, String pw, Context content) {
        System.out.println("id : "+id+" pw : "+pw);
        boolean state = clientService.connect(SERVER_IP,PORT, id, pw);
        if(state) {
            Toast.makeText(content, "서버에 접속되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(content, ChatActivity.class);
            content.startActivity(intent);
        }else {
            Toast.makeText(content, "서버에 접속할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    //회원가입
    public static void join(String id, String name, String pw, AppCompatActivity context) {
        boolean state = clientService.join(SERVER_IP,PORT, id,name, pw);
        if(state) {
            Toast.makeText(context, "가입되었습니다.", Toast.LENGTH_SHORT).show();
            context.finish();
        }else {
            Toast.makeText(context, "서버에 접속할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void sendImage(File file, AppCompatActivity context) {

    }
    //getter
    public static ClientService getClientService() {
        return clientService;
    }

    //소켓종료
    public static void disconnect() {
        clientService.close();
    }
}
