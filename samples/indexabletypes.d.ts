// extract example from https://www.typescriptlang.org/docs/handbook/interfaces.html#indexable-types
interface NumberDictionary {
    [index: string]: number;
}
interface ReadonlyStringArray {
    readonly [index: number]: string;
}
