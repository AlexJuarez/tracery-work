# Tracery [![Build Status](https://travis-ci.com/facebookincubator/tracery-prerelease.svg?token=7sCGrGNQ9WPN1jgBCXfJ&branch=master)](https://travis-ci.com/facebookincubator/tracery-prerelease)

## Setup
```
brew install node thrift
node install -g npm
```

## Run the Demo
1. Populate the database (this command will auto-build the service):

        ./tracery-service.sh insert --diskio example_trace ./tracery.db

2. Start the data layer:

        ./tracery-service.sh server ./tracery.db

3. Build the client:

        npm run build

4. Open [http://localhost:9090](http://localhost:9090) in your browser

## Develop the UI

**NOTE: This functionality is currently disabled while we figure out how to allow the UI to come from
`webpack-dev-server` but the data to come from `tracery-service.sh`. For now, follow the demo instructions above and refresh the browser manually whenever the code changes**

1. If you've just rebased, pick up any dependencies and rebuild the Thrift layer:

        npm install
        npm run thrift

2. Start the Webpack Dev Server

        npm start

2. Open [http://localhost:8080](http://localhost:8080) in Chrome
3. Start coding! Many types of edits will automatically be applied thanks to React Hot Loader. Refresh the browser for those that don't (there'll be messages in the Console).
4. Use Chrome DevTools for debugging. (React and Redux browser extensions are helpful.)5.

### Other Commands
| Command | Purpose
| ------- | -------
| `npm run jest` | Runs Jest tests on the JavaScript code
| `npm run coverage` | Runs Jest tests on the JavaScript code, while recording coverage. Find the report at `coverage/lcov-report/index.html`.
| `npm run lint-all` | Runs ESLint
| `npm run fix-all` | Runs ESLint in fixing mode
| `npm run flow` | Runs Flow
| `npm run thrift` | Builds the Thrift stubs for accessing the data layer
| `npm test` | Runs the JavaScript tests that Travis would run

## Develop the Data Layer
### Gradle commands
| Command | Purpose
| ------- | -------
| `./gradlew fatJar` | Builds a `.jar` with the data layer and all of its dependencies
| `./gradlew run <params>` | Runs the data layer (equivalent to `./tracery-service.sh`)
| `./gradlew check` | Runs the Java tests that Travis would run. Reports are in `build/reports/{checkstyle, findbugs}/`.
| `./gradlew build` | Build and test everything

### `tracery-service.sh` commands
| Command | Purpose
| ------- | -------
| `./tracery-service.sh insert --diskio <trace file> <db path>` | Insert disk IO trace data from the given trace into the given db
| `./tracery-service.sh server <db path>` | Start the data layer and open the given db file
| `./tracery-service.sh client` | Start the data layer test client, which will make a few test queries

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
