package im.protocol.request;

import im.protocol.command.Command;
import im.protocol.Packet;
import lombok.Data;


@Data
public class LoginRequestPacket extends Packet {
    private String userId;
    private String username;
    private String password;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_REQUEST;
    }
}
