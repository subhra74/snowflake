package snowflake.components.main;

import snowflake.components.newsession.SessionInfo;

public class SessionHolder {
    private SessionInfo info;

    public SessionHolder(SessionInfo info){
        this.info=info;
    }

    @Override
    public String toString() {
        return info.getName();
    }

    public void close(){

    }
}
