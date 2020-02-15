export class Parent {
    foo(): void;
    bar(a: string): void;
    width: number;
    readonly x: number;
}

export class Child extends Parent {
    foo(): void;
    width: number;
    readonly x: number;
}
