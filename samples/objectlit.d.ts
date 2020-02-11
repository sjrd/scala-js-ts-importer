declare var ObjectType: { name: string; age: number };

declare var NumericKeyObjectType: { 0: number; 1: number; 2.1: number };

type IPlugin = {
    name: string;
    sum(a: number, b: number): number;
};
type IPlugin2<T> = {
    name: T;
};

declare function area(options: {
    width: number;
    height: number;
}): number;
