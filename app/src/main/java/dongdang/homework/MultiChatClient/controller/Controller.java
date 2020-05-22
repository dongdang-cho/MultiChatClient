package dongdang.homework.MultiChatClient.controller;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import dongdang.homework.MultiChatClient.view.ChatActivity;
import dongdang.homework.MultiChatClient.model.bl.ClientService;

public class Controller {
    private static ClientService clientService = new ClientService();

    public static void serverConncect(String ip, int port, String id, String name, Context content) {
        boolean state = clientService.connect(ip, port, id, name);
        if(state) {
            Toast.makeText(content, "서버에 접속되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(content, ChatActivity.class);
            content.startActivity(intent);
        }else {
            Toast.makeText(content, "서버에 접속할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static ClientService getClientService() {
        return clientService;
    }
    public static void disconnect() {
        clientService.close();
    }
}
