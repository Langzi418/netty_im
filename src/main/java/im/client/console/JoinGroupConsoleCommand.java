package im.client.console;

import im.protocol.request.JoinGroupRequestPacket;
import io.netty.channel.Channel;

import java.util.Scanner;

public class JoinGroupConsoleCommand implements ConsoleCommand {

    @Override
    public void exec(Scanner scanner, Channel channel) {
        JoinGroupRequestPacket requestPacket = new JoinGroupRequestPacket();

        System.out.print("输入加入群组的id：");
        String id = scanner.next();
        requestPacket.setGroupId(id);

        channel.writeAndFlush(requestPacket);
    }
}
