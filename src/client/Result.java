package client;

public enum Result {

    WIN("wins"),
    DRAW("draw"),
    FAIL("fails"),
    CONTINUE("continue"),
    END("end"),
    RETRY("retry");
    String result;
    Result(String result) {
        this.result = result;
    }
    public String getResult(){
        return result;
    }
}
