declare namespace intersectiontype {

    type ArchiverOptions = CoreOptions & ExtraOptions & MoreExtraOptions;
    type UnionOfIntersection = CoreOptions | CoreOptions & ExtraOptions | CoreOptions & MoreExtraOptions;
    type Duplicates = CoreOptions & CoreOptions & ExtraOptions & MoreExtraOptions;

    interface CoreOptions {
        statConcurrency: number;
    }

    interface ExtraOptions {
        allowHalfOpen: boolean;
    }

    interface MoreExtraOptions {
        store: boolean;
    }

    export function test(v : CoreOptions & ExtraOptions): CoreOptions & MoreExtraOptions;

}
