package im.protocol.command;

import im.serializer.Serializer;
import im.serializer.impl.JSONSerializer;
import io.netty.buffer.ByteBuf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PacketCodecTest {

    @Test
    public void encode() {
        /*Serializer serializer = new JSONSerializer();
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();

        loginRequestPacket.setVersion((byte) 1);
        loginRequestPacket.setUserId(123);
        loginRequestPacket.setUsername("zhangsan");
        loginRequestPacket.setPassword("password");

        PacketCodec packetCodec = new PacketCodec();
        ByteBuf byteBuf = packetCodec.encode(loginRequestPacket);
        Packet decodedPacket = packetCodec.decode(byteBuf);

        Assertions.assertArrayEquals(serializer.serialize(loginRequestPacket),
                serializer.serialize(decodedPacket));*/
    }
}
