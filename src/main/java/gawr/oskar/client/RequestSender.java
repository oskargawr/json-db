package gawr.oskar.client;

import gawr.oskar.server.ServerConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RequestSender {
    private final RequestBuilder requestBuilder;

    public RequestSender(final RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    public void sendRequest() {
        try (Socket socket = new Socket("localhost", ServerConstants.PORT);
             DataOutputStream output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
             DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {

            String jsonRequest = requestBuilder.buildRequest();
            System.out.println("Sent: " + jsonRequest);
            output.writeUTF(jsonRequest);
            output.flush();

            String jsonResponse = input.readUTF();
            System.out.println("Received: " + jsonResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
