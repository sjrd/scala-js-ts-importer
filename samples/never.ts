declare module nevertype {

  export class RangeQuery {
      never: never;

      value(queryVal: string | number): string;

      method(foo: never): Array<never>;
  }

}
