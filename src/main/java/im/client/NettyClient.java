package im.client;

import im.client.console.ConsoleCommandManager;
import im.client.console.LoginConsoleCommand;
import im.client.handler.*;
import im.codec.PacketCodecHandler;
import im.codec.Split;
import im.handler.IMIdleStateHandler;
import im.util.SessionUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private String host;
    private int port;
    // 执行指数退避的最大重连次数
    private int maxRetry;

    public NettyClient(String host, int port) {
        this(host, port, 5);
    }

    public NettyClient(String host, int port, int retry) {
        this.host = host;
        this.port = port;
        maxRetry = retry;
    }

    public void run() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IMIdleStateHandler());
                        ch.pipeline().addLast(new Split());
                        ch.pipeline().addLast(PacketCodecHandler.INSTANCE);
                        ch.pipeline().addLast(new LoginResponseHandler());
                        ch.pipeline().addLast(new MessageResponseHandler());
                        ch.pipeline().addLast(new CreateGroupResponseHandler());
                        ch.pipeline().addLast(new JoinGroupResponseHandler());
                        ch.pipeline().addLast(new QuitGroupResponseHandler());
                        ch.pipeline().addLast(new ListGroupMembersResponseHandler());
                        ch.pipeline().addLast(new GroupMessageResponseHandler());
                        ch.pipeline().addLast(new LogoutResponseHandler());

                        // 心跳定时器
                        ch.pipeline().addLast(new HeartBeatTimerHandler());
                    }
                }).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        connect(bootstrap, host, port, maxRetry);

    }

    private void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(
                future -> {
                    if (future.isSuccess()) {
                        System.out.println("连接成功...");
                        Channel channel = ((ChannelFuture) future).channel();
                        // 连接成功后，开启控制台线程
                        startConsoleThread(channel);
                    } else if (retry == 0)
                        System.err.println("重连次数用完，放弃连接....");
                    else {
                        // 第几次重连
                        int order = maxRetry - retry + 1;
                        // 时间间隔
                        int delay = 1 << order;
                        System.err.format("%s：连接失败，第%d次重连...%n", new Date(), order);
                        // 重连
                        bootstrap.config().group().schedule(
                                () -> connect(bootstrap, host, port, retry - 1),
                                delay, TimeUnit.SECONDS);
                    }
                }
        );
    }


    private void startConsoleThread(Channel channel) {
        Scanner scanner = new Scanner(System.in);
        ConsoleCommandManager manager = new ConsoleCommandManager();
        LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand();
        new Thread(() -> {
            while (!Thread.interrupted()) {
                if (!SessionUtil.hasLogin(channel)) {
                    loginConsoleCommand.exec(scanner, channel);
                } else {
                    // 已登录
                    manager.exec(scanner, channel);
                }
            }
        }).start();
    }


    public static void main(String[] args) {
        new NettyClient("localhost", 8000).run();
    }
}
