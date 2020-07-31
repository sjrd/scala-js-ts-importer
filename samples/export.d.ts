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

declare namespace PIXI2 {
    const VERSION: string;
}
declare namespace PIXI3 {
    const VERSION: string;
}

declare module "pixi.js" {
    export = PIXI;
}

export as namespace asNamespace;

export default Hoge

export {}
export { PIXI }
export { PIXI2, PIXI3 }
