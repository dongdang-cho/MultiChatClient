package dongdang.homework.MultiChatClient.model.bl;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dongdang.homework.MultiChatClient.model.dto.ChatBubbleDTO;
import dongdang.homework.MultiChatClient.model.dto.Message;
import dongdang.homework.MultiChatClient.model.dto.UserInfoDTO;

public class ClientService {
    private final int GOOD = 200; //통신 양호
    private final int GOOD_ALLOW = 201; //통신 양호-의미 긍정
    private final int GOOD_DENY = 202; //통신 양호-의미 부정

    private Socket clientSock;
    private boolean state;
    private BufferedReader br;
    private BufferedWriter bw;
    private UserInfoDTO user;
    private List<UserInfoDTO> visitors;
    HashMap<String, ChatBubbleDTO> typeMap;


    public ClientService() {
        state=false;
        typeMap = new HashMap<>();
        typeMap.put("broadcast",new ChatBubbleDTO(Gravity.LEFT,"broadcast", Color.rgb(179, 214, 218)));
        typeMap.put("unicast",new ChatBubbleDTO(Gravity.LEFT,"unicast",Color.rgb(249 , 223, 228)));
        typeMap.put("multicast",new ChatBubbleDTO(Gravity.LEFT,"multicast",Color.rgb(214,165,131)));
        typeMap.put("system",new ChatBubbleDTO(Gravity.CENTER,"system",Color.rgb(140,140,140)));
        typeMap.put("my",new ChatBubbleDTO(Gravity.RIGHT,"my",Color.rgb(180,162,242)));
    }



    public boolean connect(final String ip, final int port, String id, String pw) {
        user = new UserInfoDTO(id,pw);
        user.setType("login");
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    clientSock = new Socket(ip,port);
                    bw = new BufferedWriter(new OutputStreamWriter(clientSock.getOutputStream()));
                    br = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                    state=false;
                }
                try {
                    String message = new Gson().toJson(user);
                    write(message);
                    String readMsg = read();
                    System.out.println(readMsg);
                    UserInfoDTO responseDTO = new Gson().fromJson(readMsg, UserInfoDTO.class);
                    if(responseDTO.getStatus()==GOOD_ALLOW) {
                        state=true;
                        user.setName(responseDTO.getName());
                    }else if(responseDTO.getStatus()==GOOD_DENY){
                        user = null;
                        state=false;
                        close();
                    }else {
                        user = null;
                        state=false;
                        try {
                            clientSock.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        close();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return state;
    }
    public boolean join(final String ip, final int port, String id, String name, String pw) {
        final UserInfoDTO joinUser = new UserInfoDTO(id,name,pw);
        joinUser.setType("join");
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    clientSock = new Socket(ip,port);
                    bw = new BufferedWriter(new OutputStreamWriter(clientSock.getOutputStream()));
                    br = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                    state=false;
                }
                try {
                    String message = new Gson().toJson(joinUser);
                    write(message);
                    String readMsg = read();
                    System.out.println(readMsg);
                    UserInfoDTO responseDTO = new Gson().fromJson(readMsg, UserInfoDTO.class);
                    if(responseDTO.getStatus()==GOOD_ALLOW) {
                        state=true;
                    }else if(responseDTO.getStatus()==GOOD_DENY){
                        state=false;
                    }else {
                        state=false;
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    clientSock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return state;
    }
    public Socket getClientSock() {
        return clientSock;
    }

    public BufferedReader getBr() {
        return br;
    }

    public BufferedWriter getBw() {
        return bw;
    }

    public UserInfoDTO getUser() {
        return user;
    }

    public void setVisitors(List<UserInfoDTO> visitors) {
        this.visitors = visitors;
    }

    public List<UserInfoDTO> getVisitors() {
        return visitors;
    }

    public HashMap<String, ChatBubbleDTO> getTypeMap() {
        return typeMap;
    }

    public void send(String content) {
        Message msg = classificationType(content);

        final String m = new Gson().toJson(msg);
        System.out.println(m);
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    write(m);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Message classificationType(String content) {
        Pattern pattern = Pattern.compile("(@\\S+) ");
        Matcher matcher = pattern.matcher(content);
        List<String> receiverList= new ArrayList<>();
        receiverList.add(user.getId());
        while(matcher.find()) {
            receiverList.add(matcher.group().replaceAll("(\\s+)","").substring(1));
            content = content.replace(matcher.group(),"");
        }
        System.out.println("Saddas->"+receiverList);
        content = content.trim();

        Message msg = new Message();
        if(receiverList.size()==2) {
            msg.setType("unicast");
        }else if(receiverList.size()>2) {
            msg.setType("multicast");
        }else {
            msg.setType("broadcast");
        }
        msg.setSender(user.getName());
        msg.setMessage(content);
        msg.setReceiver(receiverList);
        return msg;
    }
    public void receive(ListView chatListView, ChatAdapter chatAdapter, ArrayList<ChatBubbleDTO> chatList, ListView navListView, AppCompatActivity context) {
        new ClientReceiverThread(clientSock,chatListView,chatAdapter,chatList,navListView,context).start();
    }

    public String read() throws IOException {
        return br.readLine();
    }

    public void write(String message) throws IOException {
        if(message==null) return;
        bw.write(message+"\n");
        bw.flush();
    }
    public void close() {
        try {
            if(bw!=null)  bw.close();
            if(br!=null)br.close();
            if(clientSock!=null)clientSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
