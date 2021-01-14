package protocol;

import javafx.application.Platform;
import protocol.classes.ListProjectEntry;
import utils.StringUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class ChatUtils {

    public static void sendChatMsg(
            String username, String projectName, String text,
            InetAddress multicastAddress, int port)
    {
        String toSend =
                CSOperations.CHAT_MSG + ";"
                        + username + ";"
                        + projectName + ";"
                        + Long.toString(System.currentTimeMillis()) + ";"
                        + text;

        byte[] data = StringUtils.stringToBytes(toSend);

        DatagramPacket dp = new DatagramPacket(data, data.length, multicastAddress, port);

        try(DatagramSocket ms = new DatagramSocket()) {
            ms.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendChatStop(
            String username, String projectName,
            InetAddress multicastAddress, int port)
    {
        String toSend =
                CSOperations.CHAT_STOP + ";"
                        + username + ";"
                        + projectName;

        byte[] data = StringUtils.stringToBytes(toSend);

        DatagramPacket dp = new DatagramPacket(data, data.length, multicastAddress, port);

        try(DatagramSocket ms = new DatagramSocket()) {
            ms.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
