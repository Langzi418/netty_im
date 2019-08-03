package im.session;


import lombok.Data;

@Data
public class Session {
    // 用户唯一性标识
    private String userId;
    private String username;

    public Session(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }


    @Override
    public String toString() {
        return "Session{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
