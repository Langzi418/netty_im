package im.client.console;

import im.protocol.request.MessageRequestPacket;
import io.netty.channel.Channel;

import java.util.Scanner;

public class SendToUserConsoleCommand implements ConsoleCommand {

    @Override
    public void exec(Scanner scanner, Channel channel) {
        String toUserId = scanner.next();
        String message = scanner.nextLine();

        channel.writeAndFlush(new MessageRequestPacket(toUserId, message));
    }
}
