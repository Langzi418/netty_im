package im.server.handler;

import im.protocol.request.MessageRequestPacket;
import im.protocol.response.MessageResponsePacket;
import im.session.Session;
import im.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {
    public static final MessageRequestHandler INSTANCE = new MessageRequestHandler();

    MessageRequestHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket messageRequestPacket) throws Exception {
        // 拿到客户端session
        Session session = SessionUtil.getSession(ctx.channel());

        // 通过向发送方的会话信息构造要发送的消息
        MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
        messageResponsePacket.setFromUserId(session.getUserId());
        messageResponsePacket.setFromUserName(session.getUsername());
        messageResponsePacket.setMessage(messageRequestPacket.getMessage());

        // 拿到消息接收方的channel
        Channel toUserChannel = SessionUtil.getChannel(messageRequestPacket.getToUserId());

        if (toUserChannel != null && SessionUtil.hasLogin(toUserChannel)) {
            // 向接收方客户端发送消息响应数据报
            toUserChannel.writeAndFlush(messageResponsePacket);
        } else {
            System.err.format("[%s] 不在线，发送失败!", messageRequestPacket.getToUserId());
        }
    }
}
