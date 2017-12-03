export class Point {
    constructor(x: number, y: number);
    readonly x: number;
    readonly y: number;
    static isPoint(thing: any): boolean;
}

declare module nested {
    type Line = Array<Point>;

    export class Circle {
        constructor(center: Point, radius: number);
        readonly center: Point;
        readonly radius: number;
        static isCirce(thing: any): boolean;
    }
}

declare const globalConst: String;
declare let globalVar: String;
declare function globalFunc(): String;
