package uk.co.olbois.facecraft.model.serverconnection;

import uk.co.olbois.facecraft.sqlite.Identifiable;

public class ServerConnection implements Identifiable<Long> {

    private Long id;
    private String host;
    private int port = 25565;
    private Long userId;

    // TODO delete these, they will eventually be queried from the server
    private int userCount;
    private Role role = Role.MEMBER;

    public ServerConnection(){

    }

    public ServerConnection(Long id, String host, int port, int userCount, Role role, Long userId) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.userCount = userCount;
        this.role = role;
        this.userId = userId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public enum Role {
        MEMBER, ADMIN, OWNER
    }

}
