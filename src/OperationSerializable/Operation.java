package OperationSerializable;

import java.io.Serializable;

public class Operation implements Serializable {
    private double o1;
    private char op;
    private double o2;

    public Operation(double o1, char op, double o2) {
        this.o1 = o1;
        this.op = op;
        this.o2 = o2;
    }

    public double getO1() { return o1; }
    public char getOp() { return op; }
    public double getO2() { return o2; }
}