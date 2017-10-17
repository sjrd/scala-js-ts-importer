declare module numberlit {

    export type HttpStatuscode = 200 | 404 | 503 ;

    export interface Machine {
        state?: 0 | 1;

        setState(flag: 0 | 1 | boolean): 0 | 1;
    }
}
