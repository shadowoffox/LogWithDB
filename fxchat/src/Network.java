import javafx.application.Platform;
import javafx.scene.control.ComboBox;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Network implements Closeable {
    DataOutputStream out;
    DataInputStream in;
    Scanner scanner = new Scanner(System.in);

    private Socket socket;
    private sendMsg messageSender;
    private String AUTH_PATTERN= "/auth %s %s";
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^/w (\\w+) (.+)", Pattern.MULTILINE);
    private static final String USER_LIST_PATTERN = "/userlist";
    private static final String MESSAGE_SEND_PATTERN = "/w %s %s";
    private static final String CHANGE_NAME_PATTERN = "/w changeName %s";
    private final Thread receiver;
    private String hostname;
    private int port;
    private String username;
    public ComboBox<String> onlineNames;

    public  Network(String hostname, int port, sendMsg messageSender) {
        this.hostname =hostname;
        this.port=port;
        this.messageSender = messageSender;
        this.receiver = createReceiverThread();
       // this.onlineNames = onlineNames;
    }

    private Thread createReceiverThread(){
     return new Thread(new Runnable() {

         @Override
         public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        String msg = in.readUTF();
                        System.out.println("Message: " + msg);

                        if (msg.startsWith("ONLINE_MESSAGE_LIST")){
                            String[] users = msg.replace("ONLINE_USER_LIST ", "").split(":");
                            System.out.println(Arrays.toString(users));
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    chat.clients.getItems().clear();
                                    for (String q: users) {
                                        chat.clients.getItems().add(q);

                                    }
                            //System.out.println(onlineNames.getItems());
                                }
                            });
                        }
                        Matcher matcher = MESSAGE_PATTERN.matcher(msg);
                        if (matcher.matches()){
                            Message message = new Message(matcher.group(1),username,matcher.group(2));
                            messageSender.sendMsg(message);
                        } else if (msg.startsWith(USER_LIST_PATTERN)){

                        }
                              } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
             System.out.printf("Network connection is closed for user %s%n", username);
         }
                });
    }

    public void authorise(String username, String password) throws IOException {
        socket = new Socket(hostname,port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        try {
            out.writeUTF(String.format(AUTH_PATTERN, username, password));
            out.flush();
            String response = in.readUTF();
            if (response.equals("/auth succesful")){
              this.username=username;
                Message welcomeMsg = new Message(" ",username,username + " Hello! Welcome to our chat!");
                messageSender.sendMsg(welcomeMsg);
             receiver.start();
            } else {
                throw new AuthException(){};

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void  sendChangeName(String newName){
        sendMessage(String.format(CHANGE_NAME_PATTERN,newName));
    }

    public void sendMessageToUser(Message message) {
        sendMessage(String.format(MESSAGE_SEND_PATTERN, message.getUserTo(), message.getText()));
    }

        public void sendMessage(String msg){
        try {
        out.writeUTF(msg);
        out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getUsername() {
        return username;
    }
    public void setUsetname(String name){
        this.username = name;
    }
    @Override
    public void close()  {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        receiver.interrupt();
        try {
            receiver.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
