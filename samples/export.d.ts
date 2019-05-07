export const numberRegexp: string;

export interface StringValidator {
    isAcceptable(s: string): boolean;
}

export namespace Hoge {
    export class Fuga {
        name: string
    }
}

declare namespace PIXI {
    const VERSION: string;
}

declare module "pixi.js" {
    export = PIXI;
}

export as namespace asNamespace;

export default Hoge
