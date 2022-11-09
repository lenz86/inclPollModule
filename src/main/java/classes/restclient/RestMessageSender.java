package classes.restclient;

import classes.MessageSender;

public class RestMessageSender implements MessageSender<RestTransferData> {

    Communication communication;

    public RestMessageSender(Communication communication) {
        this.communication = communication;
    }

    @Override
    public boolean sendData(RestTransferData restTransferData) {
        return communication.saveInclinometrValue(restTransferData.getInclinometrValue());
    }
}
