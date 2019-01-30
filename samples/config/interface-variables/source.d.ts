export interface GPGPUProgram {
    variableNames: string[];
    outputShape: number[];
    params: Array<{}>;
    userCode: string;
    supportsBroadcasting?: boolean;
}

export interface Foo {
    field: string[];
    method(): void;
}
