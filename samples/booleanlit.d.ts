declare module booleanlit {

    export type True = true;

    export interface TruthMachine {
        update(input: true): void;
    }
}
