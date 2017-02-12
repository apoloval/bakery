/*
 * Copyright 2017 Alvaro Polo Valdenebro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package bakery

import scala.annotation.{compileTimeOnly, StaticAnnotation}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise to expand macro annotations")
class Dependency[T] extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro Dependency.impl
}

object Dependency {

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def flattenTypes(trees: Seq[c.Tree]): c.Tree = trees.toList match {
      case Nil => tq""
      case head :: Nil => head
      case head :: tail => tq"$head with ${flattenTypes(tail)}"
    }

    val dependencyType = c.prefix.tree match {
      case q"new $name [ ( ..$tpt ) ]" => flattenTypes(tpt)
      case q"new $name [ $tpt ]" => tpt
      case _ => c.abort(c.enclosingPosition,
        "In @Module, annotation has no type parameters")
    }

    def extendSelf(tree: c.Tree): c.Tree = tree match {
      case q"$mods val $name: $tpt = $rhs" =>
        q"$mods val $name: ${extendType(tpt)} = $rhs"
      case _ =>
        c.abort(c.enclosingPosition,
          s"In @Module, unexpected syntax tree received for self: $tree")
    }

    def extendType(tree: c.Tree): c.Tree = tree match {
      case tq"" => dependencyType
      case tq"$tpt" => tq"$dependencyType with $tree"
    }

    def extendTrait(tree: c.Tree): c.Tree = tree match {
      case q"$mods trait $tpname[..$tparams] extends { ..$earlydefns } with ..$parents { $self => ..$body }" => {
        q"""
            $mods trait $tpname[..$tparams] extends { ..$earlydefns } with ..$parents {
              ${extendSelf(self)} =>
              private implicit val _providedDeps = this
              ..$body
            }"""
      }
      case _ => c.abort(c.enclosingPosition, "Annotation @Module can be used only with traits")
    }

    val result = {
      annottees.map(_.tree).toList match {
        case annottee :: Nil =>
          extendTrait(annottee)
        case annottee :: companion :: Nil =>
          q"${extendTrait(annottee)} ; $companion"
        case _ =>
          c.abort(c.enclosingPosition, "Annotation @Module can be used only with traits")
      }
    }
    c.Expr[Any](result)
  }
}
