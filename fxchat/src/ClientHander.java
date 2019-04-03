import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHander {
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^/w (\\w+) (.+)", Pattern.MULTILINE);
    private static final String MESSAGE_SEND_PATTERN = "/w %s %s";
    private static final String USER_LIST_PATTERN = "/userlist";
    private final Pattern CHANGE_NAME_PATTERN = Pattern.compile("^/w changeName (\\w+)");

    private final Thread handleThread;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final ChatServer server;
    private final User user;
    private final Socket socket;

    public ClientHander(User user, Socket socket, ChatServer server) throws IOException {
     this.user = user;
       this.socket = socket;
        this.server = server;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        this.handleThread = new Thread(new Runnable() {
            @Override
            public void run() {
               try {
                    while (!Thread.currentThread().isInterrupted()){
                        String msg = in.readUTF();
                        System.out.printf("Message from user %s: %s%n", ClientHander.this.user.username, msg);
                        Matcher changeNameMatcher = CHANGE_NAME_PATTERN.matcher(msg);
                        if (changeNameMatcher.matches()){
                            String newName = changeNameMatcher.group(1);
                            server.changeName(user,newName);
                        }
                        Matcher matcher = MESSAGE_PATTERN.matcher(msg);
                        if (matcher.matches()){
                            String userTo = matcher.group(1);
                            String message = matcher.group(2);

                                server.sendMessage(user,userTo, message);

                                server.sendMessage(user, user.username,message);

                        }
                    }
                } catch (IOException e) {
                  e.printStackTrace();
                } finally {
                    System.out.printf("Client %s disconnected%n", ClientHander.this.user.username);
                    try {
                       socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                   }
                    server.unsubscribeClient(ClientHander.this);
                }
            }
        });
        handleThread.start();
    }

    public void sendMessage(String userTo, String msg) throws IOException {
        out.writeUTF(String.format(MESSAGE_SEND_PATTERN, userTo, msg));
    }

    public String getUsername() {
        return ClientHander.this.user.username;
    }

    public void sendUsersList(String allUsers) throws IOException {
        out.writeUTF(allUsers);
    }
}
