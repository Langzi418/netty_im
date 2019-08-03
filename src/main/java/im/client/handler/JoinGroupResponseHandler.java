package im.client.handler;

import im.protocol.response.JoinGroupResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class JoinGroupResponseHandler extends SimpleChannelInboundHandler<JoinGroupResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JoinGroupResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()) {
            System.out.format("加入群【%s】成功！%n", responsePacket.getGroupId());
        } else {
            System.out.format("加入群【%s】失败，原因是%s%n",
                    responsePacket.getGroupId(), responsePacket.getReason());
        }
    }
}
