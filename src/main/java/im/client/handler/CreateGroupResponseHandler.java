package im.client.handler;

import im.protocol.response.CreateGroupResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CreateGroupResponseHandler extends SimpleChannelInboundHandler<CreateGroupResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateGroupResponsePacket responsePacket) throws Exception {
        if(responsePacket.isSuccess()){
            System.out.format("群创建成功，id为【%s】，群里有：%s%n",
                    responsePacket.getGroupId(), responsePacket.getUsernameList());
        }
    }
}
