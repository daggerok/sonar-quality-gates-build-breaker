# Write custom maven plugin
Here are some notes, about hot to write custom maven plugin

[[toc]]

## pom.xml

### naming convention

::: warning NOTE
All uses should use `$yourName-maven-plugin` convention for their plugins
:::

### packaging

@[code lang=xml transcludeWith=tag::packaging](@/../sonar-breaker-maven-plugin/pom.xml)

### dependencies

Custom plugin project `pom.xml` file should contains next required dependencies:

```xml
<dependencies>
    
</dependencies>
```
