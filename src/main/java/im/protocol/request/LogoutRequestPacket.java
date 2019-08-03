package im.protocol.request;

import im.protocol.Packet;
import im.protocol.command.Command;

public class LogoutRequestPacket extends Packet {
    @Override
    public Byte getCommand() {
        return Command.LOGOUT_REQUEST;
    }
}
