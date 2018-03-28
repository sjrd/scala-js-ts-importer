declare module M {

  interface A {}
  interface B {}

  function f<T extends A & B>(t: T);
}
