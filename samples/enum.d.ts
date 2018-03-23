declare module enumtype {
    export enum Color {
        Red, Green, Blue
    }

    export enum Button {
        Submit = "submit",
        Reset = "reset",
        Button = "button"
    }

    export enum Mixed {
        EMPTY, NUMERIC = 2, STRING = "string", NEGATIVE = -1
    }

}
