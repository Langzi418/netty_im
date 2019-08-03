package im.protocol.response;

import im.protocol.Packet;
import im.protocol.command.Command;

public class HeartBeatResponsePacket extends Packet {

    @Override
    public Byte getCommand() {
        return Command.HEARTBEAT_RESPONSE;
    }
}
