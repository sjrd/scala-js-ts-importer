declare module numberlit {

    export type HttpStatuscode = 200 | 404 | 503 ;

    // 1 to Int, 1.0 (which is valid-int) to Double
    export function floating(prob: 0.1 | 0.5 | 1.0): 0.0 | 1

    export interface Machine {
        state?: 0 | 1;

        setState(flag: 0 | 1 | boolean): 0 | 1;
    }
}
