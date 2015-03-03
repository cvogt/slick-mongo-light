package org.cvogt.slick_mongo_light
import scala.language.implicitConversions

import org.cvogt.slick_mongo_light.expressions._

object Implicits{
  type Expression = org.cvogt.slick_mongo_light.expressions.Expression
  def field = Field(_)
  implicit def scalar[T](value: T) = Scalar[T](value)
  implicit def mongoSeq(value: Seq[Any]) = Sequence(value)
  implicit def scalaOption(value: Option[_]) = Scalar(value.getOrElse(null))

  implicit class MongoFieldStringContext(val context: StringContext) extends AnyVal{
    def m() = Field(context.parts.mkString(""))
  }
  
  implicit class MongoFieldExtensions(val field: Field) extends AnyVal{
    def ->(ex: Expression) = With( field, ex )
  }

  /**
  See http://docs.mongodb.org/manual/reference/operator/query/
  */
  implicit class MongoExpressionExtensions(val value: Expression) extends AnyVal{    
    // Comparison
    // For comparison of different BSON type values, see the specified BSON comparison order.

    /** \$gt Matches values that are greater than the value specified in the query. */
    def >(other: Expression): Expression = InfixOperator(">","$gt",value,other)

    /** \$gte  Matches values that are greater than or equal to the value specified in the query. */
    def >=(other: Expression): Expression = InfixOperator(">=","$gte",value,other)

    /** \$lt Matches values that are less than the value specified in the query. */
    def <(other: Expression): Expression = InfixOperator("<","$lt",value,other)

    /** \$lte  Matches values that are less than or equal to the value specified in the query. */
    def <=(other: Expression): Expression = InfixOperator("<=","$lte",value,other)

    /** \$ne Matches all values that are not equal to the value specified in the query. */
    def =!=(other: Expression): Expression = InfixOperator("=!=","$ne",value,other)

    /** \$ne Matches all values that are not equal to the value specified in the query. */
    def ===(other: Expression): Expression = InfixOperator("===","$eq",value,other)

    /** \$in Matches any of the values that exist in an array specified in the query. */
    def contains(other: Expression): Expression = InfixOperator("contains","$in",value,other)

    /** \$in Matches any of the values that exist in an array specified in the query. */
    def in(other: Expression): Expression = InfixOperator("in","$in",value,other)

/*
    /** \$nin  Matches values that do not exist in an array specified to the query. */
    def notIn(other: Expression): Expression = value !(in other)
*/

    // Logical

    /** \$or Joins query clauses with a logical OR returns all documents that match the conditions of either clause. */
    def ||(other: Expression): Expression = PrefixOperator("||","$or",value,other)

    /** \$and  Joins query clauses with a logical AND returns all documents that match the conditions of both clauses. */
    def &&(other: Expression): Expression = PrefixOperator("&&","$and",value,other)

/*
    // NOT works differently in Mongo than Scala: http://docs.mongodb.org/manual/reference/operator/query/not/#op._S_not
    /** \$not  Inverts the effect of a query expression and returns documents that do not match the query expression. */
    def unary_! : Expression = UnaryOperator("!","$not",value)

    /** \$nor  Joins query clauses with a logical NOR returns all documents that fail to match both clauses. */
    def (other: Expression): Expression = PrefixOperator("","$nor",value,other)
*/
    // Element

    /** \$exists Matches documents that have the specified field. */
    def exists: Expression = InfixOperator("exists","$exists",value,Scalar(true))

    /** \$type Selects documents if a field is of the specified type. */
    def isOfType(tpe: Type): Expression = InfixOperator("isOfType","$type",value,tpe.number)

/*

    //Evaluation

    /** \$mod  Performs a modulo operation on the value of a field and selects documents with a specified result. */
    def (other: Expression): Expression = Operator("","$mod",value,other)

    /** \$regex  Selects documents where values match a specified regular expression. */
    def (other: Expression): Expression = Operator("","$regex",value,other)

    /** \$text Performs text search. */
    def (other: Expression): Expression = Operator("","$text",value,other)

    /** \$where  Matches documents that satisfy a JavaScript expression. */
    def (other: Expression): Expression = Operator("","$where",value,other)


    // Geospatial

    /** \$geoWithin  Selects geometries within a bounding GeoJSON geometry. The 2dsphere and 2d indexes support $geoWithin. */
    def (other: Expression): Expression = Operator("","$geoWithin",value,other)

    /** \$geoIntersects  Selects geometries that intersect with a GeoJSON geometry. The 2dsphere index supports $geoIntersects. */
    def (other: Expression): Expression = Operator("","$geoIntersects",value,other)

    /** \$near Returns geospatial objects in proximity to a point. Requires a geospatial index. The 2dsphere and 2d indexes support $near. */
    def (other: Expression): Expression = Operator("","$near",value,other)

    /** \$nearSphere Returns geospatial objects in proximity to a point on a sphere. Requires a geospatial index. The 2dsphere and 2d indexes support $nearSphere. */
    def (other: Expression): Expression = Operator("","$nearSphere",value,other)


    // Array

    /** \$all  Matches arrays that contain all elements specified in the query. */
    def (other: Expression): Expression = Operator("","$all",value,other)

    /** \$elemMatch  Selects documents if element in the array field matches all the specified $elemMatch conditions. */
    def (other: Expression): Expression = Operator("","$elemMatch",value,other)

    /** \$size Selects documents if the array field is a specified size. */
    def (other: Expression): Expression = Operator("","$size",value,other)


    // Comments

    /** \$comment  Adds a comment to a query predicate. */
    def (other: Expression): Expression = Operator("","$comment",value,other)


    // Projection Operators
    /** \$ Projects the first element in an array that matches the query condition. */
    def (other: Expression): Expression = Operator("","$",value,other)

    /** \$elemMatch  Projects the first element in an array that matches the specified $elemMatch condition. */
    def (other: Expression): Expression = Operator("","$elemMatch",value,other)

    /** \$meta Projects the document’s score assigned during $text operation. */
    def (other: Expression): Expression = Operator("","$meta",value,other)

    /** \$slice  Limits the number of elements projected from an array. Supports skip and limit slices. */
    def (other: Expression): Expression = Operator("","$slice",value,other)

    Update Operators

    Fields
    Name  Description
    /** \$inc  Increments the value of the field by the specified amount. */
    def (other: Expression): Expression = Operator("","$inc",value,other)

    /** \$mul  Multiplies the value of the field by the specified amount. */
    def (other: Expression): Expression = Operator("","$mul",value,other)

    /** \$rename Renames a field. */
    def (other: Expression): Expression = Operator("","$rename",value,other)

    /** \$setOnInsert  Sets the value of a field if an update results in an insert of a document. Has no effect on update operations that modify existing documents. */
    def (other: Expression): Expression = Operator("","$setOnInsert",value,other)

    /** \$set  Sets the value of a field in a document. */
    def (other: Expression): Expression = Operator("","$set",value,other)

    /** \$unset  Removes the specified field from a document. */
    def (other: Expression): Expression = Operator("","$unset",value,other)

    /** \$min  Only updates the field if the specified value is less than the existing field value. */
    def (other: Expression): Expression = Operator("","$min",value,other)

    /** \$max  Only updates the field if the specified value is greater than the existing field value. */
    def (other: Expression): Expression = Operator("","$max",value,other)

    /** \$currentDate  Sets the value of a field to current date, either as a Date or a Timestamp. */
    def (other: Expression): Expression = Operator("","$currentDate",value,other)

    Array
    Operators
    Name  Description
    /** \$ Acts as a placeholder to update the first element that matches the query condition in an update. */
    def (other: Expression): Expression = Operator("","$",value,other)

    /** \$addToSet Adds elements to an array only if they do not already exist in the set. */
    def (other: Expression): Expression = Operator("","$addToSet",value,other)

    /** \$pop  Removes the first or last item of an array. */
    def (other: Expression): Expression = Operator("","$pop",value,other)

    /** \$pullAll  Removes all matching values from an array. */
    def (other: Expression): Expression = Operator("","$pullAll",value,other)

    /** \$pull Removes all array elements that match a specified query. */
    def (other: Expression): Expression = Operator("","$pull",value,other)

    /** \$pushAll  Deprecated. Adds several items to an array. */
    def (other: Expression): Expression = Operator("","$pushAll",value,other)

    /** \$push Adds an item to an array. */
    def (other: Expression): Expression = Operator("","$push",value,other)

    Modifiers
    Name  Description
    /** \$each Modifies the $push and $addToSet operators to append multiple items for array updates. */
    def (other: Expression): Expression = Operator("","$each",value,other)

    /** \$slice  Modifies the $push operator to limit the size of updated arrays. */
    def (other: Expression): Expression = Operator("","$slice",value,other)

    /** \$sort Modifies the $push operator to reorder documents stored in an array. */
    def (other: Expression): Expression = Operator("","$sort",value,other)

    /** \$position Modifies the $push operator to specify the position in the array to add elements. */
    def (other: Expression): Expression = Operator("","$position",value,other)

    Bitwise
    Name  Description
    /** \$bit  Performs bitwise AND, OR, and XOR updates of integer values. */
    def (other: Expression): Expression = Operator("","$bit",value,other)

    Isolation
    Name  Description
    /** \$isolated Modifies the behavior of a write operation to increase the isolation of the operation. */
    def (other: Expression): Expression = Operator("","$isolated",value,other)










    Aggregation Pipeline Operators

    Stage Operators

    Pipeline stages appear in an array. Documents pass through the stages in sequence.

    db.collection.aggregate( [ { <stage> }, ... ] )
    Name  Description
    /** \$project  Reshapes each document in the stream, such as by adding new fields or removing existing fields. For each input document, outputs one document. */
    def (other: Expression): Expression = Operator("","$project",value,other)

    /** \$match  Filters the document stream to allow only matching documents to pass unmodified into the next pipeline stage. $match uses standard MongoDB queries. For each input document, outputs either one document (a match) or zero documents (no match). */
    def (other: Expression): Expression = Operator("","$match",value,other)

    /** \$redact Reshapes each document in the stream by restricting the content for each document based on information stored in the documents themselves. Incorporates the functionality of $project and $match. Can be used to implement field level redaction. For each input document, outputs either one or zero document. */
    def (other: Expression): Expression = Operator("","$redact",value,other)

    /** \$limit  Passes the first n documents unmodified to the pipeline where n is the specified limit. For each input document, outputs either one document (for the first n documents) or zero documents (after the first n documents). */
    def (other: Expression): Expression = Operator("","$limit",value,other)

    /** \$skip Skips the first n documents where n is the specified skip number and passes the remaining documents unmodified to the pipeline. For each input document, outputs either zero documents (for the first n documents) or one document (if after the first n documents). */
    def (other: Expression): Expression = Operator("","$skip",value,other)

    /** \$unwind Deconstructs an array field from the input documents to output a document for each element. Each output document replaces the array with an element value. For each input document, outputs n documents where n is the number of array elements and can be zero for an empty array. */
    def (other: Expression): Expression = Operator("","$unwind",value,other)

    /** \$group  Groups input documents by a specified identifier expression and applies the accumulator expression(s), if specified, to each group. Consumes all input documents and outputs one document per each distinct group. The output documents only contain the identifier field and, if specified, accumulated fields. */
    def (other: Expression): Expression = Operator("","$group",value,other)

    /** \$sort Reorders the document stream by a specified sort key. Only the order changes; the documents remain unmodified. For each input document, outputs one document. */
    def (other: Expression): Expression = Operator("","$sort",value,other)

    /** \$geoNear  Returns an ordered stream of documents based on the proximity to a geospatial point. Incorporates the functionality of $match, $sort, and $limit for geospatial data. The output documents include an additional distance field and can include a location identifier field. */
    def (other: Expression): Expression = Operator("","$geoNear",value,other)

    /** \$out  Writes the resulting documents of the aggregation pipeline to a collection. To use the $out stage, it must be the last stage in the pipeline. */
    def (other: Expression): Expression = Operator("","$out",value,other)

    Expression Operators

    These expression operators are available to construct expressions for use in the aggregation pipeline.

    Operator expressions are similar to functions that take arguments. In general, these expressions take an array of arguments and have the following form:

    { <operator>: [ <argument1>, <argument2> ... ] }
    If operator accepts a single argument, you can omit the outer array designating the argument list:

    { <operator>: <argument> }
    To avoid parsing ambiguity if the argument is a literal array, you must wrap the literal array in a $literal expression or keep the outer array that designates the argument list.

    Boolean Operators
    Boolean expressions evaluate their argument expressions as booleans and return a boolean as the result.

    In addition to the false boolean value, Boolean expression evaluates as false the following: null, 0, and undefined values. The Boolean expression evaluates all other values as true, including non-zero numeric values and arrays.

    Name  Description
    /** \$and  Returns true only when all its expressions evaluate to true. Accepts any number of argument expressions. */
    def (other: Expression): Expression = Operator("","$and",value,other)

    /** \$or Returns true when any of its expressions evaluates to true. Accepts any number of argument expressions. */
    def (other: Expression): Expression = Operator("","$or",value,other)

    /** \$not  Returns the boolean value that is the opposite of its argument expression. Accepts a single argument expression. */
    def (other: Expression): Expression = Operator("","$not",value,other)

    Set Operators
    Set expressions performs set operation on arrays, treating arrays as sets. Set expressions ignores the duplicate entries in each input array and the order of the elements.

    If the set operation returns a set, the operation filters out duplicates in the result to output an array that contains only unique entries. The order of the elements in the output array is unspecified.

    If a set contains a nested array element, the set expression does not descend into the nested array but evaluates the array at top-level.

    Name  Description
    /** \$setEquals  Returns true if the input sets have the same distinct elements. Accepts two or more argument expressions. */
    def (other: Expression): Expression = Operator("","$setEquals",value,other)

    /** \$setIntersection  Returns a set with elements that appear in all of the input sets. Accepts any number of argument expressions. */
    def (other: Expression): Expression = Operator("","$setIntersection",value,other)

    /** \$setUnion Returns a set with elements that appear in any of the input sets. Accepts any number of argument expressions. */
    def (other: Expression): Expression = Operator("","$setUnion",value,other)

    /** \$setDifference  Returns a set with elements that appear in the first set but not in the second set; i.e. performs a relative complement of the second set relative to the first. Accepts exactly two argument expressions. */
    def (other: Expression): Expression = Operator("","$setDifference",value,other)

    /** \$setIsSubset  Returns true if all elements of the first set appear in the second set, including when the first set equals the second set; i.e. not a strict subset. Accepts exactly two argument expressions. */
    def (other: Expression): Expression = Operator("","$setIsSubset",value,other)

    /** \$anyElementTrue Returns true if any elements of a set evaluate to true; otherwise, returns false. Accepts a single argument expression. */
    def (other: Expression): Expression = Operator("","$anyElementTrue",value,other)

    /** \$allElementsTrue  Returns true if no element of a set evaluates to false, otherwise, returns false. Accepts a single argument expression. */
    def (other: Expression): Expression = Operator("","$allElementsTrue",value,other)

    Comparison Operators
    Comparison expressions return a boolean except for $cmp which returns a number.

    The comparison expressions take two argument expressions and compare both value and type, using the specified BSON comparison order for values of different types.

    Name  Description
    /** \$cmp  Returns: 0 if the two values are equivalent, 1 if the first value is greater than the second, and -1 if the first value is less than the second. */
    def (other: Expression): Expression = Operator("","$cmp",value,other)

    /** \$eq Returns true if the values are equivalent. */
    def (other: Expression): Expression = Operator("","$eq",value,other)

    /** \$gt Returns true if the first value is greater than the second. */
    def (other: Expression): Expression = Operator("","$gt",value,other)

    /** \$gte  Returns true if the first value is greater than or equal to the second. */
    def (other: Expression): Expression = Operator("","$gte",value,other)

    /** \$lt Returns true if the first value is less than the second. */
    def (other: Expression): Expression = Operator("","$lt",value,other)

    /** \$lte  Returns true if the first value is less than or equal to the second. */
    def (other: Expression): Expression = Operator("","$lte",value,other)

    /** \$ne Returns true if the values are not equivalent. */
    def (other: Expression): Expression = Operator("","$ne",value,other)

    Arithmetic Operators
    Arithmetic expressions perform mathematic operations on numbers. Some arithmetic expressions can also support date arithmetic.

    Name  Description
    /** \$add  Adds numbers to return the sum, or adds numbers and a date to return a new date. If adding numbers and a date, treats the numbers as milliseconds. Accepts any number of argument expressions, but at most, one expression can resolve to a date. */
    def (other: Expression): Expression = Operator("","$add",value,other)

    /** \$subtract Returns the result of subtracting the second value from the first. If the two values are numbers, return the difference. If the two values are dates, return the difference in milliseconds. If the two values are a date and a number in milliseconds, return the resulting date. Accepts two argument expressions. If the two values are a date and a number, specify the date argument first as it is not meaningful to subtract a date from a number. */
    def (other: Expression): Expression = Operator("","$subtract",value,other)

    /** \$multiply Multiplies numbers to return the product. Accepts any number of argument expressions. */
    def (other: Expression): Expression = Operator("","$multiply",value,other)

    /** \$divide Returns the result of dividing the first number by the second. Accepts two argument expressions. */
    def (other: Expression): Expression = Operator("","$divide",value,other)

    /** \$mod  Returns the remainder of the first number divided by the second. Accepts two argument expressions. */
    def (other: Expression): Expression = Operator("","$mod",value,other)

    String Operators
    String expressions, with the exception of $concat, only have a well-defined behavior for strings of ASCII characters.

    /** \$concat behavior is well-defined regardless of the characters used. */
    def (other: Expression): Expression = Operator("","$concat",value,other)


    Name  Description
    /** \$concat Concatenates any number of strings. */
    def (other: Expression): Expression = Operator("","$concat",value,other)

    /** \$substr Returns a substring of a string, starting at a specified index position up to a specified length. Accepts three expressions as arguments: the first argument must resolve to a string, and the second and third arguments must resolve to integers. */
    def (other: Expression): Expression = Operator("","$substr",value,other)

    /** \$toLower  Converts a string to lowercase. Accepts a single argument expression. */
    def (other: Expression): Expression = Operator("","$toLower",value,other)

    /** \$toUpper  Converts a string to uppercase. Accepts a single argument expression. */
    def (other: Expression): Expression = Operator("","$toUpper",value,other)

    /** \$strcasecmp Performs case-insensitive string comparison and returns: 0 if two strings are equivalent, 1 if the first string is greater than the second, and -1 if the first string is less than the second. */
    def (other: Expression): Expression = Operator("","$strcasecmp",value,other)

    Text Search Operators
    Name  Description
    /** \$meta Access text search metadata. */
    def (other: Expression): Expression = Operator("","$meta",value,other)

    Array Operators
    Name  Description
    /** \$size Returns the number of elements in the array. Accepts a single expression as argument. */
    def (other: Expression): Expression = Operator("","$size",value,other)

    Variable Operators
    Name  Description
    /** \$map  Applies a subexpression to each element of an array and returns the array of resulting values in order. Accepts named parameters. */
    def (other: Expression): Expression = Operator("","$map",value,other)

    /** \$let  Defines variables for use within the scope of a subexpression and returns the result of the subexpression. Accepts named parameters. */
    def (other: Expression): Expression = Operator("","$let",value,other)

    Literal Operators
    Name  Description
    /** \$literal  Return a value without parsing. Use for values that the aggregation pipeline may interpret as an expression. For example, use a $literal expression to a string that starts with a $ to avoid parsing as a field path. */
    def (other: Expression): Expression = Operator("","$literal",value,other)

    Date Operators
    Name  Description
    /** \$dayOfYear  Returns the day of the year for a date as a number between 1 and 366 (leap year). */
    def (other: Expression): Expression = Operator("","$dayOfYear",value,other)

    /** \$dayOfMonth Returns the day of the month for a date as a number between 1 and 31. */
    def (other: Expression): Expression = Operator("","$dayOfMonth",value,other)

    /** \$dayOfWeek  Returns the day of the week for a date as a number between 1 (Sunday) and 7 (Saturday). */
    def (other: Expression): Expression = Operator("","$dayOfWeek",value,other)

    /** \$year Returns the year for a date as a number (e.g. 2014). */
    def (other: Expression): Expression = Operator("","$year",value,other)

    /** \$month  Returns the month for a date as a number between 1 (January) and 12 (December). */
    def (other: Expression): Expression = Operator("","$month",value,other)

    /** \$week Returns the week number for a date as a number between 0 (the partial week that precedes the first Sunday of the year) and 53 (leap year). */
    def (other: Expression): Expression = Operator("","$week",value,other)

    /** \$hour Returns the hour for a date as a number between 0 and 23. */
    def (other: Expression): Expression = Operator("","$hour",value,other)

    /** \$minute Returns the minute for a date as a number between 0 and 59. */
    def (other: Expression): Expression = Operator("","$minute",value,other)

    /** \$second Returns the seconds for a date as a number between 0 and 60 (leap seconds). */
    def (other: Expression): Expression = Operator("","$second",value,other)

    /** \$millisecond  Returns the milliseconds of a date as a number between 0 and 999. */
    def (other: Expression): Expression = Operator("","$millisecond",value,other)

    Conditional Expressions
    Name  Description
    /** \$cond A ternary operator that evaluates one expression, and depending on the result, returns the value of one of the other two expressions. Accepts either three expressions in an ordered list or three named parameters. */
    def (other: Expression): Expression = Operator("","$cond",value,other)

    /** \$ifNull Returns either the non-null result of the first expression or the result of the second expression if the first expression results in a null result. Null result encompasses instances of undefined values or missing fields. Accepts two expressions as arguments. The result of the second expression can be null. */
    def (other: Expression): Expression = Operator("","$ifNull",value,other)

    Accumulators

    Accumulators, available only for the $group stage, compute values by combining documents that share the same group key. Accumulators take as input a single expression, evaluating the expression once for each input document, and maintain their state for the group of documents.

    Name  Description
    /** \$sum  Returns a sum for each group. Ignores non-numeric values. */
    def (other: Expression): Expression = Operator("","$sum",value,other)

    /** \$avg  Returns an average for each group. Ignores non-numeric values. */
    def (other: Expression): Expression = Operator("","$avg",value,other)

    /** \$first  Returns a value from the first document for each group. Order is only defined if the documents are in a defined order. */
    def (other: Expression): Expression = Operator("","$first",value,other)

    /** \$last Returns a value from the last document for each group. Order is only defined if the documents are in a defined order. */
    def (other: Expression): Expression = Operator("","$last",value,other)

    /** \$max  Returns the highest expression value for each group. */
    def (other: Expression): Expression = Operator("","$max",value,other)

    /** \$min  Returns the lowest expression value for each group. */
    def (other: Expression): Expression = Operator("","$min",value,other)

    /** \$push Returns an array of expression values for each group. */
    def (other: Expression): Expression = Operator("","$push",value,other)

    /** \$addToSet Returns an array of unique expression values for each group. Order of the array elements is undefined. */
    def (other: Expression): Expression = Operator("","$addToSet",value,other)





    Operators

    Modifiers
    Many of these operators have corresponding methods in the shell. These methods provide a straightforward and user-friendly interface and are the preferred way to add these options.

    Name  Description
    /** \$comment  Adds a comment to the query to identify queries in the database profiler output. */
    def (other: Expression): Expression = Operator("","$comment",value,other)

    /** \$explain  Forces MongoDB to report on query execution plans. See explain(). */
    def (other: Expression): Expression = Operator("","$explain",value,other)

    /** \$hint Forces MongoDB to use a specific index. See hint() */
    def (other: Expression): Expression = Operator("","$hint",value,other)

    /** \$maxScan  Limits the number of documents scanned. */
    def (other: Expression): Expression = Operator("","$maxScan",value,other)

    /** \$maxTimeMS  Specifies a cumulative time limit in milliseconds for processing operations on a cursor. See maxTimeMS(). */
    def (other: Expression): Expression = Operator("","$maxTimeMS",value,other)

    /** \$max  Specifies an exclusive upper limit for the index to use in a query. See max(). */
    def (other: Expression): Expression = Operator("","$max",value,other)

    /** \$min  Specifies an inclusive lower limit for the index to use in a query. See min(). */
    def (other: Expression): Expression = Operator("","$min",value,other)

    /** \$orderby  Returns a cursor with documents sorted according to a sort specification. See sort(). */
    def (other: Expression): Expression = Operator("","$orderby",value,other)

    /** \$returnKey  Forces the cursor to only return fields included in the index. */
    def (other: Expression): Expression = Operator("","$returnKey",value,other)

    /** \$showDiskLoc  Modifies the documents returned to include references to the on-disk location of each document. */
    def (other: Expression): Expression = Operator("","$showDiskLoc",value,other)

    /** \$snapshot Forces the query to use the index on the _id field. See snapshot(). */
    def (other: Expression): Expression = Operator("","$snapshot",value,other)

    /** \$query  Wraps a query document. */
    def (other: Expression): Expression = Operator("","$query",value,other)

    Sort Order
    Name  Description
    /** \$natural  A special sort order that orders documents using the order of documents on disk. */
    def (other: Expression): Expression = Operator("","$natural",value,other)
*/

  }
}
