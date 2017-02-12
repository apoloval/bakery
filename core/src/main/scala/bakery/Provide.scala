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

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise to expand macro annotations")
class Provide extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro Provide.impl
}

object Provide {
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def expandImplicit(tree: c.Tree): c.Tree = tree match {
      case q"$mods def $tname: $tpt = $expr" =>
        q"${expandModsWithImplicit(mods)} def $tname: $tpt = $expr"
      case q"$mods val $tname: $tpt = $expr" =>
        q"${expandModsWithImplicit(mods)} val $tname: $tpt = $expr"
      case _ =>
        c.abort(c.enclosingPosition, "Annotation @Provide can be used only with defs or vals")
    }

    def expandModsWithImplicit(tree: c.Modifiers): c.Modifiers =
      Modifiers(tree.flags | Flag.IMPLICIT)

    val result = {
      annottees.map(_.tree).toList match {
        case annottee :: Nil =>
          expandImplicit(annottee)
        case _ =>
          c.abort(c.enclosingPosition, "Annotation @Provide can be used only with defs or vals")
      }
    }
    c.Expr[Any](result)
  }
}
