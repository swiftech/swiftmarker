# SwiftMarker

SwiftMarker is a lightweight template engine. 
SwiftMarker是一个轻量级的模板引擎

> This project is published under Apache license, and currently in ***Development***


### Features
* Lightweight and few dependencies.
* Simple and easy to use but flexible.
* You can use dotted name like ```foo.bar``` to select params in data model.
* Multiple types are supported to composite data model: array, ```List```, ```JsonArray```, ```Map```, ```JsonObject``` and plain Bean object, you can combine them freely.


### Dependencies
* commons-lang3 3.7+
* gson 2.8+

### Tutorial


* Intialize
	```java
	String strTemplate = "......";
	com.google.gson.JsonObject model = ...;
	SwiftMarker swiftMarker = new SwiftMarker();
	swiftMarker.prepare(strTemplate);
	String result = swiftMarker.render(model);
	```

* Basic

	Use ```${}``` to select params from data model.

	* Template
	```
	Fear leads to ${yoda.word1},
	${yoda.word1} leads to ${yoda.word2},
	${yoda.word2} leads to suffering.
	```

	* Data Model
	```javascript
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

* Collection (with key-value elements)

	Use ```$[]``` to select array/JsonArray/List in data model and use ```${}``` to select params in key-value element.
	* Template
	```
	${question.title}
	$[question.options]${index}: ${option}$[]
	```
	> Attention: ends with '$[]' is required for the template stanza.

	* Data Model
	```javascript
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
	```

	> Attention: The line only contains the '$[]' place holder will not output a new line.

> If the array selected contains array/JsonArray/List, use ```${0}```, ```${1}``` to select params in it.

> Limits:
> * Only one array/JsonArray/List place holder ```$[]``` is allowed for one line,


* Collection (with array/JsonArray/List element)

	* Template
	```
	${question.title}
	$[question.options]${0}: ${1}$[]
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
					}
					);
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


* Multi line template stanza

	Template
	```
	${question.title}
	$[question.options]
		${index}:
		    ${option}
	$[]
	```


* Other supported data model type

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
> Of course, you can have these data model types nested.


* Config

	* You can customize SwiftMarker by provide a ```Config``` object to it.
	```java
	...
	SwiftMarker swiftMarker = new SwiftMarker();
	Config config = new Config();
	config.setDebug(true);
    swiftMarker.prepare(strTemplate, config);
	...
	```

	* Options to config SwiftMarker:

name|description|default
-|-|-
debug|是否输出日志|false
inputLineBreaker|输入文件换行符|\\n
outputLineBreaker|输出文件换行符|\\n

### Maven

```xml
<dependency>
	<groupId>com.github.swiftech</groupId>
	<artifactId>swiftmarker</artifactId>
	<version>1.0-RC</version>
</dependency>
```


### Known issues
...
