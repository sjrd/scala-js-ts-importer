declare module nevertype {

  export class ValueTermQueryBase {
      never: never;

      value(queryVal: string | number): string;
  }

  export class RangeQuery extends ValueTermQueryBase {
      /**
       * @override
       * @throws {Error} This method cannot be called on RangeQuery
       */
      value(queryVal: string | number): never;
  }

}
