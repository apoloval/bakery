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
