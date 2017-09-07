declare module monaco {

  const id: string;
  export type BuiltinTheme = 'vs' | 'vs-dark' | 'hc-black';

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
  }

  export interface IEditorOptions {
      ariaLabel?: string;
      rulers?: number[];
      selectionClipboard?: boolean;
      lineNumbers?: 'on' | 'off' | 'relative' | ((lineNumber: number) => string);
      readable?: 'yes' | boolean | 'restricted';
  }
}
