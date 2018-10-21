export interface GPGPUProgram {
    variableNames: string[];
    outputShape: number[];
    params: Array<{}>;
    userCode: string;
    supportsBroadcasting?: boolean;
}