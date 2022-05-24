# spring-module-system

This repository hosts sample applications for using the Spring Framework with the Java
Module System.

----

For example, the `maven-surefire-patched-module` project uses Maven Surefire to run tests
in a patched module.

Changing the `spring.version` in `pom.xml` to `5.3.20` or `6.0.0-M4` will cause the build
to fail, demonstrating that Spring Framework did not support module path scanning prior to
6.0 M5 (which has not yet been released). Running the build as-is using Spring Framework
6.0 snapshots allows the tests to pass thanks to [this recent
commit](https://github.com/spring-projects/spring-framework/commit/19b436c6aa14e79e6f3a98c1
5a8edff2a8c351fc) that added implicit support for scanning the module path – for example,
when using `@ComponentScan`.
