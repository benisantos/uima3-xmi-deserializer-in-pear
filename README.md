
# uima3-xmi-deserializer-in-pear

This is a sample project to reproduce a likely bug with UIMA v3 XMI deserialization in a PEAR context.


## Description

When running an AAE in a PEAR context (uimaj-core 3.3.1), if a CAS Multiplier tries to populate a new CAS while loading it from an XMI file,
a `NullPointerException` is thrown by the method `maybeMakeBaseVersionForPear` of the class `CASImpl.java` (line 1536).


## Steps to reproduce the problem

```sh
# 1) Generate the pear in target/uima3-xmi-deserializer-in-pear-0.1.0-SNAPSHOT.pear
mvn package

# 2) Install PEAR and run the AAE
cp=~/.m2/repository/org/apache/uima/uimaj-core/3.3.1/uimaj-core-3.3.1.jar
cp=$cp:~/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar
cp=${cp}:target/uima3-xmi-deserializer-in-pear-0.1.0-SNAPSHOT.jar
java -cp $cp uima3.PearRunner target/uima3-xmi-deserializer-in-pear-0.1.0-SNAPSHOT.pear
```

It outputs an exception stack trace with the NPE!

```
Caused by: java.lang.NullPointerException
	at org.apache.uima.cas.impl.CASImpl.maybeMakeBaseVersionForPear(CASImpl.java:1536)
	at org.apache.uima.cas.impl.FeatureStructureImplC.<init>(FeatureStructureImplC.java:201)
	at org.apache.uima.jcas.cas.TOP.<init>(TOP.java:83)
	at org.apache.uima.jcas.cas.AnnotationBase.<init>(AnnotationBase.java:110)
	at org.apache.uima.jcas.tcas.Annotation.<init>(Annotation.java:99)
	at org.apache.uima.jcas.tcas.DocumentAnnotation.<init>(DocumentAnnotation.java:76)
...
```

**Note**: It was tested with OpenJDK Temurin 1.8.0_322 and Apache Maven 3.8.5.


## Workaround to "fix" it

Use the uimaj-io-json library and serialize JSON instead of XMI.
