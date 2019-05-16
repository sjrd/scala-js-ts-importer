declare module typequery {
    export interface C {
        catch<T>(ex: T): void;
        delete<T>(array: T[], key: number): void;
    }
    export const C: C;

    export interface D {
        c: typeof C;
        cat: typeof C.catch;
        del: typeof C.delete;
    }
}
