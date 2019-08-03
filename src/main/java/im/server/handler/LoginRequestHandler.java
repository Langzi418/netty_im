package im.server.handler;

import im.protocol.request.LoginRequestPacket;
import im.protocol.response.LoginResponsePacket;
import im.session.Session;
import im.util.IDUtil;
import im.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 不加 Sharable标识，出现如下错误
 * im.server.handler.LoginRequestHandler is not a @Sharable handler, so can't be added or removed multiple times.
 */
@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {
    public static final LoginRequestHandler INSTANCE = new LoginRequestHandler();

    private LoginRequestHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket loginRequestPacket) throws Exception {
        // 处理登录
        LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
        String username = loginRequestPacket.getUsername();
        loginResponsePacket.setUsername(username);

        if (vaild(loginRequestPacket)) {
            loginResponsePacket.setSuccess(true);
            String userId = IDUtil.randomId();
            loginResponsePacket.setUserId(userId);
            System.out.format("%s：登录成功!%n", username);
            SessionUtil.bindSession(new Session(userId, username), ctx.channel());
        } else {
            loginResponsePacket.setReason("账号或密码错误");
            loginResponsePacket.setSuccess(false);
            System.out.format("%s：登录失败!%n", username);
        }

        // 向客户端发送登录响应
        ctx.channel().writeAndFlush(loginResponsePacket);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionUtil.unBindSession(ctx.channel());
    }

    // 登录验证
    private boolean vaild(LoginRequestPacket loginRequestPacket) {
        return true;
    }

}
