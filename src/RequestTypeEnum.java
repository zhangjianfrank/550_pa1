public enum RequestTypeEnum {
    REGISTER(1),
    UNREGISTER(2),
    LOOKUP(3),
    DISCONNECT(4),
    ;

    private final int code;
    RequestTypeEnum(int code){
        this.code=code;
    }

    public static RequestTypeEnum getEnumByCode(int code){
        for(RequestTypeEnum requestTypeEnum:values()){
            if(requestTypeEnum.getCode()==code){
                    return REGISTER;
            }
        }

        return REGISTER;
    }

    public int getCode() {
        return code;
    }
}
