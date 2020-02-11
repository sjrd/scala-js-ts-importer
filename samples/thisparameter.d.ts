export class jQuery<T> {
  foo(mapper: (this: T, index: number) => string): jQuery<T>;
}
