package types;

public enum VarType {
    BOOLEAN("boolean"), INT("int"), DOUBLE("double"), STRING("string"), NULL("null"), VAR("var");

    public String type;

    VarType(String type) {
        this.type = type;
    }

    static public VarType getType(String type) {
        switch (type) {
            case "boolean": return VarType.BOOLEAN;
            case "int": return  VarType.INT;
            case "string": return VarType.STRING;
            case "null": return VarType.NULL;
            case "double": return VarType.DOUBLE;
            case "var": return VarType.VAR;
            default: throw new IllegalArgumentException(type);
        }
    }
}
