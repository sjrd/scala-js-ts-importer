/// <reference path="./.assets/module.d.ts" />
// Import variants from https://www.typescriptlang.org/docs/handbook/modules.html#import

import Default from "module";
import { Single } from "module";
import { Single as Renamed } from "module";
import { First, Second } from "module";
import { Third as T, Fourth } from "module";
import { First as Alpha, Second as Bravo, } from "module";
import Foo, { Second as Z } from "module";
import * as validator from "module";
import "./assets/module";

declare const hello: String;

declare module mod {
    export function f(x: Number): String;
}
