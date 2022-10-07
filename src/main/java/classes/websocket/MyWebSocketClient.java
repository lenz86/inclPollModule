package classes.websocket;


import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyWebSocketClient {

    private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    private StompSession stompSession;

    public void connect(String server, String port) {

        String url = "ws://{host}:{port}/wshost";

        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);

        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        ListenableFuture<StompSession> future = stompClient.connect(url, headers, new MyHandler(), server, port);
        try {
            stompSession = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void send(String jsonMsg) {
        stompSession.send("/app/chat.send", jsonMsg.getBytes());
    }

    private class MyHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            System.out.println("Now connected");
        }
    }

}
