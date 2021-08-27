# SwiftMarker

SwiftMarker is a lightweight template engine.

SwiftMarker是一个轻量级的模板引擎


### Features
* Lightweight and few dependencies.
* Simple and easy to use but flexible. 
* Supports loop expression, logical expression and nested expression.
* You can simply use dotted name like ```foo.bar``` to select params in data model.
* Multiple types are supported to composite data model: array, ```List```, ```JsonArray```, ```Map```, ```JsonObject``` and even plain Bean object. you can combine them freely.

### Dependencies
* JDK 8+

### Tutorial

##### Quick Start

```java
String strTemplate = "......";
com.google.gson.JsonObject model = ...;
SwiftMarker swiftMarker = new SwiftMarker();
swiftMarker.prepare(strTemplate);
String result = swiftMarker.render(model);
```

##### Basic Usage

Use ```${}``` to select params from data model.

* Template
```
Fear leads to ${yoda.word1},
${yoda.word1} leads to ${yoda.word2},
${yoda.word2} leads to suffering.
```

* Data Model
```json
{
    "yoda": {
        "word1": "anger",
        "word2": "hate"
    }
}
```

* Result
```
Fear leads to anger,
anger leads to hate,
hate leads to suffering.
```

###### Loop Expression

Use ```$[var]...$[]``` pair to select elements in data model and loop them. If no any elements selected, anything between them will not be rendered.

* loop expression with key-value elements

Use ```$[]``` to select array/JsonArray/List in data model and use ```${}``` to select params in key-value element. To select params in the loop the expression must starts with `.`, expression in the loop starts without `.` will select params from the global data model.

* Template
```
${question.title}
$[question.options]${.index}: ${.option}$[]

My choice is: ${question.options.1.index}
```
> Notice: loop expression must ends with '$[]'.

* Data Model
```json
{
	"question": {
		"title": "What's your favorite color?",
		"options": [
			{"index": "A", "option": "Red"},
			{"index": "B", "option": "Green"},
			{"index": "C", "option": "Blue"}
		]			
	}
}
```

* Result
```
What's your favorite color?
A: Red
B: Green
C: Blue

My choice is: B
```

> Notice: The line only contains the '$[]' placeholder will not output a new line.


* Loop expression with array/JsonArray/List element

If the array selected contains array/JsonArray/List, use ```${.0}```, ```${.1}```... to select params in the elements.

* Template
```
${question.title}
$[question.options]${.0}: ${.1}$[]
```

* Data Model
```java
Map dataMode = new HashMap() {
	{
		put("question", new HashMap() {
			{
				put("title", "What's your favorite color?");
				put("options": new ArrayList(){
					add(new String[]{"A", "Red"});
					add(new String[]{"B", "Green"});
					add(new String[]{"C", "Blue"});
				});
			}
		})
	}
};
```

* Result
```
What's your favorite color?
A: Red
B: Green
C: Blue
```

##### Logical Expression

Logical expression is used to decide whether display content in it, you can have any layer nested logical expression. Only the expression condition determined to logic true

* Template:
```
?{logic1}
${say}
?{}
?{logic2}
${think}
?{}

?{logic1}
${say}
    ?{logic2}
${think}
    ?{}
?{}
```

* Data Model:
```json
{
    "logic1": true,
    "say": "hello github",
    "logic2": false,
    "think": "goodbye"
}
```

* Result:
```
hello github
hello github
```

* Logical condition judgement for object types:

Logic|String|Number|Boolean|Date|Calendar|JsonPrimitive|Collection|JsonArray|Map|JsonObject|Array
-|-|-|-|-|-|-|-|-|-|-|-
Logic true|Y/y/YES/yes/Yes/non-empty text|>0|true|>0|>0|true/>0|size()>0|size()>0|size()>0|size()>0|length>0
Logic false|N/n/NO/no/No/empty text|<=0|true|=0|=0|true/<=0|size()=0|size()=0|size()=0|size()=0|length=0

* Use "!" in logical expression to perform logic negation, for example:
```
?{!foo.bar}
?{}
```
or
```
$[collection]
?{!.foo.bar}
?{}
$[]
```


* Other supported data model object types

* Map
```java
Map m = new HashMap() {
	{
		put("yoda", new HashMap() {
			{
				put("word1", "anger");
				put("word2", "hate");
			}
		})
	}
};
swiftMarker.render(m);
```

* Plain Bean Object
```java
public class Yoda{
	YodaWords yoda;
}
public class YodaWords {
	String word1 = "anger";
	String word2 = "hate";
}
Yoda yoda = new Yoda();
......
swiftMarker.render(yoda);
```

> Of course, you can have all these data objects nested in any way.

##### Escaping

If the template contains text like `${xxx}` which is not expression but the engine would recognize as expression, you can use the escaping symbol `\` to let the engine ignore them, for example:

```
$\{xxx}
$\[xxx] $\[]
?\{xxx} ?\{}
```

Engine will recognize them as text instead of expression.

##### Config

* You can customize SwiftMarker by providing a ```Config``` object to it.
```java
...
SwiftMarker swiftMarker = new SwiftMarker();
Config config = new Config();
log.setLevel(Logger.LEVEL_DEBUG);
swiftMarker.prepare(strTemplate, config);
...
```

* Options to config SwiftMarker:

name|description|default
-|-|-
debug|true to output debug logs|false
inputLineBreaker|line breaker of input|\\n
outputLineBreaker|line breaker of output|\\n
renderExpressionIfValueIsBlank| set false to avoid render expression if no value provided | true

### Maven

```xml
<dependency>
	<groupId>com.github.swiftech</groupId>
	<artifactId>swiftmarker</artifactId>
	<version>3.0</version>
</dependency>
```


### Release Update
see [changelog](changelog.md)

### Limitation
* Comments in the template is not supported yet.
