declare module stringlit {

  export type BuiltinTheme = 'vs' | 'vs-dark' | 'hc-black';

  export interface IEditorOptions {
      ariaLabel?: string;
      rulers?: number[];
      selectionClipboard?: boolean;
      lineNumbers?: 'on' | 'off' | 'relative' | ((lineNumber: number) => string);
      readable?: 'yes' | boolean | 'restricted';
  }
}
