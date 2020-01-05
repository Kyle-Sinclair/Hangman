package common;

import java.io.Serializable;

public class Message implements Serializable {

    public final MessageType type;
    public final Object payload;


    public Message(MessageType type, String payload){
        this.type = type;
        this.payload = payload;
    }

    public Object getPayload(){ return payload; }

    @Override
    public String toString(){
        return '{' + "\"type\":\""  + type + "\", \"payload\":\"" + payload + "\"}";
    }
}
