import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatServer {
    public static final Logger log = LogManager.getLogger(ChatServer.class);
   // User nname;
    private final Pattern AUTH_PATTERN = Pattern.compile("^/auth (.+) (.+)$");

    Socket socket;
    private AuthService authService;

    {
        authService = new AuthServiceImpl("User_base");
    }

    public Map<User,ClientHander> clientHandlerMap= Collections.synchronizedMap(new HashMap<>());

    public void ChatServer() {

        try (ServerSocket serverSocket = new ServerSocket(8888))

        {
            log.info("Server Start");
         //   System.out.println("Server Startsssss");

            while (true) {
             socket = serverSocket.accept();
             log.info("Client connected!");
                System.out.println("Client connected!");
                try {
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    String authMessage = in.readUTF();
                    Matcher matcher = AUTH_PATTERN.matcher(authMessage);
                    if (matcher.matches()) {
                        String username = matcher.group(1);
                        String password = matcher.group(2);
                        User authUser = authService.getAuthUser(username,password);
                        if (authUser != null) {
                            clientHandlerMap.put(authUser, new ClientHander(authUser, socket,this));
                            log.info("Авторизация успешна!");
                            System.out.println("Авторизация успешна!");
                            out.writeUTF("/auth succesful");
                            out.flush();
                            log.info("Client " + username +" Connected");
                            System.out.println("Client " + username +" Connected");
                            updateClientNames();
                        } else {
                            log.info("Ошибка авторизации");
                            System.out.println("Ошибка авторизации");
                            out.writeUTF("/auth fail");
                            out.flush();
                            socket.close();
                        }
                    } else {
                        log.info("Ошибка сети!");
                        System.out.println("Ошибка сети!");
                        out.writeUTF("/auth fail");
                        out.flush();
                        socket.close();

                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }


            }
    } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void changeName(User user, String newName){
        authService.changeName(user.username,newName);
        user.username = newName;
        updateClientNames();
    }

    public void updateClientNames(){
        List<String> onlineUsers = clientHandlerMap.keySet().stream()
                    .map(user -> user.username)
                    .collect(Collectors.toList());

        clientHandlerMap.forEach((username, handler) -> {
            try {
                handler.sendUsersList("ONLINE_USER_LIST" + String.join(":",onlineUsers));
            } catch (IOException e){
                e.printStackTrace();
            }
        });

    }
    public void unsubscribeClient(ClientHander clientHandler) {
        clientHandlerMap.remove(clientHandler.getUsername());
       // broadcastUserDisconnected();
    }
    public void sendMessage(User userFrom, String userTo, String msg) throws IOException {
        clientHandlerMap.forEach((user, handler) -> {
            if (user.username.equals(userTo)) {
                try {
                    handler.sendMessage(userFrom.username, msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
