package im.client.console;

import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * 控制台指令
 */
public interface ConsoleCommand {
    void exec(Scanner scanner, Channel channel);
}
