# Code Documentation

The backend is implemented in Java, so the project uses Javadoc-style comments
for generated technical documentation. Public services, controllers, and engine
classes should document their responsibility, important parameters, return
values, and game-rule side effects.

Generate the documentation with:

```bash
mvn javadoc:javadoc
```

The generated HTML documentation is written to:

```text
target/reports/apidocs/index.html
```

For future frontend JavaScript or TypeScript code, JSDoc can be used in the
same spirit. Doxygen is also possible, but Javadoc is the standard tool for the
Java backend in this project.
