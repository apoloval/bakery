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

    val dependencyType = c.prefix.tree match {
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
