/// <reference path="./assets/module" />
/// <reference path="./assets/module2" />
// Import variants from https://www.typescriptlang.org/docs/handbook/modules.html#import

import Default from "module";
import { Single } from "module";
import { Single as Renamed } from "module";
import { First, Second } from "module";
import { Third as T, Fourth } from "module";
import * as validator from "module";
import "./assets/module";
import { Third as X } from 'module'
import { First as Alpha, Second as Blavo, } from "module";
import Foo, { Bar as Z } from "module2";

declare const hello: String;

declare module mod {
    export function f(x: Number): String;
}
