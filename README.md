slick-mongo-light
==========================
Slick-style cross-library MongoDB query builder.

http://cvogt.org/slick-mongo-light

**This is an early version.** Expect gaps and breakages. **PRs welcome.**

slick-mongo-light is a project by one of Slick's core developers,
but not actually technically related to the Slick database library.
It is however inspired by it and allows Slick-style query building
compatible with Casbah, ReactiveMongo, PlayJson and easy to integrate
with others. *slick-mongo-light is NOT type-safe.*

Currently only supported are conditions. All supported operators can
be seen here: http://cvogt.org/slick-mongo-light/api/0.2/#org.cvogt.slick_mongo_light.Implicits$$MongoExpressionExtensions

**SBT settings** (don't forget to include one supported Mongo client library):

    libraryDependencies += "org.cvogt" %% "slick-mongo-light" % "0.2"

    resolvers ++= Seq(
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype Releases" at "https://oss.sonatype.org/service/local/repositories/releases/content",
      "Sonatype Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )

**Getting started:**

    import org.cvogt.slick_mongo_light.Implicits._
    import org.cvogt.slick_mongo_light.dialect.Casbah._ // or another dialect

    // compare a top-level field
    col.find(m"someField" === 1)

    // compare a nested field
    col.find(m"someField.nestedField" === 1)

    // compare multiple nested fields
    col.find(m"someField" -> {
      m"nestedField1" === 1 && m"nestedField2" === "foo"
    })

    // embed ordinary mongo queries
    col.find(m"someField" -> {
      m"nestedField1" === 1 && MongoDBObject("nestedField2" -> MongoDBObject("$eq" -> "foo"))
    })

    // check existence in a list
    col.find(m"someField" in List(1,2,3))

    // more examples
    col.find(m"someField".exists)

    import org.cvogt.slick_mongo_light.expressions.Type
    col.find(m"someField".isOfType(Type.Boolean))

    col.find(m"someField" > org.joda.time.DateTime)
