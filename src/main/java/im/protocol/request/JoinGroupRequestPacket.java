package im.protocol.request;

import im.protocol.Packet;
import im.protocol.command.Command;
import lombok.Data;


@Data
public class JoinGroupRequestPacket extends Packet {
    private String groupId;

    @Override
    public Byte getCommand() {
        return Command.JOIN_GROUP_REQUEST;
    }
}
