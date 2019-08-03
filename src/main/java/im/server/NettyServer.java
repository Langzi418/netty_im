package im.server;

import im.codec.PacketCodecHandler;
import im.codec.Split;
import im.handler.IMIdleStateHandler;
import im.server.handler.AuthHandler;
import im.server.handler.HeartBeatRequestHandler;
import im.server.handler.IMHandler;
import im.server.handler.LoginRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyServer {
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IMIdleStateHandler());
                            ch.pipeline().addLast(new Split());
                            ch.pipeline().addLast(PacketCodecHandler.INSTANCE);
                            ch.pipeline().addLast(LoginRequestHandler.INSTANCE);
                            ch.pipeline().addLast(HeartBeatRequestHandler.INSTANCE);
                            ch.pipeline().addLast(AuthHandler.INSTANCE);
                            ch.pipeline().addLast(IMHandler.INSTANCE);

                        }
                    }).option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture f = b.bind(port).addListener(
                    future -> {
                        if (future.isSuccess())
                            System.out.format("端口[%d]绑定成功...%n", port);
                        else
                            System.out.format("端口[%d]绑定失败...%n", port);
                    }
            ).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyServer(8000).run();
    }
}
