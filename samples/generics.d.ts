declare namespace generics {
    class Thing {
        name: String
    }
    export class Container<T> {
        t: T
    }
    export class ContainerWithUpperBound<T extends Thing> {
        t: T
    }
    export class ContainerWithDefault<T = any> {
        t: T
    }
    export class ContainerWithUpperBoundAndDefault<T extends Thing = any> {
        t: T
    }
}
