declare module typequery {
    export interface C {
        catch<T>(ex: T): void;
        delete<T>(array: T[], key: number): void;
    }
    export const X: C;

    export interface D {
        x: typeof X;
        cat: typeof X.catch;
        del: typeof X.delete;
    }
}
