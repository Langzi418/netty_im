package im.server.handler;

import im.protocol.request.CreateGroupRequestPacket;
import im.protocol.response.CreateGroupResponsePacket;
import im.util.IDUtil;
import im.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class CreateGroupRequestHandler extends SimpleChannelInboundHandler<CreateGroupRequestPacket> {
    public static final CreateGroupRequestHandler INSTANCE = new CreateGroupRequestHandler();

    CreateGroupRequestHandler() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateGroupRequestPacket requestPacket) throws Exception {
        List<String> userIds = requestPacket.getUserIdList();

        // channel分组
        ChannelGroup channelGroup = new DefaultChannelGroup(ctx.executor());

        List<String> usernameList = new ArrayList<>();
        for (String userId : userIds) {
            Channel channel = SessionUtil.getChannel(userId);
            if (channel != null) {
                channelGroup.add(channel);
                usernameList.add(SessionUtil.getSession(channel).getUsername());
            }
        }

        CreateGroupResponsePacket responsePacket = new CreateGroupResponsePacket();
        String groupId = IDUtil.randomId();
        responsePacket.setSuccess(true);
        responsePacket.setGroupId(groupId);
        responsePacket.setUsernameList(usernameList);
        SessionUtil.putChannelGroup(groupId, channelGroup);

        // 向客户端组发送响应数据报
        channelGroup.writeAndFlush(responsePacket);

        System.out.format("群创建成功，id为【%s】，群里有：%s%n",
                responsePacket.getGroupId(), responsePacket.getUsernameList());
    }

}
