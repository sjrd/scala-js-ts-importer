/* TypeScript importer for Scala.js
 * Copyright 2013-2014 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package org.scalajs.tools.tsimporter

object Utils {

  def scalaEscape(ident: String): String =
    if (needsEscaping(ident)) "`" + ident + "`"
    else ident

  def needsEscaping(ident: String): Boolean = (
      ident.isEmpty ||
      (!ident.head.isUnicodeIdentifierStart && ident.head != '_') ||
      !ident.tail.forall(_.isUnicodeIdentifierPart) ||
      isScalaKeyword(ident)
  )

  val isScalaKeyword: Set[String] = Set(
      "abstract", "case", "class", "catch", "def", "do", "else", "extends",
      "false", "final", "finally", "for", "forSome", "if", "implicit",
      "import", "lazy", "match", "new", "null", "object", "override", "package",
      "private", "protected", "return", "sealed", "super", "then", "this",
      "throw", "trait", "true", "try", "type", "val", "var", "with", "while",
      "yield", ".", "_", ":", "=", "=>", "<-", "<:", "<%", ">:", "#", "@")

}
