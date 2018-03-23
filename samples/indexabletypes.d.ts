// extract example from https://www.typescriptlang.org/docs/handbook/interfaces.html#indexable-types
interface NumberDictionary {
    [index: string]: number;
}
interface ReadonlyStringArray {
    [index: number]: string;
}

// extract example from https://github.com/Microsoft/vscode/blob/bc7b804fdec62173b5437d188e6aa64c036d24f0/src/vs/vscode.d.ts#L3601
export interface WorkspaceConfiguration {
    readonly [key: string]: any;
}