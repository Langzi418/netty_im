package im.protocol.response;

import im.protocol.Packet;
import im.protocol.command.Command;
import lombok.Data;

@Data
public class MessageResponsePacket extends Packet {
    private String fromUserId;
    private String fromUserName;
    private String message;

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_RESPONSE;
    }
}
