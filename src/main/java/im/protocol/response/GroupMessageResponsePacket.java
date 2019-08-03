package im.protocol.response;

import im.protocol.Packet;
import im.protocol.command.Command;
import im.session.Session;
import lombok.Data;

@Data
public class GroupMessageResponsePacket extends Packet {
    private String fromGroupId;
    private String message;
    private Session fromUser;

    @Override
    public Byte getCommand() {
        return Command.GROUP_MESSAGE_RESPONSE;
    }
}
