[![Build Status](https://travis-ci.com/facebookincubator/tracery-prerelease.svg?token=7sCGrGNQ9WPN1jgBCXfJ&branch=master)](https://travis-ci.com/facebookincubator/tracery-prerelease)

# Frontend

## Setup
### Once

```
brew install node
node install -g npm
```

### On any rebase

```
npm install
```

### To use locally

1. ```npm start```
2. Open `localhost:8080` in Chrome
3. Many types of edits will automatically be applied thanks to React Hot Loader

### To test

```
npm run jest
```

Will run Jest tests.

```
npm run coverage
```

Will run Jest tests while recording coverage. Find the report at `coverage/lcov-report/index.html`.

```
npm test
```

Will run Flow typechecking, then Jest tests, then ESLint style checking.

# Backend

### Setup

#### Thrift
Install the `thrift` compiler if it's not already installed. On OS X:
```
brew install thrift
```

### Build

```
./gradlew fatJar
```

Or to build and test everything:
```
./gradlew build
```

### Run

```
./gradlew run
```

### Test

#### Checkstyle, Findbugs and Unit Tests
```
./gradlew check
```
Reports are in `build/reports/{checkstyle, findbugs}/`.

### Package

Build everything into a single executable jar file:
```
./gradlew fatJar
```

Run the executable jar file:
```
java -jar build/libs/tracery-service-all-1.0.jar
```

or use the `tracery-service.sh` script.

### Examples

#### Insert trace data into database

```
$ ./tracery-service.sh insert --diskio example_trace ./tracery.db
$ sqlite3 ./tracery.db
sqlite> .tables
com_facebook_tracery_database_trace_DiskPhysOpTable
com_facebook_tracery_database_trace_FileInfoTable
trace_master
sqlite> .headers on
sqlite> .mode column
sqlite> SELECT * FROM trace_master;
...
sqlite> SELECT * FROM com_facebook_tracery_database_trace_FileInfoTable;
...
sqlite> SELECT * FROM com_facebook_tracery_database_trace_DiskPhysOpTable;
...
sqlite> .exit
```

#### Run server

```
$ ./tracery-service.sh server ./tracery.db
```

#### Run test client

```
$ ./tracery-service.sh client
```
