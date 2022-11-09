package classes.websocket;

import java.time.LocalDateTime;

/*POJO for transfer data to web-socket server as JSON*/
public class WebSocketTransferData {
    private LocalDateTime regTime;
    private Object[] data;

    public WebSocketTransferData(Object[] data) {
        this.regTime = LocalDateTime.now();
        this.data = data;
    }

    public LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }
}
