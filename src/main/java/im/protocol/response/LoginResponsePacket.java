package im.protocol.response;

import im.protocol.Packet;
import im.protocol.command.Command;
import lombok.Data;

@Data
public class LoginResponsePacket extends Packet {
    private String userId;
    private String username;

    // 登录成功标志
    private boolean success;
    // 登录失败原因
    private String reason;


    @Override
    public Byte getCommand() {
        return Command.LOGIN_RESPONSE;
    }
}
