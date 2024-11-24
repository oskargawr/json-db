package gawr.oskar.client;


public class Client {
    private final RequestSender requestSender;

    public Client(final RequestSender requestSender) {
        System.out.println("Client started");
        this.requestSender = requestSender;
        requestSender.sendRequest();
    }

    public Client(final String type, final String index, final String value) {
        System.out.println("Client started");
        this.requestSender = new RequestSender(new RequestBuilder(type, index, value));
        requestSender.sendRequest();
    }

    public Client(final String inFilePath) {
        this.requestSender = new RequestSender(new RequestBuilder(inFilePath));
        requestSender.sendRequest();
    }
}
