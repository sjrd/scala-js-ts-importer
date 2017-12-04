// copy example from https://github.com/Microsoft/TypeScript/pull/11929
interface Thing {
    name: string;
    width: number;
    height: number;
    inStock: boolean;
}

type K1 = keyof Thing;  // "name" | "width" | "height" | "inStock"
type K2 = keyof Thing[];  // "length" | "push" | "pop" | "concat" | ...
type K3 = keyof { [x: string]: Thing };  // string

type P1 = Thing["name"];  // string
type P2 = Thing["width" | "height"];  // number
type P3 = Thing["name" | "inStock"];  // string | boolean
type P4 = string["charAt"];  // (pos: number) => string
type P5 = string[]["push"];  // (...items: string[]) => number

// following line will work after merged https://github.com/sjrd/scala-js-ts-importer/pull/47
// type P6 = string[][0];  // string

// extract example from https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/lodash/index.d.ts
interface LoDashStatic {
    at<T>(
        object: T | null | undefined,
        ...props: Array<keyof T>
    ): Array<T[keyof T]>;
}
