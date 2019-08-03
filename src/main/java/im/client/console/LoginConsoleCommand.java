package im.client.console;

import im.protocol.request.LoginRequestPacket;
import io.netty.channel.Channel;

import java.util.Scanner;

public class LoginConsoleCommand implements ConsoleCommand {

    @Override
    public void exec(Scanner scanner, Channel channel) {

        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        System.out.println("输入用户名登录：");
        String line = scanner.nextLine();

        loginRequestPacket.setUsername(line);
        loginRequestPacket.setPassword("pwd");

        // 发送登录数据报
        channel.writeAndFlush(loginRequestPacket);
        waitForLoginResponse();
    }

    // 登录模拟
    private static void waitForLoginResponse() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
