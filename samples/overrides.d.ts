declare module overrides {

  export class A {
    equals(other: A): boolean;
    clone(): A;
    toString(): string;
  }

  interface BLike {
    toString(): string;
  }

  export class B implements BLike {
    equals(other: any): boolean;
    clone(): BLike;
    toString(): string;
  }

  interface C {
    equals(other: any): boolean;
    clone(): C;
    toString(): string;
  }
}
