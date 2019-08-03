package im.protocol;

import im.protocol.request.*;
import im.protocol.response.*;
import im.serializer.Serializer;
import im.serializer.impl.JSONSerializer;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import static im.protocol.command.Command.*;

public class PacketCodec {
    // 协议类型映射表
    private final Map<Byte, Class<? extends Packet>> PACKET_TYPE_MAP;
    // 序列化器映射表
    private final Map<Byte, Serializer> SERIALIZER_MAP;

    // 魔数，本协议标识
    public static final int MAGIC_NUMBER = 0x12345678;
    // 单例
    public static final PacketCodec INSTANCE = new PacketCodec();

    // 初始化协议类型和序列化器映射
    private PacketCodec() {
        PACKET_TYPE_MAP = new HashMap<>();
        PACKET_TYPE_MAP.put(LOGIN_REQUEST, LoginRequestPacket.class);
        PACKET_TYPE_MAP.put(LOGIN_RESPONSE, LoginResponsePacket.class);
        PACKET_TYPE_MAP.put(MESSAGE_REQUEST, MessageRequestPacket.class);
        PACKET_TYPE_MAP.put(MESSAGE_RESPONSE, MessageResponsePacket.class);
        PACKET_TYPE_MAP.put(LOGOUT_REQUEST, LogoutRequestPacket.class);
        PACKET_TYPE_MAP.put(LOGOUT_RESPONSE, LogoutResponsePacket.class);
        PACKET_TYPE_MAP.put(CREATE_GROUP_REQUEST, CreateGroupRequestPacket.class);
        PACKET_TYPE_MAP.put(CREATE_GROUP_RESPONSE, CreateGroupResponsePacket.class);
        PACKET_TYPE_MAP.put(JOIN_GROUP_REQUEST, JoinGroupRequestPacket.class);
        PACKET_TYPE_MAP.put(JOIN_GROUP_RESPONSE, JoinGroupResponsePacket.class);
        PACKET_TYPE_MAP.put(QUIT_GROUP_REQUEST, QuitGroupRequestPacket.class);
        PACKET_TYPE_MAP.put(QUIT_GROUP_RESPONSE, QuitGroupResponsePacket.class);
        PACKET_TYPE_MAP.put(LIST_GROUP_MEMBERS_REQUEST, ListGroupMembersRequestPacket.class);
        PACKET_TYPE_MAP.put(LIST_GROUP_MEMBERS_RESPONSE, ListGroupMembersResponsePacket.class);
        PACKET_TYPE_MAP.put(GROUP_MESSAGE_REQUEST, GroupMessageRequestPacket.class);
        PACKET_TYPE_MAP.put(GROUP_MESSAGE_RESPONSE, GroupMessageResponsePacket.class);


        Serializer serializer = new JSONSerializer();
        SERIALIZER_MAP = new HashMap<>();
        SERIALIZER_MAP.put(serializer.getSerializerAlgorithm(), serializer);
    }

    public ByteBuf encode(ByteBuf buffer, Packet packet) {
        // Todo: 可接受序列化器参数

        // 序列化java对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        // 实际写数据过程
        buffer.writeInt(MAGIC_NUMBER);
        buffer.writeByte(packet.getVersion());
        buffer.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
        buffer.writeByte(packet.getCommand());
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);

        return buffer;
    }


    public Packet decode(ByteBuf buffer) {
        // 跳过魔数和版本号 4+1
        buffer.skipBytes(5);

        byte serializeAlgorithm = buffer.readByte();
        byte command = buffer.readByte();
        int length = buffer.readInt();

        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        Class<? extends Packet> requestType = PACKET_TYPE_MAP.get(command);
        Serializer serializer = SERIALIZER_MAP.get(serializeAlgorithm);

        if (serializer != null && requestType != null)
            return serializer.deserialize(requestType, bytes);

        return null;
    }
}
