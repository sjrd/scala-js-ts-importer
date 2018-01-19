declare module duplicateliteraltypes {
    export interface duplicateliteraltypes {
        duplicate(input: true): void;
        duplicate(input: false): void;

        duplicate(input: "hello"): void;
        duplicate(input: string): void;
    }
}
