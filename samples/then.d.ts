declare module then {

  interface Thenable<T> {
    then<TResult>(
      onfulfilled?: (value: T) => TResult | Thenable<TResult>,
      onrejected?: (reason: any) => TResult | Thenable<TResult>): Thenable<TResult>;
  }

  class then {}
}
