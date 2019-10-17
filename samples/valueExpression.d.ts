declare module valueExpression {
    export enum StringEnum {
        A = "test",
        B = A,
        C = "test" + "test"
    }
    export enum NumberEnum {
        A = 3 + 3,
        B = 1 << 3,
        C = 10**2+1*8- - -3/3>>2<<3>>>+19%~~~3+ + +9
    }
}