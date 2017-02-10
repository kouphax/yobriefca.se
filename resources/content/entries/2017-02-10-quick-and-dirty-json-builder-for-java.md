---
date: 2017-02-10T00:00:00Z
title: "Quick and Dirty JSON Builder for Java"
published: true
categories: [Java]
type: article
external: false
---
If you're writing tests involving JSON or trying to return some basic JSON structure in a simple Play or Spark application here is a simple shorthand notation that can be used to build said response.

Given the following JSON structure,

```json
{
  "title" : "Test Schema",
  "type" : "object",
  "properties" : {
    "firstName" : {
      "type" : "string"
    },
    "lastName" : {
      "type" : "string"
    },
    "age" : {
      "description" : "Age in years",
      "type" : "integer",
      "minimum" : 0
    }
  },
  "required" : [ "firstName", "lastName" ]
}
```

It is possible to represent this in Java like this,

```java
toJson(of(
    "title", "Test Schema",
    "type", "object",
    "properties", of(
        "firstName", of("type", "string"),
        "lastName", of("type", "string"),
        "age", of(
            "description", "Age in years",
            "type", "integer",
            "minimum", 0
        )
    ),
    "required", new String[] { "firstName", "lastName" }
))
```

In order to achieve this then 2 static imports must be used,

```java
import static com.google.common.collect.ImmutableMap.of;
import static play.libs.Json.toJson;
```

Obviously, this assume the use of Guava and Play but we can at least avoid the use of Play (i.e. if you're using some other web framework) by shimming the `toJson` method using the Jackson `ObjectMapper` directly e.g.

```java
public JsonNode toJson(Object data) {
    // either provide or create an ObjectMapper
    return new ObjectMapper().valueToTree(data);
}
```

It's nicer than using, and having to wrangle, a massive string blob because it helps avoid some of accidental syntactical JSON errors that you can make (extra commas, unbalanced braces etc.) when dealing with a raw string.  It's also a bit terser than using the Jackson builder directly as that can make deep chaining a bit awkward from what I've experienced.

CAVEAT: I use this in tests and basic webapps were performance is not important in the slightest so I haven't attempted to understand the overhead of using this approach.

