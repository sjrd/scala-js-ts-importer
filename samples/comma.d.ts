export class Foo {
    foo(
        options: {
            key1: string,
            key2: string,
        },
        key3: string,
    ): void;
}

export interface Bar {
    key1: string,
    key2: string,
}

export type Callback<R> = (
    value: R
) => void;

export type Handler<T,R> = (
    event: T,
    callback: Callback<R>,
) => void;
