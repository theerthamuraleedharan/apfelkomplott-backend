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

