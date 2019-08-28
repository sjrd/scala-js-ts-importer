interface Thing {
    name: string;
    type: string;
    obj: {
        x?: number,
        y?: number
    };
    inStock?: boolean;
    for?: string
    foo: string | null;
}

interface MethodOnly {
    method(s: string): void;
}

interface MethodAndProperty {
    prop: string;
    method(s: string): void;
}

interface MethodOnly {
    method(s: string): void;
}

interface MethodAndProperty {
    prop: string;
    method(s: string): void;
}
