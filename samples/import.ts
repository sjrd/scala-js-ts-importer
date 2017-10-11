// Import variants from https://www.typescriptlang.org/docs/handbook/modules.html#import

import Single from "module.js";
import { Single } from "module.js";
import { Single as Renamed } from "module.js";
import { First, Second } from "module.js";
import { First as F, Second } from "module.js";
import * as validator from "module.js";
import "./my-module.js";

declare const hello: String;

declare module mod {
    import { NestedImport } from "module.js";

    export function f(x: Number): String;
}
