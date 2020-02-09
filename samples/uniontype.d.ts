declare namespace uniontype {
    export type NumberOrString = number | string;
    export type LeadingPipe = | number | string;
    export type MultilineLeadingPipe =
        | number
        | string;
}
