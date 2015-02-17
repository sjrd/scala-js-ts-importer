# Importer from TypeScript type definitions to Scala.js

This tool reads type definitions files written for
[TypeScript](http://www.typescriptlang.org/) (.d.ts files) and rewrites them to
.scala files usable with
[Scala.js](http://www.scala-js.org/).

The process is not 100 % accurate, so manual editing is often needed
afterwards. This can be improved, but not to perfection, because the features
offered by the type systems of TypeScript and Scala.js differ in some subtle
ways.

## Usage

    $ sbt 'run somelib.d.ts SomeLib.scala'

## License

The TypeScript Importer for Scala.js is distributed under the
[Scala License](http://www.scala-lang.org/license.html).
