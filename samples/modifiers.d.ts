declare module modifiers {

  const id: string;

  let name: string;

  interface IEvent<T> {}

  export class Emitter<T> {
      constructor();
      public readonly event: IEvent<T>;
      fire(event?: T): void;
      dispose(): void;
  }

  export var EditorType: {
      ICodeEditor: string;
      IDiffEditor: string;
  };

  export let EditorType2: {
      ICodeEditor: string;
      IDiffEditor: string;
  };

  export const CursorMoveByUnit: {
      Line: string;
      WrappedLine: string;
      Character: string;
      HalfLine: string;
  };

  export class Uri {
      static isUri(thing: any): boolean;
      public static parse(value: string): Uri;
      protected constructor();
      readonly scheme: string;
      readonly authority: string;
      readonly path: string;
      private cache;
      private ignoreMe: number;
      private static ignoreMe2: number;
      private updateCache();
      private static resolve(): String;
  }

}
