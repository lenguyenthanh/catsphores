import sbt._

object Dependencies {

  object Cats {
    val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.11"
    val catsMtl = "org.typelevel" %% "cats-mtl" % "1.2.1"

    val all = Seq(catsEffect, catsMtl)
  }

  object Tests {
    val munit = "org.scalameta" %% "munit" % "1.0.0" % Test
    val munitCatsEffect = "org.typelevel" %% "munit-cats-effect" % "2.0.0" % Test
    val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.16.0" % Test
    val munitScalaCheck = "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test

    val all = Seq(munit, munitScalaCheck, munitCatsEffect, scalaCheck)
  }

  val all = Cats.all ++ Tests.all

}
