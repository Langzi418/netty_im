package im.client.handler;

import im.protocol.response.LoginResponsePacket;
import im.session.Session;
import im.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket loginResponsePacket) throws Exception {
        String userId = loginResponsePacket.getUserId();
        String username = loginResponsePacket.getUsername();

        if (loginResponsePacket.isSuccess()) {
            System.out.format("[%s] 登录成功，用户ID为：[%s]%n", username, userId);
            // 为什么客户端、服务端channel都要绑定
            // 客户端 channel 客户端socket-> 服务端socket
            // 服务端 channel 服务端socket-> 客户端socket
            SessionUtil.bindSession(new Session(userId, username), ctx.channel());
            // SessionUtil.print();
        } else {
            System.out.format("[%s]：登录失败，原因：%s%n",
                    username, loginResponsePacket.getReason());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端连接被关闭!");
    }
}
