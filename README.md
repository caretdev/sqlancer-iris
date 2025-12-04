# **SQLancer-IRIS**

**Automated SQL Testing for InterSystems IRIS Using Differential Oracles**

`sqlancer-iris` is an extension of the [SQLancer](https://github.com/sqlancer/sqlancer) project that enables automated detection of logical bugs in **InterSystems IRIS** SQL engine.
It generates random SQL queries and uses multiple *testing oracles* to verify whether IRIS returns correct and consistent results — without requiring predefined expected outputs.

This tool aims to help IRIS developers, DBAs, integrators, and contributors identify silent correctness bugs, optimizer issues, and corner cases in SQL behavior.

---

## ⭐️ **Why SQLancer for IRIS?**

InterSystems IRIS powers mission-critical systems in healthcare, finance, logistics, and government.
A single silent SQL correctness bug may lead to:

* wrong analytics or reports
* inconsistent query behavior
* incorrect predicate evaluation
* optimizer mis-rewriting
* hard-to-trace application errors

**SQLancer-IRIS automatically generates queries that explore edge cases and verifies correctness using proven differential testing techniques.**

The approach has uncovered hundreds of bugs in engines like MySQL, PostgreSQL, SQLite, DuckDB, and CockroachDB — now the same power is available for IRIS.


## 📦 Installation

You will need:

* Java 11+
* Maven or Gradle
* Access to an InterSystems IRIS instance (local or remote)
* JDBC connection parameters

Clone the repo:

```bash
git clone https://github.com/caretdev/sqlancer-iris.git
cd sqlancer-iris
```

Build:

```bash
mvn package -DskipTests
```

Or run directly:

```bash
java -jar target/sqlancer-*.jar --username _system --password SYS iris --oracle where
```

---

## 🚀 Running SQLancer-IRIS

Example minimal run:

```bash
java -jar target/sqlancer-*.jar \
  --username _system \
  --password SYS \
  --host localhost \
  --port 1972 \
  iris \
  --oracle WHERE
```

Or using connection url

```bash
java -jar target/sqlancer-*.jar \
  iris \
  --connection-url 'IRIS://_SYSTEM:SYS@localhost:1972/USER'
  --oracle WHERE
```

To run with both WHERE and NOREC oracles:

```bash
--oracle WHERE --oracle NOREC
```

---

## 🧪 What To Do With Findings

SQLancer outputs:

* the **random seed**
* database **schema**
* the **failing SQL query**
* the **oracle check that failed**
* the **mismatching results**

Recommended workflow:

1. Reproduce the failing query using the provided seed.
2. Simplify the query to a minimal test case.
3. Identify whether it's a parser, executor, optimizer, or type system bug.
4. Report to InterSystems Developer Community or GitHub (if applicable).
5. Add the failing seed to regression tests.

---

## 🤝 Contributing

PRs, issue reports, and discussions are encouraged.
