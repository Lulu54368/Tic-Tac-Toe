package client;

import java.io.Serializable;

public enum Result implements Serializable {

    WIN("wins"),
    DRAW("draw"),
    CONTINUE("continue"),
    END("end"),
    RETRY("retry");
    String result;
    Result(String result) {
        this.result = result;
    }

}
